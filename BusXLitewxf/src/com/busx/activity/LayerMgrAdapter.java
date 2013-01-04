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
import com.busx.entities.LayerItem;

public class LayerMgrAdapter extends BaseAdapter
{
	@SuppressWarnings("unused")
	private Context context;
	private List<LayerItem> mLayerItems=null;
	private LayoutInflater mInflater;

	public LayerMgrAdapter(Context context,List<LayerItem> layerItems)
	{
		this.context=context;
		this.mLayerItems=layerItems;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() 
	{
		if ( mLayerItems != null )
		{
			return mLayerItems.size();
		}
		else
		{
			return 0;
		}
	}

	@Override
	public Object getItem(int position) 
	{
		if ( mLayerItems != null )
		{
			return mLayerItems.get( position );
		}
		else
		{
			return 0;
		}
	}

	@Override
	public long getItemId(int position) 
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(convertView==null)
		{
			convertView=mInflater.inflate( R.layout.list_items_layers, null);
		}

		ImageView imgView = (ImageView) convertView.findViewById(R.id.ItemImage);
		TextView layerName = (TextView) convertView.findViewById(R.id.ItemTitle);
		ImageView layerOpen = (ImageView) convertView.findViewById(R.id.BtnDefault);

		LayerItem layerItem = mLayerItems.get( position );
		if ( layerItem != null )
		{
			imgView.setImageResource( layerItem.icon );
			layerName.setText( layerItem.name );
			if ( layerItem.open )
			{
				layerOpen.setImageResource( R.drawable.btn_check_buttonless_on );
			}
			else
			{
				layerOpen.setBackgroundResource( R.drawable.btn_check_buttonless_off );
			}
		}

		return convertView;
	}

}
