package com.sunrise.epark.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.method.DateTimeKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.Park;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.datedialog.DatePickerDialog;
import com.sunrise.epark.datedialog.DatePickerDialog.OnDateSetListener;
import com.sunrise.epark.datedialog.DateTimePickerDialog;
import com.sunrise.epark.datedialog.DateTimePickerDialog.OnDateTimeSetListener;
import com.sunrise.epark.util.DateTool;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;

/**
 * 预约界面
 * 
 * @author Rocky
 *
 */
public class SubscribeActivity extends BaseActivity implements OnClickListener {
	//套餐类型
	private int type = 0;//0 小时  1 天 2周   3 月
	
	private ImageView back;
	private ImageView sub1_parkimg;
	
	private TextView hour;
	private TextView day;
	private ViewPager viewPager;
	private View[] subscribeViews;

	private TextView hourStart;
	private TextView hourEnd;
	private TextView dayStart;
	
	public static final int RESULT_H_S = 1;
	public static final int RESULT_H_E = 2;
	public static final int RESULT_D_S = 3;
	public static final int RESULT_D_E = 4;
	
	private TextView dayTip;
	private Button subDay;
	private Button subWeek;
	private Button subMonth;
	
	
	private ImageView selDay;
	private ImageView selWeek;
	private ImageView selMonth;
	//向右的箭头
	private ImageView rightArrow;
	
	private TextView dayEndTime;
	// 请求队列
	private RequestQueue mQueue;
	//停车场id
	private String parkId;
	//停车场详情，显示
	private TextView parkName;
	private TextView parkType;
	private TextView parkAdd;
	private Button sure;
	//总价
	private TextView hMoney;
	private TextView dMoney;
	//单价
	private float h_price;//小时
	private float d_price;//tian
	private float w_price;//zhou
	private float m_price;//yue
	private float n_price;//正常一天的价格
	
	// private int[] prices = new int[4];//需要支付的单价
	private float[] prices = new float[4];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscribe);
     
		parkId = getIntent().getStringExtra("parkId");
		initCom();
		getInitDate();
	}

	private void initCom() {
		back = (ImageView)findViewById(R.id.sub_back);
		back.setOnClickListener(this);
		sub1_parkimg=(ImageView) findViewById(R.id.sub1_parkimg);
		
		hour = (TextView) findViewById(R.id.orderByHour);
		day = (TextView) findViewById(R.id.orderByDay);
		
		//停车场详情显示
		parkName = (TextView)findViewById(R.id.sub1_parkname);
		parkType = (TextView)findViewById(R.id.sub1_parktype);
		parkAdd = (TextView)findViewById(R.id.sub1_parkadd);
		sure = (Button)findViewById(R.id.sub1_sure);
		
		sure.setOnClickListener(this);
		hour.setOnClickListener(this);
		day.setOnClickListener(this);

		viewPager = (ViewPager) findViewById(R.id.sub_viewpager);
		subscribeViews = new View[2];
		subscribeViews[0] = View.inflate(this, R.layout.subscribe_hour, null);
		subscribeViews[1] = View.inflate(this, R.layout.subscribe_day, null);
		//总价
		hMoney = (TextView)subscribeViews[0].findViewById(R.id.hour_money);
		dMoney= (TextView)subscribeViews[1].findViewById(R.id.day_money);
		//车牌
		TextView carName = (TextView) subscribeViews[0].findViewById(R.id.hour_carName);
		TextView carName1 = (TextView) subscribeViews[1].findViewById(R.id.day_carName);
		carName.setText(UserInfo.carName);
		carName1.setText(UserInfo.carName);
		//时间选择器
		hourStart = (TextView) subscribeViews[0].findViewById(R.id.hour_start_time);
		hourEnd = (TextView) subscribeViews[0].findViewById(R.id.hour_end_time);
		
		dayStart = (TextView)subscribeViews[1].findViewById(R.id.day_start_time);
		dayTip = (TextView) subscribeViews[1].findViewById(R.id.sub_day_tip);
		subDay = (Button) subscribeViews[1].findViewById(R.id.sub_day_day);
		subWeek = (Button)subscribeViews[1]. findViewById(R.id.sub_day_week);
		subMonth = (Button)subscribeViews[1].findViewById(R.id.sub_day_month);
		
		selDay = (ImageView)subscribeViews[1].findViewById(R.id.sub_day_1);
		selWeek = (ImageView)subscribeViews[1].findViewById(R.id.sub_day_2);
		selMonth = (ImageView)subscribeViews[1].findViewById(R.id.sub_day_3);
		//向右的箭头
		rightArrow = (ImageView)subscribeViews[1].findViewById(R.id.day_end_img);
		dayEndTime = (TextView)subscribeViews[1].findViewById(R.id.day_end_time);
		
		//天套餐可以选结束时间
		dayEndTime.setOnClickListener(this);
		
		
		subDay.setOnClickListener(this);
		subWeek.setOnClickListener(this);
		subMonth.setOnClickListener(this);
		
		hourStart.setOnClickListener(this);
		hourEnd.setOnClickListener(this);
		dayStart.setOnClickListener(this);

		viewPager.setAdapter(new MyPageApdaper(subscribeViews));
		// 监听滑动
		viewPager.setOnPageChangeListener(pageListener);
		viewPager.setCurrentItem(0);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
		switch (id) {
		case R.id.sub_back:
			finish();
			break;
		case R.id.orderByDay:
			// viewpage 改变
			viewPager.setCurrentItem(1);
			type = 1;
			break;
		case R.id.orderByHour:
			// viewpage 改变
			viewPager.setCurrentItem(0);
			type = 0;
			break;
		case R.id.hour_start_time://按小时 开始时间
			showTimeDialog(id);
//			Intent intent = new Intent(getApplicationContext(), TimePicker.class);
//			intent.putExtra("type", "hour");
//			startActivityForResult(intent,RESULT_H_S);
			break;
		case R.id.hour_end_time://按小时 结束时间
			if("".equals(hourStart.getText().toString()))
			{
				ToastUtil.show(getApplicationContext(), "请选择开始时间");
				break;
			}
			showTimeDialog(id);
//			Intent intent1 = new Intent(getApplicationContext(), TimePicker.class);
//			intent1.putExtra("type", "hour");
//			startActivityForResult(intent1, RESULT_H_E);
			break;
		case R.id.day_start_time://按天 开始时间
			showDateDialog(id);
			//startActivityForResult(new Intent(getApplicationContext(), TimePicker.class), RESULT_D_S);
			break;
		case R.id.sub_day_day://选择天套餐
			dayTip.setText(getString(R.string.sub_day_tip, d_price,d_price*1));
			selDay.setVisibility(View.VISIBLE);
			selWeek.setVisibility(View.INVISIBLE);
			selMonth.setVisibility(View.INVISIBLE);
			//向右箭头显示
			rightArrow.setVisibility(View.VISIBLE);
			type = 1;
			//suanqian
			getDayMoney();
			break;
		case R.id.sub_day_week://选择周套餐
			dayTip.setText(getString(R.string.sub_day_tip, w_price,w_price*7));
			selDay.setVisibility(View.INVISIBLE);
			selWeek.setVisibility(View.VISIBLE);
			selMonth.setVisibility(View.INVISIBLE);
			//向右箭头显示
			rightArrow.setVisibility(View.INVISIBLE);
			
			type = 2;
			//如果开始时间不为空自动填充结束时间
			if(!"".equals(dayStart.getText())){
				fillEndDate(dayStart.getText().toString());
			}
			//suanqian
			getDayMoney();
			break;
		case R.id.sub_day_month://选择月套餐
			dayTip.setText(getString(R.string.sub_day_tip, m_price,m_price*30));
			selDay.setVisibility(View.INVISIBLE);
			selWeek.setVisibility(View.INVISIBLE);
			selMonth.setVisibility(View.VISIBLE);
			//向右箭头显示
			rightArrow.setVisibility(View.INVISIBLE);
			
			type = 3;
			//如果开始时间不为空自动填充结束时间
			if(!"".equals(dayStart.getText())){
				fillEndDate(dayStart.getText().toString());
			}
			//suanqian
			getDayMoney();
			break;
		case R.id.sub1_sure://确认预约
			if(valite())
			{
				submit();
			}
			break;
			
		case R.id.day_end_time://结束时间
			if(type==1)
			{
				if("".equals(dayStart.getText().toString()))
				{
					ToastUtil.show(getApplicationContext(), "请选择开始时间");
					break;
				}
				showDateDialog(id);
				//天套餐
				//startActivityForResult(new Intent(getApplicationContext(), TimePicker.class), RESULT_D_E);
			}
			break;
		default:
			break;
		}
	}

	
	
	//获取停车场详情
	private void getInitDate()
	{
		showLoadingDialog();
		mQueue = Volley.newRequestQueue(getApplicationContext());
		StringRequest register = new StringRequest(Method.POST,
				Urls.STOPCAR_MINGXI, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeLoadingDialog();
						LogUtil.e("预约获取停车场明细", response);

						try {
							JSONObject jObject = new JSONObject(response);

							String code = jObject.getString("code");
							if ("000".equals(code.trim())) {
								//成功
								JSONArray resultpackage = jObject.getJSONArray("resultPackage");
								String dayprice = jObject.getJSONObject("result").getString("parkDayPrice");
								n_price = Float.parseFloat(dayprice.equals("")?"0":dayprice);
								for (int i = 0; i < resultpackage.length(); i++) {
									JSONObject object = resultpackage.getJSONObject(i);
									String type = object.getString("packageType");
									String price = object.getString("packagePrice");
									switch (type) {
									case "00"://小时套餐
										h_price = Float.parseFloat(price);
										prices[0] = h_price;
										break;
									case "01"://天套餐
										d_price =n_price-Float.parseFloat(price);
										d_price = d_price<0?0:d_price;
										dayTip.setText(getString(R.string.sub_day_tip, d_price+"","0"));
										prices[1] = Float.parseFloat(price);
										break;
									case "02"://周套餐
										w_price = n_price - Float.parseFloat(price)/7;
										w_price = w_price<0?0:w_price;
										prices[2] = Float.parseFloat(price);
										break;
									case "03"://月套餐
										m_price = n_price - Float.parseFloat(price)/30;
										m_price = m_price<0?0:m_price;
										prices[3] = Float.parseFloat(price);
										break;
									default:
										break;
									}
								}
								JSONObject result = jObject.getJSONObject("result");
								//改变显示
								parkAdd.setText(result.getString("parkAddr"));
								parkName.setText(result.getString("parkName"));
								String type = result.getString("parkType");
								if(type.equals("00"))
								{
									parkType.setText(getString(R.string.parkType_shinei));
								}else{
									parkType.setText(getString(R.string.parkType_shiwai));
								}
								//sub1_parkimg
								String imgId = result.getString("imgUrl");
								if (StringUtils.isNotNull(imgId)) {
									@SuppressWarnings("deprecation")
									ImageRequest imageRequest = new ImageRequest(
											imgId,
											new Response.Listener<Bitmap>() {
												@Override
												public void onResponse(
														Bitmap response) {
													sub1_parkimg.setImageBitmap(response);
												}
											}, 0, 0, Config.RGB_565,
											new Response.ErrorListener() {
												@Override
												public void onErrorResponse(
														VolleyError error) {
												}
											});
									BaseApplication.mQueue.add(imageRequest);
								}

							} else if("010".equals(code.trim())) {
								DialogUtil.loginAgain(SubscribeActivity.this);
							} else{
								// 失败
								showLoadingDialog("获取失败");
								
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							// 失败
							showLoadingDialog("获取失败");
						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						closeLoadingDialog();

						showLoadingDialog(error.getMessage());
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				map.put("parkId", parkId);
				return map;
			}
		};
		mQueue.add(register);
	
	}
	/**
	 * 提交预约
	 */
	private void submit()
	{


		mQueue = Volley.newRequestQueue(getApplicationContext());
		StringRequest register = new StringRequest(Method.POST,
				Urls.MAKEAPPOINTMENT, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						closeLoadingDialog();
						LogUtil.e("提交预约", response);

						try {
							JSONObject jObject = new JSONObject(response);

							String code = jObject.getString("code");
							if ("000".equals(code.trim())) {
								JSONObject obj = jObject.getJSONObject("result");
								String orderId = obj.getString("orderId");
								String paymoney = obj.getString("orderAmount");
								String title = parkName.getText().toString();
								Intent intent = new Intent(SubscribeActivity.this, PayMainActivity.class);
								 intent.putExtra("orderId",orderId);
								 intent.putExtra("money",paymoney);
								 intent.putExtra("name",title);
								 startActivity(intent);
							}else if("010".equals(code.trim())) {
								DialogUtil.loginAgain(SubscribeActivity.this);
							}else{
								// 失败
								showLoadingDialog("获取失败");
								
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							// 失败
							showLoadingDialog("获取失败");
						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						closeLoadingDialog();

						showLoadingDialog(error.getMessage());
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				getParam(map);
				return map;
			}
		};
		mQueue.add(register);
	
	
	}
	private void getParam(Map<String, String> map)
	{
		String hStart = hourStart.getText().toString();
		String hEnd = hourEnd.getText().toString();
		String dStart = dayStart.getText().toString();
		String dEnd = dayEndTime.getText().toString();
		
		map.put("uuid", UserInfo.uuid);
		map.put("userId", UserInfo.userId);
		map.put("parkId", parkId);
		map.put("carName", UserInfo.carName);
		switch (type) {
		case 0://小时套餐
		
			map.put("startTime", hStart);
			map.put("endTime", hEnd);
			map.put("diffVal", "1");
			map.put("price", prices[0]+"");
			map.put("packageType", "00");
			break;
		case 1:
			
			map.put("startTime", dStart);
			map.put("endTime", dEnd);
			map.put("diffVal", "1");
			map.put("price", dMoney.getText().toString().split("元")[0]);
			map.put("packageType", "01");
			break;

		case 2:
			
			map.put("startTime", dStart);
			map.put("endTime", dEnd);
			map.put("diffVal", "1");
			map.put("price", prices[2]+"");
			map.put("packageType", "02");
			break;

		case 3:
			
			map.put("startTime", dStart);
			map.put("endTime", dEnd);
			map.put("diffVal", "1");
			map.put("price", prices[3]+"");
			map.put("packageType", "03");
			break;

		default:
			break;
		}
		
	}
	/**
	 * 验证是否填写开始结束时间
	 * @return
	 */
	private boolean valite()
	{
		boolean f =true ;
		String hStart = hourStart.getText().toString();
		String hEnd = hourEnd.getText().toString();
		String dStart = dayStart.getText().toString();
		String dEnd = dayEndTime.getText().toString();
		if(type ==0){
			if(hStart.equals(""))
			{
				ToastUtil.show(getApplicationContext(), "请选择开始时间");
				 f = false;
				 
			}
			if(hEnd.equals(""))
			{
				ToastUtil.show(getApplicationContext(), "请选择结束时间");
				f = false;
				
			}
			if ("0.0元".equals(hMoney.getText().toString())) {
				ToastUtil.show(getApplicationContext(), "没有此套餐，不能进行预约!");
				f = false;
			}
		}else{
			if(dStart.equals(""))
			{
				ToastUtil.show(getApplicationContext(), "请选择开始时间");
				 f = false;
			}
			if(dEnd.equals(""))
			{
				ToastUtil.show(getApplicationContext(), "请选择结束时间");
				 f = false;
			}
			if ("0.0元".equals(dMoney.getText().toString())) {
				ToastUtil.show(getApplicationContext(), "没有此套餐，不能进行预约!");
				f = false;
			}
		}
		return f;
	}
	// 点击“按天”改变样式
	private void changeTabStyleDay() {
		hour.setTextAppearance(getApplicationContext(),
				R.style.left_item_uncheck);
		day.setTextAppearance(getApplicationContext(), R.style.right_item_check);
		hour.setBackground(getResources().getDrawable(
				R.drawable.button_round_left));
		day.setBackground(getResources().getDrawable(
				R.drawable.button_round_right_check));
	}

	// 点击“按小时”改变样式
	private void changeTabStyleHour() {
		hour.setTextAppearance(getApplicationContext(), R.style.left_item_check);
		day.setTextAppearance(getApplicationContext(),
				R.style.right_item_uncheck);
		hour.setBackground(getResources().getDrawable(
				R.drawable.button_round_left_check));
		day.setBackground(getResources().getDrawable(
				R.drawable.button_round_right));
	}
	/**
	 * 
	 * @param data
	 */
	private void fillEndDate(String data)
	{
		//如果是按照周、月套餐自动填充结束时间
		if(type==2)//周
		{
			
			dayEndTime.setText(DateTool.GetDelayDate(data, 6));
		}else if(type==3)//月
		{
			dayEndTime.setText(DateTool.GetDelayDate(data, 29));
		}
	}
	
	private void getDayMoney()
	{
		boolean f = false; //是否为空
		String end = dayEndTime.getText().toString();
		if("".equals(end))
		{
			f = true;
		}
		if(type == 1)
		{
			if(!f)
			{
				Date startT = DateTool.string2date(dayStart.getText().toString()); 
				Date endT = DateTool.string2date(dayEndTime.getText().toString());
				float tp =d_price*DateTool.TianCha(startT, endT);
				dMoney.setText(getString(R.string.how_yuan,tp));
				dayTip.setText(getString(R.string.sub_day_tip, d_price,tp));
			}
			
		}else if(type==2)
		{
			dMoney.setText(getString(R.string.how_yuan,prices[2]));
		}else if(type ==3)
		{
			dMoney.setText(getString(R.string.how_yuan,prices[3]));
		}
	}
	/**
	 * viewpage适配器
	 * 
	 * @author Rocky
	 *
	 */
	class MyPageApdaper extends PagerAdapter {

		private View[] list;

		public MyPageApdaper(View[] list) {
			// TODO Auto-generated constructor stub
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.length;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			View v = list[position];
			((ViewPager) container).addView(v, 0);
			return v;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

	}

	private OnPageChangeListener pageListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			switch (arg0) {
			case 0:
				changeTabStyleHour();
				break;
			case 1:
				changeTabStyleDay();
				break;

			default:
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};
	/**
	 * 日期
	 * 显示选择对话狂
	 */
	public void showDateDialog(final int id)
	{
		DatePickerDialog dialog  = new DatePickerDialog(this, System.currentTimeMillis());
		dialog.setOnDateSetListener(new OnDateSetListener()
	      {
			public void OnDateTimeSet(AlertDialog dialog, long date)
			{
				String str = DateTool.getStringDate(date);
				switch (id) {
				case R.id.day_start_time://按天开始
					Date startd = DateTool.string2date(str);
					if(!DateTool.beyondTody(startd))//判断开始时间是否为明天
					{
						ToastUtil.show(getApplicationContext(), "开始时间必须大于今天");
						
					}else{
						dayStart.setText(str);
						if(type ==1)
						{
							dayEndTime.setText("");
						}
					}
					
					fillEndDate(dayStart.getText().toString());
					break;
				case R.id.day_end_time://按天结束
					//判断结束时间是否小于开始时间
					Date startT = DateTool.string2date(dayStart.getText().toString()); 
					Date endT = DateTool.string2date(str);
					
					if(endT.before(startT) || dayStart.getText().toString().equals(str)){
						
						ToastUtil.show(getApplicationContext(), "结束时间不可小于开始时间");
					}else{
						int tian = DateTool.TianCha(startT, endT);
						if(tian>30)
						{
							ToastUtil.show(getApplicationContext(), "天套餐最长不能超过30天");
							break;
						}
						dayEndTime.setText(str);
						dayTip.setText(getString(R.string.sub_day_tip, d_price,d_price*tian));
						//自动算价格
						 dMoney.setText(getString(R.string.how_yuan,prices[1]*tian));
					}
					break;
				default:
					break;
				}
				
			}
		});
		dialog.show();
	}
	/**
	 * 日期
	 * 显示选择对话狂
	 */
	public void showTimeDialog(final int v)
	{
		DateTimePickerDialog dialog  = new DateTimePickerDialog(this, System.currentTimeMillis());
		dialog.setOnDateTimeSetListener(new OnDateTimeSetListener()
	      {
			public void OnDateTimeSet(AlertDialog dialog, long date)
			{
				String str = DateTool.getStringTime(date);
				switch (v) {
				case R.id.hour_start_time://按小时开始
					Date start = DateTool.string2date(str);
					if(!DateTool.beyondTody(start))//判断开始时间是否为明天
					{
						ToastUtil.show(getApplicationContext(), "开始时间必须大于今天");
						
					}else{
						hourStart.setText(str);
					}
					break;
				case R.id.hour_end_time://按小时结束
					hourEnd.setText(str);
					//判断结束时间是否小于开始时间
					Date startDate = DateTool.string2date(hourStart.getText().toString()); 
					Date endDate = DateTool.string2date(hourEnd.getText().toString());
					
					if(endDate.before(startDate) || hourStart.getText().toString().equals(hourEnd.getText().toString())){
						
						ToastUtil.show(getApplicationContext(), "结束时间不可小于开始时间");
						hourEnd.setText("");
					}else{
						int cha =  DateTool.XiaoShiCha(startDate, endDate);
						if(cha>30*24)
						{
							ToastUtil.show(getApplicationContext(), "预约不能超过30天");
							hourEnd.setText("");
							break;
						}
						float f = h_price * cha;
						prices[0] = f;
						hMoney.setText(getString(R.string.how_yuan,f+""));
					}
					break;
				default:
					break;
				}
				
			}
		});
		dialog.show();
	}

}
