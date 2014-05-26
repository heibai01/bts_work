package com.led.player.defview.gif3;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Base64;
import android.widget.ImageView;
/**
 * 边播放便解析
 * @author 1231
 *
 */
public class GifImageView extends ImageView implements GifDecodeInterface,PrepareListener{
	
	private Executor mExecutor;
	private Handler mHandler = new Handler();
	private GifHandler mGifHandler;
	private Runnable mGifRun;
	private Uri mUri;
	private static final int FIX_DELAY = 40;//default 20
	
	
	public GifImageView(Context context) {
		super(context);
		mExecutor = Executors.newCachedThreadPool();
//		mExecutor = Executors.newFixedThreadPool(4);
	}
	
	public GifImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mExecutor = Executors.newCachedThreadPool();
//		mExecutor = Executors.newFixedThreadPool(4);
	}
	
	
	/**������Դ������gif*/
	public void setCurrentGif(final Uri uri) throws FileNotFoundException{
		mUri = uri;
        final InputStream data = getContext().getContentResolver().openInputStream(uri);
        mExecutor.execute(new Runnable() {
			
			public void run() {
	           
	   		if (null != mGifHandler) {
	   			recycleGif();
			}
   			mGifHandler = new GifHandler();
   			mGifHandler.setListener(GifImageView.this);
			mGifHandler.initGifData(getContext(), data,Base64.encodeToString(uri.toString().getBytes(), 0).trim());
			}
		});
	}
	
	
    /**�ͷ�������Դ����(Ԥ���ص�*/
	public void recycleGif() {
//		System.out.println("recycleGif");
		if (null != mHandler) {
//			System.out.println("这个时候mHandler不为null的。。。。。。");
			mHandler.removeCallbacks(mGifRun);
			mGifRun = null;
		}
		if (null != mGifHandler) {
//			System.out.println("这个时候的mGifHandler也不为null。。。。。。。。！！！！。。。。");
			mGifHandler.setListener(null);
			mGifHandler.resume();
			mGifHandler.destroy();
			mGifHandler = null;
		}
	}
	
    private void showBitmap(final Bitmap bm) {
        mHandler.post(new Runnable() {
			
				public void run() {
					setImageBitmap(bm);
				}
			});
    }
    
    /**��ͣ*/
    public void pause(){
    	mGifHandler.pause();
    }
    /**����*/
    public void resume(){
    	mGifHandler.resume();
    }
    
	public void onDecodeEnd(int status) {
//		System.out.println("status = " +status);
		if(status == GIFDecode.STATUS_OK){
			try {
				setCurrentGif(mUri);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void onPrepare(int count, Bitmap image, int delay) {
		showBitmap(image);
	
		try{
			if(delay <= 0){//make sure it's vailid
				delay = 80;
			}else if(delay > 40){
				delay = delay - FIX_DELAY;
			}
//System.out.println("delay: "+delay+"---thread id:"+Thread.currentThread().getId()+"\n--- thread name:"+Thread.currentThread().getName());			
			Thread.sleep(delay);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		recycleGif();
	}

}
