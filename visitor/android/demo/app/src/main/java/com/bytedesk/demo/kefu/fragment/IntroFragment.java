package com.bytedesk.demo.kefu.fragment;


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
        mTopBar.setTitle("客服接口说明");
    }

    private void initGroupListView() {

        QMUICommonListItemView introItem = mGroupListView.createItemView("1. 匿名访客咨询客服");

        QMUIGroupListView.newSection(getContext())
                .setTitle("应用场景：")
                .setDescription("医疗、金融、电商、教育等行业")
                .addItemView(introItem, null)
                .addTo(mGroupListView);

    }

}
