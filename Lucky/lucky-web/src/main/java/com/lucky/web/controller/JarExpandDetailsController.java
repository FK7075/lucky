package com.lucky.web.controller;

import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.MethodUtils;
import com.lucky.utils.reflect.ParameterUtils;
import com.lucky.web.annotation.*;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.enums.Rest;
import com.lucky.web.mapping.*;
import com.lucky.web.webfile.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 *
 * @author fk
 * @version 1.0
 * @date 2020/12/8 0008 16:13
 */
public abstract class JarExpandDetailsController extends JarExpandController{
    private static final Logger log= LoggerFactory.getLogger("c.l.web.controller.JarExpandDetailsController");
    private static final String AUTHOR="lucky";
    private static final String VERSION="2.0.0";
    private static  String TEMP_FOLDER=System.getProperty("java.io.tmpdir");
    static {
        TEMP_FOLDER=TEMP_FOLDER.endsWith(File.separator)?TEMP_FOLDER:TEMP_FOLDER+File.separator;
        TEMP_FOLDER=TEMP_FOLDER+"lucky"+File.separator+"jarExpand"+File.separator;
    }

    /**
     * 使用文件上传的方式添加一个Jar扩展
     * @param jar 外部扩展的Jar包
     * @param expandName 扩展名
     * @param groupId 组织名
     * @throws IOException
     */
    @PostMapping("/uploadJar")
    @Description(
            desc="上传Jar扩展",
            author = AUTHOR,
            version = VERSION,
            comment = "添加一个外部Jar扩展，这个外部扩展由用户上传，上传的扩展的格式必须为`.jar`,否则将不会生效！")
    public void uploadJarExpand(MultipartFile jar,String expandName,String groupId) throws IOException {
        uploadJar(jar,expandName,groupId);
    }

    /**
     * 添加一个本地已存在的Jar扩展
     * @param jarExpand Jar扩展的信息
     * @throws IOException
     */
    @RequestMapping("/addJar")
    @Description(
            desc="添加本地Jar扩展",
            author = AUTHOR,
            version = VERSION,
            comment = "添加一个Jar扩展，这个扩展应该存在于本地，扩展的格式必须为`.jar`,否则将不会生效！如果这个扩展的扩展名存在于`无效名单`中，则只会恢复这个扩展，不会执行Jar包解析操作！")
    public void addJar(JarExpand jarExpand) throws IOException {
        andExpandJar(jarExpand);
    }

    @GetMapping("/addJarBlackList")
    @Description(
            desc="将一个扩展列入黑名单",
            author = AUTHOR,
            version = VERSION,
            comment = "将一个扩展列入黑名单，进入黑名单的扩展将无法再被外部访问！黑名单中的扩展是可以被恢复的！")
    public void addBlackList(String expandName){
        deleteExpandJar(expandName);
    }

    @GetMapping("/deleteJarExpand")
    @Description(
            desc="删除一个扩展",
            author = AUTHOR,
            version = VERSION,
            comment = "删除一个扩展，被删除的扩展的信息会被彻底抹除，不可被恢复！")
    public void deleteJarExpand(String expandName){
        removerExpandJar(expandName);
    }

    /**
     * 获取所有过期的扩展信息【被逻辑删除的扩展】
     * @return
     */
    @ResponseBody
    @GetMapping("/invalidJarExpand")
    @Description(
            desc="获取扩展黑名单",
            author = AUTHOR,
            version = VERSION,
            comment = "获取所有黑名单中的扩展信息[扩展名、jar包位置、项目的GroupId]")
    public Collection<JarExpand> getLogicDelJarExpand(){
        Set<String> deleteExpand = getUrlMappingCollection().getDeleteExpand();
        Map<String, JarExpand> expandInfoMap = getUrlMappingCollection().getExpandInfoMap();
        List<JarExpand> invalid=new ArrayList<>(deleteExpand.size());
        for (String expandName : deleteExpand) {
            invalid.add(expandInfoMap.get(expandName));
        }
        return invalid;
    }

    /**
     * 获取所有的扩展信息
     * @return
     */
    @ResponseBody
    @GetMapping("/allJarExpand")
    @Description(
            desc="获取所有扩展",
            author = AUTHOR,
            version = VERSION,
            comment = "获取所有扩展的信息[扩展名、jar包位置、项目的GroupId]")
    public Collection<JarExpand> getAllJarExpand(){
        return getUrlMappingCollection().getExpandInfoMap().values();
    }

    /**
     * 获取所有Mapping的详细信息
     * @return
     */
    @ResponseBody
    @GetMapping("/jarExpandInfo")
    @Description(
            desc="获取所有映射的详细描述",
            author = AUTHOR,
            version = VERSION,
            comment = "获取所有URL映射和Exception映射的所有详细信息[描述信息、映射信息、参数信息、返回信息和访问信息]。")
    public List<MappingDto> jarExpandInfo(){
        return new MappingDto().getExpandMappingInfo();
    }

    /**
     * 禁用一个UrlMapping
     * @param id UrlMapping的唯一ID
     */
    @GetMapping("/disableUrl")
    @Description(
            desc="禁用一个URL映射",
            author = AUTHOR,
            version = VERSION,
            comment = "禁用的URL映射，需要传入这个URL映射的唯一ID，ID不正确此操作将不会生效")
    public void disableUrlMapping(String id){
        if(Assert.isBlankString(id)){
            log.info("传入的ID为NULL，无法执行UrlMapping禁用操作！");
            return;
        }
        UrlMapping urlMapping = getUrlMappingCollection().getUrlMappingById(id);
        if(Assert.isNull(urlMapping)){
            log.info("没有找到ID为`{}`的UrlMapping,无法执行UrlMapping禁用操作！",id);
            return;
        }
        urlMapping.setDisable(true);
        log.debug("ID为`{}`的UrlMapping组件已经被禁用！该组件处理的URL为`{}`",id,urlMapping.getUrl());
    }

    @GetMapping("/restoreUrl")
    @Description(
            desc="恢复一个URL映射",
            author = AUTHOR,
            version = VERSION,
            comment = "恢复一个被禁用的URL映射，需要传入这个URL映射的唯一ID，ID不正确此操作将不会生效")
    public void restoreUrlMapping(String id){
        if(Assert.isBlankString(id)){
            log.info("传入的ID为NULL，无法执行UrlMapping恢复操作！");
            return;
        }
        UrlMapping urlMapping = getUrlMappingCollection().getUrlMappingById(id);
        if(Assert.isNull(urlMapping)){
            log.info("没有找到ID为`{}`的UrlMapping,无法执行UrlMapping恢复操作！",id);
            return;
        }
        urlMapping.setDisable(false);
        log.debug("ID为`{}`的UrlMapping组件已经恢复！该组件处理的URL为`{}`",id,urlMapping.getUrl());
    }



    class MappingDto{
        private String expandName;
        private List<UrlMappingDto> urls;
        private List<ExceptionMappingDto> exceptions;

        private MappingDto(){};

         public MappingDto(String expandName, List<UrlMappingDto> urls, List<ExceptionMappingDto> exceptions) {
             this.expandName = expandName;
             this.urls = urls;
             this.exceptions = exceptions;
         }

         public List<MappingDto> getExpandMappingInfo(){
             UrlMappingCollection urlMappingCollection = getUrlMappingCollection();
             ExceptionMappingCollection exceptionMappingCollection = getExceptionMappingCollection();
             Map<String, UrlMappingCollection> urlMap = urlMappingCollection.getExpandMap();
             Map<String, ExceptionMappingCollection> expMap = exceptionMappingCollection.getExpandMap();
             Set<String> keys = urlMap.keySet();
             List<MappingDto> list=new ArrayList<>(keys.size()+1);
             list.add(new MappingDto("default"
                     ,UrlMappingDto.getUrlMappingDtoList(urlMappingCollection)
                     ,ExceptionMappingDto.getExceptionMappingDtoList(exceptionMappingCollection)));
             for (String key : keys) {
                list.add(new MappingDto(key,
                        UrlMappingDto.getUrlMappingDtoList(urlMap.get(key)),
                        ExceptionMappingDto.getExceptionMappingDtoList(expMap.get(key))));
             }
             return list;
        }

    }

    static class BaseMapping{
        protected String id;
        protected boolean isDisable;
        protected String controller;
        protected String method;
        protected Rest rest;
        protected MappingDescription description;

        public BaseMapping(Mapping mapping){
            id=mapping.getId();
            isDisable=mapping.isDisable();
            controller=mapping.getObject().getClass().getName();
            method=mapping.getMapping().getName();
            description=new MappingDescription(mapping.getObject().getClass(),mapping.getMapping());
            rest=mapping.getRest();
        }

    }

    static class UrlMappingDto extends BaseMapping{
        private String url;
        private RequestMethod[] requestMethods;
        private String[] paramTypes;
        private String[] paramNames;
        private String returnType;

        private UrlMappingDto(UrlMapping urlMapping){
            super(urlMapping);
            url=urlMapping.getUrl();
            requestMethods=urlMapping.getMethods();
            Parameter[] parameters = urlMapping.getParameters();
            String[] paramNames = MethodUtils.getParamNamesByParameter(urlMapping.getMapping());
            paramTypes=new String[parameters.length];
            this.paramNames=new String[parameters.length];
            for (int i = 0,j=parameters.length; i < j; i++) {
                paramTypes[i]=parameters[i].getType().getName();
                this.paramNames[i]=ParameterUtils.getParamName(parameters[i],paramNames[i]);
            }
            this.returnType =urlMapping.getMapping().getReturnType().getName();
        }

        public static List<UrlMappingDto> getUrlMappingDtoList(UrlMappingCollection urlMappings){
            List<UrlMappingDto> list=new ArrayList<>(urlMappings.size());
            for (UrlMapping urlMapping : urlMappings) {
                list.add(new UrlMappingDto(urlMapping));
            }
            return list;
        }
    }

    static class ExceptionMappingDto extends BaseMapping{
        private String[] exceptions;
        private String[] scopes;

        private ExceptionMappingDto(ExceptionMapping exceptionMapping){
            super(exceptionMapping);
            scopes=exceptionMapping.getScopes();
            if(scopes.length==0){
                scopes=new String[]{"global"};
            }
            Class<? extends Throwable>[] exceptions = exceptionMapping.getExceptions();
            this.exceptions =new String[exceptions.length];
            for (int i = 0,j=exceptions.length; i < j; i++) {
                this.exceptions[i]=exceptions[i].getName();
            }
        }

        public static List<ExceptionMappingDto> getExceptionMappingDtoList(ExceptionMappingCollection exceptionMappings){
            List<ExceptionMappingDto> list=new ArrayList<>(exceptionMappings.size());
            for (ExceptionMapping exceptionMapping : exceptionMappings) {
                list.add(new ExceptionMappingDto(exceptionMapping));
            }
            return list;
        }
    }

}
