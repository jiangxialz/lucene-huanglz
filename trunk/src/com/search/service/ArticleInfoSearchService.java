package com.search.service;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import self.wonder.search.JSONUtil;

import com.common.confManager.ConfManager;
import com.constants.IndexConstant;
import com.search.po.ArticlePO;
import com.search.po.DataGrid;
import com.search.po.SearchBasePO;
import com.search.support.KeyAnalysisSupport;
import com.ue.data.search.indexer.lucene.Indexer;
import com.ue.data.search.indexer.lucene.Indexer2;
import com.ue.data.search.indexer.lucene.Indexer3;
import com.ue.data.search.indexer.lucene.MultiIndexer;
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
	
	public static <T> DataGrid<List<T>> getDataKeyAnalysisStr(T object,SearchBasePO sbpo)
	{
		List<T> resultList = new ArrayList<T>();
		 // 建立搜索条件
        ArrayList<String> words = KeyAnalysisSupport.analysePostSearchKey(sbpo);
        // 总记录数
        int lngRowCount = 0;
        Indexer2 indexer2 = new Indexer2(cm.getPropValue(IndexConstant.NEWS_PATH), null);
        DataGrid<List<T>> dataGrid2 = new DataGrid<List<T>>();
        try
        {
            // 搜索各分词结果
            for (String word : words)
            {
                LogHelper.getLogger().info("sbWhere=======  " + word);
                DataGrid<JSONArray> dataGrid = indexer2.search(word, sbpo.getOffset(), sbpo.getOffset() + sbpo.getLimit(), sbpo.getOrder_str());

//                if (dataGrid.getTotalElements()<=0) 
//                {
//                	DataGrid<List<T>> dataGrid_2 =  indexer.search(object, word, 0, 1, null);
//					dataGrid.setTotalElements(dataGrid_2.getTotalElements());
//				}
                if (dataGrid.getData().size() > 0)
                {
                    sbpo.setOffset(0);
                    /** 第一个分词结果集大于5条的话只取第一个分词结果集前5条记录；如果小于5，取第一
                                                                分词结果集n条记录(n<5)+第二个分词结果集的前5-n条记录
                    */
                    for (int i = 0; sbpo.getLimit() - resultList.size() > 0 && i < dataGrid.getData().size(); i++)
                    {
                    	JSONObject jsonObject =  (JSONObject) dataGrid.getData().get(i);
                    	object = (T) JSONUtil.fromJson(jsonObject.toString(), object.getClass());
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
        dataGrid2.setData(resultList);
        dataGrid2.setTotalElements(lngRowCount);
        return dataGrid2;
	}
	
	public static <T> DataGrid<List<T>> getDataKeyAnalysisStr2(T object,SearchBasePO sbpo)
	{
		List<T> resultList = new ArrayList<T>();
		 // 建立搜索条件
        ArrayList<String> words = KeyAnalysisSupport.analysePostSearchKey(sbpo);
        // 总记录数
        int lngRowCount = 0;
        Indexer3 indexer3 = new Indexer3(cm.getPropValue(IndexConstant.NEWS_PATH), null);
        DataGrid<List<T>> dataGrid = new DataGrid<List<T>>();
        try
        {
            // 搜索各分词结果
            for (String word : words)
            {
                LogHelper.getLogger().info("sbWhere=======  " + word);
                dataGrid = indexer3.search(object, word, sbpo.getOffset(), sbpo.getOffset() + sbpo.getLimit(), sbpo.getOrder_str());

//                if (dataGrid.getTotalElements()<=0) 
//                {
//                	DataGrid<List<T>> dataGrid_2 =  indexer.search(object, word, 0, 1, null);
//					dataGrid.setTotalElements(dataGrid_2.getTotalElements());
//				}
                if (dataGrid.getData().size() > 0)
                {
                    sbpo.setOffset(0);
                    /** 第一个分词结果集大于5条的话只取第一个分词结果集前5条记录；如果小于5，取第一
                                                                分词结果集n条记录(n<5)+第二个分词结果集的前5-n条记录
                    */
                    for (int i = 0; sbpo.getLimit() - resultList.size() > 0 && i < dataGrid.getData().size(); i++)
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
	
	public static <T> DataGrid<List<T>> getDataKeyAnalysisByMultiSearch(T object,SearchBasePO sbpo)
	{
		List<T> resultList = new ArrayList<T>();
		 // 建立搜索条件
        ArrayList<String> words = KeyAnalysisSupport.analysePostSearchKey(sbpo);
        // 总记录数
        int lngRowCount = 0;
        MultiIndexer indexer = new MultiIndexer();
        DataGrid<List<T>> dataGrid = new DataGrid<List<T>>();
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
                    /** 第一个分词结果集大于5条的话只取第一个分词结果集前5条记录；如果小于5，取第一
                                                                分词结果集n条记录(n<5)+第二个分词结果集的前5-n条记录
                    */
                    for (int i = 0; sbpo.getLimit() - resultList.size() > 0 && i < dataGrid.getData().size(); i++)
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
		int curPage = 1;
		sbpo.setCurPage(curPage);
//		String paramStr = "title :工程师  AND type : [0 TO 3]";
//		sbpo.setParamStr(paramStr);
		sbpo.setParam("key", "软件开发工程师");
		sbpo.setParam("type", "title");
		sbpo.setOrder_str("type desc");
//        ArticlePO article = new ArticlePO();

//        PageableResultDataImpl<List<ArticlePO>> dataGrid = getData(article, sbpo);
//        PageableResultDataImpl<List<ArticlePO>> dataGrid = getDataKeyAnalysisStr(new ArticlePO(),sbpo);
//		  DataGrid<List<ArticlePO>> dataGrid = getDataKeyAnalysisStr(new ArticlePO(),sbpo);
//        dataGrid.init(sbpo.getCurPage(), sbpo.getLimit());
//        System.out.println(dataGrid.getTotalCount());
//        if (null !=dataGrid && dataGrid.getTotalCount() > 0) 
//        {
//        	List<ArticlePO> list = (List<ArticlePO>)dataGrid.getResultData();
//	        for (ArticlePO articlePO : list) 
//	        {
//				System.out.println("id : "+ articlePO.getId()+ " title :"+articlePO.getTitle()+ " content :"+articlePO.getContent()+ " IntCreateTime : " + articlePO.getIntCreateTime()+" author : " + articlePO.getAuthor()+" type : " + articlePO.getType());
//			}
//        }
//        DataGrid<List<ArticlePO>> dataGrid = getDataKeyAnalysisStr2(new ArticlePO(),sbpo);
        DataGrid<List<ArticlePO>> dataGrid = getDataKeyAnalysisByMultiSearch(new ArticlePO(),sbpo);
//        DataGrid<List<ArticlePO>> dataGrid = getDataKeyAnalysisStr(new ArticlePO(),sbpo);
        System.out.println(dataGrid.getTotalElements());
        if (null !=dataGrid && dataGrid.getTotalElements() > 0) 
        {
        	List<ArticlePO> list = (List<ArticlePO>)dataGrid.getData();
	        for (ArticlePO articlePO : list) 
	        {
				System.out.println("ID : "+ articlePO.getId()+ " title :"+articlePO.getTitle()+ " content :"+articlePO.getContent()+ " IntCreateTime : " + articlePO.getIntCreateTime()+" author : " + articlePO.getAuthor()+" type : " + articlePO.getType());
			}
        }
	}
	
//	public static void main(String[] args)
//	{
//		SearchBasePO sbpo = new SearchBasePO();
//		int curPage = 1;
//		sbpo.setCurPage(curPage);
////		String paramStr = "title :工程师  AND type : [0 TO 3]";
////		sbpo.setParamStr(paramStr);
//		sbpo.setParam("key", "软件开发工程师");
//		sbpo.setParam("type", "title");
//        ArticlePO article = new ArticlePO();
//
////        PageableResultDataImpl<List<ArticlePO>> dataGrid = getData(article, sbpo);
//        PageableResultDataImpl<List<ArticlePO>> dataGrid = getDataKeyAnalysis(article,sbpo);
//        dataGrid.init(sbpo.getCurPage(), sbpo.getLimit());
//        System.out.println(dataGrid.getTotalCount());
//        if (null !=dataGrid && dataGrid.getTotalCount() > 0) 
//        {
//        	List<ArticlePO> list = (List<ArticlePO>)dataGrid.getResultData();
//	        for (ArticlePO articlePO : list) 
//	        {
//				System.out.println("id : "+ articlePO.getId()+ " title :"+articlePO.getTitle()+ " content :"+articlePO.getContent()+ " IntCreateTime : " + articlePO.getIntCreateTime()+" author : " + articlePO.getAuthor()+" type : " + articlePO.getType());
//			}
//        }
//	}
	
	/**
	// 调试
	public static void main(String[] args)
	{
		SearchBasePO sbpo = new SearchBasePO();
		int curPage = 1;
		sbpo.setCurPage(curPage);
		sbpo.setParam("key", "软件开发工程师");
        sbpo.setParam("type", "title");
//        sbpo.setOrder_str("type desc");
//		String paramStr = "intCreateTime : 20111108  AND (*:* OR type : 1^3)";
//		String paramStr = "title : 软件开发工程师";
        ArticlePO article = new ArticlePO();

//        PageableResultDataImpl<List<ArticlePO>> dataGrid = getData(article, sbpo);
        PageableResultDataImpl<List<ArticlePO>> dataGrid = getDataKeyAnalysis(article,sbpo);
        dataGrid.init(sbpo.getCurPage(), sbpo.getLimit());
        System.out.println(dataGrid.getTotalCount());
        if (null !=dataGrid && dataGrid.getTotalCount() > 0) 
        {
        	List<ArticlePO> list = (List<ArticlePO>)dataGrid.getResultData();
	        for (ArticlePO articlePO : list) 
	        {
				System.out.println("id : "+ articlePO.getId()+ " title :"+articlePO.getTitle()+ " content :"+articlePO.getContent()+ " IntCreateTime : " + articlePO.getIntCreateTime()+" type : " + articlePO.getType());
			}
        }
	}
	*/
	
}
