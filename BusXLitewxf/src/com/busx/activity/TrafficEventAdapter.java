package com.busx.activity;

import java.util.List;

import com.busx.R;
import com.busx.entities.TrafficEvent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TrafficEventAdapter extends BaseAdapter 
{
	private LayoutInflater mInflater = null;
	private List<TrafficEvent> mTransferInfoList = null;
	public TrafficEventAdapter(Context context,List<TrafficEvent> transferInfoList)
	{
		mInflater = LayoutInflater.from(context);
		this.mTransferInfoList = transferInfoList;
	}
	@Override
	public int getCount() {
		return mTransferInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return mTransferInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if(convertView==null)
		{
			convertView=mInflater.inflate( R.layout.list_items_transferinfo, null);
		}
		
		TrafficEvent transferInfo = mTransferInfoList.get(position);
		TextView textView = (TextView)convertView.findViewById(R.id.transferInfoTitleTextView);
		textView.setText(transferInfo.title);
		return convertView;
	}

}
