package com.bytedesk.demo.im.fragment.contact;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.room.entity.ContactEntity;
import com.bytedesk.core.util.BDPreferenceManager;
import com.bytedesk.core.viewmodel.ContactViewModel;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.ListViewDecoration;
import com.bytedesk.demo.im.adapter.SelectAdapter;
import com.bytedesk.ui.base.BaseFragment;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

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
 * 选人组群
 */
public class SelectFragment extends BaseFragment implements SwipeItemClickListener {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
//    @BindView(R.id.emptyView) QMUIEmptyView mEmptyView;
    @BindView(R.id.pull_to_refresh) QMUIPullRefreshLayout mPullRefreshLayout;
    @BindView(R.id.bytedesk_search_edittext) EditText mSearchEditText;
    @BindView(R.id.recycler_view) SwipeMenuRecyclerView mSwipeMenuRecyclerView;

    private SelectAdapter mSelectAdapter;
    private ContactViewModel mContactViewModel;
    private List<ContactEntity> mContactEntities;
    private List<ContactEntity> mSelectedEntities;

    private BDPreferenceManager mPreferenceManager;

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_select, null);
        ButterKnife.bind(this, root);
        mPreferenceManager = BDPreferenceManager.getInstance(getContext());

        initTopBar();
        initRecycleView();
        initModel();
        getContacts();

        return root;
    }

    /**
     *
     */
    protected void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        //
        mTopBar.addRightTextButton(getResources().getString(R.string.bytedesk_ok), R.id.topbar_right_about_button).setOnClickListener(view -> {
            // 调用建群接口
            if (mSelectedEntities.size() < 2) {
                Toast.makeText(getContext(), "至少选择2人及以上", Toast.LENGTH_SHORT).show();
                return;
            }
            //
            List<String> selectedContactUids = new ArrayList<>();
            String nickname = mPreferenceManager.getNickname();
            for (int i = 0; i < mSelectedEntities.size(); i++) {
                ContactEntity contactEntity = mSelectedEntities.get(i);
                if (i < 4) {
                    nickname += "," + contactEntity.getRealName();
                }
                selectedContactUids.add(contactEntity.getUid());
            }
            // 调用建群接口
            BDCoreApi.createGroup(getContext(), nickname, selectedContactUids, new BaseCallback() {
                @Override
                public void onSuccess(JSONObject object) {

                    // TODO: 本地存储group

                    popBackStack();
                }

                @Override
                public void onError(JSONObject object) {
                    Logger.e("创建群组失败");
                }
            });
        });
        mTopBar.setTitle(getResources().getString(R.string.bytedesk_select));
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
        mSwipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSwipeMenuRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mSwipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mSwipeMenuRecyclerView.addItemDecoration(new ListViewDecoration(getContext()));// 添加分割线。
        mSwipeMenuRecyclerView.setSwipeItemClickListener(this); //
        //
        mSelectAdapter = new SelectAdapter(getContext());
        mSwipeMenuRecyclerView.setAdapter(mSelectAdapter);
    }

    /**
     *
     */
    private void initModel() {
        //
        mSelectedEntities = new ArrayList<>();
        //
        mContactViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        mContactViewModel.getContacts().observe(this, contactEntities -> {
            mContactEntities = contactEntities;
            mSelectAdapter.setContacts(contactEntities);
        });
    }

    private void searchModel(String search) {
        mContactViewModel.searchContacts(search).observe(this, contactEntities -> {
            mContactEntities = contactEntities;
            mSelectAdapter.setContacts(contactEntities);
        });
    }

    private void getContacts() {
        Logger.i("getContacts");

        BDCoreApi.getContacts(getContext(), new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {
                JSONArray jsonArray = null;

                try {
                    jsonArray = object.getJSONArray("data");
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
        Logger.d("select contact item clicked");

        ContactEntity contactEntity = mContactEntities.get(position);
        contactEntity.setSelected(!contactEntity.isSelected());
        if (contactEntity.isSelected()) {
            mSelectedEntities.add(contactEntity);
        } else {
            mSelectedEntities.remove(contactEntity);
        }
        mContactEntities.set(position, contactEntity);
        mSelectAdapter.setContacts(mContactEntities);
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
