package randomizer.fates.singletons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import randomizer.Randomizer;
import randomizer.common.enums.CharacterType;
import randomizer.fates.model.structures.FatesCharacter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FatesCharacters {
    private static FatesCharacters instance;
    private List<FatesCharacter> characters;
    private List<String> bannedPids;

    private List<FatesCharacter> workingCharacters;

    private FatesCharacters() {
        try {
            // Parse default characters from JSON.
            Type characterType = new TypeToken<List<FatesCharacter>>() {}.getType();
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("data/json/FatesCharacters.json"), "UTF-8")));
            characters = gson.fromJson(reader, characterType);
            reader.close();

            // Parse banned PIDs.
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("data/text/FatesBannedPids.txt"), "UTF-8"));
            bannedPids = new ArrayList<>();
            String line;
            while((line = streamReader.readLine()) != null) {
                bannedPids.add(line);
            }

            // Parse custom characters.
            File customFile = new File(System.getProperty("user.dir"), "CustomCharacters.json");
            if(customFile.exists()) {
                reader = new JsonReader(new BufferedReader(new FileReader(customFile)));
                characters.addAll(gson.fromJson(reader, characterType));
                reader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FatesCharacters getInstance() {
        if (instance == null)
            instance = new FatesCharacters();
        return instance;
    }

    public List<FatesCharacter> getSelectedCharacters() { // TODO: Refactor getSelectedCharacters
        boolean[] selected = FatesGui.getInstance().getSelectedCharacters();
        List<FatesCharacter> selectedCharacters = new ArrayList<>();
        List<FatesCharacter> npcs = getCharactersByType(CharacterType.NPC);
        for(FatesCharacter c : characters) {
            if(c.getCharacterType() != CharacterType.NPC)
                selectedCharacters.add(c);
        }
        for(int x = 0; x < npcs.size(); x++) {
           if(selected[x]) {
               selectedCharacters.add(npcs.get(x));
           }
        }
        return selectedCharacters;
    }

    public List<FatesCharacter> getCharactersByType(CharacterType type) {
        if(type == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "getCharactersByType. type must not be null.");

        List<FatesCharacter> fatesCharacters = new ArrayList<>();
        for(FatesCharacter c : characters) {
            if(c.getCharacterType() == type)
                fatesCharacters.add(c);
        }
        return fatesCharacters;
    }

    public FatesCharacter getByPid(String pid) {
        if(pid == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "getByPid. pid must not be null.");

        for(FatesCharacter c : workingCharacters) {
            if(c.getPid().equals(pid))
                return c;
        }
        return null;
    }

    public FatesCharacter getReplacement(String pid) {
        if(pid == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "getReplacement. pid must not be null.");

        for(FatesCharacter c : workingCharacters) {
            if(c.getTargetPid().equals(pid))
                return c;
        }
        return null;
    }

    public List<FatesCharacter> getCharacters() {
        return characters;
    }

    public List<String> getBannedPids() {
        return bannedPids;
    }

    public List<FatesCharacter> getWorkingCharacters() {
        return workingCharacters;
    }

    public void setWorkingCharacters(List<FatesCharacter> workingCharacters) {
        this.workingCharacters = workingCharacters;
    }
}
