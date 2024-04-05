package com.bytedesk.demo.kefu.fragment;

import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.room.entity.ThreadEntity;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.bytedesk.demo.common.ListViewDecoration;
import com.bytedesk.demo.kefu.adapter.ThreadAdapter;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.arch.QMUIFragment;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ThreadFragment extends BaseFragment {

    private int page;
    private int size;

    @BindView(R.id.session_topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.session_refreshlayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.session_recyclerview) RecyclerView mRecyclerView;

    private ThreadAdapter mThreadAdapter;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_thread, null);
        ButterKnife.bind(this, root);

        page = 0;
        size = 20;

        initTopBar();
        initRecyclerView();

        return root;
    }

    private void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(v ->  popBackStack());
        mTopBar.setTitle("会话历史记录接口");
    }

    private void initRecyclerView() {
        //
        mSwipeRefreshLayout.setOnRefreshListener(() -> getThreads());
        //
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mRecyclerView.addItemDecoration(new ListViewDecoration(getContext()));// 添加分割线。
        //
        mThreadAdapter = new ThreadAdapter(getContext());
        mRecyclerView.setAdapter(mThreadAdapter);
        //
        getThreads();
    }

    @Override
    public QMUIFragment.TransitionConfig onFetchTransitionConfig() {
        return SCALE_TRANSITION_CONFIG;
    }


    private void getThreads() {
        //
        BDCoreApi.visitorGetThreads(getContext(), page, size, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                List<ThreadEntity> threadEntityList = new LinkedList<>();
                try {

                    JSONArray jsonArray = object.getJSONObject("data").getJSONArray("content");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        //
                        ThreadEntity threadEntity = new ThreadEntity();
//                        threadEntity.setId(jsonObject.getLong("id"));
                        threadEntity.setTid(jsonObject.getString("tid"));
                        threadEntity.setUnreadCount(jsonObject.getInt("unreadCount"));
                        threadEntity.setToken(jsonObject.getString("token"));
                        threadEntity.setContent(jsonObject.getString("content"));
                        threadEntity.setNickname(jsonObject.getJSONObject("visitor").getString("nickname"));
                        threadEntity.setTimestamp(jsonObject.getString("timestamp"));
                        threadEntity.setAvatar(jsonObject.getJSONObject("visitor").getString("avatar"));
                        threadEntityList.add(threadEntity);
                    }
                    mThreadAdapter.setThreadList(threadEntityList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                page++;
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(JSONObject object) {
                Logger.e("获取会话记录错误");

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
