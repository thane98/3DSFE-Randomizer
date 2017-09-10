package randomizer.fates.model.processors.chapter;

import randomizer.common.enums.ItemType;
import randomizer.common.fs.model.Decompiler;
import randomizer.common.fs.model.ScriptCompiler;
import randomizer.common.structures.Chapter;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.*;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ScriptHandler {
    private static boolean[] options = FatesGui.getInstance().getSelectedOptions();
    private static FatesItems fatesItems = FatesItems.getInstance();
    private static FatesChapters fatesChapters = FatesChapters.getInstance();
    private static FatesFiles fileData = FatesFiles.getInstance();

    public static void randomizeScript() {
        List<FatesCharacter> characters = FatesCharacters.getInstance().getWorkingCharacters();
        List<Chapter> chapters = fatesChapters.getSelectedChapters();

        Decompiler decompiler = new Decompiler();
        ScriptCompiler compiler;
        for(Chapter c : chapters) {
            try {
                // Swap PID values within the script.
                Path path = fileData.getScript().get(c.getCid()).toPath();
                String script = decompiler.decompile(path);
                for(FatesCharacter ch : characters) {
                    script = script.replaceAll(ch.getPid(), ch.getAid() + "RANDOMIZERTMP");
                }
                for(FatesCharacter ch : characters) {
                    script = script.replaceAll(ch.getTargetPid().replace("PID_", "AID_")
                            + "RANDOMIZERTMP", ch.getPid());

                    // Replace the forced reclass in chapter 5 with the player's newly
                    // assigned class.
                    if(ch.getId() == 1 && c.getCid().equals("A005")) {
                        script = script.replaceAll("JID_ダークプリンス男", ch.getCharacterClass().getJid());
                    }
                    else if(ch.getId() == 2 && c.getCid().equals("A005")) {
                        script = script.replaceAll("JID_ダークプリンセス女", ch.getCharacterClass().getJid());
                    }
                }

                // Patch unusual map scripts.
                if(c.getCid().equals("A011") && options[3])
                    script = patchA011Script(script);

                // Recompile for use in game.
                compiler = new ScriptCompiler(path.toFile().getName());
                compiler.compile(path, script);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if(options[7])
                randomizeBev(c, characters);
            if(options[2])
                randomizeTerrain(c);
        }
    }

    private static void randomizeBev(Chapter chapter, List<FatesCharacter> characters) {
        if(chapter == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "randomizeBev. chapter must not be null.");
        if(characters == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "randomizeBev. characters must not be null.");

        ArrayList<File> arrfile = new ArrayList<>();
        File[] tempFiles = fileData.getBev().listFiles((dir, name) -> name.startsWith(chapter.getCid()));
        if(tempFiles != null) {
            Collections.addAll(arrfile, tempFiles);
        }
        Decompiler decompiler = new Decompiler();
        ScriptCompiler compiler;
        for(File f : arrfile) {
            try {
                // Swap tagless PIDs.
                String script = decompiler.decompile(f.toPath());
                script = script.replaceAll("\"法衣裏返しレオン\"", "\"レオン\""); // Leo chapter 1 model.
                for(FatesCharacter ch : characters) {
                    script = script.replaceAll(ch.getTaglessPid(), ch.getAid() + "RANDOMIZERTMP");
                }
                for(FatesCharacter ch : characters) {
                    script = script.replaceAll(ch.getTargetPid().replace("PID_", "AID_")
                            + "RANDOMIZERTMP", ch.getTaglessPid());
                }

                // Recompile the script for use in game.
                compiler = new ScriptCompiler(f.getName());
                compiler.compile(f.toPath(), script);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void randomizeTerrain(Chapter chapter) {
        if(chapter == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "randomizeTerrain. chapter must not be null.");

        if(fileData.getTerrain().containsKey(chapter.getCid())) {
            try {
                // Generate new chest items.
                Decompiler decompiler = new Decompiler();
                ScriptCompiler compiler;
                Path path = fileData.getTerrain().get(chapter.getCid()).toPath();
                String tmp = decompiler.decompile(path);
                String lines[] = tmp.split("\\r?\\n");
                StringBuilder script = new StringBuilder();
                for(String line : lines) {
                    if(line.startsWith("ev::ItemGain(string\"")) {
                        line = "ev::ItemGain(string(\"" + fatesItems.getSelectedItems(ItemType.Treasure) + "\"))";
                    }
                    script.append(line).append(System.lineSeparator());
                }
                script.append(System.lineSeparator());
                compiler = new ScriptCompiler(path.toFile().getName());
                compiler.compile(path, script.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String patchA011Script(String script) {
        if(script == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "patchA011Script. chapter must not be null.");

        // Remove all references to the Birthright handover file.
        String[] arr = script.split("\\r?\\n");
        List<String> lines = new ArrayList<>();
        lines.addAll(Arrays.asList(arr));
        lines.removeIf(s -> s.contains("A_HANDOVER"));
        StringBuilder builder = new StringBuilder();
        for(String line : lines)
            builder.append(line).append(System.lineSeparator());
        builder.append(System.lineSeparator());
        return builder.toString();
    }
}
