package cz.stechy.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws Exception {
        LOGGER.trace("Trace log");
        LOGGER.debug("Debug log");
        LOGGER.info("Info log");
        LOGGER.warn("Warn log");
        LOGGER.error("Error log");

    }

}
