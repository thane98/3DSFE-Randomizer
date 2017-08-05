package randomizer.gui;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import org.controlsfx.control.CheckListView;
import randomizer.awakening.AwakeningHub;
import randomizer.awakening.model.structures.ACharacter;
import randomizer.awakening.singletons.*;
import randomizer.common.enums.CharacterType;
import randomizer.common.enums.ItemType;
import randomizer.common.structures.FEItem;
import randomizer.common.structures.Job;
import randomizer.common.structures.Skill;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class AwakeningOptions implements Initializable {
    @FXML private CheckListView<String> configList;
    @FXML private ComboBox<String> menuBox;

    @FXML private Slider baseStatPasses;
    @FXML private Slider baseStatMax;
    @FXML private Slider baseStatMin;
    @FXML private Slider modPasses;
    @FXML private Slider modMin;
    @FXML private Slider modMax;

    @FXML private ProgressBar progressBar;

    private ListChangeListener<Integer> listener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuBox.getItems().addAll(Arrays.asList(
                "Basic Options", "Characters", "Classes",
                "Skills", "Items"
        ));
        menuBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                changeMenu(newValue));

        AGui gui = AGui.getInstance();
        gui.baseStatPassesProperty().bind(baseStatPasses.valueProperty());
        gui.baseStatMinProperty().bind(baseStatMin.valueProperty());
        gui.baseStatMaxProperty().bind(baseStatMax.valueProperty());
        gui.modPassesProperty().bind(modPasses.valueProperty());
        gui.modMinProperty().bind(modMin.valueProperty());
        gui.modMaxProperty().bind(modMax.valueProperty());
    }

    @FXML
    private void changeMenu(String newValue) {
        configList.getItems().clear();
        if(listener != null)
            configList.getCheckModel().getCheckedIndices().removeListener(listener);
        switch(newValue) {
            case "Basic Options":
                for(String s : AGui.getInstance().getOptions()) {
                    configList.getItems().add(s);
                }
                for(int x = 0; x < configList.getItems().size(); x++) {
                    if(AGui.getInstance().getSelectedOptions()[x])
                        configList.getCheckModel().check(x);
                }
                listener = c -> {
                    c.next();
                    int index;
                    if(c.wasAdded())
                        index = c.getAddedSubList().get(0);
                    else
                        index = c.getRemoved().get(0);
                    AGui.getInstance().getSelectedOptions()[index] = c.wasAdded();
                };
                configList.getCheckModel().getCheckedIndices().addListener(listener);
                break;
            case "Characters":
                for(ACharacter c : ACharacters.getInstance().getCharactersByType(CharacterType.FirstGen)) {
                    configList.getItems().add(c.getName());
                }
                for(int x = 0; x < configList.getItems().size(); x++) {
                    if(AGui.getInstance().getSelectedCharacters()[x])
                        configList.getCheckModel().check(x);
                }
                listener = c -> {
                    c.next();
                    int index;
                    if(c.wasAdded())
                        index = c.getAddedSubList().get(0);
                    else
                        index = c.getRemoved().get(0);
                    AGui.getInstance().getSelectedCharacters()[index] = c.wasAdded();
                };
                configList.getCheckModel().getCheckedIndices().addListener(listener);
                break;
            case "Classes":
                for(Job j : AJobs.getInstance().getJobs()) {
                    configList.getItems().add(j.getName());
                }
                for(int x = 0; x < configList.getItems().size(); x++) {
                    if(AGui.getInstance().getSelectedJobs()[x])
                        configList.getCheckModel().check(x);
                }
                listener = c -> {
                    c.next();
                    int index;
                    if(c.wasAdded())
                        index = c.getAddedSubList().get(0);
                    else
                        index = c.getRemoved().get(0);
                    AGui.getInstance().getSelectedJobs()[index] = c.wasAdded();
                };
                configList.getCheckModel().getCheckedIndices().addListener(listener);
                break;
            case "Skills":
                for(Skill s : ASkills.getInstance().getSkills()) {
                    configList.getItems().add(s.getName());
                }
                for(int x = 0; x < configList.getItems().size(); x++) {
                    if(AGui.getInstance().getSelectedSkills()[x])
                        configList.getCheckModel().check(x);
                }
                listener = c -> {
                    c.next();
                    int index;
                    if(c.wasAdded())
                        index = c.getAddedSubList().get(0);
                    else
                        index = c.getRemoved().get(0);
                    AGui.getInstance().getSelectedSkills()[index] = c.wasAdded();
                };
                configList.getCheckModel().getCheckedIndices().addListener(listener);
                break;
            case "Items":
                for(FEItem i : AItems.getInstance().getItems()) {
                    if(i.getType() != ItemType.Treasure)
                        configList.getItems().add(i.getName());
                }
                for(int x = 0; x < configList.getItems().size(); x++) {
                    if(AGui.getInstance().getSelectedItems()[x])
                        configList.getCheckModel().check(x);
                }
                listener = c -> {
                    c.next();
                    int index;
                    if(c.wasAdded())
                        index = c.getAddedSubList().get(0);
                    else
                        index = c.getRemoved().get(0);
                    AGui.getInstance().getSelectedItems()[index] = c.wasAdded();
                };
                configList.getCheckModel().getCheckedIndices().addListener(listener);
                break;
        }
    }

    @FXML
    private void randomize() {
        AwakeningHub hub = new AwakeningHub();
        hub.randomize();
    }

    @FXML
    private void randomizeWithSettings() {

    }
}
