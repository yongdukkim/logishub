package com.logishub.mobile.launcher.v5.DATA;

import android.os.Parcel;
import android.os.Parcelable;

public class ConfigData implements Parcelable, Cloneable {
    private String sPushConfig = "Y";
    private String sGpsConfig = "Y";

    public ConfigData(){}

    public static final Parcelable.Creator CREATOR	= new Parcelable.Creator()
    {
        public ConfigData createFromParcel(Parcel in)
        {
            return new ConfigData(in);
        }

        public ConfigData[] newArray(int size)
        {
            return new ConfigData[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    ConfigData(Parcel src)
    {
        this.sPushConfig = src.readString();
        this.sGpsConfig = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.sPushConfig);
        dest.writeString(this.sGpsConfig);
    }

    public void setPushConfig(String sVal)
    {
        this.sPushConfig = sVal;
    }

    public void setGpsConfig(String sVal)
    {
        this.sGpsConfig = sVal;
    }

    public String getPushConfig()
    {
        return this.sPushConfig;
    }

    public String getGpsConfig()
    {
        return this.sGpsConfig;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}