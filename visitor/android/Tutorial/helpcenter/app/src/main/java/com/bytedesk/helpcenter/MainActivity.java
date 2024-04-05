package com.bytedesk.helpcenter;

import android.content.Context;
import android.os.Bundle;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.LoginCallback;
import com.bytedesk.helpcenter.R;
import com.bytedesk.ui.api.BDUiApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // 管理后台：https://www.bytedesk.com/admin/#/antv/user/login
    // 获取管理员uid, 登录后台->客服管理->客服账号->管理员账号(唯一ID(uid))列
    public final static String adminUId = "201808221551193";
    // 获取appkey，登录后台->客服管理->渠道管理->添加应用->appkey
    public final static String appkey = "201809171553112";
    // 获取subDomain，也即企业号：登录后台->客服管理->客服账号->企业号
    public final static String subdomain = "vip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 初始化萝卜丝UI界面库
        BDUiApi.init(this);
        anonymousLogin();

        final Context context = this;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 打开帮助中心界面
                BDUiApi.startSupportURLActivity(context, adminUId);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 匿名登录
     */
    private void anonymousLogin() {

        // 授权登录接口
        final Context context = this;
        BDCoreApi.visitorLogin(this, appkey, subdomain, new LoginCallback() {

            @Override
            public void onSuccess(JSONObject object) {

            }

            @Override
            public void onError(JSONObject object) {

                try {

                    String message = object.getString("message");
//                    int status_code = object.getInt("status_code");
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
