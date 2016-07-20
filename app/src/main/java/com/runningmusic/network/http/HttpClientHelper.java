package com.runningmusic.network.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.runningmusic.network.http.HttpReqResult.ResultType;
import com.runningmusic.utils.Log;



public class HttpClientHelper {

	private final static int TIME_OUT = 60;

	private HttpClientHelper() {

	}

	public static HttpReqResult executePost(String url, HttpEntity httpEntity) {
		HttpReqResult result = new HttpReqResult(ResultType.FAILED, "网络请求失败。");
		if (url == null || url.length() == 0 || httpEntity == null) {
			return result;
		}
		// 创建HttpClient实例
		DefaultHttpClient httpClient = getDefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		// 设置超时时间
		HttpParams hcParams = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(hcParams, TIME_OUT * 1000);
		HttpConnectionParams.setSoTimeout(hcParams, TIME_OUT * 1000);
		httpPost.setEntity(httpEntity);
		// 发送请求，得到响应
		try {
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			// entity不为null说明HTTP请求成功
			if (responseEntity != null) {
				// 获取响应代码
				int Status = response.getStatusLine().getStatusCode();
				// 200响应成功
				if (Status == 200) {
					// 得到返回的网页源码
					String resource = new String(EntityUtils.toByteArray(responseEntity), "UTF-8").trim();
					Log.d("resource", resource);
					return new HttpReqResult(ResultType.SUCCESS, resource);
				} else {
					return new HttpReqResult(ResultType.FAILED, "您的网络不给力 (" + Status + ")，请稍后再试。");
				}
			} else {
				return new HttpReqResult(ResultType.FAILED, "您的网络不给力，请稍后再试。");
			}
		} catch (UnknownHostException e) {
			return new HttpReqResult(ResultType.FAILED, "您的网络不给力,无法解析服务器地址。");
		} catch (SocketTimeoutException e) {
			return new HttpReqResult(ResultType.FAILED, "您的网络不给力,请求超时啦。");
		} catch (SSLException e) {
			return new HttpReqResult(ResultType.FAILED, "站点证书校验失败。");
		} catch (FileNotFoundException e) {
			return new HttpReqResult(ResultType.FAILED, "上传文件错误。");
		} catch (Exception e) {
			return new HttpReqResult(ResultType.FAILED, "您的网络不给力，请稍后再试。");
		} finally {
			abortConnection(httpPost, httpClient);
		}
	}

	public static DefaultHttpClient getDefaultHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, Boolean.FALSE);
		DefaultHttpClient httpClient = new DefaultHttpClient(params);
		httpClient.setHttpRequestRetryHandler(requestRetryHandler);
		// 加入Gzip压缩
		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
				if (!request.containsHeader("Accept-Encoding")) {
					request.addHeader("Accept-Encoding", "gzip");
				}
			}
		});
		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
				HttpEntity entity = response.getEntity();
				Header ceheader = entity.getContentEncoding();
				if (ceheader != null) {
					HeaderElement[] codecs = ceheader.getElements();
					for (int i = 0; i < codecs.length; i++) {
						if (codecs[i].getName().equalsIgnoreCase("gzip")) {
							response.setEntity(new GzipDecompressingEntity(response.getEntity()));
							return;
						}
					}
				}
			}
		});
		return httpClient;
	}

	private static void abortConnection(final HttpRequestBase httpPost, final HttpClient httpClient) {
		if (httpPost != null) {
			httpPost.abort();
		}
		if (httpClient != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}

	private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
		public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			if (executionCount >= 3) {
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				return false;
			}
			HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				return true;
			}
			return false;
		}
	};

	public static UrlEncodedFormEntity getUrlEncodedFormEntity(Map<String, String> paramsMap) {
		UrlEncodedFormEntity result = null;
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> map : paramsMap.entrySet()) {
				try {
					params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
				} catch (Exception e) {
				}
			}
			result = new UrlEncodedFormEntity(params, "UTF-8");
		} catch (Exception e) {
		}
		return result;
	}

	public static MultipartEntity getMultipartEntity(Map<String, String> paramsMap, Map<String, String> fileMap) {
		MultipartEntity result = new MultipartEntity();
		try {
			for (Map.Entry<String, String> map : paramsMap.entrySet()) {
				try {
					result.addPart(map.getKey(), new StringBody(map.getValue(),Charset.forName("UTF-8")));
				} catch (Exception e) {
				}
			}
			for (Map.Entry<String, String> map : fileMap.entrySet()) {
				try {
					result.addPart(map.getKey(), new FileBody(new File(map.getValue())));
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}
		return result;
	}

}
