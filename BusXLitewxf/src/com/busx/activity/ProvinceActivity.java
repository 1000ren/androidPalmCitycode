package com.busx.activity;


import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.busx.R;
import com.busx.activity.base.BaseActivity;
import com.busx.activity.view.SpellListView;
import com.busx.activity.view.SpellListView.OnTouchingLetterChangedListener;
import com.busx.database.SaveManager;
import com.busx.entities.Province;
import com.busx.utils.ActivityMgr;
import com.busx.utils.Constants;

public class ProvinceActivity extends BaseActivity 
{
	private ListView mListView = null;
	private List<Province> mProvinces = null;
	private SpellListView spellListView = null;
	private Handler handler;
	private OverlayThread overlayThread;
	private TextView overlay;
	private WindowManager windowManager;
	private TextView mTxt = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_province);
		mListView = (ListView)findViewById(R.id.list_province);
		mProvinces = getProvinceList();
		ProvinceAdapter transferInfoAdapter = new ProvinceAdapter(ProvinceActivity.this, mProvinces);
		mListView.setAdapter(transferInfoAdapter);
		mTxt = (TextView)findViewById(R.id.provincetext);
		mTxt.setText("请选择省");
		mListView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				ActivityMgr.getActivityManager().pushActivity(ProvinceActivity.this);
				Intent intent = new Intent( ProvinceActivity.this, CityActivity.class );
				intent.putExtra("provincecode", mProvinces.get(arg2).provincecode);
		        startActivity( intent );
			}
		});
		
		spellListView = (SpellListView)findViewById(R.id.spellListView);
		spellListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
		
		initOverlay();
		handler = new Handler();
        overlayThread = new OverlayThread();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		overlay.setVisibility(View.INVISIBLE);
		windowManager.removeView(overlay);
	}
	public List<Province> getProvinceList ()
	{
		StringBuffer sql = new StringBuffer(" select * from "+Constants.TABLE_PROVINCE);
		sql.append(" order by provincenamep");
		int count = SaveManager.getConditionCount(sql.toString());
		Province[] province=new Province[count];
		for (int i=0;i<count;i++) 
		{
			province[i] =new Province();
		}
		SaveManager.readConditionData(sql.toString(), province);
		
		List<Province> provinceList = Arrays.asList(province);
		return provinceList;
	}

	private class LetterListViewListener implements OnTouchingLetterChangedListener
	{

		@Override
		public void onTouchingLetterChanged(final String s) {
			int position = 0;
			for (Province province : mProvinces) 
			{
				if (s.equals(province.provincenamep.substring(0, 1).toUpperCase())) 
				{
					break;
				}
				position++;
			}
			mListView.setSelection(position);
			overlay.setText(s);
			overlay.setVisibility(View.VISIBLE);
			handler.removeCallbacks(overlayThread);
			//延迟一秒后执行，让overlay为不可见
			handler.postDelayed(overlayThread, 1500);
		}
    	
    }
	
	private void initOverlay() 
	{
    	LayoutInflater inflater = LayoutInflater.from(this);
    	overlay = (TextView) inflater.inflate(R.layout.popup_char_hint, null);
    	overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
    }
	 //设置overlay不可见
    private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}
    	
    }
}
