package com.bytedesk.demo.common;

import android.view.LayoutInflater;
import android.view.View;

import com.bytedesk.core.util.BDPreferenceManager;
import com.bytedesk.demo.R;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 二维码
 */
public class ScanQRFragment extends BaseFragment {

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
        mTopBar.setTitle("二维码");
    }

    private void initGroupListView() {

        QMUICommonListItemView profileItem = mGroupListView.createItemView("个人二维码");
        profileItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUICommonListItemView groupItem = mGroupListView.createItemView("群组二维码");
        groupItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUICommonListItemView scanItem = mGroupListView.createItemView("扫一扫");
        scanItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUIGroupListView.newSection(getContext())
                .addItemView(profileItem, v -> {

                    QRCodeFragment qrCodeFragment = new QRCodeFragment();
                    startFragment(qrCodeFragment);

                }).addItemView(groupItem, v -> {

                    QRCodeFragment qrCodeFragment = new QRCodeFragment();
                    startFragment(qrCodeFragment);

                }).addItemView(scanItem, v -> {

                    ScanFragment scanFragment = new ScanFragment();
                    startFragment(scanFragment);

                }).addTo(mGroupListView);
    }






}
