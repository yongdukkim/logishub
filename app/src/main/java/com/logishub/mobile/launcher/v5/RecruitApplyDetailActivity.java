package com.logishub.mobile.launcher.v5;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.DATA.RecruitData;

/**
 * Created by SPICE on 2016-10-25.
 */

public class RecruitApplyDetailActivity extends AppCompatActivity {
    private String mTitle;
    private RecruitData mRecruitApplyData;
    private TextView mRecruitTitle;
    private TextView mRecruitCompany;
    private TextView mRecruitManager;
    private TextView mRecruitTel;
    private TextView mRecruitFax ;
    private TextView mRecruitAddress;
    private TextView mRecruitClose;
    private TextView mRecruitJob;
    private TextView mRecruitArea;
    private TextView mRecruitNum;
    private TextView mRecruitDetail;
    private CustomProgressDialog mProgressDialog;
    private String mErrorCode;
    private String mErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruitapplydetail);

        mTitle = getIntent().getExtras().getString(Define.ACT_PUT_REQ_ACT_TITLE);
        mRecruitApplyData = (RecruitData)getIntent().getSerializableExtra(Define.ACT_PUT_REQ_RECRUITAPPLYDETAIL_DATA);

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

        mTitle = null;
        mRecruitApplyData = null;
        mRecruitTitle = null;
        mRecruitCompany = null;
        mRecruitManager = null;
        mRecruitTel = null;
        mRecruitFax  = null;
        mRecruitAddress = null;
        mRecruitClose = null;
        mRecruitJob = null;
        mRecruitArea = null;
        mRecruitNum = null;
        mRecruitDetail = null;
        mProgressDialog = null;
        mErrorCode = null;
        mErrorMessage = null;
    }

    private void setLayout() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.actionbar_sub);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            View v = getSupportActionBar().getCustomView();
            TextView titleTxtView = (TextView) v.findViewById(R.id.tv_actionbar_title);
            titleTxtView.setText(mTitle);
        }

        mRecruitTitle = (TextView) findViewById(R.id.tv_recruit_title);
        mRecruitCompany = (TextView) findViewById(R.id.tv_recruit_company);
        mRecruitManager = (TextView) findViewById(R.id.tv_recruit_manager);
        mRecruitTel =  (TextView) findViewById(R.id.tv_recruit_tel);
        mRecruitFax = (TextView) findViewById(R.id.tv_recruit_fax);
        mRecruitAddress = (TextView) findViewById(R.id.tv_recruit_address);
        mRecruitClose = (TextView) findViewById(R.id.tv_recruit_close);
        mRecruitJob = (TextView) findViewById(R.id.tv_recruit_job);
        mRecruitArea = (TextView) findViewById(R.id.tv_recruit_area);
        mRecruitNum = (TextView) findViewById(R.id.tv_recruit_num);
        mRecruitDetail = (TextView) findViewById(R.id.tv_recruit_detail);

        onLoadData();
    }

    private void onLoadData() {
        if(mRecruitApplyData != null) {
            mRecruitTitle.setText(mRecruitApplyData.getSubject());
            mRecruitCompany.setText(mRecruitApplyData.getName());
            mRecruitManager.setText(mRecruitApplyData.getContact());
            mRecruitTel.setText(mRecruitApplyData.getPhone());
            mRecruitFax.setText(mRecruitApplyData.getFax());
            mRecruitAddress.setText(mRecruitApplyData.getAddress());
            mRecruitClose.setText(mRecruitApplyData.getDueDateName());
            mRecruitJob.setText(mRecruitApplyData.getJobTypeName());
            mRecruitArea.setText(mRecruitApplyData.getWorkAreaName());
            mRecruitNum.setText(mRecruitApplyData.getHireNumber());
            mRecruitDetail.setText(Html.fromHtml("" + mRecruitApplyData.getDetailInformation() + ""));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
