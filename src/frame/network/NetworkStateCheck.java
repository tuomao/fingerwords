package frame.network;

import frame.runtimedata.RuntimeManager;

import android.app.Application;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkStateCheck {

	/**
	 * 网络状�?正常
	 */
	public static final int NET_OK = 0;

	/**
	 * 当前为飞行模�?
	 */
	public static final int NET_AIRPLANE_MODE = 1;

	/**
	 * 数据网络已关�?
	 */
	public static final int NET_NETWORK_OFF = 2;

	/**
	 * 无法连接到Handpod服务�?
	 */
	public static final int NET_HANDPOD_ERR = 3;

	/**
	 *本机网络设置有问�?
	 */
	public static final int NET_SET_ERR = 4;

	/**
	 *其他未知问题
	 */
	public static final int NET_OTHER_ERR = 5;

	public static int checkNetworkState() {
		return checkNetworkState(false);
	}

	public static int checkNetworkState(boolean isInit) {
		int resultCode = 0;
		if (android.provider.Settings.System.getInt(RuntimeManager.getContext()
				.getContentResolver(),
				android.provider.Settings.System.AIRPLANE_MODE_ON, 0) != 0) {
			resultCode = NET_AIRPLANE_MODE;
		} else if (NetworkData.getNetworkInfo() == null
				|| !NetworkData.getNetworkInfo().isAvailable()) {
			resultCode = NET_NETWORK_OFF;
		} else if (doPing(NetworkData.getNetworkInfo().getType(),
				NetworkData.handpodHeart)) {
			if (isInit) {
				resultCode = NET_OK;
			} else {
				resultCode = NET_OTHER_ERR;
			}
		} else if (doPing(NetworkData.getNetworkInfo().getType(),
				NetworkData.googleHost)) {
			resultCode = NET_HANDPOD_ERR;
		} else {
			resultCode = NET_SET_ERR;
		}

		return resultCode;
	}

	/**
	 * @param param
	 *            指定的域名如(www.google.com)或IP地址�?
	 */
	private static boolean doPing(int networkType, String hostAddressString) {
		// ip_devdiv = "";
		try {
			java.net.InetAddress x = java.net.InetAddress
					.getByName(hostAddressString);
			String ip_devdiv = x.getHostAddress();// 得到字符串形式的ip地址
			Log.d("TAG", ip_devdiv);

			if (ip_devdiv == null)
				return false;
			String[] parts = ip_devdiv.split("\\.");
			if (parts.length != 4) {
				// throw new UnknownHostException(ip_devdiv);
				return false;
			}

			int a = Integer.parseInt(parts[0]);
			int b = Integer.parseInt(parts[1]) << 8;
			int c = Integer.parseInt(parts[2]) << 16;
			int d = Integer.parseInt(parts[3]) << 24;

			int IP = a | b | c | d;
			ConnectivityManager connectivityManager = (ConnectivityManager) RuntimeManager
					.getSystemService(Application.CONNECTIVITY_SERVICE);

			return connectivityManager.requestRouteToHost(networkType, IP);
		} catch (Exception e) {
			return false;
		}

	}
}
