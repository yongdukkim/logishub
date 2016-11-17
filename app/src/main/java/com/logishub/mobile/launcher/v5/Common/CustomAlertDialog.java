package com.logishub.mobile.launcher.v5.Common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.logishub.mobile.launcher.v5.R;

public class CustomAlertDialog extends Dialog {
    public CustomAlertDialog(final Context context, String message) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getApplicationContext().getSystemService(context.getApplicationContext().LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_alert_dialog, null);
        TextView customTitle = (TextView)view.findViewById(R.id.tv_message);
        customTitle.setText(message);
        customTitle.setTextColor(Color.BLACK);
        ImageView customIcon = (ImageView)view.findViewById(R.id.iv_appIcon);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setNegativeButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}