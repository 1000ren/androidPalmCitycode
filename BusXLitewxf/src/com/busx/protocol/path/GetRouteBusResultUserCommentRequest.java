package com.busx.protocol.path;

import android.util.Log;

import com.busx.protocol.ProtocolDef;

import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

public class GetRouteBusResultUserCommentRequest extends BaseHttpRequest{
	
	

	public GetRouteBusResultUserCommentRequest(String sid,String pathcomid,int evaluate)
	{
		setMethod(GET);
		String urlString;
		urlString = ProtocolDef.ABSOLUTEURI + ProtocolDef.METHOD_BUSPATH_USER_COMMENT;
		urlString += "&sid=" + sid + "&compathid=" + pathcomid +"&evaluate=" +evaluate;
		
		Log.e( "BUSX", urlString );
		setAbsoluteURI( urlString );
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new GetRouteBusResultUserCommentResponse();
	}

}
