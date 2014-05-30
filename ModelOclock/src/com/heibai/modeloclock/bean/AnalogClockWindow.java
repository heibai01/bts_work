package com.heibai.modeloclock.bean;

import android.R.integer;

/**
 * 模拟时钟属性封装。
 * 
 * @author 1231
 * 
 */
public class AnalogClockWindow {
	/**
	 * 开始坐标
	 */
	private int startX = 0;
	private int startY = 0;
	/**
	 * 宽高
	 */
	private int width;
	private int height;
	/**
	 * 边框类型?
	 */
	private int borderType;
	/**
	 * 背景颜色
	 */
	private int bgClr;
	/**
	 * 是否显示日期 1显示,0不显示, 默认显示
	 */
	private int showDate = 1;
	/**
	 * 是否显示星期
	 */
	private int showWeek = 1;
	/**
	 * 时钟类型,0模拟时钟 1数字时钟
	 */
	private int clkType;
	/**
	 * 皮肤0为默认绘制,1皮肤一号,2皮肤二号,3皮肤三号,4皮肤四号
	 */
	private int skin;
	/**
	 * 时钟透明度(1-100)
	 */
	private int transparence;
	/**
	 * 时针颜色值
	 */
	private int hourHandColor;
	/**
	 * 分针颜色值
	 */
	private int minuteHandColor;
	/**
	 * 秒针颜色值
	 */
	private int secondHandColor;
	/**
	 * 表盘时点颜色值
	 */
	private int hourPointColor;
	/**
	 * 表盘分点颜色值
	 */
	private int minutePointColor;
	/**
	 * 地名
	 */
	private String address;

	
	
	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getBorderType() {
		return borderType;
	}

	public void setBorderType(int borderType) {
		this.borderType = borderType;
	}

	public int getBgClr() {
		return bgClr;
	}

	public void setBgClr(int bgClr) {
		this.bgClr = bgClr;
	}

	public int getShowDate() {
		return showDate;
	}

	public void setShowDate(int showDate) {
		this.showDate = showDate;
	}

	public int getShowWeek() {
		return showWeek;
	}

	public void setShowWeek(int showWeek) {
		this.showWeek = showWeek;
	}

	public int getClkType() {
		return clkType;
	}

	public void setClkType(int clkType) {
		this.clkType = clkType;
	}

	public int getSkin() {
		return skin;
	}

	public void setSkin(int skin) {
		this.skin = skin;
	}

	public int getTransparence() {
		return transparence;
	}

	public void setTransparence(int transparence) {
		this.transparence = transparence;
	}

	public int getHourHandColor() {
		return hourHandColor;
	}

	public void setHourHandColor(int hourHandColor) {
		this.hourHandColor = hourHandColor;
	}

	public int getMinuteHandColor() {
		return minuteHandColor;
	}

	public void setMinuteHandColor(int minuteHandColor) {
		this.minuteHandColor = minuteHandColor;
	}

	public int getSecondHandColor() {
		return secondHandColor;
	}

	public void setSecondHandColor(int secondHandColor) {
		this.secondHandColor = secondHandColor;
	}

	public int getHourPointColor() {
		return hourPointColor;
	}

	public void setHourPointColor(int hourPointColor) {
		this.hourPointColor = hourPointColor;
	}

	public int getMinutePointColor() {
		return minutePointColor;
	}

	public void setMinutePointColor(int minutePointColor) {
		this.minutePointColor = minutePointColor;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
