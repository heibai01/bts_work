package com.led.player.window;

/**
 * 倒计时窗属性。
 * @author 1231
 *
 */
public class CountDownWindow {

	private int StartX = 0;
	private int StartY = 0;
	private int Width;
	private int Heiht;
	private int BorderType;
//	private int BgClr;
	private String EndDate;
	private String EndTime;
	private Boolean ShowDate = true;
	private Boolean ShowHour = true;
	private Boolean ShowMins = true;
	private Boolean ShowSeconds = true;
	private Boolean MultiLine = true;
	private TextViewAttr textviewattr = new TextViewAttr();
	
	public void setStartX(int par){
		this.StartX = par;
	}
	public void setStartY(int par){
		this.StartY = par;
	}
	public void setWidth(int par){
		this.Width = par;
	}
	public void setHeight(int par){
		this.Heiht = par;
	}
	public void setBorderType(int par){
		this.BorderType = par;
	}
	
	public void setEndDate(String enddate){
		this.EndDate = enddate;
	}
	public void setEndTime(String endtime){
		this.EndTime = endtime;
	}
	public void setShowDate(Boolean showdate){
		this.ShowDate = showdate;
	}
	public void setShowHour(Boolean showtime){
		this.ShowHour = showtime;
	}
	public void setShowMins(Boolean showtime){
		this.ShowMins = showtime;
	}
	public void setShowSeconds(Boolean showtime){
		this.ShowSeconds = showtime;
	}
	public void setBgClr(int par){
		this.textviewattr.setBgClr(par);
	}
	public void setWordClr(int clr){
		this.textviewattr.setWordClr(clr);
	}
	public void setFixedWordClr(int clr){
		this.textviewattr.setFixedWordClr(clr);
	}
	public void setLanguage(boolean language){
		this.textviewattr.setLanguage(language);
	}
	public void setContent(String Content){
		this.textviewattr.setContent(Content);
	}
	public void setItalic(int clr){
		this.textviewattr.setItalic(clr);
	}
	public void setUnderline(int clr){
		this.textviewattr.setUnderline(clr);
	}
	public void setFaceName(int i){
		this.textviewattr.setFaceName(i);
	}
	public void setIfHeight(int height){
		this.textviewattr.setHeight(height);
	}
	public void setWeight(int weight){
		this.textviewattr.setWeight(weight);
	}
	public void setMultiLine(Boolean Line){
		this.MultiLine = Line;
	}
	
	public TextViewAttr getTextViewAttr(){
		return textviewattr;
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
	public String getEndDate(){
		return EndDate;
	}
	public String getEndTime(){
		return EndTime;
	}
	public Boolean getShowDate(){
		return ShowDate;
	}
	public Boolean getShowHour(){
		return ShowHour;
	}
	public Boolean getShowMins(){
		return ShowMins;
	}
	public Boolean getShowSeconds(){
		return ShowSeconds;
	}
	public Boolean getMultiLine(){
		return MultiLine;
	}
}
