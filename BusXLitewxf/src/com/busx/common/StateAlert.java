package com.busx.common;

import java.util.Hashtable;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import cm.framework.net.ControlRunnable;
import cm.framework.net.IErrorListener;
import cm.framework.net.INetStateListener;
import cm.framework.protocol.BaseHttpRequest;
import cm.framework.protocol.BaseJSONRequest;
import cm.framework.protocol.BaseXMLRequest;
import cm.framework.protocol.ErrorResponse;

import com.busx.R;

/**
 * 功能：状态提醒类 包括: 验证输入框内容， 网络连接状态， 请求返回错误的提示
 * 
 * @author gyx
 * 
 */
public class StateAlert implements INetStateListener, IErrorListener {
	// 等待窗口处理
	protected WaitHandler waitHandler = new WaitHandler();
	// 提示窗口处理
	protected AlertHandler alertHandler = new AlertHandler();
	// 等待窗口
	public WaitDialog dlgWait = null;

	public Context context;
	// 错误信息
	private String errMsg = "";
	// 等待信息
	private String waitMsg = "请稍候...";

	private String dialogTitle = "";

	public StateAlert(Context context) {

		this.context = context;

		init();

	}

	public StateAlert(Context context, String waitMsg) {
		this.context = context;
		this.dialogTitle = waitMsg;

		init();
	}

	private void init()
	{
		dlgWait = new WaitDialog(context, this.dialogTitle);
	}

	private void hideWait() {
		try {
			if (dlgWait != null)
				dlgWait.hide();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cancel()
	{
		dlgWait.dismiss();
	}

	@Override
	public void onStartConnect(BaseHttpRequest request,
			ControlRunnable currentThread) {

		dlgWait.setControlRunnable(currentThread);

		if (request instanceof BaseXMLRequest) {
			waitMsg = hintHashs.get(((BaseXMLRequest) request).getRequestId());
		} else if (request instanceof BaseJSONRequest) {
			waitMsg = hintHashs.get(((BaseJSONRequest) request).getRequestId());
		}

		waitHandler.sendEmptyMessage(1);

	}

	public void showWaitDialog(String showMsg) {
		waitMsg = showMsg;
		waitHandler.sendEmptyMessage(1);
	}

	public void hidenWaitDialog() {
		waitHandler.sendEmptyMessage(0);
	}

	@Override
	public void onCancel(BaseHttpRequest request, ControlRunnable currentThread) {

	}

	@Override
	public void onConnected(BaseHttpRequest request,
			ControlRunnable currentThread) {

	}

	@Override
	public void onNetError(BaseHttpRequest request,
			ControlRunnable currentThread, ErrorResponse errorInfo) {
		waitHandler.sendEmptyMessage(0);

	}

	@Override
	public void onRecv(BaseHttpRequest request, ControlRunnable currentThread,
			int len) {

	}

	@Override
	public void onRecvFinish(BaseHttpRequest request,
			ControlRunnable currentThread) {
		waitHandler.sendEmptyMessage(0);
	}

	@Override
	public void onSend(BaseHttpRequest request, ControlRunnable currentThread,
			int len) {

	}

	@Override
	public void onSendFinish(BaseHttpRequest request,
			ControlRunnable currentThread) {

	}

	@Override
	public void onStartRecv(BaseHttpRequest request,
			ControlRunnable currentThread, int totalLen) {

	}

	@Override
	public void onStartSend(BaseHttpRequest request,
			ControlRunnable currentThread, int totalLen) {

	}

	@Override
	public void onError(ErrorResponse errorResponse, BaseHttpRequest request,
			ControlRunnable currentThread) {

		if (errorResponse == null) {
			waitHandler.sendEmptyMessage(0);
			return;
		}

		errMsg = getErrorDesc(errorResponse);
		alertHandler.sendEmptyMessage(0);

	}

	/**
	 * 功能：根据请求返回的errorResponse取得错误提示信息
	 * 
	 * @param errorResponse
	 * @return
	 */
	public String getErrorDesc(ErrorResponse errorResponse) {

		if (errorResponse.getErrorType() == ErrorResponse.ERROR_NULL_RESULT) {
			return getResString(R.string.err_null);
		} else if (errorResponse.getErrorType() == ErrorResponse.ERROR_INVALID_RESULT) {
			return errorResponse.getErrorDesc();
		} else if ((errorResponse.getErrorType() >= ErrorResponse.ERROR_NET_NO_CONNECTION)
				&& (errorResponse.getErrorType() <= ErrorResponse.ERROR_NET_TIMEOUT)) {
			return getResString(R.string.err_net);
		} else if (errorResponse.getErrorType() == ErrorResponse.ERROR_CLIENT_NET_DISCONNECTED) {
			return getResString(R.string.err_client_net);
		} else if (errorResponse.getErrorType() == ErrorResponse.ERROR_SERVER) {
			return getResString(R.string.err_server);
		} else if (errorResponse.getErrorDesc().equals(String.valueOf(ErrorResponse.ERROR_LOGIN_OVERTIME))) {
			return getResString(R.string.err_timeout);
		} else {
			return errorResponse.getErrorDesc();
		}
	}

	/**
	 * 功能：根据资源ID获得名称
	 * 
	 * @param res_string_id
	 *            资源ID
	 * @return 名称
	 */
	public String getResString(int res_string_id) {
		return context.getResources().getString(res_string_id);
	}

	public void setTitle(String title) {
		dialogTitle = title;
		dlgWait.setTitle(dialogTitle);
	}

	public void showAlert(int res_string_id)
	{
		showAlert(getResString(res_string_id));
	}

	public void showAlert(String msg) {
		 AlertDialog alert = new AlertDialog.Builder(context).create();
		 alert.setTitle(getResString(R.string.app_name));
		 alert.setMessage(msg);
		 alert.setIcon(android.R.drawable.ic_dialog_alert);
		 alert.setButton(AlertDialog.BUTTON_POSITIVE, "确定", (Message) null);
		 alert.show();
	}

	/**
	 * 等待窗口的处理类
	 * 
	 * @author gyx
	 * 
	 */
	class WaitHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 0)
				hideWait();
			else {
				if (dlgWait != null)
					dlgWait.show(waitMsg);
			}
		}
	};

	/**
	 * 提示窗口的处理类
	 * 
	 * @author gyx
	 * 
	 */
	class AlertHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (dlgWait.isShowing()) {
				dlgWait.hide();
			}
			showAlert(errMsg);
		}
	};

	static private Hashtable<String, String> hintHashs = new Hashtable<String, String>();
	static {
		hintHashs.put("1000000", "请稍候...");

	}

}
