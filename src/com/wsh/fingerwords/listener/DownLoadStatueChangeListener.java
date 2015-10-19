package com.wsh.fingerwords.listener;

public interface DownLoadStatueChangeListener {
	/**
	 * 
	 * 
	 * 功能：开始下载，得到下载进度
	 */
	public void onStartListener(String info);// 开始下载

	/**
	 * 
	 * 
	 * 功能：暂停下载
	 */
	public void onPausedListener(String info);// 暂停下载

	/**
	 * 
	 * 
	 * 功能：完成下载
	 */
	public void onFinishedListener(String info);// 完成

	/**
	 * 
	 * 
	 * 功能：下载出错，网络异常，异常中断。。。给出异常信息在主线程显示
	 */
	public void onErrorListener(String info);// 下载出错，网络异常，异常中断。。。
	
	/**
	 * 
	 * @return 功能：手动暂停
	 */
	public boolean isPausedListener();// 暂停

}
