package com.bytedesk.demo.im.fragment.login;


import android.view.LayoutInflater;
import android.view.View;

import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class IntroFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView) QMUIGroupListView mGroupListView;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_intro, null);
        ButterKnife.bind(this, root);

        initTopBar();
        initGroupListView();

        return root;
    }

    private void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle("IM接口说明");
    }

    private void initGroupListView() {

        QMUICommonListItemView threadItem = mGroupListView.createItemView("1. 接待访客");
        QMUICommonListItemView contactItem = mGroupListView.createItemView("2. 一对一聊天");
        QMUICommonListItemView groupItem = mGroupListView.createItemView("3. 群组聊天");

        QMUIGroupListView.newSection(getContext())
                .setTitle("应用场景：")
                .setDescription("企业内部IM、社交IM")
                .addItemView(threadItem, null)
                .addItemView(contactItem, null)
                .addItemView(groupItem, null)
                .addTo(mGroupListView);

    }


}
