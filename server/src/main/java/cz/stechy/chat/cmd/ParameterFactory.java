package cz.stechy.chat.cmd;

import com.google.inject.Singleton;

@Singleton
public class ParameterFactory implements IParameterFactory {

    private IParameterProvider parameterProvider;

    @Override
    public IParameterProvider getParameters() {
        if (parameterProvider == null) {
            throw new IllegalStateException("Je potřeba nejdříve inicializovat parametry.");
        }
        return parameterProvider;
    }

    @Override
    public IParameterProvider getParameters(String[] args) {
        if (parameterProvider == null) {
            parameterProvider = new CmdParser(args);
        }

        return parameterProvider;
    }
}