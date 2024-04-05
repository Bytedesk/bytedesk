package com.bytedesk.ui.activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;
import android.widget.Toast;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.room.entity.ContactEntity;
import com.bytedesk.core.util.BDPreferenceManager;
import com.bytedesk.core.viewmodel.ContactViewModel;
import com.bytedesk.ui.R;
import com.bytedesk.ui.adapter.SelectAdapter;
import com.bytedesk.ui.util.BDListViewDecoration;
import com.bytedesk.ui.util.BDUiConstant;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContactSelectActivity extends AppCompatActivity implements SwipeItemClickListener {

    private QMUITopBarLayout mTopBar;
    private QMUIPullRefreshLayout mPullRefreshLayout;
    private SwipeMenuRecyclerView mSwipeMenuRecyclerView;

    private SelectAdapter mSelectAdapter;
    private ContactViewModel mContactViewModel;
    private List<ContactEntity> mContactEntities;
    private List<ContactEntity> mSelectedEntities;

    private BDPreferenceManager mPreferenceManager;

    private String mGid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_contact_select);
        mPreferenceManager = BDPreferenceManager.getInstance(this);
        //
        mGid = getIntent().getStringExtra(BDUiConstant.EXTRA_UID);
        //
        initTopBar();
        initRecycleView();
        initModel();
        getContacts();
    }

    private void initTopBar() {
        //
        mTopBar = findViewById(R.id.bytedesk_contact_topbar);
        //
        final Context context = this;
        mTopBar.addRightTextButton("邀请", R.mipmap.icon_topbar_overflow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 调用邀请入群接口
                if (mSelectedEntities.size() < 1) {
                    Toast.makeText(context, "至少选择1人及以上", Toast.LENGTH_SHORT).show();
                    return;
                }
                //
                List<String> selectedContactUids = new ArrayList<>();
                for (int i = 0; i < mSelectedEntities.size(); i++) {
                    ContactEntity contactEntity = mSelectedEntities.get(i);
                    selectedContactUids.add(contactEntity.getUid());
                }
                // 调用建群接口
                BDCoreApi.inviteListToGroup(context, selectedContactUids, mGid, new BaseCallback() {
                    @Override
                    public void onSuccess(JSONObject object) {

                        finish();
                    }

                    @Override
                    public void onError(JSONObject object) {
                        Logger.e("邀请失败");
                    }
                });
            }
        });
        //
        mTopBar.setTitle("邀请人");
        mTopBar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //
        QMUIStatusBarHelper.translucent(this);
    }

    /**
     * 界面初始化
     */
    private void initRecycleView () {
        //
        mPullRefreshLayout = findViewById(R.id.bytedesk_contact_pulltorefresh);
        mPullRefreshLayout.setOnPullListener(pullListener);

        // 初始化
        mSwipeMenuRecyclerView = findViewById(R.id.bytedesk_contact_fragment_recyclerview);
        mSwipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSwipeMenuRecyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        mSwipeMenuRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mSwipeMenuRecyclerView.addItemDecoration(new BDListViewDecoration(this));// 添加分割线。
        mSwipeMenuRecyclerView.setSwipeItemClickListener(this); //

        // 设置适配器adapter
        mSelectAdapter = new SelectAdapter(this);
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
        mContactViewModel.getContacts().observe(this, new Observer<List<ContactEntity>>() {
            @Override
            public void onChanged(@Nullable List<ContactEntity> contactEntities) {
                //
                mContactEntities = contactEntities;
                mSelectAdapter.setContacts(mContactEntities);
            }
        });
    }

    /**
     *
     * @param position
     */
//    @Override
//    public void onItemClicked(int position) {
//        Logger.i("contact item clicked: " + position);
//
//    }

    /**
     * 加载联系人
     */
    private void getContacts() {
        Logger.i("getContacts");

        BDCoreApi.getContacts(this, new BaseCallback() {

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
            getContacts();
        }
    };


    @Override
    public void onItemClick(View itemView, int position) {
        //
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
}
