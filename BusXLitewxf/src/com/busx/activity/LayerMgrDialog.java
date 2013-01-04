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
import com.busx.entities.LayerItem;

public class LayerMgrDialog extends Dialog 
							implements OnItemClickListener, OnItemSelectedListener
{
	@SuppressWarnings("unused")
	private Context context;
	private LayerMgrAdapter mAdapter;
	protected OnLMListItemClick mOnClickListener;
	private List<LayerItem> mLayerItems;

	public LayerMgrDialog(Context context) 
	{
		this(context,android.R.style.Theme_Dialog);
	}

	public LayerMgrDialog(Context context, int theme)
	{
		super(context,theme);
	}

	public LayerMgrDialog(Context context,
			List<LayerItem> layerItems)
	{
		this(context,android.R.style.Theme_Dialog);
		this.mLayerItems=layerItems;
		this.context=context;
		mAdapter=new LayerMgrAdapter(context,mLayerItems);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dlg_layermgr );
		ListView listView=(ListView) findViewById(R.id.ListView_layers);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id)
			{
				dismiss();
				mOnClickListener.onListItemClick(LayerMgrDialog.this,mLayerItems.get(position));
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

	interface OnLMListItemClick
	{
		public void onListItemClick(LayerMgrDialog layerMgrDialog,
				LayerItem layerItem);
	}
	
	public void setOnListClickListener(OnLMListItemClick l)
	{
		mOnClickListener = l;
	}
}
