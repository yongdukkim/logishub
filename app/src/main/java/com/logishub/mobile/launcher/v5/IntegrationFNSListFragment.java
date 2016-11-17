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
import com.logishub.mobile.launcher.v5.DATA.CommunityListData;
import com.logishub.mobile.launcher.v5.DATA.DefineListData;
import com.logishub.mobile.launcher.v5.DATA.IntegrationFNSListData;
import com.logishub.mobile.launcher.v5.DATA.IntegrationSubFnsData;
import com.logishub.mobile.launcher.v5.DATA.RequestFreightRegisterData;
import com.logishub.mobile.launcher.v5.DATA.RequestIntegrationFNSData;
import com.logishub.mobile.launcher.v5.DATA.ResponseCargoData;
import com.logishub.mobile.launcher.v5.DATA.ResponseIntegrationFNSData;
import com.logishub.mobile.launcher.v5.DATA.ServiceData;
import com.logishub.mobile.launcher.v5.DATA.UserData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by SPICE on 2016-10-26.
 */

public class IntegrationFNSListFragment extends Fragment {

    public static IntegrationFNSListAdapter mIntegrationFNSListAdapter= null;
    private static ArrayList<UserData> mArrUserList = null;
    private static ArrayList<IntegrationFNSListData> mArrIntegrationFNSList = null;
    private static ArrayList<CommunityListData> mArrCommunityList = null;
    private static ArrayList<String> mArrCommunityName = null;
    private static ArrayList<DefineListData> mArrTonCodeList = null;



    public View mView;
    private CustomProgressDialog mProgressDialog;

    private String mErrorCode;
    private String mErrorMessage;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;

    private Spinner mCommunitySpinner;
    private Button mIntegrationFNSSearch;
    private ExpandableListView mIntegrationFNSList;
    private TextView tvNoData;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private DefineListData mDefineListData;


    private ArrayList<String> mArrTonCode;
    private ArrayList<String> mArrTonName;

    private String mOrgOid;
    private EditText mEditTextTonCode;
    private AlertDialog.Builder mAlertDialogBuilder;
    private List<String> ItemsIntoList;
    private boolean[] mTonSelected;
    private String[] mTonCodes;
    private String[] mTonNames;
    private ArrayList<String> mSendTonCode;
    private Spinner mSpnRefresh;
    private CountDownTimer countDownTimer;
    private TextView mTimeText;
    private TextView mTimeEmptyText;

    private CargoListFragment.FnsListAdapter mIntegrationFnsListAdapter = null;
    private ExpandableListView mIntegrationFnsList;
    private String mFreightOwnerPhone;

    private ArrayAdapter<String> mArrAdCommunity = null;
    private ArrayList<ServiceData> mArrServiceList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_integrationfnslist, container, false);

        onLoadData(true);

        setLayout(mView);

        //mIntegrationFNSListAdapter.notifyDataSetChanged();

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
        hideProgressDialog();
    }

    private void setLayout(View v) {

        mArrUserList = new ArrayList<>();
        mArrUserList = ((IntegrationFNSActivity)getActivity()).mArrUserList;
        mArrIntegrationFNSList = new ArrayList<>();
        mArrCommunityList = new ArrayList<>();
        mArrCommunityName = new ArrayList<>();
        mArrTonCodeList = new ArrayList<>();
        mArrTonCode = new ArrayList<>();
        mArrTonName = new ArrayList<>();
        mSendTonCode = new ArrayList<>();
        mArrServiceList = new ArrayList<ServiceData>();

        mCommunitySpinner = (Spinner) mView.findViewById(R.id.sp_item_community);
        List<String> arrCommunityList = new ArrayList<>();
        mArrAdCommunity = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arrCommunityList);
        mArrAdCommunity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCommunitySpinner.setAdapter(mArrAdCommunity);
        mCommunitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mOrgOid = mArrCommunityList.get(position).getOid();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mEditTextTonCode = (EditText) mView.findViewById(R.id.et_ton_code);
        mEditTextTonCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetTonCode();
            }
        });

        mSpnRefresh = (Spinner) v.findViewById(R.id.sp_search_time_interval);
        mSpnRefresh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });


        mTimeText = (TextView) v.findViewById(R.id.tv_time);
        mTimeEmptyText = (TextView) v.findViewById(R.id.tv_time_empty);

        mIntegrationFNSSearch = (Button) mView.findViewById(R.id.btn_integration_search);
        mIntegrationFNSSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadData(false);
            }
        });

        mIntegrationFNSList = (ExpandableListView) v.findViewById(R.id.eplv_integration_list);
        tvNoData = (TextView) v.findViewById(R.id.tv_no_data);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mIntegrationFNSListAdapter = new IntegrationFNSListAdapter(getActivity(), R.layout.integrationfns_list, mArrIntegrationFNSList, mArrIntegrationFNSList);
        mIntegrationFNSList.setAdapter(mIntegrationFNSListAdapter);
        mIntegrationFNSList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int groupCount = mIntegrationFNSListAdapter.getGroupCount();

                for (int i = 0; i < groupCount; i++) {
                    if (!(i == groupPosition))
                        mIntegrationFNSList.collapseGroup(i);
                }
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
                    onIntegrationFNSList(bFirst);
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void onIntegrationFNSList(final boolean bFirst) {
        RequestIntegrationFNSData request = new RequestIntegrationFNSData();
        request.setUserID(Func.checkStringNull(mArrUserList.get(0).getUserID()));
        request.setUserPortalKey(mArrUserList.get(0).getUserKey());

        String mTonCodeList = "";

        for (int i = 0; i < mSendTonCode.size(); i++) {
            if (i == mSendTonCode.size() - 1) {
                mTonCodeList += Func.checkStringNull(mSendTonCode.get(i).toString());
            } else {
                mTonCodeList += Func.checkStringNull(mSendTonCode.get(i).toString()) + ",";
            }
        }

        request.setTonCode(mTonCodeList);
        request.setOrgOid(mOrgOid);

        if(bFirst) {
            ArrayList<DefineListData> defineLists = new ArrayList<DefineListData>();
            mDefineListData = new DefineListData();
            mDefineListData.setType(Define.DEFINE_VEHICLE_TON_CODE_TYPE);
            defineLists.add(mDefineListData);
            request.setDefineTypeList(defineLists);
            request.setAddYN("Y");
        }

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<ResponseIntegrationFNSData> call = mHttpService.getIntegrationFNSList(request);
        call.enqueue(new Callback<ResponseIntegrationFNSData>() {
            @Override
            public void onResponse(Call<ResponseIntegrationFNSData> call, Response<ResponseIntegrationFNSData> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        ResponseIntegrationFNSData data = response.body();

                        if (data.getErrorCode().equals("0")) {
                            //화물목록
                            int mIntegrationFNSSize = data.getIntegrationFNSList().size();

                            mArrIntegrationFNSList.clear();

                            if (mIntegrationFNSSize > 0) {

                                for (int i = 0; i < mIntegrationFNSSize; i++) {
                                    IntegrationFNSListData integrationListData = new IntegrationFNSListData();
                                    integrationListData.setIntegrationMainFNS(data.getIntegrationFNSList().get(i).getIntegrationMainFNS());
                                    integrationListData.setIntegrationSubFNSList(data.getIntegrationFNSList().get(i).getIntegrationSubFNSList());
                                    mArrIntegrationFNSList.add(integrationListData);
                                }
                            }

                            if(bFirst) {
                                // Define정보
                                int mDefineSize = data.getDefineList().size();

                                if (mDefineSize > 0) {

                                    mArrTonCode.clear();
                                    mArrTonName.clear();

                                    for (int i = 0; i < mDefineSize; i++) {
                                        DefineListData defineListData = data.getDefineList().get(i);

                                        switch (defineListData.getType()) {
                                            case Define.DEFINE_VEHICLE_TON_CODE_TYPE:
                                                mArrTonCode.add(defineListData.getCode());
                                                mArrTonName.add(defineListData.getValue());
                                                break;
                                        }
                                    }

                                    mTonCodes = mArrTonCode.toArray(new String[mArrTonCode.size()]);
                                    mTonNames = mArrTonName.toArray(new String[mArrTonName.size()]);

                                    mTonSelected = new boolean[mDefineSize];
                                    Arrays.fill(mTonSelected, Boolean.FALSE);
                                }

                                //커뮤니티정보
                                int mCommunitySize = data.getCommunityList().size();

                                if (mCommunitySize > 0) {

                                    mArrCommunityName.add("선택");
                                    mArrCommunityList.add(new CommunityListData());

                                    for (int i = 0; i < mCommunitySize; i++) {
                                        CommunityListData communityListData = data.getCommunityList().get(i);

                                        mArrCommunityName.add(communityListData.getCommunityName());
                                        mArrCommunityList.add(communityListData);
                                    }
                                }
                            }

                            mIntegrationFNSListAdapter.notifyDataSetChanged();

                            if(bFirst) {
                                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_PORTALDEFINE_SUCCESS);
                            } else {
                                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS);
                            }
                        }
                        else {
                            mErrorCode = Func.checkStringNull(data.getErrorCode());
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
            public void onFailure(Call<ResponseIntegrationFNSData> call, Throwable t) {
                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
            }
        });
    }

    private void setSpinnerData() {
        //커뮤니티
        for(int i = 0; i < mArrCommunityName.size(); i++) {
            mArrAdCommunity.add(mArrCommunityName.get(i));
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            hideProgressDialog();
            switch(msg.what)
            {
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
        if (mArrIntegrationFNSList.size() > 0) {
            tvNoData.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    /** List Adapter */
    public class IntegrationFNSListAdapter extends BaseExpandableListAdapter
    {

        private ArrayList<IntegrationFNSListData> itemList;
        private ArrayList<IntegrationFNSListData> subList;
        private Context context;
        private int rowResourceId;

        public IntegrationFNSListAdapter(Context context, int textViewResourceId, ArrayList<IntegrationFNSListData> itemList, ArrayList<IntegrationFNSListData> subList) {
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
            return itemList.get(groupPosition).getIntegrationMainFNS().getTurnInfoOid();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            View v = convertView;

            IntegrationFNSListData item = itemList.get(groupPosition);
            if(item != null)
            {
                v = SetIntegrationFnsList(context, v, this.rowResourceId, item);
            }

            return v;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return itemList.get(groupPosition).getIntegrationSubFNSList().size();
        }

        @Override
        public String getChild(int groupPosition, int childPosition) {
            return subList.get(groupPosition).getIntegrationSubFNSList().get(0).getTransOrder();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        // 차일드뷰 각각의 ROW
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            View v = convertView;

            IntegrationSubFnsData item = subList.get(groupPosition).getIntegrationSubFNSList().get(childPosition);
            if(item != null)
            {
                v = SetIntegrationFnsSubList(context, v, this.rowResourceId, item, childPosition, isLastChild);
            }

            return v;
        }

        @Override
        public boolean hasStableIds() { return true; }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
    }

    private View SetIntegrationFnsList(Context context, View v, int rowResourceId, IntegrationFNSListData Data) {
        try {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.integrationfns_list, null);

            LinearLayout linLayout = (LinearLayout) v.findViewById(R.id.lin_layout);
            TextView tvFreightInfo = (TextView) v.findViewById(R.id.tv_freight_info);
            TextView tvFreightPrice = (TextView) v.findViewById(R.id.tv_freight_price);
            TextView tvFreightTon = (TextView) v.findViewById(R.id.tv_freight_ton);
            TextView tvFreightRoute = (TextView) v.findViewById(R.id.tv_freight_route);

            String pickupTypeTimeType = Data.getIntegrationSubFNSList().get(0).getPickupTimeType();

            if (pickupTypeTimeType.equals("10")) {
                linLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.mark_emergency));
            } else if (pickupTypeTimeType.equals("70")) {
                linLayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.mark_reservation));
            }

            String pickupTypeName = (pickupTypeTimeType.equals("20") || pickupTypeTimeType.equals("30")) ? "당일" : Data.getIntegrationSubFNSList().get(0).getPickupTimeTypeName();
            String freightInfo = (Func.checkStringNull(pickupTypeName).equals("") ? "상차요청 : " : pickupTypeName + "/ 상차요청 : " ) + Data.getIntegrationSubFNSList().get(0).getPickupTime() + (Data.getIntegrationMainFNS().getVehicleAllocationBy().equals("4") ? "(지정) [" : " [") + Data.getIntegrationMainFNS().getOrgName() + "]";

            tvFreightInfo.setText(freightInfo);
            tvFreightTon.setText(Data.getIntegrationMainFNS().getTonCode());
            tvFreightRoute.setText(Data.getIntegrationMainFNS().getRoute());
            tvFreightPrice.setText(Data.getIntegrationMainFNS().getChargeAmount());


        } catch (Exception ex) { }

        return v;
    }


    private View SetIntegrationFnsSubList(Context context, View v, int rowResourceId, final IntegrationSubFnsData Data, int childPosition, boolean isLastChild)
    {
        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.integrationfns_sublist, null);

        LinearLayout layCommpanyInfo = (LinearLayout) v.findViewById(R.id.lay_cominfo);
        LinearLayout layFunction = (LinearLayout) v.findViewById(R.id.lay_function);
        TextView tvDeliveryCustomerName = (TextView) v.findViewById(R.id.tv_delivery_customer_name);
        TextView tvDeliveryCustomerPhone = (TextView) v.findViewById(R.id.tv_delivery_customer_phone);
        TextView tvRoute = (TextView) v.findViewById(R.id.tv_route);
        TextView tvTransId = (TextView) v.findViewById(R.id.tv_trans_id);


        TextView tvPickupType = (TextView) v.findViewById(R.id.tv_pickup_type);
        TextView tvPickupAddress = (TextView) v.findViewById(R.id.tv_pickup_address);
        TextView tvDeliveryType = (TextView) v.findViewById(R.id.tv_delivery_type);
        TextView tvDeliveryAddress = (TextView) v.findViewById(R.id.tv_delivery_address);
        TextView tvItemKindType = (TextView) v.findViewById(R.id.tv_item_kind_type);
        TextView tvVehicleType = (TextView) v.findViewById(R.id.tv_vehicle_type);
        TextView tvCustomerOrderType = (TextView) v.findViewById(R.id.tv_customer_order_type);
        TextView tvPaymentType = (TextView) v.findViewById(R.id.tv_payment_type);
        TextView tvRemark = (TextView) v.findViewById(R.id.tv_remark);
        TextView tv_time = (TextView) v.findViewById(R.id.tv_time);
        TextView tvCommunityName = (TextView) v.findViewById(R.id.tv_community_name);
        TextView tvVehicleNo2 = (TextView) v.findViewById(R.id.tv_vehicle_no2);

        if(childPosition == 0) {
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
        } else {
            layCommpanyInfo.setVisibility(View.GONE);
        }

        String loading = "";
        String unloading = "";

        if(!Data.getPickupCustomerName().equals("")) {
            loading += Data.getPickupCustomerName();
        } else if(!Data.getPickupCustomerName().equals("")) {
            loading += Data.getPickupCustomerName();
        } else {
            loading += Data.getPickupArea();
        }

        if(!Data.getDeliveryCustomerName().equals("")) {
            unloading += Data.getDeliveryCustomerName();
        } else if(!Data.getDeliveryDepotName().equals("")) {
            unloading += Data.getDeliveryDepotName();
        } else {
            unloading += Data.getDeliveryArea();
        }

        if(!Data.getTransOrderID().equals("")) {
            tvTransId.setText("오더:" + Data.getTransOrderID());
            tvTransId.setVisibility(View.VISIBLE);
        }

        String pickupTypeName = "";
        String pickupTime = "";
        String loadingMethodName = "";
        String pickupAddress = "";
        String unLoadingMethodName = "";
        String deliveryTime = "";
        String deliveryAddress = "";

        // FNS오더
        if (Data.getVehicleAllocationBy().equals("2")) {
            if (!Data.getPickupTimeTypeName().equals("") || !Data.getLoadingMethodName().equals("")) {

                if (!Data.getPickupTimeTypeName().equals("")) {
                    pickupTypeName = Data.getPickupTimeTypeName();
                }

                if (Data.getPickupTimeType().equals("20") || Data.getPickupTimeType().equals("30") || Data.getPickupTimeType().equals("70")) {
                    pickupTime = " (" + Data.getPickupTime() + ")";
                }

                if (!Data.getPickupTimeTypeName().equals("") && !Data.getLoadingMethodName().equals("")) {
                    loadingMethodName = " / " + Data.getLoadingMethodName();
                }

                if(!Data.getLoadingMethodName().equals("") && !Data.getPickupAddress().equals("")) {
                    pickupAddress = " / " + Data.getPickupAddress();
                }

                tvPickupType.setText("[" + loading + "]" + pickupTypeName + pickupTime + loadingMethodName + pickupAddress);
                tvPickupType.setVisibility(View.VISIBLE);

            }

            if (Data.getDeliveryTime().length() > 4) {
                deliveryTime = " (" + Data.getDeliveryTime().substring(0, 5) + ")";
            }

            if (!Data.getUnloadingMethodName().equals("")) {
                unLoadingMethodName = " / " + Data.getUnloadingMethodName();
            }

            if (!Data.getDeliveryAddress().equals("")) {
                deliveryAddress = " / " + Data.getDeliveryAddress();
            }

            tvDeliveryType.setText("[" + unloading + "]" + Data.getDeliveryTimeTypeName() + deliveryTime + unLoadingMethodName + deliveryAddress);
            tvDeliveryType.setVisibility(View.VISIBLE);

            if (!Data.getItemInfo().equals("") || !Data.getWeight().equals("") || !Data.getTransTypeName().equals("")) {
                String itemKindName = Data.getItemInfo().equals("") ? "" : Data.getItemInfo();
                String weight = Data.getWeight().equals("") ? "" : " (" + Data.getWeight() + " 톤)";
                String transTypeName = Data.getTransTypeName().equals("") ? "" : " / " + Data.getTransTypeName();
                tvItemKindType.setText(itemKindName + weight + transTypeName);
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
        } else {

            if (!Data.getPickupCustomerName().equals("")) {
                tvPickupType.setText("상차 : [" + Data.getPickupCustomerName() + "] " + Data.getPickupTime() );
                tvPickupType.setVisibility(View.VISIBLE);
            } else if (!Data.getPickupDepotName().equals("")) {
                tvPickupType.setText("상차 : [" + Data.getPickupDepotName() + "] " + Data.getPickupTime() );
                tvPickupType.setVisibility(View.VISIBLE);
            } else {
                tvPickupType.setText("상차 : [" + Data.getPickupArea() + "] " + Data.getPickupTime() );
                tvPickupType.setVisibility(View.VISIBLE);
            }

            if(!Data.getDeliveryAddress().equals("")) {
                tvPickupAddress.setText(Data.getDeliveryAddress());
                tvPickupAddress.setVisibility(View.VISIBLE);
            }

            if (!Data.getDeliveryCustomerName().equals("")) {
                tvDeliveryType.setText("상차 : [" + Data.getDeliveryCustomerName() + "] " + Data.getDeliveryTime() );
                tvDeliveryType.setVisibility(View.VISIBLE);
            } else if (!Data.getDeliveryDepotName().equals("")) {
                tvDeliveryType.setText("상차 : [" + Data.getDeliveryDepotName() + "] " + Data.getDeliveryTime() );
                tvDeliveryType.setVisibility(View.VISIBLE);
            } else {
                tvDeliveryType.setText("상차 : [" + Data.getDeliveryArea() + "] " + Data.getDeliveryTime() );
                tvDeliveryType.setVisibility(View.VISIBLE);
            }

            if(!Data.getDeliveryAddress().equals("")) {
                tvDeliveryAddress.setText(Data.getDeliveryAddress());
                tvDeliveryAddress.setVisibility(View.VISIBLE);
            }

            if(!Data.getItemInfo().equals("")) {
                tvItemKindType.setText(Data.getItemInfo());
                tvItemKindType.setVisibility(View.VISIBLE);
            }

            if(!Data.getVehicleInfo().equals("")) {
                tvVehicleType.setText(Data.getVehicleInfo());
                tvVehicleType.setVisibility(View.VISIBLE);
            }

            if (!Data.getRemark().equals("")) {
                tvRemark.setText("비고 : " + Data.getRemark());
                tvRemark.setVisibility(View.VISIBLE);
            }
        }

        tvCommunityName.setText("게시 : " + Data.getOrgOwnerName() + " - " + Data.getOrgName());
        tvCommunityName.setVisibility(View.VISIBLE);

        if(!Data.getVehicleNo2().equals("")) {
            tvVehicleNo2.setText(Data.getVehicleNo2());
            tvVehicleNo2.setVisibility(View.VISIBLE);
        }

        //기능 버튼
        if(isLastChild) {

            final Spinner spnCommunity = (Spinner) v.findViewById(R.id.sp_item_community);
            Button btnApply = (Button) v.findViewById(R.id.btn_apply);

            if (Data.getVehicleAllocationBy().equals("4")) {
                spnCommunity.setVisibility(View.GONE);
                btnApply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onApplyOrder("", Data.getTurnInfoOid(), Data.getVehicleNo2());
                    }
                });
            } else {

                ArrayAdapter<String> mArrCommunity;
                final Vector<String[]> mVectorCommunity;
                mVectorCommunity = new Vector<String[]>();
                List<String> arrCommunity = new ArrayList<String>();
                mArrCommunity = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrCommunity);
                mArrCommunity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnCommunity.setAdapter(mArrCommunity);
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

                        onApplyOrder(sVal[0], Data.getTurnInfoOid(), "");
                    }
                });

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

                spnCommunity.setVisibility(View.VISIBLE);
            }
        } else {
            layFunction.setVisibility(View.GONE);
        }

        return v;
    }

    private void onApplyOrder(final String mOrgID, final String mTurnInfoOid, final String mVehicleNo) {
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    onApply(mOrgID, mTurnInfoOid, mVehicleNo);
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void onApply(String mOrgID, String mTurnInfoOid, String mVehicleNo) {
        RequestFreightRegisterData requestFreightRegisterData = new RequestFreightRegisterData();
        requestFreightRegisterData.setTurnInfoOid(mTurnInfoOid);
        requestFreightRegisterData.setOrgID(mOrgID);
        requestFreightRegisterData.setUserID(mArrUserList.get(0).getUserID());
        requestFreightRegisterData.setUserPortalKey(mArrUserList.get(0).getUserKey());
        //requestFreightRegisterData.setUserSiteKey(Func.checkStringNull(PrefsUtil.getValue(((IntegrationFNSActivity)getActivity()).mServiceCode, "")));

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

    private void callPhone(String mphone) {
        if (!mphone.equals("")) {
            Uri uri= Uri.parse("tel:" + mphone);
            Intent i = new Intent(Intent.ACTION_CALL, uri);
            startActivity(i);
        }
    }

    private void onSetTonCode() {
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

    public void countDownTimer(final int mTime) {
        countDownTimer = new CountDownTimer(mTime * Define.SMS_RECEIVE_COUNT_DOWN_INTERVAL, Define.SMS_RECEIVE_COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                mTimeText.setText(String.valueOf(millisUntilFinished / Define.SMS_RECEIVE_COUNT_DOWN_INTERVAL));
            }
            public void onFinish() {
                countDownTimer.cancel();
                onIntegrationFNSList(false);
            }
        };
    }
}
