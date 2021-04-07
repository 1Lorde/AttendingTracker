package com.savchuk.app.models;

public enum Access{
    None("none"), Admin("admin"), Scanner("scanner");
    private String code;
    Access(String code){
        this.code = code;
    }
    public String getCode(){ return code;}
}
