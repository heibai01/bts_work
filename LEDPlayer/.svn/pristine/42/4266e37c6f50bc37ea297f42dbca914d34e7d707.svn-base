package com.led.player.window;

import java.util.ArrayList;

import android.graphics.Color;
import android.util.Log;
/**
 * 通用窗的一些属性封装。。包括了视屏资源的集合、图片资源的集合、文字资源的集合
 * @author 1231
 *
 */
public class UniversalWindow {
	
	private ArrayList<VideoVo> videoVolist = new ArrayList<VideoVo>();
	private ArrayList<ImageVo> iamgeVolist = new ArrayList<ImageVo>();
	private ArrayList<TextVo> textVolist = new ArrayList<TextVo>();
	/**
	 * 存放 每个资源文件的  ProType 字段信息。 表示一个通用窗中播放文件的顺序。和集合数据存放顺序对应
	 * --文件类型（0：图片或Gif，1：视频，2：视频，3：HTML文件，4：TXT文件，5：PPT文件，6：DOC文件，7：EXCEL文件，8：SWF文件，21：复杂文本（rtf文件），22：单行文本，23：静态文本，24：走马灯，25：模拟时钟，26：数字时钟，27：倒计时，28：天气，29：网页，30：视频设备（摄像头、电视）
	 * 不曾用到
	 */
	private ArrayList<Integer> playerlist = new ArrayList<Integer>();
	
	private int videocount = 0;
	private int imagecount = 0;
	private int textcount = 0;
	
	private int WindowRotation = 0;
	public int startX = 0;
	public  int startY = 0;
	public int width;
	public int heiht;
	public int BorderType;
	public Color BorderColor;
	
	/**
	 * 窗口名 如 "imagewindow"，艹，固然是大婶制作。
	 */
	private String protype;
	
	public void setWindowRotation(int par){
		this.WindowRotation = par;
	}
	public void setStartX(int par){
		this.startX = par;
	}
	public void setStartY(int par){
		this.startY = par;
	}
	public void setWidth(int par){
		this.width = par;
	}
	public void setHeiht(int par){
		this.heiht = par;
	}
	public void setBorderType(int par){
		this.BorderType = par;
	}
	public void setBorderColor(Color par){
		this.BorderColor = par;
	}
	public void setProType(String str){
		this.protype = str;
	}
	public void AddImageVo(ImageVo imageVo){
		iamgeVolist.add(imageVo);
	}
	public void AddVideoVo(VideoVo videoVo){
		videoVolist.add(videoVo);
	}
	public void AddTextVo(TextVo textVo){
		textVolist.add(textVo);
	}
	public void Addplayerlist(Integer i){
		playerlist.add(i);
	}
	
	public void Display(){
		Log.i("UniversalWindow", "WindowRotation "+WindowRotation+"  StartX  "
				+startX+" StartY  "+startY+" Width "+width+"  Heiht  "+heiht
				+"  BorderType  "+BorderType+"  BorderColor  "+BorderColor);
		for(int i = 0;i<playerlist.size();i++){
			Log.i("UniversalWindow", "  "+playerlist.get(i));
		}
	}
	
	public int getWindowRotation(){
		return WindowRotation;
	}
	public int getStartX(){
		return startX ;
	}
	public int getStartY(){
		return startY;
	}
	public int getWidth(){
		return width ;
	}
	public int getHeiht(){
		return heiht ;
	}
	public int getBorderType(){
		return BorderType;
	}
	public Color getBorderColor(){
		return BorderColor;
	}
	public String getProType(){
		return protype;
	}
	
	public int getVideoCount(){
		if(videocount>=videoVolist.size()){
			videocount = 0;
		}
		return videocount++;
	}
	public int getImageCountAdd(){
		if(imagecount>=iamgeVolist.size()){
			imagecount = 0;
		}
		return imagecount++;
	}
//	public int getImageCount(){
//		return imagecount;
//	}
	public int getTextCount(){
		if(textcount>=textVolist.size()){
			textcount = 0;
		}
		return textcount++;
	}

//	public void setVideoCount(){
//		videocount = 0;
//	}
//	public void setImageCount(){
//		imagecount = 0;
//	}
//	public void setTextCount(){
//		textcount = 0;
//	}
	
	public VideoVo getVideoVo(int i){
		return videoVolist.get(i);
	}
	public ImageVo getImageVo(int i){
		return iamgeVolist.get(i);
	}
	public TextVo getTextVo(int i){
		return textVolist.get(i);
	}
	public ArrayList<VideoVo> getVideoVoList(){
		return videoVolist;
	}
	public ArrayList<ImageVo> getImageVoList(){
		return iamgeVolist;
	}
	public ArrayList<TextVo> getTextVoList(){
		return textVolist;
	}
	/**
	 * 设置FileType用到了
	 * 存放 每个资源文件的  ProType 字段信息。 表示一个通用窗中播放文件的顺序。和集合数据存放顺序对应
	 * --文件类型（0：图片或Gif，1：视频，2：视频，3：HTML文件，4：TXT文件，5：PPT文件，6：DOC文件，7：EXCEL文件，8：SWF文件，21：复杂文本（rtf文件），22：单行文本，23：静态文本，24：走马灯，25：模拟时钟，26：数字时钟，27：倒计时，28：天气，29：网页，30：视频设备（摄像头、电视）
	 * 
	 * BaseFileIndexArray[winindex] 数组对应窗口集合，指定winindex的值对应指定窗口目前的fileIndex值(当前窗口目前的文件索引，这个索引值与文件类型对应的)
	 */
	public ArrayList<Integer> getPlayerList(){
		return playerlist;
	}
	@Override
	public String toString() {
		return "UniversalWindow [videoVolist=" + videoVolist + ", iamgeVolist="
				+ iamgeVolist + ", textVolist=" + textVolist + ", playerlist="
				+ playerlist + ", videocount=" + videocount + ", imagecount="
				+ imagecount + ", textcount=" + textcount + ", WindowRotation="
				+ WindowRotation + ", StartX=" + startX + ", StartY=" + startY
				+ ", Width=" + width + ", Heiht=" + heiht + ", BorderType="
				+ BorderType + ", BorderColor=" + BorderColor + ", protype="
				+ protype + "]";
	}

	
	
}
