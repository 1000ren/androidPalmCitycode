package com.busx.protocol.trafficEvent;

import java.util.List;
import org.apache.http.NameValuePair;
import android.util.Log;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;
import com.busx.protocol.ProtocolDef;

public class GetTrafficEventRequest extends BaseHttpRequest 
{

	public GetTrafficEventRequest( int start, int rows,String sid, String admincode)
	{
		setMethod(GET);
		String urlString;
		urlString = ProtocolDef.ABSOLUTEURI + ProtocolDef.METHOD_TRAFFICEVENT + 
				"&start=" + start + "&rows=" + rows+ "&admincode=" + admincode+ "&sid=" + sid;
		Log.e( "BUSX", urlString );
		setAbsoluteURI( urlString );
	
	}
	
	@Override
	public BaseHttpResponse createResponse()
	{
		return new GetTrafficEventResponse();
	}

	@Override
	public List<NameValuePair> getPostParams() 
	{
		return null;

	}
}
