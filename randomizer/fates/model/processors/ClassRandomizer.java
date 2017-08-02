package randomizer.fates.model.processors;

import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Job;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.FatesData;
import randomizer.fates.singletons.FatesGui;

import java.util.List;
import java.util.Random;

public class ClassRandomizer {
    private static boolean[] options = FatesGui.getInstance().getSelectedOptions();
    private static List<Job> maleBaseJobs = FatesData.getInstance().getMaleBaseClasses();
    private static List<Job> malePromotedJobs = FatesData.getInstance().getMalePromotedClasses();
    private static List<Job> femaleBaseJobs = FatesData.getInstance().getFemaleBaseClasses();
    private static List<Job> femalePromotedJobs = FatesData.getInstance().getFemalePromotedClasses();
    private static List<Job> malePlayerJobs = FatesData.getInstance().getEligibleJobs(true, 0x14);
    private static List<Job> femalePlayerJobs = FatesData.getInstance().getEligibleJobs(false, 0x14);
    private static Random random = new Random();

    static void randomizeClasses(List<FatesCharacter> characters) {
        for(FatesCharacter c : characters) {
            FatesCharacter target = FatesData.getInstance().getByPid(c.getTargetPid());
            
            // Current character class.
            if(c.getCharacterType() != CharacterType.Player) {
                c.setCharacterClass(generateClass(c.isMale(), target.isPromoted()));
            }

            // Reclasses.
            Job[] reclasses = new Job[2];
            reclasses[0] = generateClass(c.isMale(), false);
            Job tmp = generateClass(c.isMale(), false);
            while(tmp.getJid().equals(reclasses[0].getJid()))
                tmp = generateClass(c.isMale(), false);
            reclasses[1] = tmp;
            c.setReclasses(reclasses);
        }

        // Player classes.
        if(FatesGui.getInstance().getSelectedCharacters()[0]) {
            characters.get(0).setCharacterClass(malePlayerJobs.get(random.nextInt(malePlayerJobs.size())));
        }
        if(FatesGui.getInstance().getSelectedCharacters()[1]) {
            characters.get(1).setCharacterClass(femalePlayerJobs.get(random.nextInt(femalePlayerJobs.size())));
        }
        if(FatesGui.getInstance().getSelectedCharacters()[0] && FatesGui.getInstance().getSelectedCharacters()[1]) {
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
}
