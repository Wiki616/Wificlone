package com.kdn.wifimanager;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final TabHost tabHost = getTabHost();
		SQLiteDatabase db = null;
		// 选项卡菜单
		tabHost.addTab(tabHost.newTabSpec("tag1").setIndicator("WI-FI设备控制")
				.setContent(new Intent(this, ControlActivity.class)));

		tabHost.addTab(tabHost.newTabSpec("tag2").setIndicator("AP 列表管理")
				.setContent(new Intent(this, WifiScanActivity.class)));
		
		tabHost.addTab(tabHost.newTabSpec("tag3").setIndicator("文件同步")
				.setContent(new Intent(this, WificloneActivity.class)));
		
		db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
		//if (tabbleIsExist(db,"speed")) db.execSQL("DROP TABLE speed");
		db.execSQL("CREATE TABLE if not exists speed (RSSI VARCHAR,SP INTEGER)");  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_layout, menu);
		return true;
	}
	
	 public boolean tabbleIsExist(SQLiteDatabase db , String tableName){
         boolean result = false;
         if(tableName == null){
                 return false;
         }
         Cursor cursor = null;
         try {
                 String sql = "select count(*) as c from "+"test.db"+" where type ='table' and name ='"+tableName.trim()+"' ";
                 cursor = db.rawQuery(sql, null);
                 if(cursor.moveToNext()){
                         int count = cursor.getInt(0);
                         if(count>0){
                                 result = true;
                         }
                 }
                 
         } catch (Exception e) {
                 // TODO: handle exception
         }               
         return result;
 }
}
