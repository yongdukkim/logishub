package com.neosystems.logishubmobile50.DATA;

public class UserData implements  Cloneable
{
    private String UserOid = "";
    private String UserID = "";
    private String UserName = "";
    private String UserPassWord = "";
    private String UserMobilePhone = "";

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
}
