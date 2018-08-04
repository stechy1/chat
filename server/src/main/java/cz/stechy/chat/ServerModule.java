package cz.stechy.chat;

import com.google.inject.AbstractModule;
import cz.stechy.chat.cmd.IParameterFactory;
import cz.stechy.chat.cmd.ParameterFactory;

public class ServerModule extends AbstractModule {

    @Override
    public void configure() {
        bind(IParameterFactory.class).to(ParameterFactory.class);
    }
}
