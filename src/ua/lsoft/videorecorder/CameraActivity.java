package ua.lsoft.videorecorder;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import ua.lsoft.videorecorder.core.CameraController;
import ua.lsoft.videorecorder.core.DataPicking;
import ua.lsoft.videorecorder.core.XMLWriter;
import ua.lsoft.videorecorder.utils.DataCollector;
import ua.lsoft.videorecorder.utils.Lg;

import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.SensorManager;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
//import android.widget.Toast;
import android.widget.RelativeLayout;

public class CameraActivity extends Activity implements iRibbonMenuCallback {

	static int timeWriter = 10000;
	private Camera camera;
	private Camera.Parameters parameters;
	private SurfaceHolder surfaceHolder;
	private SurfaceView surfaceView;
	private MediaRecorder recorder;
	private SurfaceCallbacks surfaceCallbacks = new SurfaceCallbacks();
	private CameraCallbacks cameraCallbacks = new CameraCallbacks();
	
//	private CameraController cameraController;

	private OrientationEventListener orientationEventListener;
	private int displayOrientation = 0;

	private boolean recording = false;

	private Button safari;
	private RibbonMenuView menu;

	private boolean mp4 = true;
	private boolean low = true;
	
	File folder;
	String fileName;
	
	DataPicking dataPicking;
	private PowerManager.WakeLock wl;
	KeyguardManager keyguardManager;
	KeyguardLock lock;
	RelativeLayout container;
	
	Handler h = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what==1) {
				
				stopRecord();

				startRercord();
				h.sendEmptyMessageAtTime(1, SystemClock.uptimeMillis()+30000);
			}
			
			if (msg.what==2) {
				
				
				XMLWriter xml = new XMLWriter();
				try {
					xml.writeState(dataPicking, folder, fileName);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				h.sendEmptyMessageAtTime(2, SystemClock.uptimeMillis()+timeWriter);
			}

		}

	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);
		
		setContentView(R.layout.activity_camera);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNjfdhotDimScreen");
        wl.acquire();
		
        keyguardManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();
        
		container = (RelativeLayout) findViewById(R.id.scontainer);
//		surfaceHolder = surfaceView.getHolder();
//		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//		surfaceHolder.addCallback(surfaceCallbacks);

		safari = (Button) findViewById(R.id.ca_safari);
		menu = (RibbonMenuView) findViewById(R.id.ribbonMenu);
		menu.setCurrentCodec("mp4");
		menu.setCurrentSize("low");
		menu.setMenuClickCallback(this);
		
//		cameraController = new CameraController(this);
		
		initOrientationEventListener();
		

	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();


		dataPicking = new DataPicking(this);
		camera = getCamera();
		
		surfaceView = new SurfaceView(this);

		container.addView(surfaceView);
		
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(surfaceCallbacks);
		menu.bringToFront();
		recorder = new MediaRecorder();
		DataCollector dc;
		if (android.os.Build.VERSION.SDK_INT >=11)
			dc = new DataCollector(this, camera.getParameters().flatten(), camera.getParameters().getSupportedVideoSizes());
		else {
			dc = new DataCollector(this,camera.getParameters().flatten());
		}
		dc.sendInfo();
		


	}

	@Override
	protected void onPause() {
		super.onPause();
		stopRecord();
		
		if (recorder != null) {
			recorder.release();
			recorder = null;
		}

		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera = null;
			
		}
		container.removeView(surfaceView);
		surfaceView = null;
		dataPicking.release();
		h.removeMessages(1);
		h.removeMessages(2);
	}

	@Override
	protected void onDestroy() {
		orientationEventListener.disable();
		wl.release();
		lock.reenableKeyguard();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		stopRecord();
		super.onBackPressed();
	}

	public void OnSafari(View view) {
		if (recording) {
			stopRecord();
			h.removeMessages(1);
			h.removeMessages(2);
		} else {
			h.sendEmptyMessage(1);
			h.sendEmptyMessage(2);
		}
	}

	private synchronized void stopRecord() {
		if (recording) {
			recorder.stop();
			Lg.d(this, "stop");
			try {
				Lg.d(this, "reconnect");
				camera.reconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Lg.d(this, "startPreview");
			camera.startPreview();
			safari.setBackgroundResource(R.drawable.iconblue);
			recording = false;
		}

	}

	private synchronized void startRercord() {
		if (!recording) {
			Lg.d(this, "stopPreview");
			camera.stopPreview();
			camera.unlock();
			Lg.d(this, "setCamera");
			recorder.setCamera(camera);
			initRecorder();

			try {
				Lg.d(this, "prepare");
				recorder.prepare();
				recorder.start();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			safari.setBackgroundResource(R.drawable.iconred);
			recording = true;
			Lg.d(this, "RercordingStarts");
		}

	}

	private void initRecorder() {
		Lg.d(this, "recorderInitOne");
		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		recorder.setPreviewDisplay(surfaceHolder.getSurface());
		Lg.d(this, "recorderInitTwo");
		CamcorderProfile camProfile = getCamcorderProfile();
		if (camProfile != null) {
			recorder.setProfile(camProfile);
			Lg.d(this, "recorderInitsetProfile");
		} else {
			if (mp4)
				recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			else
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			Lg.d(this, "recorderInitCustomSet");
		}

		folder = new File(
				Environment.getExternalStorageDirectory(), "VideoRegistrator/");
		folder.mkdirs();

		Calendar c = Calendar.getInstance();
		fileName =

		fromInt(c.get(Calendar.YEAR))
				+ (fromInt(c.get(Calendar.MONTH) + 1).length() > 1 ? fromInt(c
						.get(Calendar.MONTH) + 1) : "0"
						+ fromInt(c.get(Calendar.MONTH) + 1))
				+ (fromInt(c.get(Calendar.DAY_OF_MONTH)).length() > 1 ? fromInt(c
						.get(Calendar.DAY_OF_MONTH)) : "0"
						+ fromInt(c.get(Calendar.DAY_OF_MONTH)))
				+ (fromInt(c.get(Calendar.HOUR_OF_DAY)).length() > 1 ? fromInt(c
						.get(Calendar.HOUR_OF_DAY)) : "0"
						+ fromInt(c.get(Calendar.HOUR_OF_DAY)))
				+ (fromInt(c.get(Calendar.MINUTE)).length() > 1 ? fromInt(c
						.get(Calendar.MINUTE)) : "0"
						+ fromInt(c.get(Calendar.MINUTE)))
				+ (fromInt(c.get(Calendar.SECOND)).length() > 1 ? fromInt(c
						.get(Calendar.SECOND)) : "0"
						+ fromInt(c.get(Calendar.SECOND)));
		Lg.d(this, "recorderInitOutPutFile:"+folder.getPath());
		recorder.setOutputFile(String.format(folder.getPath() + "/"
				+ fileName + ".%s", mp4 ? "mp4" : "3gp"));

	}

	public String fromInt(int val) {
		return String.valueOf(val);
	}

	private void initOrientationEventListener() {

		orientationEventListener = new OrientationEventListener(this,
				SensorManager.SENSOR_DELAY_NORMAL) {

			@SuppressLint("NewApi")
			@Override
			public void onOrientationChanged(int gegrees) {
				Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
						.getDefaultDisplay();
				int dOrientation = 0;

				if (gegrees > 315 || gegrees < 45) {
					dOrientation = display.getRotation() == 0 ? 90 : 0;
				}

				if (gegrees > 45 && gegrees < 135) {
					dOrientation = display.getRotation() == 0 ? 180 : 90;
				}

				if (gegrees > 135 && gegrees < 225) {
					dOrientation = display.getRotation() == 0 ? 270 : 180;
				}

				if (gegrees > 225 && gegrees < 315) {
					dOrientation = display.getRotation() == 0 ? 0 : 270;
				}

				if (dOrientation != displayOrientation && camera != null
						&& recorder != null && !recording) {
					Camera.Parameters p = camera.getParameters();
					p.setRotation(dOrientation);

					camera.setParameters(p);
					// camera.setDisplayOrientation(dOrientation);
					if (android.os.Build.VERSION.SDK_INT > 8)
						recorder.setOrientationHint(dOrientation);
					displayOrientation = dOrientation;
					
//					cameraController.setOrientation(dOrientation);

					menu.rotateElements(450 - dOrientation);
				}

			}
		};

		if (orientationEventListener.canDetectOrientation()) {
//			Toast.makeText(this, "Can DetectOrientation", Toast.LENGTH_LONG).show();
			orientationEventListener.enable();
		} else {
//			Toast.makeText(this, "Can't DetectOrientation", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private CamcorderProfile getCamcorderProfile() {
		CamcorderProfile camProfile = null;
		if (low) {
			camProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
		} else if (android.os.Build.VERSION.SDK_INT < 11){			
			camProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
		} 
//		else if (CamcorderProfile.hasProfile(0, CamcorderProfile.QUALITY_480P)){
//			camProfile = CamcorderProfile.get(0, CamcorderProfile.QUALITY_480P);
//		}


		if (camProfile != null) {
			if (mp4)
				camProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
			else
				camProfile.fileFormat = MediaRecorder.OutputFormat.THREE_GPP;
			camProfile.videoCodec = MediaRecorder.VideoEncoder.DEFAULT;
			camProfile.audioCodec = MediaRecorder.AudioEncoder.DEFAULT;
			return camProfile;
		}

		if (android.os.Build.VERSION.SDK_INT < 9) {
			return camProfile;

		}

//		if (Camera.getNumberOfCameras() > 0) {
//			camProfile = CamcorderProfile.get(0,
//					low ? CamcorderProfile.QUALITY_LOW
//							: CamcorderProfile.QUALITY_HIGH);
//		}
//
//		if (camProfile != null) {
//			if (mp4)
//				camProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
//			else
//				camProfile.fileFormat = MediaRecorder.OutputFormat.THREE_GPP;
//			camProfile.videoCodec = MediaRecorder.VideoEncoder.DEFAULT;
//			camProfile.audioCodec = MediaRecorder.AudioEncoder.DEFAULT;
//			return camProfile;
//		}

		return camProfile;
	}

	/**
	 * Hardware-safe scan cameras. Return first background camera, if that not
	 * exist - return front camera.
	 * 
	 * @return object camera. NULL - if camera not exists.
	 */
	@SuppressLint("NewApi")
	private Camera getCamera() {

		Camera camera = Camera.open();

		if (camera != null || android.os.Build.VERSION.SDK_INT < 9)
			return camera;

		if (Camera.getNumberOfCameras() > 0)
			return Camera.open(0);

		return null;
	}

	/**
	 * Callbacks for Camera
	 * 
	 * @author Enigmatic
	 * 
	 */
	private class CameraCallbacks implements Camera.PictureCallback,
			Camera.AutoFocusCallback {

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * Callbacks for SurfaceHolder
	 * 
	 * @author Enigmatic
	 * 
	 */
	private class SurfaceCallbacks implements SurfaceHolder.Callback {

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.i("Surface","Created");
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Log.i("Surface","Changed");

			try {
				camera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			camera.stopPreview();

			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = getBestPreviewSize(width, height, parameters);

			if (size != null) {
				parameters.setPreviewSize(size.width, size.height);
				camera.setParameters(parameters);

			}
			
	        Size previewSize = camera.getParameters().getPreviewSize();
	        float aspect = (float) previewSize.height / previewSize.width;

	        int previewSurfaceWidth = surfaceView.getWidth();
	        
			RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) container.getLayoutParams();
			lp.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
			container.setLayoutParams(lp);
	    //    LayoutParams lp = surfaceView.getLayoutParams();
	        
//            camera.setDisplayOrientation(90);
            lp.height = (int) (previewSurfaceWidth / aspect);
            lp.width = previewSurfaceWidth;
           

	        surfaceView.setLayoutParams(lp);
			

			camera.setDisplayOrientation(90);
			parameters.setRotation(90);
			camera.setParameters(parameters);
			displayOrientation = 90;
			camera.startPreview();

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.i("Surface","Destroy");

		}

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

	@Override
	public void RibbonMenuItemClick(int itemId) {
		if (itemId == RibbonMenuView.SIZE) {
			low = !low;
//			Toast.makeText(this, low + "!", Toast.LENGTH_SHORT).show();
		} else if (itemId == RibbonMenuView.CODEC) {
			mp4 = !mp4;
//			Toast.makeText(this, mp4 + "!", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void RibbonMenuItemClick(int itemId, boolean status) {
		// TODO Auto-generated method stub

	}

}
