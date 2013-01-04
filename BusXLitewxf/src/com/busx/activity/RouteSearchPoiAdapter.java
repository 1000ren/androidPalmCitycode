package com.busx.activity;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.busx.R;
import com.busx.entities.POIItem;

public class RouteSearchPoiAdapter extends BaseAdapter
{
	@SuppressWarnings("unused")
	private Context context;
	private List<POIItem> poiItems=null;
	private LayoutInflater mInflater;
	
	public RouteSearchPoiAdapter(Context context,List<POIItem> poiItems)
	{
		this.context=context;
		this.poiItems=poiItems;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount()
	{
		return poiItems.size();
	}

	@Override
	public Object getItem(int position)
	{
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(convertView==null){
			convertView=mInflater.inflate( R.layout.list_items_poi_result, null);
		}
		
		TextView PoiName = ((TextView) convertView
				.findViewById(R.id.poiName));
		TextView poiAddress = (TextView) convertView
				.findViewById(R.id.poiAddress);

		PoiName.setText(poiItems.get(position).name);

		String address=null;
		if(poiItems.get(position).addr!=null){
			address=poiItems.get(position).addr;
		}else{
			address="中国";
		}
		if(null == address || address.trim().equals(""))
		{
			address = "暂无";
		}
		poiAddress.setText("地址:"+address);
		return convertView;
	}

}
