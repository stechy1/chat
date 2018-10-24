package cz.stechy.chat.widget;

import cz.stechy.chat.model.ServerEntry;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

/**
 * Třída obsahující grafickou reprezentaci jednoho záznamu s informacemi o serveru
 */
public class ServerEntryCell extends ListCell<ServerEntry> {

    // region Constants

    private static final String FXML_PATH = "/fxml/connect/server_entry.fxml";
    private static final String ADDRESS_PORT_FORMAT = "%s:%d";

    // endregion

    // region Variables

    // region FXML

    @FXML
    private Label lblName;

    @FXML
    private Label lblClients;

    @FXML
    private Label lblAddress;

    // endregion

    private Parent container;

    // endregion

    public ServerEntryCell() {
        final FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        loader.setController(this);
        try {
            container = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(ServerEntry item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
            lblName.textProperty().unbind();
            lblClients.textProperty().unbind();
        } else {
            lblName.textProperty().bind(item.serverNameProperty());
            lblClients.textProperty().bind(item.clientsProperty());
            lblAddress.textProperty().set(String.format(ADDRESS_PORT_FORMAT, item.getServerAddress().getHostAddress(), item.getPort()));
            setGraphic(container);
        }
    }
}
