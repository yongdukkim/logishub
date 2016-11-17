package com.logishub.mobile.launcher.v5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.Common.PrefsUtil;
import com.logishub.mobile.launcher.v5.DATA.CommunityListData;
import com.logishub.mobile.launcher.v5.DATA.ConfigData;
import com.logishub.mobile.launcher.v5.DATA.LoginSessionData;
import com.logishub.mobile.launcher.v5.DATA.MenuData;
import com.logishub.mobile.launcher.v5.DATA.MessageListData;
import com.logishub.mobile.launcher.v5.DATA.ServiceData;
import com.logishub.mobile.launcher.v5.DATA.UserData;
import com.logishub.mobile.launcher.v5.DB.ConfigAdapter;
import com.logishub.mobile.launcher.v5.DB.LoginSessionAdapter;
import com.logishub.mobile.launcher.v5.Geo.GeoLocationHandler;
import com.nhn.android.naverlogin.OAuthLogin;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";
    public static Context mContext;
    BroadcastReceiver mReceiver;

    long pressTime;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private LoginSessionAdapter mLoginSessionDb = null;
    private LoginSessionData mLoginSessionData = null;
    private ConfigAdapter mConfigDb = null;
    private ConfigData mConfigData = null;
    private CustomProgressDialog mProgressDialog;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private Menu mMenu;
    private GoogleSignInOptions gso;
    public static String mUserNickName;
    public static ArrayList<UserData> mArrUserList = null;
    public static ArrayList<MenuData> mArrMenuList = null;
    public static ArrayList<ServiceData> mArrServiceList = null;
    public static ArrayList<CommunityListData> mArrCommunityList = null;
    public static ArrayList<MessageListData> mArrMessageList = null;
    private boolean mEnableGPS;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Menu mOptionsMenu;
    private ImageView mGPSView;
    private String mPushLink;
    private String mPushType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** 화면 꺼짐 방지 */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        onOpenDB();

        setLayout();

        mEnableGPS = mConfigData.getGpsConfig().equals("Y") ? true : false;

        if (mEnableGPS) {
            /** Location Timer Start */
            GeoLocationHandler.initialize();
            GeoLocationHandler.getInstance().start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            mConfigData = mConfigDb.GetConfigData();
            mEnableGPS = mConfigData.getGpsConfig().equals("Y") ? true : false;
            onGPS(mEnableGPS);
        } catch (Exception ex) {}
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (GeoLocationHandler.getInstance() != null) {
            GeoLocationHandler.getInstance().stop();
        }

        if (mLoginSessionDb != null)
        {
            mLoginSessionDb.close();
            mLoginSessionDb = null;
        }

        if (mConfigDb != null)
        {
            mConfigDb.close();
            mConfigDb = null;
        }

        mGoogleApiClient = null;
        mAuth = null;
        mLoginSessionDb = null;
        mLoginSessionData = null;
        mProgressDialog = null;
        drawer = null;
        toggle = null;
        toolbar = null;
        navigationView = null;
        mMenu = null;
        gso = null;
        mArrUserList = null;
        mArrMenuList = null;
        mArrServiceList = null;
        mArrCommunityList = null;
        mArrMessageList = null;
        tabLayout = null;
        viewPager = null;
        mOptionsMenu = null;
        mGPSView = null;
        mPushLink = null;
        mPushType = null;

        unregisterReceiver(mReceiver);

        hideProgressDialog();
    }

    /** sdk 23v Error patch */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    private void setLayout() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        mAuth = FirebaseAuth.getInstance();
        mArrUserList = new ArrayList<>();
        mArrMenuList = new ArrayList<>();
        mArrServiceList = new ArrayList<>();
        mArrCommunityList = new ArrayList<>();
        mArrMessageList = new ArrayList<>();

        Intent intent = getIntent();
        mUserNickName = intent.getExtras().getString(Define.ACT_PUT_REQ_NICK_NAME);
        mPushLink = intent.getExtras().getString(Define.ACT_PUT_REQ_PUSH_LINK);
        mPushType = intent.getExtras().getString(Define.ACT_PUT_REQ_PUSH_TYPE);

        mArrUserList = (ArrayList<UserData>) getIntent().getSerializableExtra(Define.ACT_PUT_REQ_USER_LIST);
        mArrMenuList = (ArrayList<MenuData>) getIntent().getSerializableExtra(Define.ACT_PUT_REQ_MENU_LIST);
        mArrServiceList = (ArrayList<ServiceData>) getIntent().getSerializableExtra(Define.ACT_PUT_REQ_SERVICE_LIST);
        mArrMessageList = (ArrayList<MessageListData>) getIntent().getSerializableExtra(Define.ACT_PUT_REQ_MESSAGE_LIST);
        mArrCommunityList = (ArrayList<CommunityListData>) getIntent().getSerializableExtra(Define.ACT_PUT_REQ_COMMUNITY_LIST);

        mContext = MainActivity.this;

        PrefsUtil.setValue(Define.PHONE_NO, mLoginSessionData.getLoginUserPhoneNumber().replace("+82", "0"));
        PrefsUtil.setValue(Define.DEVICE_ID, mLoginSessionData.getLoginUserPhoneNumber().replace("+82", "0"));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mMenu = navigationView.getMenu();

        int mMenuSize = mArrMenuList.size();
        MenuItem menuItem;

        if (mMenuSize > 0) {
            for (int i = 0; i < mMenuSize; i++) {

                if (mArrMenuList.get(i).getName() != null && mArrMenuList.get(i).getLevel().equals(Define.MENU_LEVEL1)) {
                    SpannableString ss = new SpannableString(mArrMenuList.get(i).getName());
                    switch(mArrMenuList.get(i).getName())
                    {
                        case Define.NAVIGATION_MENU_HOME_NAME:
                            menuItem = mMenu.add(1, Define.NAVIGATION_MENU_HOME, Define.NAVIGATION_MENU_HOME, mArrMenuList.get(i).getName());
                            ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, mArrMenuList.get(i).getName().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            menuItem.setTitle(ss);
                            menuItem.setIcon(R.drawable.ic_menu_home);
                            break;
                        case Define.NAVIGATION_MENU_COMMUNITY_NAME:
                            menuItem = mMenu.add(1, Define.NAVIGATION_MENU_COMMUNITY, Define.NAVIGATION_MENU_COMMUNITY, mArrMenuList.get(i).getName());
                            ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, mArrMenuList.get(i).getName().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            menuItem.setTitle(ss);
                            menuItem.setIcon(R.drawable.ic_menu_community);
                            break;
                        case Define.NAVIGATION_MENU_MESSAGE_NAME:
                            menuItem = mMenu.add(1, Define.NAVIGATION_MENU_MESSAGE, Define.NAVIGATION_MENU_MESSAGE, mArrMenuList.get(i).getName());
                            ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, mArrMenuList.get(i).getName().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            menuItem.setTitle(ss);
                            menuItem.setIcon(R.drawable.ic_menu_message);
                            break;
                        case Define.NAVIGATION_MENU_MORE_NAME:
                            menuItem = mMenu.add(1, Define.NAVIGATION_MENU_MORE, Define.NAVIGATION_MENU_MORE, mArrMenuList.get(i).getName());
                            ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, mArrMenuList.get(i).getName().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            menuItem.setTitle(ss);
                            menuItem.setIcon(R.drawable.ic_menu_more);
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        SpannableString ss = new SpannableString(Define.NAVIGATION_MENU_LOGOUT_NAME);

        if (mLoginSessionData.getLoginType().equals(Define.LOGINTYPE_LOGISHUB)) {
            menuItem = mMenu.add(2, Define.NAVIGATION_MENU_LOGISHUB_LOGOUT, Define.NAVIGATION_MENU_LOGISHUB_LOGOUT, Define.NAVIGATION_MENU_LOGOUT_NAME);
            menuItem.setIcon(R.drawable.ic_menu_logout);
            ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, Define.NAVIGATION_MENU_LOGOUT_NAME.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            menuItem.setTitle(ss);
        }
        else if (mLoginSessionData.getLoginType().equals(Define.LOGINTYPE_KAKAO)) {
            menuItem = mMenu.add(2, Define.NAVIGATION_MENU_KAKAO_LOGOUT, Define.NAVIGATION_MENU_KAKAO_LOGOUT, Define.NAVIGATION_MENU_LOGOUT_NAME);
            menuItem.setIcon(R.drawable.ic_menu_logout);
            ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, Define.NAVIGATION_MENU_LOGOUT_NAME.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            menuItem.setTitle(ss);
        } else if (mLoginSessionData.getLoginType().equals(Define.LOGINTYPE_NAVER)) {
            menuItem = mMenu.add(2, Define.NAVIGATION_MENU_NAVER_LOGOUT, Define.NAVIGATION_MENU_NAVER_LOGOUT, Define.NAVIGATION_MENU_LOGOUT_NAME);
            menuItem.setIcon(R.drawable.ic_menu_logout);
            ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, Define.NAVIGATION_MENU_LOGOUT_NAME.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            menuItem.setTitle(ss);
        } else if (mLoginSessionData.getLoginType().equals(Define.LOGINTYPE_GOOGLE)) {
            menuItem = mMenu.add(2, Define.NAVIGATION_MENU_GOOGLE_LOGOUT, Define.NAVIGATION_MENU_GOOGLE_LOGOUT, Define.NAVIGATION_MENU_LOGOUT_NAME);
            menuItem.setIcon(R.drawable.ic_menu_logout);
            ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, Define.NAVIGATION_MENU_LOGOUT_NAME.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            menuItem.setTitle(ss);
        } else if (mLoginSessionData.getLoginType().equals(Define.LOGINTYPE_FACEBOOK)) {
            menuItem = mMenu.add(2, Define.NAVIGATION_MENU_FACEBOOK_LOGOUT, Define.NAVIGATION_MENU_FACEBOOK_LOGOUT, Define.NAVIGATION_MENU_LOGOUT_NAME);
            menuItem.setIcon(R.drawable.ic_menu_logout);
            ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, Define.NAVIGATION_MENU_LOGOUT_NAME.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            menuItem.setTitle(ss);
        }

        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(Define.SEND_BROADCAST_FLAG);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sendFlag = intent.getStringExtra(Define.SEND_BROADCAST_WEB_FLAG);
                String sendUrl = intent.getStringExtra(Define.SEND_BROADCAST_WEB_URL);
                String sendType = intent.getStringExtra(Define.SEND_BROADCAST_WEB_TYPE);
                String sendTitle = intent.getStringExtra(Define.SEND_BROADCAST_WEB_TITLE);
                if (sendFlag.equals(Define.LOGOUT_FLAG)) {
                    switch (mLoginSessionData.getLoginType()) {
                        case Define.LOGINTYPE_LOGISHUB:
                            onClickLogishubLogOut();
                            break;
                        case Define.LOGINTYPE_KAKAO:
                            onClickKakaoLogOut();
                            break;
                        case Define.LOGINTYPE_NAVER:
                            onClickNaverLogOut();
                            break;
                        case Define.LOGINTYPE_GOOGLE:
                            onClickGoogleLogOut();
                            break;
                        case Define.LOGINTYPE_FACEBOOK:
                            onClickFaceBookLogOut();
                            break;
                    }
                } else if (sendFlag.equals(Define.MENU_WEB_FLAG)) {
                    Intent mIntent = new Intent(getApplicationContext(), WebViewActivity.class);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mIntent.putExtra(Define.ACT_PUT_REQ_WEB_URL, sendUrl);
                    mIntent.putExtra(Define.ACT_PUT_REQ_WEB_TYPE, sendType);
                    mIntent.putExtra(Define.ACT_PUT_REQ_WEB_TITLE, sendTitle);
                    mIntent.putExtra(Define.ACT_PUT_REQ_WEB_ID, mArrUserList.get(0).getUserID());
                    mIntent.putExtra(Define.ACT_PUT_REQ_WEB_PASS, mArrUserList.get(0).getUserPassWord());
                    startActivity(mIntent);
                }
            }
        };

        registerReceiver(mReceiver, intentfilter);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        mGPSView = new ImageView(this);

        /** 사용자 로그인 시 Site Key 초기화 */
        PrefsUtil.removeAllPreferences();

        //링크가 있을경우 웹뷰로이동
        if(!Func.checkStringNull(mPushType).equals("")) {
            if(mPushType.equals(Define.PUSH_TYPE_COMMUNITYNOTICE_REG)) {
                if(!Func.checkStringNull(mPushLink).equals("")) {
                    String mSendTitle = "";
                    String[] mArrPushLink = mPushLink.split("\\?");

                    if(mArrPushLink.length > 1) {
                        String[] mArrPushLinkParam = mArrPushLink[1].split("&");

                        for(int i = 0; i < mArrPushLinkParam.length; i++) {
                            String[] mArrParam = mArrPushLinkParam[i].split("=");
                            if(mArrParam[0].equals("orgName")) {
                                mSendTitle = mArrParam[1] + "'s 공지사항";
                            }
                        }

                        //Intent sendIntent = new Intent(Define.SEND_BROADCAST_FLAG);
                        //sendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //sendIntent.putExtra(Define.SEND_BROADCAST_WEB_FLAG, Define.MENU_WEB_FLAG);
                        //sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TYPE, Define.MENU_WEB_MENU_COMMUNITY_TYPE);
                        //sendIntent.putExtra(Define.SEND_BROADCAST_WEB_URL, mPushLink);
                        //sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TITLE, mSendTitle);
                        //MainActivity.this.sendBroadcast(sendIntent);

                        Intent mIntent = new Intent(getApplicationContext(), WebViewActivity.class);
                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mIntent.putExtra(Define.ACT_PUT_REQ_WEB_URL, mPushLink);
                        mIntent.putExtra(Define.ACT_PUT_REQ_WEB_TYPE, Define.MENU_WEB_MENU_COMMUNITY_TYPE);
                        mIntent.putExtra(Define.ACT_PUT_REQ_WEB_TITLE, mSendTitle);
                        mIntent.putExtra(Define.ACT_PUT_REQ_WEB_ID, mArrUserList.get(0).getUserID());
                        mIntent.putExtra(Define.ACT_PUT_REQ_WEB_PASS, mArrUserList.get(0).getUserPassWord());
                        startActivity(mIntent);
                    }
                }
            }
        }
    }

    /** SQL Lite Open & Data Init */
    private void onOpenDB() {
        mLoginSessionDb = new LoginSessionAdapter(this);
        mLoginSessionDb.open();

        mLoginSessionData = mLoginSessionDb.GetLoginSessionData();

        mConfigDb = new ConfigAdapter(this);
        mConfigDb.open();

        mConfigData = mConfigDb.GetConfigData();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void onClickLogishubLogOut() {
        showProgressDialog();
        mLoginSessionDb.DeleteLoginSessionData();
        redirectLoginActivity();
    }

    private void onClickKakaoLogOut() {
        showProgressDialog();

        try {
            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    mLoginSessionDb.DeleteLoginSessionData();
                    redirectLoginActivity();
                }
            });
        } catch (Exception e) { }
    }

    private void onClickNaverLogOut() {
        showProgressDialog();
        try {
            OAuthLogin.getInstance().logout(mContext);
            redirectLoginActivity();
        } catch (Exception e) { }
    }

    public void onClickGoogleLogOut() {
        showProgressDialog();
        try {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            mLoginSessionDb.DeleteLoginSessionData();
                            redirectLoginActivity();
                        }
                    });
        } catch (Exception e) { }
    }

    public void onClickFaceBookLogOut() {
        showProgressDialog();

        try {
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            mLoginSessionDb.DeleteLoginSessionData();
            redirectLoginActivity();
        } catch (Exception e) { }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void redirectLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra(Define.ACT_PUT_REQ_DEVICE_TOKEN, "");
        intent.putExtra(Define.ACT_PUT_REQ_PHONE_NUMBER, "");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        hideProgressDialog();
        finish();
    }

    @Override
    public void onBackPressed() {

        if(System.currentTimeMillis() - pressTime < Define.BACK_PRESS_TIME) {
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
        getMenuInflater().inflate(R.menu.main, menu);
        mOptionsMenu = menu;
        onGPS(mEnableGPS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case Define.NAVIGATION_MENU_HOME:
                setCurrentInflateItem(0);
                break;
            case Define.NAVIGATION_MENU_COMMUNITY:
                setCurrentInflateItem(1);
                break;
            case Define.NAVIGATION_MENU_MESSAGE:
                setCurrentInflateItem(2);
                break;
            case Define.NAVIGATION_MENU_MORE:
                setCurrentInflateItem(3);
                break;
            case Define.NAVIGATION_MENU_LOGISHUB_LOGOUT:
                onClickLogishubLogOut();
                break;
            case Define.NAVIGATION_MENU_KAKAO_LOGOUT:
                onClickKakaoLogOut();
                break;
            case Define.NAVIGATION_MENU_NAVER_LOGOUT:
                onClickNaverLogOut();
                break;
            case Define.NAVIGATION_MENU_GOOGLE_LOGOUT:
                onClickGoogleLogOut();
                break;
            case Define.NAVIGATION_MENU_FACEBOOK_LOGOUT:
                onClickFaceBookLogOut();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        MainActivity.ViewPagerAdapter adapter = new MainActivity.ViewPagerAdapter(getSupportFragmentManager());

        int mMenuSize = mArrMenuList.size();

        for (int i =0; i < mMenuSize; i++)
        {
            if (mArrMenuList.get(i).getName() != null && mArrMenuList.get(i).getLevel().equals(Define.MENU_LEVEL1)) {
                switch (mArrMenuList.get(i).getName()) {
                    case Define.NAVIGATION_MENU_HOME_NAME:
                        adapter.addFragment(new HomeFragment(), mArrMenuList.get(i).getId(), mArrMenuList.get(i).getName());
                        break;

                    case Define.NAVIGATION_MENU_COMMUNITY_NAME:
                        adapter.addFragment(new CommunityFragment(), mArrMenuList.get(i).getId(), mArrMenuList.get(i).getName());
                        break;

                    case Define.NAVIGATION_MENU_MESSAGE_NAME:
                        adapter.addFragment(new MessageFragment(), mArrMenuList.get(i).getId(), mArrMenuList.get(i).getName());
                        break;

                    case Define.NAVIGATION_MENU_MORE_NAME:
                        adapter.addFragment(new SubMenuFragment(), mArrMenuList.get(i).getId(), mArrMenuList.get(i).getName());
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
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String menuId, String title) {
            Bundle bundle = new Bundle();
            bundle.putString(Define.FRG_PUT_REQ_MENU_ID, menuId);
            bundle.putString(Define.FRG_PUT_REQ_MENU_TYPE, title);
            fragment.setArguments(bundle);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void onGPS(boolean mGPS) {
        try {
            if (mGPS) {
                mGPSView.setImageResource(R.drawable.ic_gps_on);
                Animation mAnimation = new AlphaAnimation(1, 0);
                mAnimation.setDuration(1000);
                mAnimation.setInterpolator(new LinearInterpolator());
                mAnimation.setRepeatCount(Animation.INFINITE);
                mAnimation.setRepeatMode(Animation.REVERSE);
                mGPSView.setAnimation(mAnimation);
                mOptionsMenu.findItem(R.id.action_gps).setActionView(mGPSView);
            } else {
                mGPSView.setImageResource(android.R.color.transparent);
                mOptionsMenu.findItem(R.id.action_gps).setActionView(mGPSView);
                mOptionsMenu.findItem(R.id.action_gps).setIcon(null);
                mOptionsMenu.findItem(R.id.action_gps).setTitle("");
            }
        } catch (Exception ex) { }
    }

    private void setCurrentInflateItem(int type){
        viewPager.setCurrentItem(type);
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
