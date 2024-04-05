package com.bytedesk.demo.im.fragment.contact;

import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bytedesk.core.event.MessageEvent;
import com.bytedesk.demo.R;
import com.bytedesk.ui.api.BDUiApi;
import com.bytedesk.ui.base.BaseFragment;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView) QMUIGroupListView mGroupListView;

    private QMUIRadiusImageView avatarImageView;
    private QMUICommonListItemView profileItem;

    private String uid;
    private String nickname;
    private String realName;
    private String avatar;
    private String description;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_detail, null);
        ButterKnife.bind(this, root);

        //
        uid = getArguments().getString("uid");
        nickname = getArguments().getString("nickname");
        realName = getArguments().getString("realName");
        avatar = getArguments().getString("avatar");
        description = getArguments().getString("description");

        //
        initTopBar();
        initGroupListView();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     *
     */
    protected void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle("个人详情");
    }

    private void initGroupListView() {
        //
        avatarImageView = new QMUIRadiusImageView(getContext());
        Glide.with(getContext()).load(avatar).into(avatarImageView);

        ////
        profileItem = mGroupListView.createItemView(realName);
        profileItem.setOrientation(QMUICommonListItemView.VERTICAL);

//        profileItem.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.icon_tabbar_contact));
        profileItem.setImageDrawable(avatarImageView.getDrawable());
        profileItem.setDetailText(description);

        QMUIGroupListView.newSection(getContext()).addItemView(profileItem, view -> Logger.d("profile item clicked")).addTo(mGroupListView);

        QMUICommonListItemView chatItem = mGroupListView.createItemView("开始对话");
        QMUIGroupListView.newSection(getContext()).addItemView(chatItem, view -> {

        // TODO：区分一对一，群组会话
        BDUiApi.startContactChatActivity(getContext(), uid, realName);

        }).addTo(mGroupListView);
    }


    /**
     * 监听 EventBus 广播消息
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        Logger.i("onMessageEvent");


    }


}

