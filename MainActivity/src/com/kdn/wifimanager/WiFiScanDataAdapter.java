package com.kdn.wifimanager;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WiFiScanDataAdapter extends ArrayAdapter<Items> {

	// 读取XML对象的布局
	private LayoutInflater mInflater;

	public WiFiScanDataAdapter(Context context, ArrayList<Items> object) {

		// 初始化类
		super(context, 0, object);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	// 显示样式
	@Override
	public View getView(int position, View v, ViewGroup parent) {
		View view = null;

		// 查看当前列表获得控制

		if (v == null) {
			// XML 通过列表视图布局
			view = mInflater.inflate(R.layout.item_layout, null);
		} else {
			view = v;
		}


		final Items item = this.getItem(position);

		if (item != null) {

			TextView tv = (TextView) view.findViewById(R.id.textView1);
			TextView tv2 = (TextView) view.findViewById(R.id.textView2);

			tv.setText(item.getSsid());
//			tv.setTextColor(Color.WHITE);

			tv2.setText(item.getRssi());
//			tv.setTextColor(Color.WHITE);
		}

		return view;
	}

}
