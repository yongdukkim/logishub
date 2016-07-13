package com.neosystems.logishubmobile50;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.Activity;
import android.widget.Toast;

import com.neosystems.logishubmobile50.Common.Define;
import com.neosystems.logishubmobile50.Geo.GeoLocationHandler;
import com.neosystems.logishubmobile50.Task.VehicleOperationTask;

public class MainActivity extends AppCompatActivity {
    public static EditText etResponse;
    public static TextView tvIsConnected;
    public static Context context;
    private static MainActivity instance;
    public ActionBar actionBar;

    public static MainActivity getInstance(){
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionbar();

        context = MainActivity.this;
        etResponse = (EditText) findViewById(R.id.etResponse);
        etResponse.setFocusable(false);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);

        if(isConnected()) {
            tvIsConnected.setText("연결");
            new VehicleOperationTask().execute(Define.LOGISHUBURL + Define.VEHICLEORPERATION);

            GeoLocationHandler.initialize();
            GeoLocationHandler.getInstance().start();
        }
        else {
            tvIsConnected.setText("연결실패");
        }
    }

    protected void setActionbar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.actionbar_main, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);
        mTitleTextView.setText("LogisHub");

        ImageButton imageButton = (ImageButton) mCustomView.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "메뉴오픈", Toast.LENGTH_LONG).show();
            }
        });

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    protected  void onDestroy() {
        if (GeoLocationHandler.getInstance() != null) {
            GeoLocationHandler.getInstance().stop();
            GeoLocationHandler.getInstance().clearListeners();
        }

        super.onDestroy();
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
