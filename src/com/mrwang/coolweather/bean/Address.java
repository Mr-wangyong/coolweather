package com.mrwang.coolweather.bean;

import java.util.List;

/**
 * 解析地址的javaBean
 * @author Administrator
 */
public class Address {
	public List<Province> arealist;
	
	public class Province{
		public String name;
		public List<City> sub;
		public int type;
	}
	public class City{
		public String name;
		public List<County> sub;
		public int type;
	}
	public class County{
		public String name;
	}
}
