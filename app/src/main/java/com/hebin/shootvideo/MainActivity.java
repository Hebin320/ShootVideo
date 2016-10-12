package com.hebin.shootvideo;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;


import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private RoundProgressBar roundProgressBar;
    private RelativeLayout rl_shoot_video;

    Timer timer;
    TimerTask timerTask;
    int progress = 0;

    // 拍视频的类
    private CommonShootVideo commonShootVideo;
    // 视频文件
    File videoFile = new File(Environment.getExternalStorageDirectory()
            + "/my.mp4");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }

    private void initListener() {

        rl_shoot_video.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 当SD卡可用时，才能拍摄视频，否则报错
                        // 按下开始拍视频
                        commonShootVideo.startVideo(videoFile);
                        // 启动计时器
                        progress = 0;
                        roundProgressBar.setMax(100);
                        if (timer == null) {
                            timer = new Timer();
                        }
                        if (timerTask == null) {
                            timerTask = new TimerTask() {

                                @Override
                                public void run() {
                                    roundProgressBar.setProgress(progress += 1);
                                    if (progress >= 100) {
                                        // 10秒时间到了
                                        stopShootVideo();
                                    }
                                }
                            };
                        }
                        timer.schedule(timerTask, 0, 100);
                        break;
                    case MotionEvent.ACTION_UP:
                        stopShootVideo();
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void stopShootVideo() {
        // 停止拍视频
        if (commonShootVideo.stopVideo()) {
            // 拍摄成功
            String path = Environment.getExternalStorageDirectory() + "/my.mp4";
            Intent i = new Intent();
            i.putExtra("videoFile", path);
            i.setClass(MainActivity.this, VideoActivity.class);
            startActivity(i);
            roundProgressBar.setProgress(0);
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
            timerTask = null;
        }

    }

    private void initView() {

        SurfaceView surfaceview = (SurfaceView) findViewById(R.id.surfaceview);
        commonShootVideo = new CommonShootVideo(this, surfaceview);
        roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
        rl_shoot_video = (RelativeLayout) findViewById(R.id.rl_shoot_video);
    }

}
