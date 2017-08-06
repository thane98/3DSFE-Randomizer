package randomizer.awakening.model.processors.prep;

import randomizer.awakening.model.structures.ACharacter;
import randomizer.awakening.singletons.ACharacters;
import randomizer.awakening.singletons.AItems;
import randomizer.awakening.singletons.AJobs;
import randomizer.awakening.singletons.ASkills;
import randomizer.common.enums.CharacterType;
import randomizer.common.structures.FEItem;
import randomizer.common.structures.Job;
import randomizer.common.structures.Skill;
import randomizer.common.utils.CompressionUtils;
import randomizer.common.utils.MessageBinUtils;

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
            if(split.length > 1) {
                map.put(split[0], split[1]);
            }
        }

        // Use the map to assign names.
        for(ACharacter c : ACharacters.getInstance().getCharacters()) {
            if(c.getCharacterType() != CharacterType.Player)
                c.setName(map.get(c.getMPid()));
        }
        for(Skill s : ASkills.getInstance().getSkills()) {
            s.setName(map.get("M" + s.getSeid()));
        }
        for(FEItem i : AItems.getInstance().getItems()) {
            i.setName(map.get("M" + i.getIid()));
        }
        for(Job j : AJobs.getInstance().getJobs()) {
            j.setName(map.get("M" + j.getJid().substring(0, j.getJid().length() - 1)));
        }
    }
}
