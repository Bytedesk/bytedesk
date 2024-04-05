package com.bytedesk.demo.kefu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.api.BDMqttApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.callback.LoginCallback;
import com.bytedesk.core.event.ConnectionEvent;
import com.bytedesk.core.util.BDCoreConstant;
import com.bytedesk.core.util.BDPreferenceManager;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.AboutFragment;
import com.bytedesk.demo.common.BaseController;
import com.bytedesk.demo.kefu.fragment.ChatFragment;
import com.bytedesk.demo.kefu.fragment.IntroFragment;
import com.bytedesk.demo.kefu.fragment.ProfileFragment;
import com.bytedesk.demo.kefu.fragment.StatusFragment;
import com.bytedesk.demo.kefu.fragment.ThreadFragment;
import com.bytedesk.demo.utils.BDDemoConst;
import com.bytedesk.ui.api.BDUiApi;
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

/**
 * @author bytedesk.com on 2018/3/26.
 */

public class KeFuController extends BaseController {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView) QMUIGroupListView mGroupListView;

    private BDPreferenceManager mPreferenceManager;
    private QMUICommonListItemView loginItem;

    public KeFuController(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.controller_visitor, this);
        ButterKnife.bind(this);

        mPreferenceManager = BDPreferenceManager.getInstance(getContext());

        EventBus.getDefault().register(this);

        initTopBar();
        initGroupListView();
    }

    @Override
    protected String getTitle() {
        return "客服接口(未连接)";
    }

    protected void initTopBar() {
        mTopBar.setTitle(getTitle());
        mTopBar.addRightImageButton(R.mipmap.icon_topbar_about, R.id.topbar_right_about_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutFragment fragment = new AboutFragment();
                startFragment(fragment);
            }
        });
    }

    private void initGroupListView() {
        //
        QMUICommonListItemView introItem = mGroupListView.createItemView("1.接口说明");

        loginItem = mGroupListView.createItemView("2.登录接口");
        loginItem.setDetailText("当前未连接，点我建立连接");

        QMUICommonListItemView chatItem = mGroupListView.createItemView("3.开始新会话接口");

        QMUICommonListItemView userInfoItem = mGroupListView.createItemView("4.用户信息接口");

        QMUICommonListItemView statusItem = mGroupListView.createItemView("5.在线状态接口");

        QMUICommonListItemView sessionHistoryItem = mGroupListView.createItemView("6.历史会话记录接口");

        QMUICommonListItemView wapChatItem = mGroupListView.createItemView("7.网页会话演示");

        QMUIGroupListView.newSection(getContext())
                .addItemView(introItem, view -> startFragment(new IntroFragment()))
                .addItemView(loginItem, view -> {
                    // 如果已经连接，则调用登出接口，否则调用登录接口
                    if (BDMqttApi.isConnected(getContext())) {
                        logout();
                    } else {
                        login();
                    }
                })
                .addItemView(chatItem, view -> startFragment(new ChatFragment()))
                .addItemView(userInfoItem, view -> startFragment(new ProfileFragment()))
                .addItemView(statusItem, view -> startFragment(new StatusFragment()))
                .addItemView(sessionHistoryItem, view -> startFragment(new ThreadFragment()))
                .addItemView(wapChatItem, view -> {
                    // 注意: 登录后台->客服管理->技能组->获取代码 获取相应URL
                    String url = "https://vip.bytedesk.com/chat?uid=" + BDDemoConst.DEFAULT_TEST_ADMIN_UID + "&wid=201807171659201&type=workGroup&aid=&ph=ph";
                    BDUiApi.startHtml5Chat(getContext(), url, "在线客服");
                })
                .addTo(mGroupListView);
    }


    private void login() {

        // 授权登录接口
        BDCoreApi.visitorLogin(getContext(), BDDemoConst.DEFAULT_TEST_APPKEY, BDDemoConst.DEFAULT_TEST_SUBDOMAIN, new LoginCallback() {

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

    /**
     * 退出登录
     */
    private void logout() {
        //
        BDCoreApi.logout(getContext(), new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

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
