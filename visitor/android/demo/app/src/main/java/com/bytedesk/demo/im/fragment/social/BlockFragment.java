package com.bytedesk.demo.im.fragment.social;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.room.entity.ContactEntity;
import com.bytedesk.core.util.JsonToEntity;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.bytedesk.demo.common.ListViewDecoration;
import com.bytedesk.demo.common.TabEvent;
import com.bytedesk.demo.im.adapter.ContactAdapter;
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
public class BlockFragment extends BaseFragment implements SwipeItemClickListener {

    @BindView(R.id.emptyView) QMUIEmptyView mEmptyView;
    @BindView(R.id.pull_to_refresh) QMUIPullRefreshLayout mPullRefreshLayout;
    @BindView(R.id.recycler_view) SwipeMenuRecyclerView mSwipeMenuRecyclerView;

    private ContactAdapter mContactAdapter;
    private List<ContactEntity> mContactEntities;

    private JsonToEntity jsonToEntity;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_block, null);
        ButterKnife.bind(this, root);

        mContactEntities = new ArrayList<>();
        jsonToEntity = JsonToEntity.instance(getContext());
        initRecycleView();
        initModel();

        getBlocks();

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
        mContactAdapter = new ContactAdapter(getContext());
        mSwipeMenuRecyclerView.setAdapter(mContactAdapter);
    }

    /**
     *
     */
    private void initModel() {
        //
    }

    /**
     *
     */
    private void getBlocks() {
        Logger.d("getBlocks");

        BDCoreApi.getBlocks(getContext(), 0, 20, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                mContactEntities.clear();

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
                        ContactEntity contactEntity = jsonToEntity.contactEntity(contactArray.getJSONObject(i).getJSONObject("blockedUser"));
                        mContactEntities.add(contactEntity);
                    }
                    mContactAdapter.setContacts(mContactEntities);

                    if (mContactEntities.size() > 0) {
                        mEmptyView.hide();
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

        final ContactEntity contactEntity = mContactEntities.get(position);

        final String[] items = new String[]{"添加关注", "取消关注", "拉黑", "取消拉黑"};
//        final int checkedIndex = 0;
        new QMUIDialog.CheckableDialogBuilder(getActivity())
//                .setCheckedIndex(checkedIndex)
                .addItems(items, (dialog, index) -> {

                    Toast.makeText(getActivity(), "你选择了 " + items[index], Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    if (index == 0) {
                        // 添加关注
                        addFollow(contactEntity.getUid());

                    } else if (index == 1) {
                        // 取消关注
                        unFollow(contactEntity.getUid());

                    } else if (index == 2) {
                        // 拉黑
                        addBlock(contactEntity.getUid());

                    } else if (index == 3) {
                        // 取消拉黑
                        unBlock(contactEntity.getUid());
                    }
                }).show();
    }

    /**
     * 添加关注
     *
     * @param uid
     */
    private void addFollow(String uid) {
        //
        BDCoreApi.addFollow(getContext(), uid, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    String message = object.getString("message");
                    int status_code = object.getInt("status_code");
                    if (status_code != 200) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(getContext(), "添加关注成功", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {

                Toast.makeText(getContext(), "添加关注失败", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 取消关注
     *
     * @param uid
     */
    private void unFollow(String uid) {
        //
        BDCoreApi.unFollow(getContext(), uid, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    String message = object.getString("message");
                    int status_code = object.getInt("status_code");
                    if (status_code != 200) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(getContext(), "取消关注成功", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {

                Toast.makeText(getContext(), "取消关注失败", Toast.LENGTH_LONG).show();
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
        BDCoreApi.addBlock(getContext(), uid, "添加备注","", "",  new BaseCallback() {

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
     * 取消拉黑
     *
     * @param uid
     */
    private void unBlock(String uid) {
        //
        BDCoreApi.unBlock(getContext(), uid, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    String message = object.getString("message");
                    int status_code = object.getInt("status_code");
                    if (status_code != 200) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        return;
                    }

                    Toast.makeText(getContext(), "取消拉黑成功", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {

                Toast.makeText(getContext(), "取消拉黑失败", Toast.LENGTH_LONG).show();
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

            getBlocks();
        }
    };


    /**
     * 监听 EventBus 广播消息
     *
     * @param tabEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTabEvent(TabEvent tabEvent) {

        if (tabEvent.getIndex() == 4) {
            getBlocks();
        }
    }


}







