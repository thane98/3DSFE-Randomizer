package randomizer.awakening.model.processors.global;

import feflib.awakening.data.person.ACharacterBlock;
import feflib.awakening.data.person.AwakeningPerson;
import randomizer.awakening.model.structures.ACharacter;
import randomizer.awakening.singletons.AFiles;
import randomizer.awakening.singletons.AGui;
import randomizer.awakening.singletons.AJobs;
import randomizer.common.enums.CharacterType;
import randomizer.common.utils.CompressionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class StaticHandler {
    private static boolean[] options = AGui.getInstance().getSelectedOptions();

    public static void randomizeCharacters(AwakeningPerson data, List<ACharacter> characters) {
        for(ACharacter c : characters) {
            if(c.getId() == 0)
                continue;
            ACharacterBlock b = data.getCharacters().get(c.getId() + 1); // Offset by 1 because of AVATAR_N
            if(options[0]) {
                b.setJob(c.getCharacterClass().getJid());
                b.setWeaponRanks(AJobs.getInstance().generateWeaponsRanks(c.getCharacterClass()));
            }

            //writeStats(c, b);
        }

        try {
            Files.write(AFiles.getInstance().getCharacterFile().toPath(),
                    CompressionUtils.compress(data.getRaw()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeStats(ACharacter c, ACharacterBlock b) {
        b.setStats(c.getStats());
        b.setModifiers(c.getModifiers());
        if(options[3]) {
            b.setLevel(c.getLevel());
            if(c.getCharacterType() == CharacterType.SecondGen) {
                b.setParent(c.getLinkedPid());
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
        }
    }
}
