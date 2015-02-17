package com.mrwang.coolweather.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.google.gson.Gson;
import com.mrwang.coolweather.bean.Weather;
import com.mrwang.coolweather.receiver.AutoUpdateReceiver;
import com.mrwang.coolweather.util.HttpCallBackListener;
import com.mrwang.coolweather.util.HttpUtil;
import com.mrwang.coolweather.util.SharedPreferencesUtil;
import com.mrwang.coolweather.util.URLBase;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateWeather();
			}
		}).start();
		//系统警报类,用于定时执行更新的任务
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 8 * 60 * 60 * 1000; // 这是8小时的毫秒数
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	private Weather weather;

	/**
	 * 更新天气信息。
	 */
	private void updateWeather() {
		String county = SharedPreferencesUtil.getStringData(this, "county", "");
		String result = county.substring(0, 2);
		try {
			String address = URLBase.BaseUrl
					+ URLEncoder.encode(result, "UTF-8");
			System.out.println("地址==" + address);
			HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
				@Override
				public void onFinish(String response) {
					System.out.println("获取天气数据为" + response);
					Gson gson = new Gson();
					weather = gson.fromJson(response, Weather.class);
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
		SharedPreferencesUtil.saveStringData(this, "city_name",
				weather.data.city);
		SharedPreferencesUtil.saveStringData(this, "temp1",
				weather.data.forecast.get(0).high);
		SharedPreferencesUtil.saveStringData(this, "temp2",
				weather.data.forecast.get(0).low);
		SharedPreferencesUtil.saveStringData(this, "weather_desp",
				weather.data.ganmao);
		SharedPreferencesUtil.saveStringData(this, "publish_time",
				weather.data.forecast.get(0).type);
		SharedPreferencesUtil.saveStringData(this, "current_date",
				weather.data.forecast.get(0).date);
	}

}
