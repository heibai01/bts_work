package com.led.player.aidance;

import java.util.ArrayList;

import android.os.Handler;

import com.led.player.window.AnalogClockWindow;
import com.led.player.window.CameraWindow;
import com.led.player.window.CountDownWindow;
import com.led.player.window.DigitalClockWindow;
import com.led.player.window.HumitureWindow;
/**
 * 代表某个节目页。 CreateWindow 消息时传递的这个对象的实例。。。
 * 要让Activity去创建的窗口对象 信息。  ，，
 * 开放式：包含走马灯类 LedTextInfoVo，通用窗口obj集合ArrayList<UniversalWindow>赋值null，winAmount通用窗口个数1
 * 		  指定线程的Handler，当前页面的startX,startY,和width，height???   这些参数……决定这个页面下的窗口的位置和高度，至少开放式需要。。
 * moudle
 * @author 1231
 *
 */
public class PageInfoVo {
	private Handler handler;
	private int startX;  //开放式播放：0
	private int startY;	 //开放式播放：0
	private int width;    //开放式播放：led_width_height
	private int heigth;    //开放式播放：led_width_height
	/**
	 * 当前 通用窗口 的数量
	 * 开放式播放时给1
	 */
	private int winAmount; //
	/**
	 * 这个放ArrayList<UniversalWindow> 放通用窗口
	 * 如果是开放式播放 给null
	 */
	private Object obj = null;  //开放式播放 这里给null，全局页面这给数组。
	private LedTextInfoVo ledtextInfoVo = null;		//开放式播放这里给值，全局页面播放这里给null
	private AnalogClockWindow analogclockwindow = null;
	private CameraWindow camerawindow = null;
	private CountDownWindow countdownwindow = null;
	private DigitalClockWindow digitalclockwindow = null;
	private HumitureWindow humiturewindow = null;


	/**
	 * 所有窗口的 存储顺序。(却只在创建其他窗口时用到)。//开放式播放这里给null(它是根据fileNames数组确定顺序)，全局页播放这里给值，基本页也赋值
	 */
	private ArrayList<Integer> Windowlist = null;  

	public void setHandler(Handler mhandler){
		this.handler = mhandler;
	}
	public void setStartX(int mstartX){
		this.startX = mstartX;
	}
	public void setStartY(int mstartY){
		this.startX = mstartY;
	}
	public void setWidth(int mwidth){
		this.width = mwidth;
	}
	public void setHeigth(int mheigth){
		this.heigth = mheigth;
	}
	/**
	 * 当前 通用窗口 的数量
	 */
	public void setWinCount(int mwincount){
		this.winAmount = mwincount;
	}
	/**
	 * 这个放ArrayList<UniversalWindow> 放通用窗口
	 */
	public void setObject(Object mobj){
		this.obj = mobj;
	}
	/**
	 * 包含走马灯类 LedTextInfoVo
	 * @param obj
	 */
	public void setLedTextInfo(LedTextInfoVo obj){
		this.ledtextInfoVo = obj;
	}
	public void setAnalogClockWindow(AnalogClockWindow obj){
		this.analogclockwindow = obj;
	}
	public void setCameraWindow(CameraWindow obj){
		this.camerawindow = obj;
	}
	public void setCountDownWindow(CountDownWindow obj){
		this.countdownwindow = obj;
	}
	public void setDigitalClockWindow(DigitalClockWindow obj){
		this.digitalclockwindow = obj;
	}
	public void setHumitureWindow(HumitureWindow obj){
		this.humiturewindow = obj;
	}
	public void setWindowlist(ArrayList<Integer> obj){
		this.Windowlist = obj;
	}

	public Handler getHandler(){
		return this.handler;
	}
	public int getStartX(){
		return this.startX;
	}
	public int getStartY(){
		return this.startY;
	}
	public int getWidth(){
		return this.width;
	}
	public int getHeigth(){
		return this.heigth;
	}
	/**
	 * 当前 通用窗口 的数量
	 * 开放式播放时给1
	 */
	public int getWinAmount(){
		return this.winAmount;
	}
	/**
	 * 这个放ArrayList<UniversalWindow> 放通用窗口
	 * ，他的存储顺序与解析的xml一致。在集合中索引值越小，当然越先播放。
	 */
	public Object getObject(){
		return this.obj;
	}
	public LedTextInfoVo getLedTextInfo(){
		return this.ledtextInfoVo;
	}
	public AnalogClockWindow getAnalogClockWindow(){
		return this.analogclockwindow;
	}
	public CameraWindow getCameraWindow(){
		return this.camerawindow;
	}
	public CountDownWindow getCountDownWindow(){
		return this.countdownwindow;
	}
	public DigitalClockWindow getDigitalClockWindow(){
		return this.digitalclockwindow;
	}
	public HumitureWindow getHumitureWindow(){
		return this.humiturewindow;
	}
	/**
	 * 放窗口的 存储顺序（主要给其他窗口用）。。/开放式播放这里给null(它是根据fileNames数组确定顺序)，全局页播放这里给值
	 * 窗口类型，有0代表通用窗口，1代表时钟窗口(要么数字时钟要么模拟时钟)，2代表温湿度窗口，3代表倒计时窗口，5代表外部视频窗口。
	 * 
	 */
	public ArrayList<Integer> getWindowlist(){
		return this.Windowlist;
	}

}
