package com.mrwang.coolweather.util;
/**
 * http网络请求回调的接口
 * @author Administrator
 *
 */
public interface HttpCallBackListener {
	public void onFinish(String response);
	public void onError(Exception e);
}
