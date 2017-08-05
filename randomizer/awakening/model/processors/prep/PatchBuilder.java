package randomizer.awakening.model.processors.prep;

import randomizer.awakening.singletons.AFiles;
import randomizer.common.utils.BinUtils;
import randomizer.common.utils.CompressionUtils;
import randomizer.common.utils.MessageBinUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

public class PatchBuilder {
    /**
     * Copies the relevant patch files from the ROM to a new directory,
     * adds the copied files to the AFiles singleton, and decompresses
     * every bin file.
     */
    public static void createPatch() {
        // Create folder.
        File dir = new File(System.getProperty("user.dir") + "/Patch/romfs");
        if(!dir.getParentFile().exists()) {
            dir.mkdirs();
        }
        else {
            BinUtils.deleteFolder(dir.getParentFile());
            dir.mkdirs();
        }

        // Parent directories for randomizer files.
        File gameDataDir = new File(dir, "data");
        File disposDir = new File(gameDataDir, "Dispos");
        File personDir = new File(gameDataDir, "Person");
        File scriptsDir = new File(dir, "Scripts");
        File textDir = new File(dir, "m");
        gameDataDir.mkdir();
        disposDir.mkdir();
        personDir.mkdir();
        scriptsDir.mkdir();
        textDir.mkdir();

        for(File f : AFiles.getInstance().getOriginalFileList()) {
            File copy = new File(f.getAbsolutePath().replace(AFiles.getInstance().getRom().getAbsolutePath(),
                    dir.getAbsolutePath()));
            if(!copy.getName().endsWith(".lz") && !copy.getName().endsWith(".cmb"))
                copy.mkdirs();
            else
                copy.getParentFile().mkdirs();
            try {
                if(copy.getName().startsWith("static.bin.lz")) {
                    AFiles.getInstance().setCharacterFile(copy);
                    Files.write(copy.toPath(), CompressionUtils.decompress(f));
                }
                else if(BinUtils.isInSubDirectory(disposDir, copy)) {
                    AFiles.getInstance().getDispos().put(getCid(copy.getName()), copy);
                    Files.write(copy.toPath(), CompressionUtils.decompress(f));
                }
                else if(BinUtils.isInSubDirectory(personDir, copy)) {
                    AFiles.getInstance().getPerson().put(getCid(copy.getName()), copy);
                    Files.write(copy.toPath(), CompressionUtils.decompress(f));
                }
                else if(BinUtils.isInSubDirectory(textDir, copy)) {
                    AFiles.getInstance().getText().put(getCid(copy.getName()), copy);
                    Files.write(copy.toPath(), Arrays.asList(MessageBinUtils.extractMessageArchive(
                            CompressionUtils.decompress(f))));
                }
                else if(BinUtils.isInSubDirectory(scriptsDir, copy) && copy.getName().endsWith("_Terrain.cmb")) {
                    AFiles.getInstance().getTerrain().put(getCid(copy.getName()), copy);
                    Files.copy(f.toPath(), copy.toPath());
                }
                else if(BinUtils.isInSubDirectory(scriptsDir, copy)) {
                    AFiles.getInstance().getScript().put(getCid(copy.getName()), copy);
                    Files.copy(f.toPath(), copy.toPath());
                }
                else if(BinUtils.isInSubDirectory(gameDataDir, copy) && copy.getName().equals("GameData.bin.lz")) {
                    AFiles.getInstance().setGameData(copy);
                    Files.write(copy.toPath(), CompressionUtils.decompress(f));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getCid(String fname) {
        return fname.substring(0, fname.indexOf("."));
    }
}
