package com.busx.activity;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.busx.R;
import com.busx.entities.Province;

public class ProvinceAdapter extends BaseAdapter 
{
	private LayoutInflater mInflater = null;
	private List<Province> mProvinceList = null;
	public ProvinceAdapter(Context context,List<Province> provinceList)
	{
		mInflater = LayoutInflater.from(context);
		this.mProvinceList = provinceList;
	}
	@Override
	public int getCount() {
		return mProvinceList.size();
	}

	@Override
	public Object getItem(int position) {
		return mProvinceList.get(position);
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
		Province province = mProvinceList.get(position);
		TextView firstCharTextView=(TextView)convertView.findViewById(R.id.text_first_char_hint);
		String firstChar = province.provincenamep.substring(0,1);
		firstCharTextView.setText(firstChar);
		firstCharTextView.setVisibility(View.VISIBLE);
		
		
		if (position!=0) 
		{
			Province provinceOld = mProvinceList.get(position-1);
			String oldFirstChar = provinceOld.provincenamep.substring(0,1);
				if (firstChar.equals(oldFirstChar)) 
				{
					firstCharTextView.setVisibility(View.GONE);
				}
		}
		TextView nameTextView = (TextView)convertView.findViewById(R.id.province_name);
		nameTextView.setText(province.provincename);
		return convertView;
	}

}
