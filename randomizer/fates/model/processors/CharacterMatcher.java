package randomizer.fates.model.processors;

import randomizer.common.enums.ChapterType;
import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Chapter;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.FatesChapters;
import randomizer.fates.singletons.FatesCharacters;
import randomizer.fates.singletons.FatesGui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Responsible for assigning characters new spots in the join
 * order and creating new parent/child combinations.
 */
public class CharacterMatcher {
    private static boolean[] options = FatesGui.getInstance().getSelectedOptions();

    public static void matchCharacters() {
        List<FatesCharacter> characters = FatesCharacters.getInstance().getWorkingCharacters();

        // No join order randomization.
        if(!options[3]) {
            for(FatesCharacter c : characters)
                c.setTargetPid(c.getPid());
            return;
        }

        // Sort by character type.
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

    private static void assignSameSexTargets(List<FatesCharacter> characters) {
        if(characters == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "assignSameSexTargets. characters must not be null.");

        // Sort by male/female.
        List<FatesCharacter> male = new ArrayList<>();
        List<FatesCharacter> female = new ArrayList<>();
        for(FatesCharacter c : characters) {
            if(c.isMale())
                male.add(c);
            else
                female.add(c);
        }

        // Assign from each list separately.
        assignTargets(male);
        assignTargets(female);
    }

    private static void assignTargets(List<FatesCharacter> characters) {
        if(characters == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "assignTargets. characters must not be null.");

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
        if(firstGen == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "assignParents. firstGen must not be null.");
        if(secondGen == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "assignParents. secondGen must not be null.");

        List<Chapter> fatesChapters = FatesChapters.getInstance().getChaptersByType(ChapterType.Child);
        for(Chapter c : fatesChapters) {
            FatesCharacter parent = FatesCharacters.getInstance().getReplacement(c.getParentPid());
            FatesCharacter child = FatesCharacters.getInstance().getReplacement(c.getChildPid());
            if(parent != null && child != null) {
                parent.setLinkedPid(child.getPid());
                child.setLinkedPid(parent.getPid());
            }
        }
    }
}
