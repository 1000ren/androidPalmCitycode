package com.busx.protocol.poi;

import android.util.Log;

import com.busx.protocol.ProtocolDef;

import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

public class GetPointAllListRequest extends BaseHttpRequest 
{
	public GetPointAllListRequest(String sid, String keyword, int startrow, int pagenum ,boolean isPinYin)
	{
		setMethod(GET);
		String urlString = ProtocolDef.ABSOLUTEURI;
		if(isPinYin)
		{
			urlString += ProtocolDef.METHOD_POI_ALLPP_QUERY;
		}
		else
		{
			urlString += ProtocolDef.METHOD_POI_ALL_QUERY;
		}
		urlString += "&key="+ keyword + "&num=" + pagenum +
				"&startrow="+ startrow +"&sid=" + sid;

		Log.e( "BUSX", urlString );
		setAbsoluteURI( urlString );
	}
	
	@Override
	public BaseHttpResponse createResponse() 
	{
		return new GetPoiListResponse();
	}

}
