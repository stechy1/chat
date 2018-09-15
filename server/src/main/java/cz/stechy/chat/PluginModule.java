package cz.stechy.chat;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import cz.stechy.chat.plugins.IPlugin;
import cz.stechy.chat.plugins.Plugin;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class PluginModule extends AbstractModule {

    // region Constants

    private static final FilenameFilter PLUGIN_FILTER = (file, name) -> name.contains(".jar");

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
    /**
     * Pokusí se načíst všechny pluginy ve složce
     *
     * @param pluginBinder {@link MapBinder}
     */
    private void loadPlugins(MapBinder<String, IPlugin> pluginBinder) {
        final File pluginsFolder = new File(pluginsFolderPath);
        if (!pluginsFolder.exists() || !pluginsFolder.isDirectory()) {
            return;
        }

        final File[] plugins = pluginsFolder.listFiles(PLUGIN_FILTER);
        if (plugins == null) {
            return;
        }

        Arrays.stream(plugins)
            .map(this::loadPlugin)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(plugin -> pluginBinder.addBinding(plugin.getName()).to(plugin.getClass()).asEagerSingleton());
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

        loadPlugins(pluginBinder);
    }
}
