package com.common.confManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 基本配置类
 * <一句话功能简述> <功能详细描述>
 *
 * @author honny.huang
 * @version [版本号, 2011-4-2]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ConfManager
{
    private static final Logger logger = Logger.getLogger(ConfManager.class);

    static private ConfManager instance;

    Properties sysProps = null;

    InputStream is = null;

    FileOutputStream os = null;

    private String resourceURI = "/com/conf/conf.properties";//

    private static String webinfPath;

    public void setResourceURI(String resourceURI)
    {
        this.resourceURI = resourceURI;
    }

    public ConfManager()
    {
        this.init();
    }

    static synchronized public ConfManager getInstance()
    {
        if (instance == null)
        {
            instance = new ConfManager();
        }
        return instance;
    }

    public void init()
    {
        is = getClass().getResourceAsStream(resourceURI);
        sysProps = new Properties();
        try
        {
            sysProps.load(is);
        }
        catch (Exception e)
        {
            logger.error("不能读取属性文件:conf.properties");
            System.err.println("不能读取属性文件:conf.properties");
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (Exception ee)
                {
                    logger.error("Exception:" + ee);
                    System.out.println("Exception : " + ee);
                }
            }
        }
    }

    public String getPropValue(String propertyName)
    {
        return getPropValue(propertyName, "");
    }

    /**
     * @param propertyName
     * @param defaultValue ֵ
     * @return
     */
    public String getPropValue(String propertyName, String defaultValue)
    {

        if (propertyName.equals("LocalPath"))
        {
            return getWebAppsPath();
        }

        if (sysProps != null)
        {
            return sysProps.getProperty(propertyName);

        }

        return defaultValue;
    }

    /**
     * 
     * @return Properties
     */
    public Properties getProperties()
    {
        return sysProps;
    }

    public void setProperties(Properties props)
    {
        this.sysProps = props;
    }

    /**
     * @return Properties
     */
    public void reload()
    {
        init();
    }

    public void setPropValue(String propertyName, String propertyValue)
    {
        if (sysProps != null)
        {
            sysProps.setProperty(propertyName, propertyValue);
        }
    }

    public void store(String comments)
    {
        if (sysProps != null)
        {
            try
            {
                String path = getClass().getResource(resourceURI).getPath();
                os = new FileOutputStream(path);
                sysProps.store(os, comments);
            }
            catch (FileNotFoundException e)
            {
                logger.error("FileNotFoundException:" + e);
                System.out.println("FileNotFoundException : " + e);
            }
            catch (IOException e)
            {
                logger.error("IOException:" + e);
                System.out.println("IOException : " + e);
            }
            finally
            {
                if (os != null)
                {
                    try
                    {
                        os.close();
                    }
                    catch (Exception ee)
                    {
                        logger.error("Exception:" + ee);
                        System.out.println("Exception : " + ee);
                    }
                }
            }
        }
    }

    public static String getWebInfPath()
    {

        if (webinfPath == null)
        {

            java.io.File file = new java.io.File(com.common.confManager.ConfManager.class.getClassLoader().getResource("../web.xml").getFile());


            if (System.getProperty("os.name").toUpperCase().indexOf("WINDOW") > -1)
            {
                webinfPath = file.getParent().replace("\\", "/").replace("/classes/..", "") + "/";
            }
            else
            {
                webinfPath = file.getParent().replace("/classes/..", "") + "/";
            }
            logger.info("WEB-INF: " + webinfPath);

        }
        return webinfPath;
    }

    /**
     */
    public static String getWebAppsPath()
    {
        String webInfPath = getWebInfPath();
        int index = webInfPath.substring(0, webInfPath.length() - 9).replace("\\", "/").lastIndexOf("/");
        return webInfPath.substring(0, index) + "/";
    }

    public static void main(String[] args)
    {
         String webInfPath = getWebInfPath();
         System.out.println(webInfPath);
       // String webInfPath = "/home/wtest/tomcat/apache-tomcat-5.5.17/webapps/bbs/WEB-INF/";
       // int index = webInfPath.substring(0, webInfPath.length() - 9).replace("\\", "/").lastIndexOf("/");
       // System.out.print(webInfPath.substring(0, index));
    }
}
