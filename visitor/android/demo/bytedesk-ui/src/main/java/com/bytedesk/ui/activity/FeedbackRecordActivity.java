package com.bytedesk.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.room.entity.FeedbackEntity;
import com.bytedesk.ui.R;
import com.bytedesk.ui.adapter.FeedbackRecordAdapter;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeedbackRecordActivity extends AppCompatActivity {

    private QMUITopBarLayout mTopBar;
//    private QMUIGroupListView mGroupListView;
    private QMUIEmptyView mEmptyView;
    private RecyclerView mRecyclerView;
    private FeedbackRecordAdapter feedbackRecordAdapter;

    private String mTitle = "我的反馈";
//    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_feedback_record);
        //
        mTopBar = findViewById(R.id.bytedesk_feedback_record_topbar);
        mEmptyView = findViewById(R.id.bytedesk_feedback_record_emptyView);
//        mGroupListView = findViewById(R.id.bytedesk_feedback_record_groupListView);
        mRecyclerView = findViewById(R.id.bytedesk_feedback_record_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //
        feedbackRecordAdapter = new FeedbackRecordAdapter(this);
        mRecyclerView.setAdapter(feedbackRecordAdapter);
        //
//        mUid = getIntent().getStringExtra(BDUiConstant.EXTRA_UID);
        QMUIStatusBarHelper.translucent(this);
        //
        initTopBar();
        initGroupListView();
        //
        getFeedbacks();
    }

    private void initTopBar() {

        mTopBar.addLeftBackImageButton().setOnClickListener(v -> finish());
        mTopBar.setTitle(mTitle);
    }

    private void initGroupListView() {

    }

    private void getFeedbacks() {

        BDCoreApi.getFeedbacks(this, 0, 20, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                try {

                    List<FeedbackEntity> feedbackEntityList = new ArrayList<>();
                    JSONArray jsonArray = object.getJSONObject("data").getJSONArray("content");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        FeedbackEntity feedbackEntity = new FeedbackEntity();
                        feedbackEntity.setContent(jsonObject.getString("content"));
                        feedbackEntity.setReplyContent(jsonObject.getString("replyContent"));
                        //
                        feedbackEntityList.add(feedbackEntity);
                    }
                    feedbackRecordAdapter.setFeedbacks(feedbackEntityList);
                    if (feedbackEntityList.size() == 0) {
                        mRecyclerView.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mEmptyView.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(JSONObject object) {

            }
        });
    }

}
