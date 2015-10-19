package com.wsh.fingerwords.Manager;

import java.util.ArrayList;

import com.wsh.fingerwords.entity.FavoriteWord;

/**
 * 
 * 用于各个页面之间的数据共享与传递
 * @author 魏申鸿
 *
 */
public class DataManager {
	private static DataManager instance;
    public static DataManager Instance() {
      if (instance == null) {
        instance = new DataManager();
      }
      return instance;
    }
    
	public ArrayList<FavoriteWord> favWordList=null;//收藏的单词的列表 
	
	
}
