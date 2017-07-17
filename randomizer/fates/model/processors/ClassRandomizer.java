package randomizer.fates.model.processors;

import randomizer.common.data.FatesData;
import randomizer.common.data.FatesGui;
import randomizer.common.structures.Job;
import randomizer.fates.model.structures.FatesCharacter;

import java.util.List;
import java.util.Random;

public class ClassRandomizer {
    private static boolean[] options = FatesGui.getInstance().getSelectedOptions();
    private static List<Job> maleBaseJobs = FatesData.getInstance().getMaleBaseClasses();
    private static List<Job> malePromotedJobs = FatesData.getInstance().getMalePromotedClasses();
    private static List<Job> femaleBaseJobs = FatesData.getInstance().getFemaleBaseClasses();
    private static List<Job> femalePromotedJobs = FatesData.getInstance().getFemalePromotedClasses();
    private static Random random = new Random();

    static void randomizeClasses(List<FatesCharacter> characters) {
        for(FatesCharacter c : characters) {
            FatesCharacter target = FatesData.getInstance().getByPid(c.getTargetPid());
            
            // Current character class.
            c.setCharacterClass(generateClass(c.isMale(), target.isPromoted()));

            // Reclasses.
            Job[] reclasses = new Job[2];
            reclasses[0] = generateClass(c.isMale(), false);
            Job tmp = generateClass(c.isMale(), false);
            while(tmp.getJid().equals(reclasses[0].getJid()))
                tmp = generateClass(c.isMale(), false);
            reclasses[1] = tmp;
            c.setReclasses(reclasses);
        }
    }
    
    private static Job generateClass(boolean male, boolean promoted) {
        if(male) {
            if(promoted) {
                return malePromotedJobs.get(random.nextInt(malePromotedJobs.size()));
            }
            else {
                return maleBaseJobs.get(random.nextInt(maleBaseJobs.size()));
            }
        }
        else {
            if(promoted) {
                return femalePromotedJobs.get(random.nextInt(femalePromotedJobs.size()));
            }
            else {
                return femaleBaseJobs.get(random.nextInt(femaleBaseJobs.size()));
            }
        }
    }
}
