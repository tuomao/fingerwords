package com.wsh.fingerwords.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fiji.threshold.Auto_Threshold;

import android.R.fraction;
import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ImageUtil {
	private static String tag = "IMAGE_UTIL";

	
	// 放大缩小图片
	/**
	 * 
	 * 
	 * @param bitmap
	 * @param w
	 *            缩放后的图片的宽度
	 * @param h
	 *            缩放后图片的高度
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		// 这个函数传入的是方缩短的比例
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	// 将Drawable转化为Bitmap
	/**
	 * 将Drawable转化为Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		// bitmapconfig 决定了位图的存储格式和质量
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	// 获得圆角图片的方法
	/**
	 * 
	 * 
	 * @param bitmap
	 * @param roundPx
	 *            圆角半径的的大小
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	// 获得带倒影的图片方法
	/***
	 * 获得带倒影的图片方法
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		return bitmapWithReflection;
	}

	// 压缩图片大小
	/**
	 * 
	 * image.compress(Bitmap.CompressFormat.JPEG, 100, baos); //
	 * 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	 * 
	 * @param image
	 * @param type
	 * @param quality
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image, CompressFormat format,
			int quality) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(format, quality, baos);
		// image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
		// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 将彩色图转换为灰度图
	 * 
	 * @param img
	 *            位图
	 * @return 返回转换好的位图
	 */
	public static Bitmap convertGreyImg(Bitmap img) {

		int width = img.getWidth(); // 获取位图的宽
		int height = img.getHeight(); // 获取位图的高

		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

		// 参数的意义 getPixels (int[] pixels, int offset, int stride, int x, int y,
		// int width, int height)
		/**
		 * 
		 * 1.piexls 获取的像素的信息放置的数组 2.offect 开始放置置像素的第一个为止 eg：offect=0，代表从
		 * piexls[0] 3.stride 每次在进行下一行的像素时，应该加的长度 stride>=width eg： stride=10 则
		 * 第一行为piexls[0,9],第二行为piexls[10,19];
		 */
		img.getPixels(pixels, 0, width, 0, 0, width, height);
		// int alpha = 0xFF << 24;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];
				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);
				
				// 计算灰度，用整数来算，减少浮点数的运算，灰度也称为灰街，直方图用此来统计
				grey = (int) ((red * 30 + green * 59 + blue * 11) / 100);
				// Color。reb(grey,grey,grey)表示灰度图。 这里加上了透明度
				grey = Color.rgb(grey, grey, grey);
				pixels[width * i + j] = grey;
			}
		}
		/*
		 * int threshhold=GetOSTUThreshold(); Log.e(tag, threshhold+"");
		 * 
		 * for(int i=0;i<width*height;i++){ Log.e("piex", pixels[i]+"");
		 * if(pixels[i]>=threshhold){ pixels[i]=Color.rgb(255, 255, 255); }else{
		 * pixels[i]=Color.rgb(0, 0, 0); } } for(int i=0;i<255;i++){
		 * Log.e("his", HistGram[i]+""); }
		 */
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		result.setPixels(pixels, 0, width, 0, 0, width, height);
		return result;
	}

	// 获取图像的灰阶信息
	public static void getGreyInfos(Bitmap img, int[] greyInfos) {
		int width = img.getWidth(); // 获取位图的宽
		int height = img.getHeight(); // 获取位图的高

		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组
		// 参数的意义 getPixels (int[] pixels, int offset, int stride, int x, int y,
		// int width, int height)
		/**
		 * 
		 * 1.piexls 获取的像素的信息放置的数组 
		 * 2.offect 开始放置置像素的第一个为止 eg：offect=0，代表从
		 * piexls[0] 
		 * 3.stride 每次在进行下一行的像素时，应该加的长度 stride>=width eg： stride=10 则
		 * 第一行为piexls[0,9],第二行为piexls[10,19];
		 */
		img.getPixels(pixels, 0, width, 0, 0, width, height);
		// int alpha = 0xFF << 24;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];

				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);

				// 计算灰度，用整数来算，减少浮点数的运算，灰度也称为灰街，直方图用此来统计
				grey = (int) ((red * 30 + green * 59 + blue * 11) / 100);
				// Color。reb(grey,grey,grey)表示灰度图。 这里加上了透明度

				greyInfos[width * i + j] = grey;
			}
		}
	}
	/**
	 * http://www.cnblogs.com/Imageshop/archive/2013/04/22/3036127.html
	 * 通过osta的方法获取二值化图像
	 * @param img
	 * @return
	 */
	public static Bitmap getOstaBinaryBitmap(Bitmap img){
		int width = img.getWidth(); // 获取位图的宽
		int height = img.getHeight(); // 获取位图的高
		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组
		int[] HistGram=new int[256];//直方图信息  统计每一个灰阶的像素点的个数总数
		int[] greyInfos=new int[width*height];
		//对直方图赋初始值
		for(int i=0;i<256;i++){
			HistGram[i]=0;
		}
		img.getPixels(pixels, 0, width, 0, 0, width, height);
		// int alpha = 0xFF << 24;
		//获取灰度信息
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];
				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);
				
				// 计算灰度，用整数来算，减少浮点数的运算，灰度也称为灰街，直方图用此来统计
				grey = (int) ((red * 30 + green * 59 + blue * 11) / 100);
				//相应灰阶加1
				HistGram[grey]++;
				//保存该响度点的灰度信息
				greyInfos[width * i + j]=grey;
			}
		}
		int threshold=AutoThreshold.GetOSTUThreshold(HistGram);
		for(int i=0;i<width*height;i++){
			if(greyInfos[i]>threshold){
				//通过比较当前的灰阶和阀值信息，得出二值化图像
				pixels[i]=Color.rgb(255, 255, 255);
			}else{
				pixels[i]=Color.rgb(0, 0, 0);
			}
		}
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		result.setPixels(pixels, 0, width, 0, 0, width, height);
		return result;
	}
	/**
	 * 
	 * 通过局部比较的自适应算法获取图像的二值化图像
	 * @param img
	 * @return
	 */
	public static Bitmap getAdaptiveBinaryBitmap(Bitmap img){
		int width = img.getWidth(); // 获取位图的宽
		int height = img.getHeight(); // 获取位图的高
		int[] greyInfos=new int[width*height];
		//获取图像的灰度信息
		getGreyInfos(img, greyInfos);
		//通过局部的自适应算法，获取图像的二值化图像
		Bitmap result=AutoThreshold.adaptiveThreshold(greyInfos, width, height);
		return result;
	}

	// 图片Url保存为位图并进行缩放操作
	// 通过传入图片url获取位图方法
	public static Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.v(tag, bitmap.toString());

		return bitmap;
	}

	/***
	 * 加载本地图片
	 * 
	 * @param context
	 *            ：主运行函数实例
	 * @param bitAdress
	 *            ：图片地址，一般指向R下的drawable目录
	 * @return
	 */
	public static Bitmap CreatImage(Context context, int bitAdress) {
		Bitmap bitmaptemp = null;
		bitmaptemp = BitmapFactory.decodeResource(context.getResources(),
				bitAdress);
		return bitmaptemp;
	}

	/***
	 * 2.图片平均分割方法，将大图平均分割为N行N列，方便用户使用 图片分割
	 * 
	 * @param g
	 *            ：画布
	 * @param paint
	 *            ：画笔
	 * @param imgBit
	 *            ：图片
	 * @param x
	 *            ：X轴起点坐标
	 * @param y
	 *            ：Y轴起点坐标
	 * @param w
	 *            ：单一图片的宽度
	 * @param h
	 *            ：单一图片的高度
	 * @param line
	 *            ：第几列
	 * @param row
	 *            ：第几行
	 */
	public static void cuteImage(Canvas g, Paint paint, Bitmap imgBit, int x,
			int y, int w, int h, int line, int row) {
		g.clipRect(x, y, x + w, h + y);
		g.drawBitmap(imgBit, x - line * w, y - row * h, paint);
		g.restore();
	}

	/***
	 * 4.绘制带有边框的文字，一般在游戏中起文字的美化作用 绘制带有边框的文字
	 * 
	 * @param strMsg
	 *            ：绘制内容
	 * @param g
	 *            ：画布
	 * @param paint
	 *            ：画笔
	 * @param setx
	 *            ：：X轴起始坐标
	 * @param sety
	 *            ：Y轴的起始坐标
	 * @param fg
	 *            ：前景色
	 * @param bg
	 *            ：背景色
	 */
	public static void drawText(String strMsg, Canvas g, Paint paint, int setx,
			int sety, int fg, int bg) {
		paint.setColor(bg);
		g.drawText(strMsg, setx + 1, sety, paint);
		g.drawText(strMsg, setx, sety - 1, paint);
		g.drawText(strMsg, setx, sety + 1, paint);
		g.drawText(strMsg, setx - 1, sety, paint);
		paint.setColor(fg);
		g.drawText(strMsg, setx, sety, paint);
		g.restore();
	}
	/**
	 * 
	 * 通过传人bitmap，对指定的bitmap进行剪切
	 * @param bitmap 源bitmap
	 * @param x   剪切的起始位置的左边
	 * @param y
	 * @param width  剪切的长度和宽度
	 * @param height
	 * @return
	 */
	public static Bitmap cuteBitmap(Bitmap bitmap,int x,int y,int width,int height){
		Bitmap resultBitmap=Bitmap.createBitmap(bitmap, x, y, width, height);
		return resultBitmap;
	}

}
