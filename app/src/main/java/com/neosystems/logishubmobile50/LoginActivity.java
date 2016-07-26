package com.neosystems.logishubmobile50;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.FacebookSdk;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.neosystems.logishubmobile50.Common.Define;
import com.neosystems.logishubmobile50.Common.Func;
import com.neosystems.logishubmobile50.DATA.LoginSessionData;
import com.neosystems.logishubmobile50.DB.LoginSessionAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private LoginSessionAdapter mLoginSessionDb = null;
    private LoginSessionData mLoginSessionData = null;
    long pressTime;
    SessionCallback callback;
    private CallbackManager callbackManager;
    /** Google+ */
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** FaceBook Init setContext보다 먼저 호출 */
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mLoginSessionDb = new LoginSessionAdapter(this);
        mLoginSessionDb.open();

        mLoginSessionData = mLoginSessionDb.GetLoginSessionData();

        hideActionBar();

        Intent intent = getIntent();
        String DeviceToken = intent.getExtras().getString("DeviceToken");
        String PhoneNumber = intent.getExtras().getString("PhoneNumber");

        //Toast.makeText(getApplicationContext(), DeviceToken + "/"  + PhoneNumber, Toast.LENGTH_LONG).show();

        findViewById(R.id.textView2).setOnClickListener(this);
        findViewById(R.id.btnGoogleLogin).setOnClickListener(this);

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

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String userImageUrl = "";

            if (acct.getPhotoUrl() != null)
                userImageUrl = acct.getPhotoUrl().getPath();

            redirectMainActivity(Define.LOGINTYPE_GOOGLE, acct.getId(), acct.getDisplayName(), userImageUrl);
        } else {

        }
    }

    private void onGoogleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    /** FaceBook Login */
    private void onFaceBookLogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","user_photos","public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json, GraphResponse response) {
                        if (response.getError() != null) {
                            // handle error
                        } else {
                            try {
                                String jsonresult = String.valueOf(json);
                                //String str_email = json.getString("email");
                                String str_id = json.getString("id");
                                String str_firstname = json.getString("first_name");
                                String str_lastname = json.getString("last_name");

                                redirectMainActivity(Define.LOGINTYPE_FACEBOOK, str_id, str_firstname + str_lastname, "");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }).executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "On cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, error.toString());
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
                redirectMainActivity(Define.LOGINTYPE_KAKAO, Long.toString(userProfile.getId()), userProfile.getNickname(), userProfile.getProfileImagePath());
            }
        });
    }

    /** Kakao Login */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = Func.onCreateProgressDialog(this);
            mProgressDialog.setMessage(Define.LOADING);
            mProgressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGoogleLogin:
                onGoogleLogin();
                break;
            case R.id.textView2:
                startActivity(new Intent(getApplication(), Login2Activity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Google Sing In
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            //kakao Sing In
            if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }

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
                    redirectMainActivity(Define.LOGINTYPE_KAKAO, Long.toString(userProfile.getId()), userProfile.getNickname(), userProfile.getProfileImagePath());
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
        }
    }

    private void redirectMainActivity(String LoginType, String LoginUserID, String LoginUserName, String LoginUserLoginUrl) {
        mLoginSessionDb.DeleteLoginSessionData();
        mLoginSessionData.SetLoginType(LoginType);
        mLoginSessionData.SetLoginUserID(LoginUserID);
        mLoginSessionData.SetLoginUserName(LoginUserName);
        mLoginSessionData.SetLoginUserImageUrl(LoginUserLoginUrl);
        mLoginSessionDb.CreateLoginSessionData(mLoginSessionData);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("userID", LoginUserID);
        intent.putExtra("userNickName", LoginUserName);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - pressTime <2000){
            finishAffinity();
            return;
        }
        Toast.makeText(this, "한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_LONG).show();
        pressTime = System.currentTimeMillis();
    }
}
