package com.bytedesk.ui.manager;

import android.content.Context;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.util.BDCoreUtils;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cgspine on 2018/1/14.
 */

public class QDUpgradeManager {

    private static QDUpgradeManager sQDUpgradeManager = null;

    private Context mContext;

    private QDUpgradeManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static final QDUpgradeManager getInstance(Context context) {
        if (sQDUpgradeManager == null) {
            sQDUpgradeManager = new QDUpgradeManager(context);
        }
        return sQDUpgradeManager;
    }

    public void check(String appkey) {
        //
        int currentVersion = BDCoreUtils.getVersionCode(mContext);
        // TODO: 网络请求最新版本

        BDCoreApi.checkAppVersion(mContext, appkey, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {
                Logger.i("check app version success %s", object.toString());

                try {

                    JSONObject app = object.getJSONObject("data");
                    String version = app.getString("version");
                    int versionCode = app.getInt("versionCode");
                    String tip = app.getString("tip");
                    String url = app.getString("url");
                    boolean forceUpgrade = app.getBoolean("forceUpgrade");
                    Logger.i("version %s, tip %s, url %s, forceUpgrade %s", version, tip, url, forceUpgrade);

//                    int newVersion = Integer.parseInt(version);
                    if (versionCode > currentVersion) {
//                        new UpgradeTipTask().upgrade(mContext, tip, url);
//                        TODO: eventbus发送广播，提示需要升级，前端收到事件之后，弹窗提示，如果要升级，则跳转下载url
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(JSONObject object) {
                Logger.i("check version failed %s", object.toString());
            }
        });
    }

}
