package com.logishub.mobile.launcher.v5.DATA;

import java.util.List;

/**
 * Created by SPICE on 2016-10-26.
 */

public class ResponseIntegrationFNSData {
    public List<IntegrationFNSListData> getIntegrationFNSList() {
        return integrationFNSList;
    }

    public void setIntegrationFNSList(List<IntegrationFNSListData> integrationFNSList) {
        this.integrationFNSList = integrationFNSList;
    }

    public List<CommunityListData> getCommunityList() {
        return communityList;
    }

    public void setCommunityList(List<CommunityListData> communityList) {
        this.communityList = communityList;
    }

    public List<DefineListData> getDefineList() {
        return defineList;
    }

    public void setDefineList(List<DefineListData> defineList) {
        this.defineList = defineList;
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

    private List<IntegrationFNSListData> integrationFNSList;
    public List<CommunityListData> communityList;
    public List<DefineListData> defineList;
    private String errorCode;
    private String errorContent;
}
