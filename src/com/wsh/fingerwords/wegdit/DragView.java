package com.wsh.fingerwords.wegdit;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.wsh.fingerwords.R;
import com.wsh.fingerwords.listener.OnActionListener;

public class DragView extends Button {

	private Context mContext;
	public WindowManager manager;
	public WindowManager.LayoutParams params;
	private float mTouchStartX, mTouchStartY;// down动作下去的相对该view的位置
	private int x, y;// 相对屏幕的位置
	private float statusBarHigh;// 状态栏高度
	private long startTime = 0, endTime = 0;// 记录点击操作的开始和结束的时间
	private int GAP = 100;
	private int startx, starty;
	private OnActionListener onActionListener;
	private LayoutParams layoutParams;
	private LayoutInflater layoutInflater;
	
	public DragView(Context context,OnActionListener onActionListener) {
		super(context);
		mContext = context;
		this.onActionListener=onActionListener;
		
		setBackgroundResource(R.drawable.textnone);
		//setLayoutParams(lp);
		//((Activity) mContext).getLayoutInflater().inflate(R.layout.activity_gt,
			//	this);
		ini();
		iniData();
		// TODO Auto-generated constructor stub
	}
	/**
	 * 初始化位置等数据
	 */
	public void iniData(){
		mTouchStartX=0;
		mTouchStartY=0;
		x=0;
		y=0;
		statusBarHigh=getStutsBarHigh(mContext);
		startTime=0;
		endTime=0;
		startx=0;
		starty=0;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		// 获取相对屏幕的坐标，即以屏幕左上角为原点
		x = (int) event.getRawX();
		y = (int) (event.getRawY());
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 获取相对View的坐标，即以此View左上角为原点
			mTouchStartX = event.getX();
			mTouchStartY = event.getY();
			startx = (int) event.getRawX();
			starty = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			updateViewPosition();
			break;
		case MotionEvent.ACTION_UP:
			updateViewPosition();
			//如果拉动的距离少于10像素，则视为点击操作，调用onclick
			if (Math.sqrt(Math.pow(x - startx, 2) + Math.pow(y - starty, 2)) <=10){
				if(onActionListener!=null){
					Log.e("vhh", params.horizontalWeight+" "+params.horizontalMargin+
							" "+params.verticalWeight+" "+params.verticalMargin);
					onActionListener.onClick(params.x,params.y);
				}
			}
			//动作完成重新初始化数据，为下一次事件调用准备
			iniData();
			break;
		}
		return false;
	}

	/**
	 * 
	 * 更新view的位置
	 */
	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		params.x = (int) (x - mTouchStartX);
		params.y = (int) (y - mTouchStartY - statusBarHigh);// 减去状态栏的高度。后面介绍原因和获得办法。
		// 更新浮动窗口
		manager.updateViewLayout(this, params);
	}

	/**
	 * 
	 * 通过反射获取状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStutsBarHigh(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0;
		int sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
		}
		return sbar;
	}

	public void ini() {
		manager = (WindowManager) mContext.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();

		params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
				| WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
		// // 设置window type,此控件位于打电话的那一层
		// 这两点是设置的关键的地方
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		params.x = 50;
		params.y = 50;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;

		statusBarHigh = getStutsBarHigh(mContext);
		manager.addView(this, params);
	}
	
}
