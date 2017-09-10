package randomizer.fates.model.processors.prep;

import randomizer.common.enums.CharacterType;
import randomizer.common.structures.FEItem;
import randomizer.common.structures.Job;
import randomizer.common.structures.Skill;
import randomizer.common.utils.CompressionUtils;
import randomizer.common.utils.MessageBinUtils;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.FatesCharacters;
import randomizer.fates.singletons.FatesItems;
import randomizer.fates.singletons.FatesJobs;
import randomizer.fates.singletons.FatesSkills;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class NameMatcher {
    /**
     * Opens up the given GameData text file and assigns characters,
     * skills, items, and classes the correct names for the current
     * region by matching IDs with text.
     *
     * @param file A compressed GameData text file.
     */
    public static void matchNames(File file) {
        if(file == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "matchNames. file must not be null.");

        // Decompress the file and extract it's text contents.
        // Split each line and store the result in a map.
        String[] lines = MessageBinUtils.extractMessageArchive(CompressionUtils.decompress(file));
        Map<String, String> map = new TreeMap<>();
        for(int x = 6; x < lines.length; x++) {
            String[] split = lines[x].split(": ");
            map.put(split[0], split[1]);
        }

        // Use the map to assign names.
        for(FatesCharacter c : FatesCharacters.getInstance().getCharacters()) {
            if(c.getCharacterType() != CharacterType.Player && !c.getPid().startsWith("PID_カンナ")) {
                if(map.containsKey(c.getMPid()))
                    c.setName(map.get(c.getMPid()));
            }
        }
        for(Skill s : FatesSkills.getInstance().getSkills()) {
            s.setName(map.get("M" + s.getSeid()));
        }
        for(FEItem i : FatesItems.getInstance().getItems()) {
            i.setName(map.get("M" + i.getIid()));
        }
        for(Job j : FatesJobs.getInstance().getJobs()) {
            j.setName(map.get("M" + j.getJid().substring(0, j.getJid().length() - 1)));
        }
    }
}
