package com.busx.location;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public class PhoneLocationManager {

	protected Activity context;
	private LocationManager locationManager;
	private LocationListener locationListener;
	public static final String GPS_LOCATION_ACTION = "GPS_LOCATION_ACTION";
	
	private int mcc = 30;
	private int mnc = 100;
	public PhoneLocationManager(Activity context) {
		this.context = context;
	}

	// 设置GPS定位侦听
	public void setUpGPSListener() {
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onLocationChanged(Location l) {
				MLocation mlocation = new MLocation(l.getLatitude(),
						l.getLongitude());
				sendBroadcast(mlocation);

			}
		};
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				100, locationListener);

	}

	// 设置基站定位侦听
	public void setUpApnListener() {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if(null != tm && !tm.getNetworkOperator().equals("")){
			mcc = Integer
					.valueOf(tm.getNetworkOperator().substring(0, 3));
			mnc = Integer
					.valueOf(tm.getNetworkOperator().substring(3, 5));
		}
		
		PhoneStateListener listener = new PhoneStateListener() {
			@Override
			public void onCellLocationChanged(CellLocation location) {
				super.onCellLocationChanged(location);
				try {
					GsmCellLocation gcl = (GsmCellLocation) location;
					JSONObject holder = new JSONObject();
					holder.put("version", "1.1.0");
					holder.put("host", "maps.google.com");
					holder.put("address_language", "zh_CN");
					holder.put("request_address", true);
					int cid = gcl.getCid();
					int lac = gcl.getLac();
					JSONArray array = new JSONArray();
					JSONObject data = new JSONObject();
					data.put("cell_id", cid);
					data.put("location_area_code", lac);
					data.put("mobile_country_code", mcc);
					data.put("mobile_network_code", mnc);
					array.put(data);
					holder.put("cell_towers", array);

					HttpClient httpClient = new DefaultHttpClient();

					HttpConnectionParams.setConnectionTimeout(
							httpClient.getParams(), 20 * 1000);
					HttpConnectionParams.setSoTimeout(httpClient.getParams(),
							20 * 1000);

					HttpPost post = new HttpPost(
							"http://74.125.71.147/loc/json");
					// 设置代理
					String host = android.net.Proxy.getDefaultHost();// 通过andorid.net.Proxy可以获取默认的代理地址
					int port = android.net.Proxy.getDefaultPort();// 通过andorid.net.Proxy可以获取默认的代理端口
					if (host != null && host.trim().length() > 0) {
						HttpHost proxy = new HttpHost(host, port);
						httpClient.getParams().setParameter(
								ConnRouteParams.DEFAULT_PROXY, proxy);
					}
					StringEntity se = new StringEntity(holder.toString());
					post.setEntity(se);
					HttpResponse response = httpClient.execute(post);
					MLocation mlocation = transResponse(response);
					sendBroadcast(mlocation);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		tm.listen(listener, PhoneStateListener.LISTEN_CELL_LOCATION);
	}

	public void release() {
		if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
		}
	}

	private MLocation transResponse(HttpResponse response)
	{
		MLocation location = null;
		if (response.getStatusLine().getStatusCode() == 200) {

			HttpEntity entity = response.getEntity();
			BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(
						entity.getContent()));
				StringBuffer sb = new StringBuffer();
				String result = br.readLine();
				while (result != null) {
					sb.append(result);
					result = br.readLine();
				}
				JSONObject json = new JSONObject(sb.toString());
				JSONObject lca = json.getJSONObject("location");
				location = new MLocation(lca.getDouble("latitude"),
						lca.getDouble("longitude"));

			} catch (Exception e) {
				e.printStackTrace();
				location = null;
			}
		}
		return location;
	}

	private void sendBroadcast(MLocation mlocation) {
		Intent i = new Intent(GPS_LOCATION_ACTION);
		i.putExtra("mlocation", mlocation);
		context.sendBroadcast(i);
	}

	public static class MLocation implements Serializable 
	{
		private static final long serialVersionUID = 1L;

		public double Latitude;
		public double Longitude;

		public MLocation(double latitude, double longitude)
		{
			super();
			Latitude = latitude;
			Longitude = longitude;
		}
	}

}
