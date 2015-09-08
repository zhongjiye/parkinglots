package com.sunrise.epark.activity;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
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
/***
 * 
 *this class is used for...个人中心-钱包余额-充值
 * @wangyingjie  create
 *@time 2015年7月27日上午9:35:22
 */
public class NotecaseChongzhiActivity extends BaseActivity implements OnClickListener{
	private RelativeLayout back;
	private EditText moneyy;
	private RelativeLayout webmoney;
	private RelativeLayout zhifubao;
	private ImageView radiobtn;
	private ImageView radiobtn2;
	private Button button;
	private String type = "wx";//ｗｘ
	private String money;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notecase_chongzhi);
		initview();
		bindEvent();
	}

		/**初始化数据*/
	private void initview() {
		button = (Button) findViewById(R.id.confrim);
		button.setOnClickListener(this);
		back=(RelativeLayout)findViewById(R.id.back);
		moneyy=(EditText)findViewById(R.id.moneyy);
		moneyy.setFilters(new InputFilter[] { lengthfilter });
		webmoney=(RelativeLayout)findViewById(R.id.webmoney);
		zhifubao=(RelativeLayout)findViewById(R.id.zhifubao);
		radiobtn=(ImageView)findViewById(R.id.radiobtn);
		radiobtn2=(ImageView)findViewById(R.id.radiobtn2);
	}
	/**
	 * 发送请求
	 */
	private void SendRequest(final float f) {
		StringRequest register = new StringRequest(Method.POST,
				Urls.CHONGZHI_REQUEST, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.e("充值请求", response);
						try {
							JSONObject object = new JSONObject(response);
							if ("000".equals(object.getString("code"))) {
								StringBuffer orderId =new StringBuffer();
								orderId.append(object.getJSONObject("result").getString("orderId"));
										//object.getJSONObject("result").getString("orderId");
								//orderId += ("_0_"+(int)(f*100))+"_00";
								orderId.append(("_0_"+(int)(f*100)));
								orderId.append("_00");
								if(type.equals("al"))
								{
									//支付宝
									new PayMain(NotecaseChongzhiActivity.this, mHandler).pay("用户充值","用户充值",orderId.toString(),String.valueOf(f));
								}else{
									//微信
									WXPayMain wxPayMain = new WXPayMain(NotecaseChongzhiActivity.this);
									//
									wxPayMain.setOut_trade_no(orderId.toString());
									wxPayMain.setBody("用户充值");
									wxPayMain.setAttach("12");
									wxPayMain.pay((int)(f*100));
								}
								
							} else if ("001".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"查询不到数据");
							} else if ("002".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"超时");
							} else if ("003".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"请求数据不正确");
							} else if ("004".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"程序执行异常");
							} else if ("005".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"其他");
							} else if ("006".equals(object.getString("code"))) {
								DialogUtil.showToast(getApplicationContext(),
										"操作失败");
							} else if ("010".equals("code")) {
								DialogUtil.loginAgain(NotecaseChongzhiActivity.this);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						ToastUtil.showLong(NotecaseChongzhiActivity.this,
								VolleyErrorUtil.getMessage(error,
										NotecaseChongzhiActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("payType", "04");
				map.put("payLogo", "00");
				map.put("payAmount", String.valueOf(f));
				System.out.println(map.toString());
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);
	}
		/**绑定事件*/
	private void bindEvent() {
		back.setOnClickListener(this);
		button.setOnClickListener(this);
		webmoney.setOnClickListener(this);
		zhifubao.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View arg0) {
		 int v = arg0.getId();
		switch (v) {
		case R.id.back:
			finish();
			break;
		case R.id.webmoney:
			radiobtn.setBackgroundResource(R.drawable.btn_pay_click);
			radiobtn2.setBackgroundResource(R.drawable.btn_pay_normal);
			type = "wx";//微信支付
			break;
		case R.id.zhifubao:
			radiobtn.setBackgroundResource(R.drawable.btn_pay_normal);
			radiobtn2.setBackgroundResource(R.drawable.btn_pay_click);
			type = "al";//支付宝支付
			break;
		case R.id.confrim:
			String f = moneyy.getText().toString();
			if("".equals(f))
			{
				ToastUtil.showShort(getApplicationContext(), "请输入有效金额");
				break;
			}
			float ff = Float.parseFloat(f);
			if(ff == 0)
			{
				ToastUtil.showShort(getApplicationContext(), "请输入有效金额");
				break;
			}
			SendRequest(ff);
			//支付宝充值金额
			//String money = moneyy.getText().toString().trim();
			//new PayMain(NotecaseChongzhiActivity.this, mHandler).pay(money);
			//微信
			//new WXPayMain(NotecaseChongzhiActivity.this).pay(1);
			break;

		default:
			break;
		}
		
	}

	//充值后的回调
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
					showPaySuccessDialog("充值成功");
/*					Toast.makeText(NotecaseChongzhiActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();*/
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(NotecaseChongzhiActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						showPaySuccessDialog("充值失败");
						/*Toast.makeText(NotecaseChongzhiActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();*/

					}
				}
				break;
			}
			case Key.SDK_CHECK_FLAG: {
				Toast.makeText(NotecaseChongzhiActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};
	/** 输入框小数的位数*/
	private static final int DECIMAL_DIGITS = 2;                
/**
	 *  设置小数位数控制   
	 */
    InputFilter lengthfilter = new InputFilter() {   
        public CharSequence filter(CharSequence source, int start, int end,   
                Spanned dest, int dstart, int dend) {   
            // 删除等特殊字符，直接返回   
            if ("".equals(source.toString())) {   
                return null;   
            }   
            String dValue = dest.toString();   
            String[] splitArray = dValue.split("\\.");   
            if (splitArray.length > 1) {   
                String dotValue = splitArray[1];   
                int diff = dotValue.length() + 1 - DECIMAL_DIGITS;   
                if (diff > 0) {   
                    return source.subSequence(start, end - diff);   
                }   
            }   
            return null;   
        }   
    }; 
	
}
