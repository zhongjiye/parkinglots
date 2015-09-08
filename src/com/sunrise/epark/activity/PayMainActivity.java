package com.sunrise.epark.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;






import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sunrise.epark.R;
import com.sunrise.epark.alipay.Key;
import com.sunrise.epark.alipay.PayMain;
import com.sunrise.epark.alipay.PayResult;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;
import com.sunrise.epark.wxapi.WXPayMain;

import android.content.Intent;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PayMainActivity extends BaseActivity implements OnClickListener {

	private Button payButton;

	private TextView orderView;
	private TextView parkName;
	private TextView totleMoney;
	private TextView wellt_money;
	private TextView payMoney;
	private CheckBox useWellt; 
	private ImageView close;
	private RadioGroup group;
	
	private String orderid;
	private String pname;
	private String tmoney;
	private String type="00";//主单，子单
	private String mainId;

	private String payType = "al";//ｗｘ
	

	private double amount;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_main);
		Intent intent = getIntent();
		orderid = intent.getStringExtra("orderId");
		pname = intent.getStringExtra("name");
		tmoney = intent.getStringExtra("money");
		type = intent.getStringExtra("type");
		mainId=intent.getStringExtra("mainId");
		initCom();
	}

	private void initCom() {
		payButton = (Button) findViewById(R.id.pay_sure);
		payButton.setOnClickListener(this);

		parkName = (TextView) findViewById(R.id.pay_park_name);
		orderView = (TextView) findViewById(R.id.pay_orderid);
		totleMoney = (TextView) findViewById(R.id.pay_totle_money);
		wellt_money = (TextView) findViewById(R.id.wellt_money);
		payMoney = (TextView) findViewById(R.id.pay_money);
		close = (ImageView) findViewById(R.id.pay_close);
		close.setOnClickListener(this);
		if ("leave".equals(BaseApplication.className)) {
			close.setVisibility(View.GONE);
		}else if ("leave_buDan".equals(BaseApplication.className)) {
			close.setVisibility(View.GONE);
		}else{
			close.setVisibility(View.VISIBLE);
		}
		useWellt = (CheckBox)findViewById(R.id.use_wallet);
		useWellt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				// TODO Auto-generated method stub
				if(checked){
					wellt_money.setText(getString(R.string.how_yuan,amount));
					double pay = Double.parseDouble(tmoney) - amount;
					pay = pay<0?0:pay;
					payMoney.setText(getString(R.string.how_yuan,pay));
					
				}else{
					
					payMoney.setText(getString(R.string.how_yuan,tmoney));
				}
			}
		});
		group = (RadioGroup)findViewById(R.id.pay_way);
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				 //获取变更后的选中项的ID
				 int radioButtonId = arg0.getCheckedRadioButtonId();
				//根据ID获取RadioButton的实例
				if(radioButtonId == R.id.pay_alipay)
				{
					ToastUtil.show(getApplicationContext(), "支付宝");
					payType = "al";
				}else{
					ToastUtil.show(getApplicationContext(), "微信");
					payType = "wx";
				}
			}
		});
		
		
		parkName.setText(pname);
		totleMoney.setText(getString(R.string.totle_money,tmoney));
		orderView.setText(mainId);
		
		getYu();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		//showPayingDialog("支付中 请稍候");
		//pay();
		int id = arg0.getId();
		switch (id) {
		case R.id.pay_close:
			StopCarDetailActivity.countStatu=true;
			finish();
			
			break;
		case R.id.pay_sure:
			if(useWellt.isChecked())
			{

				double p = Double.parseDouble(tmoney) - amount;
				if(p<=0)
				{//使用余额
					welltPay(tmoney);
				}else{
					//余额和  （支付宝或微信）支付
					int jin = (int)p*100;//支付的金额
					int yue = (int)(amount*100);//用到的余额
					orderid += "_"+yue+"_"+jin+"_"+type;
					//网络支付的钱 为 p
					if(payType.equals("al"))
					{
						
						new PayMain(PayMainActivity.this, mHandler).pay(pname, "测试描述", orderid, String.format("%.2f", p));
					}else{
						int i = (int)(p*100);
						WXPayMain wxPayMain = new WXPayMain(PayMainActivity.this);
						//
						wxPayMain.setOut_trade_no(orderid);
						wxPayMain.setAttach(String.valueOf(amount));
						wxPayMain.setBody(pname);
						wxPayMain.pay(i);
					}
				}
			}else{
				//支付宝或微信
				if(payType.equals("al"))
				{
					new PayMain(PayMainActivity.this, mHandler).pay(pname, "测试描述", orderid, tmoney);
				}else{
					int i = Integer.parseInt(tmoney)*100;
					WXPayMain wxPayMain = new WXPayMain(PayMainActivity.this);
					//
					wxPayMain.setOut_trade_no(orderid);
					wxPayMain.setAttach("");
					wxPayMain.pay(i);
				}
			}
			break;
		default:
			break;
		}
				
	}

	private void getYu(){
		showLoadingDialog("请求中");
		StringRequest getAmount = new StringRequest(Method.POST, Urls.ACCOUNT_AMOUNT,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
                       LogUtil.d("获取余额", response.toString());
                       try {
						JSONObject object=new JSONObject(response);
						String code=object.getString("code");
						switch (code) {
						case "000":
							amount = Double.parseDouble(object.getString("result"));
							
							wellt_money.setText(getString(R.string.how_yuan,amount));
							double pay = Double.parseDouble(tmoney) - amount;
							pay = pay<0?0:pay;
							payMoney.setText(getString(R.string.how_yuan,pay));
							break;
						case "010":
							DialogUtil.loginAgain(PayMainActivity.this);
							break;
						
						default:
							showShortToast(object.getString("result"));
							break;
						}
						closeLoadingDialog();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						showLoadingDialog("请求失败");
						ToastUtil.showLong(PayMainActivity.this,
								VolleyErrorUtil.getMessage(error,
										PayMainActivity.this));

					}
				}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				return map;
			}
		};
		getAmount.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(getAmount);
	}
	/**
	 * 钱包支付
	 */
	private void welltPay(final String money)
	{
		showLoadingDialog("请求中");
		StringRequest getAmount = new StringRequest(Method.POST, Urls.WELLT_PAY,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
                       LogUtil.d("余额支付", response.toString());
                       try {
						JSONObject object=new JSONObject(response);
						String code=object.getString("code");
						closeLoadingDialog();
						switch (code) {
						
						case "000":
							//showPaySuccessDialog("支付成功");
							
							if ("leave".equals(BaseApplication.className)) {
								//正常出场
								Intent i=new Intent(PayMainActivity.this,StopCarRecordActivity.class);
								startActivity(i);
							}else if ("leave_buDan".equals(BaseApplication.className)) {
								//补单出场
								Intent i=new Intent(PayMainActivity.this,StopCarRecordActivity.class);
								startActivity(i);
							}else if ("delayPay".equals(BaseApplication.className)) {
								//延长付费
								Intent data = new Intent();
								Bundle bundle = new Bundle();
								bundle.putString("money", tmoney);
								bundle.putString("orderId", mainId);
								data.putExtras(bundle);
								setResult(113, data);
							}else if ("yuYue".equals(BaseApplication.className)) {
								//预约
								Intent i=new Intent(PayMainActivity.this,SubscribeListActivity.class);
								startActivity(i);
							}
							//new Thread.sleep(3000);//休息三秒
							//hidePayDialog();
							finish();
							break;
						case "010":
							DialogUtil.loginAgain(PayMainActivity.this);
							break;
						
						default:
							showShortToast(object.getString("result"));
							break;
						}
						closeLoadingDialog();
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						showLoadingDialog("请求失败");
						ToastUtil.showLong(PayMainActivity.this,
								VolleyErrorUtil.getMessage(error,
										PayMainActivity.this));

					}
				}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				map.put("orderId", orderid);
				map.put("payAmount", money);
				map.put("type", type);
				return map;
			}
		};
		getAmount.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(getAmount);
	}
	/**
	 * 支付
	 */
	private void pay() {
		StringRequest delayPay = new StringRequest(Method.POST, Urls.DELAY_PAY,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
                       LogUtil.d("延长续费", response.toString());
                       try {
						JSONObject object=new JSONObject(response);
						String code=object.getString("code");
						switch (code) {
						case "000":
							String result=object.getString("result");
							LogUtil.d("延长付费。。。", result);
							break;
						case "010":
							DialogUtil.loginAgain(PayMainActivity.this);
							break;
					
						default:
							showShortToast(object.getString("result"));
							break;
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						showLoadingDialog("请求失败");
						ToastUtil.showLong(PayMainActivity.this,
								VolleyErrorUtil.getMessage(error,
										PayMainActivity.this));

					}
				}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderId", orderid);
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				return map;
			}
		};
		delayPay.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(delayPay);
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Key.SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();
				
				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(PayMainActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
					Intent data = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("money", tmoney);
					data.putExtras(bundle);
					setResult(113, data);
					finish();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(PayMainActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(PayMainActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case Key.SDK_CHECK_FLAG: {
				Toast.makeText(PayMainActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};
}
