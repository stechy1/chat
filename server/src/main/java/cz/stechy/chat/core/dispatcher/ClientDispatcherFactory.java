package cz.stechy.chat.core.dispatcher;

import com.google.inject.Singleton;

@Singleton
public class ClientDispatcherFactory implements IClientDispatcherFactory {

    @Override
    public IClientDispatcher getClientDispatcher(int waitingQueueSize) {
        return new ClientDispatcher(waitingQueueSize);
    }
}