package com.logishub.mobile.launcher.v5.DATA;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResponseUserRegisterData {

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(String belongTo) {
        this.belongTo = belongTo;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAuthNumber() {
        return authNumber;
    }

    public void setAuthNumber(String authNumber) {
        this.authNumber = authNumber;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getCargillUser() {
        return cargillUser;
    }

    public void setCargillUser(String cargillUser) {
        this.cargillUser = cargillUser;
    }

    public String getNewUser() {
        return newUser;
    }

    public void setNewUser(String newUser) {
        this.newUser = newUser;
    }

    public List<MenuData> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<MenuData> menuList) {
        this.menuList = menuList;
    }

    public List<ServiceData> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<ServiceData> serviceList) {
        this.serviceList = serviceList;
    }

    public List<CommunityListData> getCommunityList() {
        return communityList;
    }

    public void setCommunityList(List<CommunityListData> communityList) {
        this.communityList = communityList;
    }

    public List<MessageListData> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<MessageListData> messageList) {
        this.messageList = messageList;
    }

    @SerializedName("oid")
    private String oid;

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("password")
    private String password;

    @SerializedName("mobilePhone")
    private String mobilePhone;

    @SerializedName("belongTo")
    private String belongTo;

    @SerializedName("key")
    private String key;

    @SerializedName("authNumber")
    private String authNumber;

    @SerializedName("appVersion")
    private String appVersion;

    @SerializedName("cargillUser")
    private String cargillUser;

    @SerializedName("newUser")
    private String newUser;

    @SerializedName("menuList")
    private List<MenuData> menuList = new ArrayList<MenuData>();

    @SerializedName("orgList")
    private List<ServiceData> serviceList = new ArrayList<ServiceData>();

    @SerializedName("commList")
    private List<CommunityListData> communityList = new ArrayList<CommunityListData>();

    @SerializedName("messageList")
    private List<MessageListData> messageList = new ArrayList<MessageListData>();

    @SerializedName("errorCode")
    private String errorCode;

    @SerializedName("errorContent")
    private String errorContent;
}