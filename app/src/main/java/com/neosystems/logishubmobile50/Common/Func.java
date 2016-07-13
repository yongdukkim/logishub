package com.neosystems.logishubmobile50.Common;

import android.app.ProgressDialog;
import android.content.Context;

public class Func {

    public static ProgressDialog onCreateProgressDialog(Context context)
    {
        ProgressDialog progDialog = new ProgressDialog(context)
        {
            @Override
            public boolean onSearchRequested() {
                return false;
            }
        };

        progDialog.setCancelable(false);

        return progDialog;
    }

}
