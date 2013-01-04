package com.busx.activity;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.busx.R;
import com.busx.activity.base.BaseActivity;


public class AboutUsActivity extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_aboutus);
		
		TextView titleTextView = (TextView) findViewById(R.id.title_text);
		titleTextView.setText( this.getResString( R.string.menu_about ) );
	}
}
