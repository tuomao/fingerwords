package frame.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import frame.network.INetStateReceiver;




import android.util.Log;

public abstract class BaseJSONResponseForUploadImg implements BaseHttpResponse {
	protected InputStream inputStream = null;
	public String Result = "";
	public String resultURL="";
	@Override
	public InputStream getInputStream() {
		// TODO Auto-generated method stub
		return inputStream;
	}

	@Override
	public boolean isAllowCloseInputStream() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ErrorResponse parseInputStream(int rspCookie,
			BaseHttpRequest request, InputStream inputStream, int len,
			INetStateReceiver stateReceiver) throws IOException {
		// TODO Auto-generated method stub
		this.inputStream = inputStream;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream));

			StringBuffer sb = new StringBuffer();

			String result = br.readLine();

			while (result != null) {
				sb.append(result);
				result = br.readLine();
			}
			Log.e("JSONObject", sb.toString());
			String json = sb.toString();
			
			
			
			urlResult(json);
			return null;
			
//
//			if (sb.toString().equals("{}")) {
//				Result = "未知错误";
//				return new ErrorResponse(ErrorResponse.ERROR_PROTOCOL, Result);
//			}
//
//			JSONObject resoultsource = new JSONObject(json);

//			Result = resoultsource.getString("result");
//			if (Result.equals("success")) {
//				parseResult(resoultsource);
//				return null;

//			} else {
//				return new ErrorResponse(ErrorResponse.ERROR_PROTOCOL, Result);
//			}
		} catch (Exception e) {

			e.printStackTrace();

		}
		return new ErrorResponse();
	}

//	public abstract void parseResult(JSONObject resoultsource)
//			throws JSONException;
	
	public abstract void urlResult(String url);
	
}
