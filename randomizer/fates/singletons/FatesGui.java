package randomizer.fates.singletons;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import randomizer.Randomizer;
import randomizer.common.enums.CharacterType;
import randomizer.common.enums.SkillType;
import randomizer.common.structures.Skill;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FatesGui {
    private static transient FatesGui instance;
    private transient IntegerProperty baseStatPasses = new SimpleIntegerProperty();
    private transient IntegerProperty baseStatMin = new SimpleIntegerProperty();
    private transient IntegerProperty baseStatMax = new SimpleIntegerProperty();
    private transient IntegerProperty growthPasses = new SimpleIntegerProperty();
    private transient IntegerProperty growthMin = new SimpleIntegerProperty();
    private transient IntegerProperty growthMax = new SimpleIntegerProperty();
    private transient IntegerProperty modPasses = new SimpleIntegerProperty();
    private transient IntegerProperty modMin = new SimpleIntegerProperty();
    private transient IntegerProperty modMax = new SimpleIntegerProperty();
    private transient List<String> options;
    private transient String[] paths = { "Birthright", "Conquest", "Revelation" };

    private transient boolean[] selectedCharacters = new boolean[FatesCharacters.getInstance()
            .getCharactersByType(CharacterType.NPC).size()];
    private boolean[] selectedJobs = new boolean[FatesJobs.getInstance().getJobs().size()];
    private boolean[] selectedItems = new boolean[FatesItems.getInstance().getWeapons().size()];
    private boolean[] selectedSkills = new boolean[FatesSkills.getInstance().getSkills().size()];
    private boolean[] selectedPaths = new boolean[paths.length];
    private boolean[] selectedOptions;

    private FatesGui() {
        try {
            options = Files.readAllLines(Paths.get(Randomizer.class.getResource(
                    "data/text/FatesOptions.txt").toURI()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        // Set up default selections.
        List<Skill> skills = FatesSkills.getInstance().getSkills();
        selectedOptions = new boolean[options.size()];
        for(int x = 0; x < selectedOptions.length; x++) {
            selectedOptions[x] = true;
        }
        for(int x = 0; x < selectedCharacters.length; x++) {
            selectedCharacters[x] = true;
        }
        for(int x = 0; x < selectedJobs.length; x++) {
            selectedJobs[x] = true;
        }
        for(int x = 0; x < skills.size(); x++) {
            selectedSkills[x] = skills.get(x).getType() != SkillType.Enemy;
        }
        for(int x = 0; x < selectedItems.length; x++) {
            selectedItems[x] = true;
        }
        for(int x = 0; x < selectedPaths.length; x++) {
            selectedPaths[x] = false;
        }
    }

    public static FatesGui getInstance() {
        if (instance == null)
            instance = new FatesGui();
        return instance;
    }

    public boolean[] getSelectedCharacters() {
        return selectedCharacters;
    }

    public void setSelectedCharacters(boolean[] selectedCharacters) {
        this.selectedCharacters = selectedCharacters;
    }

    public boolean[] getSelectedJobs() {
        return selectedJobs;
    }

    public void setSelectedJobs(boolean[] selectedJobs) {
        this.selectedJobs = selectedJobs;
    }

    public boolean[] getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(boolean[] selectedItems) {
        this.selectedItems = selectedItems;
    }

    public boolean[] getSelectedSkills() {
        return selectedSkills;
    }

    public void setSelectedSkills(boolean[] selectedSkills) {
        this.selectedSkills = selectedSkills;
    }

    public List<String> getOptions() {
        return options;
    }

    public String[] getPaths() {
        return paths;
    }

    public boolean[] getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(boolean[] selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public boolean[] getSelectedPaths() {
        return selectedPaths;
    }

    public void setSelectedPaths(boolean[] selectedPaths) {
        this.selectedPaths = selectedPaths;
    }

    public int getBaseStatPasses() {
        return baseStatPasses.get();
    }

    public IntegerProperty baseStatPassesProperty() {
        return baseStatPasses;
    }

    public int getBaseStatMin() {
        return baseStatMin.get();
    }

    public IntegerProperty baseStatMinProperty() {
        return baseStatMin;
    }

    public int getBaseStatMax() {
        return baseStatMax.get();
    }

    public IntegerProperty baseStatMaxProperty() {
        return baseStatMax;
    }

    public int getGrowthPasses() {
        return growthPasses.get();
    }

    public IntegerProperty growthPassesProperty() {
        return growthPasses;
    }

    public int getGrowthMin() {
        return growthMin.get();
    }

    public IntegerProperty growthMinProperty() {
        return growthMin;
    }

    public int getGrowthMax() {
        return growthMax.get();
    }

    public IntegerProperty growthMaxProperty() {
        return growthMax;
    }

    public int getModPasses() {
        return modPasses.get();
    }

    public IntegerProperty modPassesProperty() {
        return modPasses;
    }

    public int getModMin() {
        return modMin.get();
    }

    public IntegerProperty modMinProperty() {
        return modMin;
    }

    public int getModMax() {
        return modMax.get();
    }

    public IntegerProperty modMaxProperty() {
        return modMax;
    }
}
