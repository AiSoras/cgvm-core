package ru.etu.cgvm.ui.controllers;

import com.gluonhq.charm.glisten.control.ToggleButtonGroup;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.etu.cgvm.GraphViewer;
import ru.etu.cgvm.notations.cgif.CgifGenerator;
import ru.etu.cgvm.notations.cgif.parser.CgifParser;
import ru.etu.cgvm.notations.cgif.parser.ParseException;
import ru.etu.cgvm.notations.xml.XmlGenerator;
import ru.etu.cgvm.notations.xml.XmlParser;
import ru.etu.cgvm.objects.base.Graph;
import ru.etu.cgvm.objects.graphs.Context;
import ru.etu.cgvm.query.SelectProcessor;
import ru.etu.cgvm.ui.GraphPainter;
import ru.etu.cgvm.utils.GraphObjectUtils;
import ru.etu.cgvm.utils.SettingManager;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.etu.cgvm.utils.FileUtils.readContent;
import static ru.etu.cgvm.utils.FileUtils.saveContentToFile;

@NoArgsConstructor
@Slf4j
public class ViewerSceneController {

    private static final FileChooser fileChooser = new FileChooser();
    private static final CgifParser parser = new CgifParser();

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
    private TextArea queryOutput;

    @FXML
    private Tab resultTab;

    @FXML
    private TabPane queryTabs;

    @FXML
    private SwingNode canvas;

    @FXML
    private void closeApp() {
        GraphViewer.getPrimaryStage().close();
    }

    @FXML
    private void drawGraph() {
        Graph graph = parseGraph(input.getText());
        if (graph != null) {
            mxGraphComponent graphComponent = new GraphPainter().drawGraph(graph);
            canvas.setContent(graphComponent);
        }
    }

    private Context parseGraph(String text) {
        Context context = null;
        try {
            context = switch (getSelectedNotation()) {
                case CGIF -> parser.parse(text);
                case XML -> XmlParser.parse(text, Context.class);
                default -> throw new IllegalArgumentException("Unsupported notation!");
            };
        } catch (Exception e) {
            showErrorAlert(e);
        }
        return context;
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
        try {
            Context query = parser.parse(queryInput.getText());
            Context originalGraph = parseGraph(input.getText());
            var queryContext = GraphObjectUtils.getNonNestedObjects(query, Context.class).iterator().next(); // Обрабатываем только один запрос
            if (!queryContext.isSpecialContext()) {
                showInfoAlert("Выражение не является запросом!");
            }
            Collection<Context> results = SelectProcessor.select(originalGraph, queryContext);
            queryOutput.setText(results.stream()
                    .map(CgifGenerator::convert)
                    .collect(Collectors.joining("\n***\n")));
            queryTabs.getSelectionModel().select(resultTab);
        } catch (ParseException e) {
            showErrorAlert(e);
        }
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
        var file = fileChooser.showOpenDialog(GraphViewer.getPrimaryStage());
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
        var file = fileChooser.showSaveDialog(GraphViewer.getPrimaryStage());
        if (file != null) {
            try {
                var fileExtension = StringUtils.substringAfterLast(file.getName(), '.');
                var fileNotation = Notation.valueOf(fileExtension.toUpperCase());
                if (getSelectedNotation() == fileNotation) {
                    saveContentToFile(input.getText(), file);
                } else {
                    var context = parseGraph(input.getText());
                    if (context != null) {
                        switch (fileNotation) {
                            case XML -> saveContentToFile(XmlGenerator.convert(context), file);
                            case CGIF -> saveContentToFile(CgifGenerator.convert(context), file);
                            default -> throw new IllegalArgumentException("Unsupported file notation!");
                        }
                    } else {
                        return;
                    }
                }
                showInfoAlert(String.format("The file [%s] is saved successfully!", file.getAbsolutePath()));
            } catch (Exception e) {
                showErrorAlert(e);
            }
        }
    }

    @FXML
    private void showAbout() {
        var message = String.format("%s%nVersion: %s",
                SettingManager.getProperty("app.description"),
                SettingManager.getProperty("app.version"));
        showInfoAlert(message);
    }

    private Notation getSelectedNotation() {
        return Notation.valueOf(notationSelector.getToggles().filtered(ToggleButton::isSelected).get(0).getText());
    }

    private void showErrorAlert(Exception exception) {
        log.error(exception.getMessage());

        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(exception.getClass().getSimpleName());
        alert.setHeaderText("The exception is occurred!");
        alert.setContentText("Details: " + exception.getMessage());
        alert.showAndWait();
    }

    private void showInfoAlert(String message) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setInitialDirectory(
                new File(SettingManager.getProperty("file.default_folder"))
        );
        fileChooser.getExtensionFilters().add(Notation.CGIF.getFileFilter());
        fileChooser.getExtensionFilters().add(Notation.XML.getFileFilter());
        // fileChooser.getExtensionFilters().add(Notation.RDF.getFileFilter());
    }
}