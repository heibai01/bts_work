package com.led.weatherdemo.util;

import android.content.Context;
import android.provider.Settings.Secure;

/**
 * 获取一些系统及硬件的信息
 * 
 * @author heibai
 * @company http://www.bts-led.com/
 * @date 2014年5月24日
 */
public class SystemInfo {
	/**
	 * 安卓终端唯一id标识
	 */
	private static String mAndroidId;
	private static char[] filterStr = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z' };
	private static char[] filterStrtoNum = { '7', '3', '8', '1', '5', '8', '0',
			'4', '2', '8', '2', '3', '5', '7', '3', '9', '5', '4', '1', '5',
			'6', '4', '3', '2', '1', '9' };

	private SystemInfo() {
	}

	public static Long getAndroidId(Context context) {
		String mAndroidIdStr = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		mAndroidIdStr = mAndroidIdStr.replace("-", "").replace(" ", "")
				.toUpperCase();
		mAndroidId = getLongAndroidId(mAndroidIdStr);
		return Long.parseLong(mAndroidId);
	}

	/**
	 * 根据android id返回一个数字字符串
	 * 
	 * @param parm
	 * @return
	 */
	private static String getLongAndroidId(String parm) {
		int tempi = -1;
		String tempStr = "";
		StringBuilder tempNum = new StringBuilder("");
		for (int i = 0; i < parm.length(); i++) {
			tempi = -1;
			for (int j = 0; j < filterStr.length; j++) {
				if (filterStr[j] == parm.charAt(i)) {
					tempi = j;
				}
			}
			if ((tempi >= 0) && (tempi <= 26)) {
				tempStr = tempStr + filterStrtoNum[tempi];
			}

			if (tempi == -1) {
				tempNum.append(parm.charAt(i));
			}
		}
		tempStr = tempStr + tempNum;

		if (tempStr.length() == 16)
			return tempStr;
		else
			return tempStr;
	}

}
