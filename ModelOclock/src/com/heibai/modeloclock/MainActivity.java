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

		// 得到容器
		mContainer = (LinearLayout) findViewById(R.id.ll_container);
		// 解析xml配置参数
		config = parseLocalXml();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				config.getWidth(), config.getHeight());
		MyAnalogClock mClock;
		if (config.getSkin() == 0) { // 默认绘制
			mClock = new MyAnalogClock(this);
			mClock.setAddress(config.getAddress());
			mClock.setHourHandColor(config.getHourHandColor());
			mClock.setMinuteHandColor(config.getMinuteHandColor());
			mClock.setSecondHandColor(config.getSecondHandColor());
			mClock.setHourPointColor(config.getHourPointColor());
			mClock.setMinutePointColor(config.getMinutePointColor());
			mClock.setShowDate(config.getShowDate());
			mClock.setShowWeek(config.getShowWeek());
			mContainer.addView(mClock, params);
		} else if (config.getSkin() == 1) {// 皮肤1号
			mClock = new MyAnalogClock(this);
			mClock.setmDial(getResources().getDrawable(R.drawable.clock_dail_1));
			mClock.setmHourHand(getResources().getDrawable(
					R.drawable.default_clock_hour));
			mClock.setmMinuteHand(getResources().getDrawable(
					R.drawable.default_clock_min));
			mClock.setmSecondHand(getResources().getDrawable(
					R.drawable.default_clock_second));
			mContainer.addView(mClock, params);
		} else if (config.getSkin() == 2) {// 皮肤2号
			mClock = new MyAnalogClock(this);
			mClock.setmDial(getResources().getDrawable(R.drawable.clock_dail_1));
			mClock.setmHourHand(getResources().getDrawable(
					R.drawable.default_clock_hour));
			mClock.setmMinuteHand(getResources().getDrawable(
					R.drawable.default_clock_min));
			mClock.setmSecondHand(getResources().getDrawable(
					R.drawable.default_clock_second));

			mContainer.addView(mClock, params);
		} else if (config.getSkin() == 3) {// 皮肤3号
			mClock = new MyAnalogClock(this);
			mClock.setmDial(getResources().getDrawable(R.drawable.clock_dail_1));
			mClock.setmHourHand(getResources().getDrawable(
					R.drawable.default_clock_hour));
			mClock.setmMinuteHand(getResources().getDrawable(
					R.drawable.default_clock_min));
			mClock.setmSecondHand(getResources().getDrawable(
					R.drawable.default_clock_second));

			mContainer.addView(mClock, params);
		} else if (config.getSkin() == 4) {// 皮肤4号
			mClock = new MyAnalogClock(this);
			mClock.setmDial(getResources().getDrawable(R.drawable.clock_dail_1));
			mClock.setmHourHand(getResources().getDrawable(
					R.drawable.default_clock_hour));
			mClock.setmMinuteHand(getResources().getDrawable(
					R.drawable.default_clock_min));
			mClock.setmSecondHand(getResources().getDrawable(
					R.drawable.default_clock_second));

			mContainer.addView(mClock, params);
		}

	}

	private AnalogClockWindow parseLocalXml() {
		if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
			try {
				FileInputStream fis = new FileInputStream(new File(
						Environment.getExternalStorageDirectory(), CONFIG_PATH));
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(fis, "UTF-8");

				int eventType = parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT) {
					String tagName = parser.getName();
					switch (eventType) {
					case XmlPullParser.START_TAG:
						if ("PlayWindowData".equals(tagName)
								&& "时钟窗口".equals(parser.getAttributeValue(null,
										"WindowName"))) {
							AnalogClockWindow config = new AnalogClockWindow();
						} else if ("StartX".equals(tagName) && config != null) {
							config.setStartX(Integer.parseInt(parser.nextText()));
						} else if ("StartY".equals(tagName) && config != null) {
							config.setStartY(Integer.parseInt(parser.nextText()));
						} else if ("Width".equals(tagName) && config != null) {
							config.setWidth(Integer.parseInt(parser.nextText()));
						} else if ("Height".equals(tagName) && config != null) {
							config.setHeight(Integer.parseInt(parser.nextText()));
						} else if ("ClkType".equals(tagName) && config != null) {
							config.setClkType(Integer.parseInt(parser
									.nextText()));
						} else if ("Skin".equals(tagName) && config != null) {
							config.setSkin(Integer.parseInt(parser.nextText()));
						} else if ("Transparence".equals(tagName)
								&& config != null) {
							config.setTransparence(Integer.parseInt(parser
									.nextText()));
						} else if ("HourHandColor".equals(tagName)
								&& config != null) {
							config.setHourHandColor(parser.nextText());
						} else if ("MinuteHandColor".equals(tagName)
								&& config != null) {
							config.setMinuteHandColor(parser.nextText());
						} else if ("SecondHandColor".equals(tagName)
								&& config != null) {
							config.setSecondHandColor(parser.nextText());
						} else if ("HourPointColor".equals(tagName)
								&& config != null) {
							config.setHourPointColor(parser.nextText());
						} else if ("MinutePointColor".equals(tagName)
								&& config != null) {
							config.setMinutePointColor(parser.nextText());
						} else if ("Address".equals(tagName) && config != null) {
							config.setAddress(parser.nextText());
						} else if ("ShowWeek".equals(tagName) && config != null) {
							config.setShowWeek(Integer.parseInt(parser
									.nextText()));
						} else if ("ShowDate".equals(tagName) && config != null) {
							config.setShowDate(Integer.parseInt(parser
									.nextText()));
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
		} else {
			throw new RuntimeException("外部存储卡不存在");
		}
		return null;
	}

}
