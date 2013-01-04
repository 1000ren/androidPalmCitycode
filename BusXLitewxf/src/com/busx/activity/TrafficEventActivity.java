package com.busx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IResponseListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.busx.R;
import com.busx.activity.base.BaseActivity;
import com.busx.entities.TrafficEvent;
import com.busx.protocol.trafficEvent.GetTrafficEventDetailRequest;
import com.busx.protocol.trafficEvent.GetTrafficEventDetailResponse;

public class TrafficEventActivity extends BaseActivity 
{
	private ListView mListView = null;
	private TextView mResTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search_reslist);
		mListView = (ListView)findViewById(R.id.ListView_SearchRes);
		mResTitle = (TextView) findViewById( R.id.ItemPOIRst );
		mResTitle.setText(" 出行提示 ");
		TrafficEventAdapter transferInfoAdapter = new TrafficEventAdapter(TrafficEventActivity.this, mCommonApplication.mTrafficEventList);
		mListView.setAdapter(transferInfoAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				//查看详细
		    	TrafficEvent transferInfo=mCommonApplication.mTrafficEventList.get(arg2);

		    	ClientSession.getInstance().asynGetResponse(new GetTrafficEventDetailRequest(transferInfo.id, mCommonApplication.mUserLoginInfo.sid, mCommonApplication.mCity.admincode),
						new IResponseListener()
				{
							@Override
							public void onResponse(BaseHttpResponse arg0, BaseHttpRequest arg1,ControlRunnable arg2) 
							{
								GetTrafficEventDetailResponse trafficEventDetailResponse = (GetTrafficEventDetailResponse) arg0;
								mCommonApplication.mTrafficEvent = trafficEventDetailResponse.mTrafficEvent;
					    		Intent intent = new Intent();
					    		intent.setClass(TrafficEventActivity.this, TrafficEventDetailActivity.class);
					    		startActivity(intent);
							}
				});
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			mCommonApplication.mIsBack = true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
