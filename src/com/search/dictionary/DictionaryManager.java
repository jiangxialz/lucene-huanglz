package com.search.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.ue.data.search.analysis.DictionaryAnalyzer;
import com.util.WebConfig;

public class DictionaryManager
{
    private static DictionarySet m_dicNumbers = new DictionarySet();

    private static DictionarySet m_dicAreas = new DictionarySet();

    private static DictionarySet m_dicPositions = new DictionarySet();
    
    private static DictionarySet m_dicCorpKeyWord = new DictionarySet();

    private static DictionarySet m_dicIndustrys = new DictionarySet();

    private static DictionarySet m_all = new DictionarySet();
    static
    {
        String strBasePath = WebConfig.ApplicationPath + "dictionarys/";
        // loadSet(m_dicNumbers,strBasePath + "numbers.dic");
        // loadMap(m_dicAreas,strBasePath + "areas.dic");
        loadMap(m_dicPositions, strBasePath + "positions.dic");
        loadMap(m_dicCorpKeyWord, strBasePath + "ext_corpkeyword.dic");
        // loadMap(m_dicIndustrys,strBasePath + "industrys.dic");
        // DataTable dtAreas =
        // DBManager.getDataTable("select name from t_areainfo");
        // for(DataRow row : dtAreas.getRows()){
        // addWord(m_dicAreas,row.getString("name"));
        // }
        // DataTable dtPositions =
        // DBManager.getDataTable("select name from t_positiontype");
        // for(DataRow row : dtPositions.getRows()){
        // addWord(m_dicPositions,row.getString("name"));
        // }
        // DataTable dtIndustrys =
        // DBManager.getDataTable("select domainName from t_domain");
        // for(DataRow row : dtIndustrys.getRows()){
        // addWord(m_dicIndustrys,row.getString("domainName"));
        // }
        //
//        m_all.addAll(m_dicAreas);
        m_all.addAll(m_dicPositions);
        m_all.addAll(m_dicCorpKeyWord);
//        m_all.addAll(m_dicIndustrys);
    }
    
    public static DictionarySet getCorpKeyWord()
    {
    	return m_dicCorpKeyWord;
    }

    public static DictionarySet getNumbers()
    {
        return m_dicNumbers;
    };

    public static DictionarySet getAreas()
    {
        return m_dicAreas;
    };

    public static DictionarySet getPositions()
    {
        return m_dicPositions;
    };

    public static DictionarySet getIndustrys()
    {
        return m_dicIndustrys;
    };

    public static DictionarySet getAll()
    {
        return m_all;
    };

    private static void loadSet(DictionarySet words, String strFile)
    {
        String dataline;
        try
        {
            File file = new File(strFile);
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            while ((dataline = in.readLine()) != null)
            {
                if ((dataline.indexOf("#") > -1) || (dataline.length() == 0))
                {
                    continue;
                }
                words.add(dataline.intern());
            }
            in.close();
        }
        catch (Exception e)
        {
            System.err.println("IOException: " + e);
        }
    }

    private static void loadMap(DictionarySet words, String strFile)
    {
        String newword;
        try
        {
            File file = new File(strFile);
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            while ((newword = in.readLine()) != null)
            {
                if ((newword.indexOf("#") == -1))
                {
                    addWord(words, newword);
                }
            }
            in.close();
        }
        catch (Exception e)
        {
            System.err.println("IOException: " + e);
        }
    }

    public static void addWord(DictionarySet words, String strWord)
    {
        if (!words.contains(strWord))
        {
            words.add(strWord.intern());
        }
    }

    public static void main(String[] args)
    {
        System.out.println(m_dicPositions.size());
        DictionaryAnalyzer mainsegmenter = new DictionaryAnalyzer(m_dicPositions);
        ArrayList<String> words = mainsegmenter.analyse("销售经理");
//        ArrayList<String> words = mainsegmenter.analyse("网站架构工程师");
        for (String s : words)
        {
            System.out.println(s);
        }
        // System.out.println(strWords);
    }
}
