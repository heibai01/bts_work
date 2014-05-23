package com.led.weatherdemo.engine.impl;

import java.io.File;
import java.io.FileInputStream;

import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.led.weatherdemo.ConstantValue;
import com.led.weatherdemo.bean.WeatherConfig;
import com.led.weatherdemo.engine.WeatherEngine;

public class WeatherEngineImpl extends DefaultHandler implements WeatherEngine {

	private static final String TAG = "WeatherEngineImpl";
	@Override
	public WeatherConfig getWeatherConfig() {
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			Log.e(TAG, "sdCard is not mounted!");
		} else {
			try {
				File file = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath(), ConstantValue.CONFIG_FILE);
				FileInputStream fis = new FileInputStream(file);
				// 获得pull解析器对象
				XmlPullParser parser = Xml.newPullParser();
				// 指定解析的文件和编码格式
				parser.setInput(fis, "utf-8");
				int eventType = parser.getEventType(); // 获得事件类型
				WeatherConfig config = null;
				while (eventType != XmlPullParser.END_DOCUMENT) {
					String tagName = parser.getName();
					switch (eventType) {
					case XmlPullParser.START_TAG:
						if ("PlayWindowData".equals(tagName)
								&& "天气窗口".equals(parser.getAttributeValue(null,
										"WindowName"))) {
							config = new WeatherConfig();
						} else if ("ShowType".equals(tagName) && config != null) {
							config.setShowType(Integer.parseInt(parser
									.nextText()));
						} else if ("Language".equals(tagName) && config != null) {
							config.setLanguage(Integer.parseInt(parser.nextText()));
						} else if ("ForecastDays".equals(tagName)
								&& config != null) {
							config.setForecastDays(Integer.parseInt(parser
									.nextText()));
						} else if ("UpdateFrequency".equals(tagName)
								&& config != null) {
							config.setUpdateFrequency(Integer.parseInt(parser
									.nextText()));
						} else if ("StartX".equals(tagName) && config != null) {
							config.setStartX(Integer.parseInt(parser.nextText()));
						} else if ("StartY".equals(tagName) && config != null) {
							config.setStartY(Integer.parseInt(parser.nextText()));
						} else if ("Width".equals(tagName) && config != null) {
							config.setWidth(Integer.parseInt(parser.nextText()));
						} else if ("Height".equals(tagName) && config != null) {
							config.setHeight(Integer.parseInt(parser.nextText()));
						}
						break;
					case XmlPullParser.END_TAG:
						if ("PlayWindowData".equals(tagName) && config != null) {
							fis.close();
							return config;
						}
						break;
					default:
						break;
					}
					eventType = parser.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
