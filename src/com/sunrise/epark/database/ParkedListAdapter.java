/**
 *ParkedListAdapter.java
 *classes:com.sunrise.epark.database.ParkedListAdapter
 *李敏 Create at 2015年7月30日 下午1:51:54
 */
package com.sunrise.epark.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sunrise.epark.R;
import com.sunrise.epark.activity.ParkedListActivity;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.ToastUtil;
import com.sunrise.epark.util.VolleyErrorUtil;

/**
 *com.sunrise.epark.database.ParkedListAdapter
 * @author 李敏
 * create at 2015年7月30日 下午1:51:54
 */
public class ParkedListAdapter extends BaseAdapter {

	Context context;
	List<JSONObject> list;
	
	
	//private JSONArray jsonArray;
	private RequestQueue queue;
	

	public ParkedListAdapter(Context context, List<JSONObject> list) {
		this.context = context;
		this.list = list;
		queue=Volley.newRequestQueue(context);
		initPreview();
	}

	/**
	 * 发出第一个请求获取所有数据
	 */
	private void initPreview() {
		StringRequest parks = new StringRequest(Method.POST,
				Urls.PARKLIST_ACTION, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.e("停车列表", response);
						try {
							JSONObject object = new JSONObject(response);
							String result = object.getString("result");
							JSONArray array = new JSONArray(result);
							System.out.println("ggggggggggg");
							for (int i = 0; i < array.length(); i++) {
								list.add(array.getJSONObject(i));
							}
							for (int j = 0; j < list.size(); j++) {
								System.out.println(list.get(j).getString("parkName")+".......");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
                       notifyDataSetChanged();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						/*ToastUtil.showLong(ParkedListAdapter.this,
								VolleyErrorUtil.getMessage(error,
										ParkedListAdapter.this));*/
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("longitude", "0");
				map.put("latitude", "0");
				map.put("distance", "3");
				map.put("page", "1");
				map.put("priceSort", "");
				return map;
			}
		};
		queue.add(parks);
		queue.start();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}


	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("gggggggggggsssssssssss");
		ViewHolder viewHolder = null;
		JSONObject json=null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.park_list_item, null);
			viewHolder.parkAddr=(TextView)convertView.findViewById(R.id.tv_parkAddr);
			viewHolder.parkCity=(TextView)convertView.findViewById(R.id.tv_parkCity);
			viewHolder.parkName=(TextView)convertView.findViewById(R.id.tv_parkName);
			viewHolder.parkDistance=(TextView)convertView.findViewById(R.id.tv_parkDistance);
			//viewHolder.parkId=(TextView)convertView.findViewById(R.id.tv_parkId);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		json=list.get(position);
		if (json!=null) {
			try {
				System.out.println("ggggggggggg"+json.getString("parkAddr")+"ssssssssssss");
				viewHolder.parkAddr.setText(json.getString("parkAddr"));
				viewHolder.parkCity.setText(json.getString("parkCity"));
				viewHolder.parkName.setText(json.getString("parkName"));
				//viewHolder.parkDistance.setText((CharSequence) json.get("parkAddr"));
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
		
		
		return convertView;
	}
	
	public void release() {
		// 把与context关联的所有下载任务都取消掉
		queue.cancelAll(context);
	}
	
	public static class ViewHolder {
		TextView parkDistance;
		TextView parkName;
		TextView parkCity;
		TextView parkAddr;
		
	}

	/*@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("gggggggggggsssssssssss");
		JSONObject json=null;
		if (convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.park_list_item, null);
		}
		TextView parkName=(TextView) convertView.findViewById(R.id.tv_parkName);	
		TextView parkCity=(TextView) convertView.findViewById(R.id.tv_parkCity);	
		TextView parkAddr=(TextView) convertView.findViewById(R.id.tv_parkAddr);	
		TextView parkDistance=(TextView) convertView.findViewById(R.id.tv_parkDistance);	
		json=list.get(position);
		try {
			parkAddr.setText(json.getString("parkAddr"));
			parkCity.setText(json.getString("parkCity"));
			parkName.setText(json.getString("parkName"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}*/
	
}
