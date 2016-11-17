package com.logishub.mobile.launcher.v5.DATA;

import java.io.Serializable;

public class CommunityListData implements Serializable
{
    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCommunityLevel() {
        return communityLevel;
    }

    public void setCommunityLevel(String communityLevel) {
        this.communityLevel = communityLevel;
    }

    public String getiLParentOrganization() {
        return iLParentOrganization;
    }

    public void setiLParentOrganization(String iLParentOrganization) {
        this.iLParentOrganization = iLParentOrganization;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLeaderOid() {
        return leaderOid;
    }

    public void setLeaderOid(String leaderOid) {
        this.leaderOid = leaderOid;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }

    public String getNewArticleCount() {
        return newArticleCount;
    }

    public void setNewArticleCount(String newArticleCount) {
        this.newArticleCount = newArticleCount;
    }

    public String getLastArticleCreatedTime() {
        return lastArticleCreatedTime;
    }

    public void setLastArticleCreatedTime(String lastArticleCreatedTime) {
        this.lastArticleCreatedTime = lastArticleCreatedTime;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getUseSubOrganization() {
        return useSubOrganization;
    }

    public void setUseSubOrganization(String useSubOrganization) {
        this.useSubOrganization = useSubOrganization;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    private String oid = "";
    private String communityName = "";
    private String communityLevel = "";
    private String iLParentOrganization = "";
    private String communityId = "";
    private String serviceUrl = "";
    private String site = "";
    private String created = "";
    private String leaderOid = "";
    private String leader = "";
    private String memberCount = "";
    private String newArticleCount = "";
    private String lastArticleCreatedTime = "";
    private String isDefault = "";
    private String useSubOrganization = "";
    private String serviceType = "";
}
