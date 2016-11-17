package com.logishub.mobile.launcher.v5;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.logishub.mobile.launcher.v5.Common.CustomAlertDialog;
import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;

import java.net.URISyntaxException;
import java.util.List;

public class WebViewFragment extends Fragment {
    public static WebView mWebView;
    public View mView;
    public String mUrl;
    private CustomProgressDialog mProgressDialog;

    public WebViewFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_logishubweb, container, false);

        Bundle extra = getArguments();
        mUrl = extra.getString(Define.FRG_PUT_REQ_URL);

        showProgressDialog();
        setLayout(mView);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        /*try {
            mWebView.resumeTimers();
        } catch (Exception ex) {}*/
    }

    @Override
    public void onPause() {
        super.onPause();

        /*try {
            mWebView.pauseTimers();
        } catch (Exception ex) {}*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setLayout(View v){
        mWebView = (WebView) v.findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClientClass());
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                hideProgressDialog();
                Log.e("web", consoleMessage.message() + '\n' + consoleMessage.messageLevel() + '\n' + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }
        });
        mWebView.setBackgroundColor(0x00000000);
        mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setSaveFormData(false);
        String appCachePath = getActivity().getApplicationContext().getCacheDir().getAbsolutePath();
        mWebView.getSettings().setAppCachePath(appCachePath);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mWebView != null) {
                        if (mWebView.canGoBack()) {
                            mWebView.goBack();
                        } else {
                            getActivity().onBackPressed();
                        }
                    }

                    return true;
                }

                return false;
            }
        });

        mWebView.loadUrl(mUrl);
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            try {
                /** 현대카드 check */
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                if(intent.getDataString().startsWith("droidxantivirusweb"))
                {
                    Intent hydVIntent = new Intent(Intent.ACTION_VIEW);
                    hydVIntent.setData(Uri.parse("market://search?q=net.nshc.droidxantivirus"));
                    startActivity(hydVIntent);
                }

                if (isInstalledApp(getActivity(), "net.nshc.droidx3web"))
                {
                    if(url.startsWith("droidx3web"))
                    {
                        Uri uri = Uri.parse(intent.getDataString());
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }
                else
                {
                    if(intent.getDataString().startsWith("droidx3web"))
                    {
                        Intent hydVIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        hydVIntent.setData(Uri.parse("market://details?id=net.nshc.droidx3web"));
                        startActivity(hydVIntent);
                    }
                }

                if(url.startsWith("vguardstart://"))
                {
                    Uri uri = Uri.parse(intent.getDataString());
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (url != null && (url.startsWith("intent://") || url.startsWith("intent:")))
            {
                try
                {
                    Intent intent2 = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (isInstalledApp(getActivity(), intent2.getPackage()))
                    {
                        startActivity(intent2);
                    }
                    else
                    {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        marketIntent.setData(Uri.parse("market://details?id="+intent2.getPackage()));
                        startActivity(marketIntent);
                    }
                    return true;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (url != null && url.startsWith("vguardstart://"))
            {
                return true;
            }
            else if (url != null && url.startsWith("vguardcheck://"))
            {
                return true;
            }
            else if (url != null && url.startsWith("vguardend://"))
            {
                return true;
            }
            else if (url != null && url.startsWith("ahnlabv3mobileplus:"))
            {
                return true;
            }
            else if (url != null && url.startsWith("javascript:"))
            {
                return false;
            }
            else if (url != null && url.startsWith("droidxantivirusweb"))
            {
                return true;
            }
            else if (url != null && url.startsWith("droidx3web://"))
            {
                return true;
            }
            else if (url != null && url.startsWith("hdcardappcardansimclick://"))
            {
                return true;
            }
            else if (url != null && url.startsWith("shinhan-sr-ansimclick://"))
            {
                return true;
            }
            else if (url != null && url.startsWith("mpocket.online.ansimclick://"))
            {
                return true;
            }
            else if (url != null && url.startsWith("lotteappcard://"))
            {
                return true;
            }
            else if (url != null && url.startsWith("kb-acp://"))
            {
                return true;
            }
            else if (url != null && url.startsWith("hanaansim://"))
            {
                return true;
            }
            else if (url != null && url.startsWith("market://"))
            {
                try
                {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    if (intent != null)
                    {
                        startActivity(intent);
                    }
                    return true;
                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                }
            }
            else if (url != null && (url.startsWith("sms:") || url.startsWith("smsto:") || url.startsWith("mms:") || url.startsWith("mmsto:"))) {
                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(i);
                return true;
            }
            else if (url != null && url.startsWith("tel:")) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(i);
                return true;
            }
            else if (url.contains("type=logout")) {
                Intent sendIntent = new Intent(Define.SEND_BROADCAST_FLAG);
                sendIntent.putExtra(Define.SEND_BROADCAST_WEB_FLAG, Define.LOGOUT_FLAG);
                sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TYPE, "");
                sendIntent.putExtra(Define.SEND_BROADCAST_WEB_URL, "");
                sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TITLE, "");
                getActivity().sendBroadcast(sendIntent);
            }
            else if (url.contains("type=cancel")) {
                getActivity().onBackPressed();
            }

            view.loadUrl(url);
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            hideProgressDialog();
            if (url.toUpperCase().contains("PORTALDISPATCHERLIST.HTML") || url.toUpperCase().contains("TCALCULATE2.HTML") || url.toUpperCase().contains("TAXORDERCOMPLETE.HTML") || url.toUpperCase().contains("PREFERENCECOMMUNITYLIST.HTML")
                    || url.toUpperCase().contains("PORTALCOMMUNITYNOTICELIST.HTML") || url.toUpperCase().contains("RECRUITLIST.HTML") || url.toUpperCase().contains("USERGUIDE.HTML") || url.toUpperCase().contains("MEMBERUPDATEINFO.HTML")
                    || url.toUpperCase().contains("MEMBERUPDATEPASS.HTML") || url.toUpperCase().contains("LEGAL.HTML") || url.toUpperCase().contains("WITHDRAWAL.HTML") || url.toUpperCase().contains("SERVICEMENU.HTML")
                    || url.toUpperCase().contains("COMMUNITY.HTML") || url.toUpperCase().contains("ADMINMENU.HTML"))
            {
                view.clearHistory();
            }

            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            switch(errorCode) {
                case ERROR_AUTHENTICATION:
                case ERROR_BAD_URL:
                case ERROR_CONNECT:
                case ERROR_FAILED_SSL_HANDSHAKE:
                case ERROR_FILE:
                case ERROR_FILE_NOT_FOUND:
                case ERROR_HOST_LOOKUP:
                case ERROR_IO:
                case ERROR_PROXY_AUTHENTICATION:
                case ERROR_REDIRECT_LOOP:
                case ERROR_TIMEOUT:
                case ERROR_TOO_MANY_REQUESTS:
                case ERROR_UNKNOWN:
                case ERROR_UNSUPPORTED_AUTH_SCHEME:
                case ERROR_UNSUPPORTED_SCHEME:

                    view.loadUrl("about:blank");
                    new CustomAlertDialog(getActivity(), "알림" + "\n\n네트워크 연결이 원활하지 않습니다.\n연결 설정을 확인 하세요.");

                break;
            }
        }
    }

    /** package name check */
    private static boolean isInstalledApp(Context context, String packageName) {
        List<ApplicationInfo> appList = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : appList) {
            if (appInfo.packageName.equals(packageName)) {
                return true;
            }
        }

        return false;
    }

    private void showProgressDialog() {
        mProgressDialog = new CustomProgressDialog(getActivity());
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}