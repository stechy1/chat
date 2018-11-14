package cz.stechy.chat.controller.main;

import cz.stechy.chat.controller.OnCloseListener;
import cz.stechy.chat.controller.connect.ConnectController;
import cz.stechy.chat.model.ChatContact;
import cz.stechy.chat.service.ChatService;
import cz.stechy.chat.service.ClientCommunicationService;
import cz.stechy.chat.service.IChatService;
import cz.stechy.chat.service.IClientCommunicationService;
import cz.stechy.chat.widget.ChatEntryCell;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.MapChangeListener;
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
    private ListView<ChatContact> lvContactList;
    @FXML
    private TabPane paneChatContainer;
    @FXML
    private Button btnSend;
    @FXML
    private TextField txtMessage;

    private final IClientCommunicationService communicator = new ClientCommunicationService();
    private final IChatService chatService = new ChatService(communicator);

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

    private final MapChangeListener<? super String, ? super ChatContact> chatClientListener = change -> {
        if (change.wasAdded()) {
            lvContactList.getItems().addAll(change.getValueAdded());
        }

        if (change.wasRemoved()) {
            lvContactList.getItems().removeAll(change.getValueRemoved());
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lvContactList.setCellFactory(param -> new ChatEntryCell());

        chatService.getClients().addListener(this.chatClientListener);
    }

    @Override
    public void onClose() {
        communicator.disconnect();
    }

    @FXML
    private void handleConnect(ActionEvent actionEvent) {
        try {
            final ConnectController controller = showNewWindow("connect/connect", "Připojit k serveru...");
            controller.setCommunicator(communicator);
            controller.setChatService(chatService);
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
