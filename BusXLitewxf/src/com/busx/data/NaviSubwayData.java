package com.busx.data;

import java.util.Hashtable;

import android.content.ContentValues;
import android.database.Cursor;

import com.busx.database.SaveDataListener;

public class NaviSubwayData implements SaveDataListener {
	
	public final static String CRSTOPID="CRSTOPID";
	public final static String NAMEC = "stopname";
	
	public String stopid;
	public String name;

	@Override
	public Hashtable<String, String> onCreateTable() {
		return null;
	}

	@Override
	public String onFilterData() {
		return null;
	}

	@Override
	public ContentValues onSaveData() throws Exception {
		return null;
	}

	@Override
	public void onReadData(Cursor oCursor) throws Exception {
		if (oCursor.getColumnIndex(CRSTOPID) != -1) 
		{
			stopid = oCursor.getString(oCursor.getColumnIndex(CRSTOPID));
			stopid = stopid==null ? null :stopid.trim();
		}

		if (oCursor.getColumnIndex(NAMEC) != -1) 
		{
			name = oCursor.getString(oCursor.getColumnIndex(NAMEC));
			name = name==null ? null :name.trim();
		}
	}

}
