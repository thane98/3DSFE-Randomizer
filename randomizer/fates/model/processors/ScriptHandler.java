package randomizer.fates.model.processors;

import randomizer.common.enums.ItemType;
import randomizer.common.fs.model.Decompiler;
import randomizer.common.fs.model.ScriptCompiler;
import randomizer.common.structures.Chapter;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.singletons.FatesData;
import randomizer.fates.singletons.FatesFileData;
import randomizer.fates.singletons.FatesGui;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ScriptHandler {
    private static boolean[] options = FatesGui.getInstance().getSelectedOptions();
    private static FatesData fatesData = FatesData.getInstance();
    private static FatesFileData fileData = FatesFileData.getInstance();

    static void randomizeScript(List<FatesCharacter> characters) {
        List<Chapter> chapters = fatesData.getSelectedChapters();
        Decompiler decompiler = new Decompiler();
        ScriptCompiler compiler;
        for(Chapter c : chapters) {
            try {
                Path path = fileData.getScript().get(c.getCid()).toPath();
                String script = decompiler.decompile(path);
                for(FatesCharacter ch : characters) {
                    script = script.replaceAll(ch.getPid(), ch.getAid() + "RANDOMIZERTMP");
                }
                for(FatesCharacter ch : characters) {
                    script = script.replaceAll(ch.getTargetPid().replace("PID_", "AID_")
                            + "RANDOMIZERTMP", ch.getPid());
                    if(ch.getId() == 1 && c.getCid().equals("A005")) { // Fix for chapter 5 forced class.
                        script = script.replaceAll("JID_ダークプリンス男", ch.getCharacterClass().getJid());
                    }
                    else if(ch.getId() == 2 && c.getCid().equals("A005")) {
                        script = script.replaceAll("JID_ダークプリンセス女", ch.getCharacterClass().getJid());
                    }
                }
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
        ArrayList<File> arrfile = new ArrayList<>();
        File[] tempFiles = fileData.getBev().listFiles((dir, name) -> name.startsWith(chapter.getCid()));
        if(tempFiles != null) {
            Collections.addAll(arrfile, tempFiles);
        }
        Decompiler decompiler = new Decompiler();
        ScriptCompiler compiler;
        for(File f : arrfile) {
            try {
                String script = decompiler.decompile(f.toPath());
                for(FatesCharacter ch : characters) {
                    script = script.replaceAll(ch.getTaglessPid(), ch.getAid() + "RANDOMIZERTMP");
                }
                for(FatesCharacter ch : characters) {
                    script = script.replaceAll(ch.getTargetPid().replace("PID_", "AID_")
                            + "RANDOMIZERTMP", ch.getTaglessPid());
                }
                compiler = new ScriptCompiler(f.getName());
                compiler.compile(f.toPath(), script);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void randomizeTerrain(Chapter chapter) {
        if(fileData.getTerrain().containsKey(chapter.getCid())) {
            try {
                Decompiler decompiler = new Decompiler();
                ScriptCompiler compiler;
                Path path = fileData.getTerrain().get(chapter.getCid()).toPath();
                String tmp = decompiler.decompile(path);
                String lines[] = tmp.split("\\r?\\n");
                StringBuilder script = new StringBuilder();
                for(String line : lines) {
                    if(line.startsWith("ev::ItemGain(string\"")) { // TODO: Make sure the string is the ONLY parameter.
                        line = "ev::ItemGain(string(\"" + fatesData.getSelectedItems(ItemType.Treasure) + "\"))";
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
}
