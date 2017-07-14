package randomizer.common.data;

import randomizer.Main;
import randomizer.common.enums.SkillType;
import randomizer.common.structures.Skill;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FatesGui {
    private static FatesGui instance;

    private List<String> options;
    private List<String> experimentalOptions;
    private String[] paths = { "Birthright", "Conquest", "Revelation" };

    private boolean[] selectedCharacters = new boolean[FatesData.getInstance().getCharacters().size()];
    private boolean[] selectedJobs = new boolean[FatesData.getInstance().getJobs().size()];
    private boolean[] selectedItems = new boolean[FatesData.getInstance().getWeapons().size()];
    private boolean[] selectedSkills = new boolean[FatesData.getInstance().getSkills().size()];
    private boolean[] selectedPaths = new boolean[paths.length];
    private boolean[] selectedOptions;
    private boolean[] selectedExperimentalOptions;

    private FatesGui() {
        try {
            options = Files.readAllLines(Paths.get(Main.class.getResource(
                    "common/data/text/FatesOptions.txt").toURI()));
            experimentalOptions = Files.readAllLines(Paths.get(Main.class.getResource(
                    "common/data/text/FatesExperimentalOptions.txt").toURI()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        // Set up default selections.
        List<Skill> skills = FatesData.getInstance().getSkills();
        selectedOptions = new boolean[options.size()];
        for(int x = 0; x < selectedOptions.length; x++) {
            selectedOptions[x] = true;
        }
        selectedExperimentalOptions = new boolean[experimentalOptions.size()];
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

    public List<String> getExperimentalOptions() {
        return experimentalOptions;
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

    public boolean[] getSelectedExperimentalOptions() {
        return selectedExperimentalOptions;
    }

    public void setSelectedExperimentalOptions(boolean[] selectedExperimentalOptions) {
        this.selectedExperimentalOptions = selectedExperimentalOptions;
    }

    public boolean[] getSelectedPaths() {
        return selectedPaths;
    }

    public void setSelectedPaths(boolean[] selectedPaths) {
        this.selectedPaths = selectedPaths;
    }
}
