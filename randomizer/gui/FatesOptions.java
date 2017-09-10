package randomizer.gui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.controlsfx.control.CheckListView;
import randomizer.common.enums.CharacterType;
import randomizer.common.enums.ItemType;
import randomizer.common.structures.FEItem;
import randomizer.common.structures.Job;
import randomizer.common.structures.Skill;
import randomizer.data.Gui;
import randomizer.fates.FatesHub;
import randomizer.fates.model.structures.FatesCharacter;
import randomizer.fates.model.structures.SettingsWrapper;
import randomizer.fates.singletons.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FatesOptions implements Initializable {
    @FXML
    private CheckListView<String> configList;
    @FXML
    private ComboBox<String> menuBox;
    @FXML
    private Slider baseStatPasses;
    @FXML
    private Slider baseStatMax;
    @FXML
    private Slider baseStatMin;
    @FXML
    private Slider growthPasses;
    @FXML
    private Slider growthMin;
    @FXML
    private Slider growthMax;
    @FXML
    private Slider modPasses;
    @FXML
    private Slider modMin;
    @FXML
    private Slider modMax;

    @FXML
    private ProgressBar progressBar;

    private ListChangeListener<Integer> listener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Build menus.
        menuBox.getItems().addAll(Arrays.asList(
                "Basic Options", "Characters", "Classes",
                "Skills", "Items", "Paths"
        ));
        menuBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                changeMenu(newValue));

        // Bind stat sliders to properties in the FatesGui singleton.
        FatesGui gui = FatesGui.getInstance();
        gui.baseStatPassesProperty().bind(baseStatPasses.valueProperty());
        gui.baseStatMinProperty().bind(baseStatMin.valueProperty());
        gui.baseStatMaxProperty().bind(baseStatMax.valueProperty());
        gui.growthPassesProperty().bind(growthPasses.valueProperty());
        gui.growthMinProperty().bind(growthMin.valueProperty());
        gui.growthMaxProperty().bind(growthMax.valueProperty());
        gui.modPassesProperty().bind(modPasses.valueProperty());
        gui.modMinProperty().bind(modMin.valueProperty());
        gui.modMaxProperty().bind(modMax.valueProperty());
    }

    @FXML
    private void changeMenu(String newValue) {
        configList.getItems().clear();
        if (listener != null)
            configList.getCheckModel().getCheckedIndices().removeListener(listener);
        switch (newValue) {
            case "Basic Options":
                for (String s : FatesGui.getInstance().getOptions()) {
                    configList.getItems().add(s);
                }
                for (int x = 0; x < configList.getItems().size(); x++) {
                    if (FatesGui.getInstance().getSelectedOptions()[x])
                        configList.getCheckModel().check(x);
                }
                listener = c -> {
                    c.next();
                    int index;
                    if (c.wasAdded())
                        index = c.getAddedSubList().get(0);
                    else
                        index = c.getRemoved().get(0);
                    FatesGui.getInstance().getSelectedOptions()[index] = c.wasAdded();
                };
                configList.getCheckModel().getCheckedIndices().addListener(listener);
                break;
            case "Characters":
                for (FatesCharacter c : FatesCharacters.getInstance().getCharactersByType(CharacterType.NPC)) {
                    configList.getItems().add(c.getName());
                }
                for (int x = 0; x < configList.getItems().size(); x++) {
                    if (FatesGui.getInstance().getSelectedCharacters()[x])
                        configList.getCheckModel().check(x);
                }
                listener = c -> {
                    c.next();
                    int index;
                    if (c.wasAdded())
                        index = c.getAddedSubList().get(0);
                    else
                        index = c.getRemoved().get(0);
                    FatesGui.getInstance().getSelectedCharacters()[index] = c.wasAdded();
                };
                configList.getCheckModel().getCheckedIndices().addListener(listener);
                break;
            case "Classes":
                for (Job j : FatesJobs.getInstance().getJobs()) {
                    configList.getItems().add(j.getName());
                }
                for (int x = 0; x < configList.getItems().size(); x++) {
                    if (FatesGui.getInstance().getSelectedJobs()[x])
                        configList.getCheckModel().check(x);
                }
                listener = c -> {
                    c.next();
                    int index;
                    if (c.wasAdded())
                        index = c.getAddedSubList().get(0);
                    else
                        index = c.getRemoved().get(0);
                    FatesGui.getInstance().getSelectedJobs()[index] = c.wasAdded();
                };
                configList.getCheckModel().getCheckedIndices().addListener(listener);
                break;
            case "Skills":
                for (Skill s : FatesSkills.getInstance().getSkills()) {
                    configList.getItems().add(s.getName());
                }
                for (int x = 0; x < configList.getItems().size(); x++) {
                    if (FatesGui.getInstance().getSelectedSkills()[x])
                        configList.getCheckModel().check(x);
                }
                listener = c -> {
                    c.next();
                    int index;
                    if (c.wasAdded())
                        index = c.getAddedSubList().get(0);
                    else
                        index = c.getRemoved().get(0);
                    FatesGui.getInstance().getSelectedSkills()[index] = c.wasAdded();
                };
                configList.getCheckModel().getCheckedIndices().addListener(listener);
                break;
            case "Items":
                for (FEItem i : FatesItems.getInstance().getItems()) {
                    if (i.getType() != ItemType.Treasure)
                        configList.getItems().add(i.getName());
                }
                for (int x = 0; x < configList.getItems().size(); x++) {
                    if (FatesGui.getInstance().getSelectedItems()[x])
                        configList.getCheckModel().check(x);
                }
                listener = c -> {
                    c.next();
                    int index;
                    if (c.wasAdded())
                        index = c.getAddedSubList().get(0);
                    else
                        index = c.getRemoved().get(0);
                    FatesGui.getInstance().getSelectedItems()[index] = c.wasAdded();
                };
                configList.getCheckModel().getCheckedIndices().addListener(listener);
                break;
            case "Paths":
                for (String s : FatesGui.getInstance().getPaths()) {
                    configList.getItems().add(s);
                }
                for (int x = 0; x < configList.getItems().size(); x++) {
                    if (FatesGui.getInstance().getSelectedPaths()[x])
                        configList.getCheckModel().check(x);
                }
                listener = c -> {
                    c.next();
                    int index;
                    if (c.wasAdded())
                        index = c.getAddedSubList().get(0);
                    else
                        index = c.getRemoved().get(0);
                    FatesGui.getInstance().getSelectedPaths()[index] = c.wasAdded();
                };
                configList.getCheckModel().getCheckedIndices().addListener(listener);
                break;
            default:
                throw new RuntimeException("Error: changeMenu. Unknown menu type specified.");
        }
    }

    @FXML
    private void randomize() {
        loadCodeBin();
//        Task task = new Task<Void>() {
//            @Override
//            public Void call() {
//                Platform.runLater(() -> progressBar.setVisible(true));
//                Platform.runLater(() -> progressBar.setProgress(-1));
//                FatesHub hub = new FatesHub();
//                hub.randomize();
//                Platform.runLater(() -> progressBar.setProgress(1));
//                return null;
//            }
//        };
//        new Thread(task).start();
        FatesHub hub = new FatesHub();
        hub.randomize();
    }

    @FXML
    private void randomizeWithSettings() {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "JSON Files (*.json)", "*.json"));
        File file = chooser.showOpenDialog(Gui.getInstance().getMainStage());
        try {
            Type type = new TypeToken<SettingsWrapper>() {
            }.getType();
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new BufferedReader(new FileReader(file)));
            SettingsWrapper wrapper = gson.fromJson(reader, type);
            reader.close();
            FatesGui.getInstance().setSelectedOptions(wrapper.getGui().getSelectedOptions());
            FatesGui.getInstance().setSelectedPaths(wrapper.getGui().getSelectedPaths());
            FatesGui.getInstance().setSelectedItems(wrapper.getGui().getSelectedItems());
            FatesGui.getInstance().setSelectedSkills(wrapper.getGui().getSelectedSkills());
            if (wrapper.getGui().getSelectedPaths()[0] && !FatesFiles.getInstance().isBirthrightVerified()) {
                throwUnverifiedPathDialog();
                return;
            }
            if (wrapper.getGui().getSelectedPaths()[1] && !FatesFiles.getInstance().isConquestVerified()) {
                throwUnverifiedPathDialog();
                return;
            }
            if (wrapper.getGui().getSelectedPaths()[2] && !FatesFiles.getInstance().isRevelationVerified()) {
                throwUnverifiedPathDialog();
                return;
            }
            List<FatesCharacter> selectedCharacters = wrapper.getCharacters();
            loadCodeBin();
            FatesHub hub = new FatesHub();
            hub.randomizeWithSettings(selectedCharacters);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCodeBin() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Fates Randomizer");
        alert.setHeaderText("Load code.bin?");
        alert.setContentText("The option you selected requires a decompressed code.bin file. " +
                "Would you like to select one now? If you don't, the option will be ignored!");
        Optional<ButtonType> res = alert.showAndWait();
        res.ifPresent(e -> {
            if (e == ButtonType.OK) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Select code.bin");
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Bin Files (*.bin)",
                        "*.bin"));
                File file = chooser.showOpenDialog(Gui.getInstance().getMainStage());
                if (file != null) {
                    FatesFiles.getInstance().setCode(file);
                }
            }
        });
    }

    private void throwUnverifiedPathDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Unverified Path");
        alert.setHeaderText("Unverified path in settings.");
        alert.setContentText("The specified settings file uses a path which failed to verify.");
        alert.showAndWait();
    }
}
