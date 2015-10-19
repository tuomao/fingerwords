package com.wsh.fingerwords.listener;

public interface DownLoadStatusCallBack {
	public void downLoadSuccess(String localFilPath);
	public void downLoadFaild(String errorInfo);
	
}
