package com.etz.gh.kyc.processor;

import com.etz.conn.ds.Configuration;
import com.etz.conn.ds.SimplePool;
import com.etz.gh.kyc.model.AccountValidateRequest;
import com.etz.gh.kyc.model.TokenGenerationResponse;
import com.etz.gh.kyc.model.TokenRquest;
import com.etz.gh.kyc.util.Config;
import com.etz.security.util.passmgr.Credential;
import com.etz.gh.kyc.util.HttpClient;
import com.etz.gh.kyc.util.SuperDomParser;
import com.etz.gh.kyc.util.SuperHttpClient;
import java.sql.Timestamp;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Random;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class GIPProcessor extends Processor {
    
    private static final Logger logger = LogManager.getLogger(GIPProcessor.class);
    final static Properties props = new Properties();
    
    static {
        try {
            props.load(new FileInputStream(new File("cfg\\ini.properties")));
        } catch (IOException ex) {
            logger.error("Sorry, something wrong!", ex);
        }
    }
    
    static String getPropertyValue(String key) {
        return props.getProperty(key);
    }
    
    static String setPropertyValue(String key, String value) {
        return new JSONObject(props.setProperty(key, value)).toString();
    }
    
    public static synchronized String getDatetime() {
        return (new Timestamp(System.currentTimeMillis()) + "").replaceAll("[^\\d]", "").substring(2, 14);
    }
    
    public static String getRef(int size) {
        String value = "";
        for (int t = 0; t < size; t++) {
            value = value + new Random().nextInt(9);
        }
        return value;
    }
    
    private static String paddZeros(String amt) {
        if (amt.length() == 12) {
            return amt;
        }
        int diff = 12 - amt.length();
        for (int i = 0; i < diff; i++) {
            amt = "0" + amt;
        }
        return amt;
    }
    
    public static void main(String[] args) {
//        String resultSet = "0210011709151246~090*****5_1~09001211865_1~Joshua~0|0210010000023120~010*****008~01063030008~0|0210010000027288~090*****865~09001211865~0|";
//        StringBuffer sb;
//        sb = new StringBuffer(resultSet.replace("null", ""));
//
//        System.out.println(sb.deleteCharAt(sb.length() - 1).toString());

        String numb = new DecimalFormat("000000").format(new Random().nextInt(999999));
        System.out.println("RANDOM::: " + numb);
        
    }
    
    public String getAccountProfileMySQL2(String phoneNum) {
        Credential localCredential = new Credential();
        localCredential = localCredential.getCredential("", "cfg/credential.xml");
        
        Configuration config = new Configuration();
        config.setDbPassword(localCredential.getPassword());
        config.setDbUser(localCredential.getUsername());
        config.setDbURI(Config.DBURL);
        config.setDbDriverName(Config.DBDRIVER);
        config.setDbPoolMaxSize(Config.DB_MAX_POOL);
        
        PreparedStatement preparedstatement = null;
        ResponseMsg aplResp = new ResponseMsg();
        
        try ( Connection connection = SimplePool.ds.getConnection();) {
            
            preparedstatement = connection.prepareStatement("select group_concat(concat(card_num,'~',concat(left(card_account,3),'*****',right(card_account,5)),'~',card_account,'~',firstname,'~',change_pin) separator '|') card_account from ecarddb.e_cardholder "
                    + "where phone = ?");
            preparedstatement.setString(1, phoneNum);
            long start = System.currentTimeMillis();
            try ( ResultSet rs = preparedstatement.executeQuery()) {
                while (rs.next()) {
                    logger.info("RS: " + rs.toString());
                    if (rs.getString("card_account") != null) {
                        
                        aplResp.setResponseCode("00");
                        aplResp.setLookup(rs.getString("card_account").replace("\"", ""));
                        aplResp.setMessage("Got Data!!");
                    } else {
                        
                        aplResp.setResponseCode("06");
                        aplResp.setLookup("CARD_ACCOUNT is null");
                        aplResp.setMessage("Didn't get Data!!");
                    }
                }
            } catch (Exception e) {
                logger.error("Exception, " + e.getMessage());
            }
            
            long end = System.currentTimeMillis();
            System.out.println("->DB TAT" + (end - start));
            logger.info("->DB TAT" + (end - start));
            aplResp.setTat((end - start));
            
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (preparedstatement != null) {
                    preparedstatement.close();
                }
            } catch (SQLException e3) {
                logger.error(" Sorry, something wrong!", e3);
            }
        }
        return new JSONObject(aplResp).toString();
    }
    
    @Override
    public String getAccountProfile(String phoneNum) {
        
        Credential localCredential = new Credential();
        localCredential = localCredential.getCredential("", "cfg/Credential.xml");
        
        Configuration config = new Configuration();
        config.setDbPassword(localCredential.getPassword());
        config.setDbUser(localCredential.getUsername());
        config.setDbURI(Config.DBURL);
        config.setDbDriverName(Config.DBDRIVER);
        config.setDbPoolMaxSize(Config.DB_MAX_POOL);
        
        PreparedStatement preparedstatement = null;
        
        ResponseMsg aplResp = new ResponseMsg();
        try ( Connection connection = SimplePool.ds.getConnection()) {
            SimplePool.init(config);
            SimplePool.printDataSourceStats(SimplePool.ds);
            
            preparedstatement = connection.prepareStatement("select ((card_num + '~' + (left(card_account,3) + '*****' + right(card_account,3)) + '~' + card_account + '~' + firstname + '~' + change_pin)) card_account from e_cardholder "
                    + "where phone = ?");
            preparedstatement.setString(1, phoneNum);
            
            try ( ResultSet rs = preparedstatement.executeQuery()) {
                String resultSet = null;
                StringBuffer sb;
                while (rs.next()) {
                    logger.info("RS: " + rs.toString());
                    resultSet += rs.getString("card_account") + "|";
                }
                if (resultSet != null) {
                    aplResp.setResponseCode("00");
                    sb = new StringBuffer(resultSet.replace("null", ""));
                    aplResp.setLookup(sb.deleteCharAt(sb.length() - 1).toString());
                    aplResp.setMessage("Got Data!!");
                } else {
                    aplResp.setResponseCode("06");
                    aplResp.setLookup("CARD_ACCOUNT is null");
                    aplResp.setMessage("Didn't get Data!!");
                }
            } catch (Exception e) {
                logger.error("Exception, " + e.getMessage());
            }
            
        } catch (SQLException ex) {
            logger.error(" Sorry, something wrong!", ex);
        } finally {
            try {
                if (preparedstatement != null) {
                    preparedstatement.close();
                }
                
            } catch (SQLException e3) {
                logger.error(" Sorry, something wrong!", e3);
            }
        }
        String json = new Gson().toJson(aplResp);
        return json;
    }
    
    public String getAccountProfileSybase(String account) throws SQLException {
        
        Credential localCredential = new Credential();
        localCredential = localCredential.getCredential("", "cfg/Credential.xml");
        
        Configuration config = new Configuration();
        config.setDbPassword(localCredential.getPassword());
        config.setDbUser(localCredential.getUsername());
        config.setDbURI(Config.DBURL);
        config.setDbDriverName(Config.DBDRIVER);
        config.setDbPoolMaxSize(Config.DB_MAX_POOL);
        
        PreparedStatement preparedstatement = null;
        
        ResponseMsg aplResp = new ResponseMsg();
        SimplePool.init(config);
        SimplePool.printDataSourceStats(SimplePool.ds);
        try ( Connection connection = SimplePool.ds.getConnection()) {
            
            preparedstatement = connection.prepareStatement("select ((phone + '|' + card_account + '|' + firstname + ' '  + lastname + '|' + card_num + '|' + default_pin + '|'  + change_pin + '|'  + card_pin )) account_record from e_cardholder "
                    + "where card_account = ?");
            preparedstatement.setString(1, account);
            try ( ResultSet rs = preparedstatement.executeQuery()) {
                String resultSet = null;
                StringBuffer sb;
                while (rs.next()) {
                    logger.info("RS: " + rs.toString());
                    resultSet = rs.getString("account_record");
                }
                if (resultSet != null) {
                    aplResp.setResponseCode("00");
                    sb = new StringBuffer(resultSet.replace("null", ""));
                    aplResp.setLookup(sb.toString());
                    aplResp.setMessage("Got Data!!");
                } else {
                    aplResp.setResponseCode("06");
                    aplResp.setLookup("CARD_ACCOUNT is null");
                    aplResp.setMessage("Didn't get Data!!");
                }
            } catch (Exception e) {
                logger.error("Exception, " + e.getMessage());
            }
            
        } catch (SQLException ex) {
            logger.error(" Sorry, something wrong!", ex);
        } finally {
            try {
                if (preparedstatement != null) {
                    preparedstatement.close();
                }
                
            } catch (SQLException e3) {
                logger.error(" Sorry, something wrong!", e3);
            }
        }
        String json = new Gson().toJson(aplResp);
        return json;
    }
    
    public String getAccountProfileSybaseWithGhanaCard(String account) throws SQLException {
        
        Credential localCredential = new Credential();
        localCredential = localCredential.getCredential("", "cfg/Credential.xml");
        
        Configuration config = new Configuration();
        config.setDbPassword(localCredential.getPassword());
        config.setDbUser(localCredential.getUsername());
        config.setDbURI(Config.DBURL);
        config.setDbDriverName(Config.DBDRIVER);
        config.setDbPoolMaxSize(Config.DB_MAX_POOL);
        
        PreparedStatement preparedstatement = null;
        
        ResponseMsg aplResp = new ResponseMsg();
        SimplePool.init(config);
        SimplePool.printDataSourceStats(SimplePool.ds);
        try ( Connection connection = SimplePool.ds.getConnection()) {
            
            preparedstatement = connection.prepareStatement("select ((phone + '|' + card_account + '|' + firstname + ' '  + lastname + '|' + card_num + '|' + default_pin + '|'  + change_pin + '|'  + card_pin )) account_record from e_cardholder "
                    + "where card_account = ?");
            preparedstatement.setString(1, account);
            try ( ResultSet rs = preparedstatement.executeQuery()) {
                String resultSet = null;
                StringBuffer sb;
                while (rs.next()) {
                    logger.info("RS: " + rs.toString());
                    resultSet = rs.getString("account_record");
                }
                if (resultSet != null) {
                    aplResp.setResponseCode("00");
                    sb = new StringBuffer(resultSet.replace("null", ""));
                    aplResp.setLookup(sb.toString() + "|"
                            + (gcbALForGhanaCardService(account).split("\\|")[0].equals("00")
                            ? gcbALForGhanaCardService(account).split("\\|")[1] : "No Ghana Card"));
                    aplResp.setMessage("Got Data!!");
                } else {
                    aplResp.setResponseCode("06");
                    aplResp.setLookup("CARD_ACCOUNT is null");
                    aplResp.setMessage("Didn't get Data!!");
                }
            } catch (Exception e) {
                logger.error("Exception, " + e.getMessage());
            }
            
        } catch (SQLException ex) {
            logger.error(" Sorry, something wrong!", ex);
        } finally {
            try {
                if (preparedstatement != null) {
                    preparedstatement.close();
                }
                
            } catch (SQLException e3) {
                logger.error(" Sorry, something wrong!", e3);
            }
        }
        String json = new Gson().toJson(aplResp);
        return json;
    }
    
    @Override
    public String getAccountProfileMySQL(String phoneNum) {
        
        Credential localCredential = new Credential();
        localCredential = localCredential.getCredential("", "cfg/credential.xml");
        
        Configuration config = new Configuration();
        config.setDbPassword(localCredential.getPassword());
        config.setDbUser(localCredential.getUsername());
        config.setDbURI(Config.DBURL);
        config.setDbDriverName(Config.DBDRIVER);
        config.setDbPoolMaxSize(Config.DB_MAX_POOL);
        
        PreparedStatement preparedstatement = null;
        
        ResponseMsg aplResp = new ResponseMsg();
        try {
            
            SimplePool.init(config);
            SimplePool.printDataSourceStats(SimplePool.ds);
            try ( Connection connection = SimplePool.ds.getConnection()) {
                
                preparedstatement = connection.prepareStatement("select group_concat(concat(card_num,'~',concat(left(card_account,3),'*****',right(card_account,5)),'~',card_account,'~',firstname,'~',change_pin) separator '|') card_account from ecarddb.e_cardholder "
                        + "where phone = ?");
                preparedstatement.setString(1, phoneNum);
                
                long start = System.currentTimeMillis();
                try ( ResultSet rs = preparedstatement.executeQuery()) {
                    while (rs.next()) {
                        logger.info("RS: " + rs.toString());
                        System.out.println("RS: " + rs.toString());
                        if (rs.getString("card_account") != null) {
                            
                            aplResp.setResponseCode("00");
                            aplResp.setLookup(rs.getString("card_account").replace("\"", ""));
                            logger.info("CARD_ACCOUNT:: " + rs.getString("card_account"));
                            aplResp.setMessage("Got Data!!");
                        } else {
                            
                            aplResp.setResponseCode("06");
                            aplResp.setLookup("CARD_ACCOUNT is null");
                            aplResp.setMessage("Didn't get Data!!");
                        }
                    }
                } catch (Exception e) {
                    logger.error("Exception, " + e.getMessage());
                }
                
                long end = System.currentTimeMillis();
                System.out.println("->DB TAT" + (end - start));
                logger.info("->DB TAT" + (end - start));
                aplResp.setTat((end - start));
                
            } catch (SQLException ex) {
                logger.error(" Sorry, something wrong!" + ex.getMessage());
                java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedstatement != null) {
                        preparedstatement.close();
                    }
                    
                } catch (SQLException e3) {
                    logger.error(" Sorry, something wrong!" + e3.getMessage());
                }
            }
            
        } catch (SQLException e) {
            logger.error(" Sorry, something wrong!" + e.getMessage());
            java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, e);
            
        }
        String json = new Gson().toJson(aplResp);
        System.out.println("APL:: " + json);
        return json;
    }
    
    @Override
    public String getField37FromEHostRespMySQL(String reference) {
        
        System.out.println("->GOT HERE!!! " + reference);
        logger.info("->GOT HERE!!! " + reference);
        Credential localCredential = new Credential();
        localCredential = localCredential.getCredential("", "cfg/credential_37.xml");
        
        System.out.println("->USER:: " + localCredential.getUsername());
        logger.info("->USER:: " + localCredential.getUsername());
        Configuration config = new Configuration();
        config.setDbPassword(localCredential.getPassword());
        config.setDbUser(localCredential.getUsername());
        config.setDbURI(Config.DBURL_37);
        config.setDbDriverName(Config.DBDRIVER_37);
        config.setDbPoolMaxSize(Config.DB_MAX_POOL);
        
        PreparedStatement preparedstatement = null;
        
        ResponseMsg aplResp = new ResponseMsg();
        try {
            SimplePool.init(config);
            SimplePool.printDataSourceStats(SimplePool.ds);
            
            try ( Connection connection = SimplePool.ds.getConnection()) {
                
                preparedstatement = connection.prepareStatement("select de37 from ecarddb.e_hostrequestlog "
                        + "where unique_transid = ?");
                preparedstatement.setString(1, reference);
                
                long start = System.currentTimeMillis();
                ResultSet rs = preparedstatement.executeQuery();
                long end = System.currentTimeMillis();
                System.out.println("->DB TAT" + (end - start));
                logger.info("->DB TAT" + (end - start));
                aplResp.setTat((end - start));
                
                while (rs.next()) {
                    logger.info("RS: " + rs.toString());
                    System.out.println("RS: " + rs.toString());
                    if (rs.getString("de37") != null) {
                        
                        aplResp.setResponseCode("00");
                        aplResp.setLookup(rs.getString("de37").replace("\"", ""));
                        logger.info("transkey:: " + rs.getString("de37"));
                        aplResp.setMessage("Got Data!!");
                    } else {
                        
                        aplResp.setResponseCode("06");
                        aplResp.setLookup("de37 is null");
                        aplResp.setMessage("Didn't get Data!!");
                    }
                }
            } catch (SQLException ex) {
                logger.error(" Sorry, something wrong!" + ex.getMessage());
                java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedstatement != null) {
                        preparedstatement.close();
                    }
                } catch (SQLException e3) {
                    logger.error(" Sorry, something wrong!" + e3.getMessage());
                }
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        String json = new Gson().toJson(aplResp);
        System.out.println("E_CARD:: " + json);
        return json;
    }
    
    @Override
    public String getAccountFromEcard(String card_number, String phone) {
        System.out.println("->GOT HERE!!! " + card_number);
        logger.info("->GOT HERE!!!");
        Credential localCredential = new Credential();
        localCredential = localCredential.getCredential("", "cfg/credential.xml");
        
        System.out.println("->USER:: " + localCredential.getUsername());
        logger.info("->USER:: " + localCredential.getUsername());
        Configuration config = new Configuration();
        config.setDbPassword(localCredential.getPassword());
        config.setDbUser(localCredential.getUsername());
        config.setDbURI(Config.DBURL);
        config.setDbDriverName(Config.DBDRIVER);
        config.setDbPoolMaxSize(Config.DB_MAX_POOL);
        
        PreparedStatement preparedstatement = null;
        
        ResponseMsg aplResp = new ResponseMsg();
        try {
            SimplePool.init(config);
            SimplePool.printDataSourceStats(SimplePool.ds);
            
            try ( Connection connection = SimplePool.ds.getConnection()) {
                
                if (phone != null) {
                    System.out.println("FOR PHONE ONLY");
                    preparedstatement = connection.prepareStatement("select group_concat(card_account separator '|') card_account from ecarddb.e_cardholder "
                            + "where phone = ?");
                    preparedstatement.setString(1, phone);
                } else if (card_number != null) {
                    System.out.println("FOR CARD ONLY");
                    preparedstatement = connection.prepareStatement("select card_account from ecarddb.e_cardholder "
                            + "where card_num = ?");
                    preparedstatement.setString(1, card_number);
                } else {
                    preparedstatement = connection.prepareStatement("select card_account from ecarddb.e_cardholder "
                            + "where card_num = ?");
                    preparedstatement.setString(1, card_number);
                }
                
                long start = System.currentTimeMillis();
                try ( ResultSet rs = preparedstatement.executeQuery()) {
                    while (rs.next()) {
                        logger.info("RS: " + rs.toString());
                        System.out.println("RS: " + rs.toString());
                        if (rs.getString("card_account") != null) {
                            
                            aplResp.setResponseCode("00");
                            aplResp.setLookup(rs.getString("card_account").replace("\"", ""));
                            logger.info("CARD_ACCOUNT:: " + rs.getString("card_account"));
                            aplResp.setMessage("Got Data!!");
                        } else {
                            
                            aplResp.setResponseCode("06");
                            aplResp.setLookup("CARD_ACCOUNT is null");
                            aplResp.setMessage("Didn't get Data!!");
                        }
                    }
                } catch (Exception e) {
                    logger.error("Exception, " + e.getMessage());
                }
                
                long end = System.currentTimeMillis();
                System.out.println("->DB TAT" + (end - start));
                logger.info("->DB TAT" + (end - start));
                aplResp.setTat((end - start));
                
            } catch (SQLException ex) {
                logger.error(" Sorry, something wrong!" + ex.getMessage());
                java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedstatement != null) {
                        preparedstatement.close();
                    }
                } catch (SQLException e3) {
                    logger.error(" Sorry, something wrong!" + e3.getMessage());
                }
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        String json = new Gson().toJson(aplResp);
        System.out.println("E_CARD:: " + json);
        return json;
    }
    
    @Override
    public String getAccountFromEcardSybase(String card_number, String phone) {
        
        Credential localCredential = new Credential();
        localCredential = localCredential.getCredential("", "cfg/Credential.xml");
        
        Configuration config = new Configuration();
        config.setDbPassword(localCredential.getPassword());
        config.setDbUser(localCredential.getUsername());
        config.setDbURI(Config.DBURL);
        config.setDbDriverName(Config.DBDRIVER);
        config.setDbPoolMaxSize(Config.DB_MAX_POOL);
        
        PreparedStatement preparedstatement = null;
        
        ResponseMsg aplResp = new ResponseMsg();
        try {
            SimplePool.init(config);
            SimplePool.printDataSourceStats(SimplePool.ds);
            
            try ( Connection connection = SimplePool.ds.getConnection()) {
                
                if (card_number != null) {
                    preparedstatement = connection.prepareStatement("select card_account from e_cardholder "
                            + "where card_num = ?");
                    preparedstatement.setString(1, card_number);
                } else if (phone != null) {
                    preparedstatement = connection.prepareStatement("select card_account from e_cardholder "
                            + "where phone = ?");
                    preparedstatement.setString(1, phone);
                } else {
                    preparedstatement = connection.prepareStatement("select card_account from e_cardholder "
                            + "where card_num = ?");
                    preparedstatement.setString(1, card_number);
                }
                
                long start = System.currentTimeMillis();
                try ( ResultSet rs = preparedstatement.executeQuery()) {
                    String resultSet = null;
                    StringBuffer sb;
                    while (rs.next()) {
                        logger.info("RS: " + rs.toString());
                        resultSet += rs.getString("card_account") + "|";
                    }
                    logger.info("RESULT SET::: " + resultSet);
                    if (resultSet != null) {
                        aplResp.setResponseCode("00");
                        sb = new StringBuffer(resultSet.replace("null", ""));
                        aplResp.setLookup(sb.deleteCharAt(sb.length() - 1).toString());
                        aplResp.setMessage("Got Data!!");
                    } else {
                        aplResp.setResponseCode("06");
                        aplResp.setLookup("CARD_ACCOUNT is null");
                        aplResp.setMessage("Didn't get Data!!");
                    }
                } catch (Exception e) {
                    logger.error("Exception, " + e.getMessage());
                }
                
                long end = System.currentTimeMillis();
                System.out.println("->DB TAT" + (end - start));
                logger.info("->DB TAT" + (end - start));
                aplResp.setTat((end - start));
                
            } catch (SQLException ex) {
                logger.error(" Sorry, something wrong!" + ex.getMessage());
                java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedstatement != null) {
                        preparedstatement.close();
                    }
                    
                } catch (SQLException e3) {
                    logger.error(" Sorry, something wrong!" + e3.getMessage());
                }
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        String json = new Gson().toJson(aplResp);
        System.out.println("E_CARD:: " + json);
        return json;
    }
    
    @Override
    public String updateControlIdInEcardSybase(String card_number, String control_id) {
        
        Credential localCredential = new Credential();
        localCredential = localCredential.getCredential("", "cfg/Credential.xml");
        
        Configuration config = new Configuration();
        config.setDbPassword(localCredential.getPassword());
        config.setDbUser(localCredential.getUsername());
        config.setDbURI(Config.DBURL);
        config.setDbDriverName(Config.DBDRIVER);
        config.setDbPoolMaxSize(Config.DB_MAX_POOL);
        
        PreparedStatement preparedstatement = null;
        
        ResponseMsg aplResp = new ResponseMsg();
        try {
            SimplePool.init(config);
            SimplePool.printDataSourceStats(SimplePool.ds);
            
            try ( Connection connection = SimplePool.ds.getConnection()) {
                
                long start = System.currentTimeMillis();
                if (card_number != null) {
                    preparedstatement = connection.prepareStatement("update e_cardholder set control_id = ?"
                            + " where card_num = ?");
                    preparedstatement.setString(1, control_id);
                    preparedstatement.setString(2, card_number);
                    
                    int rs = preparedstatement.executeUpdate();
                    long end = System.currentTimeMillis();
                    System.out.println("->DB TAT" + (end - start));
                    logger.info("->DB TAT" + (end - start));
                    aplResp.setTat((end - start));
                    logger.info("UPDATE RESULT:: " + rs);
                    
                    if (rs == 1) {
                        aplResp.setResponseCode("00");
                        aplResp.setLookup(card_number + " record update successful");
                        aplResp.setMessage("Success");
                    } else {
                        aplResp.setResponseCode("06");
                        aplResp.setLookup("Update failed");
                        aplResp.setMessage("Update failed!!");
                    }
                }
                
            } catch (SQLException ex) {
                logger.error(" Sorry, something wrong!" + ex.getMessage());
                java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedstatement != null) {
                        preparedstatement.close();
                    }
                } catch (SQLException e3) {
                    logger.error(" Sorry, something wrong!" + e3.getMessage());
                }
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        String json = new Gson().toJson(aplResp);
        System.out.println("E_CARD:: " + json);
        return json;
    }
    
    public String getAccountFromEcardForVB(String card_number, String phone) {
        System.out.println("->GOT HERE!!! " + card_number);
        logger.info("->GOT HERE!!!");
        Credential localCredential = new Credential();
        localCredential = localCredential.getCredential("", "cfg/credential.xml");
        
        System.out.println("->USER:: " + localCredential.getUsername());
        logger.info("->USER:: " + localCredential.getUsername());
        Configuration config = new Configuration();
        config.setDbPassword(localCredential.getPassword());
        config.setDbUser(localCredential.getUsername());
        config.setDbURI(Config.DBURL);
        config.setDbDriverName(Config.DBDRIVER);
        config.setDbPoolMaxSize(Config.DB_MAX_POOL);
        
        PreparedStatement preparedstatement = null;
        
        ResponseMsg aplResp = new ResponseMsg();
        try {
            SimplePool.init(config);
            SimplePool.printDataSourceStats(SimplePool.ds);
            try ( Connection connection = SimplePool.ds.getConnection()) {
                
                if (phone != null) {
                    System.out.println("FOR PHONE ONLY");
                    preparedstatement = connection.prepareStatement("select group_concat(card_num separator '|') card_num from ecarddb.e_cardholder "
                            + "where phone = ?");
                    preparedstatement.setString(1, phone);
                } else if (card_number != null) {
                    System.out.println("FOR CARD ONLY");
                    preparedstatement = connection.prepareStatement("select card_num from ecarddb.e_cardholder "
                            + "where card_num = ?");
                    preparedstatement.setString(1, card_number);
                } else {
                    preparedstatement = connection.prepareStatement("select card_num from ecarddb.e_cardholder "
                            + "where phone = ?");
                    preparedstatement.setString(1, phone);
                }
                
                long start = System.currentTimeMillis();
                try ( ResultSet rs = preparedstatement.executeQuery()) {
                    while (rs.next()) {
                        logger.info("RS: " + rs.toString());
                        System.out.println("RS: " + rs.toString());
                        if (rs.getString("card_num") != null) {
                            
                            aplResp.setResponseCode("00");
                            aplResp.setLookup(rs.getString("card_num").replace("\"", ""));
                            logger.info("CARD_NUM:: " + rs.getString("card_num"));
                            aplResp.setMessage("Got Data!!");
                        } else {
                            
                            aplResp.setResponseCode("06");
                            aplResp.setLookup("CARD_NUM is null");
                            aplResp.setMessage("Didn't get Data!!");
                        }
                    }
                } catch (Exception e) {
                    logger.error("Exception, " + e.getMessage());
                }
                
                long end = System.currentTimeMillis();
                System.out.println("->DB TAT" + (end - start));
                logger.info("->DB TAT" + (end - start));
                aplResp.setTat((end - start));
                
            } catch (SQLException ex) {
                logger.error(" Sorry, something wrong!" + ex.getMessage());
                java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedstatement != null) {
                        preparedstatement.close();
                    }
                } catch (SQLException e3) {
                    logger.error(" Sorry, something wrong!" + e3.getMessage());
                }
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        String json = new Gson().toJson(aplResp);
        System.out.println("E_CARD:: " + json);
        return json;
    }
    
    public String getSumOfAccountFromEcardForVB(String cardAccount, String email) throws SQLException {
        System.out.println("->GOT HERE!!! " + cardAccount);
        logger.info("->GOT HERE!!!");
        
        System.out.println("->EMAIL HERE::: " + email);
        logger.info("->EMAIL HERE::: " + email);
        Credential localCredential = new Credential();
        localCredential = localCredential.getCredential("", "cfg/credential.xml");
        
        Configuration config = new Configuration();
        config.setDbPassword(localCredential.getPassword());
        config.setDbUser(localCredential.getUsername());
        config.setDbURI(Config.DBURL);
        config.setDbDriverName(Config.DBDRIVER);
        config.setDbPoolMaxSize(Config.DB_MAX_POOL);
        
        PreparedStatement preparedstatement = null;
        
        ResponseMsg aplResp = new ResponseMsg();
        SimplePool.init(config);
        SimplePool.printDataSourceStats(SimplePool.ds);
        try ( Connection connection = SimplePool.ds.getConnection()) {
            
            if (!cardAccount.isEmpty()) {
                cardAccount = "'" + cardAccount.replace(",", "','") + "'";
                System.out.println("CARD_ACCOUNT::: " + cardAccount);
                logger.info("CARD_ACCOUNT::: " + cardAccount);
                preparedstatement = connection.prepareStatement("SELECT SUM(ONLINE_BALANCE) sumBal FROM ecarddb.e_cardholder WHERE CARD_ACCOUNT IN (" + cardAccount + ") "
                        + "OR CARD_ACCOUNT like '" + email + "'");
                
            } else {
                preparedstatement = connection.prepareStatement("SELECT SUM(ONLINE_BALANCE) sumBal FROM ecarddb.e_cardholder WHERE"
                        + " CARD_ACCOUNT like '" + email + "'");
                
            }
            
            long start = System.currentTimeMillis();
            try ( ResultSet rs = preparedstatement.executeQuery()) {
                while (rs.next()) {
                    logger.info("RS: " + rs.toString());
                    System.out.println("RS: " + rs.toString());
                    if (rs.getString("sumBal") != null) {
                        
                        aplResp.setResponseCode("00");
                        aplResp.setLookup(rs.getString("sumBal"));
                        logger.info("TOTAL BALANCE:: " + rs.getString("sumBal"));
                        aplResp.setMessage("Got Data!!");
                    } else {
                        
                        aplResp.setResponseCode("06");
                        aplResp.setLookup("0.0");
                        aplResp.setMessage("Didn't get Data!!");
                    }
                }
            } catch (Exception e) {
                logger.error("Exception, " + e.getMessage());
            }
            
            long end = System.currentTimeMillis();
            System.out.println("->DB TAT" + (end - start));
            logger.info("->DB TAT" + (end - start));
            aplResp.setTat((end - start));
            
        } catch (SQLException ex) {
            logger.error(" Sorry, something wrong!" + ex.getMessage());
            java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (preparedstatement != null) {
                    preparedstatement.close();
                }
            } catch (SQLException e3) {
                logger.error(" Sorry, something wrong!" + e3.getMessage());
            }
        }
        String json = new Gson().toJson(aplResp);
        System.out.println("E_CARD:: " + json);
        return json;
    }
    
    public String getSumOfAgentFromEcardForVB(String cardAccount) {
        System.out.println("->GOT HERE!!! " + cardAccount);
        logger.info("->GOT HERE!!!");
        
        Credential localCredential = new Credential();
        localCredential = localCredential.getCredential("", "cfg/credential.xml");
        
        Configuration config = new Configuration();
        config.setDbPassword(localCredential.getPassword());
        config.setDbUser(localCredential.getUsername());
        config.setDbURI(Config.DBURL);
        config.setDbDriverName(Config.DBDRIVER);
        config.setDbPoolMaxSize(Config.DB_MAX_POOL);
        
        PreparedStatement preparedstatement = null;
        
        ResponseMsg aplResp = new ResponseMsg();
        try {
            SimplePool.init(config);
            SimplePool.printDataSourceStats(SimplePool.ds);
            try ( Connection connection = SimplePool.ds.getConnection()) {
                
                long start = System.currentTimeMillis();
                if (!cardAccount.isEmpty()) {
                    cardAccount = "'" + cardAccount.replace(",", "','") + "'";
                    System.out.println("CARD_ACCOUNT::: " + cardAccount);
                    logger.info("CARD_ACCOUNT::: " + cardAccount);
                    preparedstatement = connection.prepareStatement("SELECT SUM(ONLINE_BALANCE) sumBal FROM ecarddb.e_cardholder WHERE CARD_ACCOUNT IN (" + cardAccount + ") ");
                    
                    try ( ResultSet rs = preparedstatement.executeQuery()) {
                        while (rs.next()) {
                            logger.info("RS: " + rs.toString());
                            System.out.println("RS: " + rs.toString());
                            if (rs.getString("sumBal") != null) {
                                
                                aplResp.setResponseCode("00");
                                aplResp.setLookup(rs.getString("sumBal"));
                                logger.info("TOTAL BALANCE:: " + rs.getString("sumBal"));
                                aplResp.setMessage("Got Data!!");
                            } else {
                                
                                aplResp.setResponseCode("06");
                                aplResp.setLookup("0.0");
                                aplResp.setMessage("Didn't get Data!!");
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Exception, " + e.getMessage());
                    }
                }
                
                long end = System.currentTimeMillis();
                System.out.println("->DB TAT" + (end - start));
                logger.info("->DB TAT" + (end - start));
                aplResp.setTat((end - start));
                
            } catch (SQLException ex) {
                logger.error(" Sorry, something wrong!" + ex.getMessage());
                java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedstatement != null) {
                        preparedstatement.close();
                    }
                    
                } catch (SQLException e3) {
                    logger.error(" Sorry, something wrong!" + e3.getMessage());
                }
            }
        } catch (SQLException ex) {
            logger.error(" Sorry, something wrong!" + ex.getMessage());
            java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        String json = new Gson().toJson(aplResp);
        System.out.println("E_CARD:: " + json);
        return json;
    }
    
    public String getSumFromEcardForVB(String card_account, String phone) {
        System.out.println("->GOT HERE!!! " + card_account);
        logger.info("->GOT HERE!!!");
        Credential localCredential = new Credential();
        localCredential = localCredential.getCredential("", "cfg/credential.xml");
        
        System.out.println("->USER:: " + localCredential.getUsername());
        logger.info("->USER:: " + localCredential.getUsername());
        Configuration config = new Configuration();
        config.setDbPassword(localCredential.getPassword());
        config.setDbUser(localCredential.getUsername());
        config.setDbURI(Config.DBURL);
        config.setDbDriverName(Config.DBDRIVER);
        config.setDbPoolMaxSize(Config.DB_MAX_POOL);
        
        PreparedStatement preparedstatement = null;
        
        ResponseMsg aplResp = new ResponseMsg();
        try {
            SimplePool.init(config);
            SimplePool.printDataSourceStats(SimplePool.ds);
            try ( Connection connection = SimplePool.ds.getConnection()) {
                
                if (phone != null) {
                    System.out.println("FOR PHONE ONLY");
                    preparedstatement = connection.prepareStatement("select group_concat(card_num separator '|') card_num from ecarddb.e_cardholder "
                            + "where phone = ?");
                    preparedstatement.setString(1, phone);
                } else if (card_account != null) {
                    System.out.println("FOR CARD ONLY");
                    preparedstatement = connection.prepareStatement("select card_num from ecarddb.e_cardholder "
                            + "where card_num = ?");
                    preparedstatement.setString(1, card_account);
                } else {
                    preparedstatement = connection.prepareStatement("select card_num from ecarddb.e_cardholder "
                            + "where phone = ?");
                    preparedstatement.setString(1, phone);
                }
                
                long start = System.currentTimeMillis();
                try ( ResultSet rs = preparedstatement.executeQuery()) {
                    while (rs.next()) {
                        logger.info("RS: " + rs.toString());
                        System.out.println("RS: " + rs.toString());
                        if (rs.getString("card_num") != null) {
                            
                            aplResp.setResponseCode("00");
                            aplResp.setLookup(rs.getString("card_num").replace("\"", ""));
                            logger.info("CARD_NUM:: " + rs.getString("card_num"));
                            aplResp.setMessage("Got Data!!");
                        } else {
                            
                            aplResp.setResponseCode("06");
                            aplResp.setLookup("CARD_NUM is null");
                            aplResp.setMessage("Didn't get Data!!");
                        }
                    }
                } catch (Exception e) {
                    logger.error("Exception, " + e.getMessage());
                }
                
                long end = System.currentTimeMillis();
                System.out.println("->DB TAT" + (end - start));
                logger.info("->DB TAT" + (end - start));
                aplResp.setTat((end - start));
                
            } catch (SQLException ex) {
                logger.error(" Sorry, something wrong!" + ex.getMessage());
                java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    if (preparedstatement != null) {
                        preparedstatement.close();
                    }
                    
                } catch (SQLException e3) {
                    logger.error(" Sorry, something wrong!" + e3.getMessage());
                }
            }
        } catch (SQLException ex) {
            logger.error(" Sorry, something wrong!" + ex.getMessage());
            java.util.logging.Logger.getLogger(GIPProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        String json = new Gson().toJson(aplResp);
        System.out.println("E_CARD:: " + json);
        return json;
    }
    
    public String boaALService(String account) {
        ResponseMsg aplResp = new ResponseMsg();
        String accLookUpStatus;
        String responseCode;
        String accountLookUpResponse;
        String accountLookUpXML;
        
        String action = "http://tempuri.org/ValidateAccount";
        try {
            accountLookUpXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                    + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                    + "  <soap:Body>\n"
                    + "    <ValidateAccount xmlns=\"http://tempuri.org/\">\n"
                    + "      <account_no>" + account + "</account_no>\n"
                    + "      <usrname>" + Config.BOA_USERNAME + "</usrname>\n"
                    + "      <caller_ref>" + getRef(12) + "</caller_ref>\n"
                    + "      <pwd>" + Config.BOA_PASSWORD + "</pwd>\n"
                    + "    </ValidateAccount>\n"
                    + "  </soap:Body>\n"
                    + "</soap:Envelope>";
            
            try {
//                logTranslatedRequest(isomsg, ISOFIELD, accountLookUpXML);

                long start = System.currentTimeMillis();
                accountLookUpResponse = HttpClient.postData(Config.BOA_ACCLOOKUP_ENDPOINT_URL, accountLookUpXML, action);
                System.out.println("==========BOA ACCOUNT LOOKUP XML REQUEST=======");
                
                System.out.println(accountLookUpXML.replace(Config.BOA_USERNAME, "********").replace(Config.BOA_PASSWORD, "********"));
                long end = System.currentTimeMillis();
                System.out.println("->TAT" + (end - start));
                
                System.out.println("==========BOA ACCOUNT LOOKUP XML RESPONSE=======");
                System.out.println(accountLookUpResponse);
                SuperDomParser dom = new SuperDomParser(accountLookUpResponse);
                accLookUpStatus = dom.getElementValue("status");
                
                if (accLookUpStatus != null && accLookUpStatus.equalsIgnoreCase("ok")) {
                    
                    String customerName = dom.getElementValue("cust_name");
                    String customerPhone = dom.getElementValue("msg");
                    String accounReturned = dom.getElementValue("result_value");
                    
                    String cardXML = customerPhone + "|" + customerName + "|" + accounReturned;
                    
                    responseCode = "00";
                    aplResp.setLookup(cardXML);
                    aplResp.setMessage("Got data!!!");
                    aplResp.setResponseCode(responseCode);
                    
                } else {
                    responseCode = "06";
                    String errorDesc = new SuperDomParser(dom.getElementValue("status")).getElementValue("msg");
                    aplResp.setResponseCode(responseCode);
                    aplResp.setMessage(errorDesc);
                }
                
            } catch (NumberFormatException ex) {
                
                responseCode = "06";
                logger.error(ex.getMessage());
                aplResp.setResponseCode(responseCode);
                aplResp.setMessage("An error occured please try again");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        String json = new Gson().toJson(aplResp);
        System.out.println(json);
        
        return json;
    }
    
    public String bestPointALService(String account) {
        ResponseMsg aplResp = new ResponseMsg();
        String responseData = null;
        HttpClient client = new HttpClient();
//        String lookup = "ACCOUNT LINKED|CURRENCY|ACCOUNT TYPE|ACCOUNT NAME|CUSTOMER PHONE|BALANCE";
        String lookup = null;
        try {
            
            long start = System.currentTimeMillis();
            
            responseData = client.getHttp(Config.BP_ACCLOOKUP_ENDPOINT_URL + account, Config.BP_KEY, Config.BP_SECRET);
            long end = System.currentTimeMillis();
            System.out.println("->TAT" + (end - start));
            
            System.out.println("=========LOOKUP ACCOUNT WITH PHONE RESPONSE==========");
            System.out.println(responseData);
            logger.info(responseData);
            
            if (responseData != null || !"".equals(responseData)) {
                JSONObject jsonData = new JSONObject(responseData);
                String responseCode = jsonData.optString("responseCode");
                String responseMsg = jsonData.optString("message");
                System.out.println("RESP MESSAGE:: " + responseMsg);
                
                JSONArray jsonObjData = jsonData.optJSONArray("data");
                
                if (jsonObjData.toString().contains("[{")) {
                    
                    if (responseCode.equals("000") && responseMsg.equalsIgnoreCase("Success") && jsonObjData.length() != 0) {
                        
                        for (int i = 0; i < jsonObjData.length(); i++) {
                            JSONObject jObjData = jsonObjData.optJSONObject(i);
                            
                            lookup = jObjData.get("primaryAccountPhoneNumber") + "|" + jObjData.optString("acctName")
                                    + "|" + jObjData.optString("acctLink")
                                    + "|" + jObjData.optString("currency");
                            
                        }
                        aplResp.setResponseCode("00");
                        aplResp.setLookup(lookup);
                        logger.info(responseMsg);
                    } else {
                        aplResp.setResponseCode("06");
                        aplResp.setMessage(responseMsg);
                        logger.info(responseMsg);
                    }
                } else {
                    aplResp.setResponseCode("06");
                    aplResp.setMessage(responseMsg + ".Data is empty!!!");
                    logger.info(responseMsg);
                }
            } else {
                aplResp.setResponseCode("06");
                aplResp.setMessage("Response is null!!!");
                logger.info("Response is null!!!");
            }
            
        } catch (JSONException e) {
            aplResp.setResponseCode("96");
            aplResp.setMessage(e.getMessage());
            logger.info("Exception >>>", e);
        }
        
        return new JSONObject(aplResp).toString();
        
    }
    
    public String gcbALService(String account) throws Exception {
        ResponseMsg aplResp = new ResponseMsg();
        
        String cardXML;
        Map<String, String> rspMap = new HashMap<>();
        
        try {
            
            TokenGenerationResponse loginResponse = getTokenGCB(Config.GCB_AUTH_ENDPOINT_URL, Config.GCB_USERNAME, Config.GCB_PASSWORD, Config.GCB_GRANTTYPE);
            System.out.println("****ACCOUNT LOOKUP REQUEST****");
            System.out.println(Config.GCB_ACCLOOKUP_ENDPOINT_URL + "api/accounts/" + account + "/account");
            long start = System.currentTimeMillis();
            
            rspMap = SuperHttpClient.doGet(Config.GCB_ACCLOOKUP_ENDPOINT_URL + "api/accounts/" + account + "/account", loginResponse.getAccessToken());
            System.out.println("->TAT" + (System.currentTimeMillis() - start));
            
            System.out.println("******ACCOUNT BALANCE RESPONSE*******");
            System.out.println("RESPONSE MAP:: " + rspMap.toString());
            System.out.println(rspMap.get("body"));
            if (rspMap.get("code").equals("200") && rspMap.get("body") != null) {
                JSONObject json = new JSONObject(rspMap.get("body"));
                
                cardXML = json.optString("mobNo") + "|" + json.optString("firstName") + " " + json.optString("middleName")
                        + " " + json.optString("lastName") + "|" + json.optString("accountNumber") + "|" + json.optString("ghanaCard");
                
                aplResp.setResponseCode("00");
                aplResp.setMessage("Account Lookup successful!");
                aplResp.setLookup(cardXML);
            } else {
                cardXML = "<CardQuery><Card>Account Not Found</CardAccount></CardQuery>";
                
                aplResp.setResponseCode("06");
                aplResp.setMessage("Account Lookup Failed");
                aplResp.setLookup(cardXML);
                
            }
        } catch (JSONException e) {
            logger.error("Ecception>>> " + e.getMessage());
            aplResp.setLookup(e.getMessage());
            aplResp.setResponseCode("06");
            aplResp.setMessage("An Error Occured");
            
        }
        return new JSONObject(aplResp).toString();
    }
    
    public String gcbALForGhanaCardService(String account) throws Exception {
        
        String cardXML;
        Map<String, String> rspMap = new HashMap<>();
        
        try {
            
            TokenGenerationResponse loginResponse = getTokenGCB(Config.GCB_AUTH_ENDPOINT_URL, Config.GCB_USERNAME, Config.GCB_PASSWORD, Config.GCB_GRANTTYPE);
            System.out.println("****ACCOUNT LOOKUP REQUEST****");
            System.out.println(Config.GCB_ACCLOOKUP_ENDPOINT_URL + "api/accounts/" + account + "/account");
            long start = System.currentTimeMillis();
            
            rspMap = SuperHttpClient.doGet(Config.GCB_ACCLOOKUP_ENDPOINT_URL + "api/accounts/" + account + "/account", loginResponse.getAccessToken());
            System.out.println("->TAT" + (System.currentTimeMillis() - start));
            
            System.out.println("******ACCOUNT BALANCE RESPONSE*******");
            System.out.println("RESPONSE MAP:: " + rspMap.toString());
            System.out.println(rspMap.get("body"));
            if (rspMap.get("code").equals("200") && rspMap.get("body") != null) {
                JSONObject json = new JSONObject(rspMap.get("body"));
                
                cardXML = "00|" + json.optString("ghanaCard");
            } else {
                cardXML = "06|Failed";
                
            }
        } catch (JSONException e) {
            logger.error("Exception>>> " + e.getMessage());
            cardXML = "06|Exception";
            
        }
        return cardXML;
    }
    
    public String nibALService(String account) throws IOException, Exception {
        
        AccountValidateRequest request = new AccountValidateRequest();
        TokenGenerationResponse loginResponse = getToken(Config.NIB_AUTH_ENDPOINT_URL, Config.NIB_USERNAME, Config.NIB_PASSWORD, Config.NIB_GRANTTYPE);
        String responseData;
        
        HttpClient client = new HttpClient();
        ResponseMsg aplResp = new ResponseMsg();
        
        try {
            if (loginResponse.getToken() != null) {
                request.setClientid(Config.NIB_CLIENTID);
                request.setClientcode(getRef(15));
                request.setAccountno(account);
                
                System.out.println("=========ACOUNT LOOKUP REQUEST==========");
                System.out.println(new JSONObject(request).toString().replace(Config.NIB_CLIENTID, "********"));
                logger.info(new JSONObject(request).toString().replace(Config.NIB_CLIENTID, "********"));
                System.out.println("ENDPOINT POSTED TO:: " + Config.NIB_ACCLOOKUP_ENDPOINT_URL);
                String accountValidateFormat;
                responseData = client.post(new JSONObject(request).toString(), Config.NIB_ACCLOOKUP_ENDPOINT_URL, loginResponse.getToken());
                
                System.out.println("=========ACOUNT LOOKUP RESPONSE==========");
                System.out.println(responseData);
                logger.info(responseData);
                
                if (responseData != null) {
                    JSONObject validateAccObject = new JSONObject(responseData);
                    
                    int responseCode = validateAccObject.getInt("response_code");
                    String responseMsg = validateAccObject.getString("response_msg");
                    
                    if (responseCode == 1 && responseMsg.equalsIgnoreCase("SUCCESSFUL")) {
                        
                        if (validateAccObject.getString("currency").trim().equalsIgnoreCase("GHS")) {
                            String customerName = validateAccObject.getString("customername");
                            String phoneNo = validateAccObject.getString("phoneno");
                            String currency = validateAccObject.getString("currency");
                            accountValidateFormat = phoneNo + "|" + customerName + "|" + account.substring(3) + "|" + currency;
                            
                            aplResp.setResponseCode("00");
                            aplResp.setLookup(accountValidateFormat);
                            logger.info("ACCOUNT DETAILS:: " + accountValidateFormat);
                            aplResp.setMessage("Got Data!!");
                        } else {
                            
                            aplResp.setResponseCode("06");
                            aplResp.setLookup("CURRENCYNOTGHS");
                            aplResp.setMessage("Got Data but account not in cedis!!");
                            
                        }
                        
                    } else {
                        
                        aplResp.setResponseCode("06");
                        aplResp.setLookup(responseMsg);
                        aplResp.setMessage("Didn't get Data!!");
                    }
                } else {
                    
                    aplResp.setResponseCode("06");
                    aplResp.setLookup("Response null");
                    aplResp.setMessage("Didn't get Data!!");
                }
                
            } else {
                return "Get token response is null";
            }
        } catch (JSONException e) {
            logger.info("Exception >>>", e);
        }
        String json = new Gson().toJson(aplResp);
        System.out.println("E_CARD:: " + json);
        return json;
    }
    
    public TokenGenerationResponse getToken(String AuthEndPointURL, String username, String password, String grant_type) {
        TokenGenerationResponse tokenGenResp = new TokenGenerationResponse();
        
        String expiry = getPropertyValue("TOKEN_EXPIRY");
        String token = getPropertyValue("TOKEN");
        HttpClient client = new HttpClient();
        
        Boolean tokenExpired;
        
        try {
            DateFormat df = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss Z");
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.MINUTE, 2);
            Date checkTime = df.parse(df.format(c.getTime()));
            tokenExpired = checkTime.after(df.parse(expiry));
            
            System.out.println("tokenExpired:: " + tokenExpired);
            
        } catch (ParseException es) {
            logger.error(es.getMessage());
            tokenExpired = true;
        }
        if (expiry == null || token == null || tokenExpired) {
            
            try {
                String authData = client.postURLEncoded(AuthEndPointURL, username, password, grant_type);
                System.out.println("AUTH RESPONSE >>> " + authData);
                logger.info((Object) ("AUTH RESPONSE >>> " + authData));
                
                JSONObject jsonObj = new JSONObject(authData);
                
                tokenGenResp.setExpiration(jsonObj.optString(".expires"));
                tokenGenResp.setToken(jsonObj.optString("access_token"));
                tokenGenResp.setTokenType(jsonObj.optString("token_type"));
                tokenGenResp.setMsg("New Token");
                
                logger.info((Object) ("New Token"));
                System.out.println("New Token");
                
                setPropertyValue("TOKEN_EXPIRY", tokenGenResp.getExpiration().replace(",", ""));
                setPropertyValue("TOKEN", tokenGenResp.getToken());
                
            } catch (IOException | JSONException ex) {
                logger.info((Object) ("TOKEN EXCEPTION=>" + ex.getMessage()));
            }
        } else {
            System.out.println("Old Token");
            logger.info((Object) ("Old Token"));
            
            tokenGenResp.setToken(token);
            tokenGenResp.setMsg("Old Token");
            
        }
        return tokenGenResp;
    }
    
    public String errorCode(String OSBErrCode) {
        String errorCode = "96";
        
        switch (OSBErrCode) {
            case "Refer to card issuer":
                errorCode = "01";
                break;
            
            case "Refer to card issuer, special condition":
                errorCode = "02";
                break;
            
            case "OSB-90005":
                errorCode = "03";
                break;
            
            case "ST-OTHR-001":
                errorCode = "96";
                break;
            case "Pick-up card":
                errorCode = "05";
                break;
            case "Do not honor":
                errorCode = "05";
                break;
            case "Error":
                errorCode = "06";
                break;
            case "Pick-up card, special condition":
                errorCode = "07";
                break;
            case "Honor with identification":
                errorCode = "08";
                break;
            case "Request in progress":
                errorCode = "09";
                break;
            case "Approved, partial":
                errorCode = "10";
                break;
            case "Approved, VIP":
                errorCode = "11";
                break;
            case "Invalid transaction":
                errorCode = "12";
                break;
            case "ST-VALS-012":
                errorCode = "14";
                break;
            
            case "Invalid card number":
                errorCode = "14";
                break;
            case "No such issuer":
                errorCode = "15";
                break;
            
            case "Approved, update track 3":
                errorCode = "16";
                break;
            
            case "Customer cancellation":
                errorCode = "17";
                break;
            
            case "Customer dispute":
                errorCode = "18";
                break;
            
            case "Re-enter transaction":
                errorCode = "19";
                break;
            
            case "Invalid response":
                errorCode = "20";
                break;
            
            case "No action taken":
                errorCode = "21";
                break;
            case "Suspected malfunction":
                errorCode = "22";
                break;
            
            case "Unacceptable transaction fee":
                errorCode = "23";
                break;
            
            case "File update not supported":
                errorCode = "24";
                break;
            
            case "Unable to locate record":
                errorCode = "25";
                break;
            
            case "Duplicate record":
                errorCode = "26";
                break;
            
            case "AC-OVD01":
                errorCode = "51";
                break;
            case "AC-OVD02":
                errorCode = "51";
                break;
            case "AC-OVD05":
                errorCode = "51";
                break;
            
            case "Invalid Account":
                errorCode = "14";
                break;
            
            case "Issuer or switch inoperative":
                errorCode = "91";
                break;
            
            case "Duplicate transaction":
                errorCode = "94";
                break;
            
            case "Exceeds cash limit":
                errorCode = "98";
                break;
            
            case "System malfunction":
                errorCode = "96";
                break;
            
            case "Violation of law":
                errorCode = "93";
                break;
            
            case "Cut-off in progress":
                errorCode = "90";
                break;
            
            case "PIN tries exceeded":
                errorCode = "75";
                break;
            
            case "Reconcile error":
                errorCode = "95";
                break;
            
            case "Security violation":
                errorCode = "63";
                break;
            
            case "Suspected fraud":
                errorCode = "59";
                break;
            
            case "Success":
                errorCode = "00";
                break;
            
            default:
            
        }
        
        return errorCode;
    }
    
    public TokenGenerationResponse getTokenGCB(String AuthEndPointURL, String username, String password, String grant_type) throws Exception {
        TokenGenerationResponse tokenGenResp = new TokenGenerationResponse();
        
        String accessToken = Config.getValue("token");
        String expiry = Config.getValue("tokenExpiration");
        
        Boolean tokenExpired;
        try {
            DateFormat df = new SimpleDateFormat("dd MMM yyyy hh:mm:ss S");
            Date checkTime = df.parse(df.format(new Date()));
            tokenExpired = checkTime.after(df.parse(expiry));
            
        } catch (ParseException es) {
            logger.error(es.getMessage());
            tokenExpired = true;
        }
        if (expiry == null || accessToken == null || tokenExpired) {
            TokenRquest tokenReq = new TokenRquest();
            tokenReq.setGrantType(grant_type);
            tokenReq.setUserName(username);
            tokenReq.setPassword(password);
            
            try {
                Map<String, String> rspMap = new HashMap<>();
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("userName", username));
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("grant_type", grant_type));
                
                rspMap = SuperHttpClient.doPostMultiPart(AuthEndPointURL + "token", params);
                
                if (rspMap != null) {
                    
                    if (rspMap.get("code").equals("200")) {
                        JSONObject json = new JSONObject(rspMap.get("body"));
                        
                        System.out.println("RESPONSE>>> " + json);
                        expiry = json.optString("expires_in", "");
                        accessToken = json.optString("access_token", "");
                        tokenGenResp.setAccessToken(json.getString("access_token"));
                        tokenGenResp.setTokenTypeTwo(json.getString("token_type"));
                    }
                    
                }
                
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss S");
                calendar.add(Calendar.MILLISECOND, Integer.parseInt(expiry));
                Date addMilliSeconds = calendar.getTime();
                
                System.out.println("Using New Token");
                Config.setValue(tokenGenResp.getAccessToken(), sdf.format(addMilliSeconds));
                
            } catch (JSONException ex) {
                logger.info((Object) ("TOKEN EXCEPTION=>" + ex.getMessage()));
            }
        } else {
            logger.info((Object) ("Old Token"));
            tokenGenResp.setAccessToken(accessToken);
            
        }
        
        return tokenGenResp;
    }
    
    public String gaRuralALService(String account) {
        ResponseMsg aplResp = new ResponseMsg();
        HttpClient client = new HttpClient();
        String lookup;
        JSONObject obj = new JSONObject();
        String rsp;
        
        try {
            
            obj.put("account_number", account);
            obj.put("function_type", "GET_ACCOUNTS_BY_ACCT");
            obj.put("app_username", Config.GARURAL_USERNAME);
            obj.put("app_password", Config.GARURAL_PASSWORD);
            
            System.out.println("REQUEST SENT>>> " + obj.toString().replace(Config.GARURAL_PASSWORD, "*******"));
            
            rsp = client.postGaRural(Config.GARURAL_LOOKUP_ENDPOINT, obj.toString());
            
            System.out.println("RESPONSE RECEIVED:: " + rsp);
            
            JSONObject getAccByAccObj = new JSONObject(rsp);
            
            if (getAccByAccObj.getString("response_code").equals("0") && getAccByAccObj.getString("response_message").equalsIgnoreCase("Approved or completed successfully.")) {
                
                lookup = getAccByAccObj.getString("ph_no") + "|" + getAccByAccObj.getString("name").trim() + "|" + account;
                
                aplResp.setResponseCode("00");
                aplResp.setLookup(lookup);
                
            } else {
                aplResp.setResponseCode("06");
                aplResp.setMessage(getAccByAccObj.getString("response_message"));
                aplResp.setLookup(getAccByAccObj.getString("response_message"));
            }
            
        } catch (JSONException e) {
            logger.error("Exception>>> " + e.getMessage());
            aplResp.setResponseCode("99");
            aplResp.setMessage(e.getMessage());
        }
        
        System.out.println("GA RURAL RESPONSE>>> " + new JSONObject(aplResp).toString());
        return new JSONObject(aplResp).toString();
        
    }
    
    public String talentService(String account) {
        ResponseMsg aplResp = new ResponseMsg();
        HttpClient client = new HttpClient();
        String lookup;
        String numb = new DecimalFormat("000000").format(new Random().nextInt(999999));
        JSONObject obj = new JSONObject();
        String rsp;
        String[] message;
        
        try {
            
            obj.put("Transid", numb);
            obj.put("Transcode", "09");
            obj.put("Devkey", Config.TALENT_DEVKEY);
            obj.put("acctno", account);
            
            System.out.println("REQUEST SENT>>> " + obj.toString().replace(Config.TALENT_DEVKEY, "*******"));
            
            rsp = client.postGaRural(Config.TALENT_ENDPOINT, obj.toString());
            
            System.out.println("RESPONSE RECEIVED:: " + rsp);
            
            JSONObject getAccByAccObj = new JSONObject(rsp);
            
            if (getAccByAccObj.getString("Responsecode").equals("00")) {
                message = getAccByAccObj.getString("Message").split(",");
                String name = message[0] + " " + message[1];
                String phoNo = message[8];
                lookup = phoNo + "|" + name + "|" + account;
                
                aplResp.setMessage("SUCCESSFUL");
                aplResp.setResponseCode("00");
                aplResp.setLookup(lookup);
                
            } else {
                aplResp.setResponseCode("06");
                aplResp.setMessage(getAccByAccObj.getString("response_message"));
                aplResp.setLookup(getAccByAccObj.getString("response_message"));
            }
            
        } catch (JSONException e) {
            logger.error("Exception>>> " + e.getMessage());
            aplResp.setResponseCode("99");
            aplResp.setMessage(e.getMessage());
        }
        
        System.out.println("TALENT MICROFINANCE RESPONSE>>> " + new JSONObject(aplResp).toString());
        return new JSONObject(aplResp).toString();
        
    }
    
}
