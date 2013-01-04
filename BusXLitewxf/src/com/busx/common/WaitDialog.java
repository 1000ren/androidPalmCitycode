package com.busx.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.KeyEvent;
import cm.framework.net.ClientSession;
import cm.framework.net.ControlRunnable;

import com.busx.R;

/**
 * 功能：等待窗口
 * 
 * @author gyx
 * 
 */
public class WaitDialog extends ProgressDialog {
	// fields
	private String sText = null;
	private String sTitle = "";
	private ControlRunnable currentThread = null;

	// methods
	public WaitDialog(Context context) {
		super(context);
		initialize(context);
	}

	// methods
	public WaitDialog(Context context, String waitMsg) {
		super(context);
		sTitle = waitMsg;
		initialize(context);
	}

	public void setControlRunnable(ControlRunnable currentThread) {
		this.currentThread = currentThread;
	}

	public ControlRunnable getcurrentThread() {

		return currentThread;
	}

	private void initialize(Context context) {
		sText = context.getString(R.string.progress_text);
		if (sTitle.equals("")) {
			setTitle(R.string.progress_title);
		} else {
			setTitle(sTitle);
		}
		setProgressStyle(ProgressDialog.STYLE_SPINNER);
		setIndeterminate(true);
	}

	public void show(String sDialogText) {
		if (sDialogText == null)
			sDialogText = this.sText;
		setMessage(sDialogText);
		try {
			show();
		} catch (Exception e) {
//			e.printStackTrace();
//			Log.e("DIA", e.getMessage());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// do something on back.
			ClientSession.getInstance().cancel(currentThread);
			this.hide();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
}
