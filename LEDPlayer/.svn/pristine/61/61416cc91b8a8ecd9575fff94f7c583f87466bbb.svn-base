package com.led.player.apn;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class ApnSetting {

	private Context mContext;
	private int add_apn_id = 0;
	private int apn_id = 0;
	public static final Uri APN_URI = Uri.parse("content://telephony/carriers");
	public static final Uri CURRENT_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
	
	public ApnSetting(Context context){
		mContext = context;
	}
	
	public void SetApn(){
		apn_id = checkAPN();
		  if(apn_id==-1){
			  InsetAPN();
			  if(add_apn_id != 0){
				  setDefaultApn(add_apn_id);
			  }
			  
		  }else{
			  setDefaultApn(apn_id);
		  }
	}
	
	private String getSimOperator()
	 {
	  TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
	  String SimOperator = tm.getSimOperator();
	  if(SimOperator!=null){
		  return SimOperator;
	  }else{
		  return null;
	  }
	  
	 }
	
	private String getMCC()
	 {
	  TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
	  String numeric = tm.getSimOperator();
	  if(numeric!=null&&numeric.length() >= 3){
		  String mcc = numeric.substring(0, 3);
		  Log.i("MCC  is", mcc);
		  return mcc;
	  }else{
		  Log.i("MCC  is", "null");
		  return null;
	  }
	  
	 }

	private String getMNC()
	 {
	  TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
	  String numeric = tm.getSimOperator();
	  if(numeric!=null&&numeric.length() >= 5){
		  String mnc = numeric.substring(3, numeric.length());
		  Log.i("MNC  is", mnc);
		  return mnc;
	  }else{
		  Log.i("MNC  ","is null");
		  return null;
	  }
	 }
	  
	public int checkAPN()
	 {
	  Log.i("ApnSetting", "&&&&&&&&& checkAPN");
	  ApnNode checkApn = CreateApnNode();
	  return isApnExisted(checkApn);
	 }

	public int addNewApn(ApnNode apnNode)
	 {
		Log.i("ApnSetting", "&&&&&&&&& addNewApn");
	  int apnId = -1;
	  ContentResolver resolver = mContext.getContentResolver();
	  ContentValues values = new ContentValues();
	  values.put("name", apnNode.getName());
	  values.put("apn", apnNode.getApn());
	  values.put("proxy", apnNode.getProxy());
	  values.put("port", apnNode.getPort());
	  values.put("user", apnNode.getUser());
	  values.put("password", apnNode.getPassword());
	  values.put("mcc", apnNode.getMcc());
	  values.put("mnc", apnNode.getMnc());
	  values.put("numeric", apnNode.getNumeric());
	  // Note: this values need to be update, and for now, it only for XT800.

	  Cursor c = null;
	  try
	  {
	   Uri newRow = resolver.insert(APN_URI, values);
	   if (newRow != null)
	   {
	    c = resolver.query(newRow, null, null, null, null);
	    
	    int idindex = c.getColumnIndex("_id");
	    c.moveToFirst();
	    apnId = c.getShort(idindex);
	    Log.i("addNewApn", "New ID: " + apnId + ": Inserting new APN succeeded!");
	   }
	  }
	  catch (SQLException e)
	  {
	   
	  }

	  if (c != null)
	   c.close();

	  return apnId;
}

	private void InsetAPN()
	 {
		Log.i("ApnSetting", "&&&&&&&&& InsetAPN");
	 ApnNode addApnNode = CreateApnNode();
	  add_apn_id = addNewApn(addApnNode);
}

	public ApnNode CreateApnNode(){
	 Log.i("ApnSetting", "&&&&&&&&& CreateApnNode");
	 ApnNode NewApnNode = new ApnNode();
	 NewApnNode.setName("中国联通3GNET");
	 NewApnNode.setApn("3gnet");
	//  NewApnNode.setProxy("10.0.0.200");
	//  NewApnNode.setPort("80");
//	 NewApnNode.setUser("ctnet@mycdma.com");
//	 NewApnNode.setPassword("vnet.mobi");
	 NewApnNode.setMcc(getMCC());
	 NewApnNode.setMnc(getMNC());
	 NewApnNode.setType("default");
//	 NewApnNode.setMcc("460");
//	 NewApnNode.setMnc("01");
	  //NewApnNode.setNumeric("PAP��CHAP");
	 NewApnNode.setNumeric(getSimOperator());
	  
	  return NewApnNode;
}

	public boolean setDefaultApn(int apnId)
	 {
		Log.i("ApnSetting", "&&&&&&&&& setDefaultApn");
	  boolean res = false;
	  ContentResolver resolver = mContext.getContentResolver();
	  ContentValues values = new ContentValues();
	  values.put("apn_id", apnId);
	 

	  try
	  {
	   resolver.update(CURRENT_APN_URI, values, null, null);
	   Cursor c = resolver.query(CURRENT_APN_URI, new String[] { "name", "apn" }, "_id=" + apnId, null, null);
	   if (c != null)
	   {
	    res = true;
	    c.close();
	   }
	  }
	  catch (SQLException e)
	  {
	  
	  }
	  return res;

	 }

	public void SetDefaultAPN(int id)
	 {
	  setDefaultApn(id);
	  ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
	  cm.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "*");
	  Cursor cursor = mContext.getContentResolver().query(CURRENT_APN_URI, null, null, null, null);
	  int rows = cursor.getCount();
	  cursor.moveToFirst();
	  String apn;
	  for (int i = 0; i < rows; i++)
	  {
	   apn = cursor.getString(1);
	   Log.e("----------------", apn);
	   cursor.moveToNext();
	  }

	 }

	public int isApnExisted(ApnNode apnNode)
	  {
		Log.i("ApnSetting", "&&&&&&&&& isApnExisted");
	  int apnId = -1;
	  Cursor mCursor = mContext.getContentResolver().query(APN_URI, null, null, null, null);
	  while (mCursor != null && mCursor.moveToNext())
	  {
	   apnId = mCursor.getShort(mCursor.getColumnIndex("_id"));
	   String name = mCursor.getString(mCursor.getColumnIndex("name"));
	   String apn = mCursor.getString(mCursor.getColumnIndex("apn"));
	   String type = mCursor.getString(mCursor.getColumnIndex("type"));
	   String proxy = mCursor.getString(mCursor.getColumnIndex("proxy"));
	   String port = mCursor.getString(mCursor.getColumnIndex("port"));
	   String current = mCursor.getString(mCursor.getColumnIndex("current"));
	   String mcc = mCursor.getString(mCursor.getColumnIndex("mcc"));
	   String mnc = mCursor.getString(mCursor.getColumnIndex("mnc"));
	   String numeric = mCursor.getString(mCursor.getColumnIndex("numeric"));
	   //Log.e("isApnExisted", "info:" + apnId + "_" + name + "_" + apn + "_" + type + "_" + current + "_" + proxy);
//	   if (name.equals(apnNode.getName())&&apn.equals(apnNode.getApn()) && mcc.equals(apnNode.getMcc()) 
//			   && mnc.equals(apnNode.getMnc()) && numeric.equals(apnNode.getNumeric())&&type.equals(apnNode.getType()))
	   if (name.equals(apnNode.getName())&&apn.equals(apnNode.getApn())&& apnNode.getMcc()!=null&&apnNode.getMnc()!=null)
	   {
	    return apnId;
	   }
	   else
	   {
	    apnId = -1;
	   }

	  }
	  return apnId;
	 }
	}
