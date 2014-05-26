package com.led.player.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.led.player.meta.MetaData;
import com.led.player.meta.MySqliteOpenHelper;
import com.led.player.po.UniversalWindowPo;
import com.led.player.window.UniversalWindow;

public class GPSLoopUniversalWindowDao {
	private Context mContext;
	private SQLiteDatabase db;
	
	public GPSLoopUniversalWindowDao(Context mContext) {
		this.mContext = mContext;
		this.db = MySqliteOpenHelper.getSqliteDatabase(mContext, MetaData.DB_NAME, null, MetaData.DB_VERSION).getReadableDatabase();;
	}

	//*  
	public boolean addUniversalWindow(UniversalWindowPo universalWindowVo){
		ContentValues values = new ContentValues();
		values.put(MetaData.TB_Universalwindow._BORDERCOLOR, universalWindowVo.get_bordercolor());
		values.put(MetaData.TB_Universalwindow._BORDERTYPE, universalWindowVo.get_bordertype());
		values.put(MetaData.TB_Universalwindow._HEIGHT, universalWindowVo.get_height());
		values.put(MetaData.TB_Universalwindow._STARTX, universalWindowVo.get_startX());
		values.put(MetaData.TB_Universalwindow._STARTY, universalWindowVo.get_startY());
		values.put(MetaData.TB_Universalwindow._WIDTH, universalWindowVo.get_width());
		values.put(MetaData.TB_Universalwindow._WINDOWROTATION, universalWindowVo.get_windowrotation());
		Uri uri = mContext.getContentResolver().insert(Uri.parse(MetaData.TB_Universalwindow.URI_UNIVERSAL), values);
		
//		MyLog.e("betters", "添加成功~！");
		if (uri==null) {
			return false;
		}else {
			return true;
		}
	}
	
	public boolean addUniversalWindowByOrg(UniversalWindowPo universalWindowVo){
		ContentValues values = new ContentValues();
		values.put(MetaData.TB_Universalwindow._BORDERCOLOR, universalWindowVo.get_bordercolor());
		values.put(MetaData.TB_Universalwindow._BORDERTYPE, universalWindowVo.get_bordertype());
		values.put(MetaData.TB_Universalwindow._HEIGHT, universalWindowVo.get_height());
		values.put(MetaData.TB_Universalwindow._STARTX, universalWindowVo.get_startX());
		values.put(MetaData.TB_Universalwindow._STARTY, universalWindowVo.get_startY());
		values.put(MetaData.TB_Universalwindow._WIDTH, universalWindowVo.get_width());
		values.put(MetaData.TB_Universalwindow._WINDOWROTATION, universalWindowVo.get_windowrotation());
		return db.insert(MetaData.TB_Universalwindow.TB_NAME, null, values)>-1;
	}
	
	public void addUniversalWindowBySql(UniversalWindowPo windowVo){
		String sql = "insert into "+MetaData.TB_Universalwindow.TB_NAME+"("+MetaData.TB_Universalwindow._STARTX+","
				+MetaData.TB_Universalwindow._STARTY+","+MetaData.TB_Universalwindow._WIDTH+","
				+MetaData.TB_Universalwindow._HEIGHT+","+MetaData.TB_Universalwindow._WINDOWROTATION+","
				+MetaData.TB_Universalwindow._BORDERTYPE+","+MetaData.TB_Universalwindow._BORDERCOLOR+")"
				+" values("+windowVo._startX
				+","+windowVo._startY+","+windowVo._width+","+windowVo._height+","+windowVo._windowrotation
				+","+windowVo._bordertype+",'"+windowVo._bordercolor+"')";
		db.execSQL(sql);
	}
	
	public ArrayList<UniversalWindow> findUniversalWindowsByCondition(String selection,String[] selectionArgs,String groupBy,String having,String orderBy,String limit){
		ArrayList<UniversalWindow> universalWindows = new ArrayList<UniversalWindow>();
		UniversalWindow universalWindow = null;
		Cursor cursor = db.query(true, MetaData.TB_Universalwindow.TB_NAME, null, selection, selectionArgs, groupBy, having, orderBy, limit);
		while (cursor!=null&&cursor.moveToNext()) {
			universalWindow = new UniversalWindow();
			universalWindow.startX = cursor.getInt(cursor.getColumnIndex(MetaData.TB_Universalwindow._STARTX));
			universalWindow.startY = cursor.getInt(cursor.getColumnIndex(MetaData.TB_Universalwindow._STARTY));
			universalWindow.heiht = cursor.getInt(cursor.getColumnIndex(MetaData.TB_Universalwindow._HEIGHT));
			universalWindow.width = cursor.getInt(cursor.getColumnIndex(MetaData.TB_Universalwindow._WIDTH));
	//没用到		universalwindow.BorderColor = Color.parseColor(cursor.getString(cursor.getColumnIndex(MetaData.TB_Universalwindow._BORDERCOLOR)));
			universalWindow.BorderType = cursor.getInt(cursor.getColumnIndex(MetaData.TB_Universalwindow._BORDERTYPE));
			universalWindows.add(universalWindow);
		}
		if (cursor!=null) {
			cursor.close();
			cursor = null;
		}
		return universalWindows;
	}
	
	
	 //
	public boolean delAllWindows(){
		boolean isDel = false;
		isDel = mContext.getContentResolver().delete(Uri.parse(MetaData.TB_Universalwindow.URI_UNIVERSAL), null, null)>0?true:false;
		return isDel;
	}
	
	public void delAllWinByOrg(){
		db.execSQL("delete from "+MetaData.TB_Universalwindow.TB_NAME);
	}
}
