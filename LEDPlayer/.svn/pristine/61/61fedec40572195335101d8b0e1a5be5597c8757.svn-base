package  com.led.player.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.util.Log;

public class HandleXMLFiles
{
	public static String sDirtory = "mnt" + File.separatorChar + "sdcard" +  File.separatorChar  + "program" +File.separatorChar;
	public static List<String> NeedFiles = new ArrayList<String>();
	public static List<String> deleteNoNeedFiles = new ArrayList<String>();
	
	private static Document doc;
	
	
	public  static boolean deleteExcessFiles()
	{
    	 //ɾ������ļ�
		String strDirPath =  "mnt" + File.separatorChar + "sdcard" +  File.separatorChar  + "program" +File.separatorChar;
		ArrayList<String> allFilesList  = null;
		allFilesList = getFileNameList(strDirPath);
		
		getNeedFilesFromXmlFile(strDirPath + "loopprogram.xml");
		

  	   	//��ӡ�˴���Ҫ���ļ����
  	 	Iterator<String> ltsss = NeedFiles.iterator();
  	 	int index = 0;
  	 	while(ltsss.hasNext())
  	 	{
  	 		index++;
  	 		System.out.println(index +"Need file name is :" +  (String) ltsss.next().trim());
  	 	}
  	   	
  	   	//��ӡ�˴���Ҫ���ļ����
  	 	Iterator<String> ltAll = allFilesList.iterator();
  	 	index = 0;
  	 	while(ltAll.hasNext())
  	 	{
  	 		index++;
  	 		System.out.println(index +"All file name is  :" +  (String) ltAll.next().trim());
  	 	}
  	 	
  	 	String strName;
  	 	Iterator<String> ltAllFileName = allFilesList.iterator();
  	 	
  	 	System.out.println("start delete files");
  	 	boolean bResult = false;
  	 	index = 0;
  	 	while(ltAllFileName.hasNext())
  	 	{
  	 		strName = ltAllFileName.next();
  		 
  	 		if(!NeedFiles.contains(strDirPath+ strName))
  	 		{
  	 			index++;
//  	 			System.out.println(index +"delete file name is:" +strDirPath+ strName);
  	 			
  	 			bResult = deleteFile(strDirPath+strName);
  	 		}
  	 	}
  	 	System.out.println("end delete files");
  	 	
  	 	return bResult;
	}
	
	/**
	 * ��Xml�ļ��л�ȡ��Ҫ���ļ�
	 * @param FilePath
	 */
	public  static  boolean getNeedFilesFromXmlFile(String filePath)
	{
		NeedFiles.clear();
		File f=new File(filePath);
		if(!f.exists())
		{
			return false;
		}

		System.out.println(" *************&&&&&&&&&&&&filePath: " + filePath);
		//
		doc = DomUtils.load(filePath);
		
		NodeList tasklistNodeList = DomUtils.selectNodes(doc, "LEDProject/PageData/DisplayWindowData/PlayPageData/PlayWindowData/ProgramData");
		
		System.out.println("-----------------------------files node length is :" + tasklistNodeList.getLength() + " filePath: " + filePath);
		
		NeedFiles.add(sDirtory + "loopprogram.xml");
		String NodeFilePath;
		String NodeFileName;
		String AndriodNodeFileName;
		for (int i=0;i<tasklistNodeList.getLength();i++)
		{
			
			NodeFilePath =  DomUtils.getAttributeString((Element)tasklistNodeList.item(i), "ProgramName");
			NodeFilePath = NodeFilePath.trim();
			//System.out.println("-----------------------------NodeFilePath :" + NodeFilePath);
			NodeFileName =  NodeFilePath.substring(NodeFilePath.lastIndexOf("/")+1);  
			NodeFileName =  NodeFileName.substring(NodeFileName.lastIndexOf("\\")+1);
			System.out.println("-----------------------------files NodeFileName is :" + NodeFileName);
			AndriodNodeFileName =  sDirtory + NodeFileName;
			if(!NeedFiles.contains(AndriodNodeFileName))
			{
				NeedFiles.add(AndriodNodeFileName);
			}
		}
		
		printNeedFiles();
		return true;
	}
	
	
	
	private  static  ArrayList<String> getFileNameList(String FilePath)
	{
		File path = new File(FilePath);
        System.out.println("�ļ�·����Ϊ ------------0000��"+ FilePath);
		if(!path.exists())
			return null;
        ArrayList<String> FileNameList=new ArrayList<String>();
        FileNameList.clear();
        //������ļ��еĻ�
        System.out.println("�ļ�·����Ϊ ------------��"+ FilePath);
        if(path.isDirectory())
        {
        	File[] files = path.listFiles();// ��ȡ    
        	
            if (files != null) 
            {
            	// ���ж�Ŀ¼�Ƿ�Ϊ�գ�����ᱨ��ָ��    
                for (File file : files) 
                {   
                    if (!file.isDirectory()) 
                    {   
                    	System.out.println(file.getAbsolutePath());
                    	//�����ļ��Ĵ���
                        String filePath = file.getAbsolutePath(); 
                        //�ļ���
                        String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
                        
                        System.out.println("--------------File name is "+ fileName);
                        //���
                        FileNameList.add(fileName);
                    }   
                }   
            }
        }
		return FileNameList;
	}
	
	public  static  boolean deleteloopprogramFile()
	{
		String strFile = sDirtory + "loopprogram.xml";
		boolean bResult = false;
		File file = new File(strFile);
		if(file.isFile() && file.exists())
		{
				bResult  = file.delete();
		}
		return bResult;

	}
	
	
	private  static  boolean deleteFile(String strFilePath)
	{
		boolean bResult = false;
		File file = new File(strFilePath);
		if(file.isFile() && file.exists())
		{
				System.out.println("delete file name is : " + strFilePath);
				bResult  = file.delete();
		}
		return bResult;

	}
	
	public  static   boolean renameProgramListXml(int taskType)
	{
		String savePath =sDirtory;
		
		String sFilePath = null ;
		String snewFilePath = null ;
	
		switch(taskType)
		{
		case 1:
			//sFilePath = savePath + "loopprogram_.xml";
			sFilePath = savePath + "external.xml";
			snewFilePath = savePath + "loopprogram.xml";
			break;
		case 2:
			//sFilePath =savePath + "demandprogram_.xml";
			sFilePath = savePath + "external.xml";
			snewFilePath = savePath + "demandprogram.xml";
			break;
		case 3:
			sFilePath = savePath+ "pluginprogram_.xml";
			snewFilePath = savePath + "pluginprogram.xml";
			break;
		default:
			break;
		}
		
		boolean bResult = false;

		File file = new File(sFilePath);
		 
		 if(file.exists())
		 {// mark 如果以前就存在一个 loopprogram.xml 就要先删了他，然后再改名 external.xml为 loopprogram.xml
//			boolean isDelOld =  new File(snewFilePath).delete();
//				Log.e("删除旧的loopprogram.xml", isDelOld?"成功":"失败");
			
			 bResult = file.renameTo(new File(snewFilePath));
			 System.out.println(bResult?"Rename btsplayxml success!":"改名失败了~~~~~");
		 }
		
		 return bResult;
	}
	
	private  static  void printNeedFiles()
	{
		Iterator it = NeedFiles.iterator();
		String sFileName;
		int i = 0;
		
		System.out.println("print need files start!");
		while(it.hasNext())
		{
			i++;
			sFileName = (String)it.next();
			System.out.println(i +" :" + sFileName);
		}
	}


}