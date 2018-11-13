package cz.stechy.chat.controller.main;

import cz.stechy.chat.controller.OnCloseListener;
import cz.stechy.chat.controller.connect.ConnectController;
import cz.stechy.chat.model.ChatContact;
import cz.stechy.chat.service.ChatService;
import cz.stechy.chat.service.ClientCommunicationService;
import cz.stechy.chat.service.IChatService;
import cz.stechy.chat.service.IClientCommunicationService;
import cz.stechy.chat.widget.ChatEntryCell;
import cz.stechy.chat.widget.ChatTab;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
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

    private ChatTab makeNewTab(ChatContact chatContact) {
        final ChatTab chatTab = new ChatTab(chatContact);
        chatTab.setUserData(chatContact);
        return chatTab;
    }

    private void showConversation(ChatContact contact) {
        final Optional<ChatTab> optionalTab = paneChatContainer.getTabs()
            .stream()
            .filter(tab -> tab.getUserData() == contact)
            .map(tab -> (ChatTab) tab)
            .findFirst();

        if (optionalTab.isPresent()) {
            paneChatContainer.getSelectionModel().select(optionalTab.get());
        } else {
            paneChatContainer.getTabs().add(makeNewTab(contact));
        }
    }

    private final MapChangeListener<? super String, ? super ChatContact> chatClientListener = change -> {
        if (change.wasAdded()) {
            lvContactList.getItems().addAll(change.getValueAdded());
        }

        if (change.wasRemoved()) {
            lvContactList.getItems().removeAll(change.getValueRemoved());
        }
    };

    private final EventHandler<? super MouseEvent> listContactsClick = event -> {
        final int clickCount = event.getClickCount();
        if (clickCount != 2) {
            return;
        }

        final ChatContact contact = lvContactList.getSelectionModel().getSelectedItem();
        if (contact == null) {
            return;
        }

        showConversation(contact);
    };

    private ChangeListener<? super String> messageContentListener = (observable, oldValue, newValue) -> {
        final ChatTab tab = (ChatTab) paneChatContainer.getSelectionModel().getSelectedItem();
        if (tab == null) {
            return;
        }

        final String id = ((ChatContact) tab.getUserData()).getId();
        chatService.notifyTyping(id, !newValue.isEmpty());
    };

    // Při přechodu mezi taby
    private ChangeListener<? super Tab> tabChangeListener = (observable, oldValue, newValue) -> {
        // Pokud opouštím starý tab, tak s klientem si zřejmě již psát nechci, tak mu to řeknu
        if (oldValue != null) {
            final ChatTab oldTab = (ChatTab) oldValue;
            final String id = ((ChatContact) oldTab.getUserData()).getId();
            chatService.notifyTyping(id, false);
        }

        // Přecházím na nový tab
        // Pokud mám rozepsanou nějakou zprávu, zřejmě ji pošlu tomuto klientovi, tak mu to řeknu
        if (newValue != null) {
            if (!txtMessage.getText().isEmpty()) {
                final ChatTab newTab = (ChatTab) newValue;
                final String id = ((ChatContact) newTab.getUserData()).getId();
                chatService.notifyTyping(id, true);
            }
        } else {
            // Zavřel jsem i poslední tab
            txtMessage.clear();
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lvContactList.setCellFactory(param -> new ChatEntryCell());
        lvContactList.setOnMouseClicked(this.listContactsClick);

        btnSend.disableProperty().bind(txtMessage.textProperty().isEmpty());
        txtMessage.disableProperty().bind(paneChatContainer.getSelectionModel().selectedItemProperty().isNull());
        txtMessage.textProperty().addListener(this.messageContentListener);

        chatService.getClients().addListener(this.chatClientListener);
        paneChatContainer.getSelectionModel().selectedItemProperty().addListener(this.tabChangeListener);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose(ActionEvent actionEvent) {

    }

    @FXML
    private void handleSendMessage(ActionEvent actionEvent) {
        final ChatTab tab = (ChatTab) paneChatContainer.getSelectionModel().getSelectedItem();
        if (tab == null) {
            return;
        }

        final String id = ((ChatContact) tab.getUserData()).getId();
        final String message = txtMessage.getText();
        chatService.sendMessage(id, message);
        txtMessage.clear();
        txtMessage.requestFocus();
    }
}
