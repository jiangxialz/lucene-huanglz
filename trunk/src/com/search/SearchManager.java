package com.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKQueryParser;
import org.wltea.analyzer.lucene.IKSimilarity;

import sample.dw.paper.lucene.index.IndexManager;

/**
 * This class is used to search the 
 * Lucene index and return search results
 */
public class SearchManager {
	
    private String searchWord;
    
    private IndexManager indexManager;
    
    private Analyzer analyzer;
    
    public SearchManager(String searchWord){
    	this.searchWord   =  searchWord;
    	this.indexManager =  new IndexManager();
    	this.analyzer     =  new StandardAnalyzer(Version.LUCENE_31);
    }
    
    /**
     * do search
     */
    @SuppressWarnings("unchecked")
    public List search()
    {
    	List searchResult = new ArrayList();
    	if(false == indexManager.ifIndexExist())
    	{
    		try {
				if(false == indexManager.createIndex()){
					return searchResult;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return searchResult;
			}
    	}
    	
    	File indexDir = new File(indexManager.getIndexDir());
    	String fields = "title";
//    	String[] fields = {"title"};
//    	String[] fields = { "path", "title", "content"};
    	IndexSearcher indexSearcher = null;
    	try
        {
    	    Directory fsDirectory = new SimpleFSDirectory(indexDir);  
    	    indexSearcher = new IndexSearcher(fsDirectory);
    	    indexSearcher.setSimilarity(new IKSimilarity());
            Query query = IKQueryParser.parse(fields, searchWord);
//            Query query = IKQueryParser.parseMultiField(fields, searchWord);
            TopDocs tops = indexSearcher.search(query, 20);
            ScoreDoc[] scores = tops.scoreDocs;
            Analyzer analyzer = new IKAnalyzer();
            SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter(
                    "<font color=\"red\">", "</font>");
            Highlighter highlighter = new Highlighter(htmlFormatter,
                    new QueryScorer(query));
            highlighter.setTextFragmenter(new SimpleFragmenter(180));
            for(int i = 0; i < scores.length; i ++){
                SearchResultBean resultBean = new SearchResultBean();
                
                Document doc = indexSearcher.doc(scores[i].doc);
                
                resultBean.setHtmlPath(doc.get("path"));
                String text = highlighter.getBestFragment(analyzer, "title", doc.get("title"));
                if (text == null || text.equals("")) {
                    text = doc.get("title");
                }
                resultBean.setHtmlTitle(text);
//                resultBean.setHtmlTitle(doc.get("title"));
                searchResult.add(resultBean);
            }
        }
    	 catch (IOException e) {
             e.printStackTrace();
         } catch (InvalidTokenOffsetsException e) {
             e.printStackTrace();
         } finally {
             if (indexSearcher != null) {
                 try {
                     indexSearcher.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
         }
    	
    /**	
    	IndexSearcher indexSearcher = null;
    	
    	try{
    	    Directory fsDirectory = new SimpleFSDirectory(new File(indexManager.getIndexDir()));  
    		indexSearcher = new IndexSearcher(fsDirectory);
//    		indexSearcher = new IndexSearcher(indexManager.getIndexDir());
    	}catch(IOException ioe){
    		ioe.printStackTrace();
    	}
        
    	QueryParser queryParser = new QueryParser(Version.LUCENE_31,"title",analyzer);
//    	QueryParser queryParser = new QueryParser("content",analyzer);
    	Query query = null;
    	try {
			query = queryParser.parse(searchWord);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(null != query && null != indexSearcher){			
			try {
			    TopDocs tops = indexSearcher.search(query, 20);
			    ScoreDoc[] scores = tops.scoreDocs;
//				Hits hits = indexSearcher.search(query);
				for(int i = 0; i < scores.length; i ++){
					SearchResultBean resultBean = new SearchResultBean();
					
					Document doc = indexSearcher.doc(scores[i].doc);
					
					resultBean.setHtmlPath(doc.get("path"));
					resultBean.setHtmlTitle(doc.get("title"));
					searchResult.add(resultBean);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		*/
    	return searchResult;
    }
}
