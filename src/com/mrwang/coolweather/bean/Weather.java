package com.mrwang.coolweather.bean;

import java.util.List;

/**
 * 天气的实体类
 * @author Administrator
 *
 */
public class Weather {
	public String desc;
	public String status;
	public Info data;
	
	public class Info{
		public String wendu;
		public String ganmao;
		
		public List<Forecast> forecast;
		public Yesterday yesterday;
		public String city;
	}
	public class Forecast{
		public String fengxiang;
		public String fengli;
		public String high;
		public String type;
		public String low;
		public String date;
	}
	public class Yesterday{
		public String fl;
		public String fx;
		public String high;
		public String type;
		public String low;
		public String date;
	}
}
