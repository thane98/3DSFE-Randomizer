package randomizer.fates.singletons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import randomizer.Randomizer;
import randomizer.common.enums.*;
import randomizer.common.structures.Chapter;
import randomizer.common.structures.FEItem;
import randomizer.common.structures.Job;
import randomizer.common.structures.Skill;
import randomizer.fates.model.structures.FatesCharacter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("common/data/json/FatesChapters.json"))));
            chapters = gson.fromJson(reader, chapterType);
            reader.close();
            reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("common/data/json/FatesCharacters.json"))));
            characters = gson.fromJson(reader, characterType);
            reader.close();
            reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("common/data/json/FatesClasses.json"))));
            jobs = gson.fromJson(reader, jobType);
            reader.close();
            reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("common/data/json/FatesItems.json"))));
            items = gson.fromJson(reader, itemType);
            reader.close();
            reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("common/data/json/FatesSkills.json"))));
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

    List<FEItem> getWeapons() {
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

    public FatesCharacter getReplacement(List<FatesCharacter> characters, String pid) {
        for(FatesCharacter c : characters) {
            if(c.getTargetPid().equals(pid))
                return c;
        }
        return null;
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

    public List<FEItem> getSelectedItems(ItemType type) {
        List<FEItem> selected = new ArrayList<>();
        List<FEItem> weapons = getWeapons();
        boolean[] selectedItems = FatesGui.getInstance().getSelectedItems();
        for(int x = 0; x < weapons.size(); x++) {
            if(weapons.get(x).getType() == type && selectedItems[x])
                selected.add(weapons.get(x));
        }
        return selected;
    }

    public FEItem generateEligibleItem(ItemType type, int maxLength) {
        List<FEItem> eligible = new ArrayList<>();
        try {
            for(FEItem i : getSelectedItems(type)) {
                if(i.getIid().getBytes("shift-jis").length <= maxLength)
                    eligible.add(i);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Random random = new Random();
        return eligible.get(random.nextInt(eligible.size()));
    }

    public List<Job> getEligibleJobs(boolean male, int maxLength) {
        List<Job> eligible = new ArrayList<>();
        List<Job> pool;
        if(male)
            pool = getMaleBaseClasses();
        else
            pool = getFemaleBaseClasses();
        try {
            for(Job j : pool) {
                if(j.getJid().getBytes("shift-jis").length <= maxLength)
                    eligible.add(j);
            }
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return eligible;
    }
    
    public byte[] generateWeaponsRanks(Job j) {
        byte[] weaponRanks = new byte[8];
        if(j.getItemType() == ItemType.Swords) {
            weaponRanks[0] = (byte) WeaponRank.C.value();
        }
        else if(j.getItemType() == ItemType.Lances) {
            weaponRanks[1] = (byte) WeaponRank.C.value();
        }
        else if(j.getItemType() == ItemType.Axes) {
            weaponRanks[2] = (byte) WeaponRank.C.value();
        }
        else if(j.getItemType() == ItemType.Shurikens) {
            weaponRanks[3] = (byte) WeaponRank.C.value();
        }
        else if(j.getItemType() == ItemType.Bows) {
            weaponRanks[4] = (byte) WeaponRank.C.value();
        }
        else if(j.getItemType() == ItemType.Tomes) {
            weaponRanks[5] = (byte) WeaponRank.C.value();
        }
        else if(j.getItemType() == ItemType.Staves) {
            weaponRanks[6] = (byte) WeaponRank.C.value();
        }
        else {
            weaponRanks[7] = (byte) WeaponRank.C.value();
        }
        return weaponRanks;
    }

    public FEItem generateItem(Job j) {
        Random random = new Random();
        List<FEItem> items = getSelectedItems(j.getItemType());
        return items.get(random.nextInt(items.size()));
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
