package com.funny.developers.baseconnectionlib.asnycktask;

import org.apache.http.HttpResponse;

import com.funny.developers.baseconnectionlib.consts.BaseConsts;
import com.funny.developers.baseconnectionlib.dialog.LoadingFragmentDialog;
import com.funny.developers.baseconnectionlib.httpclient.BaseHttpClient;
import com.funny.developers.baseconnectionlib.httpclient.BaseHttpResponse;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;

public class BaseAsyncTask extends AsyncTask<Integer, Integer, HttpResponse>{
	
	//초기 세팅
	private Context mContext = null;
	private OnRequestInterFace mCallback = null;
	private FragmentManager mFragmentManager = null;
	private int mRequestType = BaseConsts.REQUEST_TYPE_GET;
	private int mResultType = BaseConsts.RESULT_TYPE_JSON;
	
	//로딩 다이얼로그
	private Dialog mLoadingDialog = null;
	private LoadingFragmentDialog mLoadingFragmentDialog = null;
	
	public interface OnRequestInterFace
	{
		//request and result get method
		int getRequestType();
		int getResultType();
		
		//get api request url method
		String onRequestGetUrl();
		
		//get api request parameter method
		String onRequestGetParameter();
		
		void onRequestResult(Object result, int resultType);
		void onRequestError(int errorType);
		void onRequestCancle();
	}
	
	public BaseAsyncTask(Context context){
		mContext = context;
		onPreExecute();
	}
	
	public BaseAsyncTask(Context context, OnRequestInterFace callback) {
		mContext = context;
		mCallback = callback;
		
		onPreExecute();
	}
	
	public BaseAsyncTask(Context context, OnRequestInterFace callback, Dialog loadingDialog) {
		mContext = context;
		mCallback = callback;
		mLoadingDialog = loadingDialog;
		
		onPreExecute();
	}
	
	public BaseAsyncTask(Context context, OnRequestInterFace callback, LoadingFragmentDialog loadingFragmentDialog, FragmentManager fragmentManager) {
		mContext = context;
		mCallback = callback;
		mLoadingFragmentDialog = loadingFragmentDialog;
		mFragmentManager = fragmentManager;
		
		onPreExecute();
	}
	
	@Override
    protected void onPreExecute() {
		if(this.mLoadingDialog != null)
		{
			this.mLoadingDialog.show();
		}

		if(this.mLoadingFragmentDialog != null)
		{
			this.mLoadingFragmentDialog.show(mFragmentManager,"Loading");
		}
	}
	
	@Override
	protected HttpResponse doInBackground(Integer... params) {
		
		if(!BaseHttpClient.getInstance().checkConnectivity(mContext)){
			mCallback.onRequestError(BaseConsts.HTTP_STATUS_SERVICE_UNAVAILABLE);
			return null;
		}
		
		mRequestType = mCallback.getRequestType();
		
		if(mRequestType == BaseConsts.REQUEST_TYPE_GET){
			return BaseHttpClient.getInstance().doGet(mCallback.onRequestGetUrl(), mCallback.onRequestGetParameter());
		} else if(mRequestType == BaseConsts.REQUEST_TYPE_POST){
			return BaseHttpClient.getInstance().doPost(mCallback.onRequestGetUrl(), mCallback.onRequestGetParameter());
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(HttpResponse result) {
		
		if(result == null){
			mCallback.onRequestError(BaseConsts.HTTP_STATUS_SERVICE_UNAVAILABLE);
			return;
		}
		
		int http_status_code = result.getStatusLine().getStatusCode();
		String resultString = null;
		
		if(http_status_code == BaseConsts.HTTP_STATUS_OK){
			
			mResultType = mCallback.getResultType();
			resultString = BaseHttpResponse.getInstance().convertResponseToString(result);
			
			if(mResultType == BaseConsts.RESULT_TYPE_JSON){
				mCallback.onRequestResult(BaseHttpResponse.getInstance().convertStringToJSONObject(resultString), BaseConsts.RESULT_TYPE_JSON);
			} else {
				mCallback.onRequestResult(BaseHttpResponse.getInstance().convertStringToXml(resultString), BaseConsts.RESULT_TYPE_XML);
			}
		} else {
			mCallback.onRequestError(http_status_code);
		}
    }
}
