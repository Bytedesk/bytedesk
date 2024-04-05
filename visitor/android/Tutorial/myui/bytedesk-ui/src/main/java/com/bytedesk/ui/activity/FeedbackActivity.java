package com.bytedesk.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.util.BDCoreUtils;
import com.bytedesk.ui.R;
import com.bytedesk.ui.util.BDUiConstant;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.yanzhenjie.album.Action;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 意见反馈
 *
 * @author bytedesk.com
 */
public class FeedbackActivity extends AppCompatActivity {

    private QMUITopBarLayout mTopBar;
    private QMUIGroupListView mGroupListView;

    private QMUICommonListItemView categoryItem;
    private QMUICommonListItemView contentItem;
    private QMUICommonListItemView imageItem;
    private QMUICommonListItemView mobileItem;
    private QMUICommonListItemView emailItem;

    private String mTitle = "意见反馈";
    private String mUid;

    //
    private Map<String, String> mCategoryMap = new HashMap<>();
    //
    private String mCategoryCid;
    //
    private String mContent;
    private String mMobile;
    private String mEmail;
    private String mFileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_feedback);
        //
        mTopBar = findViewById(R.id.bytedesk_feedback_topbar);
        mGroupListView = findViewById(R.id.bytedesk_feedback_groupListView);
        //
        mUid = getIntent().getStringExtra(BDUiConstant.EXTRA_UID);
        QMUIStatusBarHelper.translucent(this);
        //
        initTopBar();
        initGroupListView();
        //
        getFeedbackCategories();
    }

    private void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> finish());
        //
        mTopBar.addRightTextButton("我的反馈", QMUIViewHelper.generateViewId())
                .setOnClickListener(v -> {
                    Intent intent = new Intent(this, FeedbackRecordActivity.class);
                    intent.putExtra(BDUiConstant.EXTRA_UID, mUid);
                    startActivity(intent);
                });
        mTopBar.setTitle(mTitle);
    }

    private void initGroupListView() {
        //
        categoryItem = mGroupListView.createItemView("分类");
        categoryItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUIGroupListView.newSection(this)
                .addItemView(categoryItem, v -> {

                    showChooseCategoryDialog();

                }).addTo(mGroupListView);

        // 内容
        contentItem = mGroupListView.createItemView("内容");
        QMUIGroupListView.newSection(this)
                .addItemView(contentItem, v -> {

                    showEditContentDialog();

                }).addTo(mGroupListView);

        // 图片
        imageItem = mGroupListView.createItemView(
                ContextCompat.getDrawable(this, R.drawable.bytedesk_album_add_image),
                "",
                "",
                QMUICommonListItemView.HORIZONTAL,
                QMUICommonListItemView.ACCESSORY_TYPE_NONE);
        QMUIGroupListView.newSection(this)
                .setTitle("图片")
                .addItemView(imageItem, v -> {

                    chooseImage();

                }).addTo(mGroupListView);

        // 联系方式
        mobileItem = mGroupListView.createItemView("手机号");
        emailItem = mGroupListView.createItemView("邮箱");
        QMUIGroupListView.newSection(this)
                .addItemView(mobileItem, v -> {

                    showEditMobileDialog();

                }).addItemView(emailItem, v -> {

            showEditEmailDialog();

        }).addTo(mGroupListView);

        // 提交
        QMUICommonListItemView submitItem = mGroupListView.createItemView("提交");
        submitItem.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        QMUIGroupListView.newSection(this)
                .addItemView(submitItem, v -> {
                    Logger.i("提交");

                    createFeedback();

                }).addTo(mGroupListView);

    }

    /**
     * 加载意见反馈分类
     */
    private void getFeedbackCategories() {

        final Context context = this;
        BDCoreApi.getFeedbackCategories(this, mUid, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    int status_code = object.getInt("status_code");
                    if (status_code == 200) {

                        JSONArray categoryArray = object.getJSONArray("data");
                        for (int i = 0; i < categoryArray.length(); i++) {
                            //
                            JSONObject categoryObject = categoryArray.getJSONObject(i);
                            String cid = categoryObject.getString("cid");
                            String name = categoryObject.getString("name");
                            Logger.i("cid %s, name %s", cid, name);
                            //
                            mCategoryMap.put(name, cid);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {
                Toast.makeText(context, "加载意见反馈分类失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 选择意见反馈分类
     */
    private void showChooseCategoryDialog() {

//        final String[] items = new String[]{"选项1", "选项2", "选项3"};
        final String[] items = mCategoryMap.keySet().toArray(new String[mCategoryMap.size()]);
//        final int checkedIndex = 0;
        new QMUIDialog.CheckableDialogBuilder(this)
//                .setCheckedIndex(checkedIndex)
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        String name = items[which];
                        mCategoryCid = mCategoryMap.get(name);
                        Logger.i("name %s, cid %s", name, mCategoryCid);

                        categoryItem.setDetailText(name);
                    }
                }).show();
    }

    /**
     * 输入反馈内容
     */
    private void showEditContentDialog() {

        final Context context = this;
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("工单内容")
                .setPlaceholder("在此输入内容")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        final CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {

                            dialog.dismiss();
                            mContent = text.toString();
                            contentItem.setDetailText(text.toString());


                        } else {
                            Toast.makeText(context, "请填入内容", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();

    }

    /**
     * 选择图片
     */
    private void chooseImage() {
        final Context context = this;
        // 目前仅允许一次选一张图片
        Album.image(this)
                .singleChoice()
                .camera(false)
                .onResult(new Action<ArrayList<AlbumFile>>() {
                    @Override
                    public void onAction(@NonNull ArrayList<AlbumFile> result) {

                        if (result.size() > 0) {
                            AlbumFile albumFile = result.get(0);

                            String imageName = BDCoreUtils.getPictureTimestamp();
                            uploadImage(albumFile.getPath(), imageName);
                        }
                    }
                })
                .onCancel(new Action<String>() {
                    @Override
                    public void onAction(@NonNull String result) {
                        Toast.makeText(context, "取消选择图片", Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }

    /**
     * 输入手机号
     */
    private void showEditMobileDialog() {

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
     * 输入邮箱
     */
    private void showEditEmailDialog() {

        final Context context = this;
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
        builder.setTitle("邮箱")
                .setPlaceholder("在此输入邮箱")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        final CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 0) {

                            dialog.dismiss();
                            mEmail = text.toString();
                            emailItem.setDetailText(text.toString());

                        } else {
                            Toast.makeText(context, "请填入邮箱", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();

    }

    /**
     * 提交反馈
     */
    private void createFeedback() {
        //
        final Context context = this;
        BDCoreApi.createFeedback(this, mUid,
                mCategoryCid, mContent, mMobile, mEmail, mFileUrl,
                new BaseCallback() {

                    @Override
                    public void onSuccess(JSONObject object) {

                        //
                        try {

                            int status_code = object.getInt("status_code");
                            if (status_code == 200) {

                                Toast.makeText(context, "提交意见反馈成功", Toast.LENGTH_LONG).show();

                            } else {

                                String message = object.getString("message");
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(JSONObject object) {
                        Toast.makeText(context, "提交意见反馈失败", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // 请求拍照 和 相册权限
    // 动态请求权限详细用法：https://github.com/googlesamples/easypermissions
    private final int RC_CAMERA_AND_ALBUM = 100;

    @AfterPermissionGranted(RC_CAMERA_AND_ALBUM)
    private void requirePermissions() {
        String[] perms = {Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing


        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "请求权限",
                    RC_CAMERA_AND_ALBUM, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 上传图片
     * @param filePath
     */
    private void uploadImage(String filePath, String fileName) {

        Logger.i("uploadImage %s", filePath);

        BDCoreApi.uploadImage(this, filePath, fileName, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    String imageUrl = object.getString("data");
                    Logger.i("imageUrl %s", imageUrl);

                    // 设置
                    mFileUrl = imageUrl;
                    new DownloadImageTask().execute(imageUrl) ;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(JSONObject object) {

            }
        });
    }


    private Drawable loadImageFromNetwork(String imageUrl) {

        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(new URL(imageUrl).openStream(), "bytedesk_album_add_image.png");
        } catch (IOException e) {
            Logger.d("test", e.getMessage());
        }

        if (drawable == null) {
            Logger.e("error", "null drawable");
        }

        return drawable ;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Drawable> {

        protected Drawable doInBackground(String... urls) {
            return loadImageFromNetwork(urls[0]);
        }

        protected void onPostExecute(Drawable result) {
            imageItem.setImageDrawable(result);
        }
    }

}
