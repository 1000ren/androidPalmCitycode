package com.busx.protocol.login;

import java.util.List;

import org.apache.http.NameValuePair;

import android.util.Log;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;

import com.busx.protocol.ProtocolDef;

public class LoginRequest extends BaseHttpRequest 
{

	public LoginRequest( String username, String password )
	{
		setMethod(GET);
		String urlString;
		urlString = ProtocolDef.ABSOLUTEURI + ProtocolDef.METHOD_LOGIN + 
				"&usrname=" + username + 
				"&psw=" + password;

		Log.e( "BUSX", urlString );
		setAbsoluteURI( urlString );
	}

	@Override
	public BaseHttpResponse createResponse()
	{
		return new LoginResponse();
	}

	@Override
	public List<NameValuePair> getPostParams() 
	{
		return null;
	}

}
