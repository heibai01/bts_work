package com.led.player.utils;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import com.led.player.moudle.APInfo;
import android.util.Log;


public class WriteAPinfo {
	/**
	 * 把一个apinfo信息写入到文件
	 * @return
	 */
	public static final String PATH = "/data/misc/wifi/wpa_supplicant.conf";
	public static final String COMMAND = "chmod 777 " + "/data/misc/wifi";
	public static boolean writeApinfo(APInfo apInfo){
//		Process process = null;
//		DataOutputStream dos = null;
		BufferedWriter bw = null;
		try {
//			process = Runtime.getRuntime().exec("su");
//            dos = new DataOutputStream(process.getOutputStream());
//            dos.write((COMMAND+"\n").getBytes("utf-8"));
////            dos.writeBytes(COMMAND + "\n"); //都一样了。
////            dos.writeBytes("exit\n");
//            dos.write("exit\n".getBytes("utf-8"));
//            dos.flush();
//            process.waitFor();
            EthernetSet.writeOsData(COMMAND); //没有waitfor生效太慢？？可能导致我下面写文件失败。。。第二次写入就可以了。。。
            File file = new File(PATH);
            if (file.isFile()==false||file.exists()==false) {
				Log.e("joybug", "文件不存在~自动创建");
//				return false;
	        }
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"utf-8"));
            //写入一个wpa-psk加密的连接信息进入文件
            bw.write("ctrl_interface=DIR=/data/misc/\n");
            bw.write("wpa_supplicant GROUP=system\n");
            bw.write("update_config=1\n");
            bw.write("\n");
            bw.write("network={\n");
            bw.write("\tssid=\""+apInfo.ssid+"\"\n");
            bw.write("\tpsk=\""+apInfo.psk+"\"\n");
            bw.write("\tkey_mgmt="+apInfo.key_mgmt+"\n}");
            bw.flush();
			
		} catch (IOException e) { 
			// TODO Auto-generated catch block
			e.printStackTrace();
			writeApinfo(apInfo);  //跟我斗~~~哈哈哈
//			return false;
		} finally{
			try {
				if (bw!=null) {
					bw.close();
				}
//				if (dos!=null) {
//					dos.close();
//				}
//				if (process!=null) {
////					process.destroy();
//				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
