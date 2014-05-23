package com.led.weatherdemo.view;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.led.weatherdemo.ConstantValue;
import com.led.weatherdemo.R;
import com.led.weatherdemo.bean.WeatherConfig;
import com.led.weatherdemo.bean.WeatherInfo;
import com.led.weatherdemo.engine.WeatherEngine;
import com.led.weatherdemo.net.HttpClientUtil;
import com.led.weatherdemo.util.BeanFactory;
import com.led.weatherdemo.util.DensityUtil;
import com.led.weatherdemo.util.GetYahooWeatherSaxTools;

public class WeatherView {
	private Context context;
	public WeatherView(Context context){
		this.context = context;
	}
	private static final String TAG = "MainActivity";
	private String[] mShowTypeOfTemperature = { "°C", "°F" };
	SharedPreferences sp;
	private List<String> mWeekList;
	private static char[] filterStr = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z' };
	private static char[] filterStrtoNum = { '7', '3', '8', '1', '5', '8', '0',
			'4', '2', '8', '2', '3', '5', '7', '3', '9', '5', '4', '1', '5',
			'6', '4', '3', '2', '1', '9' };
	/**
	 * 安卓终端唯一id标识
	 */
	private String mAndroidId;
	/**
	 * 显示天气的容器，水平方向
	 */
	private LinearLayout mWeatherContainer;
	WeatherEngine mWeatherEngine;
	View view = null;
	private WeatherConfig config;
	/**
	 * 语言
	 */
	private static final int DEFAULT = 0;
	private static final int CHINESE = 1;
	private static final int ENGLISH = 2;
	private static final int WEATHER_IS_READY = 0;
	protected static final int GET_WEATHER_SUCCESS = 1;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == WEATHER_IS_READY) {// 利用handler发送延时消息,根据用户指定更新时间,更新天气
				if (mWeatherContainer.getChildCount() != 0) {
					mWeatherContainer.removeAllViews();
				}
				// String cityCode = config.getCityCode();
				String temperature = config.getShowType() > 0 ? "f" : "c";
				// showWeatherInfos("2161853", temperature);
				showWeatherInfos(temperature);
				handler.sendEmptyMessageDelayed(WEATHER_IS_READY,
						config.getUpdateFrequency() * 60 * 60);
			} else if (msg.what == GET_WEATHER_SUCCESS) {
				List<WeatherInfo> infos;
				SAXParserFactory factory = null;
				SAXParser saxParser = null;
				XMLReader xmlReader = null;
				GetYahooWeatherSaxTools tools = null;
				try {
					factory = SAXParserFactory.newInstance();
					saxParser = factory.newSAXParser();
					xmlReader = saxParser.getXMLReader();
					infos = new ArrayList<WeatherInfo>();
					tools = new GetYahooWeatherSaxTools(infos);

					xmlReader.setContentHandler(tools);
					xmlReader.parse(new InputSource(new StringReader(
							(String) msg.obj)));
					// 获取当前是周几,用数字表示
					int currentDay = mWeekList.indexOf(infos.get(0).getDay());
					int forecastDay = 0;
					// 将得到的list天气数据,展示到屏幕上
					for (int i = 0; i < config.getForecastDays(); i++) {
						view = View.inflate(context,
								R.layout.weather_item, null);
						TextView tvDay = (TextView) view
								.findViewById(R.id.tv_day);
						TextView tvLow = (TextView) view
								.findViewById(R.id.tv_temperature_low);
						TextView tvHigh = (TextView) view
								.findViewById(R.id.tv_temperature_high);
						TextView tvWeather = (TextView) view
								.findViewById(R.id.tv_weather);
						ImageView ivWeatherIcon = (ImageView) view
								.findViewById(R.id.iv_weather_icon);
						forecastDay = currentDay + i;
						if (forecastDay >= 7) {
							forecastDay = forecastDay % 7;
						}
						tvDay.setText(context.getResources().getStringArray(
								R.array.weather_day_normalform)[forecastDay]);
						tvLow.setText(infos.get(i).getLow()
								+ mShowTypeOfTemperature[config.getShowType()]);
						tvHigh.setText(infos.get(i).getHigh()
								+ mShowTypeOfTemperature[config.getShowType()]);
						tvWeather.setText(context.getResources().getStringArray(
								R.array.weather_condition)[infos.get(i)
								.getCode()]);
						ivWeatherIcon.setImageResource(R.drawable.w0);

						int width = config.getWidth()
								/ config.getForecastDays();
						int height = config.getHeight();
						float wScale = width / 100f;
						float hScale = height / 100f;
						float squarScale = width * height / (100 * 100);

						LayoutParams viewChildParams = (LayoutParams) tvDay
								.getLayoutParams();
						viewChildParams.height *= hScale;
						viewChildParams.width *= wScale;
						tvDay.setLayoutParams(viewChildParams);

						viewChildParams = (LayoutParams) tvLow
								.getLayoutParams();
						viewChildParams.height *= hScale;
						viewChildParams.width *= wScale;
						tvLow.setLayoutParams(viewChildParams);
						tvHigh.setLayoutParams(viewChildParams);

						viewChildParams = (LayoutParams) tvWeather
								.getLayoutParams();
						viewChildParams.height *= hScale;
						viewChildParams.width *= wScale;
						tvWeather.setLayoutParams(viewChildParams);

						viewChildParams = (LayoutParams) ivWeatherIcon
								.getLayoutParams();
						viewChildParams.height *= hScale;
						viewChildParams.width *= wScale;
						ivWeatherIcon.setLayoutParams(viewChildParams);

						tvDay.setTextSize(DensityUtil.dip2px(context,
								14 * squarScale));
						tvLow.setTextSize(DensityUtil.dip2px(context,
								14 * squarScale));
						tvHigh.setTextSize(DensityUtil.dip2px(
								context, 14 * squarScale));
						tvWeather.setTextSize(DensityUtil.dip2px(
								context, 14 * squarScale));

						LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						viewParams.weight = 1;

						mWeatherContainer.addView(view, viewParams);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		}

	};
	private String mGetWeatherUrl;
	private String mGetCityCodeUrl;

	protected void onCreate(Bundle savedInstanceState) {
		sp = context.getSharedPreferences("weather_config", Context.MODE_PRIVATE);
		init();

	}

	/**
	 * 初始化,查找ID,获取天气配置文件,设置容器属性
	 */
	private void init() {
		mWeatherContainer = new LinearLayout(context);
		// 利用工厂模式,获取天气业务对象,得到天气配置信息
		mWeatherEngine = BeanFactory.getImpl(WeatherEngine.class);
		config = mWeatherEngine.getWeatherConfig();
		
		String mAndroidIdStr = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		mAndroidIdStr = mAndroidIdStr.replace("-", "").replace(" ", "")
				.toUpperCase();
		mAndroidId = getLongAndroidId(mAndroidIdStr);
		Long num = Long.parseLong(mAndroidId);
		// 周列表,获取对应在数字,好直接从res/arrays获取不同国家的语言
		mWeekList = new ArrayList<String>();
		mWeekList.add("Sun");
		mWeekList.add("Mon");
		mWeekList.add("Tue");
		mWeekList.add("Wed");
		mWeekList.add("Thu");
		mWeekList.add("Fri");
		mWeekList.add("Sat");

		// 容器的大小及位置由用户指定
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				DensityUtil.dip2px(context, config.getWidth()),
				DensityUtil.dip2px(context, config.getHeight()));
		mWeatherContainer.setLayoutParams(params);
		// mWeatherContainer.layout(config.getStartX(), config.getStartY(),
		// DensityUtil.px2dip(MainActivity.this, config.getWidth()),
		// DensityUtil.px2dip(MainActivity.this, config.getHeight()));
		mWeatherContainer.setBackgroundColor(Color.YELLOW);
	}

	protected void onResume() {
		if (mWeatherContainer.getChildCount() != 0) {
			mWeatherContainer.removeAllViews();
		}
		String temperature = config.getShowType() > 0 ? "f" : "c";
		// showWeatherInfos("2161853", temperature);
		showWeatherInfos(temperature);
		handler.sendEmptyMessageDelayed(WEATHER_IS_READY,
				config.getUpdateFrequency() * 60 * 60 * 1000);
	}

	/**
	 * 根据城市代号与温度显示方式从雅虎服务器异步获取数据,显示天气预报
	 * 
	 * @param cityCode
	 *            城市代号
	 * @param temperature
	 *            f/c
	 */
	private void showWeatherInfos(final String temperature) {
		new Thread() {
			@Override
			public void run() {
//				HttpClientUtil http = new HttpClientUtil();
//				mGetCityCodeUrl = ConstantValue.LEDPLAYER_URI
//						+ "/public/getCityCode.jsp?i=" + mAndroidId;
//				String cityCode = http.sendDataByGet(mGetCityCodeUrl);
//				if (cityCode != null && !cityCode.equals("0")) {
//					mGetWeatherUrl = ConstantValue.YAHOO_WEATHER_URI + "?w="
//							+ cityCode + "&u=" + temperature;
//				} else {
					mGetWeatherUrl = ConstantValue.YAHOO_WEATHER_URI + "?w="
							+ ConstantValue.SHENZHEN_CITYCODE + "&u="
							+ temperature;
//				}
				HttpClientUtil http = new HttpClientUtil();
				String result = http.sendDataByGet(mGetWeatherUrl);

				if (result != null) {
					Message msg = Message.obtain();
					msg.what = GET_WEATHER_SUCCESS;
					msg.obj = result;
					handler.sendMessage(msg);
				}
			}
		}.start();
	}

	protected void onDestroy() {
		mWeatherContainer.removeAllViews();
		mWeatherContainer = null;
		mWeekList = null;
	}

	/**
	 * 根据android id返回一个数字字符串
	 * 
	 * @param parm
	 * @return
	 */
	private static String getLongAndroidId(String parm) {
		int tempi = -1;
		String tempStr = "";
		StringBuilder tempNum = new StringBuilder("");
		for (int i = 0; i < parm.length(); i++) {
			tempi = -1;
			for (int j = 0; j < filterStr.length; j++) {
				if (filterStr[j] == parm.charAt(i)) {
					tempi = j;
				}
			}
			if ((tempi >= 0) && (tempi <= 26)) {
				tempStr = tempStr + filterStrtoNum[tempi];
			}

			if (tempi == -1) {
				tempNum.append(parm.charAt(i));
			}
		}
		tempStr = tempStr + tempNum;

		if (tempStr.length() == 16)
			return tempStr;
		else
			return tempStr;
	}
}
