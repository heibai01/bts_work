package com.heibai.modeloclock;

import java.io.File;
import java.io.FileInputStream;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Xml;
import android.widget.LinearLayout;

import com.heibai.modeloclock.bean.AnalogClockWindow;
import com.heibai.modeloclock.view.MyAnalogClock;

public class MainActivity extends Activity {

	private LinearLayout mContainer;
	private AnalogClockWindow config;
	private final static String CONFIG_PATH = "external.xml";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
	}
	private void init() {
		
		//得到容器
		mContainer = (LinearLayout) findViewById(R.id.ll_container);
		//解析xml配置参数
		config = parseLocalXml();
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200,
				200);
		MyAnalogClock mClock1 = new MyAnalogClock(this);
		mContainer.addView(mClock1, params);
		
		MyAnalogClock mClock2 = new MyAnalogClock(this);
		mClock2.setmDial(getResources().getDrawable(R.drawable.clock_dail_3));
		mClock2.setmHourHand(getResources().getDrawable(
				R.drawable.default_clock_hour));
		mClock2.setmMinuteHand(getResources().getDrawable(
				R.drawable.default_clock_min));
		mClock2.setmSecondHand(getResources().getDrawable(
				R.drawable.default_clock_second));
		mContainer.addView(mClock2, params);
		MyAnalogClock mClock3 = new MyAnalogClock(this);
		mContainer.addView(mClock3, params);
		
	}
	private AnalogClockWindow parseLocalXml(){
		if(Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()){
			try {
				FileInputStream fis = new FileInputStream(new File(Environment.getExternalStorageDirectory(), CONFIG_PATH));
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(fis, "UTF-8");
				
				int eventType = parser.getEventType();
				while(eventType != XmlPullParser.END_DOCUMENT){
					String tagName = parser.getName();
					switch (eventType) {
					case XmlPullParser.START_TAG:
						if("PlayWindowData".equals(tagName) && "时钟窗口".equals(parser.getAttributeValue(null, "WindowName"))){
							AnalogClockWindow config = new AnalogClockWindow();
						}else if ("StartX".equals(tagName) && config != null) {
							config.setStartX(Integer.parseInt(parser.nextText()));
						}else if ("StartY".equals(tagName) && config != null) {
							config.setStartY(Integer.parseInt(parser.nextText()));
						}else if ("Width".equals(tagName) && config != null) {
							config.setWidth(Integer.parseInt(parser.nextText()));
						}else if ("Height".equals(tagName) && config != null) {
							config.setHeight(Integer.parseInt(parser.nextText()));
						}else if ("ClkType".equals(tagName) && config != null) {
							config.setClkType(Integer.parseInt(parser.nextText()));
						}else if ("Skin".equals(tagName) && config != null) {
							config.setSkin(Integer.parseInt(parser.nextText()));
						}else if ("Transparence".equals(tagName) && config != null) {
							config.setTransparence(Integer.parseInt(parser.nextText()));
						}else if ("HourHandColor".equals(tagName) && config != null) {
							config.setHourHandColor(Integer.parseInt(parser.nextText()));
						}else if ("MinuteHandColor".equals(tagName) && config != null) {
							config.setMinuteHandColor(Integer.parseInt(parser.nextText()));
						}else if ("SecondHandColor".equals(tagName) && config != null) {
							config.setSecondHandColor(Integer.parseInt(parser.nextText()));
						}else if ("HourPointColor".equals(tagName) && config != null) {
							config.setHourPointColor(Integer.parseInt(parser.nextText()));
						}else if ("MinutePointColor".equals(tagName) && config != null) {
							config.setMinutePointColor(Integer.parseInt(parser.nextText()));
						}else if ("Address".equals(tagName) && config != null) {
							config.setAddress(parser.nextText());
						}else if ("ShowWeek".equals(tagName) && config != null) {
							config.setShowWeek(Integer.parseInt(parser.nextText()));
						}else if ("ShowDate".equals(tagName) && config != null) {
							config.setShowDate(Integer.parseInt(parser.nextText()));
						}
						break;
					case XmlPullParser.END_TAG:
						if ("PlayWindowData".equals(tagName) && config != null) {
							fis.close();
							return config;
						}
					default:
						break;
					}
				}
				eventType = parser.next();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			throw new RuntimeException("外部存储卡不存在");
		}
		return null;
	}

}
