package com.neosystems.logishubmobile50.DATA;

public class VehicleOperationData implements  Cloneable
{
    private String sLon = "";
    private String sLat = "";
    private String sAddr = "";

    public void SetLon(String sVal)
    {
        this.sLon = sVal;
    }

    public void SetLat(String sVal)
    {
        this.sLat = sVal;
    }

    public void SetAddr(String sVal)
    {
        this.sAddr = sVal;
    }

    public String GetLon()
    {
        return this.sLon;
    }

    public String GetLat()
    {
        return this.sLat;
    }

    public String GetAddr()
    {
        return this.sAddr;
    }
}
