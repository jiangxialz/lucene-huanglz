package self.wonder.search;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.article.database.DBConnector;
import com.common.confManager.ConfManager;
import com.constants.IndexConstant;
import com.ue.data.search.indexer.lucene.Indexer;
import com.util.LogHelper;

public class PostInfoIndex2 extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	public void init(HttpServletRequest request, HttpServletResponse response) {
		try {
			doPost(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PostInfoIndex2() {
		super();
	}
	public void destroy() {
		super.destroy(); 
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
           request.setCharacterEncoding("utf-8");
           response.setCharacterEncoding("utf-8");
           this.doPost(request, response);
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
		try {
			writeIndex();
//			writeIndex(request,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 数据库连接变量
    Connection con = null;
    Statement sm = null;
    PreparedStatement psm = null;
   
    // 配置管理类实例
    private static ConfManager cm = ConfManager.getInstance();
    
    private static final int LIMIT = Integer.parseInt(cm.getPropValue(IndexConstant.POSTINFO_LIMIT));
    
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
//            sm = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE,
//                    ResultSet.HOLD_CURSORS_OVER_COMMIT);
            sm = con.createStatement();
//            sm.setFetchSize(Integer.MIN_VALUE);
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
//    public void writeIndex(HttpServletRequest request, HttpServletResponse response) throws Exception 
    {
    	init();
        LogHelper.getLogger().info("Enter PostInfoIndex.writeIndex()");
        LogHelper.getLogger().info("Enter PostInfoIndex.queryCreateIndexTimeForLast()");
        String index_log_updateTime = queryCreateIndexTimeForLast();
        LogHelper.getLogger().info("Exit PostInfoIndex.queryCreateIndexTimeForLast()");
        RequestDispatcher rd = null;
		int resultCount = 0;
		String lastRecordUpdateTime = "";
		long startTime = new Date().getTime();
		for (int i = 0; true; i++) 
		{
			int start = i * LIMIT;
		
	        String sql = "select *, IF(0 = ? OR a.last_crawl_date > ? ,1 ,2) addORupdate from post a where  a.last_crawl_date > ? ";
	        sql = sql + " limit " + start + "," + LIMIT;
        
	        try {
	            // 判断SQL语句是否为查询语句，以放置修改数据库
	            sql = sql.trim();
//	            LogHelper.getLogger().info("查询的sql语句为："+ sql);
	            if (!sql.toUpperCase().startsWith("SELECT ")){
	                throw new Exception("输入的SQL语句必须是查询语句！");
	            }
	            // 执行查询
	            psm = con.prepareStatement(sql);
	            // 将SQL语句中的？赋值
				psm.setString(1, index_log_updateTime);
				psm.setString(2, index_log_updateTime);
				psm.setString(3, index_log_updateTime);
//				LogHelper.getLogger().info("根据查询条件查询记录数");
				ResultSet rs = psm.executeQuery();
				
				// 获取结果集的记录数
				rs.last();
				// 获取查询sql中最后一条记录的更新时间作为更新索引的时间，准确度相对来说比更新完索引后插入now()要高些
				lastRecordUpdateTime = rs.getString("last_crawl_date");
			    resultCount = rs.getRow();
				rs.beforeFirst();
//				LogHelper.getLogger().info("查询出的结果集的记录总数为："+ resultCount);
				int addCount = 0;
//				int updateCount = 0;
				
				// 在查询完数据之后再进行索引的相关操作，避免长时间打开索引，减少破坏索引的几率
				Indexer indexer = new Indexer(null);
				// 索引的存储路径
				File localFile = new File(cm.getPropValue(IndexConstant.POST_PATH) + cm.getPropValue(IndexConstant.SEGMENTS));
				indexer.open(cm.getPropValue(IndexConstant.POST_PATH), !(localFile.exists()));
				while (rs.next())
				{
					HashMap<String, Object> map = new HashMap<String, Object>();
					// 索引字段必须与javaBean中的字段要一样，可不区分大小写
					map.put("ID", rs.getString("id"));
					map.put("name", rs.getString("name"));
					map.put("corpCrawlCode", rs.getObject("corp_crawl_code"));
					map.put("jobType", rs.getString("job_type"));
					map.put("area", rs.getString("area"));
					map.put("workYear", rs.getString("work_year"));
					map.put("diploma", rs.getString("diploma"));
					map.put("description", rs.getString("description"));
					map.put("status", rs.getString("status"));
					map.put("lastCrawlDate", rs.getString("last_crawl_date"));
					String[] teStrings = rs.getString("last_crawl_date").split(" ");
					map.put("intLastCrawlDate", teStrings[0].replaceAll("-", ""));
					
					if (rs.getInt("addORupdate") == 1) {
//						LogHelper.getLogger().info("增量索引");
						indexer.add(map);
						addCount++;
					} else if (rs.getInt("addORupdate") == 2) {
						LogHelper.getLogger().info("更新索引");
						indexer.update(rs.getString("id"), map);
//						updateCount++;
					}
				}
				LogHelper.getLogger().info("此次增量索引数量："+ addCount);
//				LogHelper.getLogger().info("此次更新索引数量："+ updateCount);
				 // 优化整理索引文件
				indexer.optimize();
				indexer.close(); // 关闭IndexWriter时,才把数据写入到索引文件中
	        } catch (Exception e){
	        	LogHelper.getLogger().info("出错了：",e);
//	        	request.setAttribute("res", "索引失败");	
//				rd=request.getRequestDispatcher("./results.jsp");
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
		long endTime = new Date().getTime();
		LogHelper.getLogger().info("\n这花费了" + (endTime - startTime) + " 毫秒增加到索引!");
//		request.setAttribute("res", "索引创建成功");
//		rd=request.getRequestDispatcher("./results.jsp");
//		rd.forward(request, response);
		LogHelper.getLogger().info("Exit PostInfoIndex.writeIndex()");
    }
    
    /**
     * 取出新闻上一次生成索引的时间
     * @return
     */
    private String queryCreateIndexTimeForLast() throws Exception 
    {
    	String index_log_sql = "select * from index_log where type = 1";
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
    	String sql = "insert into index_log(type,create_time,update_time,last_id) " +
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
    
 // 测试
	public static void main(String[] args) 
	{
		PostInfoIndex2 ci = new PostInfoIndex2();
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
