package com.busx.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.busx.R;
import com.busx.entities.BusRoute;
import com.busx.entities.RouteGuide;

public class RouteBusResultDetailAdapter extends BaseAdapter
{
	@SuppressWarnings("unused")
	private Context context;
	private LayoutInflater mInflater;
	private BusRoute mBusRoute;

	public RouteBusResultDetailAdapter(Context context, BusRoute busRoute)
	{
		this.context=context;
		mInflater = LayoutInflater.from(context);
		mBusRoute = busRoute;
	}

	@Override
	public int getCount()
	{
		if ( mBusRoute != null && mBusRoute.guideList != null)
		{
			return mBusRoute.guideList.size();
		}
		else
		{
			return 0; 
		}
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
		if ( mBusRoute != null && position >= 0 && position < mBusRoute.guideList.size() )
		{
			ImageView imgView = ((ImageView) convertView
					.findViewById(R.id.busdetail_ItemImage));
			TextView itemTitle = (TextView) convertView
					.findViewById(R.id.busdetail_ItemTitle);
			
			RouteGuide routeGuide = mBusRoute.guideList.get( position );
			if ( routeGuide != null )
			{
				switch ( routeGuide.type )
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
				if ( routeGuide.type == 4 && 
						routeGuide.walkGuide != null && routeGuide.walkGuide.length() > 0 )
				{
					String walkTxt=  routeGuide.desc + "\n" +  routeGuide.walkGuide ;
					SpannableString s = new SpannableString( walkTxt );
			        s.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), walkTxt.indexOf("【"), walkTxt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			        s.setSpan(new RelativeSizeSpan(0.8f), walkTxt.indexOf("【"), walkTxt.length(),  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
			        itemTitle.setText( s );
				}
				else
				{
					itemTitle.setText( routeGuide.desc );
				}
			}
		}

		return convertView;
	}

}
