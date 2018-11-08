package cz.stechy.chat.plugins;

import cz.stechy.chat.plugins.auth.AuthPlugin;
import cz.stechy.chat.plugins.chat.ChatPlugin;
import cz.stechy.chat.plugins.hello.HelloPlugin;

public enum  Plugin {
    HELLO(HelloPlugin.class),
    AUTH(AuthPlugin.class),
    CHAT(ChatPlugin.class);

    public final Class<? extends IPlugin> clazz;
    Plugin(Class<? extends IPlugin> clazz) {
        this.clazz = clazz;
    }

}
