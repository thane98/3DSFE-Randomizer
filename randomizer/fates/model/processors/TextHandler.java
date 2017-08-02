package randomizer.fates.model.processors;

import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Chapter;
import randomizer.common.utils.CompressionUtils;
import randomizer.common.utils.MessageBinUtils;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.FatesData;
import randomizer.fates.singletons.FatesFileData;
import randomizer.fates.singletons.FatesGui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

class TextHandler {
    private static boolean[] options = FatesGui.getInstance().getSelectedOptions();
    private static FatesData fatesData = FatesData.getInstance();
    private static FatesFileData fileData = FatesFileData.getInstance();

    static void randomizeText(List<FatesCharacter> characters) {
        List<Chapter> chapters = fatesData.getSelectedChapters();
        for(Chapter c : chapters) {
            randomizeText(fileData.getText().get(c.getCid()), characters);
        }
        randomizeText(fileData.getGMap(), characters);
    }

    private static void randomizeText(File file, List<FatesCharacter> characters) {
        try {
            List<String> text = Files.readAllLines(file.toPath());
            for(int x = 0; x < text.size(); x++) {
                String s = text.get(x);
                if(s.startsWith("MID_")) {
                    String[] split = s.split(": ", 2);
                    for(FatesCharacter ch : characters) {
                        if(ch.getCharacterType() == CharacterType.Player)
                            continue;
                        split[1] = split[1].replaceAll(ch.getTaglessPid(), ch.getTaglessPid() + "TMP");
                        if(options[6])
                            split[1] = split[1].replaceAll(ch.getSound(), ch.getSound() + "TMP");
                        split[1] = split[1].replaceAll(ch.getName(), ch.getName() + "TMP");
                    }
                    for(FatesCharacter ch : characters) {
                        if(ch.getCharacterType() == CharacterType.Player)
                            continue;
                        FatesCharacter target = fatesData.getByPid(ch.getTargetPid());
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
