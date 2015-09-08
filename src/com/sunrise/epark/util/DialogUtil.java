package com.sunrise.epark.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sunrise.epark.R;
import com.sunrise.epark.activity.LoginActivity;
import com.sunrise.epark.activity.SendReceiptActivity;
import com.sunrise.epark.activity.UserInfosActivity;
import com.sunrise.epark.app.BaseApplication;

public class DialogUtil {
	public static void showDialog(Context context, int titleid, int msgid,
			int leftbtnid, int rightbtnid, OnClickListener LeftOnClickListener,
			OnClickListener RightOnClickListener, boolean cancelable) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setCancelable(false);
		builder.setTitle(titleid);
		builder.setMessage(msgid)
				.setNegativeButton(leftbtnid, LeftOnClickListener)
				.setPositiveButton(rightbtnid, RightOnClickListener).create()
				.show();
	}

	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void showDialog(Context context, String title, String msg,
			String leftbtn, String rightbtn,
			OnClickListener LeftOnClickListener,
			OnClickListener RightOnClickListener, boolean cancelable) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setCancelable(cancelable);
		builder.setTitle(title).setMessage(msg)
				.setNegativeButton(leftbtn, LeftOnClickListener)
				.setPositiveButton(rightbtn, RightOnClickListener).create()
				.show();
	}

	public static void showNoTitleDialog(Context context, int msgid,
			int leftbtnid, int rightbtnid, OnClickListener LeftOnClickListener,
			OnClickListener RightOnClickListener, boolean cancelable) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setCancelable(cancelable);
		builder.setMessage(msgid)
				.setNegativeButton(leftbtnid, LeftOnClickListener)
				.setPositiveButton(rightbtnid, RightOnClickListener).create()
				.show();
	}

	public static void showNoTitleDialog(Context context, String msg,
			String leftbtn, String rightbtn,
			OnClickListener LeftOnClickListener,
			OnClickListener RightOnClickListener, boolean cancelable) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
				.setCancelable(cancelable);
		builder.setMessage(msg).setNegativeButton(leftbtn, LeftOnClickListener)
				.setPositiveButton(rightbtn, RightOnClickListener).create()
				.show();
	}

	/**
	 * 得到自定义的progressDialog
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public static Dialog createLoadingDialog(Context context, String msg) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
		// main.xml中的ImageView
		ProgressBar spaceshipImage = (ProgressBar) v.findViewById(R.id.loading);
		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
		tipTextView.setText(msg);// 设置加载信息
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

		loadingDialog.setCancelable(false);// 不可以用“返回键”取消
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
		return loadingDialog;

	}

	/**
	 * 请先登录
	 */
	public static Dialog pleaseLoginDialog(Context context, String msg) {
		Button yes;
		final Dialog dialog = new Dialog(context, R.style.mask_dialog);// 创建自定义样式dialog
		LinearLayout popView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.please_login_tishi, null);
		// 获取对象
		/** 温馨提示 */
		TextView tv = (TextView) popView.findViewById(R.id.tv);
		/** 内容 */
		TextView tv1 = (TextView) popView.findViewById(R.id.tv1);
		/** 确定 */
		yes = (Button) popView.findViewById(R.id.yes);
		yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setCancelable(false);// 不可以用“返回键”取消
		dialog.setContentView(popView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		return dialog;
	}
	/**
	 * 请进入选择默认车牌
	 */
	public static Dialog pleaseSelectCarIdDialog(Context context, String msg) {
		Button yes;
		final Dialog dialog = new Dialog(context, R.style.mask_dialog);// 创建自定义样式dialog
		LinearLayout popView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.please_in_userinfo_tishi, null);
		// 获取对象
		/** 温馨提示 */
		TextView tv = (TextView) popView.findViewById(R.id.tv);
		/** 内容 */
		TextView tv1 = (TextView) popView.findViewById(R.id.tv1);
		/** 确定 */
		yes = (Button) popView.findViewById(R.id.yes);
		yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setCancelable(false);// 不可以用“返回键”取消
		dialog.setContentView(popView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		return dialog;
	}

	/**
	 * 请进入个人信息维护
	 */
	public static Dialog pleaseInUserInfoDialog(final Context context, String msg) {
		Button yes;
		final Dialog dialog = new Dialog(context, R.style.mask_dialog);// 创建自定义样式dialog
		LinearLayout popView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.please_in_userinfo_tishi, null);
		// 获取对象
		/** 温馨提示 */
		TextView tv = (TextView) popView.findViewById(R.id.tv);
		/** 内容 */
		TextView tv1 = (TextView) popView.findViewById(R.id.tv1);
		tv1.setText("请先维护个人信息!");
		/** 确定 */
		yes = (Button) popView.findViewById(R.id.yes);
		yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				context.startActivity(new Intent(context,UserInfosActivity.class));
			}
		});
		dialog.setCancelable(false);// 不可以用“返回键”取消
		dialog.setContentView(popView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		return dialog;
	}

	/**
	 * 提示信息弹出框
	 * 
	 * @param context
	 * @param msg
	 *            弹出框内容
	 * @param cancel
	 *            是否可取消
	 */
	public static void showDialog(Context context, String msg, boolean cancel,final EditText editText) {
		Button yes;
		final Dialog dialog = new Dialog(context, R.style.mask_dialog);// 创建自定义样式dialog
		LinearLayout popView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.please_in_userinfo_tishi, null);

		((TextView) popView.findViewById(R.id.tv1)).setText(msg);
		yes = (Button) popView.findViewById(R.id.yes);
		yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				editText.setFocusable(true);
				editText.setFocusableInTouchMode(true);
				editText.requestFocus();
			}
		});
		dialog.setCancelable(cancel);// 不可以用“返回键”取消
		dialog.setContentView(popView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		dialog.show();
		WindowManager windowManager = ((Activity) context).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int) (display.getWidth()) - 200; // 设置宽度
		dialog.getWindow().setAttributes(lp);
	}
	/**
	 * 提示信息弹出框
	 * 
	 * @param context
	 * @param msg
	 *            弹出框内容
	 * @param cancel
	 *            是否可取消
	 */
	public static void showDialog(Context context, String msg, boolean cancel) {
		Button yes;
		final Dialog dialog = new Dialog(context, R.style.mask_dialog);// 创建自定义样式dialog
		LinearLayout popView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.please_in_userinfo_tishi, null);
		
		((TextView) popView.findViewById(R.id.tv1)).setText(msg);
		yes = (Button) popView.findViewById(R.id.yes);
		yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setCancelable(cancel);// 不可以用“返回键”取消
		dialog.setContentView(popView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		dialog.show();
		WindowManager windowManager = ((Activity) context).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = (int) (display.getWidth()) - 200; // 设置宽度
		dialog.getWindow().setAttributes(lp);
	}

	/**
	 * 单点登陆
	 */
	@SuppressLint("InflateParams")
	public static void loginAgain(final Context context) {
		Button yes;
		final Dialog dialog = new Dialog(context, R.style.mask_dialog);// 创建自定义样式dialog
		LinearLayout popView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.please_in_userinfo_tishi, null);
		((TextView) popView.findViewById(R.id.tv1))
				.setText(R.string.login_again);
		yes = (Button) popView.findViewById(R.id.yes);
		yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				StatusUtil.exit(context);
				BaseApplication.getInstance().AppExit();
				context.startActivity(new Intent(context, LoginActivity.class));
			}
		});
		dialog.setCancelable(false);// 不可以用“返回键”取消
		dialog.setContentView(popView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		dialog.show();
	}

	/**
	 * 付款中请稍候dialog
	 */
	public static Dialog payingDialog(Context context, String msg) {
		Dialog dialog = new Dialog(context, R.style.mask_dialog);// 创建自定义样式dialog
		LinearLayout popView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.pay_dialog, null);
		ProgressBar progressBar = (ProgressBar) popView
				.findViewById(R.id.pay_load_icon);
		progressBar.setIndeterminateDrawable(context.getResources()
				.getDrawable(R.drawable.rotate_loading_360));
		TextView textView = (TextView) popView.findViewById(R.id.pay_load_tip);
		textView.setText(msg);
		dialog.setCancelable(false);// 不可以用“返回键”取消
		dialog.setContentView(popView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		return dialog;
	}

	/**
	 * 付款成功dialog
	 */
	public static Dialog paySuccessDialog(Context context, String msg) {
		Dialog dialog = new Dialog(context, R.style.mask_dialog);// 创建自定义样式dialog
		LinearLayout popView = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.pay_dialog, null);
		ProgressBar progressBar = (ProgressBar) popView
				.findViewById(R.id.pay_load_icon);
		progressBar.setIndeterminateDrawable(context.getResources()
				.getDrawable(R.drawable.icon_ok));
		TextView textView = (TextView) popView.findViewById(R.id.pay_load_tip);
		textView.setText(msg);
		dialog.setCancelable(true);// 不可以用“返回键”取消
		dialog.setContentView(popView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
		dialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);
		return dialog;
	}
}
