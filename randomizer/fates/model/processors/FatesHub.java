package randomizer.fates.model.processors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import feflib.fates.gamedata.CharacterBlock;
import feflib.fates.gamedata.FatesGameData;
import randomizer.common.structures.Skill;
import randomizer.common.utils.CompressionUtils;
import randomizer.fates.model.processors.prep.PatchBuilder;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.model.structures.SettingsWrapper;
import randomizer.fates.singletons.FatesData;
import randomizer.fates.singletons.FatesFileData;
import randomizer.fates.singletons.FatesGui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for processing selected options and calling the relevant
 * helper classes for randomization.
 */
public class FatesHub {

    private FatesData fatesData;
    private boolean[] options = FatesGui.getInstance().getSelectedOptions();

    public FatesHub() {
        fatesData = FatesData.getInstance();
    }

    public void randomize() {
        // Initial setup.
        List<FatesCharacter> selectedCharacters = FatesData.getInstance().getSelectedCharacters();
        PatchBuilder.createPatch();
        FatesGameData data = new FatesGameData(FatesFileData.getInstance().getGameData());

        // Synchronize randomizer and GameData's characters.
        for(CharacterBlock c : data.getCharacters()) {
            FatesCharacter character = FatesData.getInstance().getByPid(c.getPid());
            if(character == null)
                continue;
            if(character.getId() == 0)
                continue;

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
                    skills[x] = fatesData.getSkillById(skillIds[x]);
            }
            character.setSkills(skills);
            character.setPersonSkill(c.getPersonalSkills()[0]);
        }

        // Perform matching, make edits to GameData.
        CharacterMatcher.matchCharacters(selectedCharacters);
        ClassRandomizer.randomizeClasses(selectedCharacters);
        StatCalculator.randomizeStats(selectedCharacters);
        GameDataHandler.randomizeGameData(data, selectedCharacters);

        // Modify chapters.
        ChapterHandler.randomizeChapterData(selectedCharacters);
        ScriptHandler.randomizeScript(selectedCharacters);
        if(options[3])
            TextHandler.randomizeText(selectedCharacters);
        try {
            Files.write(FatesFileData.getInstance().getGameData().toPath(),
                    CompressionUtils.compress(data.getRaw()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Process code.
        if(FatesFileData.getInstance().getCode() != null)
            CodeHandler.process(selectedCharacters);

        // Create output files.
        createSettings(selectedCharacters);
        createOutputText(selectedCharacters);
    }

    public void randomizeWithSettings(List<FatesCharacter> selectedCharacters) {
        PatchBuilder.createPatch();

        // Modify GameData.
        FatesGameData data = new FatesGameData(FatesFileData.getInstance().getGameData());
        GameDataHandler.randomizeGameData(data, selectedCharacters);

        // Modify chapters.
        ChapterHandler.randomizeChapterData(selectedCharacters);
        ScriptHandler.randomizeScript(selectedCharacters);
        if(options[5])
            TextHandler.randomizeText(selectedCharacters);
        try {
            Files.write(FatesFileData.getInstance().getGameData().toPath(),
                    CompressionUtils.compress(data.getRaw()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        createOutputText(selectedCharacters);
    }

    private void createSettings(List<FatesCharacter> selectedCharacters) {
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

    private void createOutputText(List<FatesCharacter> selectedCharacters) {
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
