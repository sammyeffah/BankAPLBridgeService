package com.etz.gh.kyc.processor;

public abstract class Processor {

    public abstract String getAccountProfile(String phone);

    public abstract String getAccountProfileMySQL(String phone);

    public abstract String getAccountFromEcardSybase(String a, String b);

    public abstract String getAccountFromEcard(String a, String b);

    public abstract String getField37FromEHostRespMySQL(String ref);

    public abstract String updateControlIdInEcardSybase(String a, String b);

}
