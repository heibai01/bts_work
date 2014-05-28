package com.led.player.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.led.player.meta.MetaData;
import com.led.player.meta.MetaData.TB_Video;
import com.led.player.meta.MySqliteOpenHelper;
import com.led.player.po.GPSLoopVideoPo;

public class GPSLoopVideoDao {
	private Context mContext;
	private SQLiteDatabase db;
	
	public GPSLoopVideoDao(Context mContext) {
		this.mContext = mContext;
		this.db = MySqliteOpenHelper.getSqliteDatabase(mContext, MetaData.DB_NAME, null, MetaData.DB_VERSION).getReadableDatabase();;
	}

	 // ����һ����Ƶ�ļ�
	public boolean addVideo(GPSLoopVideoPo videoVo){
		boolean isAdded = false;
		ContentValues values = new ContentValues();
		values.put(MetaData.TB_Video._BEGINTIME, videoVo.get_begintime());
		values.put(MetaData.TB_Video._FILEPATH, videoVo.get_filepath());
		values.put(MetaData.TB_Video._VIDEOPROPORTION, videoVo.get_videoproportion());
		values.put(MetaData.TB_Video._VOICEVALUE, videoVo.get_voicevalue());
		isAdded = mContext.getContentResolver().insert(Uri.parse(MetaData.TB_Video.URI_TB_VIDEO), values)==null?false:true;
		
		return isAdded;
	}
	
	public boolean addVideoByOrg(GPSLoopVideoPo videoVo){
		boolean isAdd = false;
		ContentValues values = new ContentValues();
		values.put(MetaData.TB_Video._BEGINTIME, videoVo.get_begintime());
		values.put(MetaData.TB_Video._FILEPATH, videoVo.get_filepath());
		values.put(MetaData.TB_Video._VIDEOPROPORTION, videoVo.get_videoproportion());
		values.put(MetaData.TB_Video._VOICEVALUE, videoVo.get_voicevalue());
		isAdd = db.insert(MetaData.TB_Video.TB_NAME, null, values)>-1?true:false;
		return isAdd; 
	}
	
	public void addVideoBySql(GPSLoopVideoPo videoVo){
		String sql = "insert into "+MetaData.TB_Video.TB_NAME+"("+MetaData.TB_Video._VOICEVALUE+","
				+MetaData.TB_Video._VIDEOPROPORTION+","+MetaData.TB_Video._FILEPATH+","+MetaData.TB_Video._BEGINTIME+")"
				+" values("+videoVo._voicevalue+","+videoVo._videoproportion+",'"+videoVo._filepath+"','"
				+videoVo._begintime+"')";
				
		db.execSQL(sql);
	}
	

	 //* ɾ�����е���Ƶ��Դ
	 
	public boolean delAllVideo(){
		boolean isDel = false;
		isDel = mContext.getContentResolver().delete(Uri.parse(MetaData.TB_Video.URI_TB_VIDEO), null, null)>0?true:false;
		return isDel;
	}
	
	public void delAllVideoByOrg(){
		db.execSQL("delete from "+MetaData.TB_Video.TB_NAME);
	}
	
	public ArrayList<com.led.player.window.VideoVo> findVideoVosByCondition(String selection,String[] selectionArgs,String groupBy,String having,String orderBy,String limit){
		ArrayList<com.led.player.window.VideoVo> videoVos = new ArrayList<com.led.player.window.VideoVo>();
		com.led.player.window.VideoVo videoVo = null;
		Cursor cursor = db.query(true, MetaData.TB_Video.TB_NAME, null, selection, selectionArgs, groupBy, having, orderBy, limit);
		while (cursor!=null&&cursor.moveToNext()) {
			videoVo = new com.led.player.window.VideoVo();
			videoVo.VideoProportion = cursor.getInt(2);
			videoVo.VoiceValue = cursor.getInt(1);
			videoVo.FilePath = cursor.getString(3);
			videoVos.add(videoVo);
		}
		if (cursor!=null) {
			cursor.close();
		}
		return videoVos;
	}
	
	public void dropVideoTable(){
		SQLiteDatabase db = MySqliteOpenHelper.getSqliteDatabase(mContext, MetaData.DB_NAME, null, MetaData.DB_VERSION).getReadableDatabase();
		db.execSQL("drop table if exists "+MetaData.TB_Video.TB_NAME);
		db.execSQL("create table "+MetaData.TB_Video.TB_NAME+"("
				+TB_Video._ID+" integer primary key autoincrement,"
				+TB_Video._VOICEVALUE+" integer ,"
				+TB_Video._VIDEOPROPORTION+" integer,"
				+TB_Video._FILEPATH+" text not null,"
				+TB_Video._BEGINTIME+" text )"		);
	}
}
