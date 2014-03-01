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

	// ��ȡXML����Ĳ���
	private LayoutInflater mInflater;

	public WiFiScanDataAdapter(Context context, ArrayList<Items> object) {

		// ��ʼ����
		super(context, 0, object);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	// ��ʾ��ʽ
	@Override
	public View getView(int position, View v, ViewGroup parent) {
		View view = null;

		// �鿴��ǰ�б��ÿ���

		if (v == null) {
			// XML ͨ���б���ͼ����
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
