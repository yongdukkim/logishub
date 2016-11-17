package com.logishub.mobile.launcher.v5.DATA;

import com.google.gson.annotations.SerializedName;

public class ResponseFreightRegisterData {

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorContent() {
        return errorContent;
    }

    public void setErrorContent(String errorContent) {
        this.errorContent = errorContent;
    }

    @SerializedName("errorCode")
    private String errorCode;

    @SerializedName("errorContent")
    private String errorContent;
}