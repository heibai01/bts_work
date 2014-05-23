package com.led.weatherdemo.bean;


public class WeatherInfo {
	/**
	 * 城市名
	 */
	private String city;
	/**
	 * 时间
	 */
	private String date;
	/**
	 * 天气状况代码 0-47,3200为不可用
	 */
	private int code;
	/**
	 * 天气状况
	 */
	private String weather;
	/**
	 * 当日最低 low
	 */
	private String low;
	/**
	 * 当日最高温度 high
	 */
	private String high;
	/**
	 * 温度（华氏）
	 */
	private String f;
	/**
	 * 温度（摄氏）
	 */
	private String c;
	/**
	 * 周几
	 */
	private String day;
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public String getLow() {
		return low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public String getF() {
		return f;
	}
	public void setF(String f) {
		this.f = f;
	}
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
}
