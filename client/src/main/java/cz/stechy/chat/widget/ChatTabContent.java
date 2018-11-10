package cz.stechy.chat.widget;

import cz.stechy.chat.ThreadPool;
import java.util.concurrent.CompletableFuture;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ChatTabContent {

    @FXML
    private Circle circle;
    @FXML
    private Label lblFrom;
    @FXML
    private TextArea areaMessage;
    @FXML
    private ImageView imgLoading;

    private void enableArea() {
        imgLoading.setVisible(false);
        areaMessage.setDisable(false);
    }

    void setColor(Color color) {
        circle.setFill(color);
    }

    void setContactName(String name) {
        lblFrom.setText(name);
    }

    void setMessage(String message) {
        areaMessage.setText(message);
    }

    /**
     * Pokusí se změnit velikost oblasti se zprávou tak, aby se vešla celá do okna
     */
    void askForResizeTextArea() {
        if (areaMessage.getLength() <= 58) {
            enableArea();
            return;
        }
        // Toto je trošku čuňárna
        CompletableFuture.runAsync(() -> {
            // Nejdříve chvíli počkám v jiném vlákně
            // Čekám, protože musím mít jistotu, že se vložený text již nastavil a vypočítaly
            // se veškeré vlastnosti
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) { }
        }, ThreadPool.COMMON_EXECUTOR)
            .thenAcceptAsync(aVoid -> {
                // Nyní můžu najít jiř přiřazený text
                final Node text = areaMessage.lookup(".text");
                if (text == null) {
                    return;
                }
                // A nabindovat správnou výšku textArea
                areaMessage.prefHeightProperty().bind(Bindings.createDoubleBinding(
                    () -> text.getBoundsInLocal().getHeight(), text.boundsInLocalProperty()).add(20));
                enableArea();
            }, ThreadPool.JAVAFX_EXECUTOR);
    }
}
