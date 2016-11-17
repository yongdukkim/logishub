package com.logishub.mobile.launcher.v5.DATA;

import java.io.Serializable;

public class DefineListData implements Serializable
{
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String type = "";
    private String code = "";
    private String value = "";
}