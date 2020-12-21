package com.lucky.scaffold.project;

import lombok.Data;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/9/23 12:28
 */
@Data
public class ProjectInFo {

    private static final String dir = System.getProperty("user.dir");

    private static Stack<String> operating;

    private static Map<String,String> prompt;

    private static Map<String,String> localMavenMavenDependency;

    private static Map<String,String> localMavenMavenDependencyAlias;

    private static Set<String> addMavenDependencyName=new HashSet<>();

    public static StringBuilder addMavenDependencyString=new StringBuilder("");

    private String groupId;

    private String mavenRepository;

    private String scaffold;

    private String projectPath=dir;

    private String artifactId;

    private String version="1.0-SNAPSHOT";

    private String projectName;

    private String mainClassName;

    private String mainClass;


    static {
        try {
            localMavenMavenDependency=Constant.getMavenDependency();
            localMavenMavenDependencyAlias=Constant.getMavenDependencyAlias();
        } catch (IOException e) {
            e.printStackTrace();
        }
        operating=new Stack<>();
        operating.push("o");
        operating.push("a");
        operating.push("g");
        prompt=new HashMap<>();
        prompt.put("g","请输入 groupId                  _> : ");
        prompt.put("a","请输入 artifactId               _> : ");
        prompt.put("v","请输入 version                  _> : ");
        prompt.put("m","请输入 MavenRepository          _> : ");
        prompt.put("p","请输入生成项目的父级目录           _> : ");
        prompt.put("d",Constant.getMavenDependencyTables()+"\n在上表中选择您需要的Maven依赖      _> : ");
        prompt.put("o","所有必要配置已配置完成，请输入任意非命令字符结束输入 _>:");
    }

    private ProjectInFo(){}

    private static Properties readCfg() throws IOException {
        String cfgPath=dir.endsWith(File.separator)?dir+"build.properties":dir+File.separator+"build.properties";
        BufferedReader br=null;
        if(new File(cfgPath).exists()){
            br=new BufferedReader(new InputStreamReader(new FileInputStream(cfgPath),"UTF-8"));
        }else{
            br=new BufferedReader(new InputStreamReader(ProjectInFo.class.getResourceAsStream("/build.properties"),"UTF-8"));
        }
        Properties properties=new Properties();
        properties.load(br);
        return properties;
    }

    /**
     * 从配置文件中获取项目信息
     * @return
     */
    public static ProjectInFo getProjectInFo(){
        ProjectInFo pif=new ProjectInFo();
        Properties properties=null;
        try {
            properties=readCfg();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Field[] fields = ProjectInFo.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName=field.getName();
            if(properties.containsKey(fieldName)){
                fieldSet(pif,field,properties.get(fieldName));
            }
        }
        return pif;
    }

    /**
     * 从键盘获取项目信息
     * @return
     */
    public static ProjectInFo inputProjectInfo(){
        JackLamb.welcome();
        System.out.println("欢迎使用Lucky脚手架，请依照指引完成生成Lucky项目的必要配置！");
        help();
        ProjectInFo pif=new ProjectInFo();
        Scanner sc = new Scanner(System.in);
        while (!operating.isEmpty()){
            String currOP=operating.pop();
            switch (currOP){
                case "g":{
                    System.out.print(prompt.get(currOP));
                    String in = sc.nextLine();
                    if(addOP(in,currOP,pif)){
                        if(isPackage(in)){
                            pif.setGroupId(in);
                        }else{
                            System.out.println("[error] -- 不合法的groupId："+in);
                            operating.push("g");
                        }
                    }
                    break;
                }
                case "a":{
                    System.out.print(prompt.get(currOP));
                    String in = sc.nextLine();
                    if(addOP(in,currOP,pif)){
                        if(isValidFileName(in)){
                            pif.setArtifactId(in);
                        }else{
                            System.out.println("[error] -- 不合法的artifactId："+in);
                            operating.push("a");
                        }
                    }
                    break;
                }
                case "v":{
                    System.out.print(prompt.get(currOP));
                    String in = sc.nextLine();
                    if(addOP(in,currOP,pif)){
                        pif.setVersion(in);
                    }
                    break;
                }
                case "m":{
                    System.out.print(prompt.get(currOP));
                    String in = sc.nextLine();
                    if(addOP(in,currOP,pif)){
                        if(checkPathValid(in)){
                            pif.setMavenRepository(in);
                        }else{
                            System.out.println("[error] -- 不合法的文件路径："+in);
                            operating.push("m");
                        }
                    }
                    break;
                }
                case "p":{
                    System.out.print(prompt.get(currOP));
                    String in = sc.nextLine();
                    if(addOP(in,currOP,pif)){
                        if(checkPathValid(in)){
                            pif.setProjectPath(in);
                        }else{
                            System.out.println("[error] -- 不合法的文件路径："+in);
                            operating.push("p");
                        }
                    }
                    break;
                }
                case "o":{
                    System.out.print(prompt.get(currOP));
                    String in = sc.nextLine();
                    addOP(in,currOP,pif);
                    break;
                }
                case "d":{
                    System.out.print(prompt.get(currOP));
                    String in = sc.nextLine();
                    if(addOP(in,currOP,pif)){
                        in=in.replaceAll(" ","");
                        String[] dArray=in.split(",");
                        List<String> errD=new ArrayList<>();
                        for (String d : dArray) {
                            if(d.startsWith("-")){
                                if(!localMavenMavenDependencyAlias.containsKey(d)){
                                    errD.add(d);
                                }
                            }else{
                                if(!localMavenMavenDependency.containsKey(d)){
                                    errD.add(d);
                                }
                            }
                        }
                        if(errD.isEmpty()){
                            for (String d : dArray) {
                                if(d.startsWith("-")){
                                    d=localMavenMavenDependencyAlias.get(d);
                                }
                                if(!addMavenDependencyName.contains(d)){
                                    addMavenDependencyString.append(localMavenMavenDependency.get(d));
                                    addMavenDependencyName.add(d);
                                }
                            }
                        }else{
                            System.out.println("[error] -- 本次录入失败，发现未收录的Maven依赖："+errD);
                            operating.push("d");
                        }
                    }
                    break;
                }
            }
        }
        return pif;
    }


    public static boolean addOP(String in, String op, ProjectInFo currPif){
        switch (in){
            case "":{
                operating.push(op);
                return false;
            }
            case "@g":{
                operating.remove("g");
                operating.push(op);
                operating.push("g");
                return false;
            }
            case "@a":{
                operating.remove("a");
                operating.push(op);
                operating.push("a");
                return false;
            }
            case "@v":{
                operating.remove("v");
                operating.push(op);
                operating.push("v");
                return false;
            }
            case "@m":{
                operating.remove("m");
                operating.push(op);
                operating.push("m");
                return false;
            }
            case "@h":{
                operating.push(op);
                help();
                return false;
            }
            case "@p":{
                operating.remove("p");
                operating.push(op);
                operating.push("p");
                return false;
            }
            case "@cfg":{
                operating.push(op);
                showCurrCfg(currPif);
                return false;
            }
            case "@d":{
                operating.remove("d");
                operating.push(op);
                operating.push("d");
                return false;
            }
            default:return true;
        }
    }

    /**
     * 查看帮助信息
     */
    private static void help(){
        System.out.println("----------------------------------------------------------------------------------------------------\n帮助文档\n---------------------------------------------------------------------------------------------------- ");
        System.out.println("@g   -> [必要] 重新输入groupId");
        System.out.println("@a   -> [必要] 重新输入artifactId");
        System.out.println("@v   -> [选择] 重新输入version（默认值：1.0-SNAPSHOT）");
        System.out.println("@m   -> [选择] 重新输入MavenRepository（默认值：NULL，当不为NULL时，脚手架程序会将Lucky库导入你所配置的本地Maven创库中）");
        System.out.println("@p   -> [选择] 重新输入生成项目的父级目录（默认值：脚手架程序所在目录）");
        System.out.println("@d   -> [选择] 添加其他Maven依赖");
        System.out.println("@h   -> 查看帮助文档");
        System.out.println("@cfg -> 查看当前配置信息\n----------------------------------------------------------------------------------------------------\n");
    }

    /**
     * 显示当前配置
     */
    private static void showCurrCfg(ProjectInFo currPif){
        System.out.println("----------------------------------------------------------------------------------------------------\n当前配置\n---------------------------------------------------------------------------------------------------- ");
        System.out.printf("MavenRepository     -> %s\n",currPif.getMavenRepository());
        System.out.printf("ProjectPath         -> %s\n",currPif.getProjectPath());
        System.out.printf("groupId             -> %s\n",currPif.getGroupId());
        System.out.printf("artifactId          -> %s\n",currPif.getArtifactId());
        System.out.printf("version             -> %s\n",currPif.getVersion());
        System.out.printf("MavenDependency     -> %s\n",currPif.addMavenDependencyName);
        System.out.println("----------------------------------------------------------------------------------------------------\n");
    }



    public static ProjectInFo getFinalProjectInFo(ProjectInFo pif){
        pif.setArtifactId(pif.getArtifactId()
                .replaceAll(" +"," ")
                .replaceAll("_+","_")
                .replaceAll("-+","-"));
        pif.setGroupId(pif.getGroupId()
                .replaceAll(" +"," ")
                .replaceAll("_+","_")
                .replaceAll("-+","-"));
        pif.setProjectName(pif.getArtifactId());
        pif.setMainClassName(getMainClassName(pif.getArtifactId())+"Application");
        pif.setMainClass(pif.getGroupId()+"."+pif.getMainClassName());
        return pif;
    }

    private static void fieldSet(Object source, Field field, Object value){
        field.setAccessible(true);
        try {
            field.set(source,value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static String getMainClassName(String sourceStr){
        String[] A1 = sourceStr.split(" ");
        StringBuilder sb=new StringBuilder();
        for (String s : A1) {
            sb.append(capitalizeFirstLetter(s));
        }
        String[] A2 = sb.toString().split("_");
        sb=new StringBuilder();
        for (String s : A2) {
            sb.append(capitalizeFirstLetter(s));
        }
        String[] A3 = sb.toString().split("-");
        sb=new StringBuilder();
        for (String s : A3) {
            sb.append(capitalizeFirstLetter(s));
        }
        return sb.toString();
    }

    /**
     * 单词的首字母大写
     * @param tableName 原始单词
     * @return 首字母变大写后的单词
     */
    public static String capitalizeFirstLetter(String tableName) {
        return tableName.toUpperCase().substring(0, 1)+tableName.substring(1, tableName.length());
    }

    private static final Pattern linux_path_pattern=Pattern.compile("(/([a-zA-Z0-9][a-zA-Z0-9_\\-]{0,255}/)*([a-zA-Z0-9][a-zA-Z0-9_\\-]{0,255})|/)");
    private static final Pattern windows_path_pattern=Pattern.compile("(^[a-zA-Z]:((\\\\|/)([a-zA-Z0-9\\-_]){1,255}){1,255}|([A-Z]:(\\\\|/)))");
    private static final String OS=System.getProperty("os.name").toLowerCase();

    public static boolean checkPathValid(String path){
        if (OS.contains(OSType.LINUX.name().toLowerCase())||OS.contains(OSType.MAC.name().toLowerCase())) {
            return checkPatternMatch(linux_path_pattern,path);
        }
        if (OS.contains(OSType.WINDOWS.name().toLowerCase())){
            return checkPatternMatch(windows_path_pattern,path);
        }
        return false;
    }
    private static boolean checkPatternMatch(Pattern pattern,String target){
        return pattern.matcher(target).matches();
    }

    private static final Pattern JAVA_PACKAGE=Pattern.compile("[a-zA-Z]+[0-9a-zA-Z_]*(\\.[a-zA-Z]+[0-9a-zA-Z_]*)*");

    public static boolean isPackage(String packageStr){
        return JAVA_PACKAGE.matcher(packageStr).matches();
    }


    public static boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.length() > 255||fileName.startsWith(" ")) {
            return false;
        } else{
            String[] d={"?","*",":","\"","<",">","\\","/","|"};
            for (String s : d) {
                if(fileName.contains(s)){
                    return false;
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {
//        System.out.println(getFinalProjectInFo(inputProjectInfo()));
        System.out.println(isPackage("com.vfv.w7ewr.ku"));
    }



}

