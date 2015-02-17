package com.mrwang.coolweather.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络请求的工具类
 * 
 * @author Administrator
 */
public class HttpUtil {
	public static void sendHttpRequest(final String address,
			final HttpCallBackListener listener) {
		new Thread() {
			public void run() {
				HttpURLConnection conn = null;
				try {
					URL url = new URL(address);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(8000);
					conn.setReadTimeout(5000);
					if (conn.getResponseCode() == 200) {
						InputStream is = conn.getInputStream();
						String stream = StreamUtils.readStream(is);
						if (listener!=null) {
							listener.onFinish(stream);
						}
					}
				} catch (Exception e) {
					if (listener!=null) {
						listener.onError(e);
					}
				} finally {
					if (conn!=null) {
						conn.disconnect();
					}
				}
			};
		}.start();
	}
}
