package com.search.po;

/**
 * 
 * <数据处理bean>
 * <功能详细描述>
 * 
 * @author  honny.huang
 * @version  [版本号, 2011-9-20]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class DataGrid<T> 
{
	/**
	 * 记录总数
	 */
	private int totalElements;
	/**
	 * 记录列表
	 */
	private T data;
	
	public int getTotalElements() {
		return totalElements;
	}
	public void setTotalElements(int totalElements) {
		this.totalElements = totalElements;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	

}
