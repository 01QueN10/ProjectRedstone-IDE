package projectredstone.ide;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import projectredstone.ide.api.FunctionData;
import projectredstone.ide.api.Linker;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

public class Main extends Application {

	public static final String TITLE = "ProjectRedstone IDE 1.0";
	public static File sourceDirectory;
	public static File apiDirectory;
	public static final File SETTINGS = new File("UserData" + File.separator + "settings.json");
	public static final Logger LOGGER = LoggerFactory.getLogger("ProjectRedstone");

	public static void main(String[] args) {
		LOGGER.info("Launching application..");
		launch(args);
	}

	public void init() {
		LOGGER.info("Loading settings..");
		loadSettings();
		LOGGER.info("Source directory: {}", sourceDirectory);
		LOGGER.info("API directory: {}", apiDirectory);
	}

	private void loadSettings() {
		try {
			boolean isExists = !SETTINGS.createNewFile();
			Gson json = new GsonBuilder().setPrettyPrinting().create();
			if (!isExists) {
				Settings settings = new Settings();
				settings.sourceDirectory = "Sources";
				settings.apiDirectory = "Library" + File.separator + "ProjectRedstone-API";
				FileWriter writer = new FileWriter(SETTINGS);
				json.toJson(settings, writer);
				writer.flush();
				writer.close();
			}
			FileReader reader = new FileReader(SETTINGS);
			try {
				Settings settings = json.fromJson(reader, Settings.class);
				reader.close();
				sourceDirectory = new File(settings.sourceDirectory);
				apiDirectory = new File(settings.apiDirectory);
			} catch (JsonSyntaxException | NullPointerException e) {
				LOGGER.error("Cannot load the config file.");
				LOGGER.warn("Creating a new config file..");
				reader.close();
				SETTINGS.delete();
				loadSettings();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadAPI(File api) {
		try {
			URLClassLoader classLoader = new URLClassLoader(new URL[]{api.toURI().toURL()}, Main.class.getClassLoader());
			Class linkerRaw = classLoader.loadClass("qn.projectredstone.api.Linker");
			boolean isLinker = false;
			for (Class interface_ : linkerRaw.getInterfaces()) {
				if (interface_.newInstance() instanceof Linker) isLinker = true;
			}
			if (!isLinker) throw new Exception("qn.projectredstone.apiDirectory.Linker is not a instance of Linker!");
			Linker linker = (Linker) linkerRaw.newInstance();
			FunctionData[] registeredFunctions = linker.getRegisteredFunctions();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ScriptSerializer loadScript(File file) throws FileNotFoundException {
		return new Gson().fromJson(new FileReader(file), ScriptSerializer.class);
	}

	public void start(Stage stage) {
		try {
			LOGGER.info("Initializing GUI..");
			stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("ide.fxml"))));
			stage.setTitle(TITLE);
			stage.setMaximized(true);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
