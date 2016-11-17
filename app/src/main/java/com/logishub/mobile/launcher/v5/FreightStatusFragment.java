package com.logishub.mobile.launcher.v5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.logishub.mobile.launcher.v5.Common.CustomAlertDialog;
import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.Common.HttpHelper;
import com.logishub.mobile.launcher.v5.Common.HttpService;
import com.logishub.mobile.launcher.v5.Common.PrefsUtil;
import com.logishub.mobile.launcher.v5.DATA.FnsData;
import com.logishub.mobile.launcher.v5.DATA.FnsTemplateData;
import com.logishub.mobile.launcher.v5.DATA.RequestFreightRegisterData;
import com.logishub.mobile.launcher.v5.DATA.ResponseFreightData;
import com.logishub.mobile.launcher.v5.DATA.ServiceData;
import com.logishub.mobile.launcher.v5.DB.FnsOrderAdapter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreightStatusFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public View mView;
    private CustomProgressDialog mProgressDialog;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;
    private String mErrorMessage;
    private Spinner mTerm;
    private EditText mStartDate;
    private EditText mEndDate;
    private ArrayList<FnsData> mArrFnsList = null;
    private ArrayList<ServiceData> mArrServiceList = null;
    private FnsListAdapter mFnsListAdapter = null;
    private ExpandableListView mFnsList;
    private TextView tvNoData;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mDateField;
    private FnsOrderAdapter mTemplateOrderDb = null;

    public FreightStatusFragment() {

    }

    public interface CustomOnClickListener {
        public void onClicked(FnsData data, String mModifyMode);
    }

    private CustomOnClickListener customOnClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_freight_status, container, false);

        onOpenDB();

        setLayout(mView);

        onLoadData();

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

        mView = null;
        mProgressDialog = null;
        mHttpHelper = null;
        mHttpService = null;
        mErrorMessage = null;
        mTerm = null;
        mStartDate = null;
        mEndDate = null;
        mArrFnsList = null;
        mArrServiceList = null;
        mFnsListAdapter = null;
        mFnsList = null;

        if (mTemplateOrderDb != null)
        {
            mTemplateOrderDb.close();
            mTemplateOrderDb = null;
        }
    }

    private void setLayout(View v) {
        mArrFnsList = new ArrayList<>();
        mArrServiceList = new ArrayList<>();
        mFnsList = (ExpandableListView) v.findViewById(R.id.eplv_freight_list);
        tvNoData = (TextView) v.findViewById(R.id.tv_no_data);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mFnsListAdapter = new FnsListAdapter(getActivity(), R.layout.freightlist_list, mArrFnsList, mArrFnsList);
        mFnsList.setAdapter(mFnsListAdapter);
        mFnsList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int groupCount = mFnsListAdapter.getGroupCount();

                for (int i = 0; i < groupCount; i++) {
                    if (!(i == groupPosition))
                        mFnsList.collapseGroup(i);
                }
            }
        });
        mFnsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                onListItemClick(parent, v, position, id);
            }
        });

        mTerm = (Spinner) v.findViewById(R.id.sp_term);
        mTerm.setOnItemSelectedListener(SpnTerm);

        mStartDate = (EditText) v.findViewById(R.id.et_start_date);
        mEndDate = (EditText) v.findViewById(R.id.et_end_date);
        mStartDate.setText(Func.nowDate(0, 0));
        mEndDate.setText(Func.nowDate(0, 0));

        v.findViewById(R.id.btn_freight_info).setOnClickListener(this);
        v.findViewById(R.id.et_start_date).setOnClickListener(this);
        v.findViewById(R.id.et_end_date).setOnClickListener(this);

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onLoadData();
            }
        });
    }

    /** SQL Lite Open & Data Init */
    private void onOpenDB() {

        try {
            mTemplateOrderDb = new FnsOrderAdapter(getActivity());
            mTemplateOrderDb.open();

        } catch (Exception e) { }
    }

    private void onLoadData() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    onFreightStatus();
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void onFreightOrderDelete(final String transOid) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    onFreightDelete(transOid);
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void onFreightOrderCancel(final String turnOid, final String driverOid) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    onFreightCancel(turnOid, driverOid);
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void onFreightStatus() {
        RequestFreightRegisterData requestFreightRegisterData = new RequestFreightRegisterData();
        requestFreightRegisterData.setDateFrom(mStartDate.getText().toString());
        requestFreightRegisterData.setDateTo(mEndDate.getText().toString());
        requestFreightRegisterData.setSiteCode(((FreightInfoActivity)getActivity()).mServiceCode);
        requestFreightRegisterData.setServiceUrl(((FreightInfoActivity)getActivity()).mServiceURL);
        requestFreightRegisterData.setUserID(((FreightInfoActivity)getActivity()).mArrUserList.get(0).getUserID());
        requestFreightRegisterData.setUserSiteKey(Func.checkStringNull(PrefsUtil.getValue(((FreightInfoActivity)getActivity()).mServiceCode, "")));

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseFreightData> call = mHttpService.getFNSOrder(requestFreightRegisterData);
        call.enqueue(new Callback<ResponseFreightData>() {
            @Override
            public void onResponse(Call<ResponseFreightData> call, Response<ResponseFreightData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseFreightData data = response.body();

                        if (data.getErrorCode().equals("0")) {

                            mArrFnsList.clear();

                            int mFnsSize = data.getFnsList().size();
                            if (mFnsSize > 0) {
                                for (int i = 0; i < mFnsSize; i++) {
                                    FnsData fnsData = new FnsData();
                                    fnsData.setId(Func.checkStringNull(data.getFnsList().get(i).getId()));
                                    fnsData.setTransId(Func.checkStringNull(data.getFnsList().get(i).getTransId()));
                                    fnsData.setOid(Func.checkStringNull(data.getFnsList().get(i).getOid()));
                                    fnsData.setTurnOid(Func.checkStringNull(data.getFnsList().get(i).getTurnOid()));
                                    fnsData.setDriverUsreOid(Func.checkStringNull(data.getFnsList().get(i).getDriverUsreOid()));
                                    fnsData.setStatus(Func.checkStringNull(data.getFnsList().get(i).getStatus()));
                                    fnsData.setOrderDate(Func.checkStringNull(data.getFnsList().get(i).getOrderDate()));
                                    fnsData.setEarlistPickupTime(Func.checkStringNull(data.getFnsList().get(i).getEarlistPickupTime()));
                                    fnsData.setTransDate(Func.checkStringNull(data.getFnsList().get(i).getTransDate()));
                                    fnsData.setTimeSpan(Func.checkStringNull(data.getFnsList().get(i).getTimeSpan()));
                                    fnsData.setTimeSpanText(Func.checkStringNull(data.getFnsList().get(i).getTimeSpanText()));
                                    fnsData.setIlDeliveryCustomer(Func.checkStringNull(data.getFnsList().get(i).getIlDeliveryCustomer()));
                                    fnsData.setDeliveryCustomerName(Func.checkStringNull(data.getFnsList().get(i).getDeliveryCustomerName()));
                                    fnsData.setDeliveryCustomerPhone(Func.checkStringNull(data.getFnsList().get(i).getDeliveryCustomerPhone()));
                                    fnsData.setIlPickupArea(Func.checkStringNull(data.getFnsList().get(i).getIlPickupArea()));
                                    fnsData.setPickupAreaName(Func.checkStringNull(data.getFnsList().get(i).getPickupAreaName()));
                                    fnsData.setPickupTypeCode(Func.checkStringNull(data.getFnsList().get(i).getPickupTypeCode()));
                                    fnsData.setPickupTypeName(Func.checkStringNull(data.getFnsList().get(i).getPickupTypeName()));
                                    fnsData.setPickupTime(Func.checkStringNull(data.getFnsList().get(i).getPickupTime()));
                                    fnsData.setDeliveryTime(Func.checkStringNull(data.getFnsList().get(i).getDeliveryTime()));
                                    fnsData.setIlArea(Func.checkStringNull(data.getFnsList().get(i).getIlArea()));
                                    fnsData.setAreaName(Func.checkStringNull(data.getFnsList().get(i).getAreaName()));
                                    fnsData.setAddress(Func.checkStringNull(data.getFnsList().get(i).getAddress()));
                                    fnsData.setDetailAddress(Func.checkStringNull(data.getFnsList().get(i).getDetailAddress()));
                                    fnsData.setDeliveryTimeTypeCode(Func.checkStringNull(data.getFnsList().get(i).getDeliveryTimeTypeCode()));
                                    fnsData.setDeliveryTimeTypeName(Func.checkStringNull(data.getFnsList().get(i).getDeliveryTimeTypeName()));
                                    fnsData.setTonCode(Func.checkStringNull(data.getFnsList().get(i).getTonCode()));
                                    fnsData.setTonName(Func.checkStringNull(data.getFnsList().get(i).getTonName()));
                                    fnsData.setContainerTypeCode(Func.checkStringNull(data.getFnsList().get(i).getContainerTypeCode()));
                                    fnsData.setContainerTypeName(Func.checkStringNull(data.getFnsList().get(i).getContainerTypeName()));
                                    fnsData.setPrice(Func.checkStringNull(data.getFnsList().get(i).getPrice()));
                                    fnsData.setPaymentType(Func.checkStringNull(data.getFnsList().get(i).getPaymentType()));
                                    fnsData.setPaymentTypeName(Func.checkStringNull(data.getFnsList().get(i).getPaymentTypeName()));
                                    fnsData.setItemKindCode(Func.checkStringNull(data.getFnsList().get(i).getItemKindCode()));
                                    fnsData.setItemKindName(Func.checkStringNull(data.getFnsList().get(i).getItemKindName()));
                                    fnsData.setVehicleID(Func.checkStringNull(data.getFnsList().get(i).getVehicleID()));
                                    fnsData.setLoadingMethod(Func.checkStringNull(data.getFnsList().get(i).getLoadingMethod()));
                                    fnsData.setLoadingMethodName(Func.checkStringNull(data.getFnsList().get(i).getLoadingMethodName()));
                                    fnsData.setUnloadingMethod(Func.checkStringNull(data.getFnsList().get(i).getUnloadingMethod()));
                                    fnsData.setUnloadingMethodName(Func.checkStringNull(data.getFnsList().get(i).getUnloadingMethodName()));
                                    fnsData.setDeliveryType(Func.checkStringNull(data.getFnsList().get(i).getDeliveryType()));
                                    fnsData.setDeliveryTypeName(Func.checkStringNull(data.getFnsList().get(i).getDeliveryTypeName()));
                                    fnsData.setWidthText(Func.checkStringNull(data.getFnsList().get(i).getWidthText()));
                                    fnsData.setLengthText(Func.checkStringNull(data.getFnsList().get(i).getLengthText()));
                                    fnsData.setHeightText(Func.checkStringNull(data.getFnsList().get(i).getHeightText()));
                                    fnsData.setWeight(Func.checkStringNull(data.getFnsList().get(i).getWeight()));
                                    fnsData.setCustomOrderType(Func.checkStringNull(data.getFnsList().get(i).getCustomOrderType()));
                                    fnsData.setCustomOrderTypeName(Func.checkStringNull(data.getFnsList().get(i).getCustomOrderTypeName()));
                                    fnsData.setRemark(Func.checkStringNull(data.getFnsList().get(i).getRemark()));
                                    fnsData.setRequestedPickupDate(Func.checkStringNull(data.getFnsList().get(i).getRequestedPickupDate()));
                                    fnsData.setRequestedDeliveryDate(Func.checkStringNull(data.getFnsList().get(i).getRequestedDeliveryDate()));
                                    fnsData.setOrderTime(Func.checkStringNull(data.getFnsList().get(i).getOrderTime()));
                                    fnsData.setDriverName(Func.checkStringNull(data.getFnsList().get(i).getDriverName()));
                                    fnsData.setDriverMobilePhone(Func.checkStringNull(data.getFnsList().get(i).getDriverMobilePhone()));
                                    fnsData.setStatusName(Func.checkStringNull(data.getFnsList().get(i).getStatusName()));
                                    fnsData.setCharged(Func.checkStringNull(data.getFnsList().get(i).getCharged()));
                                    fnsData.setChargedText(Func.checkStringNull(data.getFnsList().get(i).getChargedText()));
                                    fnsData.setUserName(Func.checkStringNull(data.getFnsList().get(i).getUserName()));
                                    fnsData.setUserMobilePhone(Func.checkStringNull(data.getFnsList().get(i).getUserMobilePhone()));
                                    fnsData.setOwnerName(Func.checkStringNull(data.getFnsList().get(i).getOwnerName()));
                                    fnsData.setOwnerPhone(Func.checkStringNull(data.getFnsList().get(i).getOwnerPhone()));
                                    fnsData.setVehicleNo(Func.checkStringNull(data.getFnsList().get(i).getVehicleNo()));
                                    mArrServiceList.clear();

                                    int mServiceSize = data.getFnsList().get(i).getOrgList().size();
                                    if (mServiceSize > 0) {
                                        for (int j = 0; j < mServiceSize; j++) {
                                            ServiceData serviceData = new ServiceData();
                                            serviceData.setServiceOid(Func.checkStringNull(data.getFnsList().get(i).getOrgList().get(j).getServiceOid()));
                                            serviceData.setServiceName(Func.checkStringNull(data.getFnsList().get(i).getOrgList().get(j).getServiceName()));

                                            mArrServiceList.add(serviceData);

                                            fnsData.setOrgList(mArrServiceList);
                                        }
                                    }

                                    mArrFnsList.add(fnsData);
                                }
                            }

                            mFnsListAdapter.notifyDataSetChanged();

                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS);
                        }
                        else {
                            mErrorMessage = Func.checkStringNull(data.getErrorContent());
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
            public void onFailure(Call<ResponseFreightData> call, Throwable t) {
                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
            }
        });
    }

    private void onFreightDelete(String transOid) {
        RequestFreightRegisterData requestFreightRegisterData = new RequestFreightRegisterData();
        requestFreightRegisterData.setIlTransOrder(transOid);
        requestFreightRegisterData.setSiteCode(((FreightInfoActivity)getActivity()).mServiceCode);
        requestFreightRegisterData.setServiceUrl(((FreightInfoActivity)getActivity()).mServiceURL);
        requestFreightRegisterData.setUserPortalKey(((FreightInfoActivity)getActivity()).mArrUserList.get(0).getUserKey());
        requestFreightRegisterData.setUserSiteKey(Func.checkStringNull(PrefsUtil.getValue(((FreightInfoActivity)getActivity()).mServiceCode, "")));

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseFreightData> call = mHttpService.deleteFNSOrder(requestFreightRegisterData);
        call.enqueue(new Callback<ResponseFreightData>() {
            @Override
            public void onResponse(Call<ResponseFreightData> call, Response<ResponseFreightData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseFreightData data = response.body();

                        if (data.getErrorCode().equals("0")) {
                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS_RELOAD);
                        }
                        else {
                            mErrorMessage = Func.checkStringNull(data.getErrorContent());
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
            public void onFailure(Call<ResponseFreightData> call, Throwable t) {
                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
            }
        });
    }

    private void onFreightCancel(String turnOid, String driverOid) {
        RequestFreightRegisterData requestFreightRegisterData = new RequestFreightRegisterData();
        requestFreightRegisterData.setTurnOid(turnOid);
        requestFreightRegisterData.setDriverUserOid(driverOid);
        requestFreightRegisterData.setSiteCode(((FreightInfoActivity)getActivity()).mServiceCode);
        requestFreightRegisterData.setServiceUrl(((FreightInfoActivity)getActivity()).mServiceURL);
        requestFreightRegisterData.setUserPortalKey(((FreightInfoActivity)getActivity()).mArrUserList.get(0).getUserKey());
        requestFreightRegisterData.setUserSiteKey(Func.checkStringNull(PrefsUtil.getValue(((FreightInfoActivity)getActivity()).mServiceCode, "")));
        requestFreightRegisterData.setUserID(((FreightInfoActivity)getActivity()).mArrUserList.get(0).getUserID());

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseFreightData> call = mHttpService.cancelFNSOrder(requestFreightRegisterData);
        call.enqueue(new Callback<ResponseFreightData>() {
            @Override
            public void onResponse(Call<ResponseFreightData> call, Response<ResponseFreightData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseFreightData data = response.body();

                        if (data.getErrorCode().equals("0")) {
                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS_RELOAD);
                        }
                        else {
                            mErrorMessage = Func.checkStringNull(data.getErrorContent());
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
            public void onFailure(Call<ResponseFreightData> call, Throwable t) {
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
                case Define.HANDLER_MESSAGE_SUCCESS_RELOAD:
                    onFreightStatus();
                    break;
                case Define.HANDLER_MESSAGE_ERROR:
                    new CustomAlertDialog(getActivity(), "알림\n\n" + mErrorMessage);
                    break;
                case Define.HANDLER_MESSAGE_FAIL:
                    new CustomAlertDialog(getActivity(), "알림" + "\n\n잠시 후 다시 이용해 주시길 바랍니다.");
                    break;
                default:
                    break;
            }

            onCloseGroup();
            onRefreshLayout();
            mSwipeRefreshLayout.setRefreshing(false);
            return true;
        }
    });

    private void onCloseGroup() {
        int groupCount = mFnsListAdapter.getGroupCount();

        for (int i = 0; i < groupCount; i++) {
            mFnsList.collapseGroup(i);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_freight_info:
                try {
                    if (!mStartDate.getText().equals("") && !mEndDate.getText().equals("")) {
                        int from = Integer.valueOf(mStartDate.getText().toString().replace("-", ""));
                        int to = Integer.valueOf(mEndDate.getText().toString().replace("-", ""));

                        if (to < from) {
                            new CustomAlertDialog(getActivity(), "알림" + "\n\n검색일를 재설정 해주시길 바랍니다.");
                            return;
                        }
                    }
                } catch (Exception ex) { }
                onLoadData();
                break;

            case R.id.et_start_date:
                mDateField = "DATEFROM";
                try {
                    int year = Integer.parseInt(mStartDate.getText().toString().split("-")[0]);
                    int month = Integer.parseInt(mStartDate.getText().toString().split("-")[1]);
                    int day = Integer.parseInt(mStartDate.getText().toString().split("-")[2]);
                    onDatePicker(year, month - 1, day);
                } catch (Exception ex) {}
                break;

            case R.id.et_end_date:
                mDateField = "DATETO";
                try {
                    int year = Integer.parseInt(mEndDate.getText().toString().split("-")[0]);
                    int month = Integer.parseInt(mEndDate.getText().toString().split("-")[1]);
                    int day = Integer.parseInt(mEndDate.getText().toString().split("-")[2]);
                    onDatePicker(year, month - 1, day);
                } catch (Exception ex) {}
                break;
        }
    }

    private void onRefreshLayout() {
        if (mArrFnsList.size() > 0) {
            tvNoData.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    private AdapterView.OnItemSelectedListener SpnTerm = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int aa = position;

            switch (position) {
                case 0:
                case 1:
                    mStartDate.setText(Func.nowDate(0, 0));
                    mEndDate.setText(Func.nowDate(0, 0));
                    break;

                case 2:
                    mStartDate.setText(Func.nowDate(0, -1));
                    mEndDate.setText(Func.nowDate(0, -1));
                    break;

                case 3:
                    mStartDate.setText(Func.nowDate(0, -7));
                    mEndDate.setText(Func.nowDate(0, 0));
                    break;

                case 4:
                    mStartDate.setText(Func.nowDate(0, -30));
                    mEndDate.setText(Func.nowDate(0, 0));
                    break;

                case 5:
                    mStartDate.setText(Func.nowWeekSunDay());
                    mEndDate.setText(Func.nowWeekSaturDay());
                    break;

                case 6:
                    mStartDate.setText(Func.nowMonthFirstDay());
                    mEndDate.setText(Func.nowMonthLastDay());
                    break;

                case 7:
                    mStartDate.setText(Func.agoMonthFirstDay());
                    mEndDate.setText(Func.agoMonthLastDay());
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    protected void onListItemClick(AdapterView<?> parent, View v, int position, long id) { }

    public class FnsListAdapter extends BaseExpandableListAdapter {
        private ArrayList<FnsData> itemList;
        private ArrayList<FnsData> subList;
        private Context context;
        private int rowResourceId;

        public FnsListAdapter(Context context, int textViewResourceId, ArrayList<FnsData> itemList, ArrayList<FnsData> subList) {
            this.rowResourceId = textViewResourceId;
            this.itemList = itemList;
            this.subList = subList;
            this.context = context;
        }

        @Override
        public int getGroupCount() {
            return itemList.size();
        }

        @Override
        public String getGroup(int groupPosition) {
            return itemList.get(groupPosition).getId();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            View v = convertView;

            FnsData item = itemList.get(groupPosition);
            if(item != null)
            {
                v = SetFnsList(context, v, this.rowResourceId, item);
            }

            return v;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public String getChild(int groupPosition, int childPosition) {
            return subList.get(groupPosition).getId();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        // 차일드뷰 각각의 ROW
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            View v = convertView;

            FnsData item = subList.get(groupPosition);
            if(item != null)
            {
                v = SetFnsSubList(context, v, this.rowResourceId, item);
            }

            return v;
        }

        @Override
        public boolean hasStableIds() { return true; }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
    }

    private View SetFnsList(Context context, View v, int rowResourceId, FnsData Data)
    {
        try {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.freightlist_list, null);

            LinearLayout linLayout = (LinearLayout) v.findViewById(R.id.lin_layout);
            TextView tvFreightInfo = (TextView) v.findViewById(R.id.tv_freight_info);
            TextView tvFreightPrice = (TextView) v.findViewById(R.id.tv_freight_price);
            TextView tvFreightTon = (TextView) v.findViewById(R.id.tv_freight_ton);
            TextView tvFreightPickupArea = (TextView) v.findViewById(R.id.tv_freight_pickup_area);
            TextView tvFreightDeliveryArea = (TextView) v.findViewById(R.id.tv_freight_delivery_area);

            if (Data.getTimeSpanText().equals("확정")) {
                linLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.mark_confirm));
            } else if (Data.getTimeSpanText().equals("완료")) {
                linLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.mark_complete));
            }

            String price;
            price = Func.setPrice(Data.getPrice(), false);
            tvFreightInfo.setText(Data.getTimeSpanText() + " / " + Data.getOrderDate() + " " + Data.getEarlistPickupTime());

            String pickupName = Data.getPickupTypeName();

            if (!pickupName.equals("예약")) {
                pickupName = "당일";
            }

            tvFreightPrice.setText(pickupName + " " + price);
            tvFreightTon.setText(Data.getTonName());
            tvFreightPickupArea.setText(Data.getPickupAreaName());
            tvFreightDeliveryArea.setText(Data.getAreaName());

        } catch (Exception ex) { }

        return v;
    }

    private View SetFnsSubList(Context context, View v, int rowResourceId, final FnsData Data)
    {
        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.freightlist_sublist, null);

        TextView tvDeliveryCustomerName = (TextView) v.findViewById(R.id.tv_delivery_customer_name);
        TextView tvDeliveryCustomerPhone = (TextView) v.findViewById(R.id.tv_delivery_customer_phone);
        TextView tvPickupType = (TextView) v.findViewById(R.id.tv_pickup_type);
        TextView tvDeliveryType = (TextView) v.findViewById(R.id.tv_delivery_type);
        TextView tvItemKindType = (TextView) v.findViewById(R.id.tv_item_kind_type);
        TextView tvVehicleType = (TextView) v.findViewById(R.id.tv_vehicle_type);
        TextView tvCustomerOrderType = (TextView) v.findViewById(R.id.tv_customer_order_type);
        TextView tvPaymentType = (TextView) v.findViewById(R.id.tv_payment_type);
        TextView tvRemark = (TextView) v.findViewById(R.id.tv_remark);
        TextView tvCommunityName = (TextView) v.findViewById(R.id.tv_community_name);
        TextView tvTransID = (TextView) v.findViewById(R.id.tv_trans_id);
        Button btnCancel = (Button) v.findViewById(R.id.btn_cancel);
        btnCancel.setVisibility(View.GONE);
        /*btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFreightOrderCancel(Data.getTurnOid(), Data.getDriverUsreOid());
            }
        });*/
        Button btnModify = (Button) v.findViewById(R.id.btn_modify);
        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FreightInfoActivity)getActivity()).viewPager.setCurrentItem(0);
                onCloseGroup();
                customOnClickListener.onClicked(Data, Define.SCREEN_MODE_MODIFY);
            }
        });
        Button btnDelete = (Button) v.findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFreightOrderDelete(Data.getOid());
            }
        });
        Button btnReRegistration = (Button) v.findViewById(R.id.btn_reregistration);
        btnReRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FreightInfoActivity)getActivity()).viewPager.setCurrentItem(0);
                onCloseGroup();
                customOnClickListener.onClicked(Data, Define.SCREEN_MODE_REGISTER);
            }
        });
        Button btnTemplate = (Button) v.findViewById(R.id.btn_templete);
        btnTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTemplate(Data);
            }
        });

        if (!Data.getDeliveryCustomerName().equals("")) {
            tvDeliveryCustomerName.setVisibility(View.VISIBLE);
            tvDeliveryCustomerName.setText(Data.getDeliveryCustomerName());
        }

        if (!Data.getDeliveryCustomerPhone().equals("")) {
            tvDeliveryCustomerPhone.setVisibility(View.VISIBLE);
            tvDeliveryCustomerPhone.setText(PhoneNumberUtils.formatNumber(Data.getDeliveryCustomerPhone()));
            tvDeliveryCustomerPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri= Uri.parse("tel:" + Data.getDeliveryCustomerPhone());
                    Intent i = new Intent(Intent.ACTION_CALL, uri);
                    startActivity(i);
                }
            });
        }

        if (!Data.getPickupTypeName().equals("") || !Data.getLoadingMethodName().equals("")) {
            tvPickupType.setVisibility(View.VISIBLE);
            String pickupTime = "";
            String loadingMethodName = Data.getLoadingMethodName().equals("") ? "" : " / " + Data.getLoadingMethodName();
            String detailAddress = Data.getDetailAddress().equals("") ? "" : " / " + Data.getDetailAddress();

            if (Data.getPickupTypeCode().equals("20") || Data.getPickupTypeCode().equals("30") || Data.getPickupTypeCode().equals("70")) {
                pickupTime = Data.getRequestedPickupDate().equals("") ? "" : "(" + Data.getRequestedPickupDate() + ")";
            }

            tvPickupType.setText(Data.getPickupTypeName() + " " + pickupTime + loadingMethodName + detailAddress);
        }

        String deliveryTimeTypeName = Data.getDeliveryTimeTypeName().equals("") ? "" : Data.getDeliveryTimeTypeName();
        String deliveryTime = Data.getRequestedDeliveryDate().equals("") ? "" : " (" + Data.getRequestedDeliveryDate() + ")";
        String unloadingMethodName = Data.getUnloadingMethodName().equals("") ? "" : " / " + Data.getUnloadingMethodName();

        tvDeliveryType.setText(deliveryTimeTypeName + deliveryTime + unloadingMethodName);
        tvDeliveryType.setVisibility(View.VISIBLE);

        if (!Data.getItemKindName().equals("") || (!Data.getWeight().equals("") && !Data.getWeight().equals("0")) || !Data.getDeliveryTypeName().equals("")) {
            String itemKindName = Data.getItemKindName().equals("") ? "" : Data.getItemKindName();
            String weight = Data.getWeight().equals("") ? "" : " (" + Data.getWeight() + " 톤)";
            String deliveryTypeName = Data.getDeliveryTypeName().equals("") ? "" : " / " + Data.getDeliveryTypeName();
            tvItemKindType.setText(itemKindName + weight + deliveryTypeName);
            tvItemKindType.setVisibility(View.VISIBLE);
        }

        String widthText;
        String lengthText;
        String heightText;

        if (!Data.getWidthText().equals("") || !Data.getLengthText().equals("") || !Data.getHeightText().equals("")) {
            widthText = Func.checkStringNull(Data.getWidthText()).equals("") ? "" : " / 폭 " + Data.getWidthText() + ",";
            lengthText = Func.checkStringNull(Data.getLengthText()).equals("") ? "" : " 길이 " + Data.getLengthText() + ",";
            heightText = Func.checkStringNull(Data.getHeightText()).equals("") ? "" : " 높이 " + Data.getHeightText();
            tvVehicleType.setText(Data.getContainerTypeName() + widthText + lengthText + heightText);
            tvVehicleType.setVisibility(View.VISIBLE);
        }

        if (!Data.getCustomOrderTypeName().equals("")) {
            tvCustomerOrderType.setText(Data.getCustomOrderTypeName());
            tvCustomerOrderType.setVisibility(View.VISIBLE);
        }

        if (!Data.getPaymentTypeName().equals("")) {
            tvPaymentType.setText(Data.getPaymentTypeName());
            tvPaymentType.setVisibility(View.VISIBLE);
        }

        if (!Data.getRemark().equals("")) {
            tvRemark.setText("비고 : " + Data.getRemark());
            tvRemark.setVisibility(View.VISIBLE);
        }

        String communityName = "";
        int orgSize = Data.getOrgList().size();
        if (orgSize > 0) {
            for (int i = 0; i < orgSize; i++) {
                if (i == orgSize - 1) {
                    communityName += Data.getOrgList().get(i).getServiceName();
                } else {
                    communityName += Data.getOrgList().get(i).getServiceName() + ",";
                }
            }
        }

        tvCommunityName.setText("커뮤니티 : " + communityName);
        tvCommunityName.setVisibility(View.VISIBLE);
        tvTransID.setText("오더 : " + Data.getTransId());
        tvTransID.setVisibility(View.VISIBLE);

        if (Func.checkStringNull(Data.getStatus()).equals("3")) {
            btnCancel.setVisibility(View.VISIBLE);
        }

        if (Func.checkStringNull(Data.getStatus()).equals("11") || Func.checkStringNull(Data.getStatus()).equals("10")) {
            btnModify.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        }

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        customOnClickListener = (CustomOnClickListener)activity;
    }

    private void onTemplate(FnsData data) {
        try {

            if (mTemplateOrderDb.FetchTemplateCnt(((FreightInfoActivity)getActivity()).mServiceCode) >= Define.TEMPLETE_MAX_COUNT) {
                new CustomAlertDialog(getActivity(), "알림" + "\n\n템플릿은 최대 "+ Define.TEMPLETE_MAX_COUNT + "개까지 등록 가능합니다.");
                return;
            }

            FnsTemplateData fnsTemplateData = new FnsTemplateData();
            fnsTemplateData.setSiteCode(((FreightInfoActivity)getActivity()).mServiceCode);
            fnsTemplateData.setID(data.getId());
            fnsTemplateData.setTransID(data.getTransId());
            fnsTemplateData.setOid(data.getOid());
            fnsTemplateData.setTurnOid(data.getTurnOid());
            fnsTemplateData.setDriverUserOid(data.getDriverUsreOid());
            fnsTemplateData.setStatus(data.getStatus());
            fnsTemplateData.setOrderDate(data.getOrderDate());
            fnsTemplateData.setEarlistPickupTime(data.getEarlistPickupTime());
            fnsTemplateData.setTransDate(data.getTransDate());
            fnsTemplateData.setTimeSpan(data.getTimeSpan());
            fnsTemplateData.setTimeSpanText(data.getTimeSpanText());
            fnsTemplateData.setIlDeliveryCustomer(data.getIlDeliveryCustomer());
            fnsTemplateData.setDeliveryCustomerName(data.getDeliveryCustomerName());
            fnsTemplateData.setDeliveryCustomerPhone(data.getDeliveryCustomerPhone());
            fnsTemplateData.setIlPickupArea(data.getIlPickupArea());
            fnsTemplateData.setPickupAreaName(data.getPickupAreaName());
            fnsTemplateData.setPickupTypeCode(data.getPickupTypeCode());
            fnsTemplateData.setPickupTypeName(data.getPickupTypeName());
            fnsTemplateData.setPickupTime(data.getPickupTime());
            fnsTemplateData.setDeliveryTime(data.getDeliveryTime());
            fnsTemplateData.setIlArea(data.getIlArea());
            fnsTemplateData.setAreaName(data.getAreaName());
            fnsTemplateData.setAddress(data.getAddress());
            fnsTemplateData.setDetailAddress(data.getDetailAddress());
            fnsTemplateData.setDeliveryTimeTypeCode(data.getDeliveryTimeTypeCode());
            fnsTemplateData.setDeliveryTimeTypeName(data.getDeliveryTimeTypeName());
            fnsTemplateData.setTonCode(data.getTonCode());
            fnsTemplateData.setTonName(data.getTonName());
            fnsTemplateData.setContainerTypeCode(data.getContainerTypeCode());
            fnsTemplateData.setContainerTypeName(data.getContainerTypeName());
            fnsTemplateData.setPrice(data.getPrice());
            fnsTemplateData.setPaymentType(data.getPaymentType());
            fnsTemplateData.setPaymentTypeName(data.getPaymentTypeName());
            fnsTemplateData.setItemKindCode(data.getItemKindCode());
            fnsTemplateData.setItemKindName(data.getItemKindName());
            fnsTemplateData.setVehicleID(data.getVehicleID());
            fnsTemplateData.setLoadingMethod(data.getLoadingMethod());
            fnsTemplateData.setLoadingMethodName(data.getLoadingMethodName());
            fnsTemplateData.setUnloadingMethod(data.getUnloadingMethod());
            fnsTemplateData.setUnloadingMethodName(data.getUnloadingMethodName());
            fnsTemplateData.setDeliveryType(data.getDeliveryType());
            fnsTemplateData.setDeliveryTypeName(data.getDeliveryTypeName());
            fnsTemplateData.setWidthText(data.getWidthText());
            fnsTemplateData.setLengthText(data.getLengthText());
            fnsTemplateData.setHeightText(data.getHeightText());
            fnsTemplateData.setWeight(data.getWeight());
            fnsTemplateData.setCustomOrderType(data.getCustomOrderType());
            fnsTemplateData.setCustomOrderTypeName(data.getCustomOrderTypeName());
            fnsTemplateData.setRemark(data.getRemark());
            fnsTemplateData.setRequestedPickupDate(data.getRequestedPickupDate());
            fnsTemplateData.setRequestedDeliveryDate(data.getRequestedDeliveryDate());
            fnsTemplateData.setOrderTime(data.getOrderTime());
            fnsTemplateData.setDriverName(data.getDriverName());
            fnsTemplateData.setDriverMobilePhone(data.getDriverMobilePhone());
            fnsTemplateData.setStatusName(data.getStatusName());
            fnsTemplateData.setCharged(data.getCharged());
            fnsTemplateData.setChargedText(data.getChargedText());
            fnsTemplateData.setUserName(data.getUserName());
            fnsTemplateData.setUserMobilePhone(data.getUserMobilePhone());
            fnsTemplateData.setOwnerName(data.getOwnerName());
            fnsTemplateData.setOwnerPhone(data.getOwnerPhone());
            fnsTemplateData.setVehicleNo(data.getVehicleNo());
            mTemplateOrderDb.CreateTemplateData(fnsTemplateData);

            Toast.makeText(getActivity(), "템플릿으로 저장 되었습니다.", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), "템플릿 저장에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private void onDatePicker(int year, int month, int day) {
        DatePickerDialog dpd = DatePickerDialog.newInstance(this, year, month, day);
        dpd.setThemeDark(true);
        dpd.vibrate(true);
        dpd.dismissOnPause(true);
        dpd.showYearPickerFirst(false);
        dpd.setYearRange(year - 1, year);
        dpd.setAccentColor(Color.parseColor("#04a2bc"));
        dpd.setTitle("날짜 설정");
        dpd.show(getActivity().getFragmentManager(), "Datepicker");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String month;
        String day;

        if ((monthOfYear + 1) < 10) {
            month = "0" + (monthOfYear + 1);
        } else {
            month = "" + (monthOfYear + 1);
        }

        if (dayOfMonth < 10) {
            day = "0" + dayOfMonth;
        } else {
            day = "" + dayOfMonth;
        }

        String date = year + "-" + month + "-" + day;
        if (mDateField.equals("DATEFROM")) {
            mStartDate.setText(date);
        } else {
            mEndDate.setText(date);
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