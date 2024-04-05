package com.bytedesk.demo.im.fragment.queue;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.api.BDMqttApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.event.MessageEvent;
import com.bytedesk.core.room.entity.QueueEntity;
import com.bytedesk.core.viewmodel.QueueViewModel;
import com.bytedesk.core.viewmodel.ThreadViewModel;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.bytedesk.demo.common.ListViewDecoration;
import com.bytedesk.demo.im.adapter.QueueAdapter;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
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
 * @author bytedesk.com on 2018/3/26.
 *
 */
public class QueueFragment extends BaseFragment implements SwipeItemClickListener {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.emptyView) QMUIEmptyView mEmptyView;
    @BindView(R.id.pull_to_refresh) QMUIPullRefreshLayout mPullRefreshLayout;
    @BindView(R.id.recycler_view) SwipeMenuRecyclerView mSwipeMenuRecyclerView;

    private QueueAdapter mQueueAdapter;
    private QueueViewModel mQueueViewModel;
    private ThreadViewModel mThreadViewModel;

    private List<QueueEntity> mQueueEntities;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_queue, null);
        ButterKnife.bind(this, root);

        initTopBar();
        initRecycleView();
        initModel();

        getQueues();

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
        mTopBar.setTitle(getResources().getString(R.string.bytedesk_queue));
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
        mQueueAdapter = new QueueAdapter(getContext());
        mSwipeMenuRecyclerView.setAdapter(mQueueAdapter);
    }

    /**
     *
     */
    private void initModel() {
        //
        mQueueViewModel = ViewModelProviders.of(this).get(QueueViewModel.class);
        mQueueViewModel.getQueues().observe(this, queueEntities -> {
            mQueueEntities = queueEntities;
            mQueueAdapter.setQueues(queueEntities);

            if (mQueueEntities.size() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        });
        //
        mThreadViewModel = ViewModelProviders.of(this).get(ThreadViewModel.class);
    }

    /**
     *
     * @param search
     */
    private void searchModel(String search) {

        mQueueViewModel.searchQueues(search).observe(this, queueEntities -> {
            mQueueEntities = queueEntities;
            mQueueAdapter.setQueues(queueEntities);
        });
    }

    /**
     *
     */
    private void getQueues() {
        Logger.d("getQueues");
        //
        BDCoreApi.getQueues(getContext(), new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                JSONArray jsonArray = null;

                try {
                    jsonArray = object.getJSONObject("data").getJSONArray("content");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mQueueViewModel.insertQueueJson(jsonArray.getJSONObject(i));
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPullRefreshLayout.finishRefresh();
            }
        });
    }

    @Override
    public void onItemClick(View itemView, int position) {
        // SwipeItemClickListener
        Logger.d("item clicked");

        final QueueEntity queueEntity = mQueueEntities.get(position);
        //
        BDCoreApi.acceptQueue(getContext(), queueEntity.getQid(), new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                // 本地删除队列
                mQueueViewModel.deleteQueueEntity(queueEntity);

                // 持久化会话
                try {

                    JSONObject threadObject = object.getJSONObject("data").getJSONObject("thread");
                    mThreadViewModel.insertThreadJson(threadObject);

                    // 添加订阅会话
                    String tid = threadObject.getString("tid");
                    BDMqttApi.subscribeTopic(getContext(), tid);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {
                try {

                    String message = object.getString("message") + object.getString("data");
                    Logger.e(message);

                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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
            getQueues();
        }
    };


}
