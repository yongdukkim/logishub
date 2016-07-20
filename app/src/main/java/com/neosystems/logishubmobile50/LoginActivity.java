package com.neosystems.logishubmobile50;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.neosystems.logishubmobile50.Common.Func;

public class LoginActivity extends Activity {
    long pressTime;
    SessionCallback callback;
    private ProgressDialog m_progDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        String DeviceToken = intent.getExtras().getString("DeviceToken");
        String PhoneNumber = intent.getExtras().getString("PhoneNumber");

        Toast.makeText(getApplicationContext(), DeviceToken + "/"  + PhoneNumber, Toast.LENGTH_LONG).show();

        TextView txtAddOption = (TextView) findViewById(R.id.textView2);
        txtAddOption.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), Login2Activity.class));
            }
        });

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        m_progDialog = Func.onCreateProgressDialog(this);
        m_progDialog.setMessage("카카오 로그인중...");
        m_progDialog.show();

        //간편로그인시 호출 ,없으면 간편로그인시 로그인 성공화면으로 넘어가지 않음
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
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

                    if(m_progDialog != null)
                        m_progDialog.dismiss();
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    if(m_progDialog != null)
                        m_progDialog.dismiss();
                }

                @Override
                public void onNotSignedUp() {
                    if(m_progDialog != null)
                        m_progDialog.dismiss();
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    Log.d("UserProfile", userProfile.toString());

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("userProfile", userProfile.toString());
                    startActivity(intent);
                    LoginActivity.this.finish();

                    if(m_progDialog != null)
                        m_progDialog.dismiss();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때

            if(m_progDialog != null)
                m_progDialog.dismiss();
        }
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
