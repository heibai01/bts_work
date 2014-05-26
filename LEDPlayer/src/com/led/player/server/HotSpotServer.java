package com.led.player.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.led.player.aidance.LedProtocol;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
/**
 * 热点启动后的监听 Socket线程
 * @author 1231
 *
 */
public class HotSpotServer implements Runnable{
	public static boolean isRun = false;
	 static List<Socket> sockets = new ArrayList<Socket>();
	 private Handler mHandler = null;
	public static  boolean flag = true;
	public HotSpotServer(Handler mHandler) {
		// TODO Auto-generated constructor stub
		this.mHandler = mHandler;
	}
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(1124);
			while (flag) {
				//监听客户端会阻塞
				Socket client = serverSocket.accept();
				sockets.add(client);
				//每连接上来一个客户端就在新县城中 操作
				new Thread(new ServerThread(client,mHandler)).start();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class ServerThread implements Runnable{
	private Socket client = null;
	private BufferedReader br = null;
	private Handler mHandler = null;
	public ServerThread(Socket client,Handler mHandler) throws UnsupportedEncodingException, IOException {
		// TODO Auto-generated constructor stub
		this.client = client;
		this.mHandler = mHandler;
		br = new BufferedReader(new InputStreamReader(client.getInputStream(), "utf-8"));
	}
	public void run() {
		// TODO Auto-generated method stub
		String content = null;
		while ((content=readFromClient())!=null) {
Log.e("joywiif", "收到的字符串："+content);			
			if (content.trim().equalsIgnoreCase("OFF_AP")) {
				//发送给所有的客户端一个退出标志
				for (Socket socket : HotSpotServer.sockets) {
					try {
						OutputStream os = socket.getOutputStream();
						os.write("exit\n".getBytes("utf-8"));
						os.flush();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}finally{
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				//关闭热点
				mHandler.sendEmptyMessage(LedProtocol.OFF_AP);
				//停止他/她的启动线程
				HotSpotServer.flag = false;
				//清空所有客户端,释放内存
				HotSpotServer.sockets.clear();
				Runtime.getRuntime().gc();
				return;
			}
			if (content.trim().equalsIgnoreCase("TEST_MSG")) {
				mHandler.sendEmptyMessage(LedProtocol.TEST_MSG);
				try {
					OutputStream os = client.getOutputStream();
					os.write("exit\n".getBytes("utf-8"));
					os.flush();
					HotSpotServer.sockets.remove(client);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					try {
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	//定义读取客户端数据的方法
	private String readFromClient(){
		try {
			return br.readLine();
			//如果捕捉到异常，说明客户端已经关闭了，超时也是
		} catch (IOException e) {
			e.printStackTrace();
			HotSpotServer.sockets.remove(client);
		}
		return null;
	}
}
