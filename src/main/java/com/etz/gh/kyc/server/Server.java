/*
 * 17042020 05:00
 */
package com.etz.gh.kyc.server;

import com.etz.gh.kyc.util.Config;
import io.undertow.Undertow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Seth Sebeh-Kusi
 */
public class Server {

    private static final Logger logger = LogManager.getLogger(Server.class);
    private static Undertow server;

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        try {
            long start = System.currentTimeMillis();
            logger.info("Starting server...");
            String host = Config.HOST;
            int port = (Config.PORT);
            Undertow.Builder builder = Undertow.builder()
                    .addHttpListener(port, host)
                    .setHandler(RouteHandler.ROUTES())
                    .setIoThreads(50)
                    .setWorkerThreads(200);
            server = builder.build();
            server.start();
            long tat = System.currentTimeMillis() - start;
            logger.info("Server started in {}ms; listening on {}:{}", tat, host, port);
            System.out.println("Server started in " + tat + " ms");
        } catch (Exception e) {
            logger.error("Error occured starting server", e);
            logger.error("Shutting down...");
        }
    }

    public static void stop() {
    }

}
