package com.heaton.baselib.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by admin on 2017/1/16.
 */

public class FileUtils {

    //删除文件夹下的所有文件
    public static boolean deleteDir(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                deleteDir(f);
            }
        }else if (file.exists()) {
            file.delete();
        }
        // 目录此时为空，可以删除
        return true;
    }

    //读取指定目录下的所有TXT文件的文件内容
    public static String getFileContent(File file) {
        String content = "";
        if (!file.isDirectory()) {  //检查此路径名的文件是否是一个目录(文件夹)
            if (file.getName().endsWith("txt")) {//文件格式为""文件
                try {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null) {
                        InputStreamReader inputreader
                                = new InputStreamReader(instream, "UTF-8");
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line = "";
                        //分行读取
                        while ((line = buffreader.readLine()) != null) {
                            content += line + "\n";
                        }
                        instream.close();//关闭输入流
                    }
                } catch (java.io.FileNotFoundException e) {
                    Log.d("TestFile", "The File doesn't not exist.");
                } catch (IOException e) {
                    Log.d("TestFile", e.getMessage());
                }
            }
        }
        return content;
    }

    /**
     * 获取外部存储的目录，若不存在，则获取内部存储目录
     * @param context
     * @param child  如:  download   log
     * @return  /storage/emulated/0/Android/data/< package name >/files
     */
    public static File getFilePath(Context context, String child){
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
            || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            return context.getExternalFilesDir(child);
        }else {
            //外部存储不可用,获取内部存储 /data/data/< package name >/files/…
            String dirPath = context.getFilesDir() + File.separator + child;
            File file = new File(dirPath);
            if (!file.exists()){
                file.mkdirs();
            }
            return file;
        }
    }

    /**
     * 获取sd卡的缓存目录
     * @param context
     * @return  /storage/emulated/0/Android/data/包名/cache
     */
    public static File getCachePath(Context context){
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            return context.getExternalCacheDir();
        }else {
            //外部存储不可用
            return context.getCacheDir();
        }
    }




}
