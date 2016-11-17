package com.logishub.mobile.launcher.v5;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.logishub.mobile.launcher.v5.Common.CustomAlertDialog;
import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.Common.HttpHelper;
import com.logishub.mobile.launcher.v5.Common.HttpService;
import com.logishub.mobile.launcher.v5.DATA.RequestUserData;
import com.logishub.mobile.launcher.v5.DATA.RequestUserRegisterData;
import com.logishub.mobile.launcher.v5.DATA.ResponseUserRegisterData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private CustomProgressDialog mProgressDialog;
    private String mPhoneNumber;
    private EditText etPhoneNumber;
    private EditText etName;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        setLayout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPhoneNumber = null;
        etPhoneNumber = null;
        etName = null;

        mHttpHelper = null;
        mHttpService = null;

//        hideProgressDialog();
    }

    private void setLayout() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.actionbar_main);
        }

        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etName = (EditText) findViewById(R.id.etName);
        //etName.setFilters(new InputFilter[] {Func.filterKor});

        findViewById(R.id.btn_find_pw).setOnClickListener(this);

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
            case R.id.btn_find_pw:
                onFindPassword();
                break;
        }
    }

    private void onFindPassword() {
        if (!Func.isValidCellPhoneNumber(etPhoneNumber.getText().toString())) {
            etPhoneNumber.setError("'-'를 제외한 핸드폰번호를 정확히 입력해 주세요.");
            etPhoneNumber.requestFocus();
            showKeyBoard();
            return;
        }

        if (!Func.isValidString(etName.getText().toString())) {
            etName.setError("가입자명을 입력해 주세요.");
            etName.requestFocus();
            showKeyBoard();
            return;
        }

        hideKeyBoard();
        showProgressDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    findPassword();
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void findPassword() {
        ArrayList<RequestUserData> userLists = new ArrayList<RequestUserData>();
        RequestUserData requestUserList = new RequestUserData();
        RequestUserRegisterData requestUserRegisterData = new RequestUserRegisterData();
        requestUserList.setMobilePhone(etPhoneNumber.getText().toString());
        requestUserList.setName(etName.getText().toString());
        userLists.add(requestUserList);
        requestUserRegisterData.setUserList(userLists);

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<List<ResponseUserRegisterData>> call = mHttpService.findPassword(requestUserRegisterData);
        call.enqueue(new Callback<List<ResponseUserRegisterData>>() {
            @Override
            public void onResponse(Call<List<ResponseUserRegisterData>> call, Response<List<ResponseUserRegisterData>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ResponseUserRegisterData> data = response.body();

                        if (data.get(0).getErrorCode().equals("0")) {
                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS);
                            showDialog(Func.checkStringNull(data.get(0).getErrorContent()));
                        }
                        else {
                            String errorMessage = Func.checkStringNull(data.get(0).getErrorContent());
                            new CustomAlertDialog(FindPasswordActivity.this, "알림\n\n" + errorMessage);
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
                hideProgressDialog();
                new CustomAlertDialog(FindPasswordActivity.this, "알림" + "\n\n서버에 응답이 없습니다.\n관리자에게 문의 하세요.");
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
                    break;
                case Define.HANDLER_MESSAGE_ERROR:
                    break;
                case Define.HANDLER_MESSAGE_FAIL:
                    new CustomAlertDialog(FindPasswordActivity.this, "알림" + "\n\n서버에 응답이 없습니다.\n관리자에게 문의 하세요.");
                    break;
                default:
                    break;
            }

            hideProgressDialog();
            return true;
        }
    });

    private void showDialog(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                redirectLoginActivity();
            }
        });
        alert.setCancelable(false);
        alert.setTitle("알림");
        alert.setMessage(message);
        alert.show();
    }

    private void redirectLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra(Define.ACT_PUT_REQ_DEVICE_TOKEN, "");
        intent.putExtra(Define.ACT_PUT_REQ_PHONE_NUMBER, "");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
        imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
    }
}
