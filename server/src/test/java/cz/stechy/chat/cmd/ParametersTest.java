package cz.stechy.chat.cmd;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ParametersTest {

    private static final String[] PARAMETERS = {
        "-port=6298", "-clients=5", "-max_waiting_queue=5", "name=test"
    };

    private IParameterProvider parameterProvider;

    @Before
    public void setUp() throws Exception {
        parameterProvider = new CmdParser(PARAMETERS);
    }

    @Test
    public void getStringTest() {
        final String key = "name";
        final String name = "test";
        assertEquals("Chyba, hodnota názvu serveru se neshoduje.", name, parameterProvider.getString(key));
    }

    @Test
    public void getStringNegativeTest() {
        final String key = "unknown";
        final String value = CmdParser.DEFAULT_STRING;
        assertEquals("Chyba, je špatně definovaná výchozí hodnota.", value, parameterProvider.getString(key));
    }

    @Test
    public void getIntegerTest() {
        final String key = "port";
        final int value = 6298;
        assertEquals("Chyba, hodnota portu se neshoduje.", value, parameterProvider.getInteger(key));
    }

    @Test
    public void getIntegerNegativeTest() {
        final String key = "unknown";
        final int value = CmdParser.DEFAULT_INTEGER;
        assertEquals("Chyba, je špatně definovaná výchozí hodnota.", value, parameterProvider.getInteger(key));
    }
}