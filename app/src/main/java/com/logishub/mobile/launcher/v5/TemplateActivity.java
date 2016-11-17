package com.logishub.mobile.launcher.v5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.DATA.FnsTemplateData;
import com.logishub.mobile.launcher.v5.DB.FnsOrderAdapter;

import java.util.ArrayList;

public class TemplateActivity extends AppCompatActivity {
    private String mTitle;
    private String mSiteCode;
    private FnsOrderAdapter mTemplateOrderDb = null;
    private ExpandableListView mTemplateList;
    private TemplateListAdapter mTemplateListAdapter = null;
    private ArrayList<FnsTemplateData> mArrTemplateList = null;
    private TextView tvNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templete);

        onOpenDB();

        setLayout();
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

        mTitle = null;
        mTemplateListAdapter = null;
        mArrTemplateList = null;
        mTemplateList = null;
        tvNoData = null;

        if (mTemplateOrderDb != null)
        {
            mTemplateOrderDb.close();
            mTemplateOrderDb = null;
        }
    }

    /** SQL Lite Open & Data Init */
    private void onOpenDB() {
        mTemplateOrderDb = new FnsOrderAdapter(this);
        mTemplateOrderDb.open();
    }

    private void setLayout() {

        Intent intent = getIntent();
        mTitle = intent.getExtras().getString(Define.ACT_PUT_REQ_ACT_TITLE);
        mSiteCode = intent.getExtras().getString(Define.ACT_PUT_REQ_SERVICE_CODE);

        mArrTemplateList = new ArrayList<>();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.actionbar_sub);
            View v = getSupportActionBar().getCustomView();
            TextView titleTxtView = (TextView) v.findViewById(R.id.tv_actionbar_title);
            titleTxtView.setText(mTitle);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        mTemplateList = (ExpandableListView) findViewById(R.id.eplv_templete_list);
        mTemplateListAdapter = new TemplateListAdapter(this, R.layout.freightlist_list, mArrTemplateList, mArrTemplateList);
        mTemplateList.setAdapter(mTemplateListAdapter);
        mTemplateList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int groupCount = mTemplateListAdapter.getGroupCount();

                for (int i = 0; i < groupCount; i++) {
                    if (!(i == groupPosition))
                        mTemplateList.collapseGroup(i);
                }
            }
        });
        mTemplateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                onListItemClick(parent, v, position, id);
            }
        });

        onSetTemplateData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onDeleteTemplateData(String mRowOid) {
        mTemplateOrderDb.DeleteTemplateData(Long.valueOf(mRowOid));
        onSetTemplateData();
    }

    private void onSetTemplateData() {
        try {
            mArrTemplateList.clear();
            mArrTemplateList.addAll(mTemplateOrderDb.GetFnsTemplateData(mSiteCode));
            mTemplateListAdapter.notifyDataSetChanged();

            onRefreshLayout();
            onCloseGroup();
        } catch (Exception ex) { }
    }

    private void onRefreshLayout() {
        if (mArrTemplateList.size() > 0) {
            tvNoData.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    private void onCloseGroup() {
        int groupCount = mTemplateListAdapter.getGroupCount();

        for (int i = 0; i < groupCount; i++) {
            mTemplateList.collapseGroup(i);
        }
    }

    protected void onListItemClick(AdapterView<?> parent, View v, int position, long id) { }

    public class TemplateListAdapter extends BaseExpandableListAdapter {
        private ArrayList<FnsTemplateData> itemList;
        private ArrayList<FnsTemplateData> subList;
        private Context context;
        private int rowResourceId;

        public TemplateListAdapter(Context context, int textViewResourceId, ArrayList<FnsTemplateData> itemList, ArrayList<FnsTemplateData> subList) {
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
            return itemList.get(groupPosition).getRowOid();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            View v = convertView;

            FnsTemplateData item = itemList.get(groupPosition);
            if(item != null)
            {
                v = SetTemplateList(context, v, this.rowResourceId, item);
            }

            return v;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public String getChild(int groupPosition, int childPosition) {
            return subList.get(groupPosition).getRowOid();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        // 차일드뷰 각각의 ROW
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            View v = convertView;

            FnsTemplateData item = subList.get(groupPosition);
            if(item != null)
            {
                v = SetTemplateSubList(context, v, this.rowResourceId, item);
            }

            return v;
        }

        @Override
        public boolean hasStableIds() { return true; }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
    }

    private View SetTemplateList(Context context, View v, int rowResourceId, FnsTemplateData Data)
    {
        try {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.freightlist_list, null);

            TextView tvFreightInfo = (TextView) v.findViewById(R.id.tv_freight_info);
            TextView tvFreightPrice = (TextView) v.findViewById(R.id.tv_freight_price);
            TextView tvFreightTon = (TextView) v.findViewById(R.id.tv_freight_ton);
            TextView tvFreightPickupArea = (TextView) v.findViewById(R.id.tv_freight_pickup_area);
            TextView tvFreightDeliveryArea = (TextView) v.findViewById(R.id.tv_freight_delivery_area);

            tvFreightInfo.setVisibility(View.GONE);
            tvFreightPrice.setVisibility(View.GONE);
            tvFreightTon.setText(Data.getTonName());
            tvFreightPickupArea.setText(Data.getPickupAreaName());
            tvFreightDeliveryArea.setText(Data.getAreaName());

        } catch (Exception ex) { }

        return v;
    }

    private View SetTemplateSubList(Context context, View v, int rowResourceId, final FnsTemplateData Data)
    {
        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.freightlist_sublist, null);

        TextView tvPickupType = (TextView) v.findViewById(R.id.tv_pickup_type);
        TextView tvDeliveryType = (TextView) v.findViewById(R.id.tv_delivery_type);
        TextView tvItemKindType = (TextView) v.findViewById(R.id.tv_item_kind_type);
        TextView tvVehicleType = (TextView) v.findViewById(R.id.tv_vehicle_type);
        TextView tvCustomerOrderType = (TextView) v.findViewById(R.id.tv_customer_order_type);
        TextView tvPaymentType = (TextView) v.findViewById(R.id.tv_payment_type);
        TextView tvRemark = (TextView) v.findViewById(R.id.tv_remark);
        Button btnCancel = (Button) v.findViewById(R.id.btn_cancel);
        btnCancel.setVisibility(View.GONE);
        Button btnModify = (Button) v.findViewById(R.id.btn_modify);
        btnModify.setVisibility(View.GONE);
        Button btnDelete = (Button) v.findViewById(R.id.btn_delete);
        btnDelete.setVisibility(View.VISIBLE);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteTemplateData(Data.getRowOid());
            }
        });
        Button btnReRegistration = (Button) v.findViewById(R.id.btn_reregistration);
        btnReRegistration.setVisibility(View.GONE);
        Button btnTemplate = (Button) v.findViewById(R.id.btn_templete);
        btnTemplate.setVisibility(View.GONE);

        if (!Data.getPickupTypeName().equals("")) {
            tvPickupType.setText(Data.getPickupTypeName());
            tvPickupType.setVisibility(View.VISIBLE);
        }

        if (!Data.getDeliveryTimeTypeName().equals("")) {
            tvDeliveryType.setText(Data.getDeliveryTimeTypeName());
            tvDeliveryType.setVisibility(View.VISIBLE);
        }

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

        return v;
    }
}