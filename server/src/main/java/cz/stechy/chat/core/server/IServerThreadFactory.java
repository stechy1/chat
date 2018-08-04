package cz.stechy.chat.core.server;

import cz.stechy.chat.cmd.IParameterProvider;
import java.io.IOException;

/**
 * Rozhraní továrny pro vlákno serveru
 */
public interface IServerThreadFactory {

    /**
     * Vytvoří nové vlákno serveru
     *
     * @param parameters {@link IParameterProvider} Poskytovatel parametrů
     * @return {@link IServerThread}
     */
    IServerThread getServerThread(IParameterProvider parameters) throws IOException;

}