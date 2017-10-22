package projectredstone.ide;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import qn.projectredstone.api.ProjectRedstoneAPI;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Set;

public class Main extends Application {

	public static final String TITLE = "ProjectRedstone IDE 1.0";
	public static File sourceDirectory;
	public static File api;
	public static final File SETTINGS = new File("settings.json");

	public static void main(String[] args) {
		System.out.println("Running: " + TITLE);
		System.out.println("Source Directory: " + sourceDirectory);
		System.out.println("Loading settings..");
		loadSettings();
		launch(args);
	}

	public static void loadSettings() {
		try {
			boolean isExists = !SETTINGS.createNewFile();
			Gson json = new Gson();
			if (!isExists) {
				Settings settings = new Settings();
				settings.sourceFolder = "Sources";
				settings.apiLocation = "Library" + File.separator + "ProjectRedstone-API";
				json.toJson(settings, new FileWriter(SETTINGS));
			}
			Settings settings = json.fromJson(new FileReader(SETTINGS), Settings.class);
			sourceDirectory = new File(settings.sourceFolder);
			api = new File("apiLocation");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadAPI(File api) {
		try {
			URLClassLoader classLoader = new URLClassLoader(new URL[]{api.toURI().toURL()}, Main.class.getClassLoader());
			Class linker = classLoader.loadClass("qn.projectredstone.api.Linker");
			Class functionData = classLoader.loadClass("qn.projectredstone.api.FunctionLinker");
			@SuppressWarnings("unchecked")
			Method getRegisteredFunctions = linker.getMethod("getRegisteredFunctions");
			functionData a = functionData.cast(getRegisteredFunctions.invoke(null));
		} catch (ClassNotFoundException | MalformedURLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static ScriptSerializer loadScript(File file) throws FileNotFoundException {
		return new Gson().fromJson(new FileReader(file), ScriptSerializer.class);
	}

	@Override
	public void start(Stage stage) {
		try {
			System.out.println("Initializing GUI..");
			stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("ide.fxml"))));
			stage.setTitle(TITLE);
			stage.setMaximized(true);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
