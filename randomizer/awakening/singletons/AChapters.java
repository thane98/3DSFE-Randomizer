package randomizer.awakening.singletons;

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

public class AChapters {
    private static AChapters instance;
    private List<Chapter> chapters;

    private AChapters() {
        Type chapterType = new TypeToken<List<Chapter>>() {}.getType();

        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("data/json/AwakeningChapters.json"), "UTF-8")));
            chapters = gson.fromJson(reader, chapterType);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AChapters getInstance() {
        if (instance == null)
            instance = new AChapters();
        return instance;
    }

    public List<Chapter> getChaptersByType(ChapterType type) {
        List<Chapter> selected = new ArrayList<>();
        for(Chapter c : chapters) {
            if(c.getType() == type)
                selected.add(c);
        }
        return selected;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }
}
