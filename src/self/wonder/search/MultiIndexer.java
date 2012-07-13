package self.wonder.search;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.MapFieldSelector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import com.common.confManager.ConfManager;
import com.constants.IndexConstant;
import com.search.po.DataGrid;
import com.ue.data.search.IInderer;

public class MultiIndexer implements IInderer
{
	 // 配置管理类实例
    private static ConfManager cm = ConfManager.getInstance();
    
    IInderer m_indexer = null;

    Directory m_directory = null;

    IndexWriter m_indexWriter = null;

    Properties m_properties;

    String m_strPath = "";
    //多索引目录
    String m_strPath1 = "";
    String m_strPath2 = "";
    String m_strPath3 = "";

    public MultiIndexer(Properties properties)
    {
        m_properties = properties;
    }

    public MultiIndexer() {
		// TODO Auto-generated constructor stub
	}

	/*
     * @see com.ue.data.search.IIndexer#close()
     */
    @SuppressWarnings("static-access")
	public void close() throws IOException
    {
    	/**
    	 * write.lock  bug
    	 * after which, you must be certain not to use the writer instance anymore.
    	 * try {
			   	writer.close();
			 } finally {
			   if (IndexWriter.isLocked(directory)) {
			     IndexWriter.unlock(directory);
			   }
			 }
    	 */
        if (m_indexWriter != null)
        {
            try{
                m_indexWriter.close(); // 关闭IndexWriter时,才把内存中的数据写到文件
            }catch (IOException e){
                e.printStackTrace();
            }finally {
            	 if (m_directory != null)
            	 {
            		 if (m_indexWriter.isLocked(m_directory))
    	           	 {
    	       			  m_indexWriter.unlock(m_directory);
    	       		 }
            		 try{
                         m_directory.close(); // 关闭索引存放目录
                     }catch (IOException e){
                         e.printStackTrace();
                     }
            	 }
            }
        }
    }

    /*
     * @see com.ue.data.search.IIndexer#open(java.lang.String, boolean)
     */
    public void open(String strPathOne, String strPathTwo, String strPathThree, boolean bCreate) throws Exception
    {
    	m_strPath1 = strPathOne;
        m_strPath2 = strPathTwo;
        m_strPath3 = strPathThree;
        m_strPath = StringHelper.isNullOrEmpty(m_strPath1)?(StringHelper.isNullOrEmpty(m_strPath2)? m_strPath3 : m_strPath2) : m_strPath1;
        m_directory = FSDirectory.open(new File(m_strPath));
        Analyzer luceneAnalyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);

        // 第一个参数是存放索引目录有FSDirectory（存储到磁盘上）和RAMDirectory（存储到内存中）， 第二个参数是使用的分词器，
        // 第三个：true，建立全新的索引，false,建立增量索引，第四个是建立的索引的最大长度。
        m_indexWriter = new IndexWriter(m_directory, luceneAnalyzer, bCreate, IndexWriter.MaxFieldLength.UNLIMITED);

        // 索引合并因子
        // SetMergeFactor（合并因子）
        // SetMergeFactor是控制segment合并频率的，其决定了一个索引块中包括多少个文档，当硬盘上的索引块达到多少时，
        // 将它们合并成一个较大的索引块。当MergeFactor值较大时，生成索引的速度较快。MergeFactor的默认值是10，建议在建立索引前将其设置的大一些。
        m_indexWriter.setMergeFactor(100);
        // SetMaxBufferedDocs（最大缓存文档数）
        // SetMaxBufferedDocs是控制写入一个新的segment前内存中保存的document的数目，
        // 设置较大的数目可以加快建索引速度，默认为10。
        m_indexWriter.setMaxBufferedDocs(100);
        // SetMaxMergeDocs（最大合并文档数）
        // SetMaxMergeDocs是控制一个segment中可以保存的最大document数目，值较小有利于追加索引的速度，默认Integer.MAX_VALUE，无需修改。
        // 在创建大量数据的索引时，我们会发现索引过程的瓶颈在于大量的磁盘操作，如果内存足够大的话，
        // 我们应当尽量使用内存，而非硬盘。可以通过SetMaxBufferedDocs来调整，增大Lucene使用内存的次数。
        m_indexWriter.setMaxMergeDocs(1000);
        // SetUseCompoundFile这个方法可以使Lucene在创建索引库时，会合并多个 Segments 文件到一个.cfs中。
        // 此方式有助于减少索引文件数量，对于将来搜索的效率有较大影响。
        // 压缩存储（True则为复合索引格式）
        m_indexWriter.setUseCompoundFile(true);

        // m_indexWriter.optimize();// 对索引进行优化
    }
    
    /*
     * @see com.ue.data.search.IIndexer#open(java.lang.String, boolean)
     */
    public void open(String strPath, boolean bCreate) throws Exception
    {
        m_strPath = strPath;
        m_directory = FSDirectory.open(new File(strPath));
        Analyzer luceneAnalyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);

        // 第一个参数是存放索引目录有FSDirectory（存储到磁盘上）和RAMDirectory（存储到内存中）， 第二个参数是使用的分词器，
        // 第三个：true，建立全新的索引，false,建立增量索引，第四个是建立的索引的最大长度。
        m_indexWriter = new IndexWriter(m_directory, luceneAnalyzer, bCreate, IndexWriter.MaxFieldLength.UNLIMITED);

        // 索引合并因子
        // SetMergeFactor（合并因子）
        // SetMergeFactor是控制segment合并频率的，其决定了一个索引块中包括多少个文档，当硬盘上的索引块达到多少时，
        // 将它们合并成一个较大的索引块。当MergeFactor值较大时，生成索引的速度较快。MergeFactor的默认值是10，建议在建立索引前将其设置的大一些。
        m_indexWriter.setMergeFactor(100);
        // SetMaxBufferedDocs（最大缓存文档数）
        // SetMaxBufferedDocs是控制写入一个新的segment前内存中保存的document的数目，
        // 设置较大的数目可以加快建索引速度，默认为10。
        m_indexWriter.setMaxBufferedDocs(100);
        // SetMaxMergeDocs（最大合并文档数）
        // SetMaxMergeDocs是控制一个segment中可以保存的最大document数目，值较小有利于追加索引的速度，默认Integer.MAX_VALUE，无需修改。
        // 在创建大量数据的索引时，我们会发现索引过程的瓶颈在于大量的磁盘操作，如果内存足够大的话，
        // 我们应当尽量使用内存，而非硬盘。可以通过SetMaxBufferedDocs来调整，增大Lucene使用内存的次数。
        m_indexWriter.setMaxMergeDocs(1000);
        // SetUseCompoundFile这个方法可以使Lucene在创建索引库时，会合并多个 Segments 文件到一个.cfs中。
        // 此方式有助于减少索引文件数量，对于将来搜索的效率有较大影响。
        // 压缩存储（True则为复合索引格式）
        m_indexWriter.setUseCompoundFile(true);

        // m_indexWriter.optimize();// 对索引进行优化
    }

    /*
     * 获得目录
     */
    public Directory getDirectoryOne() throws Exception
    {
        return FSDirectory.open(new File(m_strPath1));
    }
    
    /*
     * 获得目录
     */
    public Directory getDirectoryTwo() throws Exception
    {
        return FSDirectory.open(new File(m_strPath2));
    }
    
    /*
     * 获得目录
     */
    public Directory getDirectoryThree() throws Exception
    {
        return FSDirectory.open(new File(m_strPath3));
    }
    
    /*
     * 获得目录
     */
    public Directory getDirectory() throws Exception
    {
        return FSDirectory.open(new File(m_strPath));
    }

    /**
     * 添加
     */
    public boolean add(HashMap<String, Object> map) throws Exception
    {
        Document document = new Document();
        for (Map.Entry<String, Object> entry : map.entrySet())
        {
        	String strKey = entry.getKey();
        	Object objValue = entry.getValue();
            if (objValue != null)
            {
                Field field = new Field(strKey, objValue.toString(), Field.Store.YES, Field.Index.ANALYZED);
                document.add(field);
            }
        }
        m_indexWriter.addDocument(document);
        return true;
    }
    
    /**
     * <优化索引>
     */
    public boolean optimize() throws CorruptIndexException, IOException
    {
        m_indexWriter.optimize();
        m_indexWriter.commit();
        return true;
    }

    /*
     * @see com.ue.data.search.IIndexer#remove(java.lang.String)
     */
    public boolean remove(String strID) throws Exception
    {
        Term term = new Term("ID", strID);
        m_indexWriter.deleteDocuments(term);
        m_indexWriter.optimize();
        m_indexWriter.commit();
        return true;
    }

    /*
     * @see com.ue.data.search.IIndexer#removeAll()
     */
    public boolean removeAll()
    {
        return true;
    }

    /*
     * 更新索引文件
     * @see com.ue.data.search.IIndexer#update(java.lang.String,
     * java.util.HashMap)
     */
    public boolean update(String strID, HashMap<String, Object> map) throws Exception
    {
        Term term = new Term("ID", strID);
        Document document = new Document();
        for (Map.Entry<String, Object> entry : map.entrySet())
        {
        	String strKey = entry.getKey();
        	Object objValue = entry.getValue();
            if (objValue != null)
            {
                Field field = new Field(strKey, objValue.toString(), Field.Store.YES, Field.Index.ANALYZED);
                document.add(field);
            }
        }
        m_indexWriter.updateDocument(term, document);
        return true;
    }
    
    /**
     * 搜索
     * @param strQueryString 查询条件
     * @param offset
     * @param limit
     * @param strSorts 排序规则
     * @return
     * @throws Exception
     */
  public <T> DataGrid<List<T>> search(T object, String strQueryString, WonderSearchBasePO sbpo) throws Exception
  {
	  /**
      Analyzer analyzer = new IKAnalyzer();
      StringReader reader = new StringReader(strQueryString);
      TokenStream ts = analyzer.tokenStream("*", reader);
      //添加工具类  注意：以下这些与之前lucene2.x版本不同的地方  
      /**
       * 2011-12-14 16:05:05 [INFO]-[ArticleInfoSearchService.java:73]-[main]-sbWhere=======   ( title : 软件开发工程师 ) 
       * 软件开发|(11 15)软件|(11 13)开发|(13 15)工程师|(15 18)工程|(15 17)
       *
//      TermAttribute termAtt  = (TermAttribute)ts.addAttribute(TermAttribute.class);  
//      OffsetAttribute offAtt  = (OffsetAttribute)ts.addAttribute(OffsetAttribute.class);  
//      // 循环打印出分词的结果，及分词出现的位置  
//      while(ts.incrementToken()){  
//          System.out.print(termAtt.term() + "|("+ offAtt.startOffset() + " " + offAtt.endOffset()+")");   
//      }  
      Iterator<AttributeImpl> it = ts.getAttributeImplsIterator();
      while (it.hasNext())
      {
          System.out.println((AttributeImpl) it.next());
      }
      */
      return search(object, strQueryString, sbpo, null);
  }

  /**
   * 调用搜索方法返回搜索结果并对结果进行处理
   * @param <T>
   * @param object
   * @param query
   * @param offset
   * @param limit
   * @param strSorts
   * @return
   */
  @SuppressWarnings("unchecked")
public <T> DataGrid<List<T>> search(T object, Query query, WonderSearchBasePO sbpo)
{
	  DataGrid<List<T>> dataGrid = new DataGrid<List<T>>();
	  List<T> beanList = new ArrayList<T>();
	  MultiSearcher searcher = null;
      try
      {
//    	  Directory directoryOne = getDirectoryOne();
//    	  Directory directoryTwo = getDirectoryTwo();
//    	  Directory directoryThree = getDirectoryThree();
//    	  //创建两个IndexSearcher，以实现在多个索引目录进行查询
    	  IndexSearcher searcherOne = new IndexSearcher(FSDirectory.open(new File(cm.getPropValue(IndexConstant.CORP_PATH_ONE))), true);
    	  IndexSearcher searcherTwo = new IndexSearcher(FSDirectory.open(new File(cm.getPropValue(IndexConstant.CORP_PATH_TWO))), true);
    	  IndexSearcher searcherThree = new IndexSearcher(FSDirectory.open(new File(cm.getPropValue(IndexConstant.CORP_PATH_THREE))), true);
    	  IndexSearcher[] searchers = { searcherOne, searcherTwo, searcherThree };
//    	  IndexSearcher[] searchers = new IndexSearcher[Integer.valueOf(cm.getPropValue(IndexConstant.CORP_INDEX_NUM))];
//    	  for (int i = 1; i <= searchers.length; i++) {
//    		  System.out.println(m_strPath+i);
//    		  System.out.println("m_strPath"+i);
//    		  searchers[i]=new IndexSearcher(FSDirectory.open(new File(cm.getPropValue(IndexConstant.CORP_PATH1))), true);
//		  }
          //使用MultiSearcher进行多域搜索
          searcher = new MultiSearcher(searchers);
          // 给定义的字段设置排序
          Sort sort = getFieldsSort(sbpo.getOrder_str());
          Class clazzT = object.getClass();
          if (query == null)
              query = new MatchAllDocsQuery();
              // 对索引中的字段进行查询
	          TopDocs topDocs = searcher.search(query, null, sbpo.getOffset()+sbpo.getLimit(), sort);
	          // 获取总记录数
	          dataGrid.setTotalElements(topDocs.totalHits);
	          ScoreDoc[] scoreDocs = topDocs.scoreDocs;
              if (scoreDocs.length > 0)
	          {
	              Document document = searcher.doc(scoreDocs[0].doc);
	              // 获取需要查询的属性，这就要求bean中得属性要与索引中的字段名称完全对应
	              MapFieldSelector mapFieldSelector = getSearchFields(sbpo.getSearchFields(),clazzT);
	              for (int i = sbpo.getOffset(); i < scoreDocs.length; i++)
	              {
	            	  // 获取document文档信息
	                  document = searcher.doc(scoreDocs[i].doc, mapFieldSelector);
	                  JSONObject jsonObject = new JSONObject();
	                  T o = (T)clazzT.newInstance();
	                  for (Fieldable fieldable : document.getFields())
	                  {
	                	  String strField = fieldable.name();
	                	  jsonObject.put(strField, document.get(strField));
	                  }
	                  // 将json数据转换为对应的bean对象
	                  o = jsonToVO(o, jsonObject.toString());
	                  beanList.add(o);
	              }
	             
	          }
      }
//      try
//      {
//    	  Directory directory = getDirectory();
//          searcher = new IndexSearcher(directory, true);
//          // 给定义的字段设置排序
//          Sort sort = getFieldsSort(strSorts);
//          Class clazzT = object.getClass();
//          if (query == null)
//              query = new MatchAllDocsQuery();
//              // 对索引中的字段进行查询
//	          TopDocs topDocs = searcher.search(query, null, limit, sort);
//	          dataGrid.setTotalElements(topDocs.totalHits);
//	          ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//	          // limit == scoreDocs.length; 
//              if (scoreDocs.length > 0)
//	          {
//	              Document document = searcher.doc(scoreDocs[0].doc);
////	              java.text.NumberFormat format = java.text.NumberFormat.getNumberInstance();  
//	              for (int i = offset; i < scoreDocs.length; i++)
//	              {
////	            	  System.out.println("准确度为：" + format.format(scoreDocs[i].score * 100.0) + "%");
//	                  document = searcher.doc(scoreDocs[i].doc);
//	                  JSONObject jsonObject = new JSONObject();
//	                  T o = (T)clazzT.newInstance();
//	                  for (Fieldable fieldable : document.getFields())
//	                  {
//	                	  String strField = fieldable.name();
//	                	  jsonObject.put(strField, document.get(strField));
//	                  }
//	                  o = jsonToVO(o, jsonObject.toString());
//	                  beanList.add(o);
//	              }
//	             
//	          }
//      }
      catch (Exception e)
      {
          e.printStackTrace();
      }
      finally
      {
          if (searcher != null)
          {
              try
              {
                  searcher.close();
              }
              catch (IOException e)
              {
                  e.printStackTrace();
              }
          }
      }
      dataGrid.setData(beanList);
      return dataGrid;
  }
  
  /**
   * <获取查询的属性>
   * <功能详细描述>
   * @param searchFields
   * @param clazz
   * @return [参数说明]
   * 
   * @return MapFieldSelector [返回类型说明]
   * @exception throws [违例类型] [违例说明]
   * @see [类、类#方法、类#成员]
   */
  public  MapFieldSelector getSearchFields(String[] searchFields, Class clazz){
      MapFieldSelector mapFieldSelector = null;
	  // 如果设置了要查询的属性则返回要查询的,否则返回整个bean对象包含的属性
	  if (StringHelper.isNullOrEmpty(searchFields)) {
		  List<String> list = new ArrayList<String>();
		  // 获取实体类的所有属性，返回Field数组
		  java.lang.reflect.Field[] field = clazz.getDeclaredFields();
		  for (int i = 0; i < field.length; i++) { // 遍历所有属性
			  list.add(field[i].getName());// 获取属性的名字
		  }
		  mapFieldSelector = new MapFieldSelector(list);
	  }else {
		  mapFieldSelector = new MapFieldSelector(searchFields);
	  }
	  return mapFieldSelector;
  }
  
  /**
   * <获取查询的属性>
   * <功能详细描述>
   * @param searchFields
   * @param clazz
   * @return [参数说明]
   * 
   * @return MapFieldSelector [返回类型说明]
   * @exception throws [违例类型] [违例说明]
   * @see [类、类#方法、类#成员]
   */
  public  MapFieldSelector getSearchFields2(String[] searchFields, Class clazz){
      MapFieldSelector mapFieldSelector = null;
	  try {
		  // 如果设置了要查询的属性则返回要查询的,否则返回整个bean对象包含的属性
		  if (StringHelper.isNullOrEmpty(searchFields)) {
			  BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			  PropertyDescriptor[] pr = beanInfo.getPropertyDescriptors();
			  List<String> list = new ArrayList<String>();
			  for(int i=0 ; i<pr.length ; i++){
				  list.add(pr[i].getName());
			  }		
			  mapFieldSelector = new MapFieldSelector(list);
		  }else {
			  mapFieldSelector = new MapFieldSelector(searchFields);
		  }
	  } catch (IntrospectionException e) {
		e.printStackTrace();
	  }
	  return mapFieldSelector;
  }
  
  /**
	 * 将json转换为对应的bean对象
	 * @param json
 * @return 
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T jsonToVO(T object,String json) throws IOException{
		ObjectMapper objectMapper=new ObjectMapper();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",Locale.US);
		
		SerializationConfig serConfig = objectMapper.getSerializationConfig();
		
		serConfig.setDateFormat(dateFormat);

		DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();

		deserializationConfig.setDateFormat(dateFormat);

		objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
		try {
			object = (T) objectMapper.readValue(json, object.getClass());
			return object;
		} catch (IOException e) {
			throw e;
		}
	}
  
  /**
   * 给定义的字段排序
   * @param strSorts
   */
  private Sort getFieldsSort(String strSorts)
  {
	  Sort sort = new Sort();
      if (StringUtils.isNotBlank(strSorts))
      {
          String[] sorts = strSorts.split(",");
          SortField[] sortFields = new SortField[sorts.length + 1];
          sortFields[sorts.length] = SortField.FIELD_DOC;
          for (int i = 0; i < sorts.length; i++)
          {
              int intSortType = SortField.STRING;
              String[] arr = sorts[i].split(" ");
              String strSortField = arr[0];
              int bPos = strSortField.indexOf('[');
              int ePos = strSortField.indexOf(']');
              if (bPos > 0 && ePos > bPos)
              {
                  strSortField = arr[0].substring(0, bPos);
                  String strSortType = arr[0].substring(bPos + 1, ePos);
                  intSortType = getSortType(strSortType);
              }
              boolean bReverse = false;
              if (arr.length == 2 && "desc".equalsIgnoreCase(arr[1]))
              {
                  bReverse = true;
              }
              sortFields[i] = new SortField(strSortField, intSortType, bReverse);
          }
          sort.setSort(sortFields);
      }
      return sort;
  }
  
    private int getSortType(String strSortType)
    {
        if ("BYTE".equalsIgnoreCase(strSortType))
            return SortField.BYTE;
        // if("CUSTOM".equalsIgnoreCase(strSortType)) return SortField.CUSTOM;
        // if("DOC".equalsIgnoreCase(strSortType)) return SortField.DOC;
        if ("DOUBLE".equalsIgnoreCase(strSortType))
            return SortField.DOUBLE;
        if ("FLOAT".equalsIgnoreCase(strSortType))
            return SortField.FLOAT;
        if ("INT".equalsIgnoreCase(strSortType))
            return SortField.INT;
        if ("LONG".equalsIgnoreCase(strSortType))
            return SortField.LONG;
        if ("SCORE".equalsIgnoreCase(strSortType))
            return SortField.SCORE;
        if ("STRING_VAL".equalsIgnoreCase(strSortType))
            return SortField.STRING_VAL;
        else
            return SortField.STRING;
    }
    

    /**
     * <搜索处理方法>
     * @param strQueryString 查询条件
     * @param offset
     * @param limit
     * @param strSorts 排序规则
     * @param analyzer 分词器 默认StandardAnalyzer
     * @return
     * @throws Exception [参数说明]
     * 
     * @return DataTable [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    
	  public <T> DataGrid<List<T>> search(T object, String strQueryString, WonderSearchBasePO sbpo, Analyzer analyzer)
	  throws Exception
	  {
		
			if (analyzer == null)
			{
			  analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
			}
			// 为查询分析器QueryParser 指定查询字段和分词器
			// QueryParser组合多种分析器，提供类似sql语句的lucene查询语句，以方便实现高级搜索行为。
			QueryParser queryParser = new QueryParser(Version.LUCENE_CURRENT, "", analyzer);
			queryParser.setAllowLeadingWildcard(true);// 设为true，允许使用通配符
			queryParser.setEnablePositionIncrements(false);// 设为true，以便在查询结果的立场增量
			queryParser.setLowercaseExpandedTerms(false);
			Query query = null;
			if (StringUtils.isNotBlank(strQueryString))
			{
			  queryParser.setDefaultOperator(QueryParser.OR_OPERATOR);// 设置的QueryParser的布尔运算符。
			  // 用分析器 queryParser创建查询语句,关键字为strQueryString
			  // 用创建的分词器对关键字querystring分词,然后对索引中的content字段进行查询
			  query = queryParser.parse(strQueryString); 
			}
			return search(object, query, sbpo);
	 }
	  
}
