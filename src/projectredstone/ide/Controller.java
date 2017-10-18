package projectredstone.ide;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller implements Initializable {

	@FXML private TreeView<Object> fileView;
	@FXML private ComboBox<Function> functionList;
	@FXML private CheckBox privateFunction;
	@FXML private VBox codeBox;
	//private Script openedScript;

	public void initialize(URL location, ResourceBundle resources) {
		Callback<ListView<Function>, ListCell<Function>> factory = lv -> new ListCell<Function>() {
			@Override
			protected void updateItem(Function item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.name);
			}
		};
		functionList.setCellFactory(factory);
		functionList.setButtonCell(factory.call(null));
		loadFiles(Main.sourceDirectory);
	}

	@FXML
	public void createFile() {
		try {
			File parent = getFile(fileView.getSelectionModel().getSelectedItem());
			if (parent.isFile()) {
				parent = parent.getParentFile();
			}
			System.out.println("New script will locate at: " + parent.getPath());
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle(Main.TITLE);
			dialog.setHeaderText("Create a new script.");
			dialog.setContentText("Name: ");
			Optional<String> result = dialog.showAndWait();
			if (!result.isPresent()) return;
			File newFile = new File(parent + File.separator + result.get() + ".prs");
			try {
				boolean isSuccessful = newFile.createNewFile();
				if (!isSuccessful) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle(Main.TITLE);
					alert.setHeaderText("There is the file with the same name already!");
					alert.setContentText("The file '" + newFile.getPath() + "' exists already.");
					alert.show();
				}
				System.out.println("Script created: " + newFile.getPath());
				loadFiles(Main.sourceDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (NullPointerException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle(Main.TITLE);
			alert.setHeaderText("Please select the folder where file will be created!");
			alert.setContentText("You didn't select any folder.");
			alert.show();
		}
	}

	@FXML
	public void openFileWithMouse(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) openFile();
	}

	@FXML
	public void openFileWithKeyboard(KeyEvent event) {
		if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) openFile();
	}

	public void openFile() {
		if (fileView.getSelectionModel().isEmpty()) return;
		//TODO Stop loading if the file is same as before
		File file = getFile(fileView.getSelectionModel().getSelectedItem());
		System.out.println("Selected file: " + file.getPath()); /*
		if (file.getPath().endsWith(".prs")) try {
			System.out.println("Load file: " + file.getPath());
			setupWorkspace(Main.loadScript(new File(file.getPath())));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} */
	}
/*
	public void setupWorkspace(Script script) {
		for (Function function : script.functions) {
			functionList.
		}
	} */

	private File getFile(TreeItem item) throws NullPointerException {
		ArrayList<TreeItem> itemList = new ArrayList<>();
		itemList.add(item);
		while (item.getParent() != null) {
			item = item.getParent();
			itemList.add(item);
		}
		StringBuilder fileLocationBuilder = new StringBuilder();
		for (int i = itemList.size() - 1; i >= 0; i--) {
			fileLocationBuilder.append(itemList.get(i).getValue()).append(File.separator);
		}
		String fileLocation = fileLocationBuilder.toString();
		fileLocation = fileLocation.substring(0, fileLocation.length() - 1);
		return new File(fileLocation);
	}

	private void loadFiles(File rootDirectory) {
		TreeItem<Object> tree = new TreeItem<>(rootDirectory.getPath());

		List<TreeItem<Object>> dirs = new ArrayList<>();
		List<TreeItem<Object>> files = new ArrayList<>();

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(rootDirectory.getPath()))) {
			for(Path path: directoryStream) {
				if (Files.isDirectory(path)) {
					TreeItem<Object> subDirectory = new TreeItem<>(path);
					try (DirectoryStream<Path> directoryStream_ = Files.newDirectoryStream(Paths.get(path.toString()))) {
						for(Path subDir: directoryStream_) {
							if (!Files.isDirectory(subDir)) {
								String subTree = subDir.toString();
								TreeItem<Object> subLeafs = new TreeItem<>(subTree.substring(1 + subTree.lastIndexOf(File.separator)));
								subDirectory.getChildren().add(subLeafs);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					dirs.add(subDirectory);
				} else {
					String strPath = path.toString();
					files.add(new TreeItem<>(strPath.substring(1 + strPath.lastIndexOf(File.separator))));
				}
			}

			tree.getChildren().addAll(dirs);
			tree.getChildren().addAll(files);

			fileView.setRoot(tree);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
