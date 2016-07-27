package com.neosystems.logishubmobile50.DATA;

import android.os.Parcel;
import android.os.Parcelable;

public class LoginSessionData implements Parcelable, Cloneable {
    private String sLoginType = "";
    private String sLoginUserID = "";
    private String sLoginUserName = "";
    private String sLoginUserImageUrl = "";

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
        this.sLoginUserName = src.readString();
        this.sLoginUserImageUrl = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.sLoginType);
        dest.writeString(this.sLoginUserID);
        dest.writeString(this.sLoginUserName);
        dest.writeString(this.sLoginUserImageUrl);
    }

    public void SetLoginType(String sVal)
    {
        this.sLoginType = sVal;
    }

    public void SetLoginUserID(String sVal)
    {
        this.sLoginUserID = sVal;
    }

    public void SetLoginUserName(String sVal)
    {
        this.sLoginUserName = sVal;
    }

    public void SetLoginUserImageUrl(String sVal)
    {
        this.sLoginUserImageUrl = sVal;
    }

    public String GetLoginType()
    {
        return this.sLoginType;
    }

    public String GetLoginUserID()
    {
        return this.sLoginUserID;
    }

    public String GetLoginUserName()
    {
        return this.sLoginUserName;
    }

    public String GetLoginUserImageUrl()
    {
        return this.sLoginUserImageUrl;
    }

    //객체복사
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }



}