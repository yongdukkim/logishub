package com.logishub.mobile.launcher.v5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;

public class AuthPhone2Activity extends AppCompatActivity implements View.OnClickListener {

    private CustomProgressDialog mProgressDialog;
    private String mPhoneNumber;
    private String mAuthNumber;
    private EditText etAuthNumber;
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_phone2);

        setLayout();
    }

    private void setLayout() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.actionbar_main);
        }

        Intent intent = getIntent();
        mPhoneNumber = intent.getExtras().getString(Define.ACT_PUT_REQ_PHONE_NUMBER);
        mAuthNumber = intent.getExtras().getString(Define.ACT_PUT_REQ_AUTH_NUMBER);

        //Toast.makeText(this, mAuthNumber,Toast.LENGTH_LONG).show();
        Log.d("AuthNum", mAuthNumber);

        etAuthNumber = (EditText) findViewById(R.id.etAuthNumber);
        findViewById(R.id.btn_auth_confirm).setOnClickListener(this);

        /** SMS Receiver 등록 */
        registerReceiver(SMSReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED" ));

        showProgressDialog();

        countDownTimer();
        countDownTimer.start();
    }

    @Override
    public void onStart() {
        super.onStart();
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

        unregisterReceiver(SMSReceiver);

        try {
            countDownTimer.cancel();
        }
        catch (Exception e) {

        }
        countDownTimer=null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_auth_confirm:
                onAuthConfirm();
                break;
        }
    }

    private void onAuthConfirm() {
        if (!Func.isValidString(etAuthNumber.getText().toString())) {
            etAuthNumber.setError("인증번호를 입력해 주세요.");
            etAuthNumber.requestFocus();
            showKeyBoard();
            return;
        }

        if (mAuthNumber.equals(etAuthNumber.getText().toString())) {
            redirectRegisterActivity();
        }
        else {
            etAuthNumber.setError("인증번호가 맞지 않습니다.");
            etAuthNumber.requestFocus();
            showKeyBoard();
        }

        hideKeyBoard();
    }

    /** SMS Receiver */
    BroadcastReceiver SMSReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SMS_RECEIVED)) {

                Bundle bundle = intent.getExtras();
                Object messages[] = (Object[])bundle.get("pdus");
                SmsMessage smsMessage[] = new SmsMessage[messages.length];

                for(int i = 0; i < messages.length; i++) {
                    smsMessage[i] = SmsMessage.createFromPdu((byte[])messages[i]);
                }

                String origNumber = smsMessage[0].getOriginatingAddress();
                String message = smsMessage[0].getMessageBody().toString();

                if (origNumber.equals(Define.SMS_RECEIVE_TEL)) {
                    etAuthNumber.setText(Func.isValidNumber(message));
                    Toast.makeText(getApplicationContext(), "인증번호가 수신 되었습니다.", Toast.LENGTH_LONG).show();

                    if (mAuthNumber.equals(Func.isValidNumber(message))) {
                        hideProgressDialog();
                        redirectRegisterActivity();
                    }
                    else {
                        etAuthNumber.setError("인증번호가 맞지 않습니다.");
                        etAuthNumber.requestFocus();
                        showKeyBoard();
                    }
                }
            }
        }
    };

    private void redirectRegisterActivity() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        intent.putExtra(Define.ACT_PUT_REQ_PHONE_NUMBER, mPhoneNumber);
        startActivity(intent);
        finish();
    }

    public void countDownTimer() {
        countDownTimer = new CountDownTimer(Define.SMS_RECEIVE_MILLISINFUTURE, Define.SMS_RECEIVE_COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                hideProgressDialog();
            }
        };
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

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etAuthNumber.getWindowToken(), 0);
    }
}
