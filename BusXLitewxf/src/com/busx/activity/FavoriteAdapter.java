package com.busx.activity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.busx.R;
import com.busx.data.FavoritesData;

public class FavoriteAdapter extends BaseAdapter
{
	private LayoutInflater mInflater;
	private List<FavoritesData> mFavorites;
	private WeakReference<Activity> mWeak;
	private Activity mActivity = null;
	private HashMap<Integer, Boolean> isChecked = null;
	private Map<String,String> mCityMap;
	
	public FavoriteAdapter(FavoriteActivity context, List<FavoritesData> favorites,
			HashMap<Integer,Boolean> mMap,Map<String,String> cityMap)
	{
		this.mWeak = new WeakReference<Activity>((Activity)context);
		this.mActivity = this.mWeak.get();
		mInflater = LayoutInflater.from(context);
		this.mFavorites = favorites;
		//初始化多选框
		isChecked = mMap;
		mCityMap = cityMap;
	}
	
	public void setIsChecked(HashMap<Integer, Boolean> isChecked)
	{
		this.isChecked = isChecked;
	}
	
	public void setListData(List<FavoritesData> favorites)
	{
		this.mFavorites = favorites;
	}

	@Override
	public int getCount() {
		return null == mFavorites ? 0 :mFavorites.size();
	}

	@Override
	public Object getItem(int position) {
		return mFavorites.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		if(convertView==null)
		{
			convertView=mInflater.inflate( R.layout.list_items_favorite_reslist, null);
		}
		CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.ItemCheckBox);
		checkBox.setChecked(isChecked.get(position));
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					((FavoriteActivity) mActivity).mMap.put(position, true);
				}
				else
				{
					((FavoriteActivity) mActivity).mMap.put(position, false);
				}
				
			}
		});
		
		TextView buslineName = (TextView) convertView.findViewById(R.id.ItemTitle);
		TextView ridingRoute = (TextView) convertView.findViewById(R.id.ItemText);
		ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.BtnDefault);
		final FavoritesData favorite = mFavorites.get(position);
		buslineName.setText(favorite.mEntityName);
		ridingRoute.setText(mCityMap==null ?"":mCityMap.get(favorite.mCityCode)+" "+favorite.mEntityRidingRoute);
		
		
		imageButton.setTag(favorite);
		imageButton.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				((FavoriteActivity) mActivity).searchResult(favorite,true);
			}
		});
		return convertView;
	}

}
