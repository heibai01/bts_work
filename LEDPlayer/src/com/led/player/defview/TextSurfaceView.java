package com.led.player.defview;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.led.player.window.TextViewAttr;
import com.led.player.window.TextVo;
/*8
 * 网络发送过来的走马灯文字类 需要的 自定义显示试图
 * 这个类也是显示走马灯：就是需要 xml文件才能播放的需要这个类。
 */
public class TextSurfaceView extends SurfaceView  implements Callback {
	private boolean DEBUG = false;
	private Activity mactivity;
	private TextVo textVo = null;
	private TextViewAttr textviewattr = null;
	private SurfaceHolder holder;

	private float textLength = 0f;// 文本长度
	private float viewWidth = 0f;
	private float passPx = 0f;// 文字的横坐标
	private float y = 0f;// 文字的纵坐标
	private float temp_view_plus_text_length = 0.0f;// 用于计算的临时变�?
	private float temp_view_plus_two_text_length = 0.0f;// 用于计算的临时变�?
	private float static_text_start = 0.0f;
	private Boolean callcreate = false;
	private Boolean isdrawtext = true;
	private ArrayList<String>  mTextList;
	private int []  mTextStart ;

	public Paint paint = null;// 绘图样式
	private CharSequence text = "";// 文本内容
	//        private float speed = 2.0f; //滚动速度
	private float speed = 1.0f; //滚动速度  2014年3月5日 14:03:55从 2.0f改成1.0f
	private boolean ifstatic = false;
	//        private int walkspeed = 40; //2014年3月5日 14:05:49 改成下面变量
	private float walkspeed = 16.7f;
	private int SunTime;

	private Timer timer = null;
	int time = 0;

	public TextSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(this.DEBUG)Log.i("LedActivity", "TextSurfaceView(Context context, AttributeSet attrs)");
	}
	public TextSurfaceView(Activity activity){
		super(activity);
		if(this.DEBUG)Log.i("LedActivity", "TextSurfaceView(Activity activity)");
		this.mactivity = activity;

	}

	/**
	 * 创建试图时就调用了。
	 */
	public void setSurfaceView(){
		this.holder = getHolder();
		this.holder.addCallback(this); //设置Surface生命周期回调

		setZOrderOnTop(true);
		this.holder.setFormat(PixelFormat.TRANSLUCENT);
//		setBackgroundColor(Color.GREEN);
////		设置透明
//		getBackground().setAlpha(0);

	}

	/**
	 * openview时调用显示。
	 * @param width
	 * @param textVo
	 * @param sumtime
	 */
	public void init(float width,TextVo textVo,int sumtime) {
		if(this.DEBUG)Log.i("TextSurfaceView", "TextSurfaceView  init");
		this.paint = new Paint();
//		paint.setTextAlign(Paint.Align.RIGHT);
		this.paint.setAntiAlias(true);  //mark 2014年2月18日 16:49:45 改成true

		this.textVo = textVo;
		this.textviewattr = textVo.getTextViewAttr();

		this.SunTime = sumtime;
		this.text = this.textviewattr.getContent();  //text在这里可能得到null，来佰特思写代码的兄弟注意啦……
		this.paint.setTextSize(this.textviewattr.getHeight());
		this.paint.setColor(this.textviewattr.getWordClr());
		//    		setFaceName(paint,textviewattr.getFaceName()); mark 注释掉 2014年2月18日 16:41:05
		if(this.textviewattr.getItalic()==1){
			this.paint.setTextSkewX(-0.5f);
		}
		if(this.textviewattr.getUnderline()==1){
			this.paint.setUnderlineText(true);
		}
		if(this.textviewattr.getWeight()==700){
			this.paint.setFakeBoldText(true);
		}
		//Log.e("dfdf", "回事null？？："+paint+"--text:"+text);
		if (this.text==null) {
			this.text="this is defualt marquee, please check your design!";
		}
		this.textLength = this.paint.measureText(this.text.toString());
		this.viewWidth=width;
		this.passPx = this.textLength;
		this.temp_view_plus_text_length = this.viewWidth + this.textLength;
		this.temp_view_plus_two_text_length = this.viewWidth + (this.textLength * 2);

		this.static_text_start = (this.viewWidth - this.textLength)/2;
		if(this.static_text_start < 0){
			this.static_text_start = 0;
		}
		FontMetrics fontMetrics = this.paint.getFontMetrics();
		this.y = (this.textviewattr.getHeight() - fontMetrics.descent)+4;  //mark 这里是距离屏幕顶部高度文字的
//		System.out.println("top:"+fontMetrics.top+"\nascent:"+fontMetrics.ascent+"\ndescent"+fontMetrics.descent+"\nbottom:"+fontMetrics.bottom);
		
//		y = -fontMetrics.top-(fontMetrics.bottom-fontMetrics.descent);艹，不知道如何计算这个基线坐标了。。。。
//		float offY = fontTotalHeight / 2 - fontMetrics.bottom;
//		float newY = 0 + offY;
//		this.y = newY;
//		System.out.println("字体高度："+textviewattr.getHeight()+"y坐标是多少呢？："+((this.textviewattr.getHeight() - fontMetrics.descent)+4));
		if(this.DEBUG)Log.i("LedActivity", "text height "+this.textviewattr.getHeight()+" bottom "+fontMetrics.bottom+" descent "+fontMetrics.descent);
		if(!this.textVo.getSingleLine()){
			this.mTextList = autoSplit((String)this.text, this.paint, width);
			this.mTextStart = new int[this.mTextList.size()];
			for(int i = 0;i<this.mTextStart.length;i++){
				this.mTextStart[i] = (int) ((this.viewWidth - this.paint.measureText(this.mTextList.get(i)))/2);
				if(this.mTextStart[i] < 0){
					this.mTextStart[i] = 0;
				}
			}
		}
		if(this.textVo.getStatic()){
			this.ifstatic = true;
		}else{
			this.ifstatic = false;
		}

		//    		if(textwindow.getWalkSpeed() > 60){
			//    			walkspeed = 20;
			//    		}else{
		//    			walkspeed = 80 - textwindow.getWalkSpeed();
		//    		} 改成下面if语句块 2014年3月5日 14:05:29
		if(this.textVo.getWalkSpeed() > 60){
			this.walkspeed = 16.7f;
		}else{
			this.walkspeed = 1016.7f - (this.textVo.getWalkSpeed()*16.7f);
		}
		this.time = 0;
		this.isdrawtext = true;

		if(this.callcreate){
			PlayerText();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {

	}
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if(this.DEBUG)Log.i("LedActivity", "###########################TextSurfaceView  surfaceCreated");

		this.callcreate = true;
		PlayerText();
	}
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(this.DEBUG)Log.i("LedActivity", "##########################TextSurfaceView  surfaceDestroyed");
		this.callcreate = false;
		if(this.timer!=null){
			this.timer.cancel();
			this.timer = null;
		}
	}
	
	private void PlayerText(){
		if(this.ifstatic){
			if(this.timer!=null){
				this.timer.cancel();
				this.timer = null;
			}
			synchronized (TextSurfaceView.this.holder) {
				Canvas  canvas = this.holder.lockCanvas();
				if(canvas!=null){
					if(this.textVo.getTransparent()){
						canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
					}else{
						canvas.drawColor(this.textviewattr.getBgClr());
					}
					if(this.textVo.getSingleLine()){
						canvas.drawText(this.text,0,this.text.length(), this.static_text_start, this.y, this.paint);
					}else{
						for(int i = 0;i < this.mTextList.size();i++){
							canvas.drawText(this.mTextList.get(i),0,this.mTextList.get(i).length(),
									this.mTextStart[i], this.y*(i+1), this.paint);
						}

					}
					this.holder.unlockCanvasAndPost(canvas);
				}
			}
			
		}else{//动太走马灯

			this.time = 0;
			this.isdrawtext = true;

			if(this.timer!=null){
				this.timer.cancel();
				this.timer = null;
			}
			this.timer = new Timer("TextSurfaceView_Thread");
			this.timer.schedule(new TextTimerTask() , 1000 , (int)this.walkspeed);

		}
	}

	private ArrayList<String> autoSplit(String content, Paint p, float width) {
		ArrayList<String> lineTexts = new ArrayList<String>();
		float textWidth = p.measureText(content);
		if(textWidth <= width) {
			lineTexts.add(content);
			return lineTexts;
		}

		int length = content.length();
		int start = 0, end = 1;

		while(start < length) {
			if(end == length) {//不足一行的文本
				lineTexts.add((String) content.subSequence(start, end));
				break;
			}
			String temp = (String) content.subSequence(end - 1, end);
			if("\n".equalsIgnoreCase(temp)||"\r".equalsIgnoreCase(temp)){
				lineTexts.add((String) content.subSequence(start, end));
				start = end;
			}
			if(p.measureText(content, start, end) > (width-30)) { //文本宽度超出控件宽度时
				lineTexts.add((String) content.subSequence(start, end));
				start = end;
			}

			end += 1;
		}
		return lineTexts;
	}

	public class TextTimerTask extends TimerTask{
		@Override
		public void run() {
			Thread.currentThread().setName("TextSurface_thread"+(int)Math.random());
			synchronized (TextSurfaceView.this.holder) {//mark 2014年4月8日 21:53:08试试看能否解决界面卡住不动了的问题的问题。
				if(TextSurfaceView.this.isdrawtext){
//					if((TextSurfaceView.this.time > TextSurfaceView.this.SunTime) ){
//						timer.cancel();
//						TextSurfaceView.this.isdrawtext = false;
//						if(TextSurfaceView.this.DEBUG)Log.i("LedActivity", "timer.cancel();");
//					}else{
//						TextSurfaceView.this.time += TextSurfaceView.this.walkspeed;
//					}
					//下面容易得到很多 的null线程都500多个了………… mark bug
					Canvas  canvas = TextSurfaceView.this.holder.lockCanvas();//界面卡这里了，，，mark，为何，一直报个错：Exception locking surface  java.lang.IllegalArgumentException
					if(canvas!=null){
						if(TextSurfaceView.this.textVo.getTransparent()){
							canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
						}else{
							canvas.drawColor(TextSurfaceView.this.textviewattr.getBgClr());
						}
						canvas.drawText(TextSurfaceView.this.text,0,TextSurfaceView.this.text.length(), TextSurfaceView.this.temp_view_plus_text_length - TextSurfaceView.this.passPx, TextSurfaceView.this.y, TextSurfaceView.this.paint);

						TextSurfaceView.this.passPx += TextSurfaceView.this.speed;
						if (TextSurfaceView.this.passPx > TextSurfaceView.this.temp_view_plus_two_text_length){
							if(TextSurfaceView.this.DEBUG)Log.i("LedActivity", "step  "+TextSurfaceView.this.passPx);
							TextSurfaceView.this.passPx = TextSurfaceView.this.textLength;
						}

						TextSurfaceView.this.holder.unlockCanvasAndPost(canvas);
					}else{
						if(TextSurfaceView.this.DEBUG)Log.i("TextSurfaceView", "canvas = null");
					}

				}
			}


		}

	}
}
