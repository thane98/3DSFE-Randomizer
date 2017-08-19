package randomizer.fates.singletons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import randomizer.Randomizer;
import randomizer.common.enums.CharacterType;
import randomizer.fates.model.structures.FatesCharacter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FatesCharacters {
    private static FatesCharacters instance;
    private List<FatesCharacter> characters;
    private List<String> bannedPids;

    private FatesCharacters() {
        Type characterType = new TypeToken<List<FatesCharacter>>() {}.getType();
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("data/json/FatesCharacters.json"))));
            characters = gson.fromJson(reader, characterType);
            reader.close();

            bannedPids = Files.readAllLines(Paths.get(Randomizer.class.getResource(
                    "data/text/FatesBannedPids.txt").toURI()));

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

    public List<FatesCharacter> getSelectedCharacters() {
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
        List<FatesCharacter> fatesCharacters = new ArrayList<>();
        for(FatesCharacter c : characters) {
            if(c.getCharacterType() == type)
                fatesCharacters.add(c);
        }
        return fatesCharacters;
    }

    public FatesCharacter getByPid(String pid) {
        for(FatesCharacter c : characters) {
            if(c.getPid().equals(pid))
                return c;
        }
        return null;
    }

    public FatesCharacter getByPid(List<FatesCharacter> characters, String pid) {
        for(FatesCharacter c : characters) {
            if(c.getPid().equals(pid))
                return c;
        }
        return null;
    }

    public FatesCharacter getReplacement(List<FatesCharacter> characters, String pid) {
        for(FatesCharacter c : characters) {
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
}
