package com.bytedesk.demo.im;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.api.BDMqttApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.event.ConnectionEvent;
import com.bytedesk.core.util.BDCoreConstant;
import com.bytedesk.core.util.BDPreferenceManager;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.AboutFragment;
import com.bytedesk.demo.common.BaseController;
import com.bytedesk.demo.im.fragment.contact.ContactFragment;
import com.bytedesk.demo.im.fragment.group.GroupFragment;
import com.bytedesk.demo.im.fragment.login.IntroFragment;
import com.bytedesk.demo.im.fragment.profile.ProfileFragment;
import com.bytedesk.demo.im.fragment.queue.QueueFragment;
import com.bytedesk.demo.im.fragment.thread.ThreadFragment;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IMController extends BaseController {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView)
    QMUIGroupListView mGroupListView;

    private BDPreferenceManager mPreferenceManager;
    private QMUICommonListItemView loginItem;

    public IMController(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.controller_social, this);
        ButterKnife.bind(this);

        mPreferenceManager = BDPreferenceManager.getInstance(getContext());

        EventBus.getDefault().register(this);

        initTopBar();
        initGroupListView();
    }

    @Override
    protected String getTitle() {
        return "IM接口(未连接)";
    }

    protected void initTopBar() {
        mTopBar.setTitle(getTitle());
        mTopBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_color_blue));
        mTopBar.addRightImageButton(R.mipmap.icon_topbar_about, R.id.topbar_right_about_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
            AboutFragment fragment = new AboutFragment();
            startFragment(fragment);
            }
        });
    }

    private void initGroupListView() {

        QMUICommonListItemView introItem = mGroupListView.createItemView("1.接口说明");

        loginItem = mGroupListView.createItemView("2.登录接口");
        loginItem.setDetailText("当前未连接，点我建立连接");

        QMUICommonListItemView contactItem = mGroupListView.createItemView("3.联系人接口");

        QMUICommonListItemView groupItem = mGroupListView.createItemView("4.群组接口");

        QMUICommonListItemView threadItem = mGroupListView.createItemView("5.会话接口");

        QMUICommonListItemView queueItem = mGroupListView.createItemView("6.排队接口");

        QMUICommonListItemView settingItem = mGroupListView.createItemView("7.设置接口");

        QMUIGroupListView.newSection(getContext())
                .addItemView(introItem, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IntroFragment introFragment = new IntroFragment();
                        startFragment(introFragment);
                    }
                })
                .addItemView(loginItem, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 如果已经连接，则调用登出接口，否则调用登录接口
                        if (BDMqttApi.isConnected(getContext())) {
                            logout();
                        } else {
                            login();
                        }
                    }
                })
                .addItemView(contactItem, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ContactFragment contactFragment = new ContactFragment();
                        startFragment(contactFragment);
                    }
                })
                .addItemView(groupItem, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        GroupFragment groupFragment = new GroupFragment();
                        startFragment(groupFragment);
                    }
                })
                .addItemView(threadItem, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ThreadFragment threadFragment = new ThreadFragment();
                        startFragment(threadFragment);
                    }
                })
                .addItemView(queueItem, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        QueueFragment queueFragment = new QueueFragment();
                        startFragment(queueFragment);
                    }
                })
                .addItemView(settingItem, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProfileFragment profileFragment = new ProfileFragment();
                        startFragment(profileFragment);
                    }
                })
                .addTo(mGroupListView);
    }


    private void login() {
        // 测试账号：test1，密码：123456
        // 测试账号：test2，密码：123456
        // 测试账号：test3，密码：123456
        String username = "test3";
        String password = "123456";
        String appKey = "201809171553112";
        String subDomain = "vip";

        // 调用登录接口
        BDCoreApi.login(getContext(), username, password, appKey, subDomain, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    Logger.d("login success:"
                            + " access_token: " + object.get("access_token")
                            + " token_type:" + object.get("token_type")
                            + " refresh_token:" + object.get("refresh_token")
                            + " expires_in:" + object.get("expires_in")
                            + " scope:" + object.get("scope")
                            + " jti:" + object.get("jti"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {
                Logger.e("login failed message");
            }
        });
    }

    /**
     * 退出登录
     */
    private void logout() {
        //
        BDCoreApi.logout(getContext(), new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                Toast.makeText(getContext(), "退出登录成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(JSONObject object) {

                Logger.e("退出登录失败");
                Toast.makeText(getContext(), "退出登录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 监听 EventBus 连接消息
     *
     * @param connectionEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent connectionEvent) {

        String connectionStatus = connectionEvent.getContent();
        Logger.i("onConnectionEvent: " + connectionStatus);

        String title = connectionStatus;
        if (connectionStatus.equals(BDCoreConstant.USER_STATUS_CONNECTING)) {

            title = mPreferenceManager.loginAsVisitor() ? "客服接口(连接中...)" : "IM接口(连接中...)";
        } else if (connectionStatus.equals(BDCoreConstant.USER_STATUS_CONNECTED)) {

            title = mPreferenceManager.loginAsVisitor() ? "客服接口(已连接)" : "IM接口(已连接)";
            loginItem.setDetailText("连接已建立，点我断开连接");
        } else if (connectionStatus.equals(BDCoreConstant.USER_STATUS_DISCONNECTED)) {

            title = mPreferenceManager.loginAsVisitor() ? "客服接口(连接断开)" : "IM接口(连接断开)";
            loginItem.setDetailText("当前未连接，点我建立连接");
        }
        mTopBar.setTitle(title);
    }
}
