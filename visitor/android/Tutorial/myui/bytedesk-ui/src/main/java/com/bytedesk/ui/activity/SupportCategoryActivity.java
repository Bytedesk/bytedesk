package com.bytedesk.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.core.room.entity.ArticleEntity;
import com.bytedesk.ui.R;
import com.bytedesk.ui.util.BDUiConstant;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 帮助中心
 *
 * @author bytedesk.com
 */
public class SupportCategoryActivity extends AppCompatActivity {

    private QMUITopBarLayout mTopBar;
    private QMUIGroupListView mGroupListView;

    private String mTitle = "分类详情";
    private String mCid;

    private QMUIGroupListView.Section mArticleSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_support_category);
        //
        mTopBar = findViewById(R.id.bytedesk_support_category_topbar);
        mGroupListView = findViewById(R.id.bytedesk_support_category_groupListView);
        //
        mCid = getIntent().getStringExtra(BDUiConstant.EXTRA_SUPPORT_CATEGORY);
        QMUIStatusBarHelper.translucent(this);

        initTopBar();
        initGroupListView();

        // 加载分类
        getArticles();
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> finish());
        mTopBar.setTitle(mTitle);
    }

    private void initGroupListView() {
        //
        mArticleSection = QMUIGroupListView.newSection(this).setTitle("常见问题");
    }

    private void getArticles() {

        BDCoreApi.getSupportArticles(this, mCid, new BaseCallback() {

            @Override
            public void onSuccess(JSONObject object) {

                try {

                    JSONArray articleArray = object.getJSONObject("data").getJSONArray("content");
                    for (int i = 0; i < articleArray.length(); i++) {
                        ArticleEntity articleEntity = new ArticleEntity(articleArray.getJSONObject(i));
                        Logger.i("article:" + articleEntity.getTitle());

                        QMUICommonListItemView articleItem = mGroupListView.createItemView(articleEntity.getTitle());
                        mArticleSection.addItemView(articleItem, v -> {
                            //
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //
                mArticleSection.addTo(mGroupListView);
            }

            @Override
            public void onError(JSONObject object) {

            }
        });
    }

}
