package projectredstone.ide;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {

	public static final String TITLE = "ProjectRedstone IDE 1.0";
	public static File sourceDirectory = new File("Sources");
	public static File openedFile;

	public static void main(String[] args) {
		launch(args);
	}

	public static Script loadScript(File file) throws FileNotFoundException {
		Gson json = new Gson();
		return json.fromJson(new FileReader(file), Script.class);
	}

	@Override
	public void start(Stage stage) {
		try {
			stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("ide.fxml"))));
			stage.setTitle(TITLE);
			stage.setMaximized(true);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
