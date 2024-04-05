package com.bytedesk.demo.common;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import com.bytedesk.core.util.BDPreferenceManager;
import com.bytedesk.demo.R;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 配置
 */
public class SettingFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView)
    QMUIGroupListView mGroupListView;

    private BDPreferenceManager preferenceManager;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_server, null);
        ButterKnife.bind(this, root);

        preferenceManager = BDPreferenceManager.getInstance(getContext());

        initTopBar();
        initGroupListView();

        return root;
    }

    private void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(view -> popBackStack());
        //
        mTopBar.addRightImageButton(R.mipmap.icon_topbar_about, R.id.topbar_right_about_button).setOnClickListener(view -> {
            AboutFragment fragment = new AboutFragment();
            startFragment(fragment);
        });
        //
        mTopBar.setTitle(getResources().getString(R.string.bytedesk_setting));
    }

    private void initGroupListView() {

        ///////
        QMUICommonListItemView sendVoiceItem = mGroupListView.createItemView("发送消息播放提示音");
        sendVoiceItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        sendVoiceItem.getSwitch().setChecked(preferenceManager.shouldRingWhenSendMessage());
        sendVoiceItem.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferenceManager.setRingWhenSendMessage(isChecked);
            }
        });
        ///////
        QMUICommonListItemView receiveVoiceItem = mGroupListView.createItemView("收到消息播放提示音");
        receiveVoiceItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        receiveVoiceItem.getSwitch().setChecked(preferenceManager.shouldRingWhenReceiveMessage());
        receiveVoiceItem.getSwitch().setOnCheckedChangeListener((buttonView, isChecked) -> preferenceManager.setRingWhenReceiveMessage(isChecked));
        ///////
        QMUICommonListItemView vibrateItem = mGroupListView.createItemView("收到消息振动");
        vibrateItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
         vibrateItem.getSwitch().setChecked(preferenceManager.shouldVibrateWhenReceiveMessage());
        vibrateItem.getSwitch().setOnCheckedChangeListener((buttonView, isChecked) -> preferenceManager.setVibrateWhenReceiveMessage(isChecked));
        //
        QMUIGroupListView.newSection(getContext())
                .addItemView(sendVoiceItem, view -> {
                    //
                }).addItemView(receiveVoiceItem, view -> {
                    //
                }).addItemView(vibrateItem, view -> {
                    //
                }).addTo(mGroupListView);

    }







}
