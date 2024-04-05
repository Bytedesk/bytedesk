package com.bytedesk.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.api.BDMqttApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.event.PreviewEvent;
import com.bytedesk.core.event.TransferAcceptEvent;
import com.bytedesk.core.event.TransferEvent;
import com.bytedesk.core.event.TransferRejectEvent;
import com.bytedesk.core.repository.BDRepository;
import com.bytedesk.core.util.BDCoreConstant;
import com.bytedesk.core.util.BDCoreUtils;
import com.bytedesk.core.util.BDFileUtils;
import com.bytedesk.core.util.BDPreferenceManager;
import com.bytedesk.core.viewmodel.MessageViewModel;
import com.bytedesk.ui.R;
import com.bytedesk.ui.adapter.ChatAdapter;
import com.bytedesk.ui.adapter.EmotionViewPagerAdapter;
import com.bytedesk.ui.listener.ChatItemClickListener;
import com.bytedesk.ui.recorder.KFRecorder;
import com.bytedesk.ui.recorder.KFRecorderService;
import com.bytedesk.ui.recorder.KFRemainingTimeCalculator;
import com.bytedesk.ui.util.BDPermissionUtils;
import com.bytedesk.ui.util.BDUiConstant;
import com.bytedesk.ui.util.BDUiUtils;
import com.bytedesk.ui.util.EmotionMaps;
import com.bytedesk.ui.util.EmotionMapsGroup;
import com.bytedesk.ui.util.ExpressionUtil;
import com.bytedesk.ui.widget.InputAwareLayout;
import com.bytedesk.ui.widget.KeyboardAwareLinearLayout;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  用途：
 *  1. 1v1、群聊 聊天界面
 *
 *  TODO:
 *    1. 访客关闭会话窗口的时候通知客服
 *    2. 客服端关闭会话之后，禁止访客继续发送消息
 *
 * @author bytedesk.com
 */
public class ChatIMGroupActivity extends ChatBaseActivity implements ChatItemClickListener,
        View.OnClickListener,
        View.OnTouchListener,
        ViewPager.OnPageChangeListener,
        AdapterView.OnItemClickListener,
        View.OnFocusChangeListener, KFRecorder.OnStateChangedListener,
        KeyboardAwareLinearLayout.OnKeyboardShownListener,
        KeyboardAwareLinearLayout.OnKeyboardHiddenListener {

    private InputAwareLayout mInputAwaireLayout;
    private QMUITopBarLayout mTopBar;
    private QMUIPullRefreshLayout mPullRefreshLayout;
    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;

    // 是否从会话列表进入
    private boolean mIsFromThread = false;
    // 是否是机器人会话
    private boolean mIsRobot = false;
    // 常用语
    private Button mCuwButton;
    // 切换文字、录音按钮
    private Button mVoiceButton;
    // 按住说话
    private Button mRecordVoiceButton;
    // 显示表情
    private Button mEmotionButton;
    // 显示扩展按钮
    private Button mPlusButton;
    // 发送文本消息按钮
    private Button mSendButton;
    // 输入框
    private EditText mInputEditText;

    private boolean mIsRecording = false;
    // 录音整体UI
    private LinearLayout mRecordVoiceLayout;
    // 正在录音
    private LinearLayout mRecordVoiceHintLayout;
    private LinearLayout mRecordVoiceCancelHintLayout;
    // 取消录音
    private LinearLayout mRecordVoiceTextLayout;
    private LinearLayout mRecordVoiceCancelTextLayout;
    // 音量
    private ImageView mRecordVoiceHintAMPImageView;
    // ////////////////////录音机////////////////////////
    private boolean m_voiceRecordRequestCanBeChanged = false;
    private KFRecorder m_voiceRecorder;
    private RecorderReceiver m_voiceRecordReceiver;
    private boolean m_voiceRecordShowFinishButton = false;
    // 设置为-1，表示大小无限制
    private long m_voiceRecordMaxFileSize = -1;
    private KFRemainingTimeCalculator m_voiceRecordRemainingTimeCalculator;
    private String m_voiceRecordingVoiceFileName;
//    private String m_imageCaptureFileName;
    // 录音开始和结束时间戳
    private long m_startRecordingTimestamp, m_endRecordingTimestamp;
    private int m_recordedVoiceLength;
    private Handler m_voiceRecordHandler = new Handler();
    private static final int VOICE_RECORDING_REFRESH_AMP_INTERVAL = 100;
    private static final int CHECK_RECORD_AUDIO_PERMISSION = 5;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    //
    private QMUIPopup mListPopup;

    // 表情
    public RelativeLayout mEmotionLayout;
    private ViewPager mEmotionViewPager;
    private EmotionViewPagerAdapter mEmotionViewPagerAdapter;
    private EmotionMapsGroup mEmotionMaps;

    // 表情pager indicator
    private int mCurrentEmotionViewPagerIndex;
    private ImageView mEmotionViewPagerIndicator1;
    private ImageView mEmotionViewPagerIndicator2;
    private ImageView mEmotionViewPagerIndicator3;
    private ImageView mEmotionViewPagerIndicator4;
    private ImageView mEmotionViewPagerIndicator5;
    // 扩展
    public LinearLayout mExtensionLayout;
    // Model
    private MessageViewModel mMessageViewModel;
    // 根据会话类型不同所代表意义不同：
//    private String mUid;
    // 工作组wid
    private String mWorkGroupWid = "";
    //
    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_chat_im_group);
        //
        if (null != getIntent()) {
            //
            mPreferenceManager = BDPreferenceManager.getInstance(this);
            mRepository = BDRepository.getInstance(this);
            //
            mIsFromThread = getIntent().getBooleanExtra(BDUiConstant.EXTRA_IS_THREAD, false);
            if (mIsFromThread) {
                // 从会话列表进入聊天页面
                mThreadEntity.setTid(getIntent().getStringExtra(BDUiConstant.EXTRA_THREAD_TID));
                mThreadEntity.setTopic(getIntent().getStringExtra(BDUiConstant.EXTRA_THREAD_TOPIC));
                mThreadEntity.setType(getIntent().getStringExtra(BDUiConstant.EXTRA_THREAD_TYPE));
                mThreadEntity.setClient(getIntent().getStringExtra(BDUiConstant.EXTRA_THREAD_CLIENT));
                mThreadEntity.setNickname(getIntent().getStringExtra(BDUiConstant.EXTRA_THREAD_NICKNAME));
                mThreadEntity.setAvatar(getIntent().getStringExtra(BDUiConstant.EXTRA_THREAD_AVATAR));
                //
                mUUID = mThreadEntity.getTid();
                mTopic = mThreadEntity.getTopic();
                mClient = mThreadEntity.getClient();
                mTitle = mThreadEntity.getNickname();
                mThreadType = mThreadEntity.getType();
                //
                mPreferenceManager.setCurrentThreadTid(mThreadEntity.getTid());
            } else {
                // 从联系人详情页面进入聊天页面
                mUUID = getIntent().getStringExtra(BDUiConstant.EXTRA_UUID);
                mTitle = getIntent().getStringExtra(BDUiConstant.EXTRA_TITLE);
                mThreadType = getIntent().getStringExtra(BDUiConstant.EXTRA_THREAD_TYPE);
                //
            }
        }
        //
        initTopBar();
        initView();
        initModel();
        initRecorder(savedInstanceState);
        // 从服务器加载thread
        if (!mIsFromThread) {
            requestThread();
        }
        // 从服务器端加载聊天记录，默认暂不加载
        getMessages();
    }

    @Override
    public void onStart() {
        super.onStart();
        //
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addDataScheme("file");
        registerReceiver(m_voiceRecordSDCardMountEventReceiver, iFilter);

        // 注册监听
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // ///////////////////////////////////////////////////
        if (m_voiceRecordRequestCanBeChanged) {
            m_voiceRecorder.reset();
        }

        m_voiceRecordRequestCanBeChanged = false;
        if (!m_voiceRecorder.syncStateWithService()) {
            m_voiceRecorder.reset();
        }

        if (m_voiceRecorder.state() == KFRecorder.RECORDING_STATE) {
            if (!m_voiceRecorder.sampleFile().getName()
                    .endsWith(BDCoreConstant.EXT_AMR)) {
                m_voiceRecorder.reset();
            } else {
                m_voiceRecordRemainingTimeCalculator
                        .setBitRate(BDCoreConstant.BITRATE_AMR);
            }
        } else {
            File file = m_voiceRecorder.sampleFile();
            if (file != null && !file.exists()) {
                m_voiceRecorder.reset();
            }
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(KFRecorderService.RECORDER_SERVICE_BROADCAST_NAME);
        registerReceiver(m_voiceRecordReceiver, filter);

        if (KFRecorderService.isRecording()) {
            Intent intent = new Intent(this, KFRecorderService.class);
            intent.putExtra(KFRecorderService.ACTION_NAME,
                    KFRecorderService.ACTION_DISABLE_MONITOR_REMAIN_TIME);
            startService(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (m_voiceRecorder.state() != KFRecorder.RECORDING_STATE
                || m_voiceRecordShowFinishButton
                || m_voiceRecordMaxFileSize != -1) {
            m_voiceRecorder.stop();
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .cancel(KFRecorderService.NOTIFICATION_ID);
        }

        if (m_voiceRecordReceiver != null) {
            unregisterReceiver(m_voiceRecordReceiver);
        }

        m_voiceRecordRequestCanBeChanged = true;
        if (KFRecorderService.isRecording()) {
            Intent intent = new Intent(this, KFRecorderService.class);
            intent.putExtra(KFRecorderService.ACTION_NAME, KFRecorderService.ACTION_ENABLE_MONITOR_REMAIN_TIME);
            startService(intent);
        }
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
        if (m_voiceRecordSDCardMountEventReceiver != null) {
            unregisterReceiver(m_voiceRecordSDCardMountEventReceiver);
            m_voiceRecordSDCardMountEventReceiver = null;
        }
        //主动回收
        Runtime.getRuntime().gc();
    }

    /**
     * TODO: 客服端输入框显示常用回复按钮
     * @param view
     */
    @Override
    public void onClick(View view) {
        //
        if (view.getId() == R.id.bytedesk_chat_input_send_button) {
            //
            final String content = mInputEditText.getText().toString();
            if (content.trim().length() > 0) {
                String textContent = ExpressionUtil.faceToCN(this, content);
//                Logger.i("faceToCn: " + textContent);
                sendTextMessage(textContent);
                mInputEditText.setText(null);
            }
        }
        else if (view.getId() == R.id.bytedesk_chat_input_plus_button) {
            BDUiUtils.showSysSoftKeybord(this, mInputEditText, false);
            if (mExtensionLayout.getVisibility() == View.VISIBLE) {
                mExtensionLayout.setVisibility(View.GONE);
            } else {
                mExtensionLayout.setVisibility(View.VISIBLE);
                mEmotionLayout.setVisibility(View.GONE);
            }

        } else if (view.getId() == R.id.bytedesk_chat_input_emotion_button) {
            BDUiUtils.showSysSoftKeybord(this, mInputEditText, false);
            if (mEmotionLayout.getVisibility() == View.VISIBLE) {
                mEmotionLayout.setVisibility(View.GONE);
            } else {
                mEmotionLayout.setVisibility(View.VISIBLE);
                mExtensionLayout.setVisibility(View.GONE);
                mRecordVoiceButton.setVisibility(View.GONE);
                mInputEditText.setVisibility(View.VISIBLE);
            }

        } else if (view.getId() == R.id.bytedesk_chat_input_voice_button) {
            BDUiUtils.showSysSoftKeybord(this, mInputEditText, false);
            if (mRecordVoiceButton.getVisibility() == View.VISIBLE) {
                mRecordVoiceButton.setVisibility(View.GONE);
                mInputEditText.setVisibility(View.VISIBLE);
            } else {
                mRecordVoiceButton.setVisibility(View.VISIBLE);
                mInputEditText.setVisibility(View.GONE);
            }
            mEmotionLayout.setVisibility(View.GONE);
            mExtensionLayout.setVisibility(View.GONE);

        } else if (view.getId() == R.id.bytedesk_chat_input_cuw_button) {
            BDUiUtils.showSysSoftKeybord(this, mInputEditText, false);
            mEmotionLayout.setVisibility(View.GONE);
            mExtensionLayout.setVisibility(View.GONE);
            // TODO: 回调常用语列表
            Intent intent = new Intent(ChatIMGroupActivity.this, CuwActivity.class);
            startActivityForResult(intent, BDUiConstant.SELECT_CUW);
//            registerForActivityResult

        }  else if (view.getId() == R.id.appkefu_plus_pick_picture_btn) {

            // TODO: 收到客服关闭会话 或者 自动关闭会话消息之后，禁止访客发送消息
            pickImageFromAlbum();

        } else if (view.getId() == R.id.appkefu_plus_take_picture_btn) {

            // TODO: 收到客服关闭会话 或者 自动关闭会话消息之后，禁止访客发送消息
            takeCameraImage();

        } else if (view.getId() == R.id.appkefu_plus_show_red_packet_btn) {

            final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(ChatIMGroupActivity.this);
            builder.setTitle("发送红包")
                .setPlaceholder("在此输入金额")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {

                    final CharSequence text = builder.getEditText().getText();
                    if (text != null && text.length() > 0) {

                        // TODO: 检查是否有效数字？
                        sendRedPacketMessage(text.toString());

                        dialog.dismiss();
                    } else {
                        Toast.makeText(ChatIMGroupActivity.this, "请填入金额", Toast.LENGTH_SHORT).show();
                    }

                }).show();

        } else if (view.getId() == R.id.appkefu_plus_file_btn) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "选择文件"), BDUiConstant.SELECT_FILE);

        } else if (view.getId() == R.id.appkefu_read_destroy_btn) {

            toggleDestroyAfterReading();

        } else if (view.getId() == R.id.appkefu_plus_shop_btn) {

            Toast.makeText(this, "自定义跳转页面，选择商品，发送", Toast.LENGTH_LONG).show();

            // TODO: 自定义跳转页面，选择商品，发送
//            JsonCustom jsonCustom = new JsonCustom();
//            jsonCustom.setType(BDCoreConstant.MESSAGE_TYPE_COMMODITY);
//            jsonCustom.setTitle("商品标题");
//            jsonCustom.setContent("商品详情");
//            jsonCustom.setPrice("9.99");
//            jsonCustom.setUrl("https://item.m.jd.com/product/12172344.html");
//            jsonCustom.setImageUrl("https://m.360buyimg.com/mobilecms/s750x750_jfs/t4483/332/2284794111/122812/4bf353/58ed7f42Nf16d6b20.jpg!q80.dpg");
//            jsonCustom.setId(100121);
//            jsonCustom.setCategoryCode("100010003");
//            //
//            String custom = new Gson().toJson(jsonCustom);
//            sendCommodityMessage(custom);
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (view.getId() == R.id.bytedesk_chat_input_record_voice_button) {
            //
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (!mIsRecording)
                    return false;

                mRecordVoiceHintLayout.setVisibility(View.GONE);
                m_endRecordingTimestamp = System.currentTimeMillis();
                stopVoiceRecording();

                // 发送录音
                if (motionEvent.getY() >= 0) {

                    m_recordedVoiceLength = (int) (m_endRecordingTimestamp - m_startRecordingTimestamp) / 1000;
                    if (m_recordedVoiceLength < 1) {
                        Toast.makeText(ChatIMGroupActivity.this, R.string.kfds_record_voice_too_short, Toast.LENGTH_LONG).show();
                    } else if (m_recordedVoiceLength > 60) {
                        Toast.makeText(ChatIMGroupActivity.this, R.string.kfds_record_voice_too_long, Toast.LENGTH_LONG).show();
                    } else {
                        //
                        if (mIsRobot) {
                            Toast.makeText(ChatIMGroupActivity.this, R.string.kfds_robot_cannot_send_voice, Toast.LENGTH_LONG).show();
                            return false;
                        }
                        //
                        String filePath = BDFileUtils.getVoiceWritePath(m_voiceRecordingVoiceFileName + BDCoreConstant.EXT_AMR);
                        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                        Logger.i("filePath:" + filePath + " fileName: " + fileName);

                        // TODO: 上传语音
                        uploadVoice(filePath, fileName, m_recordedVoiceLength);
                    }

                }

            } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                // android 6.0动态授权机制
                // http://jijiaxin89.com/2015/08/30/Android-s-Runtime-Permission/
                // http://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
                if (Build.VERSION.SDK_INT >= 23) {
//					int checkRecordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
//					int checkRecordAudioPermission = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        new AlertDialog.Builder(ChatIMGroupActivity.this)
                                .setMessage(getString(R.string.kfds_record_permission_tip))
                                .setPositiveButton(
                                        getString(R.string.kfds_ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ActivityCompat.requestPermissions(ChatIMGroupActivity.this,
                                                        new String[] { Manifest.permission.RECORD_AUDIO,
                                                                Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                                        CHECK_RECORD_AUDIO_PERMISSION);
                                            }
                                        })
                                .setNegativeButton(getString(R.string.kfds_cancel), null)
                                .create().show();
                    } else {
                        startVoiceRecording();
                    }
                } else {
                    startVoiceRecording();
                }
            }
        }

        // 切换效果：按住录音按钮，然后上滑出按钮，然后退回按住录音按钮
        if (motionEvent.getY() < 0) {
            mRecordVoiceHintLayout.setVisibility(View.GONE);
            mRecordVoiceCancelHintLayout.setVisibility(View.VISIBLE);
            mRecordVoiceTextLayout.setVisibility(View.GONE);
            mRecordVoiceCancelTextLayout.setVisibility(View.VISIBLE);
        } else {
            mRecordVoiceHintLayout.setVisibility(View.VISIBLE);
            mRecordVoiceCancelHintLayout.setVisibility(View.GONE);
            mRecordVoiceTextLayout.setVisibility(View.VISIBLE);
            mRecordVoiceCancelTextLayout.setVisibility(View.GONE);
        }

        return false;
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
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> finish());

        if (mThreadType.equals(BDCoreConstant.MESSAGE_SESSION_TYPE_CONTACT)) {
            // 一对一会话
            mTopBar.addRightImageButton(R.mipmap.icon_topbar_overflow, QMUIViewHelper.generateViewId())
                    .setOnClickListener(view -> {
                        //
                        Intent intent = new Intent(ChatIMGroupActivity.this, ContactProfileActivity.class);
                        intent.putExtra(BDUiConstant.EXTRA_UID, mUUID);
                        startActivity(intent);
                    });

        } else if (mThreadType.equals(BDCoreConstant.MESSAGE_SESSION_TYPE_GROUP)) {
            // 群组
            mTopBar.addRightImageButton(R.mipmap.icon_topbar_overflow, QMUIViewHelper.generateViewId())
                    .setOnClickListener(view -> {
                        //
                        Intent intent = new Intent(ChatIMGroupActivity.this, GroupProfileActivity.class);
                        intent.putExtra(BDUiConstant.EXTRA_UID, mUUID);
                        startActivity(intent);
                    });
        } else {
            // 客服会话
            mTopBar.addRightImageButton(R.mipmap.icon_topbar_overflow, QMUIViewHelper.generateViewId()).setOnClickListener(view -> {
                initListPopupIfNeed();
                mListPopup.animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);
                mListPopup.preferredDirection(QMUIPopup.DIRECTION_TOP);
                mListPopup.show(view);
            });
        }
        QMUIStatusBarHelper.translucent(this);
    }

    /**
     * 界面初始化
     */
    private void initView () {
        //
        mInputAwaireLayout = findViewById(R.id.bytedesk_activity_chat_im);
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

        // 录音HUD
        mRecordVoiceLayout = findViewById(R.id.bytedesk_chat_wx_voice_record);
        mRecordVoiceHintLayout = findViewById(R.id.appkefu_voice_record_hint_layout);
        mRecordVoiceCancelHintLayout = findViewById(R.id.appkefu_voice_record_hint_cancel_layout);
        mRecordVoiceTextLayout = findViewById(R.id.appkefu_voice_record_hint_text_record_layout);
        mRecordVoiceCancelTextLayout = findViewById(R.id.appkefu_voice_record_hint_text_cancel_layout);
        mRecordVoiceHintAMPImageView = findViewById(R.id.appkefu_voice_record_hint_amp);

        // 常用语
        mCuwButton = findViewById(R.id.bytedesk_chat_input_cuw_button);
        mCuwButton.setOnClickListener(this);

        // 语音
        mVoiceButton = findViewById(R.id.bytedesk_chat_input_voice_button);
        mVoiceButton.setOnClickListener(this);

        // 按住录音
        mRecordVoiceButton = findViewById(R.id.bytedesk_chat_input_record_voice_button);
        mRecordVoiceButton.setOnTouchListener(this);

        // 表情
        mEmotionButton = findViewById(R.id.bytedesk_chat_input_emotion_button);
        mEmotionButton.setOnClickListener(this);

        // 选择图片、拍照
        mPlusButton = findViewById(R.id.bytedesk_chat_input_plus_button);
        mPlusButton.setOnClickListener(this);

        // 发送文本消息
        mSendButton = findViewById(R.id.bytedesk_chat_input_send_button);
        mSendButton.setOnClickListener(this);

        // 输入框
        mInputEditText = findViewById(R.id.bytedesk_chat_fragment_input);
        mInputEditText.addTextChangedListener(inputTextWatcher);
        mInputEditText.setOnFocusChangeListener(this);

        // 图片大图预览
//        imagePreview = findViewById(R.id.bytedesk_image_preivew);
//        mScreenSize = ImageViewerUtil.getScreenSize(this);
//        imagePreview.setDefSize(mScreenSize.x, mScreenSize.y);
//        imagePreview.setImageDraggerType(ImageDraggerType.DRAG_TYPE_WX);

        // 表情
        mEmotionLayout = findViewById(R.id.bytedesk_chat_emotion);
        mEmotionMaps = new EmotionMapsGroup(this);
        mEmotionViewPagerAdapter = new EmotionViewPagerAdapter(mEmotionMaps.getGridViewArrayList());
        mEmotionViewPager = findViewById(R.id.appkefu_emotion_viewpager);
        mEmotionViewPager.setAdapter(mEmotionViewPagerAdapter);
        mEmotionViewPager.addOnPageChangeListener(this);
        
        // 
        mEmotionViewPagerIndicator1 = findViewById(R.id.appkefu_emotionview_pageindicator_imageview_1);
        mEmotionViewPagerIndicator2 = findViewById(R.id.appkefu_emotionview_pageindicator_imageview_2);
        mEmotionViewPagerIndicator3 = findViewById(R.id.appkefu_emotionview_pageindicator_imageview_3);
        mEmotionViewPagerIndicator4 = findViewById(R.id.appkefu_emotionview_pageindicator_imageview_4);
        mEmotionViewPagerIndicator5 = findViewById(R.id.appkefu_emotionview_pageindicator_imageview_5);

        // 扩展
        mExtensionLayout = findViewById(R.id.bytedesk_chat_extension);
        // 照片相册
        findViewById(R.id.appkefu_plus_pick_picture_btn).setOnClickListener(this);
        // 拍照
        findViewById(R.id.appkefu_plus_take_picture_btn).setOnClickListener(this);
        // 红包
        findViewById(R.id.appkefu_plus_show_red_packet_btn).setOnClickListener(this);
        // 文件
        findViewById(R.id.appkefu_plus_file_btn).setOnClickListener(this);
        // 阅后即焚
        findViewById(R.id.appkefu_read_destroy_btn).setOnClickListener(this);
//        // 仅有一对一单聊支持阅后即焚，客服会话和群聊不支持
//        if (!mThreadType.equals(BDCoreConstant.THREAD_TYPE_CONTACT)) {
//            findViewById(R.id.appkefu_read_destroy_btn).setVisibility(View.GONE);
//        }
        // 商品
        findViewById(R.id.appkefu_plus_shop_btn).setOnClickListener(this);
    }

    /**
     * 初始化ModelView
     *
     * TODO: 完善收发消息界面出现闪动的情况
     */
    private void initModel () {
        //
        mMessageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        //
        if (mThreadType.equals(BDCoreConstant.THREAD_TYPE_CONTACT)) {
            Logger.i("客服端：一对一会话");

            mMessageViewModel.getContactMessages(mUUID).observe(this, messageEntities -> {
                mChatAdapter.setMessages(messageEntities);
                mRecyclerView.scrollToPosition(messageEntities.size() - 1);
            });
        } else if (mThreadType.equals(BDCoreConstant.THREAD_TYPE_GROUP)) {
            Logger.i("客服端：群组会话 %s", mUUID);

            mMessageViewModel.getGroupMessages(mUUID).observe(this, messageEntities -> {
                mChatAdapter.setMessages(messageEntities);
                mRecyclerView.scrollToPosition(messageEntities.size() - 1);
            });
        } else {
            String uid = mTopic.split("/")[1];
            Logger.i("客服端：visitor %s", uid);

            mMessageViewModel.getVisitorMessages(uid).observe(this, messageEntities -> {
                mChatAdapter.setMessages(messageEntities);
                mRecyclerView.scrollToPosition(messageEntities.size() - 1);
            });
        }
    }

    /**
     * 从服务器加载thread
     */
    private void requestThread() {

        if (mThreadType.equals(BDCoreConstant.THREAD_TYPE_CONTACT)) {

            BDCoreApi.getContactThread(this, mUUID, new BaseCallback() {

                @Override
                public void onSuccess(JSONObject object) {

                    try {

                        JSONObject thread = object.getJSONObject("data");

                        mUUID = thread.getString("tid");
                        mThreadEntity.setTid(thread.getString("tid"));
                        mThreadEntity.setType(thread.getString("type"));
                        mThreadEntity.setTopic(thread.getString("topic"));
                        mThreadEntity.setNickname(thread.getString("nickname"));
                        mThreadEntity.setAvatar(thread.getString("avatar"));

                        Logger.i("getContactThread %s", mUUID);

                        initModel();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(JSONObject object) {

                }
            });

        } else if (mThreadType.equals(BDCoreConstant.THREAD_TYPE_GROUP)) {

            BDCoreApi.getGroupThread(this, mUUID, new BaseCallback() {

                @Override
                public void onSuccess(JSONObject object) {

                    try {

                        JSONObject thread = object.getJSONObject("data");

                        mUUID = thread.getString("tid");
                        mThreadEntity.setTid(thread.getString("tid"));
                        mThreadEntity.setType(thread.getString("type"));
                        mThreadEntity.setTopic(thread.getString("topic"));
                        mThreadEntity.setNickname(thread.getString("nickname"));
                        mThreadEntity.setAvatar(thread.getString("avatar"));

                        Logger.i("getGroupThread %s", mUUID);

                        initModel();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(JSONObject object) {

                }
            });
        }

    }

    /**
     * 从服务器加载聊天记录
     */
    protected void getMessages() {

        if (mThreadType.equals(BDCoreConstant.THREAD_TYPE_CONTACT)) {
            Logger.i("一对一会话 cid: " + mUUID);

            BDCoreApi.getMessagesWithContact(getBaseContext(), mUUID, mPage, mSize, new BaseCallback() {

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
                        Toast.makeText(ChatIMGroupActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else if (mThreadType.equals(BDCoreConstant.THREAD_TYPE_GROUP)) {
            Logger.i("群组会话 gid: " + mUUID);

            BDCoreApi.getMessagesWithGroup(getBaseContext(), mUUID, mPage, mSize, new BaseCallback() {
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
                        Toast.makeText(ChatIMGroupActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {

            String uid = mTopic.split("/")[1];
            Logger.i("客服会话 uid: %s ", uid);
            BDCoreApi.getMessagesWithUser(getBaseContext(), uid, mPage, mSize, new BaseCallback() {
                @Override
                public void onSuccess(JSONObject object) {
                    Logger.i("getMessagesWithUser %s", object.toString());

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
                        Toast.makeText(ChatIMGroupActivity.this, object.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    /**
     * 从相册中选择图片
     * FIXME: 当前版本，禁止选择视频文件
     */
    private void pickImageFromAlbum() {

        // 目前仅允许一次选一张图片
        Album.image(this)
                .singleChoice()
                .camera(false)
                .onResult(result -> {

                    if (result.size() > 0) {
                        AlbumFile albumFile = result.get(0);

                        String imageName = BDCoreUtils.getPictureTimestamp()  + "_" + mPreferenceManager.getUsername();
                        uploadImage(albumFile.getPath(), imageName);
                    }
                })
                .onCancel(result -> Toast.makeText(ChatIMGroupActivity.this, "取消发送图片", Toast.LENGTH_LONG).show())
                .start();
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

                    String imageFileName = BDCoreUtils.getPictureTimestamp()  + "_" + mPreferenceManager.getUsername();
                    uploadImage(result, imageFileName);
                })
                .onCancel(result -> Toast.makeText(ChatIMGroupActivity.this, "取消拍照", Toast.LENGTH_LONG).show())
                .start();
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

    @Override
    public void onMessageVideoItemClick(String videoUrl) {
        Logger.d("videoUrl:" + videoUrl);

        Intent intent = new Intent(this, ChatVideoActivity.class);
        intent.putExtra("video_url", videoUrl);
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
     * TODO: 区分是否当前会话
     *
     * @param previewEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPreviewEvent(PreviewEvent previewEvent) {
        Logger.i("onPreviewEvent");
        //
        if (previewEvent.getContent().trim().length() == 0) {
            return;
        }
        //
        mHandler.postDelayed(() -> mTopBar.setTitle(mTitle), 3000);
    }

    /**
     * 收到被转接会话
     *
     * @param transferEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTransferEvent(TransferEvent transferEvent) {
        Logger.i("transferEvent");

    }

    /**
     * 对方接受转接
     *
     * @param transferEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTransferAcceptEvent(TransferAcceptEvent transferEvent) {
        Logger.i("transferAcceptEvent");
        //
        Toast.makeText(this, "转接对话被接受", Toast.LENGTH_LONG).show();
    }

    /**
     * 对方拒绝接受转接
     *
     * @param transferEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTransferRejectEvent(TransferRejectEvent transferEvent) {
        Logger.i("transferRejectEvent");
        //
        Toast.makeText(this, "转接对话被拒绝", Toast.LENGTH_LONG).show();
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
            String content = charSequence.toString();
//            Logger.i("input content: %s", content);

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String content = charSequence.toString();
//            Logger.i("input content: %s", content);

            // 切换扩展按钮和发送按钮
            if (content.length() > 0) {
                mPlusButton.setVisibility(View.GONE);
                mSendButton.setVisibility(View.VISIBLE);
            } else {
                mPlusButton.setVisibility(View.VISIBLE);
                mSendButton.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
//            Logger.i("afterTextChanged");

            String content = editable.toString();
            if (content != null) {
                // 输入框文字变化时，发送消息输入状态消息
//                BDMqttApi.sendPreviewMessage(ChatIMActivity.this, mUUID, content, mThreadType);
            }
        }
    };


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentEmotionViewPagerIndex = position;
        switch (position) {
            case 0:
                mEmotionViewPagerIndicator1.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_active));
                mEmotionViewPagerIndicator2.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator3.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator4.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator5.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                break;
            case 1:
                mEmotionViewPagerIndicator1.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator2.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_active));
                mEmotionViewPagerIndicator3.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator4.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator5.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                break;
            case 2:
                mEmotionViewPagerIndicator1.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator2.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator3.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_active));
                mEmotionViewPagerIndicator4.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator5.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                break;
            case 3:
                mEmotionViewPagerIndicator1.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator2.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator3.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator4.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_active));
                mEmotionViewPagerIndicator5.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                break;
            case 4:
                mEmotionViewPagerIndicator1.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator2.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator3.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator4.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator5.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_active));
                break;
            default:
                mEmotionViewPagerIndicator1.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_active));
                mEmotionViewPagerIndicator2.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator3.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator4.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                mEmotionViewPagerIndicator5.setImageDrawable(getResources().getDrawable(R.drawable.appkefu_page_normal));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int arg2, long arg3) {
        Logger.i("on item clicked:" + arg2);

        int emotionIndex = mCurrentEmotionViewPagerIndex * 21 + arg2;
        if ((emotionIndex + 1) % 21 == 0) {
            int index = mInputEditText.getSelectionStart();
            Editable editable = mInputEditText.getText();

            if (index >= 0 && (index - 12) >= 0) {
                char[] dest = new char[12];
                editable.getChars(index - 12, index, dest, 0);
                if (String.valueOf(dest).startsWith("appkefu_")) {
                    editable.delete(index - 12, index);
                } else {
                    editable.delete(index - 1, index);
                }
            } else if (index > 0) {
                editable.delete(index - 1, index);
            }
        } else {

            String emotionName;
            if (emotionIndex < 9) {
                emotionName = "appkefu_f00" + (emotionIndex + 1);
            } else if (emotionIndex < 99) {
                emotionName = "appkefu_f0" + (emotionIndex + 1);
            } else {
                emotionName = "appkefu_f" + (emotionIndex + 1);
            }

            int emotionImageResId = mEmotionMaps.kfEmotionIdsForGridView[emotionIndex];
            Bitmap emotionBitmap = BitmapFactory.decodeResource(getResources(), emotionImageResId);
            ImageSpan imageSpan = new ImageSpan(getApplicationContext(), emotionBitmap);
            SpannableString spannableString = new SpannableString(emotionName);
            spannableString.setSpan(imageSpan, 0, emotionName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mInputEditText.append(spannableString);
        }
    }

    /**
     * 仅监听输入框mInputEditText焦点事件
     *
     * @param view
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View view, boolean hasFocus) {

        // 隐藏表情和类型扩展
        if (hasFocus) {
            mEmotionLayout.setVisibility(View.GONE);
            mExtensionLayout.setVisibility(View.GONE);
        }
    }
    

    /**
     * 录音相关
     */
    private void initRecorder(Bundle savedInstanceState) {
        // /////////////////////////////////////////////////////////////////////////
        m_voiceRecorder = new KFRecorder(this);
        m_voiceRecorder.setOnStateChangedListener(this);
        m_voiceRecordReceiver = new RecorderReceiver();
        m_voiceRecordRemainingTimeCalculator = new KFRemainingTimeCalculator();

        if (savedInstanceState != null) {
            Bundle recorderState = savedInstanceState.getBundle(BDCoreConstant.RECORDER_STATE_KEY);
            if (recorderState != null) {
                m_voiceRecorder.restoreState(recorderState);
                m_voiceRecordMaxFileSize = recorderState.getLong(BDCoreConstant.MAX_FILE_SIZE_KEY, -1);
            }
        }
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // //////////////////////////////////////////////////////////////////////////
    }

    private BroadcastReceiver m_voiceRecordSDCardMountEventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            m_voiceRecorder.reset();
        }
    };

    private class RecorderReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(KFRecorderService.RECORDER_SERVICE_BROADCAST_STATE)) {
                boolean isRecording = intent.getBooleanExtra(KFRecorderService.RECORDER_SERVICE_BROADCAST_STATE, false);
                m_voiceRecorder.setState(isRecording ? KFRecorder.RECORDING_STATE : KFRecorder.IDLE_STATE);

            } else if (intent.hasExtra(KFRecorderService.RECORDER_SERVICE_BROADCAST_ERROR)) {
                int error = intent.getIntExtra(KFRecorderService.RECORDER_SERVICE_BROADCAST_ERROR, 0);
                m_voiceRecorder.setError(error);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (m_voiceRecorder.sampleLength() == 0)
            return;

        Bundle recorderState = new Bundle();
        if (m_voiceRecorder.state() != KFRecorder.RECORDING_STATE) {
            m_voiceRecorder.saveState(recorderState);
        }

        recorderState.putLong(BDCoreConstant.MAX_FILE_SIZE_KEY, m_voiceRecordMaxFileSize);
        outState.putBundle(BDCoreConstant.RECORDER_STATE_KEY, recorderState);
    }

    private void stopAudioPlayback() {
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        sendBroadcast(i);
    }

    @SuppressLint("InlinedApi")
    private void startRecording(String voiceName) {

        m_voiceRecordRemainingTimeCalculator.reset();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Logger.i("appkefu_record_voice_insert_sdcard");
            
        } else if (!m_voiceRecordRemainingTimeCalculator.diskSpaceAvailable()) {
            Logger.i("appkefu_record_voice_sdcard_full");
            
        } else {
            stopAudioPlayback();
            m_voiceRecordRemainingTimeCalculator.setBitRate(BDCoreConstant.BITRATE_AMR);
            m_voiceRecorder.startRecording(MediaRecorder.OutputFormat.AMR_NB, voiceName,
                    BDCoreConstant.EXT_AMR, false, m_voiceRecordMaxFileSize);

            if (m_voiceRecordMaxFileSize != -1) {
                m_voiceRecordRemainingTimeCalculator.setFileSizeLimit(
                        m_voiceRecorder.sampleFile(), m_voiceRecordMaxFileSize);
            }
        }

    }

    private Runnable voiceRecordingRefreshAMPTaskThread = new Runnable() {
        public void run() {
            updateRecordVoiceAMP();
            m_voiceRecordHandler.postDelayed(
                    voiceRecordingRefreshAMPTaskThread,
                    VOICE_RECORDING_REFRESH_AMP_INTERVAL);
        }
    };

    private void startVoiceRecording() {
        mIsRecording = true;
        mRecordVoiceLayout.setVisibility(View.VISIBLE);
        m_startRecordingTimestamp = System.currentTimeMillis();
        m_voiceRecordingVoiceFileName = BDCoreUtils.uuid();

        startRecording(m_voiceRecordingVoiceFileName);
        m_voiceRecordHandler.postDelayed(voiceRecordingRefreshAMPTaskThread, VOICE_RECORDING_REFRESH_AMP_INTERVAL);
    }

    private void stopVoiceRecording() {
        mIsRecording = false;
        mRecordVoiceLayout.setVisibility(View.GONE);
        m_voiceRecordHandler.removeCallbacks(voiceRecordingRefreshAMPTaskThread);
        m_voiceRecorder.stop();
        m_voiceRecorder.reset();
        mRecordVoiceHintAMPImageView.setImageResource(R.drawable.appkefu_voice_rcd_hint_amp1);
    }

    private void updateRecordVoiceAMP() {
        double amp = m_voiceRecorder.getAmplitude();
        switch ((int) amp) {
            case 0:
                mRecordVoiceHintAMPImageView.setImageResource(R.drawable.appkefu_voice_rcd_hint_amp1);
                break;
            case 1:
                mRecordVoiceHintAMPImageView.setImageResource(R.drawable.appkefu_voice_rcd_hint_amp2);
                break;
            case 2:
                mRecordVoiceHintAMPImageView.setImageResource(R.drawable.appkefu_voice_rcd_hint_amp3);
                break;
            case 3:
                mRecordVoiceHintAMPImageView.setImageResource(R.drawable.appkefu_voice_rcd_hint_amp4);
                break;
            case 4:
                mRecordVoiceHintAMPImageView.setImageResource(R.drawable.appkefu_voice_rcd_hint_amp5);
                break;
            case 5:
                mRecordVoiceHintAMPImageView.setImageResource(R.drawable.appkefu_voice_rcd_hint_amp6);
                break;
            case 6:
                mRecordVoiceHintAMPImageView.setImageResource(R.drawable.appkefu_voice_rcd_hint_amp7);
                break;
            default:
                mRecordVoiceHintAMPImageView.setImageResource(R.drawable.appkefu_voice_rcd_hint_amp7);
                break;
        }
    }

    /**
     * KFRecorder.OnStateChangedListener
     */
    @Override
    public void onStateChanged(int state) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onError(int error) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BDUiConstant.SELECT_FILE) {
            //
            if (resultCode == Activity.RESULT_OK) {
                String filePath;
                Uri uri = data.getData();
                if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开
                    filePath = uri.getPath();
                    Toast.makeText(this,filePath,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    filePath = BDFileUtils.getPath(ChatIMGroupActivity.this, uri);
                    Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
                } else {//4.4以下下系统调用方法
                    filePath = BDFileUtils.getRealPathFromURI(ChatIMGroupActivity.this, uri);
                    Toast.makeText(ChatIMGroupActivity.this, filePath, Toast.LENGTH_SHORT).show();
                }
//                Logger.i("filePath:" + filePath);
                // 上传、发送文件
                uploadFile(filePath, BDCoreUtils.uuid());
            }
        } else if (requestCode == BDUiConstant.SELECT_CUW) {
            // 忽略resultCode直接获取content即可
            if (resultCode == 1) {
                String type = data.getStringExtra("type");
                String content = data.getStringExtra("content");
                if (type.equals("text")) {
                    sendTextMessage(content);
                } else if (type.equals("image")) {
                    sendImageMessage(content);
                } else if (type.equals("file")) {
                    sendFileMessage(content);
                } else if (type.equals("voice")) {
                    sendVoiceMessage(content);
                } else if (type.equals("video")) {
                    sendVideoMessage(content);
                } else {
                    Toast.makeText(ChatIMGroupActivity.this, "不支持的消息类型", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 发送文本消息
     *
     * @param content
     */
    private void sendTextMessage(String content) {
        // 自定义本地消息id，用于判断消息发送状态. 消息通知或者回调接口中会返回此id
        final String localId = BDCoreUtils.uuid();

//        if (!BDMqttApi.isConnected(this)) {
            // FIXME: 长链接发消息经常显示loading发送中
            Logger.i("disconnected, sendMessageRest");
            //
            String timestamp = BDCoreUtils.currentDate();
            String client = BDCoreConstant.CLIENT_ANDROID;
            String type = BDCoreConstant.MESSAGE_TYPE_TEXT;
            //
            JSONObject messageObject = new JSONObject();
            try {
                //
                messageObject.put("mid", localId);
                messageObject.put("timestamp", timestamp);
                messageObject.put("client", client);
                messageObject.put("version", "1");
                messageObject.put("type", type);
                //
                JSONObject userObject = new JSONObject();
                userObject.put("uid", mPreferenceManager.getUid());
                userObject.put("nickname", mPreferenceManager.getNickname());
                userObject.put("avatar", mPreferenceManager.getAvatar());
                // Extra
                JSONObject extraObject = new JSONObject();
                extraObject.put("agent", true);
                userObject.put("extra", extraObject);
                //
                messageObject.put("user", userObject);
                //
                JSONObject textObject = new JSONObject();
                textObject.put("content", content);
                messageObject.put("text", textObject);
                //
                JSONObject threadObject = new JSONObject();
                threadObject.put("tid", mThreadEntity.getTid());
                threadObject.put("type", mThreadEntity.getType());
                threadObject.put("content", content);
                threadObject.put("nickname", mThreadEntity.getNickname());
                threadObject.put("avatar", mThreadEntity.getAvatar());
                threadObject.put("topic", mThreadEntity.getTopic());
                threadObject.put("client", mThreadEntity.getClient());
                threadObject.put("timestamp", timestamp);
                threadObject.put("unreadCount", 0);
                messageObject.put("thread", threadObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 插入本地消息
            mRepository.insertTextMessageLocal(mUUID, mWorkGroupWid, mUUID, content, localId, mThreadType);
            // rest接口发送消息
            String jsonContent = messageObject.toString();
            BDCoreApi.sendMessageRest(this, jsonContent, new BaseCallback() {
                @Override
                public void onSuccess(JSONObject object) {
                    // 插入本地消息
//                    mRepository.insertTextMessageLocal(mUUID, mWorkGroupWid, mUUID, content, localId, mThreadType);
                }
                @Override
                public void onError(JSONObject object) {
                    Toast.makeText(getApplicationContext(), "网络断开，请稍后重试", Toast.LENGTH_LONG).show();
                }
            });

            return;
//        } else {
//            Logger.i("sendTextMessage is connected");
//        }
//
//        if (content.length() >= 500) {
//            Toast.makeText(this, "消息太长，请分多次发送", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        // 插入本地消息
//        mRepository.insertTextMessageLocal(mUUID, mWorkGroupWid, mUUID, content, localId, mThreadType);
//        // mqtt发送消息
//        BDMqttApi.sendTextMessageProtobuf(this, localId, content, mThreadEntity);
    }

    private void sendImageMessage(String imageUrl) {

        final String localId = BDCoreUtils.uuid();

        // 插入本地消息
        mRepository.insertImageMessageLocal(mUUID, mWorkGroupWid, mUUID, imageUrl, localId, mThreadType);

        BDMqttApi.sendImageMessageProtobuf(ChatIMGroupActivity.this, localId, imageUrl, mThreadEntity);
    }

    private void sendFileMessage(String imageUrl) {

        final String localId = BDCoreUtils.uuid();

        // 插入本地消息
        mRepository.insertFileMessageLocal(mUUID, mWorkGroupWid, mUUID, imageUrl, localId, mThreadType, "doc", "fileName", "fileSize");

        BDMqttApi.sendFileMessageProtobuf(ChatIMGroupActivity.this, localId, imageUrl, mThreadEntity);
    }

    private void sendVoiceMessage(String imageUrl) {

        final String localId = BDCoreUtils.uuid();

        // 插入本地消息
        mRepository.insertVoiceMessageLocal(mUUID, mWorkGroupWid, mUUID, imageUrl, localId, mThreadType, 0);

        BDMqttApi.sendVoiceMessageProtobuf(ChatIMGroupActivity.this, localId, imageUrl, mThreadEntity);
    }

    private void sendVideoMessage(String imageUrl) {

        final String localId = BDCoreUtils.uuid();

        // 插入本地消息
        mRepository.insertVideoMessageLocal(mUUID, mWorkGroupWid, mUUID, imageUrl, localId, mThreadType);

        BDMqttApi.sendVideoMessageProtobuf(ChatIMGroupActivity.this, localId, imageUrl, mThreadEntity);
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

        final QMUITipDialog loadingDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("上传中...")
                .create();
        loadingDialog.show();

        BDCoreApi.uploadImage(this, filePath, fileName, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                loadingDialog.dismiss();

                try {

                    // TODO: 无客服在线时，禁止发送图片

                    // TODO: 收到客服关闭会话 或者 自动关闭会话消息之后，禁止访客发送消息

                    // 自定义本地消息id，用于判断消息发送状态。消息通知或者回调接口中会返回此id
                    final String localId = BDCoreUtils.uuid();

                    String imageUrl = object.getString("data");

                    // 插入本地消息
                    mRepository.insertImageMessageLocal(mUUID, mWorkGroupWid, mUUID, imageUrl, localId, mThreadType);

                    BDMqttApi.sendImageMessageProtobuf(ChatIMGroupActivity.this, localId, imageUrl, mThreadEntity);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {
                loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), "上传图片失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 上传并发送语音
     *
     * @param filePath 路径
     * @param fileName 文件名
     */
    private void uploadVoice(String filePath, String fileName, final int voiceLength) {

        BDCoreApi.uploadVoice(this, filePath, fileName, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    // TODO: 无客服在线时，禁止发送语音

                    // TODO: 收到客服关闭会话 或者 自动关闭会话消息之后，禁止访客发送消息

                    // 自定义本地消息id，用于判断消息发送状态。消息通知或者回调接口中会返回此id
                    final String localId = BDCoreUtils.uuid();
                    String voiceUrl = object.getString("data");

                    // 插入本地消息
                    mRepository.insertVoiceMessageLocal(mUUID, mWorkGroupWid, mUUID, voiceUrl, localId, mThreadType, voiceLength);

                    BDMqttApi.sendVoiceMessageProtobuf(ChatIMGroupActivity.this, localId, voiceUrl, mThreadEntity);
//                    BDMqttApi.sendVoiceMessageProtobuf(ChatIMActivity.this, localId, voiceUrl,
//                            mUUID, mThreadEntity.getTopic(), mThreadEntity.getType(), mThreadEntity.getNickname(), mThreadEntity.getAvatar());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {

                Toast.makeText(ChatIMGroupActivity.this, "上传语音失败", Toast.LENGTH_LONG).show();
            }

        });
    }

    /**
     * 上传并发送文件
     *
     * @param filePath
     * @param fileName
     */
    private void uploadFile(String filePath, String fileName) {

        BDCoreApi.uploadFile(this, filePath, fileName, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {
                    // 自定义本地消息id，用于判断消息发送状态。消息通知或者回调接口中会返回此id
                    final String localId = BDCoreUtils.uuid();
                    String fileUrl  = object.getString("data");

                    // 插入本地消息
                    mRepository.insertFileMessageLocal(mUUID, mWorkGroupWid, mUUID, fileUrl, localId, mThreadType, "doc", "fileName", "fileSize");

                    BDMqttApi.sendFileMessageProtobuf(ChatIMGroupActivity.this, localId, fileUrl, mThreadEntity);
//                    BDMqttApi.sendFileMessageProtobuf(ChatIMActivity.this, localId, fileUrl,
//                            mUUID, mThreadEntity.getTopic(), mThreadEntity.getType(), mThreadEntity.getNickname(), mThreadEntity.getAvatar());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(JSONObject object) {

            }
        });
    }

    private void initListPopupIfNeed() {
        if (mListPopup == null) {
            String[] listItems = new String[]{
                    "用户信息",
                    "转接会话",
                    "邀请评价",
                    "发送表单",
                    "关闭会话",
                    "拉黑用户"
                    // TODO: 创建工单、添加到CRM、会话小结
            };
            List<String> data = new ArrayList<>();
            Collections.addAll(data, listItems);
            ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.bytedesk_simple_list_item, data);
            AdapterView.OnItemClickListener onItemClickListener = (adapterView, view, i, l) -> {
                //
                if (mListPopup != null) {
                    mListPopup.dismiss();
                }
                if (i == 0) {
                    Logger.i("用户信息");
                    //
                    String uid = mThreadEntity.getTopic().split("/")[1];
                    Intent intent = new Intent(ChatIMGroupActivity.this, VisitorInfoActivity.class);
                    intent.putExtra(BDUiConstant.EXTRA_UID, uid);
                    startActivity(intent);

                } else if (i == 1) {
                    Logger.i("转接会话");
                    // TODO: 弹窗选择在线的客服
                    Context context = this;
                    BDCoreApi.getOnlineAgents(this, 0, 100, new BaseCallback() {
                        @Override
                        public void onSuccess(JSONObject object) {
                            // 无客服在线，提示
                            // 有在线，弹窗供选择
                            try {
                                //
                                JSONArray agentArray = object.getJSONObject("data").getJSONArray("content");
                                if (agentArray.length() > 0) {
                                    //
                                    List<String> agentRealNames = new ArrayList<>();
                                    List<String> agentUids = new ArrayList<>();
                                    for (int j = 0; j < agentArray.length(); j++) {
                                        JSONObject agent = agentArray.getJSONObject(j);
                                        agentUids.add(agent.getString("uid"));
                                        agentRealNames.add(agent.getString("realName"));
                                    }
                                    //
                                    String[] itemsArray = new String[agentRealNames.size()];
                                    itemsArray = agentRealNames.toArray(itemsArray);
                                    new QMUIDialog.MenuDialogBuilder(context)
                                        .addItems(itemsArray, (dialog, which) -> {
                                            Toast.makeText(context, "你选择了 " + agentRealNames.get(which), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            // 调用转接会话接口， TODO: 自定义附言内容
                                            String transferAgentUid = agentUids.get(which);
                                            BDMqttApi.sendTransferMessageProtobuf(context, mThreadEntity, transferAgentUid, "附言");
                                        })
                                        .create(mCurrentDialogStyle).show();

                                } else {
                                    Toast.makeText(context, "当前无其他客服在线", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(JSONObject object) {
                            try {
                                String message = object.getString("message");
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else if (i == 2) {
                    Logger.i("邀请评价");
                    // 插入本地
                    final String localId = BDCoreUtils.uuid();
                    // 插入本地消息
                    mRepository.insertInviteRateMessageLocal(mUUID, mWorkGroupWid, mUUID, "邀请评价", localId, mThreadType);
                    //
                    BDMqttApi.sendInviteRateMessageProtobuf(this, localId, "邀请评价", mThreadEntity);

                } else if (i == 3) {
                    Logger.i("发送表单");
                    // 插入本地
                    // TODO: 弹窗选择字段
                    final String localId = BDCoreUtils.uuid();
                    mRepository.insertFormRequestMessageLocal(mUUID, mWorkGroupWid, mUUID, "发送表单", localId, mThreadType);
                    //
                    String formContent = "{ 'form': [ '姓名', '手机' ]}";
                    BDMqttApi.sendFormRequestMessageProtobuf(this, localId, formContent, mThreadEntity);

                } else if (i == 4) {
                    Logger.i("关闭会话");
                    // 弹窗确认
                    Context context = this;
                    new QMUIDialog.MessageDialogBuilder(context)
                        .setTitle("关闭会话")
                        .setMessage("确定要关闭会话？")
                        .addAction("取消", (dialog, index) -> dialog.dismiss())
                        .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_POSITIVE, (dialog, index) -> {
                            dialog.dismiss();
                            // 关闭会话
                            agentCloseThread();
                        })
                        .create(mCurrentDialogStyle).show();
                } else if (i == 5) {
                    Logger.i("拉黑用户");
                    // 弹窗，输入备注内容
                    Context context = this;
                    final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
                    builder.setTitle("拉黑")
//                            .setPlaceholder("请输入拉黑理由")
                            .setDefaultText("垃圾用户")
                            .setInputType(InputType.TYPE_CLASS_TEXT)
                            .addAction("取消", (dialog, index) -> dialog.dismiss())
                            .addAction("确定", (dialog, index) -> {
                                CharSequence text = builder.getEditText().getText();
                                if (text != null && text.length() > 0) {
                                    dialog.dismiss();
                                    // 拉黑
                                    addBlock(text.toString());
                                } else {
                                    Toast.makeText(context, "请输入拉黑理由", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create(mCurrentDialogStyle).show();

                }
            };
            mListPopup = QMUIPopups.listPopup(this,
                    QMUIDisplayHelper.dp2px(this, 100),
                    QMUIDisplayHelper.dp2px(this, 300),
                    adapter,
                    onItemClickListener)
                    .animStyle(QMUIPopup.ANIM_GROW_FROM_CENTER)
                    .preferredDirection(QMUIPopup.DIRECTION_TOP)
                    .shadow(true)
                    .offsetYIfTop(QMUIDisplayHelper.dp2px(this, 5))
                    .onDismiss(() -> {
//                            Toast.makeText(this, "onDismiss", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void agentCloseThread () {

        Context context = this;
        BDCoreApi.agentCloseThread(this, mThreadEntity.getTid(), new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                // TODO: 结果提醒
                try {
                    String message = object.getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {
                try {
                    String message = object.getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 拉黑
    private void addBlock (String note) {
        //
        Context context = this;
        String uid = mThreadEntity.getTopic().split("/")[1];
        String uuid = mThreadEntity.getTopic().split("/")[0];

        BDCoreApi.addBlock(this, uid, note, mThreadType, uuid, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                // TODO: 结果提醒
                try {
                    String message = object.getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {
                try {
                    String message = object.getString("message");
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 开启、关闭发送阅后即焚消息
     */
    private void toggleDestroyAfterReading() {

        boolean isDestroyAfterReadingEnabled = mPreferenceManager.getDestroyAfterReading(mUUID, mThreadType);
        int destroyAfterLength = mPreferenceManager.getDestroyAfterLength(mUUID, mThreadType);
        final String[] items = new String[]{isDestroyAfterReadingEnabled ? "开启("+destroyAfterLength+"秒)" : "开启", "关闭"};
        final int checkedIndex = isDestroyAfterReadingEnabled ? 0 : 1;
        new QMUIDialog.CheckableDialogBuilder(this)
                .setTitle("阅后即焚")
                .setCheckedIndex(checkedIndex)
                .addItems(items, (dialogInterface, which) -> {
                    Toast.makeText(ChatIMGroupActivity.this,  items[which] + "阅后即焚", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();

                    boolean enabled = which == 0 ? true : false;
                    mPreferenceManager.setDestroyAfterReading(mUUID, mThreadType, enabled);

                    if (enabled) {
                        // 设置长度
                        setDestroyAfterLength();
                    }

                }).show();
    }

    private void setDestroyAfterLength() {

        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(ChatIMGroupActivity.this);
        builder.setTitle("阅后即焚")
                .setPlaceholder("输入时长(秒)")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", (dialog, index) -> {})
                .addAction("确定", (dialog, index) -> {
                    final CharSequence text = builder.getEditText().getText();
                    if (text != null && text.length() > 0) {

                        // 检查是否有效数字 且 大于0
                        if (BDCoreUtils.isNumeric(text.toString()) && Integer.valueOf(text.toString()) > 0) {
                            mPreferenceManager.setDestroyAfterLength(mUUID, mThreadType, Integer.valueOf(text.toString()));
                            dialog.dismiss();

                            // TODO: 设置保存到服务器端，并通知对方，在聊天界面显示通知

                        }

                    } else {
                        Toast.makeText(ChatIMGroupActivity.this, "请填入时长", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    @Override
    public void onKeyboardHidden() {
        Logger.i("onKeyboardHidden");
    }

    @Override
    public void onKeyboardShown() {
        Logger.i("onKeyboardShown");
        mRecyclerView.scrollToPosition(mChatAdapter.getItemCount() - 1);
        mExtensionLayout.setVisibility(View.GONE);
        mEmotionLayout.setVisibility(View.GONE);
    }

    /**
     * 发送红包消息
     * @param money 金额
     */
    private void sendRedPacketMessage(String money) {

        // 自定义本地消息id，用于判断消息发送状态. 消息通知或者回调接口中会返回此id
        final String localId = BDCoreUtils.uuid();
        // 插入本地消息
        mRepository.insertRedPacketMessageLocal(mUUID, mWorkGroupWid, mUUID, money, localId, mThreadType);

//        BDCoreApi.sendRedPacketMessage(this, mUUID, money, localId, mThreadType, new BaseCallback() {
//
//            @Override
//            public void onSuccess(JSONObject object) {
//                //
//                try {
//
//                    int status_code = object.getInt("status_code");
//                    if (status_code == 200) {
//
//                        String localId = object.getJSONObject("data").getString("localId");
//                        Logger.i("callback localId: " + localId);
//
//                        // TODO: 更新消息发送状态为成功
//                        mRepository.updateMessageStatusSuccess(localId);
//
//                        // 发送成功
//                    } else {
//
//                        // 修改本地消息发送状态为error
//                        mRepository.updateMessageStatusError(localId);
//
//                        // 发送消息失败
//                        String message = object.getString("message");
//                        Toast.makeText(ChatIMActivity.this, message, Toast.LENGTH_LONG).show();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(JSONObject object) {
//                // 发送消息失败
//                Toast.makeText(ChatIMActivity.this, "发送消息失败", Toast.LENGTH_LONG).show();
//            }
//
//        });

    }

}

