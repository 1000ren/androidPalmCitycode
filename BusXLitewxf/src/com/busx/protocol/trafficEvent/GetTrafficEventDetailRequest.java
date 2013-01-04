package com.busx.protocol.trafficEvent;

import java.util.List;
import org.apache.http.NameValuePair;
import android.util.Log;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;
import com.busx.protocol.ProtocolDef;

public class GetTrafficEventDetailRequest extends BaseHttpRequest 
{

	public GetTrafficEventDetailRequest( String eventid, String sid, String admincode)
	{
		setMethod(GET);
		String urlString;
		urlString = ProtocolDef.ABSOLUTEURI + ProtocolDef.METHOD_TRAFFICEVENT + 
				"&eventid=" + eventid + "&sid=" + sid+"&admincode="+admincode;
		Log.e( "BUSX", urlString );
		setAbsoluteURI( urlString );
	
	}
	
	@Override
	public BaseHttpResponse createResponse()
	{
		return new GetTrafficEventDetailResponse();
	}

	@Override
	public List<NameValuePair> getPostParams() 
	{
		return null;

	}
}
