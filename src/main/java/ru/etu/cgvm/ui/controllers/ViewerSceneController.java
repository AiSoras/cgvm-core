package ru.etu.cgvm.ui.controllers;

import com.gluonhq.charm.glisten.control.ToggleButtonGroup;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;
import lombok.NoArgsConstructor;
import ru.etu.cgvm.GraphViewer;
import ru.etu.cgvm.notations.cgif.parser.CgifParser;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.ui.GraphPainter;
import ru.etu.cgvm.utils.SettingManager;

import java.io.File;
import java.io.FileNotFoundException;

import static ru.etu.cgvm.utils.FileUtils.readContent;
import static ru.etu.cgvm.utils.FileUtils.saveContentToFile;

@NoArgsConstructor
public class ViewerSceneController {

    private static final FileChooser fileChooser = new FileChooser();

    static {
        configureFileChooser(fileChooser);
    }

    @FXML
    private ToggleButtonGroup notationSelector;

    @FXML
    private TextArea input;

    @FXML
    private TextArea queryInput;

    @FXML
    private SwingNode canvas;

    @FXML
    private void closeApp() {
        GraphViewer.getPrimaryStage().close();
    }

    @FXML
    private void drawGraph() {
        Graph graph = null;
        try {
            switch (getSelectedNotation()) {
                case CGIF -> graph = new CgifParser().parse(input.getText());
                default -> /* RDF =RdfReader*/ showErrorAlert(new IllegalArgumentException("Only CGIF is supported!"));
            }
        } catch (Exception e) {
            showErrorAlert(e);
        }

        if (graph != null) {
            mxGraphComponent graphComponent = new GraphPainter().drawGraph(graph);
            canvas.setContent(graphComponent);
        }
    }

    @FXML
    private void clearGraph() {
        mxGraph graph = ((mxGraphComponent) canvas.getContent()).getGraph();
        graph.getModel().beginUpdate();
        graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
        graph.getModel().endUpdate();
    }

    @FXML
    private void executeQuery() {
        String query = queryInput.getText();
    }

    @FXML
    private void zoomIn() {
        ((mxGraphComponent) canvas.getContent()).zoomIn();
    }

    @FXML
    private void zoomOut() {
        ((mxGraphComponent) canvas.getContent()).zoomOut();
    }

    @FXML
    private void openFile() {
        File file = fileChooser.showOpenDialog(GraphViewer.getPrimaryStage());
        if (file != null) {
            try {
                String content = readContent(file);
                input.setText(content);
            } catch (Exception e) {
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

    @FXML
    private void showAbout() {
        String message = String.format("%s%nVersion: %s",
                SettingManager.getProperty("app.description"),
                SettingManager.getProperty("app.version"));
        showInfoAlert(message);
    }

    private Notation getSelectedNotation() {
        return Notation.valueOf(notationSelector.getToggles().filtered(ToggleButton::isSelected).get(0).getText());
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
                new File(SettingManager.getProperty("file.default_folder"))
        );
        fileChooser.getExtensionFilters().add(Notation.CGIF.getFileFilter());
        fileChooser.getExtensionFilters().add(Notation.RDF.getFileFilter());
    }
}