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
import com.busx.entities.NearbyKind;

public class NearbyKindAdapter extends BaseAdapter {

	//private Context context;
	private LayoutInflater mInflater;
	private List<NearbyKind> nearby_kinds;
	
	public NearbyKindAdapter(Context context,List<NearbyKind> nearby_kinds){
		this.mInflater=LayoutInflater.from(context);
		this.nearby_kinds=nearby_kinds;
	}
	@Override
	public int getCount() {
		return nearby_kinds.size();
	}

	@Override
	public Object getItem(int position) {
		return nearby_kinds.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null)
		{
			convertView=mInflater.inflate( R.layout.list_items_poi_nearby_kind, null);
		}
		TextView kindItemTitle=(TextView)convertView.findViewById(R.id.KindItemTitle);
		
		NearbyKind nearby_kinditem=nearby_kinds.get(position);
		kindItemTitle.setText(nearby_kinditem.name);
		ImageView imageView=(ImageView)convertView.findViewById(R.id.ItemImage);
		int resId = R.drawable.icon_class_bus_station;
		switch ( position )
		{
			case 0:
			{
				resId = R.drawable.icon_class_bus_station;
				break;
			}
			case 1:
			{
				resId = R.drawable.icon_class_dinner;
				break;
			}
			case 2:
			{
				resId = R.drawable.icon_class_ktv;
				break;
			}
			case 3:
			{
				resId = R.drawable.icon_class_bank;
				break;
			}
			case 4:
			{
				resId = R.drawable.icon_class_hotel;
				break;
			}
			case 5:
			{
				resId = R.drawable.icon_class_supermarket;
				break;
			}
			case 6:
			{
				resId = R.drawable.icon_class_hospital;
				break;
			}
			default:
			{
				resId = R.drawable.icon_class_bus_station;
				break;
			}
			
		}
		imageView.setImageResource( resId );
		return convertView;
	}

}
