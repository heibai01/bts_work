package com.led.player.biz;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.led.player.dao.GPSLoopImageDao;
import com.led.player.dao.GPSLoopTextDao;
import com.led.player.dao.GPSLoopUniversalWindowDao;
import com.led.player.dao.GPSLoopVideoDao;
import com.led.player.po.GPSLoopImagePo;
import com.led.player.po.GPSLoopTextPo;
import com.led.player.po.GPSLoopVideoPo;
import com.led.player.po.UniversalWindowPo;

/* iFileType  2-视频  0-图片   24-文字
 */

public class PullParseService_old {
    protected static final int VIDEO_TYPE = 2;	    		//视频
    protected static final int PIC_TYPE= 0;	    			//图片
    protected static final int TEXT_TYPE= 24;	    		//文字
    
    private static final String TAG = "PULL_PARSE_SERVICE";	    		//
    Context mContext;
    
    public PullParseService_old(Context context)
    {
    	this.mContext = context;
    }
    
    private long beginInsert = 0;
	public boolean readSyncWidInfo(Context cont,InputStream inputStream){
		Log.e("PullParseService", "开始解析xml:");
		beginInsert = System.currentTimeMillis();
		try{
			mContext = cont;
			UniversalWindowPo univWindVo = null;// = new UniversalWindowVo();
			XmlPullParser parser = Xml.newPullParser();  
			parser.setInput(inputStream, "UTF-8");  
	        int event = parser.getEventType();//产生第一个事�? 
	        boolean bReadWinInfoFinished = false;
	        
	        while(event!=XmlPullParser.END_DOCUMENT){  
	            switch(event){  
	            case XmlPullParser.START_DOCUMENT://判断当前事件是否是文档开始事�? 
	                break;  
	            case XmlPullParser.START_TAG://判断当前事件是否是标签元素开始事�? 
	                if("PlayWindowData".equalsIgnoreCase(parser.getName())){//判断�?��标签元素是否是book  
	                	if(null == univWindVo){
	                		univWindVo =  new UniversalWindowPo();
	                	}
	                }
	                
	            	if(univWindVo!=null){  
	                    if("WindowRotation".equalsIgnoreCase(parser.getName())){//判断�?��标签元素是否是name  
	                    	univWindVo.set_windowrotation(Integer.parseInt(parser.nextText()));  
	                    }else if("StartX".equalsIgnoreCase(parser.getName())){//判断�?��标签元素是否是price  
	                    	univWindVo.set_startX(Integer.parseInt(parser.nextText()));  
	                    } else if("StartY".equalsIgnoreCase(parser.getName())){
	                    	univWindVo.set_startY(Integer.parseInt(parser.nextText()));		                    	
	                    }else if("Width".equalsIgnoreCase(parser.getName())){
	                    	univWindVo.set_width(Integer.parseInt(parser.nextText()));		                    	
	                    }else if("Height".equalsIgnoreCase(parser.getName())){
	                    	univWindVo.set_height(Integer.parseInt(parser.nextText()));		                    	
	                    }else if("BorderType".equalsIgnoreCase(parser.getName())){
	                    	univWindVo.set_bordertype(Integer.parseInt(parser.nextText()));
	                    }else if("BorderColor".equalsIgnoreCase(parser.getName())){
	                    	univWindVo.set_bordercolor(parser.nextText());
	                    }
	                }
	                break;  
	            case XmlPullParser.END_TAG://判断当前事件是否是标签元素结束事�? 
	                if("PlayWindowData".equalsIgnoreCase(parser.getName())){//判断结束标签元素是否是book  
	                	//保存进入数据�?
	                	if(univWindVo!=null){
	                		String str = univWindVo.toString();
	                		Log.i(TAG, str);
	                		
	                		GPSLoopUniversalWindowDao universalWindowDao = new GPSLoopUniversalWindowDao(mContext);
	                		
	                		universalWindowDao.addUniversalWindow(univWindVo);
	                		Log.i(TAG, "----窗口对象插入完毕");
	                		bReadWinInfoFinished = true;
	                	}
	                }  
	                break;
	            }  
	            //TODO
	            if(bReadWinInfoFinished)
	            	break;
	            event = parser.next();//进入下一个元素并触发相应事件  
	        }//end while  
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if (inputStream!=null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}
	

	public boolean readSyncMedioInfor(Context cont,InputStream inputStream){
		boolean isAddOk = false;
		try{
			mContext = cont;
			GPSLoopVideoPo videoVo = new GPSLoopVideoPo();
			GPSLoopImagePo imageVo = new GPSLoopImagePo();
			GPSLoopTextPo textVo = new GPSLoopTextPo();
			XmlPullParser parser = Xml.newPullParser();  
			parser.setInput(inputStream, "UTF-8");  
	        int event = parser.getEventType();//产生第一个事�? 
	        int i = 0;
	        int iFileType = -1;
	        
	        while(event!=XmlPullParser.END_DOCUMENT){  
	            switch(event){  
	            case XmlPullParser.START_DOCUMENT://判断当前事件是否是文档开始事�? 
	                break;  
	            case XmlPullParser.START_TAG://判断当前事件是否是标签元素开始事�? 
	            	if("ProgramData".equalsIgnoreCase(parser.getName())){//判断�?��标签元素是否是book  
//	            		MyLog.e(TAG,i+  "VIDEO_TYPE-Node Name: " + parser.getName());
	            		iFileType = Integer.parseInt(parser.getAttributeValue(1));
//	            		MyLog.e(TAG, "iFileType : " + iFileType);
	            	}
	            		
	            	if(VIDEO_TYPE == iFileType){
//		            		MyLog.e(TAG, "VIDEO_TYPE-Node Name: " + parser.getName());
		            		
		            		if("VideoProportion".equalsIgnoreCase(parser.getName())){//判断�?��标签元素是否是VideoProportion 
//		            			int iProportion = Integer.parseInt(parser.nextText());
//		            			MyLog.e(TAG, "VIDEO_TYPE-VideoProportion: " + iProportion);
//		            			videoVo.set_videoproportion(i);
	            				videoVo.set_videoproportion(Integer.parseInt(parser.nextText()));  
	            			}else if("VoiceValue".equalsIgnoreCase(parser.getName())){//判断�?��标签元素是否是price  
	            				videoVo.set_voicevalue(Integer.parseInt(parser.nextText()));  
	            			} else if("FilePath".equalsIgnoreCase(parser.getName())){
	            				String filePath = "syncloop_program/" +  parser.nextText();
	            				videoVo.set_filepath(filePath);		                    	
	            			} else if("beginTime".equalsIgnoreCase(parser.getName())){
	            				videoVo.set_begintime(parser.nextText());		                    	
	            			}
	            		}
	            	
	            	if(PIC_TYPE == iFileType){
	            		if("Transparent".equalsIgnoreCase(parser.getName())){//判断�?��标签元素是否是name  
	            			if("false".equalsIgnoreCase(parser.nextText())){
	            				imageVo.setTransparent(0);
	            			}else {
	            				imageVo.setTransparent(1);	            				
	            			}
//	            			imageVo.setTransparent(Integer.parseInt(parser.nextText()));  
	            		}else if("Effects".equalsIgnoreCase(parser.getName())){//判断�?��标签元素是否是price  
	            			imageVo.setEffects(Integer.parseInt(parser.nextText()));  
	            		} else if("TxtSpeed".equalsIgnoreCase(parser.getName())){
	            			imageVo.setTxtspeed(Integer.parseInt(parser.nextText()));		                    	
		                }else if("Description".equalsIgnoreCase(parser.getName())){
		                	imageVo.setDescription(Integer.parseInt(parser.nextText()));		                    	
		                }else if("StayLine".equalsIgnoreCase(parser.getName())){
		                	imageVo.setStayline(Integer.parseInt(parser.nextText()));		                    	
		                }else if("BgClr".equalsIgnoreCase(parser.getName())){
		                	imageVo.setBgclr(parser.nextText());
		                }else if("FilePath".equalsIgnoreCase(parser.getName())){
            				String filePath = "syncloop_program/" +  parser.nextText();
		                   	imageVo.setFilepath(filePath);
		                   	
		                }else if("beginTime".equalsIgnoreCase(parser.getName())){
		                	imageVo.setBegintime(parser.nextText());
		                }
	            	}
	            	
	            	
	            	if(TEXT_TYPE == iFileType){
	            		if("ProLen".equalsIgnoreCase(parser.getName())){//判断�?��标签元素是否是name  
	            			textVo.set_prolen(Integer.parseInt(parser.nextText()));  
	            		}else if("BgClr".equalsIgnoreCase(parser.getName())){//判断�?��标签元素是否是price  
	            			textVo.set_bgclr(parser.nextText());  
	            		} else if("Transparent".equalsIgnoreCase(parser.getName())){//判断�?��标签元素是否是price
	            			if("false".equalsIgnoreCase(parser.nextText())){
	            				textVo.set_transparent(0);
	            			}else {
	            				textVo.set_transparent(1);	            				
	            			}
//	            			textVo.set_bgclr(parser.nextText());  
	            		}else if("Content".equalsIgnoreCase(parser.getName())){//判断�?��标签元素是否是price  
	            			textVo.set_content(parser.nextText());  
	            		} else if("lfHeight".equalsIgnoreCase(parser.getName())){
	            			textVo.set_height(Integer.parseInt(parser.nextText()));	
	            		} else if("lfItalic".equalsIgnoreCase(parser.getName())){
	            			textVo.set_italic(Integer.parseInt(parser.nextText()));
	            		} else if("lfWeight".equalsIgnoreCase(parser.getName())){
	            			textVo.set_weight(Integer.parseInt(parser.nextText()));
	            		} else if("lfUnderline".equalsIgnoreCase(parser.getName())){
	            			textVo.set_underline(Integer.parseInt(parser.nextText()));
	        			} else if("lfFaceName".equalsIgnoreCase(parser.getName())){
	        				textVo.set_facename(Integer.parseInt(parser.nextText()));
	        			} else if("WordClr".equalsIgnoreCase(parser.getName())){
	        				textVo.set_wordclr(parser.nextText());
	        			} else if("WalkSpeed".equalsIgnoreCase(parser.getName())){
	        				textVo.set_walkspeed(Integer.parseInt(parser.nextText()));
	        			} else if("SingleLine".equalsIgnoreCase(parser.getName())){
	        				
	            			if("false".equalsIgnoreCase(parser.nextText())){
	            				textVo.set_singleline(0);
	            			}else {
	            				textVo.set_singleline(1);	            				
	            			}
//	        				textVo.set_singleline(Integer.parseInt(parser.nextText()));		                    	
	        			} else if("Static".equalsIgnoreCase(parser.getName())){
	            			if("false".equalsIgnoreCase(parser.nextText())){
	            				textVo.set_static(0);
	            			}else {
	            				textVo.set_static(1);	            				
	            			}
//	        				textVo.set_static(Integer.parseInt(parser.nextText()));		                    	
	        			}else if("beginTime".equalsIgnoreCase(parser.getName())){
	        				textVo.set_begintime(parser.nextText());		                    	
	            		}
	            	}
	            	i++;
	            	break;  
		        case XmlPullParser.END_TAG://判断当前事件是否是标签元素结束事�? 
		        	if("ProgramData".equalsIgnoreCase(parser.getName())){//判断结束标签元素是否是ProgramData  
//		            	MyLog.e(TAG,i+ "保存至文件END_TAG: " + parser.getName());
		            	String str;
		            	if(PIC_TYPE == iFileType){
		                	str = imageVo.toString();
//	                		MyLog.i(TAG, str);	       
	                		GPSLoopImageDao imageDao = new GPSLoopImageDao(mContext);
	                		isAddOk = imageDao.addImage(imageVo);
	                		if (isAddOk==false) {
								imageDao.dropImageTable();
							}
		            	}else if(TEXT_TYPE == iFileType){
	                		str = textVo.toString();
//	                		MyLog.i(TAG, str);		     
	                		GPSLoopTextDao textDao = new GPSLoopTextDao(mContext);
	                		isAddOk = textDao.addText(textVo);
	                		if (isAddOk==false) {
								textDao.dropTextTable();
							}
		            	}else if(VIDEO_TYPE == iFileType){
	                		str = videoVo.toString();
//	                		MyLog.i(TAG, str);
	                		
	                		GPSLoopVideoDao videoDao = new GPSLoopVideoDao(mContext);
	                		isAddOk = videoDao.addVideo(videoVo);
	                		if (isAddOk==false) {
								videoDao.dropVideoTable();
							}
		            	}
		            }  
		            break;  
		        }  
	            event = parser.next();//进入下一个元素并触发相应事件  
	        }//end while  
	        Log.i(TAG, "---------------读取数据完毕！----已入库-----------------");		     
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}finally{
			if (inputStream!=null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
Log.i(TAG, "---xml解析完毕----------------耗时："+(System.currentTimeMillis()-beginInsert)+"-----------------------");		
		return isAddOk;
	}

	public boolean delSyncAllWidInfor(){
		GPSLoopUniversalWindowDao univWindDao = new GPSLoopUniversalWindowDao(mContext);
		return univWindDao.delAllWindows();
	}
	
	public boolean delSyncAllVideoInfor(){
		try {
			GPSLoopVideoDao videoDao = new GPSLoopVideoDao(mContext);
			return videoDao.delAllVideo();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	public boolean delSyncAllPicInfor(){
		try {
			GPSLoopImageDao imageDao = new GPSLoopImageDao(mContext);
			return imageDao.delAllImage();	
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return false;
	}
	
	public boolean delSyncAllTextInfor(){
		try {
			GPSLoopTextDao textDao = new GPSLoopTextDao(mContext);
			return textDao.delAllText();			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	

}  