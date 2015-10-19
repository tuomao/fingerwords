package frame.network;


import frame.protocol.BaseProtocolDef;
import frame.runtimedata.RuntimeManager;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class NetworkData {

	// public static NetworkInfo wifiNetInfo = null;
	// public static NetworkInfo mobNetInfo = null;

	// public static NetworkInfo networkInfo;

	public static String sessionId;

	public static String accessPoint = BaseProtocolDef.getXmlAbsoluteURI();

	public static String mapDownloadUrl;
	public static String cellIDUrl;
	public static String tempImageUploadUrl;
	public static String offsetLocationUrl;
	public static String besideIconDownloadUrl;
	public static String pulseCheckUrl;
	public static String trafficUrl;

	public static String handpodHeart = "www.surfingo.net";
	public static String googleHost = "www.google.com.hk";

	public static String SMD = "";
	public static String PhoneNum = "";
	public static String IMEI = "";
	public static String IMSI = "";

	public static String removeString = "_60x46_86x66_120x92";
	public static String replaceString = "_120x92";


	public static NetworkInfo getNetworkInfo() {
		Log.e("Application.CONNECTIVITY_SERVICE",""+ RuntimeManager.getApplication());
		return ((ConnectivityManager) RuntimeManager.getApplication()
				.getSystemService(Application.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
	}
}
