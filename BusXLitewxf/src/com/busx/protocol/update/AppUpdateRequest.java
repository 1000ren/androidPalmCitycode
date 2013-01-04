package com.busx.protocol.update;

import java.util.List;

import org.apache.http.NameValuePair;

import android.util.Log;

import com.busx.Version;
import com.busx.protocol.ProtocolDef;

import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

public class AppUpdateRequest extends BaseHttpRequest
{
	public AppUpdateRequest( String sid )
	{
		setMethod(GET);
		String urlString;
		urlString = ProtocolDef.ABSOLUTEURI + ProtocolDef.METHOD_APPUPDATE + 
				"&proname=" + Version.proName + 
				"&prover=" + Version.proVer + 
				"&pubdate=" + Version.pubDate;
		urlString += "&sid=" + sid;

		Log.e( "BUSX", urlString );
		setAbsoluteURI( urlString );
	}
	
	@Override
	public BaseHttpResponse createResponse()
	{
		return new AppUpdateResponse();
	}

	@Override
	public List<NameValuePair> getPostParams() 
	{
		return null;
	}

}
