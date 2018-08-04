package cz.stechy.chat.plugins.hello.service;

import com.google.inject.ImplementedBy;
import cz.stechy.chat.core.Client;
import cz.stechy.chat.net.message.IMessage;

@ImplementedBy(HelloService.class)
public interface IHelloService {

    IMessage sayHello();

    void onClientConnected(Client client);

    void onClientDisconnected(Client client);

}
