package randomizer.fates.singletons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import randomizer.Randomizer;
import randomizer.common.enums.ChapterType;
import randomizer.common.structures.Chapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FatesChapters {
    private static FatesChapters instance;
    private List<Chapter> chapters;

    private FatesChapters() {
        try {
            Type chapterType = new TypeToken<List<Chapter>>() {}.getType();
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("data/json/FatesChapters.json"), "UTF-8")));
            chapters = gson.fromJson(reader, chapterType);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FatesChapters getInstance() {
        if (instance == null)
            instance = new FatesChapters();
        return instance;
    }

    public List<Chapter> getChaptersByType(ChapterType type) {
        if(type == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "getChaptersByType. type must not be null.");

        List<Chapter> selected = new ArrayList<>();
        for(Chapter c : chapters) {
            if(c.getType() == type)
                selected.add(c);
        }
        return selected;
    }
    
    public List<Chapter> getSelectedChapters() {
        List<Chapter> selected = new ArrayList<>();
        boolean[] paths = FatesGui.getInstance().getSelectedPaths();
        for(Chapter c : chapters) {
            if(c.getType() == ChapterType.AllRoutes || c.getType() == ChapterType.Amiibo 
                    || c.getType() == ChapterType.Child)
                selected.add(c);
            else if(c.getType() == ChapterType.Birthright && paths[0])
                selected.add(c);
            else if(c.getType() == ChapterType.Conquest && paths[1])
                selected.add(c);
            else if(c.getType() == ChapterType.Revelation && paths[2])
                selected.add(c);
        }
        return selected;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }
}
