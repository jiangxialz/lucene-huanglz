/*
 * 文 件 名:  ArticleInfoIndex.java
 * 版    权:  深圳埃思欧纳信息咨询有限公司版权所有. YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  honny.huang
 * 修改时间:  2011-10-18
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.article.myownservlet;

import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;

import com.article.database.DBConnector;
import com.common.confManager.ConfManager;
import com.constants.IndexConstant;
import com.ue.data.search.indexer.lucene.Indexer;
import com.util.LogHelper;
/**
 * <执行main方法创建索引，或者定时创建索引>
 * <功能详细描述>
 * 
 * @author  honny.huang
 * @version  [版本号, 2011-10-18]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ArticleInfoIndex
{
    // 数据库连接变量
    Connection con = null;
    Statement sm = null;
    PreparedStatement psm = null;
   
    // 配置管理类实例
    private static ConfManager cm = ConfManager.getInstance();
    
    private static final int LIMIT = Integer.parseInt(cm.getPropValue(IndexConstant.NEWSINFO_LIMIT));
    
    /**
     * 初始化Servlet实例时调用该方法
     */
    public void init()throws ServletException
    {
        // 从conf.properties中读取servlet的配置参数
        String dbURL = cm.getPropValue("dbURL");
        String driver = cm.getPropValue("driver");
        String username = cm.getPropValue("username");
        String password = cm.getPropValue("password");
        try {
            // 连接数据库
            con = DBConnector.getConnection(driver, dbURL, username, password);
            sm = con.createStatement();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    /**
     * <创建索引>
     * <功能详细描述>
     * @throws Exception [参数说明]
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public void writeIndex() throws Exception 
    {
    	init();
        LogHelper.getLogger().info("Enter ArticleInfoIndex.writeIndex()");
        LogHelper.getLogger().info("Enter ArticleInfoIndex.queryCreateIndexTimeForLast()");
        String index_log_updateTime = queryCreateIndexTimeForLast();
        LogHelper.getLogger().info("Exit ArticleInfoIndex.queryCreateIndexTimeForLast()");
        
		int resultCount = 0;
		String lastRecordUpdateTime = "";
		for (int i = 0; true; i++) 
		{
			int start = i * LIMIT;
		
	        String sql = "select *, IF(0 = ? OR a.create_time > ? ,1 ,2) addORupdate from news a where  a.create_time > ? OR a.update_time > ?";
//	        String sql = "select *, IF(0 = ? OR a.create_time > ? ,1 ,2) addORupdate from news a where  DATE_FORMAT(a.saveTime, '%Y%m%d%H%i%s') > ?updateTime OR DATE_FORMAT(a.updateTime, '%Y%m%d%H%i%s') > ?updateTime";
	        sql = sql + " limit " + start + "," + LIMIT;
        
	        try {
	            // 判断SQL语句是否为查询语句，以放置修改数据库
	            sql = sql.trim();
	            LogHelper.getLogger().info("查询的sql语句为："+ sql);
	            if (!sql.toUpperCase().startsWith("SELECT ")){
	                throw new Exception("输入的SQL语句必须是查询语句！");
	            }
	            // 执行查询
	            psm = con.prepareStatement(sql);
	            // 将SQL语句中的？赋值
				psm.setString(1, index_log_updateTime);
				psm.setString(2, index_log_updateTime);
				psm.setString(3, index_log_updateTime);
				psm.setString(4, index_log_updateTime);
				LogHelper.getLogger().info("根据查询条件查询记录数");
				ResultSet rs = psm.executeQuery();
				
				// 获取结果集的记录数
				rs.last();
				// 获取查询sql中最后一条记录的更新时间作为更新索引的时间，准确度相对来说比更新完索引后插入now()要高些
				lastRecordUpdateTime = rs.getString("update_time");
			    resultCount = rs.getRow();
				rs.beforeFirst();
				LogHelper.getLogger().info("查询出的结果集的记录总数为："+ resultCount);
				int addCount = 0;
				int updateCount = 0;
				
				// 在查询完数据之后再进行索引的相关操作，避免长时间打开索引，减少破坏索引的几率
				Indexer indexer = new Indexer(null);
				// 索引的存储路径
				File localFile = new File(cm.getPropValue(IndexConstant.NEWS_PATH) + cm.getPropValue(IndexConstant.SEGMENTS));
				indexer.open(cm.getPropValue(IndexConstant.NEWS_PATH), !(localFile.exists()));
				while (rs.next())
				{
					HashMap<String, Object> map = new HashMap<String, Object>();
					// 索引字段必须与javaBean中的字段要一样，可不区分大小写
					map.put("ID", rs.getString("id"));
					map.put("title", rs.getString("title"));
					map.put("content", rs.getObject("content"));
					map.put("author", rs.getString("author"));
					map.put("create_time", rs.getString("create_time"));
					String[] teStrings = rs.getString("create_time").split(" ");
					map.put("intCreateTime", teStrings[0].replaceAll("-", ""));
					map.put("update_time", rs.getString("update_time"));
					map.put("type", rs.getString("type"));
					
					if (rs.getInt("addORupdate") == 1) {
						LogHelper.getLogger().info("增量索引");
						indexer.add(map);
						addCount++;
					} else if (rs.getInt("addORupdate") == 2) {
						LogHelper.getLogger().info("更新索引");
						indexer.update(rs.getString("id"), map);
						updateCount++;
					}
				}
				LogHelper.getLogger().info("此次增量索引数量："+ addCount);
				LogHelper.getLogger().info("此次更新索引数量："+ updateCount);
				 // 优化整理索引文件
				indexer.optimize();
				indexer.close(); // 关闭IndexWriter时,才把数据写入到索引文件中
	        } catch (Exception e){
	        	LogHelper.getLogger().info("出错了："+ e.getMessage());
	        	throw new RuntimeException(e);
	        }
	        // 查询完所有需要更新的记录 记录当次索引更新时间
			if(resultCount < LIMIT && resultCount > 0)
			{
				LogHelper.getLogger().info("往索引日志表增加一条记录，如果存在该记录则更新该记录");
				insertCreateIndexTimeForCurrent(lastRecordUpdateTime);
				break;
			}
		}
		LogHelper.getLogger().info("Exit ArticleInfoIndex.writeIndex()");
    }
    
    /**
     * <创建索引>
     * <功能详细描述>
     * @throws Exception [参数说明]
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public void writeIndex2() throws Exception 
    {
    	init();
        LogHelper.getLogger().info("Enter ArticleInfoIndex.writeIndex()");
        LogHelper.getLogger().info("Enter ArticleInfoIndex.queryCreateIndexTimeForLast()");
        String index_log_updateTime = queryCreateIndexTimeForLast();
        LogHelper.getLogger().info("Exit ArticleInfoIndex.queryCreateIndexTimeForLast()");
       
		int resultCount = 0;
		for (int i = 0; true; i++) 
		{
			int start = i * LIMIT;
		
	        String sql = "select *, IF(0 = ? OR a.create_time > ? ,1 ,2) addORupdate from news a";
	        sql = sql + " limit " + start + "," + LIMIT;
        
	        try {
	            // 判断SQL语句是否为查询语句，以放置修改数据库
	            sql = sql.trim();
	            LogHelper.getLogger().info("查询的sql语句为："+ sql);
	            if (!sql.toUpperCase().startsWith("SELECT ")){
	                throw new Exception("输入的SQL语句必须是查询语句！");
	            }
	            // 执行查询
	            psm = con.prepareStatement(sql);
	            // 将SQL语句中的？赋值
				psm.setString(1, index_log_updateTime);
				psm.setString(2, index_log_updateTime);
				LogHelper.getLogger().info("根据查询条件查询记录数");
				ResultSet rs = psm.executeQuery();
				
				// 获取结果集的记录数
				rs.last();
			    resultCount = rs.getRow();
				rs.beforeFirst();
				LogHelper.getLogger().info("查询出的结果集的记录总数为："+ resultCount);
				int addCount = 0;
				int updateCount = 0;
				// 在查询完数据之后再进行索引的相关操作，避免长时间打开索引，减少破坏索引的几率
				Indexer indexer = new Indexer(null);
		        // 索引的存储路径
				File localFile = new File(cm.getPropValue(IndexConstant.NEWS_PATH) + cm.getPropValue(IndexConstant.SEGMENTS));
				indexer.open(cm.getPropValue(IndexConstant.NEWS_PATH), !(localFile.exists()));
				while (rs.next())
				{
					HashMap<String, Object> map = new HashMap<String, Object>();
					// 索引字段必须与javaBean中的字段要一样，可不区分大小写
					map.put("ID", rs.getString("id"));
					map.put("title", rs.getString("title"));
					map.put("content", rs.getObject("content"));
					map.put("author", rs.getString("author"));
					map.put("create_time", rs.getString("create_time"));
					String[] teStrings = rs.getString("create_time").split(" ");
					map.put("intCreateTime", teStrings[0].replaceAll("-", ""));
					map.put("update_time", rs.getString("update_time"));
					map.put("type", rs.getString("type"));
					
					if (rs.getInt("addORupdate") == 1) {
						LogHelper.getLogger().info("增量索引");
						indexer.add(map);
						addCount++;
					} else if (rs.getInt("addORupdate") == 2) {
						LogHelper.getLogger().info("更新索引");
						indexer.update(rs.getString("id"), map);
						updateCount++;
					}
				}
				LogHelper.getLogger().info("此次增量索引数量："+ addCount);
				LogHelper.getLogger().info("此次更新索引数量："+ updateCount);
				 // 优化整理索引文件
				indexer.optimize();
				indexer.close(); // 关闭IndexWriter时,才把数据写入到索引文件中
	        } catch (Exception e){
	        	LogHelper.getLogger().info("出错了："+ e.getMessage());
	        	throw new RuntimeException(e);
	        }
	       
	        // 查询完所有需要更新的记录 记录当次索引更新时间
			if(resultCount < LIMIT)
			{
				LogHelper.getLogger().info("往索引日志表增加一条记录，如果存在该记录则更新该记录");
				insertCreateIndexTimeForCurrent2();
				break;
			}
		}
		LogHelper.getLogger().info("Exit ArticleInfoIndex.writeIndex()");
    }
    
    /**
     * 取出新闻上一次生成索引的时间
     * @return
     */
    private String queryCreateIndexTimeForLast() throws Exception 
    {
    	String index_log_sql = "select * from lucene_index_log_test where type = 1";
        // 执行查询
        ResultSet index_log_rs = sm.executeQuery(index_log_sql);
        String index_log_updateTime = "0";
        while (index_log_rs.next()) {
        	index_log_updateTime  = (String) index_log_rs.getObject("update_time");
		}
		return index_log_updateTime;
    }
    
    /**
     * 往索引日志表增加一条记录，如果存在该记录则更新该记录
     * @throws Exception
     */
    private void insertCreateIndexTimeForCurrent(String lastRecordUpdateTime)throws Exception 
    {
    	String sql = "insert into lucene_index_log_test(type,create_time,update_time,last_id) " +
    			"values (?,?,?,?) on duplicate key update update_time=? ,last_id=?";
    	// 执行查询
        psm = con.prepareStatement(sql);
        // 将SQL语句中的？赋值
		psm.setInt(1, 1);
		psm.setString(2, lastRecordUpdateTime);
		psm.setString(3, lastRecordUpdateTime);
		psm.setInt(4, 0);
		psm.setString(5, lastRecordUpdateTime);
		psm.setInt(6, 0);
		psm.execute();
    }
    
    /**
     * 往索引日志表增加一条记录，如果存在该记录则更新该记录
     * @throws Exception
     */
    private void insertCreateIndexTimeForCurrent2()throws Exception 
    {
    	String sql = "insert into lucene_index_log_test(type,create_time,update_time,last_id) " +
    			"values (?,now(),now(),?) on duplicate key update update_time=now() ,last_id=?";
    	// 执行查询
        psm = con.prepareStatement(sql);
        // 将SQL语句中的？赋值
		psm.setInt(1, 1);
		psm.setInt(2, 0);
		psm.setInt(3, 0);
		psm.execute();
    }

    // 测试
	public static void main(String[] args) 
	{
	    ArticleInfoIndex ci = new ArticleInfoIndex();
		try {
			long startTime = new Date().getTime();
			ci.writeIndex();
			long endTime = new Date().getTime();
			LogHelper.getLogger().info("\n这花费了" + (endTime - startTime) + " 毫秒增加到索引!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
