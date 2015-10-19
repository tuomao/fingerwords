package frame.protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.Header;


import frame.network.INetStateReceiver;
import frame.runtimedata.RuntimeManager;




/**
 * http请求包抽象基类，提供构建特定http请求的基本接口
 * 
 * @author zhouyin
 * 
 */
public abstract class BaseHttpRequest {

	// 希望请求被发送的方式
	public final static int GET = 1;
	public final static int POST = 2;

	protected boolean needGZIP = true;

	public boolean isGetCache = true;

	private String absoluteURI;
	private int method = POST;
	private int connectionTimeout = 30000;

	public boolean getNeedGZip() {
		return needGZIP;
	}

	// http返回协议头
	public Header[] headers;

	/**
	 * 设置请求资源绝对地址 格式为：http://www.zhangxinda.cn/xxxx/xxx.xx
	 * 
	 * @param absoluteURI
	 */
	public final void setAbsoluteURI(String absoluteURI) {
		this.absoluteURI = absoluteURI;
	}

	/**
	 * 设置请求方法
	 */
	public final void setMethod(int method) {
		this.method = method;
	}

	/**
	 * 获取请求主机地址接口 格式分两种: 1. IP地址，eg：192.168.12.12[:port] 2.
	 * 域名形式，eg：www.zhangxinda.cn[:port]
	 * 
	 * @return
	 */
	public final String getHost() {
		try {
			URL parser = new URL(absoluteURI);
			if (parser != null) {
				return parser.getHost();
			} else {
				return "";
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}

	}

	/**
	 * 获取请求资源相对地址接口 格式为：/xxxx/xxx.xx
	 * 
	 * @return
	 */
	public final String getRelativeURI() {
		try {
			URL parser = new URL(absoluteURI);
			if (parser != null) {
				return parser.getPath();
			} else {
				return "";
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 获取请求资源的绝对地址接口 格式为：http://www.zhangxinda.cn/xxxx/xxx.xx
	 * 
	 * @return
	 */
	public final String getAbsoluteURI() {
		return absoluteURI;
	}

	/**
	 * 创建该请求特定回复接口
	 * 
	 * @return
	 */
	public abstract BaseHttpResponse createResponse();

	/**
	 * 获取请求发送方式接口，默认返回POST方法 暂时只提供GET,POST两种方式
	 * 
	 * @return
	 */
	public int getMethod() {
		return method;
	}

	/**
	 * 获取请求附加头接口，默认返回null 返回二维数组应为2列，第一列为头属性名，第二列为头属性值。 属性名及值应符合http协议的标准头格式，eg:
	 * 属性名：Content-Type 属性值：text/html;charset=utf-8
	 * 
	 * @return
	 */
	public String[][] getExtraHeaders() {
		return null;
	}

	/**
	 * 填充待发送的请求内容接口，默认不填充任何内容
	 * 
	 * @param cookie
	 *            : 标识该请求的cookie
	 * @param output
	 *            : http输出流
	 * @param stateReceiver
	 *            : 状态接收器
	 * @throws IOException
	 */
	public void fillOutputStream(int cookie, OutputStream output,
			INetStateReceiver stateReceiver) throws IOException {
	}

	/**
	 * 是否需要缓存本请求回复接口，默认不需要 注：需要缓存回复的请求需要重写hashCode方法，方便hashtable正常工作
	 * 
	 * @return
	 */
	public boolean needCacheResponse() {
		return false;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	protected int requestId = -1;

	protected int requestMsg = -1;

	public String getRequestId() {
		if (requestId == -1) {
			return "";
		} else {
			return RuntimeManager.getString(requestId);
		}
	}

	public String getRequestMsg() {
		if (requestMsg == -1) {
			return "";
		} else {
			return RuntimeManager.getString(requestMsg);
		}

	}

}