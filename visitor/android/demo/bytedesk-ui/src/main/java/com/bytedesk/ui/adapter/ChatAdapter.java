package com.bytedesk.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bytedesk.core.api.BDMqttApi;
import com.bytedesk.core.event.LongClickEvent;
import com.bytedesk.core.event.QueryAnswerEvent;
import com.bytedesk.core.event.SendCommodityEvent;
import com.bytedesk.core.repository.BDRepository;
import com.bytedesk.core.room.entity.MessageEntity;
import com.bytedesk.core.room.entity.ThreadEntity;
import com.bytedesk.core.util.BDCoreConstant;
import com.bytedesk.core.util.BDFileUtils;
import com.bytedesk.core.util.JsonCustom;
import com.bytedesk.ui.R;
import com.bytedesk.ui.activity.BigImageViewActivity;
import com.bytedesk.ui.activity.ChatBaseActivity;
import com.bytedesk.ui.api.BDUiApi;
import com.bytedesk.ui.listener.ChatItemClickListener;
import com.bytedesk.ui.util.BDUiUtils;
import com.bytedesk.ui.util.ExpressionUtil;
import com.bytedesk.ui.util.KFResUtil;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIProgressBar;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.textview.QMUILinkTextView;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: 一对一聊天隐藏左侧昵称
 *
 * @author bytedesk.com on 2017/8/23.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> implements MediaPlayer.OnErrorListener {

    private Context mContext;
    private List<MessageEntity> mMessages;
    private ChatItemClickListener mChatItemClickListener;

    private MediaPlayer m_mediaPlayer;
    private ChangeImage m_changeVoiceImage;

    public ChatAdapter(Context context, ChatItemClickListener chatItemClickListener) {
        mContext = context;
        mMessages = new ArrayList<>();
        mChatItemClickListener = chatItemClickListener;

        m_mediaPlayer = new MediaPlayer();
        m_mediaPlayer.setOnErrorListener(this);
    }

    public void setMessages(final List<MessageEntity> messageEntities) {
        if (null == mMessages) {
            mMessages = messageEntities;
            notifyItemRangeInserted(0, messageEntities.size());
        }
        else {
            mMessages = messageEntities;
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout;

        switch (viewType) {
            case MessageEntity.TYPE_TEXT_ID:
                layout = R.layout.bytedesk_message_item_text;
                break;
            case MessageEntity.TYPE_TEXT_SELF_ID:
                layout = R.layout.bytedesk_message_item_text_self;
                break;
            case MessageEntity.TYPE_IMAGE_ID:
                layout = R.layout.bytedesk_message_item_image;
                break;
            case MessageEntity.TYPE_IMAGE_SELF_ID:
                layout = R.layout.bytedesk_message_item_image_self;
                break;
            case MessageEntity.TYPE_FILE_ID:
                layout = R.layout.bytedesk_message_item_file;
                break;
            case MessageEntity.TYPE_FILE_SELF_ID:
                layout = R.layout.bytedesk_message_item_file_self;
                break;
            case MessageEntity.TYPE_VIDEO_ID:
                layout = R.layout.bytedesk_message_item_video;
                break;
            case MessageEntity.TYPE_VIDEO_SELF_ID:
                layout = R.layout.bytedesk_message_item_video_self;
                break;
            case MessageEntity.TYPE_VOICE_ID:
                layout = R.layout.bytedesk_message_item_voice;
                break;
            case MessageEntity.TYPE_VOICE_SELF_ID:
                layout = R.layout.bytedesk_message_item_voice_self;
                break;
            case MessageEntity.TYPE_ROBOT_ID:
                layout = R.layout.bytedesk_message_item_robot;
                break;
            case MessageEntity.TYPE_ROBOT_SELF_ID:
                layout = R.layout.bytedesk_message_item_robot_self;
                break;
            case MessageEntity.TYPE_QUESTIONNAIRE_ID:
                layout = R.layout.bytedesk_message_item_questionnaire;
                break;
            case MessageEntity.TYPE_NOTIFICATION_ID:
                layout = R.layout.bytedesk_message_item_notification;
                break;
            case MessageEntity.TYPE_COMMODITY_ID:
                layout = R.layout.bytedesk_message_item_commodity;
                break;
            case MessageEntity.TYPE_RED_PACKET_ID:
                layout = R.layout.bytedesk_message_item_red_packet;
                break;
            case MessageEntity.TYPE_RED_PACKET_SELF_ID:
                layout = R.layout.bytedesk_message_item_red_packet_self;
                break;
            default:
                layout = R.layout.bytedesk_message_item_text;
        }

        View view = LayoutInflater.from(mContext).inflate(layout, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        MessageEntity beforeEntity = position > 0 ? mMessages.get(position - 1) : null;
        MessageEntity messageEntity = mMessages.get(position);
        //
        boolean showTimestamp = true;
        if (beforeEntity != null) {
            showTimestamp = BDUiUtils.showTime(messageEntity.getCreatedAt(), beforeEntity.getCreatedAt());
        }
        viewHolder.setContent(showTimestamp, messageEntity);

        if (null != mChatItemClickListener) {
            viewHolder.setItemClickListener(this.mChatItemClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return mMessages == null ? 0 : mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages == null ? MessageEntity.TYPE_TEXT_ID : mMessages.get(position).getTypeId();
    }

    //
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private int messageViewType;
        private TextView timestampTextView;
        private TextView nicknameTextView;
        private QMUIRadiusImageView avatarImageView;
        // 文字消息
        private QMUILinkTextView contentTextView;
        // 图片消息
        private ImageView imageImageView;
        // 视频消息
        private ImageView videoImageView;
        // 语音消息
        public TextView voiceTextView;
        public TextView voiceLengthTextView;
        public View voiceUnplayedView;
        // 通知消息
        private TextView notificationTextView;
        // 商品消息
        private TextView commodityTitleTextView;
        private TextView commodityPriceTextView;
        private ImageView commodityImageView;
        private Button commoditySendButton;
        // 文件消息
        private QMUILinkTextView fileTextView;
        // 机器人
        public TextView robotTextView;
        // 阅后即焚倒计时
        private QMUIProgressBar destroyAfterReadingProgressBar;
        // 发送中
        private ProgressBar progressBar;
        // 发送错误
        private ImageView errorImageView;
        // 送达、已读状态
        private TextView statusTextView;
        //
        private ChatItemClickListener itemClickListener;

        public ViewHolder(View itemView, int msgViewType) {
            super(itemView);
            //
            messageViewType = msgViewType;
            timestampTextView = itemView.findViewById(R.id.bytedesk_message_item_timestamp_textview);
            // 文字消息
            if (messageViewType == MessageEntity.TYPE_TEXT_ID
                    || messageViewType == MessageEntity.TYPE_TEXT_SELF_ID
                    || messageViewType == MessageEntity.TYPE_EVENT_ID
                    || messageViewType == MessageEntity.TYPE_EVENT_SELF_ID) {
                initAvatar();
                contentTextView = itemView.findViewById(R.id.bytedesk_message_item_content);
            }
            // 图片消息
            else if (messageViewType == MessageEntity.TYPE_IMAGE_ID
                    || messageViewType == MessageEntity.TYPE_IMAGE_SELF_ID) {
                initAvatar();
                imageImageView = itemView.findViewById(R.id.bytedesk_message_item_image);
            }
            // 视频消息
            else if (messageViewType == MessageEntity.TYPE_VIDEO_ID
                    || messageViewType == MessageEntity.TYPE_VIDEO_SELF_ID) {
                initAvatar();
                videoImageView = itemView.findViewById(R.id.bytedesk_message_item_video);
            }
            // 语音消息
            else if (messageViewType == MessageEntity.TYPE_VOICE_ID
                    || messageViewType == MessageEntity.TYPE_VOICE_SELF_ID) {
                initAvatar();
                voiceTextView = itemView.findViewById(R.id.bytedesk_message_item_content_voice);
                voiceLengthTextView = itemView.findViewById(R.id.bytedesk_message_item_voice_length);
                if (messageViewType == MessageEntity.TYPE_VOICE_ID) {
                    voiceUnplayedView = itemView.findViewById(R.id.bytedesk_message_item_voice_unplayed);
                }
            }
            // 问卷消息
            else if (messageViewType == MessageEntity.TYPE_QUESTIONNAIRE_ID) {
                initAvatar();
                contentTextView = itemView.findViewById(R.id.bytedesk_message_item_content);
            }
            // 通知消息
            else if (messageViewType == MessageEntity.TYPE_NOTIFICATION_ID) {
                notificationTextView = itemView.findViewById(R.id.bytedesk_message_item_notification_textview);
            }
            // 商品消息
            else if (messageViewType == MessageEntity.TYPE_COMMODITY_ID) {
                commodityTitleTextView = itemView.findViewById(R.id.bytedesk_commodity_title_textview);
                commodityPriceTextView = itemView.findViewById(R.id.bytedesk_commodity_price_textview);
                commodityImageView = itemView.findViewById(R.id.bytedesk_commodity_imageview);
                commoditySendButton = itemView.findViewById(R.id.bytedesk_commodity_send_button);
            }
            // 红包消息
            else if (messageViewType == MessageEntity.TYPE_RED_PACKET_ID
                    || messageViewType == MessageEntity.TYPE_RED_PACKET_SELF_ID) {
                initAvatar();
                // TODO
            }
            // 文件消息
            else if (messageViewType == MessageEntity.TYPE_FILE_ID
                    || messageViewType == MessageEntity.TYPE_FILE_SELF_ID) {
                initAvatar();
                fileTextView = itemView.findViewById(R.id.bytedesk_message_item_file);
                // TODO
            } else if (messageViewType == MessageEntity.TYPE_ROBOT_ID
                    || messageViewType == MessageEntity.TYPE_ROBOT_SELF_ID) {
                initAvatar();
                robotTextView = itemView.findViewById(R.id.bytedesk_message_item_content);
            }

            // 收到的消息
            if (messageViewType == MessageEntity.TYPE_TEXT_ID
                    || messageViewType == MessageEntity.TYPE_IMAGE_ID
                    || messageViewType == MessageEntity.TYPE_VOICE_ID
                    || messageViewType == MessageEntity.TYPE_VIDEO_ID
                    || messageViewType == MessageEntity.TYPE_FILE_ID
                    || messageViewType == MessageEntity.TYPE_RED_PACKET_ID
                    || messageViewType == MessageEntity.TYPE_ROBOT_ID) {
                nicknameTextView = itemView.findViewById(R.id.bytedesk_message_item_nickname);
            }

            // 发送的消息
            if (messageViewType == MessageEntity.TYPE_TEXT_SELF_ID
                    || messageViewType == MessageEntity.TYPE_IMAGE_SELF_ID
                    || messageViewType == MessageEntity.TYPE_VOICE_SELF_ID
                    || messageViewType == MessageEntity.TYPE_VIDEO_SELF_ID
                    || messageViewType == MessageEntity.TYPE_FILE_SELF_ID
                    || messageViewType == MessageEntity.TYPE_RED_PACKET_SELF_ID
                    || messageViewType == MessageEntity.TYPE_ROBOT_SELF_ID) {
                progressBar = itemView.findViewById(R.id.bytedesk_message_item_loading);
                errorImageView = itemView.findViewById(R.id.bytedesk_message_item_error);
                statusTextView = itemView.findViewById(R.id.bytedesk_message_item_status);
            }
        }

        public void setContent(boolean showTimestamp, final MessageEntity msgEntity) {
//            Logger.i("type: %s, content: %s, status: %s", msgEntity.getType(), msgEntity.getContent(), msgEntity.getStatus());

            if (showTimestamp) {
                timestampTextView.setVisibility(View.VISIBLE);
                timestampTextView.setText(BDUiUtils.friendlyTime(msgEntity.getCreatedAt(), mContext));
            } else {
                timestampTextView.setVisibility(View.GONE);
            }

            // 文字消息
            // TODO: 支持html富文本，优化机器人问答时常见问题列表显示
            if (messageViewType == MessageEntity.TYPE_TEXT_ID
                    || messageViewType == MessageEntity.TYPE_TEXT_SELF_ID
                    || messageViewType == MessageEntity.TYPE_EVENT_ID
                    || messageViewType == MessageEntity.TYPE_EVENT_SELF_ID) {
                //
                loadAvatar(msgEntity);
                //
                String emotionText = ExpressionUtil.CNtoFace(mContext, msgEntity.getContent());
                String emotionRegex = "appkefu_f0[0-9]{2}|appkefu_f10[0-5]";
                try {
                    SpannableString spannableString = ExpressionUtil.getExpressionString(mContext, emotionText, emotionRegex);
                    contentTextView.setText(spannableString);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                // 点击link
                contentTextView.setOnLinkClickListener(new QMUILinkTextView.OnLinkClickListener() {

                    @Override
                    public void onTelLinkClick(String phoneNumber) {
                        // TODO:
                        Toast.makeText(mContext, "识别到电话号码是：" + phoneNumber, Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
//                        mContext.startActivity(intent);
                        // 复制内容
                        BDUiUtils.copy(mContext, phoneNumber);
                    }

                    @Override
                    public void onMailLinkClick(String mailAddress) {
                        // TODO:
                        Toast.makeText(mContext, "识别到邮件地址是：" + mailAddress, Toast.LENGTH_SHORT).show();
                        // 复制内容
                        BDUiUtils.copy(mContext, mailAddress);
                    }

                    @Override
                    public void onWebUrlLinkClick(String url) {
                        // TODO:
                        Toast.makeText(mContext, "识别到网页链接是：" + url, Toast.LENGTH_SHORT).show();
                        BDUiApi.startHtml5Chat(mContext, url, "打开网址");
                        // 复制内容
                        BDUiUtils.copy(mContext, url);
                    }
                });
                // 长按
                contentTextView.setOnLongClickListener(v -> {
//                    Logger.d("mid:" + msgEntity.getMid());
                    EventBus.getDefault().post(new LongClickEvent(msgEntity));
                    // 复制内容
                    Toast.makeText(mContext, "复制成功:" + msgEntity.getContent(), Toast.LENGTH_SHORT).show();
                    BDUiUtils.copy(mContext, msgEntity.getContent());
                    return false;
                });
            }
            // 图片消息
            else if (messageViewType == MessageEntity.TYPE_IMAGE_ID
                    || messageViewType == MessageEntity.TYPE_IMAGE_SELF_ID) {
                loadAvatar(msgEntity);
                //
                Glide.with(mContext).load(msgEntity.getImageUrl()).into(imageImageView);
                imageImageView.setOnClickListener(view -> {
                    Logger.d("image clicked:" + msgEntity.getImageUrl());
                    if (null != itemClickListener) {
//                            int[] location = new int[2];
//                            // 获取在整个屏幕内的绝对坐标
//                            imageImageView.getLocationOnScreen(location);
//                            ViewData viewData = new ViewData();
//                            viewData.setTargetX(location[0]);
//                            // 此处注意，获取 Y 轴坐标时，需要根据实际情况来处理《状态栏》的高度，判断是否需要计算进去
//                            viewData.setTargetY(location[1]);
//                            viewData.setTargetWidth(imageImageView.getWidth());
//                            viewData.setTargetHeight(imageImageView.getHeight());
//                            itemClickListener.onMessageImageItemClick(viewData, msgEntity.getImageUrl());
                        itemClickListener.onMessageImageItemClick(msgEntity.getImageUrl());
                    }
                });
            }
            // 视频消息
            else if (messageViewType == MessageEntity.TYPE_VIDEO_ID
                    || messageViewType == MessageEntity.TYPE_VIDEO_SELF_ID) {
                loadAvatar(msgEntity);
                // TODO: 显示视频真实thumb
                Glide.with(mContext).load("https://bytedesk.oss-cn-shenzhen.aliyuncs.com/images/videoplay.png").into(videoImageView);
                videoImageView.setOnClickListener(view -> {
                    Logger.d("video clicked:" + msgEntity.getVideoOrShortUrl());
                    if (null != itemClickListener) {
                        itemClickListener.onMessageVideoItemClick(msgEntity.getVideoOrShortUrl());
                    }
                });
            }
            // TODO: 语音消息
            else if (messageViewType == MessageEntity.TYPE_VOICE_ID
                    || messageViewType == MessageEntity.TYPE_VOICE_SELF_ID) {
                loadAvatar(msgEntity);
                //
                if (messageViewType == MessageEntity.TYPE_VOICE_ID) {
                    voiceTextView.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.appkefu_chatfrom_voice_playing, 0);
                    if (msgEntity.isPlayed()) {
                        voiceUnplayedView.setVisibility(View.GONE);
                    }
                } else {
                    voiceTextView.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.appkefu_chatto_voice_playing, 0);
                }
                int width = 40* msgEntity.getLength() > 300 ? 300 : 40*msgEntity.getLength();
                voiceTextView.setWidth(width);
                voiceLengthTextView.setText(msgEntity.getLength()+"\"");
                // TODO: 点击播放语音
                voiceTextView.setOnClickListener(view -> {
//                        Toast.makeText(mContext,"点击播放语音：" + msgEntity.getVoiceUrl() + " length:" + msgEntity.getLength(), Toast.LENGTH_LONG).show();
                    // TODO: 本地播放语音
                    boolean isSend = (messageViewType == MessageEntity.TYPE_VOICE_SELF_ID);
                    onVoiceMessageClicked((TextView)view, isSend, msgEntity.getVoiceUrl());
                    // TODO: 调用接口通知服务器语音已经播放
                });
            }
            // TODO: 问卷消息
            else if (messageViewType == MessageEntity.TYPE_QUESTIONNAIRE_ID) {
                contentTextView.setText(msgEntity.getContent());
            }
            // 通知消息
            else if (messageViewType == MessageEntity.TYPE_NOTIFICATION_ID) {
                notificationTextView.setText(msgEntity.getContent());
            }
            // 商品消息
            else if (messageViewType == MessageEntity.TYPE_COMMODITY_ID) {
                final JsonCustom jsonCustom = new Gson().fromJson(msgEntity.getContent(), JsonCustom.class);
                commodityTitleTextView.setText(jsonCustom.getTitle());
                commodityPriceTextView.setText("¥" + jsonCustom.getPrice());
                //
                Glide.with(mContext).load(jsonCustom.getImageUrl()).into(commodityImageView);
                commodityImageView.setOnClickListener(view -> {
                    Logger.d("点击商品图片: "+ jsonCustom.getUrl());
                    Intent intent = new Intent(mContext, BigImageViewActivity.class);
                    intent.putExtra("image_url", jsonCustom.getImageUrl());
                    mContext.startActivity(intent);
                });
                //
                commoditySendButton.setOnClickListener(view -> {
                    Logger.i("发送商品信息");
                    // TODO
                    EventBus.getDefault().post(new SendCommodityEvent());
                });
            }
            // 红包消息
            else if (messageViewType == MessageEntity.TYPE_RED_PACKET_ID
                    || messageViewType == MessageEntity.TYPE_RED_PACKET_SELF_ID) {
                loadAvatar(msgEntity);
                // TODO: 红包xml layout初始化
            }
            // 文件消息
            else if (messageViewType == MessageEntity.TYPE_FILE_ID
                    || messageViewType == MessageEntity.TYPE_FILE_SELF_ID) {
                loadAvatar(msgEntity);
                //
                fileTextView.setText(msgEntity.getFileUrl());
                fileTextView.setOnLinkClickListener(new QMUILinkTextView.OnLinkClickListener() {

                    @Override
                    public void onTelLinkClick(String phoneNumber) {
                        // TODO:
                        Toast.makeText(mContext, "识别到电话号码是：" + phoneNumber, Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
//                        mContext.startActivity(intent);
                    }
                    @Override
                    public void onMailLinkClick(String mailAddress) {
                        // TODO:
                        Toast.makeText(mContext, "识别到邮件地址是：" + mailAddress, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onWebUrlLinkClick(String url) {
                        // TODO:
                        Toast.makeText(mContext, "识别到网页链接是：" + url, Toast.LENGTH_SHORT).show();
                        BDUiApi.startHtml5Chat(mContext, url, "打开网址");
                    }
                });
                // 长按
                fileTextView.setOnLongClickListener(v -> {
                    Logger.d("mid:" + msgEntity.getMid());
                    EventBus.getDefault().post(new LongClickEvent(msgEntity));
                    return false;
                });
            }
            // 机器人
            else if (messageViewType == MessageEntity.TYPE_ROBOT_ID
                        || messageViewType == MessageEntity.TYPE_ROBOT_SELF_ID) {
                loadAvatar(msgEntity);
                robotTextView.setText("");
                //
                String robotMessageString = msgEntity.getContent();
                Boolean matchFlagBoolean = false;
                int startIndex = 0;
                Pattern robotPattern = Pattern.compile("[0-9]+:[0-9A-Za-z:/[-]_#[?][(),\"\"][“”（），、。？][ ][=][.][&][\\u4e00-\\u9fa5]]*");
                Matcher robotMatcher = robotPattern.matcher(robotMessageString);
                //
                while (robotMatcher.find()) {
                    //
                    matchFlagBoolean = true;
                    robotTextView.append(robotMessageString.substring(startIndex, robotMatcher.start()));

                    String matchString = robotMessageString.substring(robotMatcher.start(), robotMatcher.end());

                    String[] matParts = matchString.split(":");

                    String pidString = matParts[0];
                    String questionString = matchString.substring(pidString.length()+1);

                    ClickableSpan robotSpan = new CustomizedClickableSpan(pidString, questionString);
                    SpannableString spannableMatch = new SpannableString(questionString);
                    spannableMatch.setSpan(robotSpan, 0, questionString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

                    robotTextView.append(spannableMatch);
                    startIndex = robotMatcher.end();
                }
                //
                if (!matchFlagBoolean) {
                    Logger.i("robot not match:" + msgEntity.getContent());
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        robotTextView.setText(Html.fromHtml(msgEntity.getContent(), Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        robotTextView.setText(Html.fromHtml(msgEntity.getContent()));
                    }
                }
                robotTextView.setMovementMethod(LinkMovementMethod.getInstance());
            }

            // 收到的消息
            if (messageViewType == MessageEntity.TYPE_TEXT_ID
                    || messageViewType == MessageEntity.TYPE_IMAGE_ID
                    || messageViewType == MessageEntity.TYPE_VOICE_ID
                    || messageViewType == MessageEntity.TYPE_VIDEO_ID
                    || messageViewType == MessageEntity.TYPE_FILE_ID
                    || messageViewType == MessageEntity.TYPE_RED_PACKET_ID
                    || messageViewType == MessageEntity.TYPE_ROBOT_ID) {
                nicknameTextView.setText(msgEntity.getNickname());
            }

            // 发送的消息
            if (messageViewType == MessageEntity.TYPE_TEXT_SELF_ID
                    || messageViewType == MessageEntity.TYPE_IMAGE_SELF_ID
                    || messageViewType == MessageEntity.TYPE_VOICE_SELF_ID
                    || messageViewType == MessageEntity.TYPE_VIDEO_SELF_ID
                    || messageViewType == MessageEntity.TYPE_FILE_SELF_ID
                    || messageViewType == MessageEntity.TYPE_RED_PACKET_SELF_ID
                    || messageViewType == MessageEntity.TYPE_ROBOT_SELF_ID) {
                //
                if (msgEntity.getStatus() == null) {
                    return;
                }
                //
                if (msgEntity.getStatus().equals(BDCoreConstant.MESSAGE_STATUS_SENDING)) {
                    progressBar.setVisibility(View.VISIBLE);
                    errorImageView.setVisibility(View.GONE);
                    statusTextView.setVisibility(View.GONE);
                } else if (msgEntity.getStatus().equals(BDCoreConstant.MESSAGE_STATUS_ERROR)) {
                    progressBar.setVisibility(View.GONE);
                    errorImageView.setVisibility(View.VISIBLE);
                    statusTextView.setVisibility(View.GONE);
                } else if (msgEntity.getStatus().equals(BDCoreConstant.MESSAGE_STATUS_RECALL) ||
                        msgEntity.getStatus().equals(BDCoreConstant.MESSAGE_STATUS_RECEIVED) ||
                        msgEntity.getStatus().equals(BDCoreConstant.MESSAGE_STATUS_READ)) {
                    progressBar.setVisibility(View.GONE);
                    errorImageView.setVisibility(View.GONE);
                    statusTextView.setVisibility(View.VISIBLE);
                    statusTextView.setText(msgEntity.getStatus() == BDCoreConstant.MESSAGE_STATUS_RECEIVED ? "送达" : "已读");
                } else {
                    progressBar.setVisibility(View.GONE);
                    errorImageView.setVisibility(View.GONE);
                    statusTextView.setVisibility(View.GONE);
                }
            }

//          // FIXME: 仅针对单聊和客服会话有效，群聊暂不发送已读状态
            // 收到消息的时候，不在当前页，重新进入页面之后，发送已读回执
//          // 非自己发送的消息
            //  && !msgEntity.getSessionType().equals(BDCoreConstant.THREAD_TYPE_GROUP)
            if (!msgEntity.isSend()) {
                // 非系统消息类型
                if (!msgEntity.getType().startsWith(BDCoreConstant.MESSAGE_TYPE_NOTIFICATION)) {
                    // 消息状态
                    if (msgEntity.getStatus() == null ||
                            msgEntity.getStatus().equals(BDCoreConstant.MESSAGE_STATUS_STORED) ||
                            msgEntity.getStatus().equals(BDCoreConstant.MESSAGE_STATUS_RECEIVED)) {
                        // 更新本地消息为已读
//                        msgEntity.setStatus(BDCoreConstant.MESSAGE_STATUS_READ);
//                        BDRepository.getInstance(mContext).insertMessageEntity(msgEntity);
                        // 发送已读回执，通知服务器更新状态
//                        BDMqttApi.sendReceiptReadMessage(mContext, msgEntity.getMid(), msgEntity.getThreadTid());
                        // FIXME: 容易造成重复发送，暂时注释掉
//                        ThreadEntity threadEntity = ((ChatBaseActivity)mContext).mThreadEntity;
//                        BDMqttApi.sendReceiptReadMessageProtobuf(mContext, threadEntity, msgEntity.getMid());
                    }
                }
            }

            // TODO: 检查是否阅后即焚, 并开始倒计时
            if (msgEntity.isDestroyAfterReading()) {
                Logger.i("阅后即焚 content: " + msgEntity.getContent() + " length: " + msgEntity.getDestroyAfterLength());
                initDestroyAfterReading(msgEntity);
            }
        }

        @Override
        public void onClick(View view) {
            if (null != itemClickListener) {

            }
        }

        public void setItemClickListener(ChatItemClickListener chatItemClickListener) {
            this.itemClickListener = chatItemClickListener;
        }

        private void initAvatar() {
            avatarImageView = itemView.findViewById(R.id.bytedesk_message_item_header);
            avatarImageView.setBorderColor(ContextCompat.getColor(mContext, R.color.bytedesk_config_color_gray_6));
            avatarImageView.setBorderWidth(QMUIDisplayHelper.dp2px(mContext, 1));
            avatarImageView.setSelectedMaskColor(ContextCompat.getColor(mContext, R.color.bytedesk_config_color_gray_8));
            avatarImageView.setSelectedBorderColor(ContextCompat.getColor(mContext, R.color.bytedesk_config_color_gray_4));
            avatarImageView.setTouchSelectModeEnabled(true);
            avatarImageView.setCircle(true);
        }

        private void loadAvatar(MessageEntity messageEntity) {
            //
            Glide.with(mContext).load(messageEntity.getAvatar()).into(avatarImageView);
            avatarImageView.setOnClickListener(view -> {
                //
                Logger.d("avatar clicked:" + messageEntity.getAvatar());
            });
        }

        /**
         * FIXME: 待完善
         */
        private void initDestroyAfterReading(MessageEntity messageEntity) {
            destroyAfterReadingProgressBar = itemView.findViewById(R.id.bytedesk_message_item_destroy_progress_bar);
            destroyAfterReadingProgressBar.setVisibility(View.VISIBLE);
            // maxValue默认100
            destroyAfterReadingProgressBar.setQMUIProgressBarTextGenerator((progressBar1, value, maxValue) -> 100 * value / maxValue + "%");

            ProgressHandler myHandler = new ProgressHandler();
            myHandler.setProgressBar(destroyAfterReadingProgressBar);
            //
            destroyAfterReadingProgressBar.setMaxValue(messageEntity.getDestroyAfterLength());
            new Thread(() -> {
                for (int i = 0; i <= messageEntity.getDestroyAfterLength(); i++) {
//                    Logger.i("destroy i:" + i);
                    if (i == messageEntity.getDestroyAfterLength()) {
                        Message msg = new Message();
                        msg.what = STOP;
                        msg.arg1 = i;
                        msg.obj = messageEntity.getMid();
                        myHandler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = NEXT;
                        msg.arg1 = i;
                        myHandler.sendMessage(msg);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    /**
     * 响应用户点击语音，播放语音
     *
     * @param textView
     * @param isSend
     * @param voiceUrl
     */
    private void onVoiceMessageClicked(TextView textView, boolean isSend, String voiceUrl) {

        if(m_changeVoiceImage==null) {
            m_changeVoiceImage = new ChangeImage(textView, isSend, 1);
            textView.postDelayed(m_changeVoiceImage, 500);
        }
        else {
            if(m_changeVoiceImage.getView().equals(textView)){
                m_changeVoiceImage.stop();
                m_mediaPlayer.stop();
                m_changeVoiceImage=null;
                return;
            }else{
                m_changeVoiceImage.stop();
                m_changeVoiceImage = new ChangeImage(textView, isSend, 1);
                textView.postDelayed(m_changeVoiceImage, 500);
            }
        }
        playMusic(voiceUrl);
        // 隐藏键盘
//        BDUiUtils.showSysSoftKeybord(mContext, false);
//        ((ChatIMActivity) mContext).mEmotionLayout.setVisibility(View.GONE);
//        ((ChatIMActivity) mContext).mExtensionLayout.setVisibility(View.GONE);
    }

    private final class ChangeImage implements Runnable {

        private TextView mTextView;
        private Boolean isSend;
        private int currentImg;

        ChangeImage(TextView tv, Boolean isTo, int currentImg) {
            this.mTextView = tv;
            this.isSend = isTo;
            this.currentImg = currentImg;
        }

        public TextView getView() {
            return mTextView;
        }

        @Override
        public void run() {
            if (!isSend) {
                mTextView.setCompoundDrawablesWithIntrinsicBounds(
                        KFResUtil.getResofR(mContext).getDrawable(
                                "appkefu_chatfrom_voice_playing_f" + (currentImg++ %3 + 1)),
                        0,0,0);
            } else {
                mTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,
                        KFResUtil.getResofR(mContext).getDrawable(
                                "appkefu_chatto_voice_playing_f" + (currentImg++ % 3 + 1)), 0);
            }
            mTextView.postDelayed(this, 1000);
        }

        public void stop() {
            mTextView.removeCallbacks(this);
            if (!isSend) {
                mTextView.setCompoundDrawablesWithIntrinsicBounds(
                        KFResUtil.getResofR(mContext).getDrawable("appkefu_chatfrom_voice_playing"), 0, 0, 0);
            } else {
                mTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        KFResUtil.getResofR(mContext).getDrawable("appkefu_chatto_voice_playing"), 0);
            }
        }
    }

    private void playMusic(String voiceUrl) {

        // 将voiceUrl转为本地filePath
        String voiceFilePath = BDFileUtils.getVoiceFilePathFromUrl(voiceUrl);

        try {

            if (m_mediaPlayer == null) {
                m_mediaPlayer = new MediaPlayer();
                m_mediaPlayer.setOnErrorListener(this);
            }

            if (m_mediaPlayer.isPlaying()) {
                m_mediaPlayer.stop();
            }

            m_mediaPlayer.reset();
            m_mediaPlayer.setDataSource(voiceFilePath);
            m_mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer player) {
                    // TODO Auto-generated method stub
                    player.start();
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            // mp.release();
                            if(null != m_changeVoiceImage)
                                m_changeVoiceImage.stop();
                        }
                    });
                }
            });

            m_mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static final int STOP = 0x10000;
    protected static final int NEXT = 0x10001;

    private class ProgressHandler extends Handler {
        private WeakReference<QMUIProgressBar> weakCircleProgressBar;

        void setProgressBar( QMUIProgressBar circleProgressBar) {
            weakCircleProgressBar = new WeakReference<>(circleProgressBar);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STOP:
                    String mid = (String) msg.obj;
                    Logger.i("销毁 mid：" + mid);
                    // 本地销毁阅后即焚消息
                    BDRepository.getInstance(mContext).deleteMessage(mid);
                    // 通知服务器销毁
//                    BDMqttApi.sendReceiptReceivedMessage(mContext, mid);
                case NEXT:
                    if (!Thread.currentThread().isInterrupted()) {
                        if (weakCircleProgressBar.get() != null) {
                            weakCircleProgressBar.get().setProgress(msg.arg1);
                        }
                    }
            }

        }
    }

    private class CustomizedClickableSpan extends ClickableSpan {

        String aid;
        String question;

        public CustomizedClickableSpan(String aid, String question) {
            super();
            this.aid = aid;
            this.question = question;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
            // ds.setColor(Color.GREEN);
            ds.setUnderlineText(true);
            // ds.setAlpha(50);
        }

        @Override
        public void onClick(View widget) {
            Logger.i("aid:" + aid + " question:"+question);

            EventBus.getDefault().post(new QueryAnswerEvent(aid, question));
        }
    }

    @Override
    public boolean onError(MediaPlayer player, int what, int extra) {
        // TODO Auto-generated method stub
        Logger.d("mediaplayer error what:" + what + " extra:" + extra);
        m_mediaPlayer.reset();
        return false;
    }

}
