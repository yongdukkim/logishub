package com.neosystems.logishubmobile50;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewFragment extends Fragment {
    public WebView mWebView;
    public View mView;
    public String mUrl;

    public WebViewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_logishubweb, container, false);

        Bundle extra = getArguments();
        mUrl = extra.getString("url");

        setLayout(mView);

        return mView;
    }

    private void setLayout(View v){
        mWebView = (WebView) v.findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClientClass());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setDomStorageEnabled(true);
        //String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        //mWebView.getSettings().setAppCachePath(appCachePath);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if( keyCode == KeyEvent.KEYCODE_BACK ) {
                    mWebView.goBack();
                    return true;
                } else {
                    return false;
                }
            }
        });
        mWebView.loadUrl(mUrl);
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}