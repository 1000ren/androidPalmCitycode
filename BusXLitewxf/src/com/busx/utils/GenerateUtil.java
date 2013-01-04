package com.busx.utils;

import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;

import com.busx.entities.Param;

/**
 * 
 * @author Administrator
 *
 */
public class GenerateUtil {

	// 生成sign
	public static String genSign(List<Param> params) {
		
		Collections.sort(params);
		
		StringBuffer sign = new StringBuffer();
		for (Param param : params) {
			sign.append(param.key);
			sign.append("=");
			sign.append(param.value);
		}
		
		String md5 = "";
		md5 = toMD5(sign.toString().getBytes());
		
		return md5;
	}
	
	public static String toMD5(byte[] source) {    	
    	try{
    		MessageDigest md = MessageDigest.getInstance("MD5");
    	    md.update( source );    	    
    	    StringBuffer buf=new StringBuffer();    	    
    	    for(byte b:md.digest())
    	    	buf.append(String.format("%02x", b&0xff) );    	     
    	    return buf.toString();
    	}catch( Exception e ){
    		e.printStackTrace(); return null;
    	}  
    }
}
