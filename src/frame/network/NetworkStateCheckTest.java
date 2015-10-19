package frame.network;

import java.net.UnknownHostException;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.wsh.fingerwords.R;

import frame.runtimedata.RuntimeManager;


public class NetworkStateCheckTest {

	/**
	 * 网络状�?OK
	 */
	public static final int NET_STATE_OK = 0;

	/**
	 * 网络不�?，建议尝试wifi
	 */
	public static final int NET_STATE_TRY_WIFI = 1;

	/**
	 * 网络不�?，建议尝�?G/GPRS
	 */
	public static final int NET_STATE_TRY_MOBILE = 2;

	/**
	 * 网络不�?，且无sim�?
	 */
	public static final int NET_STATE_NO_SIM = 3;

	/**
	 * 网络不�?，建议关闭wifi，尝�?G/GPRS
	 */
	public static final int NET_STATE_OFF_WIFI = 4;

	/**
	 * 网络不�?，可能是酣�?服务器问�?
	 */
	public static final int NET_STATE_HANDPOD_UNAVAILABLE = 5;

	/**
	 * 网络不�?，目前无法解�?
	 */
	public static final int NET_STATE_NET_UNAVAILABLE = 6;

	/**
	 * 网络不�?，建议尝试其他wifi接入�?
	 */
	public static final int NET_STATE_TRY_OTHERWIFI = 7;

	private int tryCount = 0;

	private int resultCode = 0;

	private NetworkInfo networkInfo;

	public NetworkStateCheckTest() {
		networkInfo = NetworkData.getNetworkInfo();
	}


	private String getIntIP(String host) {
		try {
			java.net.InetAddress x = java.net.InetAddress.getByName(host);
			String ip_devdiv = x.getHostAddress();// 得到字符串形式的ip地址
			Log.d("TAG", ip_devdiv);
			return ip_devdiv;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("TAG", "域名解析出错");
			return "";
		}
	}

	public int checkNetwork() {
		try {
			if (android.provider.Settings.System.getInt(RuntimeManager
					.getContext().getContentResolver(),
					android.provider.Settings.System.AIRPLANE_MODE_ON, 0) != 0) {
				resultCode = 10;
			} else if (networkInfo == null || !networkInfo.isAvailable()) {
				resultCode = NET_STATE_NET_UNAVAILABLE;
			} else if (networkInfo.getExtraInfo() != null
					&& networkInfo.getExtraInfo().equals("internet")) {
				resultCode = NET_STATE_OK;
			} else {
				boolean ping = doPing(networkInfo.getType(),
						NetworkData.handpodHeart);
				if (ping) {
					resultCode = NET_STATE_OK;
				} else {
					ping = doPing(networkInfo.getType(), NetworkData.googleHost);
					if (ping) {
						resultCode = NET_STATE_HANDPOD_UNAVAILABLE;
					} else if (networkInfo.getType() == 0) {// mobile
						resultCode = NET_STATE_TRY_WIFI;
					} else if (networkInfo.getType() == 1) {// wifi
						if (checkWifi()) {
							resultCode = NET_STATE_TRY_OTHERWIFI;
							// } else if (!checkSim()) {// 有无sim�?
							// resultCode = NET_STATE_NO_SIM;
						} else if (checkDataconnect()) {
							resultCode = NET_STATE_OFF_WIFI;
						} else {
							resultCode = NET_STATE_TRY_MOBILE;
						}
					}
				}
			}
		} catch (Exception e) {
			resultCode = NET_STATE_NET_UNAVAILABLE;
		}
		return resultCode;

	}

	private boolean checkSim() {
		TelephonyManager telephonyManager = (TelephonyManager) RuntimeManager
				.getSystemService(Context.TELEPHONY_SERVICE);

		return (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY);
	}

	private boolean checkWifi() {
		WifiManager wifiManager = (WifiManager) RuntimeManager
				.getSystemService(Context.WIFI_SERVICE);

		List<ScanResult> wifiList = wifiManager.getScanResults();
		return (wifiList != null && wifiList.size() > 1);
	}

	private boolean checkDataconnect() {
		State mobileState = ((ConnectivityManager) RuntimeManager
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		return (mobileState == State.CONNECTED || mobileState == State.CONNECTING);

	}

	/**
	 * @param param
	 *            指定的域名如(www.google.com)或IP地址�?
	 */
	private boolean doPing(int networkType, String hostAddressString) {
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

	public Builder getNetCheckDialog() {
		return getNetCheckDialog(resultCode);
	}

	public Builder getNetCheckDialog(int statecode) {

		if (statecode == NetworkStateCheckTest.NET_STATE_OK) {
			return null;
		}
		final Context context = RuntimeManager.getContext();

		Builder netStateAlertDialog = new AlertDialog.Builder(context)
				.setTitle(R.string.dialog_title_normal);

		DialogInterface.OnClickListener exitClickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				System.exit(0);
			}
		};

		DialogInterface.OnClickListener setClickListener = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				context.startActivity(new Intent(
						android.provider.Settings.ACTION_WIRELESS_SETTINGS));
				System.exit(0);

			}
		};

		DialogInterface.OnClickListener offWifiClickListener = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				WifiManager mWifiManager01 = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				mWifiManager01.setWifiEnabled(false);
				// isTryAgain = true;
				// handler.sendEmptyMessage(1);
			}
		};

		String messageString = "";
		if (statecode == NetworkStateCheckTest.NET_STATE_HANDPOD_UNAVAILABLE
				|| statecode == NetworkStateCheckTest.NET_STATE_NET_UNAVAILABLE) {
			messageString = RuntimeManager
					.getString(R.string.error_net_unavailable);// "网络错误，无法和服务器取得联系\n按\"�?��\"直接�?��酣�?畅游，稍后请重试";
			netStateAlertDialog.setNegativeButton(R.string.dialog_button_exit,
					exitClickListener);
		} else if (statecode == NetworkStateCheckTest.NET_STATE_TRY_OTHERWIFI) {
			messageString = RuntimeManager
					.getString(R.string.error_net_wifi_other);// "网络错误，无法和服务器取得联系\n按\"重设网络\"尝试选择其他Wifi接入点，稍后请重试\n按\"�?��\"直接�?��酣�?畅游，稍后请重试";
			netStateAlertDialog.setPositiveButton(
					R.string.dialog_button_resetNet, setClickListener)
					.setNegativeButton(R.string.error_net_wifi_other,
							exitClickListener);
		} else if (statecode == NetworkStateCheckTest.NET_STATE_TRY_WIFI) {
			// if (isTryAgain) {
			// messageString = "网络错误，无法和服务器取得联系\n按\"�?��\"直接�?��酣�?畅游，稍后请重试";
			// netStateAlertDialog.setNegativeButton("�?��", exitClickListener);
			// } else {
			messageString = RuntimeManager.getString(R.string.error_net_wifi);// "网络错误，无法和服务器取得联系\n按\"重设网络\"尝试使用Wifi接入点，稍后请重试\n按\"�?��\"直接�?��酣�?畅游，稍后请重试";
			netStateAlertDialog.setPositiveButton(
					R.string.dialog_button_resetNet, setClickListener)
					.setNegativeButton(R.string.dialog_button_exit,
							exitClickListener);
			// }
		} else if (statecode == NetworkStateCheckTest.NET_STATE_OFF_WIFI
				|| statecode == NetworkStateCheckTest.NET_STATE_TRY_MOBILE) {
			messageString = RuntimeManager
					.getString(R.string.error_net_wifi_off);// "网络错误，无法和服务器取得联系\n按\"重设网络\"选择其他Wifi接入点，稍后请重试\n按\"关闭Wifi\"关闭Wifi尝试使用3G/GPRS\n按\"�?��\"直接�?��酣�?畅游，稍后请重试";

			netStateAlertDialog.setPositiveButton(
					R.string.dialog_button_resetNet, setClickListener)
					.setNeutralButton(R.string.dialog_button_offWifi,
							offWifiClickListener).setNegativeButton(
							R.string.dialog_button_exit, exitClickListener);
		}

		netStateAlertDialog.setMessage(messageString);

		return netStateAlertDialog;
	}

	// private static String intToString(int i) {
	// return ((i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
	// + "." + ((i >> 24) & 0xFF));
	//
	// }
	//
	// private static int stringToInt(String addrString)
	// throws UnknownHostException {
	// try {
	// if (addrString == null)
	// return 0;
	// String[] parts = addrString.split("\\.");
	// if (parts.length != 4) {
	// throw new UnknownHostException(addrString);
	// }
	//
	// int a = Integer.parseInt(parts[0]);
	// int b = Integer.parseInt(parts[1]) << 8;
	// int c = Integer.parseInt(parts[2]) << 16;
	// int d = Integer.parseInt(parts[3]) << 24;
	//
	// return a | b | c | d;
	// } catch (NumberFormatException ex) {
	// throw new UnknownHostException(addrString);
	// }
	// }
	// /**
	// * @param param
	// * 指定的域名如(www.google.com)或IP地址�?
	// */
	// public void doPing2(final String param) {
	// new Thread() {
	// public void run() {
	// String line = "";
	// InputStream is = null;
	// try {
	// line = "ping -c 1 " + param;
	// // -c 1:表示ping的次数为1次�?
	// Process p = Runtime.getRuntime().exec(
	// "ping -c 1 www.google.com.hk");
	// // 等待该命令执行完毕�?
	// int status = p.waitFor();
	// if (status == 0) {
	// // 正常�?��
	// line += "Pass";
	// } else {
	// // 异常�?��
	// line += "Fail: Host unreachable";
	// }
	// // is = p.getInputStream();
	// // byte[] data = new byte;
	// // is.read(data);
	// // line += "" + new String(data);
	// } catch (UnknownHostException e) {
	// line += "Fail: Unknown Host";
	// } catch (IOException e) {
	// line += "Fail: IOException";
	// } catch (InterruptedException e) {
	// line += "Fail: InterruptedException";
	// }
	// // Message msg = new Message();
	// // msg.what = APPEND_TEXT;
	// // msg.obj = line;
	// // handler.sendMessage(msg);
	// Log.e("Ping", line);
	// }
	// }.start();
	// }
}
