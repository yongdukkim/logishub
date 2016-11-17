package com.logishub.mobile.launcher.v5.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.logishub.mobile.launcher.v5.DATA.LoginSessionData;

import java.util.ArrayList;

public class LoginSessionAdapter {
    private final String TAG = "LoginSessionDbAdapter";
    private static final String KEY_LOGIN_TYPE = "LoginType";
    private static final String KEY_LOGIN_USER_ID = "LoginUserID";
    private static final String KEY_LOGIN_USER_LOCAL_PASSWORD = "LoginUserLocalPassword";
    private static final String KEY_LOGIN_USER_PASSWORD = "LoginUserPassword";
    private static final String KEY_LOGIN_USER_NAME = "LoginUserName";
    private static final String KEY_LOGIN_USER_IMAGE_URL = "LoginUserImageUrl";
    private static final String KEY_LOGIN_USER_DEVICE_TOKEN = "LoginUserDeviceToken";
    private static final String KEY_LOGIN_USER_PHONE_NUMBER = "LoginUserPhoneNumber";
    private static final String KEY_NEW_COUNT = "NewCount";
    private final String DATABASE_NAME = "LoginSession";
    private final String DATABASE_TABLE = "TB_LoginSession";
    private final int DATABASE_VERSION = 1;
    private final Context m_Context;
    private DatabaseHelper m_DbHelper;
    private SQLiteDatabase m_Db;

    private static final String DATABASE_CREATE =
            "create table TB_LoginSession (LoginType text primary key ,"+
                    "LoginUserID text null," +
                    "LoginUserLocalPassword text null," +
                    "LoginUserPassword text null," +
                    "LoginUserName text null," +
                    "LoginUserImageUrl text null, " +
                    "LoginUserDeviceToken text null, " +
                    "LoginUserPhoneNumber text null " +
                    ");";

    public LoginSessionAdapter(Context ctx)
    {
        this.m_Context = ctx;
    }

    public LoginSessionAdapter open() throws SQLException {
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
    public ArrayList<LoginSessionData> FetchTalkData(ArrayList<LoginSessionData> arrLoginSessionData) throws SQLException
    {
        if(arrLoginSessionData == null)
            arrLoginSessionData = new ArrayList<LoginSessionData>();
        else
            arrLoginSessionData.clear();

        Cursor cs = m_Db.query(true, DATABASE_TABLE, new String[]{KEY_LOGIN_TYPE, KEY_LOGIN_USER_ID, KEY_LOGIN_USER_LOCAL_PASSWORD, KEY_LOGIN_USER_PASSWORD, KEY_LOGIN_USER_NAME, KEY_LOGIN_USER_IMAGE_URL, KEY_LOGIN_USER_DEVICE_TOKEN, KEY_LOGIN_USER_PHONE_NUMBER}, null, null, null, null, null, null);

        if(cs.moveToFirst()) {
            do {
                arrLoginSessionData.add(SetCursorData(cs));
            }while(cs.moveToNext());
        }

        cs.close();

        return arrLoginSessionData;
    }

    public LoginSessionData GetLoginSessionData() throws SQLException
    {
        LoginSessionData data = new LoginSessionData();

        Cursor cs = m_Db.query(true, DATABASE_TABLE, new String[]{KEY_LOGIN_TYPE, KEY_LOGIN_USER_ID, KEY_LOGIN_USER_LOCAL_PASSWORD, KEY_LOGIN_USER_PASSWORD, KEY_LOGIN_USER_NAME, KEY_LOGIN_USER_IMAGE_URL, KEY_LOGIN_USER_DEVICE_TOKEN, KEY_LOGIN_USER_PHONE_NUMBER}, null, null, null, null, null, null);

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
    public long CreateLoginSessionData(LoginSessionData Data)
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

    public boolean UpdateLoginSessionData(LoginSessionData Data)
    {
        return m_Db.update(DATABASE_TABLE, SetContentValues(Data), KEY_LOGIN_TYPE + "='" + Data.getLoginType() +"'", null) > 0;
    }

    /** DELETE */
    public boolean DeleteLoginSessionData()
    {
        return m_Db.delete(DATABASE_TABLE, KEY_LOGIN_TYPE + " != '' ", null) > 0;
    }

    private LoginSessionData SetCursorData(Cursor cs)
    {
        int nPos = 0;

        LoginSessionData data = new LoginSessionData();

        data.setLoginType(cs.getString(nPos++));
        data.setLoginUserID(cs.getString(nPos++));
        data.setLoginUserLocalPassword(cs.getString(nPos++));
        data.setLoginUserPassword(cs.getString(nPos++));
        data.setLoginUserName(cs.getString(nPos++));
        data.setLoginUserImageUrl(cs.getString(nPos++));
        data.setLoginUserDeviceToken(cs.getString(nPos++));
        data.setLoginUserPhoneNumber(cs.getString(nPos++));

        return data;
    }

    private ContentValues SetContentValues(LoginSessionData Data)
    {
        ContentValues val = new ContentValues();

        val.put(KEY_LOGIN_TYPE, Data.getLoginType());
        val.put(KEY_LOGIN_USER_ID, Data.getLoginUserID());
        val.put(KEY_LOGIN_USER_LOCAL_PASSWORD, Data.getLoginUserLocalPassword());
        val.put(KEY_LOGIN_USER_PASSWORD, Data.getLoginUserPassword());
        val.put(KEY_LOGIN_USER_NAME, Data.getLoginUserName());
        val.put(KEY_LOGIN_USER_IMAGE_URL, Data.getLoginUserImageUrl());
        val.put(KEY_LOGIN_USER_DEVICE_TOKEN, Data.getLoginUserDeviceToken());
        val.put(KEY_LOGIN_USER_PHONE_NUMBER, Data.getLoginUserPhoneNumber());

        return val;
    }

}

