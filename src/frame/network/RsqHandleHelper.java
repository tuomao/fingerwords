package frame.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Application;
import android.net.ConnectivityManager;
import android.os.Build;

import com.wsh.fingerwords.R;

import frame.protocol.BaseHttpRequest;
import frame.protocol.BaseHttpResponse;
import frame.protocol.BaseResponse;
import frame.protocol.ErrorResponse;
import frame.runtimedata.RuntimeManager;
/**
 * 请求处理辅助�?
 * 
 * @author wuwei
 * 
 */
public class RsqHandleHelper {

	private final static boolean needGZip = false;// GZip�?��，省得每次要截包都得去注释，平时为true，需要截包时置为false

	static BaseResponse getResponseImpl(int rspCookie, BaseHttpRequest request,
			INetStateReceiver stateReceiver) {

		DefaultHttpClient connection = null;
		BaseHttpResponse httpResponse = null;

		HttpUriRequest httpUriRequest = null;

		HttpResponse response = null;

		ErrorResponse err = null;

		try {
			// 设置连接
			connection = connectServer(rspCookie, request, stateReceiver);
			// 传�?参数
			httpUriRequest = buildAndSendRsq(connection, rspCookie, request,
					stateReceiver);
			// 执行，得到返回�?
			response = connection.execute(httpUriRequest);
			// 解析返回结果
			BaseResponse rsp = recvAndParseRsp(response, rspCookie, request,
					stateReceiver);
			if (rsp instanceof ErrorResponse) {
				return rsp;
			}
			httpResponse = (BaseHttpResponse) rsp;
			return httpResponse;
		} catch (HttpHostConnectException e) {
			e.printStackTrace();
			err = new ErrorResponse(ErrorResponse.ERROR_NET_CONNECTION,
					e.getMessage());

		} catch (ClientProtocolException e) {// Client协议异常
			e.printStackTrace();

			err = new ErrorResponse(ErrorResponse.ERROR_ClLIENT_PROTOCOL,
					e.getMessage());

		} catch (SocketTimeoutException e) {// Socket超时异常
			e.printStackTrace();

			err = new ErrorResponse(ErrorResponse.ERROR_NET_TIMEOUT,
					e.getMessage());

		} catch (SocketException e) {// Socket异常
			e.printStackTrace();

			err = new ErrorResponse(ErrorResponse.ERROR_NET_SOCKET,
					e.getMessage());

		} catch (IOException e) {// IO异常
			e.printStackTrace();

			err = new ErrorResponse(ErrorResponse.ERROR_NET_IO, e.getMessage());

		} catch (Exception e) {// 其他异常
			e.printStackTrace();

			err = new ErrorResponse(ErrorResponse.ERROR_UNKNOWN, e.getMessage());

		} finally {
			closeConnection(connection);
		}

		if (stateReceiver != null && err != null) {
			stateReceiver.onNetError(request, rspCookie, err);
		}
		return err;

	}

	static private DefaultHttpClient connectServer(int rspCookie,
			BaseHttpRequest request, INetStateReceiver stateReceiver)
			throws IOException {
		DefaultHttpClient connection = null;

		if (stateReceiver != null) {
			stateReceiver.onStartConnect(request, rspCookie);
		}

		HttpParams my_httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(my_httpParams,
				request.getConnectionTimeout());
		HttpConnectionParams.setSoTimeout(my_httpParams,
				request.getConnectionTimeout());

		connection = new DefaultHttpClient(my_httpParams);

		// 如果是使用的运营商网�?
		if (NetworkData.getNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE) {
			// 获取默认代理主机ip
			String host = android.net.Proxy.getDefaultHost();
			// 获取端口
			int port = android.net.Proxy.getDefaultPort();
			if (host != null && port != -1) {
				HttpHost proxy = new HttpHost(host, port);
				connection.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
			}
		}

		if (stateReceiver != null) {
			// Log.e("网络请求", "弹出状�?窗体");
			stateReceiver.onConnected(request, rspCookie);
		}

		return connection;

	}

	static private HttpUriRequest buildAndSendRsq(DefaultHttpClient connection,
			int rspCookie, BaseHttpRequest request,
			INetStateReceiver stateReceiver) throws IOException {

		HttpUriRequest httpUriRequest = null;

		ByteArrayEntity entity;

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		if (stateReceiver != null) {
			stateReceiver.onStartSend(request, rspCookie, -1);
		}

		if (request.getMethod() == BaseHttpRequest.GET) {
			httpUriRequest = new HttpGet(request.getAbsoluteURI());
			if (needGZip) {
				httpUriRequest.addHeader("Accept-Encoding", "gzip");
			}

			if (NetworkData.SMD != null && !NetworkData.SMD.equals("")) {
				httpUriRequest.addHeader("SMD", NetworkData.SMD);
			}

			Application application = new Application();

			String agent = RuntimeManager.getString(R.string.minName) + "/"
					+ RuntimeManager.getString(R.string.minVer) + " "
					+ RuntimeManager.getString(R.string.runEnv_name) + "/"
					+ Build.VERSION.RELEASE;
			httpUriRequest.addHeader("Client-Agent", agent);
			httpUriRequest.addHeader("ParamSet",
					"ssite=" + RuntimeManager.getString(R.string.ssite_code));
		} else {
			HttpPost httpPost = null;

			httpPost = new HttpPost(request.getAbsoluteURI());
			request.fillOutputStream(rspCookie, outputStream, stateReceiver);

			entity = new ByteArrayEntity(outputStream.toByteArray());

			String[][] aryHeaders = request.getExtraHeaders();
			if (aryHeaders != null) {
				int length = aryHeaders.length;
				if (aryHeaders != null) {
					for (int i = 0; i < length; ++i) {
						if (aryHeaders[i].length != 2) {
							throw new IllegalArgumentException(
									"aryheader must be 2 columns!");
						}

						for (int j = 0; j < 2; ++j) {
							if (aryHeaders[i][0].equals("Content-Type")) {
								entity.setContentType(aryHeaders[i][1]);
							}
						}
					}
				}
			}

			httpPost.setEntity(entity);
			if (request.getNeedGZip() && needGZip) {
				httpPost.addHeader("Accept-Encoding", "gzip");
			}
			if (NetworkData.SMD != null && !NetworkData.SMD.equals("")) {
				httpPost.addHeader("SMD", NetworkData.SMD);
			}

			Application application = RuntimeManager.getApplication();

			String agent = application.getString(R.string.minName) + "/"
					+ application.getString(R.string.minVer) + " "
					+ application.getString(R.string.runEnv_name) + "/"
					+ Build.VERSION.RELEASE;
			httpPost.addHeader("Client-Agent", agent);
			httpPost.addHeader("ParamSet",
					"ssite=" + application.getString(R.string.ssite_code));
			httpUriRequest = httpPost;
		}

		if (stateReceiver != null) {
			stateReceiver.onSendFinish(request, rspCookie);
		}

		return httpUriRequest;

	}

	static private BaseResponse recvAndParseRsp(HttpResponse response,
			int rspCookie, BaseHttpRequest request,
			INetStateReceiver stateReceiver) throws IOException {
		BaseHttpResponse httpResponse = null;

		int code = response.getStatusLine().getStatusCode();
		if (code == HttpStatus.SC_OK) {// 200

			request.headers = response.getAllHeaders();

			int len = (int) response.getEntity().getContentLength();

			// 针对当前协议，返回内容长度不应该�?,故出现此情况返回错误
			if (len == 0) {
				return new ErrorResponse(ErrorResponse.ERROR_UNKNOWN);
			}

			if (stateReceiver != null) {
				stateReceiver.onStartRecv(request, rspCookie, len);
			}

			InputStream instream = response.getEntity().getContent();
			Header contentEncoding = response
					.getFirstHeader("Content-Encoding");
			// Log.e("MAP", instream.toString());
			// Log.e("Test", Boolean.toString(contentEncoding != null));
			// Log.e("Test",
			// Boolean.toString(contentEncoding.getValue().equalsIgnoreCase("gzip")));
			if (contentEncoding != null
					&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {

				instream = new GZIPInputStream(instream);
			}

			httpResponse = request.createResponse();
			ErrorResponse err = httpResponse.parseInputStream(rspCookie,
					request, instream, len, stateReceiver);

			if (stateReceiver != null) {
				stateReceiver.onRecvFinish(request, rspCookie);
			}

			if (err != null) {
				httpResponse = null;
				return err;
			}
			return httpResponse;
		} else {
			return new ErrorResponse(ErrorResponse.ERROR_UNKNOWN);
		}

	}

	static private void closeConnection(DefaultHttpClient connection) {
		try {
			if (connection != null)
				connection.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
