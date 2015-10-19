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
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

import com.wsh.fingerwords.listener.DownLoadStatueChangeListener;

public class DownFile {
	private Context mContext;
	private URLConnection connection;
	private InputStream inputStream;
	private OutputStream outputStream;
	private String saveName;//文件的名称  包含后缀
	private String fileUrl;//文件的下载地址
	private String fileDir;//文件所在目录
	private DownLoadStatueChangeListener downLoadStatueChangeListener;
	private ProgressBar progressBar;
	public static int FILE_MIN_LENGTH=1024*4;//用于粗略的判断文件是否完整
	/**
	 * 
	 * 
	 * @param context
	 * @param urlString
	 * @param fileDir 包含/
	 * @param saveName
	 * @param downLoadStatueChangeListener
	 * @param progressBar
	 */
	public DownFile(Context context,String urlString,String fileDir, String saveName
			,DownLoadStatueChangeListener downLoadStatueChangeListener,ProgressBar progressBar){
		mContext=context;
		this.saveName = saveName;
		this.fileUrl =urlString;
		this.fileDir=fileDir;
		this.downLoadStatueChangeListener=downLoadStatueChangeListener;
		this.progressBar=progressBar;
	}
	public void DownLoadFile() {
	
		//文件的保存位置  fileDir 包含/
		String savePathString = fileDir+saveName;
		
		File tempfile = new File(savePathString);
		if(tempfile.exists()){
			tempfile.delete();
		}
		// 粗略的判断此文件是否下载完成  大小可自定义
		/*if (tempfile.exists() && tempfile.length() > FILE_MIN_LENGTH)// 说明文件已下载
		{
			if(downLoadStatueChangeListener!=null){
				if(progressBar!=null){
					downLoadStatueChangeListener.onFinishedListener(String.valueOf(progressBar.getProgress()));
				}else{
					downLoadStatueChangeListener.onFinishedListener(null);
				}
			}
			return;
		}else if(tempfile.exists()){
			tempfile.delete();
		}*/
		
		//链接到服务器  下载文件
		if (CheckNetWork.isNetworkAvailable(mContext)) {
			try {
				URL url = new URL(fileUrl);
				connection = url.openConnection();
				if (connection.getReadTimeout() >= 5) {
					if(downLoadStatueChangeListener!=null){
						if(progressBar!=null){
							downLoadStatueChangeListener.onErrorListener(String.valueOf(progressBar.getProgress()));
						}else{
							downLoadStatueChangeListener.onErrorListener(null);
						}
					}
					return;
				}
				inputStream = connection.getInputStream();
				if(downLoadStatueChangeListener!=null){
					if(progressBar!=null){
						downLoadStatueChangeListener.onStartListener(String.valueOf(progressBar.getProgress()));
					}else{
						downLoadStatueChangeListener.onStartListener(null);
					}
				}
				//Log.e("开始下载---------->connection.getReadTimeout()", "网络连接时间"+connection.getReadTimeout());
			
			} catch (MalformedURLException e1) {
				if(downLoadStatueChangeListener!=null){
					if(progressBar!=null){
						downLoadStatueChangeListener.onErrorListener(String.valueOf(progressBar.getProgress()));
					}else{
						downLoadStatueChangeListener.onErrorListener(null);
					}
				}
				e1.printStackTrace();
				return;
			
			} catch (IOException e) {
				if(downLoadStatueChangeListener!=null){
					if(progressBar!=null){
						downLoadStatueChangeListener.onErrorListener(String.valueOf(progressBar.getProgress()));
					}else{
						downLoadStatueChangeListener.onErrorListener(null);
					}
				}			
				e.printStackTrace();
				return;
			}
		} else {
			if(downLoadStatueChangeListener!=null){
				if(progressBar!=null){
					downLoadStatueChangeListener.onErrorListener(String.valueOf(progressBar.getProgress()));
				}else{
					downLoadStatueChangeListener.onErrorListener(null);
				}
			}
			return;
		}
		
		//检查文件的目录是否存在
		File file1 = new File(fileDir);
		if (!file1.exists()) {
			file1.mkdirs();
		}
		
		//开始向文件里面写东西
		File file = new File(savePathString);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(downLoadStatueChangeListener!=null){
					if(progressBar!=null){
						downLoadStatueChangeListener.onErrorListener(String.valueOf(progressBar.getProgress()));
					}else{
						downLoadStatueChangeListener.onErrorListener(null);
					}
				}
				e.printStackTrace();
				return;			
			}
		}
		try {
			//Log.e("writefilebbb", " "+FileLength);
			outputStream = new FileOutputStream(file);
			byte[] buffer = new byte[1024 * 4];
			int FileLength = connection.getContentLength();
			if(progressBar!=null){
				Message message=handler.obtainMessage(0, FileLength);
				handler.sendMessage(message);
			}
			int DownedFileLength=0;
			
			if (FileLength > 0)// 有连接
			{		
				while (DownedFileLength < FileLength) {
					//Log.e("writefile", " "+FileLength);
					int length = inputStream.read(buffer);
					DownedFileLength += length;
					outputStream.write(buffer, 0, length);
					//修改progresBar的进度
					if(progressBar!=null){
						Message message=handler.obtainMessage(1, DownedFileLength);
						handler.sendMessage(message);
					}
				}
				if(downLoadStatueChangeListener!=null){
					if(progressBar!=null){
						downLoadStatueChangeListener.onFinishedListener(String.valueOf(progressBar.getProgress()));
					}else{
						downLoadStatueChangeListener.onFinishedListener(null);
					}
				}
			}

			// reNameFile(savePathString, savePathString+".mp3");
		} catch (FileNotFoundException e) {
			if(downLoadStatueChangeListener!=null){
				if(progressBar!=null){
					downLoadStatueChangeListener.onErrorListener(String.valueOf(progressBar.getProgress()));
				}else{
					downLoadStatueChangeListener.onErrorListener(null);
				}
			}		
			e.printStackTrace();
			
		} catch (IOException e) {
			if(downLoadStatueChangeListener!=null){
				if(progressBar!=null){
					downLoadStatueChangeListener.onErrorListener(String.valueOf(progressBar.getProgress()));
				}else{
					downLoadStatueChangeListener.onErrorListener(null);
				}
			}		
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
	
	//调用这个接口开始下载文件
	public void startDownLoadFile(){
		ThreadManageUtil.sendRequest(new ThreadObject() {
			@Override
			public Object handleOperation() {
				// downloadStateListener.onStartListener();//开始下载
				DownLoadFile();
				return null;
			}
		});
	}	
	Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0://初始化progressbar的值
				
				if(progressBar!=null){
					progressBar.setMax(Integer.parseInt(msg.obj.toString()));
				}
				break;
			case 1://修改progressBar的进度
				if(progressBar!=null){
					progressBar.setProgress(Integer.parseInt(msg.obj.toString()));
				}
				 break;
			default:
				break;
			}
		}
	};
}
