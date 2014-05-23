package com.led.weatherdemo.engine;

import com.led.weatherdemo.bean.WeatherConfig;

public interface WeatherEngine {
	/**
	 * 从SD卡中解析XML,获取天气模块配置数据
	 * @return 天气配置对象
	 */
	public WeatherConfig getWeatherConfig();
	
}
