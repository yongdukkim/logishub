package com.logishub.mobile.launcher.v5;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private CustomProgressDialog mProgressDialog;
    private String mPhoneNumber;
    private EditText etRegID;
    private EditText etRegName;
    private EditText etRegPassWord;
    private EditText etRegPassWord2;
    private Spinner snRegusertype;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        etRegID = (EditText) findViewById(R.id.reg_id);
        etRegID.setFilters(new InputFilter[] {Func.filterAlphaNum});
        etRegName = (EditText) findViewById(R.id.reg_name);
        etRegName.setFilters(new InputFilter[] {Func.filterKor});
        etRegPassWord = (EditText) findViewById(R.id.reg_password);
        etRegPassWord.setFilters(new InputFilter[] {Func.filterAlphaNum});
        etRegPassWord2 = (EditText) findViewById(R.id.reg_password2);
        etRegPassWord2.setFilters(new InputFilter[] {Func.filterAlphaNum});
        snRegusertype = (Spinner) findViewById(R.id.reg_user_type);

        findViewById(R.id.btn_register).setOnClickListener(this);

        Spinner userType = (Spinner) findViewById(R.id.reg_user_type);

        List<String> list = new ArrayList<String>();
        list.add("일반");
        list.add("운전자");
        list.add("화주");
        list.add("운송사");
        list.add("포워더");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(adapter);
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

        mPhoneNumber = null;
        etRegID = null;
        etRegName = null;

        etRegPassWord = null;
        etRegPassWord2 = null;
        snRegusertype = null;

        mHttpHelper = null;
        mHttpService = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                onRegister();
                break;
        }
    }

    private void onRegister() {
        if (!Func.isValidString(etRegID.getText().toString())) {
            etRegID.setError("아이디를 입력해 주세요.");
            etRegID.requestFocus();
            showKeyBoard();
            return;
        }

        if (!Func.isValidString(etRegName.getText().toString())) {
            etRegName.setError("이름을 입력해 주세요.");
            etRegName.requestFocus();
            showKeyBoard();
            return;
        }

        if (!Func.isValidString(etRegPassWord.getText().toString()) || etRegPassWord.getText().toString().length() < 4) {
            etRegPassWord.setError("비밀번호를 입력해 주세요.\n비밀번호는 4자리 이상입니다.");
            etRegPassWord.requestFocus();
            showKeyBoard();
            return;
        }

        if (!Func.isValidString(etRegPassWord2.getText().toString()) || etRegPassWord2.getText().toString().length() < 4) {
            etRegPassWord2.setError("비밀번호를 재입력해 주세요.\n비밀번호는 4자리 이상입니다.");
            etRegPassWord2.requestFocus();
            showKeyBoard();
            return;
        }

        if (!etRegPassWord.getText().toString().equals(etRegPassWord2.getText().toString())) {
            new CustomAlertDialog(RegisterActivity.this, "회원가입\n\n비밀번호가 맞지 않습니다.");
            return;
        }

        hideKeyBoard();
        showProgressDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    registerUser();
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void registerUser() {
        ArrayList<RequestUserData> userLists = new ArrayList<RequestUserData>();
        RequestUserData requestUserList = new RequestUserData();
        RequestUserRegisterData requestUserRegisterData = new RequestUserRegisterData();
        requestUserList.setId(etRegID.getText().toString());
        requestUserList.setName(etRegName.getText().toString());
        requestUserList.setPassword(etRegPassWord.getText().toString());
        requestUserList.setMobilePhone(mPhoneNumber);
        requestUserList.setBelongTo(String.valueOf(snRegusertype.getSelectedItemId()));
        userLists.add(requestUserList);
        requestUserRegisterData.setUserList(userLists);

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<List<ResponseUserRegisterData>> call = mHttpService.register(requestUserRegisterData);
        call.enqueue(new Callback<List<ResponseUserRegisterData>>() {
            @Override
            public void onResponse(Call<List<ResponseUserRegisterData>> call, Response<List<ResponseUserRegisterData>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ResponseUserRegisterData> data = response.body();

                        if (data.get(0).getErrorCode().equals("0")) {
                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS);
                        }
                        else {
                            String errorMessage = Func.checkStringNull(data.get(0).getErrorContent());
                            new CustomAlertDialog(RegisterActivity.this, "알림\n\n" + errorMessage);
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
                new CustomAlertDialog(RegisterActivity.this, "알림" + "\n\n서버에 응답이 없습니다.\n관리자에게 문의 하세요.");
            }

        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what)
            {
                case Define.HANDLER_MESSAGE_SUCCESS:
                    showDialog();
                    break;
                case Define.HANDLER_MESSAGE_ERROR:
                    break;
                case Define.HANDLER_MESSAGE_FAIL:
                    new CustomAlertDialog(RegisterActivity.this, "알림" + "\n\n서버에 응답이 없습니다.\n관리자에게 문의 하세요.");
                    break;
                default:
                    break;
            }

            hideProgressDialog();
            return true;
        }
    });

    private void showDialog() {
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
        alert.setMessage("회원가입이 완료 되었습니다.\n로그인후 이용 하시길 바랍니다.");
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
        imm.hideSoftInputFromWindow(etRegID.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etRegName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etRegPassWord.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etRegPassWord2.getWindowToken(), 0);
    }
}
