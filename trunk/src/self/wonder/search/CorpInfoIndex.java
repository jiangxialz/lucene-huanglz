package self.wonder.search;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.article.database.DBConnector;
import com.common.confManager.ConfManager;
import com.constants.IndexConstant;
import com.util.LogHelper;

public class CorpInfoIndex extends HttpServlet{
	
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
	
	public CorpInfoIndex() {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    // 配置管理类实例
    private static ConfManager cm = ConfManager.getInstance();
    
    // 从conf.properties中读取servlet的配置参数
    String dbURL = cm.getPropValue("dbURL");
    String driver = cm.getPropValue("driver");
    String username = cm.getPropValue("username");
    String password = cm.getPropValue("password");
    
    private static final int LIMIT = Integer.parseInt(cm.getPropValue(IndexConstant.CORPINFO_LIMIT));
    
    /**
     * <创建索引>
     * <功能详细描述>
     * @throws Exception [参数说明]
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
	@SuppressWarnings("null")
	public void writeIndex() throws Exception 
    {
		Connection con = DBConnector.getConnection(driver, dbURL, username, password);;
		PreparedStatement psm = null;
		for (int i = 0; true; i++) 
		{
			int start = i * LIMIT;
		
	        String sql = "select id,name,size,type,domain,description,address,url,tel,contact_name,phone,status from corp";
	        sql = sql + " limit " + start + "," + LIMIT;
        
	        int addCount = 0;
	        try {
	            // 判断SQL语句是否为查询语句，以放置修改数据库
	            sql = sql.trim();
//	            LogHelper.getLogger().info("查询的sql语句为："+ sql);
	            if (!sql.toUpperCase().startsWith("SELECT ")){
	                throw new Exception("输入的SQL语句必须是查询语句！");
	            }
	            // 执行查询
	            psm = con.prepareStatement(sql);
//				LogHelper.getLogger().info("根据查询条件查询记录数");
				ResultSet rs = psm.executeQuery();
				
				// 在查询完数据之后再进行索引的相关操作，避免长时间打开索引，减少破坏索引的几率
				MultiIndexer indexer = new MultiIndexer();
				// 索引的存储路径
				if (i>=0 && i<=100) {
					File localFile = new File(cm.getPropValue(IndexConstant.CORP_PATH_ONE) + cm.getPropValue(IndexConstant.SEGMENTS));
					indexer.open(cm.getPropValue(IndexConstant.CORP_PATH_ONE), null, null, !(localFile.exists()));
				}else if (i>101 && i<=200){
					File localFile = new File(cm.getPropValue(IndexConstant.CORP_PATH_TWO) + cm.getPropValue(IndexConstant.SEGMENTS));
					indexer.open(null, cm.getPropValue(IndexConstant.CORP_PATH_TWO), null, !(localFile.exists()));
				}else {
					File localFile = new File(cm.getPropValue(IndexConstant.CORP_PATH_THREE) + cm.getPropValue(IndexConstant.SEGMENTS));
					indexer.open(null, null, cm.getPropValue(IndexConstant.CORP_PATH_THREE), !(localFile.exists()));
				}
				while (rs.next())
				{
					HashMap<String, Object> map = new HashMap<String, Object>();
					// 索引字段必须与javaBean中的字段要一样，可不区分大小写
					map.put("ID", rs.getString("id"));
					map.put("corpName", rs.getString("name"));
					map.put("size", rs.getObject("size"));
					map.put("type", rs.getString("type"));
					map.put("domain", rs.getString("domain"));
					map.put("description", rs.getString("description"));
					map.put("address", rs.getString("address"));
					map.put("url", rs.getString("url"));
					map.put("tel", rs.getString("tel"));
					map.put("contactName", rs.getString("contact_name"));
					map.put("phone", rs.getString("phone"));
					map.put("status", rs.getString("status"));
					
					indexer.add(map);
					addCount++;
				}
				LogHelper.getLogger().info("此次增量索引数量："+ addCount);
				 // 优化整理索引文件
				indexer.optimize();
				indexer.close(); // 关闭IndexWriter时,才把数据写入到索引文件中
	        } catch (Exception e){
	        	LogHelper.getLogger().info("出错了：",e);
	        	throw new RuntimeException(e);
	        }finally{
		        psm.close();
		        psm = null;
	        }
			if(addCount < LIMIT )
			{
				break;
			}
		}
		con.close();
    	con = null;
		LogHelper.getLogger().info("Exit PostInfoIndex.writeIndex()");
    }
	
 // 测试
	public static void main(String[] args) 
	{
		CorpInfoIndex ci = new CorpInfoIndex();
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
