package com.neosystems.logishubmobile50.Common;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;

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

    /** Base64 EnCode */
    public static String getBase64Encode(String content) {
        return Base64.encodeToString(content.getBytes(), 0);
    }

    /** Base64 DeCode */
    public static String getBase64decode(String content){
        return new String(Base64.decode(content, 0));
    }

}
