package org.blockserver;

import lombok.Getter;
import lombok.Setter;
import org.blockserver.logging.LoggingModule;

/**
 * Represents the core server implementation.
 *
 * @author BlockServer Team
 */
public class Server implements Runnable {

    @Getter @Setter private boolean running = false;

    //Modules
    @Getter private LoggingModule logger;

    public Server() {
        loadModules();
    }

    private void loadModules() {
        logger = new LoggingModule(this);
    }

    @Override
    public void run() {
        logger.info("Testing module system.");
    }
}
