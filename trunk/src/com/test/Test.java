package com.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

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
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
//		String aString = "adfadsfa该罚的，http:www.aceona.com ";
//		String bString = "adfadsfa该罚的，<a href=\"http://www.aceona.com\">";
//		if (aString.indexOf("http")!=-1) 
//		{
//			int i = aString.indexOf("http");
//			int j = aString.indexOf(" ");
//			String cString = aString.substring(i,j);
//			String dString = "<a href='"+cString+"'>";
//			aString.replace(cString, "asdfasdfasdf");
//			System.out.println(aString);
//		}
		
		List<Test> list =  new ArrayList<Test>();
		Test test1 = new Test();
		test1.setAttr_id("品牌");
		test1.setValue("test1");
		
		Test test2 = new Test();
		test2.setAttr_id("品牌");
		test2.setValue("test2");
		
		Test test3 = new Test();
		test3.setAttr_id("品牌");
		test3.setValue("test3");
		
		Test test4 = new Test();
		test4.setAttr_id("材质");
		test4.setValue("不知道");
		
//		list.add(test1);
//		list.add(test2);
//		list.add(test3);
//		list.add(test4);
		
		Map<String, String> labelValueMap = new HashMap<String, String>();
		
		for (int i = 0; i < list.size(); i++) {
			Test test = list.get(i);
			// 如果map中已经存在key，则将value值加进数组中
			String label = test.getAttr_id();
			String value = test.getValue();
			if(labelValueMap.containsKey(label)){
				String valueStr =  labelValueMap.get(label);
				labelValueMap.put(label, valueStr + "," +  value);
			}else{ // 如果map中不包含key，则将记录直接加进数组中
				labelValueMap.put(label, value);
			}
		}
		
		StringBuffer sbBuffer = new StringBuffer();
		for (Iterator<Map.Entry<String, String>> it = labelValueMap.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry<String, String> entry = it.next();
            sbBuffer.append(entry.getKey()).append(":").append(entry.getValue()).append("|");
        }
		if(StringUtils.isNotBlank(sbBuffer.toString())){
			System.err.println( sbBuffer.substring(0, sbBuffer.length() - 1));
		}
		String resultStr = sbBuffer.toString();
		resultStr = StringUtils.isNotBlank(sbBuffer.toString()) ? resultStr.substring(0, resultStr.length() - 1) : "";
		System.err.println("resultStr======" + resultStr);
		
	}
	
	private String attr_id;
	private String value;
	public String getAttr_id() {
		return attr_id;
	}
	public void setAttr_id(String attr_id) {
		this.attr_id = attr_id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	

}
