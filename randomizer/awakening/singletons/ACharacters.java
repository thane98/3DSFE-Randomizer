package randomizer.awakening.singletons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import randomizer.Randomizer;
import randomizer.awakening.model.structures.ACharacter;
import randomizer.common.enums.CharacterType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ACharacters {
    private static ACharacters instance;
    private List<ACharacter> characters;

    private ACharacters() {
        Type characterType = new TypeToken<List<ACharacter>>() {}.getType();
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("data/json/AwakeningCharacters.json"), "UTF-8")));
            characters = gson.fromJson(reader, characterType);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ACharacters getInstance() {
        if (instance == null)
            instance = new ACharacters();
        return instance;
    }

    public List<ACharacter> getSelectedCharacters() {
        boolean[] selected = AGui.getInstance().getSelectedCharacters();
        List<ACharacter> selectedCharacters = new ArrayList<>();
        List<ACharacter> npcs = getCharactersByType(CharacterType.NPC);
        for(ACharacter c : characters) {
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

    public List<ACharacter> getCharactersByType(CharacterType type) {
        List<ACharacter> ACharacters = new ArrayList<>();
        for(ACharacter c : characters) {
            if(c.getCharacterType() == type)
                ACharacters.add(c);
        }
        return ACharacters;
    }

    public ACharacter getByPid(String pid) {
        for(ACharacter c : characters) {
            if(c.getPid().equals(pid))
                return c;
        }
        return null;
    }

    public ACharacter getReplacement(List<ACharacter> characters, String pid) {
        for(ACharacter c : characters) {
            if(c.getTargetPid().equals(pid))
                return c;
        }
        return null;
    }

    public List<ACharacter> getCharacters() {
        return characters;
    }
}
