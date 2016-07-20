package com.neosystems.logishubmobile50.Geo;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

import com.neosystems.logishubmobile50.Common.HTTPUtil;
import com.neosystems.logishubmobile50.Common.PrefsUtil;
import com.neosystems.logishubmobile50.Common.Define;

import org.json.JSONObject;

public class GeoLocationFinder {
    public static final int WAITING_TIME = GeoLocationHandler.GEO_LOCATION_UPDATE_TIME;

    private Location _currentBestLocation = null;
    private GPSLocationListener _gpsLocationListener;
    private ServiceLocationListener _networkLocationListener;
    private ServiceLocationListener _passiveLocationListener;

    private IGeoLocation _geoLocation;

    private Handler _handler = new Handler();
    private boolean _enabledGPS = false;
    private boolean _isReadyGPS = false;

    private Context _ctx = null;
    private LocationManager _locationManager = null;

    public GeoLocationFinder(Context ctx) {
        _ctx = ctx;
        if (_locationManager == null)
            _locationManager = (LocationManager)_ctx.getApplicationContext().getSystemService(android.content.Context.LOCATION_SERVICE);
    }

    public void fetchLocation(IGeoLocation geoLocation) {
        Log.v("fetchLocation()", "Fetching location...");

        _geoLocation = geoLocation;

        try {
            if (_locationManager == null)
                _locationManager = (LocationManager)_ctx.getApplicationContext().getSystemService(android.content.Context.LOCATION_SERVICE);

            _enabledGPS = _locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            LocationProvider gpsProvider = _locationManager.getProvider(LocationManager.GPS_PROVIDER);
            LocationProvider networkProvider = _locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
            LocationProvider passiveProvider = _locationManager.getProvider(LocationManager.PASSIVE_PROVIDER);

            if (_enabledGPS && gpsProvider != null ) {
                Location lastKnownGPSLocation = _locationManager.getLastKnownLocation(gpsProvider.getName());

                if (_isReadyGPS) {
                    long lastGPSTime = lastKnownGPSLocation.getTime();
                    long currentTime = System.currentTimeMillis();

                    if ((currentTime - lastGPSTime) > (WAITING_TIME * 2)) {
                        _isReadyGPS = false;
                    }
                }

                if(lastKnownGPSLocation != null && isBetterLocation(lastKnownGPSLocation, _currentBestLocation))
                    _currentBestLocation = lastKnownGPSLocation;
            } else {
                _enabledGPS = false;
                _isReadyGPS = false;
            }

            if (networkProvider != null) {
                Location lastKnownNetworkLocation = _locationManager.getLastKnownLocation(networkProvider.getName());

                if (lastKnownNetworkLocation != null && isBetterLocation(lastKnownNetworkLocation, _currentBestLocation))
                    _currentBestLocation = lastKnownNetworkLocation;
            }

            if (passiveProvider != null) {
                Location lastKnownPassiveLocation = _locationManager.getLastKnownLocation(passiveProvider.getName());

                if (lastKnownPassiveLocation != null && isBetterLocation(lastKnownPassiveLocation, _currentBestLocation)) {
                    _currentBestLocation = lastKnownPassiveLocation;
                }
            }

            if (_enabledGPS && _gpsLocationListener == null && !_isReadyGPS)
                _gpsLocationListener = new GPSLocationListener();

            _networkLocationListener = new ServiceLocationListener();
            _passiveLocationListener = new ServiceLocationListener();

            if (_enabledGPS && gpsProvider != null && !_isReadyGPS) {
                _locationManager.requestLocationUpdates(gpsProvider.getName(), 0l, 0.0f, _gpsLocationListener);
            }

            if (networkProvider != null) {
                _locationManager.requestLocationUpdates(networkProvider.getName(), 0l, 0.0f, _networkLocationListener);
            }

            if(passiveProvider != null) {
                _locationManager.requestLocationUpdates(passiveProvider.getName(), 0l, 0.0f, _passiveLocationListener);
            }

            if (gpsProvider != null || networkProvider != null || passiveProvider != null) {
//	            _handler.postDelayed(timerRunnable, WAITING_TIME);
                _handler.postDelayed(timerRunnable, WAITING_TIME);
            } else {
                _handler.post(timerRunnable);
            }
        } catch (SecurityException se) {
            finish();
        }
    }

    private class ServiceLocationListener implements android.location.LocationListener
    {
        public void onLocationChanged(Location newLocation) {
            synchronized ( this ) {
                if(isBetterLocation(newLocation, _currentBestLocation)) {
                    _currentBestLocation = newLocation;

                    if(_currentBestLocation.hasAccuracy() && _currentBestLocation.getAccuracy() <= 100) {
                        finish();
                    }
                }
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String s) {}

        public void onProviderDisabled(String s) {}
    }

    private class GPSLocationListener implements android.location.LocationListener
    {
        public void onLocationChanged(Location newLocation) {
            synchronized ( this ) {
                _isReadyGPS = true;
                Log.v("onLocationChanged()", ">>> GPS onLocationChanged(): " + newLocation.getAccuracy());
                if(isBetterLocation(newLocation, _currentBestLocation)) {
                    _currentBestLocation = newLocation;

                    if(_currentBestLocation.hasAccuracy() && _currentBestLocation.getAccuracy() <= 100) {
                        finish();
                    }
                }
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.v("onStatusChanged()", ">>> GPS onStatusChanged(): " + status);
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    _isReadyGPS = false;
                    break;
                case LocationProvider.AVAILABLE:
                    _isReadyGPS = true;
                    break;
            }
        }

        public void onProviderEnabled(String s) {}

        public void onProviderDisabled(String s) {}
    }

    private synchronized void finish() {
        _handler.removeCallbacks(timerRunnable);
        _handler.post(timerRunnable);
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > WAITING_TIME;
        boolean isSignificantlyOlder = timeDelta < -WAITING_TIME;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private Runnable timerRunnable = new Runnable() {
        public void run() {
                Log.v("timerRunnable()", "GeoLocationRun!!");
            if(_currentBestLocation != null) {
                Log.v("timerRunnable()", ">>> enabledGPS: " + _enabledGPS + ", isReadyGPS: " + _isReadyGPS + ", Accuracy: " + _currentBestLocation.getAccuracy() + ", GeoProvider: " + _currentBestLocation.getProvider() + ", Lat: " + _currentBestLocation.getLatitude() + ", Lng: " + _currentBestLocation.getLongitude());

                try {
                    boolean enableGeoLocationLogger = PrefsUtil.getValue(Define.GEO_LOCATION_LOGGER_ENABLE, "N").equals("Y") ? true : false;
                    String customerServiceURL = PrefsUtil.getValue(Define.CUSTOMER_SERVICE_URL, null);

                    if (enableGeoLocationLogger && customerServiceURL != null) {
                        Hashtable<String, String> params = new Hashtable<String, String>();

                        params.put("deviceId", PrefsUtil.getValue(Define.DEVICE_ID, ""));
                        params.put("phoneNo", PrefsUtil.getValue(Define.PHONE_NO, ""));
                        params.put("provider", _currentBestLocation.getProvider());
                        params.put("lat", String.valueOf(_currentBestLocation.getLatitude()));
                        params.put("lng", String.valueOf(_currentBestLocation.getLongitude()));
                        params.put("accuracy", String.valueOf(_currentBestLocation.getAccuracy()));
                        params.put("enabledGPS", _enabledGPS ? "0" : "1");
                        params.put("isReadyGPS", _isReadyGPS ? "0" : "1");

                        String parameters = HTTPUtil.generateParameters(params, "UTF-8");

                        String url = customerServiceURL + "/GeoLocationLogger.ashx";
                        new GeoLocationLogger().execute(url, parameters, "UTF-8");
                    }
                } catch(Exception e) {
                    Log.e("timerRunnable", "run()", e);
                }

                PrefsUtil.setValue(Define.GEO_ENABLE_GPS, _enabledGPS ? "0" : "1");
                PrefsUtil.setValue(Define.GEO_ENABLE_GPS, _isReadyGPS ? "0" : "1");

                _geoLocation.onLocation(_currentBestLocation);

                clearListeners();
            }
        }
    };

    public void clearListeners() {
        if (_locationManager != null) {
            if (_enabledGPS && _isReadyGPS && _gpsLocationListener != null)
                _locationManager.removeUpdates(_gpsLocationListener);

            if (_networkLocationListener != null)
                _locationManager.removeUpdates(_networkLocationListener);

            if (_passiveLocationListener != null)
                _locationManager.removeUpdates(_passiveLocationListener);

            if (_enabledGPS) {
                if (_isReadyGPS)
                    _locationManager = null;
            } else
                _locationManager = null;
        }
    }

    /**
     */
    public void stop() {
        if (_locationManager != null) {

            if (_enabledGPS && _gpsLocationListener != null)
                _locationManager.removeUpdates(_gpsLocationListener);

            if (_networkLocationListener != null)
                _locationManager.removeUpdates(_networkLocationListener);

            if (_passiveLocationListener != null)
                _locationManager.removeUpdates(_passiveLocationListener);

            if (_enabledGPS) {
                _locationManager = null;
            } else
                _locationManager = null;
        }
    }

    public String getLocation() {
        JSONObject result = new JSONObject();

        try {
            result.put("Provider", PrefsUtil.getValue(Define.GEO_PROVIDER, null));
            result.put("Latitude", PrefsUtil.getValue(Define.GEO_LATITUDE, null));
            result.put("Longitude", PrefsUtil.getValue(Define.GEO_LONGITUDE, null));
            result.put("LastUpdatedTime", PrefsUtil.getValue(Define.GEO_LAST_UPDATED_TIME, null));
            result.put("Accuracy", PrefsUtil.getValue(Define.GEO_ACCURACY, null));
            result.put("EnableGPS", PrefsUtil.getValue(Define.GEO_ENABLE_GPS, "0"));
            result.put("IsReadyGPS", PrefsUtil.getValue(Define.GEO_IS_READY_GPS, "0"));
        } catch (Exception e) {
            Log.e("GeoLocationFinder", "getLocation()", e);

            return null;
        }

        return result.toString();
    }

    public boolean IsGPSEnabled() {
        return _locationManager != null && _locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ? true : false;
    }

    private class GeoLocationLogger extends AsyncTask<String, Void, HttpURLConnection> {
        @Override
        protected HttpURLConnection doInBackground(String... params) {
            Log.v("doInBackground()", ">>> Sending GeoLocation To Logger...");

            HttpURLConnection conn = null;
            OutputStream out = null;

            try {
                conn = (HttpURLConnection)(new URL(params[0]).openConnection());
                conn.setConnectTimeout(60000);
                conn.setReadTimeout(60000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                conn.setRequestProperty("Accept-Charset", params[2]);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + params[2]);

                out = conn.getOutputStream();
                out.write(params[1].getBytes(params[2]));
                out.flush();
                int respStatusCode = conn.getResponseCode();

                if (respStatusCode != 200) {
                    throw new Exception("GeoLocation Logging Error!! Status Code: " + respStatusCode);
                }
            } catch (Exception e) {
                Log.e("GeoLocationLogger", "doInBackground()", e);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        Log.e("GeoLocationLogger", "doInBackground()", e);
                    }
                }

            }
            return conn;
        }

        @Override
        protected void onPostExecute(HttpURLConnection conn) {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }
}