package com.hebin.shootvideo;

import java.io.File;
import java.io.IOException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 拍摄视频的公共类
 *
 */
@SuppressLint("NewApi")
public class CommonShootVideo implements SurfaceHolder.Callback {
	private MediaRecorder mediarecorder;// 录制视频的类
	private SurfaceHolder surfaceHolder;
	private Camera mCamera;
	private File mVideoFile;

	public CommonShootVideo(Context context, SurfaceView surfaceview) {

		init(context, surfaceview);
	}

	// surfaceview 显示视频的控件
	private void init(Context context, SurfaceView surfaceview) {
		// 选择支持半透明模式,在有surfaceview的activity中使用。
		((Activity) context).getWindow().setFormat(PixelFormat.TRANSLUCENT);

		SurfaceHolder holder = surfaceview.getHolder();// 取得holder
		holder.addCallback(this); // holder加入回调接口
		// setType必须设置，要不出错.
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	public void startVideo(File videoFile) {
		//当videoFile不存在时先创建文件
		initFile(videoFile);
		mVideoFile = videoFile;

		if (mediarecorder == null) {
			mediarecorder = new MediaRecorder();
		} else {
			mediarecorder.reset();
		}

		mCamera = getCameraInstance();
		mediarecorder.setCamera(mCamera);

		// 设置从麦克风采集声音
		mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 设置录制视频源为Camera(相机)
		mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		// 设置视频文件的输出格式
		// 必须在设置声音编码格式、图像编码格式之前设置
		// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
		mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		// 设置声音编码格式
		mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		// 设置录制的视频编码h263 h264
		mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
		// after setVideoSource(),after setOutFormat()
		mediarecorder.setVideoSize(640, 480);
		// 设置视频输出的格式和编码
		CamcorderProfile mProfile = CamcorderProfile
				.get(CamcorderProfile.QUALITY_480P);
		mediarecorder.setAudioEncodingBitRate(44100);
		if (mProfile.videoBitRate > 5 * 1024 * 1024) {
			// 很重要，提高视频清晰度
			mediarecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
		} else {
			mediarecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
		}
		// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
		// after setVideoSource(),after setOutFormat()
		mediarecorder.setVideoFrameRate(mProfile.videoFrameRate);
		// 指定使用SurfaceView来预览视频
		mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
		// 设置录制视频的方向
		// 加了HTC的手机会有问题
		mediarecorder.setOrientationHint(90);
		// 设置视频文件输出的路径
		mediarecorder.setOutputFile(videoFile.getAbsolutePath());

		try {
			// 准备录制
			mediarecorder.prepare();
			// 开始录制
			mediarecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 结束拍摄视频
	 *
	 * @return 拍摄成功，返回true；拍摄失败，返回false
	 */
	public boolean stopVideo() {
		// 关闭预览并释放资源

		if (mediarecorder != null) {
			try {
				// 停止录制
				mediarecorder.stop();
				// 释放资源
				mediarecorder.release();
				mediarecorder = null;
				return true;
			} catch (Exception e) {
				return false;
			}finally{
				if (mCamera != null) {
					mCamera.lock();
					mCamera.stopPreview();
					mCamera.release();
					mCamera = null;
				}
			}

		}

		return false;
	}

	/**
	 * 判断前置摄像头是否存在
	 *
	 * @return
	 */
	@SuppressLint("NewApi")
	private int FindFrontCamera() {
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number

		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
				return camIdx;
			}
		}
		return -1;
	}

	/**
	 * 判断后置摄像头是否存在
	 *
	 * @return
	 */
	@SuppressLint("NewApi")
	private int FindBackCamera() {
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number

		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				// 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
				return camIdx;
			}
		}
		return -1;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
		// 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
		surfaceHolder = holder;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
		surfaceHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// surfaceDestroyed的时候同时对象设置为null
		surfaceHolder = null;
		mediarecorder = null;
		mCamera = null;
	}

	private Camera getCameraInstance() {
		// 设置摄像头以及摄像头的方向
		int CammeraIndex = FindBackCamera();

		Camera mCamera = null;
		try {
			// 获取Camera实例
			mCamera = Camera.open(CammeraIndex);
			mCamera.setDisplayOrientation(90);
			mCamera.unlock();
		} catch (Exception e) {
			// 摄像头不可用（正被占用或不存在）
//			ToastUtil.showMessage("摄像头不可用");
		}
		// 不可用则返回null
		return mCamera;
	}
	/**
	 * 初始化应用文件夹目录
	 */
	public static void initDir(File targetDir) {
		if (!targetDir.exists()) {
			targetDir.mkdir();
		}

	}
	/**
	 * 对目标文件进行判断，不存在，创建（先创建目录，再创建文件）
	 * @param targetFile
	 */
	public static void initFile(File targetFile){
		if(targetFile.exists()){
			return ;
		}else {
			File dir = targetFile.getParentFile();
			initDir(dir);
			try {
				targetFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}