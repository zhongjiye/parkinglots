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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.sunrise.epark.R;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StatusUtil;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/**
 * 套餐选配
 * 
 * @author 
 *
 */
public class PackageSelectionActivity extends BaseActivity implements
		OnClickListener {


	private PullToRefreshListView listview;
	private ImageView back;
	
	private List<String> data;
	private QuickAdapter<String> qAdapter;
	// 分页请求参数
	private int page = 1;
	private int size = 2;

	private JSONArray results;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.package_selection);
		initCom();
	}

	private void initCom() {
		back = (ImageView) findViewById(R.id.package_back);
		back.setOnClickListener(this);
		
		data = new ArrayList<String>();
		listview = (PullToRefreshListView) findViewById(R.id.sel_list);
		listview.setMode(Mode.PULL_FROM_END);
		ILoadingLayout endlabel = listview.getLoadingLayoutProxy(false, true);
		endlabel.setPullLabel(getResources().getString(R.string.load_more));
		endlabel.setRefreshingLabel(getResources().getString(R.string.loading));
		endlabel.setReleaseLabel(getResources().getString(R.string.preload));

		qAdapter = new QuickAdapter<String>(this,
				R.layout.sel_item, data) {

			@Override
			protected void convert(BaseAdapterHelper v, final String arg1) {
				// TODO Auto-generated method stub
				v.setText(R.id.sel_item_text,arg1);
			
			}
		};
		listview.setAdapter(qAdapter);
		
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
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				try {
					JSONObject obj = results.getJSONObject((int)arg3);
					String intentStr = obj.toString();
					Intent intent = new Intent(PackageSelectionActivity.this, PackageSelDetailActivity.class);
					intent.putExtra("data", intentStr);
					startActivity(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		getdata();
	}

	@Override
	public void onClick(View arg0) {

		int id = arg0.getId();
		switch (id) {
		case R.id.sub_list_back:
			finish();
			break;
		case R.id.package_back:
			finish();
			break;
		default:
			break;
		}
	}

	private void getdata() {
//		if(!StatusUtil.StatusCheck(PackageSelectionActivity.this))
//    	{
//			finish();
//			return;
//    	}
		StringRequest register = new StringRequest(Method.POST,
				Urls.PACKAGE_SELECTION, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.e("套餐选配列表", response);
						try {
							JSONObject jObject = new JSONObject(response);

							String code = jObject.getString("code");

							switch (code) {
							case "000":
								// 成功
								results = jObject.getJSONArray("result");
								for (int i = 0; i < results.length(); i++) {
									JSONObject obj = results.getJSONObject(i);
									data.add(obj.getString("packageName"));
									
								}
								qAdapter.replaceAll(data);
								qAdapter.notifyDataSetChanged();
								// 页面自增１
								page++;
								break;
							case "001":
								showShortToast(jObject.getString("result"));
								break;
							case "002":
								showShortToast(jObject.getString("result"));
								break;
							case "003":
								showShortToast(jObject.getString("result"));
								break;
							case "004":
								showShortToast(jObject.getString("result"));
								break;
							case "010":
								DialogUtil.loginAgain(PackageSelectionActivity.this);
								break;
							default:
								showShortToast(jObject.getString("result"));
								break;
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
						ToastUtil.showLong(PackageSelectionActivity.this,
								VolleyErrorUtil.getMessage(error,
										PackageSelectionActivity.this));
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("uuid", UserInfo.uuid);
				map.put("userId", UserInfo.userId);
				map.put("page", String.valueOf(page));
				map.put("size", String.valueOf(size));
				
				return map;
			}
		};
		register.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(register);

	}
	/**
	 * 自定义
	 * 
	 * @author mwj
	 * 
	 *
	 */
//	class MyAdapter extends BaseExpandableListAdapter {
//
//		private List<String[]> str_child_items_;
//		private List<String> str_group_items_;
//		LinearLayout mGroupLayout;
//
//		public MyAdapter(List<String> str_group_items_, List<String[]> str_child_items) {
//			this.str_child_items_ = str_child_items;
//			this.str_group_items_ = str_group_items_;
//		}
//
//		/**
//		 * 	str_group_items_ = new ArrayList<String>();
//		
//		
//		str_child_items_ = new ArrayList<String[]>();
//		getdata();
//		myadapter = new MyAdapter(str_group_items_, str_child_items_);
//		expandableListView = listview.getRefreshableView();
//		expandableListView.setGroupIndicator(null);
//		expandableListView.setAdapter(myadapter);
//		
//		
//		expandableListView.setOnChildClickListener(new OnChildClickListener() {
//			
//			@Override
//			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
//					int childPosition, long id) {
//				try {
//					JSONObject obj = results.getJSONObject(groupPosition);
//					String intentStr = obj.toString();
//					Intent intent = new Intent(PackageSelectionActivity.this, PackageSelDetailActivity.class);
//					intent.putExtra("data", intentStr);
//					startActivity(intent);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				return false;
//			}
//		});
//		 */
//		
//		// ++++++++++++++++++++++++++++++++++++++++++++
//		// child's stub
//
//		@Override
//		public Object getChild(int groupPosition, int childPosition) {
//			// TODO Auto-generated method stub
//			
//			return str_child_items_.get(groupPosition)[childPosition];
//		}
//
//		@Override
//		public long getChildId(int groupPosition, int childPosition) {
//			// TODO Auto-generated method stub
//			return childPosition;
//		}
//
//		@Override
//		public int getChildrenCount(int groupPosition) {
//			// TODO Auto-generated method stub
//			return str_child_items_.get(groupPosition).length;
//		}
//
//		@Override
//		public View getChildView(int groupPosition, int childPosition,
//				boolean isLastChild, View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			TextView txt_child;
//			if (null == convertView) {
//				convertView = LayoutInflater
//						.from(PackageSelectionActivity.this).inflate(
//								R.layout.sel_item, null);
//			}
//			/* 判断是否是最后一项，最后一项设计特殊的背景 */
//
//			txt_child = (TextView) convertView.findViewById(R.id.sel_item_text);
//			txt_child.setText(str_child_items_.get(groupPosition)[childPosition]);
//
//			return convertView;
//		}
//
//		@Override
//		public Object getGroup(int groupPosition) {
//			// TODO Auto-generated method stub
//			return str_group_items_.get(groupPosition);
//		}
//
//		@Override
//		public int getGroupCount() {
//			// TODO Auto-generated method stub
//			return str_group_items_.size();
//		}
//
//		@Override
//		public long getGroupId(int groupPosition) {
//			// TODO Auto-generated method stub
//			return groupPosition;
//		}
//
//		@Override
//		public View getGroupView(int groupPosition, boolean isExpanded,
//				View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			TextView txt_group;
//			if (null == convertView) {
//				convertView = LayoutInflater
//						.from(PackageSelectionActivity.this).inflate(
//								R.layout.sel_group, null);
//			}
//			ImageView img = (ImageView) convertView
//					.findViewById(R.id.sel_group_img);
//			/* 判断是否group张开，来分别设置背景图 */
//			if (isExpanded) {
//				img.setBackgroundResource(R.drawable.icon_arrow_down);
//			} else {
//				img.setBackgroundResource(R.drawable.icon_arrow_right);
//			}
//
//			txt_group = (TextView) convertView
//					.findViewById(R.id.sel_group_text);
//			txt_group.setText(str_group_items_.get(groupPosition));
//			return convertView;
//		}
//
//		@Override
//		public boolean isChildSelectable(int arg0, int arg1) {
//			// TODO Auto-generated method stub
//			return true;
//		}
//
//		@Override
//		public boolean hasStableIds() {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//	}
}
