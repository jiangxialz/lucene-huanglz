package com.test;

import java.util.ArrayList;
import java.util.List;

public class Test 
{
	private String update_time;
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	/**
	public static void main(String[] args) 
	{
		String testString = "update_time";
		testString = testString.toLowerCase();
		System.out.println(testString);
		
		
		String q ="user.id:1 or user.id:2";
		String userIdsString[] = q.split("or");
		List list = new ArrayList();
		String userId[] = null;
		for (int i = 0; i < userIdsString.length; i++) 
		{
			String userIdString = userIdsString[i];
			userId=userIdString.split(":");
			list.add(userId[1]);
		}
		String idString ="1";
		if (list.contains(idString)) {
			list.remove(idString);
		}
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
		
		
//		String useri ="user.id:1 or user.id:2";
//		String tsdf[] = useri.split("or");
//		for (int i = 0; i < tsdf.length; i++) 
//		{
//			String test1 = tsdf[i];
//			String test[]=test1.split(":");
//			System.out.println(test[1]);
//		}
		
	}*/
	
	public static void main(String[] args) {
		String aString = "adfadsfa该罚的，http:www.aceona.com ";
//		String bString = "adfadsfa该罚的，<a href=\"http://www.aceona.com\">";
		if (aString.indexOf("http")!=-1) 
		{
			int i = aString.indexOf("http");
			int j = aString.indexOf(" ");
			String cString = aString.substring(i,j);
			String dString = "<a href='"+cString+"'>";
			aString.replace(cString, "asdfasdfasdf");
			System.out.println(aString);
		}
	}

}
