package com.lucky.framework.uitls.base;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * 基本工具类
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/14 1:56 下午
 */
public abstract class BaseUtils {

    /**
     * 单词的首字母大写
     * @param word 原始单词
     * @return 首字母变大写后的单词
     */
    public static String capitalizeTheFirstLetter(String word) {
        return word.toUpperCase().substring(0, 1)+word.substring(1, word.length());
    }

    /**
     * 单词的首字母小写
     * @param word 原始单词
     * @return 首字母变小写后的单词
     */
    public static String lowercaseFirstLetter(String word) {
        return word.toLowerCase().substring(0, 1)+word.substring(1, word.length());
    }

    public static String showtime() {
        String id=null;
        id="["+time()+"]  ";
        return id;
    }

    /**
     * 按照指定的格式获取当前时间的字符串
     * @param format 格式（YYYY-MM-DD HH:MM:SS）
     * @return
     */
    public static String time(String format) {
        Date date=new Date();
        SimpleDateFormat sf=
                new SimpleDateFormat(format);
        return sf.format(date);
    }

    /**
     * 获取当前时间
     * @return
     */
    public static String time() {
        Date date=new Date();
        SimpleDateFormat sf=
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(date);
    }

    /**
     * 将Date按照格式转化为String
     * @param date Data对象
     * @param format (eg:yyyy-MM-dd HH:mm:ss)
     * @return
     */
    public static String time(Date date,String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * 按照指定格式将字符串转化为Date对象
     * @param dateStr
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date getDate(String dateStr,String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDate(Date date,String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String getDate(Date date) {
        return getDate(date,"yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 时间运算
     * @param dateStr
     * @param format
     * @param calendarField
     * @param amount
     * @return
     */
    public static Date addDate(String dateStr,String format,int calendarField,int amount) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(getDate(dateStr,format));
        instance.add(calendarField, amount);
        return instance.getTime();
    }

    /**
     * 时间运算
     * @param dateStr
     * @param calendarField
     * @param amount
     * @return
     */
    public static Date addDate(String dateStr,int calendarField,int amount) {
        return addDate(dateStr,"yyyy-MM-dd",calendarField,amount);
    }

    /**
     * 基于当前时间的基础上的时间运算
     * @param calendarField
     * @param amount
     * @return
     */
    public static Date currAddDate(int calendarField,int amount) {
        Calendar instance = Calendar.getInstance();
        instance.add(calendarField, amount);
        return instance.getTime();
    }


    /**
     * 年月日转Date
     * @param dateStr (eg:2020-06-31)
     * @return
     */
    public static Date getDate(String dateStr) {
        return getDate(dateStr,"yyyy-MM-dd");
    }

    /**
     * 年月日时分秒转Date
     * @param dateTimeStr (eg:2020-06-31 12:23:06)
     * @return
     */
    public static Date getDateTime(String dateTimeStr) {
        return getDate(dateTimeStr,"yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 将String转化为java.sqlActuator.Date
     * @param dateStr
     * @return
     */
    public static java.sql.Date getSqlDate(String dateStr){
        return new java.sql.Date(getDate(dateStr,"yyyy-MM-dd").getTime());
    }

    /**
     * java.sqlActuator.Date的运算
     * @param dateStr
     * @param calendarField
     * @param amount
     * @return
     */
    public static java.sql.Date addSqlDate(String dateStr,int calendarField,int amount){
        return new java.sql.Date(addDate(dateStr,calendarField,amount).getTime());
    }

    /**
     * java.sqlActuator.Date的运算
     * @param dateStr
     * @param format
     * @param calendarField
     * @param amount
     * @return
     */
    public static java.sql.Date addSqlDate(String dateStr,String format,int calendarField,int amount){
        return new java.sql.Date(addDate(dateStr,format,calendarField,amount).getTime());
    }

    /**
     * 基于当前时间java.sqlActuator.Date的运算
     * @param calendarField
     * @param amount
     * @return
     */
    public static java.sql.Date currAddSqlDate(int calendarField,int amount){
        return new java.sql.Date(currAddDate(calendarField, amount).getTime());
    }

    /**
     * 获取当前时间的java.sqlActuator.Date
     * @return
     */
    public static java.sql.Date getSqlDate(){
        return new java.sql.Date(new Date().getTime());
    }

    /**
     * 将String转化为java.sqlActuator.Time
     * @param timeStr
     * @return
     */
    public static java.sql.Time getSqlTime(String timeStr){
        return new java.sql.Time(getDate(timeStr,"HH:mm:ss").getTime());
    }

    /**
     * 获取当前时间的java.sqlActuator.Time
     * @return
     */
    public static java.sql.Time getSqlTime(){
        return new java.sql.Time(new Date().getTime());
    }

    /**
     * 将String转化为java.sqlActuator.Timestamp
     * @param timestampStr
     * @return
     */
    public static Timestamp getTimestamp(String timestampStr) {
        return new Timestamp(getDate(timestampStr,"yyyy-MM-dd HH:mm:ss").getTime());
    }

    /**
     * 获取当前时间的java.sqlActuator.Timestamp
     * @return
     */
    public static Timestamp getTimestamp() {
        return new Timestamp(new Date().getTime());
    }

    /**
     * 判断该类型是否为java类型
     * @param clzz
     * @return
     */
    public static boolean isJavaClass(Class<?> clzz) {
        return clzz!=null&&clzz.getClassLoader()==null;
    }

    /**
     * 小数转百分数
     * @param d 待转化的小数
     * @param IntegerDigits 小数点前保留几位
     * @param FractionDigits 小数点后保留几位
     * @return
     */
    public static String getPercentFormat(double d, int IntegerDigits, int FractionDigits) {
        NumberFormat nf = java.text.NumberFormat.getPercentInstance();
        nf.setMaximumIntegerDigits(IntegerDigits);//小数点前保留几位
        nf.setMinimumFractionDigits(FractionDigits);// 小数点后保留几位
        String str = nf.format(d);
        return str;
    }

    /**
     * 小数转百分数
     * @param d 待转化的小数 99.333%
     * @return
     */
    public static String getPercentFormat(double d){
        return getPercentFormat(d,3,3);
    }

    /**
     * 生成一个10000以内的随机数
     * @return
     */
    public static int getRandomNumber(){
        int number=(int)(Math.random()*10000);
        return number;
    }

    /**
     * 字符串复制
     * @param str 待复制的字符串
     * @param copyNum 复制次数
     * @param joinChar 连接符
     * @return
     */
    public static String strCopy(String str,int copyNum,String joinChar){
        return String.join(joinChar, Collections.nCopies(copyNum, str));
    }

}
