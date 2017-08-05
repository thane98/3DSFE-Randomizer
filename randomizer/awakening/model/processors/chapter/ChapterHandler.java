package randomizer.awakening.model.processors.chapter;

import feflib.awakening.data.dispo.ADispoBlock;
import feflib.awakening.data.dispo.ADispoFaction;
import feflib.awakening.data.dispo.AwakeningDispo;
import feflib.awakening.data.person.ACharacterBlock;
import feflib.awakening.data.person.AwakeningPerson;
import randomizer.awakening.model.structures.ACharacter;
import randomizer.awakening.singletons.*;
import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Chapter;
import randomizer.common.utils.CompressionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChapterHandler {
    private static boolean[] options = AGui.getInstance().getSelectedOptions();
    private static AItems aItems = AItems.getInstance();
    private static ACharacters aCharacters = ACharacters.getInstance();
    private static AJobs aJobs = AJobs.getInstance();
    private static AChapters aChapters = AChapters.getInstance();
    private static AFiles fileData = AFiles.getInstance();

    public static void randomizeChapterData(List<ACharacter> selected) {
        List<Chapter> chapters = aChapters.getChapters();
        for(Chapter c : chapters) {
            HashMap<String, List<String>> aliasMap = randomizePerson(c, selected);
            randomizeDispo(c, selected, aliasMap);
        }
    }

    private static HashMap<String, List<String>> randomizePerson(Chapter chapter, List<ACharacter> selected) {
        HashMap<String, List<String>> aliasMap = new HashMap<>();
        AwakeningPerson person = new AwakeningPerson(fileData.getPerson().get(chapter.getCid()));
        for(ACharacterBlock p : person.getCharacters()) {
            for(ACharacter c : selected) {
                if(c.getCharacterType() == CharacterType.Player)
                    continue;
                ACharacter target = aCharacters.getByPid(c.getTargetPid());
                if(p.getFid().equals(target.getFid())) {
                    // Change character.
                    p.setFid(c.getFid());
                    p.setmPid(c.getMPid());
                    p.setmPidH(c.getMPidH());

                    // Reclass block.
                    p.setJob(c.getCharacterClass().getJid());
                    p.setWeaponRanks(aJobs.generateWeaponsRanks(c.getCharacterClass()));

                    // Add block to alias map.
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
                    CompressionUtils.compress(person.getRaw()));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return aliasMap;
    }

    private static void randomizeDispo(Chapter chapter, List<ACharacter> selected,
                                       HashMap<String, List<String>> aliasMap) {
        AwakeningDispo dispo = new AwakeningDispo(fileData.getDispos().get(chapter.getCid()));
        for(ADispoFaction f : dispo.getFactions()) {
            for(ADispoBlock b : f.getSpawns()) {
                for(ACharacter c : selected) {
                    // Swap out matching dispos and distribute new items.
                    ACharacter target = aCharacters.getByPid(c.getTargetPid());
                    if(pidMatches(target, b)) {
                        b.setPid(c.getPid());
                        b.setItem(0, aItems.generateItem(c.getCharacterClass()).getIid());
                        break;
                    }
                    else if(aliasMap.get(c.getPid()) != null) {
                        // Swap out aliases and distribute items.
                        for(String s : aliasMap.get(c.getPid())) {
                            if(s.equals(b.getPid())) {
                                b.setItem(0, aItems.generateItem(c.getCharacterClass()).getIid());
                                if(s.contains("ボス")) { // Workaround for bugged bosses.
                                    b.setItem(1, aItems.generateItem(c.getCharacterClass()).getIid());
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

    /**
     * Determine if the given randomizer character and dispo block refer to
     * the same character.
     *
     * @param c The randomizer character to check.
     * @param b The dispo block to check.
     * @return A boolean value representing whether or not the blocks match.
     */
    private static boolean pidMatches(ACharacter c, ADispoBlock b) {
        return c.getPid().equals(b.getPid()) || c.getPidA().equals(b.getPid()) || c.getPidB().equals(b.getPid())
                || c.getPidC().equals(b.getPid());
    }
}
