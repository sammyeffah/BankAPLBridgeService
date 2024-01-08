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

public class BankEcardHolderAccountServiceHandler implements HttpHandler {

    private static final Logger logger = LogManager.getLogger(BankEcardHolderAccountServiceHandler.class);

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

                String card = ExchangeUtils.getQueryParam(exchange, "card");
                String phone = ExchangeUtils.getQueryParam(exchange, "phone");
                String control = ExchangeUtils.getQueryParam(exchange, "control");

//                 accept either card_num or phone_number
                if ((card != null || phone != null) && control == null) {
                    LOG_PREFIX = card + "***" + phone + " ";

                    System.out.println("PARAM FROM E_CARDHOLDER GET ACCOUNT>>> " + card + "::" + phone);
                    logger.info("PARAM FROM E_CARDHOLDER GET ACCOUNT>>> " + card + "::" + phone);
                    String resp;
//                  MySQL
                  resp = new GIPProcessor().getAccountFromEcard(card, phone);
//                  MySQL VB
//                    resp = new GIPProcessor().getAccountFromEcardForVB(card, phone);
//                  Sybase 
//                  resp = new GIPProcessor().getAccountFromEcardSybase(card, phone);
                    logger.info("RESPONSE FROM E_CARDHOLDER GET ACCOUNT>>> " + resp);
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
                        String lookupResp = (!"".equals(obj.optString("lookup"))) ? obj.optString("lookup") : "Card account not found";
                        String lookupMsg = (!"".equals(obj.optString("message"))) ? obj.optString("lookup") : "Card account not found";

                        ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "FAILED");
                        ExchangeUtils.sendResponse(exchange, "{\n"
                                + "\"responseMsg\":\"" + lookupMsg + "\",\n"
                                + "\"lookup\":\"" + lookupResp + "\",\n"
                                + "\"TAT\":\"" + obj.optLong("tat") + "\",\n"
                                + "\"resultCode\":\"06\"    \n"
                                + "}", 200);

                    }

                }else if (card != null && control != null) {
                    LOG_PREFIX = card + "***" + phone + " ";

                    System.out.println("PARAM FROM E_CARDHOLDER GET ACCOUNT>>> " + card + "::" + control);
                    logger.info("PARAM FROM E_CARDHOLDER GET ACCOUNT>>> " + card + "::" + control);
                    String resp;
//                  MySQL
//                  resp = new GIPProcessor().getAccountFromEcard(card, phone);
//                  MySQL VB
//                    resp = new GIPProcessor().getAccountFromEcardForVB(card, phone);
//                  Sybase 
                  resp = new GIPProcessor().updateControlIdInEcardSybase(card, control);
                    logger.info("RESPONSE FROM E_CARDHOLDER GET ACCOUNT>>> " + resp);
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
                        String lookupResp = (!"".equals(obj.optString("lookup"))) ? obj.optString("lookup") : "Card account not found";
                        String lookupMsg = (!"".equals(obj.optString("message"))) ? obj.optString("lookup") : "Card account not found";

                        ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "FAILED");
                        ExchangeUtils.sendResponse(exchange, "{\n"
                                + "\"responseMsg\":\"" + lookupMsg + "\",\n"
                                + "\"lookup\":\"" + lookupResp + "\",\n"
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
