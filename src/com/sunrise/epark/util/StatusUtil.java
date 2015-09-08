package com.sunrise.epark.util;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.sunrise.epark.activity.LoginActivity;
import com.sunrise.epark.activity.UserInfosActivity;
import com.sunrise.epark.bean.UserInfo;

public class StatusUtil {

	public static boolean Login(JSONObject object, Context context) {
		try {
			String userId = object.getString("userId");
			String userRole = object.getString("userRole");
			String uuid = object.getString("uuid");
			String imgUrl = object.getString("imgUrl");
			String userName = object.getString("userName");
			String userTel = object.getString("userTel");
			String userMail = object.getString("userMail");
			String userAddr = object.getString("userAddr");
			String carName = object.getString("carName");
			String userLogin = object.getString("userLogin");
			String userPassWord=object.getString("userPassword");

			UserInfo.userId = userId;
			UserInfo.userRole = userRole;
			UserInfo.uuid = uuid;
			UserInfo.userPassWord=userPassWord;
			UserInfo.imgUrl = imgUrl;
			UserInfo.userName = userName;
			UserInfo.userTel = userTel;
			UserInfo.userMail = userMail;
			UserInfo.userAddr = userAddr;
			UserInfo.carName = carName;
			UserInfo.userLogin = userLogin;
		

			Editor editor = PreferenceUtil.getInstance(context).sharedPreference
					.edit();
			editor.putString("userRole", userRole);
			editor.putString("userId", userId);
			editor.putString("uuid", uuid);
			editor.putString("imgUrl", imgUrl);
			editor.putString("userName", userName);
			editor.putString("userMobile", userTel);
			editor.putString("userEmail", userMail);
			editor.putString("userAddr", userAddr);
			editor.putString("carName", carName);
			editor.putString("userLogin", userLogin);
			editor.putString("userPassword",userPassWord);

			if ("01".equals(userRole)) {
				String parkId = object.getString("parkId");
				UserInfo.parkId = parkId;
				editor.putString("parkId", parkId);
			}

			if (StringUtils.isNotNull(userMail)
					&& StringUtils.isNotNull(userTel)
					&& StringUtils.isNotNull(carName)) {
				UserInfo.loginStatus = "login";
				editor.putString("loginStatus", "login");
			} else {
				editor.putString("loginStatus", "register");
				UserInfo.loginStatus = "register";
			}
			editor.commit();
			initTagAlias(context);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	private static void initTagAlias(Context context) {
		if (StringUtils.isNotNull(UserInfo.userRole)
				&& StringUtils.isNotNull(UserInfo.userId)) {
			Set<String> tags = new HashSet<String>();// 标签
			if ("01".equals(UserInfo.userRole)) {
				tags.add("manager");
			} else {
				tags.add("user");
			}
			JPushInterface.setAliasAndTags(context, UserInfo.uuid, tags,
					new TagAliasCallback() {
						@Override
						public void gotResult(int code, String alias,
								Set<String> tag) {
							switch (code) {
							case 0:
								LogUtil.i("设置Tag和Alias", "成功,alias=" + alias);
								break;
							case 6002:
								LogUtil.i("设置Tag和Alias", "超时");
								break;
							default:
								LogUtil.e("设置Tag和Alias", "失败" + code);
							}
						}
					});
		}

	}

	public static void exit(Context context) {
		Editor editor = PreferenceUtil.getInstance(context).sharedPreference
				.edit();
		editor.putString("userRole", "");
		editor.putString("userId", "");
		editor.putString("uuid", "");
		editor.putString("imgUrl", "");
		editor.putString("userName", "");
		editor.putString("userMobile", "");
		editor.putString("userEmail", "");
		editor.putString("userAddr", "");
		editor.putString("carName", "");
		editor.putString("userLogin", "");
		editor.putString("parkId", "");
		editor.putString("userMoney", "");
		editor.putString("loginStatus", "exit");
		editor.commit();

		UserInfo.userId = null;
		UserInfo.userRole = null;
		UserInfo.uuid = null;
		UserInfo.imgUrl = null;
		UserInfo.userName = null;
		UserInfo.userTel = null;
		UserInfo.userMail = null;
		UserInfo.userAddr = null;
		UserInfo.carName = null;
		UserInfo.userLogin = null;
		UserInfo.parkId = null;
		UserInfo.money = null;
		UserInfo.loginStatus = "exit";
	}

	public static void init(Context context) {
		PreferenceUtil SPUtils = PreferenceUtil.getInstance(context);
		String status = SPUtils.getString("loginStatus", "");
		if ("login".equals(status) || "register".equals(status)) {
			UserInfo.loginStatus = status;
			UserInfo.userId = SPUtils.getString("userId", "");
			UserInfo.userRole = SPUtils.getString("userRole", "");// 用户角色
			UserInfo.uuid = SPUtils.getString("uuid", "");// UUID
			UserInfo.imgUrl = SPUtils.getString("imgUrl", "");// 头像url地址
			UserInfo.userName = SPUtils.getString("userName", "");// 用户姓名
			UserInfo.userTel = SPUtils.getString("userMobile", "");// 用户手机号码
			UserInfo.userMail = SPUtils.getString("userEmail", "");// 用户邮箱
			UserInfo.userAddr = SPUtils.getString("userAddr", "");// 用户地址
			UserInfo.carName = SPUtils.getString("carName", "");// 用户默认车牌号
			UserInfo.money = SPUtils.getString("userMoney", "");
			UserInfo.userLogin = SPUtils.getString("userLogin", "");
			if ("01".equals(UserInfo.userRole)) {
				UserInfo.parkId = SPUtils.getString("parkId", "");
			}
			if (StringUtils.isNotNull(UserInfo.userMail)
					&& StringUtils.isNotNull(UserInfo.userTel)
					&& StringUtils.isNotNull(UserInfo.carName)&&"register".equals(status)) {
				UserInfo.loginStatus = "login";
				SPUtils.put("loginStatus", "login");
			} 
			initTagAlias(context);
		} else {
			UserInfo.loginStatus = "exit";
			SPUtils.put("loginStatus","exit");
		}
	}

	/**
	 * 
	 * @param context
	 * @param type
	 *            0:未登录则提示登录,未维护个人信息则提示维护个人信息 1:提示用户登录
	 * @return
	 */
	public static boolean StatusCheck(Context context) {
		boolean flag=false;
		switch (UserInfo.loginStatus) {
		case "login":
			flag=true;
		//	context.startActivity(new Intent(context,UserInfosActivity.class));
			break;
		case "exit":
			context.startActivity(new Intent(context,LoginActivity.class));
			break;
		case "register":
			context.startActivity(new Intent(context,UserInfosActivity.class));
			break;
		default:
			break;
		}
		return flag;
	}

	/** 添加车牌 */
	public static boolean AddCar(Context context, String carName) {
		Editor editor = PreferenceUtil.getInstance(context).sharedPreference
				.edit();
		editor.putString("carName", carName);
		System.err.println("修改前"+UserInfo.carName);
		UserInfo.carName = carName;
		System.err.println("修改后"+UserInfo.carName);
		if (StringUtils.isNotNull(UserInfo.carName)
				&& StringUtils.isNotNull(UserInfo.userTel)
				&& StringUtils.isNotNull(UserInfo.userMail)) {
			UserInfo.loginStatus = "login";
			editor.putString("loginStatus", "login");
		} 
		editor.commit();
		return true;
	}

	/**
	 * 保存账户余额
	 */
	public static void SaveUserMoney(Context context, String money) {
		UserInfo.money = money;
		PreferenceUtil.getInstance(context).setString("userMoney", money);
	}

	/**
	 * 保存用户头像url
	 */
	public static boolean SaveImgUrl(Context context, String url) {
		Editor editor = PreferenceUtil.getInstance(context).sharedPreference
				.edit();
		editor.putString("imgUrl", url);
		editor.commit();
		UserInfo.imgUrl = url;
		return true;
	}

	/** 修改姓名 */
	public static boolean SaveName(Context context, String name) {
		Editor editor = PreferenceUtil.getInstance(context).sharedPreference
				.edit();
		editor.putString("userName", name);// 用户角色
		editor.commit();
		UserInfo.userName = name;
		return true;
	}

	/** 修改电话 */
	public static boolean SaveTel(Context context, String phone) {
		Editor editor = PreferenceUtil.getInstance(context).sharedPreference
				.edit();
		editor.putString("userMobile", phone);// 用户角色
		editor.commit();
		UserInfo.userTel = phone;
		if (StringUtils.isNotNull(UserInfo.carName)
				&& StringUtils.isNotNull(UserInfo.userTel)
				&& StringUtils.isNotNull(UserInfo.userMail)) {
			UserInfo.loginStatus = "login";
			editor.putString("loginStatus", "login");
		} 
		return true;
	}

	/** 修改邮箱 */
	public static boolean SaveEmail(Context context, String email) {
		Editor editor = PreferenceUtil.getInstance(context).sharedPreference
				.edit();
		editor.putString("userEmail", email);// 用户角色
		editor.commit();
		UserInfo.userMail = email;
		if (StringUtils.isNotNull(UserInfo.carName)
				&& StringUtils.isNotNull(UserInfo.userTel)
				&& StringUtils.isNotNull(UserInfo.userMail)) {
			UserInfo.loginStatus = "login";
			editor.putString("loginStatus", "login");
		}
		return true;
	}

}
