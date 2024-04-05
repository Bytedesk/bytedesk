package com.bytedesk.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.util.BDPreferenceManager;
import com.bytedesk.ui.R;
import com.bytedesk.ui.util.BDUiConstant;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIFloatLayout;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 群组详情
 *
 * @author bytedesk.com on 2018/11/22
 */
public class GroupProfileActivity extends AppCompatActivity {

    private QMUITopBarLayout mTopBar;
    private QMUIFloatLayout mMembersFloatLayout;
    private QMUIGroupListView mGroupListView;

    private String mGid;
    private String mTid;

    private QMUICommonListItemView nicknameItem;
    private QMUICommonListItemView qrCodeItem;
    private QMUICommonListItemView descriptionItem;
    private QMUICommonListItemView announcementItem;
//    private QMUICommonListItemView transferItem;
    private QMUICommonListItemView withdrawItem;
    private QMUICommonListItemView dismissItem;
    private QMUICommonListItemView clearMessageItem;
    private QMUICommonListItemView makeTopItem;
    private QMUICommonListItemView unDisturbItem;

    private BDPreferenceManager mPreferenceManager;

    private Boolean mIsAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.bytedesk_activity_group_profile);
        mPreferenceManager = BDPreferenceManager.getInstance(this);
        //
        mGid = getIntent().getStringExtra(BDUiConstant.EXTRA_UID);
        mIsAdmin = false;
        //
        initTopBar();
        initView();
        initModel();
        //
        addOtherItemToLayout();
    }


    private void initTopBar() {
        //
        mTopBar = findViewById(R.id.bytedesk_group_profile_topbarlayout);
        mTopBar.setTitle("群组详情");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //
        QMUIStatusBarHelper.translucent(this);
    }

    /**
     * 界面初始化
     */
    private void initView () {

        mMembersFloatLayout = findViewById(R.id.bytedesk_group_profile_floatlayout);
        mMembersFloatLayout.setOnLineCountChangeListener(new QMUIFloatLayout.OnLineCountChangeListener() {
            @Override
            public void onChange(int oldLineCount, int newLineCount) {
                Logger.i("oldLineCount = " + oldLineCount + " ;newLineCount = " + newLineCount);
            }
        });
        //
        mGroupListView = findViewById(R.id.bytedesk_group_profile_groupListView);
        //
        nicknameItem = mGroupListView.createItemView("群名称");
        nicknameItem.setDetailText("未命名");
        nicknameItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //
        qrCodeItem = mGroupListView.createItemView("群二维码");
        qrCodeItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.bytedesk_qr_code));
        qrCodeItem.addAccessoryCustomView(imageView);

        //
        descriptionItem = mGroupListView.createItemView("群简介");
        descriptionItem.setDetailText("未设置");
        descriptionItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //
        announcementItem = mGroupListView.createItemView("群公告");
        announcementItem.setDetailText("未设置");
        announcementItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        final Context context = this;
        QMUIGroupListView.newSection(this).addItemView(nicknameItem, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Logger.d("群聊名称");

            if (!mIsAdmin) {
                return;
            }

            final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(context);
            builder.setTitle("群聊名称")
                .setPlaceholder("在此输入群聊名称")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                    CharSequence text = builder.getEditText().getText();
                    if (text != null && text.length() > 0) {
                        dialog.dismiss();

                        final String nickname = text.toString();

                        // 调用服务器接口
                        BDCoreApi.updateGroupNickname(context, mGid, nickname, new BaseCallback() {
                            @Override
                            public void onSuccess(JSONObject object) {

                                nicknameItem.setDetailText(nickname);

                                // TODO: 更新本地群组会话thread昵称、群组昵称

                            }

                            @Override
                            public void onError(JSONObject object) {

                                Toast.makeText(context, "更新群昵称失败", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(context, "请填入群聊名称", Toast.LENGTH_SHORT).show();
                    }
                    }
                }).create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
            }
        }).addItemView(qrCodeItem, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  TODO: 群二维码
                Logger.i("群二维码");
                Intent intent = new Intent(GroupProfileActivity.this, QRCodeActivity.class);
                intent.putExtra(BDUiConstant.EXTRA_GID, mGid);
                startActivity(intent);

            }
        }).addItemView(descriptionItem, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("群简介");

                if (!mIsAdmin) {
                    return;
                }

                // FIXME: android.view.InflateException: Binary XML file line #17: Error inflating class com.qmuiteam.qmui.widget.dialog.QMUIDialogView

                final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(context);
                builder.setTitle("群简介")
                        .setPlaceholder("在此输入群描述")
                        .setInputType(InputType.TYPE_CLASS_TEXT)
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                CharSequence text = builder.getEditText().getText();
                                if (text != null && text.length() > 0) {
                                    dialog.dismiss();

                                    final String description = text.toString();
                                    // TODO: 调用服务器接口
                                    BDCoreApi.updateGroupDescription(context, mGid, description, new BaseCallback() {

                                        @Override
                                        public void onSuccess(JSONObject object) {

                                            descriptionItem.setDetailText(description);
                                        }

                                        @Override
                                        public void onError(JSONObject object) {

                                            Toast.makeText(context, "更新群简介失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    Toast.makeText(context, "请填入群公告", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();

            }
        }).addItemView(announcementItem, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("群公告");

            if (!mIsAdmin) {
                return;
            }

            // FIXME: android.view.InflateException: Binary XML file line #17: Error inflating class com.qmuiteam.qmui.widget.dialog.QMUIDialogView

            final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(context);
            builder.setTitle("群公告")
                .setPlaceholder("在此输入群公告")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                    CharSequence text = builder.getEditText().getText();
                    if (text != null && text.length() > 0) {
                        dialog.dismiss();

                        final String announcement = text.toString();
                        // TODO: 调用服务器接口
                        BDCoreApi.updateGroupAnnouncement(context, mGid, announcement, new BaseCallback() {

                            @Override
                            public void onSuccess(JSONObject object) {

                                announcementItem.setDetailText(announcement);
                            }

                            @Override
                            public void onError(JSONObject object) {

                                Toast.makeText(context, "更新群公告失败", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(context, "请填入群公告", Toast.LENGTH_SHORT).show();
                    }
                    }
                }).create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();

            }
        }).addTo(mGroupListView);

    }

    /**
     * 初始化ModelView
     *
     * 加载群成员
     */
    private void initModel () {

        // 查询群组详情
        BDCoreApi.getGroupDetail(this, mGid, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    JSONObject dataObject = object.getJSONObject("data");
                    // 群组信息
                    JSONObject groupObject = dataObject.getJSONObject("group");
                    // 群组对应会话tid
                    mTid = dataObject.getString("threadTid");
                    // 会话是否被置顶
                    boolean isTopThread = dataObject.getBoolean("isTopThread");
                    makeTopItem.getSwitch().setChecked(isTopThread);
                    // 会话是否设置免打扰
                    boolean isNoDisturb = dataObject.getBoolean("isNoDisturb");
                    unDisturbItem.getSwitch().setChecked(isNoDisturb);
                    //
                    String groupNickname = groupObject.getString("nickname");
                    nicknameItem.setDetailText(groupNickname);
                    //
                    String description = groupObject.getString("description");
                    descriptionItem.setDetailText(description);
                    //
                    String announcement = groupObject.getString("announcement");
                    announcementItem.setDetailText(announcement);
                    //
                    JSONArray membersArray = groupObject.getJSONArray("members");
                    //
                    for (int i = 0; i < membersArray.length(); i++) {
                        JSONObject memberObject = membersArray.getJSONObject(i);
                        //
                        String uid = memberObject.getString("uid");
                        String nickname = memberObject.getString("nickname");
                        String avatarUrl = memberObject.getString("avatar");
                        //
                        addItemToFloatLayout(uid, nickname, avatarUrl);
                    }
                    // TODO: 待完善
                     addPlusItemToFloatLayout();
                    //
                    JSONArray adminsArray = groupObject.getJSONArray("admins");
                    for (int i = 0; i < adminsArray.length(); i++) {
                        JSONObject adminObject = adminsArray.getJSONObject(i);
                        //
                        String adminUid = adminObject.getString("uid");
                        if (adminUid.equals(mPreferenceManager.getUid())) {
                            mIsAdmin = true;
                            addAdminItemToLayout();
                            // TODO: 待完善
                            // addMinusItemToFloatLayout();
                        }
                    }

                    if (!mIsAdmin) {
                        addWithdrawItemToLayout();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(JSONObject object) {
                Toast.makeText(getApplicationContext(), "加载群成员失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addWithdrawItemToLayout() {
        final Context context = this;
        withdrawItem = mGroupListView.createItemView("退出群");
        QMUIGroupListView.newSection(this).addItemView(withdrawItem, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("退出群");

                new QMUIDialog.MessageDialogBuilder(context)
                        .setTitle("退出群")
                        .setMessage("确定要退群吗？")
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction(0, "退群", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {

                                // TODO: 调用退群接口
                                BDCoreApi.withdrawGroup(context, mGid, new BaseCallback() {

                                    @Override
                                    public void onSuccess(JSONObject object) {

                                        Toast.makeText(context, "退群成功", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(JSONObject object) {

                                        Toast.makeText(context, "退群失败", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                dialog.dismiss();
                            }
                        }).show();
            }
        }).addTo(mGroupListView);
    }

    private void addAdminItemToLayout() {

        // 暂不添加
        // transferItem = mGroupListView.createItemView("移交群");
        dismissItem = mGroupListView.createItemView("解散群");

        final Context context = this;
        QMUIGroupListView.newSection(this).addItemView(dismissItem, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("解散群");

                new QMUIDialog.MessageDialogBuilder(context)
                    .setTitle("解散群")
                    .setMessage("确定要解散吗？")
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction(0, "解散", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {

                            // TODO: 调用服务器接口
                            BDCoreApi.dismissGroup(context, mGid, new BaseCallback() {

                                @Override
                                public void onSuccess(JSONObject object) {

                                    Toast.makeText(context, "解散成功", Toast.LENGTH_SHORT).show();

                                    // TODO: 关闭当前页面
                                }

                                @Override
                                public void onError(JSONObject object) {

                                    Toast.makeText(context, "解散失败", Toast.LENGTH_SHORT).show();
                                }
                            });

                            dialog.dismiss();
                        }
                    }).show();

            }
        }).addTo(mGroupListView);
    }

    private void addOtherItemToLayout() {

        clearMessageItem = mGroupListView.createItemView("清空聊天记录");
        makeTopItem = mGroupListView.createItemView("会话置顶");
        makeTopItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        makeTopItem.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //
                if (isChecked) {
                    BDCoreApi.markTopThread(GroupProfileActivity.this, mTid, new BaseCallback() {

                        @Override
                        public void onSuccess(JSONObject object) {

                        }

                        @Override
                        public void onError(JSONObject object) {

                        }
                    });
                } else {
                    BDCoreApi.unmarkTopThread(GroupProfileActivity.this, mTid, new BaseCallback() {
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
                    BDCoreApi.markNoDisturbThread(GroupProfileActivity.this, mTid, new BaseCallback() {
                        @Override
                        public void onSuccess(JSONObject object) {

                        }

                        @Override
                        public void onError(JSONObject object) {

                        }
                    });
                } else {
                    //
                    BDCoreApi.unmarkNoDisturbThread(GroupProfileActivity.this, mTid, new BaseCallback() {
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

        final Context context = this;
        QMUIGroupListView.newSection(this).addItemView(unDisturbItem, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Logger.i("消息免打扰");

            }
        }).addItemView(clearMessageItem, new View.OnClickListener() {
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

                        // FIXME: 传递threadId or groupId
                        BDCoreApi.markClearGroupMessage(GroupProfileActivity.this, mGid, new BaseCallback() {

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
        }).addItemView(makeTopItem, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("会话置顶");

            // FIXME: 传递threadId or groupId
            BDCoreApi.markTopThread(GroupProfileActivity.this, mTid, new BaseCallback() {

                @Override
                public void onSuccess(JSONObject object) {

                }

                @Override
                public void onError(JSONObject object) {

                }
            });

            }
        }).addTo(mGroupListView);
    }


    private void addItemToFloatLayout(final String uid, final String nickname, final String avatarUrl) {

        LinearLayout linearLayout = new LinearLayout(GroupProfileActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);

        //
        QMUIRadiusImageView avatarImageView = new QMUIRadiusImageView(this);
        ViewGroup.LayoutParams avatarLayoutParams = new LinearLayout.LayoutParams(100, 100);
        avatarImageView.setLayoutParams(avatarLayoutParams);
        Glide.with(this).load(avatarUrl).into(avatarImageView);
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("nickname clicked: " + nickname);

                // 非管理员 或者 点击了自己的头像，直接返回
                if (!mIsAdmin || uid.equals(mPreferenceManager.getUid())) {
                    return;
                }

                // 管理员操作
                final String[] items = new String[]{"转交群", "踢出群"};
                new QMUIDialog.CheckableDialogBuilder(GroupProfileActivity.this)
                    .addItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int index) {

                            if (index == 0) {
                                // 转交群
                                Logger.d("转交群");

                                new QMUIDialog.MessageDialogBuilder(GroupProfileActivity.this)
                                        .setTitle("解散群")
                                        .setMessage("确定要转交群？")
                                        .addAction("取消", (dialog1, index1) -> dialog.dismiss())
                                        .addAction(0, "转交", QMUIDialogAction.ACTION_PROP_NEGATIVE, (dialog1, index1) -> {

                                            // 调用服务器接口
                                            BDCoreApi.transferGroup(GroupProfileActivity.this, uid, mGid, false, new BaseCallback() {

                                                @Override
                                                public void onSuccess(JSONObject object) {

                                                }

                                                @Override
                                                public void onError(JSONObject object) {

                                                }
                                            });

                                            dialog.dismiss();

                                        }).show();

                            } else if (index == 1) {
                                // 踢出群
                                Logger.d("踢出群");

                                new QMUIDialog.CheckBoxMessageDialogBuilder(GroupProfileActivity.this)
                                    .setTitle("提示")
                                    .setMessage("确定要踢出群？")
                                    .setChecked(true)
                                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                                        @Override
                                        public void onClick(QMUIDialog dialog, int index) {

                                            BDCoreApi.kickGroupMember(GroupProfileActivity.this, uid, mGid, new BaseCallback() {
                                                @Override
                                                public void onSuccess(JSONObject object) {

                                                    // TODO: 从本地删除，更新UI

                                                }

                                                @Override
                                                public void onError(JSONObject object) {

                                                }
                                            });

                                            dialog.dismiss();
                                        }
                                    })
                                    .addAction("退出", new QMUIDialogAction.ActionListener() {
                                        @Override
                                        public void onClick(QMUIDialog dialog, int index) {
                                            dialog.dismiss();
                                        }
                                    }).show();

                            }

                            dialog.dismiss();
                        }
                    }).show();

            }
        });
        avatarImageView.setBorderWidth(0);
        linearLayout.addView(avatarImageView);

        //
        TextView textView = new TextView(this);
        textView.setTextSize(12);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setText(nickname);
        linearLayout.addView(textView);

//        int textViewSize = QMUIDisplayHelper.dpToPx(60);
//        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(textViewSize, textViewSize);

//        mMembersFloatLayout.addView(avatarImageView, lp);
//        mMembersFloatLayout.addView(linearLayout, lp);

        mMembersFloatLayout.addView(linearLayout);
    }

    private void addPlusItemToFloatLayout() {

        //
        QMUIRadiusImageView plusImageView = new QMUIRadiusImageView(this);
        ViewGroup.LayoutParams avatarLayoutParams = new LinearLayout.LayoutParams(100, 100);
        plusImageView.setLayoutParams(avatarLayoutParams);
        plusImageView.setImageResource(R.drawable.bytedesk_group_plus);
        plusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("plus clicked:");
                //
                Intent intent = new Intent(GroupProfileActivity.this, ContactSelectActivity.class);
                intent.putExtra(BDUiConstant.EXTRA_UID, mGid);
                startActivity(intent);
            }
        });
        plusImageView.setBorderWidth(0);

        mMembersFloatLayout.addView(plusImageView);
    }

    private void addMinusItemToFloatLayout() {

        //
        QMUIRadiusImageView avatarImageView = new QMUIRadiusImageView(this);
        avatarImageView.setImageResource(R.drawable.bytedesk_group_minus);
        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("minus clicked:");
            }
        });
        avatarImageView.setBorderWidth(0);

        int textViewSize = QMUIDisplayHelper.dpToPx(65);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(textViewSize, textViewSize);
        mMembersFloatLayout.addView(avatarImageView, lp);
    }

    private void removeItemFromFloatLayout(QMUIFloatLayout floatLayout) {
        if (floatLayout.getChildCount() == 0) {
            return;
        }
        floatLayout.removeView(floatLayout.getChildAt(floatLayout.getChildCount() - 1));
    }


}
