package com.led.weatherdemo.net;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.led.weatherdemo.ConstantValue;

public class HttpClientUtil {
	private static final String TAG = "HttpClientUtil";

	private HttpClient client;

	private HttpPost post;
	private HttpGet get;

	public HttpClientUtil() {
		client = new DefaultHttpClient();
//		// 判断是否需要设置代理信息
//		if (StringUtils.isNotBlank(GlobalParams.PROXY)) {
//			// 设置代理信息
//			HttpHost host = new HttpHost(GlobalParams.PROXY, GlobalParams.PORT);
//			client.getParams()
//					.setParameter(ConnRoutePNames.DEFAULT_PROXY, host);
//		}
	}

	/**
	 * POST请求数据
	 * 
	 * @param url
	 *            请求地址
	 * @param data
	 *            请求正文(实体)
	 */
	public String sendDataByPost(String url, String data) {
		post = new HttpPost(url);

		try {
			StringEntity entity = new StringEntity(data,
					ConstantValue.ENCONDING);
			post.setEntity(entity);

			HttpResponse response = client.execute(post);

			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity(),
						ConstantValue.ENCONDING);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * GET请求数据
	 * 
	 * @param url
	 *            (带拼接参数)
	 * @return 服务器返回的数据
	 */
	public String sendDataByGet(String url) {
		// 新建HttpGet对象
		get = new HttpGet(url);
		try {
			// 获取HttpResponse实例
			HttpResponse response = client.execute(get);
			// 判断是够请求成功
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获取返回的数据
				return EntityUtils.toString(response.getEntity(), ConstantValue.ENCONDING).trim();
			}
		} catch (Exception e) {
			Log.e(TAG, url);
			e.printStackTrace();
		}
		return null;
	}
}
