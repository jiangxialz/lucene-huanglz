package com.search.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.common.confManager.ConfManager;
import com.constants.IndexConstant;
import com.search.po.ArticlePO;
import com.search.po.SearchBasePO;
import com.search.support.KeyAnalysisSupport;
import com.ue.data.search.indexer.lucene.Indexer;
import com.util.LogHelper;
import com.util.page.PageableResultDataImpl;

public class ArticleInfoSearchService 
{
	  // 配置管理类实例
    private static ConfManager cm = ConfManager.getInstance();

    /**
     * 搜索条件不分词查询
     * @param object
     * @param sbpo
     * @return
     */
	public static <T> PageableResultDataImpl<List<T>> getData(T object, SearchBasePO sbpo)
	{
		PageableResultDataImpl<List<T>> dataGrid = new PageableResultDataImpl<List<T>>();
		String strWhere = sbpo.getParamStr();
        if (StringUtils.isBlank(strWhere))
        {
            return dataGrid;
        }
        // 先判断sbpo中的参数map!=null
        Indexer indexer = new Indexer(cm.getPropValue(IndexConstant.NEWS_PATH), null);
        
        try
        {
        	dataGrid = indexer.search(object, strWhere, sbpo.getOffset(), sbpo.getOffset()+sbpo.getLimit(), sbpo.getOrder_str());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
		return dataGrid;
	}
	
	/**
	 * 搜索条件分词
	 * @param object
	 * @param sbpo
	 * @return
	 */
	public static <T> PageableResultDataImpl<List<T>> getDataKeyAnalysis(T object, SearchBasePO sbpo)
	{
		List<T> resultList = new ArrayList<T>();
		// 防止重复
//        ArrayList<String> ids = new ArrayList<String>();
        PageableResultDataImpl<List<T>> dataGrid = new PageableResultDataImpl<List<T>>();
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
                if (dataGrid.getResultData().size() > 0)
                {
                    sbpo.setOffset(0);
                    /** 第一个分词结果集大于5条的话只取第一个分词结果集前5条记录；如果小于5，取第一
                                                                分词结果集n条记录(n<5)+第二个分词结果集的前5-n条记录
                    */
                    for (int i = 0; sbpo.getLimit() - resultList.size() > 0 && i < dataGrid.getResultData().size(); i++)
                    {
                    	object = (T) dataGrid.getResultData().get(i);
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
                    sbpo.setOffset(sbpo.getOffset() - dataGrid.getTotalCount());
                    if (sbpo.getOffset() < 0)
                        sbpo.setOffset(0);
                }
                // 总记录
                lngRowCount += dataGrid.getTotalCount();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        dataGrid.setResultData(resultList);
        dataGrid.setTotalCount(lngRowCount);
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
		int curPage = 1;
		sbpo.setCurPage(curPage);
//		sbpo.setParam("key", "软件开发工程师");
//        sbpo.setParam("type", "title");
//        sbpo.setOrder_str("type desc");
//		String paramStr = "intCreateTime : 20111108  AND (*:* OR type : 2^3)";
		String paramStr = "title :工程师  OR content: 工程师  OR type: 2";
		sbpo.setParamStr(paramStr);
        ArticlePO article = new ArticlePO();

        PageableResultDataImpl<List<ArticlePO>> dataGrid = getData(article, sbpo);
//        PageableResultDataImpl<List<ArticlePO>> dataGrid = getDataKeyAnalysis(article,sbpo);
        dataGrid.init(sbpo.getCurPage(), sbpo.getLimit());
        System.out.println(dataGrid.getTotalCount());
        if (null !=dataGrid && dataGrid.getTotalCount() > 0) 
        {
        	List<ArticlePO> list = (List<ArticlePO>)dataGrid.getResultData();
	        for (ArticlePO articlePO : list) 
	        {
				System.out.println("id : "+ articlePO.getId()+ " title :"+articlePO.getTitle()+ " content :"+articlePO.getContent()+ " CreateTime : " + articlePO.getCreate_time()+" type : " + articlePO.getType());
			}
        }
	}
}
