package com.neosystems.logishubmobile50.Task;

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

import com.neosystems.logishubmobile50.Common.CustomProgressDialog;
import com.neosystems.logishubmobile50.DATA.VehicleOperationData;
import com.neosystems.logishubmobile50.MainActivity;
import com.neosystems.logishubmobile50.MainFragment;

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
            JSONArray jsonArray = new JSONArray(result);
            MainFragment.mArrVehicleOperationList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                VehicleOperationData Data = new VehicleOperationData();
                JSONObject jObject = jsonArray.getJSONObject(i);

                Data.SetLon(jObject.getString("lon"));
                Data.SetLat(jObject.getString("lat"));
                Data.SetAddr(jObject.getString("addr"));

                MainFragment.mArrVehicleOperationList.add(Data);
                MainFragment.mVehicleOperationListAdapter.notifyDataSetChanged();
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
        mProgressDialog = new CustomProgressDialog(MainActivity.context);
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
