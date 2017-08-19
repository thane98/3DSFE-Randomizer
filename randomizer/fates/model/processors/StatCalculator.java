package randomizer.fates.model.processors;

import randomizer.common.structures.Skill;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.FatesGui;
import randomizer.fates.singletons.FatesSkills;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class StatCalculator {
    private static FatesGui gui = FatesGui.getInstance();
    private static Random random = new Random();

    private static List<Skill> skills = FatesSkills.getInstance().getSelectedSkills();
    private static List<Skill> personalSkills = FatesSkills.getInstance().getSelectedPersonalSkills();

    public static void randomizeStats(List<FatesCharacter> characters) {
        for(FatesCharacter c : characters) {
            if(c.getId() == 0)
                continue;

            // Randomize base stats, growths, and modifiers.
            c.setStats(calculateStats(c.getStats(), gui.getBaseStatPasses(), 
                    gui.getBaseStatMin(), gui.getBaseStatMax(), true));
            c.setGrowths(calculateStats(c.getGrowths(), gui.getGrowthPasses(),
                    gui.getGrowthMin(), gui.getGrowthMax(), true));
            c.setModifiers(calculateStats(c.getModifiers(), gui.getModPasses(),
                    gui.getModMin(), gui.getModMax(), false));

            // Randomize skills.
            if(gui.getSelectedOptions()[1]) {
                c.setSkills(randomizeSkills(c.getSkills()));
                c.setPersonSkill(personalSkills.get(random.nextInt(personalSkills.size())).getId());
            }
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

    private static Skill[] randomizeSkills(Skill[] original) {
        Skill[] randomized = new Skill[5];
        for(int x = 0; x < original.length; x++) {
            if(original[x] == null)
                break;
            Skill generated = skills.get(random.nextInt(skills.size()));
            for(int y = 0; y < x; y++) {
                while(generated.getId() == randomized[y].getId())
                    generated = skills.get(random.nextInt(skills.size()));
            }
            randomized[x] = generated;
        }
        return randomized;
    }
}
