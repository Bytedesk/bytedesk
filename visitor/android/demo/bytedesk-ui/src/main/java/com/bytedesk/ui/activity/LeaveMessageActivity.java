package com.bytedesk.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.ui.R;
import com.bytedesk.ui.util.BDUiConstant;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.json.JSONException;
import org.json.JSONObject;


public class LeaveMessageActivity extends AppCompatActivity {

    private QMUITopBarLayout mTopBar;

    private QMUIGroupListView mGroupListView;
    //
    private QMUICommonListItemView contentItem;
    private QMUICommonListItemView mobileItem;
//    private QMUICommonListItemView emailItem;

    private String workGroupWid;
    private String type;
    private String agentUid;
    //
    private String mContent;
    private String mMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_leave_message);

        mGroupListView = findViewById(R.id.bytedesk_leave_msg_groupListView);
        workGroupWid = getIntent().getStringExtra(BDUiConstant.EXTRA_WID);
        type = getIntent().getStringExtra(BDUiConstant.EXTRA_REQUEST_TYPE);
        agentUid = getIntent().getStringExtra(BDUiConstant.EXTRA_AID);

        initTopBar();
        initView();
    }

    private void initTopBar() {
        //
        mTopBar = findViewById(R.id.bytedesk_leave_msg_topbarlayout);
        mTopBar.setTitle("留言");
        mTopBar.setBackgroundColor(getResources().getColor(R.color.albumColorPrimary));
        mTopBar.addLeftBackImageButton().setOnClickListener(view -> finish());
        //
        QMUIStatusBarHelper.translucent(this);
    }

    private void initView() {
        //
        // 内容
        contentItem = mGroupListView.createItemView("内容");
        QMUIGroupListView.newSection(this)
                .addItemView(contentItem, v -> {

                    showEditContentDialog();

                }).addTo(mGroupListView);

        // 联系方式
        mobileItem = mGroupListView.createItemView("手机号");
//        emailItem = mGroupListView.createItemView("邮箱");
        QMUIGroupListView.newSection(this)
                .addItemView(mobileItem, v -> {

                    showEditMobileDialog();

                }).addTo(mGroupListView);

        // 提交
        QMUICommonListItemView submitItem = mGroupListView.createItemView("提交");
        submitItem.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        QMUIGroupListView.newSection(this)
                .addItemView(submitItem, v -> {
                    Logger.i("提交");

                    submit();

                }).addTo(mGroupListView);
    }


    /**
     * 输入留言内容
     */
    private void showEditContentDialog() {
        //
        final Context context = this;
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("留言内容")
                .setPlaceholder("在此输入内容")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    final CharSequence text = builder.getEditText().getText();
                    if (text != null && text.length() > 0) {

                        dialog.dismiss();
                        mContent = text.toString();
                        contentItem.setDetailText(text.toString());

                    } else {
                        Toast.makeText(context, "请填入内容", Toast.LENGTH_SHORT).show();
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    /**
     * 输入手机号
     */
    private void showEditMobileDialog() {
        //
        final Context context = this;
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("手机号")
                .setPlaceholder("在此输入手机号")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        final CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {

                            dialog.dismiss();
                            mMobile = text.toString();
                            mobileItem.setDetailText(text.toString());

                        } else {
                            Toast.makeText(context, "请填入手机号", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    /**
     * TODO: 待完善UI
     * 保存留言
     */
    private void submit() {
        //
        final Context context = this;
        BDCoreApi.leaveMessage(this, type, workGroupWid, agentUid,
                mMobile, "", mContent,
                new BaseCallback() {
                    @Override
                    public void onSuccess(JSONObject object) {

                        try {

                            int status_code = object.getInt("status_code");
                            String message = object.getString("message");
                            if (status_code == 200) {

                                // 留言成功
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                            } else {

                                // 留言失败
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(JSONObject object) {

                        Toast.makeText(context, "留言失败", Toast.LENGTH_LONG).show();
                    }
                });
    }







}
