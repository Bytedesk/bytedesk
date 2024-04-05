package com.bytedesk.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.ui.R;
import com.bytedesk.ui.util.BDUiConstant;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 常用语列表
 *
 * @author bytedesk.com
 */
public class VisitorInfoActivity extends AppCompatActivity {

    private QMUITopBarLayout mTopBar;
    private QMUIGroupListView mGroupListView;

    private String mTitle = "访客信息";
    private QMUIGroupListView.Section mInfoSection;

    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_visitor_info);
        //
        mTopBar = findViewById(R.id.bytedesk_support_category_topbar);
        mGroupListView = findViewById(R.id.bytedesk_support_category_groupListView);
        //
        mUid = getIntent().getStringExtra(BDUiConstant.EXTRA_UID);
        //
        QMUIStatusBarHelper.translucent(this);
        //
        initTopBar();
        initGroupListView();
        // 加载常用语
        getDeviceInfo(mUid);
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> finish());
        mTopBar.setTitle(mTitle);
    }

    private void initGroupListView() {
        //
        mInfoSection = QMUIGroupListView.newSection(this);
    }

    private void getDeviceInfo(String uid) {

        BDCoreApi.getDeviceInfoByUid(this, uid, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                //
                try {

                    JSONArray jsonArray = object.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String name = jsonObject.getString("name");
                        String value = jsonObject.getString("value");
//                        String key = jsonObject.getString("key");
//                        Logger.i("name %s, value %s, key %s", name, value, key);

                        String title = name + ":" + value;
                        QMUICommonListItemView articleItem = mGroupListView.createItemView(title);
                        mInfoSection.addItemView(articleItem, v -> {
                            //
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mInfoSection.addTo(mGroupListView);
            }

            @Override
            public void onError(JSONObject object) {

            }
        });
    }

}
