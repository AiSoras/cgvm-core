package ru.etu.cgvm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Getter;
import ru.etu.cgvm.utils.SettingManager;

import java.io.InputStream;

public class GraphViewer extends javafx.application.Application {

    @Getter
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        stage.setResizable(false);

        stage.setTitle(SettingManager.getProperty("app.title"));
        var url = getClass().getResource(SettingManager.getProperty("app.fxml"));
        InputStream iconStream = getClass().getResourceAsStream(SettingManager.getProperty("app.icon.path"));
        stage.getIcons().add(new Image(iconStream));
        stage.setScene(new Scene(FXMLLoader.load(url)));
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch();
    }
}