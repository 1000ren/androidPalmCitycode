package com.busx.protocol.update;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cm.framework.net.ControlRunnable;
import cm.framework.net.INetStateListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseHttpResponse;
import cm.framework.protocol.ErrorResponse;

public class AppQueryResponse implements BaseHttpResponse
{
	private InputStream inputStream = null;

	private ByteArrayOutputStream memoryStream;

	public AppQueryResponse()
	{
	}

	@Override
	public InputStream getInputStream()
	{
		return inputStream;
	}

	@Override
	public ErrorResponse parseInputStream(ControlRunnable currentThread, 
			BaseHttpRequest request, InputStream inputStream, int len, 
			INetStateListener stateReceiver) 
			throws IOException
	{
		this.inputStream = inputStream;

		int step = 100;
		int count = 0;
		synchronized (this) {
			memoryStream = new ByteArrayOutputStream(len);
			int ch;
			while ((ch = inputStream.read()) != -1) {
				memoryStream.write(ch);
				++count;
				if ((count == step) && (stateReceiver != null)) {
					stateReceiver.onRecv(request, currentThread, count);
					count = 0;
				}
			}
			if ((count != 0) && (stateReceiver != null)) {
				stateReceiver.onRecv(request, currentThread, count);
			}
		}

		return null;
	}

	public final byte[] getBytes() {
		if (memoryStream == null) {
			throw new IllegalStateException("you can't call this method until getting response!");
		}
		return memoryStream.toByteArray();
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getResponseContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorResponse parseInputStream(ControlRunnable arg0,
			BaseHttpRequest arg1, String arg2, int arg3, INetStateListener arg4)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
