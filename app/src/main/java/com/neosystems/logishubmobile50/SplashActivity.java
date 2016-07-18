package com.neosystems.logishubmobile50;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.neosystems.logishubmobile50.Common.Define;
import com.neosystems.logishubmobile50.Common.PrefsUtil;

public class SplashActivity extends Activity {
    String DeviceToken = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Handler hd = new Handler();

        FirebaseMessaging.getInstance().subscribeToTopic("NewLogisHub");
        DeviceToken = FirebaseInstanceId.getInstance().getToken();

        hd.postDelayed(new splashhandler() , 3000);
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