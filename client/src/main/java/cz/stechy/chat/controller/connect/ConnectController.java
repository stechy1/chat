package cz.stechy.chat.controller.connect;

import cz.stechy.chat.controller.OnCloseListener;
import cz.stechy.chat.model.ServerEntry;
import cz.stechy.chat.service.LocalServerService;
import cz.stechy.chat.widget.ServerEntryCell;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ConnectController implements Initializable, OnCloseListener {

    @FXML
    private Button btnConnect;
    @FXML
    private ListView<ServerEntry> lvServers;
    @FXML
    private Button btnDisconnect;
    @FXML
    private Label lblConnectedTo;
    @FXML
    private TextField txtServer;
    @FXML
    private TextField txtUsername;

    private final LocalServerService serverService = new LocalServerService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lvServers.setCellFactory(param -> new ServerEntryCell());
        serverService.getServerMap().addListener(serverMapListener);
    }

    @Override
    public void onClose() {
        serverService.stop();
    }

    @FXML
    private void handleConnect(ActionEvent actionEvent) {
        // TODO připojit se k serveru
    }

    @FXML
    private void handleDisconnect(ActionEvent actionEvent) {
        // TODO odpojit se od serveru
    }

    private MapChangeListener<? super UUID, ? super ServerEntry> serverMapListener = change -> {
        if (change.wasAdded()) {
            lvServers.getItems().addAll(change.getValueAdded());
        }

        if (change.wasRemoved()) {
            lvServers.getItems().removeAll(change.getValueRemoved());
        }
    };
}
