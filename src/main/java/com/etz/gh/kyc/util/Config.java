package com.etz.gh.kyc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 *
 * @author seth.sebeh
 */
public class Config {

    private static final Logger logger = LogManager.getLogger(Config.class);
    private static final Properties props = new Properties();
    public static final String HOST;
    public static final int PORT;

    public static final String GIP_URL;
    public static final int DB_MAX_POOL;
    public static final String GIP_IP;
    public static final String ETZ_GIP_BANK_CODE;
    public static final String SOCKET_TIMEOUT;
    public static final String TRUSTSTORE_LOC;
    public static final String TRUSTSTORE_PWD;
    public static final String KEYSTORE_LOC;
    public static final String KEYSTORE_PWD;
    public static final String ALLOWED_IP_ADDRESSES;
    public static final String NOTIFY_URL;
    public static final String NIB_APL_URL;
    public static final String BEST_APL_URL;
    public static final String DBDRIVER;
    public static final String DBURL;
    public static final String DBDRIVER_37;
    public static final String DBURL_37;
    public static final String DBUSER;
    public static final String DBPASS;
    public static final String NIB_AUTH_ENDPOINT_URL;
    public static final String NIB_ACCLOOKUP_ENDPOINT_URL;
    public static final String NIB_USERNAME;
    public static final String NIB_PASSWORD;
    public static final String NIB_GRANTTYPE;
    public static final String NIB_CLIENTID;
    public static final String BP_ACCLOOKUP_ENDPOINT_URL;
    public static final String BP_KEY;
    public static final String BP_SECRET;
    

    public static final String BOA_ACCLOOKUP_ENDPOINT_URL;
    public static final String BOA_USERNAME;
    public static final String BOA_PASSWORD;

    public static final String GCB_ACCLOOKUP_ENDPOINT_URL;
    public static final String GCB_AUTH_ENDPOINT_URL;
    public static final String GCB_GRANTTYPE;
    public static final String GCB_USERNAME;
    public static final String GCB_PASSWORD;
    public static final String GCB_TIMEOUT;
    public static final String GCB_JKSLOC;
    public static final String GCB_JKSPASS;

    public static final String GARURAL_LOOKUP_ENDPOINT;
    public static final String GARURAL_USERNAME;
    public static final String GARURAL_PASSWORD;
    
    public static final String TALENT_DEVKEY;
    public static final String TALENT_ENDPOINT;

    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    public static final String CONTENT_TYPE_TEXT_XML = "text/xml";
    public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE_APPLICATION_XML = "application/xml";

//    public static final HashMap<String, String> SERVICE_MAP;
    static {
        try {
            props.load(new FileReader(new File("cfg" + File.separator + "ini.properties")));
        } catch (IOException ex) {
            logger.error("Error loading configuration parameters. Application start up failed. Check if app configuration file ini.properties exist in cfg folder before restarting the service again.", ex);
        }
        HOST = props.getProperty("HOST");
        PORT = Integer.parseInt(props.getProperty("PORT"));
//        String[] service_props = props.getProperty("SERVICE").split("\\|");
//        SERVICE_MAP = new HashMap<>();
//        for (String service : service_props) {
//            String[] a = service.split("=");
//            SERVICE_MAP.put(a[0].trim().toUpperCase(), a[1].trim());
//        }

        GIP_URL = props.getProperty("GIP_URL");
        ETZ_GIP_BANK_CODE = props.getProperty("ETZ_GIP_BANK_CODE");
        SOCKET_TIMEOUT = props.getProperty("SOCKET_TIMEOUT");
        TRUSTSTORE_LOC = props.getProperty("TRUSTSTORE_LOC");
        TRUSTSTORE_PWD = props.getProperty("TRUSTSTORE_PWD");
        KEYSTORE_LOC = props.getProperty("KEYSTORE_LOC");
        KEYSTORE_PWD = props.getProperty("KEYSTORE_PWD");
        ALLOWED_IP_ADDRESSES = props.getProperty("ALLOWED_IP_ADDRESSES");
        GIP_IP = props.getProperty("GIP_IP");
        NOTIFY_URL = props.getProperty("NOTIFY_URL");
        NIB_APL_URL = props.getProperty("NIB_APL_URL");
        BEST_APL_URL = props.getProperty("BEST_APL_URL");
        DBDRIVER = props.getProperty("DBDRIVER");
        DBURL = props.getProperty("DBURL");
        DBDRIVER_37 = props.getProperty("DBDRIVER_37");
        DBURL_37 = props.getProperty("DBURL_37");
        DBUSER = props.getProperty("DBUSER");
        DBPASS = props.getProperty("DBPASS");
        DB_MAX_POOL = Integer.parseInt(props.getProperty("DB_MAX_POOL"));
        NIB_AUTH_ENDPOINT_URL = props.getProperty("NIB_AUTH_ENDPOINT_URL");
        NIB_ACCLOOKUP_ENDPOINT_URL = props.getProperty("NIB_ACCLOOKUP_ENDPOINT_URL");
        NIB_USERNAME = props.getProperty("NIB_USERNAME");
        NIB_PASSWORD = props.getProperty("NIB_PASSWORD");
        NIB_GRANTTYPE = props.getProperty("NIB_GRANTTYPE");
        NIB_CLIENTID = props.getProperty("NIB_CLIENTID");
        BP_ACCLOOKUP_ENDPOINT_URL = props.getProperty("BP_ACCLOOKUP_ENDPOINT_URL");
        BP_KEY = props.getProperty("BP_KEY");
        BP_SECRET = props.getProperty("BP_SECRET");
        BOA_ACCLOOKUP_ENDPOINT_URL = props.getProperty("BOA_ACCLOOKUP_ENDPOINT_URL");
        BOA_USERNAME = props.getProperty("BOA_USERNAME");
        BOA_PASSWORD = props.getProperty("BOA_PASSWORD");
        GCB_ACCLOOKUP_ENDPOINT_URL = props.getProperty("GCB_ACCLOOKUP_ENDPOINT_URL");
        GCB_USERNAME = props.getProperty("GCB_USERNAME");
        GCB_PASSWORD = props.getProperty("GCB_PASSWORD");
        GCB_TIMEOUT = props.getProperty("GCB_TIMEOUT");
        GCB_JKSLOC = props.getProperty("GCB_JKSLOC");
        GCB_JKSPASS = props.getProperty("GCB_JKSPASS");
        GCB_AUTH_ENDPOINT_URL = props.getProperty("GCB_AUTH_ENDPOINT_URL");
        GCB_GRANTTYPE = props.getProperty("GCB_GRANTTYPE");
        GARURAL_LOOKUP_ENDPOINT = props.getProperty("GARURAL_LOOKUP_ENDPOINT");
        GARURAL_USERNAME = props.getProperty("GARURAL_USERNAME");
        GARURAL_PASSWORD = props.getProperty("GARURAL_PASSWORD");
        TALENT_DEVKEY = props.getProperty("DEVKEY");
        TALENT_ENDPOINT = props.getProperty("TALENT_ENDPOINT");
    }

    private Config() {
    }

    public static void main(String[] args) {
        System.out.println(HOST);
        //System.out.println(SERVICE_MAP.get("B"));
        //System.out.println(SERVICE_MAP.get("GMONEY"));
    }

    public static String getProperty(String key) {
        return props.getProperty(key);
    }

    public static String getPropertyEager(String key) {
        try {
            props.load(new FileReader(new File("cfg" + File.separator + "ini.properties")));
        } catch (Exception ex) {
            logger.error("Sorry something went bad ooo. Unable to load config data from file|database. ", ex);
        }
        return props.getProperty(key);
    }

    public static void setValue(String token, String tokenExpiration) {
        try {
            PropertiesConfiguration conf = new PropertiesConfiguration(new File("cfg\\ini.properties"));
            conf.setProperty("token", token);
            conf.setProperty("tokenExpiration", tokenExpiration);
            conf.save();
        } catch (ConfigurationException ex) {
            System.out.println("Config->CONFIG EXCEPTION=>" + ex.getMessage());
        }
    }

    public static String getValue(final String key) {

        final Properties prop = new Properties();
        InputStream input = null;
        try {

            input = new FileInputStream(new File("cfg\\ini.properties"));

            prop.load(input);
            return prop.getProperty(key);
        } catch (IOException ex) {
            System.out.println("Config->CONFIG EXCEPTION=>" + ex.getMessage());
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    System.out.println("Config->CONFIG EXCEPTION=>" + e.getMessage());
                }
            }
        }
    }

}
