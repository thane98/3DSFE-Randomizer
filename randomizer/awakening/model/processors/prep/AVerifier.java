package randomizer.awakening.model.processors.prep;

import randomizer.awakening.singletons.AChapters;
import randomizer.awakening.singletons.AFiles;
import randomizer.common.enums.ChapterType;
import randomizer.common.structures.Chapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AVerifier {
    private static final String GAMEDATA_PATH = "/data/GameData.bin.lz";
    private static final String SCRIPTS_PATH = "/Scripts/";
    private static final String DISPOS_PATH = "/data/Dispos";
    private static final String PERSON_PATH = "/data/Person";

    /**
     * Verify that the given ROM directory contains every file necessary to
     * perform randomization.
     *
     * @param dir The ROM directory.
     * @param region The region layout that the ROM directory uses.
     * @return A boolean value indicating whether or not the ROM verified successfully.
     */
    public static boolean verify(File dir, String region) {
        List<File> verified = new ArrayList<>();
        List<File> failures = new ArrayList<>();

        // Define text locations based off of region.
        File mainText;
        switch(region) {
            case "North America":
                mainText = new File(dir.getAbsolutePath() + "/m/E");
                break;
            case "Japan":
                mainText = new File(dir.getAbsolutePath() + "/m");
                break;
            case "Europe - English":
                mainText = new File(dir.getAbsolutePath() + "/m/U");
                break;
            case "Europe - Spanish":
                mainText = new File(dir.getAbsolutePath() + "/m/S");
                break;
            case "Europe - German":
                mainText = new File(dir.getAbsolutePath() + "/m/G");
                break;
            case "Europe - Italian":
                mainText = new File(dir.getAbsolutePath() + "/m/I");
                break;
            case "Europe - French":
                mainText = new File(dir.getAbsolutePath() + "/m/F");
                break;
            default:
                return false; // Invalid region.
        }

        // Verify that chapter-independent files exist.
        File file = new File(dir.getAbsolutePath() + GAMEDATA_PATH);
        if(file.exists())
            verified.add(file);
        else
            failures.add(file);
        file = new File(dir.getAbsolutePath() + PERSON_PATH, "static.bin.lz");
        if(file.exists())
            verified.add(file);
        else
            failures.add(file);
        file = new File(mainText, "GameData.bin.lz");
        if(file.exists()) {
            NameMatcher.matchNames(file); // Get names from IDs.
        }
        else
            failures.add(file);

        for(Chapter c : AChapters.getInstance().getChapters()) {
            if(c.getType() == ChapterType.AllRoutes || c.getType() == ChapterType.Child
                    || c.getType() == ChapterType.Amiibo) {
                file = new File(mainText, c.getCid() + ".bin.lz");
                if(file.exists())
                    verified.add(file);
                else
                    failures.add(file);
                file = new File(dir.getAbsolutePath() + DISPOS_PATH, c.getCid() + ".bin.lz");
                if(file.exists())
                    verified.add(file);
                else
                    failures.add(file);
                file = new File(dir.getAbsolutePath() + PERSON_PATH, c.getCid() + ".bin.lz");
                if(file.exists())
                    verified.add(file);
                else
                    failures.add(file);
                file = new File(dir.getAbsolutePath() + SCRIPTS_PATH, c.getCid() + ".cmb");
                if(file.exists())
                    verified.add(file);
                else
                    failures.add(file);
            }

            file = new File(SCRIPTS_PATH, c.getCid() + "_Terrain.cmb");
            if(file.exists())
                verified.add(file);
        }

        // Run checks based off of failures and successes.
        if(failures.size() > 0) {
            outputErrorLog(failures);
            return false;
        }
        AFiles.getInstance().setOriginalFileList(verified);
        AFiles.getInstance().setRom(dir);
        return true;
    }

    private static void outputErrorLog(List<File> failures) {
        List<String> out = new ArrayList<>();
        out.add("The following files failed to verify: ");
        for(File f : failures)
            out.add(f.getAbsolutePath());
        try {
            Files.write(Paths.get(System.getProperty("user.dir") + "/VerificationFailures.txt"), out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
