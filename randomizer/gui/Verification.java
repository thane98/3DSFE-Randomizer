package randomizer.gui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import randomizer.Randomizer;
import randomizer.awakening.model.processors.prep.AVerifier;
import randomizer.awakening.singletons.*;
import randomizer.data.Gui;
import randomizer.fates.model.processors.prep.FatesVerifier;
import randomizer.fates.singletons.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class Verification implements Initializable {
    @FXML private ComboBox<String> regionBox;
    @FXML private ComboBox<String> gameBox;
    @FXML private Label errorLabel;
    @FXML private ProgressBar progressBar;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        gameBox.getItems().addAll(Arrays.asList(
           "Awakening", "Fates"
        ));
        regionBox.getItems().addAll(Arrays.asList(
           "North America", "Japan", "Europe - English",
                "Europe - Spanish", "Europe - German",
                "Europe - French", "Europe - Italian"
        ));

        // Only Fates is support right now.
        gameBox.getSelectionModel().select(1);
    }

    @FXML
    private void verify() {
        if(regionBox.getSelectionModel().getSelectedIndex() != -1) {
            DirectoryChooser chooser = new DirectoryChooser();
            File file = chooser.showDialog(Gui.getInstance().getMainStage());
            if(file != null) {
                errorLabel.setVisible(false);
                regionBox.setDisable(true);
                gameBox.setDisable(true);

                Task task = new Task<Void>() {
                    @Override public Void call() {
                        Platform.runLater(() -> progressBar.setProgress(-1));

                        boolean verified;
                        if(gameBox.getSelectionModel().getSelectedIndex() == 0) {
                            // Initialize Awakening singletons.
                            AItems.getInstance();
                            AChapters.getInstance();
                            ACharacters.getInstance();
                            ASkills.getInstance();
                            AJobs.getInstance();
                            verified = AVerifier.verify(file, regionBox.getSelectionModel().getSelectedItem());
                        }
                        else {
                            // Initialize Fates singletons.
                            FatesItems.getInstance();
                            FatesChapters.getInstance();
                            FatesCharacters.getInstance();
                            FatesSkills.getInstance();
                            FatesJobs.getInstance();
                            verified = FatesVerifier.verify(file, regionBox.getSelectionModel().getSelectedItem());
                        }
                        if(verified) {
                            Parent root;
                            try {
                                if(gameBox.getSelectionModel().getSelectedIndex() == 0) {
                                    root = FXMLLoader.load(Randomizer.class.getResource("gui/fxml/AwakeningOptions.fxml"));
                                }
                                else
                                    root = FXMLLoader.load(Randomizer.class.getResource("gui/fxml/FatesOptions.fxml"));
                                Scene scene = new Scene(root,500,550);
                                scene.getStylesheets().add(Randomizer.class.getResource("gui/jmetro/JMetroLightTheme.css")
                                        .toExternalForm());
                                Stage stage = Gui.getInstance().getMainStage();
                                Platform.runLater(() -> {
                                    progressBar.setProgress(1);
                                    stage.setResizable(true);
                                    stage.setTitle("Randomizer Options");
                                    stage.setScene(scene);
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            Platform.runLater(() -> {
                                progressBar.setProgress(0);
                                errorLabel.setVisible(true);
                            });
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            }
        }
    }
}
