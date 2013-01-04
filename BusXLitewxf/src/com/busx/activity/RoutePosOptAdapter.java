package com.busx.activity;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.busx.R;
import com.busx.entities.RoutePosOptItem;

public class RoutePosOptAdapter extends BaseAdapter
{
	@SuppressWarnings("unused")
	private Context context;
	private List<RoutePosOptItem> mRoutePosOptItems=null;
	private LayoutInflater mInflater;

	public RoutePosOptAdapter(Context context,List<RoutePosOptItem> optItems)
	{
		this.context=context;
		this.mRoutePosOptItems=optItems;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() 
	{
		if ( mRoutePosOptItems != null )
		{
			return mRoutePosOptItems.size();
		}
		else
		{
			return 0;
		}
	}

	@Override
	public Object getItem(int position) 
	{
		if ( mRoutePosOptItems != null )
		{
			return mRoutePosOptItems.get( position );
		}
		else
		{
			return 0;
		}
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
			convertView=mInflater.inflate( R.layout.list_items_routeposopt, null);
		}

		ImageView imgView = (ImageView) convertView.findViewById(R.id.ItemImage);
		TextView layerName = (TextView) convertView.findViewById(R.id.ItemTitle);

		RoutePosOptItem optItem = mRoutePosOptItems.get( position );
		if ( optItem != null )
		{
			imgView.setImageResource( optItem.icon );
			layerName.setText( optItem.name );
		}

		return convertView;
	}

}
