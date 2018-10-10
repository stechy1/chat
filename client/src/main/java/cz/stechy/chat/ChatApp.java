package cz.stechy.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main/main.fxml"));
        final Parent parent = loader.load();
        final Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("Chat - ITnetwork tutorial");
        stage.setWidth(640);
        stage.setHeight(420);
        stage.show();
    }
}
