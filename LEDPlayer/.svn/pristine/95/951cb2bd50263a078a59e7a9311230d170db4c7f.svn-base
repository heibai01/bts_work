package com.led.player.window;

import java.util.ArrayList;
/**
 * 基本页。 对应多个窗口。
 * @author 1231
 *
 */
public class BasicPage implements PageInterface{
	/**
	 * 存放 windowtype字段信息，窗口类型。 一个即本页面下的窗口类型和顺序(顺序和数组存放顺序一样)
	 */
	private ArrayList<Integer> WindowTypeList = new ArrayList<Integer>();
	private ArrayList<UniversalWindow> universalwindowlist = new ArrayList<UniversalWindow>();
	private int PlayType;
	private int PageType;
	/**
	 * 一个节目页里设置一个 窗口类型属性……这个写法还能在牛逼点不
	 */
	private String windowtype;
	private HumitureWindow humiturewindow = null;
	private DigitalClockWindow digitalclockwindow = null;
	private CountDownWindow countdownwindow = null;
	private CameraWindow camerawindow = null;
	private AnalogClockWindow analogclockwindow = null;
	
	public Boolean AddUniversalWindow(UniversalWindow universalwindow){
		return universalwindowlist.add(universalwindow);
	}
	public UniversalWindow getUniversalWindow(int i){
		return universalwindowlist.get(i);
	}
	public ArrayList<UniversalWindow> getUniversalWindowList(){
		return universalwindowlist;
	}
	public void setPageType(int i){
		// TODO Auto-generated method stub
		this.PageType = i;
	}
	public void setPlayType(int i){
		// TODO Auto-generated method stub
		this.PlayType = i;
	}
	public int getPlayType(){
		// TODO Auto-generated method stub
		return PlayType;
	}
	public int getPageType(){
		// TODO Auto-generated method stub
		return PageType;
	}
	public void setWindowType(String str) {
		// TODO Auto-generated method stub
		windowtype = str;
		
	}
	public String getWindowType() {
		// TODO Auto-generated method stub
		return windowtype;
	}
	public void Addwindowlist(Integer i) {
		// TODO Auto-generated method stub
		WindowTypeList.add(i);
	}
	/**
	 * 存放 windowtype字段信息，窗口类型。 一个即本页面下的窗口类型和顺序(顺序和数组存放顺序一样)
	 */
	public ArrayList<Integer> getWindowList(){
		return WindowTypeList;
	}
	public void setAnalogClockWindow(AnalogClockWindow m) {
		// TODO Auto-generated method stub
		analogclockwindow = m;
	}
	public void setCameraWindow(CameraWindow m) {
		// TODO Auto-generated method stub
		camerawindow = m;
	}
	public void setCountDownWindow(CountDownWindow m) {
		// TODO Auto-generated method stub
		countdownwindow = m;
	}
	public void setDigitalClockWindow(DigitalClockWindow m) {
		// TODO Auto-generated method stub
		digitalclockwindow = m;
	}
	public void setHumitureWindow(HumitureWindow m) {
		// TODO Auto-generated method stub
		humiturewindow = m;
	}
	
	public AnalogClockWindow getAnalogClockWindow() {
		// TODO Auto-generated method stub
		return analogclockwindow;
	}
	public CameraWindow getCameraWindow() {
		// TODO Auto-generated method stub
		return camerawindow;
	}
	public CountDownWindow getCountDownWindow() {
		// TODO Auto-generated method stub
		return countdownwindow;
	}
	public DigitalClockWindow getDigitalClockWindow() {
		// TODO Auto-generated method stub
		return digitalclockwindow;
	}
	public HumitureWindow getHumitureWindow() {
		// TODO Auto-generated method stub
		return humiturewindow;
	}

}
