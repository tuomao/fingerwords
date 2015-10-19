package frame.protocol;

import java.io.IOException;
import java.io.OutputStream;


import frame.network.INetStateReceiver;
import frame.network.NetworkData;
import frame.util.KXmlSerializer;
import frame.util.XmlSerializer;

/**
 * XML协议请求包抽象基类，搭建请求包的基本骨架�?完成协议公共头的构建，并对外提供必要
 * 接口方便请求的发送及相应回复的创�?
 * 
 * @author zhouyin
 * 
 */
public abstract class BaseXMLRequest extends BaseHttpRequest {

	public BaseXMLRequest() {
		if (NetworkData.sessionId != null && !NetworkData.sessionId.equals("")) {
			setAbsoluteURI(NetworkData.accessPoint + ";jsessionid="
					+ NetworkData.sessionId);
		} else {
			setAbsoluteURI(NetworkData.accessPoint);
		}
	}


	public void fillOutputStream(int cookie, OutputStream output,
			INetStateReceiver stateReceiver) throws IOException {
		XmlSerializer serializer = new KXmlSerializer();
		// serializer.setOutput(output, "utf-8");
		// serializer.startDocument(null, null);
		// serializer.startTag(null, "HPDSVRR");
		// serializer.attribute(null, "v", "1.0");
		// fillHeader(serializer);
		// serializer.startTag(null, "HdpRbody");
		// fillBody(serializer);
		// serializer.endTag(null, "HdpRbody");
		// serializer.endTag(null, "HPDSVRR");
		// serializer.endDocument();
	}

	@Override
	public String[][] getExtraHeaders() {
		String[][] aryHeaders = new String[1][2];
		aryHeaders[0][0] = "Content-Type";
		aryHeaders[0][1] = "text/html;charset=utf-8";
		return aryHeaders;
	}

	/**
	 * 请求包体填充抽象接口，子类实现此接口完成具体请求包体的构�?
	 * 
	 * @param serializer
	 *            ：xml流构建器
	 */
	protected abstract void fillBody(XmlSerializer serializer)
			throws IOException;

	private void fillHeader(XmlSerializer serializer) throws IOException {
		serializer.startTag(null, "HpdRheader");

		serializer.startTag(null, "svrRno");
		// serializer.attribute(null, "v", "1.0");
		serializer.attribute(null, "v", requestVersion);
		serializer.text(getRequestId());
		serializer.endTag(null, "svrRno");

		serializer.startTag(null, "sid");
		String sid = NetworkData.sessionId;
		serializer.text((sid == null) ? "" : sid);
		serializer.endTag(null, "sid");

		serializer.startTag(null, "id");
		// serializer.text(ClientSession.theClientSession.getUserName());
		serializer.text("");
		serializer.endTag(null, "id");

		serializer.startTag(null, "pd");
		// if (AccountManager.Instance().currentUser != null) {
		// if (AccountManager.Instance().currentUser.role == User.ROLE_GUEST) {
		// serializer.text("guest");
		// } else if (AccountManager.Instance().currentUser.role ==
		// User.ROLE_USER) {
		// serializer.text("user");
		// }
		// } else {
		// serializer.text("guest");
		// }
		serializer.endTag(null, "pd");

		serializer.startTag(null, "fee");
		serializer.text("free");
		serializer.endTag(null, "fee");

		serializer.startTag(null, "sec");
		serializer.text("no");
		serializer.endTag(null, "sec");

		serializer.endTag(null, "HpdRheader");
	}

	// protected String requestId;

	protected String requestVersion = BaseProtocolDef.PROTOCOL_VERSION_1;

	// public void setVersion(String version) {
	// this.CurrentVersion = version;
	// }
}