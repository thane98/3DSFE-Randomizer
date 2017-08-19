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

    public static void randomizeChapterData(List<FatesCharacter> selected) {
        List<Chapter> chapters = fatesChapters.getSelectedChapters();
        for(Chapter c : chapters) {
            if(c.getCid().equals("A000")) // Fix for chapter 1 crash.
                continue;
            HashMap<String, List<String>> aliasMap = randomizePerson(c, selected);
            randomizeDispo(c, selected, aliasMap);
        }
        randomizeJoin(selected);
    }

    private static HashMap<String, List<String>> randomizePerson(Chapter chapter, List<FatesCharacter> selected) {
        HashMap<String, List<String>> aliasMap = new HashMap<>();
        FatesPerson person = new FatesPerson(fileData.getPerson().get(chapter.getCid()));
        for(PersonBlock p : person.getCharacters()) {
            for(FatesCharacter c : selected) {
                if(c.getCharacterType() == CharacterType.Player)
                    continue;
                FatesCharacter target = fatesCharacters.getByPid(c.getTargetPid());
                if(p.getAid().equals(target.getAid())) {
                    // Change character.
                    p.setAid(c.getAid());
                    p.setFid(c.getFid());
                    p.setMPid(c.getMPid());
                    p.setMPidH(c.getMPidH());

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
        FatesDispo dispo = new FatesDispo(fileData.getDispos().get(chapter.getCid()));
        for(DispoFaction f : dispo.getFactions()) {
            for(DispoBlock b : f.getSpawns()) {
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
                                if(chapter.getCid().equals("A001")) {
                                    b.addItem(fatesItems.generateDebugItem(c.getCharacterClass()).getIid());
                                    break;
                                }
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
        // Swap out join blocks.
        FatesJoin join = new FatesJoin(fileData.getCastleJoin());
        for(JoinBlock j : join.getBlocks()) {
            for(FatesCharacter c : selected) {
                if(j.getCharacter().equals(c.getPid())) {
                    j.setCharacter(fatesCharacters.getReplacement(selected, c.getPid()).getPid());
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
                    block.setCharacter(fatesCharacters.getReplacement(selected, c.getPid()).getPid());
                    join.getBlocks().add(block);
                    break;
                }
            }
        }

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
        return c.getPid().equals(b.getPid()) || c.getPidA().equals(b.getPid()) || c.getPidB().equals(b.getPid())
                || c.getPidC().equals(b.getPid());
    }
}
