package ua.lsoft.videorecorder.core;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import ua.lsoft.videorecorder.R;
import ua.lsoft.videorecorder.utils.Utils;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;

public class CameraController {

	private Context context;
	private boolean recording = false;

	private Camera camera;
	private Camera.Parameters parameters;
	private MediaRecorder mediaRecorder;

	public CameraController(Context context) {
		this.context = context;
	}

	public void startRecord(Surface surface) throws IllegalStateException, IOException {
		if (!recording) {
			camera.stopPreview();
			camera.unlock();
			mediaRecorder.setCamera(camera);
			initRecorder(surface);

			mediaRecorder.prepare();
			mediaRecorder.start();
			recording = true;
		}
	}
	
	public void stopRecord() throws IOException {
		if (recording) {
			mediaRecorder.stop();
			camera.reconnect();
			camera.startPreview();
			recording = false;
		}
	}
	
	public void init(SurfaceHolder holder, int width, int height) throws IOException {
		camera = getCamera();
		if (camera!=null) {
			parameters = camera.getParameters();
		}
		mediaRecorder = new MediaRecorder();
		
		
		
		camera.setPreviewDisplay(holder);
		camera.stopPreview();

		Camera.Size size = getBestPreviewSize(width, height, parameters);

		if (size != null) {
			parameters.setPreviewSize(size.width, size.height);
			camera.setParameters(parameters);

		}

		camera.setDisplayOrientation(90);
		parameters.setRotation(90);
		camera.setParameters(parameters);

		camera.startPreview();
	}
	
	public void release() {
		if (mediaRecorder != null) {
			mediaRecorder.release();
			mediaRecorder = null;
		}

		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}
	
	public void setOrientation(int degrees) {
		parameters.setRotation(degrees);
		camera.setParameters(parameters);
		if (android.os.Build.VERSION.SDK_INT > 8)
			mediaRecorder.setOrientationHint(degrees);
	}

	public boolean isRecording() {
		return recording;
	}
	
	private void initRecorder(Surface surface) {

		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		mediaRecorder.setPreviewDisplay(surface);

		CamcorderProfile camProfile = getCamcorderProfile();
		if (camProfile != null) {
			mediaRecorder.setProfile(camProfile);
		} else {
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		}

		File imageFileFolder = new File(Environment.getExternalStorageDirectory(), "VideoRegistrator/");
		imageFileFolder.mkdirs();

		String date = Utils.getCurrentFileName();

		mediaRecorder.setOutputFile(String.format(imageFileFolder.getPath() + "/"
				+ date + ".%s", "mp4"));

	}

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;
					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		return (result);
	}

	/**
	 * Hardware-safe scan cameras. Return first background camera, if that not
	 * exist - return front camera.
	 * 
	 * @return object camera. NULL - if camera not exists.
	 */
	private Camera getCamera() {

		Camera camera = Camera.open();

		if (camera != null || android.os.Build.VERSION.SDK_INT < 9)
			return camera;

		if (Camera.getNumberOfCameras() > 0)
			return Camera.open(0);

		return null;
	}
	
	private CamcorderProfile getCamcorderProfile() {
		CamcorderProfile camProfile = CamcorderProfile
				.get(CamcorderProfile.QUALITY_LOW);

		if (camProfile != null) {
			camProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
			camProfile.videoCodec = MediaRecorder.VideoEncoder.DEFAULT;
			camProfile.audioCodec = MediaRecorder.AudioEncoder.DEFAULT;
			return camProfile;
		}

		if (android.os.Build.VERSION.SDK_INT < 9) {
			return camProfile;

		}

		if (Camera.getNumberOfCameras() > 0) {
			camProfile = CamcorderProfile.get(0,CamcorderProfile.QUALITY_LOW);
		}

		if (camProfile != null) {
			camProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
			camProfile.videoCodec = MediaRecorder.VideoEncoder.DEFAULT;
			camProfile.audioCodec = MediaRecorder.AudioEncoder.DEFAULT;
			return camProfile;
		}

		return camProfile;
	}

}
