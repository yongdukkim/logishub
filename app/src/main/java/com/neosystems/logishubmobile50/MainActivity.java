package com.neosystems.logishubmobile50;

import android.content.Intent;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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
import com.neosystems.logishubmobile50.Common.CustomProgressDialog;
import com.neosystems.logishubmobile50.Common.Define;
import com.neosystems.logishubmobile50.DATA.LoginSessionData;
import com.neosystems.logishubmobile50.DB.LoginSessionAdapter;
import com.neosystems.logishubmobile50.Geo.GeoLocationHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";
    public static Context context;
    public ImageView ivUserProfile;
    public static TextView tvUserProfileName;
    Bitmap bitmap;

    long pressTime;
    private GoogleApiClient mGoogleApiClient;
    private LoginSessionAdapter mLoginSessionDb = null;
    private LoginSessionData mLoginSessionData = null;
    private CustomProgressDialog mProgressDialog;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private Menu menu;
    private GoogleSignInOptions gso;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onOpenDB();

        setLayout();

        /** userProfile Image Load */
        if (mLoginSessionData.GetLoginUserImageUrl() != "") {
            onUserProfileLoad();
        }

        /** Location Timer Start */
        GeoLocationHandler.initialize();
        GeoLocationHandler.getInstance().start();

        /** Main Fragment */
        onFragmentMove(Define.FRAGMENT_TYPE_MAIN, Define.ACTION_HEADER_MAIN);
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

    private void setLayout() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        Intent intent = getIntent();
        String userID = intent.getExtras().getString("userID");
        String userNickName = intent.getExtras().getString("userNickName");

        context = MainActivity.this;
        ivUserProfile = (ImageView)findViewById(R.id.ivUserProfile);
        tvUserProfileName = (TextView) findViewById(R.id.tvUserProfileName);
        tvUserProfileName.setText(userNickName + "님 환영합니다.");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        if (mLoginSessionData.GetLoginType().equals(Define.LOGINTYPE_KAKAO)) {
            menu.findItem(R.id.googleLogOut).setVisible(false);
        }
        else if (mLoginSessionData.GetLoginType().equals(Define.LOGINTYPE_GOOGLE)) {
            menu.findItem(R.id.kakaoLogOut).setVisible(false);
        }
    }

    /** SQL Lite Open & Data Init */
    private void onOpenDB() {
        mLoginSessionDb = new LoginSessionAdapter(this);
        mLoginSessionDb.open();

        mLoginSessionData = mLoginSessionDb.GetLoginSessionData();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void onUserProfileLoad() {

        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(mLoginSessionData.GetLoginUserImageUrl());

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);

                } catch (IOException ex) {

                }
            }
        };

        mThread.start();

        try {
            mThread.join();
            ivUserProfile.setImageBitmap(bitmap);
        } catch (InterruptedException e) {

        }
    }

    private void onClickKakaoLogOut() {
        showProgressDialog();
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectLoginActivity();
            }
        });
    }

    public void onClickGoogleLogOut() {
        showProgressDialog();
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
        hideProgressDialog();
        finish();
    }

    @Override
    public void onBackPressed() {

        if(System.currentTimeMillis() - pressTime < 2000) {
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

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        if (id == R.id.nav_camera) {
            onFragmentMove(Define.FRAGMENT_TYPE_MAIN, Define.ACTION_HEADER_MAIN);
        } else if (id == R.id.nav_gallery) {
            onFragmentMove(Define.FRAGMENT_TYPE_MAIN, Define.ACTION_HEADER_WEB);
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

    private void onFragmentMove(String FragmentType, String Title) {
        if (FragmentType.equals(Define.FRAGMENT_TYPE_MAIN)) {
            fragment = new MainFragment();
        } else if (FragmentType.equals(Define.FRAGMENT_TYPE_WEB)) {
            fragment = new WebViewFragment();
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_fragment_layout, fragment);
            ft.commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
        }
    }

    private void showProgressDialog() {
        mProgressDialog = new CustomProgressDialog(this);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
