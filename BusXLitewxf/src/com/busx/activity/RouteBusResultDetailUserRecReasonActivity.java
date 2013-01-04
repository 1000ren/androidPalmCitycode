package com.busx.activity;


import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.busx.R;
import com.busx.activity.base.BaseActivity;
import com.busx.entities.BusRouteUserRecDetailList;
import com.busx.utils.Constants;


public class RouteBusResultDetailUserRecReasonActivity extends BaseActivity
{
	private TextView mResTitle;
	private ListView mListView;
	private BusRouteUserRecDetailList mBusRouteUserRecDetailList;
	private RouteBusResultDetailUserRecReasonAdapter mRouteBusResultDetailUserRecReasonAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routeresult_bus_detail_reason);
		
		mBusRouteUserRecDetailList = (BusRouteUserRecDetailList) getDataFromIntent( Constants.EXTRA_BUSROUTEUSERREC );
		int reasonNum = null == mBusRouteUserRecDetailList.mBusRouteUserInfo ? 0 :mBusRouteUserRecDetailList.mBusRouteUserInfo.size();
		mResTitle = (TextView) findViewById( R.id.ItemPOIRst );
		mResTitle.setText( "推荐理由，共"+reasonNum+"条" );

		mListView=(ListView) findViewById(R.id.ListView_SearchRes);
		
		mRouteBusResultDetailUserRecReasonAdapter = new RouteBusResultDetailUserRecReasonAdapter(this,mBusRouteUserRecDetailList.mBusRouteUserInfo);
		mListView.setAdapter(mRouteBusResultDetailUserRecReasonAdapter);
		
	}
	
}
