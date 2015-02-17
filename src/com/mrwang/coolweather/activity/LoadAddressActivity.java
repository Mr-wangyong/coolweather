package com.mrwang.coolweather.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.google.gson.Gson;
import com.mrwang.coolweather.R;
import com.mrwang.coolweather.bean.Address;
import com.mrwang.coolweather.bean.Address.City;
import com.mrwang.coolweather.bean.Address.County;
import com.mrwang.coolweather.bean.Address.Province;
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
					provinceIndex = address.arealist.get(position).type;
					selectedProvince = address.arealist.get(position);
					msg.what = QUERYCITIES;
					handler.sendMessage(msg);
				} else if (currentLevel == LEVEL_CITY) {
					cityIndex = address.arealist.get(provinceIndex).sub
							.get(position).type;
					selectedCity = address.arealist.get(provinceIndex).sub
							.get(position);
					msg.what = QUERYCOUNTIES;
					handler.sendMessage(msg);
				} else if (currentLevel == LEVEL_COUNTY) {
					countyIndex = position;
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
		dataList1.clear();
		// 说明是省级
		for (Province province : address.arealist) {
			dataList1.add(province.name);
		}
		listView.setSelection(0);
		titleText.setText("中国");
		currentLevel = LEVEL_PROVINCE;
		if (adapter == null) {
			adapter = new ArrayAdapter<String>(LoadAddressActivity.this,
					android.R.layout.simple_list_item_1, dataList1);
			listView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 查询全国所有的市
	 */
	protected void queryCities() {
		System.out.println("线程名称" + Thread.currentThread().getName());
		dataList2.clear();
		if (provinceIndex != 0) {
			for (City city : address.arealist.get(provinceIndex).sub) {
				System.out.println("city.name");
				dataList2.add(0,city.name);
			}
			adapter = new ArrayAdapter<String>(LoadAddressActivity.this,
					android.R.layout.simple_list_item_1, dataList2);
			listView.setAdapter(adapter);
			listView.setSelection(0);
			titleText.setText(selectedProvince.name);
			currentLevel = LEVEL_CITY;
		}

	}

	/**
	 * 查询全国所有的县
	 */
	protected void queryCounties() {
		dataList1.clear();
		if (provinceIndex != 0) {
			for (County county : address.arealist.get(provinceIndex).sub
					.get(cityIndex).sub) {
				dataList1.add(county.name);
			}
			adapter = new ArrayAdapter<String>(LoadAddressActivity.this,
					android.R.layout.simple_list_item_1, dataList1);
			listView.setAdapter(adapter);
			listView.setSelection(0);
			titleText.setText(selectedCity.name);
			currentLevel = LEVEL_CITY;
		}
	}

	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/*@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_PROVINCE) {
			queryProvinces();
		} else if (currentLevel == LEVEL_CITY) {
			queryCities();
		} else {
			finish();
		}
	}*/

}
