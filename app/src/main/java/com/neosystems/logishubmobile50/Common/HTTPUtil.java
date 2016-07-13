package com.neosystems.logishubmobile50.Common;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;

public class HTTPUtil {
    public static String generateParameters(Hashtable<String, String> params, String charset) throws Exception {
        StringBuilder sb = new StringBuilder();

        Enumeration<String> e = params.keys();

        while(e.hasMoreElements()) {
            String key = (String)e.nextElement();

            String param = key + "=" + URLEncoder.encode(params.get(key), charset);
            sb.append(param + "&");
        }

        if (sb.toString().endsWith("&"))
            sb.setLength(sb.length() - 1);

        return sb.toString();
    }

    public static void doPost(String url, Hashtable<String, String> params, String charset) throws Exception {
        StringBuilder sb = new StringBuilder();

        Enumeration<String> e = params.keys();

        while(e.hasMoreElements()) {
            String key = (String)e.nextElement();

            String param = key + "=" + URLEncoder.encode(params.get(key), charset);
            sb.append(param + "&");
        }

        if (sb.toString().endsWith("&"))
            sb.setLength(sb.length() - 1);

        HttpURLConnection conn = null;
        OutputStream out = null;

        try {
            conn = (HttpURLConnection)(new URL(url).openConnection());
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept-Charset", charset);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            out = conn.getOutputStream();
            out.write(sb.toString().getBytes(charset));
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }

            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        String url = "http://122.199.182.27:8080/KNL/GeoLocationLogger.ashx";
        Hashtable<String, String> params = new Hashtable<String, String>();

        params.put("id", "ID");
        params.put("provider", "Browser");
        params.put("lat", "11");
        params.put("lng", "22");
        params.put("accuracy", "40");
        params.put("enabledGPS", "1");
        params.put("isReadyGPS", "1");

        HTTPUtil.doPost(url, params, "UTF-8");
    }

}