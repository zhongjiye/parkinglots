package com.sunrise.epark.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sunrise.epark.configure.Configure;

/**
 * 
 * 记录用户名，密码之类的首选项
 * 
 */
public class PreferenceUtil {
	private static PreferenceUtil preference = null;
	public SharedPreferences sharedPreference;
	public static final String FIRST ="first";
	
	public static synchronized PreferenceUtil getInstance(Context context) {
		if (preference == null)
			preference = new PreferenceUtil(context);
		return preference;
	}

	public PreferenceUtil(Context context) {
		sharedPreference = context.getSharedPreferences(Configure.SP_NANE,Context.MODE_PRIVATE);
	}

	public boolean getFirst(){
		return sharedPreference.getBoolean(FIRST, true);
	}
	public void setFirst(boolean f){
		Editor edit = sharedPreference.edit();
		edit.putBoolean(FIRST, f);
		edit.commit();
	}
	public String getString(String name,String defValue) {
		String value = sharedPreference.getString(name, defValue);
		return value;
	}
	public void setString(String name,String value) {
		Editor edit = sharedPreference.edit();
		edit.putString(name, value);
		edit.commit();
	}
	public int getInt(String name,int defValue) {
		int value = sharedPreference.getInt(name,defValue );
		return value;
	}
	public void setInt(String name,int value) {
		Editor edit = sharedPreference.edit();
		edit.putInt(name, value);
		edit.commit();
	}
	public float getFloat(String name,float defValue) {
		float value = sharedPreference.getFloat(name, defValue);
		return value;
	}
	public void setFloat(String name,float value) {
		Editor edit = sharedPreference.edit();
		edit.putFloat(name, value);
		edit.commit();
	}
	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 * 
	 * @param context
	 * @param key
	 * @param object
	 */
	public void put(String key, Object object) {
		Editor editor = sharedPreference.edit();
		if (object instanceof String) {
			editor.putString(key, (String) object);
		} else if (object instanceof Integer) {
			editor.putInt(key, (Integer) object);
		} else if (object instanceof Boolean) {
			editor.putBoolean(key, (Boolean) object);
		} else if (object instanceof Float) {
			editor.putFloat(key, (Float) object);
		} else if (object instanceof Long) {
			editor.putLong(key, (Long) object);
		} else {
			editor.putString(key, object.toString());
		}
		editor.commit();
	}
	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 * 
	 * @param context
	 * @param key
	 * @param defaultObject
	 * @return
	 */
	public  Object get(String key, Object defaultObject) {
		if (defaultObject instanceof String) {
			return sharedPreference.getString(key, (String) defaultObject);
		} else if (defaultObject instanceof Integer) {
			return sharedPreference.getInt(key, (Integer) defaultObject);
		} else if (defaultObject instanceof Boolean) {
			return sharedPreference.getBoolean(key, (Boolean) defaultObject);
		} else if (defaultObject instanceof Float) {
			return sharedPreference.getFloat(key, (Float) defaultObject);
		} else if (defaultObject instanceof Long) {
			return sharedPreference.getLong(key, (Long) defaultObject);
		}

		return null;
	}

}
