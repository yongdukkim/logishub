package com.logishub.mobile.launcher.v5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.daimajia.androidanimations.library.Techniques;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

public class SplashActivity extends AwesomeSplash {
    private static String mDeviceToken = "";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void initSplash(ConfigSplash configSplash) {

        FirebaseMessaging.getInstance().subscribeToTopic(Define.APP_NAME);
        mDeviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("token", "Refreshed token: " + mDeviceToken);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "SplashScreen");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        configSplash.setBackgroundColor(R.color.accent);
        configSplash.setAnimCircularRevealDuration(Define.SPLASH_SCREEN_BACKGROUND_TIME);
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM);

        configSplash.setLogoSplash(R.drawable.logo_logishub);
        configSplash.setAnimLogoSplashDuration(Define.SPLASH_SCREEN_LOGO_TIME);
        configSplash.setAnimLogoSplashTechnique(Techniques.Bounce);

        configSplash.setTitleSplash("NeoSystems co., Ltd.");
        configSplash.setTitleTextColor(R.color.monsoon);
        configSplash.setTitleTextSize(16f);
        configSplash.setAnimTitleDuration(Define.SPLASH_SCREEN_TEXT_TIME);
        configSplash.setAnimTitleTechnique(Techniques.FadeIn);
    }

    @Override
    public void animationsFinished() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra(Define.ACT_PUT_REQ_DEVICE_TOKEN, mDeviceToken);
        intent.putExtra(Define.ACT_PUT_REQ_PUSH_LINK, "");
        intent.putExtra(Define.ACT_PUT_REQ_PUSH_TYPE, "");
        startActivity(intent);
        SplashActivity.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}