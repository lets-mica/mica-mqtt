package net.dreamlu.iot.mqtt.mica.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharSetUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Clob;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Title: 字符串工具类
 * Description: 一些方便的字符串工具方法.
 *
  *  IsEmpty/IsBlank
  *      - checks if a String contains text
  *  Trim/Strip
  *      - removes leading and trailing whitespace
  *  Equals
  *      - compares two strings null-safe
  *  IndexOf/LastIndexOf/Contains
  *      - null-safe index-of checks
  *  IndexOfAny/LastIndexOfAny/IndexOfAnyBut/LastIndexOfAnyBut
  *      - index-of any of a set of Strings
  *  ContainsOnly/ContainsNone
  *      - does String contains only/none of these characters
  *  Substring/Left/Right/Mid
  *      - null-safe substring extractions
  *  SubstringBefore/SubstringAfter/SubstringBetween
  *      - substring extraction relative to other strings
  *  Split/Join
  *      - splits a String into an array of substrings and vice versa
  *  Remove/Delete
  *      - removes part of a String
  *  Replace/Overlay
  *      - Searches a String and replaces one String with another
  *  Chomp/Chop
  *      - removes the last part of a String
  *  LeftPad/RightPad/Center/Repeat
  *      - pads a String
  *  UpperCase/LowerCase/SwapCase/Capitalize/Uncapitalize
  *      - changes the case of a String
  *  CountMatches
  *      - counts the number of occurrences of one String in another
  *  IsAlpha/IsNumeric/IsWhitespace/IsAsciiPrintable
  *      - checks the characters in a String
  *  DefaultString
  *      - protects against a null input String
  *  Reverse/ReverseDelimited
  *      - reverses a String
  *  Abbreviate
  *      - abbreviates a string using ellipsis
  *  Difference
  *      - compares two Strings and reports on their differences
  *  LevensteinDistance
  *      - the number of changes needed to change one String into another
  *
  *
  * The <code>StringUtils</code> class defines certain words related to
  * String handling.
  *
  *
  *  null - <code>null</code>
  *  empty - a zero-length string (<code>""</code>)
  *  space - the space character (<code>' '</code>, char 32)
  *  whitespace - the characters defined by {@link Character#isWhitespace(char)}
  *  trim - the characters &lt;= 32 as in {@link String#trim()}
 *
 *  */

public class StringUtils {
    /**
     * The empty String <code>""</code>.
     * @since 2.0
     */
    public static final String EMPTY = "";

    /**
     * Represents a failed index search.
     * @since 2.?.?
     */
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * The maximum size to which the padding constant(s) can expand.
     */
    private static final int PAD_LIMIT = 8192;

    /**
     * An array of <code>String</code>s used for padding.
     *
     * Used for efficient space padding. The length of each String expands as needed.
     */
    private static final String[] PADDING = new String[Character.MAX_VALUE];

    static {
        // space padding is most common, start with 64 chars
        PADDING[32] = "                                                                ";
    }

    public StringUtils() {

    }

    //取得两字符串中的最大子串
    //System.out.println(getMaxSubString("可选包全车服务77","可选包全车服务88"));
    //System.out.println(getMaxSubString("i ^ am a stadt","i am a student"));
     public static String getMaxSubString(String s1, String s2) {
       if (s1.length() > s2.length()) {
         String temp = s1;
         s1 = s2;
         s2 = temp;
       }
       int n = s1.length();
       int index = 0;
       ok:for (; n > 0; n--) {
         for (int i = 0; i < s1.length() - n + 1; i++) {
           String s = s1.substring(i, i + n);
           if (s2.indexOf(s) != -1) {
             index = i;
             break ok;
           }
         }
       }
       return s1.substring(index, index + n);
     }

    /**
     * 字符串按字节截取
     * @param str 原字符
     * @param len 截取长度
     * @param elide 省略符
     * @return String
     * @author kinglong
     * @since 2006.07.20
     */
    public static String interceptStringByByte(String str, int len, String elide) {
      if (str == null) {
        return "";
      }
      byte[] strByte = str.getBytes();
      int strLen = strByte.length;
      int elideLen = (elide.trim().length() == 0) ? 0 : elide.getBytes().length;
      if (len >= strLen || len < 1) {
        return str;
      }
      if (len - elideLen > 0) {
        len = len - elideLen;
      }
      int count = 0;
      for (int i = 0; i < len; i++) {
        int value = (int) strByte[i];
        if (value < 0) {
          count++;
        }
      }
      if (count % 2 != 0) {
        len = (len == 1) ? len + 1 : len - 1;
      }
      return new String(strByte, 0, len) + elide.trim();
    }

    //日期自动补零程序
    public static String appendZero(String n) {
        return (("00" + n).substring(("00" + n).length() - 2));
    }
    //任意字符串按指定长度工补0
    public static String leftAppendZero(String instr,int length) {
        for(int i=instr.getBytes().length; i<length; i++) {
              instr = "0"+instr;
        }
        return instr;
    }
    //任意字符串按指定长度右补空格(以字节算)
    public static String rightAppendBlank(String instr,int length) {
        if(instr.getBytes().length>length) {
            instr = interceptStringByByte(instr,length,"");
        }
        for(int i=instr.getBytes().length; i<length; i++) {
              instr = instr+" ";
        }
        return instr;
    }

    /*
      功能：四舍五入运算
      输入：double   value   原数据
                  int   n   保留小数位数
      返回：四舍五入后的结果double
     */
    public static double roundHalf(double value, int n) {
      BigDecimal bDec = new BigDecimal(value);
      if (n < 0) {
        n = 0;
      }
      bDec = bDec.setScale(n, BigDecimal.ROUND_HALF_EVEN); //四舍五入
      value = bDec.doubleValue();
      return value;
    }

    /**
     *   千分位格式化数据
     *   @param   str   String
     *   @return   String
     */
    public static String formatDec(String str) {
      int iPoint = str.indexOf(".");
      int iLen = str.length();
      String temp = "";
      if (iLen < 4) {
        return str;
      }
      if (iPoint < 0) {
        iPoint = 0;
      }else {
        iLen = iPoint;
      }

      for (int i = 3; i < iLen; i = i + 3) {
        temp = str.substring(iLen - i);
        str = str.substring(0, iLen - i) + ",";
        str += temp;
        i++;
        iLen++;
      }
      return str;
    }

    /**
     *   取消千分位格式化,返回实际值,如123,12.00   应返回12312.00
     *   @param   str   String
     *   @return   String
     */
    public static String unFormatDec(String str) {
        str = str.replaceAll(",", "");
        return str;
    }
    //替换掉字符串中的小数点 “.”是一个正则的特殊字符所以在前面要加“\\”
    public static String replaceRadixPoint(String str) {
        str = str.replaceAll("\\.", "");
        return str;
    }

    /**
     * 判定指定字节数组的指定位置，是否是完整的字 如"你好".getByte(gb2312),1将返回false
     * 如"你好".getByte(gb2312),2将返回true
     *
     * @param sourceByte
     *            编码的gb2312字节数组
     * @param index
     *            需要判定的数组位置
     * @return 是完整字，返回true
     */
    public static boolean splitBygb2312(byte[] sourceByte, int index) {
        int i = 0;
        // 判定，直到位标志等于或者大与index时结束
        while (i < index && i < sourceByte.length) {
            // 如果此位字节高位是1。则说明是一个汉字，跳两位判定
            if (sourceByte[i] < 0) {
                i = i + 2;
            } else {
                // 否则说明是0-127之间的字母或者符号，跳一位
                i++;
            }
        }
        // 如果位标志等于index，说明此位置开始是完整字，否则说明不是完整字
        return (i == index ? true : false);
    }

    /**
     * 截取源字节数组中，指定头index与尾index的子字节数组
     *
     * @param sourceByte 需要截取的数组
     * @param beginIndex
     *            起始位置
     * @param endIndex
     *            结束位置
     * @return 子数组
     */
    public static byte[] getByteSubStr(byte[] sourceByte, int beginIndex,
                                       int endIndex) {
        // 判定，如果头index不是完整字，则将截取位置前移一位
        if (!splitBygb2312(sourceByte, beginIndex)) {
            beginIndex--;
        }
        // 判定，如果尾index不是完整字，则将截取位置前移一位
        if (!splitBygb2312(sourceByte, endIndex)) {
            endIndex--;
        }

        // 需要截取的长度
        int length = endIndex - beginIndex;
        byte[] resultByte = null;
        if (sourceByte.length > endIndex) {
            // 如果末尾index没有越界，则直接拷贝数组
            resultByte = new byte[length];
            for (int i = 0; i < length; i++) {
                resultByte[i] = sourceByte[beginIndex + i];
            }
        } else {
            // 如果越界，则重置数组长度，拷贝数组
            length = sourceByte.length - beginIndex;
            resultByte = new byte[length];
            for (int i = 0; i < length; i++) {
                resultByte[i] = sourceByte[beginIndex + i];
            }
        }
        return resultByte;
    }

    /**
     * 截取源字符串中，指定头index与尾index的子串，类似于substring,但这里以字节截取
     * @param sourcestr 需要截取的中英文混合字符串
     * @param beginIndex
     *            起始位置
     * @param endIndex
     *            结束位置
     * @return 子串
     */
    public static String getSubStrByByte(String sourcestr, int beginIndex,int endIndex) {
      int length = endIndex - beginIndex;
      String tempstr = new String(getByteSubStr(sourcestr.getBytes(), beginIndex, endIndex));
      for(int i=tempstr.getBytes().length; i<length; i++) {
            tempstr = tempstr+" ";
      }
      return tempstr;
    }

    /**
     * 截取源字符串中，指定头index与尾index的子串，并按指定长度返回,类似于substring,但这里以字节截取
     * @param sourcestr 需要截取的中英文混合字符串
     * @param beginIndex
     *            起始位置
     * @param endIndex
     *            结束位置
     * @param length
     *            返回的子串长度(以字节计算)
     * @return 子串
     */
    public static String getSubStrByByteLength(String sourcestr, int beginIndex,
                                               int endIndex, int length) {
        String tempstr = new String(getByteSubStr(sourcestr.getBytes(),
                                                  beginIndex, endIndex));
        for (int i = tempstr.getBytes().length; i < length; i++) {
            tempstr = tempstr + " ";
        }
        return tempstr;
    }

    // 声明一些方便内部方法

    // ------------------------------------ 字符串处理方法 ----------------------------------------------
    // 将字符串 source 中的 oldStr 替换为 newStr, 并以大小写敏感方式进行查找
    public static String replaceBySubtle(String source, String oldStr, String newStr) {
        return replace(source, oldStr, newStr, true);
    }

    //private final String source = "name|telephone,fax;email.address";
    //private final String delim = ";,.|";   // 包含分号、逗号、句点、竖址分隔符

    //去掉给定字符串中指定的字符
    public static String replace(String source, String delim) {
        StringTokenizer tokenizer;
        tokenizer = new StringTokenizer(source, delim);
        String restr = "";
        while (tokenizer.hasMoreTokens()) {
            restr += tokenizer.nextToken();
        }
        return restr;
    }

    // 将字符串 source 中的 oldStr 替换为 newStr, matchCase 为是否设置大小写敏感查找
    public static String replace(String source, String oldStr, String newStr,boolean matchCase) {
        if (source == null) {
            return null;
        }
        // 首先检查旧字符串是否存在, 不存在就不进行替换
        if (source.toLowerCase().indexOf(oldStr.toLowerCase()) == -1) {
            return source;
        }

        int findStartPos = 0;
        int a = 0;
        while (a > -1) {
            int b = 0;
            String str1, str2, str3, str4, strA, strB;
            str1 = source;
            str2 = str1.toLowerCase();
            str3 = oldStr;
            str4 = str3.toLowerCase();
            if (matchCase) {
                strA = str1;
                strB = str3;
            } else {
                strA = str2;
                strB = str4;
            }
            a = strA.indexOf(strB, findStartPos);
            if (a > -1) {
                b = oldStr.length();
                findStartPos = a + b;
                StringBuffer bbuf = new StringBuffer(source);
                source = bbuf.replace(a, a + b, newStr) + "";
                // 新的查找开始点位于替换后的字符串的结尾
                findStartPos = findStartPos + newStr.length() - b;
            }
        }
        return source;
    }

    //保留小数点后两位小数,返回Double型数值
    public double getDouble2Number(double d) {
        DecimalFormat df1 = new DecimalFormat("###0.00");
        //double d = Double.parseDouble(s);
        double dd = Double.parseDouble(df1.format(d));
        return dd;
    }

    //地图上两点之间的距离
    public static double getDistance(double startlon, double startlat,
                                     double endlon, double endlat) {
        double arc2degree = 57.295779513082321;
        double lon2length = 111320.590351;
        double lat2length = 110947.089891;

        DecimalFormat df1 = new DecimalFormat("###0.000");

        double lonrate = Math.cos((startlat + endlat) / (2 + arc2degree));
        double deltalon = (startlon - endlon) * lon2length * lonrate;
        double deltalat = (startlat - endlat) * lat2length;

        return Double.parseDouble(df1.format(Math.sqrt(Math.pow(deltalon, 2) +
                Math.pow(deltalat, 2)) / 1000));
    }

    //地图上两点之间的角度
    public static double getDegree(double startlon, double startlat,
                                   double endlon, double endlat) {
        double deltalon = startlon - endlon;
        double deltalat = startlat - endlat;
        double degree = 0;
        if (deltalat == 0) {
            if (deltalon >= 0) {
                degree = 90;
            } else {
                degree = 270;
            }
        } else {
            degree = Math.atan(Math.abs(deltalon / deltalat)) * 180 /
                     3.14159255358;
            if ((deltalat < 0) && (deltalon >= 0)) {
                degree = 180 - degree;
            } else {
                if ((deltalat < 0) && (deltalon < 0)) {
                    degree = 180 + degree;
                } else {
                    if ((deltalat > 0) && (deltalon <= 0)) {
                        degree = 360 - degree;
                    }
                }
            }
        }
        return degree;
    }

    public static String ChangCourse(String course) {
        double icourse = Double.parseDouble(course);
        //int icourse = new Integer(course).intValue();
        String scourse = "";
        if ((icourse == 0) || (icourse == 360)) {
            scourse = "向北";
        } else if ((icourse > 0) && (icourse <= 30)) {
            scourse = "北偏东";
        } else if ((icourse > 30) && (icourse <= 60)) {
            scourse = "东北";
        } else if ((icourse > 60) && (icourse < 90)) {
            scourse = "东偏北";
        } else if (icourse == 90) {
            scourse = "向东";
        } else if ((icourse > 90) && (icourse <= 120)) {
            scourse = "东偏南";
        } else if ((icourse > 120) && (icourse <= 150)) {
            scourse = "东南";
        } else if ((icourse > 150) && (icourse < 180)) {
            scourse = "南偏东";
        } else if (icourse == 180) {
            scourse = "向南";
        } else if ((icourse > 180) && (icourse <= 210)) {
            scourse = "南偏西";
        } else if ((icourse > 210) && (icourse <= 240)) {
            scourse = "西南";
        } else if ((icourse > 240) && (icourse < 270)) {
            scourse = "西偏南";
        } else if (icourse == 270) {
            scourse = "向西";
        } else if ((icourse > 270) && (icourse <= 300)) {
            scourse = "西偏北";
        } else if ((icourse > 300) && (icourse <= 330)) {
            scourse = "西北";
        } else if ((icourse > 330) && (icourse < 360)) {
            scourse = "北偏西";
        }
        return scourse;
    }
    //检查字符串中是否含有中文
    public boolean isChinese(String s) {
        String pattern = "[\u4e00-\u9fa5]+";
        Pattern p = Pattern.compile(pattern);
        Matcher result = p.matcher(s);
        return result.find();
    }

    //判断字符串是否为数字的四种方法
    //1>用JAVA自带的函数
    public static boolean isNumStr(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    //2>用正则表达式
    public static boolean isNum(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    //3>用ascii码
    public static boolean isNumericString(String str) {
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) {
                return false;
            }
        }
        return true;
    }

    //4>用异常
    public static boolean isNumericStr(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException ne) {
            return false;
        }
        return true;
    }

    public static String ChangCourseCH(double icourse) {
        //double icourse = Double.parseDouble(course);
        //int icourse = new Integer(course).intValue();
        String scourse = "";
        if ((icourse == 0) || (icourse == 360)) {
            scourse = "向北";
        } else if ((icourse > 0) && (icourse <= 30)) {
            scourse = "北偏东";
        } else if ((icourse > 30) && (icourse <= 60)) {
            scourse = "东北";
        } else if ((icourse > 60) && (icourse < 90)) {
            scourse = "东偏北";
        } else if (icourse == 90) {
            scourse = "向东";
        } else if ((icourse > 90) && (icourse <= 120)) {
            scourse = "东偏南";
        } else if ((icourse > 120) && (icourse <= 150)) {
            scourse = "东南";
        } else if ((icourse > 150) && (icourse < 180)) {
            scourse = "南偏东";
        } else if (icourse == 180) {
            scourse = "向南";
        } else if ((icourse > 180) && (icourse <= 210)) {
            scourse = "南偏西";
        } else if ((icourse > 210) && (icourse <= 240)) {
            scourse = "西南";
        } else if ((icourse > 240) && (icourse < 270)) {
            scourse = "西偏南";
        } else if (icourse == 270) {
            scourse = "向西";
        } else if ((icourse > 270) && (icourse <= 300)) {
            scourse = "西偏北";
        } else if ((icourse > 300) && (icourse <= 330)) {
            scourse = "西北";
        } else if ((icourse > 330) && (icourse < 360)) {
            scourse = "北偏西";
        }
        return scourse;
    }

    // 滤除帖子中的危险 HTML 代码, 主要是脚本代码, 滚动字幕代码以及脚本事件处理代码
    public String replaceHtmlCode(String content) {
        if (isEmpty(content)) {
            return "";
        }
        // 需要滤除的脚本事件关键字
        String[] eventKeywords = {
                                 "onmouseover",
                                 "onmouseout",
                                 "onmousedown",
                                 "onmouseup",
                                 "onmousemove",
                                 "onclick",
                                 "ondblclick",
                                 "onkeypress",
                                 "onkeydown",
                                 "onkeyup",
                                 "ondragstart",
                                 "onerrorupdate",
                                 "onhelp",
                                 "onreadystatechange",
                                 "onrowenter",
                                 "onrowexit",
                                 "onselectstart",
                                 "onload",
                                 "onunload",
                                 "onbeforeunload",
                                 "onblur",
                                 "onerror",
                                 "onfocus",
                                 "onresize",
                                 "onscroll",
                                 "oncontextmenu"
        };

        content = replace(content, "<script", "&ltscript", false);
        content = replace(content, "</script", "&lt/script", false);
        content = replace(content, "<marquee", "&ltmarquee", false);
        content = replace(content, "</marquee", "&lt/marquee", false);
        content = replace(content, "\r\n", "<BR>");
        // 滤除脚本事件代码
        for (int i = 0; i < eventKeywords.length; i++) {
            content = replace(content, eventKeywords[i], "_" + eventKeywords[i], false); // 添加一个"_", 使事件代码无效
        }

        return content;
    }

    //******************************** 滤除 HTML 代码 为文本代码 *****************************
     public static String replaceHtmlToText(String input) {
         if (isEmpty(input)) {
             return "";
         }
         return setBr(setTag(input));
     }

    // 滤除 HTML 标记
    public static String setTag(String s) {
        int j = s.length();

        StringBuffer stringbuffer = new StringBuffer(j + 500);

        for (int i = 0; i < j; i++) {
            if (s.charAt(i) == '<') {
                stringbuffer.append("&lt");
            } else if (s.charAt(i) == '>') {
                stringbuffer.append("&gt");
            } else if (s.charAt(i) == '&') {
                stringbuffer.append("&amp");
            } else {
                stringbuffer.append(s.charAt(i));
            }
        }

        return stringbuffer.toString();
    }

    // 滤除 BR 代码

    public static String setBr(String s) {
        int j = s.length();

        StringBuffer stringbuffer = new StringBuffer(j + 500);

        for (int i = 0; i < j; i++) {
            if (s.charAt(i) == '\n') {
                stringbuffer.append("");
            } else if (s.charAt(i) == '\r') {
                stringbuffer.append("");
            } else {
                stringbuffer.append(s.charAt(i));
            }
        }

        return stringbuffer.toString();
    }

    // 滤除空格
    public static String setNbsp(String s) {
        int j = s.length();

        StringBuffer stringbuffer = new StringBuffer(j + 500);

        for (int i = 0; i < j; i++) {
            if (s.charAt(i) == ' ') {
                stringbuffer.append("&nbsp;");
            } else {
                stringbuffer.append(s.charAt(i));
            }
        }

        return stringbuffer.toString();
    }

    // 判断字符串是否全是数字字符
    public static boolean isAllNumeric(String input) {
        if (isEmpty(input)) {
            return false;
        }
        for (int i = 0; i < input.length(); i++) {
            char charAt = input.charAt(i);
            if (charAt < '0' || charAt > '9') {
                return false;
            }
        }
        return true;
    }

    // 转换由表单读取的数据的内码
    public static String toChi(String input) {
        try {
            byte[] bytes = input.getBytes("ISO8859-1");
            return new String(bytes);
        } catch (Exception ex) {
        }
        return null;
    }

    // 将单个的 ' 换成 ''; SQL 规则:如果单引号中的字符串包含一个嵌入的引号，可以使用两个单引号表示嵌入的单引号。
    public String replaceSql(String input) {
        return replace(input, "'", "''");
    }

    // 对给定字符进行 URL 编码
    public static String encode(String value) {
        if (isEmpty(value)) {
            return "";
        }
        return URLEncoder.encode(value);
    }

    // 对给定字符进行 URL 解码
    public static String decode(String value) {
        if (isEmpty(value)) {
            return "";
        }
        return java.net.URLDecoder.decode(value);
    }

    /**
     * 判断字符串是否未空
     */
    public static boolean isEmpty(String input) {
        if (input == null || input.length() <= 0) {
            return true;
        }
        return false;
    }

    /**
     * Checks if a String is not empty ("") and not null.
     *
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is not empty and not null
     */
    public static boolean isNotEmpty(String str) {
        return (str != null && str.length() > 0);
    }
    /**
     * 判断字符串是否未空
     */
    public static boolean isNotEmptyString(String input) {
    	boolean bl=(input!=null&&!"".equals(input.trim())&&!"null".equals(input));
    	if (bl) {
            return true;
        }
        return false;
    }

    // 将心情符号修改为对应的图片 ------------- 请修改页面中相关代码!
    public String smilies(String temp) {
        if (isEmpty(temp)) {
            return "";
        }
        // 判断是否有禁止表情字符的表单值
        //if( isEmpty(request("smilies")) ) {
        temp = replace(temp, "/:)",
                       "<IMG border=0 SRC=images/brow/regular_smile.gif>");
        temp = replace(temp, "/:d",
                       "<IMG border=0 SRC=images/brow/teeth_smile.gif>");
        temp = replace(temp, "/:o",
                       "<IMG border=0 SRC=images/brow/omg_smile.gif>");
        temp = replace(temp, "/:p",
                       "<IMG border=0 SRC=images/brow/tounge_smile.gif>");
        temp = replace(temp, "/;)",
                       "<IMG border=0 SRC=images/brow/wink_smile.gif>");
        temp = replace(temp, "/:(",
                       "<IMG border=0 SRC=images/brow/sad_smile.gif>");
        temp = replace(temp, "/:s",
                       "<IMG border=0 SRC=images/brow/confused_smile.gif>");
        temp = replace(temp, "/:|",
                       "<IMG border=0 SRC=images/brow/whatchutalkingabout_smile.gif>");
        temp = replace(temp, "/:$",
                       "<IMG border=0 SRC=images/brow/embaressed_smile.gif>");
        //}
        return temp;
    }

    /**
     * 得到文件的扩展名.
     * @param fileName 需要处理的文件的名字.
     * @return the extension portion of the file's name.
     */
    public static String getExtension(String fileName) {
        if (fileName != null) {
            int i = fileName.lastIndexOf('.');
            if (i > 0 && i < fileName.length() - 1) {
                return fileName.substring(i + 1).toLowerCase();
            }
        }
        return "";
    }

    /**
     * 得到文件的前缀名.
     * @param fileName 需要处理的文件的名字.
     * @return the prefix portion of the file's name.
     */
    public static String getPrefix(String fileName) {
        if (fileName != null) {
            int i = fileName.lastIndexOf('.');
            if (i > 0 && i < fileName.length() - 1) {
                return fileName.substring(0, i);
            }
        }
        return "";
    }

    /**
     * Checks if a String is whitespace, empty ("") or null.
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a String is not empty (""), not null and not whitespace only.
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("null")      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is
     *  not empty and not null and not whitespace
     * @since 2.0
     */
    public static boolean isNotBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0||"null".equalsIgnoreCase(str)) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return true;
            }
        }
        return false;
    }

    // Trim
    //-----------------------------------------------------------------------
    /**
     * Removes control characters (char &lt;= 32) from both
     * ends of this String, handling <code>null</code> by returning
     * an empty String ("").
     *
     * <pre>
     * StringUtils.clean(null)          = ""
     * StringUtils.clean("")            = ""
     * StringUtils.clean("abc")         = "abc"
     * StringUtils.clean("    abc    ") = "abc"
     * StringUtils.clean("     ")       = ""
     * </pre>
     *
     * @see String#trim()
     * @param str  the String to clean, may be null
     * @return the trimmed text, never <code>null</code>
     * @deprecated Use the clearer named {@link #trimToEmpty(String)}.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String clean(String str) {
        return (str == null ? EMPTY : str.trim());
    }

    /**
     * Removes control characters (char &lt;= 32) from both
     * ends of this String, handling <code>null</code> by returning
     * <code>null</code>.
     *
     * The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     * To strip whitespace use {@link #strip(String)}.
     *
     * To trim your choice of characters, use the
     * {@link #strip(String, String)} methods.
     *
     * <pre>
     * StringUtils.trim(null)          = null
     * StringUtils.trim("")            = ""
     * StringUtils.trim("     ")       = ""
     * StringUtils.trim("abc")         = "abc"
     * StringUtils.trim("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed string, <code>null</code> if null String input
     */
    public static String trim(String str) {
        return (str == null ? null : str.trim());
    }

    /**
     * Removes control characters (char &lt;= 32) from both
     * ends of this String returning <code>null</code> if the String is
     * empty ("") after the trim or if it is <code>null</code>.
     *
     * The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     * To strip whitespace use {@link #stripToNull(String)}.
     *
     * <pre>
     * StringUtils.trimToNull(null)          = null
     * StringUtils.trimToNull("")            = null
     * StringUtils.trimToNull("     ")       = null
     * StringUtils.trimToNull("abc")         = "abc"
     * StringUtils.trimToNull("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed String,
     *  <code>null</code> if only chars &lt;= 32, empty or null String input
     * @since 2.0
     */
    public static String trimToNull(String str) {
        String ts = trim(str);
        return (isEmpty(ts) ? null : ts);
    }

    /**
     * Removes control characters (char &lt;= 32) from both
     * ends of this String returning an empty String ("") if the String
     * is empty ("") after the trim or if it is <code>null</code>.
     *
     * The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     * To strip whitespace use {@link #stripToEmpty(String)}.
     *
     * <pre>
     * StringUtils.trimToEmpty(null)          = ""
     * StringUtils.trimToEmpty("")            = ""
     * StringUtils.trimToEmpty("     ")       = ""
     * StringUtils.trimToEmpty("abc")         = "abc"
     * StringUtils.trimToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed String, or an empty String if <code>null</code> input
     * @since 2.0
     */
    public static String trimToEmpty(String str) {
        return (str == null ? EMPTY : str.trim());
    }

    // Stripping
    //-----------------------------------------------------------------------
    /**
     * Strips whitespace from the start and end of a String.
     *
     * This is similar to {@link #trim(String)} but removes whitespace.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.
     *
     * A <code>null</code> input String returns <code>null</code>.
     *
     * <pre>
     * StringUtils.strip(null)     = null
     * StringUtils.strip("")       = ""
     * StringUtils.strip("   ")    = ""
     * StringUtils.strip("abc")    = "abc"
     * StringUtils.strip("  abc")  = "abc"
     * StringUtils.strip("abc  ")  = "abc"
     * StringUtils.strip(" abc ")  = "abc"
     * StringUtils.strip(" ab c ") = "ab c"
     * </pre>
     *
     * @param str  the String to remove whitespace from, may be null
     * @return the stripped String, <code>null</code> if null String input
     */
    public static String strip(String str) {
        return strip(str, null);
    }

    /**
     * Strips whitespace from the start and end of a String  returning
     * <code>null</code> if the String is empty ("") after the strip.
     *
     * This is similar to {@link #trimToNull(String)} but removes whitespace.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.
     *
     * <pre>
     * StringUtils.strip(null)     = null
     * StringUtils.strip("")       = null
     * StringUtils.strip("   ")    = null
     * StringUtils.strip("abc")    = "abc"
     * StringUtils.strip("  abc")  = "abc"
     * StringUtils.strip("abc  ")  = "abc"
     * StringUtils.strip(" abc ")  = "abc"
     * StringUtils.strip(" ab c ") = "ab c"
     * </pre>
     *
     * @param str  the String to be stripped, may be null
     * @return the stripped String,
     *  <code>null</code> if whitespace, empty or null String input
     * @since 2.0
     */
    public static String stripToNull(String str) {
        if (str == null) {
            return null;
        }
        str = strip(str, null);
        return (str.length() == 0 ? null : str);
    }

    /**
     * Strips whitespace from the start and end of a String  returning
     * an empty String if <code>null</code> input.
     *
     * This is similar to {@link #trimToEmpty(String)} but removes whitespace.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.
     *
     * <pre>
     * StringUtils.strip(null)     = ""
     * StringUtils.strip("")       = ""
     * StringUtils.strip("   ")    = ""
     * StringUtils.strip("abc")    = "abc"
     * StringUtils.strip("  abc")  = "abc"
     * StringUtils.strip("abc  ")  = "abc"
     * StringUtils.strip(" abc ")  = "abc"
     * StringUtils.strip(" ab c ") = "ab c"
     * </pre>
     *
     * @param str  the String to be stripped, may be null
     * @return the trimmed String, or an empty String if <code>null</code> input
     * @since 2.0
     */
    public static String stripToEmpty(String str) {
        return (str == null ? EMPTY : strip(str, null));
    }

    /**
     * Strips any of a set of characters from the start and end of a String.
     * This is similar to {@link String#trim()} but allows the characters
     * to be stripped to be controlled.
     *
     * A <code>null</code> input String returns <code>null</code>.
     * An empty string ("") input returns the empty string.
     *
     * If the stripChars String is <code>null</code>, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.
     * Alternatively use {@link #strip(String)}.
     *
     * <pre>
     * StringUtils.strip(null, *)          = null
     * StringUtils.strip("", *)            = ""
     * StringUtils.strip("abc", null)      = "abc"
     * StringUtils.strip("  abc", null)    = "abc"
     * StringUtils.strip("abc  ", null)    = "abc"
     * StringUtils.strip(" abc ", null)    = "abc"
     * StringUtils.strip("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str  the String to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped String, <code>null</code> if null String input
     */
    public static String strip(String str, String stripChars) {
        if (isEmpty(str)) {
            return str;
        }
        str = stripStart(str, stripChars);
        return stripEnd(str, stripChars);
    }

    /**
     * Strips any of a set of characters from the start of a String.
     *
     * A <code>null</code> input String returns <code>null</code>.
     * An empty string ("") input returns the empty string.
     *
     * If the stripChars String is <code>null</code>, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.
     *
     * <pre>
     * StringUtils.stripStart(null, *)          = null
     * StringUtils.stripStart("", *)            = ""
     * StringUtils.stripStart("abc", "")        = "abc"
     * StringUtils.stripStart("abc", null)      = "abc"
     * StringUtils.stripStart("  abc", null)    = "abc"
     * StringUtils.stripStart("abc  ", null)    = "abc  "
     * StringUtils.stripStart(" abc ", null)    = "abc "
     * StringUtils.stripStart("yxabc  ", "xyz") = "abc  "
     * </pre>
     *
     * @param str  the String to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped String, <code>null</code> if null String input
     */
    public static String stripStart(String str, String stripChars) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            while ((start != strLen) && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((start != strLen) && (stripChars.indexOf(str.charAt(start)) != -1)) {
                start++;
            }
        }
        return str.substring(start);
    }

    /**
     * Strips any of a set of characters from the end of a String.
     *
     * A <code>null</code> input String returns <code>null</code>.
     * An empty string ("") input returns the empty string.
     *
     * If the stripChars String is <code>null</code>, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.
     *
     * <pre>
     * StringUtils.stripEnd(null, *)          = null
     * StringUtils.stripEnd("", *)            = ""
     * StringUtils.stripEnd("abc", "")        = "abc"
     * StringUtils.stripEnd("abc", null)      = "abc"
     * StringUtils.stripEnd("  abc", null)    = "  abc"
     * StringUtils.stripEnd("abc  ", null)    = "abc"
     * StringUtils.stripEnd(" abc ", null)    = " abc"
     * StringUtils.stripEnd("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str  the String to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped String, <code>null</code> if null String input
     */
    public static String stripEnd(String str, String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }

        if (stripChars == null) {
            while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else if (stripChars.length() == 0) {
            return str;
        } else {
            while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    // StripAll
    //-----------------------------------------------------------------------
    /**
     * Strips whitespace from the start and end of every String in an array.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.
     *
     * A new array is returned each time, except for length zero.
     * A <code>null</code> array will return <code>null</code>.
     * An empty array will return itself.
     * A <code>null</code> array entry will be ignored.
     *
     * <pre>
     * StringUtils.stripAll(null)             = null
     * StringUtils.stripAll([])               = []
     * StringUtils.stripAll(["abc", "  abc"]) = ["abc", "abc"]
     * StringUtils.stripAll(["abc  ", null])  = ["abc", null]
     * </pre>
     *
     * @param strs  the array to remove whitespace from, may be null
     * @return the stripped Strings, <code>null</code> if null array input
     */
    public static String[] stripAll(String[] strs) {
        return stripAll(strs, null);
    }

    /**
     * Strips any of a set of characters from the start and end of every
     * String in an array.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.
     *
     * A new array is returned each time, except for length zero.
     * A <code>null</code> array will return <code>null</code>.
     * An empty array will return itself.
     * A <code>null</code> array entry will be ignored.
     * A <code>null</code> stripChars will strip whitespace as defined by
     * {@link Character#isWhitespace(char)}.
     *
     * <pre>
     * StringUtils.stripAll(null, *)                = null
     * StringUtils.stripAll([], *)                  = []
     * StringUtils.stripAll(["abc", "  abc"], null) = ["abc", "abc"]
     * StringUtils.stripAll(["abc  ", null], null)  = ["abc", null]
     * StringUtils.stripAll(["abc  ", null], "yz")  = ["abc  ", null]
     * StringUtils.stripAll(["yabcz", null], "yz")  = ["abc", null]
     * </pre>
     *
     * @param strs  the array to remove characters from, may be null
     * @param stripChars  the characters to remove, null treated as whitespace
     * @return the stripped Strings, <code>null</code> if null array input
     */
    public static String[] stripAll(String[] strs, String stripChars) {
        int strsLen;
        if (strs == null || (strsLen = strs.length) == 0) {
            return strs;
        }
        String[] newArr = new String[strsLen];
        for (int i = 0; i < strsLen; i++) {
            newArr[i] = strip(strs[i], stripChars);
        }
        return newArr;
    }

    // Equals
    //-----------------------------------------------------------------------
    /**
     * Compares two Strings, returning <code>true</code> if they are equal.
     *
     * <code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered to be equal. The comparison is case sensitive.
     *
     * <pre>
     * StringUtils.equals(null, null)   = true
     * StringUtils.equals(null, "abc")  = false
     * StringUtils.equals("abc", null)  = false
     * StringUtils.equals("abc", "abc") = true
     * StringUtils.equals("abc", "ABC") = false
     * </pre>
     *
     * @see String#equals(Object)
     * @param str1  the first String, may be null
     * @param str2  the second String, may be null
     * @return <code>true</code> if the Strings are equal, case sensitive, or
     *  both <code>null</code>
     */
    public static boolean equals(String str1, String str2) {
        return (str1 == null ? str2 == null : str1.equals(str2));
    }

    /**
     * Compares two Strings, returning <code>true</code> if they are equal ignoring
     * the case.
     *
     * <code>null</code>s are handled without exceptions. Two <code>null</code>
     * references are considered equal. Comparison is case insensitive.
     *
     * <pre>
     * StringUtils.equalsIgnoreCase(null, null)   = true
     * StringUtils.equalsIgnoreCase(null, "abc")  = false
     * StringUtils.equalsIgnoreCase("abc", null)  = false
     * StringUtils.equalsIgnoreCase("abc", "abc") = true
     * StringUtils.equalsIgnoreCase("abc", "ABC") = true
     * </pre>
     *
     * @see String#equalsIgnoreCase(String)
     * @param str1  the first String, may be null
     * @param str2  the second String, may be null
     * @return <code>true</code> if the Strings are equal, case insensitive, or
     *  both <code>null</code>
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return (str1 == null ? str2 == null : str1.equalsIgnoreCase(str2));
    }

    // IndexOf
    //-----------------------------------------------------------------------
    /**
     * Finds the first index within a String, handling <code>null</code>.
     * This method uses {@link String#indexOf(int)}.
     *
     * A <code>null</code> or empty ("") String will return <code>-1</code>.
     *
     * <pre>
     * StringUtils.indexOf(null, *)         = -1
     * StringUtils.indexOf("", *)           = -1
     * StringUtils.indexOf("aabaabaa", 'a') = 0
     * StringUtils.indexOf("aabaabaa", 'b') = 2
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChar  the character to find
     * @return the first index of the search character,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, char searchChar) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.indexOf(searchChar);
    }

    /**
     * Finds the first index within a String from a start position,
     * handling <code>null</code>.
     * This method uses {@link String#indexOf(int, int)}.
     *
     * A <code>null</code> or empty ("") String will return <code>-1</code>.
     * A negative start position is treated as zero.
     * A start position greater than the string length returns <code>-1</code>.
     *
     * <pre>
     * StringUtils.indexOf(null, *, *)          = -1
     * StringUtils.indexOf("", *, *)            = -1
     * StringUtils.indexOf("aabaabaa", 'b', 0)  = 2
     * StringUtils.indexOf("aabaabaa", 'b', 3)  = 5
     * StringUtils.indexOf("aabaabaa", 'b', 9)  = -1
     * StringUtils.indexOf("aabaabaa", 'b', -1) = 2
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChar  the character to find
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search character,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, char searchChar, int startPos) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.indexOf(searchChar, startPos);
    }

    /**
     * Finds the first index within a String, handling <code>null</code>.
     * This method uses {@link String#indexOf(String)}.
     *
     * A <code>null</code> String will return <code>-1</code>.
     *
     * <pre>
     * StringUtils.indexOf(null, *)          = -1
     * StringUtils.indexOf(*, null)          = -1
     * StringUtils.indexOf("", "")           = 0
     * StringUtils.indexOf("aabaabaa", "a")  = 0
     * StringUtils.indexOf("aabaabaa", "b")  = 2
     * StringUtils.indexOf("aabaabaa", "ab") = 1
     * StringUtils.indexOf("aabaabaa", "")   = 0
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @return the first index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.indexOf(searchStr);
    }

    /**
     * Finds the n-th index within a String, handling <code>null</code>.
     * This method uses {@link String#indexOf(String)}.
     *
     * A <code>null</code> String will return <code>-1</code>.
     *
     * <pre>
     * StringUtils.ordinalIndexOf(null, *, *)          = -1
     * StringUtils.ordinalIndexOf(*, null, *)          = -1
     * StringUtils.ordinalIndexOf("", "", *)           = 0
     * StringUtils.ordinalIndexOf("aabaabaa", "a", 1)  = 0
     * StringUtils.ordinalIndexOf("aabaabaa", "a", 2)  = 1
     * StringUtils.ordinalIndexOf("aabaabaa", "b", 1)  = 2
     * StringUtils.ordinalIndexOf("aabaabaa", "b", 2)  = 5
     * StringUtils.ordinalIndexOf("aabaabaa", "ab", 1) = 1
     * StringUtils.ordinalIndexOf("aabaabaa", "ab", 2) = 4
     * StringUtils.ordinalIndexOf("aabaabaa", "", 1)   = 0
     * StringUtils.ordinalIndexOf("aabaabaa", "", 2)   = 0
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @param ordinal  the n-th <code>searchStr</code> to find
     * @return the n-th index of the search String,
     *  <code>-1</code> (<code>INDEX_NOT_FOUND</code>) if no match or <code>null</code> string input
     * @since 2.?.?
     */
    public static int ordinalIndexOf(String str, String searchStr, int ordinal) {
        if (str == null || searchStr == null || ordinal <= 0) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return 0;
        }
        int found = 0;
        int index = INDEX_NOT_FOUND;
        do {
            index = str.indexOf(searchStr, index + 1);
            if (index < 0) {
                return index;
            }
            found++;
        } while (found < ordinal);
        return index;
    }

    /**
     * Finds the first index within a String, handling <code>null</code>.
     * This method uses {@link String#indexOf(String, int)}.
     *
     * A <code>null</code> String will return <code>-1</code>.
     * A negative start position is treated as zero.
     * An empty ("") search String always matches.
     * A start position greater than the string length only matches
     * an empty search String.
     *
     * <pre>
     * StringUtils.indexOf(null, *, *)          = -1
     * StringUtils.indexOf(*, null, *)          = -1
     * StringUtils.indexOf("", "", 0)           = 0
     * StringUtils.indexOf("aabaabaa", "a", 0)  = 0
     * StringUtils.indexOf("aabaabaa", "b", 0)  = 2
     * StringUtils.indexOf("aabaabaa", "ab", 0) = 1
     * StringUtils.indexOf("aabaabaa", "b", 3)  = 5
     * StringUtils.indexOf("aabaabaa", "b", 9)  = -1
     * StringUtils.indexOf("aabaabaa", "b", -1) = 2
     * StringUtils.indexOf("aabaabaa", "", 2)   = 2
     * StringUtils.indexOf("abc", "", 9)        = 3
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int indexOf(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        // JDK1.2/JDK1.3 have a bug, when startPos > str.length for "", hence
        if (searchStr.length() == 0 && startPos >= str.length()) {
            return str.length();
        }
        return str.indexOf(searchStr, startPos);
    }

    // LastIndexOf
    //-----------------------------------------------------------------------
    /**
     * Finds the last index within a String, handling <code>null</code>.
     * This method uses {@link String#lastIndexOf(int)}.
     *
     * A <code>null</code> or empty ("") String will return <code>-1</code>.
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *)         = -1
     * StringUtils.lastIndexOf("", *)           = -1
     * StringUtils.lastIndexOf("aabaabaa", 'a') = 7
     * StringUtils.lastIndexOf("aabaabaa", 'b') = 5
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChar  the character to find
     * @return the last index of the search character,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, char searchChar) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.lastIndexOf(searchChar);
    }

    /**
     * Finds the last index within a String from a start position,
     * handling <code>null</code>.
     * This method uses {@link String#lastIndexOf(int, int)}.
     *
     * A <code>null</code> or empty ("") String will return <code>-1</code>.
     * A negative start position returns <code>-1</code>.
     * A start position greater than the string length searches the whole string.
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *, *)          = -1
     * StringUtils.lastIndexOf("", *,  *)           = -1
     * StringUtils.lastIndexOf("aabaabaa", 'b', 8)  = 5
     * StringUtils.lastIndexOf("aabaabaa", 'b', 4)  = 2
     * StringUtils.lastIndexOf("aabaabaa", 'b', 0)  = -1
     * StringUtils.lastIndexOf("aabaabaa", 'b', 9)  = 5
     * StringUtils.lastIndexOf("aabaabaa", 'b', -1) = -1
     * StringUtils.lastIndexOf("aabaabaa", 'a', 0)  = 0
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChar  the character to find
     * @param startPos  the start position
     * @return the last index of the search character,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, char searchChar, int startPos) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.lastIndexOf(searchChar, startPos);
    }

    /**
     * Finds the last index within a String, handling <code>null</code>.
     * This method uses {@link String#lastIndexOf(String)}.
     *
     * A <code>null</code> String will return <code>-1</code>.
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *)          = -1
     * StringUtils.lastIndexOf(*, null)          = -1
     * StringUtils.lastIndexOf("", "")           = 0
     * StringUtils.lastIndexOf("aabaabaa", "a")  = 0
     * StringUtils.lastIndexOf("aabaabaa", "b")  = 2
     * StringUtils.lastIndexOf("aabaabaa", "ab") = 1
     * StringUtils.lastIndexOf("aabaabaa", "")   = 8
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @return the last index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.lastIndexOf(searchStr);
    }

    /**
     * Finds the first index within a String, handling <code>null</code>.
     * This method uses {@link String#lastIndexOf(String, int)}.
     *
     * A <code>null</code> String will return <code>-1</code>.
     * A negative start position returns <code>-1</code>.
     * An empty ("") search String always matches unless the start position is negative.
     * A start position greater than the string length searches the whole string.
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *, *)          = -1
     * StringUtils.lastIndexOf(*, null, *)          = -1
     * StringUtils.lastIndexOf("aabaabaa", "a", 8)  = 7
     * StringUtils.lastIndexOf("aabaabaa", "b", 8)  = 5
     * StringUtils.lastIndexOf("aabaabaa", "ab", 8) = 4
     * StringUtils.lastIndexOf("aabaabaa", "b", 9)  = 5
     * StringUtils.lastIndexOf("aabaabaa", "b", -1) = -1
     * StringUtils.lastIndexOf("aabaabaa", "a", 0)  = 0
     * StringUtils.lastIndexOf("aabaabaa", "b", 0)  = -1
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @param startPos  the start position, negative treated as zero
     * @return the first index of the search String,
     *  -1 if no match or <code>null</code> string input
     * @since 2.0
     */
    public static int lastIndexOf(String str, String searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return -1;
        }
        return str.lastIndexOf(searchStr, startPos);
    }

    // Contains
    //-----------------------------------------------------------------------
    /**
     * Checks if String contains a search character, handling <code>null</code>.
     * This method uses {@link String#indexOf(int)}.
     *
     * A <code>null</code> or empty ("") String will return <code>false</code>.
     *
     * <pre>
     * StringUtils.contains(null, *)    = false
     * StringUtils.contains("", *)      = false
     * StringUtils.contains("abc", 'a') = true
     * StringUtils.contains("abc", 'z') = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChar  the character to find
     * @return true if the String contains the search character,
     *  false if not or <code>null</code> string input
     * @since 2.0
     */
    public static boolean contains(String str, char searchChar) {
        if (isEmpty(str)) {
            return false;
        }
        return (str.indexOf(searchChar) >= 0);
    }

    /**
     * Checks if String contains a search String, handling <code>null</code>.
     * This method uses {@link String#indexOf(int)}.
     *
     * A <code>null</code> String will return <code>false</code>.
     *
     * <pre>
     * StringUtils.contains(null, *)     = false
     * StringUtils.contains(*, null)     = false
     * StringUtils.contains("", "")      = true
     * StringUtils.contains("abc", "")   = true
     * StringUtils.contains("abc", "a")  = true
     * StringUtils.contains("abc", "z")  = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStr  the String to find, may be null
     * @return true if the String contains the search String,
     *  false if not or <code>null</code> string input
     * @since 2.0
     */
    public static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return (str.indexOf(searchStr) >= 0);
    }

    // IndexOfAny chars
    //-----------------------------------------------------------------------
    /**
     * Search a String to find the first index of any
     * character in the given set of characters.
     *
     * A <code>null</code> String will return <code>-1</code>.
     * A <code>null</code> or zero length search array will return <code>-1</code>.
     *
     * <pre>
     * StringUtils.indexOfAny(null, *)                = -1
     * StringUtils.indexOfAny("", *)                  = -1
     * StringUtils.indexOfAny(*, null)                = -1
     * StringUtils.indexOfAny(*, [])                  = -1
     * StringUtils.indexOfAny("zzabyycdxx",['z','a']) = 0
     * StringUtils.indexOfAny("zzabyycdxx",['b','y']) = 3
     * StringUtils.indexOfAny("aba", ['z'])           = -1
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChars  the chars to search for, may be null
     * @return the index of any of the chars, -1 if no match or null input
     * @since 2.0
     */
    public static int indexOfAny(String str, char[] searchChars) {
        if (isEmpty(str) || ArrayUtils.isEmpty(searchChars)) {
            return -1;
        }
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Search a String to find the first index of any
     * character in the given set of characters.
     *
     * A <code>null</code> String will return <code>-1</code>.
     * A <code>null</code> search string will return <code>-1</code>.
     *
     * <pre>
     * StringUtils.indexOfAny(null, *)            = -1
     * StringUtils.indexOfAny("", *)              = -1
     * StringUtils.indexOfAny(*, null)            = -1
     * StringUtils.indexOfAny(*, "")              = -1
     * StringUtils.indexOfAny("zzabyycdxx", "za") = 0
     * StringUtils.indexOfAny("zzabyycdxx", "by") = 3
     * StringUtils.indexOfAny("aba","z")          = -1
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChars  the chars to search for, may be null
     * @return the index of any of the chars, -1 if no match or null input
     * @since 2.0
     */
    public static int indexOfAny(String str, String searchChars) {
        if (isEmpty(str) || isEmpty(searchChars)) {
            return -1;
        }
        return indexOfAny(str, searchChars.toCharArray());
    }

    // IndexOfAnyBut chars
    //-----------------------------------------------------------------------
    /**
     * Search a String to find the first index of any
     * character not in the given set of characters.
     *
     * A <code>null</code> String will return <code>-1</code>.
     * A <code>null</code> or zero length search array will return <code>-1</code>.
     *
     * <pre>
     * StringUtils.indexOfAnyBut(null, *)           = -1
     * StringUtils.indexOfAnyBut("", *)             = -1
     * StringUtils.indexOfAnyBut(*, null)           = -1
     * StringUtils.indexOfAnyBut(*, [])             = -1
     * StringUtils.indexOfAnyBut("zzabyycdxx",'za') = 3
     * StringUtils.indexOfAnyBut("zzabyycdxx", '')  = 0
     * StringUtils.indexOfAnyBut("aba", 'ab')       = -1
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChars  the chars to search for, may be null
     * @return the index of any of the chars, -1 if no match or null input
     * @since 2.0
     */
    public static int indexOfAnyBut(String str, char[] searchChars) {
        if (isEmpty(str) || ArrayUtils.isEmpty(searchChars)) {
            return -1;
        }
        outer : for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    /**
     * Search a String to find the first index of any
     * character not in the given set of characters.
     *
     * A <code>null</code> String will return <code>-1</code>.
     * A <code>null</code> search string will return <code>-1</code>.
     *
     * <pre>
     * StringUtils.indexOfAnyBut(null, *)            = -1
     * StringUtils.indexOfAnyBut("", *)              = -1
     * StringUtils.indexOfAnyBut(*, null)            = -1
     * StringUtils.indexOfAnyBut(*, "")              = -1
     * StringUtils.indexOfAnyBut("zzabyycdxx", "za") = 3
     * StringUtils.indexOfAnyBut("zzabyycdxx", "")   = 0
     * StringUtils.indexOfAnyBut("aba","ab")         = -1
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchChars  the chars to search for, may be null
     * @return the index of any of the chars, -1 if no match or null input
     * @since 2.0
     */
    public static int indexOfAnyBut(String str, String searchChars) {
        if (isEmpty(str) || isEmpty(searchChars)) {
            return -1;
        }
        for (int i = 0; i < str.length(); i++) {
            if (searchChars.indexOf(str.charAt(i)) < 0) {
                return i;
            }
        }
        return -1;
    }

    // ContainsOnly
    //-----------------------------------------------------------------------
    /**
     * Checks if the String contains only certain characters.
     *
     * A <code>null</code> String will return <code>false</code>.
     * A <code>null</code> valid character array will return <code>false</code>.
     * An empty String ("") always returns <code>true</code>.
     *
     * <pre>
     * StringUtils.containsOnly(null, *)       = false
     * StringUtils.containsOnly(*, null)       = false
     * StringUtils.containsOnly("", *)         = true
     * StringUtils.containsOnly("ab", '')      = false
     * StringUtils.containsOnly("abab", 'abc') = true
     * StringUtils.containsOnly("ab1", 'abc')  = false
     * StringUtils.containsOnly("abz", 'abc')  = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param valid  an array of valid chars, may be null
     * @return true if it only contains valid chars and is non-null
     */
    public static boolean containsOnly(String str, char[] valid) {
        // All these pre-checks are to maintain API with an older version
        if ((valid == null) || (str == null)) {
            return false;
        }
        if (str.length() == 0) {
            return true;
        }
        if (valid.length == 0) {
            return false;
        }
        return indexOfAnyBut(str, valid) == -1;
    }

    /**
     * Checks if the String contains only certain characters.
     *
     * A <code>null</code> String will return <code>false</code>.
     * A <code>null</code> valid character String will return <code>false</code>.
     * An empty String ("") always returns <code>true</code>.
     *
     * <pre>
     * StringUtils.containsOnly(null, *)       = false
     * StringUtils.containsOnly(*, null)       = false
     * StringUtils.containsOnly("", *)         = true
     * StringUtils.containsOnly("ab", "")      = false
     * StringUtils.containsOnly("abab", "abc") = true
     * StringUtils.containsOnly("ab1", "abc")  = false
     * StringUtils.containsOnly("abz", "abc")  = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param validChars  a String of valid chars, may be null
     * @return true if it only contains valid chars and is non-null
     * @since 2.0
     */
    public static boolean containsOnly(String str, String validChars) {
        if (str == null || validChars == null) {
            return false;
        }
        return containsOnly(str, validChars.toCharArray());
    }

    // ContainsNone
    //-----------------------------------------------------------------------
    /**
     * Checks that the String does not contain certain characters.
     *
     * A <code>null</code> String will return <code>true</code>.
     * A <code>null</code> invalid character array will return <code>true</code>.
     * An empty String ("") always returns true.
     *
     * <pre>
     * StringUtils.containsNone(null, *)       = true
     * StringUtils.containsNone(*, null)       = true
     * StringUtils.containsNone("", *)         = true
     * StringUtils.containsNone("ab", '')      = true
     * StringUtils.containsNone("abab", 'xyz') = true
     * StringUtils.containsNone("ab1", 'xyz')  = true
     * StringUtils.containsNone("abz", 'xyz')  = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param invalidChars  an array of invalid chars, may be null
     * @return true if it contains none of the invalid chars, or is null
     * @since 2.0
     */
    public static boolean containsNone(String str, char[] invalidChars) {
        if (str == null || invalidChars == null) {
            return true;
        }
        int strSize = str.length();
        int validSize = invalidChars.length;
        for (int i = 0; i < strSize; i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < validSize; j++) {
                if (invalidChars[j] == ch) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks that the String does not contain certain characters.
     *
     * A <code>null</code> String will return <code>true</code>.
     * A <code>null</code> invalid character array will return <code>true</code>.
     * An empty String ("") always returns true.
     *
     * <pre>
     * StringUtils.containsNone(null, *)       = true
     * StringUtils.containsNone(*, null)       = true
     * StringUtils.containsNone("", *)         = true
     * StringUtils.containsNone("ab", "")      = true
     * StringUtils.containsNone("abab", "xyz") = true
     * StringUtils.containsNone("ab1", "xyz")  = true
     * StringUtils.containsNone("abz", "xyz")  = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param invalidChars  a String of invalid chars, may be null
     * @return true if it contains none of the invalid chars, or is null
     * @since 2.0
     */
    public static boolean containsNone(String str, String invalidChars) {
        if (str == null || invalidChars == null) {
            return true;
        }
        return containsNone(str, invalidChars.toCharArray());
    }

    // IndexOfAny strings
    //-----------------------------------------------------------------------
    /**
     * Find the first index of any of a set of potential substrings.
     *
     * A <code>null</code> String will return <code>-1</code>.
     * A <code>null</code> or zero length search array will return <code>-1</code>.
     * A <code>null</code> search array entry will be ignored, but a search
     * array containing "" will return <code>0</code> if <code>str</code> is not
     * null. This method uses {@link String#indexOf(String)}.
     *
     * <pre>
     * StringUtils.indexOfAny(null, *)                     = -1
     * StringUtils.indexOfAny(*, null)                     = -1
     * StringUtils.indexOfAny(*, [])                       = -1
     * StringUtils.indexOfAny("zzabyycdxx", ["ab","cd"])   = 2
     * StringUtils.indexOfAny("zzabyycdxx", ["cd","ab"])   = 2
     * StringUtils.indexOfAny("zzabyycdxx", ["mn","op"])   = -1
     * StringUtils.indexOfAny("zzabyycdxx", ["zab","aby"]) = 1
     * StringUtils.indexOfAny("zzabyycdxx", [""])          = 0
     * StringUtils.indexOfAny("", [""])                    = 0
     * StringUtils.indexOfAny("", ["a"])                   = -1
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStrs  the Strings to search for, may be null
     * @return the first index of any of the searchStrs in str, -1 if no match
     */
    public static int indexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }
        int sz = searchStrs.length;

        // String's can't have a MAX_VALUEth index.
        int ret = Integer.MAX_VALUE;

        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            String search = searchStrs[i];
            if (search == null) {
                continue;
            }
            tmp = str.indexOf(search);
            if (tmp == -1) {
                continue;
            }

            if (tmp < ret) {
                ret = tmp;
            }
        }

        return (ret == Integer.MAX_VALUE) ? -1 : ret;
    }

    /**
     * Find the latest index of any of a set of potential substrings.
     *
     * A <code>null</code> String will return <code>-1</code>.
     * A <code>null</code> search array will return <code>-1</code>.
     * A <code>null</code> or zero length search array entry will be ignored,
     * but a search array containing "" will return the length of <code>str</code>
     * if <code>str</code> is not null. This method uses {@link String#indexOf(String)}
     *
     * <pre>
     * StringUtils.lastIndexOfAny(null, *)                   = -1
     * StringUtils.lastIndexOfAny(*, null)                   = -1
     * StringUtils.lastIndexOfAny(*, [])                     = -1
     * StringUtils.lastIndexOfAny(*, [null])                 = -1
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["ab","cd"]) = 6
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["cd","ab"]) = 6
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn","op"]) = -1
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn",""])   = 10
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param searchStrs  the Strings to search for, may be null
     * @return the last index of any of the Strings, -1 if no match
     */
    public static int lastIndexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }
        int sz = searchStrs.length;
        int ret = -1;
        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            String search = searchStrs[i];
            if (search == null) {
                continue;
            }
            tmp = str.lastIndexOf(search);
            if (tmp > ret) {
                ret = tmp;
            }
        }
        return ret;
    }

    // Substring
    //-----------------------------------------------------------------------
    /**
     * Gets a substring from the specified String avoiding exceptions.
     *
     * A negative start position can be used to start <code>n</code>
     * characters from the end of the String.
     *
     * A <code>null</code> String will return <code>null</code>.
     * An empty ("") String will return "".
     *
     * <pre>
     * StringUtils.substring(null, *)   = null
     * StringUtils.substring("", *)     = ""
     * StringUtils.substring("abc", 0)  = "abc"
     * StringUtils.substring("abc", 2)  = "c"
     * StringUtils.substring("abc", 4)  = ""
     * StringUtils.substring("abc", -2) = "bc"
     * StringUtils.substring("abc", -4) = "abc"
     * </pre>
     *
     * @param str  the String to get the substring from, may be null
     * @param start  the position to start from, negative means
     *  count back from the end of the String by this many characters
     * @return substring from start position, <code>null</code> if null String input
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }

        // handle negatives, which means last n characters
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return EMPTY;
        }

        return str.substring(start);
    }

    /**
     * Gets a substring from the specified String avoiding exceptions.
     *
     * A negative start position can be used to start/end <code>n</code>
     * characters from the end of the String.
     *
     * The returned substring starts with the character in the <code>start</code>
     * position and ends before the <code>end</code> position. All position counting is
     * zero-based -- i.e., to start at the beginning of the string use
     * <code>start = 0</code>. Negative start and end positions can be used to
     * specify offsets relative to the end of the String.
     *
     * If <code>start</code> is not strictly to the left of <code>end</code>, ""
     * is returned.
     *
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", * ,  *)    = "";
     * StringUtils.substring("abc", 0, 2)   = "ab"
     * StringUtils.substring("abc", 2, 0)   = ""
     * StringUtils.substring("abc", 2, 4)   = "c"
     * StringUtils.substring("abc", 4, 6)   = ""
     * StringUtils.substring("abc", 2, 2)   = ""
     * StringUtils.substring("abc", -2, -1) = "b"
     * StringUtils.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str  the String to get the substring from, may be null
     * @param start  the position to start from, negative means
     *  count back from the end of the String by this many characters
     * @param end  the position to end at (exclusive), negative means
     *  count back from the end of the String by this many characters
     * @return substring from start position to end positon,
     *  <code>null</code> if null String input
     */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) {
            return EMPTY;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    // Left/Right/Mid
    //-----------------------------------------------------------------------
    /**
     * Gets the leftmost <code>len</code> characters of a String.
     *
     * If <code>len</code> characters are not available, or the
     * String is <code>null</code>, the String will be returned without
     * an exception. An exception is thrown if len is negative.
     *
     * <pre>
     * StringUtils.left(null, *)    = null
     * StringUtils.left(*, -ve)     = ""
     * StringUtils.left("", *)      = ""
     * StringUtils.left("abc", 0)   = ""
     * StringUtils.left("abc", 2)   = "ab"
     * StringUtils.left("abc", 4)   = "abc"
     * </pre>
     *
     * @param str  the String to get the leftmost characters from, may be null
     * @param len  the length of the required String, must be zero or positive
     * @return the leftmost characters, <code>null</code> if null String input
     */
    public static String left(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        } else {
            return str.substring(0, len);
        }
    }

    /**
     * Gets the rightmost <code>len</code> characters of a String.
     *
     * If <code>len</code> characters are not available, or the String
     * is <code>null</code>, the String will be returned without an
     * an exception. An exception is thrown if len is negative.
     *
     * <pre>
     * StringUtils.right(null, *)    = null
     * StringUtils.right(*, -ve)     = ""
     * StringUtils.right("", *)      = ""
     * StringUtils.right("abc", 0)   = ""
     * StringUtils.right("abc", 2)   = "bc"
     * StringUtils.right("abc", 4)   = "abc"
     * </pre>
     *
     * @param str  the String to get the rightmost characters from, may be null
     * @param len  the length of the required String, must be zero or positive
     * @return the rightmost characters, <code>null</code> if null String input
     */
    public static String right(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        } else {
            return str.substring(str.length() - len);
        }
    }

    /**
     * Gets <code>len</code> characters from the middle of a String.
     *
     * If <code>len</code> characters are not available, the remainder
     * of the String will be returned without an exception. If the
     * String is <code>null</code>, <code>null</code> will be returned.
     * An exception is thrown if len is negative.
     *
     * <pre>
     * StringUtils.mid(null, *, *)    = null
     * StringUtils.mid(*, *, -ve)     = ""
     * StringUtils.mid("", 0, *)      = ""
     * StringUtils.mid("abc", 0, 2)   = "ab"
     * StringUtils.mid("abc", 0, 4)   = "abc"
     * StringUtils.mid("abc", 2, 4)   = "c"
     * StringUtils.mid("abc", 4, 2)   = ""
     * StringUtils.mid("abc", -2, 2)  = "ab"
     * </pre>
     *
     * @param str  the String to get the characters from, may be null
     * @param pos  the position to start from, negative treated as zero
     * @param len  the length of the required String, must be zero or positive
     * @return the middle characters, <code>null</code> if null String input
     */
    public static String mid(String str, int pos, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0 || pos > str.length()) {
            return EMPTY;
        }
        if (pos < 0) {
            pos = 0;
        }
        if (str.length() <= (pos + len)) {
            return str.substring(pos);
        } else {
            return str.substring(pos, pos + len);
        }
    }

    // SubStringAfter/SubStringBefore
    //-----------------------------------------------------------------------
    /**
     * Gets the substring before the first occurrence of a separator.
     * The separator is not returned.
     *
     * A <code>null</code> string input will return <code>null</code>.
     * An empty ("") string input will return the empty string.
     * A <code>null</code> separator will return the input string.
     *
     * <pre>
     * StringUtils.substringBefore(null, *)      = null
     * StringUtils.substringBefore("", *)        = ""
     * StringUtils.substringBefore("abc", "a")   = ""
     * StringUtils.substringBefore("abcba", "b") = "a"
     * StringUtils.substringBefore("abc", "c")   = "ab"
     * StringUtils.substringBefore("abc", "d")   = "abc"
     * StringUtils.substringBefore("abc", "")    = ""
     * StringUtils.substringBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring before the first occurrence of the separator,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String substringBefore(String str, String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.length() == 0) {
            return EMPTY;
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * Gets the substring after the first occurrence of a separator.
     * The separator is not returned.
     *
     * A <code>null</code> string input will return <code>null</code>.
     * An empty ("") string input will return the empty string.
     * A <code>null</code> separator will return the empty string if the
     * input string is not <code>null</code>.
     *
     * <pre>
     * StringUtils.substringAfter(null, *)      = null
     * StringUtils.substringAfter("", *)        = ""
     * StringUtils.substringAfter(*, null)      = ""
     * StringUtils.substringAfter("abc", "a")   = "bc"
     * StringUtils.substringAfter("abcba", "b") = "cba"
     * StringUtils.substringAfter("abc", "c")   = ""
     * StringUtils.substringAfter("abc", "d")   = ""
     * StringUtils.substringAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring after the first occurrence of the separator,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String substringAfter(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return EMPTY;
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    /**
     * Gets the substring before the last occurrence of a separator.
     * The separator is not returned.
     *
     * A <code>null</code> string input will return <code>null</code>.
     * An empty ("") string input will return the empty string.
     * An empty or <code>null</code> separator will return the input string.
     *
     * <pre>
     * StringUtils.substringBeforeLast(null, *)      = null
     * StringUtils.substringBeforeLast("", *)        = ""
     * StringUtils.substringBeforeLast("abcba", "b") = "abc"
     * StringUtils.substringBeforeLast("abc", "c")   = "ab"
     * StringUtils.substringBeforeLast("a", "a")     = ""
     * StringUtils.substringBeforeLast("a", "z")     = "a"
     * StringUtils.substringBeforeLast("a", null)    = "a"
     * StringUtils.substringBeforeLast("a", "")      = "a"
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring before the last occurrence of the separator,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String substringBeforeLast(String str, String separator) {
        if (isEmpty(str) || isEmpty(separator)) {
            return str;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * Gets the substring after the last occurrence of a separator.
     * The separator is not returned.
     *
     * A <code>null</code> string input will return <code>null</code>.
     * An empty ("") string input will return the empty string.
     * An empty or <code>null</code> separator will return the empty string if
     * the input string is not <code>null</code>.
     *
     * <pre>
     * StringUtils.substringAfterLast(null, *)      = null
     * StringUtils.substringAfterLast("", *)        = ""
     * StringUtils.substringAfterLast(*, "")        = ""
     * StringUtils.substringAfterLast(*, null)      = ""
     * StringUtils.substringAfterLast("abc", "a")   = "bc"
     * StringUtils.substringAfterLast("abcba", "b") = "a"
     * StringUtils.substringAfterLast("abc", "c")   = ""
     * StringUtils.substringAfterLast("a", "a")     = ""
     * StringUtils.substringAfterLast("a", "z")     = ""
     * </pre>
     *
     * @param str  the String to get a substring from, may be null
     * @param separator  the String to search for, may be null
     * @return the substring after the last occurrence of the separator,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String substringAfterLast(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(separator)) {
            return EMPTY;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1 || pos == (str.length() - separator.length())) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    // Substring between
    //-----------------------------------------------------------------------
    /**
     * Gets the String that is nested in between two instances of the
     * same String.
     *
     * A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> tag returns <code>null</code>.
     *
     * <pre>
     * StringUtils.substringBetween(null, *)            = null
     * StringUtils.substringBetween("", "")             = ""
     * StringUtils.substringBetween("", "tag")          = null
     * StringUtils.substringBetween("tagabctag", null)  = null
     * StringUtils.substringBetween("tagabctag", "")    = ""
     * StringUtils.substringBetween("tagabctag", "tag") = "abc"
     * </pre>
     *
     * @param str  the String containing the substring, may be null
     * @param tag  the String before and after the substring, may be null
     * @return the substring, <code>null</code> if no match
     * @since 2.0
     */
    public static String substringBetween(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    /**
     * Gets the String that is nested in between two Strings.
     * Only the first match is returned.
     *
     * A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> open/close returns <code>null</code> (no match).
     * An empty ("") open/close returns an empty string.
     *
     * <pre>
     * StringUtils.substringBetween(null, *, *)          = null
     * StringUtils.substringBetween("", "", "")          = ""
     * StringUtils.substringBetween("", "", "tag")       = null
     * StringUtils.substringBetween("", "tag", "tag")    = null
     * StringUtils.substringBetween("yabcz", null, null) = null
     * StringUtils.substringBetween("yabcz", "", "")     = ""
     * StringUtils.substringBetween("yabcz", "y", "z")   = "abc"
     * StringUtils.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str  the String containing the substring, may be null
     * @param open  the String before the substring, may be null
     * @param close  the String after the substring, may be null
     * @return the substring, <code>null</code> if no match
     * @since 2.0
     */
    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    // Nested extraction
    //-----------------------------------------------------------------------
    /**
     * Gets the String that is nested in between two instances of the
     * same String.
     *
     * A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> tag returns <code>null</code>.
     *
     * <pre>
     * StringUtils.getNestedString(null, *)            = null
     * StringUtils.getNestedString("", "")             = ""
     * StringUtils.getNestedString("", "tag")          = null
     * StringUtils.getNestedString("tagabctag", null)  = null
     * StringUtils.getNestedString("tagabctag", "")    = ""
     * StringUtils.getNestedString("tagabctag", "tag") = "abc"
     * </pre>
     *
     * @param str  the String containing nested-string, may be null
     * @param tag  the String before and after nested-string, may be null
     * @return the nested String, <code>null</code> if no match
     * @deprecated Use the better named {@link #substringBetween(String, String)}.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String getNestedString(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    /**
     * Gets the String that is nested in between two Strings.
     * Only the first match is returned.
     *
     * A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> open/close returns <code>null</code> (no match).
     * An empty ("") open/close returns an empty string.
     *
     * <pre>
     * StringUtils.getNestedString(null, *, *)          = null
     * StringUtils.getNestedString("", "", "")          = ""
     * StringUtils.getNestedString("", "", "tag")       = null
     * StringUtils.getNestedString("", "tag", "tag")    = null
     * StringUtils.getNestedString("yabcz", null, null) = null
     * StringUtils.getNestedString("yabcz", "", "")     = ""
     * StringUtils.getNestedString("yabcz", "y", "z")   = "abc"
     * StringUtils.getNestedString("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str  the String containing nested-string, may be null
     * @param open  the String before nested-string, may be null
     * @param close  the String after nested-string, may be null
     * @return the nested String, <code>null</code> if no match
     * @deprecated Use the better named {@link #substringBetween(String, String, String)}.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String getNestedString(String str, String open, String close) {
        return substringBetween(str, open, close);
    }

    // Splitting
    //-----------------------------------------------------------------------
    /**
     * Splits the provided text into an array, using whitespace as the
     * separator.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.
     *
     * The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the Tokenizer class.
     *
     * A <code>null</code> input String returns <code>null</code>.
     *
     * <pre>
     * StringUtils.split(null)       = null
     * StringUtils.split("")         = []
     * StringUtils.split("abc def")  = ["abc", "def"]
     * StringUtils.split("abc  def") = ["abc", "def"]
     * StringUtils.split(" abc ")    = ["abc"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] split(String str) {
        return split(str, null, -1);
    }

    /**
     * Splits the provided text into an array, separator specified.
     * This is an alternative to using StringTokenizer.
     *
     * The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the Tokenizer class.
     *
     * A <code>null</code> input String returns <code>null</code>.
     *
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("a.b.c", '.')    = ["a", "b", "c"]
     * StringUtils.split("a..b.c", '.')   = ["a", "b", "c"]
     * StringUtils.split("a:b:c", '.')    = ["a:b:c"]
     * StringUtils.split("a\tb\nc", null) = ["a", "b", "c"]
     * StringUtils.split("a b c", ' ')    = ["a", "b", "c"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChar  the character used as the delimiter,
     *  <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String input
     * @since 2.0
     */
    public static String[] split(String str, char separatorChar) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List list = new ArrayList();
        int i = 0, start = 0;
        boolean match = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                }
                start = ++i;
                continue;
            }
            match = true;
            i++;
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * Splits the provided text into an array, separators specified.
     * This is an alternative to using StringTokenizer.
     *
     * The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     * For more control over the split use the Tokenizer class.
     *
     * A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separatorChars splits on whitespace.
     *
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("abc def", null) = ["abc", "def"]
     * StringUtils.split("abc def", " ")  = ["abc", "def"]
     * StringUtils.split("abc  def", " ") = ["abc", "def"]
     * StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChars  the characters used as the delimiters,
     *  <code>null</code> splits on whitespace
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] split(String str, String separatorChars) {
        return split(str, separatorChars, -1);
    }

    /**
     * Splits the provided text into an array with a maximum length,
     * separators specified.
     *
     * The separator is not included in the returned String array.
     * Adjacent separators are treated as one separator.
     *
     * A <code>null</code> input String returns <code>null</code>.
     * A <code>null</code> separatorChars splits on whitespace.
     *
     * If more than <code>max</code> delimited substrings are found, the last
     * returned string includes all characters after the first <code>max - 1</code>
     * returned strings (including separator characters).
     *
     * <pre>
     * StringUtils.split(null, *, *)            = null
     * StringUtils.split("", *, *)              = []
     * StringUtils.split("ab de fg", null, 0)   = ["ab", "cd", "ef"]
     * StringUtils.split("ab   de fg", null, 0) = ["ab", "cd", "ef"]
     * StringUtils.split("ab:cd:ef", ":", 0)    = ["ab", "cd", "ef"]
     * StringUtils.split("ab:cd:ef", ":", 2)    = ["ab", "cd:ef"]
     * </pre>
     *
     * @param str  the String to parse, may be null
     * @param separatorChars  the characters used as the delimiters,
     *  <code>null</code> splits on whitespace
     * @param max  the maximum number of elements to include in the
     *  array. A zero or negative value implies no limit
     * @return an array of parsed Strings, <code>null</code> if null String input
     */
    public static String[] split(String str, String separatorChars, int max) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List list = new ArrayList();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = len;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    // Joining
    //-----------------------------------------------------------------------
    /**
     * Concatenates elements of an array into a single String.
     * Null objects or empty strings within the array are represented by
     * empty strings.
     *
     * <pre>
     * StringUtils.concatenate(null)            = null
     * StringUtils.concatenate([])              = ""
     * StringUtils.concatenate([null])          = ""
     * StringUtils.concatenate(["a", "b", "c"]) = "abc"
     * StringUtils.concatenate([null, "", "a"]) = "a"
     * </pre>
     *
     * @param array  the array of values to concatenate, may be null
     * @return the concatenated String, <code>null</code> if null array input
     * @deprecated Use the better named {@link #join(Object[])} instead.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String concatenate(Object[] array) {
        return join(array, null);
    }

    /**
     * Joins the elements of the provided array into a single String
     * containing the provided list of elements.
     *
     * No separator is added to the joined String.
     * Null objects or empty strings within the array are represented by
     * empty strings.
     *
     * <pre>
     * StringUtils.join(null)            = null
     * StringUtils.join([])              = ""
     * StringUtils.join([null])          = ""
     * StringUtils.join(["a", "b", "c"]) = "abc"
     * StringUtils.join([null, "", "a"]) = "a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @return the joined String, <code>null</code> if null array input
     * @since 2.0
     */
    public static String join(Object[] array) {
        return join(array, null);
    }

    /**
     * Joins the elements of the provided array into a single String
     * containing the provided list of elements.
     *
     * No delimiter is added before or after the list.
     * Null objects or empty strings within the array are represented by
     * empty strings.
     *
     * <pre>
     * StringUtils.join(null, *)               = null
     * StringUtils.join([], *)                 = ""
     * StringUtils.join([null], *)             = ""
     * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
     * StringUtils.join(["a", "b", "c"], null) = "abc"
     * StringUtils.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use
     * @return the joined String, <code>null</code> if null array input
     * @since 2.0
     */
    public static String join(Object[] array, char separator) {
        if (array == null) {
            return null;
        }
        int arraySize = array.length;
        int bufSize = (arraySize == 0 ? 0 : ((array[0] == null ? 16 : array[0].toString().length()) + 1) * arraySize);
        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * Joins the elements of the provided array into a single String
     * containing the provided list of elements.
     *
     * No delimiter is added before or after the list.
     * A <code>null</code> separator is the same as an empty String ("").
     * Null objects or empty strings within the array are represented by
     * empty strings.
     *
     * <pre>
     * StringUtils.join(null, *)                = null
     * StringUtils.join([], *)                  = ""
     * StringUtils.join([null], *)              = ""
     * StringUtils.join(["a", "b", "c"], "--")  = "a--b--c"
     * StringUtils.join(["a", "b", "c"], null)  = "abc"
     * StringUtils.join(["a", "b", "c"], "")    = "abc"
     * StringUtils.join([null, "", "a"], ',')   = ",,a"
     * </pre>
     *
     * @param array  the array of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @return the joined String, <code>null</code> if null array input
     */
    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }
        int arraySize = array.length;

        // ArraySize ==  0: Len = 0
        // ArraySize > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        int bufSize =
            ((arraySize == 0)
                ? 0
                : arraySize
                    * ((array[0] == null ? 16 : array[0].toString().length())
                        + separator.length()));

        StringBuffer buf = new StringBuffer(bufSize);

        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    /**
     * Joins the elements of the provided <code>Iterator</code> into
     * a single String containing the provided elements.
     *
     * No delimiter is added before or after the list. Null objects or empty
     * strings within the iteration are represented by empty strings.
     *
     * See the examples here: {@link #join(Object[],char)}.
     *
     * @param iterator  the <code>Iterator</code> of values to join together, may be null
     * @param separator  the separator character to use
     * @return the joined String, <code>null</code> if null iterator input
     * @since 2.0
     */
    public static String join(Iterator iterator, char separator) {
        if (iterator == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(256); // Java default is 16, probably too small
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
            if (iterator.hasNext()) {
                buf.append(separator);
            }
        }
        return buf.toString();
    }

    /**
     * Joins the elements of the provided <code>Iterator</code> into
     * a single String containing the provided elements.
     *
     * No delimiter is added before or after the list.
     * A <code>null</code> separator is the same as an empty String ("").
     *
     * See the examples here: {@link #join(Object[],String)}.
     *
     * @param iterator  the <code>Iterator</code> of values to join together, may be null
     * @param separator  the separator character to use, null treated as ""
     * @return the joined String, <code>null</code> if null iterator input
     */
    public static String join(Iterator iterator, String separator) {
        if (iterator == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(256); // Java default is 16, probably too small
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
            if ((separator != null) && iterator.hasNext()) {
                buf.append(separator);
            }
        }
        return buf.toString();
    }

    // Delete
    //-----------------------------------------------------------------------
    /**
     * Deletes all 'space' characters from a String as defined by
     * {@link Character#isSpace(char)}.
     *
     * This is the only StringUtils method that uses the
     * <code>isSpace</code> definition. You are advised to use
     * {@link #deleteWhitespace(String)} instead as whitespace is much
     * better localized.
     *
     * <pre>
     * StringUtils.deleteSpaces(null)           = null
     * StringUtils.deleteSpaces("")             = ""
     * StringUtils.deleteSpaces("abc")          = "abc"
     * StringUtils.deleteSpaces(" \t  abc \n ") = "abc"
     * StringUtils.deleteSpaces("ab  c")        = "abc"
     * StringUtils.deleteSpaces("a\nb\tc     ") = "abc"
     * </pre>
     *
     * Spaces are defined as <code>{' ', '\t', '\r', '\n', '\b'}</code>
     * in line with the deprecated <code>isSpace</code> method.
     *
     * @param str  the String to delete spaces from, may be null
     * @return the String without 'spaces', <code>null</code> if null String input
     * @deprecated Use the better localized {@link #deleteWhitespace(String)}.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String deleteSpaces(String str) {
        if (str == null) {
            return null;
        }
        return CharSetUtils.delete(str, " \t\r\n\b");
    }

    /**
     * Deletes all whitespaces from a String as defined by
     * {@link Character#isWhitespace(char)}.
     *
     * <pre>
     * StringUtils.deleteWhitespace(null)         = null
     * StringUtils.deleteWhitespace("")           = ""
     * StringUtils.deleteWhitespace("abc")        = "abc"
     * StringUtils.deleteWhitespace("   ab  c  ") = "abc"
     * </pre>
     *
     * @param str  the String to delete whitespace from, may be null
     * @return the String without whitespaces, <code>null</code> if null String input
     */
    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int sz = str.length();
        char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }

    // Remove
    //-----------------------------------------------------------------------
    /**
     * Removes a substring only if it is at the begining of a source string,
     * otherwise returns the source string.
     *
     * A <code>null</code> source string will return <code>null</code>.
     * An empty ("") source string will return the empty string.
     * A <code>null</code> search string will return the source string.
     *
     * <pre>
     * StringUtils.removeStart(null, *)      = null
     * StringUtils.removeStart("", *)        = ""
     * StringUtils.removeStart(*, null)      = *
     * StringUtils.removeStart("www.domain.com", "www.")   = "domain.com"
     * StringUtils.removeStart("domain.com", "www.")       = "domain.com"
     * StringUtils.removeStart("www.domain.com", "domain") = "www.domain.com"
     * StringUtils.removeStart("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the String to search for and remove, may be null
     * @return the substring with the string removed if found,
     *  <code>null</code> if null String input
     * @since 2.1
     */
    public static String removeStart(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.startsWith(remove)){
            return str.substring(remove.length());
        }
        return str;
    }

    /**
     * Removes a substring only if it is at the end of a source string,
     * otherwise returns the source string.
     *
     * A <code>null</code> source string will return <code>null</code>.
     * An empty ("") source string will return the empty string.
     * A <code>null</code> search string will return the source string.
     *
     * <pre>
     * StringUtils.removeEnd(null, *)      = null
     * StringUtils.removeEnd("", *)        = ""
     * StringUtils.removeEnd(*, null)      = *
     * StringUtils.removeEnd("www.domain.com", ".com.")  = "www,domain"
     * StringUtils.removeEnd("www.domain.com", ".com")   = "www.domain"
     * StringUtils.removeEnd("www.domain.com", "domain") = "www.domain.com"
     * StringUtils.removeEnd("abc", "")    = "abc"
     * </pre>
     *
     * @param str  the source String to search, may be null
     * @param remove  the String to search for and remove, may be null
     * @return the substring with the string removed if found,
     *  <code>null</code> if null String input
     * @since 2.1
     */
    public static String removeEnd(String str, String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    // Replacing
    //-----------------------------------------------------------------------
    /**
     * Replaces a String with another String inside a larger String, once.
     *
     * A <code>null</code> reference passed to this method is a no-op.
     *
     * <pre>
     * StringUtils.replaceOnce(null, *, *)        = null
     * StringUtils.replaceOnce("", *, *)          = ""
     * StringUtils.replaceOnce("any", null, *)    = "any"
     * StringUtils.replaceOnce("any", *, null)    = "any"
     * StringUtils.replaceOnce("any", "", *)      = "any"
     * StringUtils.replaceOnce("aba", "a", null)  = "aba"
     * StringUtils.replaceOnce("aba", "a", "")    = "ba"
     * StringUtils.replaceOnce("aba", "a", "z")   = "zba"
     * </pre>
     *
     * @see #replace(String text, String repl, String with, int max)
     * @param text  text to search and replace in, may be null
     * @param repl  the String to search for, may be null
     * @param with  the String to replace with, may be null
     * @return the text with any replacements processed,
     *  <code>null</code> if null String input
     */
    public static String replaceOnce(String text, String repl, String with) {
        return replace(text, repl, with, 1);
    }

    /**
     * Replaces all occurrences of a String within another String.
     *
     * A <code>null</code> reference passed to this method is a no-op.
     *
     * <pre>
     * StringUtils.replace(null, *, *)        = null
     * StringUtils.replace("", *, *)          = ""
     * StringUtils.replace("any", null, *)    = "any"
     * StringUtils.replace("any", *, null)    = "any"
     * StringUtils.replace("any", "", *)      = "any"
     * StringUtils.replace("aba", "a", null)  = "aba"
     * StringUtils.replace("aba", "a", "")    = "b"
     * StringUtils.replace("aba", "a", "z")   = "zbz"
     * </pre>
     *
     * @see #replace(String text, String repl, String with, int max)
     * @param text  text to search and replace in, may be null
     * @param repl  the String to search for, may be null
     * @param with  the String to replace with, may be null
     * @return the text with any replacements processed,
     *  <code>null</code> if null String input
     */
    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    /**
     * Replaces a String with another String inside a larger String,
     * for the first <code>max</code> values of the search String.
     *
     * A <code>null</code> reference passed to this method is a no-op.
     *
     * <pre>
     * StringUtils.replace(null, *, *, *)         = null
     * StringUtils.replace("", *, *, *)           = ""
     * StringUtils.replace("any", null, *, *)     = "any"
     * StringUtils.replace("any", *, null, *)     = "any"
     * StringUtils.replace("any", "", *, *)       = "any"
     * StringUtils.replace("any", *, *, 0)        = "any"
     * StringUtils.replace("abaa", "a", null, -1) = "abaa"
     * StringUtils.replace("abaa", "a", "", -1)   = "b"
     * StringUtils.replace("abaa", "a", "z", 0)   = "abaa"
     * StringUtils.replace("abaa", "a", "z", 1)   = "zbaa"
     * StringUtils.replace("abaa", "a", "z", 2)   = "zbza"
     * StringUtils.replace("abaa", "a", "z", -1)  = "zbzz"
     * </pre>
     *
     * @param text  text to search and replace in, may be null
     * @param repl  the String to search for, may be null
     * @param with  the String to replace with, may be null
     * @param max  maximum number of values to replace, or <code>-1</code> if no maximum
     * @return the text with any replacements processed,
     *  <code>null</code> if null String input
     */
    public static String replace(String text, String repl, String with, int max) {
        if (text == null || isEmpty(repl) || with == null || max == 0) {
            return text;
        }

        StringBuffer buf = new StringBuffer(text.length());
        int start = 0, end = 0;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();

            if (--max == 0) {
                break;
            }
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    // Replace, character based
    //-----------------------------------------------------------------------
    /**
     * Replaces all occurrences of a character in a String with another.
     * This is a null-safe version of {@link String#replace(char, char)}.
     *
     * A <code>null</code> string input returns <code>null</code>.
     * An empty ("") string input returns an empty string.
     *
     * <pre>
     * StringUtils.replaceChars(null, *, *)        = null
     * StringUtils.replaceChars("", *, *)          = ""
     * StringUtils.replaceChars("abcba", 'b', 'y') = "aycya"
     * StringUtils.replaceChars("abcba", 'z', 'y') = "abcba"
     * </pre>
     *
     * @param str  String to replace characters in, may be null
     * @param searchChar  the character to search for, may be null
     * @param replaceChar  the character to replace, may be null
     * @return modified String, <code>null</code> if null string input
     * @since 2.0
     */
    public static String replaceChars(String str, char searchChar, char replaceChar) {
        if (str == null) {
            return null;
        }
        return str.replace(searchChar, replaceChar);
    }

    /**
     * Replaces multiple characters in a String in one go.
     * This method can also be used to delete characters.
     *
     * For example:<br />
     * <code>replaceChars(&quot;hello&quot;, &quot;ho&quot;, &quot;jy&quot;) = jelly</code>.
     *
     * A <code>null</code> string input returns <code>null</code>.
     * An empty ("") string input returns an empty string.
     * A null or empty set of search characters returns the input string.
     *
     * The length of the search characters should normally equal the length
     * of the replace characters.
     * If the search characters is longer, then the extra search characters
     * are deleted.
     * If the search characters is shorter, then the extra replace characters
     * are ignored.
     *
     * <pre>
     * StringUtils.replaceChars(null, *, *)           = null
     * StringUtils.replaceChars("", *, *)             = ""
     * StringUtils.replaceChars("abc", null, *)       = "abc"
     * StringUtils.replaceChars("abc", "", *)         = "abc"
     * StringUtils.replaceChars("abc", "b", null)     = "ac"
     * StringUtils.replaceChars("abc", "b", "")       = "ac"
     * StringUtils.replaceChars("abcba", "bc", "yz")  = "ayzya"
     * StringUtils.replaceChars("abcba", "bc", "y")   = "ayya"
     * StringUtils.replaceChars("abcba", "bc", "yzx") = "ayzya"
     * </pre>
     *
     * @param str  String to replace characters in, may be null
     * @param searchChars  a set of characters to search for, may be null
     * @param replaceChars  a set of characters to replace, may be null
     * @return modified String, <code>null</code> if null string input
     * @since 2.0
     */
    public static String replaceChars(String str, String searchChars, String replaceChars) {
        if (isEmpty(str) || isEmpty(searchChars)) {
            return str;
        }
        if (replaceChars == null) {
            replaceChars = "";
        }
        boolean modified = false;
        StringBuffer buf = new StringBuffer(str.length());
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            int index = searchChars.indexOf(ch);
            if (index >= 0) {
                modified = true;
                if (index < replaceChars.length()) {
                    buf.append(replaceChars.charAt(index));
                }
            } else {
                buf.append(ch);
            }
        }
        if (modified) {
            return buf.toString();
        } else {
            return str;
        }
    }

    // Overlay
    //-----------------------------------------------------------------------
    /**
     * Overlays part of a String with another String.
     *
     * <pre>
     * StringUtils.overlayString(null, *, *, *)           = NullPointerException
     * StringUtils.overlayString(*, null, *, *)           = NullPointerException
     * StringUtils.overlayString("", "abc", 0, 0)         = "abc"
     * StringUtils.overlayString("abcdef", null, 2, 4)    = "abef"
     * StringUtils.overlayString("abcdef", "", 2, 4)      = "abef"
     * StringUtils.overlayString("abcdef", "zzzz", 2, 4)  = "abzzzzef"
     * StringUtils.overlayString("abcdef", "zzzz", 4, 2)  = "abcdzzzzcdef"
     * StringUtils.overlayString("abcdef", "zzzz", -1, 4) = IndexOutOfBoundsException
     * StringUtils.overlayString("abcdef", "zzzz", 2, 8)  = IndexOutOfBoundsException
     * </pre>
     *
     * @param text  the String to do overlaying in, may be null
     * @param overlay  the String to overlay, may be null
     * @param start  the position to start overlaying at, must be valid
     * @param end  the position to stop overlaying before, must be valid
     * @return overlayed String, <code>null</code> if null String input
     * @throws NullPointerException if text or overlay is null
     * @throws IndexOutOfBoundsException if either position is invalid
     * @deprecated Use better named {@link #overlay(String, String, int, int)} instead.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String overlayString(String text, String overlay, int start, int end) {
        return new StringBuffer(start + overlay.length() + text.length() - end + 1)
            .append(text.substring(0, start))
            .append(overlay)
            .append(text.substring(end))
            .toString();
    }

    /**
     * Overlays part of a String with another String.
     *
     * A <code>null</code> string input returns <code>null</code>.
     * A negative index is treated as zero.
     * An index greater than the string length is treated as the string length.
     * The start index is always the smaller of the two indices.
     *
     * <pre>
     * StringUtils.overlay(null, *, *, *)            = null
     * StringUtils.overlay("", "abc", 0, 0)          = "abc"
     * StringUtils.overlay("abcdef", null, 2, 4)     = "abef"
     * StringUtils.overlay("abcdef", "", 2, 4)       = "abef"
     * StringUtils.overlay("abcdef", "", 4, 2)       = "abef"
     * StringUtils.overlay("abcdef", "zzzz", 2, 4)   = "abzzzzef"
     * StringUtils.overlay("abcdef", "zzzz", 4, 2)   = "abzzzzef"
     * StringUtils.overlay("abcdef", "zzzz", -1, 4)  = "zzzzef"
     * StringUtils.overlay("abcdef", "zzzz", 2, 8)   = "abzzzz"
     * StringUtils.overlay("abcdef", "zzzz", -2, -3) = "zzzzabcdef"
     * StringUtils.overlay("abcdef", "zzzz", 8, 10)  = "abcdefzzzz"
     * </pre>
     *
     * @param str  the String to do overlaying in, may be null
     * @param overlay  the String to overlay, may be null
     * @param start  the position to start overlaying at
     * @param end  the position to stop overlaying before
     * @return overlayed String, <code>null</code> if null String input
     * @since 2.0
     */
    public static String overlay(String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }
        if (overlay == null) {
            overlay = EMPTY;
        }
        int len = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > len) {
            start = len;
        }
        if (end < 0) {
            end = 0;
        }
        if (end > len) {
            end = len;
        }
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        return new StringBuffer(len + start - end + overlay.length() + 1)
            .append(str.substring(0, start))
            .append(overlay)
            .append(str.substring(end))
            .toString();
    }

    // Chomping
    //-----------------------------------------------------------------------
    /**
     * Removes one newline from end of a String if it's there,
     * otherwise leave it alone.  A newline is &quot;<code>\n</code>&quot;,
     * &quot;<code>\r</code>&quot;, or &quot;<code>\r\n</code>&quot;.
     *
     * NOTE: This method changed in 2.0.
     * It now more closely matches Perl chomp.
     *
     * <pre>
     * StringUtils.chomp(null)          = null
     * StringUtils.chomp("")            = ""
     * StringUtils.chomp("abc \r")      = "abc "
     * StringUtils.chomp("abc\n")       = "abc"
     * StringUtils.chomp("abc\r\n")     = "abc"
     * StringUtils.chomp("abc\r\n\r\n") = "abc\r\n"
     * StringUtils.chomp("abc\n\r")     = "abc\n"
     * StringUtils.chomp("abc\n\rabc")  = "abc\n\rabc"
     * StringUtils.chomp("\r")          = ""
     * StringUtils.chomp("\n")          = ""
     * StringUtils.chomp("\r\n")        = ""
     * </pre>
     *
     * @param str  the String to chomp a newline from, may be null
     * @return String without newline, <code>null</code> if null String input
     */
    public static String chomp(String str) {
        if (isEmpty(str)) {
            return str;
        }

        if (str.length() == 1) {
            char ch = str.charAt(0);
            if (ch == '\r' || ch == '\n') {
                return EMPTY;
            } else {
                return str;
            }
        }

        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);

        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
        } else if (last == '\r') {
            // why is this block empty?
            // just to skip incrementing the index?
        } else {
            lastIdx++;
        }
        return str.substring(0, lastIdx);
    }

    /**
     * Removes <code>separator</code> from the end of
     * <code>str</code> if it's there, otherwise leave it alone.
     *
     * NOTE: This method changed in version 2.0.
     * It now more closely matches Perl chomp.
     * For the previous behavior, use {@link #substringBeforeLast(String, String)}.
     * This method uses {@link String#endsWith(String)}.
     *
     * <pre>
     * StringUtils.chomp(null, *)         = null
     * StringUtils.chomp("", *)           = ""
     * StringUtils.chomp("foobar", "bar") = "foo"
     * StringUtils.chomp("foobar", "baz") = "foobar"
     * StringUtils.chomp("foo", "foo")    = ""
     * StringUtils.chomp("foo ", "foo")   = "foo"
     * StringUtils.chomp(" foo", "foo")   = " "
     * StringUtils.chomp("foo", "foooo")  = "foo"
     * StringUtils.chomp("foo", "")       = "foo"
     * StringUtils.chomp("foo", null)     = "foo"
     * </pre>
     *
     * @param str  the String to chomp from, may be null
     * @param separator  separator String, may be null
     * @return String without trailing separator, <code>null</code> if null String input
     */
    public static String chomp(String str, String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (str.endsWith(separator)) {
            return str.substring(0, str.length() - separator.length());
        }
        return str;
    }

    /**
     * Remove any &quot;\n&quot; if and only if it is at the end
     * of the supplied String.
     *
     * @param str  the String to chomp from, must not be null
     * @return String without chomped ending
     * @throws NullPointerException if str is <code>null</code>
     * @deprecated Use {@link #chomp(String)} instead.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String chompLast(String str) {
        return chompLast(str, "\n");
    }

    /**
     * Remove a value if and only if the String ends with that value.
     *
     * @param str  the String to chomp from, must not be null
     * @param sep  the String to chomp, must not be null
     * @return String without chomped ending
     * @throws NullPointerException if str or sep is <code>null</code>
     * @deprecated Use {@link #chomp(String,String)} instead.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String chompLast(String str, String sep) {
        if (str.length() == 0) {
            return str;
        }
        String sub = str.substring(str.length() - sep.length());
        if (sep.equals(sub)) {
            return str.substring(0, str.length() - sep.length());
        } else {
            return str;
        }
    }

    /**
     * Remove everything and return the last value of a supplied String, and
     * everything after it from a String.
     *
     * @param str  the String to chomp from, must not be null
     * @param sep  the String to chomp, must not be null
     * @return String chomped
     * @throws NullPointerException if str or sep is <code>null</code>
     * @deprecated Use {@link #substringAfterLast(String, String)} instead
     *             (although this doesn't include the separator)
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String getChomp(String str, String sep) {
        int idx = str.lastIndexOf(sep);
        if (idx == str.length() - sep.length()) {
            return sep;
        } else if (idx != -1) {
            return str.substring(idx);
        } else {
            return EMPTY;
        }
    }

    /**
     * Remove the first value of a supplied String, and everything before it
     * from a String.
     *
     * @param str  the String to chomp from, must not be null
     * @param sep  the String to chomp, must not be null
     * @return String without chomped beginning
     * @throws NullPointerException if str or sep is <code>null</code>
     * @deprecated Use {@link #substringAfter(String,String)} instead.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String prechomp(String str, String sep) {
        int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(idx + sep.length());
        } else {
            return str;
        }
    }

    /**
     * Remove and return everything before the first value of a
     * supplied String from another String.
     *
     * @param str  the String to chomp from, must not be null
     * @param sep  the String to chomp, must not be null
     * @return String prechomped
     * @throws NullPointerException if str or sep is <code>null</code>
     * @deprecated Use {@link #substringBefore(String,String)} instead
     *             (although this doesn't include the separator).
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String getPrechomp(String str, String sep) {
        int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(0, idx + sep.length());
        } else {
            return EMPTY;
        }
    }

    // Chopping
    //-----------------------------------------------------------------------
    /**
     * Remove the last character from a String.
     *
     * If the String ends in <code>\r\n</code>, then remove both
     * of them.
     *
     * <pre>
     * StringUtils.chop(null)          = null
     * StringUtils.chop("")            = ""
     * StringUtils.chop("abc \r")      = "abc "
     * StringUtils.chop("abc\n")       = "abc"
     * StringUtils.chop("abc\r\n")     = "abc"
     * StringUtils.chop("abc")         = "ab"
     * StringUtils.chop("abc\nabc")    = "abc\nab"
     * StringUtils.chop("a")           = ""
     * StringUtils.chop("\r")          = ""
     * StringUtils.chop("\n")          = ""
     * StringUtils.chop("\r\n")        = ""
     * </pre>
     *
     * @param str  the String to chop last character from, may be null
     * @return String without last character, <code>null</code> if null String input
     */
    public static String chop(String str) {
        if (str == null) {
            return null;
        }
        int strLen = str.length();
        if (strLen < 2) {
            return EMPTY;
        }
        int lastIdx = strLen - 1;
        String ret = str.substring(0, lastIdx);
        char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (ret.charAt(lastIdx - 1) == '\r') {
                return ret.substring(0, lastIdx - 1);
            }
        }
        return ret;
    }

    /**
     * Removes <code>\n</code> from end of a String if it's there.
     * If a <code>\r</code> precedes it, then remove that too.
     *
     * @param str  the String to chop a newline from, must not be null
     * @return String without newline
     * @throws NullPointerException if str is <code>null</code>
     * @deprecated Use {@link #chomp(String)} instead.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String chopNewline(String str) {
        int lastIdx = str.length() - 1;
        if (lastIdx <= 0) {
            return EMPTY;
        }
        char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
        } else {
            lastIdx++;
        }
        return str.substring(0, lastIdx);
    }

    // Conversion
    //-----------------------------------------------------------------------
    /**
     * Escapes any values it finds into their String form.
     *
     * So a tab becomes the characters <code>'\\'</code> and
     * <code>'t'</code>.
     *
     * As of Lang 2.0, this calls {@link StringEscapeUtils#escapeJava(String)}
     * behind the scenes.
     *
     * @see StringEscapeUtils#escapeJava(String)
     * @param str String to escape values in
     * @return String with escaped values
     * @throws NullPointerException if str is <code>null</code>
     * @deprecated Use {@link StringEscapeUtils#escapeJava(String)}
     *             This method will be removed in Commons Lang 3.0
     */
    public static String escape(String str) {
        return StringEscapeUtils.escapeJava(str);
    }

    // Padding
    //-----------------------------------------------------------------------
    /**
     * Repeat a String <code>repeat</code> times to form a
     * new String.
     *
     * <pre>
     * StringUtils.repeat(null, 2) = null
     * StringUtils.repeat("", 0)   = ""
     * StringUtils.repeat("", 2)   = ""
     * StringUtils.repeat("a", 3)  = "aaa"
     * StringUtils.repeat("ab", 2) = "abab"
     * StringUtils.repeat("a", -2) = ""
     * </pre>
     *
     * @param str  the String to repeat, may be null
     * @param repeat  number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated,
     *  <code>null</code> if null String input
     */
    public static String repeat(String str, int repeat) {
        // Performance tuned for 2.0 (JDK1.4)

        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return EMPTY;
        }
        int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return padding(repeat, str.charAt(0));
        }

        int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1 :
                char ch = str.charAt(0);
                char[] output1 = new char[outputLength];
                for (int i = repeat - 1; i >= 0; i--) {
                    output1[i] = ch;
                }
                return new String(output1);
            case 2 :
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--, i--) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                }
                return new String(output2);
            default :
                StringBuffer buf = new StringBuffer(outputLength);
                for (int i = 0; i < repeat; i++) {
                    buf.append(str);
                }
                return buf.toString();
        }
    }

    /**
     * Returns padding using the specified delimiter repeated
     * to a given length.
     *
     * <pre>
     * StringUtils.padding(0, 'e')  = ""
     * StringUtils.padding(3, 'e')  = "eee"
     * StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
     * </pre>
     *
     * @param repeat  number of times to repeat delim
     * @param padChar  character to repeat
     * @return String with repeated character
     * @throws IndexOutOfBoundsException if <code>repeat &lt; 0</code>
     */
    private static String padding(int repeat, char padChar) {
        // be careful of synchronization in this method
        // we are assuming that get and set from an array index is atomic
        String pad = PADDING[padChar];
        if (pad == null) {
            pad = String.valueOf(padChar);
        }
        while (pad.length() < repeat) {
            pad = pad.concat(pad);
        }
        PADDING[padChar] = pad;
        return pad.substring(0, repeat);
    }

    /**
     * Right pad a String with spaces (' ').
     *
     * The String is padded to the size of <code>size</code>.
     *
     * <pre>
     * StringUtils.rightPad(null, *)   = null
     * StringUtils.rightPad("", 3)     = "   "
     * StringUtils.rightPad("bat", 3)  = "bat"
     * StringUtils.rightPad("bat", 5)  = "bat  "
     * StringUtils.rightPad("bat", 1)  = "bat"
     * StringUtils.rightPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @return right padded String or original String if no padding is necessary,
     *  <code>null</code> if null String input
     */
    public static String rightPad(String str, int size) {
        return rightPad(str, size, ' ');
    }

    /**
     * Right pad a String with a specified character.
     *
     * The String is padded to the size of <code>size</code>.
     *
     * <pre>
     * StringUtils.rightPad(null, *, *)     = null
     * StringUtils.rightPad("", 3, 'z')     = "zzz"
     * StringUtils.rightPad("bat", 3, 'z')  = "bat"
     * StringUtils.rightPad("bat", 5, 'z')  = "batzz"
     * StringUtils.rightPad("bat", 1, 'z')  = "bat"
     * StringUtils.rightPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padChar  the character to pad with
     * @return right padded String or original String if no padding is necessary,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(padding(pads, padChar));
    }

    /**
     * Right pad a String with a specified String.
     *
     * The String is padded to the size of <code>size</code>.
     *
     * <pre>
     * StringUtils.rightPad(null, *, *)      = null
     * StringUtils.rightPad("", 3, "z")      = "zzz"
     * StringUtils.rightPad("bat", 3, "yz")  = "bat"
     * StringUtils.rightPad("bat", 5, "yz")  = "batyz"
     * StringUtils.rightPad("bat", 8, "yz")  = "batyzyzy"
     * StringUtils.rightPad("bat", 1, "yz")  = "bat"
     * StringUtils.rightPad("bat", -1, "yz") = "bat"
     * StringUtils.rightPad("bat", 5, null)  = "bat  "
     * StringUtils.rightPad("bat", 5, "")    = "bat  "
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padStr  the String to pad with, null or empty treated as single space
     * @return right padded String or original String if no padding is necessary,
     *  <code>null</code> if null String input
     */
    public static String rightPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return rightPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    /**
     * Left pad a String with spaces (' ').
     *
     * The String is padded to the size of <code>size<code>.
     *
     * <pre>
     * StringUtils.leftPad(null, *)   = null
     * StringUtils.leftPad("", 3)     = "   "
     * StringUtils.leftPad("bat", 3)  = "bat"
     * StringUtils.leftPad("bat", 5)  = "  bat"
     * StringUtils.leftPad("bat", 1)  = "bat"
     * StringUtils.leftPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @return left padded String or original String if no padding is necessary,
     *  <code>null</code> if null String input
     */
    public static String leftPad(String str, int size) {
        return leftPad(str, size, ' ');
    }

    /**
     * Left pad a String with a specified character.
     *
     * Pad to a size of <code>size</code>.
     *
     * <pre>
     * StringUtils.leftPad(null, *, *)     = null
     * StringUtils.leftPad("", 3, 'z')     = "zzz"
     * StringUtils.leftPad("bat", 3, 'z')  = "bat"
     * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
     * StringUtils.leftPad("bat", 1, 'z')  = "bat"
     * StringUtils.leftPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padChar  the character to pad with
     * @return left padded String or original String if no padding is necessary,
     *  <code>null</code> if null String input
     * @since 2.0
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return padding(pads, padChar).concat(str);
    }

    /**
     * Left pad a String with a specified String.
     *
     * Pad to a size of <code>size</code>.
     *
     * <pre>
     * StringUtils.leftPad(null, *, *)      = null
     * StringUtils.leftPad("", 3, "z")      = "zzz"
     * StringUtils.leftPad("bat", 3, "yz")  = "bat"
     * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
     * StringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
     * StringUtils.leftPad("bat", 1, "yz")  = "bat"
     * StringUtils.leftPad("bat", -1, "yz") = "bat"
     * StringUtils.leftPad("bat", 5, null)  = "  bat"
     * StringUtils.leftPad("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padStr  the String to pad with, null or empty treated as single space
     * @return left padded String or original String if no padding is necessary,
     *  <code>null</code> if null String input
     */
    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            char[] padding = new char[pads];
            char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    // Centering
    //-----------------------------------------------------------------------
    /**
     * Centers a String in a larger String of size <code>size</code>
     * using the space character (' ').
     *
     * If the size is less than the String length, the String is returned.
     * A <code>null</code> String returns <code>null</code>.
     * A negative size is treated as zero.
     *
     * Equivalent to <code>center(str, size, " ")</code>.
     *
     * <pre>
     * StringUtils.center(null, *)   = null
     * StringUtils.center("", 4)     = "    "
     * StringUtils.center("ab", -1)  = "ab"
     * StringUtils.center("ab", 4)   = " ab "
     * StringUtils.center("abcd", 2) = "abcd"
     * StringUtils.center("a", 4)    = " a  "
     * </pre>
     *
     * @param str  the String to center, may be null
     * @param size  the int size of new String, negative treated as zero
     * @return centered String, <code>null</code> if null String input
     */
    public static String center(String str, int size) {
        return center(str, size, ' ');
    }

    /**
     * Centers a String in a larger String of size <code>size</code>.
     * Uses a supplied character as the value to pad the String with.
     *
     * If the size is less than the String length, the String is returned.
     * A <code>null</code> String returns <code>null</code>.
     * A negative size is treated as zero.
     *
     * <pre>
     * StringUtils.center(null, *, *)     = null
     * StringUtils.center("", 4, ' ')     = "    "
     * StringUtils.center("ab", -1, ' ')  = "ab"
     * StringUtils.center("ab", 4, ' ')   = " ab"
     * StringUtils.center("abcd", 2, ' ') = "abcd"
     * StringUtils.center("a", 4, ' ')    = " a  "
     * StringUtils.center("a", 4, 'y')    = "yayy"
     * </pre>
     *
     * @param str  the String to center, may be null
     * @param size  the int size of new String, negative treated as zero
     * @param padChar  the character to pad the new String with
     * @return centered String, <code>null</code> if null String input
     * @since 2.0
     */
    public static String center(String str, int size, char padChar) {
        if (str == null || size <= 0) {
            return str;
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padChar);
        str = rightPad(str, size, padChar);
        return str;
    }

    /**
     * Centers a String in a larger String of size <code>size</code>.
     * Uses a supplied String as the value to pad the String with.
     *
     * If the size is less than the String length, the String is returned.
     * A <code>null</code> String returns <code>null</code>.
     * A negative size is treated as zero.
     *
     * <pre>
     * StringUtils.center(null, *, *)     = null
     * StringUtils.center("", 4, " ")     = "    "
     * StringUtils.center("ab", -1, " ")  = "ab"
     * StringUtils.center("ab", 4, " ")   = " ab"
     * StringUtils.center("abcd", 2, " ") = "abcd"
     * StringUtils.center("a", 4, " ")    = " a  "
     * StringUtils.center("a", 4, "yz")   = "yayz"
     * StringUtils.center("abc", 7, null) = "  abc  "
     * StringUtils.center("abc", 7, "")   = "  abc  "
     * </pre>
     *
     * @param str  the String to center, may be null
     * @param size  the int size of new String, negative treated as zero
     * @param padStr  the String to pad the new String with, must not be null or empty
     * @return centered String, <code>null</code> if null String input
     * @throws IllegalArgumentException if padStr is <code>null</code> or empty
     */
    public static String center(String str, int size, String padStr) {
        if (str == null || size <= 0) {
            return str;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padStr);
        str = rightPad(str, size, padStr);
        return str;
    }

    // Case conversion
    //-----------------------------------------------------------------------
    /**
     * Converts a String to upper case as per {@link String#toUpperCase()}.
     *
     * A <code>null</code> input String returns <code>null</code>.
     *
     * <pre>
     * StringUtils.upperCase(null)  = null
     * StringUtils.upperCase("")    = ""
     * StringUtils.upperCase("aBc") = "ABC"
     * </pre>
     *
     * @param str  the String to upper case, may be null
     * @return the upper cased String, <code>null</code> if null String input
     */
    public static String upperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    /**
     * Converts a String to lower case as per {@link String#toLowerCase()}.
     *
     * A <code>null</code> input String returns <code>null</code>.
     *
     * <pre>
     * StringUtils.lowerCase(null)  = null
     * StringUtils.lowerCase("")    = ""
     * StringUtils.lowerCase("aBc") = "abc"
     * </pre>
     *
     * @param str  the String to lower case, may be null
     * @return the lower cased String, <code>null</code> if null String input
     */
    public static String lowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    /**
     * Capitalizes a String changing the first letter to title case as
     * per {@link Character#toTitleCase(char)}. No other letters are changed.
     *
     * For a word based algorithm, see {@link WordUtils#capitalize(String)}.
     * A <code>null</code> input String returns <code>null</code>.
     *
     * <pre>
     * StringUtils.capitalize(null)  = null
     * StringUtils.capitalize("")    = ""
     * StringUtils.capitalize("cat") = "Cat"
     * StringUtils.capitalize("cAt") = "CAt"
     * </pre>
     *
     * @param str  the String to capitalize, may be null
     * @return the capitalized String, <code>null</code> if null String input
     * @see WordUtils#capitalize(String)
     * @see #uncapitalize(String)
     * @since 2.0
     */
    public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen)
            .append(Character.toTitleCase(str.charAt(0)))
            .append(str.substring(1))
            .toString();
    }

    /**
     * Capitalizes a String changing the first letter to title case as
     * per {@link Character#toTitleCase(char)}. No other letters are changed.
     *
     * @param str  the String to capitalize, may be null
     * @return the capitalized String, <code>null</code> if null String input
     * @deprecated Use the standardly named {@link #capitalize(String)}.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String capitalise(String str) {
        return capitalize(str);
    }

    /**
     * Uncapitalizes a String changing the first letter to title case as
     * per {@link Character#toLowerCase(char)}. No other letters are changed.
     *
     * For a word based algorithm, see {@link WordUtils#uncapitalize(String)}.
     * A <code>null</code> input String returns <code>null</code>.
     *
     * <pre>
     * StringUtils.uncapitalize(null)  = null
     * StringUtils.uncapitalize("")    = ""
     * StringUtils.uncapitalize("Cat") = "cat"
     * StringUtils.uncapitalize("CAT") = "cAT"
     * </pre>
     *
     * @param str  the String to uncapitalize, may be null
     * @return the uncapitalized String, <code>null</code> if null String input
     * @see #capitalize(String)
     * @since 2.0
     */
    public static String uncapitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StringBuffer(strLen)
            .append(Character.toLowerCase(str.charAt(0)))
            .append(str.substring(1))
            .toString();
    }

    /**
     * Uncapitalizes a String changing the first letter to title case as
     * per {@link Character#toLowerCase(char)}. No other letters are changed.
     *
     * @param str  the String to uncapitalize, may be null
     * @return the uncapitalized String, <code>null</code> if null String input
     * @deprecated Use the standardly named {@link #uncapitalize(String)}.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String uncapitalise(String str) {
        return uncapitalize(str);
    }

    /**
     * Swaps the case of a String changing upper and title case to
     * lower case, and lower case to upper case.
     *
     *
     *  Upper case character converts to Lower case
     *  Title case character converts to Lower case
     *  Lower case character converts to Upper case
     *
     *
     * For a word based algorithm, see {@link WordUtils#swapCase(String)}.
     * A <code>null</code> input String returns <code>null</code>.
     *
     * <pre>
     * StringUtils.swapCase(null)                 = null
     * StringUtils.swapCase("")                   = ""
     * StringUtils.swapCase("The dog has a BONE") = "tHE DOG HAS A bone"
     * </pre>
     *
     * NOTE: This method changed in Lang version 2.0.
     * It no longer performs a word based algorithm.
     * If you only use ASCII, you will notice no change.
     * That functionality is available in WordUtils.
     *
     * @param str  the String to swap case, may be null
     * @return the changed String, <code>null</code> if null String input
     */
    public static String swapCase(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        StringBuffer buffer = new StringBuffer(strLen);

        char ch = 0;
        for (int i = 0; i < strLen; i++) {
            ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                ch = Character.toUpperCase(ch);
            }
            buffer.append(ch);
        }
        return buffer.toString();
    }

    /**
     * Capitalizes all the whitespace separated words in a String.
     * Only the first letter of each word is changed.
     *
     * Whitespace is defined by {@link Character#isWhitespace(char)}.
     * A <code>null</code> input String returns <code>null</code>.
     *
     * @param str  the String to capitalize, may be null
     * @return capitalized String, <code>null</code> if null String input
     * @deprecated Use the relocated {@link WordUtils#capitalize(String)}.
     *             Method will be removed in Commons Lang 3.0.
     */
    public static String capitaliseAllWords(String str) {
        return WordUtils.capitalize(str);
    }

    // Count matches
    //-----------------------------------------------------------------------
    /**
     * Counts how many times the substring appears in the larger String.
     *
     * A <code>null</code> or empty ("") String input returns <code>0</code>.
     *
     * <pre>
     * StringUtils.countMatches(null, *)       = 0
     * StringUtils.countMatches("", *)         = 0
     * StringUtils.countMatches("abba", null)  = 0
     * StringUtils.countMatches("abba", "")    = 0
     * StringUtils.countMatches("abba", "a")   = 2
     * StringUtils.countMatches("abba", "ab")  = 1
     * StringUtils.countMatches("abba", "xxx") = 0
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param sub  the substring to count, may be null
     * @return the number of occurrences, 0 if either String is <code>null</code>
     */
    public static int countMatches(String str, String sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    public static int toInt(String str,int defaultvalue) {
        int reint=0;
    	if (isEmpty(str)) {
    		reint= defaultvalue;
        }
        try {
        	reint = Integer.parseInt(str);
        } catch (NumberFormatException ne) {
        	reint= defaultvalue;
        }

        return reint;
    }

    // Character Tests
    //-----------------------------------------------------------------------
    /**
     * Checks if the String contains only unicode letters.
     *
     * <code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.
     *
     * <pre>
     * StringUtils.isAlpha(null)   = false
     * StringUtils.isAlpha("")     = true
     * StringUtils.isAlpha("  ")   = false
     * StringUtils.isAlpha("abc")  = true
     * StringUtils.isAlpha("ab2c") = false
     * StringUtils.isAlpha("ab-c") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains letters, and is non-null
     */
    public static boolean isAlpha(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetter(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the String contains only unicode letters and
     * space (' ').
     *
     * <code>null</code> will return <code>false</code>
     * An empty String ("") will return <code>true</code>.
     *
     * <pre>
     * StringUtils.isAlphaSpace(null)   = false
     * StringUtils.isAlphaSpace("")     = true
     * StringUtils.isAlphaSpace("  ")   = true
     * StringUtils.isAlphaSpace("abc")  = true
     * StringUtils.isAlphaSpace("ab c") = true
     * StringUtils.isAlphaSpace("ab2c") = false
     * StringUtils.isAlphaSpace("ab-c") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains letters and space,
     *  and is non-null
     */
    public static boolean isAlphaSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isLetter(str.charAt(i)) == false) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the String contains only unicode letters or digits.
     *
     * <code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.
     *
     * <pre>
     * StringUtils.isAlphanumeric(null)   = false
     * StringUtils.isAlphanumeric("")     = true
     * StringUtils.isAlphanumeric("  ")   = false
     * StringUtils.isAlphanumeric("abc")  = true
     * StringUtils.isAlphanumeric("ab c") = false
     * StringUtils.isAlphanumeric("ab2c") = true
     * StringUtils.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains letters or digits,
     *  and is non-null
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetterOrDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the String contains only unicode letters, digits
     * or space (<code>' '</code>).
     *
     * <code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.
     *
     * <pre>
     * StringUtils.isAlphanumeric(null)   = false
     * StringUtils.isAlphanumeric("")     = true
     * StringUtils.isAlphanumeric("  ")   = true
     * StringUtils.isAlphanumeric("abc")  = true
     * StringUtils.isAlphanumeric("ab c") = true
     * StringUtils.isAlphanumeric("ab2c") = true
     * StringUtils.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains letters, digits or space,
     *  and is non-null
     */
    public static boolean isAlphanumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isLetterOrDigit(str.charAt(i)) == false) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the string contains only ASCII printable characters.
     *
     * <code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.
     *
     * <pre>
     * StringUtils.isAsciiPrintable(null)     = false
     * StringUtils.isAsciiPrintable("")       = true
     * StringUtils.isAsciiPrintable(" ")      = true
     * StringUtils.isAsciiPrintable("Ceki")   = true
     * StringUtils.isAsciiPrintable("ab2c")   = true
     * StringUtils.isAsciiPrintable("!ab-c~") = true
     * StringUtils.isAsciiPrintable("\u0020") = true
     * StringUtils.isAsciiPrintable("\u0021") = true
     * StringUtils.isAsciiPrintable("\u007e") = true
     * StringUtils.isAsciiPrintable("\u007f") = false
     * StringUtils.isAsciiPrintable("Ceki G\u00fclc\u00fc") = false
     * </pre>
     *
     * @param str the string to check, may be null
     * @return <code>true</code> if every character is in the range
     *  32 thru 126
     * @since 2.1
     */
    public static boolean isAsciiPrintable(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (CharUtils.isAsciiPrintable(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the String contains only unicode digits.
     * A decimal point is not a unicode digit and returns false.
     *
     * <code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.
     *
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = true
     * StringUtils.isNumeric("  ")   = false
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("12 3") = false
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains digits, and is non-null
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the String contains only unicode digits or space
     * (<code>' '</code>).
     * A decimal point is not a unicode digit and returns false.
     *
     * <code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.
     *
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = true
     * StringUtils.isNumeric("  ")   = true
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("12 3") = true
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains digits or space,
     *  and is non-null
     */
    public static boolean isNumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isDigit(str.charAt(i)) == false) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the String contains only whitespace.
     *
     * <code>null</code> will return <code>false</code>.
     * An empty String ("") will return <code>true</code>.
     *
     * <pre>
     * StringUtils.isWhitespace(null)   = false
     * StringUtils.isWhitespace("")     = true
     * StringUtils.isWhitespace("  ")   = true
     * StringUtils.isWhitespace("abc")  = false
     * StringUtils.isWhitespace("ab2c") = false
     * StringUtils.isWhitespace("ab-c") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if only contains whitespace, and is non-null
     * @since 2.0
     */
    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    // Defaults
    //-----------------------------------------------------------------------
    /**
     * Returns either the passed in String,
     * or if the String is <code>null</code>, an empty String ("").
     *
     * <pre>
     * StringUtils.defaultString(null)  = ""
     * StringUtils.defaultString("")    = ""
     * StringUtils.defaultString("bat") = "bat"
     * </pre>
     *
     * @see String#valueOf(Object)
     * @param str  the String to check, may be null
     * @return the passed in String, or the empty String if it
     *  was <code>null</code>
     */
    public static String defaultString(String str) {
        return (str == null ? EMPTY : str);
    }

    /**
     * Returns either the passed in String, or if the String is
     * <code>null</code>, the value of <code>defaultStr</code>.
     *
     * <pre>
     * StringUtils.defaultString(null, "null")  = "null"
     * StringUtils.defaultString("", "null")    = ""
     * StringUtils.defaultString("bat", "null") = "bat"
     * </pre>
     *
     * @see String#valueOf(Object)
     * @param str  the String to check, may be null
     * @param defaultStr  the default String to return
     *  if the input is <code>null</code>, may be null
     * @return the passed in String, or the default if it was <code>null</code>
     */
    public static String defaultString(String str, String defaultStr) {
        return (str == null ? defaultStr : str);
    }

    // Reversing
    //-----------------------------------------------------------------------
    /**
     * Reverses a String as per {@link StringBuffer#reverse()}.
     *
     * <A code>null</code> String returns <code>null</code>.
     *
     * <pre>
     * StringUtils.reverse(null)  = null
     * StringUtils.reverse("")    = ""
     * StringUtils.reverse("bat") = "tab"
     * </pre>
     *
     * @param str  the String to reverse, may be null
     * @return the reversed String, <code>null</code> if null String input
     */
    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuffer(str).reverse().toString();
    }

    /**
     * Reverses a String that is delimited by a specific character.
     *
     * The Strings between the delimiters are not reversed.
     * Thus java.lang.String becomes String.lang.java (if the delimiter
     * is <code>'.'</code>).
     *
     * <pre>
     * StringUtils.reverseDelimited(null, *)      = null
     * StringUtils.reverseDelimited("", *)        = ""
     * StringUtils.reverseDelimited("a.b.c", 'x') = "a.b.c"
     * StringUtils.reverseDelimited("a.b.c", ".") = "c.b.a"
     * </pre>
     *
     * @param str  the String to reverse, may be null
     * @param separatorChar  the separator character to use
     * @return the reversed String, <code>null</code> if null String input
     * @since 2.0
     */
    public static String reverseDelimited(String str, char separatorChar) {
        if (str == null) {
            return null;
        }
        // could implement manually, but simple way is to reuse other,
        // probably slower, methods.
        String[] strs = split(str, separatorChar);
        ArrayUtils.reverse(strs);
        return join(strs, separatorChar);
    }

    /**
     * Reverses a String that is delimited by a specific character.
     *
     * The Strings between the delimiters are not reversed.
     * Thus java.lang.String becomes String.lang.java (if the delimiter
     * is <code>"."</code>).
     *
     * <pre>
     * StringUtils.reverseDelimitedString(null, *)       = null
     * StringUtils.reverseDelimitedString("",*)          = ""
     * StringUtils.reverseDelimitedString("a.b.c", null) = "a.b.c"
     * StringUtils.reverseDelimitedString("a.b.c", ".")  = "c.b.a"
     * </pre>
     *
     * @param str  the String to reverse, may be null
     * @param separatorChars  the separator characters to use, null treated as whitespace
     * @return the reversed String, <code>null</code> if null String input
     * @deprecated Use {@link #reverseDelimited(String, char)} instead.
     *      This method is broken as the join doesn't know which char to use.
     *      Method will be removed in Commons Lang 3.0.
     *
     */
    public static String reverseDelimitedString(String str, String separatorChars) {
        if (str == null) {
            return null;
        }
        // could implement manually, but simple way is to reuse other,
        // probably slower, methods.
        String[] strs = split(str, separatorChars);
        ArrayUtils.reverse(strs);
        if (separatorChars == null) {
            return join(strs, ' ');
        }
        return join(strs, separatorChars);
    }

    // Abbreviating
    //-----------------------------------------------------------------------
    /**
     * Abbreviates a String using ellipses. This will turn
     * "Now is the time for all good men" into "Now is the time for..."
     *
     * Specifically:
     *
     *   If <code>str</code> is less than <code>maxWidth</code> characters
     *       long, return it.
     *   Else abbreviate it to <code>(substring(str, 0, max-3) + "...")</code>.
     *   If <code>maxWidth</code> is less than <code>4</code>, throw an
     *       <code>IllegalArgumentException</code>.
     *   In no case will it return a String of length greater than
     *       <code>maxWidth</code>.
     *
     *
     *
     * <pre>
     * StringUtils.abbreviate(null, *)      = null
     * StringUtils.abbreviate("", 4)        = ""
     * StringUtils.abbreviate("abcdefg", 6) = "abc..."
     * StringUtils.abbreviate("abcdefg", 7) = "abcdefg"
     * StringUtils.abbreviate("abcdefg", 8) = "abcdefg"
     * StringUtils.abbreviate("abcdefg", 4) = "a..."
     * StringUtils.abbreviate("abcdefg", 3) = IllegalArgumentException
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param maxWidth  maximum length of result String, must be at least 4
     * @return abbreviated String, <code>null</code> if null String input
     * @throws IllegalArgumentException if the width is too small
     * @since 2.0
     */
    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }

    /**
     * Abbreviates a String using ellipses. This will turn
     * "Now is the time for all good men" into "...is the time for..."
     *
     * Works like <code>abbreviate(String, int)</code>, but allows you to specify
     * a "left edge" offset.  Note that this left edge is not necessarily going to
     * be the leftmost character in the result, or the first character following the
     * ellipses, but it will appear somewhere in the result.
     *
     * In no case will it return a String of length greater than
     * <code>maxWidth</code>.
     *
     * <pre>
     * StringUtils.abbreviate(null, *, *)                = null
     * StringUtils.abbreviate("", 0, 4)                  = ""
     * StringUtils.abbreviate("abcdefghijklmno", -1, 10) = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 0, 10)  = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 1, 10)  = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 4, 10)  = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 5, 10)  = "...fghi..."
     * StringUtils.abbreviate("abcdefghijklmno", 6, 10)  = "...ghij..."
     * StringUtils.abbreviate("abcdefghijklmno", 8, 10)  = "...ijklmno"
     * StringUtils.abbreviate("abcdefghijklmno", 10, 10) = "...ijklmno"
     * StringUtils.abbreviate("abcdefghijklmno", 12, 10) = "...ijklmno"
     * StringUtils.abbreviate("abcdefghij", 0, 3)        = IllegalArgumentException
     * StringUtils.abbreviate("abcdefghij", 5, 6)        = IllegalArgumentException
     * </pre>
     *
     * @param str  the String to check, may be null
     * @param offset  left edge of source String
     * @param maxWidth  maximum length of result String, must be at least 4
     * @return abbreviated String, <code>null</code> if null String input
     * @throws IllegalArgumentException if the width is too small
     * @since 2.0
     */
    public static String abbreviate(String str, int offset, int maxWidth) {
        if (str == null) {
            return null;
        }
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        if (offset > str.length()) {
            offset = str.length();
        }
        if ((str.length() - offset) < (maxWidth - 3)) {
            offset = str.length() - (maxWidth - 3);
        }
        if (offset <= 4) {
            return str.substring(0, maxWidth - 3) + "...";
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if ((offset + (maxWidth - 3)) < str.length()) {
            return "..." + abbreviate(str.substring(offset), maxWidth - 3);
        }
        return "..." + str.substring(str.length() - (maxWidth - 3));
    }

    // Difference
    //-----------------------------------------------------------------------
    /**
     * Compares two Strings, and returns the portion where they differ.
     * (More precisely, return the remainder of the second String,
     * starting from where it's different from the first.)
     *
     * For example,
     * <code>difference("i am a machine", "i am a robot") -> "robot"</code>.
     *
     * <pre>
     * StringUtils.difference(null, null) = null
     * StringUtils.difference("", "") = ""
     * StringUtils.difference("", "abc") = "abc"
     * StringUtils.difference("abc", "") = ""
     * StringUtils.difference("abc", "abc") = ""
     * StringUtils.difference("ab", "abxyz") = "xyz"
     * StringUtils.difference("abcde", "abxyz") = "xyz"
     * StringUtils.difference("abcde", "xyz") = "xyz"
     * </pre>
     *
     * @param str1  the first String, may be null
     * @param str2  the second String, may be null
     * @return the portion of str2 where it differs from str1; returns the
     * empty String if they are equal
     * @since 2.0
     */
    public static String difference(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        int at = indexOfDifference(str1, str2);
        if (at == -1) {
            return EMPTY;
        }
        return str2.substring(at);
    }

    /**
     * Compares two Strings, and returns the index at which the
     * Strings begin to differ.
     *
     * For example,
     * <code>indexOfDifference("i am a machine", "i am a robot") -> 7</code>
     *
     * <pre>
     * StringUtils.indexOfDifference(null, null) = -1
     * StringUtils.indexOfDifference("", "") = -1
     * StringUtils.indexOfDifference("", "abc") = 0
     * StringUtils.indexOfDifference("abc", "") = 0
     * StringUtils.indexOfDifference("abc", "abc") = -1
     * StringUtils.indexOfDifference("ab", "abxyz") = 2
     * StringUtils.indexOfDifference("abcde", "abxyz") = 2
     * StringUtils.indexOfDifference("abcde", "xyz") = 0
     * </pre>
     *
     * @param str1  the first String, may be null
     * @param str2  the second String, may be null
     * @return the index where str2 and str1 begin to differ; -1 if they are equal
     * @since 2.0
     */
    public static int indexOfDifference(String str1, String str2) {
        if (str1.equals(str2)) {
            return -1;
        }
        if (str1 == null || str2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < str1.length() && i < str2.length(); ++i) {
            if (str1.charAt(i) != str2.charAt(i)) {
                break;
            }
        }
        if (i < str2.length() || i < str1.length()) {
            return i;
        }
        return -1;
    }

    // Misc
    //-----------------------------------------------------------------------
    /**
     * Find the Levenshtein distance between two Strings.
     *
     * This is the number of changes needed to change one String into
     * another, where each change is a single character modification (deletion,
     * insertion or substitution).
     *
     * This implementation of the Levenshtein distance algorithm
     * is from <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a>
     *
     * <pre>
     * StringUtils.getLevenshteinDistance(null, *)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance(*, null)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance("","")               = 0
     * StringUtils.getLevenshteinDistance("","a")              = 1
     * StringUtils.getLevenshteinDistance("aaapppp", "")       = 7
     * StringUtils.getLevenshteinDistance("frog", "fog")       = 1
     * StringUtils.getLevenshteinDistance("fly", "ant")        = 3
     * StringUtils.getLevenshteinDistance("elephant", "hippo") = 7
     * StringUtils.getLevenshteinDistance("hippo", "elephant") = 7
     * StringUtils.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
     * StringUtils.getLevenshteinDistance("hello", "hallo")    = 1
     * </pre>
     *
     * @param s  the first String, must not be null
     * @param t  the second String, must not be null
     * @return result distance
     * @throws IllegalArgumentException if either String input <code>null</code>
     */
    public static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        int d[][]; // matrix
        int n; // length of s
        int m; // length of t
        int i; // iterates through s
        int j; // iterates through t
        char s_i; // ith character of s
        char t_j; // jth character of t
        int cost; // cost

        // Step 1
        n = s.length();
        m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        d = new int[n + 1][m + 1];

        // Step 2
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }

        // Step 3
        for (i = 1; i <= n; i++) {
            s_i = s.charAt(i - 1);

            // Step 4
            for (j = 1; j <= m; j++) {
                t_j = t.charAt(j - 1);

                // Step 5
                if (s_i == t_j) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                // Step 6
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
            }
        }

        // Step 7
        return d[n][m];
    }

    /**
     * Gets the minimum of three <code>int</code> values.
     *
     * @param a  value 1
     * @param b  value 2
     * @param c  value 3
     * @return  the smallest of the values
     */
    private static int min(int a, int b, int c) {
        // Method copied from NumberUtils to avoid dependency on subpackage
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }

    public static String iso2gbk(String isostr) {
        try {
            byte[] byteStr = isostr.getBytes("ISO8859-1");
            String gbkStr = new String(byteStr, "GBK");
            return gbkStr;
        } catch (Exception e) {
            return null;
        }
    }

    public static String gbk2iso(String gbkstr) {
        try {
            byte[] byteStr = gbkstr.getBytes("GB2312");
            String isoStr = new String(byteStr, "ISO8859-1");
            return isoStr;
        } catch (Exception e) {
            return null;
        }
    }

    //以下两个函数用于页面上的文件名称中文字的处理
    public static String UnicodeToChinese(String s,boolean iffilter) {
        //boolean iffilter = false;
        if (iffilter) {
            try {
                if (s == null || s.equals("")) {
                    return "";
                }
                String newstring = null;
                newstring = new String(s.getBytes("ISO8859_1"), "gb2312");
                return newstring;
            } catch (UnsupportedEncodingException e) {
                return s;
            }
        } else {
            return s;
        }
    }

    public static String ChineseToUnicode(String s,boolean iffilter) {
        // true: 不加编码过滤器时使用 false:加了过滤器
        // boolean iffilter = false;
        if (iffilter) {
            try {
                if (s == null || s.equals("")) {
                    return "";
                }
                String newstring = null;
                newstring = new String(s.getBytes("gb2312"), "ISO8859_1");
                return newstring;
            } catch (UnsupportedEncodingException e) {
                return s;
            }
        } else {
            return s;
        }
    }

    public static String toUtf8String(String s,boolean iffilter) {
        // true: 不加编码过滤器时使用 false:加了过滤器
        // boolean iffilter = false;
        if (iffilter) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c >= 0 && c <= '\377') {
                    sb.append(c);
                } else {
                    byte b[];
                    try {
                        b = Character.toString(c).getBytes("utf-8");
                    } catch (Exception ex) {
                        System.out.println(ex);
                        b = new byte[0];
                    }
                    for (int j = 0; j < b.length; j++) {
                        int k = b[j];
                        if (k < 0) {
                            k += 256;
                        }
                        sb.append("%" + Integer.toHexString(k).toUpperCase());
                    }

                }
            }
            return sb.toString();
        } else {
            return s;
        }
    }

    //处理文件名中出现的空格
    //其中%20是空格在UTF-8下的编码
    public static String encodingFileName(String fileName) {
        String returnFileName = "";
        try {
            returnFileName = URLEncoder.encode(fileName, "UTF-8");
            returnFileName = replace(returnFileName, "+", "%20",false);
            if (returnFileName.length() > 150) {
                returnFileName = new String(fileName.getBytes("GB2312"),"ISO8859-1");
                returnFileName = replace(returnFileName, " ", "%20",false);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnFileName;
    }

    /// <summary> 安全字符检查 </summary>
    /// <param name="str">字符串</param>
    /// <returns>检查结果 true：安全 ｜ false：不安全 <returns>
    public static boolean IsSafeCharacters(String str) {
        boolean returnVal = true;
        //不安全字符定义
        char[] unSafeChar = {'&', ';', '\'', '\\', '"', '|', '*', '?', '~', '^',
                            '<', '>', '(', ')', '[', ']', '{', '}', '$'};
        //逐个检查
        for(int i=0;i<unSafeChar.length;i++){
            if (str.indexOf(unSafeChar[i]) != -1) {
                returnVal = returnVal||false;
            }
        }
        return returnVal;

    }

    //检测是否输入了非法字符串(如果有非法字符则返回真)
    //String s = "日你妈的去死吧！";
    //String[] shitword = {"靠", "操", "日", "干", "去死", "恶心", "妈的", "滚蛋", "操你大爷"};
    public static boolean IsUnlawfulCharacter(String s,String[] UnlawfulCharacter){
      boolean bl = false;
        for (int j = 0; j < UnlawfulCharacter.length; j++) {
          if (s.indexOf(UnlawfulCharacter[j]) > -1) {
            System.out.println("[" + UnlawfulCharacter[j] + "]" + "-----------非法字符!");
            bl = bl || true;
          }
        }
        return bl;
  }

  /// <summary>
  /// ZeroFill '0'填充
  /// </summary>
  /// <param name="code">被补0的字符串</param>
  /// <param name="number">补0后的长度</param>
  /// <returns></returns>
  public static String FillingZero(String code, int number) {
      String returncode = code.trim();
      while (returncode.length() < number) {
          returncode = "0" + returncode;
      }
      return returncode;
  }

	/**
	 * 将String转成Clob ,静态方法
	 *
	 * @param str
	 *            字段
	 * @return clob对象，如果出现错误，返回 null
	 */
	public static Clob stringToClob(String str) {
		if (null == str) {
            return null;
        } else {
			try {
				Clob c = new javax.sql.rowset.serial.SerialClob(str
						.toCharArray());
				return c;
			} catch (Exception e) {
				return null;
			}
		}
	}

	/**
	 * 将Clob转成String ,静态方法
	 *
	 * @param clob
	 *            字段
	 * @return 内容字串，如果出现错误，返回 null
	 */
	public static String clobToString(Clob clob) {
		if (clob == null) {
            return null;
        }

		StringBuffer sb = new StringBuffer(65535);// 64K
		Reader clobStream = null;
		try {
			clobStream = clob.getCharacterStream();
			char[] b = new char[60000];// 每次获取60K
			int i = 0;
			while ((i = clobStream.read(b)) != -1) {
				sb.append(b, 0, i);
			}
		} catch (Exception ex) {
			sb = null;
		} finally {
			try {
				if (clobStream != null) {
					clobStream.close();
				}
			} catch (Exception e) {
			}
		}
		if (sb == null) {
            return null;
        } else {
            return sb.toString();
        }
	}
	/**
	 * 将字符串数组转成以逗号分隔的字符串 ,静态方法
	 *
	 * @param Arr 数组
	 * @return 内容字串，如果出现错误，返回 空字符串
	 */
	public static String arrayToString(String[] Arr) {
		if ((Arr == null)||(Arr.length<1)){
			return "";
		}
		String reStr = "";
		for(int i=0;i<Arr.length;i++){
			reStr += Arr[i]+",";
		}
		if(reStr.length()>0){
			reStr = reStr.substring(0, reStr.length()-1);
		}
		return reStr;
	}

	public static String[] splitByChar(String str, char splitchar) {
		if (str == null) {
			return null;
		}
		int len = str.length();
		if (len == 0) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		int i = 0, start = 0;
		while (i < len) {
			if (str.charAt(i) == splitchar) {
				list.add(str.substring(start, i));
				start = ++i;
				continue;
			}
			i++;
		}
		if (start != i) {
			list.add(str.substring(start, i));
		}

		return (String[]) list.toArray(new String[list.size()]);
	}

	public static String[] splitByString(String str, String splitchar) {
		if (str == null) {
			return null;
		}
		int len = str.length();
		if (len == 0) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		int i = 0, start = 0;
		while (i < len) {
			if (str.charAt(i) ==splitchar.charAt(0)) {
				list.add(str.substring(start, i));
				start = ++i;
				continue;
			}
			i++;
		}
		if (start != i) {
			list.add(str.substring(start, i));
		}

		return (String[]) list.toArray(new String[list.size()]);
	}

	public static String getStringValue(HashMap hm,String name) {
		if(hm.get(name)!=null){
			return String.valueOf(hm.get(name));
		}else{
			return "";
		}
	}
	public static int getIntValue(HashMap hm,String name) {
		if(hm.get(name)!=null&&!"".equals(hm.get(name))){
			return Integer.parseInt(String.valueOf(hm.get(name)));
		}else{
			return 0;
		}
	}
	public static Date getDateValue(HashMap hm,String name) {
		if(hm.get(name)!=null&&!"".equals(hm.get(name))&&!"null".equals(hm.get(name))){
			return Date.valueOf(String.valueOf(hm.get(name)));
		}else{
			return null;
		}
	}
	public static double getDoubleValue(HashMap hm,String name) {
		if(hm.get(name)!=null&&!"".equals(hm.get(name))){
			return Double.parseDouble(String.valueOf(hm.get(name)));
		}else{
			return 0;
		}
	}

	public static long getLongValue(HashMap hm,String name) {
		if(hm.get(name)!=null&&!"".equals(hm.get(name))){
			return Long.parseLong(String.valueOf(hm.get(name)));
		}else{
			return 0;
		}
	}

	/**
	 * stringSplit
	 * <b>功能说明 : </b>字符串分隔函数，将以特定字符分隔的字符串转化成数组. <br/>
	 * @param srcStr
	 * @return String[] 说明:转换后的数组.
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月10日 下午6:42:38<br/>
	 */
	public static String[] stringSplit(String srcStr) {
		String str = "打印机*钟表//自行车**雨伞%%收音机??电脑 水杯 风扇";
        str = "03/06 12:36 09-008 故障一层2区烟感03/06 12:36 09-008 故障一层2区烟感";
        //利用+表示一个或多个
        String temp[] = srcStr.split("%%|\\*+|\\//|\\?+|\\ +|\\+");
        for(String word : temp)
        {
            System.out.println(word);
        }
        return temp;
	}

	/**
	 * stringSplit
	 * <b>功能说明 : </b>字符串分隔函数，将以特定字符分隔的字符串转化成数组. <br/>
	 * @param srcStr
	 * @param pattern 正则表达式
	 * @return String[] 说明:转换后的数组.
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月10日 下午6:42:38<br/>
	 */
	public static String[] stringSplit(String srcStr,String pattern) {
		String str = "打印机*钟表//自行车**雨伞%%收音机??电脑 水杯 风扇";
        str = "03/06 12:36 09-008 故障一层2区烟感03/06 12:36 09-008 故障一层2区烟感";
        //利用+表示一个或多个
        String temp[] = srcStr.split(pattern);/*
        for(String word : temp)
        {
            System.out.println(word);
        }*/
        return temp;
	}
	/**
	 * stringToHashMap
	 * <b>功能说明 : </b>将由Map直接toString()后得到的字符串还原为HashMap. <br/>
	 * @param mapText 对map对象直接toString()后得到的字符串文本
	 * @return HashMap 说明:HashMap对象.
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午4:17:58<br/>
	 */
	public static HashMap stringToHashMap(String mapText) {
		String str = "{UNIT_STATE_ID=228, UNIT_ID=41208, FLOOR_ID=2887, BUILDING_ID=142}";
		//示例：{IS_CHECKED_IN=0, NUMBERING_FORMAT=xx-xxx, UPDATESTAMP=2016-03-12 14:44:09.0, TIME_FORMAT=HH:mm:ss, DATE_FORMAT=yyyy-MM-dd, DTU_NO=000000000001, ARRANGEMENT_RULE=ABCDE, STATEDESC2CODE=[登录-1;退录-2;火警-10;故障-11;回答-12;复位-13;通信故障-14]}
        HashMap hm = new HashMap();

        if(mapText!=null){
	        String temp[] = StringFilterNoWithCommaMinusSign(mapText).split("%%|\\*+|\\//|\\?+|\\ +|\\+|\\,+");
	        for(String word : temp)
	        {
	        	String[] tmpArr=word.split("\\=");
	            System.out.println(tmpArr[0].trim()+"="+tmpArr[1].trim());
	            hm.put(tmpArr[0].trim(), tmpArr[1].trim());
	        }
        }

        return hm;
	}
	/**
	 * stringToHashMap
	 * <b>功能说明 : </b>将由Map直接toString()后得到的字符串还原为HashMap. <br/>
	 * @param mapText 对map对象直接toString()后得到的字符串文本
	 * @return HashMap 说明:HashMap对象.
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午4:17:58<br/>
	 */
	public static HashMap mapStringToHashMap(String mapText) {
        HashMap hm = new HashMap();
        if(mapText!=null){
        	mapText = mapText.substring(1, mapText.length()-1);
	        String temp[] = mapText.split(",");
	        for(String word : temp)
	        {
	        	String[] tmpArr=word.split("\\=");
	            System.out.println(tmpArr[0]+"="+tmpArr[1]);
	            hm.put(tmpArr[0].trim(), tmpArr[1].trim());
	        }
        }

        return hm;
	}

	/**
	 * StringFilter
	 * <b>功能说明 : </b>过滤所有特殊字符. <br/>
	 * @param str
	 * @return String 说明:过滤后的字符串
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午5:49:34<br/>
	 */
	public static String StringFilter(String str){
		// 只允许字母和数字 // String regEx ="[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx="[`~!@#$%^&*()+ =|{}':;',\\-\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	/**
	 * StringFilter
	 * <b>功能说明 : </b>过滤所有特殊字符(冒号不过滤). <br/>
	 * @param str
	 * @return String 说明:过滤后的字符串
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午5:49:34<br/>
	 */
	public static String StringFilterNoColon(String str){
		// 只允许字母和数字 // String regEx ="[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx="[`~!@#$%^&*()+ =|{}';',\\-\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	/**
	 * StringFilterNoStar
	 * <b>功能说明 : </b>过滤所有特殊字符(不含星号). <br/>
	 * @param str
	 * @return String 说明:过滤后的字符串
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午5:49:34<br/>
	 */
	public static String StringFilterNoStar(String str){
		// 只允许字母和数字 // String regEx ="[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx="[`~!@#$%^&()+ =|{}':;',\\-\\[\\].<>/?~！@#￥%……&（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * StringFilterNoStarAndQuestionMark
	 * <b>功能说明 : </b>过滤所有特殊字符(不含*和#号). <br/>
	 * @param str
	 * @return String 说明:过滤后的字符串
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午5:49:34<br/>
	 */
	public static String StringFilterNoStarAndQuestionMark(String str){
		// 只允许字母和数字 // String regEx ="[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx="[`~!@$%^&()+ =|{}':;',\\-\\[\\].<>/~！@￥%……&（）——+|{}【】‘；：”“’。，、？]"; //含空格
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * StringFilterNoStarAndQuestionAndBlankMark
	 * <b>功能说明 : </b>过滤所有特殊字符(不含*、#和空格号). <br/>
	 * @param str
	 * @return String 说明:过滤后的字符串
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午5:49:34<br/>
	 */
	public static String StringFilterNoStarAndQuestionAndBlankMark(String str){
		// 只允许字母和数字 // String regEx ="[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx="[`~!@$%^&()+=|{}':;',\\-\\[\\].<>/~！@￥%……&（）——+|{}【】‘；：”“’。，、？]"; //不含空格
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * StringFilterNoWithComma
	 * <b>功能说明 : </b>过滤特殊字符，除了逗号之外，主要用于对map.toString()后的数据处理，用于将过滤后的字符串反转为map. <br/>
	 * @param str
	 * @return String 说明:过滤后的字符串.
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午5:50:24<br/>
	 */
	public static String StringFilterNoWithCommaMinusSign(String str){
		// 只允许字母和数字 // String regEx ="[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx="[`~!@#$%^&*()+ |{}':'\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * StringFilterNoWithComma
	 * <b>功能说明 : </b>过滤特殊字符，除了逗号之外，主要用于对map.toString()后的数据处理，用于将过滤后的字符串反转为map. <br/>
	 * @param str
	 * @return String 说明:过滤后的字符串.
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午5:50:24<br/>
	 */
	public static String StringFilterNoWithComma(String str){
		// 只允许字母和数字 // String regEx ="[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx="[`~!@#$%^&*()+ |{}':;'\\-\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	/**
	 * StringFilterNoWithSemicolonAndEquals
	 * <b>功能说明 : </b>过滤特殊字符，除了逗号之外，主要用于对map.toString()后的数据处理，用于将过滤后的字符串反转为map. <br/>
	 * @param str
	 * @return String 说明:过滤后的字符串.
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午5:50:24<br/>
	 */
	public static String StringFilterNoWithSemicolonAndEquals(String str){
		// 只允许字母和数字 // String regEx ="[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx="[`~!@#$%^&*()+ |{}':'\\-\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	/**
	 * StringFilterNoWithSpace
	 * <b>功能说明 : </b>过滤所有特殊字符，但不含空格. <br/>
	 * @param str
	 * @return String 说明:过滤后的字符串.
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午5:54:25<br/>
	 */
	public static String StringFilterNoWithSpace(String str){
		// 只允许字母和数字 // String regEx ="[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx="[`~!@#$%^&*()+=|{}':;',\\-\\-\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	/**
	 * StringFilterWithEquals
	 * <b>功能说明 : </b>去除字符串中的所有特殊字符，保留等号. <br/>
	 * @param str
	 * @return String 说明:返回过滤后的字符串.
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午5:56:13<br/>
	 */
	public static String StringFilterWithEquals(String str){
		// 只允许字母和数字 // String regEx ="[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx="[`~!@#$%^&*()+|{}':;',\\-\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * StringFilterSpecificCharacter
	 * <b>功能说明 : </b>去除字符串中的特定字符. <br/>
	 * @param str
	 * @return String 说明:返回过滤后的字符串.
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午5:56:13<br/>
	 */
	public static String StringFilterSpecificCharacter(String str){
		// 清除掉所有特定字符
		String regEx="地址：|No.|No.001|No:001|时间：|位置：|描述：|描述：|类型：|事件:|类型:|地点:|编号:|事件|编号|类型|地点|时间:|位置";
		/*
		 * &机号：.01  &回路：.07
&点号：.035 &分区：.0051
&类型：感烟探头 &状态 火警
&时间：.2016.12.08 08:59
描述：&５层Ａ栋５１８房
		 * */
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll(" ").trim();
	}
	/**
	 * StringFilterSpecificCharacter
	 * <b>功能说明 : </b>去除字符串中的特定字符. <br/>
	 * @param str
	 * @return String 说明:返回过滤后的字符串.
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年3月11日 下午5:56:13<br/>
	 */
	public static String StringFilterSpecificCharacter(String regEx,String str){
		// 清除掉所有特定字符
		//String regEx="地址：|No.|时间：|位置：|类型：";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	public static void sortStringArray(String[] arrStr) {
        String temp;
        for (int i = 0; i < arrStr.length; i++) {
            for (int j = arrStr.length - 1; j > i; j--) {
                if (arrStr[i].length() < arrStr[j].length()) {
                    temp = arrStr[i];
                    arrStr[i] = arrStr[j];
                    arrStr[j] = temp;
                }
            }
        }
    }

	public static String[] Set2Array(Set<String> set) {
        String[] arr = new String[set.size()];
        //Set-->数组
        set.toArray(arr);
        sortStringArray(arr);
        //System.out.println(Arrays.toString(arr));
        return arr;
    }


	/**
	 *
	 * @Title : filterNumber
	 * @Type : FilterStr
	 * @date : 2014年3月12日 下午7:23:03
	 * @Description : 过滤出数字
	 * @param number
	 * @return
	 */
	public static String filterNumber(String number) {
		number = number.replaceAll("[^(0-9)]", "");
		return number;
	}

	/**
	 *
	 * @Title : filterAlphabet
	 * @Type : FilterStr
	 * @date : 2014年3月12日 下午7:28:54
	 * @Description : 过滤出字母
	 * @param alph
	 * @return
	 */
	public static String filterAlphabet(String alph) {
		alph = alph.replaceAll("[^(A-Za-z)]", "");
		return alph;
	}

	/**
	 *
	 * @Title : filterChinese
	 * @Type : FilterStr
	 * @date : 2014年3月12日 下午9:12:37
	 * @Description : 过滤出中文
	 * @param chin
	 * @return
	 */
	public static String filterChinese(String chin) {
		chin = chin.replaceAll("[^(\\u4e00-\\u9fa5)]", "");
		return chin;
	}

	/**
	 *
	 * @Title : filter
	 * @Type : FilterStr
	 * @date : 2014年3月12日 下午9:17:22
	 * @Description : 过滤出字母、数字和中文
	 * @param character
	 * @return
	 */
	public static String filter(String character) {
		character = character.replaceAll("[^(a-zA-Z0-9\\u4e00-\\u9fa5)]", "");
		return character;
	}

	/**
	 * 过滤字母
	 *
	 * @param alphabet
	 * @return
	 */
	public static String removeAlphabet(String alphabet) {
		return alphabet.replaceAll("[A-Za-z]", "");
	}

	/**
	 * 过滤数字
	 *
	 * @param digital
	 * @return
	 */
	public static String filterDigital(String digital) {
		return digital.replaceAll("[0-9]", "");
	}

	/**
	 * 过滤汉字
	 *
	 * @param chin
	 * @return
	 */
	public static String removeChinese(String chin) {
		return chin.replaceAll("[\\u4e00-\\u9fa5]", "");
	}

	/**
	 * 过滤 字母、数字、汉字
	 *
	 * @param character
	 * @return
	 */
	public static String filterAll(String character) {
		return character.replaceAll("[a-zA-Z0-9\\u4e00-\\u9fa5]", "");
	}

	/**
	 * 将字符串中的多个空格替换为一个空格
	 * @param str
	 * @return
	 */
	public static String replaceMultiToOneSpace(String str) {
		Pattern p = Pattern.compile("\\s+");
		Matcher m = p.matcher(str);
		return m.replaceAll(" ");
	}

	/**
	 * filterInvisibleCharacter
	 * 过滤内容只保留数字、字母、中文、标点符号
	 * @param character
	 * @return
	 */
	public static String filterInvisibleCharacter(String character) {
		character = character.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5. ，:/\\_,。\\-？“”]+", "");
		return character;
	}


	/**
	 * 取得匹配的字符串和匹配的正则表达式
	 * @param str
	 * @return
	 */
	public static String[] getDateFormatMatcher(String str,String regexp) {
		String[] dayFormats=regexp.split("\\|",100);
		String datetime= TimeUtil.getLocalTime();
		String[] matcher =new String[3];
		matcher[0]=datetime;
		matcher[1]="\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
		matcher[2]="0";
		int count=0;
		Pattern p =null;
		Matcher m = null;
		for(String date_format:dayFormats){
			p =Pattern.compile(date_format);
	        m = p.matcher(str);
	        while (m.find()) {
	        	matcher[0]=m.group();
	        	matcher[1]=date_format;
	        	count++;
	        }
		}
		matcher[2]=String.valueOf(count);
		return matcher;
	}
	/**
	 * getOptimalMatching
	 * 最佳匹配,根据正则表达式顺序进行优化匹配，即在最前面的表达式如果匹配到了，后面的将不再匹配
	 * @param str //待匹配的字符串
	 * @param regexp //用于匹配的正则表达式串
	 * @return
	 */
	public static HashMap getOptimalMatching(String str,String regexp) {
    	//str="火警记录 065号 001 11-21 15:06  厨房 感烟探测器";
        //String regexp="\\d{2}-\\d{2} \\d{2}:\\d{2}|\\d{2}-\\d{2}|\\d{2}:\\d{2}|\\d{3}";
        String[] tmpA1=regexp.split("\\|",10000);
        HashMap tmap=new HashMap();
        String value="";
		Pattern p =null;
		Matcher m = null;
        for (String exp:tmpA1){
        	p = Pattern.compile(exp);
            m = p.matcher(str);
            while (m.find()) {
            	//value=m.group();
                //System.out.println("exp==:"+exp);
                tmap=new HashMap();
                tmap.put("regexp", exp);
                tmap.put("value", m.group());
                break;
            }
            if(tmap.size()>0){
            	break;
            }
        }
        return tmap;
	}

	public static String getDateRegExp() {
		StringBuffer regexp=new StringBuffer();

		regexp.append("\\d{2,4}年\\d{1,2}月\\d{1,2}日 \\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{2,4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}|");


		regexp.append("\\d{2,4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{2,4}年\\d{1,2}月\\d{1,2}日 \\d{1,2}:\\d{1,2}:\\d{1,2}|");

		regexp.append("\\d{2,4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒|");
		regexp.append("\\d{2,4}年\\d{1,2}月\\d{1,2}日 \\d{1,2}时\\d{1,2}分\\d{1,2}秒|");

		regexp.append("\\d{4}年\\d{2}月\\d{2}日 \\d{2}时\\d{2}分|");
		regexp.append("\\d{4}年\\d{2}月\\d{2}日\\d{2}时\\d{2}分|");


		regexp.append("\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{1,2}月\\d{1,2}日 \\d{1,2}:\\d{1,2}|");


		regexp.append("\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{1,2}月\\d{1,2}日 \\d{1,2}:\\d{1,2}:\\d{1,2}|");


		regexp.append("\\d{2,4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{2,4}年\\d{1,2}月 \\d{1,2}日\\d{1,2}:\\d{1,2}|");


		regexp.append("\\d{4}年\\d{2}月\\d{2}日\\d{2}:\\d{2}:\\d{2}|");
		regexp.append("\\d{4}年\\d{2}月 \\d{2}日\\d{2}:\\d{2}:\\d{2}|");



		regexp.append("\\d{4}年\\d{2}月\\d{2}日\\d{2}:\\d{2}|");
		regexp.append("\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}|");


		regexp.append("\\d{2}年\\d{2}月\\d{2}日\\d{2}时\\d{2}分|");
		regexp.append("\\d{2}年\\d{2}月\\d{2}日 \\d{2}时\\d{2}分|");


		//10月11日11时22分
		regexp.append("\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分|");

		regexp.append("\\d{2}:\\d{2}:\\d{1,2} \\d{2},\\d{2},\\d{4}|");


		regexp.append("\\d{2,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{2,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}|");

		//regexp.append("\\d{2,4}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{4}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{2}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}|");

		//regexp.append("\\d{2,4}\\.\\d{1,2}\\.\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{4}\\.\\d{1,2}\\.\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{2}\\.\\d{1,2}\\.\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}|");

		regexp.append("\\d{4}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}|");

		regexp.append("\\d{2}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}|");

		regexp.append("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}|");

		//16:18:23 2016-12-5
		regexp.append("\\d{1,2}:\\d{1,2}:\\d{1,2} \\d{2}-\\d{1,2}-\\d{1,2}|");
		regexp.append("\\d{1,2}:\\d{1,2}:\\d{1,2} \\d{4}-\\d{1,2}-\\d{1,2}|");

		regexp.append("\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{1,2}\\.\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{2,4}\\.\\d{1,2}\\.\\d{1,2} \\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{4}\\.\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{2}|");
		regexp.append("\\d{2}\\.\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{2}|");
		regexp.append("\\d{2}\\/\\d{2}\\/\\d{2} \\d{2}:\\d{2}:\\d{2}|");
		regexp.append("\\d{2}\\/\\d{2}\\/\\d{2} \\d{2}:\\d{2}:\\d{2}|");

		regexp.append("\\d{2}\\/\\d{2}\\/\\d{2} \\d{2}:\\d{2}|");
		regexp.append("\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{1,2}\\.\\d{1,2} \\d{1,2}:\\d{1,2}|");
		regexp.append("\\d{4}\\.\\d{2}\\.\\d{2} \\d{2}:\\d{2}|");
		regexp.append("\\d{2}-\\d{2}-\\d{2} \\d{2}:\\d{2}|");
		regexp.append("\\d{2}\\/\\d{2} \\d{2}:\\d{2}|");
		regexp.append("\\d{2}\\/\\d{2}\\d{2}:\\d{2}|");
		regexp.append("\\d{2}-\\d{2} \\d{2}:\\d{2}");

		regexp.append("");

		return regexp.toString();
	}

	public static String getDeviceIdRegExp() {
		StringBuffer regexp=new StringBuffer();

		//先按有格式的方式匹配

		//016号
		regexp.append("\\d{3}号|");

		//01栋01区01层016号
		regexp.append("\\d{2}栋\\d{2}区\\d{2}层\\d{3}号|");

		//01栋00机01路103号
		regexp.append("\\d{2}栋\\d{2}机\\d{2}路\\d{3}号|");
		//00机01路103号
		regexp.append("\\d{2}机\\d{2}路\\d{3}号|");

		//N01-5018Z531
		regexp.append("[A-Za-z]{1}\\d{2}-\\d{4}[A-Za-z]{1}\\d{3}|");

		//1机03路063
		regexp.append("\\d{1}[\\u4e00-\\u9fa5]{1}\\d{2}[\\u4e00-\\u9fa5]{1}\\d{3}|");
		//N001L08M026
		regexp.append("N\\d{1,3}L\\d{1,2}M\\d{1,3}|");
		//多线08-7
		regexp.append("多线\\d{2}-\\d{1}|");
		regexp.append("[\\u4e00-\\u9fa5]{2}\\d{2}-\\d{1}|");
		//016回路253
		regexp.append("\\d{3}[\\u4e00-\\u9fa5]{2}\\d{3}|");
		//02机3-116
		regexp.append("\\d{2}[\\u4e00-\\u9fa5]{1}-\\d{3}|");
		//N2L5M7
		regexp.append("N\\d{1,2}L\\d{1,2}M\\d{1,2}|");
		//N3L2D18
		regexp.append("N\\d{1,2}L\\d{1,2}D\\d{1,2}|");
		//N1T207
		regexp.append("N\\d{1}T\\d{1,3}|");
		//84-4号
		regexp.append("\\d{2}-\\d{1}号|");
		//0-10-21
		regexp.append("\\d{1}-\\d{2}-\\d{2}|");
		//001-03-21
		regexp.append("\\d{3}-\\d{2}-\\d{2}|");
		//001-213
		regexp.append("\\d{3}-\\d{3}|");
		//10-097
		regexp.append("\\d{2}-\\d{3}|");
		regexp.append("\\d{1}-\\d{3}|");
		regexp.append("\\d{2}-\\d{2}|");
		regexp.append("\\d{1}-\\d{2}|");
		regexp.append("\\d{8}|");
		regexp.append("\\d{7}|");
		regexp.append("\\d{6}|");
		regexp.append("\\d{5}|");
		//N2
		regexp.append("N\\d{1,8}|");

		regexp.append("\\d{1,3}-\\d{1,3}|");
		regexp.append("\\d{1,3}-\\d{1,3}-\\d{1,3}|");

		regexp.append("\\d{3} |");

		regexp.append("\\d{1,2}[\\u4e00-\\u9fa5]{1}\\d{1,2}-\\d{1,2}|");
		regexp.append("\\d{1,2}[\\u4e00-\\u9fa5]{1,9}-\\d{1,2}|");
		regexp.append("\\d{1,3}[\\u4e00-\\u9fa5]{1,5}|");

		regexp.append("\\d{4}|");
		regexp.append("\\d{3}号|");


		regexp.append("\\d{3}");
		regexp.append("");

		return regexp.toString();
	}


	/**
	 * 最优匹配时间，即按最优匹配原则，找出字符串中的时间
	 * @param str 待检测的字符串
	 * @return
	 */
	public static HashMap getOptimalMatchingDate(String str) {
		return getOptimalMatching(str,getDateRegExp());
	}
	/**
	 * getMatchAllDate
	 * 匹配所有符合格式的日期
	 * @param str 需检测分析的字符串
	 * @return
	 */
	public static List getMatchAllDate(String str) {
		StringBuffer regexp=new StringBuffer();
		return getMatchAll(str,getDateRegExp());
	}

	/**
	 * getMatchDateCount
	 * 统计符合正则表达式的字符串个数，即统计有多少个匹配
	 * @param str  待检测分析的字符串
	 * @return
	 */
	public static int getMatchDateCount(String str) {
		Pattern p =null;
		Matcher m = null;
		int count=0;
		p = Pattern.compile(getDateRegExp());
	    m = p.matcher(str);
	    while (m.find()) {
	    	count++;
	    }
	    return count;
	}

	/**
	 * getMatchDeviceIdCount
	 * 统计符合正则表达式的字符串个数，即统计有多少个匹配
	 * @param str  待检测分析的字符串
	 * @return
	 */
	public static int getMatchDeviceIdCount(String str) {
		Pattern p =null;
		Matcher m = null;
		int count=0;
		p = Pattern.compile(getDeviceIdRegExp());
	    m = p.matcher(getFilterDate(str));
	    while (m.find()) {
	    	count++;
	    }
	    return count;
	}

	/**
	 * getOptimalMatchingRegexp
	 * 获得最佳匹配的正则表达式
	 * @param str  待检测分析的字符串
	 * @return
	 */
	public static String getOptimalMatchingRegexp(String str,String regexp) {
		Pattern p =null;
		Matcher m = null;
		String[] tmpA1=regexp.split("\\|",1000);
		boolean bl=false;
		for (String exp:tmpA1){
        	p = Pattern.compile(exp);
            m = p.matcher(str);
            while (m.find()) {
            	String value=m.group();
            	regexp=exp;
            	bl=true;
                break;
            }
            if(bl){
            	break;
            }
        }
	    return regexp;
	}

	/**
	 * getOptimalMatchingRegexp
	 * 获得最佳匹配的正则表达式
	 * @param str  待检测分析的字符串
	 * @return
	 */
	public static int getOptimalMatchingCount(String str,String regexp) {
		Pattern p =null;
		Matcher m = null;
		String[] tmpA1=regexp.split("\\|",10000);
		int count=0;
		boolean bl=false;
		for (String exp:tmpA1){
        	p = Pattern.compile(exp);
            m = p.matcher(str);
            while (m.find()) {
            	String value=m.group();
            	count++;
            }
            if(count>0){
            	break;
            }
        }
	    return count;
	}
	/**
	 * getOptimalMatchingRegexp
	 * 获得最佳匹配的正则表达式
	 * @param str  待检测分析的字符串
	 * @return
	 */
	public static Regexp getOptimalMatchingCountAndRegexp(String str,String regexp) {
		Pattern p =null;
		Matcher m = null;
		String[] tmpA1=regexp.split("\\|",10000);
		int count=0;
		Regexp regexp2=new Regexp();
		boolean bl=false;
		for (String exp:tmpA1){
        	p = Pattern.compile(exp);
            m = p.matcher(str);
            while (m.find()) {
            	String value=m.group();
            	count++;
            }
            if(count>0){
            	regexp2.setRegexp(exp);
            	break;
            }
        }
		regexp2.setCount(count);
	    return regexp2;
	}

	/**
	 * 最优匹配时间，即按最优匹配原则，找出字符串中的时间
	 * @param str 待检测的字符串
	 * @return
	 */
	public static HashMap getOptimalMatchingDeviceId(String str) {
		return getOptimalMatching(str,getDeviceIdRegExp());
	}


	public static List getMatchAllDeviceId(String str) {
		StringBuffer regexp=new StringBuffer();
		return getMatchAll(str,getDeviceIdRegExp());
	}

	/**
	 * getMatchAll
	 * 全部匹配，即将所有匹配的字符串取出并带出对应的用来匹配的正则式
	 * @param str  待匹配分析的字符串
	 * @param regexp 用于匹配的正则表达式
	 * @return
	 */
	public static List getMatchAll(String str,String regexp) {
    	//str="火警记录 065号 001 11-21 15:06  厨房 感烟探测器";
        //String regexp="\\d{2}-\\d{2} \\d{2}:\\d{2}|\\d{2}-\\d{2}|\\d{2}:\\d{2}|\\d{3}";
        String[] tmpA1=regexp.split("\\|",100);
        List ls = new ArrayList();
        HashMap tmap=new HashMap();
        String value="";
		Pattern p =null;
		Matcher m = null;
        for (String exp:tmpA1){
        	p = Pattern.compile(exp);
            m = p.matcher(str);
            while (m.find()) {
            	value=m.group();
                tmap=new HashMap();
                tmap.put("regexp", exp);
                tmap.put("value", value);
                str = str.replace(value, "");
                ls.add(tmap);
            }
        }/*
        for (Object obj:ls){
        	HashMap map=(HashMap)obj;
        	System.out.println("regexp:"+map.get("regexp"));
        	System.out.println("value:"+map.get("value"));
        }*/
        return ls;
	}

	/**
	 * getFilterDate
	 * 根据正则表达式过滤其中的日期
	 * @param str  待匹配分析的字符串
	 * @param str 用于匹配的正则表达式
	 * @return
	 */
	public static String getFilterDate(String str) {
		String value = "";
		Pattern p = null;
		Matcher m = null;

		p = Pattern.compile(getDateRegExp());
		m = p.matcher(str);
		while (m.find()) {
			value = m.group();
			str = str.replace(value, "");
		}

		return str;
	}

    /**
     * 比较两个字符串是否含有共同的子字符串
     * @param str1 字符串一
     * @param str2 字符串二
     * @param includeSelf 子字符串是否包括本身
     * @return 比较结果
     */
    public static boolean hasSameSubStr(String str1, String str2, boolean includeSelf)
    {
        String shortStr = str1.length() > str2.length()? str2: str1;
        String longStr = str1.length() > str2.length()? str1: str2;
        String temp = "";

        for(int i = 0; i < shortStr.length(); i++)
        {
            for(int j = i + 2; j <= shortStr.length(); j++)
            {
                temp = shortStr.substring(i, j);
                boolean flag1 = includeSelf && longStr.indexOf(temp) >= 0;
                boolean flag2 = !includeSelf && !temp.equals(shortStr) && longStr.indexOf(temp) >= 0;

                if(flag1 || flag2)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 获取两个字符串的最大的子字符串集合
     * @param str1 字符串一
     * @param str2 字符串二
     * @param includeSelf 子字符串是否包括本身
     * @return 最大的子字符串集合
     */
    public static List<String> getMaxSameSubStr(String str1, String str2, boolean includeSelf)
    {
        String shortStr = str1.length() > str2.length()? str2: str1;
        String longStr = str1.length() > str2.length()? str1: str2;
        String temp = "";
        int subLength = 0;
        List<String> sameSubs = new ArrayList<String>();

        for(int i = 0; i < shortStr.length(); i++)
        {
            for(int j = i + 2; j <= shortStr.length(); j++)
            {
                temp = shortStr.substring(i, j);
                boolean flag1 = includeSelf && longStr.indexOf(temp) >= 0;
                boolean flag2 = !includeSelf && !temp.equals(shortStr) && longStr.indexOf(temp) >= 0;

                if(flag1 || flag2)
                {
                    if(temp.length() > subLength)
                    {
                        subLength = temp.length();
                        sameSubs.clear();
                        sameSubs.add(temp);
                    }
                    else if(temp.length() == subLength)
                    {
                        sameSubs.add(temp);
                    }
                }
            }
        }

        return sameSubs;
    }

    /**
     * 比较两个字符串是否含有子字符串，如果有，获取最大的子字符串集合
     * 返回值为List，长度为2，第一个元素为boolean型，表示是否含有子字符串，第二个元素为List<String>型，表示最大子字符串的集合
     * @param str1 字符串一
     * @param str2 字符串二
     * @param includeSelf 是否包括自身
     * @return 比较结果
     */
    public static List<Object> compareStrWithSub(String str1, String str2, boolean includeSelf)
    {
        String shortStr = str1.length() > str2.length()? str2: str1;
        String longStr = str1.length() > str2.length()? str1: str2;
        String temp = "";
        int subLength = 0;
        boolean hasSame = false;
        List<String> sameSubs = new ArrayList<String>();
        List<Object> ret = new ArrayList<Object>();

        for(int i = 0; i < shortStr.length(); i++)
        {
            for(int j = i + 2; j <= shortStr.length(); j++)
            {
                temp = shortStr.substring(i, j);
                boolean flag1 = includeSelf && longStr.indexOf(temp) >= 0;
                boolean flag2 = !includeSelf && !temp.equals(shortStr) && longStr.indexOf(temp) >= 0;

                if(flag1 || flag2)
                {
                    hasSame = true;

                    if(temp.length() > subLength)
                    {
                        subLength = temp.length();
                        sameSubs.clear();
                        sameSubs.add(temp);
                    }
                    else if(temp.length() == subLength)
                    {
                        sameSubs.add(temp);
                    }
                }
            }
        }

        ret.add(hasSame);
        //ret.add(sameSubs);
        ret.addAll(sameSubs);
        return ret;
    }

    //12-13 08:35  111056 点型感烟故障 饭堂1 楼闷顶层
    /**
     * 字符串交叉的情况处理，比"点型感烟故障",类型应是“点型感烟”，事件应用“感烟故障”
     * @param src
     * @param str1
     * @param str2
     * @param includeSelf
     * @return
     */
    public static String compareStrWithSub(String src,String str1, String str2, boolean includeSelf)
    {
    	String reStr="";
    	List<Object> lobj=compareStrWithSub(str1,str2,includeSelf);
    	if(lobj.size()>0){
    		boolean bl=Boolean.parseBoolean(lobj.get(0).toString());
    		if(bl){
    			String tmpStr=str1.replace(lobj.get(1).toString(), "");
    			reStr=src.replace(tmpStr, "").replace(str2, "");
    		}else{
    			reStr=src.replace(str1, "").replace(str2, "");
    		}
    	}
    	return reStr;
    }

	/**
     * 全角字符串转换半角字符串
     *
     * @param fullWidthStr
     *            非空的全角字符串
     * @return 半角字符串
     */
    private static String fullWidth2halfWidth(String fullWidthStr) {
        if (null == fullWidthStr || fullWidthStr.length() <= 0) {
            return "";
        }
        char[] charArray = fullWidthStr.toCharArray();
        //对全角字符转换的char数组遍历
        for (int i = 0; i < charArray.length; ++i) {
            int charIntValue = (int) charArray[i];
            //如果符合转换关系,将对应下标之间减掉偏移量65248;如果是空格的话,直接做转换
            if (charIntValue >= 65281 && charIntValue <= 65374) {
                charArray[i] = (char) (charIntValue - 65248);
            } else if (charIntValue == 12288) {
                charArray[i] = (char) 32;
            }
        }
        return new String(charArray);
    }
    /**
     * 全角空格为12288，半角空格为32
     * 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
     *
     * 将字符串中的全角字符转为半角
     * @param src 要转换的包含全角的任意字符串
     * @return  转换之后的字符串
     */
    public static String toSemiangle(String src) {
        char[] c = src.toCharArray();
        for (int index = 0; index < c.length; index++) {
            if (c[index] == 12288) {// 全角空格
                c[index] = (char) 32;
            } else if (c[index] > 65280 && c[index] < 65375) {// 其他全角字符
                c[index] = (char) (c[index] - 65248);
            }
        }
        return String.valueOf(c);
    }

    /**
     * ASCII表中可见字符从!开始，偏移位值为33(Decimal)
     */
    static final char DBC_CHAR_START = 33; // 半角!

    /**
     * ASCII表中可见字符到~结束，偏移位值为126(Decimal)
     */
    static final char DBC_CHAR_END = 126; // 半角~

    /**
     * 全角对应于ASCII表的可见字符从！开始，偏移值为65281
     */
    static final char SBC_CHAR_START = 65281; // 全角！

    /**
     * 全角对应于ASCII表的可见字符到～结束，偏移值为65374
     */
    static final char SBC_CHAR_END = 65374; // 全角～

    /**
     * ASCII表中除空格外的可见字符与对应的全角字符的相对偏移
     */
    static final int CONVERT_STEP = 65248; // 全角半角转换间隔

    /**
     * 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理
     */
    static final char SBC_SPACE = 12288; // 全角空格 12288

    /**
     * 半角空格的值，在ASCII中为32(Decimal)
     */
    static final char DBC_SPACE = ' '; // 半角空格

    /**
     * <PRE>
     * 半角字符->全角字符转换
     * 只处理空格，!到˜之间的字符，忽略其他
     * </PRE>
     */
    private static String bj2qj(String src) {
        if (src == null) {
            return src;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (int i = 0; i < ca.length; i++) {
            if (ca[i] == DBC_SPACE) { // 如果是半角空格，直接用全角空格替代
                buf.append(SBC_SPACE);
            } else if ((ca[i] >= DBC_CHAR_START) && (ca[i] <= DBC_CHAR_END)) { // 字符是!到~之间的可见字符
                buf.append((char) (ca[i] + CONVERT_STEP));
            } else { // 不对空格以及ascii表中其他可见字符之外的字符做任何处理
                buf.append(ca[i]);
            }
        }
        return buf.toString();
    }

    /**
     * <PRE>
     * 全角字符->半角字符转换
     * 只处理全角的空格，全角！到全角～之间的字符，忽略其他
     * </PRE>
     */
    public static String qj2bj(String src) {
        if (src == null) {
            return src;
        }
        StringBuilder buf = new StringBuilder(src.length());
        char[] ca = src.toCharArray();
        for (int i = 0; i < src.length(); i++) {
            if (ca[i] >= SBC_CHAR_START && ca[i] <= SBC_CHAR_END) { // 如果位于全角！到全角～区间内
                buf.append((char) (ca[i] - CONVERT_STEP));
            } else if (ca[i] == SBC_SPACE) { // 如果是全角空格
                buf.append(DBC_SPACE);
            } else { // 不处理全角空格，全角！到全角～区间外的字符
                buf.append(ca[i]);
            }
        }
        return buf.toString();
    }

    private static String [] pattern ={"零","壹","贰","叁","肆","伍","陆","柒","捌","玖"};
    private static String [] cPattern ={"","拾","佰","仟","万","拾","佰","仟","亿"};
    private static String [] cfPattern = {"","角","分"};
    private static String ZEOR = "零";

	public static String convertUpper(String moneyString) {
		int dotPoint = moneyString.indexOf("."); // 判断是否为小数
		String moneyStr;
		if (dotPoint != -1) {
			moneyStr = moneyString.substring(0, moneyString.indexOf("."));
		} else {
			moneyStr = moneyString;
		}
		StringBuffer fraction = null; // 小数部分的处理,以及最后的yuan.
		StringBuffer ms = new StringBuffer();
		for (int i = 0; i < moneyStr.length(); i++) {
			ms.append(pattern[moneyStr.charAt(i) - 48]); // 按数组的编号加入对应大写汉字
		}

		int cpCursor = 1;
		for (int j = moneyStr.length() - 1; j > 0; j--) {
			ms.insert(j, cPattern[cpCursor]); // 在j之后加字符,不影响j对原字符串的相对位置
			// 只是moneyStr.length()不断增加
			// insert(j,"string")就在j位置处插入,j=0时为第一位
			cpCursor = cpCursor == 8 ? 1 : cpCursor + 1; // 亿位之后重新循环
		}

		while (ms.indexOf("零拾") != -1) { // 当十位为零时用一个"零"代替"零拾"
			// replace的起始于终止位置
			ms.replace(ms.indexOf("零拾"), ms.indexOf("零拾") + 2, ZEOR);
		}
		while (ms.indexOf("零佰") != -1) { // 当百位为零时,同理
			ms.replace(ms.indexOf("零佰"), ms.indexOf("零佰") + 2, ZEOR);
		}
		while (ms.indexOf("零仟") != -1) { // 同理
			ms.replace(ms.indexOf("零仟"), ms.indexOf("零仟") + 2, ZEOR);
		}
		while (ms.indexOf("零万") != -1) { // 万需保留，中文习惯
			ms.replace(ms.indexOf("零万"), ms.indexOf("零万") + 2, "万");
		}
		while (ms.indexOf("零亿") != -1) { // 同上
			ms.replace(ms.indexOf("零亿"), ms.indexOf("零亿") + 2, "亿");
		}
		while (ms.indexOf("零零") != -1) {// 有连续数位出现零，即有以下情况，此时根据习惯保留一个零即可
			ms.replace(ms.indexOf("零零"), ms.indexOf("零零") + 2, ZEOR);
		}
		while (ms.indexOf("亿万") != -1) { // 特殊情况，如:100000000,根据习惯保留高位
			ms.replace(ms.indexOf("亿万"), ms.indexOf("亿万") + 2, "亿");
		}
		while (ms.lastIndexOf("零") == ms.length() - 1) { // 当结尾为零j，不必显示,经过处理也只可能出现一个零
			if (ms.indexOf("零") == -1) {
				ms.delete(ms.lastIndexOf("零"), ms.lastIndexOf("零") + 1);
			} else {
				break;
			}
		}

		int end;
		if ((dotPoint = moneyString.indexOf(".")) != -1) { // 是小数的进入
			String fs = moneyString.substring(dotPoint + 1, moneyString.length());
			if (fs.indexOf("00") == -1 || fs.indexOf("00") >= 2) {// 若前两位小数全为零，则跳过操作
				end = fs.length() > 2 ? 2 : fs.length(); // 仅保留两位小数
				fraction = new StringBuffer(fs.substring(0, end));
				for (int j = 0; j < fraction.length(); j++) {
					fraction.replace(j, j + 1, pattern[fraction.charAt(j) - 48]); // 替换大写汉字
				}
				for (int i = fraction.length(); i > 0; i--) { // 插入中文标识
					fraction.insert(i, cfPattern[i]);
				}
				fraction.insert(0, "元"); // 为整数部分添加标识
			} else {
				fraction = new StringBuffer("元整");
			}

		} else {
			fraction = new StringBuffer("元整");
		}

		ms.append(fraction); // 加入小数部分
		return ms.toString();
	}

	public static class Regexp{
		int count=0;
		String regexp="";
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public String getRegexp() {
			return regexp;
		}
		public void setRegexp(String regexp) {
			this.regexp = regexp;
		}
	}

	public static void main(String[] args) {

    }
}
