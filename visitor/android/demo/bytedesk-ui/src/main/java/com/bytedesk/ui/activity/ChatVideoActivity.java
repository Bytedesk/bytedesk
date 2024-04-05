package com.bytedesk.ui.activity;

import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bytedesk.ui.R;

public class ChatVideoActivity extends AppCompatActivity {

    private VideoView mVideoView;
    private String mVideoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bytedesk_activity_chat_video);

        mVideoUrl = getIntent().getStringExtra("video_url");
        mVideoView = findViewById(R.id.bytedesk_video);
//        Uri uri = Uri.parse(mVideoUrl);

        mVideoView.setVideoPath(mVideoUrl);
        mVideoView.start();
        mVideoView.requestFocus();
    }
}