package com.neosystems.logishubmobile50.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedInputStream;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.neosystems.logishubmobile50.Common.Func;
import com.neosystems.logishubmobile50.MainActivity;

import org.json.JSONObject;

public class VehicleOperationTask extends AsyncTask<String, Void, String> {

    private ProgressDialog m_progDialog = null;

    @Override
    protected void onPreExecute() {
        m_progDialog = Func.onCreateProgressDialog(MainActivity.context);
        m_progDialog.setMessage("Loading...");
        m_progDialog.show();

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
            //JSONObject json = new JSONObject(result);
            MainActivity.etResponse.setText(result);
        }
        catch (Exception e)
        {
            Log.d("ex", e.getLocalizedMessage());
        }
        finally {
            if(m_progDialog != null)
                m_progDialog.dismiss();
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
}
