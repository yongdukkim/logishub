package com.logishub.mobile.launcher.v5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;

import java.util.ArrayList;
import java.util.List;

public class WelcomeMessageActivity extends AppCompatActivity implements View.OnClickListener {
    private String mLoginType;
    private String mUserName;
    private String mUserType;
    private EditText etMemberName;
    private Spinner snUsertype;
    private TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcomemessage);
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

        mLoginType = null;
        mUserName = null;
        mUserType = null;
        etMemberName = null;
        snUsertype = null;
        tvInfo = null;
    }

    private void setLayout() {
        Intent intent = getIntent();
        mLoginType = intent.getExtras().getString(Define.ACT_PUT_REQ_LOGIN_TYPE);

        etMemberName = (EditText) findViewById(R.id.et_member_name);
        etMemberName.setFilters(new InputFilter[] {Func.filterKor});

        tvInfo = (TextView) findViewById(R.id.tv_info);

        findViewById(R.id.btn_welcome_continue).setOnClickListener(this);
        findViewById(R.id.tv_welcome_jump).setOnClickListener(this);

        snUsertype = (Spinner) findViewById(R.id.sp_member_type);
        snUsertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        tvInfo.setText("");
                        break;
                    case 1:
                        tvInfo.setText(R.string.welcome_driver);
                        break;
                    case 2:
                        tvInfo.setText(R.string.welcome_owner);
                        break;
                    case 3:
                        tvInfo.setText(R.string.welcome_company);
                        break;
                    case 4:
                        tvInfo.setText(R.string.welcome_forwarder);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        List<String> list = new ArrayList<>();
        list.add("일반");
        list.add("운전자");
        list.add("화주");
        list.add("운송사");
        list.add("포워더");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snUsertype.setAdapter(adapter);
    }

    private void onUserInfo(Boolean bType) {

        mUserName = "";
        mUserType = "";

        if (bType) {
            if (!Func.isValidString(etMemberName.getText().toString())) {
                etMemberName.setError("이름을 입력해 주세요.");
                etMemberName.requestFocus();
                showKeyBoard();
                return;
            }

            mUserName = etMemberName.getText().toString();
            mUserType = Long.toString(snUsertype.getSelectedItemId());
        }

        hideKeyBoard();

        if (mLoginType.equals(Define.LOGINTYPE_KAKAO) || mLoginType.equals(Define.LOGINTYPE_NAVER)) {
            Intent sendIntent = new Intent(Define.SEND_BROADCAST_LOGIN);
            sendIntent.putExtra(Define.SEND_BROADCAST_LOGIN_USER_NAME, mUserName);
            sendIntent.putExtra(Define.SEND_BROADCAST_LOGIN_USER_TYPE, mUserType);
            WelcomeMessageActivity.this.sendBroadcast(sendIntent);
        } else {
            LoginActivity.mUserName = mUserName;
            LoginActivity.mUserType = mUserType;
        }

        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_welcome_continue:
                onUserInfo(true);
                break;
            case R.id.tv_welcome_jump:
                onUserInfo(false);
                break;
        }
    }

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etMemberName.getWindowToken(), 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}