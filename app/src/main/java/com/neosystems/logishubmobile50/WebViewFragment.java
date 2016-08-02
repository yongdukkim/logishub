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
import android.widget.Toast;

import com.neosystems.logishubmobile50.Common.Define;

public class WebViewFragment extends Fragment {
    public static WebView mWebView;
    public View mView;
    public String mUrl;

    public WebViewFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_logishubweb, container, false);

        Bundle extra = getArguments();
        mUrl = extra.getString(Define.FRG_PUT_REQ_URL);

        setLayout(mView);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mWebView.resumeTimers();
    }

    @Override
    public void onPause() {
        super.onPause();

        mWebView.pauseTimers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mWebView = null;
    }

    private void setLayout(View v){
        mWebView = (WebView) v.findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClientClass());
        mWebView.setWebChromeClient(new WebChromeClient());
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

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        /** Loading Complete */
        @Override
        public void onPageFinished(WebView view, String url) {
            Toast.makeText(getActivity(), "Web Load Complete", Toast.LENGTH_LONG).show();
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            switch(errorCode) {
                case ERROR_AUTHENTICATION: break;               // 서버에서 사용자 인증 실패
                case ERROR_BAD_URL: break;                           // 잘못된 URL
                case ERROR_CONNECT: break;                          // 서버로 연결 실패
                case ERROR_FAILED_SSL_HANDSHAKE: break;    // SSL handshake 수행 실패
                case ERROR_FILE: break;                                  // 일반 파일 오류
                case ERROR_FILE_NOT_FOUND: break;               // 파일을 찾을 수 없습니다
                case ERROR_HOST_LOOKUP: break;           // 서버 또는 프록시 호스트 이름 조회 실패
                case ERROR_IO: break;                              // 서버에서 읽거나 서버로 쓰기 실패
                case ERROR_PROXY_AUTHENTICATION: break;   // 프록시에서 사용자 인증 실패
                case ERROR_REDIRECT_LOOP: break;               // 너무 많은 리디렉션
                case ERROR_TIMEOUT: break;                          // 연결 시간 초과
                case ERROR_TOO_MANY_REQUESTS: break;     // 페이지 로드중 너무 많은 요청 발생
                case ERROR_UNKNOWN: break;                        // 일반 오류
                case ERROR_UNSUPPORTED_AUTH_SCHEME: break; // 지원되지 않는 인증 체계
                case ERROR_UNSUPPORTED_SCHEME: break;          // URI가 지원되지 않는 방식
            }
        }
    }
}