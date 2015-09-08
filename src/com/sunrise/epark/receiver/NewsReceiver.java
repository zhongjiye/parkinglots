package com.sunrise.epark.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

import com.sunrise.epark.activity.CarManageActivity;
import com.sunrise.epark.activity.CoverLayer;
import com.sunrise.epark.activity.LocationActivity;
import com.sunrise.epark.activity.PayMainActivity;
import com.sunrise.epark.activity.ShowActivity;
import com.sunrise.epark.activity.StopCarRecordActivity;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;

public class NewsReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			String action = intent.getAction();
			if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
				String content = bundle.getString("cn.jpush.android.ALERT");// 通知内容
				String extra = bundle.getString("cn.jpush.android.EXTRA");// 通知额外字段
				LogUtil.e("通知内容", "content:" + content + "--extra:" + extra);
			}
			if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
				JPushInterface.reportNotificationOpened(context,
						bundle.getString(JPushInterface.EXTRA_MSG_ID));
				Intent i = new Intent(context, ShowActivity.class);
				i.putExtras(bundle);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
			if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
				String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);// 通知内容
				LogUtil.e("自定义消息", content);
				BaseApplication app = (BaseApplication) context
						.getApplicationContext();
				// 结束覆盖层activity
				app.finishActivity(CoverLayer.class);
				String[] strs = content.split(",");
				String type = strs[0];
				switch (type) {
				case "01":// 进场
					// 跳转到停车记录
					LogUtil.d("....", "请进行现金支付");
					intent = new Intent(context, StopCarRecordActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					break;
				case "02":// 补单出场
					intent = new Intent(context, PayMainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("money", strs[1]);
					intent.putExtra("orderId", strs[2]);
					intent.putExtra("name", strs[3]);
					intent.putExtra("type", "00");//主单
					BaseApplication.className="leave_buDan";
					context.startActivity(intent);
					break;
				case "03":// 出场
					intent = new Intent(context, PayMainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("money", strs[1]);
					intent.putExtra("orderId", strs[2]);
					intent.putExtra("name", strs[3]);
					intent.putExtra("type", "00");//主单
					BaseApplication.className="leave";
					context.startActivity(intent);
					break;
				case "000":
					//DialogUtil.showDialog(context, "请进行现金支付", false);
					intent = new Intent(context,LocationActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					break;
				case "04"://支付成功
					//showPaySuccessDialog("支付成功");
					intent=new Intent(context,CarManageActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					break;
				default:
					break;
				}

				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
