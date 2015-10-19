package frame.network;

import java.util.Vector;

import frame.protocol.BaseHttpRequest;
import frame.protocol.BaseResponse;


/**
 * 全局唯一的客户端会话类，负责与服务器交互
 * 
 * 功能： 客户端与服务器交互时请求的发送及回复的接收； 相关状态及请求处理结果的通知；
 * 
 * @author wuwei
 * 
 */
public class ClientSession {

	private static ClientSession instance;

	/**
	 * 协议请求错误接收机
	 */
	public IErrorReceiver defErrorReceiver;

	/**
	 * 协议请求状态接收机
	 */
	public INetStateReceiver defStateReceiver;

	private final Vector<AsynRsqHandlerHelper> vecRsqHandlers = new Vector<AsynRsqHandlerHelper>();

	// public NetworkData networkData = new NetworkData();

	public ClientSession() {

	}

	public static synchronized ClientSession Instace() {
		if (instance == null) {
			instance = new ClientSession();
		}
		return instance;
	}

	/**
	 * 异步获取请求回复接口 使用默认错误接收器及网络状态接收器作为通知接口,并采用互斥方式，防止多线程并发请求
	 * 
	 * @param request
	 * @param receiver
	 *            return: 见下一接口说明
	 */
	public int asynGetResponse(BaseHttpRequest request,
			IResponseReceiver receiver) {
		return asynGetResponse(request, receiver, defErrorReceiver,
				defStateReceiver);
	}

	/**
	 * 异步获取请求回复接口 使用默认错误接收器及网络状态接收器作为通知接口,并采用互斥方式，防止多线程并发请求
	 * 
	 * @param request
	 * @param receiver
	 *            return: 见下一接口说明
	 */
	public int asynGetResponse(BaseHttpRequest request,
			IResponseReceiver receiver, IErrorReceiver defErrorReceiver) {
		return asynGetResponse(request, receiver, defErrorReceiver,
				defStateReceiver);
	}

	/**
	 * 异步获取请求回复接口 采用互斥方式，防止多线程并发请求
	 * 
	 * @param request
	 * @param rspReceiver
	 * @param errReceiver
	 * @param stateReceiver
	 *            return: 标识该请求的cookie，上层可利用该cookie来取消此次请求
	 */
	synchronized public int asynGetResponse(BaseHttpRequest request,
			IResponseReceiver rspReceiver, IErrorReceiver errReceiver,
			INetStateReceiver stateReceiver) {
		synchronized (getAsynRsqLock()) {
			return asynGetResponseWithoutLock(request, rspReceiver,
					errReceiver, stateReceiver);
		}
	}

	/**
	 * 异步获取请求回复接口 使用默认错误接收器及网络状态接收器作为通知接口,非锁定方式，这里考虑到上层可能一次会批量调用
	 * 接口多次，为提高效率，在调用之前需要上层负责锁定，以下接口同样如此。
	 * 
	 * @param request
	 * @param receiver
	 *            return: 见下一接口说明
	 */
	public int asynGetResponseWithoutLock(BaseHttpRequest request,
			IResponseReceiver receiver) {
		return asynGetResponseWithoutLock(request, receiver, defErrorReceiver,
				defStateReceiver);
	}

	/**
	 * 异步获取请求回复接口
	 * 
	 * @param request
	 * @param rspReceiver
	 * @param errReceiver
	 * @param stateReceiver
	 *            return: 标识该请求的cookie，上层可利用该cookie来取消此次请求
	 */
	public int asynGetResponseWithoutLock(BaseHttpRequest request,
			IResponseReceiver rspReceiver, IErrorReceiver errReceiver,
			INetStateReceiver stateReceiver) {

		// ExecutorService pool = Executors.newFixedThreadPool(4);

		AsynRsqHandlerHelper handler = null;

		// clear bad handler
		for (int index = 0; index < vecRsqHandlers.size();) {
			handler = vecRsqHandlers.elementAt(index);
			if (handler.isBad()) {
				vecRsqHandlers.removeElementAt(index);
			} else {
				++index;
			}
		}

		// find an idle handler to commit request
		for (int index = 0; index < vecRsqHandlers.size(); ++index) {
			handler = vecRsqHandlers.elementAt(index);
			if (!handler.isWorking()) {
				if (handler.commitRequest(index, request, rspReceiver,
						errReceiver, stateReceiver)) {
					return index;
				}
			}
		}

		// don't find, create new handler to commit request
		boolean commitSuccess = false;
		int index = vecRsqHandlers.size();
		do {
			handler = new AsynRsqHandlerHelper();
			commitSuccess = handler.commitRequest(index, request, rspReceiver,
					errReceiver, stateReceiver);
		} while (!commitSuccess);
		vecRsqHandlers.addElement(handler);
		return index;

		// 这样的话，遇到网络状况不好的时候就会不断增加线程的数量

	}

	/**
	 * 取消指定请求的处理,对于无效cookie值会被忽略
	 * 
	 * @param rsqCookie
	 *            : 提交请求时返回的cookie
	 */
	public void cancel(int rsqCookie) {
		synchronized (getAsynRsqLock()) {
			cancelWithoutLock(rsqCookie);
		}
	}

	/**
	 * 取消当前所有请求
	 */
	public void cancelAll() {
		AsynRsqHandlerHelper handler;
		synchronized (getAsynRsqLock()) {
			for (int index = 0; index < vecRsqHandlers.size(); ++index) {
				handler = (AsynRsqHandlerHelper) vecRsqHandlers
						.elementAt(index);
				if (handler.isWorking()) {
					handler.cancel();
				}
			}
		}
	}

	/**
	 * 取消指定请求的处理,对于无效cookie值会被忽略
	 * 
	 * @param rsqCookie
	 *            : 提交请求时返回的cookie
	 */
	public void cancelWithoutLock(int rsqCookie) {
		if ((rsqCookie >= vecRsqHandlers.size()) || (rsqCookie < 0)) {
			return;
		}

		AsynRsqHandlerHelper handler = (AsynRsqHandlerHelper) vecRsqHandlers
				.elementAt(rsqCookie);
		if (handler.isWorking()) {
			handler.cancel();
		}
	}

	/**
	 * 获取异步请求锁
	 * 
	 * @return
	 */
	public Object getAsynRsqLock() {
		return this;
	}

	/**
	 * 同步获取请求回复接口 调用此接口后在得到回复前会一直阻塞注, 当出现任何错误时获取到得回复实际类型为ErrorResponse,
	 * 故调用此接口后需要利用instanceof关键字判断是否是错误回复， 并进行相应错误处理。
	 * 
	 * @param request
	 * @return
	 */
	public BaseResponse syncGetResponse(BaseHttpRequest request) {
		return RsqHandleHelper.getResponseImpl(-1, request, null);
	}

}
