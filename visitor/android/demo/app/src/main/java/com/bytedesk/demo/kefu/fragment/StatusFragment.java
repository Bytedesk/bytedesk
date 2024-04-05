package com.bytedesk.demo.kefu.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StatusFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView) QMUIGroupListView mGroupListView;

    private String mDefaultWorkGroupWid = "201807171659201";
    private String mDefaultAgentUid = "201808221551193";

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_online_status, null);
        ButterKnife.bind(this, root);

        initTopBar();
        initGroupListView();

        return root;
    }

    private void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle("查询在线状态接口");
    }

    private void initGroupListView() {

        final QMUICommonListItemView workGroupStatusItem = mGroupListView.createItemView("工作组Wid：" + mDefaultWorkGroupWid);

        QMUIGroupListView.newSection(getContext())
                .setTitle("工作组在线状态接口")
//                .setDescription("默认描述")
                .addItemView(workGroupStatusItem, view -> {
                    //
                })
                .addTo(mGroupListView);

        final QMUICommonListItemView agentStatusItem = mGroupListView.createItemView("客服账号Uid：" + mDefaultAgentUid);
//        agentStatusItem.setDetailText("status");

        QMUIGroupListView.newSection(getContext())
                .setTitle("客服账号在线状态接口")
//                .setDescription("自定义描述")
                .addItemView(agentStatusItem, view -> {
                    //
                })
                .addTo(mGroupListView);


        // 获取某个工作组的在线状态：online代表在线，offline代表离线
        BDCoreApi.getWorkGroupStatus(getContext(), mDefaultWorkGroupWid, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                try {
                    String status = object.getJSONObject("data").getString("status");
                    workGroupStatusItem.setDetailText(status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(JSONObject object) {
                Logger.e("获取工作组在线状态错误");
            }
        });

        // 获取某个客服账号的在线状态：online代表在线，offline代表离线
        BDCoreApi.getAgentStatus(getContext(), mDefaultAgentUid, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                try {
                    String status = object.getJSONObject("data").getString("status");
                    agentStatusItem.setDetailText(status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject object) {
                Logger.e("获取客服在线状态错误");

            }
        });

    }

}
