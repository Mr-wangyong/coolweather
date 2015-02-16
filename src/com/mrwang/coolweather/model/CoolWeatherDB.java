package com.mrwang.coolweather.model;

import java.util.ArrayList;
import java.util.List;

import com.mrwang.coolweather.db.CoolWeatherOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库操作类
 * @author mr.wang
 *
 */
public class CoolWeatherDB {
	/**
	* 数据库名
	*/
	public static final String DB_NAME = "cool_weather";
	/**
	* 数据库版本
	*/
	public static final int VERSION = 1;
	/**
	 * 数据库操作类
	 */
	private static CoolWeatherDB coolWeatherDB;
	/**
	 * 数据库对象
	 */
	private SQLiteDatabase db;
	/**
	 * 抽取容器
	 */
	private ContentValues values=new ContentValues();
	/**
	 * 私有化构造方法,并且在构造方法中创建数据库
	 * @param context
	 */
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper helper=new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db=helper.getWritableDatabase();
	}
	/**
	 * 获取数据库实例对象  这里用到单例模式
	 * @param context
	 * @return
	 */
	public synchronized static CoolWeatherDB getInstance(Context context){
		if (coolWeatherDB!=null) {
			coolWeatherDB=new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	/**
	 * 将Province省份对象保存到数据库中
	 * @param province
	 * @return false 失败<br>
	 * 			true 成功
	 */
	public boolean savaProvince(Province province){
		if (province!=null) {
			values.clear();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			long rowid = db.insert("Province", null, values);
			if (rowid==-1) {
				return false;
			}else {
				return true;
			}
		}
		return false;
	}
	/**
	 * 将city城市对象保存到数据库中
	 * @param city
	 * @return false 失败<br>
	 * 			true 成功
	 */
	public boolean savaCity(City city){
		if (city!=null) {
			values.clear();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			long rowid = db.insert("City", null, values);
			if (rowid==-1) {
				return false;
			}else {
				return true;
			}
		}
		return false;
	}
	/**
	 * 将County县级对象保存到数据库中
	 * @param city
	 * @return false 失败<br>
	 * 			true 成功
	 */
	public boolean savaCounty(County county){
		if (county!=null) {
			values.clear();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			long rowid = db.insert("County", null, values);
			if (rowid==-1) {
				return false;
			}else {
				return true;
			}
		}
		return false;
	}
	/**
	 * 根据省份ID查询所有的城市信息
	 * @param provinceId 省份ID
	 * @return 城市list列表
	 */
	public List<City> loadCities(int provinceId){
		List<City> list=new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}
	/**
	 * 根据省份ID查询所有的城市信息
	 * @param cityId ID
	 * @return 县级list列表
	 */
	public List<County> loadCounties(int cityId){
		List<County> list=new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county=new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("city_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("city_code")));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());
		}
		return list;
	}
}
