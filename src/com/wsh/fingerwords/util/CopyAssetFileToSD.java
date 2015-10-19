package com.wsh.fingerwords.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.wsh.fingerwords.listener.OnFinishedListener;

import android.content.Context;

/**
 * 
 * @author Administrator
 * 
 *         
 * 
 */
public class CopyAssetFileToSD {
	private Context mContext;
	private OnFinishedListener oflistener;
	private String targetDir;// 
	private int sourceFileNum;// 
	
	/**
	 * 
	 * 把assets目录下指定文件夹里的文件拷贝到指定sd卡目录下
	 * 
	 * @param context
	 * @param assetDir
	 *            assets目录下指定文件夹�?
	 * @param dir
	 *            sd卡目�?
	 */
	public CopyAssetFileToSD(Context context, String assetDir, String dir,
			OnFinishedListener oflistener) throws IOException {
		mContext = context;
		this.oflistener = oflistener;

		this.targetDir = dir;
		this.sourceFileNum = mContext.getResources().getAssets().list(assetDir).length;
		if (ListFileUtil.checkSubFiles(targetDir) == sourceFileNum) {
			oflistener.onFinishedListener();
			return;
		}
		CopyAssets(assetDir, dir);
		oflistener.onFinishedListener();
	}

	public CopyAssetFileToSD(Context context, String outputdir, String outfileName,
			String filePath, OnFinishedListener onFinishedListener)
			throws IOException {
		mContext = context;
		this.oflistener = onFinishedListener;
		copyFile(outputdir, outfileName, filePath);
	}

	public void copyFile(String outputdir, String outfileName, String filePath) {
		File dFile = new File(outputdir);
		if (!dFile.exists()) {
			dFile.mkdirs();
		}
		File outFile = new File(outputdir, outfileName);
		//Log.e("outFile",outFile.length()+"");
		if (outFile.exists() && outFile.length() > 1024 * 1024) {
			return;
		} else if (outFile.exists()) {
			outFile.delete();
		}
		if (!outFile.exists()) {
			try {
				boolean flag = outFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		InputStream in = null;
		try {
			in = mContext.getAssets().open(filePath);
			OutputStream out = new FileOutputStream(outFile);
			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();

			oflistener.onFinishedListener();
			return;
		} catch (IOException e) {
			oflistener.onErrorListener();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param assetDir
	 * @param dir
	 *            功能�?iYuba/jlpt3/image
	 */
	public void CopyAssets(String assetDir, String dir) {
		String[] files;
		File mWorkingPath = new File(dir);
		// if this directory does not exists, make one.
		// 判断/iYuba/jlpt3/image文件是否存在
		if (!mWorkingPath.exists()) {
			mWorkingPath.mkdirs();
		}

		try {
			files = mContext.getResources().getAssets().list(assetDir);
			// Log.e(" assets下文件数",
			// "当前assetDir�?+assetDir+"下文件数�?+files.length);
		} catch (IOException e1) {
			// oflistener.onFinishedListener();
			return;
		}

		for (int i = 0; i < files.length; i++) {
			try {
				String fileName = files[i];
				// we make sure file name not contains '.' to be a folder.
				if (!fileName.contains(".")) {
					if (0 == assetDir.length()) {
						CopyAssets(fileName, dir + "/" + fileName + "/");
					} else {
						CopyAssets(assetDir + "/" + fileName, dir + "/"
								+ fileName + "/");
					}
					continue;
				}
				File outFile = new File(mWorkingPath, fileName);
			
				//Log.e("outFile",outFile.length()+"");
				if (outFile.exists() && outFile.length() > 1024) {
					continue;
				} else if (outFile.exists()) {
					outFile.delete();
				}
				if (!outFile.exists()) {
					boolean flag = outFile.createNewFile();
				}

				InputStream in = null;
				if (0 != assetDir.length())
					in = mContext.getAssets().open(assetDir + "/" + fileName);
				else
					in = mContext.getAssets().open(fileName);
				OutputStream out = new FileOutputStream(outFile);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				in.close();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
