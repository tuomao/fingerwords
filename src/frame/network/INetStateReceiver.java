package frame.network;

import frame.protocol.BaseHttpRequest;
import frame.protocol.ErrorResponse;

public interface INetStateReceiver {
	public void onStartConnect(BaseHttpRequest request, int rspCookie);

	public void onConnected(BaseHttpRequest request, int rspCookie);

	public void onStartSend(BaseHttpRequest request, int rspCookie, int totalLen);

	public void onSend(BaseHttpRequest request, int rspCookie, int len);

	public void onSendFinish(BaseHttpRequest request, int rspCookie);

	public void onStartRecv(BaseHttpRequest request, int rspCookie, int totalLen);

	public void onRecv(BaseHttpRequest request, int rspCookie, int len);

	public void onRecvFinish(BaseHttpRequest request, int rspCookie);

	public void onNetError(BaseHttpRequest request, int rspCookie,
			ErrorResponse errorInfo);

	public void onCancel(BaseHttpRequest request, int rspCookie);
}
