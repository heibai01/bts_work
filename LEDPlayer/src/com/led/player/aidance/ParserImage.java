package com.led.player.aidance;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

public class ParserImage {
	
		/**
		 * 创建一个  原始比例的 图片
		 * @param unscaledBitmap
		 * @param dstWidth
		 * @param dstHeight
		 * @return
		 */
		public static Bitmap createScaledBitmap(Bitmap unscaledBitmap, int dstWidth, int dstHeight) {
	
		  Rect srcRect = new Rect(0, 0,unscaledBitmap.getWidth(), unscaledBitmap.getHeight());
		  Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight);
		  Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Config.ARGB_8888);
	//System.out.println("scaledBitmap:"+scaledBitmap);		  
		  Canvas canvas = new Canvas(scaledBitmap);
		  canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(Paint.FILTER_BITMAP_FLAG));
		  
		  return scaledBitmap;
	
		}

		public static Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {

		    final float srcAspect = (float)srcWidth / (float)srcHeight;
		    final float dstAspect = (float)dstWidth / (float)dstHeight;
		    if (srcAspect > dstAspect) {
		      return new Rect(0, 0, dstWidth, (int)(dstWidth / srcAspect));
		    } else {
		      return new Rect(0, 0, (int)(dstHeight * srcAspect), dstHeight);
		    }

		}
		
		/**
		 * 
		 * @param bitmap  要优化的位图对象
		 * @param w  imageView的宽度值
		 * @param h	 imageview的高度值
		 * @return   优化后的位图对象，铺满view的bitmap， 用到了矩阵的运算。。
		 */
		public static Bitmap getReduceBitmap(Bitmap bitmap ,int w,int h){
	    	int     width     =     bitmap.getWidth();
	    	int     hight     =     bitmap.getHeight();
	    	Matrix     matrix     =     new Matrix();
	    	float     wScake     =     ((float)w/width); 
	    	float     hScake     =     ((float)h/hight);        
	    	matrix.postScale(wScake, hScake);        
	    	return Bitmap.createBitmap(bitmap, 0,0,width,hight,matrix,true);
		}


		/**
		 * 
		 * @param pathName	文件路径
		 * @param dstWidth  这是那个imageview 的宽度
		 * @param dstHeight 这个Imgaeview的高度
		 * @return
		 */
		public static Bitmap decodeFile(String pathName, int dstWidth, int dstHeight) {
//System.gc();
			  Options options = new Options();
			  options.inJustDecodeBounds = true;
			  
			  BitmapFactory.decodeFile(pathName, options);
//			  options.inJustDecodeBounds = false;
			  options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth, dstHeight);
			  if(options.inSampleSize < 1){
//				  Log.i("ParseImage", "不需要缩放啊。。。");
				  options.inSampleSize = 1;
			  }else {
//				 Log.i("ParseImage", "缩放是needed！！！"+options.inSampleSize);
				 
			  }
			  options.inPreferredConfig = Bitmap.Config.RGB_565;
			  options.inJustDecodeBounds = false;
			  Bitmap optimizeBitmap = BitmapFactory.decodeFile(pathName, options);

			  return optimizeBitmap;

		}

		public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
			
//			float biWidth  = srcWidth/dstWidth;
//			float biHeight = srcHeight/dstHeight;
//			if (biWidth>biHeight) {
//				Log.e("方式一计算结果", "方式一计算结果宽大高："+(biWidth));
//			}else {
//				Log.e("方式一计算结果", "方式一计算结果高大于宽："+biHeight);
//			}

		    final float srcAspect = (float)srcWidth / (float)srcHeight;
		    final float dstAspect = (float)dstWidth / (float)dstHeight;
		    if (srcAspect > dstAspect) {
		       return srcWidth / dstWidth;
		    } else {
		       return srcHeight / dstHeight;
		    }
		    
		    

		}


}
