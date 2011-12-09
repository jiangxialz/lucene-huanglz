package com.ue.data.search.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;
import org.wltea.analyzer.dic.Dictionary;

import com.search.dictionary.DictionarySet;
import com.util.LogHelper;

/**
 * 
 * <分词器，加载扩展词典>
 * <功能详细描述>
 * 
 * @author  huanglz
 * @version  [版本号, 2011-12-9]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class DictionaryAnalyzer
{
    private DictionarySet a;

    public DictionaryAnalyzer(DictionarySet paramDictionarySet)
    {
        this.a = paramDictionarySet;
    }

    /**
     * <查询条件分词 IK分词器 使用自定义职位系列分词词库 词库在IKjar包的main.dic>
     * 
     * 通过loadExtendStopWords方法后扩充的词没有存储在文件内。 
	 * 主词典是指内存中的词典对象，即，扩展词和原本词库内的词都会在内存中合并到相同的词库中。 
	 * IK的字典是单向加载模式，不会反向的存储到磁盘文件中，可以通过配置从文件中读取，也可以通过API动态添加。 
     */
    @SuppressWarnings("unchecked")
	public ArrayList<String> analyse(String paramString)
    {
    	// IK Analyzer语义单元（词元）
        Lexeme lex;
        StringReader sr = new StringReader(paramString);
        ArrayList<String> localArrayList = new ArrayList<String>();
        // IK主分词器 注：IKSegmentation是一个lucene无关的通用分词器,默认最细粒度切分
        IKSegmentation iks = new IKSegmentation(sr);
        /**
         * 1、public IKSegmentation(java.io.Reader input) ，默认最细粒度切分
         * 2、public IKSegmentation(java.io.Reader input,boolean isMaxWordLength) 
         *    其中isMaxWordLength - 当为true时，分词器进行最大词长切分
         * */
        // 将扩展词加入扩展词典
        Dictionary.loadExtendWords(this.a);
        try
        {
        	// 获取下一个语义单元，没有更多的词元，则返回null
            for (lex = iks.next(); lex != null; lex = iks.next())
            {
            	// 获取词元类型
                if ((lex.getLexemeType() != 0) && (lex.getLexemeType() != 2))
                    continue;
                // 获取词元的文本内容
                localArrayList.add(lex.getLexemeText());
            }
        }
        catch (IOException e)
        {
            LogHelper.getLogger().error(e.getMessage());
        }

        return ((ArrayList<String>) localArrayList);
    }
}
