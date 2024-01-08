/*
 * name: full name of customer
 * error: 0:successful, others failure
 * description: error description
 */
package com.etz.gh.kyc.model;

/**
 *
 * @author Seth Sebeh-Kusi
 */
public class KYCResponse {
    private String error;
    private String name;
    private String description;
    private String qrCode;
    private String amount;
    private String terminalId;
    private String etzMerchant;
    
    
    public KYCResponse() {}

    public KYCResponse(String error, String name, String qrCode, String amount, String terminalId, String etzMerchant) {
        this.error = error;
        this.name = name;
        this.qrCode = qrCode;
        this.amount = amount;
        this.terminalId = terminalId;
        this.etzMerchant = etzMerchant;     
        
    }
    
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getEtzMerchant() {
        return etzMerchant;
    }

    public void setEtzMerchant(String etzMerchant) {
        this.etzMerchant = etzMerchant;
    }

    
    
    
}
