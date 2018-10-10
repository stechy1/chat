package cz.stechy.chat.controller.chat;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ChatController implements Initializable {

    @FXML
    private VBox boxMessageContainer;
    @FXML
    private TextField txtMessage;
    @FXML
    private Button btnSend;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void handleSendMessage(ActionEvent actionEvent) {

    }
}
