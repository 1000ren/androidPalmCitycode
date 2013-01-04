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
import com.busx.entities.BusStop;
import com.busx.utils.Constants;

public class SearchResultDetailAdapter extends BaseAdapter
{
	private LayoutInflater mInflater;
	private int mSearchMode;
	private String[] busline;
	private List<BusStop> busStops=null;
	private List<String> detail;
	private Context mContext;
	
	public SearchResultDetailAdapter(Context context, String[] busline,List<BusStop> busStops,List<String> detail,int mSearchMode){
		this.mContext=context;
		mInflater = LayoutInflater.from(mContext);
		this.busline = busline;
		this.busStops=busStops;
		this.mSearchMode=mSearchMode;
		this.detail = detail;
	}

	@Override
	public int getCount() {
		if (mSearchMode==Constants.SEARCH_BUSSTATION) {
			return busline.length;
		}else if (mSearchMode==Constants.SEARCH_BUSLINE) {
			return busStops.size();
		}else if (mSearchMode==Constants.SEARCH_NEARBY||mSearchMode==Constants.SEARCH_POI) {
			return detail.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mSearchMode==Constants.SEARCH_BUSSTATION) {
			return busline[position];
		}else if (mSearchMode==Constants.SEARCH_BUSLINE) {
			return busStops.get(position);
		}else if (mSearchMode==Constants.SEARCH_NEARBY||mSearchMode==Constants.SEARCH_POI) {
			return detail.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null)
		{
			convertView=mInflater.inflate( R.layout.list_items_search_resdetail, null);
		}
		TextView buslineName = (TextView) convertView.findViewById(R.id.BtnTelDetail);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.ItemImage);
		if (mSearchMode==Constants.SEARCH_BUSSTATION) 
		{
			buslineName.setText(busline[position].substring(busline[position].indexOf(":")+1, busline[position].length()));
		}
		else if (mSearchMode==Constants.SEARCH_BUSLINE) 
		{
			buslineName.setText(busStops.get(position).stopname);
		}
		else if (mSearchMode==Constants.SEARCH_NEARBY||mSearchMode==Constants.SEARCH_POI) 
		{
			buslineName.setText(detail.get(position));
			if (detail.get(position).equals(mContext.getString(R.string.share_to_friend))) 
			{
				imageView.setImageResource(R.drawable.icon_share);
			}
			else if (detail.get(position).equals(mContext.getString(R.string.along_bus))) 
			{
				imageView.setImageResource(R.drawable.icon_nav_bus);
			}
			else 
			{
				imageView.setImageResource(R.drawable.icon_tel);
			}
		}
		
		return convertView;
	}

}
