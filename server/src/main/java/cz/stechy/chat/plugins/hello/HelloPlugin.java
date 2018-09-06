package cz.stechy.chat.plugins.hello;

import cz.stechy.chat.core.event.IEventBus;
import cz.stechy.chat.plugins.IPlugin;

public class HelloPlugin implements IPlugin {

    public static final String PLUGIN_NAME = "hello";

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public void init() {
        System.out.println("Inicializace pluginu: " + getName());
    }

    @Override
    public void registerMessageHandlers(IEventBus eventBus) { }
}
