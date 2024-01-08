/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author samuel.onwona
 */
public class SumBalanceInOrgServiceHandler implements HttpHandler {

    private static final Logger logger = LogManager.getLogger(SumBalanceInOrgServiceHandler.class);

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

                String account = ExchangeUtils.getQueryParam(exchange, "account");
                String email = ExchangeUtils.getQueryParam(exchange, "email");
                if (account != null) {
                    LOG_PREFIX = account + "***" + " ";

                    if (email != null) {
                        System.out.println("PARAMS FROM E_CARDHOLDER GET ACCOUNT>>> " + account + "::");
                        logger.info("PARAMS FROM E_CARDHOLDER GET ACCOUNT>>> " + account + "::");
                        String resp;

//                  MySQL VB
                        if (account.isEmpty()) {
                            long start1 = System.currentTimeMillis();
                            resp = new GIPProcessor().getSumOfAccountFromEcardForVB("", email);
                            long end = System.currentTimeMillis() - start1;
                            logger.info("RESPONSE FROM E_CARDHOLDER GET ACCOUNT>>> " + resp);
                            JSONObject obj = new JSONObject(resp);
                            if (obj.optString("responseCode").equals("00")) {

                                ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "SUCCESSFUL");
                                exchange.getResponseSender().send("{\n"
                                        + "\"responseMsg\":\"" + obj.optString("message") + "\",\n"
                                        + "\"lookup\":\"" + obj.optString("lookup") + "\",\n"
                                        + "\"TAT\":\"" + end + "\",\n"
                                        + "\"resultCode\":\"00\"    \n"
                                        + "}"
                                );

                            } else {
                                String lookupResp = (!"".equals(obj.optString("lookup"))) ? obj.optString("lookup") : "Couldn't get total balance";
                                String lookupMsg = (!"".equals(obj.optString("message"))) ? obj.optString("lookup") : "Couldn't get total balance";

                                ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "FAILED");
                                ExchangeUtils.sendResponse(exchange, "{\n"
                                        + "\"responseMsg\":\"" + lookupMsg + "\",\n"
                                        + "\"lookup\":\"" + lookupResp + "\",\n"
                                        + "\"TAT\":\"" + obj.optLong("tat") + "\",\n"
                                        + "\"resultCode\":\"06\"    \n"
                                        + "}", 200);

                            }
                        } else {
                            long start1 = System.currentTimeMillis();
                            resp = new GIPProcessor().getSumOfAccountFromEcardForVB(account, email);
                            long end = System.currentTimeMillis() - start1;
                            logger.info("RESPONSE FROM E_CARDHOLDER GET ACCOUNT>>> " + resp);
                            JSONObject obj = new JSONObject(resp);
                            if (obj.optString("responseCode").equals("00")) {

                                ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "SUCCESSFUL");
                                exchange.getResponseSender().send("{\n"
                                        + "\"responseMsg\":\"" + obj.optString("message") + "\",\n"
                                        + "\"lookup\":\"" + obj.optString("lookup") + "\",\n"
                                        + "\"TAT\":\"" + end + "\",\n"
                                        + "\"resultCode\":\"00\"    \n"
                                        + "}"
                                );

                            } else {
                                String lookupResp = (!"".equals(obj.optString("lookup"))) ? obj.optString("lookup") : "Couldn't get total balance";
                                String lookupMsg = (!"".equals(obj.optString("message"))) ? obj.optString("lookup") : "Couldn't get total balance";

                                ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "FAILED");
                                ExchangeUtils.sendResponse(exchange, "{\n"
                                        + "\"responseMsg\":\"" + lookupMsg + "\",\n"
                                        + "\"lookup\":\"" + lookupResp + "\",\n"
                                        + "\"TAT\":\"" + obj.optLong("tat") + "\",\n"
                                        + "\"resultCode\":\"06\"    \n"
                                        + "}", 200);

                            }
                        }

                    } else {
                        System.out.println("PARAMS FROM E_CARDHOLDER GET ACCOUNT>>> " + account + "::");
                        logger.info("PARAMS FROM E_CARDHOLDER GET ACCOUNT>>> " + account + "::");
                        String resp;

//                  MySQL VB
                        if (account.isEmpty()) {

                            ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "FAILED");
                            ExchangeUtils.sendResponse(exchange, "{\n"
                                    + "\"responseMsg\":\"" + "Couln't get data! List empty" + "\",\n"
                                    + "\"lookup\":\"" + 0.0 + "\",\n"
                                    + "\"TAT\":\"" + 0.0 + "\",\n"
                                    + "\"resultCode\":\"06\"    \n"
                                    + "}", 200);
                        } else {
                            long start1 = System.currentTimeMillis();
                            resp = new GIPProcessor().getSumOfAgentFromEcardForVB(account);
                            long end = System.currentTimeMillis() - start1;
                            logger.info("RESPONSE FROM E_CARDHOLDER GET ACCOUNT>>> " + resp);
                            JSONObject obj = new JSONObject(resp);
                            if (obj.optString("responseCode").equals("00")) {

                                ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "SUCCESSFUL");
                                exchange.getResponseSender().send("{\n"
                                        + "\"responseMsg\":\"" + obj.optString("message") + "\",\n"
                                        + "\"lookup\":\"" + obj.optString("lookup") + "\",\n"
                                        + "\"TAT\":\"" + end + "\",\n"
                                        + "\"resultCode\":\"00\"    \n"
                                        + "}"
                                );

                            } else {
                                String lookupResp = (!"".equals(obj.optString("lookup"))) ? obj.optString("lookup") : "Couldn't get total balance";
                                String lookupMsg = (!"".equals(obj.optString("message"))) ? obj.optString("lookup") : "Couldn't get total balance";

                                ExchangeUtils.setHeader(exchange, "X-DESCRIPTION", "FAILED");
                                ExchangeUtils.sendResponse(exchange, "{\n"
                                        + "\"responseMsg\":\"" + lookupMsg + "\",\n"
                                        + "\"lookup\":\"" + lookupResp + "\",\n"
                                        + "\"TAT\":\"" + obj.optLong("tat") + "\",\n"
                                        + "\"resultCode\":\"06\"    \n"
                                        + "}", 200);

                            }
                        }

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
