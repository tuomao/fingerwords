package com.wsh.fingerwords.util;

import fiji.threshold.Auto_Threshold;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

public class AutoThreshold {
	public static int Get1DMaxEntropyThreshold(int[] HistGram) {
		int X, Y, Amount = 0;
		double[] HistGramD = new double[256];
		double SumIntegral, EntropyBack, EntropyFore, MaxEntropy;
		int MinValue = 255, MaxValue = 0;
		int Threshold = 0;

		for (MinValue = 0; MinValue < 256 && HistGram[MinValue] == 0; MinValue++)
			;
		for (MaxValue = 255; MaxValue > MinValue && HistGram[MinValue] == 0; MaxValue--)
			;
		if (MaxValue == MinValue)
			return MaxValue; // 图像中只有一个颜色
		if (MinValue + 1 == MaxValue)
			return MinValue; // 图像中只有二个颜色

		for (Y = MinValue; Y <= MaxValue; Y++)
			Amount += HistGram[Y]; // 像素总数

		for (Y = MinValue; Y <= MaxValue; Y++)
			HistGramD[Y] = (double) HistGram[Y] / Amount + 1e-17;

		MaxEntropy = Double.MIN_VALUE;
		;
		for (Y = MinValue + 1; Y < MaxValue; Y++) {

			SumIntegral = 0;
			for (X = MinValue; X <= Y; X++)
				SumIntegral += HistGramD[X];
			EntropyBack = 0;
			for (X = MinValue; X <= Y; X++)
				EntropyBack += (-HistGramD[X] / SumIntegral * Math
						.log(HistGramD[X] / SumIntegral));
			EntropyFore = 0;
			for (X = Y + 1; X <= MaxValue; X++)
				EntropyFore += (-HistGramD[X] / (1 - SumIntegral) * Math
						.log(HistGramD[X] / (1 - SumIntegral)));
			if (MaxEntropy < EntropyBack + EntropyFore) {
				Threshold = Y;
				MaxEntropy = EntropyBack + EntropyFore;
			}
		}
		return Threshold;
	}

	/***
	 * OSTU大律法 对于比较平坦的直方图有较好的适应能力
	 * 
	 * @param HistGram
	 *            直方图
	 * @return 阀值
	 */
	public static int GetOSTUThreshold(int[] HistGram) {
		int X, Y, Amount = 0;
		int PixelBack = 0, PixelFore = 0, PixelIntegralBack = 0, PixelIntegralFore = 0, PixelIntegral = 0;
		double OmegaBack, OmegaFore, MicroBack, MicroFore, SigmaB, Sigma; // 类间方差;
		int MinValue, MaxValue;
		int Threshold = 0;

		for (MinValue = 0; MinValue < 256 && HistGram[MinValue] == 0; MinValue++)
			;
		for (MaxValue = 255; MaxValue > MinValue && HistGram[MinValue] == 0; MaxValue--)
			;
		if (MaxValue == MinValue)
			return MaxValue; // 图像中只有一个颜色
		if (MinValue + 1 == MaxValue)
			return MinValue; // 图像中只有二个颜色

		for (Y = MinValue; Y <= MaxValue; Y++)
			Amount += HistGram[Y]; // 像素总数

		PixelIntegral = 0;
		for (Y = MinValue; Y <= MaxValue; Y++)
			PixelIntegral += HistGram[Y] * Y;
		SigmaB = -1;
		for (Y = MinValue; Y < MaxValue; Y++) {
			PixelBack = PixelBack + HistGram[Y];
			PixelFore = Amount - PixelBack;
			OmegaBack = (double) PixelBack / Amount;
			OmegaFore = (double) PixelFore / Amount;
			PixelIntegralBack += HistGram[Y] * Y;
			PixelIntegralFore = PixelIntegral - PixelIntegralBack;
			MicroBack = (double) PixelIntegralBack / PixelBack;
			MicroFore = (double) PixelIntegralFore / PixelFore;
			Sigma = OmegaBack * OmegaFore * (MicroBack - MicroFore)
					* (MicroBack - MicroFore);
			if (Sigma > SigmaB) {
				SigmaB = Sigma;
				Threshold = Y;
			}
		}
		return Threshold;
	}

	/**
	 * 
	 * 自适应二维二值化算法 
	 * http://blog.csdn.net/hhygcy/article/details/4280165
	 * http://www.cnblogs.com/Imageshop/archive/2013/04/22/3036127.html
	 * 
	 * @param greyInfos 图像的灰度信息
	 * @param width 图像的宽度
	 * @param height 图像的高度
	 * @return
	 * 这里必须传入的是图像的灰度信息
	 */
	public static Bitmap adaptiveThreshold(int[] greyInfos, int width,
			int height) {
		
		int S = width>>3; // 扫描的范围为宽度的1/8
		int T = 15; // 阀值 像素点的像素值为周围亮度的可减少的百分比
		int[] bin = new int[width * height];// 输出的二值化的位图信息
		long[] integralImg = new long[width * height];// 用于保存到f(i,j)这个矩形的所有像素的和
		int i, j;
		long sum = 0; // 当前行的像素和
		int count = 0; // SxS矩形的像素点的个数
		int index;
		int x1, y1, x2, y2;
		int s2 = S / 2;// 扫描半径

		for (i = 0; i < width; i++) {
			// 由例開始计算
			// reset this column sum
			sum = 0;
			for (j = 0; j < height; j++) {
				index = j * width + i;
				sum += greyInfos[index];
				if (i == 0)
					integralImg[index] = sum;
				else
					integralImg[index] = integralImg[index - 1] + sum;
			}
		}
		// perform thresholding
		for (i = 0; i < width; i++) {
			for (j = 0; j < height; j++) {
				index = j * width + i;
				// set the SxS region
				x1 = i - s2;
				x2 = i + s2;
				y1 = j - s2;
				y2 = j + s2;
				// check the border
				if (x1 < 0)
					x1 = 0;
				if (x2 >= width)
					x2 = width - 1;
				if (y1 < 0)
					y1 = 0;
				if (y2 >= height)
					y2 = height - 1;
				// 计算像素点的个数
				count = (x2 - x1) * (y2 - y1);
				// 计算像素点的和
				sum = integralImg[y2 * width + x2]
						- integralImg[y1 * width + x2]
						- integralImg[y2 * width + x1]
						+ integralImg[y1 * width + x1];
				//Log.e("sum", sum + " " + greyInfos[index]);

				if ((long) (greyInfos[index] * count) < (long) (sum * (100 - T) / 100)) {
					bin[index] = Color.rgb(0, 0, 0);
				} else {
					bin[index] = Color.rgb(255, 255, 255);
				}
			}
		}
		Bitmap result = Bitmap.createBitmap(width, height, Config.RGB_565);
		result.setPixels(bin, 0, width, 0, 0, width, height);
		return result;
	}
}
