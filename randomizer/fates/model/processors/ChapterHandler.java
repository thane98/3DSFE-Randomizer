package randomizer.fates.model.processors;

import feflib.fates.castle.join.FatesJoin;
import feflib.fates.castle.join.JoinBlock;
import feflib.fates.gamedata.dispo.DispoBlock;
import feflib.fates.gamedata.dispo.DispoFaction;
import feflib.fates.gamedata.dispo.FatesDispo;
import feflib.fates.gamedata.person.FatesPerson;
import feflib.fates.gamedata.person.PersonBlock;
import randomizer.common.data.FatesData;
import randomizer.common.data.FatesFileData;
import randomizer.common.data.FatesGui;
import randomizer.common.structures.Chapter;
import randomizer.common.utils.CompressionUtils;
import randomizer.fates.model.structures.FatesCharacter;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class ChapterHandler {
    private static boolean[] options = FatesGui.getInstance().getSelectedOptions();
    private static boolean[] experimental = FatesGui.getInstance().getSelectedExperimentalOptions();
    private static FatesData fatesData = FatesData.getInstance();
    private static FatesFileData fileData = FatesFileData.getInstance();

    static void randomizeChapterData(List<FatesCharacter> selected) {
        List<Chapter> chapters = fatesData.getSelectedChapters();
        for(Chapter c : chapters) {
            HashMap<String, List<String>> aliasMap = randomizePerson(c, selected);
            randomizeDispo(c, selected, aliasMap);
        }
        randomizeJoin(selected);
    }

    private static HashMap<String, List<String>> randomizePerson(Chapter chapter, List<FatesCharacter> selected) {
        HashMap<String, List<String>> aliasMap = new HashMap<>();
        FatesPerson person = new FatesPerson(fileData.getPerson().get(chapter.getCid()));
        for(PersonBlock p : person.getCharacters()) {
            p.setAid(p.getAid() + "TMP"); // Fix to prevent replacing two characters at once.
        }
        for(PersonBlock p : person.getCharacters()) {
            for(FatesCharacter c : selected) {
                FatesCharacter target = fatesData.getByPid(c.getTargetPid());
                if(p.getAid().equals(target.getAid() + "TMP")) {
                    p.setAid(c.getAid());
                    p.setFid(c.getFid());
                    p.setMPid(c.getMPid());
                    p.setMPidH(c.getMPidH());
                    p.setClasses(new short[] { c.getCharacterClass().getId(), c.getCharacterClass().getTiedJob() });
                    p.setReclasses(new short[] { c.getReclasses()[0].getId(), c.getReclasses()[1].getId() });
                    p.setWeaponRanks(fatesData.generateWeaponsRanks(c.getCharacterClass()));
                    if(aliasMap.containsKey(c.getPid())) {
                        aliasMap.get(c.getPid()).add(p.getPid());
                    }
                    else {
                        List<String> aliases = new ArrayList<>();
                        aliases.add(p.getPid());
                        aliasMap.put(c.getPid(), aliases);
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
                    FatesCharacter target = fatesData.getByPid(c.getTargetPid());
                    if(target.getPid().equals(b.getPid())) {
                        b.setPid(c.getPid());
                        b.setItem(fatesData.generateItem(c.getCharacterClass()).getIid(), 0);
                        break;
                    }
                    else if(aliasMap.get(c.getPid()) != null) {
                        for(String s : aliasMap.get(c.getPid())) {
                            if(s.equals(b.getPid())) {
                                b.setItem(fatesData.generateItem(c.getCharacterClass()).getIid(), 0);
                                if(s.equals("PID_A002_ボス")) { // Workaround for Chapter 2 Kaze.
                                    b.setItem(fatesData.generateItem(c.getCharacterClass()).getIid(), 1);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        try {
            Files.write(fileData.getDispos().get(chapter.getCid()).toPath(),
                    CompressionUtils.compress(dispo.serialize()));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void randomizeJoin(List<FatesCharacter> selected) {
        FatesJoin join = new FatesJoin(fileData.getCastleJoin());
        for(JoinBlock j : join.getBlocks()) {
            for(FatesCharacter c : selected) {
                if(j.getCharacter().equals(c.getPid())) {
                    j.setCharacter(fatesData.getReplacement(selected, c.getPid()).getPid());
                    break;
                }
            }
        }
        if(options[8]) {
            JoinBlock block = new JoinBlock();
            block.setBirthrightJoin("CID_A007");
            block.setConquestJoin("CID_B007");
            block.setRevelationJoin("CID_C007");
            block.setUnknownOne(join.getBlocks().get(0).getUnknownOne());
            block.setUnknownTwo(join.getBlocks().get(0).getUnknownTwo());
            for(FatesCharacter c : selected) {
                if(c.getPid().equals("PID_アンナ")) {
                    block.setCharacter(fatesData.getReplacement(selected, c.getPid()).getPid());
                    join.getBlocks().add(block);
                    break;
                }
            }
        }
        try {
            Files.write(fileData.getCastleJoin().toPath(),
                    CompressionUtils.compress(join.serialize()));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
