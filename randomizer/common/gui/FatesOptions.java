package randomizer.common.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import org.controlsfx.control.CheckListView;
import randomizer.common.data.FatesData;
import randomizer.common.data.FatesGui;
import randomizer.common.enums.ItemType;
import randomizer.common.structures.FEItem;
import randomizer.common.structures.Job;
import randomizer.common.structures.Skill;
import randomizer.fates.model.processors.FatesHub;
import randomizer.fates.model.structures.FatesCharacter;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class FatesOptions implements Initializable {
    @FXML private CheckListView<String> configList;
    @FXML private ComboBox<String> menuBox;

    @FXML private Slider baseStatVariance;
    @FXML private Slider baseStatMax;
    @FXML private Slider baseStatMin;
    @FXML private Slider growthVariance;
    @FXML private Slider growthMin;
    @FXML private Slider growthMax;
    @FXML private Slider modVariance;
    @FXML private Slider modMin;
    @FXML private Slider modMax;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuBox.getItems().addAll(Arrays.asList(
                "Basic Options", "Experimental Options", "Characters", "Classes",
                "Skills", "Items", "Paths"
        ));
        menuBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                changeMenu(oldValue, newValue));

        FatesGui gui = FatesGui.getInstance();
        gui.setBaseStatVariance(baseStatVariance);
        gui.setBaseStatMin(baseStatMin);
        gui.setBaseStatMax(baseStatMax);
        gui.setGrowthVariance(growthVariance);
        gui.setGrowthMin(growthMin);
        gui.setGrowthMax(growthMax);
        gui.setModVariance(modVariance);
        gui.setModMin(modMin);
        gui.setModMax(modMax);
    }

    @FXML
    private void changeMenu(String oldValue, String newValue) {
        if(oldValue != null) {
            switch(oldValue) {
                case "Basic Options":
                    for(int x = 0; x < configList.getItems().size(); x++) {
                        FatesGui.getInstance().getSelectedOptions()[x] = configList.getCheckModel().isChecked(x);
                    }
                    break;
                case "Experimental Options":
                    for(int x = 0; x < configList.getItems().size(); x++) {
                        FatesGui.getInstance().getSelectedExperimentalOptions()[x] = configList.getCheckModel().isChecked(x);
                    }
                    break;
                case "Characters":
                    for(int x = 0; x < configList.getItems().size(); x++) {
                        FatesGui.getInstance().getSelectedCharacters()[x] = configList.getCheckModel().isChecked(x);
                    }
                    break;
                case "Classes":
                    for(int x = 0; x < configList.getItems().size(); x++) {
                        FatesGui.getInstance().getSelectedJobs()[x] = configList.getCheckModel().isChecked(x);
                    }
                    break;
                case "Skills":
                    for(int x = 0; x < configList.getItems().size(); x++) {
                        FatesGui.getInstance().getSelectedSkills()[x] = configList.getCheckModel().isChecked(x);
                    }
                    break;
                case "Items":
                    for(int x = 0; x < configList.getItems().size(); x++) {
                        FatesGui.getInstance().getSelectedItems()[x] = configList.getCheckModel().isChecked(x);
                    }
                    break;
                case "Paths":
                    for(int x = 0; x < configList.getItems().size(); x++) {
                        FatesGui.getInstance().getSelectedPaths()[x] = configList.getCheckModel().isChecked(x);
                    }
                    break;
            }
        }
        configList.getItems().clear();
        switch(newValue) {
            case "Basic Options":
                for(String s : FatesGui.getInstance().getOptions()) {
                    configList.getItems().add(s);
                }
                for(int x = 0; x < configList.getItems().size(); x++) {
                    if(FatesGui.getInstance().getSelectedOptions()[x])
                        configList.getCheckModel().check(x);
                }
                break;
            case "Experimental Options":
                for(String s : FatesGui.getInstance().getExperimentalOptions()) {
                    configList.getItems().add(s);
                }
                for(int x = 0; x < configList.getItems().size(); x++) {
                    if(FatesGui.getInstance().getSelectedExperimentalOptions()[x])
                        configList.getCheckModel().check(x);
                }
                break;
            case "Characters":
                for(FatesCharacter c : FatesData.getInstance().getCharacters()) {
                    configList.getItems().add(c.getName());
                }
                for(int x = 0; x < configList.getItems().size(); x++) {
                    if(FatesGui.getInstance().getSelectedCharacters()[x])
                        configList.getCheckModel().check(x);
                }
                break;
            case "Classes":
                for(Job j : FatesData.getInstance().getJobs()) {
                    configList.getItems().add(j.getName());
                }
                for(int x = 0; x < configList.getItems().size(); x++) {
                    if(FatesGui.getInstance().getSelectedJobs()[x])
                        configList.getCheckModel().check(x);
                }
                break;
            case "Skills":
                for(Skill s : FatesData.getInstance().getSkills()) {
                    configList.getItems().add(s.getName());
                }
                for(int x = 0; x < configList.getItems().size(); x++) {
                    if(FatesGui.getInstance().getSelectedSkills()[x])
                        configList.getCheckModel().check(x);
                }
                break;
            case "Items":
                for(FEItem i : FatesData.getInstance().getItems()) {
                    if(i.getType() != ItemType.Treasure)
                        configList.getItems().add(i.getName());
                }
                for(int x = 0; x < configList.getItems().size(); x++) {
                    if(FatesGui.getInstance().getSelectedItems()[x])
                        configList.getCheckModel().check(x);
                }
                break;
            case "Paths":
                for(String s : FatesGui.getInstance().getPaths()) {
                    configList.getItems().add(s);
                }
                for(int x = 0; x < configList.getItems().size(); x++) {
                    if(FatesGui.getInstance().getSelectedPaths()[x])
                        configList.getCheckModel().check(x);
                }
                break;
        }
    }

    @FXML
    private void randomize() {
        FatesHub hub = new FatesHub();
        hub.randomize();
    }

    @FXML
    private void randomizeWithSettings() {

    }
}
