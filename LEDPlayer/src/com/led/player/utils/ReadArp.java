package com.led.player.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.led.player.moudle.ArpInfo;

/**
 * 读取arp数据信息，为了设置我以太网卡的ip
 * @author 1231
 *
 */
public class ReadArp {
	private static final String PATH = "/proc/net/arp";
	/**
	 * 比较并且获得 一个arp信息
	 * @return
	 */
	public static ArpInfo  compAndGetArpInfo(List<ArpInfo> arpInfos){
		File file = new File(PATH);
		if (file.isFile()==false||file.exists()==false) {
			Log.e("ReadArp", "arp文件不存在");
			return null;
		}
		BufferedReader br = null;
		 try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String temp = null;
			br.readLine();//去掉第一行
			List<ArpInfo> arpInfos_0 = new ArrayList<ArpInfo>();
			ArpInfo arpInfo = null;
			while ((temp=br.readLine())!=null) {
//				Log.e("ReadArp", "每一行："+temp);
				String[] data = temp.split(" +");
				String ip = data[0];
				String mac = data[3];
				Log.e("ip和mac", "ip和mac:"+ip+"  "+mac);
				arpInfo = new ArpInfo(ip, mac);
				arpInfos_0.add(arpInfo);
			}
			if (arpInfos_0.size()==0||arpInfos.size()==0) {
				return null;
			}
			
			for (ArpInfo arpInfo_0 : arpInfos_0) {
				for (ArpInfo arpInfo_1 : arpInfos) {
					if (arpInfo_0.equals(arpInfo_1)) {
						Log.e("ReadArp", "找到了:"+arpInfo_0);
						return arpInfo_0;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("ReadArp", "文件读取失败~~");
			e.printStackTrace();
		} finally{
			if (br!=null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
		
	}
}
