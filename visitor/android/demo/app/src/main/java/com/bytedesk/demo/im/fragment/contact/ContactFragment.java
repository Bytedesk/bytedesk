package com.bytedesk.demo.im.fragment.contact;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.event.ContactEvent;
import com.bytedesk.core.room.entity.ContactEntity;
import com.bytedesk.core.util.BDCoreConstant;
import com.bytedesk.core.viewmodel.ContactViewModel;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.bytedesk.demo.common.ListViewDecoration;
import com.bytedesk.demo.im.adapter.ContactAdapter;
import com.orhanobut.logger.Logger;
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
 * @author bytedesk.com on 2018/3/26.
 *
 */
public class ContactFragment extends BaseFragment implements SwipeItemClickListener {

    @BindView(R.id.bytedesk_contact_topbar) QMUITopBarLayout mTopBar;
//    @BindView(R.id.emptyView) QMUIEmptyView mEmptyView;
    @BindView(R.id.pull_to_refresh) QMUIPullRefreshLayout mPullRefreshLayout;
    @BindView(R.id.bytedesk_search_edittext) EditText mSearchEditText;
    @BindView(R.id.contact_recycler_view) SwipeMenuRecyclerView mContactSwipeMenuRecyclerView;

    private ContactAdapter mContactAdapter;
    private ContactViewModel mContactViewModel;
    private List<ContactEntity> mContactEntities;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_contact, null);
        ButterKnife.bind(this, root);

        EventBus.getDefault().register(this);

        initTopBar();
        initRecycleView();
        initModel();

        // 加载联系人
        getContacts();

        return root;
    }

    /**
     *
     */
    protected void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle(getResources().getString(R.string.bytedesk_contact));
    }

    /**
     *
     */
    private void initRecycleView() {
        //
        mPullRefreshLayout.setOnPullListener(pullListener);
        // 搜索框
        mSearchEditText.addTextChangedListener(textWatcher);
        //
        mContactSwipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mContactSwipeMenuRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mContactSwipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mContactSwipeMenuRecyclerView.addItemDecoration(new ListViewDecoration(getContext()));// 添加分割线。
        mContactSwipeMenuRecyclerView.setSwipeItemClickListener(this); //
        // TODO: 添加搜索、群组等
//        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.contact_header_layout, mContactSwipeMenuRecyclerView, false);
//        mContactSwipeMenuRecyclerView.addHeaderView(headerView);
        //
        mContactAdapter = new ContactAdapter(getContext());
        mContactSwipeMenuRecyclerView.setAdapter(mContactAdapter);
    }

    /**
     *
     */
    private void initModel() {
        //
        mContactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        mContactViewModel.getContacts().observe(this, contactEntities -> {
            mContactEntities = contactEntities;
            mContactAdapter.setContacts(contactEntities);
        });
    }

    private void searchModel(String search) {
        mContactViewModel.searchContacts(search).observe(this, contactEntities -> {
            mContactEntities = contactEntities;
            mContactAdapter.setContacts(contactEntities);
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
     * 加载联系人
     */
    private void getContacts() {
        Logger.i("getContacts");

        BDCoreApi.getContacts(getContext(), new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {
                    JSONArray jsonArray = object.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mContactViewModel.insertContactJson(jsonArray.getJSONObject(i));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mPullRefreshLayout.finishRefresh();
            }

            @Override
            public void onError(JSONObject object) {

                try {
                    Logger.e(object.getString("message") +":"+ object.getString("data"));
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
        Logger.d("contact item clicked");

        ContactEntity contactEntity = mContactEntities.get(position);

        Bundle bundle = new Bundle();
        bundle.putString("uid", contactEntity.getUid());
        bundle.putString("nickname", contactEntity.getNickname());
        bundle.putString("realName", contactEntity.getRealName());
        bundle.putString("avatar", contactEntity.getAvatar());
        bundle.putString("description", contactEntity.getDescription());
        // TODO：区分一对一，群组会话
        bundle.putString("type", BDCoreConstant.MESSAGE_SESSION_TYPE_CONTACT);

        //
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        startFragment(detailFragment);
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
            getContacts();
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

}
