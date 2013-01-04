package com.busx.protocol.poi;

import java.util.List;

import org.apache.http.NameValuePair;

import android.util.Log;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.busx.protocol.ProtocolDef;

public class GetPoiListRequest extends BaseHttpRequest 
{
	private String firstUrl = ProtocolDef.ABSOLUTEURI  ;
	public GetPoiListRequest( String sid, String keyword, int startrow, int pagenum )
	{
		setMethod(GET);
		String urlString = firstUrl+ ProtocolDef.METHOD_POI_QUERY+"&key="+ keyword + "&num=" + pagenum +
				"&startrow="+ startrow +"&sid=" + sid;

		Log.e( "BUSX", urlString );
		setAbsoluteURI( urlString );
	}
	
	public GetPoiListRequest( String id,String sid)
	{
		setMethod(GET);
		String urlString = firstUrl+ProtocolDef.METHOD_POI_BYID_QUERY + "?poiid="+ id+"&sid="+sid;

		Log.e( "BUSX", urlString );
		setAbsoluteURI( urlString );
	}
	
	@Override
	public BaseHttpResponse createResponse()
	{
		return new GetPoiListResponse();
	}

	@Override
	public List<NameValuePair> getPostParams() 
	{
		return null;
	}
}
