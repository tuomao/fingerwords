package com.wsh.fingerwords;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.wsh.fingerwords.Manager.DataManager;
import com.wsh.fingerwords.listener.OnActionListener;
import com.wsh.fingerwords.listener.OnFinishedListener;
import com.wsh.fingerwords.sqlite.ZDBHelper;
import com.wsh.fingerwords.sqlite.ZDBManager;
import com.wsh.fingerwords.util.Constant;
import com.wsh.fingerwords.util.CopyAssetFileToSD;
import com.wsh.fingerwords.util.ImageUtil;
import com.wsh.fingerwords.util.OcrUtil;
import com.wsh.fingerwords.util.SDCard;
import com.wsh.fingerwords.wegdit.DragView;
import com.wsh.fingerwords.wegdit.PanelView;

import frame.runtimedata.RuntimeManager;

public class WindowManagerTest extends Activity {

	private Context mContext;
	private WindowManager manager;
	private WindowManager.LayoutParams params;
	private PanelView panelView;
	
	private Button button2;
	
	private float mTouchStartX;// 开始点击位置x
	private float mTouchStartY;// 开始点击位置y
	private float statusBarHigh = 0;// 状态栏高度
	private int x, y;
	private int startx, starty;
	private Button button;
	private DragView view;
	private long stime = 0, etime = 0;
	private Bitmap bitmap;
	private ZDBHelper helper;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_window_manager_test);
		mContext = this;
		RuntimeManager.setApplication(getApplication());
		RuntimeManager.setDisplayMetrics(this);
		iniDatabse();
		copyTessdataToSDcard();
		
		
		manager = (WindowManager) getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		bitmap=getSelectWordBitmap();
		button2=(Button)findViewById(R.id.button);
		button2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DataManager.Instance().favWordList=helper.getFavoriteWords();
				Intent intent=new Intent();
				intent.setClass(mContext, FavWordFragment.class);
				startActivity(intent);
			}
		});
		
		view=new DragView(mContext,new OnActionListener() {
			
			@Override
			public void onSelect(int x,int y) {
				// TODO Auto-generated method stub			
			}
			@Override
			public void onClick(int x,int y) {
				// TODO Auto-generated method stub
				bitmap=getSelectWordBitmap();
				if(bitmap!=null){
					//将图像转化为灰度图
					bitmap=ImageUtil.convertGreyImg(bitmap);
					//将图像二值化
					bitmap=ImageUtil.getOstaBinaryBitmap(bitmap);
					String selectWord=OcrUtil.recognizedTextFromImage("eng", bitmap);
					if(selectWord.length()!=0){
						//只取出其中的英文字符
						selectWord=selectWord.replaceAll("[^a-zA-Z]", "");
						if(selectWord.length()!=0){
							panelView=new PanelView(mContext,selectWord);
						}
					}else{
						Toast.makeText(mContext, "未能识别出单词，请重新选择单词", 1000).show();
					}
				}else{
					Toast.makeText(mContext, "您尚未选择任何单词", 1000).show();	
				}
			}
		});	 
		
		helper=new ZDBHelper(mContext);
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		manager.removeView(view);
		super.onDestroy();
	}
	
	/**
	 * 
	 * 将截图出来的图片转化为bitmap
	 * 
	 */
	public Bitmap getSelectWordBitmap(){
		Bitmap bitmap=null;
		File file=new File(Constant.SCREENSHOT_IMAGE_PATH);
		if(file.exists()){
			String[] strings=file.list();
			if(strings.length>0){
				String timeString=strings[strings.length-1];
				timeString=timeString.substring(0, timeString.indexOf("."));
				if(Math.abs(System.currentTimeMillis()- Long.parseLong(timeString))<1000*10){
					String selectWordImagePath=Constant.SCREENSHOT_IMAGE_PATH
						+strings[strings.length-1];
					bitmap=BitmapFactory.decodeFile(selectWordImagePath);
				}
			}
		}
		return bitmap;
	}
	
	public void copyTessdataToSDcard(){
		if (SDCard.hasSDCard())
	    {
	    try
	    {
	    	
	      CopyAssetFileToSD copyFileToSD = new CopyAssetFileToSD(mContext, Constant.ASSETS_TESSDATA_PATH, 
	          Constant.TESSDATA_PATH,
	          new OnFinishedListener() {
				
				@Override
				public void onFinishedListener() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onErrorListener() {
					// TODO Auto-generated method stub
					
				}
	      });
	    }
	    catch (IOException e)
	    {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	    }else {
	     
	    }	
	}
	public void iniDatabse(){
		ZDBManager manager=new ZDBManager(mContext);
		manager.openDatabase();
		manager.closeDatabase();
	}
}
