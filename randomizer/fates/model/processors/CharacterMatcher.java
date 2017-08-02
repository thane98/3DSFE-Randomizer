package randomizer.fates.model.processors;

import randomizer.common.enums.CharacterType;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.FatesData;
import randomizer.fates.singletons.FatesGui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Responsible for assigning characters new spots in the join
 * order and creating new parent/child combinations.
 */
class CharacterMatcher {
    private static boolean[] options = FatesGui.getInstance().getSelectedOptions();

    static void matchCharacters(List<FatesCharacter> characters) {
        if(!options[3]) {
            for(FatesCharacter c : characters)
                c.setTargetPid(c.getPid());
            return;
        }
        List<FatesCharacter> firstGen = new ArrayList<>();
        List<FatesCharacter> secondGen = new ArrayList<>();
        List<FatesCharacter> npcs = new ArrayList<>();
        for(FatesCharacter c : characters) {
            if(c.getCharacterType() == CharacterType.FirstGen)
                firstGen.add(c);
            else if(c.getCharacterType() == CharacterType.SecondGen)
                secondGen.add(c);
            else if(c.getCharacterType() == CharacterType.Player) // Players can only randomize classes and stats.
                c.setTargetPid(c.getPid());
            else
                npcs.add(c);
        }
        if(options[4]) {
            assignSameSexTargets(firstGen);
            assignSameSexTargets(secondGen);
            assignSameSexTargets(npcs);
        }
        else {
            assignTargets(firstGen);
            assignTargets(secondGen);
            assignTargets(npcs);
        }
        assignParents(firstGen, secondGen);
    }

    private static void assignSameSexTargets(List<FatesCharacter> characters) {
        List<FatesCharacter> male = new ArrayList<>();
        List<FatesCharacter> female = new ArrayList<>();
        for(FatesCharacter c : characters) {
            if(c.isMale())
                male.add(c);
            else
                female.add(c);
        }
        assignTargets(male);
        assignTargets(female);
    }

    private static void assignTargets(List<FatesCharacter> characters) {
        List<String> pids = new ArrayList<>();
        for (FatesCharacter character : characters) {
            pids.add(character.getPid());
        }
        Collections.shuffle(pids);
        for(int x = 0; x < pids.size(); x++) {
            characters.get(x).setTargetPid(pids.get(x));
        }
    }

    private static void assignParents(List<FatesCharacter> firstGen, List<FatesCharacter> secondGen) {
        List<String> pids = new ArrayList<>();
        Random random = new Random();
        for (FatesCharacter aFirstGen : firstGen) {
            pids.add(aFirstGen.getPid());
        }
        for(FatesCharacter c : secondGen) {
            String parentPid = pids.get(random.nextInt(pids.size()));
            FatesCharacter parent = FatesData.getInstance().getByPid(parentPid);
            pids.remove(parentPid);
            c.setLinkedPid(parentPid);
            parent.setLinkedPid(c.getPid());
        }
    }
}
