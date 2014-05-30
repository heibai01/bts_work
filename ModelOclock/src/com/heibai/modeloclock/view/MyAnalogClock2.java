package com.heibai.modeloclock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

class MyAnalogClock2 extends View{
	boolean mTickerStopped = false;
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			if(mTickerStopped){
				refreshClock();
				mHandler.sendEmptyMessageDelayed(0, 1000);
			}
			super.handleMessage(msg);
		}
		
	};

	public MyAnalogClock2(Context context) {
		super(context);
	}

	public MyAnalogClock2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyAnalogClock2(Context context, AttributeSet attrs) {
		super(context, attrs);
	
	}
	
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		if(visibility != View.VISIBLE){
			
		}
		super.onVisibilityChanged(changedView, visibility);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		super.onDraw(canvas);
	}
	
	private void refreshClock(){
		
	}
}

