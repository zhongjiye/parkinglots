package com.sunrise.epark.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.util.DialogUtil;


public class BaseActivity extends Activity {
	private Dialog dialog;
	private Dialog payDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BaseApplication.getInstance().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.getInstance().finishActivity(this);
		hidePayDialog();
	}
	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
		hidePayDialog();
	}
	public void showLongToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}

	public void showShortToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	public void showLoadingDialog() {
		dialog=null;
		dialog = DialogUtil.createLoadingDialog(this, "正在加载中..");
		dialog.setCancelable(true);
		dialog.show();
	}
	public void showLoadingDialog(String msg) {
		closeLoadingDialog();
		dialog=null;
		dialog = DialogUtil.createLoadingDialog(this, msg);
		dialog.setCancelable(true);
		dialog.show();
	}

	public void closeLoadingDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

//	public void toActivity(Context packageContext, Class<?> cls,Shop shop){
//		Intent intent=new Intent(packageContext,cls);
//		intent.putExtra("object", shop);
//		startActivity(intent);
//	}
	/**
	 * 显示付款中
	 * @param msg
	 */
	public void showPayingDialog(String msg)
	{
		dialog=null;
		payDialog = DialogUtil.payingDialog(this, msg);
		payDialog.show();
	}
	/**
	 * 显示付款成功
	 * @param msg
	 */
	public void showPaySuccessDialog(String msg)
	
	{
		hidePayDialog();
		payDialog=null;
		payDialog = DialogUtil.paySuccessDialog(this, msg);
		payDialog.show();
	}
	/**
	 * 隐藏dialog
	 */
	public void hidePayDialog()
	{
		if(payDialog != null && payDialog.isShowing())
		{
			payDialog.dismiss();
		}
	}
}
