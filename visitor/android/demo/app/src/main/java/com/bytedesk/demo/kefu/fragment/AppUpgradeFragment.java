package com.bytedesk.demo.kefu.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * TODO： 待上线
 *
 */
public class AppUpgradeFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView)
    QMUIGroupListView mGroupListView;

    private String mDefaultWorkgroupId = "14";
    private String mTitle = "引导升级";

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_feedback, null);
        ButterKnife.bind(this, root);

        initTopBar();
        initGroupListView();

        return root;
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle(mTitle);
    }

    private void initGroupListView() {

//        QMUICommonListItemView chatItem = mGroupListView.createItemView("开始会话：");
//        QMUIGroupListView.newSection(getContext())
//                .setTitle("默认会话接口")
//                .setDescription("在web管理后台开启/关闭机器人")
//                .addItemView(chatItem, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        //
//                        WXUIApi.startWorkGroupChatActivity(getContext(), mDefaultWorkgroupId, mTitle);
//
//                    }
//                })
//                .addTo(mGroupListView);


    }

}
