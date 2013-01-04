package com.busx.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.busx.R;
import com.busx.entities.BusRoute;
import com.busx.entities.BusXRes;
import com.busx.entities.RoutePathInfo;
import com.busx.utils.Constants;
import com.busx.utils.Utils;

public class RouteBusResultAdapter extends BaseAdapter
{
	@SuppressWarnings("unused")
	private Context context;
	private BusXRes mBusXRes;

	private LayoutInflater mInflater;

	public RouteBusResultAdapter(Context context, BusXRes busXRes)
	{
		this.context=context;
		mInflater = LayoutInflater.from(context);
		mBusXRes = busXRes;
	}

	@Override
	public int getCount()
	{
		if ( mBusXRes != null )
		{
			return mBusXRes.busRouteList.size();
		}
		else
		{
			return 0;
		}
	}

	@Override
	public Object getItem(int position)
	{
		if ( mBusXRes != null && position >= 0 && position < mBusXRes.busRouteList.size() )
		{
			return mBusXRes.busRouteList.get( position );
		}
		else
		{
			return null;
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
		if ( convertView == null )
		{
			convertView=mInflater.inflate( R.layout.list_items_routeresult_bus, null);
		}
		if ( position >= 0 && position < mBusXRes.busRouteList.size() )
		{
			BusRoute busRoute = mBusXRes.busRouteList.get( position );

			TextView itemTag = ((TextView) convertView
					.findViewById(R.id.ItemTag));
			TextView itemTitle = (TextView) convertView
					.findViewById(R.id.ItemTitle);
			TextView itemTime = ((TextView) convertView
					.findViewById(R.id.ItemTime));

			String strTag = "方案" + String.valueOf( position + 1 );
			itemTag.setText( strTag );

			StringBuffer sumStringBuf = new StringBuffer();
			for (int i=0,len=busRoute.pathInfoList.size(); i<len; i++)
			{
				RoutePathInfo routePathInfo = busRoute.pathInfoList.get(i);
				if ( routePathInfo == null )
				{
					continue;
				}
				if ( i != 0 )
				{
					sumStringBuf.append( "→" );
				}
				sumStringBuf.append( routePathInfo.rosenname );
			}
			itemTitle.setText( sumStringBuf.toString() );

			String strDesc = "";
			if ( mBusXRes.orderMode == Constants.ORDER_MODE_DEFAULT )
			{
				// 时间 /总距离
				String strDist;
				if ( busRoute.distance > 1000 )
				{
					// KM
					double dist = busRoute.distance / 1000.0;
					strDist = Utils.formatDoubleNum(dist)+"公里";
				}
				else
				{
					strDist = String.format( "%d米", busRoute.distance );
				}
				strDesc = busRoute.spendtime + "分钟/" + strDist;
			}
			else if ( mBusXRes.orderMode == Constants.ORDER_MODE_EXCHANGE )
			{
				// 时间 / 换乘次数
				String strRosenCount;
				if ( busRoute.rosencount == 1 )
				{
					strRosenCount = "直达";
				}
				else 
				{
					strRosenCount = String.format( "换乘 %d 次", busRoute.rosencount-1 );
				}
				strDesc = busRoute.spendtime + "分钟/" + strRosenCount;
			}
			else if ( mBusXRes.orderMode == Constants.ORDER_MODE_WALK )
			{
				// 时间 /步行距离
				String strPedDist;
				if ( busRoute.peddist > 1000 )
				{
					double dist = busRoute.peddist / 1000.0;
					strPedDist = Utils.formatDoubleNum(dist)+"公里";
				}
				else 
				{
					strPedDist = String.format( "%d米", busRoute.peddist );
				}
				strDesc = busRoute.spendtime + "分钟/" + "步行" + strPedDist;
			}
			else if ( mBusXRes.orderMode == Constants.ORDER_MODE_TOLL )
			{
				// 时间 / 费用
				String strToll = String.format( "%.1f元", busRoute.toll/100.0 );
				strDesc = busRoute.spendtime + "分钟/" + "花费" + strToll;
			}
			itemTime.setText( strDesc );
		}

		return convertView;
	}

}
