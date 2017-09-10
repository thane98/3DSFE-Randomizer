package randomizer.data;

import javafx.stage.Stage;

public class Gui {
	private static Gui instance;

	private Stage mainStage;

	public Gui() {

	}

	public static Gui getInstance() {
		if (instance == null)
			instance = new Gui();
		return instance;
	}

	public Stage getMainStage() {
		return mainStage;
	}

	public void setMainStage(Stage mainStage) {
		this.mainStage = mainStage;
	}
}