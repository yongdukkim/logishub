package com.logishub.mobile.launcher.v5.DATA;

import android.os.Parcel;
import android.os.Parcelable;

public class LoginSessionData implements Parcelable, Cloneable {
    private String sLoginType = "";
    private String sLoginUserID = "";
    private String sLoginUserLocalPassword = "";
    private String sLoginUserPassword = "";
    private String sLoginUserName = "";
    private String sLoginUserImageUrl = "";
    private String sLoginUserDeviceToken = "";
    private String sLoginUserPhoneNumber = "";

    //생성자
    public LoginSessionData(){}

    @SuppressWarnings({ "rawtypes" })
    public static final Parcelable.Creator CREATOR	= new Parcelable.Creator()
    {
        public LoginSessionData createFromParcel(Parcel in)
        {
            return new LoginSessionData(in);
        }

        public LoginSessionData[] newArray(int size)
        {
            return new LoginSessionData[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    // Parcel객체를 파라미터로 받는 생성자도 작성해주어야 한다
    LoginSessionData(Parcel src)
    {
        this.sLoginType = src.readString();
        this.sLoginUserID = src.readString();
        this.sLoginUserLocalPassword = src.readString();
        this.sLoginUserPassword = src.readString();
        this.sLoginUserName = src.readString();
        this.sLoginUserImageUrl = src.readString();
        this.sLoginUserDeviceToken = src.readString();
        this.sLoginUserPhoneNumber = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.sLoginType);
        dest.writeString(this.sLoginUserID);
        dest.writeString(this.sLoginUserLocalPassword);
        dest.writeString(this.sLoginUserPassword);
        dest.writeString(this.sLoginUserName);
        dest.writeString(this.sLoginUserImageUrl);
        dest.writeString(this.sLoginUserDeviceToken);
        dest.writeString(this.sLoginUserPhoneNumber);
    }

    public void setLoginType(String sVal)
    {
        this.sLoginType = sVal;
    }

    public void setLoginUserID(String sVal)
    {
        this.sLoginUserID = sVal;
    }

    public void setLoginUserLocalPassword(String sVal)
    {
        this.sLoginUserLocalPassword = sVal;
    }

    public void setLoginUserPassword(String sVal)
    {
        this.sLoginUserPassword = sVal;
    }

    public void setLoginUserName(String sVal)
    {
        this.sLoginUserName = sVal;
    }

    public void setLoginUserImageUrl(String sVal)
    {
        this.sLoginUserImageUrl = sVal;
    }

    public void setLoginUserDeviceToken(String sVal) {
        this.sLoginUserDeviceToken = sVal;
    }

    public void setLoginUserPhoneNumber(String sVal) {
        this.sLoginUserPhoneNumber = sVal;
    }

    public String getLoginType()
    {
        return this.sLoginType;
    }

    public String getLoginUserID()
    {
        return this.sLoginUserID;
    }

    public String getLoginUserLocalPassword()
    {
        return this.sLoginUserLocalPassword;
    }

    public String getLoginUserPassword()
    {
        return this.sLoginUserPassword;
    }

    public String getLoginUserName()
    {
        return this.sLoginUserName;
    }

    public String getLoginUserImageUrl()
    {
        return this.sLoginUserImageUrl;
    }

    public String getLoginUserDeviceToken()
    {
        return this.sLoginUserDeviceToken;
    }

    public String getLoginUserPhoneNumber()
    {
        return this.sLoginUserPhoneNumber;
    }

    //객체복사
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }



}