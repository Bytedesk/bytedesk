package com.bytedesk.demo.common;

import android.view.LayoutInflater;
import android.view.View;

import com.bytedesk.demo.R;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.bytedesk.core.api.BDConfig;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 配置
 */
public class ServerFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView) QMUIGroupListView mGroupListView;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_server, null);
        ButterKnife.bind(this, root);

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
        mTopBar.setTitle(getResources().getString(R.string.bytedesk_self_server));
    }

    private void initGroupListView() {

        QMUICommonListItemView authAddressItem = mGroupListView.createItemView("地址");
        authAddressItem.setDetailText(BDConfig.getInstance(getContext()).getRestApiHost());

        QMUIGroupListView.newSection(getContext())
                .setTitle("REST服务器,注意：以'/'结尾")
                .addItemView(authAddressItem, view -> {

                    // 修改为自己的服务器地址，注意：地址以 http或https开头, '/'结尾
//                    BDConfig.getInstance(getContext()).setRestApiHost("https://zovwus.iczhl.com/");

                }).addTo(mGroupListView);

        QMUICommonListItemView websocketAddressItem = mGroupListView.createItemView("地址");
        websocketAddressItem.setDetailText(BDConfig.getInstance(getContext()).getMqttWebSocketWssURL());

        QMUIGroupListView.newSection(getContext())
                .setTitle("websocket地址")
                .addItemView(websocketAddressItem, view -> {

                    // 修改为自己的服务器地址
//                    BDConfig.getInstance(getContext()).setMqttWebSocketWssURL("wss://zovwus.iczhl.com/websocket");

                }).addTo(mGroupListView);

        //
        QMUICommonListItemView restoreDefault = mGroupListView.createItemView("恢复默认值");
        QMUIGroupListView.newSection(getContext()).addItemView(restoreDefault, view -> {
            // 恢复默认值
            BDConfig.getInstance(getContext()).restoreDefault();
            //
            authAddressItem.setDetailText(BDConfig.getInstance(getContext()).getRestApiHost());
            websocketAddressItem.setDetailText(BDConfig.getInstance(getContext()).getMqttWebSocketWssURL());

        }).addTo(mGroupListView);

    }


}
