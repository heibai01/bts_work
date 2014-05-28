package com.led.player.service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

/**
 * 2.3.3 下载数据传送消息
(1)	方向：PC -> 控制器
(2)	说明：后台软件在接收到“下载请求应答消息”，并且消息的含义为“允许下载”后，发送如下格式的消息，进行下载数据的发送。下载数据分段发送，每次只发送如下表所示的一条数据，直到收到后台软件的“下载数据接收应答消息”（见2.3.4）后才确定是否发送下一条
(3)	格式：
标志位	消息总长度	类别	功能码	消息内容	校验和
AA 55	15+n+L	     3	 3	         文件名	剩余数据长度	文件数据	
2B	4B	1B	1B	LB;动态长度以\0结尾	4B	nB	1B
(4)	消息内容中各字段取值：
	文件名:Ascii码, 结束符为\0
	剩余数据长度：为一个4字节的整型数，含义是还需要发送的数据量。这个长度包括本次发送的软件数据和尚未发送的软件数据的总和——即第一条消息中的“剩余数据长度”等于下载文件的总长度；最后一条消息中的“剩余数据长度”等于本次发送的软件数据长度。
	文件数据：每次分段发送的原始数据。

 * @author 1231
 *
 */
public class TransHandler extends MessageHandler
{
//	public static List<File> file_news = new ArrayList<File>();
	private HashMap<String, Boolean> fileStates = null;
	private final int DATA_BUFFER_LENGTH = 66535;
	private int totalMsgLength;			//��Ϣ�ܳ���
	private String FileName;
	private int FileNameLength; 	//�������һ����\0����
	private long LeftDataLength = 0;		//ʣ����ݳ���

	private static int HaveWriteBytePos = 0;
	
	private int DataLength = 0;	//��ݳ���
	private byte DateBuffer[];//存放	文件数据：每次分段发送的原始数据
	
	RandomAccessFile oSavedFile; 
	
	public TransHandler()
	{
		fileStates = new HashMap<String, Boolean>();
		DateBuffer = new byte[DATA_BUFFER_LENGTH];
	}
	
	/**
	 * 写入数据到本地 文件
	 */
	@Override
	public int handler(byte[] receivebuff) 
	{
		//1.总的消息长度
		totalMsgLength = GetInforTotalLength(receivebuff);

		//2.获取文件名
		getFileNameFromBuffer(receivebuff);
		
		//3剩余数据长度
		getLeftDataLength(receivebuff);
		
		//4	文件数据：每次分段发送的原始数据
		getLeftData(receivebuff);
		
		//System.out.println("totalMsgLength : "+totalMsgLength +"FileName : "+ FileName + "FileNameLength : " + FileNameLength + "LeftDataLength : " + LeftDataLength + "DataLength :" + DataLength);
		//5��У���
       
		/*for (int i = 0; i <DataLength; i++)
		{ 
			String hex = Integer.toHexString(DateBuffer[i] & 0xFF); 
			if (hex.length() == 1) 
			{ 
				hex = '0' + hex; 
			} 
			System.out.print(hex.toUpperCase()  + " "); 
		}
		System.out.println(" ");
		*/
		try 
        {
			String sFileDir= "mnt" + File.separatorChar + "sdcard" +  File.separatorChar  +"program";
			
			File fileDir = new File(sFileDir);
			if(!fileDir.exists())
            {
				fileDir.mkdirs();
            }
			
        	String sFilePath= "mnt" + File.separatorChar + "sdcard" +  File.separatorChar  +"program" + File.separatorChar + FileName;
        	
        	boolean bXmlFile = false;
        	if(FileName.equals("external.xml"))
        	{
        		sFilePath =  "mnt" + File.separatorChar + "sdcard" +  File.separatorChar  +"program" + File.separatorChar + "loopprogram_.xml";
        		bXmlFile = true;
        	}
        	else
        	{
        		bXmlFile = false;
        	}
        		
        		
        	File file = new File(sFilePath);
        	File file_new = null;
        	if(bXmlFile)
        	{
        		if(file.isFile() && file.exists())
        		{
        			//System.out.println("删除文件名称为：" + sFilePath);
        			file.delete();
        		}        		
        		
        		file.createNewFile();
        	}
        	else
        	{
        		if (fileStates.get(FileName)==null) {
					fileStates.put(FileName, true);
				}
                if(!file.exists())
                {
            		fileStates.put(FileName, false);
                	file.createNewFile();
                }
        		
        	}

            //if(!file.exists())
            //{
           // 	file.createNewFile();
            //}

                try
                {
                	if (fileStates.get(FileName)!=null&&fileStates.get(FileName)==true) {
						return 0;
					}
                	byte DataBuf[] = new byte[DataLength];
                	for(int i = 0 ;i  < DataLength; i++)
                	{
                		DataBuf[i] = DateBuffer[i];
                	}
                    // ��һ���������ļ���������д��ʽ
                    RandomAccessFile randomFile = new RandomAccessFile(file_new==null?file:file_new, "rw");
                    // �ļ����ȣ��ֽ���
                    long fileLength = randomFile.length();
                    //System.out.println("**********************fileLength: " + fileLength);
                    //��д�ļ�ָ���Ƶ��ļ�β��
                    randomFile.seek(fileLength);
                    randomFile.write(DataBuf);
                    randomFile.close();
                }
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
        } catch (Exception e) {  
            e.printStackTrace();  
        }
       

		return 0;

	}
	
	/**
	 * 最后一条消息中的“剩余数据长度”等于本次发送的软件数据长度。
	 * @return
	 */
	public boolean isFileFinished()
	{
		if((LeftDataLength == DataLength)&&(LeftDataLength != 0)&&(DataLength != 0))
			return true;
		else
			return false;
		
		
	}
	
	/**
	 * 获得初始化 DataLength 数据 ， 文件数据多大
	 * @param receivebuff
	 */
	private long getFileDataLength(byte[] receivebuff)
	{
		
		DataLength = totalMsgLength  - FileNameLength -14; //mark 下面改成 -13
//		DataLength = totalMsgLength  - FileNameLength -13;
//System.out.println("totalMsgLength : " + totalMsgLength + "DataLength : "+DataLength);
		return DataLength;
		
	}
	
	/**
	 *文件数据：每次分段发送的原始数据， 随便初始化totalMsgLength
	 * @param receivebuff
	 */
	private void getLeftData(byte[] receivebuff)
	{
		getFileDataLength(receivebuff);
		if(DataLength > 0)
		{
			InitBuffer(DateBuffer);
			
			for(int i = 0 ; i < DataLength; i++)
			{
				DateBuffer[i] = receivebuff[13+i + FileNameLength]; // mark 下面我改成12开始
//				DateBuffer[i] = receivebuff[12+i + FileNameLength];
			}
		}
	}
	
	/**
	 * 剩余数据长度
	 * @param receivebuff
	 * @return
	 */
	private long getLeftDataLength(byte[] receivebuff)
	{
		  byte[] a = new byte[4];

		    for (int i = 0; i < a.length ;i++) 
			{              
			    	//��b��β��(��intֵ�ĵ�λ)��ʼcopy���
			    	a[i] = receivebuff[i+9+FileNameLength];
			}
			       
		    long v0 = (long)(a[0] & 0xff)  * (long)Math.pow(2,24);	//& 0xff��byteֵ�޲���ת��int,����Java�Զ����������,�ᱣ����λ�ķ��λ
		    long v1 = (long)((a[1] & 0xff) * (long)Math.pow(2,16));
		    long v2 = (long)((a[2] & 0xff) * (long)Math.pow(2,8));
		    long v3 = (long)((a[3] & 0xff));
			 
			LeftDataLength  = v0 + v1 + v2 + v3;
			
			//System.out.println("LeftDataLength : "+LeftDataLength);
			
			return v0 + v1 + v2 + v3;
	}
	
	/**
	 * 获取文件名 长多少个字节 从第 9个字节开始 的L个字节
	 * @param receivebuff
	 * @return
	 */
	private int getFileNameLength(byte[] receivebuff)
	{
//		int a ='0';
//		int bn = '\0';
//Log.e("fdgdg", "ascii码："+a+" 带斜杠的："+bn)		;
		int iLength = 0 ;
		if(receivebuff.length > 9)
		{
			for(int i = 8; i < receivebuff.length;i++)
			{
				
				if(receivebuff[i] == '\0')
				{
					FileNameLength = iLength;
//			System.out.println("FileNameLength文件名长度 : "+FileNameLength);
					return iLength;
				}
				iLength++;
			}
		}
		
		FileNameLength = 0;
		
		return 0;			
	}

	
	/**
	 * 
	 * 取的文件的名称 ，先得到文件名的长度
	 * @param receivebuff
	 * @return
	 */
	
	private String getFileNameFromBuffer(byte[] receivebuff)
	{
		getFileNameLength(receivebuff);
		if(FileNameLength > 0)
		{
			byte FilenameBuffer[] = new byte[FileNameLength];
			InitBuffer(FilenameBuffer);
			
			for(int i = 0 ; i < FileNameLength; i++)
			{
				FilenameBuffer[i] = receivebuff[8+i];
			}
			
			try {
				FileName = new String(FilenameBuffer,"GBK").trim();
		
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
//		System.out.println("FileName : "+FileName);
		
		return FileName;
	}
	
	/**
	 * 得到消息的字节数长度 3到6 的下标
	 * @param receivebuff
	 * @return
	 */
	public int GetInforTotalLength(byte[] receivebuff)
	{
		    byte[] a = new byte[4];
		    for (int i = 0; i < a.length ;i++) 
			{
			    	a[i] = receivebuff[i+2];
			}
		    
			/*for (int s = 0;s < a.length; s++)
			{ 
				String hex = Integer.toHexString(a[s] & 0xFF); 
				if (hex.length() == 1) 
				{
					hex = '0' + hex; 
				} 
				System.out.print(hex.toUpperCase()  + " 0x"); 
			} */
			       
			    int v0 = (int)((a[0] & 0xff)  * (int)Math.pow(2,24));	//& 0xff��byteֵ�޲���ת��int,����Java�Զ����������,�ᱣ����λ�ķ��λ
			    int v1 = (int)((a[1] & 0xff) * (int)Math.pow(2,16));
			    int v2 = (int)((a[2] & 0xff) * (int)Math.pow(2,8));
			    int v3 = (int)((a[3] & 0xff));
			       
			    totalMsgLength = v0 + v1 + v2 + v3;

			    return v0 + v1 + v2 + v3;
		
	}

	
	private void  InitBuffer(byte []buffer)
	{
		for(int i = 0 ; i < buffer.length;i++)
		{
			buffer[i] = 0;
		}
	}
	@Override
	public void handlerback(byte[] sendbuff) 
	{
		PackTrans packTrans = new PackTrans();
		packTrans.Package(sendbuff);
		
		//System.out.print("Send back start  :"); 
		
		for (int i = 0; i <10; i++)
		{
				String hex = Integer.toHexString(sendbuff[i] & 0xFF); 
				if (hex.length() == 1) 
				{ 
					hex = '0' + hex; 
				} 
			//	System.out.print(hex.toUpperCase()  + " "); 
		}
		//System.out.println("  Send back end");
	}

	public int getTotalMsgLength() {
		return totalMsgLength;
	}

	public void setTotalMsgLength(int totalMsgLength) {
		this.totalMsgLength = totalMsgLength;
	}

	
	
	
}