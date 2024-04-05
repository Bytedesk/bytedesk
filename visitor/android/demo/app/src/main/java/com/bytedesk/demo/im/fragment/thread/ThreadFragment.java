package com.bytedesk.demo.im.fragment.thread;

import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.event.ConnectionEvent;
import com.bytedesk.core.event.ThreadEvent;
import com.bytedesk.core.room.entity.ThreadEntity;
import com.bytedesk.core.util.BDCoreConstant;
import com.bytedesk.core.viewmodel.ThreadViewModel;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.bytedesk.demo.common.ListViewDecoration;
import com.bytedesk.demo.im.adapter.ThreadAdapter;
import com.bytedesk.ui.api.BDUiApi;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
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
public class ThreadFragment extends BaseFragment implements SwipeItemClickListener {

    @BindView(R.id.bytedesk_thread_topbar) QMUITopBarLayout mTopBar;
    //    @BindView(R.id.emptyView) QMUIEmptyView mEmptyView;
    @BindView(R.id.pull_to_refresh) QMUIPullRefreshLayout mPullRefreshLayout;
    @BindView(R.id.bytedesk_search_edittext) EditText mSearchEditText;
    @BindView(R.id.recycler_view) SwipeMenuRecyclerView mSwipeMenuRecyclerView;

    private ThreadAdapter mThreadAdapter;
    private ThreadViewModel mThreadViewModel;
    private List<ThreadEntity> mThreadEntities;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_im_thread, null);
        ButterKnife.bind(this, root);

        EventBus.getDefault().register(this);

        initTopBar();
        initRecycleView();
        initModel();

        // 加载会话
        getThreads();

        return root;
    }

    /**
     * 初始化topbar
     */
    protected void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle(getResources().getString(R.string.bytedesk_thread));
    }

    /**
     * 初始化recycleview
     */
    private void initRecycleView() {
        // 下拉刷新
        mPullRefreshLayout.setOnPullListener(pullListener);
        // 搜索框
        mSearchEditText.addTextChangedListener(textWatcher);
        //
        mSwipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSwipeMenuRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mSwipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mSwipeMenuRecyclerView.addItemDecoration(new ListViewDecoration(getContext()));// 添加分割线。
        mSwipeMenuRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mSwipeMenuRecyclerView.setSwipeMenuItemClickListener(swipeMenuItemClickListener);
        mSwipeMenuRecyclerView.setSwipeItemClickListener(this);
        //
        mThreadAdapter = new ThreadAdapter(getContext());
        mSwipeMenuRecyclerView.setAdapter(mThreadAdapter);
    }

    /**
     * model初始化
     */
    private void initModel() {
        //
        mThreadViewModel = ViewModelProviders.of(this).get(ThreadViewModel.class);
        //
        mThreadViewModel.getIMThreads().observe(this, threadEntities -> {
            mThreadEntities = threadEntities;
            mThreadAdapter.setThreads(threadEntities);
        });
    }

    /**
     * 搜索初始化
     * @param search
     */
    private void searchModel(String search) {
        //
        mThreadViewModel.searchThreads(search).observe(this, threadEntities -> {
            mThreadEntities = threadEntities;
            mThreadAdapter.setThreads(threadEntities);
        });
    }

    /**
     * 请求thread
     */
    private void getThreads() {
        Logger.i("getThreads");

        BDCoreApi.getThreads(getContext(), new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    JSONArray agentThreadJsonArray = object.getJSONObject("data").getJSONArray("agentThreads");
                    for (int i = 0; i < agentThreadJsonArray.length(); i++) {
                        mThreadViewModel.insertThreadJson(agentThreadJsonArray.getJSONObject(i));

//                        // 添加订阅
//                        String threadTopic = "thread/" + agentThreadJsonArray.getJSONObject(i).getString("tid");
//                        BDMqttApi.subscribeTopic(getContext(), threadTopic);
                    }

                    //
                    JSONArray contactThreadJsonArray = object.getJSONObject("data").getJSONArray("contactThreads");
                    for (int i = 0; i < contactThreadJsonArray.length(); i++) {
                        mThreadViewModel.insertThreadJson(contactThreadJsonArray.getJSONObject(i));
                    }

                    //
                    JSONArray groupThreadJsonArray = object.getJSONObject("data").getJSONArray("groupThreads");
                    for (int i = 0; i < groupThreadJsonArray.length(); i++) {
                        mThreadViewModel.insertThreadJson(groupThreadJsonArray.getJSONObject(i));
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

    /**
     * SwipeItemClickListener
     */
    @Override
    public void onItemClick(View itemView, int position) {
        //
        ThreadEntity threadEntity = mThreadEntities.get(position);
        Logger.d("thread item clicked " + position + " string: " + threadEntity.toString());

        BDUiApi.startThreadChatActivity(getContext(), threadEntity.getTid(),
                threadEntity.getTopic(), threadEntity.getClient(), threadEntity.getType(), threadEntity.getNickname(), threadEntity.getAvatar());

        // 清空本地未读数目
        threadEntity.setUnreadCount(0);
        mThreadViewModel.insertThreadEntity(threadEntity);
    }

    /**
     * 监听 EventBus 连接消息
     *
     * @param connectionEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent connectionEvent) {

        String connectionStatus = connectionEvent.getContent();
        Logger.i("onConnectionEvent: " + connectionStatus);

        if (connectionStatus.equals(BDCoreConstant.USER_STATUS_CONNECTING)) {

            mTopBar.setTitle("萝卜丝(连接中...)");
        } else if (connectionStatus.equals(BDCoreConstant.USER_STATUS_CONNECTED)) {

            mTopBar.setTitle("萝卜丝");
        } else if (connectionStatus.equals(BDCoreConstant.USER_STATUS_DISCONNECTED)) {

            mTopBar.setTitle("萝卜丝(连接断开)");
        }
    }

    /**
     * 监听 EventBus 广播消息
     *
     * @param threadEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onThreadEvent(ThreadEvent threadEvent) {
        Logger.i("onThreadEvent");

    }


    @Override
    protected void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if(data != null){
            Logger.i("onFragmentResult");
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
            getThreads();
        }
    };

    /**
     * 监听搜索框
     */
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String search = charSequence.toString();
            if (search != null && search.trim().length() > 0) {
                searchModel(search);
            } else {
                initModel();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /**
     * 创建右划菜单
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {

            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            int viewType = mThreadAdapter.getItemViewType(position);

//            Logger.i("viewType fragment: " + viewType);
            //
            if (viewType == ThreadAdapter.ViewType.TOP_UNREAD.ordinal()) {

                SwipeMenuItem markUnReadItem = new SwipeMenuItem(getContext())
                        .setBackground(R.drawable.bytedesk_selector_gray)
                        .setText("取消标记未读")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(markUnReadItem);

                SwipeMenuItem markTopItem = new SwipeMenuItem(getContext())
                        .setBackground(R.drawable.bytedesk_selector_yellow)
                        .setText("取消置顶")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(markTopItem);

            } else if (viewType == ThreadAdapter.ViewType.TOP_READ.ordinal()) {

                //
                SwipeMenuItem markUnReadItem = new SwipeMenuItem(getContext())
                        .setBackground(R.drawable.bytedesk_selector_gray)
                        .setText("标记未读")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(markUnReadItem);

                SwipeMenuItem markTopItem = new SwipeMenuItem(getContext())
                        .setBackground(R.drawable.bytedesk_selector_yellow)
                        .setText("取消置顶")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(markTopItem);

            } else if (viewType == ThreadAdapter.ViewType.UNTOP_UNREAD.ordinal()) {

                //
                SwipeMenuItem markUnReadItem = new SwipeMenuItem(getContext())
                        .setBackground(R.drawable.bytedesk_selector_gray)
                        .setText("取消标记未读")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(markUnReadItem);

                //
                SwipeMenuItem markTopItem = new SwipeMenuItem(getContext())
                        .setBackground(R.drawable.bytedesk_selector_yellow)
                        .setText("置顶")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(markTopItem);

            } else if (viewType == ThreadAdapter.ViewType.UNTOP_READ.ordinal()) {

                //
                SwipeMenuItem markUnReadItem = new SwipeMenuItem(getContext())
                        .setBackground(R.drawable.bytedesk_selector_gray)
                        .setText("标记未读")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(markUnReadItem);

                //
                SwipeMenuItem markTopItem = new SwipeMenuItem(getContext())
                        .setBackground(R.drawable.bytedesk_selector_yellow)
                        .setText("置顶")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(markTopItem);
            }

            SwipeMenuItem deleteItem = new SwipeMenuItem(getContext())
                    .setBackground(R.drawable.bytedesk_selector_red)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);
        }
    };

    /**
     * 点击右划菜单监听
     */
    private SwipeMenuItemClickListener swipeMenuItemClickListener = new SwipeMenuItemClickListener() {

        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, final int position) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
            int viewType = mThreadAdapter.getItemViewType(position);

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                Logger.d( "list第" + position + "; 右侧菜单第" + menuPosition);
                ThreadEntity threadEntity = mThreadEntities.get(position);
                Logger.d("thread item swipe " + position + " string: " + threadEntity.toString());

                //
                if (viewType == ThreadAdapter.ViewType.TOP_UNREAD.ordinal()) {

                    if (menuPosition == 0) {
                        // 取消标记未读
                        unmarkUnreadThread(threadEntity.getTid(), position);
                    } else if (menuPosition == 1) {
                        // 取消置顶
                        unmarkTopThread(threadEntity.getTid(), position);
                    }

                } else if (viewType == ThreadAdapter.ViewType.TOP_READ.ordinal()) {

                    if (menuPosition == 0) {
                        // 标记未读
                        markUnreadThread(threadEntity.getTid(), position);

                    } else if (menuPosition == 1) {
                        // 取消置顶
                        unmarkTopThread(threadEntity.getTid(), position);
                    }

                } else if (viewType == ThreadAdapter.ViewType.UNTOP_UNREAD.ordinal()) {

                    if (menuPosition == 0) {
                        // 取消标记未读
                        unmarkUnreadThread(threadEntity.getTid(), position);

                    } else if (menuPosition == 1) {
                        // 置顶
                        markTopThread(threadEntity.getTid(), position);
                    }

                } else if (viewType == ThreadAdapter.ViewType.UNTOP_READ.ordinal()) {

                    if (menuPosition == 0) {
                        // 标记未读
                        markUnreadThread(threadEntity.getTid(), position);

                    } else if (menuPosition == 1) {
                        // 置顶
                        markTopThread(threadEntity.getTid(), position);
                    }
                }

                //
                if (menuPosition == 2) {

                    deleteThread(threadEntity.getTid(), position);
                }

            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Logger.d("list第" + position + "; 左侧菜单第" + menuPosition);
            }
        }
    };


    /**
     * 会话置顶
     *
     * @param tid
     * @param position
     */
    private void markTopThread(String tid, final int position) {
        // 置顶
        BDCoreApi.markTopThread(getContext(), tid, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    int status_code = object.getInt("status_code");
                    if (status_code == 200) {

                        // TODO: 待优化
                        mThreadAdapter.notifyDataSetChanged();
                    } else {

                        String message = object.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {
                Toast.makeText(getContext(), "会话置顶失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 取消会话置顶
     *
     * @param tid
     * @param position
     */
    private void unmarkTopThread(String tid, final int position) {
        //
        BDCoreApi.unmarkTopThread(getContext(), tid, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                try {

                    int status_code = object.getInt("status_code");
                    if (status_code == 200) {

                        // TODO: 待优化
                        mThreadAdapter.notifyDataSetChanged();
                    } else {

                        String message = object.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(JSONObject object) {
                Toast.makeText(getContext(), "取消会话置顶失败", Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * 标记未读
     *
     * @param tid
     * @param position
     */
    private void markUnreadThread(String tid, final int position) {
        //
        BDCoreApi.markUnreadThread(getContext(), tid, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                try {

                    int status_code = object.getInt("status_code");
                    if (status_code == 200) {

                        // TODO: 待优化
                        mThreadAdapter.notifyDataSetChanged();
                    } else {

                        String message = object.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(JSONObject object) {
                Toast.makeText(getContext(), "标记未读失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 取消标记未读
     *
     * @param tid
     * @param position
     */
    private void unmarkUnreadThread(String tid, final int position) {
        //
        BDCoreApi.unmarkUnreadThread(getContext(), tid, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                try {

                    int status_code = object.getInt("status_code");
                    if (status_code == 200) {
                        // 成功

                        // TODO: 待优化
                        mThreadAdapter.notifyDataSetChanged();
                    } else {

                        String message = object.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(JSONObject object) {
                Toast.makeText(getContext(), "取消标记未读失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 删除会话
     *
     * @param tid
     * @param position
     */
    private void deleteThread(String tid, final int position) {

        // 删除
        BDCoreApi.markDeletedThread(getContext(), tid, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                try {

                    int status_code = object.getInt("status_code");
                    if (status_code == 200) {

                        // 删除本地会话记录
                        ThreadEntity threadEntity = mThreadEntities.get(position);
                        //
                        mThreadEntities.remove(threadEntity);
                        mThreadAdapter.notifyDataSetChanged();
                        mThreadViewModel.deleteThreadEntity(threadEntity);

                    } else {

                        String message = object.getString("message");
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(JSONObject object) {
                Toast.makeText(getContext(), "删除会话失败", Toast.LENGTH_LONG).show();
            }
        });
    }


}
