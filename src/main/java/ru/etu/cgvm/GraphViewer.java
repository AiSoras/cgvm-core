package ru.etu.cgvm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Getter;
import ru.etu.cgvm.utils.SettingManager;

import java.io.InputStream;
import java.net.URL;

public class GraphViewer extends javafx.application.Application {

    @Getter
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        stage.setTitle("Graph Viewer by Lokkina Olesia");
        URL url = getClass().getResource("/scenes/viewer_scene.fxml");
        InputStream iconStream = getClass().getResourceAsStream(SettingManager.getInstance().getProperty("icon.path"));
        stage.getIcons().add(new Image(iconStream));
        stage.setScene(new Scene(FXMLLoader.load(url)));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}