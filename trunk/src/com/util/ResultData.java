package com.util;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResultData 
{
	
	public static <T> Collection<T> getBeanCollection(T object, ResultSet rs)
	{  
	    Collection<T> collection = null;  
	  
	    Class clazzT = object.getClass();  
	    Method[] methods = clazzT.getMethods();//获得bean的方法      
	    List<Method> setterMethodList = new ArrayList<Method>();//构造一个List用来存放bean中所有set开头的方法      
	  
	    //获得Bean中所有set方法      
	    for (Method method : methods) {  
	        if (method.getName().startsWith("set")) {  
	            setterMethodList.add(method);  
	        }  
	    }  
	  
	    ResultSetMetaData meta = null;  
	    try {  
	        meta = rs.getMetaData();  
	  
	        //如果记录集中的字段不等于Bean中的属性值，抛出异常      
	        if (setterMethodList.size() != meta.getColumnCount()) {  
	            throw new IllegalArgumentException("传入的JavaBean与ResultSet不一致");  
	        }  
	  
	        //将ResultSet中的每一条记录构建一个JavaBean实例,然后添加到Collection中      
	        collection = new ArrayList<T>();  
	  
	        while (rs.next()) {  
	            T o = (T)clazzT.newInstance();  
	              
	            for (Method m : setterMethodList) {  
	                m.invoke(o, rs.getObject(m.getName().substring(3).toLowerCase()));  
	            }  
	            collection.add(o);  
	        }  
	    } catch (Exception e) {  
	        e.printStackTrace();  
//	           throw e;  
	    }   
	      
	    return collection;//最后返回这个Collection      
	} 

}
