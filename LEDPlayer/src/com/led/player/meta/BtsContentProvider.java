package com.led.player.meta;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class BtsContentProvider extends ContentProvider{
	private SQLiteDatabase db = null;
	
	private static final UriMatcher uriMatcher; 
	//1.定义 识别码 code。
	private static final int UNIVERSAL_WINDOW = 0;
	private static final int UNIVERSAL_WINDOWS = 1;
	private static final int IMAGE =2;
	private static final int IMAGES = 3;
	private static final int VIDEO = 4;
	private static final int VIDEOS = 5;
	private static final int TEXT =6;
	private static final int TEXTS =7;
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH); //参数是没头匹配时默认返回的东西。
		uriMatcher.addURI(MetaData.AUTHORITIES, "universalwindow/#", UNIVERSAL_WINDOW);
		uriMatcher.addURI(MetaData.AUTHORITIES, "universalwindow", UNIVERSAL_WINDOWS);
		
		uriMatcher.addURI(MetaData.AUTHORITIES, "image/#", IMAGE);
		uriMatcher.addURI(MetaData.AUTHORITIES, "image", IMAGES);
		uriMatcher.addURI(MetaData.AUTHORITIES, "video/#", VIDEO);
		uriMatcher.addURI(MetaData.AUTHORITIES, "video", VIDEOS);
		uriMatcher.addURI(MetaData.AUTHORITIES, "text/#", TEXT);
		uriMatcher.addURI(MetaData.AUTHORITIES, "text", TEXTS);
		
		
		
	}
	@Override
	public boolean onCreate() {
		Log.e("BtsContentProvider", "BtsContentProvider中数据库创建被调")		;		
//		openHelper = new MySqliteOpenHelper(this.getContext(), MetaData.DB_NAME, null, MetaData.DB_VERSION);
	    SQLiteOpenHelper sqLiteOpenHelper = MySqliteOpenHelper.getSqliteDatabase(getContext(), MetaData.DB_NAME, null, MetaData.DB_VERSION);
	    db = sqLiteOpenHelper.getReadableDatabase();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,	String[] selectionArgs, String orderBy) {
		Cursor cursor = null;
		switch (uriMatcher.match(uri)) {
		case UNIVERSAL_WINDOW:
			cursor = getQueryCursor(uri,MetaData.TB_Universalwindow.TB_NAME, projection, selection, orderBy);
			break;
		case UNIVERSAL_WINDOWS:
			cursor = db.query(MetaData.TB_Universalwindow.TB_NAME, projection, selection, selectionArgs, null, null, orderBy);
			break;
		case IMAGE:
			cursor = getQueryCursor(uri, MetaData.TB_Image.TB_NAME, projection, selection, orderBy);
			break;
		case IMAGES:
			cursor = db.query(MetaData.TB_Image.TB_NAME, projection, selection, selectionArgs, null, null, orderBy);
			break;
		case VIDEOS:
			cursor = db.query(MetaData.TB_Video.TB_NAME, projection, selection, selectionArgs, null, null, orderBy);
			break;
		case TEXTS:
			cursor = db.query(MetaData.TB_Text.TB_NAME, projection, selection, selectionArgs, null, null, orderBy);
			break;
		default:
			throw new IllegalArgumentException("未知的uri："+uri);
		}
		return cursor;
	}

	/**
	 * 得到查询的游标
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param sortOrder
	 * @return
	 */
	private Cursor getQueryCursor(Uri uri,String tb_name, String[] projection,	String selection, String sortOrder) {
		Cursor cursor;
		long id = ContentUris.parseId(uri);
		String where = "_id="+id;
		if (selection!=null&&"".equals(selection)==false) {
			where+=" and "+selection;
		}
		cursor = db.query(MetaData.TB_Universalwindow.TB_NAME, projection,  where,null, null, null, sortOrder);
		return cursor;
	}


	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int code = uriMatcher.match(uri);
		long id = 0; 
		Uri newUri = null; 
		switch (code) {
		case UNIVERSAL_WINDOWS:
			id = db.insert(MetaData.TB_Universalwindow.TB_NAME, null, values);
			break;
		case VIDEOS:
			id = db.insert(MetaData.TB_Video.TB_NAME, null, values);
			break;
		case IMAGES:
			id = db.insert(MetaData.TB_Image.TB_NAME, null, values);
			break;
		case TEXTS:
			id = db.insert(MetaData.TB_Text.TB_NAME, null, values);
			break;
		default:
			throw new IllegalArgumentException("URI地址不正确！");
		}
		if (id>0) {
			newUri = ContentUris.withAppendedId(uri, id);
			this.getContext().getContentResolver().notifyChange(newUri, null);
		}
		return newUri;
	}

	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		int rowsAffected = 0;
		int code = uriMatcher.match(uri);
		switch (code) {
		case UNIVERSAL_WINDOWS:
			rowsAffected = db.delete(MetaData.TB_Universalwindow.TB_NAME, whereClause, whereArgs);
			break;
		case VIDEOS:
			rowsAffected = db.delete(MetaData.TB_Video.TB_NAME	, whereClause, whereArgs);
			break;
		case IMAGES:
			rowsAffected = db.delete(MetaData.TB_Image.TB_NAME	, whereClause, whereArgs);
			break;
		case TEXTS:
			rowsAffected = db.delete(MetaData.TB_Text.TB_NAME, whereClause, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("未知的uri"+uri);
		}
		if (rowsAffected>0) {
			//又要通知 那些观察者。
			this.getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsAffected;
	}

	@Override
	public int update(Uri uri, ContentValues values, String whereClause,String[] whereArgs) {
		//更新应该和 插入一样用的uri 是多记录的uri吧。
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case UNIVERSAL_WINDOWS:
			count = db.update(MetaData.TB_Universalwindow.TB_NAME, values, whereClause, whereArgs);
			break;
		case VIDEOS:
			count = db.update(MetaData.TB_Video.TB_NAME, values, whereClause, whereArgs);
			break;
		case IMAGES:
			count = db.update(MetaData.TB_Image.TB_NAME, values, whereClause, whereArgs);
			break;
		case TEXTS:
			count = db.update(MetaData.TB_Text.TB_NAME, values, whereClause, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("未知的uri："+uri);
		}
		if (count>0) {
			this.getContext().getContentResolver().notifyChange(uri, null);//第二个参数表示不管外面有多少个 内容观察者，我注册的这个观察者必须要得到数据的变化，给个null就是不是一定要得到变化。
		}
		return count;
	}
	
	@Override
	public String getType(Uri uri) {
		String dataMIME = null;
		int code = uriMatcher.match(uri);
		if (code==UNIVERSAL_WINDOWS) {
			dataMIME = MetaData.TB_Universalwindow.UNIVERSALWIN_CONTENT_DIR_TYPE;
		}else if (code==UNIVERSAL_WINDOW) {
			dataMIME = MetaData.TB_Universalwindow.UNIVERSALWIN_CONTENT_ITEM_TYPE;
		}else if (code==VIDEOS) {
			dataMIME = MetaData.TB_Video.TB_VIDEO_CONTENT_DIR_TYPE;
		}else if (code==VIDEO) {
			dataMIME = MetaData.TB_Video.TB_VIDEO_CONTENT_ITEM_TYPE;
		}else if (code==IMAGES) {
			dataMIME = MetaData.TB_Image.TB_IMAGE_CONTENT_DIR_TYPE;
		}else if (code==IMAGE) {
			dataMIME = MetaData.TB_Image.TB_IMAGE_CONTENT_ITEM_TYPE;
		}else if (code==TEXTS) {
			dataMIME = MetaData.TB_Text.TB_TEXT_CONTENT_DIR_TYPE;
		}else if (code==TEXT) {
			dataMIME = MetaData.TB_Text.TB_TEXT_CONTENT_ITEM_TYPE;
		}else {
			throw new IllegalArgumentException("未知的URI:"+uri);
		}
		return dataMIME;
	}
}
