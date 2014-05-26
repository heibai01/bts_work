package com.led.player.window;

import android.graphics.Color;
import android.util.Log;
/**
 * 元数据。 对应xml中 ProgramData 对象。 只解析了有用的字段。
 * @author 1231
 *
 */
public class ImageVo {
	
	public int  BgClr = Color.parseColor("#FFFFFF");
	/**
	 * 比例：效果方式（0-原始尺寸 1-铺满 2-适合高度 3-适合宽度）有争议---最后决定是：0-铺满 1-原始比例 2-4:3 3-5:4 4-16:9
	 */
	public int Description = 1;	//比例：效果方式（0-原始尺寸 1-铺满 2-适合高度 3-适合宽度）有争议---最后决定是：0-铺满 1-原始比例 2-4:3 3-5:4 4-16:9
	public int Effects = 1;	//特效类型
	public int StayLine = 5;  //停留时间
	private int TxtSpeed = 2000; //特技速度
	/**
	 * 都是这种结构的 ：program/5.gif 为了和设备路劲拼凑成一个完整的路径
	 */
	public String FilePath;
	public Boolean Transparent = false;
	
	public void setBgClr(int clr){
		this.BgClr = clr;
	}
	public void setTransparent(Boolean clr){
		this.Transparent = clr;
	}
	public void setDescription(int par){
		this.Description = par;
	}
	public void setEffects(int par){
		this.Effects = par;
	}
	public void setStayLine(int par){
		this.StayLine = par;
	}
	public void setTxtSpeed(int par){
		this.TxtSpeed = (101 - par)*100;
	}
	
	public int getBgClr(){
		return BgClr;
	}
	public Boolean getTransparent(){
		return Transparent;
	}
	/**
	 * 比例：效果方式（0-原始尺寸 1-铺满 2-适合高度 3-适合宽度）有争议---最后决定是：0-铺满 1-原始比例 2-4:3 3-5:4 4-16:9
	 * @return
	 */
	public int getDescription(){
		return Description;
	}
	public int getEffects(){
		return Effects;
	}
	public int getStayLine(){
		return StayLine;
	}
	public int getTxtSpeed(){
		return TxtSpeed;
	}
	/**
	 * 都是这种结构的 ：program/5.gif 为了和设备路劲拼凑成一个完整的路径
	 */
	public void setFilePath(String str){
		this.FilePath = str;
	}
	
	/**
	 * 都是这种结构的 ：program/5.gif 为了和设备路劲拼凑成一个完整的路径
	 * @return
	 */
	public String getFilePath(){
		return FilePath;
	}
	
	public void Display(){
		Log.i("ImageWindow", "Description  "+Description+"  Effects  "
				+Effects+"  StayLine  "+StayLine+"  TxtSpeed  "+TxtSpeed);
	}

}
