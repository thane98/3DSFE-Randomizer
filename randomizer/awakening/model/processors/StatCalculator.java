package randomizer.awakening.model.processors;

import randomizer.awakening.model.structures.ACharacter;
import randomizer.awakening.singletons.ACharacters;
import randomizer.awakening.singletons.AGui;
import randomizer.common.structures.Skill;

import java.util.List;
import java.util.Random;

public class StatCalculator {
    private static AGui gui = AGui.getInstance();
    private static Random random = new Random();

    //private static List<Skill> skills = ASkills.getInstance().getSelectedSkills();

    public static void randomizeStats(List<ACharacter> characters) {
        for(ACharacter c : characters) {
            if(c.getId() == 0 || c.hasSwappedStats())
                continue;
            ACharacter target = ACharacters.getInstance().getByPid(c.getTargetPid());

            // Randomize base stats, growths, and modifiers.
            byte[] originalStats = c.getStats();
            byte[] originalMods = c.getModifiers();
            Skill[] originalSkills = c.getSkills();
            byte originalLevel = c.getLevel();
            c.setLevel(target.getLevel());
            target.setLevel(originalLevel);
            c.setStats(calculateStats(target.getStats(), gui.getBaseStatPasses(), 
                    gui.getBaseStatMin(), gui.getBaseStatMax(), true));
            c.setModifiers(calculateStats(target.getModifiers(), gui.getModPasses(),
                    gui.getModMin(), gui.getModMax(), false));
            target.setStats(calculateStats(originalStats, gui.getBaseStatPasses(),
                    gui.getBaseStatMin(), gui.getBaseStatMax(), true));
            target.setModifiers(calculateStats(originalMods, gui.getModPasses(),
                    gui.getModMin(), gui.getModMax(), false));

            // Randomize skills.
            if(gui.getSelectedOptions()[1]) {
                //c.setSkills(randomizeSkills(target.getSkills()));
                //target.setSkills(randomizeSkills(originalSkills));
            }
            c.setHasSwappedStats(true);
            target.setHasSwappedStats(true);
        }
    }

    private static byte[] calculateStats(byte[] input, int passes, int min, int max, boolean healthBias)
    {
        Random random = new Random();
        for(int x = 0; x < passes; x++)
        {
            int targetOne = random.nextInt(8);
            int targetTwo = random.nextInt(healthBias ? 10 : 8);
            if(targetTwo > 7)
                targetTwo = 0;
            if(input[targetOne] <= min || input[targetTwo] >= max || targetOne == targetTwo)
                continue;
            input[targetOne] = (byte) (input[targetOne] - 1);
            input[targetTwo] = (byte) (input[targetTwo] + 1);
        }
        return input;
    }

//    private static Skill[] randomizeSkills(Skill[] original) {
//        Skill[] randomized = new Skill[5];
//        for(int x = 0; x < original.length; x++) {
//            if(original[x] == null)
//                break;
//            Skill generated = skills.get(random.nextInt(skills.size()));
//            for(int y = 0; y < x; y++) {
//                while(generated.getId() == randomized[y].getId())
//                    generated = skills.get(random.nextInt(skills.size()));
//            }
//            randomized[x] = generated;
//        }
//        return randomized;
//    }
}
