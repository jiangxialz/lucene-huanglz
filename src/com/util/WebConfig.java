package com.util;

import java.io.File;
import java.net.URL;

/**
 * 基本配置类
 * @author honny.huang
 *
 */
public class WebConfig {
	
	public WebConfig(){
		
	}
	
	public static String classes(){
		return "classes";
	}
	
	// WebRoot根目录
	public static String ApplicationPath;

	static 
	{
		try 
		{
			Object obj;
	        if((obj = com.util.WebConfig.class.getResource("../")) != null) //  /F:/Workspaces/MyEclipse 9/knowledge9/WebRoot/WEB-INF/classes/com/
	        {
	            if(((String) (obj = ((URL) (obj)).getPath())).endsWith("com/"))
	                obj = ((String) (obj)).substring(0, ((String) (obj)).length() - 4);
	        } else
	        {
	            obj = com.util.WebConfig.class.getClassLoader().getResource("").getPath();  //  /F:/Workspaces/MyEclipse 9/knowledge9/WebRoot/WEB-INF/classes/com/
	        }
	        if(((String) (obj)).indexOf('/') == 0 && ((String) (obj)).indexOf(':') == 2)
	            obj = ((String) (obj)).substring(1);
	        obj = ((String) (obj = ((String) (obj)).replace("/", File.separator))).replace("%20", " ").replace((new StringBuilder(String.valueOf((File.separator))).append(WebConfig.classes()).toString()), "");
	        System.out.println((new StringBuilder("Path:")).append(((String) (obj))).toString());  //  F:\Workspaces\MyEclipse 9\knowledge9\WebRoot\WEB-INF\
	       // ResourcePath = (new StringBuilder(String.valueOf(obj))).append(XBit.resouce()).toString();
	        ApplicationPath = ((String) (obj)).replace((new StringBuilder(String.valueOf(File.separator))).append("WEB-INF").append(File.separator).toString(), File.separator);
	        System.out.println((new StringBuilder("ApplicationPath:")).append(ApplicationPath).toString());	//  F:\Workspaces\MyEclipse 9\knowledge9\WebRoot\		
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	// 调试
	public static void main(String[] args) {
		System.out.println(ApplicationPath);
	}
	
}
