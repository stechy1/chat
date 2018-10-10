package cz.stechy.chat.controller.connect;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class ConnectController implements Initializable {

    @FXML
    private Button btnConnect;
    @FXML
    private ListView lvServers;
    @FXML
    private Button btnDisconnect;
    @FXML
    private Label lblConnectedTo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    @FXML
    private void handleConnect(ActionEvent actionEvent) {

    }

    @FXML
    private void handleDisconnect(ActionEvent actionEvent) {

    }
}
