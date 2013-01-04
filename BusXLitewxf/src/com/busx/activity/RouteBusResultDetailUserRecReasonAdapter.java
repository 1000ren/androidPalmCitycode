package com.busx.activity;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.busx.R;
import com.busx.entities.BusRouteUserInfo;

public class RouteBusResultDetailUserRecReasonAdapter extends BaseAdapter
{
	private LayoutInflater mInflater;
	private List<BusRouteUserInfo> mBusRouteUserInfo;
	
	public RouteBusResultDetailUserRecReasonAdapter(RouteBusResultDetailUserRecReasonActivity routeBusResultDetailUserRecReasonActivity,List<BusRouteUserInfo> busRouteUserInfo)
	{
		mInflater = LayoutInflater.from(routeBusResultDetailUserRecReasonActivity);
		mBusRouteUserInfo = busRouteUserInfo;
	}
	

	@Override
	public int getCount() {
		return null == mBusRouteUserInfo ? 0 :mBusRouteUserInfo.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mBusRouteUserInfo.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(convertView==null)
		{
			convertView=mInflater.inflate( R.layout.list_items_route_reason, null);
		}
		TextView userName = ((TextView) convertView.findViewById(R.id.TextViewUserName));
		TextView time = (TextView) convertView.findViewById(R.id.TextViewTime);
		TextView reason = (TextView) convertView.findViewById(R.id.TextViewReason);
		
		userName.setText("用户名称："+mBusRouteUserInfo.get(position).nickname);
		time.setText("推荐时间："+mBusRouteUserInfo.get(position).time);
		String str = mBusRouteUserInfo.get(position).reason;
		if(null != str)
		{
			str = str.replace("/n", "\n");
		}
		reason.setText("推荐理由："+str);

		return convertView;
	}


	
}
