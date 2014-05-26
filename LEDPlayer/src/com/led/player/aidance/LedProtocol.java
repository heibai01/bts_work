package com.led.player.aidance;

import android.R.integer;

public class LedProtocol {
	
	public final static int PLAYER_END = 3;
	public final static int THREAD_END = 4;
	
	public static final int MSG_SERVICE_APK  = 6;
	public final static int EXTERNAL_XML_PARSER_FINISH = 7;
	public final static int INTERIOR_XML_PARSER_FINISH = 15;
	public final static int ACTIVITY_START_PARSER_XML = 16;
	public final static int ACTIVITY_START_PARSER_FINISH = 17;
	public static final int LED_PARSER_FINISH = 8;
	
	public static final int PLAYER_IMAGE = 9;
	public static final int PLAYER_VIDEO = 10;
	public static final int PLAYER_TEXT = 11;
	public static final int ISGLOBALPAGE = 12;
	public static final int ISBASEPAGE = 13;
	
	public static final int ISOPEPLAYER = 14;
	
	public final static int IMAGE_VIEW = 0;
	public final static int VIDEO_VIEW = 1;
	public final static int TEXT_VIEW = 2;
	public final static int DISPLAYER_VIEW = 0x04;
	/**
	 *  创建 窗口
	 */
	public final static int CREATE_WINDOW = 0x05;
	public final static int DISPLAYER_NEXT_VIEW = 0x06;
	public final static int BEGIN_DISPLAYER_VIEW = 0x07;
	/**
	 * 移除所有的播放视图
	 */
	public final static int REMOVE_ALL_VIEW = 0x08;
	public final static int CREATE_SURFACE_FINISH = 0x09;
	public final static int START_PLAYER = 0x10;
	public final static int ACCREDIT_FAIL = 0x11;
	
	public final static int STATUS_SEND_DATA_TO_UART = 18;
	public final static int STATUS_RECE_JPEG_DATA = 19;
	public final static int STATUS_STOP_JPEG_DATA = 20;
	public final static int SEND_DATA_F_UART_T_NET = 0x20;
	public final static int STOP_COUNTDOWN = 21;
	public final static int START_COUNTDOWN = 22;
	public final static int SEND_HUMIDUTY_T_APP = 23;
	
	
	/**
	 * 关闭wifi热点
	 */
	public final static int OFF_AP=0x21;
	/**
	 * 发送测试消息
	 */
	public final static int TEST_MSG=0x22;
	/**
	 * config目录下的network文件没找到
	 */
	public final static int NETWORK_NOT_FOUND=0x23;
	/**
	 * config目录下的network文件 解析失败
	 */
	public final static int NETWORK_PARSE_FAIL=0x24;
	/**
	 * 解析network文件成功
	 */
	public final static int NETWORK_PARSE_OK = 0x25;

	public final static int TOAST_MSG = 0X26; 
	
	/**
	 * 5秒后自动关屏
	 */
	public final static int OFF_SCRN_TST = 0x27;
	
	/**
	 * 制卡式播放，如果是interior.xml拷贝播放，当指定需要拷贝的文件夹下文件不存在或总大小为0时提示错误信息。
	 */
	public static final int SOURCE_ZERO_SIZE = 0X28;
	/**
	 * 制卡式拷贝播放时，开始显示进度提示框
	 */
	public static final int BEGIN_COPY = 0X29;
	
	/**
	 * 制卡式拷贝播放时，复制进行中
	 */
	public static final int COPYING = 0X30;
	/**
	 * 制卡式拷贝播放,文件拷贝已经结束
	 */
	public static final int COPY_END = 0X31;
	/**
	 * 制卡式拷贝播放,文件拷贝发生错误，比如用户在拷贝时把U盘给拔掉了。
	 */
	public static final int COPY_ERROR = 0X32;
	/**
	 * 可用的空间不足以拷贝U盘上的文件进去。
	 */
	public static final int COPY_NEED_MORE_ROM = 0X33;
	/**
	 * 在拷贝过程中，程序异常退出，
	 */
	public static final int COPY_CLEAR_MSG = 0X34;
	
	/**
	 * 创建同步点播播放的窗口
	 */
	public static final int CREATE_SYNC_DEMAND_WINDOW = 0X35;
	
}
