package randomizer.fates.model.processors;

import feflib.fates.gamedata.CharacterBlock;
import feflib.fates.gamedata.FatesGameData;
import randomizer.common.data.FatesData;
import randomizer.common.data.FatesGui;
import randomizer.common.enums.ChapterType;
import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Chapter;
import randomizer.fates.model.structures.FatesCharacter;

import java.util.List;

class GameDataHandler {
    private static boolean[] options = FatesGui.getInstance().getSelectedOptions();
    private static FatesData fatesData = FatesData.getInstance();

    static void randomizeGameData(FatesGameData data, List<FatesCharacter> characters) {
        for(FatesCharacter c : characters) {
            if(c.getId() == 0) // NPCs are not located in GameData!
                continue;
            CharacterBlock b = data.getCharacters().get(c.getId());

            // Randomize classes.
            if(options[0]) {
                b.setClasses(new short[] {c.getCharacterClass().getId(), c.getCharacterClass().getTiedJob()});
                b.setReclasses(new short[] {c.getReclasses()[0].getId(), c.getReclasses()[1].getId()});
            }

            // Make adjustments for join-order.
            if(options[3]) {
                b.setLevel(c.getLevel());
                b.setInternalLevel(c.getInternalLevel());
                if(c.getCharacterType() == CharacterType.SecondGen) {
                    b.setParent(fatesData.getByPid(c.getLinkedPid()).getId());
                }
            }

            // Assign randomized stats and patch bitflags.
            b.setStats(c.getStats());
            b.setGrowths(c.getGrowths());
            b.setModifiers(c.getModifiers());
            if(options[1]) {
                short[] skillIds = new short[5];
                for(int x = 0; x < 5; x++) {
                    if(c.getSkills()[x] == null)
                        break;
                    skillIds[x] = c.getSkills()[x].getId();
                }
                b.setSkills(skillIds);
                short[] personals = new short[3];
                for(int x = 0; x < 3; x++) {
                    personals[x] = c.getPersonSkill();
                }
                b.setPersonalSkills(personals);
            }
            // TODO: Patch bitflags for special weapons and royals.
        }

        // Patch chapters.
        if(options[3]) {
            for(Chapter c : FatesData.getInstance().getChaptersByType(ChapterType.Child)) {
                data.getChapters().get(c.getId()).setMarriedCharacter(fatesData.getByPid(
                        fatesData.getReplacement(c.getChildPid()).getLinkedPid()).getId());
            }
        }
    }
}
