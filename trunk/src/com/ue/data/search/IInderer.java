package com.ue.data.search;

import java.io.IOException;
import java.util.HashMap;

public interface IInderer
{
	public void close()throws IOException;

	public void open(String s, boolean flag)throws Exception;
	
    boolean add(HashMap<String, Object> hashmap)throws Exception;
//	public boolean add(HashMap hashmap)throws Exception;
	
	public boolean remove(String s)throws Exception;
	
	public boolean removeAll();
	
	boolean update(String s, HashMap<String, Object> hashmap) throws Exception;
	
//	public DataTable search(String s, int i, int j, String s1)throws Exception;

}
