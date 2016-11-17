package com.logishub.mobile.launcher.v5.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.logishub.mobile.launcher.v5.DATA.ConfigData;

import java.util.ArrayList;

public class ConfigAdapter {
    private static final String KEY_CONFIG_PUSH = "PushConfig";
    private static final String KEY_CONFIG_GPS = "GpsConfig";
    private static final String KEY_NEW_COUNT = "NewCount";
    private final String DATABASE_NAME = "Config";
    private final String DATABASE_TABLE = "TB_Config";
    private final int DATABASE_VERSION = 1;
    private final Context m_Context;
    private DatabaseHelper m_DbHelper;
    private SQLiteDatabase m_Db;

    private static final String DATABASE_CREATE =
            "create table TB_Config (" +
                    "PushConfig text null,"+
                    "GpsConfig text null" +
                    ");";

    public ConfigAdapter(Context ctx)
    {
        this.m_Context = ctx;
    }

    public ConfigAdapter open() throws SQLException {
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
    public ArrayList<ConfigData> FetchTalkData(ArrayList<ConfigData> arrConfigData) throws SQLException
    {
        if(arrConfigData == null)
            arrConfigData = new ArrayList<ConfigData>();
        else
            arrConfigData.clear();

        Cursor cs = m_Db.query(true, DATABASE_TABLE, new String[]{KEY_CONFIG_PUSH, KEY_CONFIG_GPS}, null, null, null, null, null, null);

        if(cs.moveToFirst()) {
            do {
                arrConfigData.add(SetCursorData(cs));
            }while(cs.moveToNext());
        }

        cs.close();

        return arrConfigData;
    }

    public ConfigData GetConfigData() throws SQLException
    {
        ConfigData data = new ConfigData();

        Cursor cs = m_Db.query(true, DATABASE_TABLE, new String[]{KEY_CONFIG_PUSH, KEY_CONFIG_GPS}, null, null, null, null, null, null);

        if(cs.moveToFirst())
        {
            do{

                data = SetCursorData(cs);

            }while(cs.moveToNext());
        }

        cs.close();

        return data;
    }

    /** INSERT */
    public long CreateConfigData(ConfigData Data)
    {
        return m_Db.insert(DATABASE_TABLE, null, SetContentValues(Data));
    }

    /** UPDATE */
    public boolean UpdateNewCountClearAll()
    {
        ContentValues val = new ContentValues();

        val.put(KEY_NEW_COUNT, 0);

        return m_Db.update(DATABASE_TABLE, val, null, null) > 0;
    }

    public boolean UpdateConfigData(ConfigData Data)
    {
        return m_Db.update(DATABASE_TABLE, SetContentValues(Data), KEY_CONFIG_PUSH + "='" + Data.getPushConfig() +"'",  null) > 0;
    }

    /** DELETE */
    public boolean DeleteConfigData()
    {
        return m_Db.delete(DATABASE_TABLE, null, null) > 0;
    }

    private ConfigData SetCursorData(Cursor cs)
    {
        int nPos = 0;

        ConfigData data = new ConfigData();

        data.setPushConfig(cs.getString(nPos++));
        data.setGpsConfig(cs.getString(nPos++));

        return data;
    }

    private ContentValues SetContentValues(ConfigData Data)
    {
        ContentValues val = new ContentValues();

        val.put(KEY_CONFIG_PUSH, Data.getPushConfig());
        val.put(KEY_CONFIG_GPS, Data.getGpsConfig());

        return val;
    }

}

