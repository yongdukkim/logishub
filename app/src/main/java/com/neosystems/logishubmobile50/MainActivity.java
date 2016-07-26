package com.neosystems.logishubmobile50;

import android.content.Intent;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.neosystems.logishubmobile50.Common.Define;
import com.neosystems.logishubmobile50.DATA.LoginSessionData;
import com.neosystems.logishubmobile50.DB.LoginSessionAdapter;
import com.neosystems.logishubmobile50.Geo.GeoLocationHandler;
import com.neosystems.logishubmobile50.Task.VehicleOperationTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";
    public static EditText etResponse;
    public static TextView tvIsConnected;
    public static TextView tvUserID;
    public static TextView tvUserNickName;
    public static Context context;
    long pressTime;
    private GoogleApiClient mGoogleApiClient;
    private LoginSessionAdapter mLoginSessionDb = null;
    private LoginSessionData mLoginSessionData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String userID = intent.getExtras().getString("userID");
        String userNickName = intent.getExtras().getString("userNickName");

        mLoginSessionDb = new LoginSessionAdapter(this);
        mLoginSessionDb.open();

        mLoginSessionData = mLoginSessionDb.GetLoginSessionData();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        context = MainActivity.this;
        etResponse = (EditText) findViewById(R.id.etResponse);
        etResponse.setFocusable(false);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        tvUserID = (TextView) findViewById(R.id.tvUserID);
        tvUserNickName = (TextView) findViewById(R.id.tvUserNickName);

        tvUserID.setText(userID);
        tvUserNickName.setText(userNickName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();

        if (mLoginSessionData.GetLoginType().equals(Define.LOGINTYPE_KAKAO)) {
            menu.findItem(R.id.googleLogOut).setVisible(false);
        }
        else if (mLoginSessionData.GetLoginType().equals(Define.LOGINTYPE_GOOGLE)) {
            menu.findItem(R.id.kakaoLogOut).setVisible(false);
        }

        if(isConnected()) {
            tvIsConnected.setText("연결");
            new VehicleOperationTask().execute(Define.LOGISHUBURL + Define.VEHICLEORPERATION);
        }
        else {
            tvIsConnected.setText("연결실패");
        }

        /** Location Timer Start */
        GeoLocationHandler.initialize();
        GeoLocationHandler.getInstance().start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (GeoLocationHandler.getInstance() != null)
            GeoLocationHandler.getInstance().stop();

        if (mLoginSessionDb != null)
        {
            mLoginSessionDb.close();
            mLoginSessionDb = null;
        }
    }

    private void onClickKakaoLogOut() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectLoginActivity();
            }
        });
    }

    public void onClickGoogleLogOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        redirectLoginActivity();
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void redirectLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("DeviceToken", "");
        intent.putExtra("PhoneNumber", "");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

        if(System.currentTimeMillis() - pressTime <2000){
            finishAffinity();
            return;
        }
        Toast.makeText(this, "한번 더 누르시면 앱이 종료됩니다.",Toast.LENGTH_LONG).show();
        pressTime = System.currentTimeMillis();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Toast.makeText(getApplicationContext(), "camera", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_gallery) {
            Toast.makeText(getApplicationContext(), "gallery", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_slideshow) {
            Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_manage) {
            Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_share) {
            Toast.makeText(getApplicationContext(), "3", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_send) {
            Toast.makeText(getApplicationContext(), "4", Toast.LENGTH_LONG).show();
        } else if (id == R.id.itmKakaoLogOut) {
            onClickKakaoLogOut();
        } else if (id == R.id.itmGoogleLogOut) {
            onClickGoogleLogOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}
