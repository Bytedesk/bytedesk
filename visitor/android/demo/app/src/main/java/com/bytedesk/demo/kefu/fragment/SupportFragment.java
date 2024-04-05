package com.bytedesk.demo.kefu.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.bytedesk.demo.utils.BDDemoConst;
import com.bytedesk.ui.api.BDUiApi;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * TODO： 待上线
 *
 */
public class SupportFragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView)
    QMUIGroupListView mGroupListView;

    private String mTitle = "帮助中心";

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_support, null);
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

        // 方式一：网页
        QMUICommonListItemView webViewItem = mGroupListView.createItemView("嵌入网页");
        QMUIGroupListView.newSection(getContext())
                .setTitle("方式一")
                .addItemView(webViewItem, view -> {
                    // 替换：URL参数uid
                    // 注意: 登录后台->客服管理->客服账号->管理员唯一uid
//                        String url = "https://www.bytedesk.com/support?uid=" + BDDemoConst.DEFAULT_TEST_ADMIN_UID + "&ph=ph";
//                        BDUiApi.startHtml5Chat(getContext(), url, "帮助中心");
                    BDUiApi.startSupportURLActivity(getContext(), BDDemoConst.DEFAULT_TEST_ADMIN_UID);
                })
                .addTo(mGroupListView);

        // TODO: 方式二：接口
        QMUICommonListItemView apiItem = mGroupListView.createItemView("调用API接口");
        QMUIGroupListView.newSection(getContext())
                .setTitle("方式二")
                .addItemView(apiItem, view -> {
                    //
                    BDUiApi.startSupportApiActivity(getContext(), BDDemoConst.DEFAULT_TEST_ADMIN_UID);
                })
                .addTo(mGroupListView);


    }

}
