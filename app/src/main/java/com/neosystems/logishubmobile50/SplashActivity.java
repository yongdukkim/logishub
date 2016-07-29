package com.neosystems.logishubmobile50;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.neosystems.logishubmobile50.Common.Define;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashActivity extends Activity {
    private static String mDeviceToken = "";
    private static String mPhoneNumber = "";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        //getAppKeyHash();

        mPhoneNumber = getPhoneNumber();
        /**
         * FCM Token
         */
        FirebaseMessaging.getInstance().subscribeToTopic(Define.APP_NAME);
        mDeviceToken = FirebaseInstanceId.getInstance().getToken();

        /**
         * FireBase Analytics
         */
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "SplashScreen");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        /**
         * Splash 화면 전환
         */
        Handler hd = new Handler();
        hd.postDelayed(new splashhandler() , Define.SPLASHSCREEN_TIME);
    }

    public String getPhoneNumber()
    {
        TelephonyManager mgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return mgr.getLine1Number();
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key", something);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private class splashhandler implements Runnable {
        public void run() {

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra(Define.ACT_PUT_REQ_DEVICE_TOKEN, mDeviceToken);
            intent.putExtra(Define.ACT_PUT_REQ_PHONE_NUMBER, mPhoneNumber);
            startActivity(intent);
            SplashActivity.this.finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}