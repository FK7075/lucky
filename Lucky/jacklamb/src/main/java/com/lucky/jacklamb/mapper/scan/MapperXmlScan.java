package com.lucky.jacklamb.mapper.scan;

import com.lucky.jacklamb.mapper.exception.JarScanException;
import com.lucky.jacklamb.mapper.xml.MapperXMLParsing;
import com.lucky.utils.config.ConfigUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/18 0018 18:13
 */
public class MapperXmlScan {

    private static String mapperXmlRoot="mapper";

    static {
        Map<String, Object> map = ConfigUtils.getYamlConfAnalysis().getMap();
        if(map.containsKey("jacklamb")){
            Map<String, Object> jacklamb = (Map<String, Object>) map.get("jacklamb");
            if(jacklamb.containsKey("mapper-xml")){
                mapperXmlRoot=jacklamb.get("mapper-xml").toString();
                mapperXmlRoot=mapperXmlRoot.startsWith("classpath:")?
                        mapperXmlRoot.substring(10):mapperXmlRoot;
                mapperXmlRoot=mapperXmlRoot.startsWith("/")?
                        mapperXmlRoot.substring(1):mapperXmlRoot;
                mapperXmlRoot=mapperXmlRoot.endsWith("/")?
                        mapperXmlRoot:mapperXmlRoot+"/";
            }
        }
    }

    public static Map<String,Map<String,String>> getAllMapperSql(){
        Set<MapperXMLParsing> mapperXmlSet=getAllMapperXml();
        Map<String,Map<String,String>> mapperSqls=new HashMap<>();
        for (MapperXMLParsing mapXmlp : mapperXmlSet) {
            Map<String, Map<String, String>> sqlMap = mapXmlp.getXmlMap();
            for(Map.Entry<String,Map<String,String>> en:sqlMap.entrySet()){
                String key = en.getKey();
                Map<String, String> map = en.getValue();
                if(mapperSqls.containsKey(key)){
                    Map<String, String> contextMap = mapperSqls.get(key);
                    for(Map.Entry<String,String> e:map.entrySet()){
                        if(contextMap.containsKey(e.getKey())){
                            throw new RuntimeException("同一个Mapper接口方法的SQL配置出现在了两个xml配置文件中！ "+key+"."+e.getKey()+"(XXX)");
                        }else{
                            contextMap.put(e.getKey(),e.getValue());
                        }
                    }
                }else{
                    mapperSqls.put(key,map);
                }
            }
        }
        return mapperSqls;
    }


    private static Set<MapperXMLParsing> getAllMapperXml(){
        URL resource = MapperXMLParsing.class.getClassLoader().getResource("");
        if(resource!=null&&!resource.getPath().contains(".jar!/")) {
            return getAllMapperXmlByPackage();
        }
        return getAllMapperXmlByJar();
    }

    private static Set<MapperXMLParsing> getAllMapperXmlByJar(){
        JarFile jarFile = null;
        Set<MapperXMLParsing> xmls=new HashSet<>();
        String jarpath=getJarFile();
        try {
            jarFile = new JarFile(jarpath);
        } catch (IOException e) {
            throw new JarScanException("找不到jar文件：["+jarpath+"]",e);
        }
        Enumeration<JarEntry> entrys = jarFile.entries();
        while (entrys.hasMoreElements()) {
            JarEntry entry = entrys.nextElement();
            String name = entry.getName();
            if (name.endsWith(".xml") && name.startsWith(mapperXmlRoot)) {
                try{
                    if(MapperXMLParsing.isMapperXml(MapperXmlScan.class.getResourceAsStream("/"+name))){
                        xmls.add(new MapperXMLParsing(MapperXmlScan.class.getResourceAsStream("/"+name)));
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return xmls;
    }

    private static String getJarFile(){
        String jarpath=MapperXmlScan.class.getResource("").getPath();
        jarpath=jarpath.substring(5);
        if(jarpath.contains(".jar!")){
            if(jarpath.contains(":")){
                jarpath=jarpath.substring(1, jarpath.indexOf(".jar!")+4);
            }else{
                jarpath=jarpath.substring(0, jarpath.indexOf(".jar!")+4);
            }
        }
        return jarpath;
    }

    private static Set<MapperXMLParsing> getAllMapperXmlByPackage(){
        Set<MapperXMLParsing> xmls=new HashSet<>();
        URL url = MapperXmlScan.class.getClassLoader().getResource(mapperXmlRoot);
        if(url==null) {
            return xmls;
        }
        try {
            File mapperXml=new File(url.getFile());
            getMapperXmlByPackage(mapperXml,xmls);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return xmls;
    }

    private static void getMapperXmlByPackage(File file, Set<MapperXMLParsing> mapXmls) throws Exception {
        if(file.isFile()&&file.getName().endsWith(".xml")&&MapperXMLParsing.isMapperXml(new FileInputStream(file))){
            mapXmls.add(new MapperXMLParsing(file));
        }else if(file.isDirectory()){
            File[] sonFiles = file.listFiles();
            for (File sonf : sonFiles) {
                getMapperXmlByPackage(sonf,mapXmls);
            }
        }
    }
}
