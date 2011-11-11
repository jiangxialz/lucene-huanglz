package com.util.page;

import java.sql.SQLException;
import java.util.List;
/**
 * 支持分页结果集的实现类
 */
public class PageableResultDataImpl 
//public class PageableResultDataImpl implements PageableResultData 
{
	// 存储数据的结果集
	private List<?> resultData;
	// 记录的总行数
	private int rowsCount;
	// 每页的记录条数，如果为0，则不分页，即一页显示所有记录
	protected int pageSize;
	//　当前页码，从1开始
    protected int curPage;
	
	private int totalPage;
	
	private int offset;
	
	// 获得每页的最大记录数
	public int getPageSize() {
	    return pageSize;
	}
	// 获得总的记录数
	public int getRowsCount() {
	    return rowsCount;
	}
	// 获得当前的页码
	public int getCurPage() {
	    return curPage;
	}
	
	public int getTotalPage(){
		return totalPage;
	}
	
	public int getOffset(){
		return offset;
	}
    
    // 构造函数
	public PageableResultDataImpl(java.util.List<?> list) throws Exception {
		if (list == null) {
			throw new SQLException("传入的ResultData为null");
		}
		// 获取结果集的记录数
		this.resultData = list;
		rowsCount = resultData.size();
		totalPage = getPageCount();
		pageSize = 0;
		curPage = 1;
	} 

	// 获得总的页数
	public int getPageCount() {
		// 没有记录则没有页数
	    if(rowsCount==0){
	    	return 0;
	    }
	    // 如果每页记录数为0，表示用一页显示所有记录，所以页数就为1
	    if(pageSize==0){
	    	return 1;
	    }
	    int tmpI = (int)Math.ceil((float)rowsCount/pageSize);
	    /**
	    // 计算总页数，总行数/页记录数
	    double tmpD=(double)rowsCount/pageSize;
	    int tmpI=(int)tmpD;
	    // 如果不能整除，则加1，最后一页没满。
	    if(tmpD>tmpI){ 
	    	tmpI++;
	    }
	    */
	    return tmpI;
	}

	// 获取当前页的记录数
	public int getPageRowsCount() {
		// 如果不分页，则当前页的记录数为所有记录
	    if(pageSize==0){
	    	return rowsCount;
	    }
	    // 如果没有记录，则返回0
	    if(rowsCount==0){
	    	return 0;
	    }
	    // 如果当前页不是最后一页，则返回页记录数
	    if(curPage!=getPageCount()){
	    	return pageSize;
	    }
	    // 否则为最后一页，则返回多余的记录数
	    return rowsCount-(getPageCount()-1)*pageSize;
	}

	// 转入到某页(当前请求的页数)
	public void gotoPage(int page) {
	    // 如果参数页码小于1或者大于总页数，则自动调整参数页码的值
	    if (page < 1)
	    {
	    	page = 1;
	    }
	    curPage = page;
	}
/**
	// 转到当前页的第一条记录
	public void pageFirst() throws java.sql.SQLException {
	    int row=(curPage-1)*pageSize+1;
	    rs.absolute(row);
	}
	// 转到当前页的最后一条记录
	public void pageLast() throws java.sql.SQLException {
	    int row=(curPage-1)*pageSize+getPageRowsCount();
	    rs.absolute(row);
	}
*/
	// 设置页面最大记录的大小
	public void setPageSize(int pageSize) {
	    if(pageSize>=0){
	        this.pageSize = pageSize;
//	        curPage=1;
	    }
	} 
	
	public void setCurPage(int curPage){
		// 如果参数页码小于1或者大于总页数，则自动调整参数页码的值
	    if (curPage < 1)
	    {
	    	curPage = 1;
	    }
	    this.curPage = curPage;
	}

}
