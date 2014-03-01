package com.kdn.wifimanager;

import android.content.Context;

public class Items {


	private Context mContext;
	private String mSsid;
	private String mRssi;

	public Items(Context context, String ssid, String rssi) {
		mContext = context;
		mSsid = ssid;
		mRssi = rssi;
	}

	public String getSsid() {
		return mSsid;
	}

	public void setSsid(String ssid) {
		this.mSsid = ssid;
	}

	public String getRssi() {
		return mRssi;
	}

	public void setRssi(String rssi) {
		this.mRssi = rssi;
	}
}
