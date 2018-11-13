package cz.stechy.chat.widget;

import cz.stechy.chat.model.ChatContact;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;

public class ChatEntryCell extends ListCell<ChatContact> {

    private final Circle circle = new Circle();
    private final Label lblName = new Label();
    private final Region spacer = new Region();
    private final Label lblUnreadedMessages = new Label();
    private final HBox container = new HBox(circle, lblName, spacer, lblUnreadedMessages);

    {
        circle.setRadius(15);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setSpacing(8);
    }

    private void bind(ChatContact item) {
        System.out.println("Binding contact: " + item);
        circle.fillProperty().bind(item.contactColorProperty());
        lblName.textProperty().bind(item.nameProperty());
        lblUnreadedMessages.textProperty().bind(item.unreadedMessagesProperty().asString());
        lblUnreadedMessages.visibleProperty().bind(item.unreadedMessagesProperty().greaterThan(0));
    }

    private void unbind() {
        circle.fillProperty().unbind();
        lblName.textProperty().unbind();
        lblUnreadedMessages.textProperty().unbind();
    }

    @Override
    protected void updateItem(ChatContact item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        if (empty) {
            unbind();
            setGraphic(null);
        } else {
            bind(item);

            setGraphic(container);
        }
    }
}