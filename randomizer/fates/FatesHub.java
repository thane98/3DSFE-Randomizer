package randomizer.fates;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feflib.fates.gamedata.CharacterBlock;
import feflib.fates.gamedata.FatesGameData;
import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Skill;
import randomizer.common.utils.CompressionUtils;
import randomizer.fates.model.processors.CharacterMatcher;
import randomizer.fates.model.processors.ClassRandomizer;
import randomizer.fates.model.processors.StatCalculator;
import randomizer.fates.model.processors.chapter.ChapterHandler;
import randomizer.fates.model.processors.chapter.ScriptHandler;
import randomizer.fates.model.processors.chapter.TextHandler;
import randomizer.fates.model.processors.global.CodeHandler;
import randomizer.fates.model.processors.global.GameDataHandler;
import randomizer.fates.model.processors.prep.PatchBuilder;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.model.structures.SettingsWrapper;
import randomizer.fates.singletons.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FatesHub {

    private FatesSkills fatesSkills = FatesSkills.getInstance();
    private boolean[] options = FatesGui.getInstance().getSelectedOptions();
    private FatesGameData data;
    
    private FatesCharacters characters = FatesCharacters.getInstance();
    
    public FatesHub() {
        PatchBuilder.createPatch();
        data = new FatesGameData(FatesFiles.getInstance().getGameData());
    }

    public void randomize() {
        characters.setWorkingCharacters(characters.getSelectedCharacters());
        
        setup();
        randomizeGlobalFiles();
        randomizeChapters();
        createSettings();
        createOutputText();
    }

    public void randomizeWithSettings(List<FatesCharacter> selectedCharacters) {
        if(selectedCharacters == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "randomizeWithSettings. selectedCharacters must not be null.");
        if(selectedCharacters.size() == 0)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "randomizeWithSettings. selectedCharacters must have a size > 0");

        characters.setWorkingCharacters(selectedCharacters);
        randomizeGlobalFiles();
        randomizeChapters();
        createOutputText();
    }

    /**
     * Performs the initial setup required to randomize the game. This includes reading
     * GameData's character blocks, pairing up characters for join order swaps, generating
     * new classes, and recalculating stats.
     */
    private void setup() {
        // Assign target characters. If "Randomize Join Order" is not selected,
        // the target will be the same as the original character.
        CharacterMatcher.matchCharacters();

        // Synchronize randomizer and GameData's characters.
        for(CharacterBlock c : data.getCharacters()) {
            FatesCharacter character = FatesCharacters.getInstance().getByPid(c.getPid());
            if(character == null)
                continue;
            if(character.getCharacterType() == CharacterType.NPC)
                continue;
            FatesCharacter replacement = FatesCharacters.getInstance().getReplacement(c.getPid());

            // Promote Jakob/Felicia.
            if(character.getPid().equals("PID_フェリシア") || character.getPid().equals("PID_ジョーカー")
                    && options[9]) {
                character.setPromoted(true);
            }

            // Stats.
            replacement.setStats(c.getStats());
            replacement.setGrowths(c.getGrowths());
            replacement.setModifiers(c.getModifiers());
            replacement.setLevel((byte) c.getLevel());
            replacement.setInternalLevel((byte) c.getInternalLevel());

            // Skills.
            Skill[] skills = new Skill[5];
            short[] skillIds = c.getSkills();
            for(int x = 0; x < 5; x++) {
                if(skillIds[x] != 0)
                    skills[x] = fatesSkills.getSkillById(skillIds[x]);
            }
            replacement.setSkills(skills);
            replacement.setPersonSkill(c.getPersonalSkills()[0]);
        }

        // Perform matching, make edits to GameData.
        ClassRandomizer.randomizeClasses();
        StatCalculator.randomizeStats();
    }

    private void randomizeChapters() {
        ChapterHandler.randomizeChapterData();
        ScriptHandler.randomizeScript();
        if(options[5])
            TextHandler.randomizeText();
    }

    private void randomizeGlobalFiles() {
        // GameData.bin.lz
        GameDataHandler.randomizeGameData(data);
        try {
            Files.write(FatesFiles.getInstance().getGameData().toPath(),
                    CompressionUtils.compress(data.getRaw()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // code.bin
        if(FatesFiles.getInstance().getCode() != null)
            CodeHandler.process();
    }

    private void createSettings() {
        File out = new File(System.getProperty("user.dir"), "settings.json");
        try (Writer writer = new FileWriter(out)) {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            SettingsWrapper wrapper = new SettingsWrapper();
            wrapper.setGui(FatesGui.getInstance());
            wrapper.setCharacters(characters.getWorkingCharacters());
            gson.toJson(wrapper, writer);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createOutputText() {
        File out = new File(System.getProperty("user.dir"), "results.txt");
        List<String> lines = new ArrayList<>();
        for(FatesCharacter c : characters.getWorkingCharacters()) {
            lines.add(c.toString());
        }
        try {
            Files.write(out.toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
