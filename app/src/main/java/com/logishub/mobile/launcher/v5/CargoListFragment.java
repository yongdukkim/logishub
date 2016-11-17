package com.logishub.mobile.launcher.v5;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.logishub.mobile.launcher.v5.DATA.DefineListData;
import com.logishub.mobile.launcher.v5.DATA.RequestDefineData;
import com.logishub.mobile.launcher.v5.DATA.RequestFreightRegisterData;
import com.logishub.mobile.launcher.v5.DATA.ResponseCargoData;
import com.logishub.mobile.launcher.v5.DATA.ResponseDefineData;
import com.logishub.mobile.launcher.v5.DATA.ServiceData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CargoListFragment extends Fragment implements View.OnClickListener {
    public View mView;
    private CustomProgressDialog mProgressDialog;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;
    private String mErrorMessage;
    private EditText mTonCode;
    private AlertDialog.Builder mAlertDialogBuilder;
    private String[] mTonCodes;
    private String[] mTonNames;
    private boolean[] mTonSelected;
    private List<String> ItemsIntoList;
    private ArrayList<String> mSendTonCode;
    private Spinner mSpnRefresh;
    private CountDownTimer countDownTimer;
    private TextView mTimeText;
    private TextView mTimeEmptyText;
    private DefineListData mDefineListData;
    private ArrayList<String> mArrTonCode;
    private ArrayList<String> mArrTonName;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<CargoData> mArrFnsList = null;
    private ArrayList<ServiceData> mArrServiceList = null;
    private FnsListAdapter mFnsListAdapter = null;
    private ExpandableListView mFnsList;
    private TextView tvNoData;
    private String mFreightOwnerPhone;

    public CargoListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_cargo_list, container, false);

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
        mTonCode = null;
        mAlertDialogBuilder = null;
        mTonCodes = null;
        mTonNames = null;
        mTonSelected = null;
        ItemsIntoList = null;
        mSendTonCode = null;
        mSpnRefresh = null;
        mTimeText = null;
        mTimeEmptyText = null;
        mDefineListData = null;
        mArrTonCode = null;
        mArrTonName = null;
        mSwipeRefreshLayout = null;
        mFnsList = null;
        tvNoData = null;
        mFreightOwnerPhone = null;

        try {
            countDownTimer.cancel();
        }
        catch (Exception e) {

        }
        countDownTimer = null;
    }

    private void setLayout(View v) {
        mArrTonCode = new ArrayList<>();
        mArrTonName = new ArrayList<>();
        mSendTonCode = new ArrayList<>();
        mArrFnsList = new ArrayList<CargoData>();
        mArrServiceList = new ArrayList<ServiceData>();
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
        mTonCode = (EditText) v.findViewById(R.id.et_ton_code);
        v.findViewById(R.id.et_ton_code).setOnClickListener(this);

        mSpnRefresh = (Spinner) v.findViewById(R.id.sp_search_time_interval);
        mSpnRefresh.setOnItemSelectedListener(SpnRefresh);

        v.findViewById(R.id.btn_freight_info).setOnClickListener(this);

        mTimeText = (TextView) v.findViewById(R.id.tv_time);
        mTimeEmptyText = (TextView) v.findViewById(R.id.tv_time_empty);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onLoadOrderData();
            }
        });

        onLoadDefineData();
    }

    private void onLoadDefineData() {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    onDefineData();
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
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

    private void onApplyOrder(final String mOrgID, final String mTurnInfoOid) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    onApply(mOrgID, mTurnInfoOid);
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void onDefineData() {
        ArrayList<DefineListData> defineLists = new ArrayList<>();
        RequestDefineData requestDefineData = new RequestDefineData();
        requestDefineData.setId(((CargoInfoActivity)getActivity()).mArrUserList.get(0).getUserID());
        requestDefineData.setUserSiteKey(PrefsUtil.getValue(((CargoInfoActivity)getActivity()).mServiceCode, ""));
        requestDefineData.setIsCommunity("Y");

        mDefineListData = new DefineListData();
        mDefineListData.setType(Define.DEFINE_VEHICLE_TON_CODE_TYPE);
        defineLists.add(mDefineListData);
        requestDefineData.setDefineTypeList(defineLists);

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseDefineData> call = mHttpService.getDefineData(requestDefineData);
        call.enqueue(new Callback<ResponseDefineData>() {
            @Override
            public void onResponse(Call<ResponseDefineData> call, Response<ResponseDefineData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseDefineData data = response.body();

                        if (data.getErrorCode().equals("0")) {

                            int mDefineSize = data.getDefineList().size();
                            if (mDefineSize > 0) {
                                mArrTonCode.clear();
                                mArrTonName.clear();

                                for (int i = 0; i < mDefineSize; i++) {
                                    mArrTonCode.add(data.getDefineList().get(i).getType());
                                    mArrTonName.add(data.getDefineList().get(i).getValue());
                                }

                                mTonCodes = mArrTonCode.toArray(new String[mArrTonCode.size()]);
                                mTonNames = mArrTonName.toArray(new String[mArrTonName.size()]);

                                mTonSelected = new boolean[mDefineSize];
                                Arrays.fill(mTonSelected, Boolean.FALSE);
                            }

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
            public void onFailure(Call<ResponseDefineData> call, Throwable t) {
                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
            }
        });
    }

    private void onOrderData() {
        String mTonNames = "";

        for (int i = 0; i < mSendTonCode.size(); i++) {
            if (i == mSendTonCode.size() - 1) {
                mTonNames += Func.checkStringNull(mSendTonCode.get(i));
            } else {
                mTonNames += Func.checkStringNull(mSendTonCode.get(i)) + ",";
            }
        }

        RequestFreightRegisterData requestFreightRegisterData = new RequestFreightRegisterData();
        requestFreightRegisterData.setOrgOid("");
        requestFreightRegisterData.setTonCode(mTonNames);
        requestFreightRegisterData.setSiteCode(((CargoInfoActivity)getActivity()).mServiceCode);
        requestFreightRegisterData.setUserID(((CargoInfoActivity)getActivity()).mArrUserList.get(0).getUserID());
        requestFreightRegisterData.setUserPortalKey(((CargoInfoActivity)getActivity()).mArrUserList.get(0).getUserKey());
        requestFreightRegisterData.setUserSiteKey(Func.checkStringNull(PrefsUtil.getValue(((CargoInfoActivity)getActivity()).mServiceCode, "")));

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseCargoData> call = mHttpService.getFNSOrderB(requestFreightRegisterData);
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

                                    mArrServiceList.clear();

                                    int mServiceSize = data.getFnsList().get(i).getOrgList().size();
                                    if (mServiceSize > 0) {
                                        for (int j = 0; j < mServiceSize; j++) {
                                            ServiceData serviceData = new ServiceData();
                                            serviceData.setServiceOid(Func.checkStringNull(data.getFnsList().get(i).getOrgList().get(j).getServiceOid()));
                                            serviceData.setServiceName(Func.checkStringNull(data.getFnsList().get(i).getOrgList().get(j).getServiceName()));

                                            mArrServiceList.add(serviceData);

                                            cargoData.setOrgList(mArrServiceList);
                                        }
                                    }

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

    private void onApply(String mOrgID, String mTurnInfoOid) {
        RequestFreightRegisterData requestFreightRegisterData = new RequestFreightRegisterData();
        requestFreightRegisterData.setTurnInfoOid(mTurnInfoOid);
        requestFreightRegisterData.setOrgID(mOrgID);
        requestFreightRegisterData.setUserID(((CargoInfoActivity)getActivity()).mArrUserList.get(0).getUserID());
        requestFreightRegisterData.setUserPortalKey(((CargoInfoActivity)getActivity()).mArrUserList.get(0).getUserKey());
        requestFreightRegisterData.setUserSiteKey(Func.checkStringNull(PrefsUtil.getValue(((CargoInfoActivity)getActivity()).mServiceCode, "")));

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseCargoData> call = mHttpService.requestFNSOrderB(requestFreightRegisterData);
        call.enqueue(new Callback<ResponseCargoData>() {
            @Override
            public void onResponse(Call<ResponseCargoData> call, Response<ResponseCargoData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseCargoData data = response.body();

                        if (data.getErrorCode().equals("0")) {
                            mFreightOwnerPhone = Func.checkStringNull(data.getFnsList().get(0).getFreightOwnerPhone());
                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS_RELOAD_CALL);
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

                case Define.HANDLER_MESSAGE_SUCCESS_RELOAD_CALL:
                    onApplyCompleteCall();
                    onLoadOrderData();
                    break;
                case Define.HANDLER_MESSAGE_SUCCESS:
                    int time = Func.replaceString(mSpnRefresh.getSelectedItem().toString());
                    if (time > 0) {
                        countDownTimer(time);
                        countDownTimer.start();
                    }
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
            case R.id.et_ton_code:
                onSetTon();
                break;

            case R.id.btn_freight_info:
                onLoadOrderData();
                break;
        }
    }

    private void onSetTon() {
        try {
            mAlertDialogBuilder = new AlertDialog.Builder(getActivity());
            ItemsIntoList = Arrays.asList(mTonCodes);
            mAlertDialogBuilder.setMultiChoiceItems(mTonNames, mTonSelected, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                }
            });

            mAlertDialogBuilder.setCancelable(false);
            mAlertDialogBuilder.setTitle("톤수 설정");
            mAlertDialogBuilder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mSendTonCode.clear();
                    int a = 0;
                    while(a < mTonSelected.length)
                    {
                        boolean value = mTonSelected[a];

                        if(value){
                            mSendTonCode.add(ItemsIntoList.get(a));
                        }
                        a++;
                    }
                }
            });

            mAlertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = mAlertDialogBuilder.create();
            dialog.show();
        } catch (Exception ex) { }
    }

    private AdapterView.OnItemSelectedListener SpnRefresh = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            try {
                countDownTimer.cancel();
            } catch (Exception ex) { }

            try {
                if (position == 0) {
                    mTimeText.setText("");
                    mTimeEmptyText.setText("");
                } else {
                    mTimeEmptyText.setText("초");
                    int time = Func.replaceString(mSpnRefresh.getSelectedItem().toString());
                    if (time > 0) {
                        countDownTimer(time);
                        countDownTimer.start();
                    }
                }

            } catch (Exception ex) { }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

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
            TextView tvFreightPrice = (TextView) v.findViewById(R.id.tv_freight_price);
            TextView tvFreightTon = (TextView) v.findViewById(R.id.tv_freight_ton);
            TextView tvFreightPickupArea = (TextView) v.findViewById(R.id.tv_freight_pickup_area);
            TextView tvFreightDeliveryArea = (TextView) v.findViewById(R.id.tv_freight_delivery_area);

            String price;
            price = Func.setPrice(Data.getChargeAmount(), true);
            String pickupTypeTime;
            pickupTypeTime = (Data.getPickupTypeCode().equals("20") || Data.getPickupTypeCode().equals("30")) ? "당일" : Data.getPickupTypeName();

            if (pickupTypeTime.equals("긴급")) {
                linLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.mark_emergency));
            }  else if (pickupTypeTime.equals("예약")) {
                linLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.mark_reservation));
            }

            tvFreightInfo.setText(pickupTypeTime + " / 상차요청시간 : " + Data.getPickupTime());
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
        v = vi.inflate(R.layout.cargolist_sublist, null);

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
        final Spinner spnCommunity = (Spinner) v.findViewById(R.id.sp_item_community);
        ArrayAdapter<String> mArrCommunity;
        final Vector<String[]> mVectorCommunity;
        mVectorCommunity = new Vector<String[]>();
        List<String>	arrCommunity = new ArrayList<String>();
        mArrCommunity = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrCommunity);
        mArrCommunity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCommunity.setAdapter(mArrCommunity);
        Button btnApply = (Button) v.findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] sVal = mVectorCommunity.get(spnCommunity.getSelectedItemPosition());

                if (sVal[0].equals("")) {
                    spnCommunity.setFocusableInTouchMode(true);
                    spnCommunity.requestFocus();
                    new CustomAlertDialog(getActivity(), "알림" + "\n\n커뮤니티를 선택해 주세요.");
                    return;
                }

                onApplyOrder(sVal[0], Data.getTurnInfoOid());
            }
        });

        if (!Data.getDispatcherName().equals("")) {
            tvDeliveryCustomerName.setVisibility(View.VISIBLE);
            tvDeliveryCustomerName.setText(Data.getDispatcherName());
        } else {
            tvDeliveryCustomerName.setVisibility(View.GONE);
        }

        if (!Data.getDispatcherPhone().equals("")) {
            tvDeliveryCustomerPhone.setVisibility(View.VISIBLE);
            tvDeliveryCustomerPhone.setText(PhoneNumberUtils.formatNumber(Data.getDispatcherPhone()));
            tvDeliveryCustomerPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callPhone(Data.getDispatcherPhone());
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

            int orgSize = Data.getOrgList().size();
            if (orgSize > 0) {

                String[] communityEmpty = new String[2];
                communityEmpty[0] = "";
                communityEmpty[1] = "";

                for (int i = 0; i < orgSize; i++) {
                    String[] sCommunityInfo = new String[2];
                    sCommunityInfo[0] = Data.getOrgList().get(i).getServiceOid();
                    sCommunityInfo[1] = Data.getOrgList().get(i).getServiceName();

                    if (i == 0) {
                        mArrCommunity.add("선택");
                        mVectorCommunity.add(communityEmpty);
                    }

                    mArrCommunity.add(Data.getOrgList().get(i).getServiceName());
                    mVectorCommunity.add(sCommunityInfo);
                }
            }

            tvCommunityName.setText("게시 : " + Data.getOrgOwnerName() + " - " + Data.getOrgName());
            tvCommunityName.setVisibility(View.VISIBLE);

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

    private void onApplyCompleteCall() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callPhone(mFreightOwnerPhone);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setCancelable(false);
        alert.setTitle("배차신청 완료");
        alert.setMessage("배차신청이 완료 되었습니다.\n화주와 통화 하시겠습니까?");
        alert.show();
    }

    private void callPhone(String mphone) {
        if (!mphone.equals("")) {
            Uri uri= Uri.parse("tel:" + mphone);
            Intent i = new Intent(Intent.ACTION_CALL, uri);
            startActivity(i);
        }
    }

    public void countDownTimer(final int mTime) {
        countDownTimer = new CountDownTimer(mTime * Define.SMS_RECEIVE_COUNT_DOWN_INTERVAL, Define.SMS_RECEIVE_COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                mTimeText.setText(String.valueOf(millisUntilFinished / Define.SMS_RECEIVE_COUNT_DOWN_INTERVAL));
            }
            public void onFinish() {
                countDownTimer.cancel();
                onLoadOrderData();
            }
        };
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