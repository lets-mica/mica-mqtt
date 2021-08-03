package net.dreamlu.iot.mqtt.mica.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {
	public static final long MS_ONE_DAY = 86400000L;

	public static final long MS_DAY_FROM = 28800000L;

	public static long clearTime(long time){
		return (time + MS_DAY_FROM) / MS_ONE_DAY * MS_ONE_DAY - MS_DAY_FROM;
	}

	public static Date clearTime(Date d){
		return new Date((d.getTime() + MS_DAY_FROM) / MS_ONE_DAY * MS_ONE_DAY - MS_DAY_FROM);
	}

	public static Date getDate(Date date, Time time){
		return new Date(clearTime(date.getTime()) + time.getTime() + MS_DAY_FROM);
	}

	public static long getDate(long date, long time){
		return clearTime(date) + time + MS_DAY_FROM;
	}
	public static Timestamp getTimestamp(){
		Timestamp d = new Timestamp(System.currentTimeMillis());
		return d;
	}

	public static Timestamp getTimestamp(long stamp){
		Timestamp d = new Timestamp(stamp);
		return d;
	}

	public static String timeStampToString(long stamp){
		Timestamp d = new Timestamp(stamp);
		String dateString ="";
        try {
        	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          	dateString = formatter.format(stamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
		return dateString;
	}

	public static Timestamp getTimestamp(Date date, Time time){
		return new Timestamp(clearTime(date.getTime()) + time.getTime() + MS_DAY_FROM);
	}

	public static Timestamp getTimestamp(Timestamp stamp, Time time){
		return new Timestamp(clearTime(stamp.getTime()) + time.getTime() + MS_DAY_FROM);
	}

	public static Timestamp getTimestamp(String dateStr){
		return getTimestamp(dateStr,"yyyy-MM-dd HH:mm:ss");
	}

	public static Timestamp getTimestamp(String dateStr, String fmt){
		SimpleDateFormat sf = new SimpleDateFormat(fmt);
		Timestamp tm=null;
		try {
			dateStr=dateStr.replace("0A", "10");
			java.util.Date date = sf.parse(dateStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			tm=new Timestamp(cal.getTimeInMillis());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return tm;
	}

	public static Timestamp formatTimeEight(String dateStr, String fmt){
		SimpleDateFormat sf = new SimpleDateFormat(fmt);
		Timestamp tm=null;
		try {
			dateStr=dateStr.replace("0A", "10");
			java.util.Date date = sf.parse(dateStr);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			tm=new Timestamp(cal.getTimeInMillis()+ 8*60*60*1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return tm;
	}

	public static String formatTimeEight(String time)throws Exception{
		java.util.Date d = null;
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		d =sd.parse(time);
		long rightTime = (long) (d.getTime() + 8*60*60*1000);//把当前得到的时间用date.getTime()的方法写成时间戳的形式，再加上8小时对应的毫秒数
		String newtime=sd.format(rightTime);//把得到的新的时间戳再次格式化成时间的格式
		return newtime;
	}

	private static String conversion(int field) {
		String str;
		if (field < 10) {
			str = "0" + field;
		} else {
			str = field + "";
		}
		return str;
	}
	private static String AM_MPTime(int field, int isRest) {
		String str;
		if (isRest == 1) {
			str = field + 12 + "";
		} else {
			str = field + "";
		}
		return str;
	}

	public static String getSystemTime() {
		Calendar date = new GregorianCalendar();
		StringBuffer str = new StringBuffer();
		int year = date.get(Calendar.YEAR);
		String month = conversion(date.get(Calendar.MONTH) + 1);
		String day = conversion(date.get(Calendar.DATE));
		String hour = AM_MPTime(date.get(Calendar.HOUR),date.get(Calendar.AM_PM));
		String minute = conversion(date.get(Calendar.MINUTE));
		String second = conversion(date.get(Calendar.SECOND));
		str.append(year);
		str.append(month);
		str.append(day);
		str.append(hour);
		str.append(minute);
		str.append(second);
		return str.toString();
	}

	public static String getLocalTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new java.util.Date());
	}

	public static String getNoYearTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
		return sdf.format(new java.util.Date());
	}

	//System.currentTimeMillis()与日期之间的相互转换
	public static long getTimeMillis(String dateTimeString){
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long time = 0;
		try {
		    time = dateformat.parse(dateTimeString).getTime();
		} catch (ParseException e) {
		    e.printStackTrace();
		}
		return time;
	}


	public static Timestamp getFullDateTimeString(String dateTimeString,String dataTimeFormats){
		String[] tmpAtr=new String[]{"19990101","010101"};;
		String dateSimeStr="";
		String regexp="[`~!@#$%^&*()+|{}':;',\\-\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？年月日时分秒 ]";
		Calendar a=Calendar.getInstance();

		if("\\d{4}年\\d{2}月\\d{2}日\\d{2}时\\d{2}分".equals(dataTimeFormats)){
			dateSimeStr=dateTimeString+"00秒";
		}else if("\\d{2}\\/\\d{2}\\/\\d{2} \\d{2}:\\d{2}".equals(dataTimeFormats)){
			dateSimeStr=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+dateTimeString+":00";
		}else if("\\d{2}\\/\\d{2}\\/\\d{2} \\d{2}:\\d{2}:\\d{2}".equals(dataTimeFormats)){
			dateSimeStr=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+dateTimeString;
		}else if("\\d{2,4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String m=tmpA[0];
			String d=tmpA[1];
			String h=tmpA[2];
			String s=tmpA[3];
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			dateSimeStr=a.get(Calendar.YEAR)+m+d+h+s+"00";
		}else if("\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String m=tmpA[0];
			String d=tmpA[1];
			String h=tmpA[2];
			String s=tmpA[3];
			String ss=tmpA[4];
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=a.get(Calendar.YEAR)+m+d+h+s+ss;
		}else if("\\d{2,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String m=tmpA[0];
			String d=tmpA[1];
			String h=tmpA[2];
			String s=tmpA[3];
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			dateSimeStr=a.get(Calendar.YEAR)+m+d+h+s+"00";
		}else if("\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String m=tmpA[0];
			String d=tmpA[1];
			String h=tmpA[2];
			String s=tmpA[3];
			String ss=tmpA[4];
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=a.get(Calendar.YEAR)+m+d+h+s+ss;
		}else if("\\d{4}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();

			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}

			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{2}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();

			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}

			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{2,4}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();

			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}

			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{1,2}\\.\\d{1,2} \\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String m=tmpA[0];
			String d=tmpA[1];
			String h=tmpA[2];
			String s=tmpA[3];
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			dateSimeStr=a.get(Calendar.YEAR)+m+d+h+s+"00";
		}else if("\\d{1,2}\\.\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String m=tmpA[0];
			String d=tmpA[1];
			String h=tmpA[2];
			String s=tmpA[3];
			String ss=tmpA[4];
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=a.get(Calendar.YEAR)+m+d+h+s+ss;
		}else if("\\d{2,4}\\.\\d{1,2}.\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{4}\\.\\d{1,2}\\.\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{2,4}\\.\\d{1,2}\\.\\d{1,2} \\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss="00";
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{4}\\.\\d{2}\\.\\d{2} \\d{2}:\\d{2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss="00";
			y= StringUtils.leftAppendZero(y, 4);
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{4}\\.\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();
			y= StringUtils.leftAppendZero(y, 4);
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{2}\\/\\d{2}\\/\\d{2} \\d{2}:\\d{2}:\\d{2}".equals(dataTimeFormats)){
			dateSimeStr=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+dateTimeString;
		}else if("\\d{2}年\\d{2}月\\d{2}日\\d{2}时\\d{2}分".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss="00";
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{2}\\/\\d{2} \\d{2}:\\d{2}".equals(dataTimeFormats)){
			dateSimeStr=a.get(Calendar.YEAR)+"/"+dateTimeString+":00";
		}else if("\\d{2}\\/\\d{2}\\d{2}:\\d{2}".equals(dataTimeFormats)){
			dateSimeStr=a.get(Calendar.YEAR)+"/"+dateTimeString+":00";
		}else if("\\d{2}-\\d{2}-\\d{2} \\d{2}:\\d{2}".equals(dataTimeFormats)){
			dateSimeStr=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+dateTimeString+":00";
		}else if("\\d{2}-\\d{2} \\d{2}:\\d{2}".equals(dataTimeFormats)){
			dateSimeStr=a.get(Calendar.YEAR)+"-"+dateTimeString+":00";
		}else if("\\d{2,4}年\\d{1,2}月\\d{1,2}日 \\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString.replaceAll(" ", ""),regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{2,4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString.replaceAll(" ", ""),regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{2,4}年\\d{1,2}月\\d{1,2}日 \\d{1,2}时\\d{1,2}分\\d{1,2}秒".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString.replaceAll(" ", ""),regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{2,4}年\\d{1,2}月\\d{1,2}日 \\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString.replaceAll(" ", ""),regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss="00";
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String m=tmpA[0];
			String d=tmpA[1];
			String h=tmpA[2];
			String s=tmpA[3];
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			dateSimeStr=a.get(Calendar.YEAR)+m+d+h+s+"00";
		}else if("\\d{1,2}月\\d{1,2}日 \\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String m=tmpA[0];
			String d=tmpA[1];
			String h=tmpA[2];
			String s=tmpA[3];
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			dateSimeStr=a.get(Calendar.YEAR)+m+d+h+s+"00";
		}else if("\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String m=tmpA[0];
			String d=tmpA[1];
			String h=tmpA[2];
			String s=tmpA[3];
			String ss=tmpA[4];
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=a.get(Calendar.YEAR)+m+d+h+s+ss;
		}else if("\\d{1,2}月\\d{1,2}日 \\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String m=tmpA[0];
			String d=tmpA[1];
			String h=tmpA[2];
			String s=tmpA[3];
			String ss=tmpA[4];
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=a.get(Calendar.YEAR)+m+d+h+s+ss;
		}else if("\\d{2,4}年\\d{1,2}月\\d{1,2}日\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss="00";
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{2,4}年\\d{1,2}月 \\d{1,2}日\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss="00";
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{4}年\\d{2}月\\d{2}日\\d{2}:\\d{2}:\\d{2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();
			y= StringUtils.leftAppendZero(y, 4);
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}:\\d{2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();
			y= StringUtils.leftAppendZero(y, 4);
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{4}年\\d{2}月\\d{2}日\\d{2}:\\d{2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss="00";
			y= StringUtils.leftAppendZero(y, 4);
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{4}年\\d{2}月\\d{2}日 \\d{2}:\\d{2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss="00";
			y= StringUtils.leftAppendZero(y, 4);
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{2}年\\d{2}月\\d{2}日\\d{2}时\\d{2}分".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+tmpA[0].trim();
			String m=tmpA[0].trim();
			String d=tmpA[1].trim();
			String h=tmpA[2].trim();
			String s=tmpA[3].trim();
			String ss="00";
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{2}年\\d{2}月\\d{2}日 \\d{2}时\\d{2}分".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+tmpA[0].trim();
			String m=tmpA[0].trim();
			String d=tmpA[1].trim();
			String h=tmpA[2].trim();
			String s=tmpA[3].trim();
			String ss="00";
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=String.valueOf(a.get(Calendar.YEAR));
			String m=tmpA[0].trim();
			String d=tmpA[1].trim();
			String h=tmpA[2].trim();
			String s=tmpA[3].trim();
			String ss="00";
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+tmpA[0].trim();
			String m=tmpA[0].trim();
			String d=tmpA[1].trim();
			String h=tmpA[2].trim();
			String s=tmpA[3].trim();
			String ss="00";
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{2}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss="00";
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if("\\d{4}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss="00";
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if ("\\d{2,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss="00";
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if ("\\d{1,2}:\\d{1,2}:\\d{1,2} \\d{2}-\\d{1,2}-\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[3].trim();
			String m=tmpA[4].trim();
			String d=tmpA[5].trim();
			String h=tmpA[0].trim();
			String s=tmpA[1].trim();
			String ss=tmpA[2].trim();
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if ("\\d{1,2}:\\d{1,2}:\\d{1,2} \\d{4}-\\d{1,2}-\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[3].trim();
			String m=tmpA[4].trim();
			String d=tmpA[5].trim();
			String h=tmpA[0].trim();
			String s=tmpA[1].trim();
			String ss=tmpA[2].trim();
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if ("\\d{2}\\.\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if ("\\d{2,4}\\.\\d{1,2}\\.\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[0].trim();
			String m=tmpA[1].trim();
			String d=tmpA[2].trim();
			String h=tmpA[3].trim();
			String s=tmpA[4].trim();
			String ss=tmpA[5].trim();
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}else if ("\\d{2}:\\d{2}:\\d{1,2} \\d{2},\\d{2},\\d{4}".equals(dataTimeFormats)){
			String[] tmpA= StringUtils.stringSplit(dateTimeString,regexp);
			String y=tmpA[5].trim();
			String m=tmpA[4].trim();
			String d=tmpA[3].trim();
			String h=tmpA[0].trim();
			String s=tmpA[1].trim();
			String ss=tmpA[2].trim();
			if(y.length()<4){
				y=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+y;
			}
			m= StringUtils.leftAppendZero(m, 2);
			d= StringUtils.leftAppendZero(d, 2);
			h= StringUtils.leftAppendZero(h, 2);
			s= StringUtils.leftAppendZero(s, 2);
			ss= StringUtils.leftAppendZero(ss, 2);
			dateSimeStr=y+m+d+h+s+ss;
		}

		//\\d{2}:\\d{2}:\\d{1,2} \\d{2},\\d{2},\\d{4}

		dateSimeStr= StringUtils.StringFilterSpecificCharacter(regexp,dateSimeStr);//  dateSimeStr.replaceAll(regexp,"");
		return TimeUtil.getTimestamp(dateSimeStr,"yyyyMMddHHmmss");
	}

	/**
	 *
	 * <b>功能说明 : </b>将不完整的字符串转换为完整的字符串. <br/>
	 * <b>适用条件 : </b>TODO(这里描述这个方法适用条件 – 可选,如果不需说明则删除此句).<br/>
	 * <b>执行流程 : </b>TODO(这里描述这个方法的执行流程 – 可选,如果不需说明则删除此句).<br/>
	 * <b>使用示例 : </b>TODO(这里描述这个方法的使用方法 – 可选,如果不需说明则删除此句).<br/>
	 * <b>注意事项 : </b>TODO(这里描述这个方法的注意事项 – 可选,如果不需说明则删除此句).<br/>
	 * @param dateTimeString
	 * @param dataFormats
	 * @param timeFormats
	 * @return String 说明:TODO(这里描述返回对象是什么 – 可选,如果不需说明则删除此句).
	 * @throws
	 * <br/><br/><br/><b>作者 </b>wsq<br/>
	 * <b>创建时间 : </b>2016年4月2日 上午11:57:59<br/>
	 */
	public static Timestamp getFullDateTimeString(String dateTimeString,String dataFormats,String timeFormats){
		String[] tmpAtr=new String[]{"19990101","010101"};;
		String dateStr="";
		String timeStr="";
		if(dateTimeString.indexOf(" ")>0){
			tmpAtr=dateTimeString.split(" ");
			dateStr=tmpAtr[0];
			timeStr=tmpAtr[1];
		}else if(dateTimeString.indexOf("日")>0){
			tmpAtr=dateTimeString.split("日");
			dateStr=tmpAtr[0]+"日";
			timeStr=tmpAtr[1];
		}else{
			return TimeUtil.getTimestamp("19990101010101","yyyyMMddHHmmss");
		}
		Calendar a=Calendar.getInstance();

		String[] dayFormats=dataFormats.split("\\|",100);

		for(String date_format:dayFormats){
			date_format = StringUtils.StringFilterNoColon(date_format).toString();
			if ("MMDD".equalsIgnoreCase(date_format)) {
				dateStr=a.get(Calendar.YEAR)+ StringUtils.StringFilter(dateStr);
			}else if ("M月D日".equalsIgnoreCase(date_format)) {
				String[] tmpA= StringUtils.stringSplit(dateStr,"-|月|/"); //  dateStr.split("月");
				String m=tmpA[0];
				m= StringUtils.leftAppendZero(m, 2);
				String d= StringUtils.removeChinese(tmpA[1]);
				d= StringUtils.leftAppendZero(d, 2);
				dateStr=a.get(Calendar.YEAR)+ StringUtils.StringFilter(m+d);
			}else if ("MM月DD日".equalsIgnoreCase(date_format)) {
				String[] tmpA= StringUtils.stringSplit(dateStr,"-|月|/"); //  dateStr.split("月");
				String m=tmpA[0];
				m= StringUtils.leftAppendZero(m, 2);
				String d= StringUtils.removeChinese(tmpA[1]);
				d= StringUtils.leftAppendZero(d, 2);
				dateStr=a.get(Calendar.YEAR)+ StringUtils.StringFilter(m+d);
			}else if ("YYMMDD".equalsIgnoreCase(date_format)) {
				dateStr=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+ StringUtils.StringFilter(dateStr);
			}else if ("YY年M月D日".equalsIgnoreCase(date_format)) {
				String[] tmpA= StringUtils.stringSplit(dateStr,"-|年|月|日|/");
				String y=tmpA[0];
				String m=tmpA[1];
				String d=tmpA[2];
				m= StringUtils.leftAppendZero(m, 2);
				d= StringUtils.leftAppendZero(d, 2);
				dateStr=a.get(Calendar.YEAR)+ StringUtils.StringFilter(y+m+d);
			}else if ("YY年MM月DD日".equalsIgnoreCase(date_format)) {
				dateStr=String.valueOf(a.get(Calendar.YEAR)).substring(0,2)+ StringUtils.StringFilterSpecificCharacter("年|月|日",dateStr);
			}else if ("YYYY年M月D日".equalsIgnoreCase(date_format)) {
				String[] tmpA= StringUtils.stringSplit(dateStr,"-|年|月|日|/");
				String y=tmpA[0];
				String m=tmpA[1];
				String d=tmpA[2];
				m= StringUtils.leftAppendZero(m, 2);
				d= StringUtils.leftAppendZero(d, 2);
				dateStr=a.get(Calendar.YEAR)+ StringUtils.StringFilter(y+m+d);
			}else if ("YYYY年MM月DD日".equalsIgnoreCase(date_format)) {
				dateStr= StringUtils.StringFilterSpecificCharacter("年|月|日",dateStr);
			}else if ("DDMM".equalsIgnoreCase(date_format)) {
				if (dateStr.replaceAll(" ", "").length()==4){
					dateStr=dateStr.substring(2, 4)+dateStr.substring(0, 2);
				}else{
					String[] tmp= StringUtils.stringSplit(dateStr);
					dateStr=a.get(Calendar.YEAR)+tmp[1]+tmp[0];
				}
			}else if ("YYYYMMDD".equalsIgnoreCase(date_format)) {
				dateStr= StringUtils.StringFilter(dateStr);
			}else if ("MMDDYYYY".equalsIgnoreCase(date_format.replace("/", ""))) {
				if (dateStr.replaceAll(" ", "").length()==4){
					dateStr=dateStr.substring(4, 8)+dateStr.substring(0, 2)+dateStr.substring(2, 4);
				}else{
					String[] tmp= StringUtils.stringSplit(dateStr);
					dateStr=tmp[2]+tmp[0]+tmp[1];
				}
			}else if ("DDMMYYYY".equalsIgnoreCase(date_format.replace("/", ""))) {
				if (dateStr.replaceAll(" ", "").length()==4){
					dateStr=dateStr.substring(4, 8)+dateStr.substring(2, 4)+dateStr.substring(0, 2);
				}else{
					String[] tmp= StringUtils.stringSplit(dateStr);
					dateStr=tmp[2]+tmp[1]+tmp[0];
				}
			}else if ("YYYYDDMM".equalsIgnoreCase(date_format.replace("/", ""))) {
				if (dateStr.replaceAll(" ", "").length()==4){
					dateStr=dateStr.substring(0, 4)+dateStr.substring(6, 8)+dateStr.substring(4, 6);
				}else{
					String[] tmp= StringUtils.stringSplit(dateStr);
					dateStr=tmp[2]+tmp[1]+tmp[0];
				}
			}else{
				dateStr = "19990101";
			}

			if(!dateStr.equals("19990101")){
				break;
			}

		}


		String[] timeFormat=timeFormats.split("\\|",100);
		for(String time_format:timeFormat){
			time_format = StringUtils.StringFilter(time_format).toString();
			if(timeStr.indexOf("时")>0){
				String regEx="时|分|秒";
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(timeStr);
				timeStr = m.replaceAll("").trim();
			}

			if(timeStr.indexOf(":")>0){
				tmpAtr=timeStr.split(":");
				if(tmpAtr.length>=3){
					timeStr= StringUtils.leftAppendZero(tmpAtr[0], 2)+ StringUtils.leftAppendZero(tmpAtr[1], 2)+ StringUtils.leftAppendZero(tmpAtr[2], 2);
				}else if(tmpAtr.length==2){
					timeStr= StringUtils.leftAppendZero(tmpAtr[0], 2)+ StringUtils.leftAppendZero(tmpAtr[1], 2)+"00";
				}else if(tmpAtr.length==1){
					timeStr= StringUtils.leftAppendZero(tmpAtr[0], 2)+ StringUtils.leftAppendZero(tmpAtr[1], 2)+ StringUtils.leftAppendZero(tmpAtr[2], 2);
				}else {
					timeStr= "00:00:00";
				}
			}

			if ("HHmm".equalsIgnoreCase(time_format)) {
				timeStr= StringUtils.StringFilter(timeStr)+"00";
			}else if ("HHmmss".equalsIgnoreCase(time_format)) {
				timeStr= StringUtils.StringFilter(timeStr);
			}else if ("HHmi".equalsIgnoreCase(time_format)) {
				timeStr= StringUtils.StringFilter(timeStr)+"00";
			}else if ("HHmiss".equalsIgnoreCase(time_format)) {
				timeStr= StringUtils.StringFilter(timeStr);
			}else{
				timeStr = "010101";
			}

			if(!timeStr.equals("010101")){
				break;
			}
		}

		return TimeUtil.getTimestamp(dateStr+timeStr,"yyyyMMddHHmmss");
	}

    private static final String FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

    /* JDK7及之前 */

    /** 获取时间戳 */
    public static Long getMillis1() {
        return System.currentTimeMillis();
    }

    /** Date转时间戳 */
    public static Long date2Millis(Date date) {
        return date.getTime();
    }

    /** Calendar转时间戳 */
    public static Long calendar2Millis(Calendar calendar) {
        return calendar.getTime().getTime();
    }

    /** 日期字符串转时间戳 */
    public static Long string2Millis(String dateStr, String formatStr) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatStr);
            return simpleDateFormat.parse(dateStr).getTime();
        } catch (Exception e) {
            return 0L;
        }
    }

    /* JDK8 */

    /** 获取时间戳 */
    public static Long getMillis2() {
        return Instant.now().toEpochMilli();
    }

    /** LocalDateTime转时间戳 */
    public static Long localDateTime2Millis(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    /** LocalDate转时间戳 */
    public static Long localDate2Millis(LocalDate localDate) {
        return LocalDateTime.of(localDate, LocalTime.MIN).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    /** Clock转时间戳 */
    public static Long clock2Millis(Clock clock) {
        return clock.millis();
    }

    /** ZoneDateTIme转时间戳(这个不常用吧~) */
    public static Long zoneDateTime2Millis(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toLocalDateTime().toInstant(ZoneOffset.ofHours(8)).toEpochMilli();//!!!好费劲
    }

    /** String转时间戳(JDK8) */
    public static Long string2MillisWithJDK8(String dateStr , String formatStr) {
        return LocalDateTime.parse(dateStr , DateTimeFormatter.ofPattern(formatStr)).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    /** String转时间戳(JDK8) */
    public static Long DateString2MillisWithJDK8(String dateStr) {
        return LocalDateTime.parse(dateStr , DateTimeFormatter.ofPattern(FORMAT_STR)).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

	/**
	 * 判断参数的格式是否为“yyyyMMdd”格式的合法日期字符串
	 *
	 */
	public static boolean isValidDate(String str) {
		try {
			if (str != null && !str.equals("")) {
				if (str.length() == 8) {
					// 闰年标志
					boolean isLeapYear = false;
					String year = str.substring(0, 4);
					String month = str.substring(4, 6);
					String day = str.substring(6, 8);
					int vYear = Integer.parseInt(year);
					// 判断年份是否合法
					if (vYear < 1900 || vYear > 2200) {
						return false;
					}
					// 判断是否为闰年
					if (vYear % 4 == 0 && vYear % 100 != 0 || vYear % 400 == 0) {
						isLeapYear = true;
					}
					// 判断月份
					// 1.判断月份
					if (month.startsWith("0")) {
						String units4Month = month.substring(1, 2);
						int vUnits4Month = Integer.parseInt(units4Month);
						if (vUnits4Month == 0) {
							return false;
						}
						if (vUnits4Month == 2) {
							// 获取2月的天数
							int vDays4February = Integer.parseInt(day);
							if (isLeapYear) {
								if (vDays4February > 29) {
									return false;
								}
							} else {
								if (vDays4February > 28) {
									return false;
								}
							}
						}
					} else {
						// 2.判断非0打头的月份是否合法
						int vMonth = Integer.parseInt(month);
						if (vMonth != 10 && vMonth != 11 && vMonth != 12) {
							return false;
						}
					}
					// 判断日期
					// 1.判断日期
					if (day.startsWith("0")) {
						String units4Day = day.substring(1, 2);
						int vUnits4Day = Integer.parseInt(units4Day);
						if (vUnits4Day == 0) {
							return false;
						}
					} else {
						// 2.判断非0打头的日期是否合法
						int vDay = Integer.parseInt(day);
						if (vDay < 10 || vDay > 31) {
							return false;
						}
					}
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public static boolean isValidDateTime(String time){
		return isValidDateTime(time,"yyyy-MM-dd HH:mm:ss")||isValidDateTime(time,"yyyy-MM-ddHH:mm:ss");
	}
	public static boolean isValidDateTime(String time,String dateFormat){
		try {
			SimpleDateFormat sss = new SimpleDateFormat(dateFormat);
			sss.parse(time.trim());
			String[] ss = time.trim().split("[-: ]");
			int year = 0;
			int month = 0;
			int day = 0;
			int hour = 0;
			int minute = 0;
			int second = 0;
			if(dateFormat.equalsIgnoreCase("yyyy-MM-dd HH:mm:ss")){
				year = Integer.valueOf(ss[0]);
				month = Integer.valueOf(ss[1]);
				day = Integer.valueOf(ss[2]);
				hour = Integer.valueOf(ss[3]);
				minute = Integer.valueOf(ss[4]);
				second = Integer.valueOf(ss[5]);
			}else if(dateFormat.equalsIgnoreCase("yyyy-MM-ddHH:mm:ss")){
				year = Integer.valueOf(ss[0]);
				month = Integer.valueOf(ss[1]);
				day = Integer.valueOf(ss[2].substring(0,2));
				hour = Integer.valueOf(ss[2].substring(2));
				minute = Integer.valueOf(ss[3]);
				second = Integer.valueOf(ss[4]);
			}else{
				return false;
			}

			if(year < 1 || year > 2099 || month < 1 || month > 12){
				return false;
			}
			int[] monthLengths = new int[]{0, 31, -1, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
			if(isLeapYear(year)){
				monthLengths[2] = 29;
			}else{
				monthLengths[2] = 28;
			}
			int monthLength = monthLengths[month];
			if(day<1 || day>monthLength){
				return false;
			}
			if(hour < 0 || hour > 23 || minute < 0 || minute>59 || second < 0 || second > 59){
				return false;
			}
			return true;
		} catch (ParseException e) {
			//e.printStackTrace();
			System.out.println("---------------- time:"+time+"  dateFormat:"+dateFormat);
			return false;
		}catch(NumberFormatException e){
			//e.printStackTrace();
			System.out.println("+++++++++++++ time:"+time+"  dateFormat:"+dateFormat);
			return false;
		}
	}

	private static boolean isLeapYear(int year){
		return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) ;
	}

	public static void main(String[] args) {
		System.out.println(TimeUtil.isValidDateTime(" 2020-06-08 15:54:41","yyyy-MM-dd HH:mm:ss"));
		//1604993679717061
		System.out.println(TimeUtil.timeStampToString(1604993679717L));
/*		System.out.println(TimeUtil.getTimestamp("2015-12-21 21:54:36"));
		System.out.println(TimeUtil.getTimestamp("20151221215436","yyyyMMddHHmmss"));
		System.out.println(TimeUtil.getNoYearTime());

		System.out.println(TimeUtil.getFullDateTimeString("7月26日16:08","M月D日|MM-DD","HHMM"));

		Calendar date = new GregorianCalendar();
		System.out.println( date.get(Calendar.YEAR));


		String[] tmpA=StringUtils.stringSplit("2016年12月28日","-|年|月|日|/");
		for(String s:tmpA){
			System.out.println("s="+s);
		}

		double i= Double.parseDouble("0.0");
		System.out.println("i="+i);


        System.out.println("====== JDK7及之前 ======");
        Long l1 = getMillis1();
        //Long l2 = date2Millis(new Date());
        Long l3 = calendar2Millis(Calendar.getInstance());
        //Long l4 = string2Millis(new SimpleDateFormat(FORMAT_STR).format(new Date()) , FORMAT_STR);//为了与以上几个保持一致
        //System.out.println(l1 + "\n" + l2 + "\n" + l3 + "\n" + l4);//会有几毫秒的差别

        System.out.println("====== JDK8 ======");
        Long l5 = getMillis2();
        Long l6 = localDateTime2Millis(LocalDateTime.now());
        Long l7 = localDate2Millis(LocalDate.now());
        Long l8 = clock2Millis(Clock.systemUTC());
        Long l9 = zoneDateTime2Millis(ZonedDateTime.now());
        String ss=LocalDateTime.now().format(DateTimeFormatter.ofPattern(FORMAT_STR)) ;
        System.out.println("ss="+ss);
        Long l10 = string2MillisWithJDK8(ss, FORMAT_STR);//为了与以上几个保持一致
        Long l11 = string2MillisWithJDK8("2015-12-21 21:54:36", FORMAT_STR);
        Long l12 = DateString2MillisWithJDK8("2015-12-21 21:54:36");
        System.out.println(l5 + "\n" + l6 + "\n" + l7 + "\n" + l8 + "\n" + l9 + "\n" + l10+ "\n" + l11+ "\n" + l12);//会有几毫秒的差别

        System.out.println(timeStampToString(System.currentTimeMillis()));


		System.out.println("qqq:"+isValidDateTime("2020-09-01 16:84:00"));*/


	}
}
