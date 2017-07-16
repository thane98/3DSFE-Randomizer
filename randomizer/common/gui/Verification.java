package randomizer.common.gui;

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
import randomizer.Main;
import randomizer.common.data.Gui;
import randomizer.fates.model.processors.FatesVerifier;

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
           "Awakening", "Fates", "Shadows of Valentia"
        ));
        regionBox.getItems().addAll(Arrays.asList(
           "North America", "Japan", "Europe - English",
                "Europe - Spanish", "Europe - German",
                "Europe - French", "Europe - Italian"
        ));

        // Only Fates is support right now.
        gameBox.getSelectionModel().select(1);
        gameBox.setDisable(true);
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
                        boolean verified = FatesVerifier.verify(file, regionBox.getSelectionModel().getSelectedItem());
                        if(verified) { // Move on to options.
                            Parent root;
                            try {
                                root = FXMLLoader.load(Main.class.getResource("common/gui/fxml/FatesOptions.fxml"));
                                Scene scene = new Scene(root,680,520);
                                scene.getStylesheets().add(Main.class.getResource("common/gui/jmetro/JMetroLightTheme.css")
                                        .toExternalForm());
                                Stage stage = Gui.getInstance().getMainStage();
                                Platform.runLater(() -> {
                                    progressBar.setProgress(1);
                                    stage.setResizable(true);
                                    stage.setTitle("Fates Randomizer Options");
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
