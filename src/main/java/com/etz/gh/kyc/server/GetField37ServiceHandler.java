package com.etz.gh.kyc.server;

import com.etz.gh.kyc.processor.GIPProcessor;
import com.etz.gh.kyc.util.Config;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.util.Headers;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class GetField37ServiceHandler implements HttpHandler {

    private static final Logger logger = LogManager.getLogger(GetField37ServiceHandler.class);

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }
        long start = System.currentTimeMillis();
        ExchangeUtils.allowAllOrigin(exchange);
        ExchangeUtils.setHeader(exchange, Headers.CONTENT_TYPE, Config.CONTENT_TYPE_TEXT_PLAIN);
        ExchangeUtils.setHeader(exchange, Headers.CONTENT_TYPE, Config.CONTENT_TYPE_APPLICATION_JSON);
        String LOG_PREFIX = "";
        try {

            if (isIPAllowed(exchange)) {
                String action = ExchangeUtils.getQueryParam(exchange, "action");
                String reference = ExchangeUtils.getQueryParam(exchange, "reference");

                if (action != null && reference != null && action.equals("dofetchrrn")) {
                    LOG_PREFIX = action + "***" + reference + " ";
                    System.out.println("PARAM FROM GET FIELD 37>>> " + action + "::" + reference);
                    logger.info("PARAM FROM GET FIELD 37>>> " + action + "::" + reference);
                    String resp;

//                    for MySQL
                    resp = new GIPProcessor().getField37FromEHostRespMySQL(reference);
                    System.out.println("RESPONSE FROM  FIELD 37>>> " + resp);
                    logger.info("RESPONSE FROM FIELD 37>>> " + resp);
                    JSONObject obj = new JSONObject(resp);
                    if (obj.optString("responseCode").equals("00")) {

                        ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "SUCCESSFUL");
                        exchange.getResponseSender().send("{\n"
                                + "\"responseMsg\":\"" + obj.optString("message") + "\",\n"
                                + "\"lookup\":\"" + obj.optString("lookup") + "\",\n"
                                + "\"TAT\":\"" + obj.optLong("tat") + "\",\n"
                                + "\"resultCode\":\"00\"    \n"
                                + "}"
                        );

                    } else {

                        ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "FAILED");
                        ExchangeUtils.sendResponse(exchange, "{\n"
                                + "\"responseMsg\":\"" + obj.optString("message") + "\",\n"
                                + "\"lookup\":\"" + obj.optString("lookup") + "\",\n"
                                + "\"TAT\":\"" + obj.optLong("tat") + "\",\n"
                                + "\"resultCode\":\"06\"    \n"
                                + "}", 200);

                    }

                } else {
                    logger.warn("Invalid request. Uknown parameters");
                    ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "INVALID PARAMETERS");
                    ExchangeUtils.sendResponse(exchange, "INVALID PARAMETERS", 400);
                }
            } else {
                ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "IP NOT ALLOWED");
                ExchangeUtils.sendResponse(exchange, "IP_NOT_ALLOWED", 401);
            }

        } catch (Exception e) {
            ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "GENERAL EXCEPTION");
            logger.error("Error processing request", e);
            exchange.dispatch(ResponseCodeHandler.HANDLE_500);
        }
        logger.info("<<< {} end of request , TAT {} ms", LOG_PREFIX, (System.currentTimeMillis() - start));
    }

    private static boolean isIPAllowed(HttpServerExchange exchange) {
        String sourceIP = ExchangeUtils.getRemoteIP(exchange);
        logger.info(">>> new request received: {} from source IP: {}", exchange.getQueryString(), sourceIP);
        String restrictIP = Config.getProperty("RESTRICT_IP");
        if (restrictIP != null & restrictIP.equals("1")) {
            String[] allowedIPs = Config.ALLOWED_IP_ADDRESSES.split("#");
            if (Arrays.asList(allowedIPs).contains(sourceIP)) {
                logger.info("REQUESTOR IP VALID. ALOW TO PROCEED");
                return true;
            } else {
                logger.info("REQUESTOR IP NOT ALLOWED");
                return false;
            }
        }
        logger.info("IP WHITELISTING DISABLED.");
        return true;
    }
}
