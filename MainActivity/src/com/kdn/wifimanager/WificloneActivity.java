package com.kdn.wifimanager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.R.integer;
import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;

public class WificloneActivity extends Activity implements OnClickListener{
	private ClientSocket cs = null;
	private static final String FILE_PATH = "/sdcard/Wificlone/";
	private String path;
	private String ip = "";
	private int port = 8877;
	private int port2 = 8888;
	private int num = 0;
	EditText editText;
	SQLiteDatabase db = null;
	private String sendMessage = "Windows";
	private Button filechoose , filestream;
	private boolean flag = true;
	static ArrayList filelist = new ArrayList(); 
	String SSID;
	WifiManager wifimanager;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wificlone_layout);
		filechoose = (Button)findViewById(R.id.filechoose);
		filestream = (Button)findViewById(R.id.filestream);
		editText = (EditText)findViewById(R.id.editText1);
		filechoose.setOnClickListener(this);
		filestream.setOnClickListener(this);
		
		wifimanager = (WifiManager) getSystemService(WIFI_SERVICE);
		if (wifimanager.isWifiEnabled() == false)
			wifimanager.setWifiEnabled(true);
		WifiInfo wifiInfo = wifimanager.getConnectionInfo();
		SSID = wifiInfo.getSSID();
		Log.d("main",SSID);
		//db.execSQL("CREATE TABLE speed (RSSI VARCHAR,SP INTEGER)");  
	}
	 void Socketstart() {
	        Socket s = null;
	        try {
	            ServerSocket ss = new ServerSocket(port2);
	            for (int i = 0;i < filelist.size();i ++) {
	                // 选择进行传输的文件
	            	String filePath = filelist.get(i).toString();
	                File fi = new File(filePath);

	                System.out.println("文件长度:" + (int) fi.length());

	                // public Socket accept() throws
	                // IOException侦听并接受到此套接字的连接。此方法在进行连接之前一直阻塞。

	                s = ss.accept();
	                System.out.println("建立socket链接");
	                DataInputStream dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
	                dis.readByte();
	                System.out.println("xxx");
	                DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
	                DataOutputStream ps = new DataOutputStream(s.getOutputStream());
	                ps.writeUTF(fi.getName());
	                ps.flush();
	                ps.writeLong((long) fi.length());
	                ps.flush();

	                int bufferSize = 8192;
	                byte[] buf = new byte[bufferSize];
	                while (true) {
	                    int read = 0;
	                    if (fis != null) {
	                        read = fis.read(buf);
	                    }

	                    if (read == -1) {
	                        break;
	                    }
	                    ps.write(buf, 0, read);
	                }
	                ps.flush();
	                // 注意关闭socket链接哦，不然客户端会等待server的数据过来，
	                // 直到socket超时，导致数据不完整。                
	                fis.close();
	                s.close();                
	                System.out.println("文件传输完成");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
	
	 private void start() {
		if (createConnection()) {
			sendMessage();
			getMessage();
		}
		else flag = false;
	}
	private void getMessage() {
		Log.d("main", "In getMessage");
		
		if (cs == null)
			return;
		DataInputStream inputStream = null;
		inputStream = cs.getMessageStream();

		Log.d("main", "In getMessage1");
		try {
			String savePath = FILE_PATH;
			int bufferSize = 8192;
			byte[] buf = new byte[bufferSize];
			int passedlen = 0;
			long len = 0;
			Log.d("main","line 222");
			
			savePath += inputStream.readUTF();
			
			Log.d("AndroidClient","@@@savePath"+savePath);
			DataOutputStream fileOut = new DataOutputStream(
					new BufferedOutputStream(new BufferedOutputStream(
							new FileOutputStream(savePath))));
			Log.d("main","very ok!");
			len = inputStream.readLong();
			Log.d("main","nice !");
			Log.d("AndoridClient","文件的长度为:"+len);
			Log.d("AndroidClient","开始接收文件");
		
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar c1 = Calendar.getInstance();
			while(true) {
				int read = 0;
				if (inputStream != null) {
					read = inputStream.read(buf);
				}
				passedlen += read;
				if (read == -1) {
					break;
				}
				Log.d("AndroidClient","文件接收了"+(passedlen*100/len)+"%/n");
				fileOut.write(buf,0,read);
			}
			Calendar c2 = Calendar.getInstance();
			
			long seconds = c2.get(Calendar.SECOND) - c1.get(Calendar.SECOND) + c2.get(Calendar.MINUTE)*60 - c1.get(Calendar.MINUTE)*60 + c2.get(Calendar.HOUR)*3600 - c1.get(Calendar.HOUR)*3600;
			
			long speed;
			db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
			ContentValues cv = new ContentValues();  
			if (seconds == 0) speed = 99999;
			else speed = len / (1000 * seconds);
			cv.put("RSSI", "'"+SSID+"'");  
			cv.put("SP", speed);
			Log.d("main","耗时：" + Long.toString(seconds) + "s");
			Log.d("main", SSID + ": " + speed + " kb/s");
			//db.execSQL("INSERT INTO speed VALUES (?, ?, ?)",SSID,
			if (speed != 99999) db.insert("speed",null,cv);  
			Log.d("AndroidClient","@@@文件接收完成"+savePath);
			db.close();
		} catch (IOException e) {
			
			Log.d("main", "In error");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void sendMessage() {
		if (cs == null)
			return;
		cs.sendMessage(sendMessage);
	}
	private boolean createConnection() {
		cs = new ClientSocket(ip, port);
		cs.createConnection();
		Log.d("Main", "连接服务器成功=====:");
		return true;
	}
	
	public void onClick(View v) {
		if (v.getId() == R.id.filechoose) {
			ip = editText.getText().toString();
			start();
			int n = 4;
			BufferedReader filebr = null;
			try {
				filebr = new BufferedReader(new FileReader("/sdcard/Wificlone/0.txt"));
			} catch (FileNotFoundException e1) {
			// TODO 自动生成的 catch 块
				Log.d("main","error");
				e1.printStackTrace();
			}
			try {
				String str=filebr.readLine();
				n = 4;
			} catch (IOException e) {
				e.printStackTrace();
			}
			//for (int i = 1;i < n;i ++)
			while (true) start();
		}
		if (v.getId() == R.id.filestream) {
			Upload();
		}
	}
	private void Upload() {
		// TODO 自动生成的方法存根
		//byte[] buff=new byte[]{};
		//try {
			//FileOutputStream out=new FileOutputStream("/sdcard/Wificlone/0.txt");
			refreshFileList("/sdcard/Wificlone/");
			//String txt = String.valueOf(filelist.size());
			//for (int i = 0;i < filelist.size();i ++)
			//	txt += "\r\n" + filelist.get(i).toString();
			//buff = txt.getBytes();  
			//out.write(buff,0,buff.length);
			Socketstart();
		//} catch (FileNotFoundException e) {
			// TODO
			//e.printStackTrace();
		//} catch (IOException e) {
			// TODO 
			//e.printStackTrace();
		//}
	}
	private static void refreshFileList(String strPath) {
		// TODO 
		File dir = new File(strPath); 
        File[] files = dir.listFiles(); 
        
        if (files == null) 
            return; 
        for (int i = 0; i < files.length; i++) { 
            if (files[i].isDirectory()) { 
                refreshFileList(files[i].getAbsolutePath()); 
            } else { 
                String strFileName = files[i].getAbsolutePath().toLowerCase();
                //System.out.println("---"+strFileName);
                filelist.add(files[i].getAbsolutePath());
                System.out.println(files[i].getAbsolutePath());
            } 
        } 
	}
}


