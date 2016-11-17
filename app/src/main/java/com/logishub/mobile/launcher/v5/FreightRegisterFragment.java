package com.logishub.mobile.launcher.v5;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.logishub.mobile.launcher.v5.Common.CustomAlertDialog;
import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.Common.HttpHelper;
import com.logishub.mobile.launcher.v5.Common.HttpService;
import com.logishub.mobile.launcher.v5.Common.PrefsUtil;
import com.logishub.mobile.launcher.v5.DATA.DefineListData;
import com.logishub.mobile.launcher.v5.DATA.FnsData;
import com.logishub.mobile.launcher.v5.DATA.FnsTemplateData;
import com.logishub.mobile.launcher.v5.DATA.RequestDefineData;
import com.logishub.mobile.launcher.v5.DATA.RequestFreightRegisterData;
import com.logishub.mobile.launcher.v5.DATA.ResponseDefineData;
import com.logishub.mobile.launcher.v5.DATA.ResponseFreightRegisterData;
import com.logishub.mobile.launcher.v5.DATA.UserData;
import com.logishub.mobile.launcher.v5.DB.FnsOrderAdapter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreightRegisterFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public View mView;
    private AlertDialog.Builder mAlertDialogBuilder;
    private String[] mCommunityCodes;
    private String[] mCommunityNames;
    private List<String> ItemsIntoList;
    private boolean[] mCommunitySelected;
    private TableLayout mTlContents;
    private TextView mMenuOpenText;
    private boolean mMenuOpen;
    public static ArrayList<UserData> mArrUserList = null;
    private CustomProgressDialog mProgressDialog;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;
    private String mErrorMessage;
    private DefineListData mDefineListData;

    private ArrayAdapter<String> mArrStartArea = null;
    private ArrayAdapter<String> mArrEndArea = null;
    private ArrayAdapter<String> mArrPickupTime = null;
    private ArrayAdapter<String> mArrDeliveryTime = null;
    private ArrayAdapter<String> mArrVehicleTonCode = null;
    private ArrayAdapter<String> mArrVehicleContainerType = null;
    private ArrayAdapter<String> mArrLoadingMethod = null;
    private ArrayAdapter<String> mArrUnLoadingMethod = null;
    private ArrayAdapter<String> mArrDeliveryType = null;
    private ArrayAdapter<String> mArrPaymentType = null;
    private ArrayAdapter<String> mArrItemKind = null;
    private ArrayAdapter<String> mArrCustomOrderType = null;

    private Vector<String[]> mVectorStartArea = null;
    private Vector<String[]> mVectorEndArea = null;
    private Vector<String[]> mVectorPickupTime = null;
    private Vector<String[]> mVectorDeliveryTime = null;
    private Vector<String[]> mVectorVehicleTonCode = null;
    private Vector<String[]> mVectorVehicleContainerType = null;
    private Vector<String[]> mVectorLoadingMethod = null;
    private Vector<String[]> mVectorUnLoadingMethod = null;
    private Vector<String[]> mVectorDeliveryType = null;
    private Vector<String[]> mVectorPaymentType = null;
    private Vector<String[]> mVectorItemKind = null;
    private Vector<String[]> mVectorCustomOrderType = null;

    private Spinner mStartArea;
    private Spinner mEndArea;
    private Spinner mPickupTime;
    private Spinner mDeliveryTime;
    private Spinner mVehicleTonCode;
    private Spinner mVehicleContainerType;
    private Spinner mLoadingMethod;
    private Spinner mUnLoadingMethod;
    private Spinner mDeliveryType;
    private Spinner mPaymentType;
    private Spinner mItemKind;
    private Spinner mCustomOrderType;
    private Spinner mItemWidthType;
    private Spinner mItemHeightType;
    private Spinner mItemLengthType;

    private EditText mPrice;
    private EditText mCompanyName;
    private EditText mCompanyContact;
    private EditText mStartAreaAddress;
    private EditText mCompanyEtc;
    private EditText mItemWeight;
    private EditText mItemWidth;
    private EditText mItemHeight;
    private EditText mItemLength;

    private String mSendStartArea;
    private String mSendEndArea;
    private String mSendPickupTime;
    private String mSendDeliveryTime;
    private String mSendVehicleTonCode;
    private String mSendVehicleContainerType;
    private String mSendLoadingMethod;
    private String mSendLoadingMethodName;
    private String mSendUnLoadingMethod;
    private String mSendUnLoadingMethodName;
    private String mSendDeliveryType;
    private String mSendDeliveryTypeName;
    private String mSendPaymentType;
    private String mSendPaymentTypeName;
    private String mSendItemKind;
    private String mSendItemKindName;
    private String mSendCustomOrderType;
    private String mSendCustomOrderTypeName;
    private ArrayList<String> mSendCommunityCode;
    private ArrayList<String> mArrCommunityCode;
    private ArrayList<String> mArrCommunityName;

    private Button mBtnPickupDate;
    private Button mBtnPickupTime;
    private Button mBtnDeliveryDate;
    private Button mBtnRegister;

    private String mDateField;
    private ArrayList<FnsData> mArrFnsData = null;
    private String mModifyType;

    private FnsOrderAdapter mTemplateOrderDb = null;
    private FnsTemplateData mTemplateOrderData = null;

    private ArrayList<FnsTemplateData> mFnsTemplateDatas;

    public FreightRegisterFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_freight_register, container, false);

        onOpenDB();

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
        mAlertDialogBuilder  = null;
        mCommunityCodes  = null;
        mCommunityNames  = null;
        ItemsIntoList = null;
        mCommunitySelected = null;
        mTlContents = null;
        mMenuOpenText = null;
        mArrStartArea = null;
        mArrEndArea = null;
        mVectorStartArea = null;
        mVectorEndArea = null;
        mStartArea = null;
        mEndArea = null;
        mPickupTime = null;
        mDeliveryTime = null;
        mVehicleTonCode = null;
        mVehicleContainerType = null;
        mLoadingMethod = null;
        mUnLoadingMethod = null;
        mDeliveryType = null;
        mPaymentType = null;
        mItemKind = null;
        mCustomOrderType = null;
        mArrUserList = null;
        mProgressDialog = null;
        mHttpHelper = null;
        mHttpService = null;
        mErrorMessage = null;
        mDefineListData = null;
        mArrPickupTime = null;
        mArrDeliveryTime = null;
        mArrVehicleTonCode = null;
        mArrVehicleContainerType = null;
        mArrLoadingMethod = null;
        mArrUnLoadingMethod = null;
        mArrDeliveryType = null;
        mArrPaymentType = null;
        mArrItemKind = null;
        mArrCustomOrderType = null;
        mVectorPickupTime = null;
        mVectorDeliveryTime = null;
        mVectorVehicleTonCode = null;
        mVectorVehicleContainerType = null;
        mVectorLoadingMethod = null;
        mVectorUnLoadingMethod = null;
        mVectorDeliveryType = null;
        mVectorPaymentType = null;
        mVectorItemKind = null;
        mVectorCustomOrderType = null;
        mSendStartArea = null;
        mSendEndArea = null;
        mSendPickupTime = null;
        mSendDeliveryTime = null;
        mSendVehicleTonCode = null;
        mSendVehicleContainerType = null;
        mSendLoadingMethod = null;
        mSendLoadingMethodName = null;
        mSendUnLoadingMethod = null;
        mSendUnLoadingMethodName = null;
        mSendDeliveryType = null;
        mSendDeliveryTypeName = null;
        mSendPaymentType = null;
        mSendPaymentTypeName = null;
        mSendItemKind = null;
        mSendItemKindName = null;
        mSendCustomOrderType = null;
        mSendCustomOrderTypeName = null;
        mSendCommunityCode = null;
        mArrCommunityCode = null;
        mArrCommunityName = null;
        mBtnPickupDate = null;
        mBtnPickupTime = null;
        mBtnDeliveryDate = null;
        mDateField = null;
        mPrice = null;
        mCompanyName = null;
        mCompanyContact = null;
        mStartAreaAddress = null;
        mCompanyEtc = null;
        mItemWeight = null;
        mItemWidth = null;
        mItemHeight = null;
        mItemLength = null;
        mArrFnsData = null;

        if (mTemplateOrderDb != null)
        {
            mTemplateOrderDb.close();
            mTemplateOrderDb = null;
        }

        mTemplateOrderData = null;
        mFnsTemplateDatas = null;
    }

    private void setLayout(View v) {
        mArrUserList = new ArrayList<>();
        mArrCommunityCode = new ArrayList<>();
        mArrCommunityName = new ArrayList<>();
        mSendCommunityCode = new ArrayList<>();
        mFnsTemplateDatas = new ArrayList<>();
        mArrFnsData = new ArrayList<>();
        mTlContents = (TableLayout) v.findViewById(R.id.tablelayout2);
        mMenuOpenText = (TextView) v.findViewById(R.id.tv_menu_open);
        mMenuOpen = false;
        mTlContents.setVisibility(View.GONE);
        mMenuOpenText.setText(R.string.freightinfo_open);
        mModifyType = Define.SCREEN_MODE_REGISTER;

        v.findViewById(R.id.btn_service).setOnClickListener(this);
        v.findViewById(R.id.fl_freightinfo_open).setOnClickListener(this);
        v.findViewById(R.id.btn_register_freightinfo).setOnClickListener(this);
        v.findViewById(R.id.btn_price_add_10).setOnClickListener(this);
        v.findViewById(R.id.btn_price_add_1).setOnClickListener(this);
        v.findViewById(R.id.btn_price_clear).setOnClickListener(this);
        v.findViewById(R.id.btn_pickup_date).setOnClickListener(this);
        v.findViewById(R.id.btn_pickup_time).setOnClickListener(this);
        v.findViewById(R.id.btn_delivery_date).setOnClickListener(this);
        v.findViewById(R.id.fab_templete).setOnClickListener(this);

        mBtnRegister = (Button) v.findViewById(R.id.btn_register_freightinfo);

        mBtnPickupDate = (Button) v.findViewById(R.id.btn_pickup_date);
        mBtnPickupDate.setText(Func.nowDate(0, 0));
        mBtnPickupTime = (Button) v.findViewById(R.id.btn_pickup_time);
        mBtnPickupTime.setText(Func.nowTime(0));
        mBtnDeliveryDate = (Button) v.findViewById(R.id.btn_delivery_date);
        mBtnDeliveryDate.setText(Func.nowDate(0, 0));

        mStartArea = (Spinner) v.findViewById(R.id.sp_start_area);
        mEndArea = (Spinner) v.findViewById(R.id.sp_end_area);
        mPickupTime = (Spinner) v.findViewById(R.id.sp_pickup_time_type);
        mDeliveryTime = (Spinner) v.findViewById(R.id.sp_delivery_time_type);
        mVehicleTonCode = (Spinner) v.findViewById(R.id.sp_vehicle_ton_code_type);
        mVehicleContainerType = (Spinner) v.findViewById(R.id.sp_vehicle_container_type);
        mLoadingMethod = (Spinner) v.findViewById(R.id.sp_loading_method_type);
        mUnLoadingMethod = (Spinner) v.findViewById(R.id.sp_unloading_method_type);
        mDeliveryType = (Spinner) v.findViewById(R.id.sp_delivery_type);
        mPaymentType = (Spinner) v.findViewById(R.id.sp_payment_type);
        mItemKind = (Spinner) v.findViewById(R.id.sp_item_kind_type);
        mCustomOrderType = (Spinner) v.findViewById(R.id.sp_custom_order_type);

        mPrice = (EditText) v.findViewById(R.id.et_price);
        mCompanyName = (EditText) v.findViewById(R.id.et_company_name);
        mCompanyContact = (EditText) v.findViewById(R.id.et_company_contact);
        mStartAreaAddress = (EditText) v.findViewById(R.id.et_start_area_address);
        mCompanyEtc = (EditText) v.findViewById(R.id.et_company_etc);
        mItemWeight = (EditText) v.findViewById(R.id.et_item_weight);
        mItemWidth = (EditText) v.findViewById(R.id.et_item_width);
        mItemHeight = (EditText) v.findViewById(R.id.et_item_height);
        mItemLength = (EditText) v.findViewById(R.id.et_item_length);

        mItemWidthType = (Spinner) v.findViewById(R.id.sp_item_width_type);
        mItemHeightType = (Spinner) v.findViewById(R.id.sp_item_height_type);
        mItemLengthType = (Spinner) v.findViewById(R.id.sp_item_length_type);

        mStartArea.setOnItemSelectedListener(SpnStartArea);
        mEndArea.setOnItemSelectedListener(SpnEndArea);
        mPickupTime.setOnItemSelectedListener(SpnPickupTime);
        mDeliveryTime.setOnItemSelectedListener(SpnDeliveryTime);
        mVehicleTonCode.setOnItemSelectedListener(SpnVehicleTonCode);
        mVehicleContainerType.setOnItemSelectedListener(SpnVehicleContainerType);
        mLoadingMethod.setOnItemSelectedListener(SpnLoadingMethod);
        mUnLoadingMethod.setOnItemSelectedListener(SpnUnLoadingMethod);
        mDeliveryType.setOnItemSelectedListener(SpnDeliveryType);
        mPaymentType.setOnItemSelectedListener(SpnPaymentType);
        mItemKind.setOnItemSelectedListener(SpnItemKind);
        mCustomOrderType.setOnItemSelectedListener(SpnCustomOrderType);

        List<String>	arrStartArea = new ArrayList<>();
        List<String>	arrEndArea = new ArrayList<>();
        List<String>	arrPickupTime = new ArrayList<>();
        List<String>	arrDeliveryTime = new ArrayList<>();
        List<String>	arrVehicleTonCode = new ArrayList<>();
        List<String>	arrVehicleContainerType = new ArrayList<>();
        List<String>	arrLoadingMethod = new ArrayList<>();
        List<String>	arrUnLoadingMethod = new ArrayList<>();
        List<String>	arrDeliveryType = new ArrayList<>();
        List<String>	arrPaymentType = new ArrayList<>();
        List<String>	arrItemKind = new ArrayList<>();
        List<String>	arrCustomOrderType = new ArrayList<>();

        mVectorStartArea = new Vector<>();
        mVectorEndArea = new Vector<>();
        mVectorPickupTime = new Vector<>();
        mVectorDeliveryTime = new Vector<>();
        mVectorVehicleTonCode = new Vector<>();
        mVectorVehicleContainerType = new Vector<>();
        mVectorLoadingMethod = new Vector<>();
        mVectorUnLoadingMethod = new Vector<>();
        mVectorDeliveryType = new Vector<>();
        mVectorPaymentType = new Vector<>();
        mVectorItemKind = new Vector<>();
        mVectorCustomOrderType = new Vector<>();

        mArrStartArea = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrStartArea);
        mArrEndArea = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrEndArea);
        mArrPickupTime = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrPickupTime);
        mArrDeliveryTime = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrDeliveryTime);
        mArrVehicleTonCode = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrVehicleTonCode);
        mArrVehicleContainerType = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrVehicleContainerType);
        mArrLoadingMethod = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrLoadingMethod);
        mArrUnLoadingMethod = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrUnLoadingMethod);
        mArrDeliveryType = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrDeliveryType);
        mArrPaymentType = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrPaymentType);
        mArrItemKind = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrItemKind);
        mArrCustomOrderType = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrCustomOrderType);

        mArrStartArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mArrEndArea.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mArrPickupTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mArrDeliveryTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mArrVehicleTonCode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mArrVehicleContainerType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mArrLoadingMethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mArrUnLoadingMethod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mArrDeliveryType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mArrPaymentType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mArrItemKind.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mArrCustomOrderType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mStartArea.setAdapter(mArrStartArea);
        mEndArea.setAdapter(mArrEndArea);
        mPickupTime.setAdapter(mArrPickupTime);
        mDeliveryTime.setAdapter(mArrDeliveryTime);
        mVehicleTonCode.setAdapter(mArrVehicleTonCode);
        mVehicleContainerType.setAdapter(mArrVehicleContainerType);
        mLoadingMethod.setAdapter(mArrLoadingMethod);
        mUnLoadingMethod.setAdapter(mArrUnLoadingMethod);
        mDeliveryType.setAdapter(mArrDeliveryType);
        mPaymentType.setAdapter(mArrPaymentType);
        mItemKind.setAdapter(mArrItemKind);
        mCustomOrderType.setAdapter(mArrCustomOrderType);

        onBindAreaData();

        mArrUserList = ((FreightInfoActivity)getActivity()).mArrUserList;

        onLoadData("DATA");

        onSetRegisterButton();
    }

    /** SQL Lite Open & Data Init */
    private void onOpenDB() {

        try {
            mTemplateOrderDb = new FnsOrderAdapter(getActivity());
            mTemplateOrderDb.open();

        } catch (Exception e) { }
    }

    private void onSetRegisterButton() {
        if (mModifyType.equals(Define.SCREEN_MODE_REGISTER)) {
            mBtnRegister.setText(R.string.action_register_freightinfo);
        } else {
            mBtnRegister.setText(R.string.modify);
        }
    }

    private void onLoadData(final String mLoadingType) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    if (mLoadingType.equals("DATA")) {
                        onDefineData();
                    } else if (mLoadingType.equals("ORDER")) {
                        onRegisterOrder();
                    }

                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void onDefineData() {
        ArrayList<DefineListData> defineLists = new ArrayList<>();
        RequestDefineData requestDefineData = new RequestDefineData();
        requestDefineData.setId(mArrUserList.get(0).getUserID());
        requestDefineData.setUserSiteKey(PrefsUtil.getValue(((FreightInfoActivity)getActivity()).mServiceCode, ""));
        requestDefineData.setIsCommunity("Y");

        mDefineListData = new DefineListData();
        mDefineListData.setType(Define.DEFINE_PICKUP_TIME_TYPE);
        defineLists.add(mDefineListData);
        requestDefineData.setDefineTypeList(defineLists);

        mDefineListData = new DefineListData();
        mDefineListData.setType(Define.DEFINE_DELIVERY_TIME_TYPE);
        defineLists.add(mDefineListData);
        requestDefineData.setDefineTypeList(defineLists);

        mDefineListData = new DefineListData();
        mDefineListData.setType(Define.DEFINE_VEHICLE_TON_CODE_TYPE);
        defineLists.add(mDefineListData);
        requestDefineData.setDefineTypeList(defineLists);

        mDefineListData = new DefineListData();
        mDefineListData.setType(Define.DEFINE_VEHICLE_CONTAINER_TYPE);
        defineLists.add(mDefineListData);
        requestDefineData.setDefineTypeList(defineLists);

        mDefineListData = new DefineListData();
        mDefineListData.setType(Define.DEFINE_PAYMENT_TYPE);
        defineLists.add(mDefineListData);
        requestDefineData.setDefineTypeList(defineLists);

        mDefineListData = new DefineListData();
        mDefineListData.setType(Define.DEFINE_LOADING_METHOD_TYPE);
        defineLists.add(mDefineListData);
        requestDefineData.setDefineTypeList(defineLists);

        mDefineListData = new DefineListData();
        mDefineListData.setType(Define.DEFINE_UNLOADING_METHOD_TYPE);
        defineLists.add(mDefineListData);
        requestDefineData.setDefineTypeList(defineLists);

        mDefineListData = new DefineListData();
        mDefineListData.setType(Define.DEFINE_DELIVERY_TYPE);
        defineLists.add(mDefineListData);
        requestDefineData.setDefineTypeList(defineLists);

        mDefineListData = new DefineListData();
        mDefineListData.setType(Define.DEFINE_ITEM_KIND_TYPE);
        defineLists.add(mDefineListData);
        requestDefineData.setDefineTypeList(defineLists);

        mDefineListData = new DefineListData();
        mDefineListData.setType(Define.DEFINE_CUSTOMER_ORDER_TYPE);
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
                                for (int i = 0; i < mDefineSize; i++) {

                                    String[] defineInfo = new String[3];
                                    defineInfo[0] = data.getDefineList().get(i).getType();
                                    defineInfo[1] = data.getDefineList().get(i).getCode();
                                    defineInfo[2] = data.getDefineList().get(i).getValue();

                                    switch (defineInfo[0]) {
                                        case Define.DEFINE_PICKUP_TIME_TYPE:
                                            onDefineDataAdd(Define.DEFINE_PICKUP_TIME_TYPE, defineInfo[2], defineInfo);
                                            break;

                                        case Define.DEFINE_DELIVERY_TIME_TYPE:
                                            onDefineDataAdd(Define.DEFINE_DELIVERY_TIME_TYPE, defineInfo[2], defineInfo);
                                            break;

                                        case Define.DEFINE_VEHICLE_TON_CODE_TYPE:
                                            onDefineDataAdd(Define.DEFINE_VEHICLE_TON_CODE_TYPE, defineInfo[2], defineInfo);
                                            break;

                                        case Define.DEFINE_VEHICLE_CONTAINER_TYPE:
                                            onDefineDataAdd(Define.DEFINE_VEHICLE_CONTAINER_TYPE, defineInfo[2], defineInfo);
                                            break;

                                        case Define.DEFINE_LOADING_METHOD_TYPE:
                                            onDefineDataAdd(Define.DEFINE_LOADING_METHOD_TYPE, defineInfo[2], defineInfo);
                                            break;

                                        case Define.DEFINE_UNLOADING_METHOD_TYPE:
                                            onDefineDataAdd(Define.DEFINE_UNLOADING_METHOD_TYPE, defineInfo[2], defineInfo);
                                            break;

                                        case Define.DEFINE_DELIVERY_TYPE:
                                            onDefineDataAdd(Define.DEFINE_DELIVERY_TYPE, defineInfo[2], defineInfo);
                                            break;

                                        case Define.DEFINE_PAYMENT_TYPE:
                                            onDefineDataAdd(Define.DEFINE_PAYMENT_TYPE, defineInfo[2], defineInfo);
                                            break;

                                        case Define.DEFINE_ITEM_KIND_TYPE:
                                            onDefineDataAdd(Define.DEFINE_ITEM_KIND_TYPE, defineInfo[2], defineInfo);
                                            break;

                                        case Define.DEFINE_CUSTOMER_ORDER_TYPE:
                                            onDefineDataAdd(Define.DEFINE_CUSTOMER_ORDER_TYPE, defineInfo[2], defineInfo);
                                            break;
                                    }
                                }
                            }

                            mArrCommunityCode.clear();
                            mArrCommunityName.clear();
                            int mCommunitySize = data.getCommunityList().size();
                            if (mCommunitySize > 0) {
                                for (int i = 0; i < mCommunitySize; i++) {
                                    mArrCommunityCode.add(data.getCommunityList().get(i).getOid());
                                    mArrCommunityName.add(data.getCommunityList().get(i).getCommunityName());
                                }
                            }

                            mCommunityCodes = mArrCommunityCode.toArray(new String[mArrCommunityCode.size()]);
                            mCommunityNames = mArrCommunityName.toArray(new String[mArrCommunityName.size()]);

                            mCommunitySelected = new boolean[mCommunitySize];
                            Arrays.fill(mCommunitySelected, Boolean.FALSE);

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
            public void onFailure(Call<ResponseDefineData> call, Throwable t) {
                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_service:
                onSetService();
                break;
            case R.id.fl_freightinfo_open:
                if (mMenuOpen) {
                    mTlContents.setVisibility(View.GONE);
                    mMenuOpenText.setText(R.string.freightinfo_open);
                    mMenuOpen = false;
                } else {
                    mTlContents.setVisibility(View.VISIBLE);
                    mMenuOpenText.setText(R.string.freightinfo_close);
                    mMenuOpen = true;
                }
                break;
            case R.id.btn_register_freightinfo:
                onDataCheck();
                break;

            case R.id.btn_price_add_10:
                onPriceControl(0);
                break;

            case R.id.btn_price_add_1:
                onPriceControl(1);
                break;

            case R.id.btn_price_clear:
                onPriceControl(2);
                break;

            case R.id.btn_pickup_date:
                mDateField = "PICKUP";
                try {
                    int year = Integer.parseInt(mBtnPickupDate.getText().toString().split("-")[0]);
                    int month = Integer.parseInt(mBtnPickupDate.getText().toString().split("-")[1]);
                    int day = Integer.parseInt(mBtnPickupDate.getText().toString().split("-")[2]);
                    onDatePicker(year, month - 1, day);
                } catch (Exception ex) {}
                break;

            case R.id.btn_pickup_time:
                try {
                    int hour = Integer.parseInt(mBtnPickupTime.getText().toString().split(":")[0]);
                    int minute = Integer.parseInt(mBtnPickupTime.getText().toString().split(":")[1]);
                    onTimePicker(hour, minute);
                } catch (Exception ex) { }

                break;

            case R.id.btn_delivery_date:
                mDateField = "DELIVERY";
                try {
                    int year = Integer.parseInt(mBtnDeliveryDate.getText().toString().split("-")[0]);
                    int month = Integer.parseInt(mBtnDeliveryDate.getText().toString().split("-")[1]);
                    int day = Integer.parseInt(mBtnDeliveryDate.getText().toString().split("-")[2]);
                    onDatePicker(year, month - 1, day);
                } catch (Exception ex) {}
                break;

            case R.id.fab_templete:
                onLoadTemplate();
                break;
        }
    }

    private void onLoadTemplate() {
        int mFetchTemplateCnt = mTemplateOrderDb.FetchTemplateCnt(((FreightInfoActivity)getActivity()).mServiceCode);

        if (mFetchTemplateCnt < 1) {
            new CustomAlertDialog(getActivity(), "알림" + "\n\n저장된 템플릿이 없습니다.");
            return;
        }
        else
        {
            try {
                final String [] mIndex;
                String [] mAddress;

                ArrayList<String> mArrAddressID = new ArrayList<>();
                ArrayList<String> mArrAddressName = new ArrayList<>();

                mFnsTemplateDatas = mTemplateOrderDb.GetFnsTemplateData(((FreightInfoActivity)getActivity()).mServiceCode);

                for (int i = 0; i < mFnsTemplateDatas.size(); i++) {
                    mArrAddressID.add(mFnsTemplateDatas.get(i).getRowOid());
                    mArrAddressName.add(mFnsTemplateDatas.get(i).getPickupAreaName() + " -> " + mFnsTemplateDatas.get(i).getAreaName());
                }

                mIndex = mArrAddressID.toArray(new String[mArrAddressID.size()]);
                mAddress = mArrAddressName.toArray(new String[mArrAddressName.size()]);

                mAlertDialogBuilder = new AlertDialog.Builder(getActivity());
                mAlertDialogBuilder.setItems(mAddress, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onSetTemplate(mIndex[which]);
                    }
                });

                mAlertDialogBuilder.setCancelable(false);
                mAlertDialogBuilder.setTitle("템플릿");
                mAlertDialogBuilder.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                    }
                });

                AlertDialog dialog = mAlertDialogBuilder.create();
                dialog.show();
            } catch (Exception ex) { }
        }
    }

    private void onSetTemplate(String mRowOid) {
        try {
            mTemplateOrderData = mTemplateOrderDb.GetTemplateData(mRowOid);

            FnsData fnsData = new FnsData();
            fnsData.setId(mTemplateOrderData.getID());
            fnsData.setTransId(mTemplateOrderData.getTransID());
            fnsData.setOid(mTemplateOrderData.getOid());
            fnsData.setTurnOid(mTemplateOrderData.getTurnOid());
            fnsData.setDriverUsreOid(mTemplateOrderData.getDriverUserOid());
            fnsData.setStatus(mTemplateOrderData.getStatus());
            fnsData.setOrderDate(mTemplateOrderData.getOrderDate());
            fnsData.setEarlistPickupTime(mTemplateOrderData.getEarlistPickupTime());
            fnsData.setTransDate(mTemplateOrderData.getTransDate());
            fnsData.setTimeSpan(mTemplateOrderData.getTimeSpan());
            fnsData.setTimeSpanText(mTemplateOrderData.getTimeSpanText());
            fnsData.setIlDeliveryCustomer(mTemplateOrderData.getIlDeliveryCustomer());
            fnsData.setDeliveryCustomerName(mTemplateOrderData.getDeliveryCustomerName());
            fnsData.setDeliveryCustomerPhone(mTemplateOrderData.getDeliveryCustomerPhone());
            fnsData.setIlPickupArea(mTemplateOrderData.getIlPickupArea());
            fnsData.setPickupAreaName(mTemplateOrderData.getPickupAreaName());
            fnsData.setPickupTypeCode(mTemplateOrderData.getPickupTypeCode());
            fnsData.setPickupTypeName(mTemplateOrderData.getPickupTypeName());
            fnsData.setPickupTime(mTemplateOrderData.getPickupTime());
            fnsData.setDeliveryTime(mTemplateOrderData.getDeliveryTime());
            fnsData.setIlArea(mTemplateOrderData.getIlArea());
            fnsData.setAreaName(mTemplateOrderData.getAreaName());
            fnsData.setAddress(mTemplateOrderData.getAddress());
            fnsData.setDetailAddress(mTemplateOrderData.getDetailAddress());
            fnsData.setDeliveryTimeTypeCode(mTemplateOrderData.getDeliveryTimeTypeCode());
            fnsData.setDeliveryTimeTypeName(mTemplateOrderData.getDeliveryTimeTypeName());
            fnsData.setTonCode(mTemplateOrderData.getTonCode());
            fnsData.setTonName(mTemplateOrderData.getTonName());
            fnsData.setContainerTypeCode(mTemplateOrderData.getContainerTypeCode());
            fnsData.setContainerTypeName(mTemplateOrderData.getContainerTypeName());
            fnsData.setPrice(mTemplateOrderData.getPrice());
            fnsData.setPaymentType(mTemplateOrderData.getPaymentType());
            fnsData.setPaymentTypeName(mTemplateOrderData.getPaymentTypeName());
            fnsData.setItemKindCode(mTemplateOrderData.getItemKindCode());
            fnsData.setItemKindName(mTemplateOrderData.getItemKindName());
            fnsData.setVehicleID(mTemplateOrderData.getVehicleID());
            fnsData.setLoadingMethod(mTemplateOrderData.getLoadingMethod());
            fnsData.setLoadingMethodName(mTemplateOrderData.getLoadingMethodName());
            fnsData.setUnloadingMethod(mTemplateOrderData.getUnloadingMethod());
            fnsData.setUnloadingMethodName(mTemplateOrderData.getUnloadingMethodName());
            fnsData.setDeliveryType(mTemplateOrderData.getDeliveryType());
            fnsData.setDeliveryTypeName(mTemplateOrderData.getDeliveryTypeName());
            fnsData.setWidthText(mTemplateOrderData.getWidthText());
            fnsData.setLengthText(mTemplateOrderData.getLengthText());
            fnsData.setHeightText(mTemplateOrderData.getHeightText());
            fnsData.setWeight(mTemplateOrderData.getWeight());
            fnsData.setCustomOrderType(mTemplateOrderData.getCustomOrderType());
            fnsData.setCustomOrderTypeName(mTemplateOrderData.getCustomOrderTypeName());
            fnsData.setRemark(mTemplateOrderData.getRemark());
            fnsData.setRequestedPickupDate(mTemplateOrderData.getRequestedPickupDate());
            fnsData.setRequestedDeliveryDate(mTemplateOrderData.getRequestedDeliveryDate());
            fnsData.setOrderTime(mTemplateOrderData.getOrderTime());
            fnsData.setDriverName(mTemplateOrderData.getDriverName());
            fnsData.setDriverMobilePhone(mTemplateOrderData.getDriverMobilePhone());
            fnsData.setStatusName(mTemplateOrderData.getStatusName());
            fnsData.setCharged(mTemplateOrderData.getCharged());
            fnsData.setChargedText(mTemplateOrderData.getChargedText());
            fnsData.setUserName(mTemplateOrderData.getUserName());
            fnsData.setUserMobilePhone(mTemplateOrderData.getUserMobilePhone());
            fnsData.setOwnerName(mTemplateOrderData.getOwnerName());
            fnsData.setOwnerPhone(mTemplateOrderData.getOwnerPhone());
            fnsData.setVehicleNo(mTemplateOrderData.getVehicleNo());
            onSetFreightData(fnsData, Define.SCREEN_MODE_REGISTER);
        } catch (Exception ex) { }
    }

    private void onDataCheck() {
        if (!Func.isValidString(mSendStartArea) || mSendStartArea.equals("0")) {
            mStartArea.setFocusableInTouchMode(true);
            mStartArea.requestFocus();
            new CustomAlertDialog(getActivity(), "알림" + "\n\n상차지를 선택해 주세요.");
            return;
        }

        if (!Func.isValidString(mSendEndArea) || mSendEndArea.equals("0")) {
            mEndArea.setFocusableInTouchMode(true);
            mEndArea.requestFocus();
            new CustomAlertDialog(getActivity(), "알림" + "\n\n하차지를 선택해 주세요.");
            return;
        }

        if (!Func.isValidString(mSendDeliveryTime) || mSendDeliveryTime.equals("0")) {
            mDeliveryTime.setFocusableInTouchMode(true);
            mDeliveryTime.requestFocus();
            new CustomAlertDialog(getActivity(), "알림" + "\n\n하차시간을 선택해 주세요.");
            return;
        }

        if (!Func.isValidString(mSendVehicleTonCode) || mSendVehicleTonCode.equals("0")) {
            mVehicleTonCode.setFocusableInTouchMode(true);
            mVehicleTonCode.requestFocus();
            new CustomAlertDialog(getActivity(), "알림" + "\n\n차종을 선택해 주세요.");
            return;
        }

        if (!Func.isValidString(mSendVehicleContainerType) || mSendVehicleContainerType.equals("0")) {
            mVehicleContainerType.setFocusableInTouchMode(true);
            mVehicleContainerType.requestFocus();
            new CustomAlertDialog(getActivity(), "알림" + "\n\n특장을 선택해 주세요.");
            return;
        }

        if (mSendCommunityCode.size() < 1) {
            new CustomAlertDialog(getActivity(), "알림" + "\n\n게시대상 커뮤니티를 선택해 주세요.");
            return;
        }

        onLoadData("ORDER");
    }

    private void onRegisterOrder() {
        String orgList = "";

        for (int i = 0; i < mSendCommunityCode.size(); i++) {
            if (i == mSendCommunityCode.size() - 1) {
                orgList += Func.checkStringNull(mSendCommunityCode.get(i));
            } else {
                orgList += Func.checkStringNull(mSendCommunityCode.get(i)) + ",";
            }
        }

        RequestFreightRegisterData requestFreightRegisterData = new RequestFreightRegisterData();

        if (mModifyType.equals(Define.SCREEN_MODE_MODIFY)) {
            requestFreightRegisterData.setIlTransOrder(mArrFnsData.get(0).getOid());
        } else {
            requestFreightRegisterData.setIlTransOrder("");
        }

        requestFreightRegisterData.setSiteCode(((FreightInfoActivity)getActivity()).mServiceCode);
        requestFreightRegisterData.setIlPickupArea(Func.checkStringNull(mSendStartArea));
        requestFreightRegisterData.setPickupAreaName(Func.checkStringNull(mStartArea.getSelectedItem().toString()).replace(" ", "/"));
        requestFreightRegisterData.setIlArea(Func.checkStringNull(mSendEndArea));
        requestFreightRegisterData.setAreaName(Func.checkStringNull(mEndArea.getSelectedItem().toString().replace(" ", "/")));
        requestFreightRegisterData.setPickupTimeType(Func.checkStringNull(mSendPickupTime));
        requestFreightRegisterData.setPickupTypeName(Func.checkStringNull(mPickupTime.getSelectedItem().toString()));
        requestFreightRegisterData.setPickupTime(mBtnPickupDate.getText().toString() + " " + mBtnPickupTime.getText().toString());
        requestFreightRegisterData.setDeliveryTimeType(Func.checkStringNull(mSendDeliveryTime));
        requestFreightRegisterData.setDeliveryTimeTypeName(Func.checkStringNull(mDeliveryTime.getSelectedItem().toString()));
        requestFreightRegisterData.setDeliveryTime(mBtnDeliveryDate.getText().toString());
        requestFreightRegisterData.setTonCode(Func.checkStringNull(mSendVehicleTonCode));
        requestFreightRegisterData.setTonCodeName(Func.checkStringNull(mVehicleTonCode.getSelectedItem().toString()));
        requestFreightRegisterData.setContainerType(Func.checkStringNull(mSendVehicleContainerType));
        requestFreightRegisterData.setContainerTypeName(Func.checkStringNull(mVehicleContainerType.getSelectedItem().toString()));
        requestFreightRegisterData.setPrice(Func.checkStringNull(mPrice.getText().toString()));
        requestFreightRegisterData.setOrganizationOidList(Func.checkStringNull(orgList));
        requestFreightRegisterData.setOwnerName(Func.checkStringNull(mCompanyName.getText().toString()));
        requestFreightRegisterData.setPhone(Func.checkStringNull(mCompanyContact.getText().toString()));
        requestFreightRegisterData.setDetailAddress(Func.checkStringNull(mStartAreaAddress.getText().toString()));
        requestFreightRegisterData.setRemark(Func.checkStringNull(mCompanyEtc.getText().toString()));
        requestFreightRegisterData.setLoadingMethod(Func.checkStringNull(mSendLoadingMethod));
        requestFreightRegisterData.setLoadingMethodName(Func.checkStringNull(mSendLoadingMethodName));
        requestFreightRegisterData.setUnloadingMethod(Func.checkStringNull(mSendUnLoadingMethod));
        requestFreightRegisterData.setUnloadingMethodName(Func.checkStringNull(mSendUnLoadingMethodName));
        requestFreightRegisterData.setDeliveryType(Func.checkStringNull(mSendDeliveryType));
        requestFreightRegisterData.setDeliveryTypeName(Func.checkStringNull(mSendDeliveryTypeName));
        requestFreightRegisterData.setPaymentType(Func.checkStringNull(mSendPaymentType));
        requestFreightRegisterData.setPaymentTypeName(Func.checkStringNull(mSendPaymentTypeName));
        requestFreightRegisterData.setItemKindCode(Func.checkStringNull(mSendItemKind));
        requestFreightRegisterData.setItemKind(Func.checkStringNull(mSendItemKindName));
        requestFreightRegisterData.setWeight(Func.checkStringNull(mItemWeight.getText().toString()));

        if (!Func.checkStringNull(mItemWidth.getText().toString()).equals("")) {
            requestFreightRegisterData.setWidthText(Func.checkStringNull(mItemWidth.getText().toString() + " " + mItemWidthType.getSelectedItem().toString()));
        } else {
            requestFreightRegisterData.setWidthText("");
        }

        if (!Func.checkStringNull(mItemHeight.getText().toString()).equals("")) {
            requestFreightRegisterData.setHeightText(Func.checkStringNull(mItemHeight.getText().toString() + " " + mItemHeightType.getSelectedItem().toString()));
        } else {
            requestFreightRegisterData.setHeightText("");
        }

        if (!Func.checkStringNull(mItemLength.getText().toString()).equals("")) {
            requestFreightRegisterData.setLengthText(Func.checkStringNull(mItemLength.getText().toString() + " " + mItemLengthType.getSelectedItem().toString()));
        } else {
            requestFreightRegisterData.setLengthText("");
        }

        requestFreightRegisterData.setCustomOrderType(Func.checkStringNull(mSendCustomOrderType));
        requestFreightRegisterData.setCustomOrderTypeName(Func.checkStringNull(mSendCustomOrderTypeName));
        requestFreightRegisterData.setUserSiteKey(Func.checkStringNull(PrefsUtil.getValue(((FreightInfoActivity)getActivity()).mServiceCode, "")));
        requestFreightRegisterData.setUserPortalKey(mArrUserList.get(0).getUserKey());
        requestFreightRegisterData.setServiceUrl(((FreightInfoActivity)getActivity()).mServiceURL);
        requestFreightRegisterData.setUserID(mArrUserList.get(0).getUserID());

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseFreightRegisterData> call = mHttpService.registerFNSOrder(requestFreightRegisterData);
        call.enqueue(new Callback<ResponseFreightRegisterData>() {
            @Override
            public void onResponse(Call<ResponseFreightRegisterData> call, Response<ResponseFreightRegisterData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseFreightRegisterData data = response.body();

                        if (data.getErrorCode().equals("0")) {
                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ORDER_REGISTER_SUCCESS);
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
            public void onFailure(Call<ResponseFreightRegisterData> call, Throwable t) {
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
                case Define.HANDLER_MESSAGE_SITE_SESSION_KEY_SUCCESS:
                    onDefineData();
                    break;
                case Define.HANDLER_MESSAGE_SUCCESS:
                    break;
                case Define.HANDLER_MESSAGE_ORDER_REGISTER_SUCCESS:
                    mArrFnsData.clear();
                    mModifyType = Define.SCREEN_MODE_REGISTER;
                    ((FreightInfoActivity)getActivity()).viewPager.getAdapter().notifyDataSetChanged();
                    onViewClear();
                    ((FreightInfoActivity)getActivity()).viewPager.setCurrentItem(1);
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

            return true;
        }
    });

    private void onViewClear() {
        mStartArea.setSelection(0);
        mEndArea.setSelection(0);
        mVehicleTonCode.setSelection(0);
        mVehicleContainerType.setSelection(0);
        mPrice.setText("");
        mCompanyName.setText("");
        mCompanyContact.setText("");
        mStartAreaAddress.setText("");
        mCompanyEtc.setText("");
        mLoadingMethod.setSelection(0);
        mUnLoadingMethod.setSelection(0);
        mDeliveryType.setSelection(0);
        mPaymentType.setSelection(0);
        mItemKind.setSelection(0);
        mItemWeight.setText("");
        mItemWidth.setText("");
        mItemWidthType.setSelection(0);
        mItemHeight.setText("");
        mItemHeightType.setSelection(0);
        mItemLength.setText("");
        mItemLengthType.setSelection(0);
        mCustomOrderType.setSelection(0);
    }

    private void onPriceControl(int priceControlType) {
        try {
            int price = Integer.parseInt(Func.checkEditTextString(mPrice.getText().toString()));

            if (priceControlType == 0) {
                price = price + 10;
                mPrice.setText(String.valueOf(price));
            } else if (priceControlType == 1) {
                price = price + 1;
                mPrice.setText(String.valueOf(price));
            } else {
                mPrice.setText("");
            }
        } catch (Exception ex) { }
    }

    private void onSetService() {
        try {
            mAlertDialogBuilder = new AlertDialog.Builder(getActivity());
            ItemsIntoList = Arrays.asList(mCommunityCodes);
            mAlertDialogBuilder.setMultiChoiceItems(mCommunityNames, mCommunitySelected, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                }
            });

            mAlertDialogBuilder.setCancelable(false);
            mAlertDialogBuilder.setTitle("게시대상");
            mAlertDialogBuilder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mSendCommunityCode.clear();
                    int a = 0;
                    while(a < mCommunitySelected.length)
                    {
                        boolean value = mCommunitySelected[a];

                        if(value){
                            mSendCommunityCode.add(ItemsIntoList.get(a));
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

    private void onBindAreaData()
    {
        String strArea;
        InputStream OpenFile;
        Resources res = getResources();
        OpenFile = res.openRawResource(R.raw.skynet_dev_area);

        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(OpenFile));

            while( (strArea = in.readLine()) != null)
            {
                String[] sAreaInfo = new String[2];

                sAreaInfo[0] = strArea.split(",")[0];
                sAreaInfo[1] = strArea.split(",")[1];

                onDefineDataAdd("", sAreaInfo[1], sAreaInfo);
            }

            in.close();
            OpenFile.close();

        }
        catch(IOException e) { }
    }

    private void onDefineDataAdd(String mKey, String sAdapterData, String[] arrVectorData)
    {
        String[] defineEmpty = new String[3];
        defineEmpty[0] = "";
        defineEmpty[1] = "";
        defineEmpty[2] = "";

        switch (mKey) {
            case Define.DEFINE_PICKUP_TIME_TYPE:
                if (mArrPickupTime.getCount() < 1) {
                    mArrPickupTime.add("선택");
                    mVectorPickupTime.add(defineEmpty);
                }
                mArrPickupTime.add(sAdapterData);
                mVectorPickupTime.add(arrVectorData);
                break;

            case Define.DEFINE_DELIVERY_TIME_TYPE:
                if (mArrDeliveryTime.getCount() < 1) {
                    mArrDeliveryTime.add("선택");
                    mVectorDeliveryTime.add(defineEmpty);
                }
                mArrDeliveryTime.add(sAdapterData);
                mVectorDeliveryTime.add(arrVectorData);
                break;

            case Define.DEFINE_VEHICLE_TON_CODE_TYPE:
                if (mArrVehicleTonCode.getCount() < 1) {
                    mArrVehicleTonCode.add("선택");
                    mVectorVehicleTonCode.add(defineEmpty);
                }
                mArrVehicleTonCode.add(sAdapterData);
                mVectorVehicleTonCode.add(arrVectorData);
                break;

            case Define.DEFINE_VEHICLE_CONTAINER_TYPE:
                if (mArrVehicleContainerType.getCount() < 1) {
                    mArrVehicleContainerType.add("선택");
                    mVectorVehicleContainerType.add(defineEmpty);
                }
                mArrVehicleContainerType.add(sAdapterData);
                mVectorVehicleContainerType.add(arrVectorData);
                break;

            case Define.DEFINE_LOADING_METHOD_TYPE:
                if (mArrLoadingMethod.getCount() < 1) {
                    mArrLoadingMethod.add("미정");
                    mVectorLoadingMethod.add(defineEmpty);
                }
                mArrLoadingMethod.add(sAdapterData);
                mVectorLoadingMethod.add(arrVectorData);
                break;

            case Define.DEFINE_UNLOADING_METHOD_TYPE:
                if (mArrUnLoadingMethod.getCount() < 1) {
                    mArrUnLoadingMethod.add("미정");
                    mVectorUnLoadingMethod.add(defineEmpty);
                }
                mArrUnLoadingMethod.add(sAdapterData);
                mVectorUnLoadingMethod.add(arrVectorData);
                break;

            case Define.DEFINE_DELIVERY_TYPE:
                if (mArrDeliveryType.getCount() < 1) {
                    mArrDeliveryType.add("미정");
                    mVectorDeliveryType.add(defineEmpty);
                }
                mArrDeliveryType.add(sAdapterData);
                mVectorDeliveryType.add(arrVectorData);
                break;

            case Define.DEFINE_PAYMENT_TYPE:
                if (mArrPaymentType.getCount() < 1) {
                    mArrPaymentType.add("미정");
                    mVectorPaymentType.add(defineEmpty);
                }
                mArrPaymentType.add(sAdapterData);
                mVectorPaymentType.add(arrVectorData);
                break;

            case Define.DEFINE_ITEM_KIND_TYPE:
                if (mArrItemKind.getCount() < 1) {
                    mArrItemKind.add("미분류");
                    mVectorItemKind.add(defineEmpty);
                }
                mArrItemKind.add(sAdapterData);
                mVectorItemKind.add(arrVectorData);
                break;

            case Define.DEFINE_CUSTOMER_ORDER_TYPE:
                if (mArrCustomOrderType.getCount() < 1) {
                    mArrCustomOrderType.add("선택");
                    mVectorCustomOrderType.add(defineEmpty);
                }
                mArrCustomOrderType.add(sAdapterData);
                mVectorCustomOrderType.add(arrVectorData);
                break;

            default:
                mArrStartArea.add(sAdapterData);
                mArrEndArea.add(sAdapterData);
                mVectorStartArea.add(arrVectorData);
                mVectorEndArea.add(arrVectorData);
                break;
        }
    }

    private void onShowPickupSetDateTime(String txt) {
        if (txt.equals("20") || txt.equals("30")) {
            mBtnPickupDate.setVisibility(View.GONE);
            mBtnPickupTime.setVisibility(View.VISIBLE);
        } else if (txt.equals("70")) {
            mBtnPickupDate.setVisibility(View.VISIBLE);
            mBtnPickupTime.setVisibility(View.VISIBLE);
        } else {
            mBtnPickupDate.setVisibility(View.GONE);
            mBtnPickupTime.setVisibility(View.GONE);
        }
    }

    private void onShowDeliverySetDate(String txt) {
        if (txt.equals("30")) {
            mBtnDeliveryDate.setVisibility(View.VISIBLE);
        } else {
            mBtnDeliveryDate.setVisibility(View.GONE);
        }
    }

    public void onSetFreightData(FnsData data, String mModifyMode) {

        try
        {
            onViewClear();
            mArrFnsData.clear();
            mArrFnsData.add(data);
            ItemsIntoList = Arrays.asList(mCommunityCodes);
            mModifyType = mModifyMode;

            onSetRegisterButton();

            /** 상차지 */
            int startAreaPosition = 0;
            for (int i = 0; i < mVectorStartArea.size(); i++) {
                String[] sVal = mVectorStartArea.get(i);
                if (sVal[1].equals(mArrFnsData.get(0).getPickupAreaName().replace("/", " "))) {
                    startAreaPosition = i;
                }
            }

            mStartArea.setSelection(startAreaPosition);

            /** 하차지 */
            int endAreaPosition = 0;
            for (int i = 0; i < mVectorEndArea.size(); i++) {
                String[] sVal = mVectorEndArea.get(i);
                if (sVal[1].equals(mArrFnsData.get(0).getAreaName().replace("/", " "))) {
                    endAreaPosition = i;
                }
            }

            mEndArea.setSelection(endAreaPosition);

            /** 상차시간 */
            int pickupTypeNamePosition = 0;
            for (int i = 0; i < mVectorPickupTime.size(); i++) {
                String[] sVal = mVectorPickupTime.get(i);
                if (sVal[1].equals(mArrFnsData.get(0).getPickupTypeCode())) {
                    pickupTypeNamePosition = i;
                }
            }

            mPickupTime.setSelection(pickupTypeNamePosition);

            /** 상차예약일시 */
            /*if (!Func.checkStringNull(mArrFnsData.get(0).getRequestedPickupDate()).equals("")) {
                mBtnPickupDate.setText(Func.nowYear(0) + "-" + mArrFnsData.get(0).getRequestedPickupDate().split(" ")[0]);
                mBtnPickupTime.setText(mArrFnsData.get(0).getRequestedPickupDate().split(" ")[1]);
            }*/

            /** 하차시간 */
            int deliveryTypeNamePosition = 0;
            for (int i = 0; i < mVectorDeliveryTime.size(); i++) {
                String[] sVal = mVectorDeliveryTime.get(i);
                if (sVal[1].equals(mArrFnsData.get(0).getDeliveryTimeTypeCode())) {
                    deliveryTypeNamePosition = i;
                }
            }

            mDeliveryTime.setSelection(deliveryTypeNamePosition);

            /**  하차예약일 */
            /*if (!Func.checkStringNull(mArrFnsData.get(0).getRequestedDeliveryDate()).equals("")) {
                mBtnDeliveryDate.setText(Func.nowYear(0) + "-" + mArrFnsData.get(0).getRequestedDeliveryDate());
            }*/

            /**  차종 */
            int tonCodePosition = 0;
            for (int i = 0; i < mVectorVehicleTonCode.size(); i++) {
                String[] sVal = mVectorVehicleTonCode.get(i);
                if (sVal[1].equals(mArrFnsData.get(0).getTonCode())) {
                    tonCodePosition = i;
                }
            }

            mVehicleTonCode.setSelection(tonCodePosition);

            /**  특장 */
            int ContainerCodePosition = 0;
            for (int i = 0; i < mVectorVehicleContainerType.size(); i++) {
                String[] sVal = mVectorVehicleContainerType.get(i);
                if (sVal[1].equals(mArrFnsData.get(0).getContainerTypeCode())) {
                    ContainerCodePosition = i;
                }
            }

            mVehicleContainerType.setSelection(ContainerCodePosition);

            /** 운송료 */
            if (!Func.checkStringNull(mArrFnsData.get(0).getPrice()).equals("")) {
                mPrice.setText(mArrFnsData.get(0).getPrice());
            }

            /** 게시대상 */
            mSendCommunityCode.clear();
            for (int i =0; i < mArrFnsData.get(0).getOrgList().size(); i++) {
                for (int j = 0; j < mCommunityNames.length; j++) {
                    if (mCommunityNames[j].equals(mArrFnsData.get(0).getOrgList().get(i).getServiceName())) {
                        mCommunitySelected[j] = true;
                        mSendCommunityCode.add(ItemsIntoList.get(j));
                    }
                }
            }

            /** 업체명*/
            if (!Func.checkStringNull(mArrFnsData.get(0).getDeliveryCustomerName()).equals("")) {
                mCompanyName.setText(mArrFnsData.get(0).getDeliveryCustomerName());
            }

            /** 업체연락처*/
            if (!Func.checkStringNull(mArrFnsData.get(0).getDeliveryCustomerPhone()).equals("")) {
                mCompanyContact.setText(mArrFnsData.get(0).getDeliveryCustomerPhone());
            }

            /**  상차지 주소*/
            if (!Func.checkStringNull(mArrFnsData.get(0).getDetailAddress()).equals("")) {
                mStartAreaAddress.setText(mArrFnsData.get(0).getDetailAddress());
            }

            /**  비고*/
            if (!Func.checkStringNull(mArrFnsData.get(0).getRemark()).equals("")) {
                mCompanyEtc.setText(mArrFnsData.get(0).getRemark());
            }

            /** 상차방법*/
            int LoadingMethodPosition = 0;
            for (int i = 0; i < mVectorLoadingMethod.size(); i++) {
                String[] sVal = mVectorLoadingMethod.get(i);
                if (sVal[1].equals(mArrFnsData.get(0).getLoadingMethod())) {
                    LoadingMethodPosition = i;
                }
            }

            mLoadingMethod.setSelection(LoadingMethodPosition);

            /** 하차방법*/
            int UnLoadingMethodPosition = 0;
            for (int i = 0; i < mVectorUnLoadingMethod.size(); i++) {
                String[] sVal = mVectorUnLoadingMethod.get(i);
                if (sVal[1].equals(mArrFnsData.get(0).getUnloadingMethod())) {
                    UnLoadingMethodPosition = i;
                }
            }

            mUnLoadingMethod.setSelection(UnLoadingMethodPosition);

            /** 운송*/
            int DeliveryTypePosition = 0;
            for (int i = 0; i < mVectorDeliveryType.size(); i++) {
                String[] sVal = mVectorDeliveryType.get(i);
                if (sVal[1].equals(mArrFnsData.get(0).getDeliveryType())) {
                    DeliveryTypePosition = i;
                }
            }

            mDeliveryType.setSelection(DeliveryTypePosition);

            /** 결제*/
            int PaymentTypePosition = 0;
            for (int i = 0; i < mVectorPaymentType.size(); i++) {
                String[] sVal = mVectorPaymentType.get(i);
                if (sVal[1].equals(mArrFnsData.get(0).getPaymentType())) {
                    PaymentTypePosition = i;
                }
            }

            mPaymentType.setSelection(PaymentTypePosition);

            /** 적재물*/
            int ItemKindTypePosition = 0;
            for (int i = 0; i < mVectorItemKind.size(); i++) {
                String[] sVal = mVectorItemKind.get(i);
                if (sVal[1].equals(mArrFnsData.get(0).getItemKindCode())) {
                    ItemKindTypePosition = i;
                }
            }

            mItemKind.setSelection(ItemKindTypePosition);

            /**  중량*/
            if (!Func.checkStringNull(mArrFnsData.get(0).getWeight()).equals("")) {
                mItemWeight.setText(mArrFnsData.get(0).getWeight());
            }

            /**  제원폭*/
            if (!Func.checkStringNull(mArrFnsData.get(0).getWidthText()).equals("")) {
                mItemWidth.setText(mArrFnsData.get(0).getWidthText().split(" ")[0]);

                int ItemWidthPositon = 0;
                for (int i = 0; i < mItemWidthType.getCount(); i++) {
                    mItemWidthType.setSelection(i);
                    String sVal = mItemWidthType.getSelectedItem().toString();
                    if (sVal.equals(mArrFnsData.get(0).getWidthText().split(" ")[1])) {
                        ItemWidthPositon = i;
                    }
                }

                mItemWidthType.setSelection(ItemWidthPositon);
            }

            /**  제원높이*/
            if (!Func.checkStringNull(mArrFnsData.get(0).getHeightText()).equals("")) {
                mItemHeight.setText(mArrFnsData.get(0).getHeightText().split(" ")[0]);

                int ItemHeightPositon = 0;
                for (int i = 0; i < mItemHeightType.getCount(); i++) {
                    mItemHeightType.setSelection(i);
                    String sVal = mItemHeightType.getSelectedItem().toString();
                    if (sVal.equals(mArrFnsData.get(0).getHeightText().split(" ")[1])) {
                        ItemHeightPositon = i;
                    }
                }

                mItemHeightType.setSelection(ItemHeightPositon);
            }

            /**  제원길이*/
            if (!Func.checkStringNull(mArrFnsData.get(0).getLengthText()).equals("")) {
                mItemLength.setText(mArrFnsData.get(0).getLengthText().split(" ")[0]);

                int ItemLengthPositon = 0;
                for (int i = 0; i < mItemLengthType.getCount(); i++) {
                    mItemLengthType.setSelection(i);
                    String sVal = mItemLengthType.getSelectedItem().toString();
                    if (sVal.equals(mArrFnsData.get(0).getHeightText().split(" ")[1])) {
                        ItemLengthPositon = i;
                    }
                }

                mItemHeightType.setSelection(ItemLengthPositon);
            }

            /**  특수선택*/
            int CustomerOrderTypePositon = 0;
            for (int i = 0; i < mVectorCustomOrderType.size(); i++) {
                String[] sVal = mVectorCustomOrderType.get(i);
                if (sVal[1].equals(mArrFnsData.get(0).getCustomOrderType())) {
                    CustomerOrderTypePositon = i;
                }
            }

            mCustomOrderType.setSelection(CustomerOrderTypePositon);

        } catch (Exception ex) { }

    }

    private AdapterView.OnItemSelectedListener SpnStartArea = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] sStartArea = mVectorStartArea.get(position);
            mSendStartArea = sStartArea[0];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener SpnEndArea = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] sEndArea = mVectorEndArea.get(position);
            mSendEndArea = sEndArea[0];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener SpnPickupTime = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] sPickupTime = mVectorPickupTime.get(position);
            mSendPickupTime = sPickupTime[1];
            onShowPickupSetDateTime(sPickupTime[1]);

            if (mArrFnsData.size() == 0) {
                if (sPickupTime[1].equals("10")) {
                    mBtnPickupTime.setText(Func.nowTime(2));
                } else if (sPickupTime[1].equals("20")) {
                    mBtnPickupTime.setText(Func.nowTime(1));
                } else if (sPickupTime[1].equals("30")) {
                    mBtnPickupTime.setText(Func.nowTime(2));
                } else if (sPickupTime[1].equals("70")) {
                    mBtnPickupTime.setText(Func.nowTime(3));
                } else {
                    mBtnPickupTime.setText(Func.nowTime(0));
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener SpnDeliveryTime = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] sDeliveryTime = mVectorDeliveryTime.get(position);
            mSendDeliveryTime = sDeliveryTime[1];
            onShowDeliverySetDate(sDeliveryTime[1]);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener SpnVehicleTonCode = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] sVehicleTonCode = mVectorVehicleTonCode.get(position);
            mSendVehicleTonCode = sVehicleTonCode[1];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener SpnVehicleContainerType = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] sVehicleContainerType = mVectorVehicleContainerType.get(position);
            mSendVehicleContainerType = sVehicleContainerType[1];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener SpnLoadingMethod = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] sLoadingMethod = mVectorLoadingMethod.get(position);
            mSendLoadingMethod = sLoadingMethod[1];
            mSendLoadingMethodName = sLoadingMethod[2];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener SpnUnLoadingMethod = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] sUnLoadingMethod = mVectorUnLoadingMethod.get(position);
            mSendUnLoadingMethod = sUnLoadingMethod[1];
            mSendUnLoadingMethodName = sUnLoadingMethod[2];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener SpnDeliveryType = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] sDeliveryType = mVectorDeliveryType.get(position);
            mSendDeliveryType = sDeliveryType[1];
            mSendDeliveryTypeName = sDeliveryType[2];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener SpnPaymentType = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] sPaymentType = mVectorPaymentType.get(position);
            mSendPaymentType = sPaymentType[1];
            mSendPaymentTypeName = sPaymentType[2];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener SpnItemKind = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] sItemKind = mVectorItemKind.get(position);
            mSendItemKind = sItemKind[1];
            mSendItemKindName = sItemKind[2];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener SpnCustomOrderType = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String[] sCustomOrderType = mVectorCustomOrderType.get(position);
            mSendCustomOrderType = sCustomOrderType[1];
            mSendCustomOrderTypeName = sCustomOrderType[2];
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String time = hourString + ":" + minuteString;
        mBtnPickupTime.setText(time);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        if (mDateField.equals("PICKUP")) {
            mBtnPickupDate.setText(date);
        } else {
            mBtnDeliveryDate.setText(date);
        }
    }

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

    private void onTimePicker(int hour, int minute) {
        TimePickerDialog tpd = TimePickerDialog.newInstance(this, hour, minute, false);
        tpd.setThemeDark(true);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
        tpd.enableMinutes(true);
        tpd.enableSeconds(false);
        tpd.setAccentColor(Color.parseColor("#04a2bc"));
        tpd.setTitle("시간 설정");
        tpd.setTimeInterval(Define.TIME_PICKER_INTERVAL_HOUR, Define.TIMEPICKER_INTERVAL_MINUTE, Define.TIMEPICKER_INTERVAL_SECONDS);
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });
        tpd.show(getActivity().getFragmentManager(), "Timepicker");
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