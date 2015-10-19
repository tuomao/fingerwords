package frame.protocol;

import java.io.IOException;
import java.io.InputStream;

import frame.network.INetStateReceiver;

/**
 * http回复包接�?
 * 
 * @author zhouyin
 * 
 */
public interface BaseHttpResponse extends BaseResponse {

	/**
	 * 从服务器端获取的http输入流解析接�?
	 * 
	 * @param rsqCookie
	 *            : 对应请求标识Cookie
	 * @param request
	 *            ：该回复对应的请�?
	 * @param inputStream
	 *            ：回复输入流
	 * @param len
	 *            : 输入流长度，�?1时表示无法获取输入流长度
	 * @param stateRecevier
	 *            : 状�?接收�?
	 * @return: 如果解析成功返回null,否则返回相应错误回复�?
	 * @throws IOException
	 */
	public ErrorResponse parseInputStream(int rspCookie,
			BaseHttpRequest request, InputStream inputStream, int len,
			INetStateReceiver stateReceiver) throws IOException;

	/**
	 * 是否允许关闭输入流接�?此接口为性能�?��而设，�?虑到某些应用可能在最上层处理原始输入流更有效�?
	 * 
	 * @return
	 */
	public boolean isAllowCloseInputStream();

	/**
	 * 获得输入�?
	 * 
	 * @return
	 */
	public InputStream getInputStream();
}
