package com.logishub.mobile.launcher.v5;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.logishub.mobile.launcher.v5.Common.CustomAlertDialog;
import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.Common.HttpHelper;
import com.logishub.mobile.launcher.v5.Common.HttpService;
import com.logishub.mobile.launcher.v5.DATA.RecruitData;
import com.logishub.mobile.launcher.v5.DATA.RequestRecruitData;
import com.logishub.mobile.launcher.v5.DATA.ResponseRecruitData;
import com.logishub.mobile.launcher.v5.DATA.UserData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SPICE on 2016-10-21.
 */

public class RecruitDetailActivity extends AppCompatActivity {
    private String mTitle;
    private RecruitData mRecruitData;
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
    private Button mRecruitSupply;
    private CustomProgressDialog mProgressDialog;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;
    private String mErrorCode;
    private String mErrorMessage;
    private static ArrayList<UserData> mArrUserList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruitdetail);

        mTitle = getIntent().getExtras().getString(Define.ACT_PUT_REQ_ACT_TITLE);
        mRecruitData = (RecruitData)getIntent().getSerializableExtra(Define.ACT_PUT_REQ_RECRUITDETAIL_DATA);
        mArrUserList = (ArrayList<UserData>) getIntent().getSerializableExtra(Define.ACT_PUT_REQ_USER_LIST);

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
        mRecruitData = null;
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
        mRecruitSupply = null;
        mProgressDialog = null;
        mHttpHelper = null;
        mHttpService = null;
        mErrorCode = null;
        mErrorMessage = null;
        mArrUserList = null;

        hideProgressDialog();
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

        mRecruitSupply = (Button) findViewById(R.id.btn_register_recruit);
        mRecruitSupply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupplyClick();
            }
        });

        onLoadData();
    }

    private void onLoadData() {
        if(mRecruitData != null) {
            mRecruitTitle.setText(mRecruitData.getSubject());
            mRecruitCompany.setText(mRecruitData.getName());
            mRecruitManager.setText(mRecruitData.getContact());
            mRecruitTel.setText(mRecruitData.getPhone());
            mRecruitFax.setText(mRecruitData.getFax());
            mRecruitAddress.setText(mRecruitData.getAddress());
            mRecruitClose.setText(mRecruitData.getDueDateName());
            mRecruitJob.setText(mRecruitData.getJobTypeName());
            mRecruitArea.setText(mRecruitData.getWorkAreaName());
            mRecruitNum.setText(mRecruitData.getHireNumber());
            mRecruitDetail.setText(Html.fromHtml("" + mRecruitData.getDetailInformation() + ""));
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

    private void onSupplyClick() {
        if(mRecruitData != null) {
            showProgressDialog();
            RequestRecruitData request = new RequestRecruitData();
            request.setId(Func.checkStringNull(mArrUserList.get(0).getUserID()));
            request.setKey(mArrUserList.get(0).getUserKey());
            request.setOid(mRecruitData.getOid());

            mHttpHelper = new HttpHelper<>();
            mHttpService = mHttpHelper.getClient(HttpService.class);
            Call<ResponseRecruitData> call = mHttpService.setRecruitApply(request);
            call.enqueue(new Callback<ResponseRecruitData>() {
                @Override
                public void onResponse(Call<ResponseRecruitData> call, Response<ResponseRecruitData> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            ResponseRecruitData data = response.body();

                            if (data.getErrorCode().equals("0")) {
                                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS);
                            }
                            else {
                                mErrorCode = Func.checkStringNull(data.getErrorCode());
                                mErrorMessage = Func.checkStringNull(data.getErrorContent());
                                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_COMMUNITY_ERROR);
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
                public void onFailure(Call<ResponseRecruitData> call, Throwable t) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
                }
            });
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            hideProgressDialog();
            switch(msg.what)
            {
                case Define.HANDLER_MESSAGE_SUCCESS:
                    new CustomAlertDialog(RecruitDetailActivity.this, "알림" + "\n\n"+ "지원에 성공하셨습니다.");
                    break;
                case Define.HANDLER_MESSAGE_ERROR:
                    break;
                case Define.HANDLER_MESSAGE_FAIL:
                    break;
                case Define.HANDLER_MESSAGE_COMMUNITY_ERROR:
                    new CustomAlertDialog(RecruitDetailActivity.this, "errorCode : " + mErrorCode + "\n\n" + mErrorMessage);
                    break;
                default:
                    break;
            }
            return true;
        }
    });


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
