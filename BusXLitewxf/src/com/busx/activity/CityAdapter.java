package com.busx.activity;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.busx.R;
import com.busx.entities.City;

public class CityAdapter extends BaseAdapter 
{
	private LayoutInflater mInflater = null;
	private List<City> mCityList = null;
	public CityAdapter(Context context,List<City> cityList)
	{
		mInflater = LayoutInflater.from(context);
		this.mCityList = cityList;
	}
	@Override
	public int getCount() {
		return mCityList.size();
	}

	@Override
	public Object getItem(int position) {
		return mCityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if(convertView==null)
		{
			convertView=mInflater.inflate( R.layout.list_items_province, null);
		}
		City city = mCityList.get(position);
		TextView firstCharTextView=(TextView)convertView.findViewById(R.id.text_first_char_hint);
		String firstChar = city.adminnamep.substring(0,1);
		firstCharTextView.setText(firstChar);
		firstCharTextView.setVisibility(View.VISIBLE);
		if (position!=0) 
		{
			City cityOld = mCityList.get(position-1);
				if (firstChar.equals(cityOld.adminnamep.substring(0,1))) 
				{
					firstCharTextView.setVisibility(View.GONE);
				}
		}
		TextView nameTextView = (TextView)convertView.findViewById(R.id.province_name);
		nameTextView.setText(city.adminname);
		return convertView;
	
	}

}
