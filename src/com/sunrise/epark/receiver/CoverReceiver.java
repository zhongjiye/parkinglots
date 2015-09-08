package com.sunrise.epark.receiver;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.util.ac;

import com.sunrise.epark.activity.CoverLayer;
import com.sunrise.epark.activity.StopCarRecordActivity;
import com.sunrise.epark.app.BaseApplication;

public class CoverReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent intent) {
		// TODO Auto-generated method stub
		
		
		String action = intent.getAction();
		if ("com.cover.receiver".equals(action) ) { //弹出遮罩
			Bundle bundle = intent.getExtras();
			intent = new Intent(arg0,CoverLayer.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtras(bundle);
			arg0.startActivity(intent);
		}

	}
}
