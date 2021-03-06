package randomizer.fates.model.processors;

import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Job;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.FatesCharacters;
import randomizer.fates.singletons.FatesJobs;

import java.util.List;
import java.util.Random;

public class ClassRandomizer {
    private static List<Job> maleBaseJobs = FatesJobs.getInstance().getMaleBaseClasses();
    private static List<Job> malePromotedJobs = FatesJobs.getInstance().getMalePromotedClasses();
    private static List<Job> femaleBaseJobs = FatesJobs.getInstance().getFemaleBaseClasses();
    private static List<Job> femalePromotedJobs = FatesJobs.getInstance().getFemalePromotedClasses();
    private static List<Job> malePlayerJobs = FatesJobs.getInstance().getEligibleJobs(true, 0x14);
    private static List<Job> femalePlayerJobs = FatesJobs.getInstance().getEligibleJobs(false, 0x14);
    private static Random random = new Random();

    public static void randomizeClasses() {
        List<FatesCharacter> characters = FatesCharacters.getInstance().getWorkingCharacters();

        int pc = 0;
        for(FatesCharacter c : characters) {
            FatesCharacter target = FatesCharacters.getInstance().getByPid(c.getTargetPid());
            
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

        // Both players must be from classes that use the same weapon type.
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
        Job[] reclasses = new Job[2];
        reclasses[0] = generateClass(isMale, false);
        Job tmp = generateClass(isMale, false);
        while(tmp.getJid().equals(reclasses[0].getJid()))
            tmp = generateClass(isMale, false);
        reclasses[1] = tmp;
        return reclasses;
    }
}
