package com.led.player.defview;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.RemoteViews.RemoteView;

import com.led.player.window.AnalogClockWindow;
import com.led.player.window.TextViewAttr;

/**
 * 模拟时钟，自定义View
 * @author 1231
 *
 */
@RemoteView
public class SurfaceAnalogClockView extends SurfaceView  implements Callback {

	private SurfaceHolder holder;
	private AnalogClockWindow mAnalogClockWindow;
	private TextViewAttr mtextviewattr;
	private Calendar calendar;
	private Paint paint = null;
	private Paint TextPaint = null;
	private Paint wordPaint = null;
	private int TextPX;
    private int TextPY;
    private int width;
    private int height;
    private String word;
    private String data;
    private String week;
    private float wordlen;
    private float datalen;
    private float weeklen;
    
    private Boolean ShowDate;
    private Boolean ShowWeek;
    
    private Timer timer = new Timer("SurfaceAnalogClockView_t");
    private Context mContext;
	
	public SurfaceAnalogClockView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		
		holder = getHolder();  
        holder.addCallback(this); //设置Surface生命周期回调  
        
        calendar = Calendar.getInstance();

        
	}
	
	public void Init(AnalogClockWindow analogclockwindow){
		mAnalogClockWindow = analogclockwindow;
		ShowDate = mAnalogClockWindow.getShowDate();
		ShowWeek = mAnalogClockWindow.getShowWeek();
		paint = new Paint();
		
		InitTextPaint(mAnalogClockWindow);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (timer!=null) {//java.lang.IllegalStateException: Timer was canceled 从被遮盖到回来这个错
			timer.schedule(new ClockTimerTask() , 2000 , 1000);	
		}else {
			timer = new Timer("SurfaceAnalogClockView_t重生版");
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		timer.cancel();
		timer = null;
	}
	
	public void InitTextPaint(AnalogClockWindow analogclockwindow){
    	mtextviewattr = analogclockwindow.getTextViewAttr();
    	TextPaint = new Paint();
    	wordPaint = new Paint();
    	TextPaint.setTextSize(mtextviewattr.getHeight());
    	wordPaint.setTextSize(mtextviewattr.getHeight());
    	TextPaint.setColor(mtextviewattr.getWordClr());
    	wordPaint.setColor(mtextviewattr.getFixedWordClr());
//    	setFaceName(TextPaint,mtextviewattr.getFaceName()); mark 2014年2月19日 11:07:35 注释掉
//    	setFaceName(wordPaint,mtextviewattr.getFaceName());
    	if(mtextviewattr.getItalic()==255){
    		TextPaint.setTextSkewX(-0.5f);
    		wordPaint.setTextSkewX(-0.5f);
		}
		if(mtextviewattr.getUnderline()==1){
			TextPaint.setUnderlineText(true);
			wordPaint.setUnderlineText(true);
		}
		if(mtextviewattr.getWeight()==700){
			TextPaint.setFakeBoldText(true);
			wordPaint.setFakeBoldText(true);
		}
    	word = analogclockwindow.getBgWord();
    	if(mtextviewattr.getLanguage()){
    		data =  String.valueOf(calendar.get(Calendar.YEAR))+"年"+
        			String.valueOf(calendar.get(Calendar.MONTH) + 1)+"月"+
        			String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+"日";
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
    		week = "星期"+mWay;
    	}else{
    		data =  String.valueOf(calendar.get(Calendar.YEAR))+"-"+
        			String.valueOf(calendar.get(Calendar.MONTH) + 1)+"-"+
        			String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        	String mWay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
    		if("1".equals(mWay)){
    			mWay ="Sun";
    		}else if("2".equals(mWay)){
    			mWay ="Mon";
    		}else if("3".equals(mWay)){
    			mWay ="Tue";
    		}else if("4".equals(mWay)){
    			mWay ="Wed";
    		}else if("5".equals(mWay)){
    			mWay ="Thu";
    		}else if("6".equals(mWay)){
    			mWay ="Fri";
    		}else if("7".equals(mWay)){
    			mWay ="Sat";
    		}
    		week = mWay;
    	}
    	
		
		wordlen = TextPaint.measureText(word);
		datalen = TextPaint.measureText(data);
		weeklen = TextPaint.measureText(week);
		
    	
    }

	private void setFaceName(Paint textpaint,int typeface){
		String texttype  = "";
		switch(typeface){
		case 0:
			texttype = "simsunb.ttf";
			break;
		case 1:
			texttype = "simfang.ttf";
			break;
		case 2:
			texttype = "black.ttf";
			break;
		case 3:
			texttype = "simkai.ttf";
			break;
		case 4:
			texttype = "simsun.ttc";
			break;
		case 5:
			texttype = "msyhbd.ttf";
			break;
		case 6:
			texttype = "xinsimsun.ttc";
			break;
		case 7:
			//texttype = "";
			break;
		case 8:
			//texttype = "";
			break;
		case 9:
			texttype = "cambria.ttc";
			break;
		case 10:
			//texttype = "";
			break;
		case 11:
			//texttype = "";
			break;
		case 12:
			//texttype = "";
			break;
		case 13:
			texttype = "seguisym.ttf";
			break;
		case 14:
			texttype = "extsimsunb.ttf";
			break;
		case 15:
			//texttype = "symbol.ttf";
			break;
		case 16:
			//texttype = "";
			break;
		case 17:
			texttype = "webdings.ttf";
			break;
		case 18:
			texttype = "wingding.ttf";
			break;
		case 19:
			texttype = "simfang.ttf";
			break;
		case 20:
			texttype = "black.ttf";
			break;
		case 21:
			texttype = "simkai.ttf";
			break;
		case 22:
			texttype = "simsun.ttc";
			break;
		case 23:
			texttype = "msyhbd.ttf";
			break;
		case 24:
			texttype = "xinsimsun.ttc";
			break;
		}
		textpaint.setTypeface(Typeface.createFromAsset(mContext.getAssets(),"fonts/simfang.ttf"));
//		textpaint.setTypeface(Typeface.createFromAsset(mContext.getAssets(),"fonts/simsunb.ttf")); 这个字体有口
//		TextPaint.setTypeface(Typeface.createFromFile("/mnt/sdcard/fonts/"+texttype));
	}
	
	public class ClockTimerTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Canvas canvas = holder.lockCanvas();
            
            canvas.drawColor(mAnalogClockWindow.getBgClr());
                                  
            width = getMeasuredWidth();
            height = getMeasuredHeight();
            TextPX = width/2;
        	TextPY = height/2;
        	
            drawClockPandle(canvas,paint);               
            drawClockPointer(canvas,paint);   
            
            if(ShowDate){
            	canvas.drawText(data, TextPX - (datalen/2), TextPY/3, TextPaint);
            }
            if(ShowWeek){
            	canvas.drawText(week, TextPX - (weeklen/2), TextPY*2/3, TextPaint);
            }
            
            canvas.drawText(word, TextPX - (wordlen/2), TextPY + (TextPY/3), wordPaint);
            
            holder.unlockCanvasAndPost(canvas);
		}
		
	}
	private void drawClockPandle(Canvas canvas,Paint paint){
    	
    	int px = width;           
    	int py = height;  
    	
    	paint.setColor(Color.RED); 
    	paint.setStyle(Paint.Style.FILL); 
    	//canvas.drawCircle(px/2,py/2,py/2-1, paint);                  
    	canvas.drawCircle(px/2,py/2,3, paint);    
    	
    	paint.setStyle(Paint.Style.FILL);
    	canvas.drawCircle(3,py/2,3, paint);
    	canvas.drawCircle(px/2,3,3, paint);
    	canvas.drawCircle(px-3,py/2,3, paint);
    	canvas.drawCircle(px/2,py-3,3, paint);
    	
    	int xoff,yoff;
    	xoff = (int) ((py/2-3)*Math.tan((2*Math.PI)/360*30));
    	yoff = (int) ((px/2-3)*Math.tan((2*Math.PI)/360*30));
    	canvas.drawCircle(2,py/2-yoff,2, paint);
    	canvas.drawCircle(px/2-xoff,2,2, paint);
    	canvas.drawCircle(px/2+xoff,2,2, paint);
    	canvas.drawCircle(py/2+yoff,px-2,2, paint);
    	
    	canvas.drawCircle(py/2+yoff,px-2,2, paint);
    	canvas.drawCircle(px/2+xoff,py-2,2, paint);
    	
    	canvas.drawCircle(px/2-xoff,py-2,2, paint);
    	canvas.drawCircle(2,py-2,2, paint);
    	
    	
//    	paint.setStyle(Paint.Style.STROKE);
//    	paint.setStrokeWidth(3); 
//    	Path path = new Path();              
//    	path.moveTo(1,py/2);          
//    	path.lineTo(px/16,py/2);           
//    	canvas.drawPath(path, paint); 
//    	
//    	path.moveTo(px/2,1);           
//    	path.lineTo(px/2,py/16);           
//    	canvas.drawPath(path, paint); 
//    	
//    	path.moveTo(px-1,py/2);           
//    	path.lineTo(px-px/16,py/2);           
//    	canvas.drawPath(path, paint); 
//    	
//    	path.moveTo(px/2,py-1);           
//    	path.lineTo(px/2,py-py/16);           
//    	canvas.drawPath(path, paint);  
//    	
//    	canvas.save();           
//    	canvas.rotate(30, px/2, py/2); 
//    	
//    	paint.setStrokeWidth(1); 
//    	Path path8 = new Path();           
//    	path8.moveTo(1,py/2);           
//    	path8.lineTo(px/30,py/2);           
//    	canvas.drawPath(path8, paint);
//    	
//    	path8.moveTo(px/2,1);           
//    	path8.lineTo(px/2,py/30);           
//    	canvas.drawPath(path8, paint); 
//    	
//    	path8.moveTo(px-1,py/2);           
//    	path8.lineTo(px-px/30,py/2);           
//    	canvas.drawPath(path8, paint); 
//    	
//    	path8.moveTo(px/2,py-1);           
//    	path8.lineTo(px/2,py-py/30);           
//    	canvas.drawPath(path8, paint); 
//    	
//    	canvas.restore();                        
//    	canvas.save();           
//    	canvas.rotate(60, px/2, py/2);  
//    	
//    	Path path9 = new Path();           
//    	path9.moveTo(1,py/2);           
//    	path9.lineTo(px/30,py/2);           
//    	canvas.drawPath(path9, paint);
//    	
//    	path9.moveTo(px/2,1);           
//    	path9.lineTo(px/2,py/30);           
//    	canvas.drawPath(path9, paint);
//    	
//    	path9.moveTo(px-1,py/2);           
//    	path9.lineTo(px-px/30,py/2);           
//    	canvas.drawPath(path9, paint);
//    	
//    	path9.moveTo(px/2,py-1);           
//    	path9.lineTo(px/2,py-py/30);           
//    	canvas.drawPath(path9, paint); 
//    	
    	canvas.restore();           
    	}//---------鏃堕挓鎸囬拡鐢诲埗鍑芥暟---------------------
    private void drawClockPointer(Canvas canvas,Paint paint){
    	int px = width;        
    	int py = height;       
    	/*-------------------------鑾峰緱褰撳墠鏃堕棿灏忔椂鍜屽垎閽熸暟---------------------*/               
    	int mHour;            
    	int mMinutes;                  
    	int mSeconds;           
    	long time = System.currentTimeMillis();    
    	final Calendar mCalendar = Calendar.getInstance();    
    	mCalendar.setTimeInMillis(time);    
    	mHour = mCalendar.get(Calendar.HOUR);    
    	mMinutes = mCalendar.get(Calendar.MINUTE);     
    	mSeconds = mCalendar.get(Calendar.SECOND);
    	/*-------------------------鑾峰緱褰撳墠鏃堕棿---------------------*/              
    	float hDegree=((mHour+(float)mMinutes/60)/12)*360;    
    	float mDegree=((mMinutes+(float)mSeconds/60)/60)*360;    
    	float sDegree=((float)mSeconds/60)*360;   
    	//鍒嗛拡锛嶏紞锛嶏紞锛嶏紞锛嶏紞锛嶏紞锛?     
    	paint.setColor(Color.YELLOW); 
    	paint.setStyle(Paint.Style.STROKE);
    	paint.setStrokeWidth(3); 
    	canvas.save();         
    	canvas.rotate(mDegree, px/2, py/2);         
    	Path path1 = new Path();            
    	path1.moveTo(px/2,py/2);         
    	path1.lineTo(px/2,py/5);         
    	canvas.drawPath(path1, paint);                  
    	canvas.restore();           
    	            
    	paint.setColor(Color.WHITE); 
    	paint.setStrokeWidth(4);
    	canvas.save();         
    	canvas.rotate(hDegree, px/2, py/2);         
    	Path path2 = new Path();            
    	path2.moveTo(px/2,py/2);         
    	path2.lineTo(px/2,py/4);         
    	canvas.drawPath(path2, paint);         
    	canvas.restore();        
    	              
    	paint.setColor(Color.RED);  
    	paint.setStrokeWidth(2);
    	canvas.save();         
    	canvas.rotate(sDegree, px/2, py/2);         
    	Path path3 = new Path();            
    	path3.moveTo(px/2,py/2);         
    	path3.lineTo(px/2,py/8);         
    	canvas.drawPath(path3, paint);                 
    	canvas.restore();        
    	}
	
}
