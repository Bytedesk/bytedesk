package com.bytedesk.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bytedesk.ui.R;


/**
 * 查看大图：
 *
 * TODO:
 *  1. 修改背景为透明黑色
 *  2. 支持缩放
 *  3. 支持点击关闭
 */
public class BigImageViewActivity extends AppCompatActivity {

    private ImageView mImageView;
    private String mImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_bigimageview);

        mImageUrl = getIntent().getStringExtra("image_url");

        mImageView = findViewById(R.id.bytedesk_bigimageview);
        Glide.with(this).load(mImageUrl).into(mImageView);
    }
}
