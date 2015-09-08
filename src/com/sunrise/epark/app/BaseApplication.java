package com.sunrise.epark.app;

import java.util.Stack;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.app.Application;
import cn.jpush.android.api.JPushInterface;

/**
 * 描述: 自定义Application 退出应用程序和初始化系统变量
 * 
 * @author zhongjy
 * @date 2015年8月17日
 * @version 1.0
 */
public class BaseApplication extends Application {

	private static Stack<Activity> activityStack;

	/** Volley请求你队列 */
	public static RequestQueue mQueue;

	/** 自定义Applicaton实例 */
	private static BaseApplication instance;
	
	public static String className;

	@Override
	public void onCreate() {
		super.onCreate();
		JPushInterface.setDebugMode(false);// 关闭极光的开发者模式
		JPushInterface.init(this);// 初始化极光
		mQueue = Volley.newRequestQueue(this);// 初始化Volley请求队列
	}

	/**
	 * 获取自定义Application实例
	 * 
	 * @return
	 */
	public synchronized static BaseApplication getInstance() {
		if (null == instance) {
			instance = new BaseApplication();
		}
		return instance;
	}

	/**
	 * add Activity 添加Activity到栈
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * get current Activity 获取当前Activity（栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 获得栈中Activity的数量
	 * 
	 * @return
	 */
	public int getSize() {
		if (activityStack != null) {
			return activityStack.size();
		} else {
			return 0;
		}

	}

	/**
	 * 退出应用程序
	 */
	public void AppExit() {
		try {
			finishAllActivity();
		} catch (Exception e) {
		}
	}

}
