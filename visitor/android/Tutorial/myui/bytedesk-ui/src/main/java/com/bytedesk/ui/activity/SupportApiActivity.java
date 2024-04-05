package com.bytedesk.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.room.entity.CategoryEntity;
import com.bytedesk.ui.R;
import com.bytedesk.ui.util.BDUiConstant;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.qmuiteam.qmui.widget.pullRefreshLayout.QMUIPullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 帮助中心
 *
 * @author bytedesk.com
 */
public class SupportApiActivity extends AppCompatActivity {

    QMUITopBarLayout mTopBar;
    QMUIPullRefreshLayout mPullRefreshLayout;
    QMUIGroupListView mGroupListView;

    private String mTitle = "常见问题";
    private String mUid;

    private QMUIGroupListView.Section mCategorySection;
//    private QMUIGroupListView.Section mArticleSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_support_api);
        //
        mTopBar = findViewById(R.id.bytedesk_support_api_topbar);
        mPullRefreshLayout = findViewById(R.id.bytedesk_support_api_pull_to_refresh);
        mGroupListView = findViewById(R.id.bytedesk_support_api_groupListView);
        //
        mUid = getIntent().getStringExtra(BDUiConstant.EXTRA_UID);
        QMUIStatusBarHelper.translucent(this);

        initTopBar();
        initGroupListView();

        // 加载分类
        getCategories();
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> finish());
        mTopBar.setTitle(mTitle);
    }

    private void initGroupListView() {
        //
        mPullRefreshLayout.setOnPullListener(pullListener);
        mCategorySection = QMUIGroupListView.newSection(this).setTitle("问题分类");
    }

    private void getCategories() {

        BDCoreApi.getSupportCategories(this, mUid, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    JSONArray categoryArray = object.getJSONArray("data");
                    for (int i = 0; i < categoryArray.length(); i++) {
                        CategoryEntity categoryEntity = new CategoryEntity(categoryArray.getJSONObject(i));
                        Logger.i("category:" + categoryEntity.getName());

                        QMUICommonListItemView categoryItem = mGroupListView.createItemView(categoryEntity.getName());
                        mCategorySection.addItemView(categoryItem, v -> {
                            //
                            Intent intent = new Intent(getApplicationContext(), SupportCategoryActivity.class);
                            intent.putExtra(BDUiConstant.EXTRA_SUPPORT_CATEGORY, categoryEntity.getCid());
                            startActivity(intent);
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mCategorySection.addTo(mGroupListView);

                // 加载常见问题
//                getArticles();

            }

            @Override
            public void onError(JSONObject object) {

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
            //
            mPullRefreshLayout.finishRefresh();
            //
//            getCategories();
//            getArticles();
        }
    };

}
