/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.etz.gh.kyc.processor;

/**
 *
 * @author samuel.onwona
 */
public class ResponseMsg {

    private String statusId;
    private String txnRefId;
    private String origAcc;
    private String origBalance;
    private String statusMsg;
    private long tat;
    private String responseCode;
    private String lookup;
    private String message;

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getTxnRefId() {
        return txnRefId;
    }

    public void setTxnRefId(String txnRefId) {
        this.txnRefId = txnRefId;
    }

    public String getOrigAcc() {
        return origAcc;
    }

    public void setOrigAcc(String origAcc) {
        this.origAcc = origAcc;
    }

    public String getOrigBalance() {
        return origBalance;
    }

    public void setOrigBalance(String origBalance) {
        this.origBalance = origBalance;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public long getTat() {
        return tat;
    }

    public void setTat(long tat) {
        this.tat = tat;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

  
}
