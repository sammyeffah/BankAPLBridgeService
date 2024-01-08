/* 
 * 17042020 05:00
 * GIPKYC Service init class. class that starts and manages the lifecycle of app
 * 
 */
package com.etz.gh.kyc.genesis;

import com.etz.gh.kyc.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Seth Sebeh-Kusi
 */
public class App {

    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        new App().start();
    }

    public void start() {
        long start = System.currentTimeMillis();
        logger.info("*** APLBankService 1.0 Starting...");
        startServer();
        logger.info("*** APLBankService 1.0 started in {} ms. Ready to receive requests", (System.currentTimeMillis() - start));
    }

    private void startServer() {
        Server.start();
    }

}
