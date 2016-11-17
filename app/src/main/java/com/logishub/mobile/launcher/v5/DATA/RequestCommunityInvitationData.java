package com.logishub.mobile.launcher.v5.DATA;

import com.google.gson.annotations.SerializedName;

public class RequestCommunityInvitationData {
    public String getOrgID() {
        return orgID;
    }

    public void setOrgID(String orgID) {
        this.orgID = orgID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getTransOid() {
        return transOid;
    }

    public void setTransOid(String transOid) {
        this.transOid = transOid;
    }

    public String getUserToOrgLinkOid() {
        return userToOrgLinkOid;
    }

    public void setUserToOrgLinkOid(String userToOrgLinkOid) {
        this.userToOrgLinkOid = userToOrgLinkOid;
    }

    @SerializedName("orgID")
    private String orgID;

    @SerializedName("status")
    private String status;

    @SerializedName("serviceUrl")
    private String serviceUrl;

    @SerializedName("siteCode")
    private String siteCode;

    @SerializedName("transOid")
    private String transOid;

    @SerializedName("userToOrgLinkOid")
    private String userToOrgLinkOid;
}