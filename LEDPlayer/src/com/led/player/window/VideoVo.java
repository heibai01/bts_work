package com.led.player.window;

import android.util.Log;
/**
 * 视频Vo,资源的一些属性。
 * @author 1231
 *
 */
public class VideoVo {
	
	public int VideoProportion = 0;
	public int VoiceValue = 100;
	public String FilePath;
	
	public void setVideoProportion(int par){
		this.VideoProportion = par;
	}
	public void setVoiceValue(int par){
		this.VoiceValue = par;
	}

	/*
	 * 0原始尺寸，1全屏
	 */
	public int getVideoProportion(){
		return VideoProportion;
	}
	public int getVoiceValue(){
		return VoiceValue;
	}
	public void setFilePath(String str){
		this.FilePath = str;
	}
	public String getFilePath(){
		return FilePath;
	}
	
	public void Display(){
		Log.i("VideoWindow", "VideoProportion  "+VideoProportion+"  VoiceValue  "
				+VoiceValue);
	}
	@Override
	public String toString() {
		return "VideoVo [VideoProportion=" + VideoProportion + ", VoiceValue="
				+ VoiceValue + ", FilePath=" + FilePath + "]";
	}
	
	
	
}
