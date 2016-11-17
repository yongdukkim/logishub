package com.logishub.mobile.launcher.v5;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SPICE on 2016-10-25.
 */

public class RecruitApplyListFragment extends Fragment {
    public static RecruitApplyListAdapter mRecruitApplyListAdapter = null;
    private static ArrayList<UserData> mArrUserList = null;
    private static ArrayList<RecruitData> mArrRecruitApplyList = null;

    public View mView;
    private CustomProgressDialog mProgressDialog;
    private ListView mRecruitApplyList;
    private TextView tvNoData;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mErrorCode;
    private String mErrorMessage;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_recruitapplylist, container, false);

        mArrUserList = new ArrayList<UserData>();
        mArrUserList = ((RecruitActivity)getActivity()).mArrUserList;
        mArrRecruitApplyList = new ArrayList<RecruitData>();

        onLoadData();

        setLayout(mView);

        mRecruitApplyListAdapter.notifyDataSetChanged();

        return mView;
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

        mRecruitApplyListAdapter = null;
        mArrUserList = null;
        mArrRecruitApplyList = null;
        mView = null;
        mProgressDialog = null;
        mRecruitApplyList = null;
        tvNoData = null;
        mSwipeRefreshLayout = null;
        mErrorCode = null;
        mErrorMessage = null;
        mHttpHelper = null;
        mHttpService = null;

        hideProgressDialog();
    }

    private void setLayout(View v) {
        mRecruitApplyList = (ListView) v.findViewById(R.id.iv_recruit_apply_list);
        tvNoData = (TextView) v.findViewById(R.id.tv_no_data);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mRecruitApplyListAdapter = new RecruitApplyListAdapter(getActivity(), R.layout.recruitapply_list, mArrRecruitApplyList);
        mRecruitApplyList.setAdapter(mRecruitApplyListAdapter);
        mRecruitApplyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                onListItemClick(parent, v, position, id);
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onLoadData();
            }
        });
    }

    private void onLoadData() {

        showProgressDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    onRecruitSupplyList();
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void onRecruitSupplyList() {
        RequestRecruitData request = new RequestRecruitData();
        request.setId(Func.checkStringNull(mArrUserList.get(0).getUserID()));
        request.setKey(mArrUserList.get(0).getUserKey());

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseRecruitData> call = mHttpService.getRecruitApplyList(request);
        call.enqueue(new Callback<ResponseRecruitData>() {
            @Override
            public void onResponse(Call<ResponseRecruitData> call, Response<ResponseRecruitData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseRecruitData resData = response.body();

                        if (resData.getErrorCode().equals("0")) {
                            int mRecruitSize = resData.getRecruitApplyList().size();

                            mArrRecruitApplyList.clear();

                            if (mRecruitSize > 0) {

                                List<RecruitData> data = resData.getRecruitApplyList();

                                for (int i = 0; i < mRecruitSize; i++) {
                                    RecruitData recruitSupplyListData = new RecruitData();

                                    recruitSupplyListData.setOid(data.get(i).getOid());
                                    recruitSupplyListData.setSubject(data.get(i).getSubject());
                                    recruitSupplyListData.setJobTypeName(data.get(i).getJobTypeName());
                                    recruitSupplyListData.setDueDateName(data.get(i).getDueDateName());
                                    recruitSupplyListData.setWorkAreaName(data.get(i).getWorkAreaName());
                                    recruitSupplyListData.setCreated(data.get(i).getCreated());

                                    recruitSupplyListData.setSalaryName(data.get(i).getSalaryName());
                                    recruitSupplyListData.setWorkExperienceName(data.get(i).getWorkExperienceName());
                                    recruitSupplyListData.setSubmitDocument(data.get(i).getSubmitDocument());
                                    recruitSupplyListData.setHireNumber(data.get(i).getHireNumber());
                                    recruitSupplyListData.setTonCodeName(data.get(i).getTonCodeName());
                                    recruitSupplyListData.setContainerTypeName(data.get(i).getContainerTypeName());
                                    recruitSupplyListData.setDetailInformation(data.get(i).getDetailInformation());
                                    recruitSupplyListData.setName(data.get(i).getName());
                                    recruitSupplyListData.setContact(data.get(i).getContact());
                                    recruitSupplyListData.setPhone(data.get(i).getPhone());
                                    recruitSupplyListData.setFax(data.get(i).getFax());
                                    recruitSupplyListData.setAddress(data.get(i).getAddress());
                                    recruitSupplyListData.setHomepage(data.get(i).getHomepage());
                                    recruitSupplyListData.setInterview(data.get(i).getInterview());

                                    mArrRecruitApplyList.add(recruitSupplyListData);
                                }
                            }

                            mRecruitApplyListAdapter.notifyDataSetChanged();

                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS);
                        }
                        else {
                            mErrorCode = Func.checkStringNull(resData.getErrorCode());
                            mErrorMessage = Func.checkStringNull(resData.getErrorContent());
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
            public void onFailure(Call<ResponseRecruitData> call, Throwable t) {
                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
            }
        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            hideProgressDialog();
            switch(msg.what)
            {
                case Define.HANDLER_MESSAGE_SUCCESS:
                    break;
                case Define.HANDLER_MESSAGE_ERROR:
                    new CustomAlertDialog(getActivity(), "알림\n\n" + mErrorMessage);
                    break;
                case Define.HANDLER_MESSAGE_FAIL:
                    break;
                default:
                    break;
            }

            onRefreshLayout();
            mSwipeRefreshLayout.setRefreshing(false);
            return true;
        }
    });

    private void onRefreshLayout() {
        if (mArrRecruitApplyList.size() > 0) {
            tvNoData.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    /** List Adapter */
    public class RecruitApplyListAdapter extends ArrayAdapter<RecruitData>
    {
        private ArrayList<RecruitData> itemList;
        private Context context;
        private int rowResourceId;

        public RecruitApplyListAdapter(Context context, int textViewResourceId, ArrayList<RecruitData> itemList)
        {
            super(context, textViewResourceId, itemList);
            this.itemList = itemList;
            this.context = context;
            this.rowResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView;
            RecruitData item = itemList.get(position);
            if(item != null)
            {
                v = SetRecruitApplyList(context, v, this.rowResourceId, item);
            }

            return v;
        }
    }

    private View SetRecruitApplyList(Context context, View v, int rowResourceId, RecruitData Data) {
        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.recruitapply_list, null);

        TextView tvRecruitTitle = (TextView) v.findViewById(R.id.tv_recruit_title);
        TextView tvRecruitCompany = (TextView) v.findViewById(R.id.tv_recruit_company);
        TextView tvRecruitClose = (TextView) v.findViewById(R.id.tv_recruit_close);
        TextView tvRecruitJob = (TextView) v.findViewById(R.id.tv_recruit_job);
        TextView tvRecruitArea = (TextView) v.findViewById(R.id.tv_recruit_area);

        tvRecruitTitle.setText(Data.getSubject());
        tvRecruitCompany.setText(Data.getName());
        tvRecruitClose.setText(Data.getDueDateName());
        tvRecruitJob.setText(Data.getJobTypeName());
        tvRecruitArea.setText(Data.getWorkAreaName());

        return v;
    }

    protected void onListItemClick(AdapterView<?> parent, View v, int position, long id)
    {
        try {
            if (mArrRecruitApplyList.size() > 0) {
                Intent intent = new Intent(getActivity().getApplicationContext(), RecruitApplyDetailActivity.class);
                intent.putExtra(Define.ACT_PUT_REQ_ACT_TITLE, Define.MENU_TITLE_RECRUITAPPLYDETAIL);
                intent.putExtra(Define.ACT_PUT_REQ_RECRUITAPPLYDETAIL_DATA, mArrRecruitApplyList.get(position));
                startActivity(intent);
            }
        } catch (Exception ex) {

        }
    }

    private void showProgressDialog() {
        mProgressDialog = new CustomProgressDialog(getActivity());
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
