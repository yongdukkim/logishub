package com.logishub.mobile.launcher.v5;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.logishub.mobile.launcher.v5.Common.CustomAlertDialog;
import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.Common.HttpHelper;
import com.logishub.mobile.launcher.v5.Common.HttpService;
import com.logishub.mobile.launcher.v5.DATA.ResponseUserRegisterData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthPhoneActivity extends AppCompatActivity implements View.OnClickListener {
    private CustomProgressDialog mProgressDialog;
    private String mPhoneNumber;
    private EditText etPhoneNumber;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;
    private String mAuthNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_phone);

        setLayout();
    }

    private void setLayout() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.actionbar_main);
        }

        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        findViewById(R.id.btn_get_auth_num).setOnClickListener(this);

        showProgressDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mPhoneNumber = getPhoneNumber();
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_PHONE_NUMBER_SUCCESS);
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private String getPhoneNumber()
    {
        TelephonyManager mgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        return mgr.getLine1Number();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_auth_num:
                onAuthPhone();
                break;
        }
    }

    private void onAuthPhone() {
        if (!Func.isValidCellPhoneNumber(etPhoneNumber.getText().toString())) {
            etPhoneNumber.setError("'-'를 제외한 핸드폰번호를 정확히 입력해 주세요.");
            etPhoneNumber.requestFocus();
            showKeyBoard();
            return;
        }

        hideKeyBoard();
        showProgressDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getPhoneAuthNum();
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void getPhoneAuthNum() {
        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<List<ResponseUserRegisterData>> call = mHttpService.getAuthNum(etPhoneNumber.getText().toString());
        call.enqueue(new Callback<List<ResponseUserRegisterData>>() {
            @Override
            public void onResponse(Call<List<ResponseUserRegisterData>> call, Response<List<ResponseUserRegisterData>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ResponseUserRegisterData> data = response.body();

                        if (data.get(0).getErrorCode().equals("0")) {
                            if (!data.get(0).getAuthNumber().equals("")) {
                                mAuthNumber = data.get(0).getAuthNumber();
                                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS);
                            }
                        } else {
                            String errorMessage = Func.checkStringNull(data.get(0).getErrorContent());
                            new CustomAlertDialog(AuthPhoneActivity.this, "알림\n\n" + errorMessage);
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
            switch(msg.what)
            {
                case Define.HANDLER_MESSAGE_PHONE_NUMBER_SUCCESS:
                    etPhoneNumber.setText(mPhoneNumber.replace("+82", "0"));
                    break;
                case Define.HANDLER_MESSAGE_SUCCESS:
                    redirectAuthPhone2Activity();
                    break;
                case Define.HANDLER_MESSAGE_ERROR:
                    break;
                case Define.HANDLER_MESSAGE_FAIL:
                    new CustomAlertDialog(AuthPhoneActivity.this, "알림" + "\n\n서버에 응답이 없습니다.\n관리자에게 문의 하세요.");
                    break;
                default:
                    break;
            }

            hideProgressDialog();
            return true;
        }
    });

    private void redirectAuthPhone2Activity() {
        Intent intent = new Intent(getApplicationContext(), AuthPhone2Activity.class);
        intent.putExtra(Define.ACT_PUT_REQ_PHONE_NUMBER, etPhoneNumber.getText().toString().replace("+82", "0"));
        intent.putExtra(Define.ACT_PUT_REQ_AUTH_NUMBER, mAuthNumber);
        startActivity(intent);
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
        imm.hideSoftInputFromWindow(etPhoneNumber.getWindowToken(), 0);
    }
}
