package com.logishub.mobile.launcher.v5;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.logishub.mobile.launcher.v5.Common.CustomAlertDialog;
import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.Common.HttpHelper;
import com.logishub.mobile.launcher.v5.Common.HttpService;
import com.logishub.mobile.launcher.v5.Common.PrefsUtil;
import com.logishub.mobile.launcher.v5.DATA.FnsData;
import com.logishub.mobile.launcher.v5.DATA.MenuData;
import com.logishub.mobile.launcher.v5.DATA.RequestUserData;
import com.logishub.mobile.launcher.v5.DATA.ResponseUserRegisterData;
import com.logishub.mobile.launcher.v5.DATA.UserData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreightInfoActivity extends AppCompatActivity implements FreightStatusFragment.CustomOnClickListener{
    private Toolbar toolbar;
    private TabLayout tabLayout;
    public ViewPager viewPager;
    private String mTitle;
    private String mServiceType;
    public String mServiceCode;
    public String mServiceURL;
    private String mMenuOid;
    public ArrayList<MenuData> mArrMenuList = null;
    public ArrayList<UserData> mArrUserList = null;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;
    private String mErrorCode;
    private String mErrorMessage;
    private CustomProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freight);

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
        tabLayout = null;
        viewPager = null;
        mTitle = null;
        mServiceType = null;
        mServiceCode = null;
        mServiceURL = null;
        mMenuOid = null;
        mArrMenuList = null;
        mArrUserList = null;
    }

    private void setLayout() {

        Intent intent = getIntent();
        mTitle = intent.getExtras().getString(Define.ACT_PUT_REQ_ACT_TITLE);
        mServiceType = intent.getExtras().getString(Define.ACT_PUT_REQ_SERVICE_TYPE);
        mServiceCode = intent.getExtras().getString(Define.ACT_PUT_REQ_SERVICE_CODE);
        mServiceURL = intent.getExtras().getString(Define.ACT_PUT_REQ_SERVICE_URL);
        mMenuOid = intent.getExtras().getString(Define.ACT_PUT_REQ_MENU_OID);
        mArrMenuList = (ArrayList<MenuData>) getIntent().getSerializableExtra(Define.ACT_PUT_REQ_MENU_LIST);
        mArrUserList = (ArrayList<UserData>) getIntent().getSerializableExtra(Define.ACT_PUT_REQ_USER_LIST);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mTitle);
        }

        if (PrefsUtil.getValue(mServiceCode, "").equals("")) {
            onLoadData();
        } else {
            onLoadViewPager();
        }
    }

    private void onLoadData() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    onSiteSession();
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void onSiteSession() {
        RequestUserData requestUserData = new RequestUserData();
        requestUserData.setId(mArrUserList.get(0).getUserID());
        requestUserData.setPassword(mArrUserList.get(0).getUserPassWord());
        requestUserData.setSiteCode(mServiceCode);
        requestUserData.setServiceUrl(mServiceURL);

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseUserRegisterData> call = mHttpService.getSiteSessionData(requestUserData);
        call.enqueue(new Callback<ResponseUserRegisterData>() {
            @Override
            public void onResponse(Call<ResponseUserRegisterData> call, Response<ResponseUserRegisterData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseUserRegisterData data = response.body();

                        if (data.getErrorCode().equals("0")) {
                            if (!Func.checkStringNull(data.getKey()).equals("")) {
                                PrefsUtil.setValue(mServiceCode, data.getKey());
                            }

                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS);
                        }
                        else {
                            mErrorCode = Func.checkStringNull(data.getErrorCode());
                            mErrorMessage = Func.checkStringNull(data.getErrorContent());
                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                        }

                    } else if (response.isSuccessful()) {
                        handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
                    } else {
                        handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
                }
            }

            @Override
            public void onFailure(Call<ResponseUserRegisterData> call, Throwable t) {
                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            hideProgressDialog();
            switch(msg.what)
            {
                case Define.HANDLER_MESSAGE_SUCCESS:
                    onLoadViewPager();
                    break;
                case Define.HANDLER_MESSAGE_ERROR:
                    new CustomAlertDialog(FreightInfoActivity.this, "알림\n\n" + mErrorMessage);
                    break;
                case Define.HANDLER_MESSAGE_FAIL:
                    new CustomAlertDialog(FreightInfoActivity.this, "알림" + "\n\n잠시 후 다시 이용해 주시길 바랍니다.");
                    break;
                default:
                    break;
            }

            return true;
        }
    });

    private void onLoadViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        String serviceType = "";
        int mMenuSize = mArrMenuList.size();

        if (mServiceType.length() > 0) {
            serviceType = mServiceType.substring(0, 3);
        }

        for (int i =0; i < mMenuSize; i++)
        {
            if (mArrMenuList.get(i).getName() != null && mArrMenuList.get(i).getLevel().equals(Define.MENU_LEVEL3) && Func.checkStringNull(mArrMenuList.get(i).getParentId()).equals(mMenuOid) && (Func.checkStringNull(mArrMenuList.get(i).getDescription()).equals(serviceType)) ||  (Func.checkStringNull(mArrMenuList.get(i).getDescription()).equals(Define.SERVICE_TYPE_FNS))) {

                switch (mArrMenuList.get(i).getName()) {
                    case Define.MENU_NAME_FREIGHT_REGISTER_A:
                        adapter.addFragment(new FreightRegisterFragment(), "0", mArrMenuList.get(i).getName());
                        break;
                    case Define.MENU_NAME_FREIGHT_STATUS:
                        adapter.addFragment(new FreightStatusFragment(), "1", mArrMenuList.get(i).getName());
                        break;
                    case Define.MENU_NAME_FREIGHT_HISTORY:
                        adapter.addFragment(new FreightHistoryFragment(), "2", mArrMenuList.get(i).getName());
                        break;
                }
            }
        }

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String menuId, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onClicked(FnsData data, String mModifyMode) {
        FreightRegisterFragment fragment = (FreightRegisterFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:"+R.id.viewpager+":0");
        fragment.onSetFreightData(data, mModifyMode);
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