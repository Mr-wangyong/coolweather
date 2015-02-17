package com.mrwang.coolweather.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mrwang.coolweather.R;
import com.mrwang.coolweather.bean.Weather;
import com.mrwang.coolweather.service.AutoUpdateService;
import com.mrwang.coolweather.util.HttpCallBackListener;
import com.mrwang.coolweather.util.HttpUtil;
import com.mrwang.coolweather.util.SharedPreferencesUtil;
import com.mrwang.coolweather.util.URLBase;

public class WeatherActivity extends Activity implements OnClickListener {
	private LinearLayout weatherInfoLayout;

	/**
	 * 用于显示城市名
	 */
	private TextView cityNameText;
	/**
	 * 用于显示发布时间
	 */
	private TextView publishText;
	/**
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	/**
	 * 用于显示气温1
	 */
	private TextView temp1Text;
	/**
	 * 用于显示气温2
	 */
	private TextView temp2Text;
	/**
	 * 用于显示当前日期
	 */
	private TextView currentDateText;
	/**
	 * 切换城市按钮
	 */
	private Button switchCity;
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;

	private String county;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		initUI();
		initData();
	}

	private void initUI() {
		// 初始化各控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}

	private void initData() {
		String county = getIntent().getStringExtra("county");
		if (!TextUtils.isEmpty(county)) {
			// 说明有县级代号,去查询天气
			publishText.setText("同步中");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			queryWeather(county);
		} else {
			// 显示本地缓存天气
			showWeather();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, LoadAddressActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中");
			county = SharedPreferencesUtil.getStringData(this,
					"county", "");
			if (!TextUtils.isEmpty(county)) {
				queryWeather(county);
			}
			break;
		}

	}
	private Weather weather;
	private void queryWeather(String county) {
		String result = county.substring(0, 2);
		try {
			String address=URLBase.BaseUrl+URLEncoder.encode(result, "UTF-8");
			System.out.println("地址=="+address);
			HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
				
				

				@Override
				public void onFinish(String response) {
					System.out.println("获取天气数据为"+response);
					Gson gson=new Gson();
					weather = gson.fromJson(response, Weather.class);
					saveData();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
					});
				}
				
				@Override
				public void onError(Exception e) {
					System.out.println("请求失败鸟");
				}
			});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	protected void saveData() {
		SharedPreferencesUtil.saveStringData(this, "city_name", weather.data.city);
		SharedPreferencesUtil.saveStringData(this, "temp1", weather.data.forecast.get(0).high);
		SharedPreferencesUtil.saveStringData(this, "temp2", weather.data.forecast.get(0).low);
		SharedPreferencesUtil.saveStringData(this, "weather_desp", weather.data.ganmao);
		SharedPreferencesUtil.saveStringData(this, "publish_time", weather.data.forecast.get(0).type);
		SharedPreferencesUtil.saveStringData(this, "current_date", weather.data.forecast.get(0).date);
	}

	private void showWeather() {
		cityNameText.setText(SharedPreferencesUtil.getStringData(this,"city_name", ""));
		temp1Text.setText(SharedPreferencesUtil.getStringData(this,"temp1", ""));
		temp2Text.setText(SharedPreferencesUtil.getStringData(this,"temp2", ""));
		weatherDespText.setText(SharedPreferencesUtil.getStringData(this,"weather_desp", ""));
		publishText.setText(SharedPreferencesUtil.getStringData(this,"publish_time", ""));
		currentDateText.setText(SharedPreferencesUtil.getStringData(this,"current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}

}
