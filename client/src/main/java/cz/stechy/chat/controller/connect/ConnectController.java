package cz.stechy.chat.controller.connect;

import cz.stechy.chat.controller.OnCloseListener;
import cz.stechy.chat.model.ServerEntry;
import cz.stechy.chat.net.ConnectionState;
import cz.stechy.chat.service.IClientCommunicationService;
import cz.stechy.chat.service.LocalServerService;
import cz.stechy.chat.widget.ServerEntryCell;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
    private IClientCommunicationService communicator;

    private void connect() {
        final String hostPort = txtServer.textProperty().get();
        final String host = hostPort.substring(0, hostPort.indexOf(":"));
        final String portRaw = hostPort.substring(hostPort.indexOf(":") + 1);
        int port;
        try {
            port = Integer.parseInt(portRaw);
        } catch (Exception ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Chyba");
            alert.setContentText("Port serveru se nezdařilo naparsovat.");
            alert.showAndWait();
            return;
        }

        this.communicator.connect(host, port)
            .exceptionally(throwable -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("Chyba");
                alert.setContentText("Připojení k serveru se nezdařilo.");
                alert.showAndWait();

                throw new RuntimeException(throwable);
            })
            .thenAccept(ignored -> {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setHeaderText("Informace");
                alert.setContentText("Spojení bylo úspěšně navázáno.");
                alert.showAndWait();
            });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lvServers.setCellFactory(param -> new ServerEntryCell());
        serverService.getServerMap().addListener(serverMapListener);
        lvServers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                txtServer.setText(null);
                return;
            }

            txtServer.textProperty().set(String.format("%s:%d", newValue.getServerAddress().getHostAddress(), newValue.getPort()));
        });
    }

    @Override
    public void onClose() {
        serverService.stop();
    }

    @FXML
    private void handleConnect(ActionEvent actionEvent) {
        connect();
    }

    @FXML
    private void handleDisconnect(ActionEvent actionEvent) {
        communicator.disconnect();
    }

    private MapChangeListener<? super UUID, ? super ServerEntry> serverMapListener = change -> {
        if (change.wasAdded()) {
            lvServers.getItems().addAll(change.getValueAdded());
        }

        if (change.wasRemoved()) {
            lvServers.getItems().removeAll(change.getValueRemoved());
        }
    };

    public void setCommunicator(IClientCommunicationService communicator) {
        this.communicator = communicator;
        final BooleanBinding connected = Bindings.createBooleanBinding(() -> this.communicator.getConnectionState() == ConnectionState.CONNECTED, this.communicator.connectionStateProperty());
        btnConnect.disableProperty().bind(connected.or(txtServer.textProperty().isEmpty()));
        btnDisconnect.disableProperty().bind(connected.not());
        lblConnectedTo.textProperty().bind(this.communicator.connectedServerNameProperty());
    }
}
