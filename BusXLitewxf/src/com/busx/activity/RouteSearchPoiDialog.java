package com.busx.activity;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import com.busx.R;
import com.busx.entities.POIItem;

public class RouteSearchPoiDialog extends Dialog 
								implements OnItemClickListener, OnItemSelectedListener
{
	@SuppressWarnings("unused")
	private Context context;
	private List<POIItem> poiItems;
	private RouteSearchPoiAdapter adapter;
	protected OnRSPListItemClick mOnClickListener;

	public RouteSearchPoiDialog(Context context)
	{
		this(context,android.R.style.Theme_Dialog);
	}

	public RouteSearchPoiDialog(Context context,int theme)
	{
		super(context,theme);
	}

	public RouteSearchPoiDialog(Context context,
			List<POIItem> poiItems)
	{
		this(context,android.R.style.Theme_Dialog);
		this.poiItems=poiItems;
		this.context=context;
		adapter=new RouteSearchPoiAdapter(context,poiItems);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_routesearch_list_poi);
		ListView listView=(ListView) findViewById(R.id.ListView_route_search_list_poi);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				dismiss();
				mOnClickListener.onListItemClick(RouteSearchPoiDialog.this,poiItems.get(position));
				}
		});
	}
	
	
	
	@Override
	public void onItemClick(AdapterView<?> view, View view1, int arg2, long arg3)
	{
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{
	}
	
    interface OnRSPListItemClick {
        public void onListItemClick(RouteSearchPoiDialog dialog,POIItem item);
    }
   
    
    public void setOnListClickListener(OnRSPListItemClick l) {
        mOnClickListener = l;
    }


}
