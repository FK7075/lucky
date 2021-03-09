package com.lucky.utils.conversion.unit;

import com.lucky.utils.conversion.JavaConversion;


/**
 * 文件单位转换工具类
 * @author fk7075
 * @version 1.0.0
 * @date 2021/3/9 下午11:04
 */
public abstract class FileUnitUtils {

    private final static long UNIT=1024;

    public static long toKb(String fileUnit){
        String size=fileUnit;
        size=size.trim().toUpperCase();
        long number;
        if(size.endsWith("KB")){
            size=size.substring(0,size.length()-2).trim();
            number= (long) JavaConversion.strToBasic(size,long.class,true);
            return number;
        }

        if(size.endsWith("K")){
            size=size.substring(0,size.length()-1).trim();
            number= (long) JavaConversion.strToBasic(size,long.class,true);
            return number;
        }

        if(size.endsWith("MB")){
            size=size.substring(0,size.length()-2).trim();
            number= (long) JavaConversion.strToBasic(size,long.class,true);
            return mbToKkb(number);
        }

        if(size.endsWith("M")){
            size=size.substring(0,size.length()-1).trim();
            number= (long) JavaConversion.strToBasic(size,long.class,true);
            return mbToKkb(number);
        }

        if(size.endsWith("GB")){
            size=size.substring(0,size.length()-2).trim();
            number= (long) JavaConversion.strToBasic(size,long.class,true);
            return gbToKb(number);
        }

        if(size.endsWith("G")){
            size=size.substring(0,size.length()-1).trim();
            number= (long) JavaConversion.strToBasic(size,long.class,true);
            return  gbToKb(number);
        }
        if(size.endsWith("TB")){
            size=size.substring(0,size.length()-2).trim();
            number= (long) JavaConversion.strToBasic(size,long.class,true);
            return tbToKb(number);
        }

        if(size.endsWith("T")){
            size=size.substring(0,size.length()-1).trim();
            number= (long) JavaConversion.strToBasic(size,long.class,true);
            return  tbToKb(number);
        }
       try {
           return (long) JavaConversion.strToBasic(size,long.class,true);
       }catch (Exception e){
           throw new RuntimeException("Wrong file unit: `"+fileUnit+"`");
       }
    }

    public static long mbToKkb(long mb){
        return mb * UNIT;
    }

    public static long gbToKb(long gb){
        return gb * UNIT * UNIT;
    }

    public static long tbToKb(long tb){
        return tb * UNIT * UNIT * UNIT;
    }

    public static void main(String[] args) {
        String fileUnit="(15*4-20)T";
        System.out.println(toKb(fileUnit));
        long i=1024*1024*1024;
        System.out.println(i);
    }

}
