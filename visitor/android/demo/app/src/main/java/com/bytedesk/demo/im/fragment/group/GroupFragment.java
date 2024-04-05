package com.bytedesk.demo.im.fragment.group;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.api.BDMqttApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.event.ContactEvent;
import com.bytedesk.core.room.entity.GroupEntity;
import com.bytedesk.core.viewmodel.GroupViewModel;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.bytedesk.demo.common.ListViewDecoration;
import com.bytedesk.demo.im.adapter.GroupAdapter;
import com.bytedesk.demo.im.fragment.contact.SelectFragment;
import com.bytedesk.ui.api.BDUiApi;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * @author bytedesk.com on 2018/3/26.
 */
public class GroupFragment extends BaseFragment implements SwipeItemClickListener {

    @BindView(R.id.bytedesk_contact_topbar) QMUITopBarLayout mTopBar;
//    @BindView(R.id.emptyView) QMUIEmptyView mEmptyView;
    @BindView(R.id.pull_to_refresh) QMUIPullRefreshLayout mPullRefreshLayout;
    @BindView(R.id.group_recycler_view) SwipeMenuRecyclerView mGroupSwipeMenuRecyclerView;

    private GroupAdapter mGroupAdapter;
    private GroupViewModel mGroupViewModel;
    private List<GroupEntity> mGroupEntities;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_group, null);
        ButterKnife.bind(this, root);

        EventBus.getDefault().register(this);

        initTopBar();
        initRecycleView();
        initModel();

        // 加载群组
        getGroups();

        return root;
    }

    /**
     *
     */
    protected void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        // 建群
        mTopBar.addRightTextButton("更多", QMUIViewHelper.generateViewId())
                .setOnClickListener(v -> showActionSheet());
        mTopBar.setTitle(getResources().getString(R.string.bytedesk_group));
    }

    /**
     *
     */
    private void initRecycleView() {
        //
        mPullRefreshLayout.setOnPullListener(pullListener);
        //
        mGroupSwipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mGroupSwipeMenuRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mGroupSwipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mGroupSwipeMenuRecyclerView.addItemDecoration(new ListViewDecoration(getContext()));// 添加分割线。
        mGroupSwipeMenuRecyclerView.setSwipeItemClickListener(this); //
        // TODO: 添加搜索、群组等
//        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.contact_header_layout, mContactSwipeMenuRecyclerView, false);
//        mContactSwipeMenuRecyclerView.addHeaderView(headerView);
        //
        mGroupAdapter = new GroupAdapter(getContext());
        mGroupSwipeMenuRecyclerView.setAdapter(mGroupAdapter);
    }

    /**
     *
     */
    private void initModel() {
        //
        mGroupViewModel = ViewModelProviders.of(this).get(GroupViewModel.class);
        mGroupViewModel.getGroups().observe(this, groupEntities -> {
            mGroupEntities = groupEntities;
            mGroupAdapter.setGroups(groupEntities);
        });
    }

    private void showTopRightSheet() {
        new QMUIBottomSheet.BottomListSheetBuilder(getActivity())
                .addItem("建群")
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    dialog.dismiss();
                    //
                    SelectFragment selectFragment = new SelectFragment();
                    startFragment(selectFragment);
                })
                .build()
                .show();
    }

    /**
     * 加载群组
     */
    private void getGroups() {

        BDCoreApi.getGroups(getActivity(), new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {
                    Logger.d("getGroups message: " + object.getString("message"));

                    JSONArray groupsArray = object.getJSONArray("data");
                    for (int i = 0; i < groupsArray.length(); i++) {
                        JSONObject groupObject = groupsArray.getJSONObject(i);
                        mGroupViewModel.insertGroupJson(groupObject);

                        // 添加订阅
                        String gId = groupObject.getString("gid");
                        String groupTopic = "group/" + gId;
                        BDMqttApi.subscribeTopic(getActivity(), groupTopic);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPullRefreshLayout.finishRefresh();
            }

            @Override
            public void onError(JSONObject object) {
                try {
                    Logger.e(object.getString("message") + object.getString("data"));

                    Toast.makeText(getContext(), object.getString("message"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPullRefreshLayout.finishRefresh();
            }
        });
    }

    private void showActionSheet() {
        new QMUIBottomSheet.BottomListSheetBuilder(getActivity())
                .addItem("建群")
                .addItem("加群")
                .setOnSheetItemClickListener((dialog, itemView, position, tag) -> {
                    dialog.dismiss();

                    if (position == 0) {

                        SelectFragment selectFragment = new SelectFragment();
                        startFragment(selectFragment);
                    } else {

                        BDCoreApi.joinGroup(getContext(), "201904231608313", new BaseCallback() {

                            @Override
                            public void onSuccess(JSONObject object) {

                                try {

                                    int status_code = object.getInt("status_code");
                                    if (status_code == 200) {

                                        Logger.d("加群成功");

                                    } else {
                                        // 发送消息失败
                                        String message = object.getString("message");
                                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(JSONObject object) {
                                Logger.e("加群失败");
                            }

                        });
                    }
                })
                .build()
                .show();
    }

    @Override
    public void onItemClick(View itemView, int position) {
        // SwipeItemClickListener
        Logger.d("group item clicked");

        GroupEntity groupEntity = mGroupEntities.get(position);
        BDUiApi.startGroupChatActivity(getContext(), groupEntity.getGid(), groupEntity.getNickname());
    }

    /**
     * 监听 EventBus 广播消息
     *
     * @param contactEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onContactEvent(ContactEvent contactEvent) {
        Logger.i("onContactEvent: " + contactEvent.getContent());

        try {
            JSONObject jsonObject = new JSONObject(contactEvent.getContent());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下拉刷新
     */
    private QMUIPullRefreshLayout.OnPullListener pullListener = new QMUIPullRefreshLayout.OnPullListener() {

        @Override
        public void onMoveTarget(int offset) {

        }

        @Override
        public void onMoveRefreshView(int offset) {

        }

        @Override
        public void onRefresh() {
            Logger.i("refresh");
            //
            getGroups();
        }
    };
}
