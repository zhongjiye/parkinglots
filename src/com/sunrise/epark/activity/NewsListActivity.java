package com.sunrise.epark.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.News;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/**
 * 
* 描述:消息推送 
* @author zhongjy
* @date 2015年8月17日
* @version 1.0
 */
public class NewsListActivity extends BaseActivity implements OnClickListener,
		OnRefreshListener<ListView> {

	private PullToRefreshListView messageListView;
	private ArrayList<News> messageList;
	private QuickAdapter<News> messageAdapter;
	
	private ImageView no_message;
	/**返回*/
	private RelativeLayout back;
    /**请求数据量*/
	private int pageSize = 20;
	/**分页请求页码*/
	private int currentPage = 1;
	/**loading框*/
	private Dialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newslist);
		initview();
		initListView();
		bindEvent();
		if(!"exit".equals(UserInfo.loginStatus)){
			initData();
		}
	}

	/**
	 * 初始化组件
	 */
	private void initview() {
		loading = DialogUtil.createLoadingDialog(this, getString(R.string.loading));
		back = (RelativeLayout) findViewById(R.id.back);
		no_message=(ImageView)findViewById(R.id.no_message);
	}

	/**
	 * 初始化listview
	 */
	private void initListView() {
		messageListView = (PullToRefreshListView) findViewById(R.id.message_list);
		messageListView.setMode(Mode.PULL_FROM_START);
		ILoadingLayout endlabel = messageListView.getLoadingLayoutProxy(false,
				true);
		endlabel.setPullLabel(getString(R.string.load_more));
		endlabel.setRefreshingLabel(getString(R.string.loading));
		endlabel.setReleaseLabel(getString(R.string.preload));

		ILoadingLayout startlable = messageListView.getLoadingLayoutProxy(true,
				false);
		startlable.setPullLabel(getString(R.string.refresh));
		startlable.setRefreshingLabel(getString(R.string.refreshing));
		startlable.setReleaseLabel(getString(R.string.prerefresh));

	}

	/**
	 * 绑定事件
	 */
	private void bindEvent() {
		back.setOnClickListener(this);
		messageListView.setOnRefreshListener(this);
	}
	
	/**
	 * 初始化数据
	 */
	private void initData() {
		messageList = new ArrayList<News>();
		messageAdapter = new QuickAdapter<News>(this,R.layout.single_message, messageList) {
			@Override
			protected void convert(BaseAdapterHelper arg0, News bean) {
				arg0.setText(R.id.message_date, bean.getMessageDate());
				arg0.setText(R.id.messsage_type, bean.getMessageType());
				arg0.setText(R.id.message_content, bean.getMessageContent());
			}
		};
		messageListView.setAdapter(messageAdapter);
		GetMessage();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
			currentPage++;
			GetMessage();
		}
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
			String label = DateUtils.formatDateTime(NewsListActivity.this,
					System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
			currentPage=1;
			GetMessage();
		}
	}
	/**
	 * 获取消息列表
	 */
	private void GetMessage(){
		loading.show();
		StringRequest getMessage = new StringRequest(Method.POST,
				Urls.MESSAGE_LIST, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("消息列表:"+Urls.MESSAGE_LIST, response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							switch (code) {
							case "000":
								String result = object.getString("result");
								JSONArray array = new JSONArray(result);
								if (array.length() == 0) {
									messageListView.onRefreshComplete();
									messageListView.setMode(Mode.PULL_FROM_START);
									no_message.setVisibility(View.VISIBLE);
								} else {
									if (array.length() == pageSize) {
										messageListView.setMode(Mode.BOTH);
									}
									if(currentPage==1){
										messageList.clear();
									}
									for (int i = 0; i < array.length(); i++) {
										JSONObject temp = array.getJSONObject(i);
										String content=temp.getString("noticeContent");
										String type=temp.getString("noticeType");
										String time=temp.getString("creatTimeString");
										switch (type) {
										case "01":
											type=getString(R.string.system_message);
											break;
										}
										messageList.add(new News(time, content, type));
									}
									messageAdapter.replaceAll(messageList);
								}
								break;
							case "001":
								messageListView.setMode(Mode.PULL_FROM_START);
								messageList.clear();
								break;
							case "010":
								DialogUtil.loginAgain(NewsListActivity.this);
								break;
							default:
								break;
							}
						} catch (JSONException e) {
							messageListView.setMode(Mode.PULL_FROM_START);
							messageList.clear();
						}finally{
							if(loading.isShowing()){
								loading.dismiss();
							}
							messageAdapter.notifyDataSetChanged();
							messageListView.onRefreshComplete();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if(loading.isShowing()){
							loading.dismiss();
						}
						ToastUtil.showShort(NewsListActivity.this, VolleyErrorUtil.getMessage(error, NewsListActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("page", String.valueOf(currentPage));
				map.put("size", String.valueOf(pageSize));
				LogUtil.i("消息列表:"+Urls.MESSAGE_LIST, StringUtils.getMap(map));
				return map;
			}
		};
		getMessage.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(getMessage);
	}

}
