package com.led.weatherdemo.util;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.led.weatherdemo.bean.WeatherInfo;

public class GetYahooWeatherSaxTools extends DefaultHandler {
	private List<WeatherInfo> infos;
	private WeatherInfo info;
	
	public GetYahooWeatherSaxTools(List<WeatherInfo> infos){
		this.infos = infos;
	}
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		if(qName.equals("yweather:forecast")){
			info = new WeatherInfo();
			
			info.setDay(attributes.getValue("day"));
			info.setDate(attributes.getValue("date"));
			info.setLow(attributes.getValue("low"));
			info.setHigh(attributes.getValue("high"));
			info.setCode(Integer.parseInt(attributes.getValue("code")));
			info.setWeather(attributes.getValue("text"));
			
			infos.add(info);
		}
		
	}
}
