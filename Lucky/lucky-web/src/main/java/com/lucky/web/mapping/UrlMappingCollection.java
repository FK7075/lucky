package com.lucky.web.mapping;

import com.lucky.framework.scan.JarExpand;
import com.lucky.utils.annotation.NonNull;
import com.lucky.utils.base.Assert;
import com.lucky.utils.dm5.MD5Utils;
import com.lucky.utils.file.Resources;
import com.lucky.utils.io.utils.AntPathMatcher;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.MethodUtils;
import com.lucky.web.annotation.CloseRun;
import com.lucky.web.annotation.InitRun;
import com.lucky.web.core.Model;
import com.lucky.web.enums.RequestMethod;
import com.lucky.web.exception.CloseRunException;
import com.lucky.web.exception.InitRunException;
import com.lucky.web.exception.RepeatUrlMappingException;
import com.lucky.web.webfile.WebFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 10:03
 */
public class UrlMappingCollection implements Iterable<UrlMapping> {

    protected static final AntPathMatcher antPathMatcher=new AntPathMatcher();
    private static final byte[] LUCKY_PAGE_BYTE=Resources.getByteArray("/lucky-web/LUCKY.html");
    private static final Logger log= LoggerFactory.getLogger("c.l.web.mapping.UrlMappingCollection");
    /** URL映射集合*/
    private final List<UrlMapping> list;
    /** 服务启动时和关闭时执行的方法*/
    private final List<UrlMapping> runList;
    /** URL扩展*/
    private Map<String,UrlMappingCollection> expandMap;
    /** 被逻辑删除的扩展名*/
    private Set<String> deleteExpand;
    /** URL扩展的具体信息*/
    private Map<String, JarExpand> expandInfoMap;


    private final List<UrlMapping> allMapping;


    public UrlMappingCollection(){
        list=new ArrayList<>(50);
        runList=new ArrayList<>(20);
        expandMap=new HashMap<>();
        deleteExpand=new HashSet<>();
        expandInfoMap=new HashMap<>();
        allMapping=new ArrayList<>(100);
    }

    public List<UrlMapping> getAllUrlMapping(){
        if(allMapping.isEmpty()){
            allMapping.addAll(list);
            expandMap.keySet().stream().filter(k -> !deleteExpand.contains(k))
                    .map(expandMap::get).forEach(coll->allMapping.addAll(coll.list));
        }
        return allMapping;
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

    @NonNull
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

    public Map<String, JarExpand> getExpandInfoMap() {
        return expandInfoMap;
    }

    public void setExpandInfoMap(Map<String, JarExpand> expandInfoMap) {
        this.expandInfoMap = expandInfoMap;
    }

    /**
     * 判断当前URL映射是否已经存在与当前集合中
     * @param map 待判断的URL映射
     * @return
     */
    public void contains(UrlMapping map) {
        for (UrlMapping urlMapping : list) {
            if(urlMapping.isEquals(map)){
                throw new RepeatUrlMappingException(urlMapping,map);
            }
        }
    }

    /**
     * 添加一个URL映射
     * @param urlMapping URL映射
     * @param isLog 是否打印日志
     * @return
     */
    public boolean add(UrlMapping urlMapping,boolean isLog) {
        contains(urlMapping);
        String id= MD5Utils.md5UpperCase((urlMapping.getUrl()+ Arrays.toString(urlMapping.getMethods())),"LUCKY",1);
        urlMapping.setId(id);
        list.add(urlMapping);
        if(isLog){
            log.debug("Mapping `Url=[{}] , RequestMethod={} , Rest={} , Method={}`",
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
     * @param jarExpand 扩展信息
     * @param expand URL映射集
     * @return
     */
    public boolean addExpand(JarExpand jarExpand,UrlMappingCollection expand){
        String expandName=jarExpand.getExpandName();
        if(deleteExpand.contains(expandName)){
            deleteExpand.remove(expandName);
            allMapping.clear();
            return true;
        }
        if(expandMap.containsKey(expandName)){
            log.warn("扩展名为 `{}` 的URL扩展集已经存在,故此次添加将不会生效！",expandName);
            return false;
        }
        //添加前的重复映射校验
        for (UrlMapping urlMapping : expand) {
            contains(urlMapping);
            expandMap.values().stream().forEach((umc)->{
                umc.contains(urlMapping);
            });
        }
        expandMap.put(expandName,expand);
        expandInfoMap.put(expandName,jarExpand);
        allMapping.clear();
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
        allMapping.clear();
        log.info("URL扩展集 `{}` 已被逻辑删除！",expandName);
        return true;
    }

    public boolean removerExpand(String expandName){
        if(!expandMap.containsKey(expandName)){
            log.warn("不存在扩展名为 `{}` 的URL扩展集,删除操作无效！",expandName);
            return false;
        }
        expandMap.remove(expandName);
        expandInfoMap.remove(expandName);
        if(deleteExpand.contains(expandName)){
            deleteExpand.remove(expandName);
        }
        allMapping.clear();
        log.info("URL扩展集 `{}` 已被物理删除！",expandName);
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
        List<UrlMapping> urlMappings =getUrlMappingListByUrl(model);
        RequestMethod requestMethod = model.getRequestMethod();

        //URL验证
        if(urlMappings.isEmpty()){
            if("".equals(model.getUri())){
                WebFileUtils.preview(model, LUCKY_PAGE_BYTE,"LUCKY.html");
                return null;
            }
            model.error("404", "找不与请求相匹配的映射资,请检查您的URL是否正确！","不正确的url："+model.getUri());
            return null;
        }

        //RequestMethod验证 -> 剔除请求类型不匹配的UrlMapping
        urlMappings.removeIf(map -> !map.methodIsEquals(requestMethod));
        if(urlMappings.isEmpty()){
            model.error("403","您的请求类型["+requestMethod+"] , 当前方法并不支持！","不合法的请求类型["+requestMethod+"]!");
            return null;
        }

        final String finalCurrIp = model.getIpAddr();
        //IP验证 -> 剔除不支持该IP地址的UrlMapping
        urlMappings.removeIf(map->!map.ipExistsInRange(finalCurrIp) || !map.ipISCorrect(finalCurrIp));
        if(urlMappings.isEmpty()){
            model.error("403", "ip地址没有被注册，服务器拒绝响应！", "不合法的请求ip：" + finalCurrIp);
            return null;
        }

        //对剩下的UrlMapping进行排序，排序后选取第一个
        Comparator<UrlMapping> comparator
                =new UrlMappingComparator(antPathMatcher.getPatternComparator(model.getUri()));
        urlMappings.sort(comparator);
        UrlMapping mapping = urlMappings.get(0);

        //禁用验证 -> 判断该UrlMapping是否已被禁用
        if(mapping.isDisable()){
            model.error("403","您请求的资源 "+model.getUri()+" 已被禁用！","资源已被禁用！");
            return null;
        }

        //设置Rest参数
        model.setRestParams(antPathMatcher.extractUriTemplateVariables(mapping.getUrl(),model.getUri()));
        return mapping;

//        UrlMapping urlMapping=null;
//        RequestMethod method=model.getRequestMethod();
//        for (UrlMapping mapping : urlMappings) {
//            if(mapping.methodIsEquals(method)){
//                urlMapping=mapping;
//                if(urlMapping.isDisable()){
//                    model.error("403","您请求的资源 "+model.getUri()+" 已被禁用！","资源已被禁用！");
//                    return null;
//                }
//                break;
//            }
//        }
//        if(urlMapping ==null){
//            model.error("403","您的请求类型["+method+"] , 当前方法并不支持！","不合法的请求类型["+method+"]!");
//            return null;
//        }
//        String currIp=model.getIpAddr();
//        if("0:0:0:0:0:0:0:1".equals(currIp)){
//            currIp="127.0.0.1";
//        }
//        if (!urlMapping.ipExistsInRange(currIp) || !urlMapping.ipISCorrect(currIp)) {
//            model.error("403", "该ip地址没有被注册，服务器拒绝响应！", "不合法的请求ip：" + currIp);
//            return null;
//        }
//        return urlMapping;
    }

//    /**
//     * 根据当前请求的URL找到一个与之对应的URLMapping集合
//     * URL吻合，其他他条件待判断
//     * @param model 当前的Model对象
//     * @return
//     */
//    private List<UrlMapping> getUrlMappingByUrl(Model model){
//        List<UrlMapping> urlMapping = getUrlMappingListByUrl(model);
//        Set<UrlMappingCollection> expandCollectSet = expandMap.keySet()
//                .stream()
//                .filter(k -> !deleteExpand.contains(k))
//                .map(expandMap::get).collect(Collectors.toSet());
//        for (UrlMappingCollection expandMappingCollection : expandCollectSet) {
//            urlMapping.addAll(expandMappingCollection.getUrlMappingListByUrl(model));
//        }
//        Comparator<UrlMapping> comparator
//                =new UrlMappingComparator(antPathMatcher.getPatternComparator(model.getUri()));
//        urlMapping.sort(comparator);
//        return urlMapping;
//    }

    /**
     * 找到可以处理当前URL的映射集合
     * @param model 当前的Model对象
     * @return
     */
    private List<UrlMapping> getUrlMappingListByUrl(Model model) throws IOException {
        List<UrlMapping> mappings=new ArrayList<>();
        List<UrlMapping> allUrlMapping = getAllUrlMapping();
        String url=model.getUri();
        for (UrlMapping urlMapping : allUrlMapping) {
            String urlPattern = urlMapping.getUrl();
            boolean isMatch=antPathMatcher.isPattern(urlPattern)?
                    antPathMatcher.match(urlPattern,url):
                    urlPattern.equals(url);
            if(isMatch){
                mappings.add(urlMapping);
            }
//            if((urlMapping.findingRestUelIsEquals(model, url))
//              ||(urlMapping.simpleUrlIsEquals(url))){
//                mappings.add(urlMapping);
//            }
        }
        return mappings;
    }

    /***
     * 将传入的URL集融合到当前的URL集中
     * @param collection 待融合的URL集
     * @return
     */
    public boolean merge(UrlMappingCollection collection){
        for (UrlMapping urlMapping : collection) {
            add(urlMapping,false);
        }
        allMapping.clear();
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
        allMapping.clear();
        return true;
    }

    /**
     * 通过ID找到一个UrlMapping
     * @param id 唯一ID
     * @return 能找到返回UrlMapping，否则返回NULL
     */
    public UrlMapping getUrlMappingById(@NonNull String id){
        Assert.notNull(id,"Incoming ID is null!");
        List<UrlMapping> allUrlMapping = getAllUrlMapping();
        for (UrlMapping urlMapping : allUrlMapping) {
            if(id.equals(urlMapping.getId())){
                return urlMapping;
            }
        }
//        for (UrlMapping urlMapping : list) {
//            if(id.equals(urlMapping.getId())){
//                return urlMapping;
//            }
//        }
//        for (UrlMappingCollection urlMappingCollection : expandMap.values()) {
//            for (UrlMapping urlMapping : urlMappingCollection) {
//                if(id.equals(urlMapping.getId())){
//                    return urlMapping;
//                }
//            }
//        }
        return null;
    }

    private static class UrlMappingComparator implements  Comparator<UrlMapping>{

        private Comparator<String> comparator;

        public UrlMappingComparator(Comparator<String> comparator){
            this.comparator=comparator;
        }

        @Override
        public int compare(UrlMapping o1, UrlMapping o2) {
            return comparator.compare(o1.getUrl(),o2.getUrl());
        }
    }
}
