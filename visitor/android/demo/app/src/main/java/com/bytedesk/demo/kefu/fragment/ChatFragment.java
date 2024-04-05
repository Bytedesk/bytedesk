package com.bytedesk.demo.kefu.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.util.BDCoreConstant;
import com.bytedesk.core.util.JsonCustom;
import com.bytedesk.demo.R;
import com.bytedesk.demo.common.BaseFragment;
import com.bytedesk.demo.utils.BDDemoConst;
import com.bytedesk.ui.api.BDUiApi;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *  TODO:
 *    5. fragment方式打开
 */
public class ChatFragment extends BaseFragment {

    @BindView(R.id.topbar) QMUITopBarLayout mTopBar;
    @BindView(R.id.groupListView) QMUIGroupListView mGroupListView;

    QMUICommonListItemView unreadCountItem;

    @Override
    protected View onCreateView() {
        //
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_chat, null);
        ButterKnife.bind(this, root);

        initTopBar();
        initGroupListView();
        //
        getUnreadCountVisitor();

        return root;
    }

    private void initTopBar() {
        //
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> popBackStack());
        mTopBar.setTitle("开始新会话接口");
    }

    private void initGroupListView() {
        //
        unreadCountItem = mGroupListView.createItemView("未读消息数目：");
        //
        QMUICommonListItemView workGroupChatItem = mGroupListView.createItemView("工作组会话:" + BDDemoConst.wId);
        QMUICommonListItemView appointChatItem = mGroupListView.createItemView("指定客服一对一会话:" + BDDemoConst.agentUid);
        //
        QMUIGroupListView.newSection(getContext())
                .setTitle("默认会话接口")
                .setDescription("在web管理后台开启/关闭机器人")
                .addItemView(unreadCountItem, view -> {
                    getUnreadCountVisitor();
                })
                .addItemView(workGroupChatItem, view -> {
                    //
                    BDUiApi.startWorkGroupChatActivity(getContext(), BDDemoConst.wId, "工作组客服");
                })
                .addItemView(appointChatItem, view -> {
                    //
                    BDUiApi.startAppointChatActivity(getContext(), BDDemoConst.agentUid, "指定客服");
                })
                .addTo(mGroupListView);
        //
        QMUICommonListItemView shopWorkGroupChatItem = mGroupListView.createItemView("电商客服工作组:" + BDDemoConst.wId);
        QMUICommonListItemView shopAppointedChatItem = mGroupListView.createItemView("电商指定客服一对一:" + BDDemoConst.agentUid);
        //
        QMUIGroupListView.newSection(getContext())
                .setTitle("电商客服")
                .setDescription("")
                .addItemView(shopWorkGroupChatItem, view -> {
                    //
                    JsonCustom jsonCustom = new JsonCustom();
                    jsonCustom.setType(BDCoreConstant.MESSAGE_TYPE_COMMODITY);
                    jsonCustom.setTitle("商品标题");
                    jsonCustom.setContent("商品详情");
                    jsonCustom.setPrice("9.99");
                    jsonCustom.setUrl("https://item.m.jd.com/product/12172344.html");
                    jsonCustom.setImageUrl("https://bytedesk.oss-cn-shenzhen.aliyuncs.com/images/123.webp");
                    jsonCustom.setId(100121);
                    jsonCustom.setCategoryCode("100010003");
                    //
                    String custom = new Gson().toJson(jsonCustom);
                    BDUiApi.startWorkGroupChatActivityShop(getContext(), BDDemoConst.wId, "电商工作组客服", custom);
                })
                .addItemView(shopAppointedChatItem, view -> {
                    //
                    JsonCustom jsonCustom = new JsonCustom();
                    jsonCustom.setType(BDCoreConstant.MESSAGE_TYPE_COMMODITY);
                    jsonCustom.setTitle("商品标题");
                    jsonCustom.setContent("商品详情");
                    jsonCustom.setPrice("9.99");
                    jsonCustom.setUrl("https://item.m.jd.com/product/12172344.html");
                    jsonCustom.setImageUrl("https://bytedesk.oss-cn-shenzhen.aliyuncs.com/images/123.webp");
                    jsonCustom.setId(100121);
                    jsonCustom.setCategoryCode("100010003");
                    //
                    String custom = new Gson().toJson(jsonCustom);
                    BDUiApi.startAppointChatActivityShop(getContext(), BDDemoConst.agentUid, "电商指定客服", custom);
                }).addTo(mGroupListView);

        //
//        QMUICommonListItemView preChoiceChatItem = mGroupListView.createItemView("前置选择:" +  BDDemoConst.preWid);
//        QMUIGroupListView.newSection(getContext())
//            .setTitle("前置选择")
//            .setDescription("在后台启用问卷选择")
//            .addItemView(preChoiceChatItem, view -> {
//                //
//                BDUiApi.startWorkGroupChatActivity(getContext(), BDDemoConst.preWid, "前置选择");
//            }).addTo(mGroupListView);

        //
        QMUICommonListItemView robotChatItem = mGroupListView.createItemView("默认机器人:" +  BDDemoConst.robotWid);
        //
        QMUIGroupListView.newSection(getContext())
                .setTitle("默认机器人")
                .setDescription("在后台设置默认机器人")
                .addItemView(robotChatItem, v -> {
                    BDUiApi.startWorkGroupChatActivity(getContext(), BDDemoConst.robotWid, "默认机器人");
                }).addTo(mGroupListView);
    }


    private void getUnreadCountVisitor() {

        BDCoreApi.getUnreadCountVisitor(getContext(), new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {
                try {
                    // 未读消息数目
                    Integer unreadCount = object.getInt("data");
                    Logger.i("unreadCount %s", unreadCount);
                    unreadCountItem.setDetailText(unreadCount.toString());
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
