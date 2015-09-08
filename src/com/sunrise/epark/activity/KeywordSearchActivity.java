package com.sunrise.epark.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import com.sunrise.epark.R;
import com.sunrise.epark.bean.SearchModel;
import com.sunrise.epark.util.AMapUtil;
import com.sunrise.epark.util.StringUtils;
import com.sunrise.epark.util.ToastUtil;

/**
 * 关键字搜索 com.sunrise.epark.activity.KeywordSearchActivity
 * 
 * @author 李敏 create at 2015年7月28日 下午5:09:45
 */
public class KeywordSearchActivity extends BaseFragmentActivity implements
		TextWatcher, OnPoiSearchListener, OnClickListener,
		OnGeocodeSearchListener {
	private EditText searchText;// 输入搜索关键字
	private String keyWord = "";// 要输入的poi搜索关键字
	private ProgressDialog progDialog = null;// 搜索时进度条
	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;// POI搜索
	private GeocodeSearch geocoderSearch;// 地理编码搜索
	private ImageView iv_search;// 搜索
	private ListView lv_history;// 搜索的历史记录
	private ListView lv_searchs;// 搜索列表
	private Button cancleButton;// 取消
	private ImageView iv_clear;// 清空
	private TextView tv_historyName;

	private List<SearchModel> mListSearch = null;//搜索的具体信息
	
	public String cityCode;

	public String text;// 输入的字符
	
	private LinearLayout ll_history,ll_searchs,ll_historys_del;

	private QuickAdapter<SearchModel> keyAdapter;
	
	private SharedPreferences spPreferences;//保存 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poikeywordsearch_activity);
		Intent intent = getIntent();
		cityCode = intent.getStringExtra("020");
		spPreferences = getSharedPreferences("maps", 0);
		init();
		initView();
		
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		// 地理编码
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		// 加载历史记录
		loaderHistory();
	}

	String[] strings;

	/**
	 * 加载历史记录
	 */
	private void loaderHistory() {
		tv_historyName = (TextView) findViewById(R.id.tv_historyName);
		lv_history = (ListView) findViewById(R.id.lv_history);
		SharedPreferences sPreferences = getSharedPreferences("maps", 0);
		String longString = sPreferences.getString("history", "你还没有任何历史记录");
		String[] hisArraysStrings = longString.split(",");

		if (hisArraysStrings.length > 1) {
			strings = new String[hisArraysStrings.length - 1];
			for (int i = 0, j = i; i < strings.length; i++, j++) {
				if (i == hisArraysStrings.length - 1) {
					j = i + 1;
				}
				strings[i] = hisArraysStrings[j];
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.search_history_item, R.id.tv_searchName, strings);
			// 设置点击事件
			lv_history.setOnItemClickListener(listener);
			lv_history.setAdapter(adapter);

		} else {
			lv_history.setVisibility(View.GONE);
			tv_historyName.setText("您没有历史记录！");
		}

	}
    //历史记录的点击事件
	OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view,
				int position, long arg3) {
			//ToastUtil.show(KeywordSearchActivity.this, strings[position]);
			getLatlon(strings[position]);

		}
	};

	/**
	 * 设置页面监听
	 */
	private void initView() {
		// 取消搜索
		cancleButton = (Button) findViewById(R.id.cancleButton);
		cancleButton.setOnClickListener(this);
		// 清空文本
		iv_clear = (ImageView) findViewById(R.id.iv_clear);
		iv_clear.setOnClickListener(this);
		
		ll_history=(LinearLayout) findViewById(R.id.ll_history);
		ll_searchs=(LinearLayout) findViewById(R.id.ll_searchs);
		
		searchText = (EditText) findViewById(R.id.keyWord);
		searchText.addTextChangedListener(this);// 添加文本输入框监听事件
		lv_searchs=(ListView) findViewById(R.id.lv_searchs);
		lv_searchs.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				SearchModel model=mListSearch.get(position);
				getLatlon(model.getInfo());
			}
		});
		iv_search = (ImageView) findViewById(R.id.iv_search);
		iv_search.setOnClickListener(this);
		ll_historys_del=(LinearLayout) findViewById(R.id.ll_historys_del);
		ll_historys_del.setOnClickListener(this);
		
	}

	/**
	 * 点击搜索按钮
	 */
	public void searchButton() {
		keyWord = AMapUtil.checkEditText(searchText);
		if ("".equals(keyWord)) {
			ToastUtil.show(KeywordSearchActivity.this, "请输入搜索关键字");
			return;
		} else {
			// 当点击搜索时，才进行保存操作
			saveHistory("history", searchText.getText().toString());
			doSearchQuery();
		}
	}


	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("正在搜索:\n" + keyWord);
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		showProgressDialog();// 显示进度框
		currentPage = 0;
		query = new PoiSearch.Query(keyWord, "", cityCode);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(10);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页
		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	/**
	 * 获取当前app的应用名字
	 */
	public String getApplicationName() {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	/**
	 * poi没有搜索到数据，返回一些推荐城市的信息
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "推荐城市\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
					+ cities.get(i).getCityCode() + "城市编码:"
					+ cities.get(i).getAdCode() + "\n";
		}
		ToastUtil.show(KeywordSearchActivity.this, infomation);

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	// 输入框的改变事件
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String newText=s.toString().trim();
		String text=searchText.toString().trim();
		if (StringUtils.isNotNull(text)) {
			iv_clear.setVisibility(View.VISIBLE);
			ll_searchs.setVisibility(View.VISIBLE);
			ll_history.setVisibility(View.GONE);
		}else {
			iv_clear.setVisibility(View.GONE);
			mListSearch.clear();
			keyAdapter.notifyDataSetChanged();
			ll_history.setVisibility(View.VISIBLE);
			ll_searchs.setVisibility(View.GONE);
		}
		Inputtips inputTips = new Inputtips(KeywordSearchActivity.this,
				new InputtipsListener() {

					@Override
					public void onGetInputtips(List<Tip> tipList, int rCode) {
						mListSearch=new ArrayList<SearchModel>();
						if (rCode == 0) {// 正确返回
							for (int i = 0; i < tipList.size(); i++) {
								//将具体信息加载到封装的实体类
								SearchModel model = new SearchModel(tipList
										.get(i).getName(), tipList.get(i)
										.getDistrict());
								// 对关键字的数据进行循环添加到集合中
								mListSearch.add(model);
							}
							keyAdapter=new QuickAdapter<SearchModel>(KeywordSearchActivity.this,R.layout.item_lv_search,mListSearch) {

								@Override
								protected void convert(BaseAdapterHelper arg0,
										SearchModel arg1) {
									if (arg1.getInfo2()==null) {
										arg0.getView(R.id.tv_info2).setVisibility(View.GONE);
									}
									arg0.setText(R.id.tv_info, arg1.getInfo());
									arg0.setText(R.id.tv_info2, arg1.getInfo2());
								}
								
							};
							lv_searchs.setAdapter(keyAdapter);
							keyAdapter.notifyDataSetChanged();
						}
					}
				});
		try {
			// 发送输入提示请求
			inputTips.requestInputtips(newText, cityCode);// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号
			getLatlon(newText);
		} catch (AMapException e) {
			e.printStackTrace();
		}
	}

	/**
	 * POI详情查询回调方法
	 */
	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int rCode) {

	}

	/**
	 * POI信息查询回调方法
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();// 隐藏对话框
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					// 取得搜索到的poiitems有多少页
					List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
					if (poiItems != null && poiItems.size() > 0) {
						// 对搜索到的数据进行循环读取
						for (PoiItem poi : poiItems) {
							Log.d("数据：",
									poi.getAdName() + "---" + poi.getAdCode()
											+ "---" + poi.getCityCode() + "---"
											+ poi.getCityName() + "---"
											+ poi.getDirection() + "---"
											+ poi.getDistance() + "---"
											+ poi.getLatLonPoint());
						}
					} else if (suggestionCities != null
							&& suggestionCities.size() > 0) {
						showSuggestCity(suggestionCities);
					} else {
						/*ToastUtil.show(KeywordSearchActivity.this,
								R.string.no_result);*/
					}
				}
			} else {
				//ToastUtil.show(KeywordSearchActivity.this, R.string.no_result);
			}
		} else if (rCode == 27) {
			ToastUtil.show(KeywordSearchActivity.this, R.string.error_network);
		} else if (rCode == 32) {
			ToastUtil.show(KeywordSearchActivity.this, R.string.error_key);
		} else {
			ToastUtil.show(KeywordSearchActivity.this,
					getString(R.string.error_other) + rCode);
		}

	}

	/**
	 * Button点击事件回调方法
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 点击搜索按钮
		 */
		case R.id.iv_search:
			searchButton();
			break;
		/**
		 * 点击取消搜索按钮
		 */
		case R.id.cancleButton:
			Intent intent = new Intent(KeywordSearchActivity.this,
					LocationActivity.class);
			intent.putExtra("isBack", true);
			startActivity(intent);
			finish();
			break;

		/**
		 * 清空文本
		 */
		case R.id.iv_clear:
			searchText.setText("");
			iv_clear.setVisibility(View.GONE);
			ll_history.setVisibility(View.VISIBLE);
			ll_searchs.setVisibility(View.GONE);
			break;
		/**
		 * 清空所有的历史记录	
		 */
		case R.id.ll_historys_del:
			Editor editor =spPreferences.edit();
			editor.clear();
			editor.commit();
			loaderHistory();
			break;
		default:
			break;
		}
	}

	/**
	 * 响应地理编码
	 */
	public void getLatlon(final String name) {
		GeocodeQuery query = new GeocodeQuery(name, cityCode);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
	}

	/**
	 * 地理编码查询回调
	 */
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				String addressName = "经纬度值:" + address.getLatLonPoint()
						+ "\n位置描述:" + address.getFormatAddress();
				// 当点击搜索时，才进行保存操作
				saveHistory("history", address.getFormatAddress());
				Intent data = new Intent();
				Bundle bundle = new Bundle();
				String adcode = address.getAdcode();
				bundle.putString("address", address.getFormatAddress());
				bundle.putDouble("lat", address.getLatLonPoint().getLatitude());
				bundle.putDouble("lon", address.getLatLonPoint().getLongitude());
				bundle.putString("city", address.getCity());
				data.putExtras(bundle);
				setResult(113, data);
				finish();
				Log.e("tag", adcode + "**");
				Log.e("tag", addressName + "aaaaaaaaaaa");
				// ToastUtil.show(SearchActivity.this, addressName);
			} else {
				//ToastUtil.show(getApplicationContext(), "对不起，没有搜索到相关数据！");
			}

		} else {
			//ToastUtil.show(getApplicationContext(), "搜索失败,请检查网络连接！");
		}

	}

	/**
	 * 逆地理编码回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	
	/**
	 * 把指定AutoCompleteTextView中内容保存到sharedPreference中指定的字符串
	 */
	public void saveHistory(String field, String text) {
		String longHiStory = spPreferences.getString(field, "你还没有任何历史记录");
		if (!longHiStory.contains(text + ",")) {
			StringBuilder sb = new StringBuilder(longHiStory);
			sb.insert(0, text + ",");
			spPreferences.edit().putString("history", sb.toString()).commit();
		}
	}

}
