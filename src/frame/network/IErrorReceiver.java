package frame.network;

import frame.protocol.BaseHttpRequest;
import frame.protocol.ErrorResponse;

/**
 * 
 * @author wuwei
 * 
 */
public interface IErrorReceiver {

	void onError(ErrorResponse errorResponse, BaseHttpRequest request,
			int rspCookie);
}
