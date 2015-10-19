package com.wsh.fingerwords.wegdit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wsh.fingerwords.R;
import com.wsh.fingerwords.util.CheckNetWork;

public class PanelView extends RelativeLayout {

	private Context mContext;
	private LayoutInflater inflater;
	private String selectText;
	
	public WindowManager manager;
	public WindowManager.LayoutParams params;
	public RelativeLayout bgLayout;// 用来控制点击内容外面消失的面板
	public WordCard wordCard;// 用来展示类容的面板
	

	public PanelView(Context context,String selectText){
		super(context);
		mContext = context;
		this.selectText=selectText;
		ini();
		iniWindowManager();
		
	}
	
	public PanelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		ini();
		iniWindowManager();
		// TODO Auto-generated constructor stub
	}
	
	public PanelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		ini();
		iniWindowManager();
	}

	public void ini() {
		inflater = ((Activity) mContext).getLayoutInflater();
		// 将此viewinflate出來的view的父控件
		inflater.inflate(R.layout.wordpanel, this);
		getView();
		setView();
	}

	public void iniWindowManager() {
		manager = (WindowManager) mContext.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();
		// 这一项非常重要，用于设置背景透明
		params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		// params.alpha=0.8f;//用于设置窗体本身的透明度

		// params.gravity = Gravity.CENTER;
		params.gravity = Gravity.FILL;
		params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
				| WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
				| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
				| WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
				| WindowManager.LayoutParams.FLAG_FULLSCREEN;
		// // 设置window type,此控件位于打电话的那一层
		// 这两点是设置的关键的地方
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		// params.x = 50;
		// params.y = 50;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		manager.addView(this, params);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.e("out touch", "");
		return false;
	}

	public void getView() {
		bgLayout = (RelativeLayout) findViewById(R.id.bg_panel);
		wordCard = (WordCard) findViewById(R.id.word_card);
	}

	public void setView() {
		bgLayout.setBackgroundColor(Color.TRANSPARENT);
		wordCard.setOnTouchListener(onTouchListener);
		bgLayout.setOnClickListener(onClickListener);
		if(CheckNetWork.isNetworkAvailable(mContext)){
				wordCard.searchWord(selectText);
		}else{
			Toast.makeText(mContext, "网络连接不可用，请检查你的网络连接", 1000).show();
		}
	}

	private OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			}
			// 此处返回true，则点击的事件不会继续传递到父层。在content这一层必须要捕获相应的点击事件
			// 做相应的处理，不然会将事件传递到父层，发生事件的错误处理
			return true;
		}
	};
	
	/**
	 * 
	 * 在背景这一层捕获点击事件，溢出该view控件
	 */
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			manager.removeView(PanelView.this);
		}
	};

}
