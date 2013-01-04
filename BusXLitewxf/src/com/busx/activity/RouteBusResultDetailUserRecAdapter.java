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
import com.busx.entities.BusRouteReqDetail;

public class RouteBusResultDetailUserRecAdapter extends BaseAdapter
{
	private LayoutInflater mInflater;
	private List<BusRouteReqDetail> mExdetail;
	private String mStartName;
	private String mEndName;

	public RouteBusResultDetailUserRecAdapter(Context context, List<BusRouteReqDetail> exdetail,String startName,String endName)
	{
		mInflater = LayoutInflater.from(context);
		mExdetail = exdetail;
		mStartName = startName;
		mEndName = endName;
	}

	@Override
	public int getCount()
	{
		return mExdetail == null ? 0 :  mExdetail.size()+2;
	}

	@Override
	public Object getItem(int position)
	{
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if ( convertView == null )
		{
			convertView=mInflater.inflate( R.layout.list_items_routeresult_bus_detail, null);
		}
			ImageView imgView = ((ImageView) convertView
					.findViewById(R.id.busdetail_ItemImage));
			TextView itemTitle = (TextView) convertView
					.findViewById(R.id.busdetail_ItemTitle);
			if(position==0)
			{
				imgView.setImageResource( R.drawable.icon_nav_start );
				itemTitle.setText( mStartName );
			}
			else if(position==getCount()-1)
			{
				imgView.setImageResource( R.drawable.icon_nav_end );
				itemTitle.setText( mEndName );
			}
			else
			{
				BusRouteReqDetail busRouteReqDetail = mExdetail.get( position-1 );
				if ( busRouteReqDetail != null )
				{
					switch ( busRouteReqDetail.type )
					{
						case 0:
						{
							imgView.setImageResource( R.drawable.icon_nav_start );
							break;
						}
						case 1:
						{
							imgView.setImageResource( R.drawable.icon_nav_bus );
							break;
						}
						case 2:
						{
							imgView.setImageResource( R.drawable.icon_nav_rail );
							break;
						}
						case 4:
						{
							imgView.setImageResource( R.drawable.icon_nav_foot );
							break;
						}
						case 9:
						{
							imgView.setImageResource( R.drawable.icon_nav_end );
							break;
						}
						default:
						{
							imgView.setImageResource( R.drawable.icon_nav_node );
							break;
						}
					}
					String desc = "步行至"+busRouteReqDetail.startstop+",乘坐"+busRouteReqDetail.linename
							+",经过"+busRouteReqDetail.num+"站,到达"+busRouteReqDetail.endstop;
					itemTitle.setText( desc );
				}
			}
			
		return convertView;
	}

}
