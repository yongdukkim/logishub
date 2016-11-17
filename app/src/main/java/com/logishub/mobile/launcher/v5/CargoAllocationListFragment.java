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
import android.widget.Button;
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
import com.logishub.mobile.launcher.v5.DATA.CargoData;
import com.logishub.mobile.launcher.v5.DATA.RequestFreightRegisterData;
import com.logishub.mobile.launcher.v5.DATA.ResponseCargoData;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CargoAllocationListFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    public View mView;
    private CustomProgressDialog mProgressDialog;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;
    private String mErrorMessage;
    private Spinner mTerm;
    private Spinner mOrderStatus;
    private EditText mStartDate;
    private EditText mEndDate;
    private String mDateField;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<CargoData> mArrFnsList = null;
    private FnsListAdapter mFnsListAdapter = null;
    private ExpandableListView mFnsList;
    private TextView tvNoData;
    private String[] mOrderStatusText = new String[]{ "", "Dispatched", "Waitng", "Confirm", "Refuse", "Executing", "Complete" };

    public CargoAllocationListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_cargo_allocation_list, container, false);

        setLayout(mView);

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
        mDateField = null;
        mSwipeRefreshLayout = null;
        mArrFnsList = null;
        mFnsListAdapter = null;
        mFnsList = null;
        tvNoData = null;
        mOrderStatus = null;
    }

    private void setLayout(View v) {
        mArrFnsList = new ArrayList<>();
        tvNoData = (TextView) v.findViewById(R.id.tv_no_data);
        mFnsList = (ExpandableListView) v.findViewById(R.id.eplv_freight_list);
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

        mOrderStatus = (Spinner) v.findViewById(R.id.sp_order_status);

        mStartDate = (EditText) v.findViewById(R.id.et_start_date);
        mEndDate = (EditText) v.findViewById(R.id.et_end_date);
        mStartDate.setText(Func.nowDate(0, 0));
        mEndDate.setText(Func.nowDate(0, 0));

        v.findViewById(R.id.btn_freight_info).setOnClickListener(this);
        v.findViewById(R.id.et_start_date).setOnClickListener(this);
        v.findViewById(R.id.et_end_date).setOnClickListener(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onLoadOrderData();
            }
        });

        onLoadOrderData();
    }

    private void onLoadOrderData() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    onOrderData();
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void onCompleteOrderData(final String mTurnInfoOid, final String mOrgID, final String mVehicleAllocationBy) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    onCompleteOrder(mTurnInfoOid, mOrgID, mVehicleAllocationBy);
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    /*private void onCancelOrderData(final String mTurnInfoOid, final String mOrgID, final String mVehicleAllocationBy) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    onCancelOrder(mTurnInfoOid, mOrgID, mVehicleAllocationBy);
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }*/

    private void onOrderData() {
        RequestFreightRegisterData requestFreightRegisterData = new RequestFreightRegisterData();
        requestFreightRegisterData.setDateFrom(mStartDate.getText().toString());
        requestFreightRegisterData.setDateTo(mEndDate.getText().toString());
        requestFreightRegisterData.setUserOID(((CargoInfoActivity)getActivity()).mArrUserList.get(0).getUserOid());
        requestFreightRegisterData.setTurnInfoOid("");
        requestFreightRegisterData.setTurnID("");
        requestFreightRegisterData.setOrgOid(((CargoInfoActivity)getActivity()).mServiceOid);
        requestFreightRegisterData.setAssignedOrgOid("");
        requestFreightRegisterData.setSiteCode(((CargoInfoActivity)getActivity()).mServiceCode);
        requestFreightRegisterData.setUserID(((CargoInfoActivity)getActivity()).mArrUserList.get(0).getUserID());
        requestFreightRegisterData.setStatus(mOrderStatusText[mOrderStatus.getSelectedItemPosition()]);
        requestFreightRegisterData.setUserPortalKey(((CargoInfoActivity)getActivity()).mArrUserList.get(0).getUserKey());
        requestFreightRegisterData.setUserSiteKey(Func.checkStringNull(PrefsUtil.getValue(((CargoInfoActivity)getActivity()).mServiceCode, "")));

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseCargoData> call = mHttpService.getAssignedFNSOrder(requestFreightRegisterData);
        call.enqueue(new Callback<ResponseCargoData>() {
            @Override
            public void onResponse(Call<ResponseCargoData> call, Response<ResponseCargoData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseCargoData data = response.body();

                        if (data.getErrorCode().equals("0")) {

                            mArrFnsList.clear();

                            int mFnsSize = data.getFnsList().size();
                            if (mFnsSize > 0) {
                                for (int i = 0; i < mFnsSize; i++) {
                                    CargoData cargoData = new CargoData();

                                    cargoData.setId(Func.checkStringNull(data.getFnsList().get(i).getId()));
                                    cargoData.setTransId(Func.checkStringNull(data.getFnsList().get(i).getTransId()));
                                    cargoData.setOid(Func.checkStringNull(data.getFnsList().get(i).getOid()));
                                    cargoData.setTurnOid(Func.checkStringNull(data.getFnsList().get(i).getTurnOid()));
                                    cargoData.setDriverUsreOid(Func.checkStringNull(data.getFnsList().get(i).getDriverUsreOid()));
                                    cargoData.setStatus(Func.checkStringNull(data.getFnsList().get(i).getStatus()));
                                    cargoData.setOrderDate(Func.checkStringNull(data.getFnsList().get(i).getOrderDate()));
                                    cargoData.setEarlistPickupTime(Func.checkStringNull(data.getFnsList().get(i).getEarlistPickupTime()));
                                    cargoData.setTransDate(Func.checkStringNull(data.getFnsList().get(i).getTransDate()));
                                    cargoData.setTimeSpan(Func.checkStringNull(data.getFnsList().get(i).getTimeSpan()));
                                    cargoData.setTimeSpanText(Func.checkStringNull(data.getFnsList().get(i).getTimeSpanText()));
                                    cargoData.setIlDeliveryCustomer(Func.checkStringNull(data.getFnsList().get(i).getIlDeliveryCustomer()));
                                    cargoData.setDeliveryCustomerName(Func.checkStringNull(data.getFnsList().get(i).getDeliveryCustomerName()));
                                    cargoData.setDeliveryCustomerPhone(Func.checkStringNull(data.getFnsList().get(i).getDeliveryCustomerPhone()));
                                    cargoData.setIlPickupArea(Func.checkStringNull(data.getFnsList().get(i).getIlPickupArea()));
                                    cargoData.setPickupAreaName(Func.checkStringNull(data.getFnsList().get(i).getPickupAreaName()));
                                    cargoData.setPickupTypeCode(Func.checkStringNull(data.getFnsList().get(i).getPickupTypeCode()));
                                    cargoData.setPickupTypeName(Func.checkStringNull(data.getFnsList().get(i).getPickupTypeName()));
                                    cargoData.setPickupTime(Func.checkStringNull(data.getFnsList().get(i).getPickupTime()));
                                    cargoData.setDeliveryTime(Func.checkStringNull(data.getFnsList().get(i).getDeliveryTime()));
                                    cargoData.setIlArea(Func.checkStringNull(data.getFnsList().get(i).getIlArea()));
                                    cargoData.setAreaName(Func.checkStringNull(data.getFnsList().get(i).getAreaName()));
                                    cargoData.setAddress(Func.checkStringNull(data.getFnsList().get(i).getAddress()));
                                    cargoData.setDetailAddress(Func.checkStringNull(data.getFnsList().get(i).getDetailAddress()));
                                    cargoData.setDeliveryTimeTypeCode(Func.checkStringNull(data.getFnsList().get(i).getDeliveryTimeTypeCode()));
                                    cargoData.setDeliveryTimeTypeName(Func.checkStringNull(data.getFnsList().get(i).getDeliveryTimeTypeName()));
                                    cargoData.setTonCode(Func.checkStringNull(data.getFnsList().get(i).getTonCode()));
                                    cargoData.setTonName(Func.checkStringNull(data.getFnsList().get(i).getTonName()));
                                    cargoData.setContainerTypeCode(Func.checkStringNull(data.getFnsList().get(i).getContainerTypeCode()));
                                    cargoData.setContainerTypeName(Func.checkStringNull(data.getFnsList().get(i).getContainerTypeName()));
                                    cargoData.setPrice(Func.checkStringNull(data.getFnsList().get(i).getPrice()));
                                    cargoData.setPaymentType(Func.checkStringNull(data.getFnsList().get(i).getPaymentType()));
                                    cargoData.setPaymentTypeName(Func.checkStringNull(data.getFnsList().get(i).getPaymentTypeName()));
                                    cargoData.setItemKindCode(Func.checkStringNull(data.getFnsList().get(i).getItemKindCode()));
                                    cargoData.setItemKindName(Func.checkStringNull(data.getFnsList().get(i).getItemKindName()));
                                    cargoData.setVehicleID(Func.checkStringNull(data.getFnsList().get(i).getVehicleID()));
                                    cargoData.setLoadingMethod(Func.checkStringNull(data.getFnsList().get(i).getLoadingMethod()));
                                    cargoData.setLoadingMethodName(Func.checkStringNull(data.getFnsList().get(i).getLoadingMethodName()));
                                    cargoData.setUnloadingMethod(Func.checkStringNull(data.getFnsList().get(i).getUnloadingMethod()));
                                    cargoData.setUnloadingMethodName(Func.checkStringNull(data.getFnsList().get(i).getUnloadingMethodName()));
                                    cargoData.setDeliveryType(Func.checkStringNull(data.getFnsList().get(i).getDeliveryType()));
                                    cargoData.setDeliveryTypeName(Func.checkStringNull(data.getFnsList().get(i).getDeliveryTypeName()));
                                    cargoData.setWidthText(Func.checkStringNull(data.getFnsList().get(i).getWidthText()));
                                    cargoData.setLengthText(Func.checkStringNull(data.getFnsList().get(i).getLengthText()));
                                    cargoData.setHeightText(Func.checkStringNull(data.getFnsList().get(i).getHeightText()));
                                    cargoData.setWeight(Func.checkStringNull(data.getFnsList().get(i).getWeight()));
                                    cargoData.setCustomOrderType(Func.checkStringNull(data.getFnsList().get(i).getCustomOrderType()));
                                    cargoData.setCustomOrderTypeName(Func.checkStringNull(data.getFnsList().get(i).getCustomOrderTypeName()));
                                    cargoData.setRemark(Func.checkStringNull(data.getFnsList().get(i).getRemark()));
                                    cargoData.setRequestedPickupDate(Func.checkStringNull(data.getFnsList().get(i).getRequestedPickupDate()));
                                    cargoData.setRequestedDeliveryDate(Func.checkStringNull(data.getFnsList().get(i).getRequestedDeliveryDate()));
                                    cargoData.setOrderTime(Func.checkStringNull(data.getFnsList().get(i).getOrderTime()));
                                    cargoData.setDriverName(Func.checkStringNull(data.getFnsList().get(i).getDriverName()));
                                    cargoData.setDriverMobilePhone(Func.checkStringNull(data.getFnsList().get(i).getDriverMobilePhone()));
                                    cargoData.setStatusName(Func.checkStringNull(data.getFnsList().get(i).getStatusName()));
                                    cargoData.setCharged(Func.checkStringNull(data.getFnsList().get(i).getCharged()));
                                    cargoData.setChargedText(Func.checkStringNull(data.getFnsList().get(i).getChargedText()));
                                    cargoData.setUserName(Func.checkStringNull(data.getFnsList().get(i).getUserName()));
                                    cargoData.setUserMobilePhone(Func.checkStringNull(data.getFnsList().get(i).getUserMobilePhone()));
                                    cargoData.setOwnerName(Func.checkStringNull(data.getFnsList().get(i).getOwnerName()));
                                    cargoData.setOwnerPhone(Func.checkStringNull(data.getFnsList().get(i).getOwnerPhone()));
                                    cargoData.setVehicleNo(Func.checkStringNull(data.getFnsList().get(i).getVehicleNo()));
                                    cargoData.setTonCodeList(Func.checkStringNull(data.getFnsList().get(i).getTonCodeList()));
                                    cargoData.setItemInfo(Func.checkStringNull(data.getFnsList().get(i).getItemInfo()));
                                    cargoData.setTransStatus(Func.checkStringNull(data.getFnsList().get(i).getTransStatus()));
                                    cargoData.setTurnInfoOid(Func.checkStringNull(data.getFnsList().get(i).getTurnInfoOid()));
                                    cargoData.setOrgOid(Func.checkStringNull(data.getFnsList().get(i).getOrgOid()));
                                    cargoData.setOrgID(Func.checkStringNull(data.getFnsList().get(i).getOrgID()));
                                    cargoData.setOrgName(Func.checkStringNull(data.getFnsList().get(i).getOrgName()));
                                    cargoData.setOrgOwnerName(Func.checkStringNull(data.getFnsList().get(i).getOrgOwnerName()));
                                    cargoData.setVehicleAllocationBy(Func.checkStringNull(data.getFnsList().get(i).getVehicleAllocationBy()));
                                    cargoData.setMultiOrderroute(Func.checkStringNull(data.getFnsList().get(i).getMultiOrderroute()));
                                    cargoData.setVehicleInfo(Func.checkStringNull(data.getFnsList().get(i).getVehicleInfo()));
                                    cargoData.setRoute(Func.checkStringNull(data.getFnsList().get(i).getRoute()));
                                    cargoData.setMultiOrder(Func.checkStringNull(data.getFnsList().get(i).getMultiOrder()));
                                    cargoData.setUserOid(Func.checkStringNull(data.getFnsList().get(i).getUserOid()));
                                    cargoData.setChargeAmount(Func.checkStringNull(data.getFnsList().get(i).getChargeAmount()));
                                    cargoData.setTurnStatus(Func.checkStringNull(data.getFnsList().get(i).getTurnStatus()));
                                    cargoData.setTransOrderCount(Func.checkStringNull(data.getFnsList().get(i).getTransOrderCount()));
                                    cargoData.setTransOrder(Func.checkStringNull(data.getFnsList().get(i).getTransOrder()));
                                    cargoData.setPickupTypeName(Func.checkStringNull(data.getFnsList().get(i).getPickupTypeName()));
                                    cargoData.setTransTypeName(Func.checkStringNull(data.getFnsList().get(i).getTransTypeName()));
                                    cargoData.setVehicleTypeName(Func.checkStringNull(data.getFnsList().get(i).getVehicleTypeName()));
                                    cargoData.setOrderTypeName(Func.checkStringNull(data.getFnsList().get(i).getOrderTypeName()));
                                    cargoData.setPayPriceTypeName(Func.checkStringNull(data.getFnsList().get(i).getPayPriceTypeName()));
                                    cargoData.setLatestPickupTime(Func.checkStringNull(data.getFnsList().get(i).getLatestPickupTime()));
                                    cargoData.setEarlistDeliveryTime(Func.checkStringNull(data.getFnsList().get(i).getEarlistDeliveryTime()));
                                    cargoData.setLatestDeliveryTime(Func.checkStringNull(data.getFnsList().get(i).getLatestDeliveryTime()));
                                    cargoData.setTransOrderID(Func.checkStringNull(data.getFnsList().get(i).getTransOrderID()));
                                    cargoData.setDriverPhone(Func.checkStringNull(data.getFnsList().get(i).getDriverPhone()));
                                    cargoData.setPickupCustomerName(Func.checkStringNull(data.getFnsList().get(i).getPickupCustomerName()));
                                    cargoData.setPickupDepotName(Func.checkStringNull(data.getFnsList().get(i).getPickupDepotName()));
                                    cargoData.setDeliveryDepotName(Func.checkStringNull(data.getFnsList().get(i).getDeliveryDepotName()));
                                    cargoData.setPickupTimeType(Func.checkStringNull(data.getFnsList().get(i).getPickupTimeType()));
                                    cargoData.setDispatcherName(Func.checkStringNull(data.getFnsList().get(i).getDispatcherName()));
                                    cargoData.setDispatcherPhone(Func.checkStringNull(data.getFnsList().get(i).getDispatcherPhone()));
                                    cargoData.setPickupTypeCode(Func.checkStringNull(data.getFnsList().get(i).getPickupTypeCode()));
                                    cargoData.setPickupTime(Func.checkStringNull(data.getFnsList().get(i).getPickupTime()));
                                    cargoData.setTonCode(Func.checkStringNull(data.getFnsList().get(i).getTonCode()));
                                    cargoData.setPickupAreaName(Func.checkStringNull(data.getFnsList().get(i).getPickupAreaName()));
                                    cargoData.setAreaName(Func.checkStringNull(data.getFnsList().get(i).getAreaName()));
                                    cargoData.setDeliveryTimeTypeName(Func.checkStringNull(data.getFnsList().get(i).getDeliveryTimeTypeName()));
                                    cargoData.setOrderDate(Func.checkStringNull(data.getFnsList().get(i).getOrderDate()));
                                    cargoData.setSiteCode(Func.checkStringNull(data.getFnsList().get(i).getSiteCode()));
                                    cargoData.setServiceUrl(Func.checkStringNull(data.getFnsList().get(i).getServiceUrl()));
                                    cargoData.setLinkStatus(Func.checkStringNull(data.getFnsList().get(i).getLinkStatus()));
                                    cargoData.setStatusText(Func.checkStringNull(data.getFnsList().get(i).getStatusText()));
                                    cargoData.setAssignedOrg(Func.checkStringNull(data.getFnsList().get(i).getAssignedOrg()));
                                    cargoData.setAssignedName(Func.checkStringNull(data.getFnsList().get(i).getAssignedName()));
                                    cargoData.setTurnID(Func.checkStringNull(data.getFnsList().get(i).getTurnID()));

                                    mArrFnsList.add(cargoData);
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
            public void onFailure(Call<ResponseCargoData> call, Throwable t) {
                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
            }
        });
    }

    private void onCompleteOrder(String mTurnInfoOid, String mOrgID, String mVehicleAllocationBy) {
        RequestFreightRegisterData requestFreightRegisterData = new RequestFreightRegisterData();
        requestFreightRegisterData.setTurnInfoOid(mTurnInfoOid);
        requestFreightRegisterData.setOrgID(mOrgID);
        requestFreightRegisterData.setVehicleAllocationBy(mVehicleAllocationBy);
        requestFreightRegisterData.setUserID(((CargoInfoActivity)getActivity()).mArrUserList.get(0).getUserID());
        requestFreightRegisterData.setUserPortalKey(((CargoInfoActivity)getActivity()).mArrUserList.get(0).getUserKey());
        requestFreightRegisterData.setUserSiteKey(Func.checkStringNull(PrefsUtil.getValue(((CargoInfoActivity)getActivity()).mServiceCode, "")));

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseCargoData> call = mHttpService.completeAssignedFNSOrder(requestFreightRegisterData);
        call.enqueue(new Callback<ResponseCargoData>() {
            @Override
            public void onResponse(Call<ResponseCargoData> call, Response<ResponseCargoData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseCargoData data = response.body();

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
            public void onFailure(Call<ResponseCargoData> call, Throwable t) {
                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
            }
        });
    }

    private void onCancelOrder(String mTurnInfoOid, String mOrgID, String mVehicleAllocationBy) {
        RequestFreightRegisterData requestFreightRegisterData = new RequestFreightRegisterData();
        requestFreightRegisterData.setTurnInfoOid(mTurnInfoOid);
        requestFreightRegisterData.setOrgID(mOrgID);
        requestFreightRegisterData.setVehicleAllocationBy(mVehicleAllocationBy);
        requestFreightRegisterData.setUserID(((CargoInfoActivity)getActivity()).mArrUserList.get(0).getUserID());
        requestFreightRegisterData.setUserPortalKey(((CargoInfoActivity)getActivity()).mArrUserList.get(0).getUserKey());
        requestFreightRegisterData.setUserSiteKey(Func.checkStringNull(PrefsUtil.getValue(((CargoInfoActivity)getActivity()).mServiceCode, "")));

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseCargoData> call = mHttpService.cancelAssignedFNSOrder(requestFreightRegisterData);
        call.enqueue(new Callback<ResponseCargoData>() {
            @Override
            public void onResponse(Call<ResponseCargoData> call, Response<ResponseCargoData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseCargoData data = response.body();

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
            public void onFailure(Call<ResponseCargoData> call, Throwable t) {
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
                case Define.HANDLER_MESSAGE_SUCCESS_RELOAD:
                    onLoadOrderData();
                    break;
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
                onLoadOrderData();
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

    private void onDatePicker(int year, int month, int day) {
        DatePickerDialog dpd = DatePickerDialog.newInstance(this, year, month, day);
        dpd.setThemeDark(true);
        dpd.vibrate(true);
        dpd.dismissOnPause(true);
        dpd.showYearPickerFirst(false);
        dpd.setYearRange(year, year);
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

    protected void onListItemClick(AdapterView<?> parent, View v, int position, long id) {
    }

    public class FnsListAdapter extends BaseExpandableListAdapter {
        private ArrayList<CargoData> itemList;
        private ArrayList<CargoData> subList;
        private Context context;
        private int rowResourceId;

        public FnsListAdapter(Context context, int textViewResourceId, ArrayList<CargoData> itemList, ArrayList<CargoData> subList) {
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
            return itemList.get(groupPosition).getTransOrder();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            View v = convertView;

            CargoData item = itemList.get(groupPosition);
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
            return subList.get(groupPosition).getTransOrder();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        // 차일드뷰 각각의 ROW
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            View v = convertView;

            CargoData item = subList.get(groupPosition);
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

    private void onCloseGroup() {
        int groupCount = mFnsListAdapter.getGroupCount();

        for (int i = 0; i < groupCount; i++) {
            mFnsList.collapseGroup(i);
        }
    }

    private void onRefreshLayout() {
        if (mArrFnsList.size() > 0) {
            tvNoData.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    private View SetFnsList(Context context, View v, int rowResourceId, CargoData Data)
    {
        try {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.freightlist_list, null);

            LinearLayout linLayout = (LinearLayout) v.findViewById(R.id.lin_layout);
            TextView tvFreightInfo = (TextView) v.findViewById(R.id.tv_freight_info);
            TextView tvFreightStatus = (TextView) v.findViewById(R.id.tv_freight_status);
            TextView tvFreightPickupTime = (TextView) v.findViewById(R.id.tv_freight_pickup_time);
            TextView tvFreightPrice = (TextView) v.findViewById(R.id.tv_freight_price);
            TextView tvFreightTon = (TextView) v.findViewById(R.id.tv_freight_ton);
            TextView tvFreightPickupArea = (TextView) v.findViewById(R.id.tv_freight_pickup_area);
            TextView tvFreightDeliveryArea = (TextView) v.findViewById(R.id.tv_freight_delivery_area);

            String price;
            price = Func.setPrice(Data.getChargeAmount(), false);
            tvFreightInfo.setText("[" + Data.getAssignedName() + "]");
            tvFreightStatus.setText(Data.getStatusText());

            if (Data.getStatusText().equals("운송중")) {
                linLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.mark_transport));
            } else if (Data.getStatusText().equals("완료")) {
                linLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.mark_complete));
            }

            tvFreightStatus.setVisibility(View.VISIBLE);
            tvFreightPickupTime.setText("상차 : " + Data.getPickupTime());
            tvFreightPickupTime.setVisibility(View.VISIBLE);
            tvFreightPrice.setText(price);
            tvFreightTon.setText(Data.getTonCode());
            tvFreightPickupArea.setText(Data.getPickupAreaName());
            tvFreightDeliveryArea.setText(Data.getAreaName());

        } catch (Exception ex) { }

        return v;
    }

    private View SetFnsSubList(Context context, View v, int rowResourceId, final CargoData Data)
    {
        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.cargoallocationlist_sublist, null);

        TextView tvDeliveryCustomerName = (TextView) v.findViewById(R.id.tv_delivery_customer_name);
        TextView tvDeliveryCustomerPhone = (TextView) v.findViewById(R.id.tv_delivery_customer_phone);
        TextView tvRoute = (TextView) v.findViewById(R.id.tv_route);
        TextView tvPickupType = (TextView) v.findViewById(R.id.tv_pickup_type);
        TextView tvDeliveryType = (TextView) v.findViewById(R.id.tv_delivery_type);
        TextView tvItemKindType = (TextView) v.findViewById(R.id.tv_item_kind_type);
        TextView tvVehicleType = (TextView) v.findViewById(R.id.tv_vehicle_type);
        TextView tvCustomerOrderType = (TextView) v.findViewById(R.id.tv_customer_order_type);
        TextView tvPaymentType = (TextView) v.findViewById(R.id.tv_payment_type);
        TextView tvRemark = (TextView) v.findViewById(R.id.tv_remark);
        TextView tv_time = (TextView) v.findViewById(R.id.tv_time);
        TextView tvCommunityName = (TextView) v.findViewById(R.id.tv_community_name);
        TextView tvTransID = (TextView) v.findViewById(R.id.tv_trans_id);
        Button btnComplete = (Button) v.findViewById(R.id.btn_complete);
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCompleteOrderData(Data.getTurnInfoOid(), Data.getOrgID(), Data.getVehicleAllocationBy());
            }
        });

        Button btnCancel = (Button) v.findViewById(R.id.btn_cancel);
        btnCancel.setVisibility(View.GONE);
        /*btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelOrderData(Data.getTurnInfoOid(), Data.getOrgID(), Data.getVehicleAllocationBy());
            }
        });*/

        if (Data.getTurnStatus().equals("6")) {
            btnComplete.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }

        if (!Data.getDispatcherName().equals("")) {
            tvDeliveryCustomerName.setVisibility(View.VISIBLE);
            tvDeliveryCustomerName.setText(Data.getDispatcherName());
        } else {
            tvDeliveryCustomerName.setVisibility(View.GONE);
        }

        if (!Func.checkStringNull(Data.getDispatcherPhone()).equals("")) {
            tvDeliveryCustomerPhone.setVisibility(View.VISIBLE);
            tvDeliveryCustomerPhone.setText(PhoneNumberUtils.formatNumber(Data.getDispatcherPhone()));
            tvDeliveryCustomerPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri= Uri.parse("tel:" + Data.getDispatcherPhone());
                    Intent i = new Intent(Intent.ACTION_CALL, uri);
                    startActivity(i);
                }
            });
        } else {
            tvDeliveryCustomerPhone.setVisibility(View.GONE);
        }

        if (!Data.getRoute().equals("")) {
            tvRoute.setVisibility(View.VISIBLE);
            tvRoute.setText(Data.getRoute() + " \n오더 : " + Data.getTransOrderID());
        } else {
            tvRoute.setVisibility(View.GONE);
        }

        String pickupTypeName = "";
        String pickupTime = "";
        String loadingMethodName = "";
        String unLoadingMethodName = "";
        String deliveryTime = "";
        if (Data.getVehicleAllocationBy().equals("2")) {
            if (!Data.getPickupTypeName().equals("") || !Data.getLoadingMethodName().equals("")) {

                if (!Data.getPickupTypeName().equals("")) {
                    pickupTypeName = Data.getPickupTypeName();
                }

                if (Data.getPickupTypeCode().equals("20") || Data.getPickupTypeCode().equals("30") || Data.getPickupTypeCode().equals("70")) {
                    pickupTime = " (" + Data.getPickupTime() + ")";
                }

                if (!Data.getPickupTypeName().equals("") && !Data.getLoadingMethodName().equals("")) {
                    loadingMethodName = " / " + Data.getLoadingMethodName();
                }

                tvPickupType.setText(pickupTypeName + pickupTime + loadingMethodName);
                tvPickupType.setVisibility(View.VISIBLE);

            }

            if (Data.getDeliveryTime().length() > 4) {
                deliveryTime = " (" + Data.getDeliveryTime().substring(0, 5) + ")";
            }

            if (!Data.getUnloadingMethodName().equals("")) {
                unLoadingMethodName = " / " + Data.getUnloadingMethodName();
            }

            tvDeliveryType.setText(Data.getDeliveryTimeTypeName() + deliveryTime + unLoadingMethodName);
            tvDeliveryType.setVisibility(View.VISIBLE);

            if (!Data.getItemInfo().equals("") || !Data.getWeight().equals("") || !Data.getTransTypeName().equals("")) {
                String itemKindName = Data.getItemInfo().equals("") ? "" : Data.getItemInfo();
                String weight = Data.getWeight().equals("") ? "" : " (" + Data.getWeight() + " 톤)";
                String deliveryTypeName = Data.getDeliveryTypeName().equals("") ? "" : " / " + Data.getDeliveryTypeName();
                tvItemKindType.setText(itemKindName + weight + deliveryTypeName);
                tvItemKindType.setVisibility(View.VISIBLE);
            }

            if (!Data.getWidthText().equals("") || !Data.getLengthText().equals("") || !Data.getHeightText().equals("")) {
                String widthText = Func.checkStringNull(Data.getWidthText()).equals("") ? "" : " / 폭 " + Data.getWidthText() + ",";
                String lengthText = Func.checkStringNull(Data.getLengthText()).equals("") ? "" : " 길이 " + Data.getLengthText() + ",";
                String heightText = Func.checkStringNull(Data.getHeightText()).equals("") ? "" : " 높이 " + Data.getHeightText();
                tvVehicleType.setText(Data.getVehicleTypeName() + widthText + lengthText + heightText);
                tvVehicleType.setVisibility(View.VISIBLE);
            }

            if (!Data.getOrderTypeName().equals("")) {
                tvCustomerOrderType.setText(Data.getOrderTypeName());
                tvCustomerOrderType.setVisibility(View.VISIBLE);
            }

            if (!Data.getPayPriceTypeName().equals("")) {
                tvPaymentType.setText(Data.getPayPriceTypeName());
                tvPaymentType.setVisibility(View.VISIBLE);
            }

            if (!Data.getRemark().equals("")) {
                tvRemark.setText("비고 : " + Data.getRemark());
                tvRemark.setVisibility(View.VISIBLE);
            }

            if (!Data.getTimeSpanText().equals("")) {
                tv_time.setText(Data.getTimeSpanText());
                tv_time.setVisibility(View.VISIBLE);
            }

            tvCommunityName.setText("게시 : " + Data.getOrgOwnerName() + " - " + Data.getOrgName());
            tvCommunityName.setVisibility(View.VISIBLE);

            if (!Data.getTransDate().equals("")) {
                tvTransID.setText("등록 : " + Data.getTransDate());
                tvTransID.setVisibility(View.VISIBLE);
            }

        } else {

            if (!Data.getPickupCustomerName().equals("")) {
                tvPickupType.setText("상차 : [" + Data.getPickupCustomerName() + "] " + Data.getPickupTime() );
                tvPickupType.setVisibility(View.VISIBLE);
            } else if (!Data.getPickupDepotName().equals("")) {
                tvPickupType.setText("상차 : [" + Data.getPickupDepotName() + "] " + Data.getPickupTime() );
                tvPickupType.setVisibility(View.VISIBLE);
            } else {
                tvPickupType.setText("상차 : [" + Data.getIlPickupArea() + "] " + Data.getPickupTime() );
                tvPickupType.setVisibility(View.VISIBLE);
            }

            if (!Data.getDeliveryCustomerName().equals("")) {
                tvPickupType.setText("상차 : [" + Data.getDeliveryCustomerName() + "] " + Data.getDeliveryTime() );
                tvPickupType.setVisibility(View.VISIBLE);
            } else if (!Data.getDeliveryDepotName().equals("")) {
                tvPickupType.setText("상차 : [" + Data.getDeliveryDepotName() + "] " + Data.getDeliveryTime() );
                tvPickupType.setVisibility(View.VISIBLE);
            } else {
                tvPickupType.setText("상차 : [" + Data.getIlArea() + "] " + Data.getDeliveryTime() );
                tvPickupType.setVisibility(View.VISIBLE);
            }

            tvRoute.setText(Data.getAddress());
            tvRoute.setVisibility(View.VISIBLE);
            tvItemKindType.setText(Data.getItemInfo());
            tvItemKindType.setVisibility(View.VISIBLE);
            tvVehicleType.setText(Data.getVehicleInfo());
            tvVehicleType.setVisibility(View.VISIBLE);
        }

        return v;
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