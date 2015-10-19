package frame.network;

import frame.protocol.BaseHttpRequest;
import frame.protocol.BaseHttpResponse;



public interface IResponseReceiver {
	void onResponse(BaseHttpResponse response, BaseHttpRequest request,
			int rspCookie);
}
