package com.lucky.scaffold.project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lucky.scaffold.file.FileCopy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/9/23 12:29
 */
public class Constant {

    /** 项目的组织ID */
    public static final String GROUP_ID="@:GroupId";
    /** 项目ID */
    public static final String ARTIFACT_ID="@:ArtifactId";
    /** 版本信息 */
    public static final String VERSION="@:Version";
    /** 项目名 */
    public static final String PROJECT_NAME="@:Name";
    /** 启动类全限定名 */
    public static final String MAIN_CLASS="@:MainClass";
    /** 替他的Maven依赖*/
    public static final String MAVEN_DEPENDENCY="@:Dependency";
    /** 启动类类名 */
    public static final String MAIN_NAME="@:MainName";
    public static final String $="@L@";
    public static final String PACKAGE="@:Package";
    public static final String JAVA="/src/main/java/";
    public static final String TOOL="/src/main/tool/scaffold/";
    public static final String RESOURCES="/src/main/resources/";
    public static final String TEST_JAVA="/src/test/java/";
    public static final String TEST_RESOURCES="/src/test/resources/";
    public static final Gson gson=new Gson();

    public static Map<String,String> getMavenDependency() throws IOException {
        BufferedReader br=new BufferedReader(new InputStreamReader(FileCopy.class.getResourceAsStream("/temp/dependency-template.json"),"UTF-8"));
        Map<String, String> map = gson.fromJson(br, new TypeToken<Map<String, String>>() {
        }.getType());
        return map;
    }

    public static Map<String,String> getMavenDependencyAlias() throws UnsupportedEncodingException {
        BufferedReader br=new BufferedReader(new InputStreamReader(FileCopy.class.getResourceAsStream("/temp/dependency-alias-template.json"),"UTF-8"));
        Map<String, String> map = gson.fromJson(br, new TypeToken<Map<String, String>>() {
        }.getType());
        return map;
    }

    public static int[] getMaxLength() throws UnsupportedEncodingException {
        int[] maxLength={0,0};
        Map<String, String> mavenDependencyAlias = getMavenDependencyAlias();
        for(Map.Entry<String,String> entity:mavenDependencyAlias.entrySet()){
            int currValueLength=entity.getValue().length();
            if(currValueLength>maxLength[1]){
                maxLength[1]=currValueLength;
            }
            int currKeyLength=entity.getKey().length();
            if(currKeyLength>maxLength[0]){
                maxLength[0]=currKeyLength;
            }
        }
        return maxLength;
    }

    public static StringBuilder format(Map.Entry<String,String> entity,int[] maxLength){
        StringBuilder format=new StringBuilder("(");
        String key=entity.getKey();
        String value=entity.getValue();
        int keyDifference=maxLength[0]-key.length();
        int valueDifference=maxLength[1]-value.length();
        format.append(key);
        for(int i=0;i<keyDifference;i++){
            format.append(" ");
        }
        format.append(")").append(value);
        for(int i=0;i<valueDifference;i++){
            format.append(" ");
        }
        return format.append("  ");
    }

    public static String getMavenDependencyTables() {
        try {
            Map<String, String> mavenDependencyAlias = getMavenDependencyAlias();
            StringBuilder tables=new StringBuilder("[\n");
            int index=1;
            int[] maxLength = getMaxLength();
            for(Map.Entry<String,String> entity:mavenDependencyAlias.entrySet()){
                if(index%3==1){
                    tables.append("    ");
                }
                tables.append(format(entity,maxLength));
                if(index%3==0){
                    tables.append("\n");
                }

                index++;
            }
            String returnTables = tables.toString().trim();
            returnTables=returnTables.endsWith(",")?returnTables.substring(0,returnTables.length()-1):returnTables;
            return returnTables+"\n]";
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(getMavenDependencyTables());
    }

}
