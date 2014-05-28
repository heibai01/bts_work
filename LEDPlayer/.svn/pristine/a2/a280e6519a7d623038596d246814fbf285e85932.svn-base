package com.led.player.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {
	/**
	 * 左上角显示
	 * @param context
	 * @param resID
	 * @param length
	 */
	private static Toast mToast;
	public static synchronized void setLTShow(Context context,int resID,int length){
		if (mToast!=null) {  
//			toast.cancel();
			mToast.setText(resID);
		}else {
			ToastUtil.mToast = Toast.makeText(context, resID, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.TOP|Gravity.LEFT, 1, 1);
		}
		mToast.show();
		
//		Toast toast = Toast.makeText(context, resID, length);
//		toast.setGravity(Gravity.TOP|Gravity.LEFT, 1, 1);
//		toast.show();
	}
	/**
	 * 左上角显示
	 * @param context
	 * @param resID
	 * @param length
	 */
	public static synchronized void setLTShow(Context context,String msg,int length){
		if (mToast!=null) { 
//			toast.cancel();
			mToast.setText(msg);
		}else {
			ToastUtil.mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.TOP|Gravity.LEFT, 1, 1);
		}
		mToast.show();
//		
//		Toast toast = Toast.makeText(context, msg, length);
//		toast.setGravity(Gravity.TOP|Gravity.LEFT, 1, 1);
//		toast.show();
	}
	/**
	 * 右下角显示
	 * @param context
	 * @param resID
	 * @param length
	 */
	public static synchronized void setRBShow(Context context,int resID,int length){
//		if (toast!=null) {  ��Щ����ʾ��������������
////			toast.cancel();
//			toast.setText(resID);
//		}else {
//			ToastUtil.toast = Toast.makeText(context, resID, Toast.LENGTH_SHORT);
//			toast.setGravity(Gravity.TOP|Gravity.LEFT, 10, 50);
//		}
//		toast.show();
//		
		Toast toast = Toast.makeText(context, resID, length);
		toast.setGravity(Gravity.RIGHT|Gravity.BOTTOM, 1, 1);
		toast.show();
	}
	/**
	 * 右下角显示
	 * @param context
	 * @param resID
	 * @param length
	 */
	public static synchronized void setRBShow(Context context,String msg,int length){
//		if (toast!=null) {  ��Щ����ʾ��������������
////			toast.cancel();
//			toast.setText(resID);
//		}else {
//			ToastUtil.toast = Toast.makeText(context, resID, Toast.LENGTH_SHORT);
//			toast.setGravity(Gravity.TOP|Gravity.LEFT, 10, 50);
//		}
//		toast.show();
//		
		Toast toast = Toast.makeText(context, msg, length);
		toast.setGravity(Gravity.RIGHT|Gravity.BOTTOM, 1, 1);
		toast.show();
	}
}
