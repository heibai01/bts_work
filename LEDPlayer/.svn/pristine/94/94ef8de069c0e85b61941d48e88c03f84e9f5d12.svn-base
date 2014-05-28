package com.led.player.defview;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

/**
 * led_xx_xx东西(开放式)
 * 使用这个类显示走马灯。
 */
public class LedTextView extends TextView {
	private boolean DEBUG = true;
	public final static String TAG = LedTextView.class.getSimpleName();
	private Activity mActivity;

	private float textLength = 0f;// 文本长度
	private float viewWidth = 0f;
	private float passX = 0f;// 文字的横坐标
	private float y = 0f;// 文字的纵坐标
	private float temp_view_plus_text_length = 0.0f;// 用于计算的临时变�?
	private float temp_view_plus_two_text_length = 0.0f;// 用于计算的临时变�? , 这些默认时段都是当字体是静态的时候使用。
	public boolean isStarting = false;// 是否正在滚动
	public Paint paint = null;// 绘图样式
	private CharSequence text = "";// 文本内容
	private float speed = 1.0f; //滚动速度
	private boolean ifstatic = false; //默认是动态文字。
	private int walkspeed = 60;

	private Timer timer = new Timer("led_xx_xx中的走马灯timer"+Math.random());

	public LedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public LedTextView(Activity activity){
		super(activity);
		this.mActivity = activity;
	}

	public void init(float width) {
		this.text=super.getText();
		this.paint = super.getPaint();
		this.paint.setAntiAlias(true);   //mark 2014年2月18日 16:49:19 改成true
		this.text = getText().toString();
		this.textLength = this.paint.measureText(this.text.toString());
		this.viewWidth=width;
		this.passX = this.textLength;
		this.temp_view_plus_text_length = this.viewWidth + this.textLength;
		this.temp_view_plus_two_text_length = this.viewWidth + (this.textLength * 2); 
		//            y = getTextSize() + getPaddingTop();   //mark 修改成下面2句
		FontMetrics fontMetrics = this.paint.getFontMetrics();
		this.y = getTextSize() - fontMetrics.descent+4; //mark 2014年4月8日 16:25:21 修改+4
		System.out.println("bottom:"+fontMetrics.bottom+"--descent:"+fontMetrics.descent);

//		this.y =0; //mark 2014年4月8日 16:25:21 修改+4
	}

	public void setStatic(boolean Ifstatic){
		this.ifstatic = Ifstatic;
		FontMetrics fontMetrics = super.getPaint().getFontMetrics();
//		DisplayMetrics displayMetrics = mActivity.getResources().getDisplayMetrics();
//		float scaledDensity = displayMetrics.scaledDensity;
//		float xdpi = displayMetrics.xdpi;
//		float ydpi = displayMetrics.ydpi;
//		float density = displayMetrics.density;
//	float heightPixels = displayMetrics.heightPixels;
//		float widthPixels = displayMetrics.widthPixels;
//System.out.println("displayMetrics的属性---scaledDensity:"+scaledDensity+"--xdpi:"+xdpi+"--ydpi:"+ydpi+"--density:"+density
//		+"--heightPixels:"+heightPixels+"--widthPixels:"+widthPixels);//scaledDensity:1.0--xdpi:160.15764--ydpi:160.42105--density:1.0--heightPixels:720.0--widthPixels:1280.0
//
//  DisplayMetrics metrics = new DisplayMetrics();
//  mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//  System.out.println("displayMetrics的222属性---scaledDensity:"+metrics.scaledDensity+"--xdpi:"+metrics.xdpi+"--ydpi:"+metrics.ydpi
//		  +"--density:"+metrics.density
//			+"--heightPixels:"+metrics.heightPixels+"--widthPixels:"+metrics.widthPixels);//同上~~~
//  
		Log.e("LedTextView", "差--leading:"+	fontMetrics.leading+"--bottom:"+fontMetrics.bottom+"--top:"+fontMetrics.top+"--ascent:"+fontMetrics.ascent+"--descent:"+fontMetrics.descent)	;
	//leading:0.0--bottom:8.671875--top:-33.796875--ascent:-29.6875--descent:7.8125
//		System.out.println("fontsize:"+getTextSize());
		getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
	}
	public void setWalkSpeed(int walkspeed){
		if(walkspeed > 60){
			walkspeed = 60;
		}
		this.walkspeed = walkspeed;
	}

	/**
	 * 开放式走马灯开始走……
	 */
	public void startScroll() {
Log.e("第五个字符", "第五个字符："+getText().charAt(5))		;
		if(this.DEBUG)Log.i("LedTextView", "%%%%%%$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$%%%%startScroll");
		this.isStarting = true;
		invalidate();
	}
	
	public void stopScroll(){
		this.isStarting = false;
		if (timer!=null) {
			timer.cancel();
			timer = null;
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		if(this.ifstatic){
			stopScroll();
			setLineSpacing(0f, 0.9f);
			super.onDraw(canvas);
		}else{
			//if(DEBUG)Log.i("LedTextView", "%%%%%$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$%%%%%onDraw");
			canvas.drawText(this.text,0,this.text.length(), this.temp_view_plus_text_length - this.passX, this.y, this.paint);
			if (!this.isStarting) {
				return;
			}
			this.passX += this.speed;
			if (this.passX > this.temp_view_plus_two_text_length){
				this.passX = this.textLength;
			}
			this.timer.schedule(new MyTimerTask(), (61 - this.walkspeed));
//			this.timer.schedule(new MyTimerTask(), (1));
		}
	}
	private class MyTimerTask extends TimerTask{
	    
		@Override
		public void run() {
//			mHandler = new Handler(Looper.myLooper());
/*			LedTextView.this.mActivity.runOnUiThread(new Runnable() {

				public void run() {
					invalidate();
				}
			});*/
//			invalidate();
			postInvalidate();
//		Log.i("LedTextView", "%LedTextView...%%%%$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$MyTimerTask run()");
			//timer.cancel();
		}
	}
}