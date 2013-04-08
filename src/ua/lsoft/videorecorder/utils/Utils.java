package ua.lsoft.videorecorder.utils;

import java.util.Calendar;

public class Utils {

	public static String getCurrentFileName() {
		Calendar c = Calendar.getInstance();
		String date =

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
		return date;
	}
	
	public static String getCurrentData() {
		Calendar c = Calendar.getInstance();
		String date =

		fromInt(c.get(Calendar.YEAR))
				+"."+ (fromInt(c.get(Calendar.MONTH) + 1).length() > 1 ? fromInt(c
						.get(Calendar.MONTH) + 1) : "0"
						+ fromInt(c.get(Calendar.MONTH) + 1))
				+"."+ (fromInt(c.get(Calendar.DAY_OF_MONTH)).length() > 1 ? fromInt(c
						.get(Calendar.DAY_OF_MONTH)) : "0"
						+ fromInt(c.get(Calendar.DAY_OF_MONTH)))
				+"_"+ (fromInt(c.get(Calendar.HOUR_OF_DAY)).length() > 1 ? fromInt(c
						.get(Calendar.HOUR_OF_DAY)) : "0"
						+ fromInt(c.get(Calendar.HOUR_OF_DAY)))
				+":"+ (fromInt(c.get(Calendar.MINUTE)).length() > 1 ? fromInt(c
						.get(Calendar.MINUTE)) : "0"
						+ fromInt(c.get(Calendar.MINUTE)))
				+":"+ (fromInt(c.get(Calendar.SECOND)).length() > 1 ? fromInt(c
						.get(Calendar.SECOND)) : "0"
						+ fromInt(c.get(Calendar.SECOND)));
		return date;
	}

	private static String fromInt(int val) {
		return String.valueOf(val);
	}

}
