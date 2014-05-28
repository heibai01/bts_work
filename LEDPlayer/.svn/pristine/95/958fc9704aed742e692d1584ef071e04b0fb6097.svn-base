package com.led.player.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.led.player.aidance.LedProtocol;


public class ToastHandler extends Handler{
	private Context mContext;
	private static ToastHandler mHandler;
	private Toast mToast;
	
	public ToastHandler() {
	}
	
	public void init(Context mContext){
		this.mHandler = this;
		this.mContext = mContext;
	}
	
	public static ToastHandler getInstance(){
		return mHandler;
	}
	
	
	public void toast(String msg,int duration){
		cancelToast();
		if (mToast==null) {
			mToast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);;
		}else {
			mToast.setText(msg);
			mToast.setDuration(duration);
		}
		mToast.show();
	}
	
	public void toast(int resid,int duration){
		cancelToast();
		if (mToast==null) {
			/*if (looper==null) {
			Looper looper = Looper.myLooper();
//				Looper.prepare();
				Looper.myLooper().getThread().setName("֪ͨ�߳�"+System.currentTimeMillis());
				mToast = Toast.makeText(mContext,resid, Toast.LENGTH_SHORT);
//				Looper.loop();  ����дҲ���У� �������� mToast.setText(resid);����ͬһ���UI�߳���ִ�У������Ǳ���ġ�
			}else {
				
			}*/
			mToast = Toast.makeText(mContext,resid, Toast.LENGTH_SHORT);
		}else {
			mToast.setText(resid);
			mToast.setDuration(duration);
		}
		mToast.show();
	}

	private void cancelToast() {//��׿4.2.2 ��cancel()����ʵ�ֲ�һ��������toast��4��������
		if (mToast != null) {
			mToast.cancel();
		}
	}
	
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case LedProtocol.TOAST_MSG:
			Object obj = msg.obj;
			int duration = msg.arg1;
			if (obj instanceof Integer) {
				toast((Integer)obj,duration);
			}
			if (obj instanceof String) {
				toast(obj.toString(),duration);
			}
			break;
		
		}
	}
}
