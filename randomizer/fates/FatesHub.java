package randomizer.fates;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feflib.fates.gamedata.CharacterBlock;
import feflib.fates.gamedata.FatesGameData;
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
import java.util.List;

public class FatesHub {

    private FatesItems fatesItems;
    private FatesSkills fatesSkills = FatesSkills.getInstance();
    private boolean[] options = FatesGui.getInstance().getSelectedOptions();
    private List<FatesCharacter> selectedCharacters;
    private FatesGameData data;

    public FatesHub() {
        PatchBuilder.createPatch();
        fatesItems = FatesItems.getInstance();
        selectedCharacters = FatesCharacters.getInstance().getSelectedCharacters();
        data = new FatesGameData(FatesFiles.getInstance().getGameData());
    }

    public void randomize() {
        setup();
        randomizeGlobalFiles();
        randomizeChapters();
        createSettings();
        createOutputText();
    }

    public void randomizeWithSettings(List<FatesCharacter> selectedCharacters) {
        this.selectedCharacters = selectedCharacters;
        randomizeGlobalFiles();
        randomizeChapters();
        createOutputText();
    }

    /**
     * Performs the initial setup required to randomize the game. This includes reading
     * GameData's character blocks, pairing up characters for join order swaps, generating
     * new classes, and recalculating stats.
     *
     */
    private void setup() {

        // Synchronize randomizer and GameData's characters.
        for(CharacterBlock c : data.getCharacters()) {
            FatesCharacter character = FatesCharacters.getInstance().getByPid(c.getPid());
            if(character == null)
                continue;
            if(character.getId() == 0)
                continue;

            // Promote Jakob/Felicia.
            if(character.getPid().equals("PID_フェリシア") || character.getPid().equals("PID_ジョーカー")
                    && options[9]) {
                character.setPromoted(true);
            }

            // Stats.
            character.setStats(c.getStats());
            character.setGrowths(c.getGrowths());
            character.setModifiers(c.getModifiers());
            character.setLevel((byte) c.getLevel());
            character.setInternalLevel((byte) c.getInternalLevel());

            // Skills.
            Skill[] skills = new Skill[5];
            short[] skillIds = c.getSkills();
            for(int x = 0; x < 5; x++) {
                if(skillIds[x] != 0)
                    skills[x] = fatesSkills.getSkillById(skillIds[x]);
            }
            character.setSkills(skills);
            character.setPersonSkill(c.getPersonalSkills()[0]);
        }

        // Perform matching, make edits to GameData.
        CharacterMatcher.matchCharacters(selectedCharacters);
        ClassRandomizer.randomizeClasses(selectedCharacters);
        StatCalculator.randomizeStats(selectedCharacters);
    }

    private void randomizeChapters() {
        ChapterHandler.randomizeChapterData(selectedCharacters);
        ScriptHandler.randomizeScript(selectedCharacters);
        if(options[5])
            TextHandler.randomizeText(selectedCharacters);
    }

    private void randomizeGlobalFiles() {
        // GameData.bin.lz
        GameDataHandler.randomizeGameData(data, selectedCharacters);
        try {
            Files.write(FatesFiles.getInstance().getGameData().toPath(),
                    CompressionUtils.compress(data.getRaw()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // code.bin
        if(FatesFiles.getInstance().getCode() != null)
            CodeHandler.process(selectedCharacters);
    }

    private void createSettings() {
        File out = new File(System.getProperty("user.dir"), "settings.json");
        try (Writer writer = new FileWriter(out)) {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            SettingsWrapper wrapper = new SettingsWrapper();
            wrapper.setGui(FatesGui.getInstance());
            wrapper.setCharacters(selectedCharacters);
            gson.toJson(wrapper, writer);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createOutputText() {
        File out = new File(System.getProperty("user.dir"), "results.txt");
        List<String> lines = new ArrayList<>();
        for(FatesCharacter c : selectedCharacters) {
            lines.add(c.toString());
        }
        try {
            Files.write(out.toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
