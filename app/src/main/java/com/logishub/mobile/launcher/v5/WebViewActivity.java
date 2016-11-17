package com.logishub.mobile.launcher.v5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.logishub.mobile.launcher.v5.Common.Define;

public class WebViewActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Fragment fragment;
    public static String mUrl;
    public static String mTitle;
    public static String mUserID;
    public static String mUserPassWord;
    public static String mWebType;
    public static String mParamAnd;
    public static String mParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        setLayout();
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

        toolbar = null;
        fragment = null;
        mUrl = null;
        mTitle = null;
        mUserID = null;
        mUserPassWord = null;
        mWebType = null;
        mParamAnd = null;
        mParam = null;
    }

    private void setLayout() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mUrl = intent.getExtras().getString(Define.ACT_PUT_REQ_WEB_URL);
        mWebType = intent.getExtras().getString(Define.ACT_PUT_REQ_WEB_TYPE);
        mTitle = intent.getExtras().getString(Define.ACT_PUT_REQ_WEB_TITLE);
        mUserID = intent.getExtras().getString(Define.ACT_PUT_REQ_WEB_ID);
        mUserPassWord = intent.getExtras().getString(Define.ACT_PUT_REQ_WEB_PASS);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mTitle);
        }

        if (mWebType.equals(Define.MENU_WEB_MENU_DEFAULT_TYPE)) {
            mParamAnd = "?";
        } else {
            mParamAnd = "&";
        }

        mParam = "userId=" + mUserID + "&userPassword=" + mUserPassWord + "&webviewYN=Y";
        onFragmentMove(mTitle, Define.LOGISHUB_WEB_DEFAULT + mUrl + mParamAnd + mParam);
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

    public void onFragmentMove(String Title, String url) {

        fragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Define.FRG_PUT_REQ_URL, url);
        fragment.setArguments(bundle);

        if (fragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_fragment_layout, fragment);
            ft.commit();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
        }
    }
}