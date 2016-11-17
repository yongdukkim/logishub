package com.logishub.mobile.launcher.v5.DATA;

import java.util.List;

/**
 * Created by SPICE on 2016-10-26.
 */

public class RequestIntegrationFNSData {
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPortalKey() {
        return userPortalKey;
    }

    public void setUserPortalKey(String userPortalKey) {
        this.userPortalKey = userPortalKey;
    }

    public String getOrgOid() {
        return orgOid;
    }

    public void setOrgOid(String orgOid) {
        this.orgOid = orgOid;
    }

    public String getTonCode() {
        return tonCode;
    }

    public void setTonCode(String tonCode) {
        this.tonCode = tonCode;
    }

    public String getAddYN() {
        return addYN;
    }

    public void setAddYN(String addYN) {
        this.addYN = addYN;
    }

    public List<DefineListData> getDefineTypeList() {
        return defineTypeList;
    }

    public void setDefineTypeList(List<DefineListData> defineTypeList) {
        this.defineTypeList = defineTypeList;
    }

    private String userID;
    private String userPortalKey;
    private String orgOid;
    private String tonCode;
    private String addYN;
    private List<DefineListData> defineTypeList;
}
