package com.logishub.mobile.launcher.v5.DATA;

import java.io.Serializable;

public class UserData implements Serializable {

   public void setUserOid(String sVal)
    {
        this.UserOid = sVal;
    }

    public void setUserID(String sVal)
    {
        this.UserID = sVal;
    }

    public void setUserPassWord(String sVal)
    {
        this.UserPassWord = sVal;
    }

    public void setUserName(String sVal)
    {
        this.UserName = sVal;
    }

    public void setUserMobilePhone(String sVal)
    {
        this.UserMobilePhone = sVal;
    }

    public String getUserOid() {
        return this.UserOid;
    }

    public String getUserID()
    {
        return this.UserID;
    }

    public String getUserPassWord()
    {
        return this.UserPassWord;
    }

    public String getUserName()
    {
        return this.UserName;
    }

    public String getUserMobilePhone()
    {
        return this.UserMobilePhone;
    }

    public String getUserKey() {
        return UserKey;
    }

    public void setUserKey(String userKey) {
        UserKey = userKey;
    }

    public String getUserBelongTo() {
        return UserBelongTo;
    }

    public void setUserBelongTo(String userBelongTo) {
        UserBelongTo = userBelongTo;
    }

    public String getUserCagill() {
        return UserCagill;
    }

    public void setUserCagill(String userCagill) {
        UserCagill = userCagill;
    }

    private String UserOid = "";
    private String UserID = "";
    private String UserName = "";
    private String UserPassWord = "";
    private String UserMobilePhone = "";
    private String UserKey = "";
    private String UserBelongTo = "";
    private String UserCagill = "";
}
