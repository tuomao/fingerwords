package com.wsh.fingerwords.util;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.wsh.fingerwords.R;

/**
 * 
 * 图片文字识别的工具  只能识别bitma为argb8888的图片
 * @author tuomao
 *
 */
public class OcrUtil {
	private static final String TAG = "orc..";
	/**
	 * 
	 * 识别图片之中的文字的标准过程
	 */
	public static String recognizedTextFromImage(String language, Bitmap bitmap) {
		TessBaseAPI baseApi = new TessBaseAPI();
		String recognizedText = null;
		baseApi.setDebug(true);// 設置為debug模式，打印处理过程信息
		baseApi.init(Constant.TESSBASE_PATH, language);
		// 初始化tess
		// android下面，tessdata肯定得放到sd卡里了
		// 如果tessdata这个目录放在sd卡的根目录
		// 那么path直接传入sd卡的目录
		// eng就是英文，关于语言，按ISO 639-3标准的代码就行，具体请移步wiki
		// 初始化tesseract引擎
		baseApi.setImage(bitmap);// 设置要ocr的图片bitmap
		recognizedText = baseApi.getUTF8Text();// 获取识别的字符串
		Log.v("Kishore", "Kishore:" + recognizedText);
		baseApi.end();
		return recognizedText;
	}
}
