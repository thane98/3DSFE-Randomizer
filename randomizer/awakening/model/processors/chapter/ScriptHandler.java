package randomizer.awakening.model.processors.chapter;

import randomizer.awakening.model.structures.ACharacter;
import randomizer.awakening.singletons.AChapters;
import randomizer.awakening.singletons.AFiles;
import randomizer.awakening.singletons.AGui;
import randomizer.awakening.singletons.AItems;
import randomizer.common.enums.ItemType;
import randomizer.common.fs.model.Decompiler;
import randomizer.common.fs.model.ScriptCompiler;
import randomizer.common.structures.Chapter;

import java.nio.file.Path;
import java.util.List;

public class ScriptHandler {
    private static boolean[] options = AGui.getInstance().getSelectedOptions();
    private static AItems aItems = AItems.getInstance();
    private static AChapters aChapters = AChapters.getInstance();
    private static AFiles fileData = AFiles.getInstance();

    public static void randomizeScript(List<ACharacter> characters) {
        List<Chapter> chapters = aChapters.getChapters();
        Decompiler decompiler = new Decompiler();
        ScriptCompiler compiler;
        for(Chapter c : chapters) {
            try {
                Path path = fileData.getScript().get(c.getCid()).toPath();
                String script = decompiler.decompile(path);
                for(ACharacter ch : characters) {
                    script = script.replaceAll(ch.getPid(), ch.getAid() + "RANDOMIZERTMP");
                }
                for(ACharacter ch : characters) {
                    script = script.replaceAll(ch.getTargetPid().replace("PID_", "AID_")
                            + "RANDOMIZERTMP", ch.getPid());
                }
                compiler = new ScriptCompiler(path.toFile().getName());
                compiler.compile(path, script);

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
                        line = "ev::ItemGain(string(\"" + aItems.getSelectedItems(ItemType.Treasure) + "\"))";
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
