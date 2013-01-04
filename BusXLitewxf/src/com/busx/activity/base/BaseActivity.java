package com.busx.activity.base;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import cm.framework.net.ClientSession;

import com.busx.CommonApplication;
import com.busx.R;
import com.busx.common.StateAlert;
import com.busx.utils.ClientAgent;

/**
 * activity基类
 * 
 * @author liujingzhou
 * 
 */
public class BaseActivity extends Activity {

	// common data
	protected Context mContext = null;
	// 状态提示
	protected StateAlert mStateAlert = null;
	// application
	protected CommonApplication mCommonApplication;

	protected ClientAgent mClientAgent = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;

		getWindow().setFormat(PixelFormat.RGBA_8888);
		mStateAlert = new StateAlert(this);
		mStateAlert.setTitle( getResString(R.string.app_name) );

		mCommonApplication = (CommonApplication)getApplicationContext();

		mClientAgent = new ClientAgent(BaseActivity.this,mStateAlert);
	}

  
	@Override
	protected void onResume()
	{
		super.onResume();
		ClientSession.getInstance().setDefErrorReceiver(mStateAlert);
		ClientSession.getInstance().setDefStateReceiver(mStateAlert);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mStateAlert.cancel();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		ClientSession.getInstance().setDefErrorReceiver(mStateAlert);
		ClientSession.getInstance().setDefStateReceiver(mStateAlert);
	}

	@Override
	public void finish()
	{
		super.finish();
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_HOME
                && event.getRepeatCount() == 0)
        {
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }

	/**
	 * 功能：设置标题
	 * 
	 * @param title
	 */
	public void setActivityTitle(String title) {

		this.setTitle(title);
	}

	/**
	 * 功能：根据资源ID获得名称
	 * 
	 * @param res_string_id
	 *            资源ID
	 * @return 名称
	 */
	public String getResString(int res_string_id) {
		return mContext.getResources().getString(res_string_id);
	}

	/**
	 * 功能：获取数据从Intent当中, 存储格式(key, value)
	 * 
	 * @param name
	 *            is key
	 * @return value
	 */
	public Serializable getDataFromIntent(String name) {
		Intent it = getIntent();
		if (it != null) {
			return getIntent().getSerializableExtra(name);
		} else {
			return null;
		}

	}
	
	public void showToast(String showString)
	{
		Toast.makeText(getApplicationContext(), showString, Toast.LENGTH_SHORT)
				.show();
	}

	public void showToast(int resId)
	{
		Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT)
		.show();
	}


}
