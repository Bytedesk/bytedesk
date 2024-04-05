package com.bytedesk.demo.kefu.fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.bytedesk.ui.util.BDUiUtils;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView) QMUIGroupListView mGroupListView;

    private QMUICommonListItemView uidItem;
    private QMUICommonListItemView nicknameItem;
    private QMUICommonListItemView descriptionItem;
    private QMUICommonListItemView avatarItem;

    private QMUICommonListItemView selfDefineItem;

    private String mUid = "";
    private String mNickname = "";
    private String mDescription = "";
    private String mAvatar = "";
    //
    private String mTagName;
    private String mTagKey;
    private String mTagValue = "";

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile, null);
        ButterKnife.bind(this, root);

        mTagName = "客服后台显示名";
        mTagKey = "myKey"; // 开发者可以自行改变key，并设置相应的value，此处只为演示目的

        initTopBar();
        initGroupListView();
        getFingerPrint();
        getProfile();

        return root;
    }

    private void initTopBar() {
        mTopBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_color_blue));
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle("设置用户信息接口");
    }

    private void initGroupListView() {

        uidItem = mGroupListView.createItemView("唯一uid");
        uidItem.setDetailText(mUid);

        nicknameItem = mGroupListView.createItemView("昵称");
        nicknameItem.setDetailText(mNickname);

        descriptionItem = mGroupListView.createItemView("描述");
        descriptionItem.setDetailText(mDescription);

        avatarItem = mGroupListView.createItemView("头像");
        avatarItem.setDetailText(mAvatar);

        QMUIGroupListView.newSection(getContext())
                .setTitle("默认用户信息接口")
                .addItemView(uidItem, view -> {})
                .addItemView(nicknameItem, v -> showEditNicknameDialog())
                .addItemView(descriptionItem, view -> setDescription())
                .addItemView(avatarItem, view -> setAvatar())
                .addTo(mGroupListView);

        selfDefineItem = mGroupListView.createItemView("自定义标签");
        selfDefineItem.setDetailText(mTagValue);

        QMUIGroupListView.newSection(getContext())
                .setTitle("自定义用户信息接口")
                .addItemView(selfDefineItem, v -> showEditTagDialog())
                .addTo(mGroupListView);
    }


    private void showEditNicknameDialog() {
        //
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("自定义昵称")
                .setPlaceholder("在此输入您的昵称")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    final CharSequence text = builder.getEditText().getText();
                    if (text != null && text.length() > 0) {

                        dialog.dismiss();
                        mNickname = text.toString();
                        setNickname();

                    } else {
                        Toast.makeText(getActivity(), "请填入昵称", Toast.LENGTH_SHORT).show();
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
    }

    private void showEditTagDialog() {

        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("自定义标签")
                .setPlaceholder("在此输入自定义标签")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    final CharSequence text = builder.getEditText().getText();
                    if (text != null && text.length() > 0) {

                        dialog.dismiss();
                        // 调用接口设置自定义标签
                        mTagValue = text.toString();
                        setTag();

                    } else {
                        Toast.makeText(getActivity(), "请填入自定义标签值", Toast.LENGTH_SHORT).show();
                    }
                })
                .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();

    }

    // 从服务器拉取用户信息
    private void getProfile () {
        //
        BDCoreApi.userProfile(getContext(), new BaseCallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                //
                try {
                    JSONObject infoObject = jsonObject.getJSONObject("data");
//                    Logger.i("");
                    uidItem.setDetailText(infoObject.getString("uid"));
                    nicknameItem.setDetailText(infoObject.getString("nickname"));
                    descriptionItem.setDetailText(infoObject.getString("description"));
                    avatarItem.setDetailText(infoObject.getString("avatar"));
//                    bdPreferenceManager.setUid(infoObject.getString("uid"));
//                    bdPreferenceManager.setUsername(infoObject.getString("username"));
                }  catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {

            }
        });
    }

    private void getFingerPrint() {
        ///////
        BDCoreApi.getFingerPrint(getContext(), new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {
                try {
                    Logger.d("get userinfo success message: " + object.get("message")
                            + " status_code:" + object.get("status_code")
                            + " data:" + object.get("data"));
                    //
                    String nickname = object.getJSONObject("data").getString("nickname");
                    nicknameItem.setDetailText(nickname);

                    // 解析自定义key/value
                    JSONArray jsonArray = object.getJSONObject("data").getJSONArray("fingerPrints");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String key = jsonObject.getString("key");
                        String value = jsonObject.getString("value");
                        if (key.equals(mTagKey)) {
                            selfDefineItem.setDetailText(value);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {
                try {
                    Logger.e("获取用户信息错误: " + object.get("message")
                            + " status_code:" + object.get("status_code")
                            + " data:" + object.get("data"));

                    BDUiUtils.showTipDialog(getContext(), "获取个人资料失败");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * 自定义昵称
     */
    private void setNickname() {
        // 调用接口设置昵称
        BDCoreApi.setNickname(getContext(), mNickname, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {
                //更新界面
                nicknameItem.setDetailText(mNickname);
            }

            @Override
            public void onError(JSONObject object) {
                BDUiUtils.showTipDialog(getContext(), "设置昵称失败");
            }
        });
    }

    /**
     * 自定义描述
     */
    private void setDescription() {
        mDescription = "自定义APP用户备注信息android";
        // 调用接口设置描述
        BDCoreApi.setDescription(getContext(), mDescription, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {
                // 更新界面
                descriptionItem.setDetailText(mDescription);
            }

            @Override
            public void onError(JSONObject object) {
                BDUiUtils.showTipDialog(getContext(), "设置描述失败");
            }
        });
    }

    /**
     * 自定义头像，请填写头像URL
     */
    private void setAvatar() {
        mAvatar = "https://chainsnow.oss-cn-shenzhen.aliyuncs.com/avatars/visitor_default_avatar.png";
        // 调用接口设置头像
        BDCoreApi.setAvatar(getContext(), mAvatar, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {
                //更新界面
                avatarItem.setDetailText(mAvatar);
            }

            @Override
            public void onError(JSONObject object) {
                BDUiUtils.showTipDialog(getContext(), "设置头像失败");
            }
        });
    }

    /**
     * 自定义标签
     */
    private void setTag() {

        BDCoreApi.setFingerPrint(getContext(), "自定义标签", mTagKey, mTagValue, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {
                selfDefineItem.setDetailText(mTagValue);
            }

            @Override
            public void onError(JSONObject object) {
                BDUiUtils.showTipDialog(getContext(), "设置自定义标签失败");

            }
        });
    }



}
