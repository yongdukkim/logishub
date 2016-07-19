package com.neosystems.logishubmobile50;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.neosystems.logishubmobile50.Common.Define;

public class SplashActivity extends Activity {
    String DeviceToken = "";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        /**
         * FCM Token
         */
        FirebaseMessaging.getInstance().subscribeToTopic("NewLogisHub");
        DeviceToken = FirebaseInstanceId.getInstance().getToken();

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
            intent.putExtra("DeviceToken", DeviceToken);
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