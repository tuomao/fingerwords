package com.wsh.fingerwords.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class CheckNetWork
{
  /**
   * 
   * @param mActivity
   * @return
   * ������������Ƿ����
   */
  public static boolean isNetworkAvailable(Context context) { 
    Context mContext=context;
    ConnectivityManager connectivity = (ConnectivityManager) context 
            .getSystemService(Context.CONNECTIVITY_SERVICE); 
    if (connectivity == null) { 
        return false; 
    } else { 
        NetworkInfo info = connectivity.getActiveNetworkInfo(); //getAllNetworkInfo()
        
        if (info != null && info.isConnected()) {
          return true;
        }
        return false; 
    } 
    
} 
  /**
   * 
   * 
   * 功能�?
   */
//  private void checkNetworkInfo()
//  {
////      ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//      //mobile 3G Data Network
//      State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
////      txt3G.setText(mobile.toString());
//      //wifi
//      State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
////      txtWifi.setText(wifi.toString());
//      
//      //如果3G网络和wifi网络都未连接，且不是处于正在连接状�? 则进入Network Setting界面 由用户配置网络连�?
//      if(mobile==State.CONNECTED||mobile==State.CONNECTING)
//          return;
//      if(wifi==State.CONNECTED||wifi==State.CONNECTING)
//          return;
//      
//      
////      startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//进入无线网络配置界面
//      //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //进入手机中的wifi网络设置界面
//      
//  }
  /**
   * 
   * @return
   * 功能：判断是否为wifi网络
   */
  public static boolean isWifi(Context context)
  {
//    Context context = mActivity.getApplicationContext(); 
    ConnectivityManager connectivity = (ConnectivityManager) context 
            .getSystemService(Context.CONNECTIVITY_SERVICE); 
    State wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
    if(wifi==State.CONNECTED||wifi==State.CONNECTING)
      return true;
    else {
      return false;
    }
  }

}
