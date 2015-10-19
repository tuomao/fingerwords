package frame.protocol;

public class BaseProtocolDef {

	public static boolean protocolTest = true;

	// xml协议资源请求绝对地址(初始)
	private static String xmlAbsoluteURI_final = "http://apps.iyuba.com/voa/";

	private static String xmlAbsoluteURI_test = "http://apps.iyuba.com/voa/";

	public static final String PROTOCOL_VERSION_1 = "1.0";

	public static final String PROTOCOL_VERSION_2 = "2.0";
	
	// 数据源，0：mapabc(高德); 1: mapbar(图吧)
	public static int dataSource = 0;
	

	// 默认游客用户�?
	public static final String defGuestName = "guest";

	// 默认游客密码
	public static final String defGuestPassword = "guest";

	public static String getXmlAbsoluteURI() {
		if (protocolTest) {
			return xmlAbsoluteURI_test;
		} else {
			return xmlAbsoluteURI_final;
		}

	}

}
