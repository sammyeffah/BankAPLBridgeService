/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.etz.gh.kyc.model;

/**
 *
 * @author samuel.onwona
 */
public class AccountValidateRequest {

    private String clientid;
    private String apikey;
    private String accountno;
    private String clientcode;

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getAccountno() {
        return accountno;
    }

    public void setAccountno(String accountno) {
        this.accountno = accountno;
    }

    public String getClientcode() {
        return clientcode;
    }

    public void setClientcode(String clientcode) {
        this.clientcode = clientcode;
    }
    
    

}
