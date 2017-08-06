package randomizer.awakening.model.processors;

import randomizer.awakening.model.structures.ACharacter;
import randomizer.awakening.singletons.AChapters;
import randomizer.awakening.singletons.ACharacters;
import randomizer.awakening.singletons.AGui;
import randomizer.common.enums.ChapterType;
import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Chapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Responsible for assigning characters new spots in the join
 * order and creating new parent/child combinations.
 */
public class CharacterMatcher {
    private static boolean[] options = AGui.getInstance().getSelectedOptions();

    public static void matchCharacters(List<ACharacter> characters) {
        // No join order randomization.
        if(!options[3]) {
            for(ACharacter c : characters)
                c.setTargetPid(c.getPid());
            return;
        }

        // Sort by character type.
        List<ACharacter> firstGen = new ArrayList<>();
        List<ACharacter> secondGen = new ArrayList<>();
        List<ACharacter> npcs = new ArrayList<>();
        for(ACharacter c : characters) {
            if(c.getCharacterType() == CharacterType.FirstGen)
                firstGen.add(c);
            else if(c.getCharacterType() == CharacterType.SecondGen)
                secondGen.add(c);
            else if(c.getCharacterType() == CharacterType.Player) // Players can only randomize classes and stats.
                c.setTargetPid(c.getPid());
            else
                npcs.add(c);
        }

        // Perform matching.
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

    private static void assignSameSexTargets(List<ACharacter> characters) {
        List<ACharacter> male = new ArrayList<>();
        List<ACharacter> female = new ArrayList<>();
        for(ACharacter c : characters) {
            if(c.isMale())
                male.add(c);
            else
                female.add(c);
        }
        assignTargets(male);
        assignTargets(female);
    }

    private static void assignTargets(List<ACharacter> characters) {
        List<String> pids = new ArrayList<>();
        for (ACharacter character : characters) {
            pids.add(character.getPid());
        }
        Collections.shuffle(pids);
        for(int x = 0; x < pids.size(); x++) {
            characters.get(x).setTargetPid(pids.get(x));
        }
    }

    private static void assignParents(List<ACharacter> firstGen, List<ACharacter> secondGen) {
        List<Chapter> fatesChapters = AChapters.getInstance().getChaptersByType(ChapterType.Child);
        for(Chapter c : fatesChapters) {
            ACharacter parent = ACharacters.getInstance().getReplacement(firstGen, c.getParentPid());
            ACharacter child = ACharacters.getInstance().getReplacement(secondGen, c.getChildPid());
            if(parent != null && child != null) {
                parent.setLinkedPid(child.getPid());
                child.setLinkedPid(parent.getPid());
            }
        }
    }
}
