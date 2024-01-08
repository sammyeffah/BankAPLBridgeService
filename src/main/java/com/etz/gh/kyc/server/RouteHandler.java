package com.etz.gh.kyc.server;

import io.undertow.server.RoutingHandler;
import io.undertow.util.Methods;

/**
 *
 * @author seth.sebeh
 */
public class RouteHandler {

    public static final RoutingHandler ROUTES() {
//        
        RoutingHandler routes = new RoutingHandler();
        routes.add(Methods.GET, "/apl", new BankAPLServiceHandler());
        routes.add(Methods.GET, "/bankservice", new BankAccLookupHandler());
        routes.add(Methods.GET, "/total/balance", new SumBalanceInOrgServiceHandler());
        routes.add(Methods.GET, "/account/ecard/lookup", new BankEcardHolderAccountServiceHandler());
        routes.add(Methods.GET, "/hostresponse/log", new GetField37ServiceHandler());
        routes.setFallbackHandler(new FallbackHandler());
        return routes;
    }

}
