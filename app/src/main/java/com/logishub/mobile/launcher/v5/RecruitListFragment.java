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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.logishub.mobile.launcher.v5.Common.CustomAlertDialog;
import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.Common.HttpHelper;
import com.logishub.mobile.launcher.v5.Common.HttpService;
import com.logishub.mobile.launcher.v5.DATA.DefineListData;
import com.logishub.mobile.launcher.v5.DATA.RecruitData;
import com.logishub.mobile.launcher.v5.DATA.RequestRecruitData;
import com.logishub.mobile.launcher.v5.DATA.ResponseRecruitData;
import com.logishub.mobile.launcher.v5.DATA.UserData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecruitListFragment extends Fragment {

    public static RecruitListAdapter mRecruitListAdapter = null;
    private static ArrayList<UserData> mArrUserList = null;
    private static ArrayList<RecruitData> mArrRecruitList = null;
    private static ArrayList<DefineListData> mArrJobTypeList = null;
    private static ArrayList<String> mArrJobTypeString = null;
    private static ArrayList<DefineListData> mArrAreaTypeList = null;
    private static ArrayList<String> mArrAreaTypeString = null;

    public View mView;
    private CustomProgressDialog mProgressDialog;
    private ListView mRecruitList;
    private TextView tvNoData;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mErrorCode;
    private String mErrorMessage;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;
    private Spinner mJobTypeSpinner;
    private ArrayAdapter mJobTypeArrayAdapter;
    private DefineListData mDefineListData;
    private Spinner mAreaTypeSpinner;
    private ArrayAdapter mAreaTypeArrayAdapter;
    private String mJobTypeCode;
    private String mAreaTypeCode;
    private Button mRecruitSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_recruitlist, container, false);

        mArrUserList = new ArrayList<UserData>();
        mArrUserList = ((RecruitActivity)getActivity()).mArrUserList;
        mArrRecruitList = new ArrayList<RecruitData>();
        mArrJobTypeList = new ArrayList<DefineListData>();
        mArrJobTypeString = new ArrayList<String>();
        mArrAreaTypeList = new ArrayList<DefineListData>();
        mArrAreaTypeString = new ArrayList<String>();

        onLoadData(true);

        setLayout(mView);

        mRecruitListAdapter.notifyDataSetChanged();

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

        mRecruitListAdapter = null;
        mArrUserList = null;
        mArrRecruitList = null;
        mArrJobTypeList = null;
        mArrJobTypeString = null;
        mArrAreaTypeList = null;
        mArrAreaTypeString = null;
        mView = null;
        mProgressDialog = null;
        mRecruitList = null;
        tvNoData = null;
        mSwipeRefreshLayout = null;
        mErrorCode = null;
        mErrorMessage = null;
        mHttpHelper = null;
        mHttpService = null;
        mJobTypeSpinner = null;
        mJobTypeArrayAdapter = null;
        mDefineListData = null;
        mAreaTypeSpinner = null;
        mAreaTypeArrayAdapter = null;
        mJobTypeCode = null;
        mAreaTypeCode = null;
        mRecruitSearch = null;

        hideProgressDialog();
    }

    private void setLayout(View v) {
        mRecruitSearch = (Button) mView.findViewById(R.id.btn_recruit_search);
        mRecruitSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadData(false);
            }
        });
        mJobTypeSpinner = (Spinner) mView.findViewById(R.id.sp_item_jobtype);
        mAreaTypeSpinner = (Spinner) mView.findViewById(R.id.sp_item_workarea);
        mRecruitList = (ListView) v.findViewById(R.id.iv_recruit_list);
        tvNoData = (TextView) v.findViewById(R.id.tv_no_data);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mRecruitListAdapter = new RecruitListAdapter(getActivity(), R.layout.recruitlist_list, mArrRecruitList);
        mRecruitList.setAdapter(mRecruitListAdapter);
        mRecruitList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                onListItemClick(parent, v, position, id);
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onLoadData(false);
            }
        });
    }

    private void onLoadData(final boolean bFirst) {

        showProgressDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    onRecruitList(bFirst);
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void onRecruitList(final boolean bFirst) {
        RequestRecruitData request = new RequestRecruitData();
        request.setId(Func.checkStringNull(mArrUserList.get(0).getUserID()));
        request.setKey(mArrUserList.get(0).getUserKey());
        if (!Func.checkStringNull(mJobTypeCode).equals("")) {
            request.setJobType(mJobTypeCode);
        }
        if (!Func.checkStringNull(mAreaTypeCode).equals("")) {
            request.setWorkArea(mAreaTypeCode);
        }

        if(bFirst) {
            ArrayList<DefineListData> defineLists = new ArrayList<DefineListData>();
            mDefineListData = new DefineListData();
            mDefineListData.setType(Define.DEFINE_JOB_TYPE);
            defineLists.add(mDefineListData);
            request.setDefineTypeList(defineLists);

            mDefineListData = new DefineListData();
            mDefineListData.setType(Define.DEFINE_AREA_TYPE);
            defineLists.add(mDefineListData);
            request.setDefineTypeList(defineLists);
        }

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseRecruitData> call = mHttpService.getRecruitList(request);
        call.enqueue(new Callback<ResponseRecruitData>() {
            @Override
            public void onResponse(Call<ResponseRecruitData> call, Response<ResponseRecruitData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseRecruitData resData = response.body();

                        if (resData.getErrorCode().equals("0")) {
                            int mRecruitSize = resData.getRecruitList().size();

                            mArrRecruitList.clear();

                            if (mRecruitSize > 0) {

                                List<RecruitData> data = resData.getRecruitList();

                                for (int i = 0; i < mRecruitSize; i++) {
                                    RecruitData recruitlistData = new RecruitData();

                                    recruitlistData.setOid(data.get(i).getOid());
                                    recruitlistData.setSubject(data.get(i).getSubject());
                                    recruitlistData.setJobTypeName(data.get(i).getJobTypeName());
                                    recruitlistData.setDueDateName(data.get(i).getDueDateName());
                                    recruitlistData.setWorkAreaName(data.get(i).getWorkAreaName());
                                    recruitlistData.setCreated(data.get(i).getCreated());

                                    recruitlistData.setSalaryName(data.get(i).getSalaryName());
                                    recruitlistData.setWorkExperienceName(data.get(i).getWorkExperienceName());
                                    recruitlistData.setSubmitDocument(data.get(i).getSubmitDocument());
                                    recruitlistData.setHireNumber(data.get(i).getHireNumber());
                                    recruitlistData.setTonCodeName(data.get(i).getTonCodeName());
                                    recruitlistData.setContainerTypeName(data.get(i).getContainerTypeName());
                                    recruitlistData.setDetailInformation(data.get(i).getDetailInformation());
                                    recruitlistData.setName(data.get(i).getName());
                                    recruitlistData.setContact(data.get(i).getContact());
                                    recruitlistData.setPhone(data.get(i).getPhone());
                                    recruitlistData.setFax(data.get(i).getFax());
                                    recruitlistData.setAddress(data.get(i).getAddress());
                                    recruitlistData.setHomepage(data.get(i).getHomepage());
                                    recruitlistData.setIsNew(data.get(i).getIsNew());

                                    mArrRecruitList.add(recruitlistData);
                                }
                            }

                            if(bFirst) {
                                int mDefineSize = resData.getDefineList().size();

                                if (mDefineSize > 0) {

                                    List<DefineListData> data = resData.getDefineList();

                                    mArrJobTypeString.add("선택");
                                    mArrJobTypeList.add(new DefineListData());

                                    mArrAreaTypeString.add("선택");
                                    mArrAreaTypeList.add(new DefineListData());

                                    for (int i = 0; i < mDefineSize; i++) {
                                        DefineListData defineListData = data.get(i);

                                        switch (defineListData.getType()) {
                                            case Define.DEFINE_JOB_TYPE:
                                                mArrJobTypeString.add(defineListData.getValue());
                                                mArrJobTypeList.add(defineListData);
                                                break;
                                            case Define.DEFINE_AREA_TYPE:
                                                mArrAreaTypeString.add(defineListData.getValue());
                                                mArrAreaTypeList.add(defineListData);
                                                break;
                                        }
                                    }
                                }
                            }

                            mRecruitListAdapter.notifyDataSetChanged();

                            if(bFirst) {
                                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_PORTALDEFINE_SUCCESS);
                            } else {
                                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS);
                            }
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

    private void setSpinnerData() {

        //직종
        mJobTypeArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, mArrJobTypeString);
        mJobTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mJobTypeSpinner.setAdapter(mJobTypeArrayAdapter);
        mJobTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mJobTypeCode = mArrJobTypeList.get(position).getCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //지역
        mAreaTypeArrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, mArrAreaTypeString);
        mAreaTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAreaTypeSpinner.setAdapter(mAreaTypeArrayAdapter);
        mAreaTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAreaTypeCode = mArrAreaTypeList.get(position).getCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                case Define.HANDLER_MESSAGE_PORTALDEFINE_SUCCESS:
                    setSpinnerData();
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
        if (mArrRecruitList.size() > 0) {
            tvNoData.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    /** List Adapter */
    public class RecruitListAdapter extends ArrayAdapter<RecruitData>
    {
        private ArrayList<RecruitData> itemList;
        private Context context;
        private int rowResourceId;

        public RecruitListAdapter(Context context, int textViewResourceId, ArrayList<RecruitData> itemList)
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
                v = SetRecruitList(context, v, this.rowResourceId, item);
            }

            return v;
        }
    }

    private View SetRecruitList(Context context, View v, int rowResourceId, RecruitData Data) {
        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.recruitlist_list, null);

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
            if (mArrRecruitList.size() > 0) {
                Intent intent = new Intent(getActivity().getApplicationContext(), RecruitDetailActivity.class);
                intent.putExtra(Define.ACT_PUT_REQ_ACT_TITLE, Define.MENU_TITLE_RECRUITDETAIL);
                intent.putExtra(Define.ACT_PUT_REQ_RECRUITDETAIL_DATA, mArrRecruitList.get(position));
                intent.putExtra(Define.ACT_PUT_REQ_USER_LIST, mArrUserList);
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
