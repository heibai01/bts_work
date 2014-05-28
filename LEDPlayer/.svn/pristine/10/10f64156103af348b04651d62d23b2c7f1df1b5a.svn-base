/*package com.led.player.biz;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.util.Xml;

import com.led.player.dao.ImageDao;
import com.led.player.dao.TextDao;
import com.led.player.dao.UniversalWindowDao;
import com.led.player.dao.VideoDao;
import com.led.player.vo.ImageVo;
import com.led.player.vo.TextVo;
import com.led.player.vo.UniversalWindowVo;
import com.led.player.vo.VideoVo;

public class ParserxmlBiz_old {
	public boolean isParseOn = false;
    protected static final int VIDEO_TYPE = 2;	    		//视频
    protected static final int PIC_TYPE= 0;	    			//图片
    protected static final int TEXT_TYPE= 24;	    		//文字
	
    private Context mContext;
    private UniversalWindowDao mUniversalWindowDao = null;
    private ImageDao mImageDao = null;
    private VideoDao mVideoDao = null;
    private TextDao mTextDao = null;
    public ArrayList<String> filePaths = null;
    
    private UniversalWindowVo mUniversalWindowVo;
    private ImageVo mImageVo;
    private VideoVo mVideoVo;
    private TextVo mTextVo;
	
    public ParserxmlBiz_old(Context context)
    {
    	this.mContext = context;
    	this.filePaths = new ArrayList<String>();
    	mImageDao = new ImageDao(mContext);
    	mVideoDao = new VideoDao(mContext);
    	mTextDao = new TextDao(mContext);
    }
    
    public boolean parseGPSLocationXml(String xmlPath){
    	boolean isParseOk = false;
    	clearStatue();
    	int iFileType=-1;
    	try {
    		XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new FileInputStream("/mnt/sdcard/gpslocdemand_progarm/gpslocdemandprogarm.xml"), "utf-8");
			int event = parser.getEventType();
			while (event!=XmlPullParser.END_DOCUMENT&&isParseOn) {
				switch (event) {
				case XmlPullParser.START_TAG:
					String tagName_head = parser.getName().trim();
					if ("PlayWindowData".equalsIgnoreCase(tagName_head)) {
						mUniversalWindowVo = new UniversalWindowVo();
					}else if ("WindowRotation".equalsIgnoreCase(tagName_head)) {
						mUniversalWindowVo._windowrotation = Integer.parseInt(parser.nextText());
					}else if ("StartX".equalsIgnoreCase(tagName_head)) {
						mUniversalWindowVo._startX = Integer.parseInt(parser.nextText());
					}else if ("StartY".equalsIgnoreCase(tagName_head)) {
						mUniversalWindowVo._startY = Integer.parseInt(parser.nextText());
					}else if ("Width".equalsIgnoreCase(tagName_head)) {
						mUniversalWindowVo._width = Integer.parseInt(parser.nextText());
					}else if ("Height".equalsIgnoreCase(tagName_head)) {
						mUniversalWindowVo._height = Integer.parseInt(parser.nextText());
					}else if ("BorderType".equalsIgnoreCase(tagName_head)) {
						mUniversalWindowVo._bordertype = Integer.parseInt(parser.nextText());
					}else if ("BorderColor".equalsIgnoreCase(tagName_head)) {
						mUniversalWindowVo._bordercolor = parser.nextText();
					}else if ("ProgramData".equalsIgnoreCase(tagName_head)) {
						iFileType = Integer.parseInt(parser.getAttributeValue(1));
						if (iFileType==PIC_TYPE) {
							mImageVo = new ImageVo();
						}else if (iFileType==VIDEO_TYPE) {
							mVideoVo = new VideoVo();
						}else if (iFileType==TEXT_TYPE) {
							mTextVo = new TextVo();
						}
					}
					
					if (PIC_TYPE == iFileType) {
						if ("Transparent".equalsIgnoreCase(tagName_head)) {
							mImageVo._transparent = Boolean.parseBoolean(parser.nextText().trim().toLowerCase())==true?1:0;
						}else if ("Effects".equalsIgnoreCase(tagName_head)) {
							mImageVo._effects = Integer.parseInt(parser.nextText());
						}else if ("TxtSpeed".equalsIgnoreCase(tagName_head)) {
							mImageVo._txtspeed = Integer.parseInt(parser.nextText());
						}else if ("Description".equalsIgnoreCase(tagName_head)) {
							mImageVo._description = Integer.parseInt(parser.nextText());
						}else if ("StayLine".equalsIgnoreCase(tagName_head)) {
							mImageVo._stayline = Integer.parseInt(parser.nextText());
						}else if ("BgClr".equalsIgnoreCase(tagName_head)) {
							mImageVo._bgclr = parser.nextText();
						}else if ("FilePath".equalsIgnoreCase(tagName_head)) {
							mImageVo._filepath = parser.nextText();
						}else if ("Coordinate".equalsIgnoreCase(tagName_head)) {
							mVideoVo._coordinate = parser.nextText();
						}else if ("CoverRadius".equalsIgnoreCase(tagName_head)) {
							mVideoVo._coverradius = Long.parseLong(parser.nextText().trim());
						}
					}else if (VIDEO_TYPE == iFileType) {
						if ("VideoProportion".equalsIgnoreCase(tagName_head)) {
							mVideoVo._videoproportion = Integer.parseInt(parser.nextText());
						}else if ("VoiceValue".equalsIgnoreCase(tagName_head)) {
							mVideoVo._voicevalue = Integer.parseInt(parser.nextText());
						}else if ("FilePath".equalsIgnoreCase(tagName_head)) {
							mVideoVo._filepath = parser.nextText();
						}else if ("Coordinate".equalsIgnoreCase(tagName_head)) {
							mVideoVo._coordinate = parser.nextText();
						}else if ("CoverRadius".equalsIgnoreCase(tagName_head)) {
							mVideoVo._coverradius = Long.parseLong(parser.nextText().trim());
						}
						
					}else if (TEXT_TYPE == iFileType) {
						if ("ProLen".equalsIgnoreCase(tagName_head)) {
							mTextVo._prolen = Integer.parseInt(parser.nextText());
						}else if ("BgClr".equalsIgnoreCase(tagName_head)) {
							mTextVo._bgclr = parser.nextText();
						}else if ("Transparent".equalsIgnoreCase(tagName_head)) {
							mTextVo._transparent = Boolean.parseBoolean(parser.nextText().trim().toLowerCase())==true?1:0;
						}else if ("Content".equalsIgnoreCase(tagName_head)) {
							mTextVo._content = parser.nextText();
						}else if ("lfHeight".equalsIgnoreCase(tagName_head)) {
							mTextVo._height = Integer.parseInt(parser.nextText());
						}else if ("lfItalic".equalsIgnoreCase(tagName_head)) {
							mTextVo._italic = Integer.parseInt(parser.nextText());
						}else if ("lfWeight".equalsIgnoreCase(tagName_head)) {
							mTextVo._weight = Integer.parseInt(parser.nextText());
						}else if ("lfUnderline".equalsIgnoreCase(tagName_head)) {
							mTextVo._underline = Integer.parseInt(parser.nextText());
						}else if ("lfFaceName".equalsIgnoreCase(tagName_head)) {
							mTextVo._facename = Integer.parseInt(parser.nextText());
						}else if ("WordClr".equalsIgnoreCase(tagName_head)) {
							mTextVo._wordclr = parser.nextText();
						}else if ("WalkSpeed".equalsIgnoreCase(tagName_head)) {
							mTextVo._walkspeed = Integer.parseInt(parser.nextText());
						}else if ("SingleLine".equalsIgnoreCase(tagName_head)) {
							mTextVo._singleline = Boolean.parseBoolean(parser.nextText().trim().toLowerCase()) == true?1:0;
						}else if ("Static".equalsIgnoreCase(tagName_head)) {
							mTextVo._static = Boolean.parseBoolean(parser.nextText().trim().toLowerCase()) == true?1:0;
						}else if ("Coordinate".equalsIgnoreCase(tagName_head)) {
							mVideoVo._coordinate = parser.nextText();
						}else if ("CoverRadius".equalsIgnoreCase(tagName_head)) {
							mVideoVo._coverradius = Long.parseLong(parser.nextText().trim());
						}
					}
					break;
				case XmlPullParser.END_TAG:
					String tagName_end = parser.getName().trim();
					if ("ProgramData".equalsIgnoreCase(tagName_end)) {
						if (VIDEO_TYPE == iFileType) {
							mVideoDao.addVideoBySql(mVideoVo);
							mVideoVo = null;
						}else if (PIC_TYPE == iFileType) {
							mImageDao.addImageBySql(mImageVo);
							mImageVo = null;
						}else if (TEXT_TYPE == iFileType) {
							mTextDao.addText(mTextVo);
							mTextVo = null;
						}
					}
					break;
				}
			}
			isParseOk = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			stopParse();
		}
    	return isParseOk;
    }

	private void clearStatue() {
		isParseOn = true;
		filePaths.clear();
		mUniversalWindowDao.delAllWinByOrg();
		mImageDao.delAllImageByOrg();
		mVideoDao.delAllVideoByOrg();
		mTextDao.delAllTextByOrg();
	}
	
	public void stopParse(){
		isParseOn = false;
	}
}
*/