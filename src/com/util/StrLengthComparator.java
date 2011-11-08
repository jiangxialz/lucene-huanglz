package com.util;

import java.util.Comparator;

@SuppressWarnings("rawtypes")
public class StrLengthComparator implements Comparator
{

    /**
     * 比较str长度排序 从长到短
     * @return
     */
    @Override
    public int compare(Object o1, Object o2)
    {
        if (o1.toString().length() < o2.toString().length())
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }
}
