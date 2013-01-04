package com.busx.entities;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class POIItem implements Serializable
{
	private static final long serialVersionUID = 10L;
	
	public String id;
	public String name;
	public String addr;
	public GPoint gPoint;
	public String cat;//subwaystop、busstop
    public String admincode;
    public String adminname;
    public String score;
    public String tel;
    public List<BusLine> busline;
    public String stopid;
    //弹出对话框用
    public String[] buslinename_dialog;
    //列出 经过线路 用
    public String buslinename="";
    // 当为周边查询时有效
    public double angle = 0.0;
    
    //0 - 分享给好友  1 - 打电话/沿途公交
    public List<String> detail;
    
    //收藏标识
    public boolean favoriteflag = false; 
    
    //封装成json
    public JSONObject packageJson()
    {
    	JSONObject jo = new JSONObject();
    	try 
    	{
    		jo.put("id", id==null?"":id);
    		jo.put("name", name==null?"":name);
    		if(null != gPoint)
    		{
    			jo.put("lon", gPoint.lon);
        		jo.put("lat", gPoint.lat);
    		}
    		else
    		{
    			jo.put("lon", 0);
        		jo.put("lat", 0);
    		}
    		jo.put("cat", cat==null?"":cat);
    		jo.put("admincode", admincode==null?"":admincode);
    		jo.put("adminname", adminname==null?"":adminname);
    		jo.put("score", score==null?"":score);
    		jo.put("tel", tel==null?"":tel);
    		jo.put("addr", addr==null?"":addr);
    		jo.put("stopid", stopid==null?"":stopid);
		} 
    	catch(JSONException e)
    	{
    		Log.d("packageJson",e.getMessage());
    	}
    	return jo;
    }
    
    
    public POIItem setJSONObjectToObject(JSONObject jsonObj)
    {
    	POIItem mPOIItem = new POIItem();
    	try
    	{
    		mPOIItem.id = jsonObj.getString("id");
    		mPOIItem.name = jsonObj.getString("name");
    		mPOIItem.gPoint = new GPoint();
    		mPOIItem.gPoint.lon = jsonObj.getDouble("lon");
    		mPOIItem.gPoint.lat = jsonObj.getDouble("lat");
    		mPOIItem.cat = jsonObj.getString("cat");
    		mPOIItem.admincode = jsonObj.getString("admincode");
    		mPOIItem.adminname = jsonObj.getString("adminname");
    		mPOIItem.score = jsonObj.getString("score");
    		mPOIItem.tel = jsonObj.getString("tel");
    		mPOIItem.addr = jsonObj.getString("addr");
    		mPOIItem.stopid = jsonObj.getString("stopid");
    		
    	}
    	catch(JSONException e)
    	{
    		Log.d("packageJson",e.getMessage());
    	}
    	
    	return mPOIItem;
    }
   
}
