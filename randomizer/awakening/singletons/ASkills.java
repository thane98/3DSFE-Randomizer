package randomizer.awakening.singletons;

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

public class ASkills {
    private static ASkills instance;

    private List<Skill> skills;

    private ASkills() {
        Type skillType = new TypeToken<List<Skill>>() {}.getType();
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("data/json/AwakeningSkills.json"), "UTF-8")));
            skills = gson.fromJson(reader, skillType);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ASkills getInstance() {
        if (instance == null)
            instance = new ASkills();
        return instance;
    }

    public Skill getSkillById(int id) {
        for(Skill s : skills) {
            if(s.getId() == id)
                return s;
        }
        return null;
    }

    public List<Skill> getSelectedSkills() {
        boolean[] selected = AGui.getInstance().getSelectedSkills();
        List<Skill> selectedSkills = new ArrayList<>();
        for(int x = 0; x < skills.size(); x++) {
            if(skills.get(x).getType() == SkillType.Personal && !AGui.getInstance().getSelectedOptions()[10])
                continue;
            if(selected[x])
                selectedSkills.add(skills.get(x));
        }
        return selectedSkills;
    }

    public List<Skill> getSelectedPersonalSkills() {
        boolean[] selected = AGui.getInstance().getSelectedSkills();
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
