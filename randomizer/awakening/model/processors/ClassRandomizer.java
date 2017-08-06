package randomizer.awakening.model.processors;

import randomizer.awakening.model.structures.ACharacter;
import randomizer.awakening.singletons.ACharacters;
import randomizer.awakening.singletons.AJobs;
import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Job;

import java.util.List;
import java.util.Random;

public class ClassRandomizer {
    private static List<Job> maleBaseJobs = AJobs.getInstance().getMaleBaseClasses();
    private static List<Job> malePromotedJobs = AJobs.getInstance().getMalePromotedClasses();
    private static List<Job> femaleBaseJobs = AJobs.getInstance().getFemaleBaseClasses();
    private static List<Job> femalePromotedJobs = AJobs.getInstance().getFemalePromotedClasses();
    private static List<Job> malePlayerJobs = AJobs.getInstance().getEligibleJobs(true, 0x14);
    private static List<Job> femalePlayerJobs = AJobs.getInstance().getEligibleJobs(false, 0x14);
    private static Random random = new Random();

    public static void randomizeClasses(List<ACharacter> characters) {
        int pc = 0;
        for(ACharacter c : characters) {
            ACharacter target = ACharacters.getInstance().getByPid(c.getTargetPid());
            
            // Current character class.
            if(c.getCharacterType() != CharacterType.Player) {
                c.setCharacterClass(generateClass(c.isMale(), target.isPromoted()));
            }
            else if(c.getId() == 1) {
                pc++;
                c.setCharacterClass(malePlayerJobs.get(random.nextInt(malePlayerJobs.size())));
            }
            else if(c.getId() == 2) {
                pc++;
                c.setCharacterClass(femalePlayerJobs.get(random.nextInt(femalePlayerJobs.size())));
            }
            c.setReclasses(generateReclasses(c.isMale()));
        }

        // The players classes must use the same weapons.
        if(pc == 2) {
            while(characters.get(0).getCharacterClass().getItemType()
                    != characters.get(1).getCharacterClass().getItemType()) {
                characters.get(1).setCharacterClass(femalePlayerJobs.get(random.nextInt(femalePlayerJobs.size())));
            }
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

    private static Job[] generateReclasses(boolean isMale) {
        Job[] reclasses = new Job[6];
        int start = isMale ? 0 : 3;
        reclasses[start] = generateClass(isMale, false);
        Job tmp = generateClass(isMale, false);
        while(tmp.getJid().equals(reclasses[start].getJid()))
            tmp = generateClass(isMale, false);
        reclasses[start + 1] = tmp;
        tmp = generateClass(isMale, false);
        while(tmp.getJid().equals(reclasses[start].getJid()) || tmp.getJid().equals(reclasses[start + 1].getJid()))
            tmp = generateClass(isMale, false);
        reclasses[start + 2] = tmp;
        return reclasses;
    }
}
