package com.sunrise.epark.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.sunrise.epark.R;
import com.sunrise.epark.activity.SubscribeListActivity.SubscribeBean;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StatusUtil;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;
/***
 * 
 *this class is used for... 个人中心-停车记录
 * @wangyingjie  create
 *@time 2015年7月27日上午9:40:54
 */
public class StopCarRecordActivity extends BaseActivity {


		private PullToRefreshListView listview;
		private List<record> data;
		private QuickAdapter<record> qAdapter;

		private ImageView back;
		private ImageView no_stopcar_record;

		// 分页请求参数
		private int page = 1;
		private int size = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stop_car_record);
		initCom();
	}

	
	private void initCom()
	{
		back= (ImageView)findViewById(R.id.stop_record_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			finish();	
			}
		});
		
		data = new ArrayList<record>();
		listview = (PullToRefreshListView) findViewById(R.id.stop_list);
		no_stopcar_record=(ImageView)findViewById(R.id.no_stopcar_record);
		listview.setMode(Mode.PULL_FROM_END);
		ILoadingLayout endlabel = listview.getLoadingLayoutProxy(false, true);
		endlabel.setPullLabel(getResources().getString(R.string.load_more));
		endlabel.setRefreshingLabel(getResources().getString(R.string.loading));
		endlabel.setReleaseLabel(getResources().getString(R.string.preload));

		// 上拉 下拉 刷新
		listview.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
					getdata();
				}

			}
		});
		// item点击监听
		listview.setOnItemClickListener(new OnItemClickListener() {
			private Intent intent;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 点击停车详情
				record r = data.get(arg2-1);
				if ("02".equals(r.getState())) {
					 intent = new Intent(StopCarRecordActivity.this, StopCarDetailActivity.class);
				}else if ("04".equals(r.getState())) {
					
					 intent = new Intent(StopCarRecordActivity.this, StopcarDetailAwayPriceActivity.class);
					 intent.putExtra("startTime", r.getStartTime());
					 intent.putExtra("endTime", r.getEndTime());
					 intent.putExtra("money", r.getMoney());
					 intent.putExtra("name", r.getName());
					 intent.putExtra("add", r.getAdd());
				}
				intent.putExtra("id", r.getOrder());
				startActivity(intent);
			}
		});
		qAdapter = new QuickAdapter<record>(this,
				R.layout.activity_stop_car_record_item, data) {

			@Override
			protected void convert(BaseAdapterHelper v, final record arg1) {
				// TODO Auto-generated method stub
				v.setText(R.id.stop_item_name, arg1.getName());
				v.setText(R.id.stop_item_order, getString(R.string.order_num, arg1.getOrder()));
				v.setText(R.id.stop_item_add, arg1.getAdd());
				String status = arg1.getState();
				
				switch (status) {
				case "02":
					status = "进行中";
					break;

				case "04":
					status = "完成";
					break;

				default:
					break;
				}
				v.setText(R.id.stop_item_state, status);
			}
		};
		listview.setAdapter(qAdapter);
		getdata();
	}
	
	
	private void getdata() {
//		if(!StatusUtil.StatusCheck(StopCarRecordActivity.this))
//    	{
//			finish();
//			return;
//    	}
	//	showLoadingDialog("数据请求");
		StringRequest register = new StringRequest(Method.POST,
				Urls.STOP_LIST, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.e("停车列表", response);
						try {
							JSONObject jObject = new JSONObject(response);

							String code = jObject.getString("code");

							//switch (code) {
							if( "000".equals(code)){
								// 成功
								JSONArray results = jObject
										.getJSONArray("result");
								for (int i = 0; i < results.length(); i++) {
									JSONObject obj = results.getJSONObject(i);
									data.add(new record(obj.getString("parkName"), obj.getString("orderId"), obj.getString("parkAddr"), obj.getString("orderStatus"),obj.getString("startTime"),obj.getString("endTime"),obj.getString("orderAmount")));
								}
								if (results.length()==0) {
									no_stopcar_record.setVisibility(View.VISIBLE);
									listview.setVisibility(View.GONE);
								}else {
									no_stopcar_record.setVisibility(View.GONE);
									listview.setVisibility(View.VISIBLE);
									
								}
								qAdapter.replaceAll(data);
								qAdapter.notifyDataSetChanged();
								// 页面自增１
								page++;
							}
							if( "010".equals(code)){
								DialogUtil.loginAgain(StopCarRecordActivity.this);
							}
							
							

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							// 刷新完成
							listview.onRefreshComplete();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						showLoadingDialog("请求失败");
						ToastUtil.showLong(StopCarRecordActivity.this,
								VolleyErrorUtil.getMessage(error,
										StopCarRecordActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderType", "02");
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				map.put("page", String.valueOf(page));
				map.put("size", String.valueOf(size));
				map.put("type", "1");
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stop_car_record, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	class record{
		private String name;//名字
		private String order;//订单号
		private String add;//地址
		private String state;//状态
		private String startTime;//开始时间
		private String endTime;//结束时间
		private String money;//总结额
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getOrder() {
			return order;
		}
		public void setOrder(String order) {
			this.order = order;
		}
		public String getAdd() {
			return add;
		}
		public void setAdd(String add) {
			this.add = add;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public record(String name, String order, String add, String state,
				String startTime, String endTime, String money) {
			super();
			this.name = name;
			this.order = order;
			this.add = add;
			this.state = state;
			this.startTime = startTime;
			this.endTime = endTime;
			this.money = money;
		}
		public record() {
		}
		public String getStartTime() {
			return startTime;
		}
		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public String getMoney() {
			return money;
		}
		public void setMoney(String money) {
			this.money = money;
		}
		
		
	}
}
