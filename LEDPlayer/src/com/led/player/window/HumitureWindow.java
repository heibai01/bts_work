package com.led.player.window;

/**
 * 温湿度窗口属性。
 * @author 1231
 *
 */
public class HumitureWindow {

	private int StartX = 0;
	private int StartY = 0;
	private int Width;
	private int Height;
	private int BorderType;
	private int BgClr;
	/**
	 * 固定文字
	 */
	private String StrTemperature;
	/**
	 * 是否显示温度
	 */
	private boolean HasTemperature;
	/**
	 * 是否显示湿度
	 */
	private boolean HasHumidity;
	/**
	 * 是否显示单位
	 */
	private boolean DisplayUnit;
	/**
	 * 是否显示摄氏温度
	 */
	private boolean Centigrade;
	/**
	 * 固定文字
	 */
	private String StrHumidity;
	/**
	 * 多汗显示
	 */
	private Boolean MultiLine = true;
	/**
	 * 显示方式“居左，居中，居右”
	 */
	private int showType = 0;
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
		this.Height = par;
	}
	public void setBorderType(int par){
		this.BorderType = par;
	}
	
	public void setStrTemperature(String Temperature){
		this.StrTemperature = Temperature;
	}
	public void setStrHumidity(String Humidity){
		this.StrHumidity = Humidity;
	}
	public void setHasTemperature(Boolean Temperature){
		this.HasTemperature = Temperature;
	}
	public void setHasHumidity(Boolean Humidity){
		this.HasHumidity = Humidity;
	}
	public void setCentigrade(Boolean centigrade){
		this.Centigrade = centigrade;
	}
	public void setDisplayUnit(Boolean Unit){
		this.DisplayUnit = Unit;
	}
	public void setMultiLine(Boolean Line){
		this.MultiLine = Line;
	}
	public void setBgClr(int par){
		this.textviewattr.setBgClr(par);
	}
	public void setWordClr(int clr){
		this.textviewattr.setWordClr(clr);
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
	
	public int getStartX(){
		return StartX ;
	}
	public int getStartY(){
		return StartY;
	}
	public int getWidth(){
		return Width ;
	}
	public int getHeight(){
		return Height ;
	}
	public int getBorderType(){
		return BorderType;
	}
	public String getStrTemperature(){
		return StrTemperature;
	}
	public String getStrHumidity(){
		return StrHumidity;
	}
	public Boolean getMultiLine(){
		return MultiLine;
	}
	public Boolean getHasTemperature(){
		return HasTemperature;
	}
	public Boolean getHasHumidity(){
		return HasHumidity;
	}
	public Boolean getDisplayUnit(){
		return DisplayUnit;
	}
	public Boolean getCentigrade(){
		return Centigrade;
	}
	public TextViewAttr getTextViewAttr(){
		return textviewattr;
	}
	public int getShowType() {
		return showType;
	}
	public void setShowType(int showType) {
		this.showType = showType;
	}
	
}
