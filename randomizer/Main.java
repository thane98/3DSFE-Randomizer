package randomizer;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import randomizer.common.data.FatesData;
import randomizer.common.data.FatesGui;
import randomizer.common.data.Gui;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Gui.getInstance().setMainStage(primaryStage);
			FatesData.getInstance();
			FatesGui.getInstance();
			Parent root = FXMLLoader.load(this.getClass().getResource("common/gui/fxml/Verification.fxml"));
			Scene scene = new Scene(root,320,260);
			scene.getStylesheets().add(getClass().getResource("common/gui/jmetro/JMetroLightTheme.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("common/assets/charlotte.png")));
			primaryStage.setTitle("Randomizer Verification");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}