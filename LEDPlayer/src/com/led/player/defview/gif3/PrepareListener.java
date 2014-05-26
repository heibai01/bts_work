package com.led.player.defview.gif3;

import android.graphics.Bitmap;

public interface PrepareListener {
	
	public void onPrepare(int count, Bitmap image, int delay);
	public void onDecodeEnd(int status);
}
