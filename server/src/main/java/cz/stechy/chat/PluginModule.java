package cz.stechy.chat;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import cz.stechy.chat.plugins.IPlugin;
import cz.stechy.chat.plugins.Plugin;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class PluginModule extends AbstractModule {

    // region Constants

    public static final String PLUGIN_IDENTIFIER = "Plugin-Class";

    // endregion

    // region Variables

    private final String pluginsFolderPath;

    // endregion

    // region Constructors

    /**
     * Vytvoří novou instanci třídy {@link PluginModule}
     *
     * @param pluginsFolderPath Cesta ke složce s pluginy
     */
    PluginModule(String pluginsFolderPath) {
        this.pluginsFolderPath = pluginsFolderPath;
    }

    // endregion

    // region Private methods

    /**
     * Filter pouze pro jar soubory
     *
     * @param dir Složka, ve které se soubor nachází
     * @param name Název testovaného souboru
     * @return True, pokud se jedná o jar soubor, jinak False
     */
    private static boolean pluginFilter(File dir, String name) {
        return name.contains(".jar");
    }

    /**
     * Načte plugin
     *
     * @param pluginFile Jar soubor s jedním pluginem
     * @return {@link Optional<IPlugin>} Pluigin, nebo prázdný optional
     */
    private Optional<IPlugin> loadPlugin(File pluginFile) {
        try {
            final ClassLoader loader = URLClassLoader.newInstance(new URL[]{pluginFile.toURI().toURL()});
            final JarInputStream jis = new JarInputStream(new FileInputStream(pluginFile));
            final Manifest mf = jis.getManifest();
            final Attributes attributes = mf.getMainAttributes();
            final String pluginClassName = attributes.getValue(PLUGIN_IDENTIFIER);
            final Class<?> clazz = Class.forName(pluginClassName, true, loader);
            final IPlugin plugin = clazz.asSubclass(IPlugin.class).newInstance();
            System.out.println("Přidávám plugin: " + plugin.getName());
            return Optional.of(plugin);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // endregion

    @Override
    protected void configure() {
        MapBinder<String, IPlugin> pluginBinder = MapBinder
            .newMapBinder(binder(), String.class, IPlugin.class);
        for (Plugin plugin : Plugin.values()) {
            pluginBinder.addBinding(plugin.name()).to(plugin.clazz).asEagerSingleton();
        }

        final File pluginsFolder = new File(pluginsFolderPath);
        if (!pluginsFolder.exists() || !pluginsFolder.isDirectory()) {
            return;
        }

        for (File pluginFile : Objects.requireNonNull(pluginsFolder.listFiles(PluginModule::pluginFilter))) {
            loadPlugin(pluginFile).ifPresent(plugin ->
                pluginBinder.addBinding(plugin.getName()).to(plugin.getClass()).asEagerSingleton());
        }

    }
}
