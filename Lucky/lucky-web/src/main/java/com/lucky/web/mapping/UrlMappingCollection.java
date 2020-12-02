package com.lucky.web.mapping;

import com.lucky.framework.uitls.base.Assert;
import com.lucky.framework.uitls.file.Resources;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.MethodUtils;
import com.lucky.web.annotation.CloseRun;
import com.lucky.web.annotation.InitRun;
import com.lucky.web.core.Model;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.exception.CloseRunException;
import com.lucky.web.exception.InitRunException;
import com.lucky.web.exception.RepeatUrlMappingException;
import com.lucky.web.webfile.WebFileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 10:03
 */
public class UrlMappingCollection {

    private static final Logger log= LogManager.getLogger("c.l.web.mapping.UrlMappingCollection");
    /** URL映射集合*/
    private List<UrlMapping> list;
    /** 服务启动时和关闭时执行的方法*/
    private List<UrlMapping> runList;
    /** URL扩展*/
    private Map<String,UrlMappingCollection> expandMap;
    /** 被逻辑删除的扩展名*/
    private Set<String> deleteExpand;


    public UrlMappingCollection(){
        list=new ArrayList<>(10);
        runList=new ArrayList<>(10);
        expandMap=new HashMap<>();
        deleteExpand=new HashSet<>();
    }

    public List<UrlMapping> getRunList() {
        return runList;
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public Iterator<UrlMapping> iterator() {
        return list.iterator();
    }

    public Iterator<UrlMapping> runIterator() {
        return runList.iterator();
    }

    public void clear() {
        list.clear();
    }

    public Map<String, UrlMappingCollection> getExpandMap() {
        return expandMap;
    }

    public void setExpandMap(Map<String, UrlMappingCollection> expandMap) {
        this.expandMap = expandMap;
    }

    public Set<String> getDeleteExpand() {
        return deleteExpand;
    }

    public void setDeleteExpand(Set<String> deleteExpand) {
        this.deleteExpand = deleteExpand;
    }

    /**
     * 判断当前URL映射是否已经存在与当前集合中
     * @param map 待判断的URL映射
     * @return
     */
    public boolean contains(UrlMapping map) {
        for (UrlMapping urlMapping : list) {
            if(urlMapping.isEquals(map)){
                return true;
            }
        }
        return false;
    }

    /**
     * 添加一个URL映射
     * @param urlMapping URL映射
     * @param isLog 是否打印日志
     * @return
     */
    public boolean add(UrlMapping urlMapping,boolean isLog) {
        if(contains(urlMapping)){
            throw new RepeatUrlMappingException(urlMapping);
        }
        list.add(urlMapping);
        if(isLog){
            log.info("Mapping `Url=[{}] , RequestMethod={} , Rest={} , Method={}`",
                    urlMapping.getUrl(), Arrays.toString(urlMapping.getMethods()),urlMapping.getRest(),
                    urlMapping.getMapping());
        }
        return true;
    }

    /**
     * 添加一个URL映射，并打印日志
     * @param urlMapping URL映射
     * @return
     */
    public boolean add(UrlMapping urlMapping){
        return add(urlMapping,true);
    }

    /**
     * 添加一个@InitRun或者@CloseRun方法的映射
     * @param controller Controller对象
     * @param runMethod Controller方法
     * @return
     */
    public boolean addRun(Object controller, Method runMethod){
        runList.add(new UrlMapping(null,null,null,
                controller,runMethod,null,null,null,null));
        return true;
    }

    /**
     * 添加一个@InitRun或者@CloseRun方法的映射
     * @param urlMapping 映射
     * @return
     */
    public boolean addRun(UrlMapping urlMapping){
        runList.add(urlMapping);
        return true;
    }

    /**
     * 执行初始化方法「被@InitRun标注的Controller方法」
     */
    public void initRun(){
        runList.stream()
            .filter(m -> AnnotationUtils.isExist(m.getMapping(), InitRun.class))
            .sorted(Comparator.comparing(m->AnnotationUtils.get(m.getMapping(),InitRun.class).value()))
            .forEach((m)->{
                if(m.getParameters().length!=0){
                    throw new InitRunException(m.getMapping());
                }
                MethodUtils.invoke(m.getObject(),m.getMapping());
            });
    }

    /**
     * 执行关闭方法「被@CloseRun标注的Controller方法」
     */
    public void closeRun(){
        closeRun(runList);
        expandMap.keySet().stream()
                .filter(en->!deleteExpand.contains(en))
                .map(expandMap::get)
                .forEach(c->closeRun(c.getRunList()));
    }

    /***
     * 添加一个URL映射集的扩展
     * @param expandName 扩展名
     * @param expand URL映射集
     * @return
     */
    public boolean addExpand(String expandName,UrlMappingCollection expand){
        if(deleteExpand.contains(expandName)){
            deleteExpand.remove(expandName);
            return true;
        }
        if(expandMap.containsKey(expandName)){
            log.warn("扩展名为 `{}` 的URL扩展集已经存在,故此次添加将不会生效！",expandName);
            return false;
        }
        Iterator<UrlMapping> iterator = expand.iterator();
        //添加前的重复映射校验
        while (iterator.hasNext()){
            UrlMapping urlMapping = iterator.next();
            if(contains(urlMapping)){
                throw new RepeatUrlMappingException(urlMapping);
            }
            expandMap.values().stream().forEach((umc)->{
                if(umc.contains(urlMapping)){
                    throw new RepeatUrlMappingException(urlMapping);
                }
            });
        }
        expandMap.put(expandName,expand);
        log.info("URL扩展集 `{}` 添加成功！URL映射总数为：{}",expandName,expand.size());
        return true;
    }

    /**
     * 删除一个URL集的扩【逻辑删除】
     * @param expandName 扩展名
     * @return
     */
    public boolean deleteExpand(String expandName){
        if(!expandMap.containsKey(expandName)){
            log.warn("不存在扩展名为 `{}` 的URL扩展集,删除操作无效！",expandName);
            return false;
        }
        if(deleteExpand.contains(expandName)){
            return false;
        }
        deleteExpand.add(expandName);
        log.info("URL扩展集 `{}` 已删除！",expandName);
        return true;
    }

    public void closeRun(List<UrlMapping> closeMappingCollection){
        closeMappingCollection.stream()
                .filter(m -> AnnotationUtils.isExist(m.getMapping(), CloseRun.class))
                .sorted(Comparator.comparing(m->AnnotationUtils.get(m.getMapping(),CloseRun.class).value()))
                .forEach((m)->{
                    if(m.getParameters().length!=0){
                        throw new CloseRunException(m.getMapping());
                    }
                    MethodUtils.invoke(m.getObject(),m.getMapping());
                });
    }

    /**
     * 根据当前Model对象找到对应的URL映射
     * @param model 当前的Model对象
     * @return
     * @throws IOException
     */
    public UrlMapping getMapping(Model model) throws IOException {
        List<UrlMapping> urlMappings =getUrlMappingByUrl(model);
        if(Assert.isEmptyCollection(urlMappings)){
            if("/".equals(model.getUri())){
                WebFileUtils.preview(model, Resources.getInputStream("/lucky-web/LUCKY.html"),"LUCKY.html");
                return null;
            }
            model.error("404", "找不与请求相匹配的映射资,请检查您的URL是否正确！","不正确的url："+model.getUri());
            return null;
        }
        UrlMapping urlMapping=null;
        RequestMethod method=model.getRequestMethod();
        for (UrlMapping mapping : urlMappings) {
            if(mapping.methodIsEquals(method)){
                urlMapping=mapping;
                break;
            }
        }
        if(urlMapping ==null){
            model.error("403","您的请求类型["+method+"] , 当前方法并不支持！","不合法的请求类型["+method+"]!");
            return null;
        }
        String currIp=model.getIpAddr();
        if("0:0:0:0:0:0:0:1".equals(currIp)){
            currIp="127.0.0.1";
        }
        if (!urlMapping.ipExistsInRange(currIp) || !urlMapping.ipISCorrect(currIp)) {
            model.error("403", "该ip地址没有被注册，服务器拒绝响应！", "不合法的请求ip：" + currIp);
            return null;
        }
        return urlMapping;
    }

    /**
     * 根据当前请求的URL找到一个与之对应的URLMapping集合
     * URL吻合，其他他条件待判断
     * @param model 当前的Model对象
     * @return
     */
    private List<UrlMapping> getUrlMappingByUrl(Model model){
        List<UrlMapping> urlMapping = getByUrl(model);
        Set<UrlMappingCollection> expandCollectSet = expandMap.keySet()
                .stream()
                .filter(k -> !deleteExpand.contains(k))
                .map(expandMap::get).collect(Collectors.toSet());
        for (UrlMappingCollection expandMappingCollection : expandCollectSet) {
            urlMapping.addAll(expandMappingCollection.getByUrl(model));

        }
        return urlMapping;
    }

    /**
     * 找到可以处理当前URL的映射集合
     * @param model 当前的Model对象
     * @return
     */
    private List<UrlMapping> getByUrl(Model model){
        List<UrlMapping> mappings=new ArrayList<>();
        String url=model.getUri();
        for (UrlMapping urlMapping : list) {
            if(urlMapping.simpleUrlIsEquals(url)
                    ||urlMapping.findingRestUelIsEquals(model, url)){
                mappings.add(urlMapping);
            }
        }
        return mappings;
    }

    /***
     * 将传入的URL集融合到当前的URL集中
     * @param collection 待融合的URL集
     * @return
     */
    public boolean merge(UrlMappingCollection collection){
        Iterator<UrlMapping> iterator = collection.iterator();
        while (iterator.hasNext()){
            add(iterator.next(),false);
        }
        return true;
    }

    /**
     * 将传入的URL集从当前的URL集中删除
     * @param collection 待删除的URL集
     * @return
     */
    public boolean remover(UrlMappingCollection collection){
        Iterator<UrlMapping> iterator = collection.iterator();
        Iterator<UrlMapping> global = list.iterator();
        while (global.hasNext()){
            UrlMapping globalElement = iterator.next();
            while (iterator.hasNext()){
                if(globalElement.isEquals(iterator.next())){
                    global.remove();
                    break;
                }
            }
        }
        return true;
    }
}
