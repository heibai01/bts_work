package com.heibai.modeloclock.bean;

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
	private int heiht;
	/**
	 * 边框类型?
	 */
	private int borderType;
	/**
	 * 背景颜色
	 */
	private int bgClr;
	/**
	 * 背景字
	 */
	private String bgWord;
	/**
	 * 是否显示日期
	 */
	private Boolean showDate = true;
	/**
	 * 是否显示星期
	 */
	private Boolean showWeek = true;
	/**
	 * 时钟类型,0模拟时钟 1数字时钟
	 */
	private int clkType;
	
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

	public int getHeiht() {
		return heiht;
	}

	public void setHeiht(int heiht) {
		this.heiht = heiht;
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

	public String getBgWord() {
		return bgWord;
	}

	public void setBgWord(String bgWord) {
		this.bgWord = bgWord;
	}

	public Boolean getShowDate() {
		return showDate;
	}

	public void setShowDate(Boolean showDate) {
		this.showDate = showDate;
	}

	public Boolean getShowWeek() {
		return showWeek;
	}

	public void setShowWeek(Boolean showWeek) {
		this.showWeek = showWeek;
	}

	public int getClkType() {
		return clkType;
	}

	public void setClkType(int clkType) {
		this.clkType = clkType;
	}

}
