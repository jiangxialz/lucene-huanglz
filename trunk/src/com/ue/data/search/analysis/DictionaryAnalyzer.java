package com.ue.data.search.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;
import org.wltea.analyzer.dic.Dictionary;

import com.search.dictionary.DictionarySet;
import com.util.LogHelper;

/*
 * 文 件 名:  DictionaryAnalyzer.java
 * 版    权:  深圳埃思欧纳信息咨询有限公司版权所有. YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  jeray.wu
 * 修改时间:  2011-3-23
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
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
     */
    @SuppressWarnings("unchecked")
	public ArrayList<String> analyse(String paramString)
    {
        Lexeme lex;
        StringReader sr = new StringReader(paramString);
        ArrayList<String> localArrayList = new ArrayList<String>();
        IKSegmentation iks = new IKSegmentation(sr);
        Dictionary.loadExtendWords(this.a);
        try
        {
            for (lex = iks.next(); lex != null; lex = iks.next())
            {
                if ((lex.getLexemeType() != 0) && (lex.getLexemeType() != 2))
                    continue;
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
