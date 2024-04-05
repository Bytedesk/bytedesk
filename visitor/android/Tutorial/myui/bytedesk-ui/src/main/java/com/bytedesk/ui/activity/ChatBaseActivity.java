package com.bytedesk.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.api.BDMqttApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.event.KickoffEvent;
import com.bytedesk.core.event.LongClickEvent;
import com.bytedesk.core.event.MessageEvent;
import com.bytedesk.core.repository.BDRepository;
import com.bytedesk.core.room.entity.ThreadEntity;
import com.bytedesk.core.util.BDCoreUtils;
import com.bytedesk.core.util.BDPreferenceManager;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 聊天页面基类
 *
 * @author bytedesk.com on 2019/2/27
 */
public class ChatBaseActivity extends AppCompatActivity {

    // 客服会话代表会话tid，一对一会话代表uid，群组会话代表gid
    protected String mUUID;
    public ThreadEntity mThreadEntity = new ThreadEntity();
    // 指定坐席uid
    protected String mAgentUid;
    protected String mTitle;
    // 是否访客端调用接口
    protected boolean mIsVisitor;
    protected boolean mIsRobot;
    // 区分客服会话workgroup/appointed、同事会话contact、群组会话group
    protected String mThreadType;
    // 区分工作组会话、指定客服会话
    protected String mRequestType;
    // 分页加载聊天记录
    protected int mPage = 0;
    protected int mSize = 20;
    // 本地存储信息
    protected BDPreferenceManager mPreferenceManager;
    protected BDRepository mRepository;

    /**
     * 监听 EventBus 广播消息: 长按消息
     *
     * @param longClickEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLongClickEvent(LongClickEvent longClickEvent) {
        Logger.i("LongClickEvent");

        // 如果不是自己发送的消息，这直接返回
        if (!longClickEvent.getMessageEntity().isSend()) {
            return;
        }

        new QMUIBottomSheet.BottomListSheetBuilder(this)
            .addItem("复制")
            .addItem("删除")
            .addItem("撤回")
            .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                switch (position) {
                    case 0:
                        String content = longClickEvent.getMessageEntity().getContent();
                        Logger.d("copy:" + content);
                        BDCoreUtils.copy(getBaseContext(), content);

                        break;
                    case 1:
                        String deleteMid = longClickEvent.getMessageEntity().getMid();
                        Logger.d("delete:" + deleteMid);

                        // 删除消息
                        BDCoreApi.markDeletedMessage(getBaseContext(), deleteMid, new BaseCallback() {

                            @Override
                            public void onSuccess(JSONObject object) {

                            }

                            @Override
                            public void onError(JSONObject object) {

                            }
                        });

                        break;
                    case 2:
                        String recallMid = longClickEvent.getMessageEntity().getMid();
                        Logger.d("recallMid: " + recallMid);

                        // 撤回消息
                        BDMqttApi.sendRecallMessageProtobuf(getBaseContext(), mThreadEntity, recallMid);

                        // 删除本地消息
                        BDRepository.getInstance(getBaseContext()).deleteMessage(recallMid);

                        break;
                }
                dialog.dismiss();
            })
            .build().show();
    }


    /**
     * 监听 EventBus 广播消息
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
//        Logger.i("MessageEvent");

        // TODO: 判断是否阅后即焚消息，如果是，则倒计时销毁

        try {

            JSONObject messageObject = messageEvent.getJsonObject();
            JSONObject threadObject = messageObject.getJSONObject("thread");
            String tid = threadObject.getString("tid");
            Logger.i("tid %s, mUUID %s ", tid, mUUID);
            if (tid.equals(mUUID)) {
                Logger.i("发送已读回执");

                if (!messageObject.isNull("user")) {
                    String senderUid = messageObject.getJSONObject("user").getString("uid");
                    // 判断是否是自己发送的消息，只有收到对方发的消息，才会发送回执
                    if (!senderUid.equals(mPreferenceManager.getUid())) {
                        //
                        String mid = messageEvent.getJsonObject().getString("mid");
                        // 检查是否当前页面消息，如果是，则发送已读消息回执
//                        BDMqttApi.sendReceiptReadMessage(this, mid, tid);
                    }
                }
            } else {
                Logger.i(" tid != mUUID");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    /**
     * 账号异地登录通知提示，开发者可自行决定是否退出当前账号登录
     *
     * @param kickoffEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKickoffEvent(KickoffEvent kickoffEvent) {

        String content = kickoffEvent.getContent();
        Logger.w("onKickoffEvent: " + content);

        // 弹窗提示
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("异地登录提示")
                .setMessage(content)
                .addAction("确定", (dialog, index) -> {
                    dialog.dismiss();

                    // TODO: 开发者可自行决定是否退出登录

                }).show();
    }





}
