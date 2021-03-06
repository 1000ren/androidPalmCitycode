package com.busx.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class NetUtil {
	private static final String TAG = "NetUtil";
	
	/**
	 * 判断是否可以连接网络
	 * @param context
	 * @return
	 */
	public static boolean isConnectingToInternet(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	/**
	 * 根据URL，判断是否连接网络
	 */
	public static boolean isConnnected(String url) {  
		HttpPost request = new HttpPost(url);
		//设置超时
		DefaultHttpClient oHttpClient = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(oHttpClient.getParams(), 3000);
		try {
			HttpResponse httpResponse = oHttpClient.execute(request);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK == statusCode) {
				return true;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
        return false;  
    }

	/**
	 * 网络可用状态下，通过get方式向server端发送请求，并返回响应数据
	 * 
	 * @param strUrl 请求网址
	 * @param context 上下文
	 * @return 响应数据
	 */
	public static JSONObject getResponseForGet(String strUrl, Context context) {
		if (isConnnected(strUrl)) {
			return getResponseForGet(strUrl);
		}
		return null;
	}

	/**
	 * 通过Get方式处理请求，并返回相应数据
	 * 
	 * @param strUrl 请求网址
	 * @return 响应的JSON数据
	 */
	public static JSONObject getResponseForGet(String strUrl) {
		HttpGet httpRequest = new HttpGet(strUrl);
		return getRespose(httpRequest);
	}

	/**
	 * 网络可用状态下，通过post方式向server端发送请求，并返回响应数据
	 * 
	 * @param market_uri 请求网址
	 * @param nameValuePairs 参数信息
	 * @param context 上下文
	 * @return 响应数据
	 */
	public static JSONObject getResponseForPost(String market_uri, List<NameValuePair> nameValuePairs, Context context) {
		if (isConnnected(market_uri)) {
			return getResponseForPost(market_uri, nameValuePairs);
		}
		return null;
	}

	/**
	 * 通过post方式向服务器发送请求，并返回响应数据
	 * 
	 * @param strUrl 请求网址
	 * @param nameValuePairs 参数信息
	 * @return 响应数据
	 */
	public static JSONObject getResponseForPost(String market_uri, List<NameValuePair> nameValuePairs) {
		if (null == market_uri || "" == market_uri) {
			return null;
		}
		HttpPost request = new HttpPost(market_uri);
		try {
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			return getRespose(request);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 响应客户端请求
	 * 
	 * @param request 客户端请求get/post
	 * @return 响应数据
	 */
	public static JSONObject getRespose(HttpUriRequest request) {
		try {
			HttpResponse httpResponse = new DefaultHttpClient().execute(request);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK == statusCode) {
				String result = EntityUtils.toString(httpResponse.getEntity());
				Log.i(TAG, "results=" + result);
				return new JSONObject(result);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
     * 查看GPS是否开启
     */
    public static void openGPSSettings(Context context)
    {
	    LocationManager alm = (LocationManager)context.getSystemService( Context.LOCATION_SERVICE );
	    if( !alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ) )
	    {
	    	Toast.makeText(context, "GPS未打开", Toast.LENGTH_SHORT ).show();
	    }
	    else
	    {
	    	Toast.makeText(context, "定位中，请稍后...", Toast.LENGTH_SHORT).show();
	    }
    }
	
}
