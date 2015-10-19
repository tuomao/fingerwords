package com.wsh.fingerwords.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.util.Log;


public class DownAudio {
	private Context mContext;
	private URLConnection connection;
	private InputStream inputStream;
	private OutputStream outputStream;
	private String saveName;
	private String fileUrl;
	
	public DownAudio(Context context,String urlString, String saveName){
		mContext=context;
		this.saveName = saveName;
		this.fileUrl =urlString;
	}
	public void DownFile() {

		// SDcard根目录//iyuba/toelflistening/audio/包名/音乐名称
		//音乐名称由titleNum+sound
		String savePathString = Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH
				+ "/" + Constant.SDCARD_FAVWORD_AUDIO_PATH + "/" + this.saveName;
		// Log.e("当前下载文件--保存路径",savePathString);
		File mp3File = new File(savePathString);

		// 粗略的判断此文件是否下载完成
		if (mp3File.exists() && mp3File.length() > 64)// 说明文件已下载
		{
			return;
		}

		// else
		File fileTemp = new File(savePathString);// +".4ma"
		if (fileTemp.exists() && fileTemp.length() > 0) {// 如果文件存在并且文件大小大于0，则该文件未下载完成，需要重新下载
			fileTemp.delete();// 删除原文件
			// downTests.remove(0);
		}

		// Log.e("下载音频-网络地址", fileUrl);
		// Log.e("下载音频-本地名称", saveName);
		/*
		 * 连接到服务器
		 */
		if (CheckNetWork.isNetworkAvailable(mContext)) {
			try {
				URL url = new URL(fileUrl);
				Log.e(fileUrl, "url");
				connection = url.openConnection();
				if (connection.getReadTimeout() >= 5) {
					return;
				}
				inputStream = connection.getInputStream();
				//Log.e("开始下载---------->connection.getReadTimeout()", "网络连接时间"+connection.getReadTimeout());
			
			} catch (MalformedURLException e1) {
				 //Log.e("下载音频", "error");
				// TODO Auto-generated catch block
				e1.printStackTrace();
				// downloadStateListener.onErrorListener(e1.getMessage());
				// downloadStateListener.onPausedListener();
			} catch (IOException e) {
				 //Log.e("下载音频", "error");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			return;
		}

		/*
		 * 文件的保存路径和和文件名其中Nobody.mp3是在手机SD卡上要保存的路径，如果不存在则新建
		 */
		String savePAth = Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH
				+ "/" + Constant.SDCARD_FAVWORD_AUDIO_PATH;
		File file1 = new File(savePAth);
		if (!file1.exists()) {
			file1.mkdirs();
		}
		// Log.e("下载音频-本地名称(完整)", savePathString);
		File file = new File(savePathString);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			//Log.e("writefilebbb", " "+FileLength);
			outputStream = new FileOutputStream(file);
			byte[] buffer = new byte[1024 * 4];
			int FileLength = connection.getContentLength();
			int DownedFileLength=0;
			if (FileLength > 0)// 有连接
			{
				
				while (DownedFileLength < FileLength) {
					//Log.e("writefile", " "+FileLength);
					int length = inputStream.read(buffer);
					DownedFileLength += length;
					outputStream.write(buffer, 0, length);
					
					// message1.what = 1;
					// handler.sendEmptyMessage(1);
				}
			}

			// reNameFile(savePathString, savePathString+".mp3");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			// handler.sendEmptyMessage(3);
			//ConfigManager.Instance().putInt(saveName, 0); // 保存进度，用于列表识别进度条
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// handler.sendEmptyMessage(3);
			//ConfigManager.Instance().putInt(saveName, 0); // 保存进度，用于列表识别进度条
			e.printStackTrace();
		} finally {
			try {
				
				inputStream.close();
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void startDownLoadAudio(){
		ThreadManageUtil.sendRequest(new ThreadObject() {
			@Override
			public Object handleOperation() {
				// downloadStateListener.onStartListener();//开始下载
				DownFile();
				return null;
			}
		});
	}
}
