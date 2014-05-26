package com.led.player.window;

import android.graphics.Color;
/**
 * 外部视频窗属性封装。
 * @author 1231
 *
 */
public class CameraWindow {

	private int StartX = 0;
	private int StartY = 0;
	private int Width;
	private int Heiht;
	private int BorderType;
	private Color BorderColor;
	
	public void setStartX(int par){
		this.StartX = par;
	}
	public void setStartY(int par){
		this.StartY = par;
	}
	public void setWidth(int par){
		this.Width = par;
	}
	public void setHeiht(int par){
		this.Heiht = par;
	}
	public void setBorderType(int par){
		this.BorderType = par;
	}
	public void setBorderColor(Color par){
		this.BorderColor = par;
	}
	
	public int getStartX(){
		return StartX ;
	}
	public int getStartY(){
		return StartY;
	}
	public int getWidth(){
		return Width ;
	}
	public int getHeiht(){
		return Heiht ;
	}
	public int getBorderType(){
		return BorderType;
	}
	public Color getBorderColor(){
		return BorderColor;
	}
}
