package randomizer.awakening.model.processors.chapter;

import randomizer.awakening.model.structures.ACharacter;
import randomizer.awakening.singletons.AChapters;
import randomizer.awakening.singletons.ACharacters;
import randomizer.awakening.singletons.AFiles;
import randomizer.awakening.singletons.AGui;
import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Chapter;
import randomizer.common.utils.CompressionUtils;
import randomizer.common.utils.MessageBinUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class TextHandler {
    private static boolean[] options = AGui.getInstance().getSelectedOptions();
    private static ACharacters aCharacters = ACharacters.getInstance();
    private static AChapters aChapters = AChapters.getInstance();
    private static AFiles fileData = AFiles.getInstance();

    public static void randomizeText(List<ACharacter> characters) {
        List<Chapter> chapters = aChapters.getChapters();
        for(Chapter c : chapters) {
            randomizeText(fileData.getText().get(c.getCid()), characters);
        }
    }

    private static void randomizeText(File file, List<ACharacter> characters) {
        try {
            List<String> text = Files.readAllLines(file.toPath());
            for(int x = 0; x < text.size(); x++) {
                String s = text.get(x);
                if(s.startsWith("MID_")) {
                    String[] split = s.split(": ", 2);
                    for(ACharacter ch : characters) {
                        if(ch.getCharacterType() == CharacterType.Player)
                            continue;
                        split[1] = split[1].replaceAll(ch.getTaglessPid(), ch.getTaglessPid() + "TMP");
                        if(options[6])
                            split[1] = split[1].replaceAll(ch.getSound(), ch.getSound() + "TMP");
                        split[1] = split[1].replaceAll(ch.getName(), ch.getName() + "TMP");
                    }
                    for(ACharacter ch : characters) {
                        if(ch.getCharacterType() == CharacterType.Player)
                            continue;
                        ACharacter target = aCharacters.getByPid(ch.getTargetPid());
                        split[1] = split[1].replaceAll(target.getTaglessPid() +  "TMP", ch.getTaglessPid());
                        if(options[6])
                            split[1] = split[1].replaceAll(target.getSound() + "TMP", ch.getSound());
                        split[1] = split[1].replaceAll(target.getName() + "TMP", ch.getName());
                    }
                    text.set(x, split[0] + ": " + split[1]);
                }
            }
            String[] lines = new String[text.size()];
            for(int x = 0; x < text.size(); x++)
                lines[x] = text.get(x);
            byte[] raw = MessageBinUtils.makeMessageArchive(lines);
            Files.write(file.toPath(), CompressionUtils.compress(raw));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
