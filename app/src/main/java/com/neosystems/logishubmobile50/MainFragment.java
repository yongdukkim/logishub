package com.neosystems.logishubmobile50;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.neosystems.logishubmobile50.Common.CustomProgressDialog;
import com.neosystems.logishubmobile50.Common.Define;
import com.neosystems.logishubmobile50.DATA.VehicleOperationData;
import com.neosystems.logishubmobile50.Task.VehicleOperationTask;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    public View mView;
    public static ArrayList<VehicleOperationData> mArrVehicleOperationList = null;
    public static VehicleOperationListAdapter mVehicleOperationListAdapter = null;
    public ListView mlvVehicleOperation;
    private CustomProgressDialog mProgressDialog;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);

        setLayout(mView);

        new VehicleOperationTask().execute(Define.LOGISHUB_DEFAULT_URL + Define.LOGISHUB_USER);

        return mView;
    }

    private void setLayout(View v) {
        mArrVehicleOperationList = new ArrayList<VehicleOperationData>();
        mlvVehicleOperation = (ListView) v.findViewById(R.id.lvVehicleOperation);
        mVehicleOperationListAdapter = new VehicleOperationListAdapter(getActivity(), R.layout.vehicleoperationlist_list, mArrVehicleOperationList);
        mlvVehicleOperation.setAdapter(mVehicleOperationListAdapter);
    }

    /** List Adapter */
    public class VehicleOperationListAdapter extends ArrayAdapter<VehicleOperationData>
    {
        private ArrayList<VehicleOperationData> itemList;
        private Context context;
        private int rowResourceId;

        public VehicleOperationListAdapter(Context context, int textViewResourceId, ArrayList<VehicleOperationData> itemList)
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

            VehicleOperationData item = itemList.get(position);

            if(item != null)
            {
                v = SetVehicleOperationList(context, v, this.rowResourceId, item);
            }

            return v;
        }
    }

    private View SetVehicleOperationList(Context context, View v, int rowResourceId, VehicleOperationData Data)
    {
        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.vehicleoperationlist_list, null);

        TextView tvLon = (TextView) v.findViewById(R.id.tvLon);
        TextView tvLat = (TextView) v.findViewById(R.id.tvLat);
        TextView tvAddr = (TextView) v.findViewById(R.id.tvAddr);

        v.setBackgroundColor(Color.rgb(247, 247, 247));

        tvLon.setText(Html.fromHtml(""+ Data.GetLon() + ""));
        tvLat.setText(Html.fromHtml(""+ Data.GetLat() + ""));
        tvAddr.setText(Html.fromHtml(""+ Data.GetAddr() + ""));

        return v;
    }
}