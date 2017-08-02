package randomizer.fates.model.processors;

import randomizer.common.utils.BinUtils;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.FatesData;
import randomizer.fates.singletons.FatesFileData;
import randomizer.fates.singletons.FatesGui;

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
            byte[] raw = Files.readAllBytes(FatesFileData.getInstance().getCode().toPath());
            int maleCreationOffset = BinUtils.search(CREATION_SEARCH_BYTES, raw) + 1;
            int femaleCreationOffset = maleCreationOffset + FEMALE_CREATION_OFFSET;
            int classOffset = BinUtils.search(CLASS_SEARCH_BYTES, raw);
            int maleClassOffset = classOffset + MALE_CLASS_OFFSET;
            int femaleClassOffset = classOffset + FEMALE_CLASS_OFFSET;

            // Overwrite job labels.
            if(FatesGui.getInstance().getSelectedCharacters()[0]) {
                byte[] itemBytes = FatesData.getInstance().generateEligibleItem(selected.get(0).getCharacterClass().getItemType(),
                        0xF).getIid().getBytes("shift-jis");
                byte[] maleClassBytes = selected.get(0).getCharacterClass().getJid().getBytes("shift-jis");
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
            if(FatesGui.getInstance().getSelectedCharacters()[1]) { // TODO: Handle case where ONLY fem Corrin is selected.
                byte[] itemBytes = FatesData.getInstance().generateEligibleItem(selected.get(0).getCharacterClass().getItemType(),
                        0xF).getIid().getBytes("shift-jis");
                byte[] femaleClassBytes = selected.get(1).getCharacterClass().getJid().getBytes("shift-jis");
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

            Files.write(FatesFileData.getInstance().getCode().toPath(), raw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
