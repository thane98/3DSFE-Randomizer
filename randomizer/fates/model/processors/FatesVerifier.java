package randomizer.fates.model.processors;

import randomizer.common.data.FatesData;
import randomizer.common.data.FatesFileData;
import randomizer.common.enums.ChapterType;
import randomizer.common.structures.Chapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FatesVerifier {
    private static final String JOIN_PATH = "/castle/castle_join.bin.lz";
    private static final String GAMEDATA_PATH = "/GameData/GameData.bin.lz";
    private static final String BEV_PATH = "/Scripts/bev";
    private static final String DISPOS_PATH = "/GameData/Dispos";
    private static final String BIRTHRIGHT_DISPOS_PATH = "/GameData/Dispos/A";
    private static final String CONQUEST_DISPOS_PATH = "/GameData/Dispos/B";
    private static final String REVELATION_DISPOS_PATH = "/GameData/Dispos/C";
    private static final String PERSON_PATH = "/GameData/Person";
    private static final String BIRTHRIGHT_PERSON_PATH = "/GameData/Person/A";
    private static final String CONQUEST_PERSON_PATH = "/GameData/Person/B";
    private static final String REVELATION_PERSON_PATH = "/GameData/Person/C";
    private static final String BIRTHRIGHT_SCRIPTS_PATH = "/Scripts/A";
    private static final String CONQUEST_SCRIPTS_PATH = "/Scripts/B";
    private static final String REVELATION_SCRIPTS_PATH = "/Scripts/C";

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
        List<File> routeFailures = new ArrayList<>();
        List<File> birthright = new ArrayList<>();
        List<File> conquest = new ArrayList<>();
        List<File> revelation = new ArrayList<>();
        boolean birthrightFlag = false;
        boolean conquestFlag = false;
        boolean revelationFlag = false;

        // Define text locations based off of region.
        File mainText;
        File birthrightText;
        File conquestText;
        File revelationText;
        switch(region) {
            case "North America":
                mainText = new File(dir.getAbsolutePath() + "/m/@E");
                birthrightText = new File(dir.getAbsolutePath() + "/m/A/@E");
                conquestText = new File(dir.getAbsolutePath() + "/m/B/@E");
                revelationText = new File(dir.getAbsolutePath() + "/m/C/@E");
                break;
            case "Japan":
                mainText = new File(dir.getAbsolutePath() + "/m");
                birthrightText = new File(dir.getAbsolutePath() + "/m/A");
                conquestText = new File(dir.getAbsolutePath() + "/m/B");
                revelationText = new File(dir.getAbsolutePath() + "/m/C");
                break;
            case "Europe - English":
                mainText = new File(dir.getAbsolutePath() + "/m/@U");
                birthrightText = new File(dir.getAbsolutePath() + "/m/A/@U");
                conquestText = new File(dir.getAbsolutePath() + "/m/B/@U");
                revelationText = new File(dir.getAbsolutePath() + "/m/C/@U");
                break;
            case "Europe - Spanish":
                mainText = new File(dir.getAbsolutePath() + "/m/@S");
                birthrightText = new File(dir.getAbsolutePath() + "/m/A/@S");
                conquestText = new File(dir.getAbsolutePath() + "/m/B/@S");
                revelationText = new File(dir.getAbsolutePath() + "/m/C/@S");
                break;
            case "Europe - German":
                mainText = new File(dir.getAbsolutePath() + "/m/@G");
                birthrightText = new File(dir.getAbsolutePath() + "/m/A/@G");
                conquestText = new File(dir.getAbsolutePath() + "/m/B/@G");
                revelationText = new File(dir.getAbsolutePath() + "/m/C/@G");
                break;
            case "Europe - Italian":
                mainText = new File(dir.getAbsolutePath() + "/m/@I");
                birthrightText = new File(dir.getAbsolutePath() + "/m/A/@I");
                conquestText = new File(dir.getAbsolutePath() + "/m/B/@I");
                revelationText = new File(dir.getAbsolutePath() + "/m/C/@I");
                break;
            case "Europe - French":
                mainText = new File(dir.getAbsolutePath() + "/m/@F");
                birthrightText = new File(dir.getAbsolutePath() + "/m/A/@F");
                conquestText = new File(dir.getAbsolutePath() + "/m/B/@F");
                revelationText = new File(dir.getAbsolutePath() + "/m/C/@F");
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
        file = new File(dir.getAbsolutePath() + JOIN_PATH);
        if(file.exists())
            verified.add(file);
        else
            failures.add(file);
        file = new File(dir.getAbsolutePath() + BEV_PATH);
        if(file.exists())
            verified.add(file);
        else
            failures.add(file);
        file = new File(mainText, "GMap.bin.lz");
        if(file.exists())
            verified.add(file);
        else
            failures.add(file);
        file = new File(mainText, "GameData.bin.lz");
        if(file.exists()) {
            verified.add(file);
            FatesNameMatcher.matchNames(file); // Get names from IDs.
        }
        else
            failures.add(file);

        for(Chapter c : FatesData.getInstance().getChapters()) {
            if(c.getType() == ChapterType.AllRoutes || c.getType() == ChapterType.Child) {
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
                file = new File(dir.getAbsolutePath() + "/Scripts", c.getCid() + ".cmb");
                if(file.exists())
                    verified.add(file);
                else
                    failures.add(file);
            }
            else if(c.getType() == ChapterType.Birthright) {
                file = new File(birthrightText, c.getCid() + ".bin.lz");
                if(file.exists())
                    birthright.add(file);
                else {
                    birthrightFlag = true;
                    routeFailures.add(file);
                }
                file = new File(dir.getAbsolutePath() + BIRTHRIGHT_DISPOS_PATH, c.getCid() + ".bin.lz");
                if(file.exists())
                    birthright.add(file);
                else {
                    birthrightFlag = true;
                    routeFailures.add(file);
                }
                file = new File(dir.getAbsolutePath() + BIRTHRIGHT_PERSON_PATH, c.getCid() + ".bin.lz");
                if(file.exists())
                    birthright.add(file);
                else {
                    birthrightFlag = true;
                    routeFailures.add(file);
                }
                file = new File(dir.getAbsolutePath() + BIRTHRIGHT_SCRIPTS_PATH, c.getCid() + ".cmb");
                if(file.exists())
                    birthright.add(file);
                else {
                    birthrightFlag = true;
                    routeFailures.add(file);
                }
            }
            else if(c.getType() == ChapterType.Conquest) {
                file = new File(conquestText, c.getCid() + ".bin.lz");
                if(file.exists())
                    conquest.add(file);
                else {
                    conquestFlag = true;
                    routeFailures.add(file);
                }
                file = new File(dir.getAbsolutePath() + CONQUEST_DISPOS_PATH, c.getCid() + ".bin.lz");
                if(file.exists())
                    conquest.add(file);
                else {
                    conquestFlag = true;
                    routeFailures.add(file);
                }
                file = new File(dir.getAbsolutePath() + CONQUEST_PERSON_PATH, c.getCid() + ".bin.lz");
                if(file.exists())
                    conquest.add(file);
                else {
                    conquestFlag = true;
                    routeFailures.add(file);
                }
                file = new File(dir.getAbsolutePath() + CONQUEST_SCRIPTS_PATH, c.getCid() + ".cmb");
                if(file.exists())
                    conquest.add(file);
                else {
                    conquestFlag = true;
                    routeFailures.add(file);
                }
            }
            else if(c.getType() == ChapterType.Revelation) {
                file = new File(revelationText, c.getCid() + ".bin.lz");
                if(file.exists())
                    revelation.add(file);
                else {
                    revelationFlag = true;
                    routeFailures.add(file);
                }
                file = new File(dir.getAbsolutePath() + REVELATION_DISPOS_PATH, c.getCid() + ".bin.lz");
                if(file.exists())
                    revelation.add(file);
                else {
                    revelationFlag = true;
                    routeFailures.add(file);
                }
                file = new File(dir.getAbsolutePath() + REVELATION_PERSON_PATH, c.getCid() + ".bin.lz");
                if(file.exists())
                    revelation.add(file);
                else {
                    revelationFlag = true;
                    routeFailures.add(file);
                }
                file = new File(dir.getAbsolutePath() + REVELATION_SCRIPTS_PATH, c.getCid() + ".cmb");
                if(file.exists())
                    revelation.add(file);
                else {
                    revelationFlag = true;
                    routeFailures.add(file);
                }
            }
        }

        // Run checks based off of failures and successes.
        if(birthrightFlag && conquestFlag && revelationFlag) { // No route verified completely.
            failures.addAll(routeFailures);
        }
        if(failures.size() > 0) {
            outputErrorLog(failures);
            return false;
        }
        if(!birthrightFlag) {
            verified.addAll(birthright);
            FatesFileData.getInstance().setBirthrightVerified(true);
        }
        if(!conquestFlag) {
            verified.addAll(conquest);
            FatesFileData.getInstance().setConquestVerified(true);
        }
        if(!revelationFlag) {
            verified.addAll(revelation);
            FatesFileData.getInstance().setBirthrightVerified(true);
        }

        for(File f : routeFailures)
            System.out.println(f.getName());
        FatesFileData.getInstance().setOriginalFileList(verified);
        FatesFileData.getInstance().setRom(dir);
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
