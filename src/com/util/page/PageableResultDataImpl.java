package com.util.page;

/**
 * 支持分页结果集的实现类
 */
public class PageableResultDataImpl<T> 
{
	// 存储数据的结果集
	private T resultData;
	// 记录的总行数
	private int totalCount;
	// 当前页码，从1开始
    protected int curPage;
	// 总页数
	private int totalPage;
	// 获取数据结果集
	public T getResultData() {
		return resultData;
	}
	// 获得总的记录数
	public int getTotalCount() {
	    return totalCount;
	}
	// 获得当前的页码
	public int getCurPage() {
	    return curPage;
	}
	// 获取总页数
	public int getTotalPage(){
		return totalPage;
	}
	public void setResultData(T resultData) {
		this.resultData = resultData;
	}
	public void setTotalCount(int totalCount) {
	     this.totalCount = totalCount;
	}
	
	public void init(int curPage, int pageSize){
		// 获取当前请求的页数
		gotoPage(curPage);
		// 获取总页数
		getPageCount(pageSize);
	}

	// 获得总的页数
	public int getPageCount(int pageSize) {
		// 没有记录则没有页数
	    if(totalCount==0){
	    	return 0;
	    }
	    // 如果每页记录数为0，表示用一页显示所有记录，所以页数就为1
	    if(pageSize==0){
	    	return 1;
	    }
	    totalPage = (int)Math.ceil((float)totalCount/pageSize);
	    return totalPage;
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

}
