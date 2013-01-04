package com.busx.protocol.path;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.busx.entities.Param;
import com.busx.protocol.ProtocolDef;

import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

public class GetRouteBusResultRecommendRequest extends BaseHttpRequest {
	
	private List<Param> params = new ArrayList<Param>();
	
	public GetRouteBusResultRecommendRequest( String sid, String uid, String startid, String endid, String content, String startlatlon, String endlatlon )
	{
		params.add(new Param("startid",startid));
		params.add(new Param("endid",endid));
		params.add(new Param("sid",sid));
		params.add(new Param("content",content));
		params.add(new Param("uid",uid));
		params.add(new Param("startlatlon",startlatlon));
		params.add(new Param("endlatlon",endlatlon));
		
		setMethod(POST);
		String urlString;
		urlString = ProtocolDef.ABSOLUTEURI + ProtocolDef.METHOD_BUSPATH_RECOMMEND;

		Log.d( "BUSX", urlString );
		setAbsoluteURI( urlString );
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		
		return new GetRouteBusResultRecommendResponse();
	}
	
	@Override
	public List<NameValuePair> getPostParams() 
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(Param param:this.params){
			params.add(new BasicNameValuePair(param.key,param.value));
		}
		return params;
	}

}
