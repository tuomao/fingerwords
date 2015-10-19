package com.wsh.fingerwords.wegdit;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wsh.fingerwords.R;

public class TitleBar extends RelativeLayout{
	private Context mContext;
	private Button menu;
	private TextView title;
	
	
	public TitleBar(Context context) {
		super(context);
		mContext=context;
		ini();
		// TODO Auto-generated constructor stub
	}
	//当没有设置自定义属性时，会调用这个构造函数,此构造函数必不可少
	public TitleBar(Context context, AttributeSet attributeSet){ 
		super(context, attributeSet);
		mContext=context;
		ini();
	}
	public TitleBar(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		ini();
	}
	
	public void setTitleText(String text){
		if(text!=null){
			title.setText(text);
		}
	}
	
	public void ini(){
		((Activity)mContext).getLayoutInflater().inflate(R.layout.title_bar, this);
		menu=(Button)findViewById(R.id.title_bar_menu);
		title=(TextView)findViewById(R.id.title);
	}
	public void setMenuOnclickListener(OnClickListener onClickListener){
		menu.setOnClickListener(onClickListener);
	}
}
