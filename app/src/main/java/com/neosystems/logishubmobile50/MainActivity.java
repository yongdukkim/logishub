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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.neosystems.logishubmobile50.Common.Define;
import com.neosystems.logishubmobile50.Geo.GeoLocationHandler;
import com.neosystems.logishubmobile50.Task.VehicleOperationTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static EditText etResponse;
    public static TextView tvIsConnected;
    public static TextView tvUserID;
    public static TextView tvUserNickName;
    public static TextView tvUserServiceID;
    public static Button kakaoLogOut;
    public static Context context;
    long pressTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Long userID = intent.getExtras().getLong("userID");
        String userNickName = intent.getExtras().getString("userNickName");
        Long userServiceID = intent.getExtras().getLong("userServiceID");

        //Toast.makeText(getApplicationContext(), userProfile, Toast.LENGTH_LONG).show();

        context = MainActivity.this;
        etResponse = (EditText) findViewById(R.id.etResponse);
        etResponse.setFocusable(false);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        kakaoLogOut = (Button) findViewById(R.id.kakaoLogOut);
        tvUserID = (TextView) findViewById(R.id.tvUserID);
        tvUserNickName = (TextView) findViewById(R.id.tvUserNickName);
        tvUserServiceID = (TextView) findViewById(R.id.tvUserServiceID);

        tvUserID.setText(userID.toString());
        tvUserNickName.setText(userNickName);
        tvUserServiceID.setText(userServiceID.toString());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        kakaoLogOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickKakaoLogOut();
            }
        });

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if(isConnected()) {
            tvIsConnected.setText("연결");
            new VehicleOperationTask().execute(Define.LOGISHUBURL + Define.VEHICLEORPERATION);
        }
        else {
            tvIsConnected.setText("연결실패");
        }

        //위치전송
        GeoLocationHandler.initialize();
        GeoLocationHandler.getInstance().start();
    }

    @Override
    public void onDestroy() {
        if (GeoLocationHandler.getInstance() != null)
            GeoLocationHandler.getInstance().stop();

        super.onDestroy();
    }

    private void onClickKakaoLogOut() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                redirectLoginActivity();
            }
        });
    }

    private void redirectLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("DeviceToken", "");
        intent.putExtra("PhoneNumber", "");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        MainActivity.this.finish();
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
        // Handle navigation view item clicks here.
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
