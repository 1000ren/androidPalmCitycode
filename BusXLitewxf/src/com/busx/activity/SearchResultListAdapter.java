package com.busx.activity;

import java.util.List;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.busx.CommonApplication;
import com.busx.R;
import com.busx.activity.view.DirectionCompassView;
import com.busx.entities.BusLine;
import com.busx.entities.BusLineRes;
import com.busx.entities.BusStation;
import com.busx.entities.BusStationRes;
import com.busx.entities.POIItem;
import com.busx.entities.POIRes;
import com.busx.protocol.poi.GetBusStopListRequest;
import com.busx.protocol.poi.GetBusStopListResponse;
import com.busx.utils.ActivityMgr;
import com.busx.utils.Constants;

public class SearchResultListAdapter extends BaseAdapter
{
	private SearchResultListActivity mContext = null;
	private POIRes mPoiRes=null;
	private List<BusStation> busstations=null;
	private List<BusLine> buslines=null; 

	private LayoutInflater mInflater;
	private int mSearchMode;
	private POIItem mPoiItem = null;
	private BusStation mBusStation = null;
	private BusLine mBusLine = null;

	
	public SearchResultListAdapter(SearchResultListActivity context, 
			POIRes poiRes, BusStationRes busStationRes,	BusLineRes busLineRes, 
			int searchMode)
	{
		this.mContext=context;
		mInflater = LayoutInflater.from(context);
		if ( poiRes != null )
		{
			mPoiRes = poiRes;
		}
		if ( busStationRes != null )
		{
			busstations = busStationRes.mBusStationList;
		}
		if ( busLineRes != null )
		{
			buslines = busLineRes.mBusLineList;
		}
		mSearchMode=searchMode;
	}
	
	public void setDate(POIRes poiRes, BusStationRes busStationRes,	BusLineRes busLineRes, 
			int searchMode)
	{
		if (searchMode==Constants.SEARCH_POI || searchMode==Constants.SEARCH_NEARBY )
		{
			mPoiRes = poiRes;
		}
		else if (searchMode==Constants.SEARCH_BUSSTATION)
		{
			busstations = busStationRes.mBusStationList;
		}
		else if (searchMode==Constants.SEARCH_BUSLINE) 
		{
			 buslines = busLineRes.mBusLineList;
		}
	}

	@Override
	public int getCount() {
		if (mSearchMode==Constants.SEARCH_POI || mSearchMode==Constants.SEARCH_NEARBY )
		{
			return mPoiRes.mPoiList.size();
		}
		else if (mSearchMode==Constants.SEARCH_BUSSTATION)
		{
			return busstations.size();
		}
		else if (mSearchMode==Constants.SEARCH_BUSLINE) 
		{
			return buslines.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position)
	{
		if ( mSearchMode==Constants.SEARCH_POI || mSearchMode==Constants.SEARCH_NEARBY )
		{
			return mPoiRes.mPoiList.get( position );
		}
		else if (mSearchMode==Constants.SEARCH_BUSSTATION) 
		{
			return busstations.get( position );
		}
		else if (mSearchMode==Constants.SEARCH_BUSLINE)
		{
			return buslines.get( position );
		}
		return null;
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
			convertView=mInflater.inflate( R.layout.list_items_search_reslist, null);
		}
		ImageView imgView = (ImageView) convertView.findViewById(R.id.ItemImage);
		TextView poiName = ((TextView) convertView.findViewById(R.id.ItemTitle));
		TextView poiAddress = (TextView) convertView.findViewById(R.id.ItemText);
		ImageButton posButton = (ImageButton)convertView.findViewById(R.id.BtnDefault);
		DirectionCompassView poidirection = (DirectionCompassView)convertView.findViewById(R.id.ItemDirection);
		int index = getImageResource(position,mSearchMode);
		if (mSearchMode==Constants.SEARCH_POI)
		{
			mPoiItem = mPoiRes.mPoiList.get(position);
			poiName.setText(mPoiItem.name);
			String address=null;
			if(mPoiItem.addr!=null)
			{
				address=mPoiItem.addr;
			}
			else
			{
				address="中国";
			}
			poiAddress.setText(address);
			imgView.setImageResource(index);
			posButton.setTag(mPoiItem);
			poidirection.setVisibility(View.GONE);
		}
		else if (mSearchMode==Constants.SEARCH_BUSSTATION) 
		{
			mBusStation = busstations.get(position);
			poiName.setText(mBusStation.name);
			poiAddress.setText(mBusStation.buslinename);
			imgView.setImageResource(index);
			posButton.setTag(mBusStation);
			poidirection.setVisibility(View.GONE);
		}
		else if (mSearchMode==Constants.SEARCH_BUSLINE)
		{
			mBusLine = buslines.get(position);
			poiName.setText(mBusLine.linename);
			StringBuffer addr = new StringBuffer("");
			if (null != mBusLine.firsttime && !"".equals(mBusLine.firsttime) && null != mBusLine.lasttime && !"".equals(mBusLine.lasttime)) 
			{
				addr.append("首:"+mBusLine.firsttime+" 末:"+mBusLine.lasttime);
			}
			if (null != mBusLine.intervalm && !"".equals(mBusLine.intervalm)) {
				addr.append(" 间隔:"+mBusLine.intervalm+"分钟");
			}
			
			poiAddress.setText(addr.toString());
			imgView.setImageResource(index);
			posButton.setTag(mBusLine);
			poidirection.setVisibility(View.GONE);
		}
		else if (mSearchMode==Constants.SEARCH_NEARBY)
		{
			mPoiItem = mPoiRes.mPoiList.get(position);
			imgView.setImageResource(index);
			poiName.setText(mPoiItem.name);
			if(mPoiItem.addr!=null)
			{
				poiAddress.setText(mPoiItem.addr);
			}
			else
			{
				poiAddress.setText(mPoiItem.adminname);
			}
			poidirection.setVisibility(View.VISIBLE);
			poidirection.setValues( mPoiRes.mValues );
			poidirection.setDirectionAngle( mPoiItem.angle );
			posButton.setTag(mPoiItem);
		}

		posButton.setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ActivityMgr.getActivityManager().pushActivity(mContext);

				Intent intent= new Intent();
				intent.setClass( mContext, SearchResultDetailActivity.class);
				intent.putExtra( Constants.EXTRA_SEARCHMODE, mSearchMode );
				if ( mSearchMode != Constants.SEARCH_BUSLINE )
				{
					if (mSearchMode==Constants.SEARCH_POI)
					{
						intent.putExtra( Constants.EXTRA_POIITEM, (POIItem)v.getTag() );
					}
					else if (mSearchMode==Constants.SEARCH_BUSSTATION)
					{
						intent.putExtra( Constants.EXTRA_BUSSTATION, (BusStation)v.getTag() );
					}
					else if (mSearchMode==Constants.SEARCH_NEARBY)
					{
						intent.putExtra( Constants.EXTRA_POIITEM, (POIItem)v.getTag() );
					}
					mContext.startActivity(intent);
				}
				else
				{
					final BusLine busLine = (BusLine)v.getTag();
					if ( busLine.busStops.size() <= 0 )
					{
						final CommonApplication commonApplication = (CommonApplication)mContext.getApplication();
						ClientSession.getInstance().asynGetResponse(new GetBusStopListRequest( 
								commonApplication.mUserLoginInfo.sid, busLine.lineid , commonApplication.mCity.admincode),
								new IResponseListener() {
									@Override
									public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2)
									{
										GetBusStopListResponse busstopListResponse = (GetBusStopListResponse) arg0;
										commonApplication.mUserLoginInfo.copySID( busstopListResponse.mUserLoginInfo );
										busLine.copy( busstopListResponse.mBusLine );

										ActivityMgr.getActivityManager().pushActivity(mContext);
										Intent intent= new Intent();
										intent.setClass( mContext, SearchResultDetailActivity.class);
										intent.putExtra( Constants.EXTRA_SEARCHMODE, mSearchMode );
										intent.putExtra( Constants.EXTRA_BUSLINE, busLine );
										mContext.startActivity(intent);
									}
						}); 
					}else {
						intent.putExtra( Constants.EXTRA_BUSLINE, busLine );
						mContext.startActivity(intent);
					}
				}
			}
		});

		return convertView;
	}


	/**
	 * 根据position获得相应位置的图片
	 * @param index
	 * @return R.drawable.ImageResource
	 */
	public int getImageResource(int index,int searchMode)
	{
			if(index==0)
			{
				return R.drawable.icon_marka;
			}
			else if(index==1)
			{
				return R.drawable.icon_markb;
			}
			else if(index==2)
			{
				return R.drawable.icon_markc;
			}
			else if(index==3)
			{
				return R.drawable.icon_markd;
			}
			else if(index==4)
			{
				return R.drawable.icon_marke;
			}
			else if(index==5)
			{
				return R.drawable.icon_markf;
			}
			else if(index==6)
			{
				return R.drawable.icon_markg;
			}
			else if(index==7)
			{
				return R.drawable.icon_markh;
			}
			else if(index==8)
			{
				return R.drawable.icon_marki;
			}
			else if(index==9)
			{
				return R.drawable.icon_markj;
			}
		return R.drawable.icon_gcoding;
	}
	
}
