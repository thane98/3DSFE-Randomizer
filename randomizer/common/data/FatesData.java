package randomizer.common.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import randomizer.common.enums.ItemType;
import randomizer.common.structures.Chapter;
import randomizer.common.structures.FEItem;
import randomizer.common.structures.Job;
import randomizer.common.structures.Skill;
import randomizer.fates.model.structures.FatesCharacter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FatesData {
    private static FatesData instance;

    private List<Skill> skills;
    private List<FEItem> items;
    private List<Job> jobs;
    private List<FatesCharacter> characters;
    private List<Chapter> chapters;

    private FatesData() {
        Type characterType = new TypeToken<List<FatesCharacter>>() {}.getType();
        Type jobType = new TypeToken<List<Job>>() {}.getType();
        Type itemType = new TypeToken<List<FEItem>>() {}.getType();
        Type skillType = new TypeToken<List<Skill>>() {}.getType();
        Type chapterType = new TypeToken<List<Chapter>>() {}.getType();

        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(this.getClass()
                    .getResourceAsStream("json/FatesChapters.json"))));
            chapters = gson.fromJson(reader, chapterType);
            reader.close();
            reader = new JsonReader(new BufferedReader(new InputStreamReader(this.getClass()
                    .getResourceAsStream("json/FatesCharacters.json"))));
            characters = gson.fromJson(reader, characterType);
            reader.close();
            reader = new JsonReader(new BufferedReader(new InputStreamReader(this.getClass()
                    .getResourceAsStream("json/FatesClasses.json"))));
            jobs = gson.fromJson(reader, jobType);
            reader.close();
            reader = new JsonReader(new BufferedReader(new InputStreamReader(this.getClass()
                    .getResourceAsStream("json/FatesItems.json"))));
            items = gson.fromJson(reader, itemType);
            reader.close();
            reader = new JsonReader(new BufferedReader(new InputStreamReader(this.getClass()
                    .getResourceAsStream("json/FatesSkills.json"))));
            skills = gson.fromJson(reader, skillType);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FatesData getInstance() {
        if (instance == null)
            instance = new FatesData();
        return instance;
    }

    public List<FEItem> getWeapons() {
        List<FEItem> weapons = new ArrayList<>();
        for(FEItem i : items) {
            if(i.getType() != ItemType.Treasure)
                weapons.add(i);
        }
        return weapons;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public List<FEItem> getItems() {
        return items;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public List<FatesCharacter> getCharacters() {
        return characters;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }
}
