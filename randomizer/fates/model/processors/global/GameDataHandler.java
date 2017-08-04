package randomizer.fates.model.processors.global;

import feflib.fates.gamedata.CharacterBlock;
import feflib.fates.gamedata.FatesGameData;
import randomizer.common.enums.ChapterType;
import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Chapter;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.*;

import java.util.List;

public class GameDataHandler {
    private static boolean[] options = FatesGui.getInstance().getSelectedOptions();
    private static FatesItems fatesItems = FatesItems.getInstance();
    private static FatesJobs fatesJobs = FatesJobs.getInstance();
    private static FatesCharacters fatesCharacters = FatesCharacters.getInstance();

    public static void randomizeGameData(FatesGameData data, List<FatesCharacter> characters) {
        for(FatesCharacter c : characters) {
            if(c.getId() == 0) // NPCs are not located in GameData!
                continue;

            CharacterBlock b = data.getCharacters().get(c.getId());
            if(options[0]) {
                b.setClasses(new short[] {c.getCharacterClass().getId(), c.getCharacterClass().getTiedJob()});
                b.setReclasses(new short[] {c.getReclasses()[0].getId(), c.getReclasses()[1].getId()});
                b.setWeaponRanks(fatesJobs.generateWeaponsRanks(c.getCharacterClass()));
            }

            writeStats(c, b);
            writeBitflags(c, b);
        }
        patchChapters(characters, data);
    }

    private static void patchChapters(List<FatesCharacter> characters, FatesGameData data) {
        if(options[3]) {
            for(Chapter c : FatesChapters.getInstance().getChaptersByType(ChapterType.Child)) {
                data.getChapters().get(c.getId()).setMarriedCharacter(fatesCharacters.getByPid(
                        fatesCharacters.getReplacement(characters, c.getChildPid()).getLinkedPid()).getId());
            }
        }
        if(options[11]) {
            for(Chapter c : FatesChapters.getInstance().getChaptersByType(ChapterType.Amiibo)) {
                data.getChapters().get(c.getId()).setType((byte) 0x1); // Paralogue.
            }
        }
    }

    private static void writeBitflags(FatesCharacter c, CharacterBlock b) {
        FatesCharacter target = fatesCharacters.getByPid(c.getTargetPid());
        byte[] bitflags = b.getBitflags();
        if(target.isRoyal()) {
            bitflags[4] = 0x8;
            switch(target.getPid()) {
                case "PID_タクミ":
                    bitflags[3] = 0x40;
                    break;
                case "PID_リョウマ":
                    bitflags[3] = (byte) 0x80;
                    break;
                case "PID_レオン":
                    bitflags[4] = 0x9;
                    break;
                case "PID_マークス":
                    bitflags[4] = 0xA;
                    break;
            }
            b.setBitflags(bitflags);
            c.setBitflags(bitflags);
        }
    }

    private static void writeStats(FatesCharacter c, CharacterBlock b) {
        b.setStats(c.getStats());
        b.setGrowths(c.getGrowths());
        b.setModifiers(c.getModifiers());
        if(options[3]) {
            b.setLevel(c.getLevel());
            b.setInternalLevel(c.getInternalLevel());
            if(c.getCharacterType() == CharacterType.SecondGen) {
                b.setParent(fatesCharacters.getByPid(c.getLinkedPid()).getId());
            }
        }
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
    }
}
