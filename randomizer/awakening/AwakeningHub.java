package randomizer.awakening;

import feflib.awakening.data.person.ACharacterBlock;
import feflib.awakening.data.person.AwakeningPerson;
import randomizer.awakening.model.processors.CharacterMatcher;
import randomizer.awakening.model.processors.ClassRandomizer;
import randomizer.awakening.model.processors.StatCalculator;
import randomizer.awakening.model.processors.chapter.ChapterHandler;
import randomizer.awakening.model.processors.chapter.ScriptHandler;
import randomizer.awakening.model.processors.chapter.TextHandler;
import randomizer.awakening.model.processors.global.StaticHandler;
import randomizer.awakening.model.processors.prep.PatchBuilder;
import randomizer.awakening.model.structures.ACharacter;
import randomizer.awakening.singletons.ACharacters;
import randomizer.awakening.singletons.AFiles;
import randomizer.awakening.singletons.AGui;
import randomizer.awakening.singletons.ASkills;
import randomizer.common.structures.Skill;
import randomizer.common.utils.CompressionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class AwakeningHub {

    private ASkills aSkills = ASkills.getInstance();
    private boolean[] options = AGui.getInstance().getSelectedOptions();
    private List<ACharacter> selectedCharacters;
    private AwakeningPerson data;

    public AwakeningHub() {
        PatchBuilder.createPatch();
        selectedCharacters = ACharacters.getInstance().getSelectedCharacters();
        data = new AwakeningPerson(AFiles.getInstance().getCharacterFile());
    }

    public void randomize() {
        setup();
        randomizeGlobalFiles();
        randomizeChapters();
        createSettings();
        createOutputText();
    }

    public void randomizeWithSettings(List<ACharacter> selectedCharacters) {
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
        for(ACharacterBlock c : data.getCharacters()) {
            ACharacter character = ACharacters.getInstance().getByPid(c.getPid());
            if(character == null)
                continue;
            if(character.getId() == 0)
                continue;

            // Stats.
            character.setStats(c.getStats());
            character.setModifiers(c.getModifiers());
            character.setLevel((byte) c.getLevel());

            // Skills.
            Skill[] skills = new Skill[5];
            short[] skillIds = c.getSkills();
            for(int x = 0; x < 5; x++) {
                if(skillIds[x] != 0)
                    skills[x] = aSkills.getSkillById(skillIds[x]);
            }
            character.setSkills(skills);
        }

        // Perform matching, make edits to GameData.
        CharacterMatcher.matchCharacters(selectedCharacters);
        ClassRandomizer.randomizeClasses(selectedCharacters);
        StatCalculator.randomizeStats(selectedCharacters);

        // TODO: Actually edit this file.
        try {
            Files.write(AFiles.getInstance().getGameData().toPath(),
                    CompressionUtils.compress(Files.readAllBytes(AFiles.getInstance().getGameData().toPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void randomizeChapters() {
        ChapterHandler.randomizeChapterData(selectedCharacters);
        ScriptHandler.randomizeScript(selectedCharacters);
        TextHandler.randomizeText(selectedCharacters);
    }

    private void randomizeGlobalFiles() {
        StaticHandler.randomizeCharacters(data, selectedCharacters);
    }

    private void createSettings() {

    }

    private void createOutputText() {
        File out = new File(System.getProperty("user.dir"), "results.txt");
        List<String> lines = new ArrayList<>();
        for(ACharacter c : selectedCharacters) {
            lines.add(c.toString());
        }
        try {
            Files.write(out.toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
