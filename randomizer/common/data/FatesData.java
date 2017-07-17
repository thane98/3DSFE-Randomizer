package randomizer.common.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import randomizer.common.enums.ChapterType;
import randomizer.common.enums.ItemType;
import randomizer.common.enums.JobState;
import randomizer.common.enums.SkillType;
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

    public List<FatesCharacter> getSelectedCharacters() {
        boolean[] selected = FatesGui.getInstance().getSelectedCharacters();
        List<FatesCharacter> selectedCharacters = new ArrayList<>();
        for(int x = 0; x < selected.length; x++) {
            if(selected[x])
                selectedCharacters.add(characters.get(x));
        }
        return selectedCharacters;
    }

    public FatesCharacter getByPid(String pid) {
        for(FatesCharacter c : characters) {
            if(c.getPid().equals(pid))
                return c;
        }
        return null;
    }

    public List<FEItem> getWeapons() {
        List<FEItem> weapons = new ArrayList<>();
        for(FEItem i : items) {
            if(i.getType() != ItemType.Treasure)
                weapons.add(i);
        }
        return weapons;
    }

    public List<Job> getMaleBaseClasses() {
        List<Job> current = new ArrayList<>();
        boolean[] selectedJobs = FatesGui.getInstance().getSelectedJobs();
        for(int x = 0; x < jobs.size(); x++) {
            Job j = jobs.get(x);
            if((j.getGender() == 0 || j.getGender() == 2) && j.getState() == JobState.Base && selectedJobs[x])
                current.add(j);
        }
        return current;
    }

    public List<Job> getFemaleBaseClasses() {
        List<Job> current = new ArrayList<>();
        boolean[] selectedJobs = FatesGui.getInstance().getSelectedJobs();
        for(int x = 0; x < jobs.size(); x++) {
            Job j = jobs.get(x);
            if((j.getGender() == 1 || j.getGender() == 2) && j.getState() == JobState.Base && selectedJobs[x])
                current.add(j);
        }
        return current;
    }

    public List<Job> getMalePromotedClasses() {
        List<Job> current = new ArrayList<>();
        boolean[] selectedJobs = FatesGui.getInstance().getSelectedJobs();
        for(int x = 0; x < jobs.size(); x++) {
            Job j = jobs.get(x);
            if((j.getGender() == 0 || j.getGender() == 2) && j.getState() != JobState.Base && selectedJobs[x])
                current.add(j);
        }
        return current;
    }

    public List<Job> getFemalePromotedClasses() {
        List<Job> current = new ArrayList<>();
        boolean[] selectedJobs = FatesGui.getInstance().getSelectedJobs();
        for(int x = 0; x < jobs.size(); x++) {
            Job j = jobs.get(x);
            if((j.getGender() == 1 || j.getGender() == 2) && j.getState() != JobState.Base && selectedJobs[x])
                current.add(j);
        }
        return current;
    }

    public Skill getSkillById(int id) {
        for(Skill s : skills) {
            if(s.getId() == id)
                return s;
        }
        return null;
    }

    public List<Skill> getSelectedSkills() {
        boolean[] selected = FatesGui.getInstance().getSelectedSkills();
        List<Skill> selectedSkills = new ArrayList<>();
        for(int x = 0; x < skills.size(); x++) {
            if(skills.get(x).getType() == SkillType.Personal && !FatesGui.getInstance().getSelectedOptions()[10])
                continue;
            if(selected[x])
                selectedSkills.add(skills.get(x));
        }
        return selectedSkills;
    }

    public List<Skill> getSelectedPersonalSkills() {
        boolean[] selected = FatesGui.getInstance().getSelectedSkills();
        List<Skill> selectedSkills = new ArrayList<>();
        for(int x = 0; x < skills.size(); x++) {
            if(skills.get(x).getType() != SkillType.Personal)
                continue;
            if(selected[x])
                selectedSkills.add(skills.get(x));
        }
        return selectedSkills;
    }

    public List<Chapter> getChaptersByType(ChapterType type) {
        List<Chapter> selected = new ArrayList<>();
        for(Chapter c : chapters) {
            if(c.getType() == type)
                selected.add(c);
        }
        return selected;
    }

    public FatesCharacter getReplacement(String pid) {
        for(FatesCharacter c : characters) {
            if(c.getTargetPid().equals(pid))
                return c;
        }
        return null;
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
