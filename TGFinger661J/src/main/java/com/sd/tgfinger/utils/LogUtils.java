package com.sd.tgfinger.utils;

import android.util.Log;

/**
 * Log信息处理类
 *
 */
public class LogUtils {

	private static final String TAG = "《==TG==》";

	private static final boolean LOGGER = true;

	public static void v(String tag, String msg) {
		if (LOGGER) {
			Log.v(TAG, tag + "-->" + msg);
		}
	}

	public static void d(String msg) {
		if (LOGGER) {
			Log.d(TAG, "-->" + msg);
		}
	}
	public static void d(String tag,String msg) {
			Log.d(tag, "-->" + msg);
	}

	public static void i(String msg) {
		if (LOGGER) {
			Log.i(TAG, "-->" + msg);
		}
	}
	public static void i(String tag,String msg) {
		Log.i(tag, "-->" + msg);
	}

	public static void w(String tag, String msg) {
		if (LOGGER) {
			Log.v(TAG, tag + "-->" + msg);
		}
	}

	public static void e(String msg) {
		if (LOGGER) {
			Log.e(TAG, "-->" + msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (LOGGER) {
			Log.e(TAG, tag + "-->" + msg);
		}
	}
}
