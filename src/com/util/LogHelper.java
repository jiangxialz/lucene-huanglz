/*
 * 文 件 名:  LogHelper.java
 * 版    权:  深圳埃思欧纳信息咨询有限公司版权所有. YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  honny.huang
 * 修改时间:  2011-10-13
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.util;

import org.apache.log4j.Logger;

/**
 * <日志助手类>
 * <功能详细描述>
 * 
 * @author  honny.huang
 * @version  [版本号, 2011-10-13]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class LogHelper
{
    private static Logger logger = null;
    
    /**
     * <初始化日志助手对象>
     * <功能详细描述> [参数说明]
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private static void init()
    {
        logger = Logger.getLogger(LogHelper.class);
    }
    
    /**
     * <获取日志助手对象>
     * <功能详细描述>
     * @return [参数说明]
     * 
     * @return Logger [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static Logger getLogger()
    {
        if (null == logger)
        {
            init();
        }
        return logger;
    }

}
