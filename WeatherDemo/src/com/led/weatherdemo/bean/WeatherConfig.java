package com.led.weatherdemo.bean;

/**
 * 从SD卡中得到的天气模块设置
 * 
 * @author heibai
 * @company http://www.bts-led.com/
 * @date 2014年5月17日
 */
public class WeatherConfig {
	/**
	 * 城市代号
	 */
	private String cityCode;
	/**
	 * 显示什么温度（华氏/摄氏）0代表摄氏，1代表华氏
	 */
	private int showType;
	/**
	 * 语言
	 */
	private int language;
	/**
	 * 预报几天
	 */
	private int forecastDays;
	/**
	 * 更新天气频率
	 */
	private int updateFrequency;
	/**
	 * 左上坐标
	 */
	private int startX;
	private int startY;
	/**
	 * 右下坐标
	 */
	private int width;
	private int height;

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCody(String cityCode) {
		this.cityCode = cityCode;
	}

	public int getShowType() {
		return showType;
	}

	public void setShowType(int showType) {
		this.showType = showType;
	}

	public int getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		this.language = language;
	}

	public int getForecastDays() {
		return forecastDays;
	}

	public void setForecastDays(int forecastDays) {
		this.forecastDays = forecastDays;
	}

	public int getUpdateFrequency() {
		return updateFrequency;
	}

	public void setUpdateFrequency(int updateFrequency) {
		this.updateFrequency = updateFrequency;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
