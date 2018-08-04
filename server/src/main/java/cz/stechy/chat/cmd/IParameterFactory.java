package cz.stechy.chat.cmd;

public interface IParameterFactory {

    /**
     * Vytvoří instanci třídy {@link IParameterProvider}
     *
     * @param args Argumenty z příkazové řádky
     * @return {@link IParameterProvider}
     */
    IParameterProvider getParameters(String[] args);
}
