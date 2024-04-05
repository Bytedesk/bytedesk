package com.bytedesk.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.api.BDMqttApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.event.PreviewEvent;
import com.bytedesk.core.event.QueryAnswerEvent;
import com.bytedesk.core.event.SendCommodityEvent;
import com.bytedesk.core.repository.BDRepository;
import com.bytedesk.core.util.BDCoreConstant;
import com.bytedesk.core.util.BDCoreUtils;
import com.bytedesk.core.util.BDPreferenceManager;
import com.bytedesk.core.viewmodel.MessageViewModel;
import com.bytedesk.ui.R;
import com.bytedesk.ui.adapter.ChatAdapter;
import com.bytedesk.ui.listener.ChatItemClickListener;
import com.bytedesk.ui.util.BDPermissionUtils;
import com.bytedesk.ui.util.BDUiConstant;
import com.bytedesk.ui.widget.InputAwareLayout;
import com.bytedesk.ui.widget.KeyboardAwareLinearLayout;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  用途：
 *  1. 客服聊天界面
 *
 *  TODO:
 *    1. 访客关闭会话窗口的时候通知客服
 *    2. 客服端关闭会话之后，禁止访客继续发送消息
 *
 * @author bytedesk.com
 */
public class ChatKFActivity extends ChatBaseActivity implements ChatItemClickListener,
        KeyboardAwareLinearLayout.OnKeyboardShownListener,
        KeyboardAwareLinearLayout.OnKeyboardHiddenListener,
        View.OnClickListener {

    private InputAwareLayout mInputAwaireLayout;
    private QMUITopBarLayout mTopBar;
    private QMUIPullRefreshLayout mPullRefreshLayout;
    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;

    private Button mPlusButton;
    private Button mSendButton;
    private EditText mInputEditText;

    private MessageViewModel mMessageViewModel;

    // 根据会话类型不同所代表意义不同：
    private String mUid;
    // 工作组wid
    private String mWorkGroupWid;
    private final Handler mHandler = new Handler();
    private String mCustom;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_chat_kf);
        //
        if (null != getIntent()) {
            //
            mIsVisitor = getIntent().getBooleanExtra(BDUiConstant.EXTRA_VISITOR, true);
            mIsRobot = false;
            mThreadType = getIntent().getStringExtra(BDUiConstant.EXTRA_THREAD_TYPE);
            mCustom = getIntent().getStringExtra(BDUiConstant.EXTRA_CUSTOM);
            //
            mPreferenceManager = BDPreferenceManager.getInstance(this);
            mPreferenceManager.setVisitor(mIsVisitor);
            mRepository = BDRepository.getInstance(this);
            //
            if (mIsVisitor) {
                Logger.i("访客会话");
                mWorkGroupWid = getIntent().getStringExtra(BDUiConstant.EXTRA_WID);
                mRequestType = getIntent().getStringExtra(BDUiConstant.EXTRA_REQUEST_TYPE);
                // 判断是否指定客服会话
                if (mRequestType.equals(BDCoreConstant.THREAD_REQUEST_TYPE_APPOINTED)) {
                    // 指定客服会话
                    mAgentUid = getIntent().getStringExtra(BDUiConstant.EXTRA_AID);
                } else {
                    // 工作组会话
                    mAgentUid = "";
                }
            } else if (mThreadType.equals(BDCoreConstant.THREAD_TYPE_WORKGROUP)) {
                Logger.i("客服会话");

                mUUID = getIntent().getStringExtra(BDUiConstant.EXTRA_TID);
            }
            //
            mUid = getIntent().getStringExtra(BDUiConstant.EXTRA_UID);
            mTitle = getIntent().getStringExtra(BDUiConstant.EXTRA_TITLE);
        }

        //
        initTopBar();
        initView();
        initModel();

        // 访客端请求会话
        if (mIsVisitor) {
            requestThread();
        }
        // 从服务器端加载聊天记录，默认暂不加载
        // getMessages();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 注册监听
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // 销毁监听
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO: 清理
    }

    /**
     * TODO: 客服端输入框显示常用回复按钮
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        //
        if (view.getId() == R.id.bytedesk_chat_input_send_button) {
            //
            final String content = mInputEditText.getText().toString();
            if (content.trim().length() > 0) {

                // TODO: 访客端客服会话：无客服在线时，发送消息会返回机器人答案

                // TODO: 收到客服关闭会话 或者 自动关闭会话消息之后，禁止访客发送消息

                // 机器人
                if (mIsRobot) {
                    //
                    sendRobotMessage(content);
                } else {
                    //
                    sendTextMessage(content);
                }

                mInputEditText.setText(null);
            }
        }
        else if (view.getId() == R.id.bytedesk_chat_input_plus_button) {

            // TODO: 收到客服关闭会话 或者 自动关闭会话消息之后，禁止访客发送消息

            new QMUIBottomSheet.BottomListSheetBuilder(this)
                .addItem("相册")
                .addItem("拍照")
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    switch (position) {
                        case 0:
                            Logger.d("album");
                            requestAlbumPermission();
                            break;
                        case 1:
                            Logger.d("camera");
                            requestCameraPermission();
                            break;
                    }
                    dialog.dismiss();
                })
                .build().show();
        }

    }

    /**
     * 顶部topbar初始化
     */
    private void initTopBar() {
        //
        mTopBar = findViewById(R.id.bytedesk_chat_topbarlayout);
        if (!BDMqttApi.isConnected(this)) {
            mTopBar.setTitle(mTitle+"(未连接)");
        } else {
            mTopBar.setTitle(mTitle);
        }
        mTopBar.addLeftBackImageButton().setOnClickListener(view -> finish());
        //
        if (mIsVisitor) {
            // 访客会话
            mTopBar.addRightImageButton(R.mipmap.icon_topbar_overflow, QMUIViewHelper.generateViewId())
                    .setOnClickListener(view -> visitorTopRightSheet());
        }
        else if (!mIsVisitor && mThreadType.equals(BDCoreConstant.MESSAGE_SESSION_TYPE_WORKGROUP)) {
            // 客服会话
            mTopBar.addRightImageButton(R.mipmap.icon_topbar_overflow, QMUIViewHelper.generateViewId())
                    .setOnClickListener(view -> showTopRightSheet());
        }
        QMUIStatusBarHelper.translucent(this);
    }

    /**
     * 界面初始化
     */
    private void initView () {
        //
        mInputAwaireLayout = findViewById(R.id.bytedesk_activity_chat_kf);
        mInputAwaireLayout.addOnKeyboardShownListener(this);
        mInputAwaireLayout.addOnKeyboardHiddenListener(this);
        //
        mPullRefreshLayout = findViewById(R.id.bytedesk_chat_pulltorefresh);
        mPullRefreshLayout.setOnPullListener(pullListener);

        // TODO: 增加点击聊天界面，去除输入框焦点，让其缩回底部
        // 初始化
        mRecyclerView = findViewById(R.id.bytedesk_chat_fragment_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // 设置适配器adapter
        mChatAdapter = new ChatAdapter(this, this);
        mRecyclerView.setAdapter(mChatAdapter);

        // 选择图片、拍照
        mPlusButton = findViewById(R.id.bytedesk_chat_input_plus_button);
        mPlusButton.setOnClickListener(this);

        // 发送文本消息
        mSendButton = findViewById(R.id.bytedesk_chat_input_send_button);
        mSendButton.setOnClickListener(this);
        mInputEditText = findViewById(R.id.bytedesk_chat_fragment_input);
        mInputEditText.addTextChangedListener(inputTextWatcher);

        // 图片大图预览
//        imagePreview = findViewById(R.id.bytedesk_image_preivew);
//        mScreenSize = ImageViewerUtil.getScreenSize(this);
//        imagePreview.setDefSize(mScreenSize.x, mScreenSize.y);
//        imagePreview.setImageDraggerType(ImageDraggerType.DRAG_TYPE_WX);
    }

    /**
     * 初始化ModelView
     *
     * TODO: 完善收发消息界面出现闪动的情况
     */
    private void initModel () {
        //
        mMessageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        // FIXME: 当工作组设置有值班工作组的情况下，则界面无法显示值班工作组新消息

        if (mIsVisitor) {

            // 判断是否指定客服会话
            if (mRequestType.equals(BDCoreConstant.THREAD_REQUEST_TYPE_APPOINTED)) {
                Logger.i("访客会话: 指定客服聊天记录");
                // 指定客服聊天记录
                mMessageViewModel.getThreadMessages(mUUID).observe(this, messageEntities -> {
                    mChatAdapter.setMessages(messageEntities);
                    mRecyclerView.scrollToPosition(messageEntities.size() - 1);
                });
            } else {
                Logger.i("访客会话: 工作组聊天记录");
                // 工作组聊天记录, TODO: 是否沿用此方式待定，转接会话聊天
                mMessageViewModel.getWorkGroupMessages(mWorkGroupWid).observe(this, messageEntities -> {
                    mChatAdapter.setMessages(messageEntities);
                    mRecyclerView.scrollToPosition(messageEntities.size() - 1);
                });
            }

        } else if (mThreadType.equals(BDCoreConstant.THREAD_TYPE_WORKGROUP)){
            Logger.i("客服端：客服会话");

            mMessageViewModel.getVisitorMessages(mUid).observe(this, messageEntities -> {
                mChatAdapter.setMessages(messageEntities);
                mRecyclerView.scrollToPosition(messageEntities.size() - 1);
            });
            // 设置当前会话
            updateCurrentThread();
        }
    }


    /**
     * 请求会话
     * 请求工作组会话和指定客服会话统一接口
     */
    private void requestThread() {

        BDCoreApi.requestThread(this, mWorkGroupWid, mRequestType, mAgentUid, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                dealWithThread(object);
            }

            @Override
            public void onError(JSONObject object) {
                try {
                    Logger.d("request thread message: " + object.get("message")
                            + " status_code:" + object.get("status_code")
                            + " data:" + object.get("data"));
                    Toast.makeText(ChatKFActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                    // TODO: token过期，要求重新登录
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 请求人工客服
     */
    private void requestAgent () {
        //
        // TODO: mUUID 替换为 agentUid, agentUid不能为空
        BDCoreApi.requestAgent(this, mWorkGroupWid, mRequestType, mUUID, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                dealWithThread(object);
            }

            @Override
            public void onError(JSONObject object) {
                try {
                    Logger.d("request thread message: " + object.get("message")
                            + " status_code:" + object.get("status_code")
                            + " data:" + object.get("data"));
                    Toast.makeText(ChatKFActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 初始化机器人
     */
    private void requestRobot() {

        BDCoreApi.initAnswer(this, mRequestType, mWorkGroupWid, mAgentUid, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    JSONObject messageObject = object.getJSONObject("data");
                    //持久化到数据库
                    mRepository.insertMessageJson(messageObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {

                try {
                    Logger.d("init answer message: " + object.get("message") + " status_code:" + object.get("status_code") + " data:" + object.get("data"));
                    Toast.makeText(ChatKFActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * 请求机器人会话
     * @param content 关键词
     */
    private void sendRobotMessage(String content) {
        Logger.i("tid: %s, wid %s, type %s, content %s", mUUID, mWorkGroupWid, mRequestType, content);

        // 自定义本地消息id，用于判断消息发送状态. 消息通知或者回调接口中会返回此id
        final String localId = BDCoreUtils.uuid();

        // 插入本地消息
        mRepository.insertTextMessageLocal(mUUID, mWorkGroupWid, mUid, content, localId, mThreadType);

        BDCoreApi.messageAnswer(this, mRequestType, mWorkGroupWid, mAgentUid, content, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {
                //
                try {

                    int status_code = object.getInt("status_code");
                    if (status_code == 200) {
                        // 正确匹配到答案
                        mRepository.deleteMessageLocal(localId);

                        //
                        JSONObject queryMessageObject = object.getJSONObject("data").getJSONObject("query");
                        JSONObject replyMessageObject = object.getJSONObject("data").getJSONObject("reply");

                        // TODO: 答案中添加 '有帮助'、'无帮助'，访客点击可反馈答案是否有用

                        //持久化到数据库
                        mRepository.insertMessageJson(queryMessageObject);

                        if (content.indexOf("人工") != -1) {
                            requestAgent();
                        } else {
                            mRepository.insertRobotRightAnswerMessageJson(replyMessageObject);
                        }

                    } else if (status_code == 201) {
                        // 未匹配到答案
                        mRepository.deleteMessageLocal(localId);
                        //
                        JSONObject queryMessageObject = object.getJSONObject("data").getJSONObject("query");
                        JSONObject replyMessageObject = object.getJSONObject("data").getJSONObject("reply");

                        // TODO: 回答内容中添加 '人工客服' 字样，访客点击可直接联系人工客服

                        //持久化到数据库
                        mRepository.insertMessageJson(queryMessageObject);

                        if (content.indexOf("人工") != -1) {

                            requestAgent();

                        } else {
                            mRepository.insertRobotNoAnswerMessageJson(replyMessageObject);
                        }

                    } else {
                        // 修改本地消息发送状态为error
                        mRepository.updateMessageStatusError(localId);

                        // 发送消息失败
                        String message = object.getString("message");
                        Toast.makeText(ChatKFActivity.this, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {

                try {
                    Logger.d("robot message: " + object.get("message") + " status_code:" + object.get("status_code") + " data:" + object.get("data"));
                    Toast.makeText(ChatKFActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * 更新当前会话
     */
    private void updateCurrentThread() {

        String preTid = mPreferenceManager.getCurrentTid();
        BDCoreApi.updateCurrentThread(this, preTid, mUUID, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {
                // 设置当前tid
                mPreferenceManager.setCurrentTid(mUUID);
            }

            @Override
            public void onError(JSONObject object) {
                Logger.e("更新当前会话失败");
                try {
                    Toast.makeText(ChatKFActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 从服务器加载聊天记录
     */
    private void getMessages() {

        if (mIsVisitor) {
            Logger.i("访客端");

            //
            BDCoreApi.getMessagesWithUser(getBaseContext(), mPage, mSize, new BaseCallback() {

                @Override
                public void onSuccess(JSONObject object) {

                    try {
                        JSONArray jsonArray = object.getJSONObject("data").getJSONArray("content");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            mMessageViewModel.insertMessageJson(jsonArray.getJSONObject(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mPullRefreshLayout.finishRefresh();
                    mPage++;
                }

                @Override
                public void onError(JSONObject object) {

                    mPullRefreshLayout.finishRefresh();

                    try {
                        Toast.makeText(ChatKFActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }  else if (mThreadType.equals(BDCoreConstant.THREAD_TYPE_WORKGROUP)) {
            Logger.i("客服端：客服会话 uid:" + mUid);

            // 客服端接口
            BDCoreApi.getMessagesWithUser(getBaseContext(), mUid, mPage, mSize, new BaseCallback() {

                @Override
                public void onSuccess(JSONObject object) {

                    try {
                        JSONArray jsonArray = object.getJSONObject("data").getJSONArray("content");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            mMessageViewModel.insertMessageJson(jsonArray.getJSONObject(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mPullRefreshLayout.finishRefresh();
                    mPage++;
                }

                @Override
                public void onError(JSONObject object) {

                    mPullRefreshLayout.finishRefresh();

                    try {
                        Toast.makeText(ChatKFActivity.this,
                                object.getString("message"),
                                Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 处理thread返回结果
     *
     * @param object
     */
    private void dealWithThread(JSONObject object) {
        //
        try {
            Logger.d("request thread success message: "
                    + object.get("message")
                    + " status_code:" + object.get("status_code"));

            mIsRobot = false;
            int status_code = object.getInt("status_code");
            if (status_code == 200 || status_code == 201) {
                // 创建新会话

                JSONObject message = object.getJSONObject("data");
                mMessageViewModel.insertMessageJson(message);

                JSONObject thread = message.getJSONObject("thread");
                mUUID = thread.getString("tid");
                mThreadEntity.setTid(thread.getString("tid"));
                mThreadEntity.setType(thread.getString("type"));
                mThreadEntity.setTopic(thread.getString("topic"));
                mThreadEntity.setNickname(thread.getJSONObject("visitor").getString("nickname"));
                mThreadEntity.setAvatar(thread.getJSONObject("visitor").getString("avatar"));

//                String threadTopic = BDCoreConstant.BD_THREAD_PREFIX + mUUID;
//                BDMqttApi.subscribeTopic(ChatKFActivity.this, threadTopic);

                if (mCustom != null && mCustom.trim().length() > 0) {
                    sendCommodityMessage(mCustom);
                }

            } else if (status_code == 202) {
                // 提示排队中

                JSONObject message = object.getJSONObject("data");
                mMessageViewModel.insertMessageJson(message);

//                mUUID = message.getJSONObject("thread").getString("tid");
                JSONObject thread = message.getJSONObject("thread");
                mUUID = thread.getString("tid");
                mThreadEntity.setTid(thread.getString("tid"));
                mThreadEntity.setType(thread.getString("type"));
                mThreadEntity.setTopic(thread.getString("topic"));
                mThreadEntity.setNickname(thread.getJSONObject("visitor").getString("nickname"));
                mThreadEntity.setAvatar(thread.getJSONObject("visitor").getString("avatar"));

//                String threadTopic = "thread/" + mUUID;
//                BDMqttApi.subscribeTopic(ChatKFActivity.this, threadTopic);

                if (mCustom != null && mCustom.trim().length() > 0) {
                    sendCommodityMessage(mCustom);
                }

            } else if (status_code == 203) {
                // 当前非工作时间，请自助查询或留言

                JSONObject message = object.getJSONObject("data");
                mMessageViewModel.insertMessageJson(message);

//                mUUID = message.getJSONObject("thread").getString("tid");
                JSONObject thread = message.getJSONObject("thread");
                mUUID = thread.getString("tid");
                mThreadEntity.setTid(thread.getString("tid"));
                mThreadEntity.setType(thread.getString("type"));
                mThreadEntity.setTopic(thread.getString("topic"));
                mThreadEntity.setNickname(thread.getJSONObject("visitor").getString("nickname"));
                mThreadEntity.setAvatar(thread.getJSONObject("visitor").getString("avatar"));

//                String threadTopic = "thread/" + mUUID;
//                BDMqttApi.subscribeTopic(ChatKFActivity.this, threadTopic);

            } else if (status_code == 204) {
                // 当前无客服在线，请自助查询或留言

                JSONObject message = object.getJSONObject("data");
                mMessageViewModel.insertMessageJson(message);

//                mUUID = message.getJSONObject("thread").getString("tid");
                JSONObject thread = message.getJSONObject("thread");
                mUUID = thread.getString("tid");
                mThreadEntity.setTid(thread.getString("tid"));
                mThreadEntity.setType(thread.getString("type"));
                mThreadEntity.setTopic(thread.getString("topic"));
                mThreadEntity.setNickname(thread.getJSONObject("visitor").getString("nickname"));
                mThreadEntity.setAvatar(thread.getJSONObject("visitor").getString("avatar"));

//                String threadTopic = "thread/" + mUUID;
//                BDMqttApi.subscribeTopic(ChatKFActivity.this, threadTopic);

            } else if (status_code == 205) {
                // 咨询前问卷

                String title = "";
                JSONObject message = object.getJSONObject("data");
                mUUID = message.getJSONObject("thread").getString("tid");

                // 存储key/value: content/qid
                final Map<String, String> questionMap = new HashMap<>();
                // 存储key/value: content/workGroups
                final Map<String, JSONArray> workGroupsMap = new HashMap<>();
                //
                List<String> questionContents = new ArrayList<>();

                if (!message.isNull("questionnaire")) {
                    //
                    JSONObject questionnaireObject = message.getJSONObject("questionnaire");
                    if (!questionnaireObject.isNull("questionnaireItems")) {
                        //
                        JSONArray questionItems = questionnaireObject.getJSONArray("questionnaireItems");
                        //
                        for (int i = 0; i < questionItems.length(); i++) {
                            // TODO: 一个questionItem作为一条消息插入
                            JSONObject questionItem = questionItems.getJSONObject(i);
                            title = questionItem.getString("title");

                            JSONArray questionnaireItemItems = questionItem.getJSONArray("questionnaireItemItems");
                            for (int j = 0; j < questionnaireItemItems.length(); j++) {
                                JSONObject questionnaireItemItem = questionnaireItemItems.getJSONObject(j);
                                //
//                                Logger.i("content " + questionnaireItemItem.getString("content"));
                                questionMap.put(questionnaireItemItem.getString("content"), questionnaireItemItem.getString("qid"));
                                workGroupsMap.put(questionnaireItemItem.getString("content"), questionnaireItemItem.getJSONArray("workGroups"));
                                questionContents.add(questionnaireItemItem.getString("content"));
                            }
                        }
                    }
                }

                // 1. 弹窗选择列表：类型、工作组
                final String[] items = questionContents.toArray(new String[0]);
                new QMUIDialog.MenuDialogBuilder(ChatKFActivity.this)
                    .setTitle(title)
                    .addItems(items, (dialog, which) -> {

                        String questionnaireItemItemQid = questionMap.get(items[which]);
                        Logger.i("qid:" + questionnaireItemItemQid + " content:" + items[which]);
                        // 留学: 意向国家 qid = '201810061551181'
                        // 移民：意向国家 qid = '201810061551183'
                        // 语培：意向类别 qid = '201810061551182'
                        // 其他：意向类别 qid = '201810061551184'
                        // 院校：意向院校 qid = '201810061551185'

                        if (questionnaireItemItemQid.equals("201810061551181")) {
                            // 单独处理 留学: 意向国家 qid = '201810061551181'
//                                requestQuestionnaire(questionnaireItemItemQid);
                            showWorkGroupDialog(workGroupsMap.get(items[which]), true);
                        } else {
                            //
                            showWorkGroupDialog(workGroupsMap.get(items[which]), false);
                        }

                        dialog.dismiss();
                    }).show();

            } else if (status_code == 206) {
                // 返回机器人初始欢迎语 + 欢迎问题列表
                mIsRobot = true;

                JSONObject message = object.getJSONObject("data");
                mMessageViewModel.insertMessageJson(message);

//                mUUID = message.getJSONObject("thread").getString("tid");
                JSONObject thread = message.getJSONObject("thread");
                mUUID = thread.getString("tid");
                mThreadEntity.setTid(thread.getString("tid"));
                mThreadEntity.setType(thread.getString("type"));
                mThreadEntity.setTopic(thread.getString("topic"));
                mThreadEntity.setNickname(thread.getJSONObject("visitor").getString("nickname"));
                mThreadEntity.setAvatar(thread.getJSONObject("visitor").getString("avatar"));

//                String threadTopic = "thread/" + mUUID;
//                BDMqttApi.subscribeTopic(ChatKFActivity.this, threadTopic);

            } else {
                // 请求会话失败
                String message = object.getString("message");
                Toast.makeText(ChatKFActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            //
            if (mRequestType.equals(BDCoreConstant.THREAD_REQUEST_TYPE_APPOINTED)) {
                Logger.i("重新加载 指定客服聊天记录");
                initModel();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示选择工作组提示框
     *
     * @param workGroupsArray
     */
    private void showWorkGroupDialog(JSONArray workGroupsArray, boolean isLiuXue) {

        try {

            final Map<String, String> workGroupMap = new HashMap<>();
            List<String> workGroupNames = new ArrayList<>();

            for (int i = 0; i < workGroupsArray.length(); i++) {

                JSONObject workGroupObject = workGroupsArray.getJSONObject(i);
                workGroupMap.put(workGroupObject.getString("nickname"), workGroupObject.getString("wid"));
                workGroupNames.add(workGroupObject.getString("nickname"));
            }

            // 1. 弹窗选择列表：工作组
            final String[] items = workGroupNames.toArray(new String[0]);
            new QMUIDialog.MenuDialogBuilder(ChatKFActivity.this)
                    .setTitle("请选择")
                    .addItems(items, (dialog, which) -> {
                        //
                        String workGroupNickname = items[which];
                        String workGroupWid = workGroupMap.get(items[which]);
                        Logger.i("nickname:" + items[which] + " workGroupWid:" + workGroupWid);
                        //
                        if (isLiuXue) {
                            chooseWorkGroupLiuXue(workGroupWid, workGroupNickname);
                        } else {
                            chooseWorkGroup(workGroupWid);
                        }

                        dialog.dismiss();
                    }).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 问卷答案中选择工作组
     *
     * @param workGroupWid
     */
    private void chooseWorkGroup(final String workGroupWid) {

        BDCoreApi.chooseWorkGroup(this, workGroupWid,  new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {
                // 重新选择工作组成功 old wid:201807171659201 new wid:201810201758121
                Logger.i("重新选择工作组成功 old wid:" + mWorkGroupWid + " new wid:" + workGroupWid);
                // 重新初始化model，根据新的wid加载聊天记录
                mWorkGroupWid = workGroupWid;
                Logger.i("mWorkGroupWid:" + mWorkGroupWid);

                initModel();
                //
                dealWithThread(object);
            }

            @Override
            public void onError(JSONObject object) {
                try {
                    Toast.makeText(ChatKFActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 留学，针对大学长定制
     *
     * @param workGroupWid
     * @param workGroupNickname
     */
    private void chooseWorkGroupLiuXue(final String workGroupWid, String workGroupNickname) {

        // LBS版本，传入当前用户所在 省份 和 城市名
        BDCoreApi.chooseWorkGroupLiuXueLBS(this, workGroupWid, workGroupNickname, "辽宁", "大连",  new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    String newWid = object.getJSONObject("data").getString("wid");

                    // 重新选择工作组成功 old wid:201807171659201 new wid:201810201758121
                    Logger.i("重新选择工作组成功 old wid:" + mWorkGroupWid + " new wid:" + newWid);

                    // 重新初始化model，根据新的wid加载聊天记录
                    mWorkGroupWid = newWid;
                    Logger.i("mWorkGroupWid:" + mWorkGroupWid);

                    initModel();
                    //
                    dealWithThread(object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(JSONObject object) {
                try {
                    Toast.makeText(ChatKFActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 从相册中选择图片
     *
     * FIXME: 当前版本，禁止选择视频文件
     *
     */
    private void pickImageFromAlbum() {

        // 目前仅允许一次选一张图片
        Album.image(this)
                .singleChoice()
                .camera(false)
                .onResult(result -> {

                    if (result.size() > 0) {
                        AlbumFile albumFile = result.get(0);

                        String imageName = mPreferenceManager.getUsername() + "_" + BDCoreUtils.getPictureTimestamp();
                        uploadImage(albumFile.getPath(), imageName);
                    }
                })
                .onCancel(result -> {
                    //
                    Toast.makeText(ChatKFActivity.this, "取消发送图片", Toast.LENGTH_LONG).show();
                })
                .start();

        // TODO: 待删除
//        Intent intent;
//        if (Build.VERSION.SDK_INT < 19) {
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//        } else {
//            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        }
//        startActivityForResult(intent, BDUiConstant.SELECT_PIC_BY_PICK_PHOTO);
    }

    /**
     * 摄像头拍摄图片
     *
     * FIXME: 当前版本，禁止拍摄视频，仅支持拍照
     */
    private void takeCameraImage() {

        // TODO: 判断是否模拟器，如果是，则弹出tip提示，并返回

        // 调用第三方库album
        Album.camera(this)
                .image()
                .onResult(result -> {

                    String imageFileName = mPreferenceManager.getUsername() + "_" + BDCoreUtils.getPictureTimestamp();
                    uploadImage(result, imageFileName);
                })
                .onCancel(result -> {
                    //
                    Toast.makeText(ChatKFActivity.this, "取消拍照", Toast.LENGTH_LONG).show();
                })
                .start();

        // TODO: 待删除
//        if (BDCoreUtils.isSDCardExist()) {
//            //
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            mImageCaptureFileName = mPreferenceManager.getUsername() + "_" + BDCoreUtils.getPictureTimestamp();
//            mPhotoUri = BDCoreUtils.getUri(BDCoreUtils.getTempImage(mImageCaptureFileName), this);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
//            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mPhotoUri);
//            startActivityForResult(intent, BDUiConstant.SELECT_PIC_BY_TAKE_PHOTO);
//        }
//        else {
//            Toast.makeText(this, "SD卡不存在，不能拍照", Toast.LENGTH_SHORT).show();
//        }

    }


    private void visitorTopRightSheet() {
        //
        Context contextActivity = this;
        new QMUIBottomSheet.BottomListSheetBuilder(this)
                .addItem("评价")
                .addItem("留言")
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    //
                    dialog.dismiss();

                    if (position == 0) {

                        final String[] items = new String[]{"非常满意", "满意", "一般", "不满意", "非常差"};
                        final int checkedIndex = 0;
                        new QMUIDialog.CheckableDialogBuilder(contextActivity)
                                .setCheckedIndex(checkedIndex)
                                .addItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), "评分：" + (5-which), Toast.LENGTH_SHORT).show();

                                        // TODO: 暂未增加评价页面，开发者可自行设计。具体参数意思可跟进函数定义查看
                                        BDCoreApi.rate(contextActivity, mUUID, 5 - which, "附言", false, new BaseCallback() {
                                            @Override
                                            public void onSuccess(JSONObject object) {

                                            }
                                            @Override
                                            public void onError(JSONObject object) {

                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                })
                                .create(mCurrentDialogStyle)
                                .show();

                    } else if (position == 1) {

                        // 跳转留言页面
                        Intent intent = new Intent(ChatKFActivity.this, LeaveMessageActivity.class);
                        intent.putExtra(BDUiConstant.EXTRA_WID, mWorkGroupWid);
                        intent.putExtra(BDUiConstant.EXTRA_REQUEST_TYPE, mRequestType);
                        intent.putExtra(BDUiConstant.EXTRA_AID, mAgentUid);
                        startActivity(intent);

                    }
                })
                .build()
                .show();
    }

    /**
     * 客服端点击右上角按钮
     */
    private void showTopRightSheet() {
        new QMUIBottomSheet.BottomListSheetBuilder(this)
                .addItem("关闭会话")
//                .addItem("访客资料") // TODO: 查看访客资料
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    dialog.dismiss();
                    //
                    BDCoreApi.agentCloseThread(getApplication(), mUUID, new BaseCallback() {

                        @Override
                        public void onSuccess(JSONObject object) {
                            // 关闭页面
                            finish();
                        }

                        @Override
                        public void onError(JSONObject object) {
                            Toast.makeText(getApplication(), "关闭会话错误", Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .build()
                .show();
    }

    /**
     * 请求相册读取权限
     */
    private void requestAlbumPermission() {

        // android 6.0动态授权机制
        // http://jijiaxin89.com/2015/08/30/Android-s-Runtime-Permission/
        // http://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // 首先提示用户，待确认之后，请求用户授权
                new QMUIDialog.MessageDialogBuilder(this)
                        .setTitle("请求授权")
                        .setMessage("相册需要授权，请授权")
                        .addAction("取消", (dialog, index) -> dialog.dismiss())
                        .addAction("确定", (dialog, index) -> {
                            dialog.dismiss();
                            // 请求授权
                            ActivityCompat.requestPermissions(ChatKFActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    BDUiConstant.PERMISSION_REQUEST_ALBUM);
                        }).show();
            }
            else {
                pickImageFromAlbum();
            }
        }
        else {
            pickImageFromAlbum();
        }
    }

    /**
     * 请求摄像头权限
     */
    private void requestCameraPermission() {

        // android 6.0动态授权机制
        // http://jijiaxin89.com/2015/08/30/Android-s-Runtime-Permission/
        // http://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

                // 首先提示用户，待确认之后，请求用户授权
                new QMUIDialog.MessageDialogBuilder(this)
                        .setTitle("请求授权")
                        .setMessage("拍照需要授权，请授权")
                        .addAction("取消", (dialog, index) -> dialog.dismiss())
                        .addAction("确定", (dialog, index) -> {
                            dialog.dismiss();
                            // 请求授权
                            ActivityCompat.requestPermissions(ChatKFActivity.this,
                                    new String[] { Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    BDUiConstant.PERMISSION_REQUEST_CAMERA);
                        }).show();
            }
            else {
                takeCameraImage();
            }
        }
        else {
            takeCameraImage();
        }
    }

    /**
     * 点击图片消息回调
     */
    @Override
    public void onMessageImageItemClick(String imageUrl) {
        Logger.d("imageUrl:" + imageUrl);

//        Glide.with(this).load(imageUrl).into(new SimpleTarget<Drawable>() {
//            @Override
//            public void onLoadCleared(@Nullable Drawable placeholder) {
//                super.onLoadCleared(placeholder);
//                imagePreview.getImageView().setImageDrawable(placeholder);
//            }
//
//            @Override
//            public void onLoadStarted(@Nullable Drawable placeholder) {
//                super.onLoadStarted(placeholder);
//                imagePreview.showProgess();
//                imagePreview.getImageView().setImageDrawable(placeholder);
//            }
//
//            @Override
//            public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                super.onLoadFailed(errorDrawable);
//                imagePreview.hideProgress();
//                imagePreview.getImageView().setImageDrawable(errorDrawable);
//            }
//
//            @Override
//            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                if (resource != null) {
//                    imagePreview.hideProgress();
//                    imagePreview.getImageView().setImageDrawable(resource);
////                    mViewData.setImageWidth(resource.getIntrinsicWidth());
////                    mViewData.setImageHeight(resource.getIntrinsicHeight());
//                }
//            }
//        });
//        imagePreview.setViewData(viewData);
//        imagePreview.start();

        Intent intent = new Intent(this, BigImageViewActivity.class);
        intent.putExtra("image_url", imageUrl);
        startActivity(intent);
    }

    /**
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case BDUiConstant.PERMISSION_REQUEST_RECORD_AUDIO:
                if (BDPermissionUtils.verifyPermissions(grantResults)) {
                    // Permission Granted, 录音
                }
                else {
                    // Permission Denied
                    Toast.makeText(this, "录音授权失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case BDUiConstant.PERMISSION_REQUEST_CAMERA:
                if (BDPermissionUtils.verifyPermissions(grantResults)) {
                    // Permission Granted
                    takeCameraImage();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "拍照授权失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case BDUiConstant.PERMISSION_REQUEST_ALBUM:
                if (BDPermissionUtils.verifyPermissions(grantResults)) {
                    // Permission Granted
                    pickImageFromAlbum();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "相册授权失败", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    /**
     * 发送文本消息
     *
     * @param content
     */
    private void sendTextMessage(String content) {

        if (!BDMqttApi.isConnected(this)) {
            Toast.makeText(this, "网络断开，请稍后重试", Toast.LENGTH_LONG).show();
            return;
        }

        // 自定义本地消息id，用于判断消息发送状态. 消息通知或者回调接口中会返回此id
        final String localId = BDCoreUtils.uuid();

        // 插入本地消息
        mRepository.insertTextMessageLocal(mUUID, mWorkGroupWid, mUid, content, localId, mThreadType);
        // 发送消息方式有两种：1. 异步发送消息，通过监听通知来判断是否发送成功，2. 同步发送消息，通过回调判断消息是否发送成功
        //
        BDMqttApi.sendTextMessageProtobuf(this, localId, content, mThreadEntity);
//        BDMqttApi.sendTextMessageProtobuf(this, localId, content,
//                mUUID, mThreadEntity.getTopic(), mThreadEntity.getType(), mThreadEntity.getNickname(), mThreadEntity.getAvatar());
    }

    /**
     * 上传并发送图片
     *
     *  {"message":"upload image",
     *  "status_code":200,
     *  "data":"http://chainsnow.oss-cn-shenzhen.aliyuncs.com/images/201808281417141_20180829105542.jpg"}
     *
     * @param filePath
     * @param fileName
     */
    private void uploadImage(String filePath, String fileName) {

        if (mIsRobot) {
            Toast.makeText(this, "机器人暂时不支持图片", Toast.LENGTH_LONG).show();
            return;
        }

        BDCoreApi.uploadImage(this, filePath, fileName, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    // TODO: 无客服在线时，禁止发送图片

                    // TODO: 收到客服关闭会话 或者 自动关闭会话消息之后，禁止访客发送消息

                    // 自定义本地消息id，用于判断消息发送状态。消息通知或者回调接口中会返回此id
                    final String localId = BDCoreUtils.uuid();

                    String imageUrl = object.getString("data");

                    // 插入本地消息
                    mRepository.insertImageMessageLocal(mUUID, mWorkGroupWid, mUid, imageUrl, localId, mThreadType);

                    // 发送消息方式有两种：1. 异步发送消息，通过监听通知来判断是否发送成功，2. 同步发送消息，通过回调判断消息是否发送成功

                    BDMqttApi.sendImageMessageProtobuf(ChatKFActivity.this, localId, imageUrl, mThreadEntity);
//                    BDMqttApi.sendImageMessageProtobuf(ChatKFActivity.this, localId, imageUrl,
//                            mUUID, mThreadEntity.getTopic(), mThreadEntity.getType(), mThreadEntity.getNickname(), mThreadEntity.getAvatar());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {
                Toast.makeText(getApplicationContext(), "上传图片失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * TODO: 区分是否当前会话
     *
     * @param previewEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPreviewEvent(PreviewEvent previewEvent) {
        Logger.i("onPreviewEvent");

        if (previewEvent.getContent().trim().length() == 0) {
            return;
        }

        if (mIsVisitor) {
            mTopBar.setTitle("对方正在输入...");
        } else {
            mTopBar.setTitle("对方正在输入:" + previewEvent.getContent());
        }
        //
        mHandler.postDelayed(() -> {
            //
            mTopBar.setTitle(mTitle);
        }, 3000);
    }

    /**
     * 监听 EventBus 广播消息: 发送商品信息
     *
     * @param sendCommodityEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendCommodityEvent(SendCommodityEvent sendCommodityEvent) {
        Logger.i("SendCommodityEvent");

        if (mCustom != null && mCustom.trim().length() > 0) {
            sendCommodityMessage(mCustom);
        }
    }

    /**
     * 监听 EventBus 广播消息: 点击智能问答消息记录上面的问题
     *
     * @param queryAnswerEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQueryAnswerEvent(QueryAnswerEvent queryAnswerEvent) {
        Logger.i("QueryAnswerEvent aid: %s, question %s", queryAnswerEvent.getAid(), queryAnswerEvent.getQuestion());

        if (queryAnswerEvent.getAid().equals("00001")) {

            // 请求人工客服
            requestAgent();

        } else {

            // 请求服务器答案
            BDCoreApi.queryAnswer(this, mUUID, queryAnswerEvent.getAid(), new BaseCallback() {

                @Override
                public void onSuccess(JSONObject object) {
                    //
                    try {

                        int status_code = object.getInt("status_code");
                        if (status_code == 200) {
                            //
                            JSONObject queryMessageObject = object.getJSONObject("data").getJSONObject("query");
                            JSONObject replyMessageObject = object.getJSONObject("data").getJSONObject("reply");

                            // TODO: 答案中添加 '有帮助'、'无帮助'，访客点击可反馈答案是否有用

                            //持久化到数据库
                            mRepository.insertMessageJson(queryMessageObject);
                            mRepository.insertMessageJson(replyMessageObject);
//                            mRepository.insertRobotRightAnswerMessageJson(replyMessageObject);

                        } else {

                            // 发送消息失败
                            String message = object.getString("message");
                            Toast.makeText(ChatKFActivity.this, message, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(JSONObject object) {

                    try {
                        Logger.d("robot message: " + object.get("message") + " status_code:" + object.get("status_code") + " data:" + object.get("data"));
                        Toast.makeText(ChatKFActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });
        }

    }

    /**
     * 下拉刷新
     */
    private QMUIPullRefreshLayout.OnPullListener pullListener = new QMUIPullRefreshLayout.OnPullListener() {

        @Override
        public void onMoveTarget(int offset) {

        }

        @Override
        public void onMoveRefreshView(int offset) {

        }

        @Override
        public void onRefresh() {
            Logger.i("refresh");
            getMessages();
        }
    };

    /**
     * 监听输入框
     */
    private TextWatcher inputTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // TODO: 查询常见问题，并高亮提示
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
            String content = editable.toString();
            if (content != null) {
                // 输入框文字变化时，发送消息输入状态消息
                BDMqttApi.sendPreviewMessageProtobuf(ChatKFActivity.this, mThreadEntity, content);
//                BDMqttApi.sendPreviewMessage(ChatKFActivity.this, mUUID, content, mThreadType);
            }
        }
    };

    /**
     * 发送商品消息等
     * @param custom
     */
    private void sendCommodityMessage(String custom) {

        // 自定义本地消息id，用于判断消息发送状态. 消息通知或者回调接口中会返回此id
        final String localId = BDCoreUtils.uuid();
        // 插入本地消息
        mRepository.insertCommodityMessageLocal(mUUID, mWorkGroupWid, mUid, custom, localId, mThreadType);
        //
        BDMqttApi.sendCommodityMessageProtobuf(this, localId, custom, mThreadEntity);
    }

    @Override
    public void onKeyboardHidden() {
//        Logger.i("onKeyboardHidden");

    }

    @Override
    public void onKeyboardShown() {
//        Logger.i("onKeyboardShown");
        mRecyclerView.scrollToPosition(mChatAdapter.getItemCount() - 1);
    }

    //    private void requestQuestionnaire(String questionnaireItemItemQid) {
//
//        BDCoreApi.requestQuestionnaire(this, mUUID, questionnaireItemItemQid, new BaseCallback() {
//
//            @Override
//            public void onSuccess(JSONObject object) {
//
//                try {
//
//                    JSONObject message = object.getJSONObject("data");
//
//                    int status_code = object.getInt("status_code");
//                    if (status_code == 200) {
//
////                      String  title = message.getString("content");
//                        if (!message.isNull("workGroups")) {
//
//                            JSONArray workGroupsArray = message.getJSONArray("workGroups");
//                            showWorkGroupDialog(workGroupsArray);
//                        }
//
//                    } else {
//
//                        //
//                        String toast = object.getString("message");
//                        Toast.makeText(ChatKFActivity.this, toast, Toast.LENGTH_LONG).show();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(JSONObject object) {
//                try {
//                    Toast.makeText(ChatKFActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

}


