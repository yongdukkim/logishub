package com.logishub.mobile.launcher.v5.DATA;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResponseFreightData {

    public List<FnsData> getFnsList() {
        return fnsList;
    }

    public void setFnsList(List<FnsData> fnsList) {
        this.fnsList = fnsList;
    }

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

    @SerializedName("fnsList")
    private List<FnsData> fnsList = new ArrayList<FnsData>();

    @SerializedName("errorCode")
    private String errorCode;

    @SerializedName("errorContent")
    private String errorContent;
}