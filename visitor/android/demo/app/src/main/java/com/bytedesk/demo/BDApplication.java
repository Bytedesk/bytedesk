package com.bytedesk.demo;

import android.app.Application;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.LoginCallback;
import com.bytedesk.demo.utils.BDDemoConst;
import com.bytedesk.ui.api.BDUiApi;
import com.facebook.stetho.Stetho;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author bytedesk.com on 2017/9/20.
 */
public class BDApplication extends Application{

    public void onCreate() {
        super.onCreate();

        // SQLite数据库调试库
        // http://facebook.github.io/stetho/
        // 使用方法： 浏览器中打开 chrome://inspect and 点击 "Inspect"
        Stetho.initializeWithDefaults(this);

        // Logger初始化
        // https://github.com/orhanobut/logger
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override public boolean isLoggable(int priority, String tag) {
//                return BuildConfig.DEBUG;
                return true;
            }
        });

        // QMUI初始化
        QMUISwipeBackActivityManager.init(this);

        // 初始化萝卜丝UI界面库
        BDUiApi.init(this);

        // 自定义服务器，请跟萝卜丝联系获取，私有部署开发者调用。公有云用户无需调用
//        BDConfig.getInstance(this).setRestApiHost("https://example.com/");
//        BDConfig.getInstance(this).setMqttWebSocketWssURL("wss://example.com/websocket");

        // 萝卜丝授权登录
        anonymousLogin();
    }


    // 访客匿名登录
    private void anonymousLogin () {
        BDCoreApi.visitorLogin(this,
                BDDemoConst.DEFAULT_TEST_APPKEY,
                BDDemoConst.DEFAULT_TEST_SUBDOMAIN, new LoginCallback() {

                    @Override
                    public void onSuccess(JSONObject object) {
                        try {
                            Logger.d("login success message: " + object.get("message")
                                    + " status_code:" + object.get("status_code"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(JSONObject object) {
                        try {
                            Logger.e("login failed message: " + object.get("message")
                                    + " status_code:" + object.get("status_code")
                                    + " data:" + object.get("data"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}
