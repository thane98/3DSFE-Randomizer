package randomizer.fates.model.processors.chapter;

import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Chapter;
import randomizer.common.utils.CompressionUtils;
import randomizer.common.utils.MessageBinUtils;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.FatesChapters;
import randomizer.fates.singletons.FatesCharacters;
import randomizer.fates.singletons.FatesFiles;
import randomizer.fates.singletons.FatesGui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class TextHandler {
    private static boolean[] options = FatesGui.getInstance().getSelectedOptions();
    private static FatesCharacters fatesCharacters = FatesCharacters.getInstance();
    private static FatesChapters fatesChapters = FatesChapters.getInstance();
    private static FatesFiles fileData = FatesFiles.getInstance();

    public static void randomizeText() {
        List<FatesCharacter> characters = fatesCharacters.getWorkingCharacters();
        List<Chapter> chapters = fatesChapters.getSelectedChapters();
        for(Chapter c : chapters)
            randomizeText(fileData.getText().get(c.getCid()), characters);
        randomizeText(fileData.getGMap(), characters);
    }

    private static void randomizeText(File file, List<FatesCharacter> characters) {
        if(file == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "randomizeText. file must not be null.");
        if(characters == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "randomizeText. characters must not be null.");

        try {
            List<String> text = Files.readAllLines(file.toPath());

            for(int x = 0; x < text.size(); x++) {
                String s = text.get(x);

                // Only valid text data should be modified.
                if(s.startsWith("MID_")) {
                    // Assign temporary labels to avoid getting duplicate copies of characters.
                    String[] split = s.split(": ", 2);
                    for(FatesCharacter ch : characters) {
                        if(ch.getCharacterType() == CharacterType.Player)
                            continue;
                        if(ch.getCharacterType() == CharacterType.NPC && file.getName().startsWith("X001")) // Fix for broken Mozu portrait.
                            continue;
                        split[1] = split[1].replaceAll(ch.getTaglessPid(), ch.getTaglessPid() + "TMP");
                        if(options[6])
                            split[1] = split[1].replaceAll(ch.getSound(), ch.getSound() + "TMP");
                        split[1] = split[1].replaceAll(ch.getName(), ch.getName() + "TMP");
                    }

                    // Swap character names, portraits, and voice lines.
                    for(FatesCharacter ch : characters) {
                        if(ch.getCharacterType() == CharacterType.Player)
                            continue;
                        if(ch.getCharacterType() == CharacterType.NPC && file.getName().startsWith("X001")) // Fix for broken Mozu portrait.
                            continue;
                        FatesCharacter target = fatesCharacters.getByPid(ch.getTargetPid());
                        split[1] = split[1].replaceAll(target.getTaglessPid() +  "TMP", ch.getTaglessPid());
                        if(options[6])
                            split[1] = split[1].replaceAll(target.getSound() + "TMP", ch.getSound());
                        split[1] = split[1].replaceAll(target.getName() + "TMP", ch.getName());
                    }
                    text.set(x, split[0] + ": " + split[1]);
                }
            }

            // Write the text back to the binary file for usage in game.
            String[] lines = new String[text.size()];
            text.toArray(lines);
            byte[] raw = MessageBinUtils.makeMessageArchive(lines);
            Files.write(file.toPath(), CompressionUtils.compress(raw));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
