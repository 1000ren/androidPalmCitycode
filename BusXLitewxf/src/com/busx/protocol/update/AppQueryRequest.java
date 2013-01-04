package com.busx.protocol.update;

import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

public class AppQueryRequest extends BaseHttpRequest {

	public AppQueryRequest(String absoluteURI)
	{
		setAbsoluteURI(absoluteURI);
		setMethod(GET);
	}

	@Override
	public BaseHttpResponse createResponse()
	{
		return new AppQueryResponse();
	}
}
