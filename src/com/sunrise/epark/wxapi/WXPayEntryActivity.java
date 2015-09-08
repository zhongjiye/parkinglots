package com.sunrise.epark.wxapi;



import com.sunrise.epark.R;
import com.sunrise.epark.activity.BaseActivity;
import com.sunrise.epark.activity.PayMainActivity;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.util.ToastUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		int code = resp.errCode;
		finish();
		if(code == 0)
		{//支付成功
			BaseApplication app = (BaseApplication) getApplication();
			app.finishActivity(PayMainActivity.class);
		}else if(code == -1){
			//失败
			ToastUtil.showShort(WXPayEntryActivity.this, "支付失败");
		}else if(code == -2){
			//用户取消
			ToastUtil.showShort(WXPayEntryActivity.this, "用户取消");
		}
	}
}