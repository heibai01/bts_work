package com.heibai.modeloclock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.xmlpull.v1.XmlPullParser;

import com.heibai.modeloclock.bean.AnalogClockWindow;

import android.R.xml;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class ModelOclock extends Activity {
	private AnalogClockWindow window;
	private SurfaceHolder holder;
	private Paint paint;
	/**
	 * 时钟宽高
	 */
	private int height;
	private int width;

	private Timer timer;
	private SurfaceView surface;
	public ModelOclock(Context context, AttributeSet attrs) {
		super();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
	}

	private void init() {
		// 从xml中解析数据,装进window里面
		window = parseLocalConfig();
		paint = new Paint();
		surface = (SurfaceView) findViewById(R.id.sv_model_clock);
		holder = surface.getHolder();
		holder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				timer.cancel();
				timer = null;
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				
				if(timer != null){
					timer.schedule(new ClockTimerTask(), 2000, 1000);
				}else{
					timer = new Timer("ModerOclock");
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub

			}
		});
	}
	/**
	 * 解析配置文件,得到时钟bean
	 * @return
	 */
	private AnalogClockWindow parseLocalConfig() {
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			try {
				XmlPullParser parser = Xml.newPullParser();
				FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory()+"/external.xml");
				parser.setInput(fis, "UTF-8");
				int eventType = parser.getEventType();
				while(eventType != XmlPullParser.END_DOCUMENT){
					String tagName = parser.getName();
					switch (eventType) {
					case XmlPullParser.START_TAG:
						if ("PlayWindowData".equals(tagName)
								&& "时钟窗口".equals(parser.getAttributeValue(null,
										"WindowName"))) {
							AnalogClockWindow window = new AnalogClockWindow();
						}else if ("StartX".equals(tagName) && window != null) {
							window.setStartX(Integer.parseInt(parser.nextText()));
						}else if ("StartY".equals(tagName) && window != null) {
							window.setStartY(Integer.parseInt(parser.nextText()));
						}else if ("Width".equals(tagName) && window != null) {
							window.setWidth(Integer.parseInt(parser.nextText()));
						}else if ("Height".equals(tagName) && window != null) {
							window.setHeiht(Integer.parseInt(parser.nextText()));
						}else if ("ClkType".equals(tagName) && window != null) {
							window.setClkType(Integer.parseInt(parser.nextText()));
						}else if ("ShowDate".equals(tagName) && window != null) {
							window.setShowDate(Boolean.valueOf(parser.nextText()));
						}else if ("ShowWeek".equals(tagName) && window != null) {
							window.setShowWeek(Boolean.valueOf(parser.nextText()));
						}else if ("BgWord".equals(tagName) && window != null) {
							window.setBgWord(parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						if("PlayWindowData".equals(tagName)){
							fis.close();
							return window;
						}
					default:
						break;
					}
					eventType = parser.next();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			throw new RuntimeException("没有挂载外部存储设备,或者外部存储设备已损坏");
		}
		return null;
	}
	public class ClockTimerTask extends TimerTask{

		@Override
		public void run() {
			width = surface.getMeasuredWidth();
			height = surface.getMeasuredHeight();
			
			Canvas canvas = holder.lockCanvas();
			canvas.drawColor(window.getBgClr());
			drawClockPointer(canvas, paint);
			
			holder.unlockCanvasAndPost(canvas);
		}
		
	}
	/**
	 * 绘制时钟点
	 * 
	 * @param canvas
	 * @param paint
	 */
	private void drawClockPointer(Canvas canvas, Paint paint) {
		int px = width;
		int py = height;

		int mHour;
		int mMinutes;
		int mSeconds;
		long time = System.currentTimeMillis();
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(time);
		mHour = mCalendar.get(Calendar.HOUR);
		mMinutes = mCalendar.get(Calendar.MINUTE);
		mSeconds = mCalendar.get(Calendar.SECOND);
		float hDegree = ((mHour + (float) mMinutes / 60) / 12) * 360;
		float mDegree = ((mMinutes + (float) mSeconds / 60) / 60) * 360;
		float sDegree = ((float) mSeconds / 60) * 360;

		paint.setColor(Color.YELLOW);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
		canvas.save();
		canvas.rotate(mDegree, px / 2, py / 2);
		Path path1 = new Path();
		path1.moveTo(px / 2, py / 2);
		path1.lineTo(px / 2, py / 5);
		canvas.drawPath(path1, paint);
		canvas.restore();

		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(4);
		canvas.save();
		canvas.rotate(hDegree, px / 2, py / 2);
		Path path2 = new Path();
		path2.moveTo(px / 2, py / 2);
		path2.lineTo(px / 2, py / 4);
		canvas.drawPath(path2, paint);
		canvas.restore();

		paint.setColor(Color.RED);
		paint.setStrokeWidth(2);
		canvas.save();
		canvas.rotate(sDegree, px / 2, py / 2);
		Path path3 = new Path();
		path3.moveTo(px / 2, py / 2);
		path3.lineTo(px / 2, py / 8);
		canvas.drawPath(path3, paint);
		canvas.restore();
	}

}
