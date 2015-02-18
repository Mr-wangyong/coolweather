package com.mrwang.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mrwang.citypicker.CityPicker;
import com.mrwang.citypicker.CityPicker.OnSelectingListener;
import com.mrwang.coolweather.R;
import com.mrwang.coolweather.util.SharedPreferencesUtil;

public class LoadAddressActivity2 extends Activity {

	private CityPicker cityPicker;
	private TextView title_text;
	private TextView tv_address;
	private String cityCode;
	private String cityName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.address);
		cityPicker = (CityPicker) findViewById(R.id.citypicker);
		title_text = (TextView) findViewById(R.id.title_text);
		title_text.setText("请选择您所在的区域");
		tv_address = (TextView)findViewById(R.id.tv_address);
		Button bt_selete=(Button)findViewById(R.id.bt_selete);
		
		cityPicker.setOnSelectingListener(new OnSelectingListener() {

			@Override
			public void selected(boolean selected, String province_name,
					String city_name, String couny_name, String city_code) {
				tv_address.setText(province_name+"-"+city_name+"-"+couny_name);
				cityCode=city_code;
				cityName=couny_name;
			}

		});
		
		bt_selete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
			    SharedPreferencesUtil.saveStringData(getApplicationContext(), "county", cityName);
			    SharedPreferencesUtil.saveStringData(getApplicationContext(), "cityCode", cityCode);
				Intent intent=new Intent(LoadAddressActivity2.this,WeatherActivity.class);
				intent.putExtra("county", cityName);
				startActivity(intent);
				finish();
			}
		});
	}

}
