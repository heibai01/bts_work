package com.led.player.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.xmlpull.v1.XmlSerializer;

import com.led.player.moudle.ConfigInfo;

import android.R.xml;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;
import android.util.Xml;
/**
 * ������ �����ȡmime
 * @author 1231
 *
 */
public class CommUtils {
	
	public static String getMIMEType(File f) { 
	    String type = "";
	    String fName = f.getName();
	    /* ȡ��)չ�� */
	    String end = fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase(); 
	
	    /* ��)չ������;�MimeType */
	    if(end.equalsIgnoreCase("m4a")
	    		|| end.equalsIgnoreCase("mp3")
	    		|| end.equalsIgnoreCase("mid")
	    		|| end.equalsIgnoreCase("xmf")
	    		|| end.equalsIgnoreCase("ogg")
	    		|| end.equalsIgnoreCase("wav")){
	    	type = "audio"+"/"+end; 
	    }
	    else if(end.equalsIgnoreCase("3gp") || end.equalsIgnoreCase("mp4")||end.equalsIgnoreCase("avi")
	    		||end.equalsIgnoreCase("mov")||end.equalsIgnoreCase("vob")){
	    	type = "video"+"/"+end;
	    }else if (end.equalsIgnoreCase("wmv")) {
			type = "video"+"/x-ms-wmv";
		}else if (end.equalsIgnoreCase("mkv")) {
			type = "video/x-matroska";
		}else if (end.equalsIgnoreCase("mpg")) {
			type = "video/mp2p";
		}
	    else if(end.equalsIgnoreCase("jpg")
	    		|| end.equalsIgnoreCase("gif")
	    		|| end.equalsIgnoreCase("png")
	    		|| end.equalsIgnoreCase("jpeg")
	    		|| end.equalsIgnoreCase("bmp")){
	    	type = "image"+"/"+end;
	    }
	    else if(end.equalsIgnoreCase("apk")){
	    	/* android.permission.INSTALL_PACKAGES */ 
	    	type = "application/vnd.android.package-archive"; 
	    } 
	    else{
	    	type = "*/*";
	    }
//	    if(!end.equalsIgnoreCase("apk")){ 
//	    	type += "/*";  
//	    } 
	    return type;
	}
	
	
	public static int getVersionCode(Context context,String packName){
		PackageManager pm = context.getPackageManager();
		int versionCode = -1;
		try {
			PackageInfo packageInfo = pm.getPackageInfo(packName, 0);//mark 阻塞这里，系统资源不够了吧。。
			versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
		
	}
	
	public static boolean parseBin(String filePath) throws IOException{
		File file = new File(filePath);
		String apkName = new File(filePath).getName();
		apkName = apkName.substring(0,apkName.lastIndexOf("."))+".apk";
		return dencrypt(file, new File(file.getParent()+"/"+apkName));
	}
	
	

	/**
	 * 
	 * @param srcFile
	 * @param destFile
	 * @return
	 * @throws IOException
	 * @hide
	 */
	private static final boolean dencrypt(File srcFile,File destFile) throws IOException{
			boolean isEncrypt = false;
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
			Base64InputStream bis = new Base64InputStream(new FileInputStream(srcFile),Base64.DEFAULT);
			int readNum = -1;
			byte[] tmp = new byte[8*1024];
			try {
				while ((readNum=bis.read(tmp))!=-1){
					bos.write(tmp, 0, readNum);
				}
				bos.flush();
				bis.close();
				isEncrypt = true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				if (bos!=null) {
					bos.close();
				}
				if (bis!=null) {
					bis.close();
				}
			}
			return isEncrypt;
		}

	//应答
	public static byte[] createNetpackage() throws UnsupportedEncodingException {
		byte[] pack = new byte[279];
		ConfigInfo configInfo = PullParseBTS.parseOutConfig();
		if (configInfo==null) {
			return pack;
		}
		pack[0] = (byte) 0xaa;
		pack[1] = 0x55;
		
		pack[4] = (279>>8)&0xff;
		pack[5] = 279&0xff;
		pack[6] = 8;
		pack[7] = 4;
		//字段开始
		pack[8] = (byte) ((configInfo.netport>>8)&0xff);
		pack[9] = (byte) (configInfo.netport&0xff);
		pack[10] = (byte) ((configInfo.controlport>>8)&0xff);
		pack[11] = (byte) (configInfo.controlport&0xff);
		//ip  15B长
		byte[] ips = configInfo.ip.getBytes("utf-8");
		for (int i = 0; i < ips.length; i++) {
			pack[12+i] = ips[i];
		}
		//nettype
		pack[27] = configInfo.nettype;
		pack[28] = configInfo.dhcp;
		byte[] displayids = configInfo.displayid.getBytes("utf-8"); //64B
		for (int i = 0; i < displayids.length; i++) {
			pack[29+i] = displayids[i];
		}
		//domainnam
		byte[] domainnames = configInfo.domainname.getBytes("utf-8"); //32
		for (int i = 0; i < domainnames.length; i++) {
			pack[93+i] = domainnames[i];
		}
		//displayname
		byte[] displaynames = configInfo.displayname.getBytes("utf-8"); //32
		for (int i = 0; i < displaynames.length; i++) {
			pack[125+i] = displaynames[i];
		}
		//heartbeat
		pack[157] = (byte) ((configInfo.heartbeat>>8)&0xff);
		pack[158] = (byte) (configInfo.heartbeat&0xff);
		//Fecthweatherinteval  8B
		pack[159] = (byte) ((configInfo.fecthweatherinteval>>56)&0xff);
		pack[160] = (byte) ((configInfo.fecthweatherinteval>>48)&0xff);
		pack[161] = (byte) ((configInfo.fecthweatherinteval>>40)&0xff);
		pack[162] = (byte) ((configInfo.fecthweatherinteval>>32)&0xff);
		pack[163] = (byte) ((configInfo.fecthweatherinteval>>24)&0xff);
		pack[164] = (byte) ((configInfo.fecthweatherinteval>>16)&0xff);
		pack[165] = (byte) ((configInfo.fecthweatherinteval>>8)&0xff);
		pack[166] = (byte) (configInfo.fecthweatherinteval&0xff);
		//saveType
		pack[167] = configInfo.savetype;
		pack[168] = configInfo.readconfigtype;
		byte[] passports = configInfo.passport.getBytes("utf-8"); //32B
		for (int i = 0; i < passports.length; i++) {
			pack[169+i] = passports[i];
		}
		byte[] passwords = configInfo.password.getBytes("utf-8");	//32B
		for (int i = 0; i < passwords.length; i++) {
			pack[201+i] = passports[i];
		}
		//preip
		byte[] preips = configInfo.preip.getBytes("utf-8");	//15B
		for (int i = 0; i < preips.length; i++) {
			pack[233+i] = preips[i];
		}
		byte[] preMasks = configInfo.premask.getBytes("utf-8");	//15B
		for (int i = 0; i < preMasks.length; i++) {
			pack[248+i] = preMasks[i];
		}
		byte[] preGateWay = configInfo.pregateway.getBytes("utf-8");	//15B
		for (int i = 0; i < preGateWay.length; i++) {
			pack[263+i] = preGateWay[i];
		}
		//效验和
		for (int i = 0; i < pack.length-1; i++) {
			pack[278]+= pack[i];
		}
		
		
		return pack;
	}


	/**
	 * 更新配置, /mnt/sdcard/config
	 * @param configInfo
	 */
	public static void updateConfig(ConfigInfo configInfo){
		File dir = new File("/mnt/sdcard/config");
		File[] files = dir.listFiles();
		if (files!=null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					files[i].delete();
				}
			}
		}
		if (dir.exists()==false) {
			dir.mkdirs();
		}
		//生成新的
		FileOutputStream os = null;
		XmlSerializer xmlSerializer = null;
		try {
			//生成Server文件
			os = new FileOutputStream(new File(dir.getAbsolutePath()+"/Server"));
			xmlSerializer = Xml.newSerializer();
			xmlSerializer.setOutput(os, "utf-8");
			xmlSerializer.startDocument("UTF-8", true);
			xmlSerializer.startTag(null, "server");xmlSerializer.text("\n");
			setTag(xmlSerializer, "netport", ""+configInfo.netport);
			setTag(xmlSerializer, "controlport", configInfo.controlport+"");
			setTag(xmlSerializer, "ip", configInfo.ip.trim());
			xmlSerializer.endDocument();
			os.flush();
			os.getChannel().force(true);
			os.close();
			//生成Network
			os = new FileOutputStream("/mnt/sdcard/config/Network");
			xmlSerializer.setOutput(os, "utf-8");
//			xmlSerializer.startDocument("UTF-8", true);
			xmlSerializer.startTag(null, "network");
			xmlSerializer.text("\n");
			setTag(xmlSerializer, "nettype", configInfo.nettype+"");
			setTag(xmlSerializer, "DHCP", configInfo.dhcp+"");
			xmlSerializer.endDocument();
			os.flush();
			os.getChannel().force(true);
			os.close();
			//生成Displays
			os = new FileOutputStream("/mnt/sdcard/config/Displays");
			xmlSerializer.setOutput(os, "utf-8");
			xmlSerializer.startDocument("UTF-8", true);
			xmlSerializer.startTag(null, "config");
			xmlSerializer.text("\n");
			xmlSerializer.startTag(null, "display");
			xmlSerializer.text("\n");
			setTag(xmlSerializer, "displayid", configInfo.displayid.trim());
			setTag(xmlSerializer, "domainname", configInfo.domainname.trim());
			setTag(xmlSerializer, "displayname", configInfo.displayname.trim());
			xmlSerializer.endDocument();
			os.flush();
			os.getChannel().force(true);
			os.close();
			//生成Advanced
			os = new FileOutputStream("/mnt/sdcard/config/Advanced");
			xmlSerializer.setOutput(os, "utf-8");
//			xmlSerializer.startDocument("UTF-8", true);
			xmlSerializer.startTag(null, "advanced");
			xmlSerializer.text("\n");
			setTag(xmlSerializer, "heartbeat", configInfo.heartbeat+"");
			setTag(xmlSerializer, "fecthweatherinteval", configInfo.fecthweatherinteval+"");
			setTag(xmlSerializer, "savetype", configInfo.savetype+"");
			setTag(xmlSerializer, "readconfigtype", configInfo.readconfigtype+"");
			xmlSerializer.endDocument();
			os.flush();
			os.getChannel().force(true);
			os.close();
			//生成preattr文件
			os = new FileOutputStream("/mnt/sdcard/config/preattr");
			xmlSerializer.setOutput(os,"utf-8");
			xmlSerializer.startTag(null, "preattr");
			xmlSerializer.text("\n");
			setTag(xmlSerializer, "passport", configInfo.passport==null?"":configInfo.passport);
			setTag(xmlSerializer, "password", configInfo.password);
			setTag(xmlSerializer, "preip", configInfo.preip==null?"":configInfo.preip);
			setTag(xmlSerializer, "premask", configInfo.premask);
			setTag(xmlSerializer, "pregateway", configInfo.pregateway);
			xmlSerializer.endDocument();
			os.flush();
			os.getChannel().force(true);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 设置 字段值
	 * @param xmlSerializer
	 * @param tagName
	 * @param tagValue
	 */
	private static void setTag(XmlSerializer xmlSerializer,String tagName,String tagValue){
		try {
			xmlSerializer.startTag(null, tagName);
			xmlSerializer.text(tagValue);
			xmlSerializer.endTag(null, tagName);
			xmlSerializer.text("\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 解析收到的数据
	 * @param reciveByte
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static ConfigInfo parseRecive(byte[] reciveByte) throws UnsupportedEncodingException {
		ConfigInfo configInfo = new ConfigInfo();
		configInfo.netport = (short) (((reciveByte[8]&0x00ff)<<8)+(reciveByte[9]&0x00ff));
		configInfo.controlport = (short) (((reciveByte[10]&0x00ff)<<8)+(reciveByte[11]&0x00ff));
		configInfo.ip = new String(reciveByte, 12, 15, "utf-8").trim();
		configInfo.nettype = reciveByte[27];
		configInfo.dhcp = reciveByte[28];
		configInfo.displayid = new String(reciveByte, 29, 64, "utf-8").trim();
		configInfo.domainname = new String(reciveByte, 93, 32, "utf-8").trim();
		configInfo.displayname = new String(reciveByte, 125, 32, "utf-8").trim();
		configInfo.heartbeat = (short) ((reciveByte[157]<<8)+reciveByte[158]);
		configInfo.fecthweatherinteval = (reciveByte[159]<<56)+(reciveByte[160]<<48)+(reciveByte[161]<<40)+(reciveByte[162]<<32)+(reciveByte[163]<<24)+(reciveByte[164]<<16)+(reciveByte[165]<<8)+reciveByte[166];
		configInfo.savetype = reciveByte[167];
		configInfo.readconfigtype = reciveByte[168];
		configInfo.passport = new String(reciveByte, 169, 32, "utf-8").trim();
		configInfo.password = new String(reciveByte, 201, 32, "utf-8").trim();
		configInfo.preip = new String(reciveByte, 233, 15, "utf-8").trim();
		configInfo.premask = new String(reciveByte, 248, 15, "utf-8").trim();
		configInfo.pregateway =  new String(reciveByte, 263, 15, "utf-8").trim();
Log.e("Commutils", "收到的数据对象："+configInfo);		
		return configInfo;
	}
	
}
