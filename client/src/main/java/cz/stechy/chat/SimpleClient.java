package cz.stechy.chat;

import cz.stechy.chat.net.message.HelloMessage;
import cz.stechy.chat.net.message.IMessage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleClient {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleClient.class);

    public static void main(String[] args) throws Exception{
        LOGGER.info("Spouštím klienta.");
        Socket socket = new Socket("localhost", 15378);
        LOGGER.info("Bylo navázané spojení.");
        Thread.sleep(1000);
        ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
        LOGGER.info("Odesílám zprávu.");
        writer.writeObject(new HelloMessage("Hello from client."));
        writer.flush();
        LOGGER.info("Čtu zprávu.");
        ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
        LOGGER.info(((IMessage) reader.readObject()).getData().toString());
        LOGGER.info("Ukončuji spojení.");
        socket.close();
        LOGGER.info("Spojení bylo ukončeno. Klient končí.");
    }

}
