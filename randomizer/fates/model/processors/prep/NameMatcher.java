package randomizer.fates.model.processors.prep;

import randomizer.common.data.FatesData;
import randomizer.common.structures.FEItem;
import randomizer.common.structures.Job;
import randomizer.common.structures.Skill;
import randomizer.common.utils.CompressionUtils;
import randomizer.common.utils.MessageBinUtils;
import randomizer.fates.model.structures.FatesCharacter;

import java.io.File;
import java.util.HashMap;

public class NameMatcher {
    /**
     * Opens up the given GameData text file and assigns characters,
     * skills, items, and classes the correct names for the current
     * region by matching IDs with text.
     *
     * @param file A compressed GameData text file.
     */
    public static void matchNames(File file) {
        String[] lines = MessageBinUtils.extractMessageArchive(CompressionUtils.decompress(file));
        HashMap<String, String> map = new HashMap<>();
        for(int x = 6; x < lines.length; x++) {
            String[] split = lines[x].split(": ");
            map.put(split[0], split[1]);
        }

        // Use the map to assign names.
        for(FatesCharacter c : FatesData.getInstance().getCharacters()) {
            c.setName(map.get(c.getMPid()));
        }
        for(Skill s : FatesData.getInstance().getSkills()) {
            s.setName(map.get("M" + s.getSeid()));
        }
        for(FEItem i : FatesData.getInstance().getItems()) {
            i.setName(map.get("M" + i.getIid()));
        }
        for(Job j : FatesData.getInstance().getJobs()) {
            j.setName(map.get("M" + j.getJid().substring(0, j.getJid().length() - 1)));
        }
    }
}
