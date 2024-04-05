package com.bytedesk.demo.im.fragment.login;

import android.view.LayoutInflater;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class LoginFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView) QMUIGroupListView mGroupListView;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_login, null);
        ButterKnife.bind(this, root);

        initTopBar();
        initGroupListView();

        return root;
    }

    private void initTopBar() {
        mTopBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_color_blue));
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle("登录接口");
    }

    private void initGroupListView() {

        QMUICommonListItemView loginItem = mGroupListView.createItemView("登录接口：");
//        loginItem.setDetailText("online");

        QMUIGroupListView.newSection(getContext())
                .setTitle("登录接口")
//                .setDescription("默认描述")
                .addItemView(loginItem, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .addTo(mGroupListView);

    }

    @Override
    public TransitionConfig onFetchTransitionConfig() {
        return SCALE_TRANSITION_CONFIG;
    }

}


