package com.bytedesk.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.ui.R;
import com.bytedesk.ui.util.BDUiConstant;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.json.JSONException;
import org.json.JSONObject;

public class ContactProfileActivity extends AppCompatActivity {

    private QMUITopBarLayout mTopBar;
    private QMUIGroupListView mGroupListView;

    private String mUid;
    private String mTid;

    private QMUIRadiusImageView avatarImageView;
    private QMUICommonListItemView profileItem;
    private QMUICommonListItemView makeTopItem;
    private QMUICommonListItemView unDisturbItem;
    private QMUICommonListItemView shieldItem;
    private QMUICommonListItemView clearMessageItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_contact_profile);
        //
        mUid = getIntent().getStringExtra(BDUiConstant.EXTRA_UID);
        //
        initTopBar();
        initView();
        initModel();
    }

    private void initTopBar() {
        //
        mTopBar = findViewById(R.id.bytedesk_contact_profile_topbarlayout);
        mTopBar.setTitle("个人详情");
        mTopBar.setBackgroundColor(getResources().getColor(R.color.albumColorPrimary));
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> finish());
        //
        QMUIStatusBarHelper.translucent(this);
    }

    /**
     * 界面初始化
     */
    private void initView () {
        //
        mGroupListView = findViewById(R.id.bytedesk_contact_profile_groupListView);
        //
        avatarImageView = new QMUIRadiusImageView(this);
//        Glide.with(this).load(avatar).into(avatarImageView);

        ////
        profileItem = mGroupListView.createItemView("");
        profileItem.setOrientation(QMUICommonListItemView.VERTICAL);

//        profileItem.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_tabbar_contact));
        profileItem.setImageDrawable(avatarImageView.getDrawable());
        profileItem.setDetailText("");

        QMUIGroupListView.newSection(this).addItemView(profileItem, view -> Logger.d("profile item clicked")).addTo(mGroupListView);
        //
        addOtherItemToLayout();
    }

    /**
     *
     */
    private void initModel () {

        BDCoreApi.userDetail(this, mUid, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {
                    JSONObject dataObject = object.getJSONObject("data");
                    JSONObject userObject = dataObject.getJSONObject("user");

                    // 群组对应会话tid
                    mTid = dataObject.getString("threadTid");
                    // 会话是否被置顶
                    boolean isTopThread = dataObject.getBoolean("isTopThread");
                    makeTopItem.getSwitch().setChecked(isTopThread);
                    // 会话是否设置免打扰
                    boolean isNoDisturb = dataObject.getBoolean("isNoDisturb");
                    unDisturbItem.getSwitch().setChecked(isNoDisturb);
                    // 是否屏蔽对方
                    boolean isShield = dataObject.getBoolean("isShield");
                    shieldItem.getSwitch().setChecked(isShield);

                    String nickname = userObject.getString("nickname");
                    profileItem.setDetailText(nickname);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {

            }
        });

    }


    private void addOtherItemToLayout() {

        final Context context = this;
        clearMessageItem = mGroupListView.createItemView("清空聊天记录");
        makeTopItem = mGroupListView.createItemView("会话置顶");
        makeTopItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        makeTopItem.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //
                if (isChecked) {
                    BDCoreApi.markTopThread(context, mTid, new BaseCallback() {

                        @Override
                        public void onSuccess(JSONObject object) {

                        }

                        @Override
                        public void onError(JSONObject object) {

                        }
                    });
                } else {
                    BDCoreApi.unmarkTopThread(context, mTid, new BaseCallback() {
                        @Override
                        public void onSuccess(JSONObject object) {

                        }

                        @Override
                        public void onError(JSONObject object) {

                        }
                    });
                }
            }
        });

        unDisturbItem = mGroupListView.createItemView("消息免打扰");
        unDisturbItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        unDisturbItem.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //
                if (isChecked) {
                    //
                    BDCoreApi.markNoDisturbThread(context, mTid, new BaseCallback() {
                        @Override
                        public void onSuccess(JSONObject object) {

                        }

                        @Override
                        public void onError(JSONObject object) {

                        }
                    });
                } else {
                    //
                    BDCoreApi.unmarkNoDisturbThread(context, mTid, new BaseCallback() {
                        @Override
                        public void onSuccess(JSONObject object) {

                        }

                        @Override
                        public void onError(JSONObject object) {

                        }
                    });
                }
            }
        });

        shieldItem = mGroupListView.createItemView("屏蔽");
        shieldItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        shieldItem.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //
                if (isChecked) {
                    // 屏蔽
                    BDCoreApi.shield(context, mUid, new BaseCallback() {
                        @Override
                        public void onSuccess(JSONObject object) {

                        }

                        @Override
                        public void onError(JSONObject object) {

                        }
                    });
                } else {
                    // 取消屏蔽
                    BDCoreApi.unshield(context, mUid, new BaseCallback() {
                        @Override
                        public void onSuccess(JSONObject object) {

                        }

                        @Override
                        public void onError(JSONObject object) {

                        }
                    });
                }
            }
        });

        QMUIGroupListView.newSection(this).addItemView(unDisturbItem, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Logger.i("消息免打扰");

            }
        }).addItemView(makeTopItem, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("会话置顶");

                //
                BDCoreApi.markTopThread(context, mTid, new BaseCallback() {

                    @Override
                    public void onSuccess(JSONObject object) {

                    }

                    @Override
                    public void onError(JSONObject object) {

                    }
                });

            }
        }).addItemView(shieldItem, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Logger.i("屏蔽");
            }
        }).addTo(mGroupListView);

        QMUIGroupListView.newSection(this).addItemView(clearMessageItem, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("清空聊天记录");

                new QMUIDialog.MessageDialogBuilder(context)
                        .setTitle("清空")
                        .setMessage("确定要清空聊天记录吗？")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction(0, "清空", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {

                                //
                                BDCoreApi.markClearContactMessage(context, mUid, new BaseCallback() {
                                    @Override
                                    public void onSuccess(JSONObject object) {

                                    }

                                    @Override
                                    public void onError(JSONObject object) {

                                    }
                                });


                                dialog.dismiss();
                            }
                        }).show();

            }
        }).addTo(mGroupListView);
    }




}
