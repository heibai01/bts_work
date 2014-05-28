package com.led.player.dao;



import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;

import com.led.player.meta.MetaData;
import com.led.player.meta.MetaData.TB_Image;
import com.led.player.meta.MySqliteOpenHelper;
import com.led.player.po.GPSLoopImagePo;

public class GPSLoopImageDao {
	private SQLiteDatabase db;
	private Context mContext;

	public GPSLoopImageDao(Context mContext) {
		this.mContext = mContext;
		this.db = MySqliteOpenHelper.getSqliteDatabase(mContext, MetaData.DB_NAME, null, MetaData.DB_VERSION).getReadableDatabase();;
	}
	
	public boolean addImage(GPSLoopImagePo imageVo){
		boolean isAdd = false;
		ContentValues values = new ContentValues();
		values.put(MetaData.TB_Image._BEGINTIME, imageVo._begintime);
		values.put(MetaData.TB_Image._BGCLR, imageVo._bgclr);
		values.put(MetaData.TB_Image._DESCRIPTION, imageVo._description);
		values.put(MetaData.TB_Image._EFFECTS, imageVo._effects);
		values.put(MetaData.TB_Image._FILEPATH, imageVo._filepath);
		values.put(MetaData.TB_Image._STAYLINE, imageVo._stayline);
		values.put(MetaData.TB_Image._TRANSPARENT, imageVo._transparent);
		values.put(MetaData.TB_Image._TXTSPEED, imageVo._txtspeed);
		isAdd = mContext.getContentResolver().insert(Uri.parse(MetaData.TB_Image.URI_TB_IMAGE), values)!=null?true:false;
		return isAdd;
	}
	
	
	public boolean delAllImage(){
		boolean isDel = false;
		isDel = mContext.getContentResolver().delete(Uri.parse(MetaData.TB_Image.URI_TB_IMAGE), null, null)>0?true:false;
		return isDel;
	}
	
	
	public void delAllImageByOrg(){
		db.execSQL("delete from "+MetaData.TB_Image.TB_NAME);
	}
	
	public boolean  addImageByOrg(GPSLoopImagePo imageVo){
		boolean isAdd = false;
		ContentValues values = new ContentValues();
		values.put(MetaData.TB_Image._BEGINTIME, imageVo._begintime);
		values.put(MetaData.TB_Image._BGCLR, imageVo._bgclr);
		values.put(MetaData.TB_Image._DESCRIPTION, imageVo._description);
		values.put(MetaData.TB_Image._EFFECTS, imageVo._effects);
		values.put(MetaData.TB_Image._FILEPATH, imageVo._filepath);
		values.put(MetaData.TB_Image._STAYLINE, imageVo._stayline);
		values.put(MetaData.TB_Image._TRANSPARENT, imageVo._transparent);
		values.put(MetaData.TB_Image._TXTSPEED, imageVo._txtspeed);
		isAdd = db.insert(MetaData.TB_Image.TB_NAME, null, values)>-1;
		return isAdd;
	}
	
	public void addImageBySql(GPSLoopImagePo imageVo){
		String sql = "insert into "+MetaData.TB_Image.TB_NAME+"("+MetaData.TB_Image._BGCLR+","
				+MetaData.TB_Image._DESCRIPTION+","+MetaData.TB_Image._EFFECTS+","
				+MetaData.TB_Image._FILEPATH+","+MetaData.TB_Image._STAYLINE+","
				+MetaData.TB_Image._TRANSPARENT+","+MetaData.TB_Image._TXTSPEED+","
				+MetaData.TB_Image._BEGINTIME+")"
				+" values('"+imageVo._bgclr+"',"+imageVo._description+","+imageVo._effects+",'"+imageVo._filepath+"',"
				+imageVo._stayline+","+imageVo._transparent+","+imageVo._txtspeed+",'"+imageVo._begintime+"')";
		db.execSQL(sql);
	}
	
	public ArrayList<com.led.player.window.ImageVo> findImageVoByCondition(String where,String[] whereArgs,String groupBy,String having,String orderBy,String limit){
		ArrayList<com.led.player.window.ImageVo> imageVos = new ArrayList<com.led.player.window.ImageVo>();
		com.led.player.window.ImageVo imageVo = null;
		Cursor cursor = db.query(true, MetaData.TB_Image.TB_NAME, null, where, whereArgs, groupBy, having, orderBy, limit);
		while (cursor!=null&&cursor.moveToNext()) {
			imageVo = new com.led.player.window.ImageVo();
			imageVo.BgClr = Color.parseColor("#"+cursor.getString(1));
			imageVo.Description = cursor.getInt(2);
			imageVo.Effects = cursor.getInt(3);
			imageVo.FilePath = cursor.getString(4);
			imageVo.StayLine = cursor.getInt(5);
			imageVo.Transparent = cursor.getInt(6)==0?false:true;
			imageVo.setTxtSpeed(cursor.getInt(7));
			imageVos.add(imageVo);
		}
		if (cursor!=null) {
			cursor.close();
		}
		return imageVos;
	}
	
	public void dropImageTable(){
		SQLiteDatabase db = MySqliteOpenHelper.getSqliteDatabase(mContext, MetaData.DB_NAME, null, MetaData.DB_VERSION).getReadableDatabase();
		db.execSQL("drop table if exists "+MetaData.TB_Image.TB_NAME);
		db.execSQL( "create table "+MetaData.TB_Image.TB_NAME+"("
				   +TB_Image._ID+" integer primary key autoincrement,"
				   +TB_Image._BGCLR+" text not null,"
				   +TB_Image._DESCRIPTION+" integer default 0,"
				   +TB_Image._EFFECTS+" integer default 1,"
				   +TB_Image._FILEPATH+" text not null,"
				   +TB_Image._STAYLINE+" integer,"
				   +TB_Image._TRANSPARENT+" integer,"
				   +TB_Image._TXTSPEED+" integer,"
				   +TB_Image._BEGINTIME+"  text )"
				   );
	}
}
