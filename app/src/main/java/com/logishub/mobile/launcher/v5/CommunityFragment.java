package com.logishub.mobile.launcher.v5;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.DATA.CommunityListData;

import java.util.ArrayList;

public class CommunityFragment extends Fragment {
    public View mView;
    public static ArrayList<CommunityListData> mArrCommunityList = null;
    public static CommunityListAdapter mCommunityListAdapter = null;
    public ListView mCommunityList;
    private CustomProgressDialog mProgressDialog;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView tvNoData;

    public CommunityFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_community, container, false);

        mArrCommunityList = new ArrayList<CommunityListData>();
        mArrCommunityList = ((MainActivity)getActivity()).mArrCommunityList;

        showProgressDialog();

        setLayout(mView);

        mCommunityListAdapter.notifyDataSetChanged();

        hideProgressDialog();

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
        mArrCommunityList = null;
        mCommunityListAdapter = null;
        mCommunityList = null;

        hideProgressDialog();
    }

    private void setLayout(View v) {
        mCommunityList = (ListView) v.findViewById(R.id.iv_community_list);
        tvNoData = (TextView) v.findViewById(R.id.tv_no_data);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mCommunityListAdapter = new CommunityListAdapter(getActivity(), R.layout.communitylist_list, mArrCommunityList);
        mCommunityList.setAdapter(mCommunityListAdapter);
        mCommunityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                onListItemClick(parent, v, position, id);
            }
        });

        onRefreshLayout();

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mArrCommunityList = ((MainActivity)getActivity()).mArrCommunityList;
                mCommunityListAdapter.notifyDataSetChanged();
                onRefreshLayout();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void onRefreshLayout() {
        if (mArrCommunityList.size() > 0) {
            tvNoData.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    /** List Adapter */
    public class CommunityListAdapter extends ArrayAdapter<CommunityListData>
    {
        private ArrayList<CommunityListData> itemList;
        private Context context;
        private int rowResourceId;

        public CommunityListAdapter(Context context, int textViewResourceId, ArrayList<CommunityListData> itemList)
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

            CommunityListData item = itemList.get(position);

            if(item != null)
            {
                v = SetVehicleOperationList(context, v, this.rowResourceId, item);
            }

            return v;
        }
    }

    private View SetVehicleOperationList(Context context, View v, int rowResourceId, CommunityListData Data)
    {
        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.communitylist_list, null);

        TextView tvCommunityTitle = (TextView) v.findViewById(R.id.tv_community_title);
        TextView tvCommunityDetail = (TextView) v.findViewById(R.id.tv_community_detail);
        TextView tvDuration = (TextView) v.findViewById(R.id.tv_duration);

        String mCreated = Data.getCreated();

        if (!mCreated.equals("")) {
            mCreated = Data.getCreated().substring(2, 10);
        }

        tvCommunityTitle.setText(Func.checkStringNull(Data.getCommunityName()));
        tvCommunityDetail.setText(Func.checkStringNull(Data.getLeader()) + " / " + "회원수 " + Func.checkStringNull(Data.getMemberCount()));
        tvDuration.setText(Func.checkStringNull(mCreated));

        return v;
    }

    protected void onListItemClick(AdapterView<?> parent, View v, int position, long id)
    {
        try {
            if (mArrCommunityList.size() > 0) {
                Intent sendIntent = new Intent(Define.SEND_BROADCAST_FLAG);
                sendIntent.putExtra(Define.SEND_BROADCAST_WEB_FLAG, Define.MENU_WEB_FLAG);
                sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TYPE, Define.MENU_WEB_MENU_COMMUNITY_TYPE);
                sendIntent.putExtra(Define.SEND_BROADCAST_WEB_URL, Define.LOGISHUB_WEB_COMMUNITY + "?leaderOid=" + mArrCommunityList.get(position).getLeaderOid() + "&communityLevel=" + mArrCommunityList.get(position).getCommunityLevel() + "&oid=" + mArrCommunityList.get(position).getOid());
                sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TITLE, mArrCommunityList.get(position).getCommunityName());
                getActivity().sendBroadcast(sendIntent);
            }

        } catch (Exception ex) {}
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