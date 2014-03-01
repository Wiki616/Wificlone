package com.kdn.wifimanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 使用的Wi-Fi信息显示在列表视图活动扫描
 */
public class WifiScanActivity extends Activity implements OnClickListener {

	private static final String TAG = "WIFIScanner";

	// WifiManager variable
	WifiManager wifimanager;

	// UI variable
	//TextView textStatus;
	Button btnScanStart;
	Button btnScanStop;
	TextView apInfoTxt;

	private int scanCount = 0;
	String text = "";
	String result = "";
	

	private List<ScanResult> mScanResult; // ScanResult List

	// 列表视图声明
	private ListView mListview;

	// 数据连接 Adapter
	WiFiScanDataAdapter mWifiScanDataAdapter;

	// 定义数据结构来保存数据
	ArrayList<Items> mItemList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifiscan_layout);

		// Setup UI
		//textStatus = (TextView) findViewById(R.id.textStatus);
		btnScanStart = (Button) findViewById(R.id.btnScanStart);
		btnScanStop = (Button) findViewById(R.id.btnScanStop);
		apInfoTxt = (TextView)findViewById(R.id.apInfoTxtView);
		
		// Setup OnClickListener
		btnScanStart.setOnClickListener(this);
		btnScanStop.setOnClickListener(this);

		// Setup WIFI Manager
		wifimanager = (WifiManager) getSystemService(WIFI_SERVICE);
		Log.d(TAG, "Setup WIfiManager getSystemService");

		// if WIFIEnabled
		if (wifimanager.isWifiEnabled() == false)
			wifimanager.setWifiEnabled(true);
		
		// 提取当前AP信息
		WifiInfo wifiInfo = wifimanager.getConnectionInfo();

		  
		String ip=intToIp(wifiInfo.getIpAddress());  
		apInfoTxt.setText("当前连接 AP : " + wifiInfo.getSSID() + "\r\nip地址：" + ip);

		// 视图列表
		mListview = (ListView) findViewById(R.id.listView);

		mItemList = new ArrayList<Items>();

		mWifiScanDataAdapter = new WiFiScanDataAdapter(this, mItemList);

		mListview.setAdapter(mWifiScanDataAdapter);

		mListview.setOnItemClickListener(mItemClickListener); 
	}

	private String intToIp(int i) {
		// TODO 自动生成的方法存根
		return (i & 0xFF)+"."+ ((i >> 8 ) & 0xFF) + "." + ((i >> 16 ) & 0xFF) +"."+((i >> 24 ) & 0xFF);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				Log.d(TAG,
						"onReceive - WifiManager.SCAN_RESULTS_AVAILABLE_ACTION");
				getWIFIScanResult(); // get WIFISCanResult
				wifimanager.startScan(); // for refresh
			} else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				Log.d(TAG,
						"onReceive - WifiManager.NETWORK_STATE_CHANGED_ACTION");
				sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
			}
		}
	};

	public void getWIFIScanResult() {
		mScanResult = wifimanager.getScanResults(); // ScanResult
		
//		textStatus.setText("Scan is counting\t" + ++scanCount + " times \n");

		// Wifi扫描结果显示在屏幕
//		textStatus.append("=======================================\n");
//		for (int i = 0; i < mScanResult.size(); i++) {
//			ScanResult result = mScanResult.get(i);
//			textStatus.append((i + 1) + ". SSID : " + result.SSID.toString()
//					+ "\t\t RSSI : " + result.level + " dBm\n");
//		}
//		textStatus.append("=======================================\n");
		
		// 
		mWifiScanDataAdapter.clear();
		for (int i = 0; i < mScanResult.size(); i++) {
			ScanResult result = mScanResult.get(i);
			mWifiScanDataAdapter.add(new Items(getApplicationContext(), "SSID : " + result.SSID.toString(), "RSSI : " + result.level + " dBm\n"));
		}
		
		/*
		 * SSID : Service Set IDentifier RSSI : Received Signal Strength
		 * Indication RSSI越接近0，越好，最差的为-100
		 */
		
		// 当前连接AP
		WifiInfo wifiInfo = wifimanager.getConnectionInfo();
		apInfoTxt.setText("当前连接 AP : " + wifiInfo.getSSID());
	}

	public void initWIFIScan() {
		// init WIFISCAN
		scanCount = 0;
		text = "";
		final IntentFilter filter = new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		//注册接收广播
		registerReceiver(mReceiver, filter);
		wifimanager.startScan();
		Log.d(TAG, "initWIFIScan()");
	}

	public void printToast(String messageToast) {
		Toast.makeText(this, messageToast, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.btnScanStart) {
			Log.d(TAG, "OnClick() btnScanStart()");
			printToast("WIFI SCAN !!!");
			initWIFIScan(); // start WIFIScan
		}
		if (v.getId() == R.id.btnScanStop) {
			Log.d(TAG, "OnClick() btnScanStop()");
			printToast("WIFI STOP !!!");
			unregisterReceiver(mReceiver); // stop WIFISCan
		}
	}

	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> p, View view, int position, long id) {
			// 显示选定AP的详细信息
			ScanResult result = mScanResult.get(position);
			String speed;
			speed = FindSpeed(result.SSID.toString()) + "";
			String msg = "AP名称 : " + result.SSID.toString() + "\n信号强度 : " + result.level + " dBm\n" +
			             "BSSID(mac) : " + result.BSSID + "\n加密方式 : \n" + result.capabilities + "\n频率 : " + result.frequency + " MHz\n速度" + speed +" kb/s\n";
			
			// 获得想要连接的AP
			final String ssid = result.SSID.toString();
			
			new AlertDialog.Builder(WifiScanActivity.this)
			.setTitle(result.SSID + " 详细信息")
			.setMessage(msg)
			.setPositiveButton("连接", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try{
						// 访问选定的AP
						WifiConfiguration wfc = new WifiConfiguration();

						// 不是通过一个必须输入密码的窗口
						String password = "";
						
						wfc.SSID = "\"".concat(ssid).concat("\"");
						wfc.status = WifiConfiguration.Status.DISABLED;
						wfc.priority = 40;
	
						// 直接连接
						wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
						wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
						wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
						wfc.allowedAuthAlgorithms.clear();
						wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
	
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
	
						// WEP 设置方法
						wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
						wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
						wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
						wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
						wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
						wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
	
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
						wfc.wepKeys[0] = "\"".concat(password).concat("\"");
						wfc.wepTxKeyIndex = 0;
	
						// WPA, WPA2 设置方法
					    wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
						wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
						wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
						wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
						wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
						wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
						wfc.preSharedKey = "\"".concat(password).concat("\"");
						
						// 尝试连接
						int networkId = wifimanager.addNetwork(wfc);
						if (networkId != -1) {
							// success, can call
							wifimanager.enableNetwork(networkId, true);
							// to connect
						}
					}catch(Exception e){
						Log.e(TAG, e.getMessage(), e);
					}
				}
			})
			.setNegativeButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//finish();
				}
			})
			.show();
			
		}

		private int FindSpeed(String str) {
			// TODO 自动生成的方法存根
			//if (str == "SAO") return;
			SQLiteDatabase db = openOrCreateDatabase("test.db", Context.MODE_PRIVATE, null);
			Cursor c = db.rawQuery("select * FROM speed where RSSI = ?", new String[]{"'" + str + "'"}); 
			int sum = 0 , t = 0;
			while (c.moveToNext()) {
				 t ++;
				 int sp = c.getInt(c.getColumnIndex("SP"));  
				 sum += sp;  
			}
			if (t != 0) sum = sum / t; 
			System.out.println(sum);
			c.close();
			db.close();
			return sum;
		}
	};
}
