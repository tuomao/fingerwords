package com.wsh.fingerwords.util;

import java.io.File;
import java.io.FilenameFilter;



import android.R.integer;
import android.util.Log;

/**
* File类的list方法返回该目录下的所有文件（包括目录）的文件名，文件名不含路径信息
* File类的listFiles方法返回目录下的所有文件（包括目录）的File对象
* FilenameFilter是文件名过滤器接口类，所有自定义的文件名过滤器必须实现该接口的accept方法
* @author Administrator
*
*/
public class ListFileUtil {

/**
* 这是一个内部类，实现了FilenameFilter接口，用于过滤文件 
*/
public static class MyFilenameFilter implements FilenameFilter{

   private String suffix = "";//文件名后缀
  
   public MyFilenameFilter(String suffix){
    this.suffix = suffix;
   }
  @Override
  public boolean accept(File dir, String filename)
  {
    //如果文件名与指定的后缀相同便返回true
    if(new File(dir,filename).isFile()){
     return filename.endsWith(suffix);
    }
    else{
     //如果是文件夹
     return true;
    }
  }
  
}
/**
 * 
 * @param dirName
 * @return
 * 功能：检查dirName下的子文件及子文件夹数
 */
public static int checkSubFiles(String dirName)
{
  int subFileNum = 0;//子文件夹数
  //如果dir不以文件分隔符结尾，自动添加文件分隔符
  if(!dirName.endsWith(File.separator)){
   dirName = dirName + File.separator;
  }
 
  File dirFile = new File(dirName);
  //如果dir对应的文件不存在，或者不是一个文件夹则退出
  if(!dirFile.exists() || (!dirFile.isDirectory())){
//   System.out.println("List失败！找不到目录："+dirName);
   return subFileNum;
  }
 
  //列出文件夹下所有的文件,listFiles方法返回目录下的所有文件（包括目录）的File对象
  File[] files = dirFile.listFiles();
//  for(int i = 0; i < files.length; i++){
//  if (files[i].isDirectory()){
////    Log.e(" checkSubFiles检查子目录！",files[i].getAbsolutePath() );
////    ListFileUtil.listAllFiles(files[i].getAbsolutePath());
//    subFileNum += 1;
//   }
//  }
  subFileNum = files.length;
  return subFileNum;
}
/**
 * 
 * @param dirName
 * 功能：删除指定目录下的所有文件夹
 */
public static void  deleteFiles(String dirName)
{
  //如果dir不以文件分隔符结尾，自动添加文件分隔符
  if(!dirName.endsWith(File.separator)){
   dirName = dirName + File.separator;
  }
 
  File dirFile = new File(dirName);
  //如果dir对应的文件不存在，或者不是一个文件夹则退出
  if(!dirFile.exists() || (!dirFile.isDirectory())){
   return;
  }
 
  //列出文件夹下所有的文件,listFiles方法返回目录下的所有文件（包括目录）的File对象
  File[] files = dirFile.listFiles();
  for(int i = 0; i < files.length; i++){
   if(files[i].isFile()){
     files[i].delete();
//    System.out.println(files[i].getAbsolutePath() + " 是文件！");
   }else if (files[i].isDirectory()){
//    System.out.println(files[i].getAbsolutePath() + " 是目录！");
    ListFileUtil.deleteFiles(files[i].getAbsolutePath());
   }
  }
}
/**
* 列出目录下所有的文件包括子目录的文件路径
* @param dirName 文件夹的文件路径
*/
public static void listAllFiles(String dirName){
  
  
   //如果dir不以文件分隔符结尾，自动添加文件分隔符
   if(!dirName.endsWith(File.separator)){
    dirName = dirName + File.separator;
   }
  
   File dirFile = new File(dirName);
   //如果dir对应的文件不存在，或者不是一个文件夹则退出
   if(!dirFile.exists() || (!dirFile.isDirectory())){
//    System.out.println("List失败！找不到目录："+dirName);
    return;
   }
  
   /*  
   * list方法返回该目录下的所有文件（包括目录）的文件名，文件名不含路径信息
   * 
      String[] files = dirFile.list();
    for(int i = 0; i < files.length; i++){
     System.out.println(files[i]);
    }
   */
  
   //列出文件夹下所有的文件,listFiles方法返回目录下的所有文件（包括目录）的File对象
   File[] files = dirFile.listFiles();
   for(int i = 0; i < files.length; i++){
    if(files[i].isFile()){
//     System.out.println(files[i].getAbsolutePath() + " 是文件！");
    }else if (files[i].isDirectory()){
//     System.out.println(files[i].getAbsolutePath() + " 是目录！");
     ListFileUtil.listAllFiles(files[i].getAbsolutePath());
    }
   }
}

/**
* 列出目录中通过文件名过滤器过滤后的文件
* @param filter 文件名过滤器对象
* @param dirName 目录名
*/
public static int listFilesByFilenameFilter(FilenameFilter filter,String dirName){
  int fileSum = 0;
   //如果dir不以文件分隔符结尾，自动添加文件分隔符
   if(!dirName.endsWith(File.separator)){
    dirName = dirName + File.separator;
   }
  
   File dirFile = new File(dirName);
   //如果dir对应的文件不存在，或者不是一个文件夹则退出
   if(!dirFile.exists() || (!dirFile.isDirectory())){
//    Log.e("List失败！找不到目录：", dirName);
    return fileSum;
   }
  
   //检查源文件夹下所有能通过过滤器的文件包括子目录
   File[] files = dirFile.listFiles(filter);
   for(int i = 0; i < files.length; i++){
    if(files[i].isFile()){
//      Log.e(files[i].getAbsolutePath() , " 是文件！");
      fileSum ++;
    }
//    else if (files[i].isDirectory()){
//      Log.e(files[i].getAbsolutePath() , " 是目录！");
//     ListFileUtil.listFilesByFilenameFilter(filter,files[i].getAbsolutePath());
//    }
   }
   return fileSum;
}
/**
 * 
 * 获取文件的大小  单位M
 * @param file
 * @return
 */
public static float getDirSize(File file) {     
    //判断文件是否存在     
    if (file.exists()) {     
        //如果是目录则递归计算其内容的总大小    
        if (file.isDirectory()) {     
            File[] children = file.listFiles();     
            float size = 0;     
            for (File f : children)     
                size += getDirSize(f);     
            return size;     
        } else {//如果是文件则直接返回其大小,以“兆”为单位   
            float size =file.length()/1024/1024;        
            return size;     
        }     
    } else {     
    	return 0;
    }     
}    
}
