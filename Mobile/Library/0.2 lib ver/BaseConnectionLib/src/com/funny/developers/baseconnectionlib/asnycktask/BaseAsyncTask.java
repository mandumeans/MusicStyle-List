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

public class BaseAsyncTask extends AsyncTask<Integer, Integer, Object>{
	
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
	
	@Override //통신 및 decoding 처리는 무조건 doInBackground에서 처리
	protected Object doInBackground(Integer... params) {
		
		//통신 가능여부 확인
		if(!BaseHttpClient.getInstance().checkConnectivity(mContext)){
			mCallback.onRequestError(BaseConsts.HTTP_STATUS_SERVICE_UNAVAILABLE);
			return null;
		}
		
		HttpResponse result = null; //통신결과
		String resultString = null; //String 통신결과
		mRequestType = mCallback.getRequestType(); //통신 방법 타입(GET, POST)
		mResultType = mCallback.getResultType(); //통신 결과 타입(JSON, XML)
		
		//통신부분
		if(mRequestType == BaseConsts.REQUEST_TYPE_GET){
			result = BaseHttpClient.getInstance().doGet(mCallback.onRequestGetUrl(), mCallback.onRequestGetParameter());
		} else if(mRequestType == BaseConsts.REQUEST_TYPE_POST){
			result = BaseHttpClient.getInstance().doPost(mCallback.onRequestGetUrl(), mCallback.onRequestGetParameter());
		}
		
		//통신결과 Decode 부분
		if(result.getStatusLine().getStatusCode() == BaseConsts.HTTP_STATUS_OK){
			
			resultString = BaseHttpResponse.getInstance().convertResponseToString(result);
			
			if(mResultType == BaseConsts.RESULT_TYPE_JSON){
				return BaseHttpResponse.getInstance().convertStringToJSONObject(resultString);
			} else {
				return BaseHttpResponse.getInstance().convertStringToXml(resultString);
			}
		} else {
			mCallback.onRequestError(result.getStatusLine().getStatusCode());
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Object result) {
		
		if(result == null){
			mCallback.onRequestError(BaseConsts.HTTP_STATUS_SERVICE_UNAVAILABLE);
			return;
		}
		
		if(mResultType == BaseConsts.RESULT_TYPE_JSON){
			mCallback.onRequestResult(result, BaseConsts.RESULT_TYPE_JSON);
		} else {
			mCallback.onRequestResult(result, BaseConsts.RESULT_TYPE_XML);
		}
    }
}
