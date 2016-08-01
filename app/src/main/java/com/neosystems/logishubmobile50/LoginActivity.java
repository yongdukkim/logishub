package com.neosystems.logishubmobile50;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.FacebookSdk;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.auth.AuthType;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.neosystems.logishubmobile50.Common.CustomProgressDialog;
import com.neosystems.logishubmobile50.Common.Define;
import com.neosystems.logishubmobile50.DATA.LoginSessionData;
import com.neosystems.logishubmobile50.DATA.UserData;
import com.neosystems.logishubmobile50.DB.LoginSessionAdapter;
import com.neosystems.logishubmobile50.Task.VehicleOperationTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private LoginSessionAdapter mLoginSessionDb = null;
    private LoginSessionData mLoginSessionData = null;
    long pressTime;
    public static String mDeviceToken;
    public static String mPhoneNumber;
    public static ArrayList<UserData> mArrUserList = null;
    public static Context context;

    /** Kakao */
    SessionCallback callback;

    /** Google+ */
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private CustomProgressDialog mProgressDialog;

    /** FaceBook */
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private LoginButton btnFaceBookLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** FaceBook Init setContext보다 먼저 호출 */
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        onOpenDB();

        hideActionBar();

        setLayout();

        /** Google Api Load */
        onLoadGoogleApi();

        /** kakao Api Load */
        onLoadKakaoApi();

        /** kakao User Check */
        requestKakao();
    }

    /** Action Bar Hide */
    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onResume() {
        AppEventsLogger.activateApp(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        AppEventsLogger.deactivateApp(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Session.getCurrentSession().removeCallback(callback);

        if (mLoginSessionDb != null)
        {
            mLoginSessionDb.close();
            mLoginSessionDb = null;
        }
    }

    private void setLayout() {
        Intent intent = getIntent();
        mDeviceToken = intent.getExtras().getString(Define.ACT_PUT_REQ_DEVICE_TOKEN);
        mPhoneNumber = intent.getExtras().getString(Define.ACT_PUT_REQ_PHONE_NUMBER);

        mArrUserList = new ArrayList<UserData>();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String userImageUrl = "";

                    if (user.getPhotoUrl() != null)
                        userImageUrl = user.getPhotoUrl().toString();

                    updateDB(Define.LOGINTYPE_FACEBOOK, user.getUid(), user.getDisplayName(), userImageUrl);
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

        findViewById(R.id.btnKakaologin).setOnClickListener(this);
        findViewById(R.id.btnGoogleLogin).setOnClickListener(this);
        findViewById(R.id.btnFaceBookLogin).setOnClickListener(this);
        findViewById(R.id.textView2).setOnClickListener(this);
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

    private void onLoadGoogleApi() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    private void onLoadKakaoApi() {
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }

    /** Google Login */
    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
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
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String userImageUrl = "";

            if (acct.getPhotoUrl() != null)
                userImageUrl = acct.getPhotoUrl().toString();

            updateDB(Define.LOGINTYPE_GOOGLE, acct.getId(), acct.getDisplayName(), userImageUrl);
        } else {

        }
    }

    private void onGoogleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void onKakaoLogin() {
        Session.getCurrentSession().open(AuthType.KAKAO_TALK, this);
    }

    /** FaceBook Login */
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        showProgressDialog();

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
                    finish();
                } else {
                    Log.d("kakao", "Login Fail.");
                }
            }
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("kakao", "Login Session Close.");
            }

            @Override
            public void onNotSignedUp() {
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile : " + userProfile);
                updateDB(Define.LOGINTYPE_KAKAO, Long.toString(userProfile.getId()), userProfile.getNickname(), userProfile.getProfileImagePath());
            }
        });
    }

    /** Kakao Login */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnKakaologin:
                onKakaoLogin();
                break;
            case R.id.btnGoogleLogin:
                onGoogleLogin();
                break;
            case R.id.btnFaceBookLogin:
                btnFaceBookLogin.performClick();
                break;
            case R.id.textView2:
                startActivity(new Intent(getApplication(), Login2Activity.class));
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
                        finish();
                    } else {
                        //redirectMainActivity();
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
                    Log.d("UserProfile", userProfile.toString());
                    updateDB(Define.LOGINTYPE_KAKAO, Long.toString(userProfile.getId()), userProfile.getNickname(), userProfile.getProfileImagePath());
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
        }
    }

    private void redirectMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(Define.ACT_PUT_REQ_ID, mArrUserList.get(0).getUserID());
        intent.putExtra(Define.ACT_PUT_REQ_PASSWORD, mArrUserList.get(0).getUserPassWord());
        intent.putExtra(Define.ACT_PUT_REQ_NICK_NAME, mLoginSessionData.GetLoginUserName());
        startActivity(intent);
        finish();
    }

    public void updateDB(String LoginType, String LoginUserID, String LoginUserName, String LoginUserLoginUrl) {
        mLoginSessionDb.DeleteLoginSessionData();
        mLoginSessionData.SetLoginType(LoginType);
        mLoginSessionData.SetLoginUserID(LoginUserID);
        mLoginSessionData.SetLoginUserName(LoginUserName);
        mLoginSessionData.SetLoginUserImageUrl(LoginUserLoginUrl);
        mLoginSessionData.SetLoginUserDeviceToken(mDeviceToken);
        mLoginSessionData.SetLoginUserPhoneNumber(mPhoneNumber);
        mLoginSessionDb.CreateLoginSessionData(mLoginSessionData);

        String param = "?MobilePhone=" + mPhoneNumber;
        new LoginTask().execute(Define.LOGISHUB_DEFAULT_URL + Define.LOGISHUB_LOGIN + param);
    }

    public class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            showProgressDialog();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try
            {
                if (result != "") {
                    JSONArray jsonArray = new JSONArray(result);
                    mArrUserList.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        UserData Data = new UserData();
                        JSONObject jObject = jsonArray.getJSONObject(i);

                        Data.setUserID(jObject.getString("ID"));
                        Data.setUserName(jObject.getString("NAME"));
                        Data.setUserPassWord(jObject.getString("PASSWORD"));

                        mArrUserList.add(Data);
                        redirectMainActivity();
                    }
                } else {
                    Toast.makeText(LoginActivity.context, "데이터가 없습니다.",Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e)
            {
                Log.d("ex", e.getLocalizedMessage());
            }
            finally {
                if(mProgressDialog != null)
                    hideProgressDialog();
            }
        }

        public String GET(String urlString) {
            InputStream inputStream = null;
            String result = "";
            try
            {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                inputStream = new BufferedInputStream(urlConnection.getInputStream());

                if(inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Empty";

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }

            return result;
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;
        }
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - pressTime < 2000) {
            finishAffinity();
            return;
        }
        Toast.makeText(this, "한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_LONG).show();
        pressTime = System.currentTimeMillis();
    }
}
