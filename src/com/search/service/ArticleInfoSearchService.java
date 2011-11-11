package com.search.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.common.confManager.ConfManager;
import com.constants.IndexConstant;
import com.search.po.ArticlePO;
import com.search.po.DataGrid;
import com.search.po.SearchBasePO;
import com.search.support.KeyAnalysisSupport;
import com.ue.data.search.indexer.lucene.Indexer;
import com.util.LogHelper;
import com.util.page.PageableResultData;
import com.util.page.PageableResultDataImpl;

public class ArticleInfoSearchService 
{
	  // 配置管理类实例
    private static ConfManager cm = ConfManager.getInstance();

	public static <T> DataGrid<List<T>> getData(T object, SearchBasePO sbpo)
	{
		DataGrid<List<T>> dataGrid = new DataGrid<List<T>>();
		String strWhere = sbpo.getParamStr();
        if (StringUtils.isBlank(strWhere))
        {
            return dataGrid;
        }
        // 先判断sbpo中的参数map!=null
        Indexer indexer = new Indexer(cm.getPropValue(IndexConstant.NEWS_PATH), null);
        
        int offset = (sbpo.getCurPage()-1)*sbpo.getPageSize();
//        int offset = PageableResultDataImpl
        
        try
        {
        	dataGrid = indexer.search(object, strWhere, offset, sbpo.getPageSize(), sbpo.getOrder_str());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
		return dataGrid;
	}
	
	public static <T> DataGrid<List<T>> getData2(T object, SearchBasePO sbpo)
	{
		DataGrid<List<T>> dataGrid = new DataGrid<List<T>>();
		String strWhere = sbpo.getParamStr();
        if (StringUtils.isBlank(strWhere))
        {
            return dataGrid;
        }
        // 先判断sbpo中的参数map!=null
        Indexer indexer = new Indexer(cm.getPropValue(IndexConstant.NEWS_PATH), null);
        
        try
        {
        	dataGrid = indexer.search(object, strWhere, sbpo.getOffset(), sbpo.getOffset() + sbpo.getLimit(), sbpo.getOrder_str());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
		return dataGrid;
	}
	
	public static <T> DataGrid<List<T>> getDataKeyAnalysis(T object, SearchBasePO sbpo)
	{
		List<T> resultList = new ArrayList<T>();
		// 防止重复
        ArrayList<String> ids = new ArrayList<String>();
		DataGrid<List<T>> dataGrid = new DataGrid<List<T>>();
		 // 建立搜索条件
        ArrayList<String> words = KeyAnalysisSupport.analysePostSearchKey(sbpo);
        // 总记录数
        int lngRowCount = 0;
        Indexer indexer = new Indexer(cm.getPropValue(IndexConstant.NEWS_PATH), null);
        
        try
        {
            // 搜索各分词结果
            for (String word : words)
            {
                LogHelper.getLogger().info("sbWhere=======  " + word);
                dataGrid = indexer.search(object, word, sbpo.getOffset(), sbpo.getOffset() + sbpo.getLimit(), sbpo.getOrder_str());

//                if (dataGrid.getTotalElements()<=0) 
//                {
//                	DataGrid<List<T>> dataGrid_2 =  indexer.search(object, word, 0, 1, null);
//					dataGrid.setTotalElements(dataGrid_2.getTotalElements());
//				}
                if (dataGrid.getData().size() > 0)
                {
                    sbpo.setOffset(0);
                    for (int i = 0; sbpo.getLimit() - ids.size() > 0 && i < dataGrid.getData().size(); i++)
                    {
                    	object = (T) dataGrid.getData().get(i);
                    	resultList.add(object);
//                    	String strID = ((T) object).getId();
////                    	String strID = dt.getRow(i).getString("ID");
//                        if (!ids.contains(strID))
//                        {   // 判断重复
//                        	resultList.add(object);
//                            ids.add(strID);
//                        }
//                        else
//                        {
//                            lngRowCount--;
//                        }
                    }
                }
                else
                {
                    sbpo.setOffset(sbpo.getOffset() - dataGrid.getTotalElements());
                    if (sbpo.getOffset() < 0)
                        sbpo.setOffset(0);
                }
                // 总记录
                lngRowCount += dataGrid.getTotalElements();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        dataGrid.setData(resultList);
        dataGrid.setTotalElements(lngRowCount);
        return dataGrid;
	}
	
	/**
	public static <T> DataGrid<List> getDataKeyAnalysis3(T object, SearchBasePO sbpo)
	{
		List<ArticlePO> resultList = new ArrayList<ArticlePO>();
		// 防止重复
        ArrayList<String> ids = new ArrayList<String>();
		DataGrid<List> dataGrid = new DataGrid<List>();
		 // 建立搜索条件
        ArrayList<String> words = KeyAnalysisSupport.analysePostSearchKey(sbpo);
        // 总记录数
        int lngRowCount = 0;
        Indexer indexer = new Indexer(cm.getPropValue(IndexConstant.NEWS_PATH), null);
        
        try
        {
            // 搜索各分词结果
            for (String word : words)
            {
                LogHelper.getLogger().info("sbWhere=======  " + word);
                dataGrid = indexer.search(word, sbpo.getOffset(), sbpo.getOffset() + sbpo.getLimit(), sbpo.getOrder_str());

                if (dataGrid.getTotalElements()<=0) 
                {
                	DataGrid<List> dataGrid_2 =  indexer.search(word, 0, 1, null);
					dataGrid.setTotalElements(dataGrid_2.getTotalElements());
				}
                if (dataGrid.getData().size() > 0)
                {
                    sbpo.setOffset(0);
                    for (int i = 0; sbpo.getLimit() - ids.size() > 0 && i < dataGrid.getData().size(); i++)
                    {
                    	ArticlePO articlePO = (ArticlePO) dataGrid.getData().get(i);
                    	String strID = articlePO.getId();
//                    	String strID = dt.getRow(i).getString("ID");
                        if (!ids.contains(strID))
                        {   // 判断重复
                        	resultList.add(articlePO);
                            ids.add(strID);
//                            dtData.addCopyRow(dt.getRow(i));
                        }
                        else
                        {
                            lngRowCount--;
                        }
                    }
                }
                else
                {
                    sbpo.setOffset(sbpo.getOffset() - dataGrid.getTotalElements());
                    if (sbpo.getOffset() < 0)
                        sbpo.setOffset(0);
                }
                // 总记录
                lngRowCount += dataGrid.getTotalElements();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        dataGrid.setData(resultList);
        dataGrid.setTotalElements(lngRowCount);
        return dataGrid;
	}
	*/
	
	// 调试
	public static void main(String[] args)
	{
		SearchBasePO sbpo = new SearchBasePO();
//		sbpo.setParam("key", "软件开发工程师");
//        sbpo.setParam("type", "title");
		sbpo.setOffset(1);
        sbpo.setParamStr("content:输入");
        sbpo.setOrder_str("intCreateTime desc");
        ArticlePO article = new ArticlePO();
        DataGrid<List<ArticlePO>> dataGrid = getData(article, sbpo);
        try {
			PageableResultData pageableResultData = (PageableResultData) new PageableResultDataImpl(dataGrid.getData());
			pageableResultData.gotoPage(sbpo.getCurPage());
		} catch (Exception e) {
			e.printStackTrace();
		}
//        DataGrid<List<ArticlePO>> dataGrid = getData(article, sbpo);
//        DataGrid<List<ArticlePO>> dataGrid = getDataKeyAnalysis(article,sbpo);
        System.out.println(dataGrid.getTotalElements());
        if (null !=dataGrid && dataGrid.getTotalElements() > 0) 
        {
        	List<ArticlePO> list = (List<ArticlePO>)dataGrid.getData();
	        for (ArticlePO articlePO : list) 
	        {
				System.out.println("id : "+ articlePO.getId()+ " title :"+articlePO.getTitle()+ "  intCreateTime : " + articlePO.getIntCreateTime());
			}
        }
	}
}
