package com.wsh.fingerwords.entity;

import android.R.integer;

/**
 * 收藏的单词
 * @author 魏申鸿
 *
 */
public class FavoriteWord {
	public int id;
	public String Word;
	public String audio;
	public String pron;
	public String def;
	public int isCloud;
	public String CreateDate;
	public String examples=""; // 例句，多条以“,”分割

}
