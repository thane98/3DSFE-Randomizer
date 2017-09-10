package randomizer.awakening.singletons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import randomizer.Randomizer;
import randomizer.common.enums.ItemType;
import randomizer.common.enums.JobState;
import randomizer.common.enums.WeaponRank;
import randomizer.common.structures.Job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AJobs {
    private static AJobs instance;
    private List<Job> jobs;

    private AJobs() {
        Type jobType = new TypeToken<List<Job>>() {}.getType();
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("data/json/AwakeningClasses.json"), "UTF-8")));
            jobs = gson.fromJson(reader, jobType);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AJobs getInstance() {
        if (instance == null)
            instance = new AJobs();
        return instance;
    }

    public List<Job> getMaleBaseClasses() {
        List<Job> current = new ArrayList<>();
        boolean[] selectedJobs = AGui.getInstance().getSelectedJobs();
        for(int x = 0; x < jobs.size(); x++) {
            Job j = jobs.get(x);
            if((j.getGender() == 0 || j.getGender() == 2) && j.getState() != JobState.Promoted && selectedJobs[x])
                current.add(j);
        }
        return current;
    }

    public List<Job> getFemaleBaseClasses() {
        List<Job> current = new ArrayList<>();
        boolean[] selectedJobs = AGui.getInstance().getSelectedJobs();
        for(int x = 0; x < jobs.size(); x++) {
            Job j = jobs.get(x);
            if((j.getGender() == 1 || j.getGender() == 2) && j.getState() != JobState.Promoted && selectedJobs[x])
                current.add(j);
        }
        return current;
    }

    public List<Job> getMalePromotedClasses() {
        List<Job> current = new ArrayList<>();
        boolean[] selectedJobs = AGui.getInstance().getSelectedJobs();
        for(int x = 0; x < jobs.size(); x++) {
            Job j = jobs.get(x);
            if((j.getGender() == 0 || j.getGender() == 2) && j.getState() != JobState.Base && selectedJobs[x])
                current.add(j);
        }
        return current;
    }

    public List<Job> getFemalePromotedClasses() {
        List<Job> current = new ArrayList<>();
        boolean[] selectedJobs = AGui.getInstance().getSelectedJobs();
        for(int x = 0; x < jobs.size(); x++) {
            Job j = jobs.get(x);
            if((j.getGender() == 1 || j.getGender() == 2) && j.getState() != JobState.Base && selectedJobs[x])
                current.add(j);
        }
        return current;
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
                if(j.getJid().getBytes("shift-jis").length <= maxLength && j.getItemType()
                        != ItemType.Staves && j.getItemType() != ItemType.Beaststone)
                    eligible.add(j);
            }
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return eligible;
    }
    
    public byte[] generateWeaponsRanks(Job j) {
        byte[] weaponRanks = new byte[5];
        if(j.getItemType() == ItemType.Swords) {
            weaponRanks[0] = (byte) WeaponRank.C.value();
        }
        else if(j.getItemType() == ItemType.Lances) {
            weaponRanks[1] = (byte) WeaponRank.C.value();
        }
        else if(j.getItemType() == ItemType.Axes) {
            weaponRanks[2] = (byte) WeaponRank.C.value();
        }
        else if(j.getItemType() == ItemType.Bows) {
            weaponRanks[3] = (byte) WeaponRank.C.value();
        }
        else if(j.getItemType() == ItemType.Tomes) {
            weaponRanks[4] = (byte) WeaponRank.C.value();
        }
        return weaponRanks;
    }

    public List<Job> getJobs() {
        return jobs;
    }
}
