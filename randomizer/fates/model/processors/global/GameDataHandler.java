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
    private static FatesJobs fatesJobs = FatesJobs.getInstance();
    private static FatesCharacters fatesCharacters = FatesCharacters.getInstance();

    public static void randomizeGameData(FatesGameData data) {
        if(data == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "randomizeGameData. data must not be null.");
        
        List<FatesCharacter> characters = fatesCharacters.getWorkingCharacters();
        
        // Patch character table.
        for(FatesCharacter c : characters) {
            if(c.getId() == 0) // NPCs are not located in GameData!
                continue;

            // Write class information.
            CharacterBlock b = data.getCharacters().get(c.getId());
            if(options[0]) {
                b.setClasses(new short[] {c.getCharacterClass().getId(), c.getCharacterClass().getTiedJob()});
                b.setReclasses(new short[] {c.getReclasses()[0].getId(), c.getReclasses()[1].getId()});
                b.setWeaponRanks(fatesJobs.generateWeaponsRanks(c.getCharacterClass()));
            }

            patchStats(c, b);
            patchBitflags(c, b);
        }
        patchChapters(data);
    }

    private static void patchChapters(FatesGameData data) {
        if(data == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "patchChapters. data must not be null.");
        
        if(options[3]) {
            for(Chapter c : FatesChapters.getInstance().getChaptersByType(ChapterType.Child)) {
                data.getChapters().get(c.getId()).setMarriedCharacter(fatesCharacters.getByPid(
                        fatesCharacters.getReplacement(c.getChildPid()).getLinkedPid()).getId());
            }
        }
        if(options[11]) {
            for(Chapter c : FatesChapters.getInstance().getChaptersByType(ChapterType.Amiibo)) {
                data.getChapters().get(c.getId()).setType((byte) 0x1); // Paralogue.
            }
        }
    }

    private static void patchBitflags(FatesCharacter c, CharacterBlock b) {
        if(c == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "patchBitflags. c must not be null.");
        if(b == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "patchBitflags. b must not be null.");
        
        FatesCharacter target = fatesCharacters.getByPid(c.getTargetPid());
        if(target == null)
            throw new RuntimeException("Error: patchBitflags. Target character for " +
                    c.getName() + " returned null.");

        // Set royal weapon and dragon vein flags for royal replacements.
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

    private static void patchStats(FatesCharacter c, CharacterBlock b) {
        if(c == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "patchStats. c must not be null.");
        if(b == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "patchStats. b must not be null.");

        // Write randomized stats to the block.
        b.setStats(c.getStats());
        b.setGrowths(c.getGrowths());
        b.setModifiers(c.getModifiers());

        // Write join-order specific stats to the block.
        if(options[3]) {
            b.setLevel(c.getLevel());
            b.setInternalLevel(c.getInternalLevel());
            if(c.getCharacterType() == CharacterType.SecondGen) {
                b.setParent(fatesCharacters.getByPid(c.getLinkedPid()).getId());
            }
            b.setSupportRoute((byte) 0x7); // Unlock supports on all routes.
        }

        // Write randomized skills to the block.
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
