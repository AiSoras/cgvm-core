package ru.etu.cgvm.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import lombok.NoArgsConstructor;
import ru.etu.cgvm.GraphViewer;
import ru.etu.cgvm.javacc.parser.CgifParser;
import ru.etu.cgvm.javacc.parser.ParseException;
import ru.etu.cgvm.objects.nodes.Graph;
import ru.etu.cgvm.utils.SettingManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static ru.etu.cgvm.utils.FileUtils.readContent;
import static ru.etu.cgvm.utils.FileUtils.saveContentToFile;

@NoArgsConstructor
public class ViewerSceneController {

    private static final FileChooser fileChooser = new FileChooser();
    private static final SettingManager settingManager = SettingManager.getInstance();

    static {
        configureFileChooser(fileChooser);
    }

    @FXML
    private TextArea input;

    @FXML
    private void clickDrawButton() {
        try {
            Graph graph = new CgifParser().parse(input.getText());
        } catch (ParseException e) {
            showErrorAlert(e);
        }
    }

    @FXML
    private void openFile() {
        File file = fileChooser.showOpenDialog(GraphViewer.getPrimaryStage());
        if (file != null) {
            try {
                String content = readContent(file);
                input.setText(content);
            } catch (IOException e) {
                showErrorAlert(e);
            }
        }
    }

    @FXML
    private void saveAsFile() {
        File file = fileChooser.showSaveDialog(GraphViewer.getPrimaryStage());
        if (file != null) {
            try {
                saveContentToFile(input.getText(), file);
                showInfoAlert(String.format("The file [%s] is saved successfully!", file.getAbsolutePath()));
            } catch (FileNotFoundException e) {
                showErrorAlert(e);
            }
        }
    }

    private void showErrorAlert(Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(exception.getClass().getSimpleName());
        alert.setHeaderText("The exception is occurred!");
        alert.setContentText("Details: " + exception.getMessage());
        alert.showAndWait();
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setInitialDirectory(
                new File(settingManager.getProperty("file.default_folder"))
        );
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        settingManager.getProperty("file.type"),
                        settingManager.getProperty("file.extension")));

    }
}