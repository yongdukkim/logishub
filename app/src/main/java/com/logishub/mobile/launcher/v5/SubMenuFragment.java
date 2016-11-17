package com.logishub.mobile.launcher.v5;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.DATA.MenuData;
import com.logishub.mobile.launcher.v5.DATA.SubMenuAdapter;

import java.util.ArrayList;

public class SubMenuFragment extends Fragment {
    public View mView;
    GridView mMenuGridView;
    GridView mServiceGridView;
    SubMenuAdapter mSubMenuAdapter;
    private CustomProgressDialog mProgressDialog;
    private String mMenuID;
    private ArrayList<String> mMenuList;
    private ArrayList<Integer> mMenuImageList;
    private ArrayList<String> mMenuUrlList;

    public static String [] mMenuNames;
    public static Integer [] mMenuImages;
    public static String [] mMenuUrls;

    public static ArrayList<MenuData> mArrMenuList = null;

    public SubMenuFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_submenu, container, false);

        mArrMenuList = new ArrayList<>();

        Bundle extra = getArguments();
        mMenuID = extra.getString(Define.FRG_PUT_REQ_MENU_ID);

        mArrMenuList = ((MainActivity)getActivity()).mArrMenuList;

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
        mMenuList = null;
        mMenuImageList = null;
        mMenuUrlList = null;
        mMenuNames = null;
        mMenuImages = null;
        mMenuUrls = null;
        mArrMenuList = null;

        hideProgressDialog();
    }

    private void setLayout(View v) {
        mMenuList = new ArrayList<>();
        mMenuImageList = new ArrayList<>();
        mMenuUrlList = new ArrayList<>();

        try {
            for (int i = 0; i < mArrMenuList.size(); i++) {
                if (mArrMenuList.get(i).getName() != null && mArrMenuList.get(i).getLevel().equals(Define.MENU_LEVEL2) && mArrMenuList.get(i).getParentId().equals(mMenuID)) {
                    mMenuList.add(mArrMenuList.get(i).getName());
                    mMenuUrlList.add(mArrMenuList.get(i).getClassName());
                    switch (mArrMenuList.get(i).getName()) {

                        case Define.MENU_NAME_CARGO_INFO:
                            mMenuImageList.add(R.drawable.ic_menu_cargoinfo);
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

                        case Define.MENU_NAME_ADMIN:
                            mMenuImageList.add(R.drawable.ic_menu_admin);
                            break;

                        default:
                            mMenuImageList.add(R.drawable.ic_notification_s);
                            break;
                    }
                }
            }

        } catch (Exception e) { }

        mMenuNames = mMenuList.toArray(new String[mMenuList.size()]);
        mMenuImages = mMenuImageList.toArray(new Integer[mMenuImageList.size()]);
        mMenuUrls = mMenuUrlList.toArray(new String[mMenuUrlList.size()]);

        mMenuGridView = (GridView) v.findViewById(R.id.gv_sub_menu);
        mSubMenuAdapter = new SubMenuAdapter(getActivity(), mMenuImages, mMenuNames, "MENU");
        mMenuGridView.setAdapter(mSubMenuAdapter);

        mMenuGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent sendIntent = new Intent(Define.SEND_BROADCAST_FLAG);
                sendIntent.putExtra(Define.SEND_BROADCAST_WEB_FLAG, Define.MENU_WEB_FLAG);
                sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TYPE, Define.MENU_WEB_MENU_DEFAULT_TYPE);
                sendIntent.putExtra(Define.SEND_BROADCAST_WEB_URL, mMenuUrls[position]);
                sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TITLE, mMenuNames[position]);
                getActivity().sendBroadcast(sendIntent);
            }
        });

        hideProgressDialog();
    }

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