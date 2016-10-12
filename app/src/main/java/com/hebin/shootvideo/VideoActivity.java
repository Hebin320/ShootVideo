package com.hebin.shootvideo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;


public class VideoActivity extends AppCompatActivity {

    VideoView videoView;
    ImageView ivBack;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoView = (VideoView) findViewById(R.id.videoview);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        path = getIntent().getStringExtra("videoFile");
    }

    @Override
    protected void onResume() {
        if (path == null) {
            return;
        }
        videoView.setMediaController(new MediaController(this));
        Uri videoUri = Uri.parse(path);
        videoView.setVideoURI(videoUri);
        videoView.start();
        //监听视频播放完的代码
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mPlayer) {
                mPlayer.start();
                mPlayer.setLooping(true);
            }
        });
        super.onResume();
    }
}
