package com.mrwang.coolweather.model;

/**
 * 区域(县级) 实体类
 * 
 * @author Administrator
 * 
 */
public class County {
	private int id;
	private String countyName;
	private String countyCode;
	private int cityId;

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public String getCountyCode() {
		return countyCode;
	}

	public void setCountyCode(String countyCode) {
		this.countyCode = countyCode;
	}

	@Override
	public String toString() {
		return "County [id=" + id + ", countyName=" + countyName
				+ ", countyCode=" + countyCode + ", cityId=" + cityId + "]";
	}

}
