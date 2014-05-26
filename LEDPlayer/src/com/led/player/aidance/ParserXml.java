package com.led.player.aidance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.graphics.Color;
import android.util.Log;

import com.led.player.window.AnalogClockWindow;
import com.led.player.window.BasicPage;
import com.led.player.window.CameraWindow;
import com.led.player.window.CountDownWindow;
import com.led.player.window.DigitalClockWindow;
import com.led.player.window.GlobalPage;
import com.led.player.window.HumitureWindow;
import com.led.player.window.ImageVo;
import com.led.player.window.LedXmlClass;
import com.led.player.window.PageInterface;
import com.led.player.window.TextVo;
import com.led.player.window.UniversalWindow;
import com.led.player.window.VideoVo;
/**
 * 解析 xml文件。这个类的实例
 * @author 1231
 *
 */
public class ParserXml {
	private boolean DEBUG = false;

	BasicPage basicpage;
	GlobalPage globalpage;
	ImageVo imageVo;
	VideoVo videoVo;
	TextVo textVo;
	UniversalWindow universalwindow;
	AnalogClockWindow analogclockwindow;
	DigitalClockWindow digitalclockwindow;
	CountDownWindow countdownwindow;
	CameraWindow camerawindow;
	HumitureWindow humiturewindow;
	int mplaytype;
	int mpagetype;
	int event;
	String PlayPageData = "PlayPageData";
	String PlayWindowData = "PlayWindowData";
	String ProgramData = "ProgramData";
	String DisplayWindowData = "DisplayWindowData";
	String WindowType = "WindowType";
	String ProType = "ProType";

	/**
	 * 解析得到 LedXmlClass 实例。包含xml所有属性设置如  GlobalPage和 ArrayList<BasicPage>
	 * @param filepath
	 * @return
	 * @throws Exception 
	 */
	public LedXmlClass ParserXML(String filepath)throws Exception{
		// XmlResourceParser锟斤拷XmlPullParser锟斤拷锟斤拷锟洁。
		boolean ifparserfinish = false;
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();

		InputStream inputstream = new FileInputStream(filepath);
		parser.setInput(inputstream, "UTF-8");


		if(this.DEBUG)Log.i("XmlResTest", "%%%%%%%%%%%%%%%   "+parser);
		LedXmlClass xmlobj = new LedXmlClass();
		this.event = parser.getEventType();
		//锟斤拷没锟叫碉拷XML锟侥碉拷锟侥斤拷尾锟斤拷
		if(this.DEBUG)Log.i("XmlResTest", "@@@@@@@@@@@@@   "+this.event);
		while (this.event != XmlPullParser.END_DOCUMENT)
		{
			switch(this.event){
			case XmlPullParser.START_DOCUMENT:
				if(this.DEBUG)Log.i("XmlResTest", "@@@@@@@@@@@@@   START_DOCUMENT  "+parser.getName());

				break;
			case XmlPullParser.START_TAG:
				if(this.DEBUG)Log.i("XmlResTest", "@@@@@@@@@@@@@   START_TAG  "+parser.getName());
				if(this.PlayPageData.equalsIgnoreCase(parser.getName())){
					ParserPage(xmlobj,parser);           //parser锟斤拷<PlayPageData>锟斤拷
				}
				break;
			case XmlPullParser.END_TAG:
				if(this.DEBUG)Log.i("XmlResTest", "@@@@@@@@@@@@@   END_TAG  "+parser.getName());
				if("LEDProject".equalsIgnoreCase(parser.getName())){
					ifparserfinish = true;
				}
				break;
			case XmlPullParser.TEXT:
				if(this.DEBUG)Log.i("XmlResTest", "@@@@@@@@@@@@@   TEXT  "+parser.getText());

				break;
			default:
				if(this.DEBUG)Log.i("XmlResTest", "@@@@@@@@@@@@@   default  "+parser.getName());
				break;
			}
			if(!(this.PlayPageData.equalsIgnoreCase(parser.getName()))){
				this.event = parser.next();
			}
			this.event = parser.getEventType();


		}
		inputstream.close();

		if(ifparserfinish){
			return xmlobj;
		}else{
			return null;
		}

	}


	public void ParserPage(LedXmlClass xmlobj,XmlPullParser parser) throws Exception{

		parser.next();
		if(this.DEBUG)Log.i("ParserPage", "@@@@@@@@@@@@@   START_TAG "+parser.getName());
		this.event = parser.next();
		if(this.DEBUG)Log.i("ParserPage", "@@@@@@@@@@@@@   START_TAG  "+parser.getName());
		this.event = parser.getEventType();
		if(parser.getName().equalsIgnoreCase("PlayType")){
			this.event = parser.next();
			this.mplaytype = Integer.parseInt(parser.getText());
			if(this.DEBUG)Log.i("ParserPage", "@@@@@@@@@@@@@   mplaytype "+this.mplaytype);
		}
		this.event = parser.next();
		if(this.DEBUG)Log.i("ParserPage", "@@@@@@@@@@@@@   START_TAG  "+parser.getName());
		this.event = parser.next();
		this.event = parser.next();
		if(this.DEBUG)Log.i("ParserPage", "@@@@@@@@@@@@@   START_TAG  "+parser.getName());
		if(parser.getName().equalsIgnoreCase("PageType")){
			parser.next();
			this.mpagetype = Integer.parseInt(parser.getText());
			if(this.DEBUG)Log.i("ParserPage", "@@@@@@@@@@@@@   mpagetype "+this.mpagetype);
		}
		if(this.mpagetype == 0){
			this.globalpage = new GlobalPage();
			this.globalpage.setPageType(this.mpagetype);
			this.globalpage.setPlayType(this.mplaytype);

			xmlobj.setIsGlobalPage(true);

			GeneralParserPage(xmlobj,this.globalpage,parser);         //parser锟斤拷<PlayWindowData>锟斤拷
		}
		else if(this.mpagetype == 1){

			this.basicpage = new BasicPage();
			this.basicpage.setPageType(this.mpagetype);
			this.basicpage.setPlayType(this.mplaytype);

			xmlobj.setIsGlobalPage(false);
			if(this.DEBUG)Log.i("ParserPage", "@@@@@@@@@@@@@   mpagetype == 1 ");
			GeneralParserPage(xmlobj,this.basicpage,parser);          ////parser锟斤拷<PlayWindowData>锟斤拷

		}
	}

	public void GeneralParserPage(LedXmlClass xmlobj,PageInterface parentpage,XmlPullParser parser) throws Exception{
		this.event = parser.getEventType();

		while(!(this.PlayPageData.equalsIgnoreCase(parser.getName()))){
			switch(this.event){
			case XmlPullParser.START_TAG:
				if(this.DEBUG)Log.i("GeneralParserPage", "@@@@@@@@@@@@@   START_TAG  "+parser.getName());
				if(this.PlayWindowData.equalsIgnoreCase(parser.getName())&&(parser.getEventType()==XmlPullParser.START_TAG)){
					while(!(this.PlayPageData.equalsIgnoreCase(parser.getName()))){
						this.event = parser.next();
						this.event = parser.next();

						if(this.WindowType.equalsIgnoreCase(parser.getName())){
							parser.next();
							parentpage.Addwindowlist(Integer.parseInt(parser.getText()));
							if(this.DEBUG)Log.i("GeneralParserPage", "@@@@@@@@@@@@@WindowList size "+parentpage.getWindowList().size());
							switch(Integer.parseInt(parser.getText())){
							case 0:
								//锟斤拷锟矫斤拷锟斤拷通锟矫达拷锟侥猴拷锟斤拷
								ParserUniversalWindow(parentpage,parser);//parser锟斤拷指示锟斤拷锟斤拷锟斤拷锟酵碉拷Text锟斤拷
								break;
							case 1:
								//鏃堕挓绐�
								ParserClockWindow(parentpage,parser);
								break;
							case 2:
								//娓╂箍搴︾獥
								ParserHumitureWindow(parentpage,parser);
								break;
							case 3:
								//鍊掕鏃�
								ParserCountDownWindow(parentpage,parser);
								break;
							case 5:
								//澶栭儴瑙嗛
								ParserCameraWindow(parentpage,parser);
								break;
							}
						}
					}

				}
				break;
			case XmlPullParser.END_TAG:
				if(this.DEBUG)Log.i("GeneralParserPage", "@@@@@@@@@@@@@   END_TAG  "+parser.getName());

				break;
			}
			if(!(this.PlayPageData.equalsIgnoreCase(parser.getName()))){
				this.event = parser.next();
			}

		}

		if((this.PlayPageData.equalsIgnoreCase(parser.getName()))&&
				(parser.getEventType()==XmlPullParser.END_TAG)){

			if(xmlobj.getIsGlobalPage()==true){
				xmlobj.setGlobalPage(this.globalpage);
			}
			else{
				xmlobj.AddBasicPage(this.basicpage);//每锟斤拷锟斤拷拥亩锟斤拷锟斤拷锟絇arserPage(parser)锟斤拷锟铰达拷锟斤拷锟侥★拷
				if(this.DEBUG)Log.i("XmlResTest", "@@@@@@@@@@@@@   xmlobj.AddBasicPage(basicpage)");
			}
			this.event = parser.next();
			this.event = parser.next();

		}

	}

	public void ParserClockWindow(PageInterface parentpage,XmlPullParser parser)throws XmlPullParserException, IOException{

		this.analogclockwindow = new AnalogClockWindow();
		this.digitalclockwindow = new DigitalClockWindow();

		if(this.DEBUG)Log.i("AnalogClockWindow", "@@@@@@@@@@@@@parentpage.setWindowType");

		this.event = parser.getEventType();

		while(!(this.PlayWindowData.equalsIgnoreCase(parser.getName()))){
			switch(this.event){
			case XmlPullParser.START_TAG:
				if(this.DEBUG)Log.i("Parseranalogclockwindow", "@@@@@@@@@@@@@   START_TAG  "+parser.getName());

				if("BorderType".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setBorderType(Integer.parseInt(parser.getText()));
					this.digitalclockwindow.setBorderType(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserUniversalWindow", "@@@@@@@@@@@@@ BorderType  "+this.analogclockwindow.getBorderType());
				}
				else if("BgClr".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setBgClr(Color.parseColor("#"+parser.getText()));
					this.digitalclockwindow.setBgClr(Color.parseColor("#"+parser.getText()));
				}
				else if("StartX".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setStartX(Integer.parseInt(parser.getText()));
					this.digitalclockwindow.setStartX(Integer.parseInt(parser.getText()));
				}
				else if("StartY".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setStartY(Integer.parseInt(parser.getText()));
					this.digitalclockwindow.setStartY(Integer.parseInt(parser.getText()));
				}
				else if("Width".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setWidth(Integer.parseInt(parser.getText()));
					this.digitalclockwindow.setWidth(Integer.parseInt(parser.getText()));
				}
				else if("Height".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setHeiht(Integer.parseInt(parser.getText()));
					this.digitalclockwindow.setHeight(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserAnalogClockWindow", "@@@@@@@@@@@@@ Height  "+this.analogclockwindow.getHeiht());
				}
				else if("StatcWord".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setContent(parser.getText());
					this.digitalclockwindow.setContent(parser.getText());
				}
				else if(this.ProgramData.equalsIgnoreCase(parser.getName())){
					if(this.DEBUG)Log.i("ParserAnalogClockWindow", "@@@@@@@@@@@@@  "+parser.getName());
					parser.next();
					parser.next();
					if(this.ProType.equalsIgnoreCase(parser.getName())){
						if(this.DEBUG)Log.i("ParserAnalogClockWindow", "@@@@@@@@@@@@@  "+parser.getName());
						parser.next();
						if(this.DEBUG)Log.i("ParserAnalogClockWindow", "@@@@@@@@@@@@@  "+parser.getText());
						switch(Integer.parseInt(parser.getText())){
						case 25:
							parentpage.setWindowType("AnalogClockWindow");
							break;
						case 26:
							parentpage.setWindowType("DigitalClockWindow");
							break;

						}
					}
				}
				else if("WordClr".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setWordClr(Color.parseColor("#"+parser.getText()));
					this.digitalclockwindow.setWordClr(Color.parseColor("#"+parser.getText()));
					if(this.DEBUG)Log.i("ParserClockWindow", "@@@@@@@@@@@@@   WordClr  "+parser.getText());
					if(this.DEBUG)Log.i("ParserClockWindow", "@@@@@@@@@@@@@   WordClr  "+Color.parseColor("#"+parser.getText()));
				}
				else if("FixedWordClr".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setFixedWordClr(Color.parseColor("#"+parser.getText()));
					this.digitalclockwindow.setFixedWordClr(Color.parseColor("#"+parser.getText()));
				}
				else if("Language".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setLanguage(Boolean.parseBoolean(parser.getText()));
					this.digitalclockwindow.setLanguage(Boolean.parseBoolean(parser.getText()));
				}
				else if("lfItalic".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setItalic(Integer.parseInt(parser.getText()));
					this.digitalclockwindow.setItalic(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserClockWindow", "@@@@@@@@@@@@@   lfItalic  "+parser.getText());
				}
				else if("lfUnderline".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setUnderline(Integer.parseInt(parser.getText()));
					this.digitalclockwindow.setUnderline(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserClockWindow", "@@@@@@@@@@@@@   lfUnderline  "+parser.getText());
				}
				else if("lfWeight".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setWeight(Integer.parseInt(parser.getText()));
					this.digitalclockwindow.setWeight(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserClockWindow", "@@@@@@@@@@@@@   lfWeight  "+parser.getText());
				}
				else if("lfFaceName".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setFaceName(Integer.parseInt(parser.getText()));
					this.digitalclockwindow.setFaceName(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserClockWindow", "@@@@@@@@@@@@@   lfFaceName  "+parser.getText());
				}
				else if("lfHeight".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setIfHeight(Integer.parseInt(parser.getText()));
					this.digitalclockwindow.setIfHeight(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserClockWindow", "@@@@@@@@@@@@@   lfHeight  "+parser.getText());
				}
				else if("BgWord".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setBgWord(parser.getText());
					if(this.DEBUG)Log.i("ParserClockWindow", "@@@@@@@@@@@@@   BgWord  "+parser.getText());
				}
				else if("ShowDate".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.analogclockwindow.setShowDate(Boolean.parseBoolean(parser.getText()));
				}
				else if("ShowWeek".equalsIgnoreCase(parser.getName())){
					parser.next();
					if(parentpage.getWindowType().equalsIgnoreCase("AnalogClockWindow")){
						this.analogclockwindow.setShowWeek(Boolean.parseBoolean(parser.getText()));
					}else if(parentpage.getWindowType().equalsIgnoreCase("DigitalClockWindow")){
						this.digitalclockwindow.setShowWeek(Boolean.parseBoolean(parser.getText()));
					}

				}
				else if("TwoBitYear".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.digitalclockwindow.setTwoBitYear(Boolean.parseBoolean(parser.getText()));
					if(this.DEBUG)Log.i("ParserclockDownWindow", "@@@@@@@@@@@@@   TwoBitYear  "+parser.getText());
				}
				else if("DateTimeStyle".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.digitalclockwindow.setDateTimeStyle(Boolean.parseBoolean(parser.getText()));
				}
				else if("TwelveHour".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.digitalclockwindow.setTwelveHour(Boolean.parseBoolean(parser.getText()));
					if(this.DEBUG)Log.i("ParserclockDownWindow", "@@@@@@@@@@@@@   TwelveHour  "+parser.getText());
				}
				else if("SingleLineShow".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.digitalclockwindow.setSingleLineShow(Boolean.parseBoolean(parser.getText()));
					if(this.DEBUG)Log.i("ParserclockDownWindow", "@@@@@@@@@@@@@   SingleLineShow  "+parser.getText());
				}
				else if("ShowDay".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.digitalclockwindow.setShowDay(Boolean.parseBoolean(parser.getText()));
					if(this.DEBUG)Log.i("ParserclockDownWindow", "@@@@@@@@@@@@@   ShowDay  "+parser.getText());
				}
				else if("ShowCHADays".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.digitalclockwindow.setShowCHADays(Boolean.parseBoolean(parser.getText()));
					if(this.DEBUG)Log.i("ParserclockDownWindow", "@@@@@@@@@@@@@   ShowCHADays  "+parser.getText());
				}
				else if("ShowAmPm".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.digitalclockwindow.setShowAmPm(Boolean.parseBoolean(parser.getText()));
					if(this.DEBUG)Log.i("ParserclockDownWindow", "@@@@@@@@@@@@@   ShowAmPm  "+parser.getText());
				}
				else if("ShowSeconds".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.digitalclockwindow.setShowSeconds(Boolean.parseBoolean(parser.getText()));
					if(this.DEBUG)Log.i("ParserclockDownWindow", "@@@@@@@@@@@@@   ShowSeconds  "+parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:
				if(this.DEBUG)Log.i("ParserUniversalWindow", "@@@@@@@@@@@@@   END_TAG  "+parser.getName());

				break;
			default:
				break;
			}

			this.event = parser.next();
		}

		if(this.PlayWindowData.equalsIgnoreCase(parser.getName())&&
				(parser.getEventType()==XmlPullParser.END_TAG)){
			if(parentpage.getWindowType().equalsIgnoreCase("AnalogClockWindow")){
				parentpage.setAnalogClockWindow(this.analogclockwindow);
				if(this.DEBUG)Log.i("GeneralParserPage", "@@@@@@@@@@@@@   parentpage.setAnalogClockWindow(analogclockwindow)");
			}else if(parentpage.getWindowType().equalsIgnoreCase("DigitalClockWindow")){
				parentpage.setDigitalClockWindow(this.digitalclockwindow);
				if(this.DEBUG)Log.i("GeneralParserPage", "@@@@@@@@@@@@@   parentpage.setDigitalClockWindow(digitalclockwindow)");
			}
			this.event = parser.next();
			this.event = parser.next();
		}

	}

	public void ParserHumitureWindow(PageInterface parentpage,XmlPullParser parser)throws XmlPullParserException, IOException{

		this.humiturewindow = new HumitureWindow();
		parentpage.setWindowType("HumitureWindow");
		if(this.DEBUG)Log.i("HumitureWindow", "@@@@@@@@@@@@@parentpage.setWindowType");

		this.event = parser.getEventType();

		while(!(this.PlayWindowData.equalsIgnoreCase(parser.getName()))){
			switch(this.event){
			case XmlPullParser.START_TAG:
				String tag_name = parser.getName();
				if(this.DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@   START_TAG  "+tag_name);
				if("BorderType".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setBorderType(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@ BorderType  "+this.humiturewindow.getBorderType());
				}
				else if("BgClr".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setBgClr(Color.parseColor("#"+parser.getText()));
				}
				else if("StartX".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setStartX(Integer.parseInt(parser.getText()));
				}
				else if("StartY".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setStartY(Integer.parseInt(parser.getText()));
				}
				else if("Width".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setWidth(Integer.parseInt(parser.getText()));
				}
				else if("Height".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setHeight(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@ Height  "+this.humiturewindow.getHeight());
				}
				else if("MultiLine".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setMultiLine(Boolean.parseBoolean(parser.getText()));
				}
				else if ("showType".equalsIgnoreCase(tag_name)) {
					parser.next();
					this.humiturewindow.setShowType(Integer.parseInt(parser.getText()));
				}
				else if("HasTemperature".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setHasTemperature(Boolean.parseBoolean(parser.getText()));
				}
				else if("HasHumidity".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setHasHumidity(Boolean.parseBoolean(parser.getText()));
				}
				else if("DisplayUnit".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setDisplayUnit(Boolean.parseBoolean(parser.getText()));
				}
				else if("Centigrade".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setCentigrade(Boolean.parseBoolean(parser.getText()));
				}
				else if("StrTemperature".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setStrTemperature(parser.getText());
					if(this.DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@   ProLen  "+parser.getText());
				}
				else if("StrHumidity".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setStrHumidity(parser.getText());
					if(this.DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@   StayLine  "+parser.getText());
				}
				else if("WordClr".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setWordClr(Color.parseColor("#"+parser.getText()));
					if(this.DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@   WordClr  "+parser.getText());
					if(this.DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@   WordClr  "+Color.parseColor("#"+parser.getText()));
				}
				else if("lfItalic".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setItalic(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@   lfItalic  "+parser.getText());
				}
				else if("lfUnderline".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.humiturewindow.setUnderline(Integer.parseInt(parser.getText()));
					if(DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@   lfUnderline  "+parser.getText());
				}
				else if("lfWeight".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setWeight(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@   lfWeight  "+parser.getText());
				}
				else if("lfFaceName".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setFaceName(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@   lfFaceName  "+parser.getText());
				}
				else if("lfHeight".equalsIgnoreCase(tag_name)){
					parser.next();
					this.humiturewindow.setIfHeight(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@   lfHeight  "+parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:
				if(this.DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@   END_TAG  "+parser.getName());

				break;
			default:
				break;
			}

			this.event = parser.next();
		}

		if(this.PlayWindowData.equalsIgnoreCase(parser.getName())&&
				(parser.getEventType()==XmlPullParser.END_TAG)){
			if(parentpage.getWindowType().equalsIgnoreCase("HumitureWindow")){
				parentpage.setHumitureWindow(this.humiturewindow);
				if(this.DEBUG)Log.i("ParserHumitureWindow", "@@@@@@@@@@@@@   parentpage.AddHumitureWindow(HumitureWindow)");
			}
			this.event = parser.next();
			this.event = parser.next();
		}
	}

	public void ParserCountDownWindow(PageInterface parentpage,XmlPullParser parser)throws XmlPullParserException, IOException{

		this.countdownwindow = new CountDownWindow();
		parentpage.setWindowType("CountDownWindow");
		if(this.DEBUG)Log.i("CountDownWindow", "@@@@@@@@@@@@@parentpage.setWindowType");

		this.event = parser.getEventType();

		while(!(this.PlayWindowData.equalsIgnoreCase(parser.getName()))){
			switch(this.event){
			case XmlPullParser.START_TAG:
				if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   START_TAG  "+parser.getName());

				if("BorderType".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setBorderType(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@ BorderType  "+this.countdownwindow.getBorderType());
				}
				else if("BgClr".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setBgClr(Color.parseColor("#"+parser.getText()));
				}
				else if("StartX".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setStartX(Integer.parseInt(parser.getText()));
				}
				else if("StartY".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setStartY(Integer.parseInt(parser.getText()));
				}
				else if("Width".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setWidth(Integer.parseInt(parser.getText()));
				}
				else if("Height".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setHeight(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@ Height  "+this.countdownwindow.getHeiht());
				}
				else if("WordClr".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setWordClr(Color.parseColor("#"+parser.getText()));
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   WordClr  "+parser.getText());
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   WordClr  "+Color.parseColor("#"+parser.getText()));
				}
				else if("FixedWordClr".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setFixedWordClr(Color.parseColor("#"+parser.getText()));
				}
				else if("Language".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setLanguage(Boolean.parseBoolean(parser.getText()));
				}
				else if("lfItalic".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setItalic(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   lfItalic  "+parser.getText());
				}
				else if("lfUnderline".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setUnderline(Integer.parseInt(parser.getText()));
					if(DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   lfUnderline  "+parser.getText());
				}
				else if("lfWeight".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setWeight(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   lfWeight  "+parser.getText());
				}
				else if("lfFaceName".equalsIgnoreCase(parser.getName())){
					parser.next();
					//						countdownwindow.setFaceName(Integer.parseInt(parser.getText())); mark 2014年2月19日 11:08:10注释掉
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   lfFaceName  "+parser.getText());
				}
				else if("lfHeight".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setIfHeight(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   lfHeight  "+parser.getText());
				}
				else if("StatcWord".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setContent(parser.getText());
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   StatcWord  "+parser.getText());
				}
				else if("EndDate".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setEndDate(parser.getText());
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   EndDate  "+parser.getText());
				}
				else if("EndTime".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setEndTime(parser.getText());
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   EndTime  "+parser.getText());
				}
				else if("ShowDateNum".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setShowDate(Boolean.parseBoolean(parser.getText()));
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   ShowDate  "+parser.getText());
				}
				else if("ShowHour".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setShowHour(Boolean.parseBoolean(parser.getText()));
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   ShowHour  "+parser.getText());
				}
				else if("ShowMins".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setShowMins(Boolean.parseBoolean(parser.getText()));
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   ShowMins  "+parser.getText());
				}
				else if("ShowSeconds".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setShowSeconds(Boolean.parseBoolean(parser.getText()));
					if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   ShowSeconds  "+parser.getText());
				}
				else if("MultiLine".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.countdownwindow.setMultiLine(Boolean.parseBoolean(parser.getText()));
				}
				break;
			case XmlPullParser.END_TAG:
				if(this.DEBUG)Log.i("ParserCountDownWindow", "@@@@@@@@@@@@@   END_TAG  "+parser.getName());

				break;
			default:
				break;
			}

			this.event = parser.next();
		}

		if(this.PlayWindowData.equalsIgnoreCase(parser.getName())&&
				(parser.getEventType()==XmlPullParser.END_TAG)){
			if(parentpage.getWindowType().equalsIgnoreCase("CountDownWindow")){
				parentpage.setCountDownWindow(this.countdownwindow);
				if(this.DEBUG)Log.i("CountDownWindow", "@@@@@@@@@@@@@   parentpage.AddUniversalWindow(universalwindow)");
			}
			this.event = parser.next();
			this.event = parser.next();
		}
	}

	public void ParserCameraWindow(PageInterface parentpage,XmlPullParser parser)throws XmlPullParserException, IOException{

		this.camerawindow = new CameraWindow();
		parentpage.setWindowType("CameraWindow");
		if(this.DEBUG)Log.i("CameraWindow", "@@@@@@@@@@@@@parentpage.setWindowType");

		this.event = parser.getEventType();

		while(!(this.PlayWindowData.equalsIgnoreCase(parser.getName()))){
			switch(this.event){
			case XmlPullParser.START_TAG:
				if(this.DEBUG)Log.i("ParserCameraWindow", "###########   START_TAG  "+parser.getName());

				if("BorderType".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.camerawindow.setBorderType(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserCameraWindow", "@@@@@@@@@@@@@ BorderType  "+this.camerawindow.getBorderType());
				}
				else if("StartX".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.camerawindow.setStartX(Integer.parseInt(parser.getText()));
				}
				else if("StartY".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.camerawindow.setStartY(Integer.parseInt(parser.getText()));
				}
				else if("Width".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.camerawindow.setWidth(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserCameraWindow", "%%%%%%%%%%%%% Width  "+this.camerawindow.getWidth());
				}
				else if("Height".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.camerawindow.setHeiht(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserCameraWindow", "^^^^^^^^^^ Height  "+this.camerawindow.getHeiht());
				}
				break;
			case XmlPullParser.END_TAG:
				if(this.DEBUG)Log.i("ParserCameraWindow", "@@@@@@@@@@@@@   END_TAG  "+parser.getName());

				break;
			default:
				break;
			}

			this.event = parser.next();
		}

		if(this.PlayWindowData.equalsIgnoreCase(parser.getName())&&
				(parser.getEventType()==XmlPullParser.END_TAG)){
			if(parentpage.getWindowType().equalsIgnoreCase("CameraWindow")){
				parentpage.setCameraWindow(this.camerawindow);
				if(this.DEBUG)Log.i("GeneralParserPage", "@@@@@@@@@@@@@   parentpage.AddUniversalWindow(universalwindow)");
			}
			this.event = parser.next();
			this.event = parser.next();
		}

	}

	public void ParserUniversalWindow(PageInterface parentpage,XmlPullParser parser)throws Exception{
		this.universalwindow = new UniversalWindow();
		parentpage.setWindowType("universalwindow");
		if(this.DEBUG)Log.i("GeneralParserPage", "@@@@@@@@@@@@@parentpage.setWindowType");

		this.event = parser.getEventType();

		while(!(this.PlayWindowData.equalsIgnoreCase(parser.getName()))){
			switch(this.event){
			case XmlPullParser.START_TAG:
				if(this.DEBUG)Log.i("ParserUniversalWindow", "@@@@@@@@@@@@@   START_TAG  "+parser.getName());
				if("WindowRotation".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.universalwindow.setWindowRotation(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserUniversalWindow", "@@@@@@@@@@@@@ WindowRotation  "+this.universalwindow.getWindowRotation());
				}
				else if("BorderType".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.universalwindow.setBorderType(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserUniversalWindow", "@@@@@@@@@@@@@ BorderType  "+this.universalwindow.getBorderType());
				}
				else if("StartX".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.universalwindow.setStartX(Integer.parseInt(parser.getText()));
				}
				else if("StartY".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.universalwindow.setStartY(Integer.parseInt(parser.getText()));
				}
				else if("Width".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.universalwindow.setWidth(Integer.parseInt(parser.getText()));
				}
				else if("Height".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.universalwindow.setHeiht(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserUniversalWindow", "@@@@@@@@@@@@@ Height  "+this.universalwindow.getHeiht());
				}
				else if(this.ProgramData.equalsIgnoreCase(parser.getName())&&(parser.getEventType()==XmlPullParser.START_TAG)){
					while(!(this.PlayWindowData.equalsIgnoreCase(parser.getName()))){
						if(this.DEBUG)Log.i("ParserUniversalWindow", "@@@@@@@@@@@@@  "+parser.getName());
//如果需要新增字段，可以在这里， begintime 点播开始时间						
						parser.next();
						parser.next();
						if(this.ProType.equalsIgnoreCase(parser.getName())){
							if(this.DEBUG)Log.i("ParserUniversalWindow", "@@@@@@@@@@@@@  "+parser.getName());
							parser.next();
							if(this.DEBUG)Log.i("ParserUniversalWindow", "@@@@@@@@@@@@@  "+parser.getText());
							this.universalwindow.Addplayerlist(Integer.parseInt(parser.getText()));
							switch(Integer.parseInt(parser.getText())){
							case 0:
								//锟斤拷锟斤拷图片锟斤拷锟斤拷锟斤拷
								ParserImageWindow(this.universalwindow,parser);//ParserImageWindow锟斤拷锟斤拷</PlayWindowData>时锟斤拷锟�
								break;
							case 1:
							case 2:
								//锟斤拷锟斤拷锟斤拷频锟斤拷锟斤拷锟斤拷
								ParserVideoWindow(this.universalwindow,parser);
								break;
							case 24:
								//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷谋锟斤拷锟斤拷锟斤拷锟�
								ParserTextWindow(this.universalwindow,parser);
								break;
							}
						}
					}

				}
				break;
			case XmlPullParser.END_TAG:
				if(this.DEBUG)Log.i("ParserUniversalWindow", "@@@@@@@@@@@@@   END_TAG  "+parser.getName());

				break;
			default:
				break;
			}
			if(!(this.PlayWindowData.equalsIgnoreCase(parser.getName()))){
				this.event = parser.next();
			}

		}

		if(this.PlayWindowData.equalsIgnoreCase(parser.getName())&&(parser.getEventType()==XmlPullParser.END_TAG)){
			if(parentpage.getWindowType().equalsIgnoreCase("universalwindow")){
				parentpage.AddUniversalWindow(this.universalwindow);
				if(this.DEBUG)Log.i("ParserUniversalWindow", "@@@@@@@@@@@@@   parentpage.AddUniversalWindow(universalwindow)");
			}
			this.event = parser.next();
			this.event = parser.next();
		}

	}

	/**
	 * 解析图片资源文件属性
	 * @param universalwindow
	 * @param parser
	 * @throws Exception
	 */
	public void ParserImageWindow(UniversalWindow universalwindow,XmlPullParser parser)throws Exception{

		this.imageVo = new ImageVo();
		universalwindow.setProType("imagewindow");
		if(this.DEBUG)Log.i("ParserImageWindow", "@@@@@@@@@@@@@universalwindow.setProType");

		this.event = parser.getEventType();

		while(!(this.ProgramData.equalsIgnoreCase(parser.getName()))){
			switch(this.event){
			case XmlPullParser.START_TAG:
				if(this.DEBUG)Log.i("ParserImageWindow", "@@@@@@@@@@@@@   START_TAG  "+parser.getName());
				if("Description".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.imageVo.setDescription(Integer.parseInt(parser.getText()));
				}
				else if("Effects".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.imageVo.setEffects(Integer.parseInt(parser.getText()));
				}
				else if("Transparent".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.imageVo.setTransparent(Boolean.parseBoolean(parser.getText()));
				}
				else if("StayLine".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.imageVo.setStayLine(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserImageWindow", "@@@@@@@@@@@@@   StayLine  "+parser.getText());
				}
				else if("BgClr".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.imageVo.setBgClr(Color.parseColor("#"+parser.getText()));
					if(this.DEBUG)Log.i("ParserImageWindow", "@@@@@@@@@@@@@   BgClr  "+parser.getText());
					if(this.DEBUG)Log.i("ParserImageWindow", "@@@@@@@@@@@@@   BgClr  "+this.imageVo.getBgClr());
				}
				else if("TxtSpeed".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.imageVo.setTxtSpeed(Integer.parseInt(parser.getText()));
				}
				else if("FilePath".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.imageVo.setFilePath(parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:
				if(this.DEBUG)Log.i("ParserImageWindow", "@@@@@@@@@@@@@   END_TAG  "+parser.getName());

				break;
			}
			this.event = parser.next();
		}

		if(this.ProgramData.equalsIgnoreCase(parser.getName())&&(parser.getEventType()==XmlPullParser.END_TAG)){
			if(universalwindow.getProType().equalsIgnoreCase("imagewindow")){
				universalwindow.AddImageVo(this.imageVo);
			}
			this.event = parser.next();
			this.event = parser.next();
		}

	}

	public void ParserVideoWindow(UniversalWindow universalwindow,XmlPullParser parser)throws XmlPullParserException, IOException{
		this.videoVo = new VideoVo();
		universalwindow.setProType("videowindow");
		if(this.DEBUG)Log.i("ParserVideoWindow", "@@@@@@@@@@@@@ParserVideoWindow.setProType");

		this.event = parser.getEventType();

		while(!(this.ProgramData.equalsIgnoreCase(parser.getName()))){
			switch(this.event){
			case XmlPullParser.START_TAG:
				if(this.DEBUG)Log.i("ParserVideoWindow", "@@@@@@@@@@@@@   START_TAG  "+parser.getName());
				if("VideoProportion".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.videoVo.setVideoProportion(Integer.parseInt(parser.getText()));
				}
				else if("VoiceValue".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.videoVo.setVoiceValue(Integer.parseInt(parser.getText()));
				}
				else if("FilePath".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.videoVo.setFilePath(parser.getText());
					if(this.DEBUG)Log.i("ParserVideoWindow", "@@@@@@@@@@@@@   FilePath  "+parser.getText());
				}
				break;
			case XmlPullParser.END_TAG:
				if(this.DEBUG)Log.i("ParserVideoWindow", "@@@@@@@@@@@@@   END_TAG  "+parser.getName());

				break;
			}
			this.event = parser.next();
		}
		if(this.ProgramData.equalsIgnoreCase(parser.getName())&&(parser.getEventType()==XmlPullParser.END_TAG)){
			if(universalwindow.getProType().equalsIgnoreCase("videowindow")){
				universalwindow.AddVideoVo(this.videoVo);
				if(this.DEBUG)Log.i("ParserVideoWindow", "@@@@@@@@@@@@@   universalwindow.AddVideoWindow(videowindow);");
			}

			this.event = parser.next();
			this.event = parser.next();
		}


	}

	public void ParserTextWindow(UniversalWindow universalwindow,XmlPullParser parser)throws XmlPullParserException, IOException{

		this.textVo = new TextVo();
		universalwindow.setProType("textwindow");
		if(this.DEBUG)Log.i("ParserTextWindow", "@@@@@@@@@@@@@ParserTextWindow.setProType");

		this.event = parser.getEventType();

		while(!(this.ProgramData.equalsIgnoreCase(parser.getName()))){
			switch(this.event){
			case XmlPullParser.START_TAG:
				if(this.DEBUG)Log.i("ParserTextWindow", "@@@@@@@@@@@@@   START_TAG  "+parser.getName());
				if("Content".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.textVo.setContent(parser.getText());
//					this.textVo.setContent(parser.nextText());
//Log.e("得到的文字难道是空", "得到的文字难道是空。。。。"+this.textVo.getTextViewAttr().getContent());					
				}
				else if("Transparent".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.textVo.setTransparent(Boolean.parseBoolean(parser.getText()));
				}
				else if("ProLen".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.textVo.setProLen(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserTextWindow", "@@@@@@@@@@@@@   ProLen  "+parser.getText());
				}
				else if("BgClr".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.textVo.setBgClr(Color.parseColor("#"+parser.getText()));
					this.textVo.getTextViewAttr().setBgClr(Color.parseColor("#"+parser.getText()));//mark 新增语句joychine，
					if(this.DEBUG)Log.i("ParserTextWindow", "@@@@@@@@@@@@@   StayLine  "+parser.getText());
				}
				else if("WordClr".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.textVo.setWordClr(Color.parseColor("#"+parser.getText()));
					if(this.DEBUG)Log.i("ParserTextWindow", "@@@@@@@@@@@@@   WordClr  "+parser.getText());
					if(this.DEBUG)Log.i("ParserTextWindow", "@@@@@@@@@@@@@   WordClr  "+Color.parseColor("#"+parser.getText()));
				}
				else if("lfItalic".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.textVo.setItalic(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserTextWindow", "@@@@@@@@@@@@@   lfItalic  "+parser.getText());
				}
				else if("lfUnderline".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.textVo.setUnderline(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserTextWindow", "@@@@@@@@@@@@@   lfUnderline  "+parser.getText());
				}
				else if("lfWeight".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.textVo.setWeight(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserTextWindow", "@@@@@@@@@@@@@   lfWeight  "+parser.getText());
				}
				else if("lfFaceName".equalsIgnoreCase(parser.getName())){
					parser.next();
					//						textwindow.setFaceName(Integer.parseInt(parser.getText()));    mark 2014年2月19日 11:08:23 注释掉
					if(this.DEBUG)Log.i("ParserTextWindow", "@@@@@@@@@@@@@   lfFaceName  "+parser.getText());
				}
				else if("lfHeight".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.textVo.setHeight(Integer.parseInt(parser.getText()));
					if(this.DEBUG)Log.i("ParserTextWindow", "@@@@@@@@@@@@@   lfHeight  "+parser.getText());
				}
				else if("WalkSpeed".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.textVo.setWalkSpeed(Integer.parseInt(parser.getText()));
				}
				else if("SingleLine".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.textVo.setSingleLine(Boolean.parseBoolean(parser.getText()));
				}
				else if("Static".equalsIgnoreCase(parser.getName())){
					parser.next();
					this.textVo.setStatic(Boolean.parseBoolean(parser.getText()));
				}
				break;
			case XmlPullParser.END_TAG:
				if(this.DEBUG)Log.i("ParserTextWindow", "@@@@@@@@@@@@@   END_TAG  "+parser.getName());

				break;
			}
			this.event = parser.next();
		}
		if(this.ProgramData.equalsIgnoreCase(parser.getName())&&
				(parser.getEventType()==XmlPullParser.END_TAG)){
			if(universalwindow.getProType().equalsIgnoreCase("textwindow")){
				universalwindow.AddTextVo(this.textVo);//锟斤拷textwindow锟斤拷锟斤拷通锟矫达拷
				if(this.DEBUG)Log.i("ParserTextWindow", "@@@@@@@@@@@@@   universalwindow.AddTextWindow(textwindow);");
			}
			this.event = parser.next();
			this.event = parser.next();
		}

	}

}
