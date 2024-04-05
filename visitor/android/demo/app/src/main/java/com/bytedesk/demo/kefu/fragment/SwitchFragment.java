package com.bytedesk.demo.kefu.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.callback.LoginCallback;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.bytedesk.demo.utils.BDDemoConst;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SwitchFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView) QMUIGroupListView mGroupListView;

    private QMUICommonListItemView infoItem;
    private QMUICommonListItemView userGirlItem;
    private QMUICommonListItemView userBoyItem;
    private QMUICommonListItemView logoutItem;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_switch, null);
        ButterKnife.bind(this, root);

        initTopBar();
        initGroupListView();

        return root;
    }

    private void initTopBar() {
        mTopBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_color_blue));
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle("切换用户");
    }

    private void initGroupListView() {

        infoItem = mGroupListView.createItemView("用户信息");
        userBoyItem = mGroupListView.createItemView("用户1男");
        userGirlItem = mGroupListView.createItemView("用户2女");
        logoutItem = mGroupListView.createItemView("退出登录");

        QMUIGroupListView.newSection(getContext())
                .setTitle("切换用户接口")
                .addItemView(infoItem, view -> startFragment(new ProfileFragment()))
                .addItemView(userBoyItem, v -> userBoyLogin())
                .addItemView(userGirlItem, view -> userGirlLogin())
                .addItemView(logoutItem, view -> logout())
                .addTo(mGroupListView);
    }

    private void userBoyLogin() {
        if (BDCoreApi.isLogin(getContext())) {
            Toast.makeText(getContext(), R.string.bytedesk_logout_first, Toast.LENGTH_LONG).show();
            return;
        }

        initWithUsernameAndNicknameAndAvatar("myandroiduserboy", "我是帅哥", "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/boy.png", BDDemoConst.DEFAULT_TEST_APPKEY, BDDemoConst.DEFAULT_TEST_SUBDOMAIN);
    }

    private void userGirlLogin() {
        if (BDCoreApi.isLogin(getContext())) {
            Toast.makeText(getContext(), R.string.bytedesk_logout_first, Toast.LENGTH_LONG).show();
            return;
        }

        initWithUsernameAndNicknameAndAvatar("myandroidusergirl", "我是美女", "https://bytedesk.oss-cn-shenzhen.aliyuncs.com/avatars/girl.png", BDDemoConst.DEFAULT_TEST_APPKEY, BDDemoConst.DEFAULT_TEST_SUBDOMAIN);
    }

    // 使用自定义用户名+昵称+头像登录
    private void initWithUsernameAndNicknameAndAvatar(String username, String nickname, String avatar, String appKey, String subDomain) {
        //
        BDCoreApi.initWithUsernameAndNicknameAndAvatar(getContext(), username, nickname, avatar, appKey, subDomain, new LoginCallback() {
            @Override
            public void onSuccess(JSONObject object) {
                try {
                    Logger.d("login success message: " + object.get("message")
                            + " status_code:" + object.get("status_code"));
                    Toast.makeText(getContext(), R.string.bytedesk_login_success, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(JSONObject object) {
                try {
                    Logger.e("login failed message: " + object.get("message")
                            + " status_code:" + object.get("status_code")
                            + " data:" + object.get("data"));
                    Toast.makeText(getContext(), R.string.bytedesk_login_failed, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void logout() {

        BDCoreApi.logout(getContext(), new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                Toast.makeText(getContext(), R.string.bytedesk_logout_success, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(JSONObject object) {

                Toast.makeText(getContext(), R.string.bytedesk_logout_failed, Toast.LENGTH_LONG).show();
            }
        });
    }




}
