package cz.stechy.chat.plugins.hello.service;

import cz.stechy.chat.core.Client;
import cz.stechy.chat.net.message.HelloMessage;
import cz.stechy.chat.net.message.IMessage;

class HelloService implements IHelloService {

    @Override
    public IMessage sayHello() {
        return new HelloMessage("Hello from server");
    }

    @Override
    public void onClientConnected(Client client) {
        System.out.println("Client: " + client.toString() + " connected.");
    }

    @Override
    public void onClientDisconnected(Client client) {
        System.out.println("Client: " + client.toString() + " disconnected.");
    }
}
