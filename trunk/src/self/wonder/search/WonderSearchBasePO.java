package self.wonder.search;

import java.io.Serializable;
import java.util.HashMap;

public class WonderSearchBasePO implements Serializable
{
    private static final long serialVersionUID = 3372877034586052209L;

    /** 查询参数 */
    private HashMap<String, Object> paramMap = new HashMap<String, Object>();

    /** 解析后的查询条件 */
    private String paramStr;
    
    /** 需要查询的字段 */
    String[] searchFields;

	/** 起始记录数 */
    int offset = 0;

    /** 截止记录数 */
    int limit = 10;

	/** 是否应用权重 */
    boolean isBoost;

    /** 排序规则 */
    private String order_str;

    /** 设置参数 */
    public void setParam(String key, Object obj)
    {
        this.paramMap.put(key, obj);
    }

    /** 获取参数 */
    public Object getParam(String key)
    {
        return this.paramMap.get(key);
    }

    public int getOffset()
    {
        return offset;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    public int getLimit()
    {
        return limit;
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
    }

    public String getOrder_str()
    {
        return order_str;
    }

    public void setOrder_str(String order_str)
    {
        this.order_str = order_str;
    }

    public HashMap<String, Object> getParamMap()
    {
        return paramMap;
    }

    public void setParamMap(HashMap<String, Object> paramMap)
    {
        this.paramMap = paramMap;
    }

    public boolean isBoost()
    {
        return isBoost;
    }

    public void setBoost(boolean isBoost)
    {
        this.isBoost = isBoost;
    }

    public String getParamStr()
    {
        return paramStr;
    }

    public void setParamStr(String paramStr)
    {
        this.paramStr = paramStr;
    }
    
    public String[] getSearchFields() {
		return searchFields;
	}

	public void setSearchFields(String[] searchFields) {
		this.searchFields = searchFields;
	}

}
