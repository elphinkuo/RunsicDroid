package com.runningmusic.network.service;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Toast;

import com.runningmusic.dto.BaseAction;
import com.runningmusic.dto.BaseAction.ActionType;
import com.runningmusic.dto.CirculateAction;
import com.runningmusic.network.http.HttpClientHelper;
import com.runningmusic.network.http.HttpReqResult;
import com.runningmusic.network.http.HttpReqResult.ResultType;

import org.apache.http.HttpEntity;

import java.util.ArrayList;

public class HttpAsyncTaskServices extends AsyncTask<Void, Void, HttpReqResult> {

	private Context context;
	private BaseAction action;
	private AsyncPreCall asyncPreCall;
	private AsyncCallBack asyncCallBack;
	private Dialog progressDialog;
	private boolean showDialog;
	private String showDialogTips;

	public HttpAsyncTaskServices(Context context, BaseAction action, AsyncPreCall asyncPreCall, AsyncCallBack asyncCallBack, boolean showDialog, String showDialogTips) {
		this.context = context;
		this.action = action;
		this.asyncCallBack = asyncCallBack;
		this.asyncPreCall = asyncPreCall;
		this.showDialog = showDialog;
		this.showDialogTips = showDialogTips;
		if (showDialog) {
			progressDialog = getProgressDialog(showDialogTips);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (progressDialog != null)
			progressDialog.show();
		if (asyncPreCall != null) {
			try {
				asyncPreCall.onPreCall();
			} catch (Exception e) {

			}
		}
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected HttpReqResult doInBackground(Void... paramTemp) {
		HttpReqResult result = null;
		try {
			if (action.getType() == ActionType.Normal) {
				result = HttpClientHelper.executePost(action.getRequestUrl().trim(), action.getHttpEntity());
			} else if (action.getType() == ActionType.Circulate) {
				CirculateAction circulateAction = (CirculateAction) action;
				ArrayList<HttpEntity> httpEntityList = circulateAction.getHttpEntityArray();
				for (int i = 0, n = httpEntityList.size(); i < n; i++) {
					HttpEntity httpEntity = httpEntityList.get(i);
					circulateAction.beforeCirculate(Integer.valueOf(i), action, httpEntity);
					result = HttpClientHelper.executePost(action.getRequestUrl().trim(), httpEntity);
					if (ResultType.SUCCESS == result.getResult()) {
						String source = result.getResultMsg();
						try {
							action.setResponseObject(source);
						} catch (Exception e) {
							circulateAction.afterCirculate(Integer.valueOf(i), action, httpEntity);
							break;
						}
						String resultCode = action.getStatus();
						if (!resultCode.equals("ok")) {
							circulateAction.afterCirculate(Integer.valueOf(i), action, httpEntity);
							break;
						}
					} else {
						circulateAction.afterCirculate(Integer.valueOf(i), action, httpEntity);
						break;
					}
					circulateAction.afterCirculate(Integer.valueOf(i), action, httpEntity);
				}
			}
		} catch (Exception e) {
		}
		return result;
	}

	@Override
	protected void onPostExecute(HttpReqResult result) {
		super.onPostExecute(result);
		if (progressDialog != null)
			progressDialog.dismiss();
		if (ResultType.SUCCESS == result.getResult()) {
			String source = result.getResultMsg();
			//showMessageDialog(source);
			try {
				action.setResponseObject(source);
			} catch (Exception e) {
				Toast.makeText(context, "解析数据包异常。", Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				
				String aa = action.getResp().toString();
				//showMessageDialog(""+aa);
				String resultStatus = action.getStatus();
				
				if (resultStatus.equals("OK")) {
					if (asyncCallBack != null) {
						try {
							asyncCallBack.onCallBack(action);
						} catch (Exception e) {
						}
					}
				} else {
					showMessageDialog("网络请求失败");
				}
				
			} catch (Exception e) {
				Toast.makeText(context, "解析数据异常。", Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			Toast.makeText(context, result.getResultMsg(), Toast.LENGTH_SHORT).show();
		}
	}

	private Dialog getProgressDialog(String showDialogTips) {
		Dialog progressDialog = null;
//		progressDialog = new Dialog(context, R.style.ProgressDialog1);
//		View view = LayoutInflater.from(context).inflate(R.layout.progressdialog_indicator, null);
//		AQuery aQuery = new AQuery(view);
//		if (!"".equals(showDialogTips))
//			aQuery.id(R.id.processTxt).text(showDialogTips);
//		progressDialog.setContentView(view);
//		progressDialog.setCancelable(false);
		return progressDialog;
	}

	public void showMessageDialog(String msg) {
		Builder MessageDialog = new Builder(context);
		MessageDialog.setTitle("温馨提示");
		MessageDialog.setMessage(msg);
		MessageDialog.setPositiveButton("确定", new OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
//				Intent intent = new Intent();
//				intent.setClass(context, .class);
//				context.startActivity(intent);
//				((Activity) context).finish();
			}
		});
		MessageDialog.create().show();
	}

}
