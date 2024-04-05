package com.bytedesk.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bytedesk.ui.R;
import com.bytedesk.ui.util.BDUiConstant;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

public class BrowserActivity extends AppCompatActivity {

    private QMUITopBarLayout mTopBar;
    private WebView mWebView;
    //
    private String mUrl;
    private String mTitle;

    //Android 中Webview 自适应屏幕
    //http://www.cnblogs.com/bluestorm/archive/2013/04/15/3021996.html

    private static ProgressDialog m_progressDialog = null;

    private ValueCallback<Uri>   mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessageForAndroid5;

    private final static int FILECHOOSER_RESULTCODE=1;
    private final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_browser);

        mUrl = getIntent().getStringExtra(BDUiConstant.EXTRA_URL);
        mTitle = getIntent().getStringExtra(BDUiConstant.EXTRA_TITLE);

        mWebView = findViewById(R.id.bytedesk_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(true);//Zoom Control on web (You don't need this
        //if ROM supports Multi-Touch
        mWebView.getSettings().setBuiltInZoomControls(true); //Enable Multitouch if supported by ROM
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        //
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setDatabaseEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setGeolocationEnabled(true);
        mWebView.getSettings().setNeedInitialFocus(true);
        mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " HDM-PBS/1.0");
        //
        mWebView.setDownloadListener(new MyWebViewDownLoadListener());
        mWebView.setInitialScale(25);//为25%，最小缩放等级
        mWebView.setWebViewClient(new SampleWebViewClient());
        mWebView.setWebChromeClient(myWebChromeClient);

        mWebView.loadUrl(mUrl);

        //
        mTopBar = findViewById(R.id.bytedesk_browser_topbarlayout);
        mTopBar.setTitle(mTitle);
        mTopBar.addLeftBackImageButton().setOnClickListener(view -> finish());
        QMUIStatusBarHelper.translucent(this);
    }

    /**
     * http://stackoverflow.com/questions/5907369/file-upload-in-webview
     * http://blog.csdn.net/zhtsuc/article/details/49154099
     */
    private final WebChromeClient myWebChromeClient = new WebChromeClient() {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                //mPullRefreshWebView.onRefreshComplete();
            }
        }

        //扩展支持alert事件
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Logger.i("onJsAlert url: {}, message: {}", url, message);
            return true;
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooserImpl(uploadMsg);
        }

        // For Android 3.0+
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            openFileChooserImpl(uploadMsg);
        }

        //For Android 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooserImpl(uploadMsg);
        }

        // For Android 5.0+
        public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> uploadMsg,
                                          WebChromeClient.FileChooserParams fileChooserParams) {
            openFileChooserImplForAndroid5(uploadMsg);
            return true;
        }

    };

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        //只从图库和文件中选择图片
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "相册"), FILECHOOSER_RESULTCODE);
        //暂时不需要拍照上传，代码先注释掉
        //startActivityForResult(createDefaultOpenableIntent(), mActivity.FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "相册");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null: intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5){
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null: intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }


    /**
     * 清除cookies
     */
    private void clearCookies() {
        try {
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieManager.setAcceptCookie(true);
        } catch (Exception e) {
//           e.printStackTrace();
            Logger.d(e.toString());
        }
    }

    private static class SampleWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view,String url) {
//            m_progressDialog.dismiss();
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Logger.d("url="+url);
            Logger.d("userAgent="+userAgent);
            Logger.d("contentDisposition="+contentDisposition);
            Logger.d("mimetype="+mimetype);
            Logger.d("contentLength="+contentLength);


            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //主动回收
        Runtime.getRuntime().gc();
    }

    // Sets the Chrome Client, and defines the onProgressChanged
    // This makes the Progress bar be updated.
	/*
    final Activity MyActivity = this;
    mWebView.setWebChromeClient(new WebChromeClient() {
    public void onProgressChanged(WebView view, int progress)
    {
    	//Make the bar disappear after URL is loaded, and changes string to Loading...
    	MyActivity.setTitle("Loading...");
    	MyActivity.setProgress(progress * 100); //Make the bar disappear after URL is loaded

    	// Return the app name after finish loading
        if(progress == 100)
        	MyActivity.setTitle(m_title);
      }
    });*/

}
