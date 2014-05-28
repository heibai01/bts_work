package com.led.player.meta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.led.player.meta.MetaData.TB_Image;
import com.led.player.meta.MetaData.TB_Text;
import com.led.player.meta.MetaData.TB_Universalwindow;
import com.led.player.meta.MetaData.TB_Video;


public class MySqliteOpenHelper extends SQLiteOpenHelper{
	private static SQLiteOpenHelper sqLiteOpenHelper = null; 

	public synchronized static SQLiteOpenHelper getSqliteDatabase(Context context,String dbname,CursorFactory factory,int version){
		if (sqLiteOpenHelper==null) {
			sqLiteOpenHelper = new MySqliteOpenHelper(context, dbname, factory, version);
		}
		return sqLiteOpenHelper;
	}
	private MySqliteOpenHelper(Context context, String name,CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	
	private  String sql4universalwin = "create table "+MetaData.TB_Universalwindow.TB_NAME+"("
									+ TB_Universalwindow._ID+" integer primary key autoincrement,"
									+TB_Universalwindow._STARTX+" integer default 0,"
									+TB_Universalwindow._STARTY+" integer default 0,"
									+TB_Universalwindow._WIDTH+" integer not null,"
									+TB_Universalwindow._HEIGHT+" integer not null,"
									+TB_Universalwindow._WINDOWROTATION+" integer default 0,"
									+TB_Universalwindow._BORDERTYPE+" integer default 0, "
									+TB_Universalwindow._BORDERCOLOR+" text default '16777215' )";
	private String sql4videovVo = "create table "+MetaData.TB_Video.TB_NAME+"("
								+TB_Video._ID+" integer primary key autoincrement,"
								+TB_Video._VOICEVALUE+" integer ,"
								+TB_Video._VIDEOPROPORTION+" integer,"
								+TB_Video._FILEPATH+" text not null,"
								+TB_Video._BEGINTIME+" text )";
	private String sql4ImageVo = "create table "+MetaData.TB_Image.TB_NAME+"("
							   +TB_Image._ID+" integer primary key autoincrement,"
							   +TB_Image._BGCLR+" text not null,"
							   +TB_Image._DESCRIPTION+" integer default 0,"
							   +TB_Image._EFFECTS+" integer default 1,"
							   +TB_Image._FILEPATH+" text not null,"
							   +TB_Image._STAYLINE+" integer,"
							   +TB_Image._TRANSPARENT+" integer,"
							   +TB_Image._TXTSPEED+" integer,"
							   +TB_Image._BEGINTIME+"  text)";
	private String sql4TextVo = "create table "+MetaData.TB_Text.TB_NAME+"("
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
							  +TB_Text._BEGINTIME+"  text )";
							   
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sql4universalwin);
		db.execSQL(sql4videovVo);
		db.execSQL(sql4ImageVo);
		db.execSQL(sql4TextVo);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
