package cz.stechy.chat;

import cz.stechy.chat.net.message.IMessage;
import cz.stechy.chat.net.message.TextMessage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SimpleClient {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 15378);
        Thread.sleep(1000);
        ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
        writer.writeObject(new TextMessage("Hello from client."));
        writer.flush();
        ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
        System.out.println(((IMessage) reader.readObject()).getData().toString());
        socket.close();
    }

}
