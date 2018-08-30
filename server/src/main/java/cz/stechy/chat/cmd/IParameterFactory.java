package cz.stechy.chat.cmd;

public interface IParameterFactory {

    /**
     * Vrátí již existující instanci třídy {@link IParameterProvider}
     * Je potřeba alespoň jednou zavolat nejdříve metodu {@link IParameterFactory#getParameters(String[])}
     *
     * @return {@link IParameterProvider}
     */
    IParameterProvider getParameters();

    /**
     * Vytvoří instanci třídy {@link IParameterProvider}
     *
     * @param args Argumenty z příkazové řádky
     * @return {@link IParameterProvider}
     */
    IParameterProvider getParameters(String[] args);
}