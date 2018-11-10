package cz.stechy.chat.controller.main;

import cz.stechy.chat.controller.OnCloseListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainController implements Initializable, OnCloseListener {

    private static final String FXML_FORMAT = "/fxml/%s.fxml";

    @FXML
    private ListView lvContactList;
    @FXML
    private TabPane paneChatContainer;
    @FXML
    private Button btnSend;
    @FXML
    private TextField txtMessage;

    /**
     * Načte a zobrazí nové okno a vrátí jeho kontroler
     *
     * @param name Název okna, případně cesta k souboru ze složky fxml v resourcech
     * @param title Titulek okna
     * @param <T> Kontroler
     * @return Kontroler okna
     * @throws IOException Pokud fxml soubor neexistuje
     */
    private <T> T showNewWindow(String name, String title) throws IOException {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource(String.format(FXML_FORMAT, name)));
        final Parent parent = loader.load();
        final T controller = loader.getController();
        final Scene scene = new Scene(parent);
        final Stage stage = new Stage();
        if (controller instanceof OnCloseListener) {
            stage.setOnCloseRequest(windowEvent -> ((OnCloseListener) controller).onClose());
        }
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
        return controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void onClose() {
        // TODO v budoucnu reagovat na zavření okna
    }

    @FXML
    private void handleConnect(ActionEvent actionEvent) {
        try {
            showNewWindow("connect/connect", "Připojit k serveru...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose(ActionEvent actionEvent) {

    }

    @FXML
    private void handleSendMessage(ActionEvent actionEvent) {

    }
}
