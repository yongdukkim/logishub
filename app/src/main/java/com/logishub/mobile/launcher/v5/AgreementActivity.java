package com.logishub.mobile.launcher.v5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.logishub.mobile.launcher.v5.Common.Define;

public class AgreementActivity extends AppCompatActivity implements View.OnClickListener {
    private CheckBox cbAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

        setLayout();
    }

    private void setLayout() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.actionbar_main);
        }

        findViewById(R.id.btnAgreement1).setOnClickListener(this);
        findViewById(R.id.btnAgreement2).setOnClickListener(this);
        findViewById(R.id.btnAgreement3).setOnClickListener(this);
        findViewById(R.id.btnAgreement4).setOnClickListener(this);
        findViewById(R.id.btnAgreement5).setOnClickListener(this);

        cbAgree = (CheckBox) findViewById(R.id.cbAgreement);
        cbAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.getId() == R.id.cbAgreement) {
                    if (isChecked) {
                        startActivity(new Intent(getApplication(), AuthPhoneActivity.class));
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        cbAgree.setChecked(false);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAgreement1:
                onShowAgreement(Define.AGREEMENT1);
                break;
            case R.id.btnAgreement2:
                onShowAgreement(Define.AGREEMENT2);
                break;
            case R.id.btnAgreement3:
                onShowAgreement(Define.AGREEMENT3);
                break;
            case R.id.btnAgreement4:
                onShowAgreement(Define.AGREEMENT4);
                break;
            case R.id.btnAgreement5:
                onShowAgreement(Define.AGREEMENT5);
                break;
        }
    }

    private void onShowAgreement(String viewType) {
        Intent intent = new Intent(getApplicationContext(), ShowAgreementActivity.class);
        intent.putExtra(Define.ACT_PUT_REQ_AGREEMENT, viewType);
        startActivity(intent);
    }

}