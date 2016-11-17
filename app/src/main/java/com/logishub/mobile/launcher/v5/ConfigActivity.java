package com.logishub.mobile.launcher.v5;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.logishub.mobile.launcher.v5.DATA.ConfigData;
import com.logishub.mobile.launcher.v5.DB.ConfigAdapter;
import com.logishub.mobile.launcher.v5.Geo.GeoLocationHandler;

public class ConfigActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private SwitchCompat mSwitchPush;
    private SwitchCompat mSwitchGPS;
    private boolean mStatus = false;
    private ConfigAdapter mConfigDb = null;
    private ConfigData mConfigData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        onOpenDB();

        setLayout();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mConfigDb != null)
        {
            mConfigDb.close();
            mConfigDb = null;
        }

        mConfigData = null;
    }

    /** SQL Lite Open & Data Init */
    private void onOpenDB() {
        mConfigDb = new ConfigAdapter(this);
        mConfigDb.open();

        mConfigData = mConfigDb.GetConfigData();
    }

    private void setLayout() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.actionbar_sub);
            View v = getSupportActionBar().getCustomView();
            TextView titleTxtView = (TextView) v.findViewById(R.id.tv_actionbar_title);
            titleTxtView.setText(R.string.action_config_title);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mSwitchPush = (SwitchCompat) findViewById(R.id.switch_push);
        mSwitchPush.setSwitchPadding(40);
        mSwitchGPS = (SwitchCompat) findViewById(R.id.switch_gps);
        mSwitchGPS.setSwitchPadding(40);

        mSwitchPush.setOnCheckedChangeListener(this);
        mSwitchGPS.setOnCheckedChangeListener(this);

        if (mConfigData.getPushConfig().equals("Y")) {
            mSwitchPush.setChecked(true);
        } else {
            mSwitchPush.setChecked(false);
        }

        if (mConfigData.getGpsConfig().equals("Y")) {
            mSwitchGPS.setChecked(true);
        } else {
            mSwitchGPS.setChecked(false);
        }

        mStatus = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_push:
                if (mStatus) {
                    setConfigData(mSwitchPush.isChecked(), mSwitchGPS.isChecked());
                }
                break;
            case R.id.switch_gps:

                if (mStatus) {
                    if (isChecked) {
                        setConfigData(mSwitchPush.isChecked(), mSwitchGPS.isChecked());
                        GeoLocationHandler.initialize();
                        GeoLocationHandler.getInstance().start();
                    } else {
                        setConfigData(mSwitchPush.isChecked(), mSwitchGPS.isChecked());
                        try {
                            if (GeoLocationHandler.getInstance() != null) {
                                GeoLocationHandler.getInstance().stop();
                            }
                        } catch (Exception e) { }
                    }
                }

                break;
        }
    }

    private void setConfigData(boolean enablePush, boolean enableGps) {
        try {
            mConfigDb.DeleteConfigData();
            if (enablePush) {
                mConfigData.setPushConfig("Y");
            } else {
                mConfigData.setPushConfig("N");
            }

            if (enableGps) {
                mConfigData.setGpsConfig("Y");
            } else {
                mConfigData.setGpsConfig("N");
            }

            mConfigDb.CreateConfigData(mConfigData);
        } catch (Exception e) { }
    }
}