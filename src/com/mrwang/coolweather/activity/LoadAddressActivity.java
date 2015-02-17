package com.mrwang.coolweather.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mrwang.coolweather.R;
import com.mrwang.coolweather.bean.Address;
import com.mrwang.coolweather.bean.Address.City;
import com.mrwang.coolweather.bean.Address.County;
import com.mrwang.coolweather.bean.Address.Province;
import com.mrwang.coolweather.util.SharedPreferencesUtil;
import com.mrwang.coolweather.util.StreamUtils;

/**
 * 遍历省市县
 * 
 * @author Administrator
 * 
 */
public class LoadAddressActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	protected static final int QUERYCITIES = 1;
	protected static final int QUERYCOUNTIES = 2;
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private List<String> dataList1 = new ArrayList<String>();
	private List<String> dataList2 = new ArrayList<String>();
	private List<String> dataList3 = new ArrayList<String>();
	/**
	 * 省列表
	 */
	private int provinceIndex;
	/**
	 * 市列表
	 */
	private int cityIndex;
	/**
	 * 县列表
	 */
	private int countyIndex;
	/**
	 * 选中的省份
	 */
	private Province selectedProvince;
	/**
	 * 选中的城市
	 */
	private City selectedCity;
	/**
	 * 选中的城市
	 */
	private County selectedCounty;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;
	private Address address;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case QUERYCITIES:
				queryCities();
				break;
			case QUERYCOUNTIES:
				queryCounties();
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_area);
		initUI();
		initData();
	}

	private void initUI() {
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
	}

	private void initData() {

		try {
			InputStream is = getAssets().open("address.json");
			String json = StreamUtils.readStream(is);
			System.out.println("解析完成的Json为+" + json);
			Gson gson = new Gson();
			address = gson.fromJson(json, Address.class);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("地址文件打开失败");
		}

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Message msg = Message.obtain();
				if (currentLevel == LEVEL_PROVINCE) {
					// 这里type 0表示直辖市 只有两级 1代表省区 有三级
					provinceIndex = position;
					selectedProvince = address.arealist.get(position);
					msg.what = QUERYCITIES;
					handler.sendMessage(msg);
				} else if (currentLevel == LEVEL_CITY) {
					cityIndex = position;
					selectedCity = address.arealist.get(provinceIndex).sub
							.get(position);
					msg.what = QUERYCOUNTIES;
					handler.sendMessage(msg);
				} else if (currentLevel == LEVEL_COUNTY) {
					countyIndex=position;
					selectedCounty = address.arealist.get(provinceIndex).sub
							.get(cityIndex).sub.get(position);
					saveAddress(false);
				}
			}
		});
		// 默认加载省级数据
		queryProvinces();
	}

	/**
	 * 查询全国所有的省
	 */
	private void queryProvinces() {
		dataList3.clear();
		// 说明是省级
		for (Province province : address.arealist) {
			dataList3.add(province.name);
		}
		listView.setSelection(0);
		titleText.setText("中国");
		adapter = new ArrayAdapter<String>(LoadAddressActivity.this,
				android.R.layout.simple_list_item_1, dataList3);
		listView.setAdapter(adapter);
		currentLevel = LEVEL_PROVINCE;
		dataList1.clear();
		dataList2.clear();
	}

	/**
	 * 查询全国所有的市
	 */
	protected void queryCities() {
		System.out.println("线程名称" + Thread.currentThread().getName());
		dataList2.clear();
		for (City city : address.arealist.get(provinceIndex).sub) {
			//System.out.println("city.name" + city.name);
			dataList2.add(city.name);
		}
		adapter = new ArrayAdapter<String>(LoadAddressActivity.this,
				android.R.layout.simple_list_item_1, dataList2);
		listView.setAdapter(adapter);
		listView.setSelection(0);
		titleText.setText(selectedProvince.name);
		dataList1.clear();
		dataList3.clear();
		currentLevel = LEVEL_CITY;

	}

	/**
	 * 查询全国所有的县
	 */
	protected void queryCounties() {
		dataList1.clear();
		if (address.arealist.get(provinceIndex).type != 0) {
			// 说明不是直辖市
			for (County county : address.arealist.get(provinceIndex).sub
					.get(cityIndex).sub) {
				dataList1.add(county.name);
			}
			adapter = new ArrayAdapter<String>(LoadAddressActivity.this,
					android.R.layout.simple_list_item_1, dataList1);
			listView.setAdapter(adapter);
			dataList2.clear();
			listView.setSelection(0);
			titleText.setText(selectedCity.name);
			dataList2.clear();
			dataList3.clear();
			currentLevel = LEVEL_COUNTY;
		}else {
			//说明是直辖市 应该立即保存当前地址
			saveAddress(true);
		}
	}
	/**
	 * 保存信息
	 * @param isCity 是否是直辖市
	 */
	private void saveAddress(boolean isCity) {
		SharedPreferencesUtil.saveStringData(this, "province", selectedProvince.name);
		SharedPreferencesUtil.saveStringData(this, "city", selectedCity.name);
		if (!isCity) {
			SharedPreferencesUtil.saveStringData(this, "county", selectedCounty.name);
		}
		//Toast.makeText(getApplicationContext(), "当前位置:"+selectedProvince.name+selectedCity.name+selectedCounty.name+"保存成功", Toast.LENGTH_SHORT).show();
		Intent intent=new Intent(this,WeatherActivity.class);
		if (isCity) {
			intent.putExtra("county", selectedCity.name);
		}else {
			intent.putExtra("county", selectedCounty.name);
		}
		
		startActivity(intent);
		finish();
	}
	
	/**
	 * 显示进度条
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载中");
			// 点击外部不会取消该对话框
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}

}
