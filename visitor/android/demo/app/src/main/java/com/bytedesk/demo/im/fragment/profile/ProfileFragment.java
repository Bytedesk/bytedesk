package com.bytedesk.demo.im.fragment.profile;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.event.ProfileEvent;
import com.bytedesk.core.util.BDPreferenceManager;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BDConstants;
import com.bytedesk.demo.common.BaseFragment;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;

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
public class ProfileFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView) QMUIGroupListView mGroupListView;

    private QMUIRadiusImageView avatarImageView;
    private QMUICommonListItemView profileItem;
    private QMUICommonListItemView autoItem;
    private QMUICommonListItemView acceptStatusItem;

    private BDPreferenceManager mPreferenceManager;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_setting, null);
        ButterKnife.bind(this, root);

        EventBus.getDefault().register(this);
        mPreferenceManager = BDPreferenceManager.getInstance(getContext());

        initTopBar();
        initGroupListView();

        getProfile();

        return root;
    }

    /**
     *
     */
    protected void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle("个人资料");
    }

    /**
     *
     */
    private void initGroupListView() {

        //
        avatarImageView = new QMUIRadiusImageView(getContext());
        Glide.with(getContext()).load(mPreferenceManager.getAvatar()).into(avatarImageView);

        ////
        profileItem = mGroupListView.createItemView(mPreferenceManager.getNickname());
        profileItem.setOrientation(QMUICommonListItemView.VERTICAL);
        profileItem.setDetailText(mPreferenceManager.getDescription());
//        profileItem.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_tabbar_contact));

        // FIXME: 无效果
        profileItem.setImageDrawable(avatarImageView.getDrawable());
        profileItem.setDetailText(mPreferenceManager.getDescription() + "：" + mPreferenceManager.getUid());

        QMUIGroupListView.newSection(getContext()).addItemView(profileItem, view -> {
            Logger.d("profile item clicked");
            showProfileSheet();
        }).addTo(mGroupListView);

        ///////
        autoItem = mGroupListView.createItemView("自动回复");
        autoItem.setDetailText(mPreferenceManager.getAutoReplyContent());
        autoItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        acceptStatusItem = mGroupListView.createItemView("接待状态");
        acceptStatusItem.setDetailText(mPreferenceManager.getAcceptStatus());
        acceptStatusItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //
        QMUIGroupListView.newSection(getContext())
                .setTitle("客服端相关接口")
                .setDescription("可用于开发者自行开发客服端(用于客服接待访客)，注意: 非访客端")
                .addItemView(autoItem, view -> {
                    Logger.d("autoItem item clicked");
                    showAutoReplySheet();
                }).addItemView(acceptStatusItem, view -> {
                    Logger.d("acceptStatusItem item clicked");
                    showAcceptStatusSheet();
                }).addTo(mGroupListView);
    }

    /**
     *
     */
    private void showProfileSheet() {

        new QMUIBottomSheet.BottomListSheetBuilder(getActivity())
                .addItem("修改头像TODO")
                .addItem("修改昵称TODO")
                .addItem("修改签名TODO")
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    dialog.dismiss();

                    if (position == 0) {

                        // 选取头像
                        chooseImage();

                    } else if (position == 1) {

                        // 弹出输入框，修改昵称
                        showEditNicknameDialog();

                    } else if (position == 2) {

                        // 弹出输入框，修改签名
                        showEditDescriptionDialog();

                    }
                })
                .build()
                .show();
    }

    /**
     *
     */
    private void showAutoReplySheet() {

        new QMUIBottomSheet.BottomListSheetBuilder(getActivity())
                .addItem(BDConstants.AUTO_REPLY_NO)
                .addItem(BDConstants.AUTO_REPLY_EAT)
                .addItem(BDConstants.AUTO_REPLY_LEAVE)
                .addItem(BDConstants.AUTO_REPLY_BACK)
                .addItem(BDConstants.AUTO_REPLY_PHONE)
                .addItem(BDConstants.AUTO_REPLY_SELF)
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    dialog.dismiss();

                    //
                    boolean isAutoReply = position == 0 ? false : true;
                    final String content = tag;
                    BDCoreApi.updateAutoReply(getContext(), isAutoReply, content, "", new BaseCallback() {
                        @Override
                        public void onSuccess(JSONObject object) {

                            autoItem.setDetailText(content);
                            mPreferenceManager.setAutoReplyContent(content);
                        }

                        @Override
                        public void onError(JSONObject object) {

                            // TODO: 报错提示
                            Toast.makeText(getActivity(), "设置自动回复错误", Toast.LENGTH_SHORT).show();
                        }
                    });

                })
                .build()
                .show();
    }

    /**
     *
     */
    private void showAcceptStatusSheet() {
        new QMUIBottomSheet.BottomListSheetBuilder(getActivity())
                .addItem(BDConstants.STATUS_ONLINE)
                .addItem(BDConstants.STATUS_BUSY)
                .addItem(BDConstants.STATUS_REST)
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    dialog.dismiss();

                    String status;
                    final String statusText = tag;
                    if (statusText.equals(BDConstants.STATUS_ONLINE)) {
                        status = BDConstants.USER_STATUS_ONLINE;
                    } else if (statusText.equals(BDConstants.STATUS_BUSY)) {
                        status = BDConstants.USER_STATUS_BUSY;
                    } else {
                        status = BDConstants.USER_STATUS_REST;
                    }
                    //
                    BDCoreApi.setAcceptStatus(getContext(), status, new BaseCallback() {
                        @Override
                        public void onSuccess(JSONObject object) {

                            acceptStatusItem.setDetailText(statusText);
                            mPreferenceManager.setAcceptStatus(statusText);
                        }

                        @Override
                        public void onError(JSONObject object) {

                            // TODO: 报错提示
                            Toast.makeText(getActivity(), "设置在线状态错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .build()
                .show();
    }


    /**
     * 输入昵称
     */
    private void showEditNicknameDialog() {

        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("昵称")
                .setPlaceholder("在此输入昵称")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    final CharSequence text = builder.getEditText().getText();
                    if (text != null && text.length() > 0) {

                        dialog.dismiss();


                    } else {
                        Toast.makeText(getActivity(), "请填入昵称", Toast.LENGTH_SHORT).show();
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    /**
     * 输入签名
     */
    private void showEditDescriptionDialog() {

        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("签名")
            .setPlaceholder("在此输入签名")
            .setInputType(InputType.TYPE_CLASS_TEXT)
            .addAction("取消", (dialog, index) -> dialog.dismiss())
            .addAction("确定", (dialog, index) -> {
                final CharSequence text = builder.getEditText().getText();
                if (text != null && text.length() > 0) {

                    dialog.dismiss();


                } else {
                    Toast.makeText(getActivity(), "请填入签名", Toast.LENGTH_SHORT).show();
                }
            })
            .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }


    /**
     * 选择图片
     */
    private void chooseImage() {
        // 目前仅允许一次选一张图片
        Album.image(this)
                .singleChoice()
                .camera(false)
                .onResult(result -> {

                    if (result.size() > 0) {
                        AlbumFile albumFile = result.get(0);

                        uploadAvatar(albumFile.getPath());
                    }
                })
                .onCancel(result -> Toast.makeText(getContext(), "取消选择图片", Toast.LENGTH_LONG).show())
                .start();
    }

    /**
     * 上传图片 并 设置头像
     * @param filePath
     */
    private void uploadAvatar(String filePath) {

        Logger.i("uploadAvatar %s", filePath);

        BDCoreApi.uploadAvatar(getContext(), filePath, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                try {

                    String avatarUrl = object.getString("data");
                    Logger.i("avatarUrl %s", avatarUrl);

                    BDCoreApi.setAvatar(getContext(), avatarUrl, new BaseCallback() {
                        @Override
                        public void onSuccess(JSONObject object) {

                        }

                        @Override
                        public void onError(JSONObject object) {

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //


            }

            @Override
            public void onError(JSONObject object) {

            }
        });

    }

    /**
     * 监听 EventBus 广播消息
     *
     * FIXME: EventBus: No subscribers registered for event class com.bytedesk.core.event.ProfileEvent
     *
     * @param profileEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProfileEvent(ProfileEvent profileEvent) {
        Logger.i("onProfileEvent");

        try {

            JSONObject infoObject = profileEvent.getJsonObject();

            mPreferenceManager.setUid(infoObject.getString("uid"));
            mPreferenceManager.setUsername(infoObject.getString("username"));
            mPreferenceManager.setNickname(infoObject.getString("nickname"));
            mPreferenceManager.setAvatar(infoObject.getString("avatar"));

            // 初始化界面
            profileItem.setText(mPreferenceManager.getNickname());
            profileItem.setDetailText(mPreferenceManager.getDescription());

            // FIXME: 无效果
            Glide.with(getContext()).load(mPreferenceManager.getAvatar()).into(avatarImageView);
            profileItem.setImageDrawable(avatarImageView.getDrawable());

//            // 添加订阅主题
//            String subDomain = infoObject.getString("subDomain");
//            String subDomainTopic = "subDomain/" + subDomain;
//            BDMqttApi.subscribeTopic(getActivity(), subDomainTopic);
//
//            String uid = infoObject.getString("uid");
//            String userTopic = "user/" + uid;
//            BDMqttApi.subscribeTopic(getActivity(), userTopic);
//
//            String contactTopic = "contact/" + uid;
//            BDMqttApi.subscribeTopic(getActivity(), contactTopic);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getProfile() {

        BDCoreApi.userProfile(getContext(), new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

            }

            @Override
            public void onError(JSONObject object) {

            }
        });
    }
}
