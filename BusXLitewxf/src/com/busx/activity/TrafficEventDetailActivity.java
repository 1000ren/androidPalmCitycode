package com.busx.activity;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.busx.R;
import com.busx.activity.base.BaseActivity;
/**
 * 显示出行信息详细
 * @author Administrator
 *
 */
public class TrafficEventDetailActivity extends BaseActivity 
{
	private TextView titleTextView=null;
	private TextView contentTextView=null;
	private TextView timeTextView=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list_items_transferinfo_detail);
		titleTextView = (TextView)findViewById(R.id.transferinfo_detail_title);
		contentTextView = (TextView)findViewById(R.id.transferinfo_detail_content);
		timeTextView = (TextView)findViewById(R.id.transferinfo_detail_time);
		
		titleTextView.setText(mCommonApplication.mTrafficEvent.title);
		contentTextView.setText(mCommonApplication.mTrafficEvent.content);
		timeTextView.setText("发布时间："+mCommonApplication.mTrafficEvent.pubtime);
	}
}
