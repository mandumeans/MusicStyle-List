package com.funny.developers.baseconnectionlib.consts;

public class BaseConsts {
	
	/*
	 * API 통신 타입
	 */
	public static final int REQUEST_TYPE_GET = 1;
	public static final int REQUEST_TYPE_POST = 2;
	
	
	/*
	 * 통신 에러 타입
	 */
	public static final int HTTP_STATUS_OK = 200;
	public static final int HTTP_STATUS_FAIL = 0;
	public static final int HTTP_STATUS_BAD_REQUEST = 400;
	public static final int HTTP_STATUS_UNAUTHORIZED = 401;
	public static final int HTTP_STATUS_FORBIDDEN = 403;
	public static final int HTTP_STATUS_NOT_FOUND = 404;
	public static final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;
	public static final int HTTP_STATUS_SERVICE_UNAVAILABLE = 503;
	public static final int HTTP_STATUS_NO_CONNECTION = 10000;
	public static final int HTTP_STATUS_ROAMING_CAN_NOT_3G = 10001;
	public static final int RESPONSE_NO_EXPIRE_TOKEN = 10002;
	public static final int RESPONSE_NO_SERVER_ERROR = 10003;
	
	
	/*
	 * 통신 결과 데이터 타입
	 */
	public static final int RESULT_TYPE_JSON = 1;
	public static final int RESULT_TYPE_XML = 2;
}
