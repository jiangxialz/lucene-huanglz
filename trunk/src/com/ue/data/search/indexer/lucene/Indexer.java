package com.ue.data.search.indexer.lucene;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.StringHelper;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.common.confManager.ConfManager;
import com.constants.IndexConstant;
import com.ue.data.search.IInderer;

//import sun.security.krb5.Config;

public class Indexer implements IInderer
{
	 // 配置管理类实例
    private static ConfManager cm = ConfManager.getInstance();
	
    IInderer m_indexer = null;

    Directory m_directory = null;

    IndexWriter m_indexWriter = null;

    Properties m_properties;

    String m_strPath = "";

    public static Indexer newInstance(Properties properties) throws Exception
    {
        Indexer indexer = new Indexer(properties);
        File file = new File(cm.getPropValue(IndexConstant.NEWS_PATH) + File.separator + "segments.gen");
        indexer.open(cm.getPropValue(IndexConstant.NEWS_PATH), !file.exists());
        return indexer;
    }

    public Indexer(Properties properties)
    {
        m_properties = properties;
    }

    public Indexer(String strPath, Properties properties)
    {
        if (strPath == null)
            strPath = cm.getPropValue(IndexConstant.NEWS_PATH);
//            strPath = Config.IndexPath;
        m_strPath = strPath;
        m_properties = properties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ue.data.search.IIndexer#close()
     */
    public void close() throws IOException
    {
        if (m_indexWriter != null)
        {
            try
            {
                m_indexWriter.close(); // 关闭IndexWriter时,才把内存中的数据写到文件
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if (m_directory != null)
        {
            try
            {
                m_directory.close(); // 关闭索引存放目录
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
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
        for (String strKey : map.keySet())
        {
            if (map.get(strKey) != null)
            {
                Field field = new Field(strKey, map.get(strKey).toString(), Field.Store.YES, Field.Index.ANALYZED);
                document.add(field);
            }
        }
        m_indexWriter.addDocument(document);
        // m_indexWriter.optimize();
        // m_indexWriter.commit();
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
     * (non-Javadoc)
     * 
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
     * (non-Javadoc)
     * 
     * @see com.ue.data.search.IIndexer#removeAll()
     */
    public boolean removeAll()
    {
        return true;
    }

    /*
     * (non-Javadoc) 更新索引文件
     * 
     * @see com.ue.data.search.IIndexer#update(java.lang.String,
     * java.util.HashMap)
     */
    public boolean update(String strID, HashMap<String, Object> map) throws Exception
    {
        Term term = new Term("ID", strID);
        Document document = new Document();
        for (String strKey : map.keySet())
        {
            if (map.get(strKey) != null)
            {
                Field field = new Field(strKey, map.get(strKey).toString(), Field.Store.YES, Field.Index.ANALYZED);
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
//    public DataTable search(String strQueryString, int offset, int limit, String strSorts) throws Exception
//    {
//        Analyzer analyzer = new IKAnalyzer();
//        StringReader reader = new StringReader(strQueryString);
//        TokenStream ts = analyzer.tokenStream("*", reader);
//        Iterator<AttributeImpl> it = ts.getAttributeImplsIterator();
//        while (it.hasNext())
//        {
//            System.out.println((AttributeImpl) it.next());
//        }
//        return search(strQueryString, offset, limit, strSorts, null);
//    }

    /**
     * @param query
     * @param offset
     * @param limit
     * @param strSorts
     * @return
     * @throws Exception [参数说明]
     * 
     * @return DataTable [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
//    public DataTable search(Query query, int offset, int limit, String strSorts)
//    {
//        // order by ID[STRING] asc,
//        Directory directory;
//        DataTable dt = new DataTable("DataTable");
//        IndexSearcher searcher = null;
//        try
//        {
//            directory = getDirectory();
//            searcher = new IndexSearcher(directory, true);
//
//            Sort sort = new Sort();
//            if (StringHelper.isNotNullAndEmpty(strSorts))
//            {
//                String[] sorts = strSorts.split(",");
//                SortField[] sortFields = new SortField[sorts.length + 1];
//                sortFields[sorts.length] = SortField.FIELD_DOC;
//                for (int i = 0; i < sorts.length; i++)
//                {
//                    int intSortType = SortField.STRING;
//                    String[] arr = sorts[i].split(" ");
//                    String strSortField = arr[0];
//                    int bPos = strSortField.indexOf('[');
//                    int ePos = strSortField.indexOf(']');
//                    if (bPos > 0 && ePos > bPos)
//                    {
//                        strSortField = arr[0].substring(0, bPos);
//                        String strSortType = arr[0].substring(bPos + 1, ePos);
//                        intSortType = getSortType(strSortType);
//                    }
//                    boolean bReverse = false;
//                    if (arr.length == 2 && "desc".equalsIgnoreCase(arr[1]))
//                    {
//                        bReverse = true;
//                    }
//                    sortFields[i] = new SortField(strSortField, intSortType, bReverse);
//                }
//                sort.setSort(sortFields);
//            }
//            /*
//             * topScoreDocCollector results =
//             * TopScoreDocCollector.create(limit,true); searcher.search(query,
//             * results); TopDocs topDocs = results.topDocs(offset,limit);
//             */
//            // q = q.rewrite(is.getIndexReader());
//            // is.setSimilarity(new DefaultSimilarity());//Expert: Set the
//            // Similarity implementation used by this Searcher.
//            // AccessibleTopHitCollector collector = new
//            // AccessibleTopHitCollector(1000, true, true);
//            if (query == null)
//                query = new MatchAllDocsQuery();
//            TopDocs topDocs = searcher.search(query, null, limit, sort);
//            dt.setTotalRecords(topDocs.totalHits);
//            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//            // System.out.print("记录条数:"+scoreDocs.length);
//            if (scoreDocs.length > 0)
//            {
//                Document document = searcher.doc(scoreDocs[0].doc);
//                for (Fieldable fieldable : document.getFields())
//                {
//                    String strField = fieldable.name();
//                    if (!dt.getColumns().containsKey(strField))
//                    {
//                        dt.getColumns().add(strField);
//                    }
//                }
//                for (int i = offset; i < scoreDocs.length; i++)
//                {
//                    document = searcher.doc(scoreDocs[i].doc);
//                    DataRow row = dt.newRow();
//                    for (Fieldable fieldable : document.getFields())
//                    {
//                        String strField = fieldable.name();
//                        String strValue = document.get(strField);
//                        row.set(strField, strValue);
//                        // System.out.println(strField + ":" + strValue);
//                    }
//                    dt.addRow(row);
//                }
//            }
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//            if (searcher != null)
//            {
//                try
//                {
//                    searcher.close();
//                }
//                catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//        return dt;
//    }

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
//    public DataTable search(String strQueryString, int offset, int limit, String strSorts, Analyzer analyzer)
//            throws Exception
//    {
//        if (analyzer == null)
//            analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
//        QueryParser queryParser = new QueryParser(Version.LUCENE_CURRENT, "", analyzer);
//        queryParser.setAllowLeadingWildcard(true);// 设为true，允许使用通配符
//        queryParser.setEnablePositionIncrements(false);// 设为true，以便在查询结果的立场增量
//        queryParser.setLowercaseExpandedTerms(false);
//        Query query = null;
//        if (StringHelper.isNotNullAndEmpty(strQueryString))
//        {
//            queryParser.setDefaultOperator(QueryParser.OR_OPERATOR);// 设置的QueryParser的布尔运算符。
//            query = queryParser.parse(strQueryString);
//        }
//
//        return search(query, offset, limit, strSorts);
//    }
//
//    public DataTable search(String strSQL, String strQueryString, int offset, int limit, String strOrders)
//            throws Exception
//    {
//        DataTable dt_1 = search(strQueryString, offset, limit, strOrders);
//        String strIDs = "";
//        for (DataRow row : dt_1.getRows())
//        {
//            strIDs = StringHelper.linkString(strIDs, ",", row.getString("ID"));
//        }
//        if (strSQL.indexOf("?ID") > 0)
//            strSQL = strSQL.replace("?ID", strIDs);
//        else
//            strSQL += " where ID in (" + strIDs + ")";
//        return DBManager.getDataTable(strSQL);
//    }

}
