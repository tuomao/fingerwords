package com.wsh.fingerwords.wegdit;

import java.io.File;
import java.util.ArrayList;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wsh.fingerwords.FavWordFragment;
import com.wsh.fingerwords.R;
import com.wsh.fingerwords.Manager.DataManager;
import com.wsh.fingerwords.entity.FavoriteWord;
import com.wsh.fingerwords.protocol.DictRequest;
import com.wsh.fingerwords.protocol.DictResponse;
import com.wsh.fingerwords.sqlite.ZDBHelper;
import com.wsh.fingerwords.util.Constant;
import com.wsh.fingerwords.util.DownAudio;
import com.wsh.fingerwords.util.Player;

import frame.network.ClientSession;
import frame.network.IResponseReceiver;
import frame.protocol.BaseHttpRequest;
import frame.protocol.BaseHttpResponse;

public class WordCard extends LinearLayout {
	private Context mContext;
	private ZDBHelper helper;
	LayoutInflater layoutInflater;
	private Button add_word, close_word;
	private ProgressBar progressBar_translate;
	private String selectText;
	private TextView key, pron, def, example;
	private Typeface mFace;
	private FavoriteWord selectCurrWordTemp;
	private ImageView speaker;
	private DownAudio downAudio;
	private Player player;

	public WordCard(Context context) {
		this(context, null);
		mContext = context;
		((Activity) mContext).getLayoutInflater().inflate(R.layout.wordcard,
				this);
		initGetWordMenu();

	}

	public WordCard(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mContext = context;
		((Activity) mContext).getLayoutInflater().inflate(R.layout.wordcard,
				this);
		initGetWordMenu();
	}

	private void initGetWordMenu() {
		progressBar_translate = (ProgressBar) findViewById(R.id.progressBar_get_Interperatatior);
		key = (TextView) findViewById(R.id.word_key);
		pron = (TextView) findViewById(R.id.word_pron);
		def = (TextView) findViewById(R.id.word_def);
		example = (TextView) findViewById(R.id.example);
		mFace = Typeface.createFromAsset(mContext.getAssets(),
				"fonts/SEGOEUI.TTF");
		speaker = (ImageView) findViewById(R.id.word_speaker);
		add_word = (Button) findViewById(R.id.word_add);
		add_word.setOnClickListener(new OnClickListener() { // 添加到生词本

			@Override
			public void onClick(View v) {
				saveNewWords(selectCurrWordTemp);
			}
		});
		close_word = (Button) findViewById(R.id.word_close);
		close_word.setText("关闭");
		close_word.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				/*
				Intent intent=new Intent();
				intent.setClass(mContext, FavWordFragment.class);
				mContext.startActivity(intent);*/
				setVisibility(View.GONE);
			}
		});
	}
	
	/**
	 * 获取单词释义
	 */
	private void getNetworkInterpretation() {
		if (selectText != null && selectText.length() != 0) {
			ClientSession.Instace().asynGetResponse(
					new DictRequest(selectText), new IResponseReceiver() {

						@Override
						public void onResponse(BaseHttpResponse response,
								BaseHttpRequest request, int rspCookie) {
							DictResponse dictResponse = (DictResponse) response;
							selectCurrWordTemp = dictResponse.word;
							if (selectCurrWordTemp != null) {
								if (selectCurrWordTemp.def != null
										&& selectCurrWordTemp.def.length() != 0) {
									handler.sendEmptyMessage(1);
								} else {
									handler.sendEmptyMessage(2);
								}
							} else {
							}
						}
					}, null, null);
		} else {
			Toast.makeText(mContext, R.string.play_please_take_the_word, 1000)
					.show();
		}
	}

	public void showWordDefInfo() {
		key.setText(selectCurrWordTemp.Word);
		pron.setTypeface(mFace);
		if (selectCurrWordTemp.pron != null
				&& selectCurrWordTemp.pron.length() != 0) {
			pron.setText(Html.fromHtml("[" + selectCurrWordTemp.pron + "]"));
		}
		def.setText(selectCurrWordTemp.def);
		example.setText(Html.fromHtml(selectCurrWordTemp.examples));
		example.setMovementMethod(ScrollingMovementMethod.getInstance());
		example.setText(Html.fromHtml(selectCurrWordTemp.examples));
		if (selectCurrWordTemp.audio != null
				&& selectCurrWordTemp.audio.length() != 0) {
			speaker.setVisibility(View.VISIBLE);
		} else {
			speaker.setVisibility(View.GONE);
		}
		speaker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				player = new Player(mContext, null);
				String url = Constant.APP_DATA_PATH
						+ Constant.SDCARD_AUDIO_PATH + "/"
						+ Constant.SDCARD_FAVWORD_AUDIO_PATH + "/"
						+ selectCurrWordTemp.Word + ".mp3";
				File file = new File(url);
				if (!file.exists()) {
					url = selectCurrWordTemp.audio;
				}
				player.playUrl(url);
			}
		});
		add_word.setVisibility(View.VISIBLE); // 选词的同事隐藏加入生词本功能
		progressBar_translate.setVisibility(View.GONE); // 显示等待
	}

	private void saveNewWords(FavoriteWord wordTemp) {
		try {

			helper = new ZDBHelper(mContext);
			boolean flag = helper.saveFavoriteWord(wordTemp);
			if (DataManager.Instance().favWordList == null) {
				ArrayList<FavoriteWord> words = helper.getFavoriteWords();
				DataManager.Instance().favWordList = words;
			} else if (flag) {
				DataManager.Instance().favWordList.add(wordTemp);
				Toast.makeText(mContext, R.string.play_ins_new_word_success,
						1000).show();
			} else if (!flag) {
				Toast.makeText(mContext, R.string.play_ins_new_word_exist, 1000)
						.show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void searchWord(String word) {
		selectText = word;
		getNetworkInterpretation();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				showWordDefInfo();
				downAudio = new DownAudio(mContext, selectCurrWordTemp.audio,
						selectCurrWordTemp.Word + ".mp3");
				downAudio.startDownLoadAudio();
				break;
			case 2:
				Toast.makeText(mContext, R.string.play_no_word_interpretation,
						1000).show();
				WordCard.this.setVisibility(View.GONE);
				break;
			}
		}
	};
}