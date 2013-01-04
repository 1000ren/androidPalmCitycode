package com.busx.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.busx.protocol.ProtocolDef;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * 图片下载工具类
 *
 * @author 
 *
 */
public class BitmapUtil {
    private static final String TAG = "BtimapUtil";
    private static final String packageName = "palmgo/assest/";
    
    /**
     * 根据网址获得图片，优先从本地获取，本地没有则从网络下载
     *
     * @param url  图片网址
     * @param context 上下文
     * @param newPicName 新图片名称
     * @return 图片
     */
    public static boolean getBitmap(String url,Context context,String newPicName){
        Log.e(TAG, "url="+url);
        String imageName= url.substring(url.lastIndexOf("/")+1, url.length());
        File file = new File(Utils.getPath(ProtocolDef.SDPACKAGEIMAGE,
        		ProtocolDef.INPACKAGEIMAGE),imageName);
        File file2 = new File(Utils.getPath(ProtocolDef.SDPACKAGEIMAGE,
        		ProtocolDef.INPACKAGEIMAGE)+newPicName);
        return getNetBitmap(url,file,context,file2);
    }
     
    /**
     * 根据传入的list中保存的图片网址，获取相应的图片列表
     *
     * @param list  保存图片网址的列表
     * @param context 上下文
     * @return 图片列表
     */
//    public static List<Bitmap> getBitmap(List<String> list,Context context){
//        List<Bitmap> result = new ArrayList<Bitmap>();
//        for(String strUrl : list){
//            Bitmap bitmap = getBitmap(strUrl,context);
//            if(bitmap!=null){
//                result.add(bitmap);
//            }
//        }
//        return result;
//    }
    
    /**
     * 查询图片是否存在
     *
     * @param context 上下文
     * @return 本地图片存储目录
     */
    public static boolean isExistPic(Context context,String path){
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(hasSDCard){
            path="/sdcard/"+packageName;
        }else{
            path="/data/data/"+packageName;    
        } 
        File file = new File(path);
       
        
        return file.exists();
    }
    
    /**
     * 网络可用状态下，下载图片并保存在本地
     *
     * @param strUrl 图片网址
     * @param file 本地保存的图片文件
     * @param context  上下文
     * @param file2 修改下载后图片的名称
     * @return 图片
     */
    public static boolean getNetBitmap(String strUrl,File file,Context context,File file2) {
        Log.e(TAG, "getBitmap from net");
        Bitmap bitmap = null;
        InputStream in = null;
        FileOutputStream out = null;
            try {
                URL url = new URL(strUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.connect();
                in = con.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
                out = new FileOutputStream(file.getPath());
                bitmap.compress(Bitmap.CompressFormat.PNG,100, out);
                file.renameTo(file2);
                out.flush();
               
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally{
            	 try {
            		 if(null != out)
            		 {
            			 out.close();
            		 }
            		 if(null != in)
            		 {
            			 in.close();
            		 }
				} catch (IOException e) {
					e.printStackTrace();
				}
            }          
        return true;
    }
     
}