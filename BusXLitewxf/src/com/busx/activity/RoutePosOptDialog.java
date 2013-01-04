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
import com.busx.entities.RoutePosOptItem;

public class RoutePosOptDialog extends Dialog 
							implements OnItemClickListener, OnItemSelectedListener
{
	@SuppressWarnings("unused")
	private Context context;
	private RoutePosOptAdapter mAdapter;
	protected OnOptListItemClick mOnClickListener;
	private List<RoutePosOptItem> mRoutePosOptItems;

	public RoutePosOptDialog(Context context) 
	{
		this(context,android.R.style.Theme_Dialog);
	}

	public RoutePosOptDialog(Context context, int theme)
	{
		super(context,theme);
	}

	public RoutePosOptDialog(Context context,
			List<RoutePosOptItem> optItems)
	{
		this(context,android.R.style.Theme_Dialog);
		this.mRoutePosOptItems=optItems;
		this.context=context;
		mAdapter=new RoutePosOptAdapter(context,mRoutePosOptItems);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_routeposopt );
		ListView listView=(ListView) findViewById(R.id.ListView_opts);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id)
			{
				dismiss();
				mOnClickListener.onListItemClick(RoutePosOptDialog.this,mRoutePosOptItems.get(position));
			}
		});
	}
	
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3)
	{
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
	}

	interface OnOptListItemClick
	{
		public void onListItemClick(RoutePosOptDialog optDialog,
				RoutePosOptItem optItem);
	}
	
	public void setOnListClickListener(OnOptListItemClick l)
	{
		mOnClickListener = l;
	}
}
