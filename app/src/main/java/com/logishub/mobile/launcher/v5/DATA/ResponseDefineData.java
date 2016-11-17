package com.logishub.mobile.launcher.v5.DATA;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResponseDefineData {

    public List<DefineListData> getDefineList() {
        return defineList;
    }

    public void setDefineList(List<DefineListData> defineList) {
        this.defineList = defineList;
    }

    public List<CommunityListData> getCommunityList() {
        return communityList;
    }

    public void setCommunityList(List<CommunityListData> communityList) {
        this.communityList = communityList;
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

    @SerializedName("defineList")
    private List<DefineListData> defineList = new ArrayList<DefineListData>();

    @SerializedName("communityList")
    private List<CommunityListData> communityList = new ArrayList<CommunityListData>();

    @SerializedName("errorCode")
    private String errorCode;

    @SerializedName("errorContent")
    private String errorContent;
}