package com.logishub.mobile.launcher.v5;

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
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.logishub.mobile.launcher.v5.Common.CustomAlertDialog;
import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.Common.HttpHelper;
import com.logishub.mobile.launcher.v5.Common.HttpService;
import com.logishub.mobile.launcher.v5.Common.PrefsUtil;
import com.logishub.mobile.launcher.v5.DATA.FnsData;
import com.logishub.mobile.launcher.v5.DATA.RequestFreightRegisterData;
import com.logishub.mobile.launcher.v5.DATA.ResponseFreightData;
import com.logishub.mobile.launcher.v5.DATA.ServiceData;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreightHistoryFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public View mView;
    private CustomProgressDialog mProgressDialog;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;
    private String mErrorMessage;
    private EditText mStartDate;
    private EditText mEndDate;
    private EditText mCompanyName;
    private EditText mDriverName;
    private Spinner mTerm;
    private Spinner mViewMethod;
    private Spinner mClassifyConclude;
    private static ArrayList<FnsData> mArrFnsHistoryList = null;
    private static ArrayList<ServiceData> mArrServiceList = null;
    private ExpandableListView mFnsHistoryList;
    private TextView tvNoData;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FnsHistoryListAdapter mFnsHistoryListAdapter = null;
    private String mDateField;
    private TextView mTotalInfo;

    public FreightHistoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_freight_history, container, false);

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
        mStartDate = null;
        mEndDate = null;
        mArrFnsHistoryList = null;
        mArrServiceList = null;
        mFnsHistoryList = null;
        mSwipeRefreshLayout = null;
        mDateField = null;
        mTerm = null;
        mViewMethod = null;
        mClassifyConclude = null;
        mCompanyName = null;
        mDriverName = null;
        mTotalInfo = null;
        mFnsHistoryListAdapter = null;
    }

    private void setLayout(View v) {
        mArrFnsHistoryList = new ArrayList<FnsData>();
        mArrServiceList = new ArrayList<ServiceData>();
        mFnsHistoryList = (ExpandableListView) v.findViewById(R.id.eplv_freight_list);
        tvNoData = (TextView) v.findViewById(R.id.tv_no_data);
        mViewMethod = (Spinner) v.findViewById(R.id.sp_view_method);
        mClassifyConclude = (Spinner) v.findViewById(R.id.sp_classify_conclude);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mFnsHistoryListAdapter = new FnsHistoryListAdapter(getActivity(), R.layout.freighthistorylist_list, mArrFnsHistoryList, mArrFnsHistoryList);
        mFnsHistoryList.setAdapter(mFnsHistoryListAdapter);

        mFnsHistoryList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int groupCount = mFnsHistoryListAdapter.getGroupCount();

                for (int i = 0; i < groupCount; i++) {
                    if (!(i == groupPosition))
                        mFnsHistoryList.collapseGroup(i);
                }
            }
        });
        mFnsHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                onListItemClick(parent, v, position, id);
            }
        });

        mTerm = (Spinner) v.findViewById(R.id.sp_term);
        mTerm.setOnItemSelectedListener(SpnTerm);

        mStartDate = (EditText) v.findViewById(R.id.et_start_date);
        mEndDate = (EditText) v.findViewById(R.id.et_end_date);
        mCompanyName = (EditText) v.findViewById(R.id.et_company_name);
        mDriverName = (EditText) v.findViewById(R.id.et_driver_name);
        mTotalInfo = (TextView) v.findViewById(R.id.tv_total_info);

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

    private void onLoadData() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    onFreightHistory();
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void onFreightHistory() {
        RequestFreightRegisterData requestFreightRegisterData = new RequestFreightRegisterData();
        requestFreightRegisterData.setDateFrom(mStartDate.getText().toString());
        requestFreightRegisterData.setDateTo(mEndDate.getText().toString());
        requestFreightRegisterData.setSiteCode(((FreightInfoActivity)getActivity()).mServiceCode);
        requestFreightRegisterData.setServiceUrl(((FreightInfoActivity)getActivity()).mServiceURL);
        requestFreightRegisterData.setUserID(((FreightInfoActivity)getActivity()).mArrUserList.get(0).getUserID());
        requestFreightRegisterData.setUserSiteKey(Func.checkStringNull(PrefsUtil.getValue(((FreightInfoActivity)getActivity()).mServiceCode, "")));
        requestFreightRegisterData.setCharged(String.valueOf(mClassifyConclude.getSelectedItemPosition()));
        requestFreightRegisterData.setCustomerName(mCompanyName.getText().toString());
        requestFreightRegisterData.setDriverName(mDriverName.getText().toString());

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseFreightData> call = mHttpService.getFNSHistory(requestFreightRegisterData);
        call.enqueue(new Callback<ResponseFreightData>() {
            @Override
            public void onResponse(Call<ResponseFreightData> call, Response<ResponseFreightData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseFreightData data = response.body();

                        if (data.getErrorCode().equals("0")) {

                            mArrFnsHistoryList.clear();
                            HashMap<String, Double> map = new HashMap<>();

                            double totalPrice = 0;
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
                                            serviceData.setServiceOid(data.getFnsList().get(i).getOrgList().get(j).getServiceOid());
                                            serviceData.setServiceName(data.getFnsList().get(i).getOrgList().get(j).getServiceName());

                                            mArrServiceList.add(serviceData);

                                            fnsData.setOrgList(mArrServiceList);
                                        }
                                    }

                                    /** 내역보기*/
                                    if (mViewMethod.getSelectedItemPosition() == 0) {
                                        mArrFnsHistoryList.add(fnsData);
                                    /** 업체별 내역보기*/
                                    } else if (mViewMethod.getSelectedItemPosition() == 1) {

                                        double price = 0;

                                        if(map.containsKey(fnsData.getDeliveryCustomerName())) {
                                            price = map.get(fnsData.getDeliveryCustomerName());
                                        }

                                        if (!Func.checkStringNull(fnsData.getPrice()).equals("")) {
                                            price += Double.valueOf(fnsData.getPrice());
                                        } else {
                                            price += 0;
                                        }

                                        map.put(fnsData.getDeliveryCustomerName(), price);

                                    /** 월별 업체별 내역보기*/
                                    } else if (mViewMethod.getSelectedItemPosition() == 2) {

                                        double price = 0;

                                        if(map.containsKey(fnsData.getOrderDate().split("-")[0] +"/"+ fnsData.getDeliveryCustomerName())) {
                                            price = map.get(fnsData.getOrderDate().split("-")[0] +"/"+ fnsData.getDeliveryCustomerName());
                                        }

                                        if (!Func.checkStringNull(fnsData.getPrice()).equals("")) {
                                            price += Double.valueOf(fnsData.getPrice());
                                        } else {
                                            price += 0;
                                        }

                                        map.put(fnsData.getOrderDate().split("-")[0] +"/"+ fnsData.getDeliveryCustomerName(), price);

                                    }
                                }

                                if (mViewMethod.getSelectedItemPosition() != 0) {
                                    Iterator<String> iter = map.keySet().iterator();
                                    while(iter.hasNext()) {
                                        String key = iter.next();
                                        Double value = map.get(key);

                                        FnsData fnsData = new FnsData();

                                        fnsData.setDeliveryCustomerName(key);
                                        fnsData.setPrice(String.valueOf(value));

                                        mArrFnsHistoryList.add(fnsData);
                                    }

                                    if (mViewMethod.getSelectedItemPosition() == 2) {
                                        List<FnsData> fnsDatas = new ArrayList<FnsData>();

                                        for (int i = 0; i < mArrFnsHistoryList.size(); i++) {
                                            FnsData fnsData = new FnsData();
                                            fnsData.setDeliveryCustomerName(mArrFnsHistoryList.get(i).getDeliveryCustomerName());
                                            fnsData.setPrice(mArrFnsHistoryList.get(i).getPrice());
                                            fnsDatas.add(fnsData);
                                        }

                                        Collections.sort(fnsDatas, mComparator);
                                        Collections.reverse(fnsDatas);

                                        mArrFnsHistoryList.clear();
                                        mArrFnsHistoryList.addAll(fnsDatas);
                                    }
                                }

                                for (int i = 0; i < mArrFnsHistoryList.size(); i++) {
                                    if (!Func.checkStringNull(mArrFnsHistoryList.get(i).getPrice()).equals("")) {
                                        totalPrice += Double.valueOf(mArrFnsHistoryList.get(i).getPrice());
                                    }
                                }

                            }

                            mTotalInfo.setText("총합계 : " + Func.setPrice(Double.toString(totalPrice), true));
                            mFnsHistoryListAdapter.notifyDataSetChanged();

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

    private final static Comparator<FnsData> mComparator = new Comparator<FnsData>() {

        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(FnsData object1, FnsData object2) {
            return collator.compare(object1.getDeliveryCustomerName(), object2.getDeliveryCustomerName());
        }
    };

    private AdapterView.OnItemSelectedListener SpnTerm = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            switch (position) {
                case 0:
                    mStartDate.setText(Func.nowDate(0, 0));
                    mEndDate.setText(Func.nowDate(0, 0));
                    break;

                case 1:
                    mStartDate.setText(Func.nowWeekSunDay());
                    mEndDate.setText(Func.nowWeekSaturDay());
                    break;

                case 2:
                    mStartDate.setText(Func.nowMonthFirstDay());
                    mEndDate.setText(Func.nowMonthLastDay());
                    break;

                case 3:
                    mStartDate.setText(Func.agoMonthFirstDay());
                    mEndDate.setText(Func.agoMonthLastDay());
                    break;

                case 4:
                    mStartDate.setText(Func.nowDate(-3, 0));
                    mEndDate.setText(Func.nowDate(0, 0));
                    break;

                case 5:
                    mStartDate.setText(Func.nowQuarterDate(1, -1));
                    mEndDate.setText(Func.nowQuarterDate(1, 1));
                    break;

                case 6:
                    mStartDate.setText(Func.nowQuarterDate(-1, -1));
                    mEndDate.setText(Func.nowQuarterDate(-1, 1));
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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

    private void onCloseGroup() {
        int groupCount = mFnsHistoryListAdapter.getGroupCount();

        for (int i = 0; i < groupCount; i++) {
            mFnsHistoryList.collapseGroup(i);
        }
    }

    private void onRefreshLayout() {
        if (mArrFnsHistoryList.size() > 0) {
            tvNoData.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    protected void onListItemClick(AdapterView<?> parent, View v, int position, long id) {
    }

    public class FnsHistoryListAdapter extends BaseExpandableListAdapter {
        private ArrayList<FnsData> itemList;
        private ArrayList<FnsData> subList;
        private Context context;
        private int rowResourceId;

        public FnsHistoryListAdapter(Context context, int textViewResourceId, ArrayList<FnsData> itemList, ArrayList<FnsData> subList) {
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
                if (mViewMethod.getSelectedItemPosition() != 0) {
                    v = SetFnsCustomerList(context, v, this.rowResourceId, item);
                } else {
                    v = SetFnsHistoryList(context, v, this.rowResourceId, item);
                }
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
                if (mViewMethod.getSelectedItemPosition() == 0) {
                    v = SetFnsHistorySubList(context, v, this.rowResourceId, item);
                } else {
                    v = null;
                }
            }

            return v;
        }

        @Override
        public boolean hasStableIds() { return true; }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
    }

    private View SetFnsHistoryList(Context context, View v, int rowResourceId, FnsData Data)
    {
        try {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.freighthistorylist_list, null);

            LinearLayout linLayout = (LinearLayout) v.findViewById(R.id.lin_layout);
            TextView tvFreightInfo = (TextView) v.findViewById(R.id.tv_delivery_date);
            TextView tvFreightStatus = (TextView) v.findViewById(R.id.tv_delivery_status);
            TextView tvFreightPrice = (TextView) v.findViewById(R.id.tv_freight_price);
            TextView tvFreightPickupArea = (TextView) v.findViewById(R.id.tv_freight_pickup_area);
            TextView tvFreightDeliveryArea = (TextView) v.findViewById(R.id.tv_freight_delivery_area);

            String price;
            price = Func.setPrice(Data.getPrice(), true);
            tvFreightInfo.setText(Data.getOrderDate());
            tvFreightStatus.setText(Data.getStatusName());
            if (Data.getStatusName().equals(getResources().getString(R.string.allocation))) {
            } else if (Data.getStatusName().equals(getResources().getString(R.string.complete))) {
                linLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.mark_complete));
            } else if (Data.getStatusName().equals(getResources().getString(R.string.allocation_fail))) {
                linLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.mark_refusal));
            } else if (Data.getStatusName().equals(getResources().getString(R.string.transfer))) {
                linLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.mark_transport));
            }

            tvFreightPrice.setText(price);
            tvFreightPickupArea.setText(Data.getPickupAreaName());
            tvFreightDeliveryArea.setText(Data.getAreaName());

        } catch (Exception ex) { }

        return v;
    }

    private View SetFnsHistorySubList(Context context, View v, int rowResourceId, final FnsData Data)
    {
        try {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.freighthistorylist_sublist, null);

            TextView tvDeliveryCustomerName = (TextView) v.findViewById(R.id.tv_delivery_customer_name);
            TextView tvDeliveryCustomerPhone = (TextView) v.findViewById(R.id.tv_delivery_customer_phone);
            TextView tvUserName = (TextView) v.findViewById(R.id.tv_delivery_user_name);
            TextView tvUserPhone = (TextView) v.findViewById(R.id.tv_delivery_user_phone);
            TextView tvArea = (TextView) v.findViewById(R.id.tv_area);
            TextView tvOrderNo = (TextView) v.findViewById(R.id.tv_order_no);
            TextView tvRegisterDate = (TextView) v.findViewById(R.id.tv_register_date);
            TextView tvDriverInfo = (TextView) v.findViewById(R.id.tv_driver_info);
            TextView tvDriverPhone = (TextView) v.findViewById(R.id.tv_driver_phone);
            TextView tvCommunityName = (TextView) v.findViewById(R.id.tv_community_name);

            if (!Data.getDeliveryCustomerName().equals("")) {
                tvDeliveryCustomerName.setText("업체 : " + Data.getDeliveryCustomerName());
                tvDeliveryCustomerName.setVisibility(View.VISIBLE);
            }

            if (!Data.getDeliveryCustomerPhone().equals("")) {
                tvDeliveryCustomerPhone.setText(PhoneNumberUtils.formatNumber(Data.getDeliveryCustomerPhone()));
                tvDeliveryCustomerPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri= Uri.parse("tel:" + Data.getDeliveryCustomerPhone());
                        Intent i = new Intent(Intent.ACTION_CALL, uri);
                        startActivity(i);
                    }
                });
                tvDeliveryCustomerPhone.setVisibility(View.VISIBLE);
            }

            if (!Data.getOwnerName().equals("")) {
                tvUserName.setText(Data.getOwnerName());
                tvUserPhone.setText(PhoneNumberUtils.formatNumber(Data.getOwnerPhone()));
                tvUserPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri= Uri.parse("tel:" + Data.getOwnerPhone());
                        Intent i = new Intent(Intent.ACTION_CALL, uri);
                        startActivity(i);
                    }
                });
            } else {
                tvUserName.setText(Data.getUserName());
                tvUserPhone.setText(PhoneNumberUtils.formatNumber(Data.getUserMobilePhone()));
                tvUserPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri= Uri.parse("tel:" + Data.getUserMobilePhone());
                        Intent i = new Intent(Intent.ACTION_CALL, uri);
                        startActivity(i);
                    }
                });
            }

            tvUserName.setVisibility(View.VISIBLE);
            tvUserPhone.setVisibility(View.VISIBLE);

            tvArea.setText(Data.getPickupAreaName() +" > "+Data.getAreaName());
            tvArea.setVisibility(View.VISIBLE);

            if (!Data.getTransId().equals("")) {
                tvOrderNo.setText("오더 : " + Data.getTransId());
                tvOrderNo.setVisibility(View.VISIBLE);
            }

            tvRegisterDate.setText("등록 : " + Data.getOrderDate());
            tvRegisterDate.setVisibility(View.VISIBLE);

            if (Integer.valueOf(Data.getStatus()) >=2 && Integer.valueOf(Data.getStatus()) <=5) {
                tvDriverInfo.setText("기사 : " + Data.getDriverName() + " (" + Data.getVehicleNo() + ") ");
                tvDriverPhone.setText(PhoneNumberUtils.formatNumber(Data.getDriverMobilePhone()));
                tvDriverPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri= Uri.parse("tel:" + Data.getDriverMobilePhone());
                        Intent i = new Intent(Intent.ACTION_CALL, uri);
                        startActivity(i);
                    }
                });

                tvDriverInfo.setVisibility(View.VISIBLE);
                tvDriverPhone.setVisibility(View.VISIBLE);
            }

            String communityName = "";
            int orgSize = Data.getOrgList().size();
            if (orgSize > 0) {
                for (int i = 0; i < orgSize; i++) {
                    if (i == orgSize - 1) {
                        communityName += Data.getOrgList().get(i).getServiceName();
                    } else {
                        communityName += Data.getOrgList().get(i).getServiceName() + ", ";
                    }
                }
            }

            tvCommunityName.setText("커뮤니티 : " + communityName);
            tvCommunityName.setVisibility(View.VISIBLE);

        } catch (Exception ex) { }

        return v;
    }

    private View SetFnsCustomerList(Context context, View v, int rowResourceId, FnsData Data)
    {
        try {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.freightcustomerlist_list, null);

            TextView tvCustomerName = (TextView) v.findViewById(R.id.tv_customer_name);
            TextView tvFreightPrice = (TextView) v.findViewById(R.id.tv_freight_price);

            String customerName;
            String price;
            price = Func.setPrice(Data.getPrice(), true);

            if (mViewMethod.getSelectedItemPosition() == 2) {
                customerName = Data.getDeliveryCustomerName().replace("/", "월 ");
                String[] customer = Data.getDeliveryCustomerName().split("/");
                if (customer.length < 2) {
                    customerName += "(미입력)";
                }
            } else {
                if (Func.checkStringNull(Data.getDeliveryCustomerName()).equals("")) {
                    customerName = "(미입력)";
                } else {
                    customerName = Data.getDeliveryCustomerName();
                }
            }

            tvCustomerName.setText(customerName);
            tvFreightPrice.setText(price);

        } catch (Exception ex) { }

        return v;
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