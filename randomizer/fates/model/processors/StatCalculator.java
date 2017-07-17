package randomizer.fates.model.processors;

import randomizer.common.data.FatesData;
import randomizer.common.data.FatesGui;
import randomizer.common.structures.Skill;
import randomizer.fates.model.structures.FatesCharacter;

import java.util.List;
import java.util.Random;

class StatCalculator {
    private static FatesGui gui = FatesGui.getInstance();
    private static Random random = new Random();

    private static List<Skill> skills = FatesData.getInstance().getSelectedSkills();
    private static List<Skill> personalSkills = FatesData.getInstance().getSelectedPersonalSkills();

    static void randomizeStats(List<FatesCharacter> characters) {
        for(FatesCharacter c : characters) {
            if(c.getId() == 0 || c.hasSwappedStats())
                continue;
            FatesCharacter target = FatesData.getInstance().getByPid(c.getTargetPid());

            // Randomize base stats, growths, and modifiers.
            byte[] originalStats = c.getStats();
            byte[] originalGrowths = c.getGrowths();
            byte[] originalMods = c.getModifiers();
            Skill[] originalSkills = c.getSkills();
            byte originalLevel = c.getLevel();
            byte originalInternalLevel = c.getInternalLevel();
            c.setLevel(target.getLevel());
            target.setLevel(originalLevel);
            c.setInternalLevel(target.getInternalLevel());
            target.setInternalLevel(originalInternalLevel);
//            c.setStats(calculateStats(getStatTotal(target.getStats()), (int) gui.getBaseStatVariance().getValue(),
//                    1, (int) gui.getBaseStatMin().getValue(), (int) gui.getBaseStatMax().getValue(), true));
//            c.setGrowths(calculateStats(getStatTotal(target.getGrowths()), (int) gui.getGrowthVariance().getValue(),
//                    5, (int) gui.getGrowthMin().getValue(), (int) gui.getGrowthMax().getValue(), true));
//            c.setModifiers(calculateStats(getStatTotal(target.getModifiers()), (int) gui.getModVariance().getValue(),
//                    1, (int) gui.getModMin().getValue(), (int) gui.getModMax().getValue(), false));
//            target.setStats(calculateStats(getStatTotal(originalStats), (int) gui.getBaseStatVariance().getValue(),
//                    1, (int) gui.getBaseStatMin().getValue(), (int) gui.getBaseStatMax().getValue(), true));
//            target.setGrowths(calculateStats(getStatTotal(originalGrowths), (int) gui.getGrowthVariance().getValue(),
//                    5, (int) gui.getGrowthMin().getValue(), (int) gui.getGrowthMax().getValue(), true));
//            target.setModifiers(calculateStats(getStatTotal(originalMods), (int) gui.getModVariance().getValue(),
//                    1, (int) gui.getModMin().getValue(), (int) gui.getModMax().getValue(), false));

            // Randomize skills.
            if(gui.getSelectedOptions()[1]) {
                c.setSkills(randomizeSkills(target.getSkills()));
                target.setSkills(randomizeSkills(originalSkills));
                c.setPersonSkill(personalSkills.get(random.nextInt(personalSkills.size())).getId());
                target.setPersonSkill(personalSkills.get(random.nextInt(personalSkills.size())).getId());
            }
            c.setHasSwappedStats(true);
            target.setHasSwappedStats(true);
        }
    }

    // TODO: Fix this broken calculator.
    private static byte[] calculateStats(int total, int variance, int increment, int min, int max, boolean healthBias) {
        byte[] stats = new byte[8];
        for(int x = 0; x < 8; x ++) {
            stats[x] = (byte) min;
        }
        total -= min * 8;
        if(total <= 0)
            return stats;
        int newTotal = total + (random.nextInt(variance * 2) - variance);
        int passes = newTotal / increment;
        for(int x = 0; x < passes; x++) {
            int selection;
            if(healthBias)
                selection = random.nextInt(10);
            else
                selection = random.nextInt(8);
            if(selection > 7) {
                if(stats[0] >= max)
                    continue;
                stats[0] += increment;
            }
            else {
                if(stats[selection] >= max)
                    continue;
                stats[selection] += increment;
            }
        }
        return stats;
    }

    private static int getStatTotal(byte[] arr) {
        int count = 0;
        for (byte anArr : arr) count += anArr;
        return count;
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
