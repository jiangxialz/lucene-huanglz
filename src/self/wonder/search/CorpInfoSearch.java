package self.wonder.search;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.common.confManager.ConfManager;
import com.constants.IndexConstant;
import com.search.po.DataGrid;
import com.search.po.SearchBasePO;
import self.wonder.search.KeyAnalysisSupport;
import self.wonder.search.Indexer;

import com.util.LogHelper;
import com.util.page.PageableResultDataImpl;

public class CorpInfoSearch extends HttpServlet {

	private static final long serialVersionUID = -3420236962803678596L;

	// 配置管理类实例
	private static ConfManager cm = ConfManager.getInstance();

	public CorpInfoSearch() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher rd = null;
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String corpSearchKey = request.getParameter("corpSearchKey");// 从主页传过来的，以后回传也用这个

		if (StringHelper.isNullOrEmpty(corpSearchKey)) {
			corpSearchKey = new String(request.getParameter("qc").getBytes("ISO-8859-1"),
					"UTF-8");
		}
		System.out.print("搜索的关键字：" + corpSearchKey);
		int offset = 0;// 从主页传过来的，以后回传也用这个
		int pagersize = 10;// 从主页传过来的，以后回传也用这个

		if (request.getParameter("pager.offset") != null) {
			offset = Integer.parseInt(request.getParameter("pager.offset")
					.toString());
		}

		try {
			WonderSearchBasePO sbpo = new WonderSearchBasePO();
			sbpo.setParam("key", corpSearchKey);
			sbpo.setParam("type", "corpName");
			sbpo.setOffset(offset);
			sbpo.setLimit(pagersize);
			
			//WonderSearchBasePO中增加定制查询指定字段信息属性searchFileds
			String[] searchFields = {"name","size","type"};
			sbpo.setSearchFields(searchFields);
			
			Date start = new Date();
			DataGrid<List<CorpBO>> dataGrid = getDataKeyAnalysis(new CorpBO(), sbpo);
			Date end = new Date();

			long counttimes = end.getTime() - start.getTime();
			DecimalFormat df = new DecimalFormat("0.00000");
			double miao = Double.parseDouble((counttimes / 1300.0) + "");
			String searchtimes = df.format(miao);
			System.out.println("查询:" + searchtimes + "秒");
			request.setAttribute("searchTimes", searchtimes);
			request.setAttribute("corpSearchKey", corpSearchKey);
			request.setAttribute("dataGrid", dataGrid);
			request.setAttribute("totalRecord", dataGrid.getTotalElements());
			request.setAttribute("currBeginRecord", offset+1);
			request.setAttribute("currEndRecord", offset+pagersize);
			request.setAttribute("corpList", dataGrid.getData());
			rd = request.getRequestDispatcher("./corpSearchResults.jsp");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rd.forward(request, response);
		}

	}

	/**
	 * 搜索条件分词
	 * 
	 * @param object
	 * @param sbpo
	 * @return
	 */
	public static <T> DataGrid<List<T>> getDataKeyAnalysis(T object, WonderSearchBasePO sbpo)
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
                dataGrid = indexer.search(object, word, sbpo);

                if (dataGrid.getData() !=null && dataGrid.getData().size() > 0)
                {
                    sbpo.setOffset(0);
                    /** 第一个分词结果集大于5条的话只取第一个分词结果集前5条记录；如果小于5，取第一
                                                                分词结果集n条记录(n<5)+第二个分词结果集的前5-n条记录
                    */
                    for (int i = 0; sbpo.getLimit() - resultList.size() > 0 && i < dataGrid.getData().size(); i++)
                    {
                    	object = (T) dataGrid.getData().get(i);
                    	resultList.add(object);
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
	
	
	public static <T> PageableResultDataImpl<List<T>> getDataKeyAnalysis2(
			T object, WonderSearchBasePO sbpo) {
		List<T> resultList = new ArrayList<T>();
		// 防止重复
		// ArrayList<String> ids = new ArrayList<String>();
		PageableResultDataImpl<List<T>> dataGrid = new PageableResultDataImpl<List<T>>();
		// 建立搜索条件
		ArrayList<String> words = KeyAnalysisSupport.analysePostSearchKey(sbpo);
		// 总记录数
		int lngRowCount = 0;
		Indexer indexer = new Indexer(cm.getPropValue(IndexConstant.POST_PATH),
				null);

		try {
			// 搜索各分词结果
			for (String word : words) {
				LogHelper.getLogger().info("sbWhere=======  " + word);
				dataGrid = indexer
						.search(object, word, sbpo.getOffset(),
								sbpo.getOffset() + sbpo.getLimit(),
								sbpo.getOrder_str());

				// if (dataGrid.getTotalElements()<=0)
				// {
				// DataGrid<List<T>> dataGrid_2 = indexer.search(object, word,
				// 0, 1, null);
				// dataGrid.setTotalElements(dataGrid_2.getTotalElements());
				// }
				if (dataGrid.getResultData().size() > 0) {
					sbpo.setOffset(0);
					/**
					 * 第一个分词结果集大于5条的话只取第一个分词结果集前5条记录；如果小于5，取第一
					 * 分词结果集n条记录(n<5)+第二个分词结果集的前5-n条记录
					 */
					for (int i = 0; sbpo.getLimit() - resultList.size() > 0
							&& i < dataGrid.getResultData().size(); i++) {
						object = (T) dataGrid.getResultData().get(i);
						resultList.add(object);
					}
				} else {
					sbpo.setOffset(sbpo.getOffset() - dataGrid.getTotalCount());
					if (sbpo.getOffset() < 0)
						sbpo.setOffset(0);
				}
				// 总记录
				lngRowCount += dataGrid.getTotalCount();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dataGrid.setResultData(resultList);
		dataGrid.setTotalCount(lngRowCount);
		return dataGrid;
	}

}
