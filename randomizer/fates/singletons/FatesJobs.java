package randomizer.fates.singletons;

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

public class FatesJobs {
    private static FatesJobs instance;
    private List<Job> jobs;

    private FatesJobs() {
        try {
            // Parse jobs from JSON.
            Type jobType = new TypeToken<List<Job>>() {}.getType();
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("data/json/FatesClasses.json"), "UTF-8")));
            jobs = gson.fromJson(reader, jobType);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FatesJobs getInstance() {
        if (instance == null)
            instance = new FatesJobs();
        return instance;
    }

    public List<Job> getMaleBaseClasses() {
        List<Job> current = new ArrayList<>();
        boolean[] selectedJobs = FatesGui.getInstance().getSelectedJobs();
        for(int x = 0; x < jobs.size(); x++) {
            Job j = jobs.get(x);
            if((j.getGender() == 0 || j.getGender() == 2) && j.getState() != JobState.Promoted && selectedJobs[x])
                current.add(j);
        }
        return current;
    }

    public List<Job> getFemaleBaseClasses() {
        List<Job> current = new ArrayList<>();
        boolean[] selectedJobs = FatesGui.getInstance().getSelectedJobs();
        for(int x = 0; x < jobs.size(); x++) {
            Job j = jobs.get(x);
            if((j.getGender() == 1 || j.getGender() == 2) && j.getState() != JobState.Promoted && selectedJobs[x])
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

    public List<Job> getEligibleJobs(boolean male, int maxLength) {
        if(maxLength < 1)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "getEligibleJobs. maxLength must be greater than 0.");

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
        if(j == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "generateItem. j must not be null.");

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

    public List<Job> getJobs() {
        return jobs;
    }
}
