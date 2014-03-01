package com.kdn.wifimanager;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;


public class ControlActivity extends Activity {
	
	private Button wifiOnBtn; 
	private Button wifiOffBtn; 
	private ToggleButton wifiTglBtn;
	
	private WifiManager wifiMgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.control_layout);
		
		wifiOnBtn = (Button)findViewById(R.id.wifiOnBtn);
		wifiOffBtn = (Button)findViewById(R.id.wifiOffBtn);
		wifiTglBtn = (ToggleButton)findViewById(R.id.wifiToggleBtn);
		
		wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);

		wifiOnBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				wifiOn();
				
				if (!wifiTglBtn.isChecked()) {
					wifiTglBtn.setChecked(true);
				}

				Toast.makeText(ControlActivity.this, "Wi-Fi 设备已经激活.", Toast.LENGTH_SHORT).show();
			}
		});

		wifiOffBtn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				wifiOff(); 

				if (wifiTglBtn.isChecked()) {
					wifiTglBtn.setChecked(false);
				}

				Toast.makeText(ControlActivity.this, "Wi-Fi 设备已被禁用.", Toast.LENGTH_SHORT).show();
			}
		});
		
        wifiTglBtn.setOnClickListener(new ToggleButton.OnClickListener() {
            public void onClick(View v) {
                if(wifiTglBtn.isChecked()) { //如果切换按钮开启
                	wifiOn();
                	Toast.makeText(ControlActivity.this, "Wi-Fi 开关已经开启.", Toast.LENGTH_SHORT).show();
                }else{ 
                	wifiOff();
                	Toast.makeText(ControlActivity.this, "Wi-Fi 开关已经关闭.", Toast.LENGTH_SHORT).show();
                }
            }
        });
	}

	void wifiOn() {
		wifiMgr.setWifiEnabled(true);
	}

	void wifiOff() {
		wifiMgr.setWifiEnabled(false);
	}
	
}
