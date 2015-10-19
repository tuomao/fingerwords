package com.wsh.fingerwords.util;

import android.os.Environment;

public class Constant {
	public static String SDCARD_PATH =Environment
			.getExternalStorageDirectory().toString();
	// 程序存放在sd卡上地址
	public static final String APP_DATA_PATH = Environment
				.getExternalStorageDirectory() + "/wsh/fingerwords/";
	// mp3文件存放sd卡上文件夹
	public static final String SDCARD_AUDIO_PATH = "audio";
	//保存收藏单词发音的地址
	public static final String SDCARD_FAVWORD_AUDIO_PATH="word";
	//播放音频时快进，前进的时间
	public static final int SEEK_NEXT=5000;
	//播放音频时快退，后退的时间
	public static final int SEEK_PRE=-5000;
	//保存收藏单词发音的地址
	public static final String SCREENSHOT_IMAGE_PATH=Environment
			.getExternalStorageDirectory()+"/QQ_Screenshot/";
	public static final String ASSETS_TESSDATA_PATH = "tessdata";
	public static final String TESSBASE_PATH = Constant.APP_DATA_PATH
			+ "tessbase/";
	public static final String TESSDATA_PATH = TESSBASE_PATH+"tessdata";
	
}
