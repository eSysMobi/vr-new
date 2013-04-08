package ua.lsoft.videorecorder.utils;

import android.util.Log;

public class Lg {
	//TODO + test - if null -> "null" or "empty"
	private static final int VERBOSE = 0;
	private static final int DEBUG = 1;
	private static final int INFO = 2;
	private static final int WARNING = 3;
	private static final int ERROR = 4;
	
	private static int LoggerLevel = 11;
	
	public static void d(Object object, String text) {
		if (LoggerLevel>=DEBUG)
			Log.d(object.getClass().getName(), text);
	}
	
	public static void d(Object object, Integer text) {
		if (LoggerLevel>=DEBUG)
			Log.d(object.getClass().getName(), String.valueOf(text));
	}
	
	public static void d(Object object, Long text) {
		if (LoggerLevel>=DEBUG)
			Log.d(object.getClass().getName(), String.valueOf(text));
	}
	
	public static void e(Object object, String text) {
		if (LoggerLevel>=ERROR)
			Log.d(object.getClass().getName(), text);
	}
	
	public static void e(String title, String text) {
		if (LoggerLevel>=ERROR)
			Log.d(title, text);
	}

}
