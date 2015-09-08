package com.sunrise.epark.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.sunrise.epark.R;
import com.sunrise.epark.activity.LocationActivity;
import com.sunrise.epark.activity.OrderRecordActivity;
import com.sunrise.epark.app.BaseApplication;
import com.sunrise.epark.bean.UserInfo;
import com.sunrise.epark.configure.Configure;
import com.sunrise.epark.configure.Urls;
import com.sunrise.epark.util.DialogUtil;
import com.sunrise.epark.util.LogUtil;
import com.sunrise.epark.util.StatusUtil;
import com.sunrise.epark.util.StringUtils;

public class ParkingLotsFragment extends Fragment implements OnClickListener {
	private ImageView parkImg;
	private TextView parkName;
	private TextView parkAddress;
	private TextView parkNum;
	private TextView parkEmptyNum;
	private RelativeLayout orderRecord;
	private Button exit;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_manager_parkinglots,
				container, false);
		initView(view);
		bindEvent();
		getParkingDetail();
		return view;
	}

	private void initView(View view) {
		parkImg = (ImageView) view.findViewById(R.id.parklinglots_img);
		parkAddress = (TextView) view.findViewById(R.id.parklinglots_address);
		parkEmptyNum = (TextView) view.findViewById(R.id.parkinglots_num_empty);
		parkNum = (TextView) view.findViewById(R.id.parkinglots_num);
		parkName = (TextView) view.findViewById(R.id.parklinglots_name);
		orderRecord = (RelativeLayout) view
				.findViewById(R.id.parkinglots_order_record);
		exit = (Button) view.findViewById(R.id.manager_exit);
	}

	private void bindEvent() {
		orderRecord.setOnClickListener(this);
		exit.setOnClickListener(this);
	}

	public void getParkingDetail() {
		StringRequest parkDetail = new StringRequest(Method.POST,
				Urls.STOPCAR_MINGXI, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogUtil.i("parkDetail停车场明细：" + Urls.STOPCAR_MINGXI,
								response);
						try {
							JSONObject object = new JSONObject(response);
							String code = object.getString("code");
							if ("000".equals(code)) {
								String result = object.getString("result");
								JSONObject object2 = new JSONObject(result);
								String parkingName = object2
										.getString("parkName");
								String parkAddr = object2.getString("parkAddr");
								String remainPark = object2
										.getString("remainPark");
								String parkSum = object2.getString("parkSum");
								String imgUrl = object2.getString("imgUrl");
								if (StringUtils.isNotNull(imgUrl)) {
									@SuppressWarnings("deprecation")
									ImageRequest imageRequest = new ImageRequest(
											imgUrl,
											new Response.Listener<Bitmap>() {
												@Override
												public void onResponse(
														Bitmap response) {
													parkImg.setImageBitmap(response);
												}
											}, 0, 0, Config.RGB_565,
											new Response.ErrorListener() {
												@Override
												public void onErrorResponse(
														VolleyError error) {
												}
											});
									BaseApplication.mQueue.add(imageRequest);
									// ImageLoader imageLoader = new
									// ImageLoader(
									// BaseApplication.mQueue, new
									// BitmapCache());
									// ImageListener listener = ImageLoader
									// .getImageListener(
									// parkImg,
									// R.drawable.parkinglots_example,
									// R.drawable.parkinglots_example);
									// imageLoader.get(imgUrl, listener);
								}
								if (StringUtils.isNotNull(parkSum)) {
									parkNum.setText(parkSum);
								}
								if (StringUtils.isNotNull(remainPark)) {

									if (Integer.parseInt(remainPark) <= 0) {
										parkEmptyNum.setText("0");
									} else {
										parkEmptyNum.setText(remainPark);
									}
								}
								if (StringUtils.isNotNull(parkAddr)) {
									parkAddress.setText(parkAddr);
								}
								if (StringUtils.isNotNull(parkingName)) {
									parkName.setText(parkingName);
								}
							}
							if ("010".equals(code)) {
								DialogUtil.loginAgain(getActivity());
							}
						} catch (JSONException e) {

						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", UserInfo.userId);
				map.put("uuid", UserInfo.uuid);
				map.put("parkId", UserInfo.parkId);
				LogUtil.i("parkDetail停车场明细：" + Urls.STOPCAR_MINGXI,
						StringUtils.getMap(map));
				return map;
			}
		};
		parkDetail.setRetryPolicy(new DefaultRetryPolicy(Configure.TimeOut,
				Configure.Retry, Configure.Multiplier));
		BaseApplication.mQueue.add(parkDetail);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.parkinglots_order_record:
			startActivity(new Intent(getActivity(), OrderRecordActivity.class));
			break;
		case R.id.manager_exit:
			StatusUtil.exit(getActivity());
			startActivity(new Intent(getActivity(), LocationActivity.class));
			getActivity().finish();
			break;
		default:
			break;
		}
	}

}
