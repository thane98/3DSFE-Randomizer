package randomizer.fates.singletons;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import randomizer.Randomizer;
import randomizer.common.enums.ItemType;
import randomizer.common.structures.FEItem;
import randomizer.common.structures.Job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FatesItems {
    private static FatesItems instance;
    private List<FEItem> items;

    private String[] playerIids = {
            "IID_青銅の剣", "IID_青銅の槍", "IID_青銅の斧", "IID_青銅の暗器",
            "IID_青銅の弓", "IID_ファイアー", "IID_ライブ", "IID_獣石",
            "IID_乱拳", "IID_岩塊"
    };

    private FatesItems() {
        try {
            Type itemType = new TypeToken<List<FEItem>>() {}.getType();
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(Randomizer.class
                    .getResourceAsStream("data/json/FatesItems.json"), "UTF-8")));
            items = gson.fromJson(reader, itemType);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FatesItems getInstance() {
        if (instance == null)
            instance = new FatesItems();
        return instance;
    }

    public List<FEItem> getWeapons() {
        List<FEItem> weapons = new ArrayList<>(); // TODO: Refactor getWeapons
        for(FEItem i : items) {
            if(i.getType() != ItemType.Treasure)
                weapons.add(i);
        }
        return weapons;
    }

    public List<FEItem> getSelectedItems(ItemType type) {
        if(type == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "getSelectedItems. type must not be null.");

        List<FEItem> selected = new ArrayList<>();
        List<FEItem> weapons = getWeapons();
        boolean[] selectedItems = FatesGui.getInstance().getSelectedItems();
        for(int x = 0; x < weapons.size(); x++) {
            if(weapons.get(x).getType() == type && selectedItems[x])
                selected.add(weapons.get(x));
        }
        return selected;
    }

    public FEItem generateItem(Job j) {
        if(j == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "generateItem. j must not be null.");

        Random random = new Random();
        List<FEItem> items = getSelectedItems(j.getItemType());
        return items.get(random.nextInt(items.size()));
    }

    public FEItem generatePlayerItem(Job j) {
        if(j == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "generatePlayerItem. j must not be null.");

        Random random = new Random();
        List<FEItem> weapons = getDebugItems();
        List<FEItem> selected = new ArrayList<>();
        for (FEItem weapon : weapons) {
            if (weapon.getType() == j.getItemType())
                selected.add(weapon);
        }
        return selected.get(random.nextInt(selected.size()));
    }

    private List<FEItem> getDebugItems() {
        List<FEItem> eligible = new ArrayList<>();
        for(String s : playerIids)
            eligible.add(getByIid(s));
        return  eligible;
    }

    private FEItem getByIid(String iid) {
        if(iid == null)
            throw new IllegalArgumentException("Violation of precondidition: " +
                    "getByIid. iid must not be null.");

        for(FEItem i : getWeapons()) {
            if(i.getIid().equals(iid))
                return i;
        }
        return null;
    }

    public List<FEItem> getItems() {
        return items;
    }
}
