package frame.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import frame.network.INetStateReceiver;



public abstract class BaseJSONResponse implements BaseHttpResponse {

	private InputStream inputStream = null;

	public ErrorResponse parseInputStream(int rspCookie,
			BaseHttpRequest request, InputStream inputStream, int len,
			INetStateReceiver stateReceiver) throws IOException {
		this.inputStream = inputStream;
//		JSONObject doc = parseResponse(inputStream);
		String doc = parseResponse(inputStream);
		if (!extractBody(null, doc)) {
			return new ErrorResponse(ErrorResponse.ERROR_PROTOCOL);
		}
		return null;
	}

	/**
	 * get inputstream
	 * 
	 * @return InputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	public boolean isAllowCloseInputStream() {
		return true;
	}

	/**
	 * 协议回复内容提取抽象接口
	 * 
	 * @param headerElement
	 * @param bodyElement
	 * @return true--success, false--fail
	 */
	protected abstract boolean extractBody(JSONObject headerEleemnt,
			String bodyElement);

	private String parseResponse(InputStream response) throws IOException {
		if (response == null)
			return null;
		else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int i = -1;
			while ((i = response.read()) != -1) {
				baos.write(i);
			}
//			try {
//				return new JSONObject(baos.toString());
//			} catch (JSONException e) {
//				e.printStackTrace();
//				return null;
//			}
			byte[] baosByte=baos.toByteArray();
			if (baosByte.length>3 &&
					baosByte[0] == -17 &&
					baosByte[1] == -69 &&
					baosByte[2] == -65){
				return new String(baosByte, 3, baosByte.length-3);
			}
			return baos.toString();
		}
	}
}
