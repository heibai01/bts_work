package com.led.player.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

import com.led.player.moudle.APInfo;
import com.led.player.moudle.ConfigInfo;

/**
 * 解析类xml文件
 * @author 1231
 *
 */
public class PullParseBTS {
	/**
	 *从输入流中解析xml文件，得到moudle，然后需要判断这个Apinfo是热点还是终端看它的wifi_type属性
	 * @param is	xml文件的输入流
	 * @return		
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	public static APInfo getParseObject() throws XmlPullParserException, IOException{
		StringBuilder sb = modifyHead();
		
		APInfo apInfo = null;
//		XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
		XmlPullParser pullParser = Xml.newPullParser();//这个是安卓提供的工具类xml比上面更好点，效果一样。
		pullParser.setInput(new FileInputStream("/mnt/extsd/config/Network"), "UTF-8");//为pull解析器设置好要解析的数据。
		//pull解析器 开始解析，先把 document哪一截 读入到一个字符数组中。如<?xml version="1.0" encoding="UTF-8"?>这个被读取，然后就不动了
		int event = pullParser.getEventType();
Log.e("PullParseBTS"	, "解析开始了第一个事件")		;

		int wifi_mode = -1;
		//下面是判断 事件，然后处理。
		while (event!=XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if (pullParser.getName().equals("wifi_mode")) {
Log.e("PullParseBTS", "wifi文件解析开始")			;		
					apInfo = new APInfo();
					//pullparser 解析器指针正指向当前 标签，所以可以用解析器获得标签的id和名称
					wifi_mode = Integer.parseInt(pullParser.nextText());
					apInfo.wifi_type = wifi_mode;
//					if (wifi_mode==2) {
//						//禁用wifi模式，wifi网卡不可用和wifi热点不可用
//						break;
//					}
				} else if (pullParser.getName().equals("ap_ssid")&&wifi_mode==0) {
					//热点模式
					String ap_name = pullParser.nextText();//取的 文本内容。
					apInfo.ssid=ap_name;
				}else if (pullParser.getName().equals("ap_password")&&wifi_mode==0) {
//					热点模式
					apInfo.psk = pullParser.nextText();
				} else if (pullParser.getName().equals("client_ssid")&&wifi_mode==1) {
					//终端模式
					apInfo.ssid=pullParser.nextText();
				} else if (pullParser.getName().equals("client_password")&&wifi_mode==1) {
					//终端模式
					apInfo.psk=pullParser.nextText();
				}
				break;
			case XmlPullParser.END_DOCUMENT: //这是不可能的啦，什么写法啊，年少的我……
				break;
			default:
				break;
			}
				event = pullParser.next();	//让解析器解析后面一个 node 不然会不动了的。不往后解析了。
			
		}
		//3.还原修改前的文件
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/mnt/extsd/config/Network")));
		bw.write(sb.toString());
		bw.flush();
		bw.close();
		
		return apInfo;
		
	}

	/**
	 * 修改添加xml格式头
	 * @param is
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static StringBuilder modifyHead( ) throws IOException,
			FileNotFoundException, UnsupportedEncodingException {
		//1.先给文件加上头：<?xml version="1.0" encoding="UTF-8"?>
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/mnt/extsd/config/Network")));
		StringBuilder sb = new StringBuilder();
		StringBuilder new_sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		String readStr = null;
		while ((readStr=br.readLine())!=null) {
			sb.append(readStr+"\n");
		}
Log.e("解析xml文件", "Network文件中的内容："+sb)	;	
		//2.把新的加了头的xml文件写入到配置文件Network中。
		FileOutputStream fi = new FileOutputStream("/mnt/extsd/config/Network");
		fi.write(new_sb.append(sb).toString().getBytes("utf-8"));
		fi.flush();
		fi.close();
		br.close();
		return sb;
	}
	
	/**
	 * 解析出配置文件对象
	 * @return
	 */
	public static ConfigInfo parseOutConfig(){
		ConfigInfo configInfo = null;
		XmlPullParser parser = Xml.newPullParser();
		
		try {
			//解析Server文件
			parser.setInput(new FileInputStream("/mnt/sdcard/config/Server"), "utf-8");
			int event = parser.getEventType();
			while (event!=XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase("server")) {
						configInfo = new ConfigInfo();
					}else
					if (name.equalsIgnoreCase("netport")) {
						configInfo.netport = Short.parseShort(parser.nextText());
					}else
					if (name.equalsIgnoreCase("controlport")) {
						configInfo.controlport = Short.parseShort(parser.nextText());
					}else
					if (name.equalsIgnoreCase("ip")) {
						configInfo.ip = parser.nextText();
					}
					break;
				}
				event=parser.next();
			}
			//解析Network文件
			parser.setInput(new FileInputStream("/mnt/sdcard/config/Network"), "utf-8");
			int event_network = parser.getEventType();
			while (event_network!=XmlPullParser.END_DOCUMENT) {
				switch (event_network) {
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase("nettype")) {
						configInfo.nettype = Byte.parseByte(parser.nextText());
					}else if (name.equalsIgnoreCase("dhcp")) {
						configInfo.dhcp = Byte.parseByte(parser.nextText());
					}
					break;
				}
				event_network= parser.next();
			}
			//解析Displays
			parser.setInput(new FileInputStream("/mnt/sdcard/config/Displays"),"utf-8");
			int event_display = parser.getEventType();
			while (event_display!=XmlPullParser.END_DOCUMENT) {
				switch (event_display) {
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase("displayid")) {
						configInfo.displayid = parser.nextText();
					}else if (name.equalsIgnoreCase("domainname")) {
						configInfo.domainname = parser.nextText();
					}else if (name.equalsIgnoreCase("displayname")) {
						configInfo.displayname  = parser.nextText();
					}
					break;
				}
				event_display = parser.next();
			}
			//解析Advanced
			parser.setInput(new FileInputStream("/mnt/sdcard/config/Advanced"),"utf-8");
			int event_advanced = parser.getEventType();
			while (event_advanced!=XmlPullParser.END_DOCUMENT) {
				switch (event_advanced) {
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase("heartbeat")) {
						configInfo.heartbeat = Short.parseShort(parser.nextText());
					}else if (name.equalsIgnoreCase("fecthweatherinteval")) {
						configInfo.fecthweatherinteval = Long.parseLong(parser.nextText());
					}else if (name.equalsIgnoreCase("savetype")) {
						configInfo.savetype = Byte.parseByte(parser.nextText());
					}else if (name.equalsIgnoreCase("readconfigtype")) {
						configInfo.readconfigtype = Byte.parseByte(parser.nextText());
					}
					break;

				}
				event_advanced = parser.next();
			}
			//解析预留属性字段文件 preattr文件
			parser.setInput(new FileInputStream("/mnt/sdcard/config/preattr"),"utf-8");
			int event_preattr = parser.getEventType();
			while (event_preattr!=XmlPullParser.END_DOCUMENT) {
				switch (event_preattr) {
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase("passport")) {
						configInfo.passport = parser.nextText();
					}else if (name.equalsIgnoreCase("password")) {
						configInfo.password = parser.nextText();
					}else if (name.equalsIgnoreCase("preip")) {
						configInfo.preip = parser.nextText();
					}else if (name.equalsIgnoreCase("premask")) {
						configInfo.premask = parser.nextText();
					}else if (name.equalsIgnoreCase("pregateway")) {
						configInfo.pregateway = parser.nextText();
					}
					break;

				}
				event_preattr = parser.next();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return configInfo;
	}
}
