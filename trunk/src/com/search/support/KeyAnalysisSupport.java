package com.search.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.QueryParser;

import com.search.dictionary.DictionaryManager;
import com.search.po.SearchBasePO;
import com.ue.data.search.analysis.DictionaryAnalyzer;
import com.util.LogHelper;
import com.util.StrLengthComparator;


public class KeyAnalysisSupport {
	/**
	 * <查询条件分析 分词及封装>
	 * 
	 * @param sbpo
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<String> analysePostSearchKey(SearchBasePO sbpo) 
	{
		// 分词
		DictionaryAnalyzer mainsegmenter = new DictionaryAnalyzer(
				DictionaryManager.getPositions());
		// 取出分词
		String keyString = (String) sbpo.getParam("key");
		ArrayList<String> keywords = new ArrayList<String>();
		if (StringUtils.isNotBlank(keyString)) 
		{
			keywords = mainsegmenter.analyse(keyString);
			keywords.add(keyString);  // 该句主要是当查询条件为英文时保证分词后的查询条件不为空
			Comparator<String> comp = new StrLengthComparator();
			Collections.sort(keywords, comp);
		}
		// 取出其他查询条件
//		StringBuffer sb = new StringBuffer();
		// 过滤长词记录
		StringBuffer notsb = new StringBuffer();
		if (keywords.size() > 0)
		{
			for (int i = 0; i < keywords.size(); i++) {
				// 对于【+ - & | ! ( ) { } [ ] ^ ~ * ? : \ 】之类进行转义
				String word = QueryParser.escape(keywords.get(i));
				// 加上搜索关键词 type 表示是职位名搜索还是公司名搜索
				keywords.set(i, " ( "+ sbpo.getParamMap().get("type") + " : " + word +  notsb.toString() +" ) ");
				// 每次将长词作为过滤条件加入notsb
				notsb.append(" - " + sbpo.getParamMap().get("type") + " : " + word);
				LogHelper.getLogger().info("============" + word);
			}
		}
		return keywords;
	}
	
	/**
	public static ArrayList<String> analysePostSearchKey2(SearchBasePO sbpo) 
	{
		// 分词
		DictionaryAnalyzer mainsegmenter = new DictionaryAnalyzer(
				DictionaryManager.getPositions());
		// 取出分词
		String keyString = (String) sbpo.getParam("key");
		ArrayList<String> keywords = new ArrayList<String>();
		if (StringUtils.isNotBlank(keyString)) 
		{
		// 搜索类型：1代表职位名，2代表公司名,当查询公司时，不进行分词器分词
//			if (!sbpo.getParam("type").equals("corpName")) 
//			{
				keywords = mainsegmenter.analyse(keyString);
//			}
			keywords.add(keyString);
			Comparator<String> comp = new StrLengthComparator();
			Collections.sort(keywords, comp);
		}
		// 取出其他查询条件
		StringBuffer sb = new StringBuffer();
		Iterator it = sbpo.getParamMap().keySet().iterator();
		String key;
		boolean flag;
		while (it.hasNext()) {
			key = (String) it.next();
			if (key.equals("key") || key.equals("type")) {
				continue;
			}
			flag = false;
			for (String value : (String.valueOf(sbpo.getParamMap().get(key)))
					.split(SearchConstants.SPLIT_STR)) {
				if (StringHelper.isNullOrEmpty(value)) {
					continue;
				}
				if (flag) {
					sb.append(" OR ");
				} else {
					sb.append(" ( ");
				}
				if (key.equals("intendTime") || key.equals("intDeployTime") || key.equals("intMinReward") || key.equals("yearPay")) {
					sb.append(key + " : " + value);
				} else {
					sb.append(key + " : " + QueryParser.escape(value));
				}

				flag = true;
			}
			sb.append(" ) ");
			sb.append(" AND ");
		}
		
		sb.append("(designType:1^5  OR designType:2^3 OR designType:0) AND (issign:9^2 OR issign:1) AND ");
		
		// 过滤长词记录
		StringBuffer notsb = new StringBuffer();
		if (keywords.size() > 0)
		{
			for (int i = 0; i < keywords.size(); i++) {
				String word = QueryParser.escape(keywords.get(i));
				// 加上搜索关键词 type 表示是职位名搜索还是公司名搜索
				keywords.set(i, sb.toString() + " ( "
						+ sbpo.getParamMap().get("type") + " : " + word
						+ notsb.toString() + " ) ");
				// 每次将长词作为过滤条件加入notsb
				notsb.append(" - " + sbpo.getParamMap().get("type") + " : " + word);
				// LogManager.debug("=========" + word);
			}
		}
		else {
			int end = sb.toString().lastIndexOf("AND");
			keywords.add(sb.toString().substring(0, end));
		}
		return keywords;
	}
	*/
}
