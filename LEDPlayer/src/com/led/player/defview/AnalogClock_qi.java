package com.led.player.defview;

import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews.RemoteView;

import com.led.player.R;
import com.led.player.window.AnalogClockWindow;
import com.led.player.window.TextViewAttr;

/**
 * 一个模拟时钟，弃用。。。
 * @author 1231
 *
 */
@RemoteView
public class AnalogClock_qi extends View {
    private Time mTime;
    private Bitmap Dial;
    private Bitmap mDial;
    private BitmapDrawable mDialDrawable;
    private BitmapDrawable mHourHandDrawable;
    private BitmapDrawable mMinuteHandDrawable;
    private BitmapDrawable mSecondHandDrawable;
    private int mDialWidth;
    private int mDialHeight;
    private boolean mAttached = false;
    private float mHours;
    private float mMinutes;
    private float mSeconds;
    private String time_zone;
    
    private Calendar calendar;
    private AnalogClockWindow manalogclockwindow;
    private TextViewAttr mtextviewattr;
    private Paint mPaint;
    private String word;
    private String data;
    private String week;
    private float wordlen;
    private float datalen;
    private float weeklen;
    
    private InputStream is =null;
    
    public String getTime_zone() {
		return time_zone;
	}

	public void setTime_zone(String timeZone) {
		time_zone = timeZone;
	}
	
	/**
     * ��־ʱ�䡢ʱ��ʱ�Ӳ��ִ�С���Ƿ��иı�
     */
    private boolean mChanged;
    
    /**
     * �̶߳��й���,��Ϣ���ݺʹ������
     */
    private Handler loopHandler = new Handler();
    
    /**
     * ��־ҳ��ˢ���߳���δִ��
     */
    private boolean isRun = false;
    
    /**
     * ʱ������
     */
    private void run()
    {
    	/**
    	 * ���̼߳������
    	 */
    	loopHandler.post(tickRunnable);
    }
    private Runnable tickRunnable = new Runnable() {   
        public void run() {
        	/**
        	 * �ڷ�UI�̵߳��ã�ǿ��ˢ�½���
        	 */
        	postInvalidate();
        	/**
        	 * ���̼߳�����У�1000���������
        	 */
            loopHandler.postDelayed(tickRunnable, 1000);   
        }   
    };   
	/**
	 * ���췽��
	 */
    public AnalogClock_qi(Context context,AnalogClockWindow analogclockwindow) {
        this(context, analogclockwindow,null);
        
    }

    public AnalogClock_qi(Context context, AnalogClockWindow analogclockwindow,AttributeSet attrs) {
        this(context, analogclockwindow,attrs, 0);
    }

    public AnalogClock_qi(Context context, AnalogClockWindow analogclockwindow,AttributeSet attrs,
                       int defStyle) {
        super(context, attrs, defStyle);
        
        manalogclockwindow = analogclockwindow;
        calendar = Calendar.getInstance();
        InitPaint(manalogclockwindow);
        /**
         * ��ʼ���������
         */
        
        mTime = new Time();
        time_zone = mTime.timezone;
        
    }
    
    public void InitPaint(AnalogClockWindow manalogclockwindow){
    	mtextviewattr = manalogclockwindow.getTextViewAttr();
    	mPaint = new Paint();
    	mPaint.setTextSize(mtextviewattr.getHeight());
    	mPaint.setColor(mtextviewattr.getWordClr());
    	word = manalogclockwindow.getBgWord();
    	data =  String.valueOf(calendar.get(Calendar.MONTH) + 1)+" 月 "+
    			String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+" 日";
    	String mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
		if("1".equals(mWay)){
			mWay ="日";
		}else if("2".equals(mWay)){
			mWay ="一";
		}else if("3".equals(mWay)){
			mWay ="二";
		}else if("4".equals(mWay)){
			mWay ="三";
		}else if("5".equals(mWay)){
			mWay ="四";
		}else if("6".equals(mWay)){
			mWay ="五";
		}else if("7".equals(mWay)){
			mWay ="六";
		}
		week = "星 期 "+mWay;
		
		wordlen = mPaint.measureText(word);
		datalen = mPaint.measureText(data);
		weeklen = mPaint.measureText(week);

    			
    	if(mtextviewattr.getItalic()==255){
    		mPaint.setTextSkewX(-0.5f);
		}
		if(mtextviewattr.getUnderline()==1){
			mPaint.setUnderlineText(true);
		}
		if(mtextviewattr.getWeight()==700){
			mPaint.setFakeBoldText(true);
		}
    	
    }
    
    public void InitResource(int width,int height){
    	Resources r = this.getContext().getResources();
		
		is = r.openRawResource(R.drawable.clock);
		mDialDrawable = new BitmapDrawable(is);
		//zoomDrawable(mDialDrawable,50,50);
		Log.i("AnalogClock", "mDialDrawable width "+mDialDrawable.getIntrinsicWidth()+
				"mDialDrawable height "+mDialDrawable.getIntrinsicHeight());
		Log.i("AnalogClock", "width "+width+"height "+height);
		Dial = mDialDrawable.getBitmap();
		mDial = zoomBitmap(Dial,width,height);
		
		is = r.openRawResource(R.drawable.hands);
		mHourHandDrawable = new BitmapDrawable(is);
		
		is = r.openRawResource(R.drawable.hands);
		mMinuteHandDrawable = new BitmapDrawable(is);
		
		is = r.openRawResource(R.drawable.hands);
		mSecondHandDrawable = new BitmapDrawable(is);
		
        /**
         * ��ȡ������Ч���ؿ��
         */
        mDialWidth = mDialDrawable.getIntrinsicWidth();
        mDialHeight = mDialDrawable.getIntrinsicHeight();
    }
    
    public Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {   
        int w = bitmap.getWidth();   
        int h = bitmap.getHeight();   
        Log.i("AnalogClock", "bitmap width "+w+"bitmap height "+h);
        Matrix matrix = new Matrix();   
        float scaleWidth = ((float) width / w);   
        float scaleHeight = ((float) height / h);   
        matrix.postScale(scaleWidth, scaleHeight);   
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);   
        Log.i("AnalogClock", "newbitmap width "+newbmp.getWidth()+"newbitmap height "+newbmp.getHeight());
        return newbmp;   
    }  
    
    public  Drawable zoomDrawable(BitmapDrawable drawable, int w, int h) {   
        int width = drawable.getIntrinsicWidth();   
        int height = drawable.getIntrinsicHeight();   
        // drawableת����bitmap   
        //Bitmap oldbmp = drawable2Bitmap(drawable); 
        Bitmap oldbmp = drawable.getBitmap();
        // ��������ͼƬ�õ�Matrix����   
        Matrix matrix = new Matrix();   
        // �������ű���   
        float sx = ((float) w / width);   
        float sy = ((float) h / height);   
        // �������ű���   
        matrix.postScale(sx, sy);   
        // �����µ�bitmap���������Ƕ�ԭbitmap�����ź��ͼ   
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,   
                matrix, true);   
        return new BitmapDrawable(newbmp);   
    }
    
    public  Bitmap drawable2Bitmap(Drawable drawable){     
        if(drawable instanceof BitmapDrawable){     
            return ((BitmapDrawable)drawable).getBitmap() ;     
        }else if(drawable instanceof Drawable){     
            Bitmap bitmap = Bitmap     
                    .createBitmap(   
                            drawable.getIntrinsicWidth(),     
                            drawable.getIntrinsicHeight(),     
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888     
                                    : Bitmap.Config.RGB_565);     
            Canvas canvas = new Canvas(bitmap);     
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),     
                    drawable.getIntrinsicHeight());     
            drawable.draw(canvas);     
            return bitmap;     
        }else{     
            return null ;     
        }     
    }    
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            getContext().registerReceiver(mIntentReceiver, filter, null, loopHandler);
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize =  MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize =  MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float )heightSize / (float) mDialHeight;
        }

        float scale = Math.min(hScale, vScale);

        setMeasuredDimension(resolveSize((int) (mDialWidth * scale), widthMeasureSpec),
                resolveSize((int) (mDialHeight * scale), heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isRun)
        {
        	run();
        	isRun = true;
        	return;
        }
        onTimeChanged();
        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }

        int availableWidth = mDial.getWidth();
        int availableHeight = mDial.getHeight();

        int x = availableWidth / 2;
        int y = availableHeight / 2; 

        final Drawable dial = mDialDrawable;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();
        
        boolean scaled = false;

        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min((float) availableWidth / (float) w,
                                   (float) availableHeight / (float) h);
            canvas.save();
            Log.i("AnalogClock", "onDraw availableWidth < w");
            canvas.scale(scale, scale, x, y);
        }

        if (changed) {

//            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        	dial.setBounds(0, 0, availableWidth, availableHeight);
        }
        dial.draw(canvas);

        canvas.save();
        
        canvas.drawText(word, x - (wordlen/2), y*2/3, mPaint);
        canvas.drawText(data, x - (datalen/2), y + (y/3), mPaint);
        canvas.drawText(week, x - (weeklen/2), y + (y*2/3), mPaint);

        canvas.rotate(mHours / 12.0f * 360.0f, x, y);
        final Drawable hourHand = mHourHandDrawable;
        if (changed) {
            w = hourHand.getIntrinsicWidth();
            h = hourHand.getIntrinsicHeight() ;
            hourHand.setBounds(x - (w / 2), y / 2 , x + (w / 2), y );
            //hourHand.setBounds(x - (w / 2), y * (3/5), x + (w / 2), y +(y*(2/5)));
            //hourHand.setBounds(x * (3/5),y * (3/5), x + (x*(2/5)), y +(y*(2/5)));
        }
        hourHand.draw(canvas);

        canvas.restore();

        canvas.save();
        canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);

        final Drawable minuteHand = mMinuteHandDrawable;
        if (changed) {
            w = minuteHand.getIntrinsicWidth() ;
            h = minuteHand.getIntrinsicHeight() ;
            minuteHand.setBounds(x - (w / 2), y / 3, x + (w / 2), y );
            //minuteHand.setBounds(x - (w / 2), y / 2, x + (w / 2), y + (y*(1/2)));
            //minuteHand.setBounds(x / 2, y / 2, x + (x*(1/2)), y + (y*(1/2)));
        }
        minuteHand.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.rotate(mSeconds / 60.0f * 360.0f, x, y);

        final Drawable scendHand = mSecondHandDrawable;
        if (changed) {
            w = scendHand.getIntrinsicWidth();
            h = scendHand.getIntrinsicHeight();
            scendHand.setBounds(x - (w / 2), y / 3, x + (w / 2), y);
            //scendHand.setBounds(x - (w / 2), y / 3, x + (w / 2), y * 5/3);
            //scendHand.setBounds(x / 3, y / 3, x * 5/3, y * 5/3);
        }
        scendHand.draw(canvas);
        canvas.restore();

        if (scaled) {
            canvas.restore();
        }
    }

    private void onTimeChanged() {

    	mTime.setToNow();

        int hour = mTime.hour;
        int minute = mTime.minute;
        int second = mTime.second;
        
        mSeconds = second;
        mMinutes = minute + second / 60.0f;
        mHours = hour + mMinutes / 60.0f;
        
        mChanged = true;
    }
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	String tz = "";
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                tz = intent.getStringExtra("time-zone");
                mTime = new Time(TimeZone.getTimeZone(tz).getID());
                time_zone = mTime.timezone;
            }
            Log.i("********************",tz);
            onTimeChanged();
            invalidate();
        }
    };
}
