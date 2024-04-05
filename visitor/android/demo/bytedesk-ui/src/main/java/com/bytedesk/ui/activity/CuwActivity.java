package com.bytedesk.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.bytedesk.core.api.BDCoreApi;
import com.bytedesk.core.callback.BaseCallback;
import com.bytedesk.ui.R;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 常用语列表
 * TODO: 待缓存，从本地加载
 *
 * @author bytedesk.com
 */
public class CuwActivity extends AppCompatActivity {

    private QMUITopBarLayout mTopBar;
    private EditText mSearchEditText;
    private QMUIGroupListView mGroupListView;
    private QMUIGroupListView mSearchResultListView;

    private String mTitle = "常用语";
//    private QMUIGroupListView.Section mCuwSection;

    private JSONObject allCuwObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_cuw);
        //
        mTopBar = findViewById(R.id.bytedesk_support_category_topbar);
        mSearchEditText = findViewById(R.id.bytedesk_cuw_search_edittext);
        mGroupListView = findViewById(R.id.bytedesk_cuw_groupListView);
        mSearchResultListView = findViewById(R.id.bytedesk_search_result_groupListView);
        //
        QMUIStatusBarHelper.translucent(this);
        allCuwObject = new JSONObject();
        //
        initTopBar();
        initGroupListView();
        // 加载常用语
        getCuws();
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(v -> finish());
        // TODO: 下一版 添加
//        mTopBar.addRightImageButton(R.mipmap.icon_topbar_plus2, QMUIViewHelper.generateViewId()).setOnClickListener(view -> {
//            Logger.i("add cuw");
//        });
        mTopBar.setTitle(mTitle);
    }

    private void initGroupListView() {
//        mCuwSection = QMUIGroupListView.newSection(this);
        // 搜索框
        mSearchEditText.addTextChangedListener(textWatcher);
    }

    private void getCuws() {

        Context context = this;
        BDCoreApi.getCuws(this, new BaseCallback() {
            @Override
            public void onSuccess(JSONObject object) {

                allCuwObject = object;

                try {

                    JSONArray mineArray = object.getJSONObject("data").getJSONArray("mine");
                    for (int i = 0; i < mineArray.length(); i++) {
                        JSONObject category = mineArray.getJSONObject(i);
                        QMUIGroupListView.Section cuwSection = QMUIGroupListView.newSection(context);
                        cuwSection.setTitle(category.getString("name"));

                        JSONArray cuwChildren = category.getJSONArray("cuwChildren");
                        for (int j = 0; j < cuwChildren.length(); j++) {
                            JSONObject cuw = cuwChildren.getJSONObject(j);
//                            Long id = cuw.getLong("id");
//                            String cid = cuw.getString("cid");
                            String type = cuw.getString("type");
                            String name = cuw.getString("name");
                            String content = cuw.getString("content");
//                            Logger.i("mine type %s, name: %s, content: %s", type, name, content);
                            //
                            QMUICommonListItemView articleItem = mGroupListView.createItemView(name);
                            articleItem.setDetailText(getDetail(type, content));

                            cuwSection.addItemView(articleItem, v -> {
                                //
                                Intent intent = new Intent();
                                intent.putExtra("type", type);
                                intent.putExtra("content", content);
                                setResult(1, intent);
                                finish();
                            });
//                            mCuwSection.addItemView(articleItem, v -> {
//                                //
//                                Intent intent = new Intent();
//                                intent.putExtra("type", type);
//                                intent.putExtra("content", content);
//                                setResult(1, intent);
//                                finish();
//                            });
                        }

                        cuwSection.addTo(mGroupListView);
                    }

                    JSONArray companyArray = object.getJSONObject("data").getJSONArray("company");
                    for (int i = 0; i < companyArray.length(); i++) {
                        JSONObject category = companyArray.getJSONObject(i);
                        QMUIGroupListView.Section cuwSection = QMUIGroupListView.newSection(context);
                        cuwSection.setTitle(category.getString("name"));

                        JSONArray cuwChildren = category.getJSONArray("cuwChildren");
                        for (int j = 0; j < cuwChildren.length(); j++) {
                            JSONObject cuw = cuwChildren.getJSONObject(j);
//                            Long id = cuw.getLong("id");
//                            String cid = cuw.getString("cid");
                            String type = cuw.getString("type");
                            String name = cuw.getString("name");
                            String content = cuw.getString("content");
//                            Logger.i("company type %s, name: %s, content: %s", type, name, content);
                            //
                            QMUICommonListItemView articleItem = mGroupListView.createItemView(name);
                            articleItem.setDetailText(getDetail(type, content));

                            cuwSection.addItemView(articleItem, v -> {
                                //
                                Intent intent = new Intent();
                                intent.putExtra("type", type);
                                intent.putExtra("content", content);
                                setResult(1, intent);
                                finish();
                            });
                        }

                        cuwSection.addTo(mGroupListView);
                    }

                    JSONArray platformArray = object.getJSONObject("data").getJSONArray("platform");
                    for (int i = 0; i < platformArray.length(); i++) {
                        JSONObject category = platformArray.getJSONObject(i);
                        QMUIGroupListView.Section cuwSection = QMUIGroupListView.newSection(context);
                        cuwSection.setTitle(category.getString("name"));

                        JSONArray cuwChildren = category.getJSONArray("cuwChildren");
                        for (int j = 0; j < cuwChildren.length(); j++) {
                            JSONObject cuw = cuwChildren.getJSONObject(j);
//                            Long id = cuw.getLong("id");
//                            String cid = cuw.getString("cid");
                            String type = cuw.getString("type");
                            String name = cuw.getString("name");
                            String content = cuw.getString("content");
//                            Logger.i("platform type %s, name: %s, content: %s", type, name, content);
                            //
                            QMUICommonListItemView articleItem = mGroupListView.createItemView(name);
                            articleItem.setDetailText(getDetail(type, content));

                            cuwSection.addItemView(articleItem, v -> {
                                //
                                Intent intent = new Intent();
                                intent.putExtra("type", type);
                                intent.putExtra("content", content);
                                setResult(1, intent);
                                finish();
                            });
                        }

                        cuwSection.addTo(mGroupListView);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                mCuwSection.addTo(mGroupListView);
            }

            @Override
            public void onError(JSONObject object) {

            }
        });
    }

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
            Log.d("search: ", search.trim());
            if (search != null && search.trim().length() > 0) {
                searchCuwResult(search.trim());
            } else {
                initCuw();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void searchCuwResult(String search) {
        //
        mGroupListView.setVisibility(View.GONE);
        mSearchResultListView.setVisibility(View.VISIBLE);
        mSearchResultListView.removeAllViews();
        //
        try {

            JSONArray mineArray = allCuwObject.getJSONObject("data").getJSONArray("mine");
            for (int i = 0; i < mineArray.length(); i++) {
                JSONObject category = mineArray.getJSONObject(i);
                QMUIGroupListView.Section cuwSection = QMUIGroupListView.newSection(this);
                cuwSection.setTitle(category.getString("name"));
                //
                boolean flag = false;
                JSONArray cuwChildren = category.getJSONArray("cuwChildren");
                for (int j = 0; j < cuwChildren.length(); j++) {
                    JSONObject cuw = cuwChildren.getJSONObject(j);

                    String type = cuw.getString("type");
                    String name = cuw.getString("name");
                    String content = cuw.getString("content");

                    if (name.contains(search) || content.contains(search)) {
                        flag = true;
                        //
                        QMUICommonListItemView articleItem = mSearchResultListView.createItemView(name);
                        articleItem.setDetailText(getDetail(type, content));

                        cuwSection.addItemView(articleItem, v -> {
                            //
                            Intent intent = new Intent();
                            intent.putExtra("type", type);
                            intent.putExtra("content", content);
                            setResult(1, intent);
                            finish();
                        });
                    }
                }
                if (flag) {
                    cuwSection.addTo(mSearchResultListView);
                }
            }

            JSONArray companyArray = allCuwObject.getJSONObject("data").getJSONArray("company");
            for (int i = 0; i < companyArray.length(); i++) {
                JSONObject category = companyArray.getJSONObject(i);
                QMUIGroupListView.Section cuwSection = QMUIGroupListView.newSection(this);
                cuwSection.setTitle(category.getString("name"));
                //
                boolean flag = false;
                JSONArray cuwChildren = category.getJSONArray("cuwChildren");
                for (int j = 0; j < cuwChildren.length(); j++) {
                    JSONObject cuw = cuwChildren.getJSONObject(j);

                    String type = cuw.getString("type");
                    String name = cuw.getString("name");
                    String content = cuw.getString("content");

                    if (name.contains(search) || content.contains(search)) {
                        flag = true;
                        //
                        QMUICommonListItemView articleItem = mSearchResultListView.createItemView(name);
                        articleItem.setDetailText(getDetail(type, content));

                        cuwSection.addItemView(articleItem, v -> {
                            //
                            Intent intent = new Intent();
                            intent.putExtra("type", type);
                            intent.putExtra("content", content);
                            setResult(1, intent);
                            finish();
                        });
                    }
                }
                if (flag) {
                    cuwSection.addTo(mSearchResultListView);
                }
            }

            JSONArray platformArray = allCuwObject.getJSONObject("data").getJSONArray("platform");
            for (int i = 0; i < platformArray.length(); i++) {
                JSONObject category = platformArray.getJSONObject(i);
                QMUIGroupListView.Section cuwSection = QMUIGroupListView.newSection(this);
                cuwSection.setTitle(category.getString("name"));
                //
                boolean flag = false;
                JSONArray cuwChildren = category.getJSONArray("cuwChildren");
                for (int j = 0; j < cuwChildren.length(); j++) {
                    JSONObject cuw = cuwChildren.getJSONObject(j);

                    String type = cuw.getString("type");
                    String name = cuw.getString("name");
                    String content = cuw.getString("content");

                    if (name.contains(search) || content.contains(search)) {
                        flag = true;
                        //
                        QMUICommonListItemView articleItem = mSearchResultListView.createItemView(name);
                        articleItem.setDetailText(getDetail(type, content));

                        cuwSection.addItemView(articleItem, v -> {
                            //
                            Intent intent = new Intent();
                            intent.putExtra("type", type);
                            intent.putExtra("content", content);
                            setResult(1, intent);
                            finish();
                        });
                    }
                }
                if (flag) {
                    cuwSection.addTo(mSearchResultListView);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initCuw() {
        mGroupListView.setVisibility(View.VISIBLE);
        mSearchResultListView.setVisibility(View.GONE);
    }

    private String getDetail(String type, String content) {
        //
        if (type.equals("text")) {
            return "[文字]" + content;
        } else if (type.equals("image")) {
            return "[图片]";
        } else if (type.equals("file")) {
            return "[文件]";
        } else if (type.equals("voice")) {
            return "[语音]";
        } else if (type.equals("video")) {
            return "[视频]";
        }
        return "[文字]" + content;
    }

}
