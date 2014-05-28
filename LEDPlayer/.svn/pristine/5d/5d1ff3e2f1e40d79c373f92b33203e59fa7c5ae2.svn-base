package com.led.player.window;

import java.util.ArrayList;
/**
 * 全局页和基本页的公用接口
 * @author 1231
 *
 */
public interface PageInterface {
	
	public Boolean AddUniversalWindow(UniversalWindow universalwindow);
	public UniversalWindow getUniversalWindow(int i);
	public ArrayList<UniversalWindow> getUniversalWindowList();
	public void setPageType(int i);
	public void setPlayType(int i);
	public int getPlayType();
	public int getPageType();
	public void setWindowType(String str);
	/**
	 * …………艹，艹
	 * @return
	 */
	public String getWindowType();
	public void Addwindowlist(Integer i);
	/**
	 * 存放 windowtype字段信息（0：通用窗口，1：时钟窗口，2：天气窗口，3：倒计时窗口，4：走马灯窗口((没这个)，5：视频设备输入窗口），窗口类型。 一个即本页面下的窗口类型和顺序(顺序和数组存放顺序一样)
	 */
	public ArrayList<Integer> getWindowList();
	public void setAnalogClockWindow(AnalogClockWindow m);
	public void setCameraWindow(CameraWindow m);
	public void setCountDownWindow(CountDownWindow m);
	public void setDigitalClockWindow(DigitalClockWindow m);
	public void setHumitureWindow(HumitureWindow m);
	
	public AnalogClockWindow getAnalogClockWindow();
	public CameraWindow getCameraWindow();
	public CountDownWindow getCountDownWindow();
	public DigitalClockWindow getDigitalClockWindow();
	public HumitureWindow getHumitureWindow();

}
