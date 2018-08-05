package cz.stechy.chat.plugins.hello;

import cz.stechy.chat.core.event.IEventBus;
import cz.stechy.chat.plugins.IPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloPlugin implements IPlugin {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloPlugin.class);

    public static final String PLUGIN_NAME = "hello";

    @Override
    public String getName() {
        return PLUGIN_NAME;
    }

    @Override
    public void init() {
        LOGGER.info("Inicializace pluginu: {}", getName());
    }

    @Override
    public void registerMessageHandlers(IEventBus eventBus) { }
}
