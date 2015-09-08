package com.sunrise.epark.util;

import com.sunrise.epark.configure.Configure;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * App相关工具类
 * @author  钟继业 E-mail:zhongjy@camelotchina.com.cn
 * @version 创建时间：2015年7月9日 上午11:29:19
 * 类说明
 */
public class AppUtil {
	/**
	 * 得到当前的手机网络类型
	 * 
	 * @param context
	 * @return
	 */
	public static int getCurrentNetType(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null) {
			return Configure.NETWORK_OFF;
		} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			return Configure.NETWORK_WIFI;
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			int subType = info.getSubtype();
			if (subType == TelephonyManager.NETWORK_TYPE_CDMA
					|| subType == TelephonyManager.NETWORK_TYPE_GPRS
					|| subType == TelephonyManager.NETWORK_TYPE_EDGE) {
				return Configure.NETWORK_2G;
			} else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
					|| subType == TelephonyManager.NETWORK_TYPE_HSDPA
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_A
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_B
					|| subType == TelephonyManager.NETWORK_TYPE_HSPAP) {
				return Configure.NETWORK_3G;
			} else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {
				return Configure.NETWORK_4G;
			}
		}
		return Configure.NETWORK_OTHER;
	}

	/**
	 * 打开网络设置界面
	 */
	public static void openSetting(Activity activity) {
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings",
				"com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}

	/**
	 * 获取UserAgent信息
	 * @param context
	 * @return
	 */
	public static String getUserAgent(Context context) {
		if (Configure.USERAGENT != null && (!"".equals(Configure.USERAGENT))) {
			return Configure.USERAGENT;
		} else {
			WebView webview = new WebView(context);
			webview.layout(0, 0, 0, 0);
			WebSettings settings = webview.getSettings();
			Configure.USERAGENT = settings.getUserAgentString();
			return Configure.USERAGENT;
		}
	}

	/**
	 * 获取应用程序名称
	 */
	public static String getAppName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			int labelRes = packageInfo.applicationInfo.labelRes;
			return context.getResources().getString(labelRes);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取应用程序版本名称信息
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取应用程序版本名称信息
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static int getVersionCode(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
