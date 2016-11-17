package com.logishub.mobile.launcher.v5;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ShowAgreementActivity extends AppCompatActivity {
    private CustomProgressDialog mProgressDialog;
    int mViewType;
    private static String mAgreementType;
    private static String mAgreementContent;
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showagreement);

        setLayout();
    }

    private void setLayout() {
        showProgressDialog();
        Intent intent = getIntent();
        mAgreementType = intent.getExtras().getString(Define.ACT_PUT_REQ_AGREEMENT);

        Uri uriData = getIntent().getData();

        if (uriData != null) {
            mAgreementType = uriData.getQueryParameter("req_agreement");
        }

        tvContent = (TextView) findViewById(R.id.tvContent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAgreementContent = readTXT(mAgreementType);
                handler.sendEmptyMessage(0);
            }
        }).start();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.actionbar_sub);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            View v = getSupportActionBar().getCustomView();
            TextView titleTxtView = (TextView) v.findViewById(R.id.tv_actionbar_title);
            switch (mAgreementType) {
                case Define.AGREEMENT1:
                    titleTxtView.setText(R.string.shortname_title);
                    break;
                case Define.AGREEMENT2:
                    titleTxtView.setText(R.string.shortname_first);
                    break;
                case Define.AGREEMENT3:
                    titleTxtView.setText(R.string.shortname_second);
                    break;
                case Define.AGREEMENT4:
                    titleTxtView.setText(R.string.shortname_third);
                    break;
                case Define.AGREEMENT5:
                    titleTxtView.setText(R.string.shortname_fourth);
                    break;
            }

        }
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

        hideProgressDialog();
    }

    private String readTXT(String viewType) {
        String data = null;
        InputStream inputStream;

        switch (viewType) {
            case Define.AGREEMENT1:
                mViewType = R.raw.agreement1;
                break;
            case Define.AGREEMENT2:
                mViewType = R.raw.agreement2;
                break;
            case Define.AGREEMENT3:
                mViewType = R.raw.agreement3;
                break;
            case Define.AGREEMENT4:
                mViewType = R.raw.agreement4;
                break;
            case Define.AGREEMENT5:
                mViewType = R.raw.agreement5;
                break;
        }

        inputStream = getResources().openRawResource(mViewType);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }

            data = new String(byteArrayOutputStream.toByteArray());
            inputStream.close();
        } catch (IOException e) {

        }
        return data;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what)
            {
                case 0:
                    tvContent.setText(mAgreementContent);
                    break;
                default:
                    break;
            }

            hideProgressDialog();
            return true;
        }
    });

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
}