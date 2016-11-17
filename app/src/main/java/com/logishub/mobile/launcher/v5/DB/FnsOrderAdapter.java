package com.logishub.mobile.launcher.v5.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.logishub.mobile.launcher.v5.DATA.FnsTemplateData;

import java.util.ArrayList;

public class FnsOrderAdapter {
    private static final String KEY_ROWID = "RowOid";
    private static final String KEY_SITECODE = "SiteCode";
    private static final String KEY_ID = "ID";
    private static final String KEY_TransID = "TransID";
    private static final String KEY_Oid = "Oid";
    private static final String KEY_TurnOid = "TurnOid";
    private static final String KEY_DriverUserOid = "DriverUserOid";
    private static final String KEY_Status = "Status";
    private static final String KEY_OrderDate = "OrderDate";
    private static final String KEY_EarlistPickupTime = "EarlistPickupTime";
    private static final String KEY_TransDate = "TransDate";
    private static final String KEY_TimeSpan = "TimeSpan";
    private static final String KEY_TimeSpanText = "TimeSpanText";
    private static final String KEY_IlDeliveryCustomer = "IlDeliveryCustomer";
    private static final String KEY_DeliveryCustomerName = "DeliveryCustomerName";
    private static final String KEY_DeliveryCustomerPhone = "DeliveryCustomerPhone";
    private static final String KEY_IlPickupArea = "IlPickupArea";
    private static final String KEY_PickupAreaName = "PickupAreaName";
    private static final String KEY_PickupTypeCode = "PickupTypeCode";
    private static final String KEY_PickupTypeName = "PickupTypeName";
    private static final String KEY_PickupTime = "PickupTime";
    private static final String KEY_DeliveryTime = "DeliveryTime";
    private static final String KEY_IlArea = "IlArea";
    private static final String KEY_AreaName = "AreaName";
    private static final String KEY_Address = "Address";
    private static final String KEY_DetailAddress = "DetailAddress";
    private static final String KEY_DeliveryTimeTypeCode = "DeliveryTimeTypeCode";
    private static final String KEY_DeliveryTimeTypeName = "DeliveryTimeTypeName";
    private static final String KEY_TonCode = "TonCode";
    private static final String KEY_TonName = "TonName";
    private static final String KEY_ContainerTypeCode = "ContainerTypeCode";
    private static final String KEY_ContainerTypeName = "ContainerTypeName";
    private static final String KEY_Price = "Price";
    private static final String KEY_PaymentType = "PaymentType";
    private static final String KEY_PaymentTypeName = "PaymentTypeName";
    private static final String KEY_ItemKindCode = "ItemKindCode";
    private static final String KEY_ItemKindName = "ItemKindName";
    private static final String KEY_VehicleID = "VehicleID";
    private static final String KEY_LoadingMethod = "LoadingMethod";
    private static final String KEY_LoadingMethodName = "LoadingMethodName";
    private static final String KEY_UnloadingMethod = "UnloadingMethod";
    private static final String KEY_UnloadingMethodName = "UnloadingMethodName";
    private static final String KEY_DeliveryType = "DeliveryType";
    private static final String KEY_DeliveryTypeName = "DeliveryTypeName";
    private static final String KEY_WidthText = "WidthText";
    private static final String KEY_LengthText = "LengthText";
    private static final String KEY_HeightText = "HeightText";
    private static final String KEY_Weight = "Weight";
    private static final String KEY_CustomOrderType = "CustomOrderType";
    private static final String KEY_CustomOrderTypeName = "CustomOrderTypeName";
    private static final String KEY_Remark = "Remark";
    private static final String KEY_RequestedPickupDate = "RequestedPickupDate";
    private static final String KEY_RequestedDeliveryDate = "RequestedDeliveryDate";
    private static final String KEY_OrderTime = "OrderTime";
    private static final String KEY_DriverName = "DriverName";
    private static final String KEY_DriverMobilePhone = "DriverMobilePhone";
    private static final String KEY_StatusName = "StatusName";
    private static final String KEY_Charged = "Charged";
    private static final String KEY_ChargedText = "ChargedText";
    private static final String KEY_UserName = "UserName";
    private static final String KEY_UserMobilePhone = "UserMobilePhone";
    private static final String KEY_OwnerName = "OwnerName";
    private static final String KEY_OwnerPhone = "OwnerPhone";
    private static final String KEY_VehicleNo = "VehicleNo";

    private final String DATABASE_NAME = "FnsOrderData";
    private final String DATABASE_TABLE = "TB_FnsOrderData";
    private final int DATABASE_VERSION = 1;
    private final Context m_Context;
    private DatabaseHelper m_DbHelper;
    private SQLiteDatabase m_Db;

    private static final String DATABASE_CREATE =
            "create table TB_FnsOrderData (RowOid integer primary key autoincrement, "+
                    "SiteCode text null,"+
                    "ID text null,"+
                    "TransID text null," +
                    "Oid text null," +
                    "TurnOid text null," +
                    "DriverUserOid text null," +
                    "Status text null," +
                    "OrderDate text null," +
                    "EarlistPickupTime text null," +
                    "TransDate text null," +
                    "TimeSpan text null," +
                    "TimeSpanText text null," +
                    "IlDeliveryCustomer text null," +
                    "DeliveryCustomerName text null," +
                    "DeliveryCustomerPhone text null," +
                    "IlPickupArea text null," +
                    "PickupAreaName text null," +
                    "PickupTypeCode text null," +
                    "PickupTypeName text null," +
                    "PickupTime text null," +
                    "DeliveryTime text null," +
                    "IlArea text null," +
                    "AreaName text null," +
                    "Address text null," +
                    "DetailAddress text null," +
                    "DeliveryTimeTypeCode text null," +
                    "DeliveryTimeTypeName text null," +
                    "TonCode text null," +
                    "TonName text null," +
                    "ContainerTypeCode text null," +
                    "ContainerTypeName text null," +
                    "Price text null," +
                    "PaymentType text null," +
                    "PaymentTypeName text null," +
                    "ItemKindCode text null," +
                    "ItemKindName text null," +
                    "VehicleID text null," +
                    "LoadingMethod text null," +
                    "LoadingMethodName text null," +
                    "UnloadingMethod text null," +
                    "UnloadingMethodName text null," +
                    "DeliveryType text null," +
                    "DeliveryTypeName text null," +
                    "WidthText text null," +
                    "LengthText text null," +
                    "HeightText text null," +
                    "Weight text null," +
                    "CustomOrderType text null," +
                    "CustomOrderTypeName text null," +
                    "Remark text null," +
                    "RequestedPickupDate text null," +
                    "RequestedDeliveryDate text null," +
                    "OrderTime text null," +
                    "DriverName text null," +
                    "DriverMobilePhone text null," +
                    "StatusName text null," +
                    "Charged text null," +
                    "ChargedText text null," +
                    "UserName text null," +
                    "UserMobilePhone text null," +
                    "OwnerName text null," +
                    "OwnerPhone text null," +
                    "VehicleNo text null" +
                    ");";

    public FnsOrderAdapter(Context ctx)
    {
        this.m_Context = ctx;
    }

    public FnsOrderAdapter open() throws SQLException {
        m_DbHelper = new DatabaseHelper(m_Context);
        m_Db = m_DbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        m_DbHelper.close();
    }

    /** SQLLite Helper Class */
    private class DatabaseHelper extends SQLiteOpenHelper
    {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +   DATABASE_TABLE);
            onCreate(db);
        }
    }

    /** SELECT */
    public FnsTemplateData GetTemplateData(String rowOid) throws SQLException
    {
        FnsTemplateData data = new FnsTemplateData();

        Cursor cs = m_Db.query(true, DATABASE_TABLE, new String[] {
                KEY_ROWID,
                KEY_SITECODE,
                KEY_ID,
                KEY_TransID,
                KEY_Oid,
                KEY_TurnOid,
                KEY_DriverUserOid,
                KEY_Status,
                KEY_OrderDate,
                KEY_EarlistPickupTime,
                KEY_TransDate,
                KEY_TimeSpan,
                KEY_TimeSpanText,
                KEY_IlDeliveryCustomer,
                KEY_DeliveryCustomerName,
                KEY_DeliveryCustomerPhone,
                KEY_IlPickupArea,
                KEY_PickupAreaName,
                KEY_PickupTypeCode,
                KEY_PickupTypeName,
                KEY_PickupTime,
                KEY_DeliveryTime,
                KEY_IlArea,
                KEY_AreaName,
                KEY_Address,
                KEY_DetailAddress,
                KEY_DeliveryTimeTypeCode,
                KEY_DeliveryTimeTypeName,
                KEY_TonCode,
                KEY_TonName,
                KEY_ContainerTypeCode,
                KEY_ContainerTypeName,
                KEY_Price,
                KEY_PaymentType,
                KEY_PaymentTypeName,
                KEY_ItemKindCode,
                KEY_ItemKindName,
                KEY_VehicleID,
                KEY_LoadingMethod,
                KEY_LoadingMethodName,
                KEY_UnloadingMethod,
                KEY_UnloadingMethodName,
                KEY_DeliveryType,
                KEY_DeliveryTypeName,
                KEY_WidthText,
                KEY_LengthText,
                KEY_HeightText,
                KEY_Weight,
                KEY_CustomOrderType,
                KEY_CustomOrderTypeName,
                KEY_Remark,
                KEY_RequestedPickupDate,
                KEY_RequestedDeliveryDate,
                KEY_OrderTime,
                KEY_DriverName,
                KEY_DriverMobilePhone,
                KEY_StatusName,
                KEY_Charged,
                KEY_ChargedText,
                KEY_UserName,
                KEY_UserMobilePhone,
                KEY_OwnerName,
                KEY_OwnerPhone,
                KEY_VehicleNo}, KEY_ROWID + "=" + rowOid, null, null, null, null, null);

        if(cs.moveToFirst())
        {
            do{

                data = SetCursorData(cs);

            }while(cs.moveToNext());
        }

        cs.close();

        return data;
    }

    public ArrayList<FnsTemplateData> GetFnsTemplateData(String mSiteCode) throws SQLException
    {
        ArrayList<FnsTemplateData> fnsTemplateDatas = new ArrayList<>();

        Cursor cs = m_Db.query(true, DATABASE_TABLE, new String[] {
                KEY_ROWID,
                KEY_SITECODE,
                KEY_ID,
                KEY_TransID,
                KEY_Oid,
                KEY_TurnOid,
                KEY_DriverUserOid,
                KEY_Status,
                KEY_OrderDate,
                KEY_EarlistPickupTime,
                KEY_TransDate,
                KEY_TimeSpan,
                KEY_TimeSpanText,
                KEY_IlDeliveryCustomer,
                KEY_DeliveryCustomerName,
                KEY_DeliveryCustomerPhone,
                KEY_IlPickupArea,
                KEY_PickupAreaName,
                KEY_PickupTypeCode,
                KEY_PickupTypeName,
                KEY_PickupTime,
                KEY_DeliveryTime,
                KEY_IlArea,
                KEY_AreaName,
                KEY_Address,
                KEY_DetailAddress,
                KEY_DeliveryTimeTypeCode,
                KEY_DeliveryTimeTypeName,
                KEY_TonCode,
                KEY_TonName,
                KEY_ContainerTypeCode,
                KEY_ContainerTypeName,
                KEY_Price,
                KEY_PaymentType,
                KEY_PaymentTypeName,
                KEY_ItemKindCode,
                KEY_ItemKindName,
                KEY_VehicleID,
                KEY_LoadingMethod,
                KEY_LoadingMethodName,
                KEY_UnloadingMethod,
                KEY_UnloadingMethodName,
                KEY_DeliveryType,
                KEY_DeliveryTypeName,
                KEY_WidthText,
                KEY_LengthText,
                KEY_HeightText,
                KEY_Weight,
                KEY_CustomOrderType,
                KEY_CustomOrderTypeName,
                KEY_Remark,
                KEY_RequestedPickupDate,
                KEY_RequestedDeliveryDate,
                KEY_OrderTime,
                KEY_DriverName,
                KEY_DriverMobilePhone,
                KEY_StatusName,
                KEY_Charged,
                KEY_ChargedText,
                KEY_UserName,
                KEY_UserMobilePhone,
                KEY_OwnerName,
                KEY_OwnerPhone,
                KEY_VehicleNo}, KEY_SITECODE + "=" + "'" + mSiteCode+ "'", null, null, null, null, null);

        if(cs.moveToLast())
        {
            do{

                fnsTemplateDatas.add(SetCursorData(cs));

            }while(cs.moveToPrevious());
        }

        cs.close();

        return fnsTemplateDatas;
    }

    /** Count*/
    public int FetchTemplateCnt(String mSiteCode) throws SQLException {
        int nCount = 0;

        Cursor cs = m_Db.query(true, DATABASE_TABLE, new String[]{"COUNT(*)"}, KEY_SITECODE + "=" + "'" + mSiteCode+ "'", null, null, null, null, null);

        if(cs.moveToFirst()) {
            nCount = cs.getInt(0);
        }

        cs.close();

        return nCount;
    }

    /** INSERT */
    public long CreateTemplateData(FnsTemplateData Data)
    {
        return m_Db.insert(DATABASE_TABLE, null, SetContentValues(Data));
    }

    /** DELETE */
    public boolean DeleteTemplateData(long rowID)
    {
        return m_Db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowID, null) > 0;
    }

    private FnsTemplateData SetCursorData(Cursor cs)
    {
        int nPos = 0;

        FnsTemplateData data = new FnsTemplateData();

        data.setRowOid(cs.getString(nPos++));
        data.setSiteCode(cs.getString(nPos++));
        data.setID(cs.getString(nPos++));
        data.setTransID(cs.getString(nPos++));
        data.setOid(cs.getString(nPos++));
        data.setTurnOid(cs.getString(nPos++));
        data.setDriverUserOid(cs.getString(nPos++));
        data.setStatus(cs.getString(nPos++));
        data.setOrderDate(cs.getString(nPos++));
        data.setEarlistPickupTime(cs.getString(nPos++));
        data.setTransDate(cs.getString(nPos++));
        data.setTimeSpan(cs.getString(nPos++));
        data.setTimeSpanText(cs.getString(nPos++));
        data.setIlDeliveryCustomer(cs.getString(nPos++));
        data.setDeliveryCustomerName(cs.getString(nPos++));
        data.setDeliveryCustomerPhone(cs.getString(nPos++));
        data.setIlPickupArea(cs.getString(nPos++));
        data.setPickupAreaName(cs.getString(nPos++));
        data.setPickupTypeCode(cs.getString(nPos++));
        data.setPickupTypeName(cs.getString(nPos++));
        data.setPickupTime(cs.getString(nPos++));
        data.setDeliveryTime(cs.getString(nPos++));
        data.setIlArea(cs.getString(nPos++));
        data.setAreaName(cs.getString(nPos++));
        data.setAddress(cs.getString(nPos++));
        data.setDetailAddress(cs.getString(nPos++));
        data.setDeliveryTimeTypeCode(cs.getString(nPos++));
        data.setDeliveryTimeTypeName(cs.getString(nPos++));
        data.setTonCode(cs.getString(nPos++));
        data.setTonName(cs.getString(nPos++));
        data.setContainerTypeCode(cs.getString(nPos++));
        data.setContainerTypeName(cs.getString(nPos++));
        data.setPrice(cs.getString(nPos++));
        data.setPaymentType(cs.getString(nPos++));
        data.setPaymentTypeName(cs.getString(nPos++));
        data.setItemKindCode(cs.getString(nPos++));
        data.setItemKindName(cs.getString(nPos++));
        data.setVehicleID(cs.getString(nPos++));
        data.setLoadingMethod(cs.getString(nPos++));
        data.setLoadingMethodName(cs.getString(nPos++));
        data.setUnloadingMethod(cs.getString(nPos++));
        data.setUnloadingMethodName(cs.getString(nPos++));
        data.setDeliveryType(cs.getString(nPos++));
        data.setDeliveryTypeName(cs.getString(nPos++));
        data.setWidthText(cs.getString(nPos++));
        data.setLengthText(cs.getString(nPos++));
        data.setHeightText(cs.getString(nPos++));
        data.setWeight(cs.getString(nPos++));
        data.setCustomOrderType(cs.getString(nPos++));
        data.setCustomOrderTypeName(cs.getString(nPos++));
        data.setRemark(cs.getString(nPos++));
        data.setRequestedPickupDate(cs.getString(nPos++));
        data.setRequestedDeliveryDate(cs.getString(nPos++));
        data.setOrderTime(cs.getString(nPos++));
        data.setDriverName(cs.getString(nPos++));
        data.setDriverMobilePhone(cs.getString(nPos++));
        data.setStatusName(cs.getString(nPos++));
        data.setCharged(cs.getString(nPos++));
        data.setChargedText(cs.getString(nPos++));
        data.setUserName(cs.getString(nPos++));
        data.setUserMobilePhone(cs.getString(nPos++));
        data.setOwnerName(cs.getString(nPos++));
        data.setOwnerPhone(cs.getString(nPos++));
        data.setVehicleNo(cs.getString(nPos++));

        return data;
    }

    private ContentValues SetContentValues(FnsTemplateData Data)
    {
        ContentValues val = new ContentValues();

        val.put(KEY_SITECODE, Data.getSiteCode());
        val.put(KEY_ID, Data.getID());
        val.put(KEY_TransID, Data.getTransID());
        val.put(KEY_Oid, Data.getOid());
        val.put(KEY_TurnOid, Data.getTurnOid());
        val.put(KEY_DriverUserOid, Data.getDriverUserOid());
        val.put(KEY_Status, Data.getStatus());
        val.put(KEY_OrderDate, Data.getOrderDate());
        val.put(KEY_EarlistPickupTime, Data.getEarlistPickupTime());
        val.put(KEY_TransDate, Data.getTransDate());
        val.put(KEY_TimeSpan, Data.getTimeSpan());
        val.put(KEY_TimeSpanText, Data.getTimeSpanText());
        val.put(KEY_IlDeliveryCustomer, Data.getIlDeliveryCustomer());
        val.put(KEY_DeliveryCustomerName, Data.getDeliveryCustomerName());
        val.put(KEY_DeliveryCustomerPhone, Data.getDeliveryCustomerPhone());
        val.put(KEY_IlPickupArea, Data.getIlPickupArea());
        val.put(KEY_PickupAreaName, Data.getPickupAreaName());
        val.put(KEY_PickupTypeCode, Data.getPickupTypeCode());
        val.put(KEY_PickupTypeName, Data.getPickupTypeName());
        val.put(KEY_PickupTime, Data.getPickupTime());
        val.put(KEY_DeliveryTime, Data.getDeliveryTime());
        val.put(KEY_IlArea, Data.getIlArea());
        val.put(KEY_AreaName, Data.getAreaName());
        val.put(KEY_Address, Data.getAddress());
        val.put(KEY_DetailAddress, Data.getDetailAddress());
        val.put(KEY_DeliveryTimeTypeCode, Data.getDeliveryTimeTypeCode());
        val.put(KEY_DeliveryTimeTypeName, Data.getDeliveryTimeTypeName());
        val.put(KEY_TonCode, Data.getTonCode());
        val.put(KEY_TonName, Data.getTonName());
        val.put(KEY_ContainerTypeCode, Data.getContainerTypeCode());
        val.put(KEY_ContainerTypeName, Data.getContainerTypeName());
        val.put(KEY_Price, Data.getPrice());
        val.put(KEY_PaymentType, Data.getPaymentType());
        val.put(KEY_PaymentTypeName, Data.getPaymentTypeName());
        val.put(KEY_ItemKindCode, Data.getItemKindCode());
        val.put(KEY_ItemKindName, Data.getItemKindName());
        val.put(KEY_VehicleID, Data.getVehicleID());
        val.put(KEY_LoadingMethod, Data.getLoadingMethod());
        val.put(KEY_LoadingMethodName, Data.getLoadingMethodName());
        val.put(KEY_UnloadingMethod, Data.getUnloadingMethod());
        val.put(KEY_UnloadingMethodName, Data.getUnloadingMethodName());
        val.put(KEY_DeliveryType, Data.getDeliveryType());
        val.put(KEY_DeliveryTypeName, Data.getDeliveryTypeName());
        val.put(KEY_WidthText, Data.getWidthText());
        val.put(KEY_LengthText, Data.getLengthText());
        val.put(KEY_HeightText, Data.getHeightText());
        val.put(KEY_Weight, Data.getWeight());
        val.put(KEY_CustomOrderType, Data.getCustomOrderType());
        val.put(KEY_CustomOrderTypeName, Data.getCustomOrderTypeName());
        val.put(KEY_Remark, Data.getRemark());
        val.put(KEY_RequestedPickupDate, Data.getRequestedDeliveryDate());
        val.put(KEY_RequestedDeliveryDate, Data.getRequestedDeliveryDate());
        val.put(KEY_OrderTime, Data.getOrderTime());
        val.put(KEY_DriverName, Data.getDriverName());
        val.put(KEY_DriverMobilePhone, Data.getDriverMobilePhone());
        val.put(KEY_StatusName, Data.getStatusName());
        val.put(KEY_Charged, Data.getCharged());
        val.put(KEY_ChargedText, Data.getChargedText());
        val.put(KEY_UserName, Data.getUserName());
        val.put(KEY_UserMobilePhone, Data.getUserMobilePhone());
        val.put(KEY_OwnerName, Data.getOwnerName());
        val.put(KEY_OwnerPhone, Data.getOwnerPhone());
        val.put(KEY_VehicleNo, Data.getVehicleNo());

        return val;
    }
}

