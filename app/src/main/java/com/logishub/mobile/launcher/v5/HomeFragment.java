package com.logishub.mobile.launcher.v5;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.DATA.MenuData;
import com.logishub.mobile.launcher.v5.DATA.ServiceData;
import com.logishub.mobile.launcher.v5.DATA.ServiceMenuAdapter;
import com.logishub.mobile.launcher.v5.DATA.SubMenuAdapter;
import com.logishub.mobile.launcher.v5.DATA.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HomeFragment extends Fragment {
    public View mView;
    GridView mMenuGridView;
    GridView mServiceGridView;
    SubMenuAdapter mSubMenuAdapter;
    ServiceMenuAdapter mServiceMenuAdapter;
    private CustomProgressDialog mProgressDialog;
    private String mMenuID;
    private ArrayList<String> mMenuList;
    private ArrayList<Integer> mMenuImageList;
    private ArrayList<String> mMenuUrlList;
    private ArrayList<String> mSiteCodeList;
    private ArrayList<String> mMenuOidList;
    private ArrayList<String> mMenuDescriptionList;
    private ArrayList<String> mServiceManagerList;
    private ArrayList<String> mServiceList;
    private ArrayList<String> mServiceImageList;
    private ArrayList<String> mServiceUrlList;
    private ArrayList<String> mServiceTypeList;
    private ArrayList<String> mServiceOidList;

    public static String [] mMenuNames;
    public static Integer [] mMenuImages;
    public static String [] mMenuUrls;
    public static String [] mSiteCodes;
    public static String [] mMenuOids;
    public static String [] mMenuDescription;
    public static String [] mServiceNames;
    public static String [] mServiceImages;
    public static String [] mServiceUrls;
    public static String [] mServiceTypes;
    public static String [] mServiceOids;
    public static String [] mServiceIsManager;

    public static ArrayList<MenuData> mArrMenuList = null;
    public static ArrayList<ServiceData> mArrServiceList = null;
    private static ArrayList<UserData> mArrUserList = null;

    public HomeFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home, container, false);

        mArrMenuList = new ArrayList<>();
        mArrServiceList = new ArrayList<>();
        mArrUserList = new ArrayList<>();

        Bundle extra = getArguments();
        mMenuID = extra.getString(Define.FRG_PUT_REQ_MENU_ID);

        mArrMenuList = ((MainActivity)getActivity()).mArrMenuList;
        mArrServiceList = ((MainActivity)getActivity()).mArrServiceList;
        mArrUserList = ((MainActivity)getActivity()).mArrUserList;

        showProgressDialog();

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
        mMenuGridView = null;
        mServiceGridView = null;
        mSubMenuAdapter = null;
        mServiceMenuAdapter = null;
        mMenuList = null;
        mMenuImageList = null;
        mServiceList = null;
        mServiceImageList = null;
        mMenuUrlList = null;
        mServiceUrlList = null;
        mSiteCodeList = null;
        mServiceTypeList = null;
        mServiceOidList = null;
        mMenuNames = null;
        mServiceNames = null;
        mMenuImages = null;
        mServiceImages = null;
        mMenuUrls = null;
        mServiceUrls = null;
        mSiteCodes = null;
        mServiceTypes = null;
        mServiceOids = null;
        mArrMenuList = null;
        mArrServiceList = null;
        mMenuOidList = null;
        mMenuOids = null;
        mServiceManagerList = null;
        mServiceIsManager = null;

        hideProgressDialog();
    }

    private void setLayout(View v) {
        mMenuList = new ArrayList<>();
        mMenuImageList = new ArrayList<>();
        mMenuUrlList = new ArrayList<>();
        mSiteCodeList = new ArrayList<>();
        mServiceTypeList = new ArrayList<>();
        mMenuOidList = new ArrayList<>();
        mMenuDescriptionList = new ArrayList<>();

        /*mServiceList = new ArrayList<>();
        mServiceImageList = new ArrayList<>();
        mServiceUrlList = new ArrayList<>();
        mServiceOidList = new ArrayList<>();
        mServiceManagerList = new ArrayList<>();*/

        HashMap<String, String> map = new HashMap<>();

        try {

            for (int i = 0; i < mArrMenuList.size(); i++) {

                for (int j = 0; j < mArrServiceList.size(); j++) {
                    if (!mArrServiceList.get(j).getServiceType().equals("")) {
                        map.put(mArrServiceList.get(j).getServiceType().substring(0, 3), mArrServiceList.get(j).getServiceType());
                    }
                }

                Iterator<String> iter = map.keySet().iterator();
                while(iter.hasNext()) {
                    String key = iter.next();

                    if (mArrMenuList.get(i).getName() != null && mArrMenuList.get(i).getLevel().equals(Define.MENU_LEVEL2) && Func.checkStringNull(mArrMenuList.get(i).getDescription()).indexOf(key) > -1 && mArrMenuList.get(i).getParentId().equals(mMenuID)) {
                        if (!mArrMenuList.get(i).getName().equals(Define.MENU_NAME_CARGO_INFO)) {
                            mMenuList.add(mArrMenuList.get(i).getName());
                            mMenuUrlList.add(mArrMenuList.get(i).getClassName());
                            mMenuOidList.add(mArrMenuList.get(i).getId());
                            mMenuDescriptionList.add(mArrMenuList.get(i).getDescription());
                            onAddMenuImage(mArrMenuList.get(i).getName());
                        }
                    }
                }

                if (mArrMenuList.get(i).getName() != null && mArrMenuList.get(i).getLevel().equals(Define.MENU_LEVEL2) && Func.checkStringNull(mArrMenuList.get(i).getDescription()).equals("") && mArrMenuList.get(i).getParentId().equals(mMenuID)) {
                    mMenuList.add(mArrMenuList.get(i).getName());
                    mMenuUrlList.add(mArrMenuList.get(i).getClassName());
                    mMenuOidList.add(mArrMenuList.get(i).getId());
                    mMenuDescriptionList.add(mArrMenuList.get(i).getDescription());
                    onAddMenuImage(mArrMenuList.get(i).getName());
                }
            }

            /*for (int i = 0; i < mArrServiceList.size(); i++) {
                if (mArrServiceList.get(i).getServiceName() != null) {
                    mServiceList.add(mArrServiceList.get(i).getServiceName());
                    mServiceUrlList.add(mArrServiceList.get(i).getServiceUrl());
                    mSiteCodeList.add(mArrServiceList.get(i).getServiceSite());
                    mServiceTypeList.add(mArrServiceList.get(i).getServiceType());
                    mServiceOidList.add(mArrServiceList.get(i).getServiceOid());
                    mServiceManagerList.add(mArrServiceList.get(i).getIsManager());

                    if (mArrServiceList.get(i).getHasImage().equals("1")) {
                        mServiceImageList.add(Define.SERVICE_SITE_IMAGE_URL + mArrServiceList.get(i).getServiceSite() + ".png");
                    } else {
                        switch (mArrServiceList.get(i).getServiceType().toUpperCase()) {
                            case Define.SERVICE_TYPE_TMS:
                            case Define.SERVICE_TYPE_TMS_LITE:
                                mServiceImageList.add(Define.SERVICE_SERVICE_IMAGE_URL + "ic_tms.png");
                                break;

                            case Define.SERVICE_TYPE_WMS:
                            case Define.SERVICE_TYPE_WMS_LITE:
                                mServiceImageList.add(Define.SERVICE_SERVICE_IMAGE_URL + "ic_wms.png");
                                break;

                            case Define.SERVICE_TYPE_DIS:
                            case Define.SERVICE_TYPE_DIS_LITE:
                                mServiceImageList.add(Define.SERVICE_SERVICE_IMAGE_URL + "ic_dis.png");
                                break;

                            case Define.SERVICE_TYPE_FNS:
                            case Define.SERVICE_TYPE_FNS_LITE:
                                mServiceImageList.add(Define.SERVICE_SERVICE_IMAGE_URL + "ic_fns.png");
                                break;

                            case Define.SERVICE_TYPE_LMS:
                            case Define.SERVICE_TYPE_LMS_LITE:
                                mServiceImageList.add(Define.SERVICE_SERVICE_IMAGE_URL + "ic_lms.png");
                                break;

                            default:
                                mServiceImageList.add(Define.SERVICE_SERVICE_IMAGE_URL + "ic_tms.png");
                                break;
                        }
                    }
                }
            }*/

        } catch (Exception e) { }

        mMenuNames = mMenuList.toArray(new String[mMenuList.size()]);
        mMenuImages = mMenuImageList.toArray(new Integer[mMenuImageList.size()]);
        mMenuUrls = mMenuUrlList.toArray(new String[mMenuUrlList.size()]);
        mSiteCodes = mSiteCodeList.toArray(new String[mSiteCodeList.size()]);
        mMenuOids = mMenuOidList.toArray(new String[mMenuOidList.size()]);
        mMenuDescription = mMenuDescriptionList.toArray(new String[mMenuDescriptionList.size()]);

        /*mServiceNames = mServiceList.toArray(new String[mServiceList.size()]);
        mServiceImages = mServiceImageList.toArray(new String[mServiceImageList.size()]);
        mServiceUrls = mServiceUrlList.toArray(new String[mServiceUrlList.size()]);
        mServiceTypes = mServiceTypeList.toArray(new String[mServiceTypeList.size()]);
        mServiceOids = mServiceOidList.toArray(new String[mServiceOidList.size()]);
        mServiceIsManager = mServiceManagerList.toArray(new String[mServiceManagerList.size()]);*/

        mMenuGridView = (GridView) v.findViewById(R.id.gv_sub_menu);
        mSubMenuAdapter = new SubMenuAdapter(getActivity(), mMenuImages, mMenuNames, "MENU");
        mMenuGridView.setAdapter(mSubMenuAdapter);

        mMenuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent;

                switch (mMenuNames[position]) {

                    case Define.MENU_NAME_FREIGHT_REGISTER:
                    case Define.MENU_NAME_FREIGHT_TEMPLATE:
                    case Define.MENU_NAME_FREIGHT_MANAGE_LIST:
                    case Define.MENU_NAME_STOCK_INFO:
                    case Define.MENU_NAME_IN_OUT_BOUND_INFO:
                    case Define.MENU_NAME_ITEM_INFO:
                        ShowServiceWithListView(mMenuNames[position], mMenuUrls[position], mMenuOids[position], mMenuDescription[position]);
                        break;

                    case Define.MENU_NAME_JOB_INFO:
                        intent = new Intent(getActivity().getApplicationContext(), RecruitActivity.class);
                        intent.putExtra(Define.ACT_PUT_REQ_ACT_TITLE, mMenuNames[position]);
                        intent.putExtra(Define.ACT_PUT_REQ_MENU_LIST, mArrMenuList);
                        intent.putExtra(Define.ACT_PUT_REQ_MENU_OID, mMenuOids[position]);
                        intent.putExtra(Define.ACT_PUT_REQ_USER_LIST, mArrUserList);
                        startActivity(intent);
                        break;

                    case Define.MENU_NAME_CARGO_INFO:
                        intent = new Intent(getActivity().getApplicationContext(), IntegrationFNSActivity.class);
                        intent.putExtra(Define.ACT_PUT_REQ_ACT_TITLE, mMenuNames[position]);
                        intent.putExtra(Define.ACT_PUT_REQ_MENU_LIST, mArrMenuList);
                        intent.putExtra(Define.ACT_PUT_REQ_MENU_OID, mMenuOids[position]);
                        intent.putExtra(Define.ACT_PUT_REQ_USER_LIST, mArrUserList);
                        startActivity(intent);
                        break;

                    default:
                        intent = new Intent(Define.SEND_BROADCAST_FLAG);
                        intent.putExtra(Define.SEND_BROADCAST_WEB_FLAG, Define.MENU_WEB_FLAG);
                        intent.putExtra(Define.SEND_BROADCAST_WEB_TYPE, Define.MENU_WEB_MENU_DEFAULT_TYPE);
                        intent.putExtra(Define.SEND_BROADCAST_WEB_URL, mMenuUrls[position]);
                        intent.putExtra(Define.SEND_BROADCAST_WEB_TITLE, mMenuNames[position]);
                        getActivity().sendBroadcast(intent);
                        break;
                }
            }
        });

        /*mServiceGridView = (GridView) v.findViewById(R.id.gv_sub_service);
        mServiceMenuAdapter = new ServiceMenuAdapter(getActivity(), mServiceImages, mServiceNames, "SERVICE");
        mServiceGridView.setAdapter(mServiceMenuAdapter);

        mServiceGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                *//*if (mServiceTypes[position].toUpperCase().equals("WMS") || mServiceTypes[position].toUpperCase().equals("WMSLITE")) {
                    Intent sendIntent = new Intent(Define.SEND_BROADCAST_FLAG);
                    sendIntent.putExtra(Define.SEND_BROADCAST_WEB_FLAG, Define.MENU_WEB_FLAG);
                    sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TYPE, Define.MENU_WEB_MENU_SERVICE_TYPE);
                    sendIntent.putExtra(Define.SEND_BROADCAST_WEB_URL, Define.LOGISHUB_WEB_SERVICE + "?serviceUrl=" + mServiceUrls[position] + "&siteCode=" + mSiteCodes[position] + "&serviceName=" + mServiceNames[position] + "&serviceType=" + mServiceTypes[position]);
                    sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TITLE, mServiceNames[position]);
                    getActivity().sendBroadcast(sendIntent);
                } else if (mServiceTypes[position].toUpperCase().equals("FNS") || mServiceTypes[position].toUpperCase().equals("FNSLITE")) {
                    ShowAlertDialogWithListView(mServiceNames[position], mServiceTypes[position], mSiteCodes[position], mServiceUrls[position], mServiceOids[position]);
                }*//*

                ShowAlertDialogWithListView(mServiceNames[position], mServiceTypes[position], mSiteCodes[position], mServiceUrls[position], mServiceOids[position], mServiceIsManager[position]);
            }
        });*/

        hideProgressDialog();
    }

    private void onAddMenuImage(String mMenuName) {
        switch (mMenuName) {

            case Define.MENU_NAME_CARGO_INFO:
                mMenuImageList.add(R.drawable.ic_menu_cargo);
                break;

            case Define.MENU_NAME_FREIGHT_REGISTER:
                mMenuImageList.add(R.drawable.ic_menu_cargoadd);
                break;

            case Define.MENU_NAME_FREIGHT_TEMPLATE:
                mMenuImageList.add(R.drawable.ic_menu_template);
                break;

            case Define.MENU_NAME_FREIGHT_MANAGE_LIST:
                mMenuImageList.add(R.drawable.ic_menu_cargosearch);
                break;

            case Define.MENU_NAME_ACCOUNT_SET:
                mMenuImageList.add(R.drawable.ic_menu_accountset);
                break;

            case Define.MENU_NAME_ALARM_SET:
                mMenuImageList.add(R.drawable.ic_menu_alarmset);
                break;

            case Define.MENU_NAME_CONDITIONS:
                mMenuImageList.add(R.drawable.ic_menu_conditions);
                break;

            case Define.MENU_NAME_PASS_SET:
                mMenuImageList.add(R.drawable.ic_menu_pwset);
                break;

            case Define.MENU_NAME_TAX_INVOICE:
                mMenuImageList.add(R.drawable.ic_menu_taxinvoice);
                break;

            case Define.MENU_NAME_USER_GUIDE:
                mMenuImageList.add(R.drawable.ic_menu_userguide);
                break;

            case Define.MENU_NAME_WITH_DRAWL:
                mMenuImageList.add(R.drawable.ic_menu_withdrawl);
                break;

            case Define.MENU_NAME_CALCULATE:
                mMenuImageList.add(R.drawable.ic_menu_calculate);
                break;

            case Define.MENU_NAME_JOB_INFO:
                mMenuImageList.add(R.drawable.ic_menu_jobinfo);
                break;

            case Define.MENU_NAME_NOTICE:
                mMenuImageList.add(R.drawable.ic_menu_notice);
                break;

            case Define.MENU_NAME_TRACKING:
            case Define.MENU_NAME_TRACKING2:
                mMenuImageList.add(R.drawable.ic_menu_tracking);
                break;

            case Define.MENU_NAME_STATISTICS:
                mMenuImageList.add(R.drawable.ic_menu_statuscheck);
                break;

            case Define.MENU_NAME_INVENTORY_LIST:
                mMenuImageList.add(R.drawable.ic_menu_stockinfo);
                break;

            case Define.MENU_NAME_RECEIVE_RELEASE_LIST:
                mMenuImageList.add(R.drawable.ic_menu_accessinfo);
                break;

            case Define.MENU_NAME_ITEM_LIST:
                mMenuImageList.add(R.drawable.ic_menu_productinfo);
                break;

            default:
                mMenuImageList.add(R.drawable.ic_notification_s);
                break;
        }
    }

    public void ShowServiceWithListView(final String mTitle, final String mMenuUri, final String mMenuOid, final String mMenuDescription)
    {
        List<String> mServiceNameList = new ArrayList<>();
        List<String> mServiceOidList = new ArrayList<>();
        List<String> mServiceTypeList = new ArrayList<>();
        List<String> mServiceCodeList = new ArrayList<>();
        List<String> mServiceUriList = new ArrayList<>();

        final String [] mServiceNames;
        final String [] mServiceOids;
        final String [] mServiceTypes;
        final String [] mServiceCodes;
        final String [] mServiceUris;

        int mServiceSize = mArrServiceList.size();

        for (int i = 0; i < mServiceSize; i++)
        {
            if (mMenuDescription.toUpperCase().equals(Define.SERVICE_TYPE_FNS) || mMenuDescription.toUpperCase().equals(Define.SERVICE_TYPE_FNS_LITE)) {
                if (!mArrServiceList.get(i).getServiceType().toUpperCase().equals(Define.SERVICE_TYPE_WMS) && !mArrServiceList.get(i).getServiceType().toUpperCase().equals(Define.SERVICE_TYPE_WMS_LITE)) {
                    mServiceNameList.add(mArrServiceList.get(i).getServiceName());
                    mServiceOidList.add(mArrServiceList.get(i).getServiceOid());
                    mServiceTypeList.add(mArrServiceList.get(i).getServiceType());
                    mServiceCodeList.add(mArrServiceList.get(i).getServiceSite());
                    mServiceUriList.add(mArrServiceList.get(i).getServiceUrl());
                }
            } else {
                mServiceNameList.add(mArrServiceList.get(i).getServiceName());
                mServiceOidList.add(mArrServiceList.get(i).getServiceOid());
                mServiceTypeList.add(mArrServiceList.get(i).getServiceType());
                mServiceCodeList.add(mArrServiceList.get(i).getServiceSite());
                mServiceUriList.add(mArrServiceList.get(i).getServiceUrl());
            }
        }

        mServiceNames = mServiceNameList.toArray(new String[mServiceNameList.size()]);
        mServiceOids = mServiceOidList.toArray(new String[mServiceOidList.size()]);
        mServiceTypes = mServiceTypeList.toArray(new String[mServiceTypeList.size()]);
        mServiceCodes = mServiceCodeList.toArray(new String[mServiceCodeList.size()]);
        mServiceUris = mServiceUriList.toArray(new String[mServiceUriList.size()]);

        final CharSequence[] csService = mServiceNameList.toArray(new String[mServiceNameList.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(mTitle);
        dialogBuilder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton){

            }
        });
        dialogBuilder.setItems(csService, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Intent intent;
                Intent sendIntent;
                switch (mTitle) {
                    case Define.MENU_NAME_FREIGHT_REGISTER:
                        intent = new Intent(getActivity().getApplicationContext(), FreightInfoActivity.class);
                        intent.putExtra(Define.ACT_PUT_REQ_ACT_TITLE, mServiceNames[item] + " " + mTitle);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_TYPE, mServiceTypes[item]);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_CODE, mServiceCodes[item]);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_URL, mServiceUris[item]);
                        intent.putExtra(Define.ACT_PUT_REQ_MENU_OID, mMenuOid);
                        intent.putExtra(Define.ACT_PUT_REQ_MENU_LIST, mArrMenuList);
                        intent.putExtra(Define.ACT_PUT_REQ_USER_LIST, mArrUserList);
                        startActivity(intent);
                        break;

                    case Define.MENU_NAME_CARGO_INFO:
                        intent = new Intent(getActivity().getApplicationContext(), CargoInfoActivity.class);
                        intent.putExtra(Define.ACT_PUT_REQ_ACT_TITLE, mServiceNames[item] + " " + mTitle);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_OID, mServiceOids[item]);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_TYPE, mServiceTypes[item]);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_CODE, mServiceCodes[item]);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_URL, mServiceUris[item]);
                        intent.putExtra(Define.ACT_PUT_REQ_MENU_OID, mMenuOid);
                        intent.putExtra(Define.ACT_PUT_REQ_MENU_LIST, mArrMenuList);
                        intent.putExtra(Define.ACT_PUT_REQ_USER_LIST, mArrUserList);
                        startActivity(intent);
                        break;

                    case Define.MENU_NAME_FREIGHT_TEMPLATE:
                        intent = new Intent(getActivity().getApplicationContext(), TemplateActivity.class);
                        intent.putExtra(Define.ACT_PUT_REQ_ACT_TITLE, mServiceNames[item] + " " + mTitle);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_CODE, mServiceCodes[item]);
                        startActivity(intent);
                        break;

                    case Define.MENU_NAME_FREIGHT_MANAGE_LIST:
                        intent = new Intent(getActivity().getApplicationContext(), CargoManagerInfoActivity.class);
                        intent.putExtra(Define.ACT_PUT_REQ_ACT_TITLE, mServiceNames[item] + " " + mTitle);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_OID, mServiceOids[item]);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_CODE, mServiceCodes[item]);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_URL, mServiceUris[item]);
                        intent.putExtra(Define.ACT_PUT_REQ_USER_LIST, mArrUserList);
                        startActivity(intent);
                        break;

                    case Define.MENU_NAME_STOCK_INFO:
                    case Define.MENU_NAME_IN_OUT_BOUND_INFO:
                    case Define.MENU_NAME_ITEM_INFO:
                        sendIntent = new Intent(Define.SEND_BROADCAST_FLAG);
                        sendIntent.putExtra(Define.SEND_BROADCAST_WEB_FLAG, Define.MENU_WEB_FLAG);
                        sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TYPE, "");
                        sendIntent.putExtra(Define.SEND_BROADCAST_WEB_URL, mMenuUri + "?serviceUrl=" + mServiceUris[item] + "&siteCode=" + mServiceCodes[item]);
                        sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TITLE, mServiceNames[item] + " " + mTitle);
                        getActivity().sendBroadcast(sendIntent);
                        break;
                }
            }
        });

        AlertDialog alertDialogObject = dialogBuilder.create();
        ListView listView = alertDialogObject.getListView();
        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.base)));
        listView.setDividerHeight(2);
        alertDialogObject.show();
    }

    /** 서비스 사용 시 표기*/
    /*public void ShowAlertDialogWithListView(final String mTitle, final String mServiceType, final String mServiceCode, final String mServiceURL, final String mServiceOid, final String mIsManager)
    {
        List<String> mMenus = new ArrayList<>();
        List<String> mMenuOidList = new ArrayList<>();
        List<String> mMenuUrlLists = new ArrayList<>();
        final String [] mMenuOids;
        final String [] mMenuUrls;
        String serviceType = "";

        int mMenuSize = mArrMenuList.size();

        if (mServiceType.length() > 0) {
            serviceType = mServiceType.substring(0, 3);
        }

        for (int i = 0; i < mMenuSize; i++)
        {
            if (mArrMenuList.get(i).getName() != null && mArrMenuList.get(i).getLevel().equals(Define.MENU_LEVEL2) && (Func.checkStringNull(mArrMenuList.get(i).getDescription()).equals(serviceType) || Func.checkStringNull(mArrMenuList.get(i).getDescription()).equals(Define.SERVICE_TYPE_FNS))) {
                if (!mArrMenuList.get(i).getName().equals(Define.MENU_NAME_CARGO_INFO)) {
                    if (mArrMenuList.get(i).getName().equals(Define.MENU_NAME_FREIGHT_MANAGE_LIST) && !mIsManager.equals("1")) {

                    } else {
                        mMenus.add(mArrMenuList.get(i).getName());
                        mMenuOidList.add(mArrMenuList.get(i).getId());
                        mMenuUrlLists.add(mArrMenuList.get(i).getClassName());
                    }
                }
            }
        }

        mMenuOids = mMenuOidList.toArray(new String[mMenuOidList.size()]);
        mMenuUrls = mMenuUrlLists.toArray(new String[mMenuUrlLists.size()]);

        final CharSequence[] Menus = mMenus.toArray(new String[mMenus.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(mTitle);
        dialogBuilder.setItems(Menus, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Intent intent;
                Intent sendIntent;
                switch (Menus[item].toString()) {
                    case Define.MENU_NAME_FREIGHT_REGISTER:
                        intent = new Intent(getActivity().getApplicationContext(), FreightInfoActivity.class);
                        intent.putExtra(Define.ACT_PUT_REQ_ACT_TITLE, mTitle);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_TYPE, mServiceType);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_CODE, mServiceCode);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_URL, mServiceURL);
                        intent.putExtra(Define.ACT_PUT_REQ_MENU_OID, mMenuOids[item]);
                        intent.putExtra(Define.ACT_PUT_REQ_MENU_LIST, mArrMenuList);
                        intent.putExtra(Define.ACT_PUT_REQ_USER_LIST, mArrUserList);
                        startActivity(intent);
                        break;

                    case Define.MENU_NAME_CARGO_INFO:
                        intent = new Intent(getActivity().getApplicationContext(), CargoInfoActivity.class);
                        intent.putExtra(Define.ACT_PUT_REQ_ACT_TITLE, mTitle);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_OID, mServiceOid);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_TYPE, mServiceType);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_CODE, mServiceCode);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_URL, mServiceURL);
                        intent.putExtra(Define.ACT_PUT_REQ_MENU_OID, mMenuOids[item]);
                        intent.putExtra(Define.ACT_PUT_REQ_MENU_LIST, mArrMenuList);
                        intent.putExtra(Define.ACT_PUT_REQ_USER_LIST, mArrUserList);
                        startActivity(intent);
                        break;

                    case Define.MENU_NAME_FREIGHT_TEMPLATE:
                        intent = new Intent(getActivity().getApplicationContext(), TemplateActivity.class);
                        intent.putExtra(Define.ACT_PUT_REQ_ACT_TITLE, mTitle);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_CODE, mServiceCode);
                        startActivity(intent);
                        break;

                    case Define.MENU_NAME_FREIGHT_MANAGE_LIST:
                        intent = new Intent(getActivity().getApplicationContext(), CargoManagerInfoActivity.class);
                        intent.putExtra(Define.ACT_PUT_REQ_ACT_TITLE, mTitle);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_OID, mServiceOid);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_CODE, mServiceCode);
                        intent.putExtra(Define.ACT_PUT_REQ_SERVICE_URL, mServiceURL);
                        intent.putExtra(Define.ACT_PUT_REQ_USER_LIST, mArrUserList);
                        startActivity(intent);
                        break;

                    case Define.MENU_NAME_STOCK_INFO:
                    case Define.MENU_NAME_IN_OUT_BOUND_INFO:
                    case Define.MENU_NAME_ITEM_INFO:
                        sendIntent = new Intent(Define.SEND_BROADCAST_FLAG);
                        sendIntent.putExtra(Define.SEND_BROADCAST_WEB_FLAG, Define.MENU_WEB_FLAG);
                        sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TYPE, "");
                        sendIntent.putExtra(Define.SEND_BROADCAST_WEB_URL, mMenuUrls[item]+ "?serviceUrl=" + mServiceURL + "&siteCode=" + mServiceCode);
                        sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TITLE, mTitle);
                        getActivity().sendBroadcast(sendIntent);
                        break;
                }
            }
        });

        AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();
    }*/

    private void showProgressDialog() {
        mProgressDialog = new CustomProgressDialog(getActivity());
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}