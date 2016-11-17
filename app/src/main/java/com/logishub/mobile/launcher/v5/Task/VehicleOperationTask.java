package com.logishub.mobile.launcher.v5.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedInputStream;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.DATA.VehicleOperationData;
import com.logishub.mobile.launcher.v5.MainActivity;
import com.logishub.mobile.launcher.v5.MainFragment;

import org.json.JSONArray;
import org.json.JSONObject;

public class VehicleOperationTask extends AsyncTask<String, Void, String> {

    private CustomProgressDialog mProgressDialog;

    @Override
    protected void onPreExecute() {
        showProgressDialog();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... urls) {
        return GET(urls[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        try
        {
            if (result != "") {
                JSONArray jsonArray = new JSONArray(result);
                MainFragment.mArrVehicleOperationList.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    VehicleOperationData Data = new VehicleOperationData();
                    JSONObject jObject = jsonArray.getJSONObject(i);

                    Data.SetLon(jObject.getString("ID"));
                    Data.SetLat(jObject.getString("NAME"));
                    Data.SetAddr(jObject.getString("MOBILEPHONE"));

                    MainFragment.mArrVehicleOperationList.add(Data);
                    MainFragment.mVehicleOperationListAdapter.notifyDataSetChanged();
                }
            } else {
                //Toast.makeText(MainActivity.mContext, "데이터가 없습니다.",Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e)
        {
            Log.d("ex", e.getLocalizedMessage());
        }
        finally {
            if(mProgressDialog != null)
                hideProgressDialog();
        }
    }

    public static String GET(String urlString) {
        InputStream inputStream = null;
        String result = "";
        try
        {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Empty";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    private void showProgressDialog() {
        mProgressDialog = new CustomProgressDialog(MainActivity.mContext);
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
