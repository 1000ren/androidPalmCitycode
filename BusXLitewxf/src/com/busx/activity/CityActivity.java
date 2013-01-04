package com.busx.activity;


import java.util.Arrays;
import java.util.List;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.amap.mapapi.core.GeoPoint;
import com.busx.R;
import com.busx.activity.base.BaseActivity;
import com.busx.activity.view.SpellListView;
import com.busx.activity.view.SpellListView.OnTouchingLetterChangedListener;
import com.busx.database.SaveManager;
import com.busx.entities.City;
import com.busx.utils.ActivityMgr;
import com.busx.utils.Constants;

public class CityActivity extends BaseActivity 
{
	private ListView mListView = null;
	private List<City> mCitys = null;
	private String provincecode = null;
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
		provincecode = (String)getDataFromIntent("provincecode");
		mCitys = getCityList();
		CityAdapter transferInfoAdapter = new CityAdapter(CityActivity.this, mCitys);
		mListView.setAdapter(transferInfoAdapter);
		mTxt = (TextView)findViewById(R.id.provincetext);
		mTxt.setText("请选择市");
		mListView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{
				City city = mCitys.get(arg2);
				
				mCommonApplication.mMapDisplayMode = Constants.MAPMODE_OTHERCITYCENTER;
				//重新设置地图的中心点
				String[] lonlat = city.lonLats.split(",");
				GeoPoint point = new GeoPoint((int) (Double.parseDouble(lonlat[1])* 1E6), (int) (Double.parseDouble(lonlat[0])* 1E6));
				mCommonApplication.mMapView.getController().animateTo(point);
				mCommonApplication.mCity =  city;
				//选择城市以后 切换地图
				ActivityMgr.getActivityManager().popAllActivity();
				finish();
				showToast(mCommonApplication.mCity.adminname);
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
	public List<City> getCityList ()
	{
		StringBuffer sql = new StringBuffer(" select * from "+Constants.TABLE_CITY);
		sql.append(" where provincecode = '").append(provincecode).append("'").append(" order by adminnamep");
		int count = SaveManager.getConditionCount(sql.toString());
		City[] citys=new City[count];
		for (int i=0;i<count;i++) 
		{
			citys[i] =new City();
		}
		SaveManager.readConditionData(sql.toString(),citys);
		List<City> cityList = Arrays.asList(citys);
		return cityList;
	}
	private class LetterListViewListener implements OnTouchingLetterChangedListener
	{

		@Override
		public void onTouchingLetterChanged(final String s) {
			int position = 0;
			for (City city : mCitys) 
			{
				if (s.equals(city.adminnamep.substring(0, 1).toUpperCase())) 
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

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			mCommonApplication.mIsBack = true;
		}
		return super.onKeyDown(keyCode, event);
	}
    
    
}
