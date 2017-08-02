package randomizer.fates.model.structures;

import randomizer.fates.singletons.FatesGui;

import java.util.List;

public class SettingsWrapper {
    private List<FatesCharacter> characters;
    private FatesGui gui;

    public SettingsWrapper() {}

    public FatesGui getGui() {
        return gui;
    }

    public void setGui(FatesGui gui) {
        this.gui = gui;
    }

    public List<FatesCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(List<FatesCharacter> characters) {
        this.characters = characters;
    }
}
