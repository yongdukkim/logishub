package com.logishub.mobile.launcher.v5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.kakao.auth.AuthType;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.logishub.mobile.launcher.v5.Common.CustomAlertDialog;
import com.logishub.mobile.launcher.v5.Common.CustomAlertUpgradeDialog;
import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.Common.HttpHelper;
import com.logishub.mobile.launcher.v5.Common.HttpService;
import com.logishub.mobile.launcher.v5.Common.LoginPrefs;
import com.logishub.mobile.launcher.v5.DATA.CommunityListData;
import com.logishub.mobile.launcher.v5.DATA.ConfigData;
import com.logishub.mobile.launcher.v5.DATA.LoginSessionData;
import com.logishub.mobile.launcher.v5.DATA.MenuData;
import com.logishub.mobile.launcher.v5.DATA.MessageListData;
import com.logishub.mobile.launcher.v5.DATA.RequestUserData;
import com.logishub.mobile.launcher.v5.DATA.RequestUserRegisterData;
import com.logishub.mobile.launcher.v5.DATA.ResponseUserRegisterData;
import com.logishub.mobile.launcher.v5.DATA.ServiceData;
import com.logishub.mobile.launcher.v5.DATA.UserData;
import com.logishub.mobile.launcher.v5.DB.ConfigAdapter;
import com.logishub.mobile.launcher.v5.DB.LoginSessionAdapter;
import com.logishub.mobile.launcher.v5.GCM.MyFirebaseInstanceIDService;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.data.OAuthLoginState;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    BroadcastReceiver mReceiver;
    private LoginSessionAdapter mLoginSessionDb = null;
    private LoginSessionData mLoginSessionData = null;
    private ConfigAdapter mConfigDb = null;
    private ConfigData mConfigData = null;
    long pressTime;
    private static String mLoginType;
    public static String mDeviceToken;
    public static String mPhoneNumber;
    public static ArrayList<UserData> mArrUserList = null;
    public static ArrayList<MenuData> mArrMenuList = null;
    public static ArrayList<ServiceData> mArrServiceList = null;
    public static ArrayList<CommunityListData> mArrCommunityList = null;
    public static ArrayList<MessageListData> mArrMessageList = null;
    public static Context mContext;
    public EditText mLoginID;
    public EditText mPassWord;
    public CustomProgressDialog mProgressDialog;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;
    private String mErrorMessage;
    public static String mUserName = null;
    public static String mUserType = null;
    private String mPushType;
    private String mPushLink;

    /** Kakao */
    SessionCallback callback;

    /** Google+ */
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    /** FaceBook */
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private LoginButton btnFaceBookLogin;

    /** Naver */
    private OAuthLogin mOAuthLoginModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** FaceBook Init setContext보다 먼저 호출 */
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        onOpenDB();

        /** Naver Api Load */
        onLoadNaverApi();

        setLayout();

        /** Google Api Load */
        onLoadGoogleApi();

        /** kakao Api Load */
        onLoadKakaoApi();

        /** kakao User Check */
        requestKakao();

        FirebaseCrash.log("LoginActivity started");
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

        Session.getCurrentSession().removeCallback(callback);

        mLoginSessionData = null;
        mConfigData = null;
        mArrUserList = null;
        mArrMenuList = null;
        mArrServiceList = null;
        mArrCommunityList = null;
        mArrMessageList = null;
        mGoogleApiClient = null;
        mCallbackManager = null;
        mAuth = null;
        mAuthListener = null;
        mUserName = null;
        mUserType = null;
        mPushType = null;
        mPushLink = null;

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

        unregisterReceiver(mReceiver);

        hideKeyBoard();
        hideProgressDialog();
    }

    private void setLayout() {
        mContext = LoginActivity.this;
        Intent intent = getIntent();
        mDeviceToken = intent.getExtras().getString(Define.ACT_PUT_REQ_DEVICE_TOKEN);

        if (Func.checkStringNull(mDeviceToken).equals("")) {
            mDeviceToken = MyFirebaseInstanceIDService.mToken;
        }

        mPushLink = intent.getExtras().getString(Define.ACT_PUT_REQ_PUSH_LINK);
        mPushType = intent.getExtras().getString(Define.ACT_PUT_REQ_PUSH_TYPE);

        mPhoneNumber = getPhoneNumber();
        mArrUserList = new ArrayList<UserData>();
        mArrMenuList = new ArrayList<MenuData>();
        mArrServiceList = new ArrayList<ServiceData>();
        mArrCommunityList = new ArrayList<CommunityListData>();
        mArrMessageList = new ArrayList<MessageListData>();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.actionbar_main);
        }

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String userImageUrl = "";

                    if (user.getPhotoUrl() != null)
                        userImageUrl = user.getPhotoUrl().toString();

                    updateDB(Define.LOGINTYPE_FACEBOOK, user.getUid(), "", "", user.getDisplayName(), userImageUrl);
                    loginProcess(Define.LOGINTYPE_FACEBOOK);
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mCallbackManager = CallbackManager.Factory.create();
        btnFaceBookLogin = (LoginButton) findViewById(R.id.btnFaceBookLoginE);
        btnFaceBookLogin.setReadPermissions("email", "public_profile");
        btnFaceBookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });

        mLoginID = (EditText) findViewById(R.id.input_id);
        mLoginID.setFilters(new InputFilter[] {Func.filterAlphaNum});
        mPassWord = (EditText) findViewById(R.id.input_password);
        //mPassWord.setFilters(new InputFilter[] {Func.filterAlphaNum});

        if (mLoginSessionData.getLoginType().equals(Define.LOGINTYPE_LOGISHUB)) {
            mLoginID.setText(mLoginSessionData.getLoginUserID());
            mPassWord.setText(mLoginSessionData.getLoginUserLocalPassword());

            onLogin();
        }

        mLoginID.setText(LoginPrefs.getValue(Define.LOGIN_ID, ""));
        mPassWord.setText(LoginPrefs.getValue(Define.LOGIN_PASS, ""));

        //mPassWord.setText(mLoginSessionData.getLoginUserLocalPassword());

        if (OAuthLoginState.OK.equals(OAuthLogin.getInstance().getState(this))) {
            onNaveLogin();
        }

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btnKakaologin).setOnClickListener(this);
        findViewById(R.id.btnGoogleLogin).setOnClickListener(this);
        findViewById(R.id.btnFaceBookLogin).setOnClickListener(this);
        findViewById(R.id.btnNaverLogin).setOnClickListener(this);
        findViewById(R.id.txtSignUp).setOnClickListener(this);
        findViewById(R.id.txtFindPW).setOnClickListener(this);
        TextView tvAgreement = (TextView) findViewById(R.id.tv_agreement_full_text);

        Linkify.TransformFilter mTransform = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return "";
            }
        };

        Pattern pattern1 = Pattern.compile("이용약관");
        Pattern pattern2 = Pattern.compile("개인정보취급방침");
        Pattern pattern3 = Pattern.compile("위치기반서비스");
        Pattern pattern4 = Pattern.compile("유료서비스");
        Pattern pattern5 = Pattern.compile("화물정보망");

        Linkify.addLinks(tvAgreement, pattern1, "logishubapp://agreement?req_agreement=agreement1", null, mTransform);
        Linkify.addLinks(tvAgreement, pattern2, "logishubapp://agreement?req_agreement=agreement2", null, mTransform);
        Linkify.addLinks(tvAgreement, pattern3, "logishubapp://agreement?req_agreement=agreement3", null, mTransform);
        Linkify.addLinks(tvAgreement, pattern4, "logishubapp://agreement?req_agreement=agreement4", null, mTransform);
        Linkify.addLinks(tvAgreement, pattern5, "logishubapp://agreement?req_agreement=agreement5", null, mTransform);

        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(Define.SEND_BROADCAST_LOGIN);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mUserName = intent.getStringExtra(Define.SEND_BROADCAST_LOGIN_USER_NAME);
                mUserType = intent.getStringExtra(Define.SEND_BROADCAST_LOGIN_USER_TYPE);

                getUserSNS();
            }
        };

        registerReceiver(mReceiver, intentfilter);
    }

    /** SQL Lite Open & Data Init */
    private void onOpenDB() {

        try {
            mLoginSessionDb = new LoginSessionAdapter(this);
            mLoginSessionDb.open();

            mLoginSessionData = mLoginSessionDb.GetLoginSessionData();

            mConfigDb = new ConfigAdapter(this);
            mConfigDb.open();

            mConfigData = mConfigDb.GetConfigData();

            if (mConfigData.getPushConfig().equals("")) {
                mConfigDb.DeleteConfigData();
                mConfigData.setPushConfig("Y");
                mConfigData.setGpsConfig("Y");
                mConfigDb.CreateConfigData(mConfigData);
            }
        } catch (Exception e) { }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void onLoadGoogleApi() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    private void onLoadKakaoApi() {
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }

    private void onLoadNaverApi() {
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(LoginActivity.this, Define.OAUTH_CLIENT_ID, Define.OAUTH_CLIENT_SECRET, Define.OAUTH_CLIENT_NAME);
    }

    /** Google Login */
    @Override
    public void onStart() {
        super.onStart();

        //showProgressDialog();

        mAuth.addAuthStateListener(mAuthListener);

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }

        //hideProgressDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String userImageUrl = "";

            //if (acct.getPhotoUrl() != null)
            //    userImageUrl = acct.getPhotoUrl().toString();

            updateDB(Define.LOGINTYPE_GOOGLE, acct.getId(), "", "", acct.getDisplayName(), userImageUrl);
            loginProcess(Define.LOGINTYPE_GOOGLE);
        } else {
            //mErrorCode = "000";
            //mErrorMessage = "Google 로그인에 실패 하였습니다.";
            //handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
        }
    }

    private void onLogin() {
        if (!Func.isValidString(mLoginID.getText().toString())) {
            mLoginID.setError("아이디를 입력해 주세요.");
            mLoginID.requestFocus();
            showKeyBoard();
            return;
        }

        if (!Func.isValidString(mPassWord.getText().toString())) {
            mPassWord.setError("패스워드를 입력해 주세요.");
            mPassWord.requestFocus();
            showKeyBoard();
            return;
        }

        hideKeyBoard();
        updateDB(Define.LOGINTYPE_LOGISHUB, mLoginID.getText().toString().trim(), mPassWord.getText().toString().trim(), mPassWord.getText().toString().trim(), "", "");
        loginProcess(Define.LOGINTYPE_LOGISHUB);
    }

    private void onGoogleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void onKakaoLogin() {
        Session.getCurrentSession().open(AuthType.KAKAO_TALK, this);
    }

    private void onNaveLogin() {
        mOAuthLoginModule.startOauthLoginActivity(LoginActivity.this, mOAuthLoginHandler);
    }

    /** FaceBook Login */
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                        }
                        hideProgressDialog();
                    }
                });
    }

    protected void requestKakao() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    Log.d("kakao", "Login Fail.");

                } else {
                    Log.d("kakao", "Login Fail.");
                }
            }
            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onNotSignedUp() {
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile : " + userProfile);
                updateDB(Define.LOGINTYPE_KAKAO, Long.toString(userProfile.getId()), "", "", userProfile.getNickname(), userProfile.getProfileImagePath());
                loginProcess(Define.LOGINTYPE_KAKAO);
            }
        });
    }

    /** Kakao Login */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    /** Naver Login */
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean b) {
            if (b) {
                String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                updateDB(Define.LOGINTYPE_NAVER, accessToken, "", "", "", "");
                loginProcess(Define.LOGINTYPE_NAVER);
            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                if (!errorCode.equals("user_cancel")) {
                    new CustomAlertDialog(LoginActivity.this, "알림\n\n" + errorDesc);
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                onLogin();
                FirebaseCrash.log("btn_login click");
                break;
            case R.id.btnKakaologin:
                hideKeyBoard();
                onKakaoLogin();
                FirebaseCrash.log("btnKakaologin click");
                break;
            case R.id.btnNaverLogin:
                hideKeyBoard();
                onNaveLogin();
                FirebaseCrash.log("btnNaverLogin click");
                break;
            case R.id.btnGoogleLogin:
                hideKeyBoard();
                onGoogleLogin();
                FirebaseCrash.log("btnGoogleLogin click");
                break;
            case R.id.btnFaceBookLogin:
                hideKeyBoard();
                btnFaceBookLogin.performClick();
                FirebaseCrash.log("btnFaceBookLogin click");
                break;
            case R.id.txtSignUp:
                hideKeyBoard();
                startActivity(new Intent(getApplication(), AgreementActivity.class));
                FirebaseCrash.log("txtSignUp click");
                break;
            case R.id.txtFindPW:
                hideKeyBoard();
                startActivity(new Intent(getApplication(), FindPasswordActivity.class));
                FirebaseCrash.log("txtFindPW click");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Google Sign In
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            //kakao Sign In
            if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {

                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.d("KAKAO", errorResult.toString());
                }

                @Override
                public void onNotSignedUp() {
                    Log.d("KAKAO", "Not Sign Up");
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    Log.d("UserProfile", userProfile.toString());
                    updateDB(Define.LOGINTYPE_KAKAO, Long.toString(userProfile.getId()), "", "", userProfile.getNickname(), userProfile.getProfileImagePath());
                    loginProcess(Define.LOGINTYPE_KAKAO);
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
        }
    }

    private void redirectMainActivity() {

        if (mLoginSessionData.getLoginType().equals(Define.LOGINTYPE_LOGISHUB)) {
            LoginPrefs.setValue(Define.LOGIN_ID, mLoginID.getText().toString());
            LoginPrefs.setValue(Define.LOGIN_PASS, mPassWord.getText().toString());
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(Define.ACT_PUT_REQ_NICK_NAME, mLoginSessionData.getLoginUserName());
        intent.putExtra(Define.ACT_PUT_REQ_USER_LIST, mArrUserList);
        intent.putExtra(Define.ACT_PUT_REQ_MENU_LIST, mArrMenuList);
        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_LIST, mArrServiceList);
        intent.putExtra(Define.ACT_PUT_REQ_MESSAGE_LIST, mArrMessageList);
        intent.putExtra(Define.ACT_PUT_REQ_COMMUNITY_LIST, mArrCommunityList);
        intent.putExtra(Define.ACT_PUT_REQ_PUSH_LINK, mPushLink);
        intent.putExtra(Define.ACT_PUT_REQ_PUSH_TYPE, mPushType);
        startActivity(intent);
        finish();
    }

    public void updateDB(String LoginType, String LoginUserID, String LoginUserLocalPassword, String LoginUserPassword, String LoginUserName, String LoginUserLoginUrl) {
        mLoginSessionDb.DeleteLoginSessionData();
        mLoginSessionData.setLoginType(LoginType);
        mLoginSessionData.setLoginUserID(LoginUserID);
        mLoginSessionData.setLoginUserLocalPassword(LoginUserLocalPassword);
        mLoginSessionData.setLoginUserPassword(LoginUserPassword);
        mLoginSessionData.setLoginUserName(LoginUserName);
        mLoginSessionData.setLoginUserImageUrl(LoginUserLoginUrl);
        mLoginSessionData.setLoginUserDeviceToken(mDeviceToken);
        mLoginSessionData.setLoginUserPhoneNumber(mPhoneNumber);
        mLoginSessionDb.CreateLoginSessionData(mLoginSessionData);
    }

    private void loginProcess(final String sLoginType) {
        showProgressDialog();
        mLoginType = sLoginType;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (sLoginType.equals(Define.LOGINTYPE_LOGISHUB)) {
                        getUser();
                    } else {
                        if (!mPhoneNumber.equals("")) {
                            //getUserSNS();
                            checkAlreadyMember();
                        } else {
                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_PHONE_NUMBER_EMPTY);
                            hideProgressDialog();
                        }
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                    hideProgressDialog();
                }
            }
        }).start();
    }

    /** SNS Login시 회원여부 판단*/
    private void checkAlreadyMember() {
        ArrayList<RequestUserData> userLists = new ArrayList<RequestUserData>();
        RequestUserData requestUserData = new RequestUserData();
        RequestUserRegisterData requestUserRegisterData = new RequestUserRegisterData();
        requestUserData.setMobilePhone(Func.checkStringNull(mPhoneNumber.replace("+82", "0")));
        userLists.add(requestUserData);
        requestUserRegisterData.setUserList(userLists);

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<List<ResponseUserRegisterData>> call = mHttpService.checkAlreadyMember(requestUserRegisterData);
        call.enqueue(new Callback<List<ResponseUserRegisterData>>() {
            @Override
            public void onResponse(Call<List<ResponseUserRegisterData>> call, Response<List<ResponseUserRegisterData>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ResponseUserRegisterData> data = response.body();

                        if (data.get(0).getErrorCode().equals("0")) {
                            /** 신규 */
                            if (data.get(0).getNewUser().equals("1") && mUserName == null && mUserType == null) {
                                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_CHECK_ALREADY_MEMBER_NOT_YET);
                            } else {
                                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_CHECK_ALREADY_MEMBER_YET);
                            }
                        }
                        else {
                            mErrorMessage = Func.checkStringNull(data.get(0).getErrorContent());
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
            public void onFailure(Call<List<ResponseUserRegisterData>> call, Throwable t) {
                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
            }

        });
    }

    private void getUser() {
        ArrayList<RequestUserData> userLists = new ArrayList<RequestUserData>();
        RequestUserData requestUserData = new RequestUserData();
        RequestUserRegisterData requestUserRegisterData = new RequestUserRegisterData();
        requestUserData.setId(Func.checkStringNull(mLoginSessionData.getLoginUserID()));
        requestUserData.setPassword(Func.checkStringNull(mPassWord.getText().toString()));
        requestUserData.setLoginType(Func.checkStringNull(mLoginSessionData.getLoginType()));
        requestUserData.setLoginKey("");
        requestUserData.setDeviceId(Func.checkStringNull(mDeviceToken));
        userLists.add(requestUserData);
        requestUserRegisterData.setUserList(userLists);

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<List<ResponseUserRegisterData>> call = mHttpService.getUser(requestUserRegisterData);
        call.enqueue(new Callback<List<ResponseUserRegisterData>>() {
            @Override
            public void onResponse(Call<List<ResponseUserRegisterData>> call, Response<List<ResponseUserRegisterData>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ResponseUserRegisterData> data = response.body();

                        if (data.get(0).getErrorCode().equals("0")) {
                            mArrUserList.clear();

                            UserData userData = new UserData();
                            userData.setUserOid(Func.checkStringNull(data.get(0).getOid()));
                            userData.setUserID(Func.checkStringNull(data.get(0).getId()));
                            userData.setUserName(Func.checkStringNull(data.get(0).getName()));
                            userData.setUserPassWord(Func.checkStringNull(data.get(0).getPassword()));
                            userData.setUserKey(Func.checkStringNull(data.get(0).getKey()));
                            userData.setUserBelongTo(Func.checkStringNull(data.get(0).getBelongTo()));
                            userData.setUserCagill(Func.checkStringNull(data.get(0).getCargillUser()));
                            mArrUserList.add(userData);

                            mArrMenuList.clear();
                            int mMenuSize = data.get(0).getMenuList().size();
                            if (mMenuSize > 0) {
                                for (int i = 0; i < mMenuSize; i++) {
                                    MenuData menuData = new MenuData();
                                    menuData.setId(Func.checkStringNull(data.get(0).getMenuList().get(i).getId()));
                                    menuData.setName(Func.checkStringNull(data.get(0).getMenuList().get(i).getName()));
                                    menuData.setLevel(Func.checkStringNull(data.get(0).getMenuList().get(i).getLevel()));
                                    menuData.setParentId(Func.checkStringNull(data.get(0).getMenuList().get(i).getParentId()));
                                    menuData.setProgramName(Func.checkStringNull(data.get(0).getMenuList().get(i).getProgramName()));
                                    menuData.setClassName(Func.checkStringNull(data.get(0).getMenuList().get(i).getClassName()));
                                    menuData.setDescription(Func.checkStringNull(data.get(0).getMenuList().get(i).getDescription()));
                                    mArrMenuList.add(menuData);
                                }
                            }

                            mArrServiceList.clear();
                            int mServiceSize = data.get(0).getServiceList().size();
                            if (mServiceSize > 0) {
                                for (int i = 0; i < mServiceSize; i++) {
                                    ServiceData serviceData = new ServiceData();
                                    serviceData.setServiceOid(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceOid()));
                                    serviceData.setServiceStatus(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceStatus()));
                                    serviceData.setServiceType(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceType()));
                                    serviceData.setServiceSite(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceSite()));
                                    serviceData.setServiceUrl(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceUrl()));
                                    serviceData.setServiceImgName(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceImgName()));
                                    serviceData.setServiceName(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceName()));
                                    serviceData.setHasImage(Func.checkStringNull(data.get(0).getServiceList().get(i).getHasImage()));
                                    serviceData.setIsManager(Func.checkStringNull(data.get(0).getServiceList().get(i).getIsManager()));
                                    mArrServiceList.add(serviceData);
                                }
                            }

                            mArrCommunityList.clear();
                            int mCommunitySize = data.get(0).getCommunityList().size();
                            if (mCommunitySize > 0) {
                                for (int i = 0; i < mCommunitySize; i++) {
                                    CommunityListData communityListData = new CommunityListData();
                                    communityListData.setOid(Func.checkStringNull(data.get(0).getCommunityList().get(i).getOid()));
                                    communityListData.setCommunityName(Func.checkStringNull(data.get(0).getCommunityList().get(i).getCommunityName()));
                                    communityListData.setCommunityLevel(Func.checkStringNull(data.get(0).getCommunityList().get(i).getCommunityLevel()));
                                    communityListData.setiLParentOrganization(Func.checkStringNull(data.get(0).getCommunityList().get(i).getiLParentOrganization()));
                                    communityListData.setCommunityId(Func.checkStringNull(data.get(0).getCommunityList().get(i).getCommunityId()));
                                    communityListData.setServiceUrl(Func.checkStringNull(data.get(0).getCommunityList().get(i).getServiceUrl()));
                                    communityListData.setSite(Func.checkStringNull(data.get(0).getCommunityList().get(i).getSite()));
                                    communityListData.setCreated(Func.checkStringNull(data.get(0).getCommunityList().get(i).getCreated()));
                                    communityListData.setLeaderOid(Func.checkStringNull(data.get(0).getCommunityList().get(i).getLeaderOid()));
                                    communityListData.setLeader(Func.checkStringNull(data.get(0).getCommunityList().get(i).getLeader()));
                                    communityListData.setMemberCount(Func.checkStringNull(data.get(0).getCommunityList().get(i).getMemberCount()));
                                    communityListData.setNewArticleCount(Func.checkStringNull(data.get(0).getCommunityList().get(i).getNewArticleCount()));
                                    communityListData.setLastArticleCreatedTime(Func.checkStringNull(data.get(0).getCommunityList().get(i).getLastArticleCreatedTime()));
                                    communityListData.setIsDefault(Func.checkStringNull(data.get(0).getCommunityList().get(i).getIsDefault()));
                                    communityListData.setUseSubOrganization(Func.checkStringNull(data.get(0).getCommunityList().get(i).getUseSubOrganization()));
                                    communityListData.setServiceType(Func.checkStringNull(data.get(0).getCommunityList().get(i).getServiceType()));
                                    mArrCommunityList.add(communityListData);
                                }
                            }

                            mArrMessageList.clear();
                            int mMessageSize = data.get(0).getMessageList().size();
                            if (mMessageSize > 0) {
                                for (int i = 0; i < mMessageSize; i++) {
                                    MessageListData messageData = new MessageListData();
                                    messageData.setOid(Func.checkStringNull(data.get(0).getMessageList().get(i).getOid()));
                                    messageData.setType(Func.checkStringNull(data.get(0).getMessageList().get(i).getType()));
                                    messageData.setDate(Func.checkStringNull(data.get(0).getMessageList().get(i).getDate()));
                                    messageData.setTime(Func.checkStringNull(data.get(0).getMessageList().get(i).getTime()));
                                    messageData.setMobilePhone(Func.checkStringNull(data.get(0).getMessageList().get(i).getMobilePhone()));
                                    messageData.setTitle(Func.checkStringNull(data.get(0).getMessageList().get(i).getTitle()));
                                    messageData.setContent(Func.checkStringNull(data.get(0).getMessageList().get(i).getContent()));
                                    messageData.setServiceUrl(Func.checkStringNull(data.get(0).getMessageList().get(i).getServiceUrl()));
                                    messageData.setSiteCode(Func.checkStringNull(data.get(0).getMessageList().get(i).getSiteCode()));
                                    messageData.setTransOid(Func.checkStringNull(data.get(0).getMessageList().get(i).getTransOid()));
                                    messageData.setTransDate(Func.checkStringNull(data.get(0).getMessageList().get(i).getTransDate()));
                                    messageData.setOrgID(Func.checkStringNull(data.get(0).getMessageList().get(i).getOrgID()));
                                    messageData.setOrgName(Func.checkStringNull(data.get(0).getMessageList().get(i).getOrgName()));
                                    messageData.setInvitation(Func.checkStringNull(data.get(0).getMessageList().get(i).getInvitation()));
                                    messageData.setCheckOrg(Func.checkStringNull(data.get(0).getMessageList().get(i).getCheckOrg()));
                                    messageData.setCheckOrgStatus(Func.checkStringNull(data.get(0).getMessageList().get(i).getCheckOrgStatus()));
                                    messageData.setCarrierName(Func.checkStringNull(data.get(0).getMessageList().get(i).getCarrierName()));
                                    messageData.setTurnInfo(Func.checkStringNull(data.get(0).getMessageList().get(i).getTurnInfo()));
                                    messageData.setTurnID(Func.checkStringNull(data.get(0).getMessageList().get(i).getTurnID()));
                                    messageData.setOrgOid(Func.checkStringNull(data.get(0).getMessageList().get(i).getOrgOid()));
                                    messageData.setUserToOrgLinkOid(Func.checkStringNull(data.get(0).getMessageList().get(i).getUserToOrgLinkOid()));
                                    mArrMessageList.add(messageData);
                                }
                            }

                            if (mLoginSessionData.getLoginType().equals(Define.LOGINTYPE_LOGISHUB)) {
                                updateDB(Define.LOGINTYPE_LOGISHUB, mArrUserList.get(0).getUserID(), mPassWord.getText().toString(), mArrUserList.get(0).getUserPassWord(), mArrUserList.get(0).getUserName(), "");
                            }

                            if (getAppVersion().equals(data.get(0).getAppVersion())) {
                                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS);
                            } else {
                                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_UPGRADE);
                            }
                        }
                        else {
                            mErrorMessage = Func.checkStringNull(data.get(0).getErrorContent());
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
            public void onFailure(Call<List<ResponseUserRegisterData>> call, Throwable t) {
                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
            }

        });
    }

    private void getUserSNS() {

        if (!mLoginType.equals(Define.LOGINTYPE_FACEBOOK)) {
            showProgressDialog();
        }

        ArrayList<RequestUserData> userLists = new ArrayList<RequestUserData>();
        RequestUserData requestUserData = new RequestUserData();
        RequestUserRegisterData requestUserRegisterData = new RequestUserRegisterData();
        requestUserData.setMobilePhone(Func.checkStringNull(mPhoneNumber.replace("+82", "0")));
        requestUserData.setLoginType(Func.checkStringNull(mLoginSessionData.getLoginType()));
        requestUserData.setLoginKey(Func.checkStringNull(mLoginSessionData.getLoginUserID()));
        requestUserData.setDeviceId(Func.checkStringNull(mDeviceToken));
        requestUserData.setName(mUserName);
        requestUserData.setBelongTo(mUserType);
        userLists.add(requestUserData);
        requestUserRegisterData.setUserList(userLists);

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<List<ResponseUserRegisterData>> call = mHttpService.getUserSNS(requestUserRegisterData);
        call.enqueue(new Callback<List<ResponseUserRegisterData>>() {
            @Override
            public void onResponse(Call<List<ResponseUserRegisterData>> call, Response<List<ResponseUserRegisterData>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ResponseUserRegisterData> data = response.body();

                        if (data.get(0).getErrorCode().equals("0")) {
                            mArrUserList.clear();

                            UserData userData = new UserData();
                            userData.setUserOid(Func.checkStringNull(data.get(0).getOid()));
                            userData.setUserID(Func.checkStringNull(data.get(0).getId()));
                            userData.setUserName(Func.checkStringNull(data.get(0).getName()));
                            userData.setUserPassWord(Func.checkStringNull(data.get(0).getPassword()));
                            userData.setUserKey(Func.checkStringNull(data.get(0).getKey()));
                            userData.setUserBelongTo(Func.checkStringNull(data.get(0).getBelongTo()));
                            userData.setUserCagill(Func.checkStringNull(data.get(0).getCargillUser()));
                            mArrUserList.add(userData);

                            mArrMenuList.clear();
                            int mMenuSize = data.get(0).getMenuList().size();
                            if (mMenuSize > 0) {
                                for (int i = 0; i < mMenuSize; i++) {
                                    MenuData menuData = new MenuData();
                                    menuData.setId(Func.checkStringNull(data.get(0).getMenuList().get(i).getId()));
                                    menuData.setName(Func.checkStringNull(data.get(0).getMenuList().get(i).getName()));
                                    menuData.setLevel(Func.checkStringNull(data.get(0).getMenuList().get(i).getLevel()));
                                    menuData.setParentId(Func.checkStringNull(data.get(0).getMenuList().get(i).getParentId()));
                                    menuData.setProgramName(Func.checkStringNull(data.get(0).getMenuList().get(i).getProgramName()));
                                    menuData.setClassName(Func.checkStringNull(data.get(0).getMenuList().get(i).getClassName()));
                                    menuData.setDescription(Func.checkStringNull(data.get(0).getMenuList().get(i).getDescription()));
                                    mArrMenuList.add(menuData);
                                }
                            }

                            mArrServiceList.clear();
                            int mServiceSize = data.get(0).getServiceList().size();
                            if (mServiceSize > 0) {
                                for (int i = 0; i < mServiceSize; i++) {
                                    ServiceData serviceData = new ServiceData();
                                    serviceData.setServiceOid(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceOid()));
                                    serviceData.setServiceStatus(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceStatus()));
                                    serviceData.setServiceType(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceType()));
                                    serviceData.setServiceSite(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceSite()));
                                    serviceData.setServiceUrl(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceUrl()));
                                    serviceData.setServiceImgName(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceImgName()));
                                    serviceData.setServiceName(Func.checkStringNull(data.get(0).getServiceList().get(i).getServiceName()));
                                    serviceData.setHasImage(Func.checkStringNull(data.get(0).getServiceList().get(i).getHasImage()));
                                    serviceData.setIsManager(Func.checkStringNull(data.get(0).getServiceList().get(i).getIsManager()));
                                    serviceData.setIsFNSLike(Func.checkStringNull(data.get(0).getServiceList().get(i).getIsFNSLike()));
                                    mArrServiceList.add(serviceData);
                                }
                            }

                            mArrCommunityList.clear();
                            int mCommunitySize = data.get(0).getCommunityList().size();
                            if (mCommunitySize > 0) {
                                for (int i = 0; i < mCommunitySize; i++) {
                                    CommunityListData communityListData = new CommunityListData();
                                    communityListData.setOid(Func.checkStringNull(data.get(0).getCommunityList().get(i).getOid()));
                                    communityListData.setCommunityName(Func.checkStringNull(data.get(0).getCommunityList().get(i).getCommunityName()));
                                    communityListData.setCommunityLevel(Func.checkStringNull(data.get(0).getCommunityList().get(i).getCommunityLevel()));
                                    communityListData.setiLParentOrganization(Func.checkStringNull(data.get(0).getCommunityList().get(i).getiLParentOrganization()));
                                    communityListData.setCommunityId(Func.checkStringNull(data.get(0).getCommunityList().get(i).getCommunityId()));
                                    communityListData.setServiceUrl(Func.checkStringNull(data.get(0).getCommunityList().get(i).getServiceUrl()));
                                    communityListData.setSite(Func.checkStringNull(data.get(0).getCommunityList().get(i).getSite()));
                                    communityListData.setCreated(Func.checkStringNull(data.get(0).getCommunityList().get(i).getCreated()));
                                    communityListData.setLeaderOid(Func.checkStringNull(data.get(0).getCommunityList().get(i).getLeaderOid()));
                                    communityListData.setLeader(Func.checkStringNull(data.get(0).getCommunityList().get(i).getLeader()));
                                    communityListData.setMemberCount(Func.checkStringNull(data.get(0).getCommunityList().get(i).getMemberCount()));
                                    communityListData.setNewArticleCount(Func.checkStringNull(data.get(0).getCommunityList().get(i).getNewArticleCount()));
                                    communityListData.setLastArticleCreatedTime(Func.checkStringNull(data.get(0).getCommunityList().get(i).getLastArticleCreatedTime()));
                                    communityListData.setIsDefault(Func.checkStringNull(data.get(0).getCommunityList().get(i).getIsDefault()));
                                    communityListData.setUseSubOrganization(Func.checkStringNull(data.get(0).getCommunityList().get(i).getUseSubOrganization()));
                                    communityListData.setServiceType(Func.checkStringNull(data.get(0).getCommunityList().get(i).getServiceType()));
                                    mArrCommunityList.add(communityListData);
                                }
                            }

                            mArrMessageList.clear();
                            int mMessageSize = data.get(0).getMessageList().size();
                            if (mMessageSize > 0) {
                                for (int i = 0; i < mMessageSize; i++) {
                                    MessageListData messageData = new MessageListData();
                                    messageData.setOid(Func.checkStringNull(data.get(0).getMessageList().get(i).getOid()));
                                    messageData.setType(Func.checkStringNull(data.get(0).getMessageList().get(i).getType()));
                                    messageData.setDate(Func.checkStringNull(data.get(0).getMessageList().get(i).getDate()));
                                    messageData.setTime(Func.checkStringNull(data.get(0).getMessageList().get(i).getTime()));
                                    messageData.setMobilePhone(Func.checkStringNull(data.get(0).getMessageList().get(i).getMobilePhone()));
                                    messageData.setTitle(Func.checkStringNull(data.get(0).getMessageList().get(i).getTitle()));
                                    messageData.setContent(Func.checkStringNull(data.get(0).getMessageList().get(i).getContent()));
                                    messageData.setServiceUrl(Func.checkStringNull(data.get(0).getMessageList().get(i).getServiceUrl()));
                                    messageData.setSiteCode(Func.checkStringNull(data.get(0).getMessageList().get(i).getSiteCode()));
                                    messageData.setTransOid(Func.checkStringNull(data.get(0).getMessageList().get(i).getTransOid()));
                                    messageData.setTransDate(Func.checkStringNull(data.get(0).getMessageList().get(i).getTransDate()));
                                    messageData.setOrgID(Func.checkStringNull(data.get(0).getMessageList().get(i).getOrgID()));
                                    messageData.setOrgName(Func.checkStringNull(data.get(0).getMessageList().get(i).getOrgName()));
                                    messageData.setInvitation(Func.checkStringNull(data.get(0).getMessageList().get(i).getInvitation()));
                                    messageData.setCheckOrg(Func.checkStringNull(data.get(0).getMessageList().get(i).getCheckOrg()));
                                    messageData.setCheckOrgStatus(Func.checkStringNull(data.get(0).getMessageList().get(i).getCheckOrgStatus()));
                                    messageData.setCarrierName(Func.checkStringNull(data.get(0).getMessageList().get(i).getCarrierName()));
                                    messageData.setTurnInfo(Func.checkStringNull(data.get(0).getMessageList().get(i).getTurnInfo()));
                                    messageData.setTurnID(Func.checkStringNull(data.get(0).getMessageList().get(i).getTurnID()));
                                    messageData.setOrgOid(Func.checkStringNull(data.get(0).getMessageList().get(i).getOrgOid()));
                                    messageData.setUserToOrgLinkOid(Func.checkStringNull(data.get(0).getMessageList().get(i).getUserToOrgLinkOid()));
                                    mArrMessageList.add(messageData);
                                }
                            }

                            if (getAppVersion().equals(data.get(0).getAppVersion())) {
                                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS);
                            } else {
                                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_UPGRADE);
                            }
                        }
                        else {
                            mErrorMessage = Func.checkStringNull(data.get(0).getErrorContent());
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
            public void onFailure(Call<List<ResponseUserRegisterData>> call, Throwable t) {
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
                    redirectMainActivity();
                    break;
                case Define.HANDLER_MESSAGE_ERROR:
                    mLoginSessionDb.DeleteLoginSessionData();
                    onKakaoLogOut();
                    onGoogleLogOut();
                    onFaceBookLogOut();
                    onNaverLogOut();
                    new CustomAlertDialog(LoginActivity.this, "알림\n\n" + mErrorMessage);
                    break;
                case Define.HANDLER_MESSAGE_FAIL:
                    mLoginSessionDb.DeleteLoginSessionData();
                    onKakaoLogOut();
                    onGoogleLogOut();
                    onFaceBookLogOut();
                    onNaverLogOut();
                    new CustomAlertDialog(LoginActivity.this, "알림" + "\n\n잠시 후 다시 이용해 주시길 바랍니다.");
                    break;
                case Define.HANDLER_MESSAGE_PHONE_NUMBER_EMPTY:
                    mLoginSessionDb.DeleteLoginSessionData();
                    onKakaoLogOut();
                    onGoogleLogOut();
                    onFaceBookLogOut();
                    onNaverLogOut();
                    new CustomAlertDialog(LoginActivity.this, "알림" + "\n\n휴대폰에서 핸드폰 정보를 찾을 수 없습니다.\n관리자에게 문의 하세요.");
                    break;
                case Define.HANDLER_MESSAGE_UPGRADE:
                    mLoginSessionDb.DeleteLoginSessionData();
                    onKakaoLogOut();
                    onGoogleLogOut();
                    onFaceBookLogOut();
                    onNaverLogOut();
                    new CustomAlertUpgradeDialog(LoginActivity.this, "알림" + "\n\n앱이 업데이트 되었습니다.\nPlay 스토어로 이동합니다.");
                    break;
                case Define.HANDLER_MESSAGE_CHECK_ALREADY_MEMBER_NOT_YET:
                    Intent intent = new Intent(getApplicationContext(), WelcomeMessageActivity.class);
                    intent.putExtra(Define.ACT_PUT_REQ_LOGIN_TYPE, mLoginType);
                    startActivity(intent);
                    break;
                case Define.HANDLER_MESSAGE_CHECK_ALREADY_MEMBER_YET:
                    getUserSNS();
                    break;
                default:
                    break;
            }

            return true;
        }
    });

    private String getPhoneNumber()
    {
        TelephonyManager mgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return mgr.getLine1Number();
    }

    private static String getAppVersion() {
        String versionName = "";
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) { }

        return versionName;
    }

    private void showProgressDialog() {
        mProgressDialog = new CustomProgressDialog(LoginActivity.this);
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

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mLoginID.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mPassWord.getWindowToken(), 0);
    }

    private void onKakaoLogOut() {
        try {
            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    mLoginSessionDb.DeleteLoginSessionData();
                }
            });
        } catch (Exception e) { }
    }

    private void onNaverLogOut() {
        try {
            OAuthLogin.getInstance().logout(mContext);
        } catch (Exception e) { }
    }

    public void onGoogleLogOut() {
        try {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            mLoginSessionDb.DeleteLoginSessionData();
                        }
                    });
        } catch (Exception e) { }
    }

    public void onFaceBookLogOut() {
        try {
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            mLoginSessionDb.DeleteLoginSessionData();
        } catch (Exception e) { }
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - pressTime < Define.BACK_PRESS_TIME) {
            finishAffinity();
            return;
        }
        Toast.makeText(this, "한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_LONG).show();
        pressTime = System.currentTimeMillis();
    }
}
