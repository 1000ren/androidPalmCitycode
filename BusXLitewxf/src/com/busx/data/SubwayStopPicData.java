package com.busx.data;

import java.util.Hashtable;

import com.busx.database.SaveDataListener;
import com.busx.database.SaveManager;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 为了保存检索出来的数据
 * @author wangxufei
 * @date
 *
 */
public class SubwayStopPicData implements SaveDataListener {
	/* 站点ID */
	private static final String CRSTOPID = "CRSTOPID";
	/* 图片名称*/
	private static final String CRSTOPPICNAME = "CRSTOPPICNAME";
	/* 图片类型 */
	private static final String CRSTOPTYPE = "CRSTOPTYPE";
	/* 图片时间戳 */
	private static final String CRSTOPDATA = "CRSTOPDATA";
	
	/** 站点ID对应的值 */
	private String crstopid = "";
	/** 图片名称对应的值*/
	private String crstoppicName = "";
	/** 图片类型对应的值 */
	private String crstoptype = "";
	/** 图片时间戳 对应的值*/
	private String crstopdata = "";
	
	/** 返回数据状态*/
	private String statue = "";

	@Override
	public Hashtable<String, String> onCreateTable() {
		// TODO Auto-generated method stub
		Hashtable<String,String> ohKey = new Hashtable<String,String>();
		ohKey.put(CRSTOPID, SaveManager.TYPE_TEXT);
		ohKey.put(CRSTOPPICNAME, SaveManager.TYPE_TEXT);
		ohKey.put(CRSTOPTYPE, SaveManager.TYPE_TEXT);
		ohKey.put(CRSTOPDATA, SaveManager.TYPE_TEXT);
		return ohKey;
	}

	@Override
	public String onFilterData() {
		String sSql = CRSTOPPICNAME + " = '" + crstoppicName + "' and "+CRSTOPTYPE+" = '"+ crstoptype +"'";
		return sSql;
	}

	@Override
	public ContentValues onSaveData() throws Exception {
		ContentValues oValues = new ContentValues();
		oValues.put(CRSTOPID, crstopid);
		oValues.put(CRSTOPPICNAME, crstoppicName);
		oValues.put(CRSTOPTYPE, crstoptype);
		oValues.put(CRSTOPDATA, crstopdata);
		return oValues;
	}

	@Override
	public void onReadData(Cursor oCursor) throws Exception {
		// TODO Auto-generated method stub
		if (oCursor.getColumnIndex(CRSTOPID) != -1) {
			// 站点ID
			byte crstopids[] = oCursor.getBlob(oCursor.getColumnIndex(CRSTOPID));
			if (crstopids != null) {
				crstopid = new String(crstopids, "UTF-8");
			}
		}
		
		if (oCursor.getColumnIndex(CRSTOPPICNAME) != -1) {
			// 图片名称
			byte crstoppicNames[] = oCursor.getBlob(oCursor.getColumnIndex(CRSTOPPICNAME));
			if (crstoppicNames != null) {
				crstoppicName = new String(crstoppicNames, "UTF-8");
			}
		}
		
		if (oCursor.getColumnIndex(CRSTOPTYPE) != -1) {
			// 图片类型
			byte crstoptypes[] = oCursor.getBlob(oCursor.getColumnIndex(CRSTOPTYPE));
			if (crstoptypes != null) {
				crstoptype = new String(crstoptypes, "UTF-8");
			}
		}
		
		if (oCursor.getColumnIndex(CRSTOPDATA) != -1) {
			// 图片时间戳
			byte crstopdatas[] = oCursor.getBlob(oCursor.getColumnIndex(CRSTOPDATA));
			if (crstopdatas != null) {
				crstopdata = new String(crstopdatas, "UTF-8");
			}
		}

	}

	public String getCrstopid() {
		return crstopid;
	}

	public void setCrstopid(String crstopid) {
		this.crstopid = crstopid;
	}

	public String getCrstoptype() {
		return crstoptype;
	}

	public void setCrstoptype(String crstoptype) {
		this.crstoptype = crstoptype;
	}

	public String getCrstopdata() {
		return crstopdata;
	}

	public void setCrstopdata(String crstopdata) {
		this.crstopdata = crstopdata;
	}

	public String getStatue() {
		return statue;
	}

	public void setStatue(String statue) {
		this.statue = statue;
	}
	
	public String getCrstoppicName() {
		return crstoppicName;
	}

	public void setCrstoppicName(String crstoppicName) {
		this.crstoppicName = crstoppicName;
	}
	
	

}
