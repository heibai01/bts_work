package com.led.player.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;


public class PicHandler {
	/**
	 * 优化图片内存占用
	 * @return drawable对象
	 */
	/**
	 * 
	 * @param context	 给背景图设置
	 * @param resId	图片资源的resId
	 * @return	返回drawable对象
	 */
	public static Drawable optimizePic(Context context,int resId) {
		Resources resources = context.getResources();
        
		//1.得到图片的 宽度和高度，也要节省内存去测量
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true; //这样下面得到的bmp就是null但是opts的width,height属性还是设置了又没有给bitmap分配内存
		Bitmap bmp = BitmapFactory.decodeResource(resources, resId, opts);
		//2.根据图片与屏幕的比例去加载图片到内存
        int screenWidth = resources.getDisplayMetrics().widthPixels ;
        int screenHeight = resources.getDisplayMetrics().heightPixels;
		int heightRatio = (int)Math.ceil(opts.outHeight/(float)screenHeight);
		int widthRatio = (int)Math.ceil(opts.outWidth/(float)screenWidth);

        if (heightRatio>=1&&widthRatio>=1) {
			opts.inSampleSize = heightRatio>widthRatio?heightRatio:widthRatio;
		}else {
			opts.inSampleSize=1;
		}
        opts.inJustDecodeBounds = false;	
        		
         Bitmap bitmap = BitmapFactory.decodeResource(resources, resId, opts);
        Drawable drawable = new BitmapDrawable(resources, bitmap);
		return drawable;
	}
	/**
	 * 得到优化后的图
	 * @return
	 */
	public static Bitmap optimizeBitmap(Context context,String picPath){
		Resources resources = context.getResources();
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(picPath, opts); //这个得到的bitmap都是一个null的
		int screenWidth = resources.getDisplayMetrics().widthPixels ;
		int screenHeight = resources.getDisplayMetrics().heightPixels;
		int heightRatio = (int)Math.ceil(opts.outHeight/(float)screenHeight);
		int widthRatio = (int)Math.ceil(opts.outWidth/(float)screenWidth);
		if (heightRatio>=1&&widthRatio>=1) {
			opts.inSampleSize = heightRatio>widthRatio?heightRatio:widthRatio;
		}else {
			opts.inSampleSize=1;
		}
		 opts.inJustDecodeBounds = false;	
		 Bitmap bitmap = BitmapFactory.decodeFile(picPath, opts);
		 return bitmap;
	}
}
