package com.lucky.jacklamb.mapper.xml;

import com.lucky.jacklamb.mapper.LuckyMapper;
import com.lucky.jacklamb.mapper.exception.XMLParsingException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/8/5 11:05 下午
 */
public class MapperXMLParsing {

    private static final String MAPPER_INTERFACE="mapper";
    private static final String CLASS="class";
    private static final String NAME="name";
    private static final String METHOD="method";

    private final Map<String, Map<String,String>> xmlMap=new HashMap<>();

    public Map<String, Map<String, String>> getXmlMap() {
        return xmlMap;
    }

    public boolean isExistClass(Class<?> mapperClass){
        return xmlMap.containsKey(mapperClass.getName());
    }

    public Map<String, String> getMapperSql(Class<?> mapperClass){
        return xmlMap.get(mapperClass.getName());
    }

    private final BufferedReader xmlReader;

    public static boolean isMapperXml(InputStream inputStream) throws Exception {
       BufferedReader xmlReader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(xmlReader);
        Element root = document.getRootElement();
        return "mapper-config".equals(root.getName());
    }

    public MapperXMLParsing(String xmlPath) throws IOException {
        xmlReader=new BufferedReader(new InputStreamReader(new FileInputStream(xmlPath),"UTF-8"));
        xmlPars();
    }

    public MapperXMLParsing(File xmlFile) throws IOException {
        xmlReader=new BufferedReader(new InputStreamReader(new FileInputStream(xmlFile),"UTF-8"));
        xmlPars();
    }

    public MapperXMLParsing(InputStream xmlInputStream) throws IOException {
        xmlReader=new BufferedReader(new InputStreamReader(xmlInputStream,"UTF-8"));
        xmlPars();
    }

    public MapperXMLParsing(Reader xmlReader){
        this.xmlReader=new BufferedReader(xmlReader);
        xmlPars();
    }

    public void xmlPars(){
        try{
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(xmlReader);
            Element root = document.getRootElement();
            List<Element> mapperInterfaces = root.elements(MAPPER_INTERFACE);
            for (Element mapInter : mapperInterfaces) {
                String mapperClass=mapInter.attributeValue(CLASS);
                Map<String,String> mapperSql=new HashMap<>();
                List<Element> methodElements = mapInter.elements(METHOD);
                for (Element mElement : methodElements) {
                    String method = mElement.attributeValue(NAME);
                    if(mapperSql.containsKey(method)){
                        throw new RuntimeException("同一个Mapper接口方法的SQL配置在同一个配置文件中出现了两次！ "+mapperClass+"."+method+"(XXX)");
                    }else{
                        String sql = mElement.getText();
                        sql=sql.replaceAll("\\$", "LUCKY_RDS_CHAR_DOLLAR_0721");
                        sql=sql.replaceAll("\r\n"," ").replaceAll("\n"," ").trim();
                        sql=sql.replaceAll("&lt;","<").replaceAll("&gt;",">");
                        sql=sql.replaceAll("LUCKY_RDS_CHAR_DOLLAR_0721","\\$" );
//                        mElement.getText().replaceAll("\r\n"," ").replaceAll("\n"," ").trim().replaceAll(" +"," ")
                        mapperSql.put(method,sql);
                    }
                }
                xmlMap.put(mapperClass,mapperSql);
            }
        }catch (DocumentException e){
            throw new XMLParsingException(e);
        }
    }
}
