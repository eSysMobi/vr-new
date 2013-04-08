package ua.lsoft.videorecorder.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class DataCollector {
	
	private Context context;
	
	private String deviceName;
	private StringBuilder listSizes = new StringBuilder();
	private String flatten;
	
	public DataCollector(Context context, String flatten) {
		this.context = context;
		deviceName = android.os.Build.MODEL+"/"+android.os.Build.MANUFACTURER+ "/"+android.os.Build.PRODUCT;
		this.flatten = flatten;  
	}
	
	public DataCollector(Context context, String flatten, List<Camera.Size> sizes) {
		this.context = context;
		this.flatten = flatten;  
		deviceName = android.os.Build.MODEL+"/"+android.os.Build.MANUFACTURER+ "/"+android.os.Build.PRODUCT;
		if (sizes!=null && sizes.size()>0) {
			for (Camera.Size s : sizes) {
				listSizes.append(s.width+"x"+s.height+";");
			}
		}
	}
	
	public void sendInfo() {
		
		DataSender sender = new DataSender();
		sender.execute();
		
	}
	
	
	
	private class DataSender extends AsyncTask<Void, Void, Boolean> {
		

		@Override
		protected void onPreExecute() {
//			Toast.makeText(context, "start", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			return send();
		}

		@Override
		protected void onPostExecute(Boolean result) {
//			Toast.makeText(context, "send: "+result, Toast.LENGTH_SHORT).show();
		}
		
		private boolean send() {
			String SYNC = "http://psi.kh.ua/test/devices.php";
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 30*1000);
			HttpConnectionParams.setSoTimeout(httpParameters, 40*1000);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			
			HttpPost httppost = null;
			

			ArrayList<NameValuePair> formparams = new ArrayList<NameValuePair>();
			listSizes.append("!");
			formparams.add(new BasicNameValuePair("deviceName", deviceName));
			formparams.add(new BasicNameValuePair("sizes", listSizes.toString()));
			formparams.add(new BasicNameValuePair("flatten", flatten));
			
			try {
				Log.i("host", SYNC);
				httppost = new HttpPost(SYNC);
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
				Log.i("entity", httppost.getURI().toString());
				httppost.setEntity(entity);
				Lg.d(this, entity+" ");

			} catch (UnsupportedEncodingException e) {
				Lg.e(this, "setQueryParams: UnsupportedEncodingException");
			}

			try {
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity responseEntity = response.getEntity();
				
				if (responseEntity != null) {    
					String json = EntityUtils.toString(responseEntity);
					Log.i("json", json+"!");
					return true;
				}
				
			} catch (ClientProtocolException e) {
				Lg.e(this, "authorization() ClientProtocolException");
				e.printStackTrace();
			} catch (IOException e) {
				Lg.e(this, "authorization() IOException");
				e.printStackTrace();
			}
			return false;

		}
		
	}

}
