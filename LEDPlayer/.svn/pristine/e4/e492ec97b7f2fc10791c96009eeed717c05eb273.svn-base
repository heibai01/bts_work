package com.led.player.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;

import com.led.player.meta.MetaData;
import com.led.player.meta.MetaData.TB_Text;
import com.led.player.meta.MySqliteOpenHelper;
import com.led.player.po.GPSLoopTextPo;

public class GPSLoopTextDao {
	private Context mContext;
	private SQLiteDatabase db;

	public GPSLoopTextDao(Context mContext) {
		this.mContext = mContext;
		this.db = MySqliteOpenHelper.getSqliteDatabase(mContext, MetaData.DB_NAME, null, MetaData.DB_VERSION).getReadableDatabase();;
	}
	
	
	public boolean addText(GPSLoopTextPo textVo){
		ContentValues values = new ContentValues();
		values.put(MetaData.TB_Text._BEGINTIME, textVo.get_begintime());
		values.put(MetaData.TB_Text._BGCLR, textVo.get_bgclr());
		values.put(MetaData.TB_Text._CONTENT, textVo.get_content() );
		values.put(MetaData.TB_Text._FACENAME, textVo.get_facename());
		values.put(MetaData.TB_Text._HEIGHT, textVo.get_height() );
		values.put(MetaData.TB_Text._ITALIC, textVo.get_italic() );
		values.put(MetaData.TB_Text._PROLEN, textVo.get_prolen() );
		values.put(MetaData.TB_Text._SINGLELINE, textVo.get_singleline());
		values.put(MetaData.TB_Text._STATIC, textVo.get_static());
		values.put(MetaData.TB_Text._TRANSPARENT, textVo.get_transparent());
		values.put(MetaData.TB_Text._UNDERLINE, textVo.get_underline());
		values.put(MetaData.TB_Text._WALKSPEED, textVo.get_walkspeed());
		values.put(MetaData.TB_Text._WEIGHT, textVo.get_weight());
		values.put(MetaData.TB_Text._WORDCLR, textVo.get_wordclr());

		return mContext.getContentResolver().insert(Uri.parse(MetaData.TB_Text.URI_TB_TEXT), values)!=null;
	}
	
	public boolean addTextByOrg(GPSLoopTextPo textVo){
		ContentValues values = new ContentValues();
		values.put(MetaData.TB_Text._BEGINTIME, textVo.get_begintime());
		values.put(MetaData.TB_Text._BGCLR, textVo.get_bgclr());
		values.put(MetaData.TB_Text._CONTENT, textVo.get_content() );
		values.put(MetaData.TB_Text._FACENAME, textVo.get_facename());
		values.put(MetaData.TB_Text._HEIGHT, textVo.get_height() );
		values.put(MetaData.TB_Text._ITALIC, textVo.get_italic() );
		values.put(MetaData.TB_Text._PROLEN, textVo.get_prolen() );
		values.put(MetaData.TB_Text._SINGLELINE, textVo.get_singleline());
		values.put(MetaData.TB_Text._STATIC, textVo.get_static());
		values.put(MetaData.TB_Text._TRANSPARENT, textVo.get_transparent());
		values.put(MetaData.TB_Text._UNDERLINE, textVo.get_underline());
		values.put(MetaData.TB_Text._WALKSPEED, textVo.get_walkspeed());
		values.put(MetaData.TB_Text._WEIGHT, textVo.get_weight());
		values.put(MetaData.TB_Text._WORDCLR, textVo.get_wordclr());
		return db.insert(MetaData.TB_Text.TB_NAME, null, values)>-1;
	}
	
	public void addTextBySql(GPSLoopTextPo textVo){
		db.execSQL("insert into "+MetaData.TB_Text.TB_NAME+"("+MetaData.TB_Text._BGCLR+","
				+MetaData.TB_Text._CONTENT+","+MetaData.TB_Text._FACENAME+","
				+MetaData.TB_Text._HEIGHT+","+MetaData.TB_Text._ITALIC+","
				+MetaData.TB_Text._PROLEN+","+MetaData.TB_Text._SINGLELINE+","
				+MetaData.TB_Text._STATIC+","+MetaData.TB_Text._TRANSPARENT+","
				+MetaData.TB_Text._UNDERLINE+","+MetaData.TB_Text._WALKSPEED+","
				+MetaData.TB_Text._WEIGHT+","+MetaData.TB_Text._WORDCLR+","
				+MetaData.TB_Text._BEGINTIME+")"
				+" values('"+textVo._bgclr+"','"+textVo._content
				+"',"+textVo._facename+","+textVo._height+","+textVo._italic+","+textVo._prolen+","
				+textVo._singleline+","+textVo._static+","+textVo._transparent+","+textVo._underline+","
				+textVo._walkspeed+","+textVo._weight+",'"+textVo._wordclr+"','"+textVo._begintime+"')");
	}
	
	public boolean delAllText(){
		return mContext.getContentResolver().delete(Uri.parse(MetaData.TB_Text.URI_TB_TEXT), null, null)>0;
	}
	
	public void delAllTextByOrg(){
		db.execSQL("delete from "+MetaData.TB_Text.TB_NAME);
	}
	
	public ArrayList<com.led.player.window.TextVo> findTextVosByCondition(String select,String[] selectArgs,String groupBy,String having,String orderBy,String limit){
		ArrayList<com.led.player.window.TextVo> textVos = new ArrayList<com.led.player.window.TextVo>();
		com.led.player.window.TextVo textVo = null;
		Cursor cursor = db.query(true, MetaData.TB_Text.TB_NAME, null, select, selectArgs, groupBy, having, orderBy, limit);
		while (cursor!=null&&cursor.moveToNext()) {
			textVo = new com.led.player.window.TextVo();
			textVo.Transparent = cursor.getInt(9)==0?false:true;
			textVo.WalkSpeed = cursor.getInt(11);
			textVo.SingleLine = cursor.getInt(7)==0?false:true;
			textVo.Static = cursor.getInt(8) ==0?false:true;
			textVo.ProLen = cursor.getInt(6);
			textVo.textviewattr.Content = cursor.getString(2);
			textVo.textviewattr.WordClr = Color.parseColor("#"+cursor.getString(13));
			textVo.textviewattr.BgClr = Color.parseColor("#"+cursor.getString(1));
			textVo.textviewattr.Italic = cursor.getInt(5);
			textVo.textviewattr.Underline = cursor.getInt(10);
			textVo.textviewattr.Height = cursor.getInt(4);
			textVo.textviewattr.FaceName = cursor.getInt(3);
			textVo.textviewattr.Weight = cursor.getInt(12);
			textVos.add(textVo);
		}
		if (cursor!=null) {
			cursor.close();
			cursor = null;
		}
		return textVos;
	}
	
	public void dropTextTable(){
		SQLiteDatabase db = MySqliteOpenHelper.getSqliteDatabase(mContext, MetaData.DB_NAME, null, MetaData.DB_VERSION).getReadableDatabase();
		db.execSQL("drop table if exists "+MetaData.TB_Text.TB_NAME);
		db.execSQL("create table "+MetaData.TB_Text.TB_NAME+"("
				  +TB_Text._ID+" integer primary key autoincrement,"
				  +TB_Text._BGCLR+" text default 'ffffff',"
				  +TB_Text._CONTENT+" text default 'this is default text',"
				  +TB_Text._FACENAME+" text default 0,"
				  +TB_Text._HEIGHT+" integer,"
				  +TB_Text._ITALIC+" integer,"
				  +TB_Text._PROLEN+" integer,"
				  +TB_Text._SINGLELINE+" integer,"
				  +TB_Text._STATIC+" integer,"
				  +TB_Text._TRANSPARENT+" integer,"
				  +TB_Text._UNDERLINE+" integer,"
				  +TB_Text._WALKSPEED+" integer,"
				  +TB_Text._WEIGHT+" integer,"
				  +TB_Text._WORDCLR+" text,"
				  +TB_Text._BEGINTIME+"  text )");
	}
}
