package com.wsh.fingerwords;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.wsh.fingerwords.Manager.DataManager;
import com.wsh.fingerwords.adapter.FavWordAdapter;
import com.wsh.fingerwords.entity.FavoriteWord;

public class FavWordFragment extends Activity{

	private Context mContext;
	private ArrayList<FavoriteWord> favWordList;
	private FavWordAdapter adapter;
	// view控件
	private Button editButton;
	private ListView favWordListView;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fav_word);
		
		ini();
		findView();
		setView();		
	}

	public void ini() {
		mContext =this;
		favWordList = DataManager.Instance().favWordList;
		adapter = new FavWordAdapter(mContext, favWordList);
	}
	
	public void findView() {
		editButton = (Button)findViewById(R.id.fav_word_edit);
		favWordListView = (ListView) findViewById(R.id.fav_word_list);
	}

	public void setView() {
		editButton.setOnClickListener(onClickListener);
		favWordListView.setAdapter(adapter);
		favWordListView.setOnItemClickListener(onItemClickListener);
		
	}
	
	// 批量删除的监听器
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int state = adapter.getState();
			if (state == 1) {// 如果不处在批量删除的状态
				editButton.setText(R.string.favtitle_finish);
				adapter.setState(0);
			} else {// 处在批量删除的状态
				editButton.setText(R.string.favtitle_edit);
				adapter.setState(1);
			}
		}
	};
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			int state = adapter.getState();
			if (state == 1) {
				adapter.updateExplainView(arg2);
			} else if (state == 0) {
				adapter.updateView(arg2);
			}
		}
	};
}