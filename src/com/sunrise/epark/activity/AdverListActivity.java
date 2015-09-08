package com.sunrise.epark.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.joanzapata.android.MultiItemTypeSupport;
import com.joanzapata.android.QuickAdapter;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.Adver;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StringUtils;

/**
 * 
 * this class is used for...个人中心-商城推广
 * 
 * @wangyingjie create
 * @time 2015年7月29日下午11:24:00
 */
public class AdverListActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener,OnRefreshListener<ListView> {
	/**
	 * 返回
	 */
	private RelativeLayout back;
	private PullToRefreshListView adverListView;
	private ArrayList<Adver> adverList;
	private QuickAdapter<Adver> adverAdapter;
	private Dialog loading;
	private int currentPage = 1;
	private int pageSize = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adverlist);
		initView();
		initListView();
		bindEvent();
		if(!"exit".equals(UserInfo.loginStatus)){
			initData();
		}
	}

	private void initView() {
		back = (RelativeLayout) findViewById(R.id.back);
		loading = DialogUtil.createLoadingDialog(this,
				getString(R.string.loading));
	}

	private void initListView() {
		adverListView = (PullToRefreshListView) findViewById(R.id.adver_listview);
		adverListView.setMode(Mode.PULL_FROM_START);
		ILoadingLayout endlabel = adverListView.getLoadingLayoutProxy(false,
				true);
		ILoadingLayout startlable = adverListView.getLoadingLayoutProxy(true,
				false);
		endlabel.setPullLabel(getString(R.string.load_more));
		endlabel.setRefreshingLabel(getString(R.string.loading));
		endlabel.setReleaseLabel(getString(R.string.preload));
		startlable.setPullLabel(getString(R.string.refresh));
		startlable.setRefreshingLabel(getString(R.string.refreshing));
		startlable.setReleaseLabel(getString(R.string.prerefresh));
	}

	private void bindEvent() {
		back.setOnClickListener(this);
		adverListView.setOnItemClickListener(this);
		adverListView.setOnRefreshListener(this);
	}

	private void initData() {
		adverList = new ArrayList<Adver>();
		MultiItemTypeSupport<Adver> multiItem = new MultiItemTypeSupport<Adver>() {
			@Override
			public int getLayoutId(int position, Adver adver) {
				if (adver.isAdverFlag()) {
					return R.layout.item_adver_first;
				}
				return R.layout.item_adver;
			}
			@Override
			public int getViewTypeCount() {
				return 2;
			}
			@Override
			public int getItemViewType(int arg0, Adver arg1) {
				if(arg1.isAdverFlag()){
					return 1;
				}else{
					return 2;
				}
			}
		};
		adverAdapter = new QuickAdapter<Adver>(
				AdverListActivity.this, adverList,
				multiItem) {
			@Override
			protected void convert(BaseAdapterHelper helper, Adver item) {
				helper.setText(R.id.adver_content, item.getAdverCon());
				helper.setImageUrl(R.id.adver_img, item.getAdverImg());
			}
		};
		adverListView.setAdapter(adverAdapter);
		getAdverList();
	}
	
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
			currentPage++;
			getAdverList();
		}
		if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
			String label = DateUtils.formatDateTime(AdverListActivity.this,
					System.currentTimeMillis(),
					DateUtils.FORMAT_SHOW_TIME
							| DateUtils.FORMAT_SHOW_DATE
							| DateUtils.FORMAT_ABBREV_ALL);
			refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(
					label);
			currentPage = 1;
			getAdverList();
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Bundle bundle = new Bundle();
		bundle.putString("url", adverList.get(position-1).getAdverUrl());
		Intent intent = new Intent(this,AdverDetailActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	private void getAdverList() {
		loading.show();
		StringRequest adverlist = new StringRequest(Method.POST,
				Urls.ADVER_LIST, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("停车推广列表:"+Urls.ADVER_LIST, response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							if ("000".equals(code)) {
								String result=object.getString("result");
								JSONArray array=new JSONArray(result);
								if(currentPage==1){
									adverList.clear();
								}
								if(array.length()==0){
									adverAdapter.replaceAll(adverList);
									adverAdapter.notifyDataSetChanged();
									adverListView.onRefreshComplete();
								}else{
									if(array.length()==pageSize){
										adverListView.setMode(Mode.BOTH);
									}
									for(int i=0;i<array.length();i++){
										JSONObject temp=array.getJSONObject(i);
										if(currentPage==1&&i==0){
											adverList.add(new Adver(true, temp.getString("imgUrl"), temp.getString("advName"), temp.getString("advUrl")));
										}else{
											adverList.add(new Adver(false, temp.getString("imgUrl"), temp.getString("advName"), temp.getString("advUrl")));
										}
									}
								}
							}
							if("010".equals(code)){
								DialogUtil.loginAgain(AdverListActivity.this);
							}
						} catch (JSONException e) {
						}finally{
							if(loading!=null&&loading.isShowing()){
								loading.dismiss();
							}
							adverAdapter.replaceAll(adverList);
							adverAdapter.notifyDataSetChanged();
							adverListView.onRefreshComplete();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if(loading!=null&&loading.isShowing()){
							loading.dismiss();
						}
						adverAdapter.replaceAll(adverList);
						adverAdapter.notifyDataSetChanged();
						adverListView.onRefreshComplete();
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("page", String.valueOf(currentPage));
				map.put("size", String.valueOf(pageSize));
				LogUtil.i("停车推广列表"+Urls.ADVER_LIST, StringUtils.getMap(map));
				return map;
			}
		};
		adverlist.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut, Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(adverlist);
	}

	
}
