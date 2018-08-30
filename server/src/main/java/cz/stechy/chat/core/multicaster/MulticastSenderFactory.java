package cz.stechy.chat.core.multicaster;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import cz.stechy.chat.cmd.IParameterFactory;
import cz.stechy.chat.core.ServerInfoProvider;

@Singleton
public class MulticastSenderFactory implements IMulticastSenderFactory {

    private final IParameterFactory parameterFactory;

    @Inject
    public MulticastSenderFactory(IParameterFactory parameterFactory) {
        this.parameterFactory = parameterFactory;
    }

    @Override
    public IMulticastSender getMulticastSender(ServerInfoProvider serverInfoProvider) {
        return new MulticastSender(parameterFactory, serverInfoProvider);
    }
}
