package projectredstone.ide;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;

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
import java.util.ResourceBundle;

public class Controller implements Initializable {

	@FXML private TreeView<Object> fileView;
	@FXML private ComboBox<String> functionList;
	@FXML private CheckBox privateFunction;
	@FXML private VBox codeBox;
	private Script openedScript;

	public void initialize(URL location, ResourceBundle resources) {
		loadFiles(Main.sourceDirectory);
	}

	@FXML
	private void openFile() {
		if (fileView.getSelectionModel().isEmpty()) return;
		TreeItem item = fileView.getSelectionModel().getSelectedItem();
		ArrayList<TreeItem> itemList = new ArrayList<>();
		itemList.add(item);
		while (item.getParent() != null) {
			item = item.getParent();
			itemList.add(item);
		}
		String fileLocation = "";
		for (int i = itemList.size() - 1; i >= 0; i--) {
			fileLocation = fileLocation + itemList.get(i).getValue() + File.separator; //TODO Use StringBuilder
		}
		fileLocation = fileLocation.substring(0, fileLocation.length() - 1);
		if (fileLocation.endsWith(".prs")) try {
			openedScript = Main.loadScript(new File(fileLocation));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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
