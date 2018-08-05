package cz.stechy.chat.plugins;

import cz.stechy.chat.core.event.IEventBus;
import java.util.Map;

/**
 * Rozhraní definující plugin
 */
public interface IPlugin {

    /**
     * Vrátí název pluginu
     *
     * @return Název pluginu
     */
    String getName();

    /**
     * Inicializace pluginu
     * Zde by se měl plugin inicializovat, ne v konstruktoru
     */
    void init();

    /**
     * Zde se musí zaregistrovat posluchače pro příchozí zprávy od klienta
     *
     * @param eventBus {@link IEventBus}
     */
    void registerMessageHandlers(IEventBus eventBus);

    /**
     * Nastavení komunikace mezi pluginy
     *
     * @param otherPlugins Kolekce ostatních pluginů
     */
    default void setupDependencies(Map<String, IPlugin> otherPlugins) {}
}
