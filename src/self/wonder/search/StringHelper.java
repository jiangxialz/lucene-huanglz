package self.wonder.search;

import java.util.Iterator;

// Referenced classes of package com.ue.util:
//            ArrayHelper

public class StringHelper
{

    private StringHelper()
    {
    }

    public static boolean isNullOrEmpty(Object obj)
    {
        return obj == null || "".equals(obj.toString().trim());
    }

    public static boolean isNotNullAndEmpty(Object obj)
    {
        return !isNullOrEmpty(obj);
    }
    
    public static String replaceHtml(String input) 
    {
      input = input.replaceAll("&lt;br/&gt;", "<br/>");
      input = input.replaceAll("&lt;br/&gt;", "<BR/>");
      input = input.replaceAll("&lt;br&gt;", "<BR>");
      input = input.replaceAll("&lt;br&gt;", "<br>");
      return input; 
    }

//    public static boolean isNumeric(String s)
//    {
//        Pattern pattern;
//        if(isNotNullAndEmpty(s))
//            return (s = (pattern = Pattern.compile("^\\d+$", 2)).matcher(s)).find();
//        else
//            return false;
//    }

    public static String linkString(String s, String s1, String s2)
    {
        if(isNullOrEmpty(s) && isNullOrEmpty(s2))
            return "";
        if(isNullOrEmpty(s))
            return s2;
        if(isNullOrEmpty(s2))
            return s;
        else
            return (new StringBuilder(String.valueOf(s))).append(s1).append(s2).toString();
    }

    public static String unescape(String s)
    {
        StringBuffer stringbuffer;
        (stringbuffer = new StringBuffer()).ensureCapacity(s.length());
        int j;
        for(int i = 0; i < s.length();)
            if((j = s.indexOf("%", i)) == i)
            {
                if(s.charAt(j + 1) == 'u')
                {
                    i = (char)Integer.parseInt(s.substring(j + 2, j + 6), 16);
                    stringbuffer.append(i);
                    i = j + 6;
                } else
                {
                    i = (char)Integer.parseInt(s.substring(j + 1, j + 3), 16);
                    stringbuffer.append(i);
                    i = j + 3;
                }
            } else
            if(j == -1)
            {
                stringbuffer.append(s.substring(i));
                i = s.length();
            } else
            {
                stringbuffer.append(s.substring(i, j));
                i = j;
            }

        return stringbuffer.toString();
    }

    public static String padLeft(String s, int i, char c)
    {
        for(; i > s.length(); s = (new StringBuilder(String.valueOf(c))).append(s).toString());
        return s.substring(0, i);
    }

    public static String padRight(String s, int i, char c)
    {
        for(int j = 0; j < i - s.length(); j++)
            s = (new StringBuilder(String.valueOf(s))).append(c).toString();

        return s.substring(s.length() - i);
    }

    public static int lastIndexOfLetter(String s)
    {
        for(int i = 0; i < s.length(); i++)
        {
            char c;
            if(!Character.isLetter(c = s.charAt(i)))
                return i - 1;
        }

        return s.length() - 1;
    }

    public static String join(String s, Object aobj[])
    {
        int i;
        if((i = aobj.length) == 0)
            return "";
        StringBuffer stringbuffer = (new StringBuffer()).append(aobj[0]);
        for(int j = 1; j < i; j++)
            stringbuffer.append(s).append(aobj[j]);

        return stringbuffer.toString();
    }

    public static String join(String s, Iterator iterator)
    {
        StringBuffer stringbuffer = new StringBuffer();
        if(iterator.hasNext())
            stringbuffer.append(iterator.next());
        for(; iterator.hasNext(); stringbuffer.append(s).append(iterator.next()));
        return stringbuffer.toString();
    }

    public static String[] add(String as[], String s, String as1[])
    {
        String as2[] = new String[as.length];
        for(int i = 0; i < as.length; i++)
            as2[i] = (new StringBuilder(String.valueOf(as[i]))).append(s).append(as1[i]).toString();

        return as2;
    }

    public static String repeat(String s, int i)
    {
        StringBuffer stringbuffer = new StringBuffer(s.length() * i);
        for(int j = 0; j < i; j++)
            stringbuffer.append(s);

        return stringbuffer.toString();
    }

    public static String replace(String s, String s1, String s2)
    {
        return replace(s, s1, s2, false);
    }

    public static String[] replace(String as[], String s, String s1)
    {
        String as1[] = new String[as.length];
        for(int i = 0; i < as.length; i++)
            as1[i] = replace(as[i], s, s1);

        return as1;
    }

    public static String replace(String s, String s1, String s2, boolean flag)
    {
        int i;
        if((i = s != null ? s.indexOf(s1) : -1) < 0)
        {
            return s;
        } else
        {
            boolean flag1;
            String s3 = (flag1 = !flag || i + s1.length() == s.length() || !Character.isJavaIdentifierPart(s.charAt(i + s1.length()))) ? s2 : s1;
            return s.substring(0, i) + s3 + replace(s.substring(i + s1.length()), s1, s2, flag);
        }
    }

    public static String replaceOnce(String s, String s1, String s2)
    {
        int i;
        if((i = s != null ? s.indexOf(s1) : -1) < 0)
            return s;
        else
            return s.substring(0, i) + s2 + s.substring(i + s1.length());
    }

//    public static String[] split(String s, String s1)
//    {
//        return split(s, s1, false);
//    }

//    public static String[] split(String s, String s1, boolean flag)
//    {
//        s1 = new String[(s = new StringTokenizer(s1, s, flag)).countTokens()];
//        flag = false;
//        while(s.hasMoreTokens()) 
//            s1[flag++] = s.nextToken();
//        return s1;
//    }

    public static String unqualify(String s)
    {
        int i;
        if((i = s.lastIndexOf(".")) < 0)
            return s;
        else
            return s.substring(s.lastIndexOf(".") + 1);
    }

    public static String qualifier(String s)
    {
        int i;
        if((i = s.lastIndexOf(".")) < 0)
            return "";
        else
            return s.substring(0, i);
    }

    public static String[] suffix(String as[], String s)
    {
        if(s == null)
            return as;
        String as1[] = new String[as.length];
        for(int i = 0; i < as.length; i++)
        {
            String s2 = s;
            String s1 = as[i];
            as1[i] = s2 != null ? (new StringBuilder(String.valueOf(s1))).append(s2).toString() : s1;
        }

        return as1;
    }

    public static String root(String s)
    {
        int i;
        if((i = s.indexOf(".")) < 0)
            return s;
        else
            return s.substring(0, i);
    }

    public static String unroot(String s)
    {
        int i;
        if((i = s.indexOf(".")) < 0)
            return s;
        else
            return s.substring(i + 1, s.length());
    }

    public static boolean booleanValue(String s)
    {
        return (s = s.trim().toLowerCase()).equals("true") || s.equals("t");
    }

    public static String toString(Object aobj[])
    {
        int i;
        if((i = aobj.length) == 0)
            return "";
        StringBuffer stringbuffer = new StringBuffer(i * 12);
        for(int j = 0; j < i - 1; j++)
            stringbuffer.append(aobj[j]).append(", ");

        return stringbuffer.append(aobj[i - 1]).toString();
    }

//    public static String[] multiply(String s, Iterator iterator, Iterator iterator1)
//    {
//        String as1[];
//        for(s = (new String[] {s}); iterator.hasNext(); s = as1)
//        {
//            String as[] = (String[])iterator1.next();
//            String s1 = (String)iterator.next();
//            s = s;
//            as1 = new String[as.length * s.length];
//            int i = 0;
//            for(int j = 0; j < as.length; j++)
//            {
//                for(int k = 0; k < s.length; k++)
//                    as1[i++] = replaceOnce(s[k], s1, as[j]);
//
//            }
//
//        }
//
//        return s;
//    }

    public static int countUnquoted(String s, char c)
    {
        if('\'' == c)
            throw new IllegalArgumentException("Unquoted count of quotes is invalid");
        if(s == null)
            return 0;
        int i = 0;
        int j = s.length();
        boolean flag = false;
        for(int k = 0; k < j; k++)
        {
            char c1 = s.charAt(k);
            if(flag)
            {
                if('\'' == c1)
                    flag = false;
            } else
            if('\'' == c1)
                flag = true;
            else
            if(c1 == c)
                i++;
        }

        return i;
    }

//    public static int[] locateUnquoted(String s, char c)
//    {
//        if('\'' == c)
//            throw new IllegalArgumentException("Unquoted count of quotes is invalid");
//        if(s == null)
//            return new int[0];
//        ArrayList arraylist = new ArrayList(20);
//        int i = s.length();
//        boolean flag = false;
//        for(int j = 0; j < i; j++)
//        {
//            char c1 = s.charAt(j);
//            if(flag)
//            {
//                if('\'' == c1)
//                    flag = false;
//            } else
//            if('\'' == c1)
//                flag = true;
//            else
//            if(c1 == c)
//                arraylist.add(new Integer(j));
//        }
//
//        return ArrayHelper.toIntArray(arraylist);
//    }

    public static String qualify(String s, String s1)
    {
        if(s1 == null || s == null)
            throw new NullPointerException();
        else
            return (new StringBuffer(s.length() + s1.length() + 1)).append(s).append('.').append(s1).toString();
    }

    public static String[] qualify(String s, String as[])
    {
        if(s == null)
            return as;
        int i;
        String as1[] = new String[i = as.length];
        for(int j = 0; j < i; j++)
            as1[j] = qualify(s, as[j]);

        return as1;
    }

    public static int firstIndexOfChar(String s, String s1, int i)
    {
        int j = -1;
        for(int k = 0; k < s1.length(); k++)
        {
            int l;
            if((l = s.indexOf(s1.charAt(k), i)) >= 0)
                if(j == -1)
                    j = l;
                else
                    j = Math.min(j, l);
        }

        return j;
    }

    public static String truncate(String s, int i)
    {
        if(s.length() <= i)
            return s;
        else
            return s.substring(0, i);
    }

    public static String generateAlias(String s, int i)
    {
        return (new StringBuilder(String.valueOf(a(s)))).append(Integer.toString(i)).append('_').toString();
    }

    private static String a(String s)
    {
        if(Character.isDigit((s = truncate(unqualifyEntityName(s), 10).toLowerCase().replace('/', '_').replace('$', '_')).charAt(s.length() - 1)))
            return (new StringBuilder(String.valueOf(s))).append("x").toString();
        else
            return s;
    }

    public static String unqualifyEntityName(String s)
    {
        int i;
        if((i = (s = unqualify(s)).indexOf('/')) > 0)
            s = s.substring(0, i - 1);
        return s;
    }

    public static String generateAlias(String s)
    {
        return (new StringBuilder(String.valueOf(a(s)))).append('_').toString();
    }

    public static String moveAndToBeginning(String s)
    {
        if(s.trim().length() > 0 && (s = (new StringBuilder(String.valueOf(s))).append(" and ").toString()).startsWith(" and "))
            s = s.substring(4);
        return s;
    }

    public static String firstCharToUpperCase(String s)
    {
        if(s != null && s.length() > 0)
            return (new StringBuilder(String.valueOf(s.substring(0, 1).toUpperCase()))).append(s.substring(1)).toString();
        else
            return null;
    }

    public static String getString(char c, int i)
    {
        String s = "";
        for(int j = 0; j < i; j++)
            s = (new StringBuilder(String.valueOf(s))).append(String.valueOf(c)).toString();

        return s;
    }

    public static String sort(String s)
    {
        if(s != null)
        {
            int i = s.length();
            String s1 = "";
            String s2 = "";
            for(i--; i >= 0; i--)
            {
                String s3;
                if((s3 = s.substring(i, i + 1)).equals("="))
                    s2 = (new StringBuilder(String.valueOf(s2))).append(s3).toString();
                else
                    s1 = (new StringBuilder(String.valueOf(s1))).append(s3).toString();
            }

            return (new StringBuilder(String.valueOf(s1))).append(s2).toString();
        } else
        {
            return null;
        }
    }

    public static final String WHITESPACE = " \n\r\f\t";
}
