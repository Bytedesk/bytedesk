package com.bytedesk.ui.api;

import android.content.Context;
import android.content.Intent;

import com.bytedesk.core.room.entity.ThreadEntity;
import com.bytedesk.core.util.BDCoreConstant;
//import com.bytedesk.core.util.MMKVUtils;
import com.bytedesk.ui.activity.BrowserActivity;
import com.bytedesk.ui.activity.ChatIMActivity;
import com.bytedesk.ui.activity.ChatIMContactActivity;
import com.bytedesk.ui.activity.ChatIMGroupActivity;
import com.bytedesk.ui.activity.ChatKFActivity;
import com.bytedesk.ui.activity.FeedbackActivity;
import com.bytedesk.ui.activity.SupportApiActivity;
import com.bytedesk.ui.activity.TicketActivity;
import com.bytedesk.ui.util.BDUiConstant;
import com.bytedesk.ui.util.MediaLoader;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;

import java.util.Locale;

/**
 * UI对外开放接口
 *
 * @author bytedesk.com
 */
public class BDUiApi {

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        // 初始化相册
        Album.initialize(AlbumConfig.newBuilder(context)
            .setAlbumLoader(new MediaLoader())
            .setLocale(Locale.getDefault())
            .build()
        );
        // key-value存储库,替代sharedPreference
//        MMKVUtils.init(context);
    }

    /**
     * 访客端接口：开启原生会话页面Activity
     * 默认工作组会话
     *
     * @param context 上下文
     * @param wId 工作组wid
     * @param title 标题
     */
    public static void startWorkGroupChatActivity(Context context, String wId, String title) {
        //
//        BDConfig.getInstance(context).switchToKF();
        //
        Intent intent = new Intent(context, ChatKFActivity.class);
        intent.putExtra(BDUiConstant.EXTRA_VISITOR, true);
//        intent.putExtra(BDUiConstant.EXTRA_UID, "");
        intent.putExtra(BDUiConstant.EXTRA_WID, wId);
        intent.putExtra(BDUiConstant.EXTRA_TITLE, title);
        intent.putExtra(BDUiConstant.EXTRA_REQUEST_TYPE, BDCoreConstant.THREAD_REQUEST_TYPE_WORK_GROUP);
        intent.putExtra(BDUiConstant.EXTRA_THREAD_TYPE, BDCoreConstant.THREAD_TYPE_WORKGROUP);
        context.startActivity(intent);
    }

    public static void startWorkGroupChatActivityShop(Context context, String wId, String title, String commodity) {
        //
//        BDConfig.getInstance(context).switchToKF();
        //
        Intent intent = new Intent(context, ChatKFActivity.class);
        intent.putExtra(BDUiConstant.EXTRA_VISITOR, true);
//        intent.putExtra(BDUiConstant.EXTRA_UID, "");
        intent.putExtra(BDUiConstant.EXTRA_WID, wId);
        intent.putExtra(BDUiConstant.EXTRA_TITLE, title);
        intent.putExtra(BDUiConstant.EXTRA_REQUEST_TYPE, BDCoreConstant.THREAD_REQUEST_TYPE_WORK_GROUP);
        intent.putExtra(BDUiConstant.EXTRA_THREAD_TYPE, BDCoreConstant.THREAD_TYPE_WORKGROUP);
        intent.putExtra(BDUiConstant.EXTRA_CUSTOM, commodity);
        context.startActivity(intent);
    }

    /**
     * 访客端接口：开启原生会话页面Activity
     * 指定客服会话
     *
     * TODO: 后台开放获取所有客服uid接口
     *
     * @param context 上下文
     * @param title 标题
     */
    public static void startAppointChatActivity(Context context, String aId, String title) {
        //
//        BDConfig.getInstance(context).switchToKF();
        //
        Intent intent = new Intent(context, ChatKFActivity.class);
        intent.putExtra(BDUiConstant.EXTRA_VISITOR, true);
//        intent.putExtra(BDUiConstant.EXTRA_UID, "");
//        intent.putExtra(BDUiConstant.EXTRA_WID, "");
        intent.putExtra(BDUiConstant.EXTRA_AID, aId);
        intent.putExtra(BDUiConstant.EXTRA_TITLE, title);
        intent.putExtra(BDUiConstant.EXTRA_REQUEST_TYPE, BDCoreConstant.THREAD_REQUEST_TYPE_APPOINTED);
        intent.putExtra(BDUiConstant.EXTRA_THREAD_TYPE, BDCoreConstant.THREAD_TYPE_APPOINTED);
        context.startActivity(intent);
    }

    public static void startAppointChatActivityShop(Context context, String aId, String title, String commodity) {
        //
//        BDConfig.getInstance(context).switchToKF();
        //
        Intent intent = new Intent(context, ChatKFActivity.class);
        intent.putExtra(BDUiConstant.EXTRA_VISITOR, true);
//        intent.putExtra(BDUiConstant.EXTRA_UID, "");
//        intent.putExtra(BDUiConstant.EXTRA_WID, "");
        intent.putExtra(BDUiConstant.EXTRA_AID, aId);
        intent.putExtra(BDUiConstant.EXTRA_TITLE, title);
        intent.putExtra(BDUiConstant.EXTRA_REQUEST_TYPE, BDCoreConstant.THREAD_REQUEST_TYPE_APPOINTED);
        intent.putExtra(BDUiConstant.EXTRA_THREAD_TYPE, BDCoreConstant.THREAD_TYPE_APPOINTED);
        intent.putExtra(BDUiConstant.EXTRA_CUSTOM, commodity);
        context.startActivity(intent);
    }


    /**
     * 访客端接口：开启h5会话页面
     *
     * @param context 上下文
     * @param url url
     */
    public static void startHtml5Chat(Context context, String url, String title) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtra(BDUiConstant.EXTRA_URL, url);
        intent.putExtra(BDUiConstant.EXTRA_TITLE, title);
        context.startActivity(intent);
    }

//    /**
//     * 客服端接口，开启原生会话页面Activity
//     * 访客会话
//     *
//     * @param context 上下文
//     * @param tId 会话tid
//     * @param title 标题
//     */
//    public static void startThreadChatActivity(Context context, String tId, String uId, String title) {
//        //
//        BDConfig.getInstance(context).switchToIM();
//        //
//        Intent intent = new Intent(context, ChatKFActivity.class);
//        intent.putExtra(BDUiConstant.EXTRA_VISITOR, false);
//        intent.putExtra(BDUiConstant.EXTRA_TID, tId);
//        intent.putExtra(BDUiConstant.EXTRA_UID, uId);
//        intent.putExtra(BDUiConstant.EXTRA_TITLE, title);
//        intent.putExtra(BDUiConstant.EXTRA_THREAD_TYPE, BDCoreConstant.THREAD_TYPE_WORKGROUP);
//        context.startActivity(intent);
//    }
//
//    public static void startThreadChatActivity(Context context, String tId, String uId, String title, String custom) {
//        //
//        BDConfig.getInstance(context).switchToIM();
//        //
//        Intent intent = new Intent(context, ChatKFActivity.class);
//        intent.putExtra(BDUiConstant.EXTRA_VISITOR, false);
//        intent.putExtra(BDUiConstant.EXTRA_TID, tId);
//        intent.putExtra(BDUiConstant.EXTRA_UID, uId);
//        intent.putExtra(BDUiConstant.EXTRA_TITLE, title);
//        intent.putExtra(BDUiConstant.EXTRA_THREAD_TYPE, BDCoreConstant.THREAD_TYPE_WORKGROUP);
//        intent.putExtra(BDUiConstant.EXTRA_CUSTOM, custom);
//        context.startActivity(intent);
//    }

    public static void startThreadChatActivity2(Context context, ThreadEntity threadEntity) {
        //
        startThreadChatActivity(context, threadEntity.getTid(), threadEntity.getTopic(), threadEntity.getClient(), threadEntity.getType(), threadEntity.getNickname(), threadEntity.getAvatar());
    }

    public static void startThreadChatActivity(Context context, String tid, String topic, String client, String type, String nickname, String avatar) {
        //
//        BDConfig.getInstance(context).switchToIM();
        //
        Intent intent = new Intent(context, ChatIMActivity.class);
        intent.putExtra(BDUiConstant.EXTRA_IS_THREAD, true);
        intent.putExtra(BDUiConstant.EXTRA_THREAD_TID, tid);
        intent.putExtra(BDUiConstant.EXTRA_THREAD_TYPE, type);
        intent.putExtra(BDUiConstant.EXTRA_THREAD_TOPIC, topic);
        intent.putExtra(BDUiConstant.EXTRA_THREAD_CLIENT, client);
        intent.putExtra(BDUiConstant.EXTRA_THREAD_NICKNAME, nickname);
        intent.putExtra(BDUiConstant.EXTRA_THREAD_AVATAR, avatar);

        context.startActivity(intent);
    }

    /**
     * 客服端接口，开启原生会话页面Activity
     * 同事会话
     *
     * @param context 上下文
     * @param uId 对方uid
     * @param title 标题
     */
    public static void startContactChatActivity(Context context, String uId, String title) {
        //
//        BDConfig.getInstance(context).switchToIM();
        //
        Intent intent = new Intent(context, ChatIMContactActivity.class);
        intent.putExtra(BDUiConstant.EXTRA_IS_THREAD, false);
//        intent.putExtra(BDUiConstant.EXTRA_UID, uId);
        intent.putExtra(BDUiConstant.EXTRA_UUID, uId);
        intent.putExtra(BDUiConstant.EXTRA_TITLE, title);
        intent.putExtra(BDUiConstant.EXTRA_THREAD_TYPE, BDCoreConstant.THREAD_TYPE_CONTACT);
        context.startActivity(intent);
    }

//    public static void startContactChatActivity(Context context, String uId, String title, String custom) {
//        //
//        BDConfig.getInstance(context).switchToIM();
//        //
//        Intent intent = new Intent(context, ChatIMActivity.class);
//        intent.putExtra(BDUiConstant.EXTRA_VISITOR, false);
//        intent.putExtra(BDUiConstant.EXTRA_UID, uId);
//        intent.putExtra(BDUiConstant.EXTRA_TITLE, title);
//        intent.putExtra(BDUiConstant.EXTRA_THREAD_TYPE, BDCoreConstant.THREAD_TYPE_CONTACT);
//        intent.putExtra(BDUiConstant.EXTRA_CUSTOM, custom);
//        context.startActivity(intent);
//    }


    /**
     * 客服端接口，开启原生会话页面Activity
     * 群聊
     *
     * @param context 上下文
     * @param gId 群组gid
     * @param title 标题
     */
    public static void startGroupChatActivity(Context context, String gId, String title) {
        //
//        BDConfig.getInstance(context).switchToIM();
        //
        Intent intent = new Intent(context, ChatIMGroupActivity.class);
        intent.putExtra(BDUiConstant.EXTRA_IS_THREAD, false);
//        intent.putExtra(BDUiConstant.EXTRA_GID, gId);
        intent.putExtra(BDUiConstant.EXTRA_UUID, gId);
        intent.putExtra(BDUiConstant.EXTRA_TITLE, title);
        intent.putExtra(BDUiConstant.EXTRA_THREAD_TYPE, BDCoreConstant.THREAD_TYPE_GROUP);
        context.startActivity(intent);
    }

//    public static void startGroupChatActivity(Context context, String gId, String title, String custom) {
//        //
//        BDConfig.getInstance(context).switchToIM();
//        //
//        Intent intent = new Intent(context, ChatIMActivity.class);
//        intent.putExtra(BDUiConstant.EXTRA_VISITOR, false);
//        intent.putExtra(BDUiConstant.EXTRA_GID, gId);
//        intent.putExtra(BDUiConstant.EXTRA_TITLE, title);
//        intent.putExtra(BDUiConstant.EXTRA_THREAD_TYPE, BDCoreConstant.THREAD_TYPE_GROUP);
//        intent.putExtra(BDUiConstant.EXTRA_CUSTOM, custom);
//        context.startActivity(intent);
//    }


    public static void startFeedbackActivity(Context context, String uid) {
        //
//        BDConfig.getInstance(context).switchToKF();
        //
        Intent intent = new Intent(context, FeedbackActivity.class);
        intent.putExtra(BDUiConstant.EXTRA_UID, uid);
        context.startActivity(intent);
    }


    public static void startTicketActivity(Context context, String uid) {
        //
//        BDConfig.getInstance(context).switchToKF();
        //
        Intent intent = new Intent(context, TicketActivity.class);
        intent.putExtra(BDUiConstant.EXTRA_UID, uid);
        context.startActivity(intent);
    }


    public static void startSupportApiActivity(Context context, String uid) {
        //
//        BDConfig.getInstance(context).switchToKF();
        //
        Intent intent = new Intent(context, SupportApiActivity.class);
        intent.putExtra(BDUiConstant.EXTRA_UID, uid);
        context.startActivity(intent);
    }


    public static void startSupportURLActivity(Context context, String uid) {

        String url = "https://www.bytedesk.com/support?uid=" + uid + "&ph=ph";
        BDUiApi.startHtml5Chat(context, url, "帮助中心");
    }




}










