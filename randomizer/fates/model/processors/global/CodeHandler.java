package randomizer.fates.model.processors.global;

import randomizer.common.utils.BinUtils;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.FatesFiles;
import randomizer.fates.singletons.FatesItems;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class CodeHandler {
    private static final int MALE_CLASS_OFFSET = -0x28;
    private static final int FEMALE_CLASS_OFFSET = 0xC;
    private static final int FEMALE_CREATION_OFFSET = 0x74;
    private static final byte[] CREATION_SEARCH_BYTES = {
            (byte) 0xEA, 0x4A, 0x49, 0x44, 0x5F
    };
    private static final byte[] CLASS_SEARCH_BYTES = {
            0x43, 0x49, 0x44, 0x5F, 0x41, 0x30, 0x30, 0x30
    };

    public static void process(List<FatesCharacter> selected) {
        try {
            // Find label offsets in code.bin
            byte[] raw = Files.readAllBytes(FatesFiles.getInstance().getCode().toPath());
            int maleCreationOffset = BinUtils.search(CREATION_SEARCH_BYTES, raw) + 1;
            int femaleCreationOffset = maleCreationOffset + FEMALE_CREATION_OFFSET;
            int classOffset = BinUtils.search(CLASS_SEARCH_BYTES, raw);
            int maleClassOffset = classOffset + MALE_CLASS_OFFSET;
            int femaleClassOffset = classOffset + FEMALE_CLASS_OFFSET;

            for(FatesCharacter c : selected) {
                if(c.getId() > 2)
                    break;

                // Male player.
                if(c.getId() == 1) {
                    byte[] itemBytes = FatesItems.getInstance().generateDebugItem(c.getCharacterClass())
                            .getIid().getBytes("shift-jis");
                    byte[] maleClassBytes = c.getCharacterClass().getJid().getBytes("shift-jis");
                    for(int x = 0; x < 0x14; x++) {
                        raw[maleCreationOffset + x] = 0;
                        raw[maleClassOffset + x] = 0;
                    }
                    for(int x = 0; x < maleClassBytes.length; x++) {
                        raw[maleCreationOffset + x] = maleClassBytes[x];
                        raw[maleClassOffset + x] = maleClassBytes[x];
                    }
                    System.arraycopy(itemBytes, 0, raw, classOffset - 16, itemBytes.length);
                }

                // Female player.
                if(c.getId() == 2) {
                    byte[] itemBytes = FatesItems.getInstance().generateDebugItem(c.getCharacterClass())
                            .getIid().getBytes("shift-jis");
                    byte[] femaleClassBytes = c.getCharacterClass().getJid().getBytes("shift-jis");
                    for(int x = 0; x < 0x14; x++) {
                        raw[femaleCreationOffset + x] = 0;
                        raw[femaleClassOffset + x] = 0;
                    }
                    for(int x = 0; x < femaleClassBytes.length; x++) {
                        raw[femaleCreationOffset + x] = femaleClassBytes[x];
                        raw[femaleClassOffset + x] = femaleClassBytes[x];
                    }
                    System.arraycopy(itemBytes, 0, raw, classOffset - 16, itemBytes.length);
                }
            }
            Files.write(FatesFiles.getInstance().getCode().toPath(), raw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
