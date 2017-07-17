package randomizer.fates.model.processors;

import feflib.fates.gamedata.CharacterBlock;
import feflib.fates.gamedata.FatesGameData;
import randomizer.common.data.FatesData;
import randomizer.common.data.FatesFileData;
import randomizer.common.data.FatesGui;
import randomizer.common.structures.Skill;
import randomizer.common.utils.CompressionUtils;
import randomizer.fates.model.processors.prep.PatchBuilder;
import randomizer.fates.model.structures.FatesCharacter;

import java.io.IOException;
import java.nio.file.Files;
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
        for(CharacterBlock c : data.getCharacters()) {
            FatesCharacter character = FatesData.getInstance().getByPid(c.getPid());
            if(character == null)
                continue;
            if(character.getId() == 0)
                continue;

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

        // TEMP
        try {
            Files.write(FatesFileData.getInstance().getGameData().toPath(), CompressionUtils.compress(data.getRaw()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
