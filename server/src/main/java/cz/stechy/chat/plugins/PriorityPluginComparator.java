package cz.stechy.chat.plugins;

import java.util.Comparator;

/**
 * Komparátor pluginů založený na prioritě jednotlivých pluginů
 */
public class PriorityPluginComparator implements Comparator<IPlugin> {

    @Override
    public int compare(IPlugin o1, IPlugin o2) {
        final PluginConfiguration o1Configuration = o1.getClass().getAnnotation(PluginConfiguration.class);
        final PluginConfiguration o2Configuration = o2.getClass().getAnnotation(PluginConfiguration.class);

        if (o1Configuration == null && o2Configuration == null) {
            return 0;
        }

        final int o1Priority = o1Configuration == null ? PluginConfiguration.DEFAULT_PRIORITY : o1Configuration.priority();
        final int o2Priority = o2Configuration == null ? PluginConfiguration.DEFAULT_PRIORITY : o2Configuration.priority();

        return Integer.compare(o1Priority, o2Priority);
    }
}
