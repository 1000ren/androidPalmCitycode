package com.busx.activity;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.busx.R;
import com.busx.entities.BusRouteReqDetail;
import com.busx.entities.BusRouteUserInfo;
import com.busx.entities.BusRouteUserRecDetailList;

public class RouteBusResultUserRecommendAdapter extends BaseAdapter
{
	private List<BusRouteUserRecDetailList> mBusRouteUserRecDetailList;

	private LayoutInflater mInflater;
	
	public RouteBusResultUserRecommendAdapter(Context context, List<BusRouteUserRecDetailList> busRouteUserRecDetailList)
	{
		mInflater = LayoutInflater.from(context);
		mBusRouteUserRecDetailList = busRouteUserRecDetailList;
	}

	@Override
	public int getCount()
	{
		return mBusRouteUserRecDetailList == null ? 0 : mBusRouteUserRecDetailList.size();
	}

	@Override
	public Object getItem(int position)
	{
			return mBusRouteUserRecDetailList.get( position );
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
		BusRouteUserRecDetailList busRouteUserRecDetailList = mBusRouteUserRecDetailList.get(position);
		
		LinearLayout rl = (LinearLayout) convertView.findViewById(R.id.RelativeLayoutUserRec);
		rl.setVisibility(0);
		
		int approve = 0;
		int opposition = 0;
		
		//累加赞和不赞
		if(null != busRouteUserRecDetailList.mBusRouteUserInfo && busRouteUserRecDetailList.mBusRouteUserInfo.size()>0)
		{
			for(BusRouteUserInfo busRouteUserInfo : busRouteUserRecDetailList.mBusRouteUserInfo)
			{
				approve += busRouteUserInfo.approve;
				opposition += busRouteUserInfo.opposition;
			}
			//赞加上推荐的条数，有一条推荐，算是一个赞
			approve += busRouteUserRecDetailList.mBusRouteUserInfo.size();
		}
		

		TextView itemTag = ((TextView) convertView
				.findViewById(R.id.ItemTag));
		TextView itemTitle = (TextView) convertView
				.findViewById(R.id.ItemTitle);
		TextView itemLineName = (TextView) convertView
				.findViewById(R.id.ItemLineName);
		TextView itemTime = ((TextView) convertView
				.findViewById(R.id.ItemTime));
		TextView appr = ((TextView) convertView
				.findViewById(R.id.textview_approve));
		TextView oppo = ((TextView) convertView
				.findViewById(R.id.textview_opposition));

		String strTag = "推荐" + String.valueOf( position + 1 );
		itemTag.setText( strTag );
		//推荐大于100条，则把"推荐1"等字体变小，否则100条以后会和内容重合
		if(getCount()>98)
		{
			itemTag.setTextSize(13);
		}
		
		appr.setText("("+approve+")");
		oppo.setText("("+opposition+")");

		StringBuffer sumStringBuf = new StringBuffer();
		for (int i=0;i<busRouteUserRecDetailList.busRouteReq.exdetail.size(); i++)
		{
			BusRouteReqDetail busRouteReqDetail = busRouteUserRecDetailList.busRouteReq.exdetail.get(i);
			if ( busRouteReqDetail == null )
			{
				continue;
			}
			if ( i != 0 )
			{
				sumStringBuf.append( "→" );
			}
			sumStringBuf.append( busRouteReqDetail.linename );
		}
		itemTitle.setText( sumStringBuf.toString() );
		itemLineName.setVisibility(View.VISIBLE);
		int num = busRouteUserRecDetailList.busRouteReq.exdetail.size();
		itemLineName.setText(busRouteUserRecDetailList.busRouteReq.exdetail.get(0).startstop+"→"+busRouteUserRecDetailList.busRouteReq.exdetail.get(num-1).endstop);
		
		String strToll = String.format( "%.1f元", busRouteUserRecDetailList.busRouteReq.cost/100.0 );
		String strDesc = "花费" + strToll +"/换乘"+busRouteUserRecDetailList.busRouteReq.exnum+"次";

		itemTime.setText( strDesc );

		return convertView;
	}
	
}
