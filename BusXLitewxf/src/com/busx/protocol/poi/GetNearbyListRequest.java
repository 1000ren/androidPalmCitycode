package com.busx.protocol.poi;

import java.util.List;

import org.apache.http.NameValuePair;

import android.util.Log;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.busx.protocol.ProtocolDef;

public class GetNearbyListRequest extends BaseHttpRequest 
{
	public GetNearbyListRequest( String sid, String latlon,String cat,String dis, int page, int pagenum )
	{
		dis=dis.substring(0, dis.length()-1);
		dis=String.valueOf(Float.parseFloat(dis)/1000);

		setMethod(GET);
		String urlString;
		urlString = ProtocolDef.ABSOLUTEURI + ProtocolDef.METHOD_AROUNDPOI_QUERY + 
				"&latlon="+ latlon + "&cat=" + cat+"&dis="+dis+"&page="+page+"&num="+pagenum;
		urlString += "&sid=" + sid;

		Log.d( "BUSX", urlString );
		setAbsoluteURI( urlString );
	}

	@Override
	public BaseHttpResponse createResponse()
	{
		return new GetNearbyListResponse();
	}

	@Override
	public List<NameValuePair> getPostParams() 
	{
		return null;
	}
}
