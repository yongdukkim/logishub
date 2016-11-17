package com.logishub.mobile.launcher.v5.DATA;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RequestDefineData {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserPortalKey() {
        return userPortalKey;
    }

    public void setUserPortalKey(String userPortalKey) {
        this.userPortalKey = userPortalKey;
    }

    public String getUserSiteKey() {
        return userSiteKey;
    }

    public void setUserSiteKey(String userSiteKey) {
        this.userSiteKey = userSiteKey;
    }

    public String getSiteCode() {
        return SiteCode;
    }

    public void setSiteCode(String siteCode) {
        SiteCode = siteCode;
    }

    public String getServiceUrl() {
        return ServiceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        ServiceUrl = serviceUrl;
    }

    public String getIsCommunity() {
        return isCommunity;
    }

    public void setIsCommunity(String isCommunity) {
        this.isCommunity = isCommunity;
    }

    public List<DefineListData> getDefineTypeList() {
        return defineTypeList;
    }

    public void setDefineTypeList(List<DefineListData> defineTypeList) {
        this.defineTypeList = defineTypeList;
    }

    @SerializedName("id")
    private String id;

    @SerializedName("userPortalKey")
    private String userPortalKey;

    @SerializedName("userSiteKey")
    private String userSiteKey;

    @SerializedName("SiteCode")
    private String SiteCode;

    @SerializedName("ServiceUrl")
    private String ServiceUrl;

    @SerializedName("isCommunity")
    private String isCommunity;

    @SerializedName("defineTypeList")
    private List<DefineListData> defineTypeList = new ArrayList<DefineListData>();
}