package randomizer.fates.model.processors;

import randomizer.common.data.FatesFileData;
import randomizer.common.utils.BinUtils;
import randomizer.common.utils.CompressionUtils;
import randomizer.common.utils.MessageBinUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

public class FatesPatchBuilder {
    /**
     * Copies the relevant patch files from the ROM to a new directory,
     * adds the copied files to the FatesFileData singleton, and decompresses
     * every bin file.
     */
    public static void createPatch() {
        // Create folder.
        File dir = new File(System.getProperty("user.dir"), "Patch");
        if(!dir.exists()) {
            dir.mkdir();
        }
        else {
            BinUtils.deleteFolder(dir);
            dir.mkdir();
        }

        // Parent directories for randomizer files.
        File gameDataDir = new File(dir, "GameData");
        File disposDir = new File(gameDataDir, "Dispos");
        File personDir = new File(gameDataDir, "Person");
        File scriptsDir = new File(dir, "Scripts");
        File textDir = new File(dir, "m");
        File castleDir = new File(dir, "castle");
        gameDataDir.mkdir();
        disposDir.mkdir();
        personDir.mkdir();
        scriptsDir.mkdir();
        textDir.mkdir();
        castleDir.mkdir();

        for(File f : FatesFileData.getInstance().getOriginalFileList()) {
            File copy = new File(f.getAbsolutePath().replace(FatesFileData.getInstance().getRom().getAbsolutePath(),
                    dir.getAbsolutePath()));
            if(!copy.getName().endsWith(".lz") && !copy.getName().endsWith(".cmb"))
                copy.mkdirs();
            else
                copy.getParentFile().mkdirs();
            try {
                if(f.getName().equals("bev") && f.isDirectory()) {
                    BinUtils.copyFolder(f, copy);
                    FatesFileData.getInstance().setBev(copy);
                }
                else {
                    if(BinUtils.isInSubDirectory(disposDir, copy)) {
                        FatesFileData.getInstance().getDispos().put(getCid(copy.getName()), copy);
                        Files.write(copy.toPath(), CompressionUtils.decompress(f));
                    }
                    else if(BinUtils.isInSubDirectory(personDir, copy)) {
                        FatesFileData.getInstance().getPerson().put(getCid(copy.getName()), copy);
                        Files.write(copy.toPath(), CompressionUtils.decompress(f));
                    }
                    else if(BinUtils.isInSubDirectory(textDir, copy)) {
                        FatesFileData.getInstance().getText().put(getCid(copy.getName()), copy);
                        Files.write(copy.toPath(), Arrays.asList(MessageBinUtils.extractMessageArchive(
                                CompressionUtils.decompress(f))));
                    }
                    else if(BinUtils.isInSubDirectory(scriptsDir, copy)) {
                        FatesFileData.getInstance().getScript().put(getCid(copy.getName()), copy);
                        Files.copy(f.toPath(), copy.toPath());
                    }
                    else if(BinUtils.isInSubDirectory(gameDataDir, copy) && copy.getName().equals("GameData.bin.lz")) {
                        FatesFileData.getInstance().setGameData(copy);
                        Files.write(copy.toPath(), CompressionUtils.decompress(f));
                    }
                    else if(BinUtils.isInSubDirectory(textDir, copy) && copy.getName().equals("GameData.bin.lz")) {
                        FatesFileData.getInstance().setGameDataText(copy);
                        Files.write(copy.toPath(), Arrays.asList(MessageBinUtils.extractMessageArchive(
                                CompressionUtils.decompress(f))));
                    }
                    else if(BinUtils.isInSubDirectory(textDir, copy) && copy.getName().equals("GMap.bin.lz")) {
                        FatesFileData.getInstance().setGMap(copy);
                        Files.write(copy.toPath(), Arrays.asList(MessageBinUtils.extractMessageArchive(
                                CompressionUtils.decompress(f))));
                    }
                    else if(BinUtils.isInSubDirectory(castleDir, copy) && copy.getName().equals("castle_join.bin.lz")) {
                        FatesFileData.getInstance().setCastleJoin(copy);
                        Files.write(copy.toPath(), CompressionUtils.decompress(f));
                    }
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
