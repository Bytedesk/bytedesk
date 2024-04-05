package com.bytedesk.demo.im.fragment.social;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.repository.BDRepository;
import com.bytedesk.core.room.entity.FriendEntity;
import com.bytedesk.core.util.JsonToEntity;
import com.bytedesk.core.viewmodel.FriendViewModel;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.bytedesk.demo.common.ListViewDecoration;
import com.bytedesk.demo.common.TabEvent;
import com.bytedesk.demo.im.adapter.FriendAdapter;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author bytedesk.com on 2018/3/26.
 *
 */
public class FriendFragment extends BaseFragment implements SwipeItemClickListener {

    @BindView(R.id.emptyView) QMUIEmptyView mEmptyView;
    @BindView(R.id.pull_to_refresh) QMUIPullRefreshLayout mPullRefreshLayout;
    @BindView(R.id.recycler_view) SwipeMenuRecyclerView mSwipeMenuRecyclerView;

    private FriendAdapter mFriendAdapter;
    private List<FriendEntity> mFriendEntities;
    private FriendViewModel mFriendViewModel;

    private JsonToEntity jsonToEntity;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_friend, null);
        ButterKnife.bind(this, root);

        mFriendEntities = new ArrayList<>();
        jsonToEntity = JsonToEntity.instance(getContext());

        initRecycleView();
        initModel();

        getFriends();

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
    private void initRecycleView() {
        //
        mPullRefreshLayout.setOnPullListener(pullListener);
        //
        mSwipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSwipeMenuRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mSwipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mSwipeMenuRecyclerView.addItemDecoration(new ListViewDecoration(getContext()));// 添加分割线。
        mSwipeMenuRecyclerView.setSwipeItemClickListener(this); //
        //
        mFriendAdapter = new FriendAdapter(getContext());
        mSwipeMenuRecyclerView.setAdapter(mFriendAdapter);
    }

    /**
     *
     */
    private void initModel() {
        //
        mFriendViewModel = ViewModelProviders.of(this).get(FriendViewModel.class);
        mFriendViewModel.getFriends().observe(this, friendEntities -> {
            mFriendEntities = friendEntities;
            mFriendAdapter.setFriends(friendEntities);
        });
    }

    /**
     *
     */
    private void getFriends() {
        Logger.d("getFriends");

        BDCoreApi.getFriends(getContext(), 0, 20, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                // 清空本地缓存
                BDRepository.getInstance(getContext()).deleteAllFriends();

                //
                try {

                    String message = object.getString("message");
                    int status_code = object.getInt("status_code");
                    if (status_code != 200) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    JSONArray contactArray = object.getJSONObject("data").getJSONArray("content");
                    for (int i = 0; i < contactArray.length(); i++) {
                        mFriendViewModel.insertFriendJson(contactArray.getJSONObject(i));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPullRefreshLayout.finishRefresh();
            }

            @Override
            public void onError(JSONObject object) {

                mPullRefreshLayout.finishRefresh();
            }
        });
    }

    @Override
    public void onItemClick(View itemView, int position) {
        // SwipeItemClickListener
        Logger.d("item clicked");

        final FriendEntity friendEntity = mFriendEntities.get(position);

        final String[] items = new String[]{ "拉黑", "删除好友"};
//        final int checkedIndex = 0;
        new QMUIDialog.CheckableDialogBuilder(getActivity())
//                .setCheckedIndex(checkedIndex)
                .addItems(items, (dialog, index) -> {

                    Toast.makeText(getActivity(), "你选择了 " + items[index], Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    if (index == 0) {
                        // 拉黑
                        addBlock(friendEntity.getUid());

                    } else if (index == 1) {
                        // 删除好友
                        removeFriend(friendEntity.getUid());

                    }
                }).show();
    }

    /**
     * 删除好友
     *
     * @param uid
     */
    private void removeFriend(String uid) {
        //
        BDCoreApi.removeFriend(getContext(), uid, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    String message = object.getString("message");
                    int status_code = object.getInt("status_code");
                    if (status_code != 200) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    // 本地缓存删除
                    BDRepository.getInstance(getContext()).deleteFriend(uid);

                    // 内存删除
                    for (int i = 0; i < mFriendEntities.size(); i++) {
                        FriendEntity friendEntity = mFriendEntities.get(i);
                        if (friendEntity.getUid().equals(uid)) {

                            mFriendEntities.remove(i);
                            mFriendAdapter.notifyDataSetChanged();
                        }
                    }

                    Toast.makeText(getContext(), "删除好友成功", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {

                Toast.makeText(getContext(), "删除好友失败", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 拉黑
     *
     * @param uid
     */
    private void addBlock(String uid) {
        //
        BDCoreApi.addBlock(getContext(), uid, "添加备注", "", "",  new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    String message = object.getString("message");
                    int status_code = object.getInt("status_code");
                    if (status_code != 200) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(getContext(), "拉黑成功", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {

                Toast.makeText(getContext(), "拉黑失败", Toast.LENGTH_LONG).show();
            }
        });
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

            getFriends();
        }
    };


    /**
     * 监听 EventBus 广播消息
     *
     * @param tabEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTabEvent(TabEvent tabEvent) {

        if (tabEvent.getIndex() == 3) {
            getFriends();
        }
    }


}
