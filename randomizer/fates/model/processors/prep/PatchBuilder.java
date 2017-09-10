package randomizer.fates.model.processors.prep;

import randomizer.common.utils.BinUtils;
import randomizer.common.utils.CompressionUtils;
import randomizer.common.utils.MessageBinUtils;
import randomizer.fates.singletons.FatesFiles;
import randomizer.fates.singletons.FatesGui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
        File patchDir = new File(System.getProperty("user.dir"), "Patch");
        if(!dir.exists()) {
            dir.mkdirs();
        }
        else {
            BinUtils.deleteFolder(dir);
            dir.mkdirs();
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

        if(FatesFiles.getInstance().getCode() != null) {
            try {
                // Copy code.bin to the patch directory.
                File file = new File(patchDir, "code.bin");
                Files.copy(FatesFiles.getInstance().getCode().toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                FatesFiles.getInstance().setCode(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Copy files from the selected romfs folder to the patch romfs folder.
        // Store the copies in their corresponding variables.
        for(File f : FatesFiles.getInstance().getOriginalFileList()) {
            // Don't copy files from routes that weren't selected.
            if(!FatesGui.getInstance().getSelectedPaths()[0] && f.getAbsolutePath().contains("\\A\\"))
                continue;
            if(!FatesGui.getInstance().getSelectedPaths()[1] && f.getAbsolutePath().contains("\\B\\"))
                continue;
            if(!FatesGui.getInstance().getSelectedPaths()[2] && f.getAbsolutePath().contains("\\C\\"))
                continue;

            File copy = new File(f.getAbsolutePath().replace(FatesFiles.getInstance().getRom().getAbsolutePath(),
                    dir.getAbsolutePath()));
            if(!copy.getName().endsWith(".lz") && !copy.getName().endsWith(".cmb"))
                copy.mkdirs();
            else
                copy.getParentFile().mkdirs();
            try {
                if(f.getName().equals("bev") && f.isDirectory()) {
                    BinUtils.copyFolder(f, copy);
                    FatesFiles.getInstance().setBev(copy);
                }
                else {
                    if(BinUtils.isInSubDirectory(disposDir, copy)) {
                        FatesFiles.getInstance().getDispos().put(getCid(copy.getName()), copy);
                        Files.write(copy.toPath(), CompressionUtils.decompress(f));
                    }
                    else if(BinUtils.isInSubDirectory(personDir, copy)) {
                        FatesFiles.getInstance().getPerson().put(getCid(copy.getName()), copy);
                        Files.write(copy.toPath(), CompressionUtils.decompress(f));
                    }
                    else if(BinUtils.isInSubDirectory(textDir, copy) && !copy.getName().equals("GMap.bin.lz")) {
                        FatesFiles.getInstance().getText().put(getCid(copy.getName()), copy);
                        Files.write(copy.toPath(), Arrays.asList(MessageBinUtils.extractMessageArchive(
                                CompressionUtils.decompress(f))));
                    }
                    else if(BinUtils.isInSubDirectory(scriptsDir, copy) && copy.getName().endsWith("_Terrain.cmb")) {
                        FatesFiles.getInstance().getTerrain().put(getCid(copy.getName()), copy);
                        Files.copy(f.toPath(), copy.toPath());
                    }
                    else if(BinUtils.isInSubDirectory(scriptsDir, copy)) {
                        FatesFiles.getInstance().getScript().put(getCid(copy.getName()), copy);
                        Files.copy(f.toPath(), copy.toPath());
                    }
                    else if(BinUtils.isInSubDirectory(gameDataDir, copy) && copy.getName().equals("GameData.bin.lz")) {
                        FatesFiles.getInstance().setGameData(copy);
                        Files.write(copy.toPath(), CompressionUtils.decompress(f));
                    }
                    else if(copy.getName().equals("GMap.bin.lz")) {
                        FatesFiles.getInstance().setGMap(copy);
                        Files.write(copy.toPath(), Arrays.asList(MessageBinUtils.extractMessageArchive(
                                CompressionUtils.decompress(f))));
                    }
                    else if(BinUtils.isInSubDirectory(castleDir, copy) && copy.getName().equals("castle_join.bin.lz")) {
                        FatesFiles.getInstance().setCastleJoin(copy);
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
