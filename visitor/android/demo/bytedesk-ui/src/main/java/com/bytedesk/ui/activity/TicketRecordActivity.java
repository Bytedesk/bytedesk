package com.bytedesk.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.room.entity.TicketEntity;
import com.bytedesk.ui.adapter.TicketRecordAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedesk.ui.R;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUIEmptyView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TicketRecordActivity extends AppCompatActivity {

    private QMUITopBarLayout mTopBar;
//    private QMUIGroupListView mGroupListView;
private QMUIEmptyView mEmptyView;
    private RecyclerView mRecyclerView;
    private TicketRecordAdapter ticketRecordAdapter;

    private String mTitle = "我的工单";
//    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_ticket_record);
        //
        mTopBar = findViewById(R.id.bytedesk_ticket_record_topbar);
        mEmptyView = findViewById(R.id.bytedesk_ticket_record_emptyView);
//        mGroupListView = findViewById(R.id.bytedesk_ticket_record_groupListView);
        mRecyclerView = findViewById(R.id.bytedesk_ticket_record_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //
        ticketRecordAdapter = new TicketRecordAdapter();
        mRecyclerView.setAdapter(ticketRecordAdapter);
        //
//        mUid = getIntent().getStringExtra(BDUiConstant.EXTRA_UID);
        QMUIStatusBarHelper.translucent(this);
        //
        initTopBar();
        initGroupListView();
        //
        getTickets();
    }

    private void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> finish());
        mTopBar.setTitle(mTitle);
    }

    private void initGroupListView() {

    }

    private void getTickets() {

        BDCoreApi.getTickets(this, 0, 20, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                try {

                    List<TicketEntity> ticketEntityList = new ArrayList<>();
                    JSONArray jsonArray = object.getJSONObject("data").getJSONArray("content");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        TicketEntity ticketEntity = new TicketEntity();
                        ticketEntity.setContent(jsonObject.getString("content"));
                        //
                        ticketEntityList.add(ticketEntity);
                    }
                    ticketRecordAdapter.setTickets(ticketEntityList);
                    if (ticketEntityList.size() == 0) {
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
