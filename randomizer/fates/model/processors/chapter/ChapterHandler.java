package randomizer.fates.model.processors.chapter;

import feflib.fates.castle.join.FatesJoin;
import feflib.fates.castle.join.JoinBlock;
import feflib.fates.gamedata.dispo.DispoBlock;
import feflib.fates.gamedata.dispo.DispoFaction;
import feflib.fates.gamedata.dispo.FatesDispo;
import feflib.fates.gamedata.person.FatesPerson;
import feflib.fates.gamedata.person.PersonBlock;
import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Chapter;
import randomizer.common.utils.CompressionUtils;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChapterHandler {
    private static boolean[] options = FatesGui.getInstance().getSelectedOptions();
    private static FatesItems fatesItems = FatesItems.getInstance();
    private static FatesCharacters fatesCharacters = FatesCharacters.getInstance();
    private static FatesJobs fatesJobs = FatesJobs.getInstance();
    private static FatesChapters fatesChapters = FatesChapters.getInstance();
    private static FatesFiles fileData = FatesFiles.getInstance();

    public static void randomizeChapterData() {
        List<FatesCharacter> selected = FatesCharacters.getInstance().getWorkingCharacters();
        List<Chapter> chapters = fatesChapters.getSelectedChapters();

        for(Chapter c : chapters) {
            HashMap<String, List<String>> aliasMap = randomizePerson(c, selected);
            randomizeDispo(c, selected, aliasMap);
        }
        randomizeJoin(selected);
    }

    private static HashMap<String, List<String>> randomizePerson(Chapter chapter, List<FatesCharacter> selected) {
        if(chapter == null)
            throw new IllegalArgumentException("Violation of precondition: randomizePerson." +
                    "chapter must not be null.");
        if(selected == null)
            throw new IllegalArgumentException("Violation of precondition: randomizePerson." +
                    "selected must not be null.");

        HashMap<String, List<String>> aliasMap = new HashMap<>();
        FatesPerson person = new FatesPerson(fileData.getPerson().get(chapter.getCid()));

        // Iterate over every block in the person file.
        for(PersonBlock p : person.getCharacters()) {
            // ... And see if the block matches with one of the characters
            // in the randomizer pool.
            for(FatesCharacter c : selected) {
                if(c.getCharacterType() == CharacterType.Player) // The player should never be swapped.
                    continue;
                FatesCharacter target = fatesCharacters.getByPid(c.getTargetPid());
                if(target == null)
                    throw new RuntimeException("Error: randomizePerson. Target character for " +
                            c.getName() + " returned null.");

                // If the block matches the character, reclass it and replace the assets.
                if(p.getAid().equals(target.getAid())) {
                    // Change character.
                    p.setAid(c.getAid());
                    p.setFid(c.getFid());
                    p.setMPid(c.getMPid());
                    p.setMPidH(c.getMPidH());
                    if(chapter.getCid().equals("A001")) // Fix for chapter 1 crash.
                        break;

                    // If the PID isn't banned, reclass.
                    if(!fatesCharacters.getBannedPids().contains(p.getPid())) {
                        // Reclass block.
                        if(options[13]) {
                            p.setClasses(new short[] { c.getCharacterClass().getId(), c.getCharacterClass().getTiedJob() });
                            p.setWeaponRanks(fatesJobs.generateWeaponsRanks(c.getCharacterClass()));
                        }

                        // Add block to alias map.
                        if(aliasMap.containsKey(c.getPid())) {
                            aliasMap.get(c.getPid()).add(p.getPid());
                        }
                        else {
                            List<String> aliases = new ArrayList<>();
                            aliases.add(p.getPid());
                            aliasMap.put(c.getPid(), aliases);
                        }
                    }
                    break;
                }
            }
        }

        // Finally, recompress the file so its usable in game.
        try {
            Files.write(fileData.getPerson().get(chapter.getCid()).toPath(),
                    CompressionUtils.compress(person.serialize()));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return aliasMap;
    }

    private static void randomizeDispo(Chapter chapter, List<FatesCharacter> selected,
                                       HashMap<String, List<String>> aliasMap) {
        if(chapter == null)
            throw new IllegalArgumentException("Violation of precondition: randomizeDispo." +
                    "chapter must not be null.");
        if(selected == null)
            throw new IllegalArgumentException("Violation of precondition: randomizeDispo." +
                    "selected must not be null.");
        if(aliasMap == null)
            throw new IllegalArgumentException("Violation of precondition: aliasMap." +
                    "aliasMap must not be null.");

        FatesDispo dispo = new FatesDispo(fileData.getDispos().get(chapter.getCid()));

        // Special fixes for chapters with unusual settings.
        if(chapter.getCid().equals("A011"))
            patchA011Dispo(dispo);

        // Iterate over every block...
        for(DispoFaction f : dispo.getFactions()) {
            // ... And every block within the faction...
            for(DispoBlock b : f.getSpawns()) {
                // ... And compare it's PID against every character and alias.
                for(FatesCharacter c : selected) {
                    // Swap out matching dispos and distribute new items.
                    FatesCharacter target = fatesCharacters.getByPid(c.getTargetPid());
                    if(pidMatches(target, b)) {
                        b.setPid(c.getPid());
                        b.addItem(fatesItems.generateItem(c.getCharacterClass()).getIid());
                        if(c.getCharacterType() == CharacterType.Player)
                            b.addItem(fatesItems.generateItem(c.getCharacterClass()).getIid());
                        break;
                    }
                    else if(aliasMap.get(c.getPid()) != null && options[13]) {
                        // Swap out aliases and distribute items.
                        for(String s : aliasMap.get(c.getPid())) {
                            if(s.equals(b.getPid())) {
                                b.addItem(fatesItems.generateItem(c.getCharacterClass()).getIid());
                                if(s.contains("ボス")) { // Workaround for bugged bosses.
                                    b.addItem(fatesItems.generateItem(c.getCharacterClass()).getIid());
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Write file.
        try {
            Files.write(fileData.getDispos().get(chapter.getCid()).toPath(),
                    CompressionUtils.compress(dispo.serialize()));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void randomizeJoin(List<FatesCharacter> selected) {
        if(selected == null)
            throw new IllegalArgumentException("Violation of precondition: randomizeJoin." +
                    "selected must not be null.");

        // Swap out join blocks.
        FatesJoin join = new FatesJoin(fileData.getCastleJoin());
        for(JoinBlock j : join.getBlocks()) {
            for(FatesCharacter c : selected) {
                if(j.getCharacter().equals(c.getPid())) {
                    j.setCharacter(fatesCharacters.getReplacement(c.getPid()).getPid());
                    break;
                }
            }
        }

        // Add block for Anna's replacement.
        if(options[8]) {
            JoinBlock block = new JoinBlock();
            block.setBirthrightJoin("CID_A006");
            block.setConquestJoin("CID_B006");
            block.setRevelationJoin("CID_C006");
            block.setUnknownOne(join.getBlocks().get(0).getUnknownOne());
            for(FatesCharacter c : selected) {
                if(c.getPid().equals("PID_アンナ")) {
                    block.setCharacter(fatesCharacters.getReplacement(c.getPid()).getPid());
                    join.getBlocks().add(block);
                    break;
                }
            }
        }

        // TODO: Fix stats for castle join servants.

        // Write file.
        try {
            Files.write(fileData.getCastleJoin().toPath(),
                    CompressionUtils.compress(join.serialize()));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Determine if the given randomizer character and dispo block refer to
     * the same character.
     *
     * @param c The randomizer character to check.
     * @param b The dispo block to check.
     * @return A boolean value representing whether or not the blocks match.
     */
    private static boolean pidMatches(FatesCharacter c, DispoBlock b) {
        if(c == null)
            throw new IllegalArgumentException("Violation of precondition: pidMatches." +
                    "c must not be null.");
        if(b == null)
            throw new IllegalArgumentException("Violation of precondition: pidMatches." +
                    "b must not be null.");

        return c.getPid().equals(b.getPid()) || c.getPidA().equals(b.getPid()) || c.getPidB().equals(b.getPid())
                || c.getPidC().equals(b.getPid());
    }

    /**
     * Make adjustments to Birthright chapter 11's spawn file
     * to prevent crashing when Reina's replacement spawns.
     *
     * @param dispo The dispo file to be patched.
     */
    private static void patchA011Dispo(FatesDispo dispo) {
        if(dispo == null)
            throw new IllegalArgumentException("Violation of precondition: patchA011Dispo." +
                    "dispo must not be null.");

        DispoFaction faction = dispo.getByName("Support01");
        assert(faction != null);
        if(options[3]) {
            dispo.getFactions().remove(faction);
        }

        // Fix for Reina's starting position.
        faction = dispo.getByName("Support02");
        assert(faction != null);
        faction.getSpawns().get(0).setFirstCoord(new byte[] { 0x1C, 0x8 });
        faction.getSpawns().get(0).setSecondCoord(new byte[] { 0x1C, 0x7 });
    }
}
