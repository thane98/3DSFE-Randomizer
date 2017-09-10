package randomizer.fates.singletons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import randomizer.Randomizer;
import randomizer.common.enums.SkillType;
import randomizer.common.structures.Skill;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FatesSkills {
    private static FatesSkills instance;

    private List<Skill> skills;

    private FatesSkills() {
        try {
            // Parse skills from JSON.
            Type skillType = new TypeToken<List<Skill>>() {}.getType();
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("data/json/FatesSkills.json"), "UTF-8")));
            skills = gson.fromJson(reader, skillType);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FatesSkills getInstance() {
        if (instance == null)
            instance = new FatesSkills();
        return instance;
    }

    public Skill getSkillById(int id) {
        if(id < 0)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "getSkillById. id must not be negative.");

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

    public List<Skill> getSkills() {
        return skills;
    }
}
