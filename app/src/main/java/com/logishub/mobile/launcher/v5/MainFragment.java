package com.logishub.mobile.launcher.v5;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.DATA.VehicleOperationData;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public View mView;
    public static ArrayList<VehicleOperationData> mArrVehicleOperationList = null;
    public static VehicleOperationListAdapter mVehicleOperationListAdapter = null;
    private AlertDialog.Builder alertdialogbuilder;
    private String[] AlertDialogItems = new String[]{ "서비스명1", "서비스명2", "서비스명3", "서비스명4", "서비스명5", "서비스명6", "서비스명7", "서비스명8", "서비스명9", "서비스명10", "서비스명11", "서비스명12", "서비스명13" };
    private List<String> ItemsIntoList;
    private boolean[] Selectedtruefalse = new boolean[]{ false, false, false, false, false, false, false, false, false, false, false, false, false };

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);

        setLayout(mView);

        return mView;
    }

    private void setLayout(View v) {
        v.findViewById(R.id.btn_service).setOnClickListener(this);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String secondString = second < 10 ? "0"+second : ""+second;
        String time = "time: "+hourString+"h"+minuteString+"m"+secondString+"s";
        Toast.makeText(getActivity(), time, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "date: "+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        Toast.makeText(getActivity(), date, Toast.LENGTH_LONG).show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_service:
                //onSetService();
                //onDatePicker();
                onTimePicker();
                break;
        }
    }

    private void onDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dpd.setThemeDark(true);
        dpd.vibrate(true);
        dpd.dismissOnPause(true);
        dpd.showYearPickerFirst(false);
        if (true) {
            dpd.setAccentColor(Color.parseColor("#04a2bc"));
        }
        if (true) {
            dpd.setTitle("날짜 설정");
        }
        if (false) {
            Calendar[] dates = new Calendar[13];
            for (int i = -6; i <= 6; i++) {
                Calendar date = Calendar.getInstance();
                date.add(Calendar.MONTH, i);
                dates[i+6] = date;
            }
            dpd.setSelectableDays(dates);
        }
        if (false) {
            Calendar[] dates = new Calendar[13];
            for (int i = -6; i <= 6; i++) {
                Calendar date = Calendar.getInstance();
                date.add(Calendar.WEEK_OF_YEAR, i);
                dates[i+6] = date;
            }
            dpd.setHighlightedDays(dates);
        }
        if (false) {
            Calendar[] dates = new Calendar[3];
            for (int i = -1; i <= 1; i++) {
                Calendar date = Calendar.getInstance();
                date.add(Calendar.DAY_OF_MONTH, i);
                dates[i+1] = date;
            }
            dpd.setDisabledDays(dates);
        }
        dpd.show(getActivity().getFragmentManager(), "Datepicker");
    }

    private void onTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(this, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
        tpd.setThemeDark(true);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
        tpd.enableSeconds(true);
        tpd.enableMinutes(true);
        if (true) {
            tpd.setAccentColor(Color.parseColor("#04a2bc"));
        }
        if (true) {
            tpd.setTitle("시간 설정");
        }
        if (true) {
            tpd.setTimeInterval(Define.TIME_PICKER_INTERVAL_HOUR, Define.TIMEPICKER_INTERVAL_MINUTE, Define.TIMEPICKER_INTERVAL_SECONDS);
        }
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });
        tpd.show(getActivity().getFragmentManager(), "Timepicker");
    }

    private void onSetService() {
        alertdialogbuilder = new AlertDialog.Builder(getActivity());
        ItemsIntoList = Arrays.asList(AlertDialogItems);
        alertdialogbuilder.setMultiChoiceItems(AlertDialogItems, Selectedtruefalse, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });

        alertdialogbuilder.setCancelable(false);
        alertdialogbuilder.setTitle("게시대상");
        alertdialogbuilder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int a = 0;
                while(a < Selectedtruefalse.length)
                {
                    boolean value = Selectedtruefalse[a];

                    if(value){
                        Toast.makeText(getActivity(), ItemsIntoList.get(a), Toast.LENGTH_LONG).show();
                    }
                    a++;
                }
            }
        });

        alertdialogbuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = alertdialogbuilder.create();
        dialog.show();
    }
}