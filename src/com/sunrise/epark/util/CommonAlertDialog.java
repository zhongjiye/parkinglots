package com.sunrise.epark.util;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sunrise.epark.R;







/**
 * 通用弹出框的样式
 * @author wenjian
 *
 */
public class CommonAlertDialog {
	private Dialog mDialog;
	private Context mContext;

	public CommonAlertDialog(Context context) {
		mContext = context;
	}

	/**
	 * 创建有两个按钮的对话框
	 * 
	 * @param resMsg
	 *            提示信息
	 * @param positiveListenr
	 *            确定按钮的监听器
	 * @param negativeListener
	 *            取消按钮的监听器
	 */
	public void showYesOrNoDialog(String resMsg,
			OnClickListener positiveListener, OnClickListener negativeListener) {
		View diaView = View.inflate(mContext, R.layout.widget_dialog, null);
		mDialog = new Dialog(mContext, R.style.dialog_default);
		mDialog.setContentView(diaView);
		TextView mTextViewMsg = (TextView) diaView
				.findViewById(R.id.dialog_msg);
		mTextViewMsg.setText(resMsg);
		Button mConfirmButton = (Button) diaView.findViewById(R.id.btn_confirm);
		mConfirmButton.setOnClickListener(positiveListener);
		Button mCancelButton = (Button) diaView.findViewById(R.id.btn_cancel);
		mCancelButton.setOnClickListener(negativeListener);
		mDialog.show();
	}
	
	/**
	 * 创建有一个按钮的对话框 用于出错的情况
	 * 
	 * @param resMsg
	 *            提示信息
	 * @param positiveListenr
	 *            确定按钮的监听器
	 */
	public void showYesErrorDialog(String resMsg, OnClickListener positiveListener) {
		View diaView = View.inflate(mContext, R.layout.widget_dialog, null);
		mDialog = new Dialog(mContext, R.style.dialog_default);
		mDialog.setContentView(diaView);
		TextView mTextViewMsg = (TextView) diaView
				.findViewById(R.id.dialog_msg);
		mTextViewMsg.setText(resMsg+"     (请联系后台管理员)");
		Button mConfirmButton = (Button) diaView.findViewById(R.id.btn_confirm);
		mConfirmButton.setOnClickListener(positiveListener);
		Button mCancelButton = (Button) diaView.findViewById(R.id.btn_cancel);
		mCancelButton.setVisibility(View.GONE);
		mDialog.show();
	}
	
	/**
	 * 创建有一个按钮的对话框
	 * 
	 * @param resMsg
	 *            提示信息
	 * @param positiveListenr
	 *            确定按钮的监听器
	 */
	public void showYesDialog(String resMsg, OnClickListener positiveListener) {
		View diaView = View.inflate(mContext, R.layout.widget_dialog, null);
		mDialog = new Dialog(mContext, R.style.dialog_default);
		mDialog.setContentView(diaView);
		TextView mTextViewMsg = (TextView) diaView
				.findViewById(R.id.dialog_msg);
		mTextViewMsg.setText(resMsg);
		Button mConfirmButton = (Button) diaView.findViewById(R.id.btn_confirm);
		mConfirmButton.setOnClickListener(positiveListener);
		Button mCancelButton = (Button) diaView.findViewById(R.id.btn_cancel);
		mCancelButton.setVisibility(View.GONE);
		mDialog.show();
	}

	/***
	 * 销毁对话框
	 */
	public void dismiss() {
		mDialog.dismiss();
	}
	
}
