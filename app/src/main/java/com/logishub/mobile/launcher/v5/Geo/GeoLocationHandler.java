package com.logishub.mobile.launcher.v5.Geo;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.logishub.mobile.launcher.v5.Common.PrefsUtil;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.MainActivity;

public class GeoLocationHandler implements IGeoLocation {
    private boolean isStarted = false;
    private static GeoLocationHandler instance = null;
    private GeoLocationFinder geoLocationFinder = null;
    private Handler handler = new Handler();

    private Runnable geoLocationTask = new Runnable()
    {
        public void run() {
            if (isStarted) {
                Log.v("run()", "Calling the GoeLocation task... GEO_LOCATION_UPDATE_TIME: " + Define.GEO_LOCATION_UPDATE_TIME + "ms");
                geoLocationFinder.fetchLocation(instance);

                handler.postDelayed(this, Define.GEO_LOCATION_UPDATE_TIME);
            }
        }
    };

    public static void initialize()
    {
        instance = new GeoLocationHandler();
    }

    public GeoLocationHandler()
    {
        init();
    }

    private void init()
    {
        instance = this;
        Log.v("init()", "GeoLocation Update Time: " + Define.GEO_LOCATION_UPDATE_TIME + "ms");

        geoLocationFinder = new GeoLocationFinder(MainActivity.mContext);

        PrefsUtil.setValue(Define.GEO_PROVIDER, null);
        PrefsUtil.setValue(Define.GEO_LATITUDE, null);
        PrefsUtil.setValue(Define.GEO_LONGITUDE, null);
        PrefsUtil.setValue(Define.GEO_LAST_UPDATED_TIME, null);
        PrefsUtil.setValue(Define.GEO_ACCURACY, null);
    }

    public static synchronized GeoLocationHandler getInstance() throws NullPointerException
    {
        if (instance == null) {
            return null; //throw new NullPointerException("Instance가 초기화되지 않았습니다.");
        }
        else {
            return instance;
        }
    }

    public void start() {
        handler.post(geoLocationTask);
        isStarted = true;
    }

    public void stop() {
        handler.removeCallbacks(geoLocationTask);
        geoLocationFinder.stop();

        PrefsUtil.setValue(Define.GEO_PROVIDER, null);
        PrefsUtil.setValue(Define.GEO_LATITUDE, null);
        PrefsUtil.setValue(Define.GEO_LONGITUDE, null);
        PrefsUtil.setValue(Define.GEO_LAST_UPDATED_TIME, null);
        PrefsUtil.setValue(Define.GEO_ACCURACY, null);

        isStarted = false;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void clearListeners() {
        if (geoLocationFinder != null)
            geoLocationFinder.clearListeners();
    }

    public boolean IsGPSEnabled() {
        return geoLocationFinder.IsGPSEnabled();
    }

    public String getLocation() {
        return geoLocationFinder.getLocation();
    }

    public void onLocation(Location location) {
        String provider = null;
        String latitude = null;
        String longitude = null;

        if (location != null) {
            provider = location.getProvider();
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }

        long currentTime = System.currentTimeMillis();

        PrefsUtil.setValue(Define.GEO_PROVIDER, provider);
        PrefsUtil.setValue(Define.GEO_LATITUDE, latitude);
        PrefsUtil.setValue(Define.GEO_LONGITUDE, longitude);
        PrefsUtil.setValue(Define.GEO_LAST_UPDATED_TIME, String.valueOf(currentTime));
        PrefsUtil.setValue(Define.GEO_ACCURACY, String.valueOf(location.getAccuracy()));
    }

}