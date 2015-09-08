package com.sunrise.epark.wxapi;

import java.io.*;
import java.util.*;

import android.util.Xml;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.ToastUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.xmlpull.v1.XmlPullParser;


public class WXPayMain {

	private static final String TAG = "MicroMsg.SDKSample.PayActivity";

	private PayReq req;
	private Activity activity;
	private int money;//充值金额　最小值为１　　单位为分   最小为一分钱
	final IWXAPI msgApi;
	Map<String,String> resultunifiedorder;
	StringBuffer sb;
	
	private String out_trade_no;//停车单号
	private String attach;//附加字段
	private String body = "weixin";
	
	public WXPayMain(Activity activity)
	{
		msgApi = WXAPIFactory.createWXAPI(activity, null);
		req = new PayReq();
		sb=new StringBuffer();

		this.activity = activity;
		msgApi.registerApp(Constants.APP_ID);
	}
	
	public void pay(int money){
		this.money = money;
		//生成订单
		GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
		getPrepayId.execute();
	}

	/**
	 生成签名
	 */

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);
		
		System.out.println(sb.toString());
		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion",packageSign);
		return packageSign;
	}
	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

        this.sb.append("sign str\n"+sb.toString()+"\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion",appSign);
		return appSign;
	}
	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<"+params.get(i).getName()+">");


			sb.append(params.get(i).getValue());
			sb.append("</"+params.get(i).getName()+">");
		}
		sb.append("</xml>");

		Log.e("orion",sb.toString());
		
		try {
			return new String(sb.toString().getBytes(), "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String,String>> {

		private ProgressDialog dialog;


		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(activity, "提示", "请稍候····");
		}

		@Override
		protected void onPostExecute(Map<String,String> result) {
			if(result !=null)
			{
				if (dialog != null) {
					dialog.dismiss();
				}
				sb.append("prepay_id\n"+result.get("prepay_id")+"\n\n");
	
				resultunifiedorder=result;
				System.out.println("调起支付....");
				//调起支付
				sendPayReq();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String,String>  doInBackground(Void... params) {

			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs();

			Log.e("orion",entity);

			byte[] buf = Util.httpPost(url, entity);

			if(buf != null)
			{
				String content = new String(buf);
				Log.e("orion", content);
				Map<String,String> xml=decodeXml(content);
				return xml;
			}else{
				Log.e("WXPayMain", "生成订单失败");
				return null;
			}
			
		}
	}



	public Map<String,String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName=parser.getName();
				switch (event) {
					case XmlPullParser.START_DOCUMENT:

						break;
					case XmlPullParser.START_TAG:

						if("xml".equals(nodeName)==false){
							//实例化student对象
							xml.put(nodeName,parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion",e.toString());
		}
		return null;

	}


	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
	
	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}
	


	

   //
	private String genProductArgs() {
		//参数详细解释：https://pay.weixin.qq.com/wiki/doc/api/app.php?chapter=9_1
		
		StringBuffer xml = new StringBuffer();
		try {
			String	nonceStr = genNonceStr();

			xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
     
            packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
            packageParams.add(new BasicNameValuePair("attach", attach));
            packageParams.add(new BasicNameValuePair("body", body));
			packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url", Urls.WX_RESPONSE));
			packageParams.add(new BasicNameValuePair("out_trade_no",out_trade_no));
			packageParams.add(new BasicNameValuePair("spbill_create_ip","127.0.0.1"));
			packageParams.add(new BasicNameValuePair("total_fee", String.valueOf(money)));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));
			
			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring =toXml(packageParams);

			return xmlstring;

		} catch (Exception e) {
			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}
		

	}
	private void genPayReq() {

		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());


		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		sb.append("sign\n"+req.sign+"\n\n");


		Log.e("orion", signParams.toString());

	}
	private void sendPayReq() {
		
		//调用支付之前，要做签名和参数处理
		genPayReq();
		
		//调用支付
		msgApi.registerApp(Constants.APP_ID);
		msgApi.sendReq(req);
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}




}

