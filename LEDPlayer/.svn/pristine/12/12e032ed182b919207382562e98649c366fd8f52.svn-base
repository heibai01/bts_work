package com.led.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.led.player.service.HandleXMLFiles;

/** 
* com Server 
*/ 

public class FileTcpServer
{ 
	private static final int PORT = 10001;// 
	private List<Socket> mList = new ArrayList<Socket>(); 
	private ServerSocket server = null; 
	private ExecutorService mExecutorService = null;
	private ScoketServerS pScoketServer;
	private Context mContext;
	
	public FileTcpServer(Context contxt) 
	{
		this.mContext = contxt;
		pScoketServer = new ScoketServerS();
		pScoketServer.start();
	} 

	private static String parseByte(byte b) {
	    int intValue = 0;   
	    if (b >= 0) {   
	            intValue = b;   
	    } else {   
	            intValue = 256 + b;   
	    }   
	    return Integer.toHexString(intValue);   
	}  



	public class ScoketServerS extends Thread
	{
		public void run()
		{
			 try 
			 {setName("FileTcpServer---thread");
				   server = new ServerSocket(PORT);
				   mExecutorService = Executors.newCachedThreadPool();// 构建线程池
	   System.out.println("FileTcpServer Socket Server start ok..."); 
				   Socket client = null; 
				   while (true) 
				   {
					    client = server.accept(); 
					    mList.add(client);
					    mExecutorService.execute(new Service(client));// 读写操作也是阻塞的，在另外的线程中执行
				   }
			  } catch (Exception ex) {
		
				  ex.printStackTrace(); 
			  } 
		} 
	}

/**
 * 运行在线程池中的 Runnable实现类，每一个连接上来的客户端独立运行在分配的各自线程中
 * @author joychine
 */
public class Service implements Runnable
{
	private Socket socket; 
	private InputStream in = null; 
	private OutputStream out = null;
	
	//add by betters 2013-10-13
	private final static int PACK_HEAD_LENGTH = 10;
	
	private final static int FLAG_BUFFER_LENGTH = 4;
	private final static int FILE_SIZE_BUFFER_LENGTH = 4;
	private final static int FILE_NAME_BUFFER_LENGTH = 2;
	private final static int CHECK_TYPE_BUFFER_LENGTH = 4;
	private final static int CHECK_TYPE_BYTES_BUFFER_LENGTH = 2;
	private	  byte[] CheckCodeByte;
	
	private final static int NONE_CHECK_TYPE_BYTE_LENGTH = 0;
	private final static int MD5_CHECK_TYPE_BYTE_LENGTH = 32;
	private final static int CRC_CHECK_TYPE_BYTE_LENGTH = 2;
	
	private final static boolean bErrorDebug = false;
	
	private final static boolean bDebug = false;
	
	

	private	  byte[] ReciveByte = new byte[65535];
	private byte[] FileFlagBuffer = new byte[4];
	private byte[] FileSizeBuffer = new byte[4];
	private byte[]HaveSavedFileSizeBuffer = new byte[4];
	private byte[]MD5Buffer = new byte[32];
	

	private int FileSizeLen;
	private int FileNameSizeLen;
	private int FileCheckCodeLen;
	private int FileCheckType;
	private String strFileName;
	private String strFileMD5;
	
	
	private final String strSavePath = "/mnt/sdcard/program";
	
	private HashMap<String, Boolean> fileStates =  new HashMap<String, Boolean>();
	//private final String strSavePath = "f:";
	
	
	private int readLen;  //从socket的InputStream中读取到的长度
	byte[] buffer = new byte[65535];
  
	public Service(Socket socket) throws IOException 
	{
		this.socket = socket; 
		try 
		{
			//设置Socket读取超时时间，设置成28800000毫秒==8个小时
			//this.socket.setSoTimeout(20000);	  
			in = socket.getInputStream();
			out = socket.getOutputStream();
			//this.sendmsg(); 
		}
		catch (IOException e) 
		{
			mList.remove(socket); //掉线就remove
			this.socket.close();
			e.printStackTrace(); 
		} 
	} 
  
	/**
	 * 回复上位机
	 * @param out
	 * @param returnType
	 */
  public void sendPackData(OutputStream out,int returnType)
  {
	  	byte[] bsendMessage = null; 
	  	int sendLen= 0;
		if(NONE_CHECK_TYPE_BYTE_LENGTH == FileCheckCodeLen)
		{
			sendLen = 26 + NONE_CHECK_TYPE_BYTE_LENGTH;
			bsendMessage = new byte[sendLen];
		  	bsendMessage[0] = (byte)0xaa;
			bsendMessage[1] = (byte)0x55;
			bsendMessage[2] = (byte)0x01;
			bsendMessage[3] = (byte)0x02;
			
			if(1 == returnType)
			{
				bsendMessage[4] = (byte)0x01;
			}
			else if(2 == returnType)
			{
				bsendMessage[4] = (byte)0x02;
			}
			bsendMessage[5] = (byte)0x00;
			bsendMessage[6] = (byte)0x00;
			bsendMessage[7] = (byte)0x00;
			
			bsendMessage[8] = (byte)0x10;
			bsendMessage[9] = (byte)0x00;

			for(int i = 10;i < 14;i++)
			{
				bsendMessage[i] = FileFlagBuffer[i-10];	
			}
			
			for(int i = 14;i < 18;i++)
			{
				bsendMessage[i] = FileSizeBuffer[i-14];	
			}
			
			//获取已经传输文件大小，如果不存在，则为0
			for(int i = 18;i < 22;i++)
			{
				bsendMessage[i] = HaveSavedFileSizeBuffer[i-18];	
			}
			
			//校验类型
			bsendMessage[22] = (byte)0x00;
			bsendMessage[23] = (byte)0x00;
			bsendMessage[24] = (byte)0x00;
			bsendMessage[25] = (byte)0x00;
		
		}
		else if(MD5_CHECK_TYPE_BYTE_LENGTH == FileCheckCodeLen)
		{
			sendLen = 26 + MD5_CHECK_TYPE_BYTE_LENGTH;
			bsendMessage = new byte[sendLen];
			InitBuffer(bsendMessage);

		  	bsendMessage[0] = (byte)0xaa;
			bsendMessage[1] = (byte)0x55;
			bsendMessage[2] = (byte)0x01;
			bsendMessage[3] = (byte)0x02;
			
			if(1 == returnType)
			{
				bsendMessage[4] = (byte)0x01;
			}
			else if(2 == returnType)
			{
				bsendMessage[4] = (byte)0x02;
			}else if (3==returnType) {
				bsendMessage[4] = (byte)0x03;
			}
			bsendMessage[5] = (byte)0x00;
			bsendMessage[6] = (byte)0x00;
			bsendMessage[7] = (byte)0x00;
			
			bsendMessage[8] = (byte)0x30;
			bsendMessage[9] = (byte)0x00;
			
			for(int i = 10;i < 14;i++)
			{
				bsendMessage[i] = FileFlagBuffer[i-10];	
			}
			
			/*for(int i = 14;i < 18;i++)
			{
				bsendMessage[i] = FileSizeBuffer[i-14];	
			}*/
			
			bsendMessage[14] = (byte) (FileSizeLen & 0xff) ;	//& 0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
			bsendMessage[15]  =(byte) ((int) (FileSizeLen/Math.pow(2,8)) & 0xff);
			bsendMessage[16] = (byte)  ((int) (FileSizeLen/Math.pow(2,16)) & 0xff);
			bsendMessage[17] = (byte)  ((int) (FileSizeLen/Math.pow(2,24)) & 0xff);
			 if(bDebug)
			Log.i("sam","FileSizeBuffer " + FileSizeLen);
			
			//获取已经传输文件大小，如果不存在，则为0
			for(int i = 18;i < 22;i++)
			{
				bsendMessage[i] = HaveSavedFileSizeBuffer[i-18];	
			}
			
			//校验类型
			bsendMessage[22] = (byte)0x01;
			bsendMessage[23] = (byte)0x00;
			bsendMessage[24] = (byte)0x00;
			bsendMessage[25] = (byte)0x00;
		    try 
		    {	
		    	String strFileTempName = null ;
		    	   //获取文件校验MD5
		    		if(strFileName.equals("external.xml"))
		    			//strFileTempName = "demandprogram.xml";
		    			strFileTempName = "loopprogram.xml";
		    		else
		    			strFileTempName = strFileName;
		    	
		    	   File file = new File(strSavePath + File.separator + strFileTempName);
		    	 
		    	   if(bDebug)
		    	   Log.i("sam",strSavePath + File.separator + strFileTempName);
		    	   
		    	   if(file.exists())
		    	   {
			    	   strFileMD5 = FileMD5.getFileMD5String(file);
			    	   MD5Buffer=strFileMD5.getBytes("UTF8");
			    	   
			    	   if(bDebug)
			    		   Log.e("sam say 1:",strFileMD5);
			    	   
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			    	   
			    	   for(int n = 0 ; n < 32; n++)
			    	   {
			    		   bsendMessage[26 +n] =  MD5Buffer[n];
			    	   }

					   //获取校验码
					  //GetCheckCode();
					  
					  if(bDebug)
						 PrintBuffer(bsendMessage,58);
		    	   }
		    	   else
		    	   {
						
			    	   for(int i = 0 ; i < 32; i++)
			    	   {
			    		   bsendMessage[26 + i] =  0x00;
			    	   }
		    	   }
		    	   
		    	   if(bErrorDebug)
		    	   { 
		    		   Log.i("sam-------","start ------");
		    		   PrintBuffer(bsendMessage,58);
		    		   Log.i("sam-------","end ------");		    		  
		    	   }
			      
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		else if(CRC_CHECK_TYPE_BYTE_LENGTH == FileCheckCodeLen)
		{
			sendLen = 26 + CRC_CHECK_TYPE_BYTE_LENGTH;
			bsendMessage = new byte[sendLen];

			bsendMessage[0] = (byte)0xaa;
			bsendMessage[1] = (byte)0x55;
			bsendMessage[2] = (byte)0x01;
			bsendMessage[3] = (byte)0x02;
			
			if(1 == returnType)
			{
				bsendMessage[4] = (byte)0x01;
			}
			else if(2 == returnType)
			{
				bsendMessage[4] = (byte)0x02;
			}
			bsendMessage[5] = (byte)0x00;
			bsendMessage[6] = (byte)0x00;
			bsendMessage[7] = (byte)0x00;
			
			bsendMessage[8] = (byte)0x10;
			bsendMessage[9] = (byte)0x00;
			
			
			for(int i = 10;i < 14;i++)
			{
				bsendMessage[i] = FileFlagBuffer[i-10];	
			}
			
			/*for(int i = 14;i < 18;i++)
			{
				bsendMessage[i] = FileSizeBuffer[i-14];	
			}
			*/
			
			
			bsendMessage[14] = (byte) (FileSizeLen & 0xff) ;	//& 0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
			bsendMessage[15]  =(byte) ((int) (FileSizeLen/Math.pow(2,8)) & 0xff);
			bsendMessage[16] = (byte)  ((int) (FileSizeLen/Math.pow(2,16)) & 0xff);
			bsendMessage[17] = (byte)  ((int) (FileSizeLen/Math.pow(2,24)) & 0xff);
			
			//获取已经传输文件大小，如果不存在，则为0
			for(int i = 18;i < 22;i++)
			{
				//bsendMessage[i] = HaveSavedFileSizeBuffer[i-18];	
			}
			
			//校验类型
			bsendMessage[22] = (byte)0x02;
			bsendMessage[23] = (byte)0x00;
			bsendMessage[24] = (byte)0x00;
			bsendMessage[25] = (byte)0x00;
			
			//CRC校验两个字节 还未实现
			
		}
		
	  	try
		{
			out.write(bsendMessage, 0, sendLen);
		}
		catch (Exception ex) 
		{ 
			 if(bDebug)
		    System.out.println("serverSend exception"); 
		    ex.printStackTrace(); 
		}


  }
  
  public void run() 
  {
	  if (!mList.contains(this.socket)) 
	  {
		return;
	  }

	  
	  try 
	  {
		  int nExtSize = -1;
		  int iFileSizeTempLen = 0;
		  long iFileSizeTempLenPlus = 0;
		  boolean bNewFile = true; 
		  boolean btest = false;
		  while (true)
		  {
			  if(btest)	  Log.e("Sam ","年末的需要找个问题咋找不到呢");  
			  readLen = 0;
			  if ((readLen = in.read(ReciveByte)) > 0)   
			  {
				  if(readLen == 10)	  PrintBuffer(ReciveByte, readLen<58?readLen:58);
				 
				  if((ReciveByte[1] == 0x55)&&(ReciveByte[0] == (byte)0xaa)&&(ReciveByte[2] == (byte)0x01)&&(ReciveByte[3] == (byte)0x02))
				  {
//					  if(true)
//					  {
//						  Log.e("Sam ","接收到命令"); 
//						  PrintBuffer(ReciveByte, readLen<58?readLen:58);
//					  }
					  //判断是不是 要升级apk 的操作
					  if (ReciveByte[4]==0x05) {
						  Log.e("joychine更新文件", "更新文件接受完了");
						  Thread.sleep(3000);
						  //更新文件
						  new Thread("你妹啊，升级"){
							  public void run() {
								 File[] files =  new File("/mnt/sdcard/program/").listFiles(new FilenameFilter() {
									public boolean accept(File dir, String filename) {
										return filename.toLowerCase().endsWith(".bin");
									}
								});
								if (files!=null) {
Log.e("files不为空时", Arrays.toString(files))									;
									File destFileDir = new File("/mnt/sdcard/bts");
									if (destFileDir.exists()==false) {
										destFileDir.mkdirs();
									}else {
//										File[] desFiles = destFileDir.listFiles();
//										if (desFiles!=null) {
//											for (File file : desFiles) {
//												if (file.getName().toLowerCase().endsWith(".bin")&&file.isFile()) {
//				Log.e("删除这里的文件", "删除这个目录先所有的文件箱");
//													file.delete();
//												}
//											}
//										}
									}
									for (File file : files) {
										String fileName = null;
										if (file.getName().toLowerCase().startsWith("ledplayer")) {
											fileName = "LEDPlayer.bin";
										}else {
											fileName = "NETClient.bin";
										}
										FileOutputStream bos = null;
										FileInputStream bis = null;
										byte[] buff = new byte[1024];
										int readLen = -1;
										try {
											bis =new FileInputStream(file.getAbsolutePath());
											bos =  new FileOutputStream("/mnt/sdcard/bts/"+fileName);
											while ((readLen=bis.read(buff))!=-1){
												bos.write(buff, 0, readLen);
											}
											bos.flush();
											bos.getChannel().force(true);
										} catch (IOException e) {
											Log.e("异常位置1", "异常位置1呃呃呃");
											e.printStackTrace();
										}finally{
											try{
												if (bos!=null)bos.close();
												if (bis!=null) bis.close(); 
												
											}catch(Exception e){
												Log.e("异常位置2", "异常位置2！！！！！！发");
												e.printStackTrace();
											}
											
										}
										//删除刚刚接受到的文件
//							Log.e("dfdfd", "删除文件前夕。。。。。。。。。。。。。。。。。");
										file.delete();
										
									}
									
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									Intent intent = new Intent("com.led.btsstudio.UPDATE");
									intent.setData(Uri.parse("file://"));
									mContext.sendBroadcast(intent);
//	Log.e("广播发出了啊11", "发出去了广播啊！！！！");
								}else {
									Log.e("FileTcpServer", "文件都去哪了！");
								}
							  };
							  
						  }.start();
						  break;
					  }
					  
					  nExtSize = GetExDataLen(); //附加数据大小，获取文件属性的数据长度
					  if(0 == nExtSize) //文件属性数据长度为0，则说明此文件发送完毕
					  {
						  if(bErrorDebug)
						 	Log.e("Sam ","文件发送完毕：" + nExtSize); 
						  
						  //发送返回
						  if(bDebug)
							  Log.i("Sam ","文件属性长度：" + nExtSize); 
						  //返回包头
						  sendPackData(out,2);
						  
    	  				  //先修改名字
    					  HandleXMLFiles.renameProgramListXml(1);
    					  Thread.sleep(100);
    					  startPlay("/mnt/sdcard/program/loopprogram.xml");  //mark 不让播  
    					  Thread.sleep(100);
    					  HandleXMLFiles.deleteExcessFiles();
    					  
Log.e("没道理啊", "线程每退出去当前name:"+Thread.currentThread().getName());
						  break;
					  }
					  else
					  {
						  if(bErrorDebug)
							  Log.e("Sam ","接收新文件 文件属性长度：" + nExtSize); 
						 
						  if(bDebug)
						  	 Log.i("Sam ","接收文件 文件属性长度：" + nExtSize + "readLen : " + readLen); 
   							bNewFile = true;
					  }
					  
					  if(bNewFile && (readLen == (nExtSize+10))&&(readLen != 0))
					  {
						  if(bErrorDebug)
							  Log.e("Sam ","获取文件属性信息"); 
						  
						  if(bDebug)
							  Log.i("Sam ","接收数据信息"); 
						 
						  //获取文件FLAG
						  GetFileFlagLen();
						  
						  //获取文件大小
						  iFileSizeTempLen =GetFileSizeLen();
						  if(bDebug)
							  Log.i("Sam ","FileSizeLen :"+FileSizeLen); 
						  
						  //获取文件名称长短
						  GetFileNameLen();
						  if(bDebug)
							  Log.i("Sam ","FileNameSizeLen :"+FileNameSizeLen); 
						  
						  //获取文件校验方式
						  GetCheckType();
						  if(bDebug)
							  Log.i("Sam ","FileCheckType :"+FileCheckType); 
						  
						  
						  //获取文件校验字符串长度
						  GetCheckCodeLen();
						  if(bDebug)
							  Log.i("Sam ","FileCheckCodeLen :"+FileCheckCodeLen); 
						  
						  
						  //获取文件名称
						  GetFileName();
						  if(bErrorDebug)
							  Log.e("Sam ","FileName :"+strFileName); 
						  
						    //获取校验码
						  GetCheckCode();
						  if(bDebug)
						  {
						    Log.i("Sam ","MD5Code :"); 
						  	PrintBuffer(CheckCodeByte,32);
						  }
						  
						  //获取已经接收文件长度
						  GetHaveReceiveFileLen( strSavePath+ File.separatorChar + strFileName);
						  if(bDebug)
							  PrintBuffer(HaveSavedFileSizeBuffer, 4);

						  if(strFileName.equals("external.xml"))
						  {
				        		String sFilePath= "mnt" + File.separatorChar + "sdcard" +  File.separatorChar  +"program" + File.separatorChar + strFileName;
				        		File file = new File(sFilePath);
				        		if(file.isFile() && file.exists())
				        			file.delete();
				           }
						  
						  //返回包头
						  sendPackData(out,1);
						  //sendPackData(out,3); //mark add 
						  bNewFile = false;
						  //判断长度是否一致，如果长度一致 2014-02-16任务在这里
						  //特别说明：此处理应做成判断MD5值是否完全一样的
						 	String sssFilePath= "mnt" + File.separatorChar + "sdcard" +  File.separatorChar  +"program" + File.separatorChar + strFileName;
				        	File file = new File(sssFilePath);
		                	if(file.exists())
		                	{
		                		RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
		                		long fileLength = randomFile.length();
		                		randomFile.close();
		                		if(fileLength == iFileSizeTempLen)
		                			break;
		                	}
						  
						  
					  }
	
				  }
				  //不是发来的文件头。。。
				  else //写文件
				  {
					  	// 连续接收信息
					  	String name = (String) strFileName;
					  
						String sFileDir= "mnt" + File.separatorChar + "sdcard" +  File.separatorChar  +"program";
						
						File fileDir = new File(sFileDir);
						if(!fileDir.exists())
			            {
							fileDir.mkdirs();
			            }
						
			        	String sFilePath= "mnt" + File.separatorChar + "sdcard" +  File.separatorChar  +"program" + File.separatorChar + strFileName;
			        	File file = new File(sFilePath);

			        	boolean bXmlFile = false;
			        	if(strFileName.equals("external.xml"))
			        	{
			        		bXmlFile = true;
			        	}
			        	else
			        	{
			        		bXmlFile = false;
			        	}

			        	//File file_new = null;
			        	if(bXmlFile)
			        	{
			        		if(!file.exists())
			        			file.createNewFile();
			        	}
			        	else
			        	{
			        		if (fileStates.get(strFileName)==null) {
								fileStates.put(strFileName, true);
							}
			        		
			                if(!file.exists())
			                {
			            		fileStates.put(strFileName, false);
			                	file.createNewFile();
			                }
			        		
			        	}

		                try
		                {
		                	if(!file.exists())
			        			file.createNewFile();
		                	RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
		                	 
		                	long fileLength = randomFile.length();
		                	
		                	if (fileStates.get(strFileName)!=null&&fileStates.get(strFileName)==true) 
		                	{
			                    if(bDebug)
			                    	 Log.i("Sam ","**********************return: "); 
			                    if(fileLength >=  FileSizeLen)
			                    {
			                    	
			                    	if( bErrorDebug)
			                    		 Log.e("Sam ","FileLength : " + fileLength + "  FileSizeLen : " + FileSizeLen); 
			                    	
			                    	randomFile.close();
			                    	
			                    	if(fileLength >  FileSizeLen)
			                    		file.delete();
			                    	
				              		  out.flush(); 
				            		  out.close(); 
				            		  in.close(); 
				            		
				            		  mList.remove(socket);
				            		  socket.close();
			                    	
			                    	return ;
			                    }
			                    else
			                    {
			                    	iFileSizeTempLenPlus = fileLength;
			                    }
	                    
							}

		                	//RandomAccessFile randomFile = new RandomAccessFile(file_new==null?file:file_new, "rw");
		                	//
		                   
		                    //如果长度大于  删除重新传输
		                    if(fileLength > FileSizeLen)
		                    {
								  if(bDebug)
								  System.out.println("Recvive Success 2!");

								  break;
							}
		                    //
		                    if(bDebug)
		                    	 Log.i("Sam ","**********************fileLength: " + fileLength); 
		                    
		                    randomFile.seek(iFileSizeTempLenPlus);
		                    randomFile.write(ReciveByte,0, readLen);
		                    if(bDebug)
		                    {
		                    	 Log.i("Sam ","**********************readLen: " + readLen); 
		                    }
		                    randomFile.getChannel().force(true);  //mark 必须成功啊！！！！ 2014年2月27日 22:30:36
		                    randomFile.close();
		                }
		                catch (IOException e) 
		                {
		                    e.printStackTrace();
		                }

						
					  iFileSizeTempLenPlus = iFileSizeTempLenPlus +readLen;
					  
	                    if(bDebug)
	                    	 Log.i("Sam ","**********************iFileSizeTempLenPlus: " + iFileSizeTempLenPlus); 
					  
					  if(iFileSizeTempLenPlus >= iFileSizeTempLen)
					  {
				          Log.i("Sam ","Recvive Success!**********************iFileSizeTempLenPlus: " + iFileSizeTempLenPlus); 
						  iFileSizeTempLenPlus = 0;
						  btest = true;
						  break;
					  }
				  }
			  }else {
				  Log.e("读完了该退了", "读完了该退了");
				  break;
			  }
		  }
	    
		  out.flush(); 
		  out.close(); 
		  in.close(); 
		
		  mList.remove(socket);
		  socket.close();
		    
	  }
	  catch (Exception ex) 
	  {
		 // System.out.println("server connect out 读超时或者是客户端那边退出导致 Connection reset 错误"); 
		  mList.remove(socket);
		  try 
		  {
			socket.close();
		  }
		  catch (IOException e) 
		  {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
		  ex.printStackTrace(); 
	  }
  }

  
	  //获取数据长度, 附加数据大小。
  	  private int GetExDataLen()
	  {
	      int v0 = (int)(ReciveByte[8] & 0xff) ;	//& 0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
	      int v1 = (int)((ReciveByte[9] & 0xff) * (int)Math.pow(2,8));
		  int v2 =  v0 + v1;
		  
		  return v2;
	  }
	  

	  
	  private long GetFileFlagLen()
	  {
		  for(int i = 0 ; i < 4;i++)
		  {
			  FileFlagBuffer[i] =  ReciveByte[PACK_HEAD_LENGTH+i];
		  }

		  long v0 = (long)(ReciveByte[0] & 0xff) ;	//& 0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
		  long v1 = (long)((ReciveByte[1] & 0xff) * (long)Math.pow(2,8));
		  long v2 = (long)((ReciveByte[2] & 0xff) * (long)Math.pow(2,16));
		  long v3 = (long)((ReciveByte[3] & 0xff) * (long)Math.pow(2,24));
		  long v4 =  v0 + v1 + v2 + v3;
		  
		  return v4;
	  }
	  
	  private int GetFileSizeLen()
	  {
		  for(int i = 0 ; i < 4;i++)
		  {
			  FileSizeBuffer[i] = ReciveByte[PACK_HEAD_LENGTH+FLAG_BUFFER_LENGTH+i];
		  }
		  int v0 = (int)(ReciveByte[14] & 0xff) ;	//& 0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
		  int v1 = (int)((ReciveByte[15] & 0xff) * (int)Math.pow(2,8));
		  int v2 = (int)((ReciveByte[16] & 0xff) * (int)Math.pow(2,16));
		  int v3 = (int)((ReciveByte[17] & 0xff) * (int)Math.pow(2,24));
		  int v4 =  v0 + v1 + v2 + v3;
	      
	      FileSizeLen = v4;
		  
		  return v4;
	  }
	  
	  private long GetFileNameLen()
	  {
	      int v0 = (int)(ReciveByte[18] & 0xff) ;	//& 0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
	      int v1 = (int)((ReciveByte[19] & 0xff) * (int)Math.pow(2,8));

	      int v4 =  v0 + v1;
		  
	      FileNameSizeLen = v4;
	      
		  return v4;
	  }
	  
	  
	  private void GetHaveReceiveFileLen(String fileName)
	  {		
		  try 
		  {
			  	File file = new File(fileName); 
				if(!file.exists())
	            {
					InitBuffer(HaveSavedFileSizeBuffer);
		            return;
	            }
				
				RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
	            //
	            long fileLength;

				fileLength = randomFile.length();
				
				 if(bDebug)
					  Log.i("Sam ","HaveReceiveFileLen :"+fileLength); 
				
	            HaveSavedFileSizeBuffer[0] = (byte) (fileLength & 0xff) ;	//& 0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
	            HaveSavedFileSizeBuffer[1]  =(byte) ((int) (fileLength/Math.pow(2,8)) & 0xff);
	            HaveSavedFileSizeBuffer[2] = (byte)  ((int) (fileLength/Math.pow(2,16)) & 0xff);
	            HaveSavedFileSizeBuffer[3] = (byte)  ((int) (fileLength/Math.pow(2,24)) & 0xff);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }

	  
	  //文件校验的方式 
	  private int GetCheckType()
	  {
		  for(int i = 0 ; i < 4;i++)
		  {
			  FileSizeBuffer[i] = ReciveByte[PACK_HEAD_LENGTH+FLAG_BUFFER_LENGTH+FILE_SIZE_BUFFER_LENGTH+FILE_NAME_BUFFER_LENGTH+i];
		  }
		  
		  
		  int v0 = (int)(ReciveByte[20] & 0xff) ;	//& 0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
		  int v1 = (int)((ReciveByte[21] & 0xff) * (int)Math.pow(2,8));
		  int v2 = (int)((ReciveByte[22] & 0xff) * (int)Math.pow(2,16));
		  int v3 = (int)((ReciveByte[23] & 0xff) * (int)Math.pow(2,24));
		  int v4 =  v0 + v1 + v2 + v3;
	      
		  FileCheckType = v4;
		  
		  return v4;
	  }
	  
	  
	  //文件校验的校验码的长度方式 
	  private int GetCheckCodeLen()
	  {
	      int v0 = (int)(ReciveByte[24] & 0xff) ;	//& 0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
	      int v1 = (int)((ReciveByte[25] & 0xff) * (int)Math.pow(2,8));

	      int v4 =  v0 + v1;

	      FileCheckCodeLen = v4;
	      
		  return v4;
	  }
	  
	  /*
	   *private final static int FLAG_BUFFER_LENGTH = 4;
	private final static int FILE_SIZE_BUFFER_LENGTH = 4;
	private final static int FILE_NAME_BUFFER_LENGTH = 2;
	private final static int CHECK_TYPE_BUFFER_LENGTH = 4;
	private final static int CHECK_TYPE_BYTES_BUFFER_LENGTH = 2;
	   */
	  
	  //获取文件名称
	  private String GetFileName()
	  {
		  if(FileNameSizeLen > 0)
		  {
			  System.out.println("FileNameSizeLen :" + FileNameSizeLen); 
			  
			  byte FilenameBuffer[] = new byte[FileNameSizeLen];
			  InitBuffer(FilenameBuffer);
				
			  for(int i = 0 ; i < FileNameSizeLen; i++)
			  {
				  FilenameBuffer[i] = ReciveByte[PACK_HEAD_LENGTH+FLAG_BUFFER_LENGTH+FILE_SIZE_BUFFER_LENGTH+FILE_NAME_BUFFER_LENGTH+CHECK_TYPE_BUFFER_LENGTH+CHECK_TYPE_BYTES_BUFFER_LENGTH+i];
			  }
			  
//			  System.out.println("打印 FilenameBuffer "); 
//			  PrintBuffer(FilenameBuffer,FilenameBuffer.length);
				
			  try 
			  {
				  strFileName = new String(FilenameBuffer,"GBK").trim();//我靠，这里是gbk解码的。。。
			  } catch (UnsupportedEncodingException e) {
				  e.printStackTrace();
			  }
			}
			return strFileName;
	  }
	  
	  private byte[] GetCheckCode()
	  {
		  if(FileCheckCodeLen > 0)
		  {
			  CheckCodeByte = new byte[FileCheckCodeLen];		
			  InitBuffer(CheckCodeByte);
			  for(int i = 0 ; i < FileCheckCodeLen; i++)
			  {
				  CheckCodeByte[i] = ReciveByte[PACK_HEAD_LENGTH+FLAG_BUFFER_LENGTH+FILE_SIZE_BUFFER_LENGTH+FILE_NAME_BUFFER_LENGTH+CHECK_TYPE_BUFFER_LENGTH+CHECK_TYPE_BYTES_BUFFER_LENGTH+FileNameSizeLen+i];
			  }
			  
			  return CheckCodeByte;  
		  }
		  else
		  {
			  return null;
		  }
	}
	  
	  
	  
	  
	public void PrintBuffer(byte[] buffer,int len)
	{
		System.out.print("net: ");
		for (int i = 0; i <len; i++)
		{ 
			String hex = Integer.toHexString(buffer[i] & 0xFF); 
			if (hex.length() == 1) 
			{
				hex = '0' + hex; 
			} 
			System.out.print(hex.toUpperCase()  + " "); 
		}
		System.out.println(" ");
			
	}

  
	private void  InitBuffer(byte []buffer)
	{
		for(int i = 0 ; i < buffer.length;i++)
		{
			buffer[i] = 0;
		}
	}
	
	
	/**
	 *
	 * @param iPlayType
	 */
	public void startPlay(String xmlPath)
	{
		System.out.println("-----------------------start play!---------------------------------------");

		String action = "PLAY_NET_PROGRAM";
		String sPath = xmlPath;
		
		Intent myIntent = new Intent();
		myIntent.setAction(action);
		myIntent.putExtra("path",sPath);
		myIntent.putExtra("demand", 0); //mark 2014年3月12日 15:36:00 测试点播
		mContext.sendBroadcast(myIntent);
	
		System.out.println("start play!");
	}
} 


} 
