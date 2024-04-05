package com.bytedesk.demo.api;

import android.view.LayoutInflater;
import android.view.View;

import com.bytedesk.core.event.ConnectionEvent;
import com.bytedesk.core.event.KickoffEvent;
import com.bytedesk.core.event.MessageEntityEvent;
import com.bytedesk.core.event.MessageEvent;
import com.bytedesk.core.util.BDCoreConstant;
import com.bytedesk.core.util.BDPreferenceManager;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.bytedesk.demo.common.ServerFragment;
import com.bytedesk.demo.common.SettingFragment;
import com.bytedesk.demo.kefu.fragment.ChatFragment;
import com.bytedesk.demo.kefu.fragment.ProfileFragment;
import com.bytedesk.demo.kefu.fragment.StatusFragment;
import com.bytedesk.demo.kefu.fragment.SwitchFragment;
import com.bytedesk.demo.kefu.fragment.ThreadFragment;
import com.bytedesk.demo.utils.BDDemoConst;
import com.bytedesk.ui.api.BDUiApi;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIPackageHelper;
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
 * 接口列表
 */
public class ApiFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView) QMUIGroupListView mGroupListView;

    private String version;

    @Override
    protected View onCreateView() {
        //
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_api, null);
        ButterKnife.bind(this, root);

        EventBus.getDefault().register(this);
        version = QMUIPackageHelper.getAppVersion(getContext());
        initTopBar();
        initGroupListView();

        return root;
    }

    private void initTopBar() {
        mTopBar.setTitle("萝卜丝" + version + "(未连接)");
    }

    private void initGroupListView() {

        // 客服接口
        QMUICommonListItemView chatItem = mGroupListView.createItemView("1.联系客服");
        chatItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUICommonListItemView userInfoItem = mGroupListView.createItemView("2.用户信息");
        userInfoItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUICommonListItemView statusItem = mGroupListView.createItemView("3.在线状态");
        statusItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUICommonListItemView sessionHistoryItem = mGroupListView.createItemView("4.历史会话");
        sessionHistoryItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUICommonListItemView ticketItem = mGroupListView.createItemView("5.提交工单");
        ticketItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUICommonListItemView feedbackItem = mGroupListView.createItemView("6.意见反馈");
        feedbackItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUICommonListItemView helpCenterItem = mGroupListView.createItemView("7.常见问题");
        helpCenterItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUICommonListItemView wapChatItem = mGroupListView.createItemView("8.网页会话");
        wapChatItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUICommonListItemView settingItem = mGroupListView.createItemView("9.消息设置");
        settingItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUICommonListItemView switchItem = mGroupListView.createItemView("10.切换用户");
        switchItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUIGroupListView.newSection(getContext())
                .setTitle("客服接口")
                .addItemView(chatItem, view -> startFragment(new ChatFragment()))
                .addItemView(userInfoItem, view -> startFragment(new ProfileFragment()))
                .addItemView(statusItem, view -> startFragment(new StatusFragment()))
                .addItemView(sessionHistoryItem, view -> startFragment(new ThreadFragment()))
                .addItemView(ticketItem, view -> BDUiApi.startTicketActivity(getContext(), BDDemoConst.DEFAULT_TEST_ADMIN_UID))
                .addItemView(feedbackItem, view -> BDUiApi.startFeedbackActivity(getContext(), BDDemoConst.DEFAULT_TEST_ADMIN_UID))
                .addItemView(helpCenterItem, view -> BDUiApi.startSupportApiActivity(getContext(), BDDemoConst.DEFAULT_TEST_ADMIN_UID))
                .addItemView(wapChatItem, view -> {
                    // 注意: 登录后台->客服管理->技能组(或客服账号)->获取客服代码 获取相应URL
                    String url = "https://chat.kefux.com/chat/h5/index.html??sub=vip&uid=201808221551193&wid=201807171659201&type=workGroup&aid=&hidenav=1&ph=ph";
                    BDUiApi.startHtml5Chat(getContext(), url, "H5在线客服");
                })
                .addItemView(settingItem, view -> startFragment(new SettingFragment()))
                .addItemView(switchItem, view -> startFragment(new SwitchFragment()))
                .addTo(mGroupListView);

        // 公共接口
        QMUICommonListItemView serverItem = mGroupListView.createItemView("0.自定义服务器(私有部署)");
        serverItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        //
        QMUIGroupListView.newSection(getContext())
                .setTitle("公共接口")
                .addItemView(serverItem, view -> startFragment(new ServerFragment()))
                .addItemView(mGroupListView.createItemView("技术支持QQ-3群: 825257535"), view -> {})
                .addTo(mGroupListView);
    }


    /**
     * 监听 EventBus 长连接状态
     *
     * @param connectionEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent connectionEvent) {

        String connectionStatus = connectionEvent.getContent();
        Logger.i("onConnectionEvent: " + connectionStatus);

        String title = connectionStatus;
        if (connectionStatus.equals(BDCoreConstant.USER_STATUS_CONNECTING)) {

            title = "萝卜丝" + version + "(连接中...)";
        } else if (connectionStatus.equals(BDCoreConstant.USER_STATUS_CONNECTED)) {

            title = "萝卜丝" + version + "(已连接)";
        } else if (connectionStatus.equals(BDCoreConstant.USER_STATUS_DISCONNECTED)) {

            title = "萝卜丝" + version + "(连接断开)";
        }
        mTopBar.setTitle(title);
    }

    /**
     * 监听 EventBus 广播消息
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        Logger.i("MessageEvent");

        try {

            JSONObject messageObject = messageEvent.getJsonObject();
            String type = messageObject.getString("type");
            String content = messageObject.getString("content");
            Logger.i("type %s,  content %s ", type, content);
            // TODO: 收到新信息，开发者可自行决定处理，如：通知栏显示消息提示


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听账号异地登录通知
     *
     * @param kickoffEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKickoffEvent(KickoffEvent kickoffEvent) {

        String content = kickoffEvent.getContent();
        Logger.w("onKickoffEvent: " + content);

        // 弹窗提示
//        new QMUIDialog.MessageDialogBuilder(getActivity())
//            .setTitle("异地登录提示")
//            .setMessage(content)
//            .addAction("确定", (dialog, index) -> {
//                dialog.dismiss();
//
//                // 开发者可自行决定是否退出登录
//                // 注意: 同一账号同时登录多个客户端不影响正常会话
//                logout();
//
//            }).show();
    }


    /**
     * 监听接收消息
     *
     * @param messageEntityEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEntityEvent(MessageEntityEvent messageEntityEvent) {

        String type = messageEntityEvent.getMessageEntity().getType();
        if (type.equals(BDCoreConstant.MESSAGE_TYPE_TEXT)) {
            Logger.i("messageEntityEvent 文本消息 %s", messageEntityEvent.getMessageEntity().getContent());

        } else if (type.equals(BDCoreConstant.MESSAGE_TYPE_IMAGE)) {
            Logger.i("messageEntityEvent 图片消息 %s", messageEntityEvent.getMessageEntity().getImageUrl());

        } else if (type.equals(BDCoreConstant.MESSAGE_TYPE_VOICE)) {
            Logger.i("messageEntityEvent 语音消息 %s", messageEntityEvent.getMessageEntity().getVoiceUrl());

        } else if (type.equals(BDCoreConstant.MESSAGE_TYPE_FILE)) {
            Logger.i("messageEntityEvent 文件消息 %s", messageEntityEvent.getMessageEntity().getFileUrl());

        } else {
            Logger.i("messageEntityEvent 其他类型消息 %s", type);
        }
    }


}
