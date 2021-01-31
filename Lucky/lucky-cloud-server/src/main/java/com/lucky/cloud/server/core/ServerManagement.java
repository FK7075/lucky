package com.lucky.cloud.server.core;

import com.lucky.cloud.server.conf.LuckyCloudServerConfig;
import com.lucky.utils.base.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/28 0028 10:41
 */
public class ServerManagement {

    private static final Logger log= LoggerFactory.getLogger(ServerManagement.class);
    private static Map<String, List<Server>> serverPool=new ConcurrentHashMap<>(256);

    /**
     * 返回所有已经注册的服务
     * @return 所有已经注册的服务
     */
    public static Map<String, List<Server>> getServerPool() {
        return serverPool;
    }

    /**
     * 注册一个服务
     * @param server 服务
     */
    public void register(Server server){
        String name=server.getServerName();
        List<Server> servers = serverPool.get(name);
        if(Assert.isEmptyCollection(servers)){
            servers=new ArrayList<>();
            servers.add(server);
            serverPool.put(name,servers);
            log.info("Service `{}` registered successfully!",server);
        }else{
            boolean isAdd=true;
            for (Server s : servers) {
                if(s.isEqual(server)){
                    isAdd=false;
                    break;
                }
            }
            if(isAdd){
                servers.add(server);
                log.info("Service `{}` registered successfully!",server);
            }
        }
    }

    public void registerYourself(){
        LuckyCloudServerConfig server=LuckyCloudServerConfig.getLuckyCloudServerConfig();
        if(server.isRegisterYourself()){
            HttpServer httpServer = new HttpServer(server.getName(), server.getIp(), server.getPort(), server.getAgreement());
            register(httpServer);
        }
    }

    /**
     * 移除一个服务
     * @param server 服务
     */
    public void remove(Server server){
        String name=server.getServerName();
        List<Server> servers = serverPool.get(name);
        if(servers==null){
            log.warn("服务`{}`不存在，移除失败！",name);
            return;
        }
        servers.removeIf(s->s.isEqual(server));
    }

    /**
     * 根据服务名获取该服务的域(随机策略)
     * @param serverName 服务名
     * @return 域
     */
    public String getServerDomain(String serverName){
        Server server = getServer(serverName);
        return server.getDomain();
    }

    /**
     * 根据服务名获取该服务名对应所有服务的域
     * @param serverName 服务名
     * @return 所有域
     */
    public Set<String> getServerDomains(String serverName){
        return getServers(serverName).stream().map(Server::getDomain).collect(Collectors.toSet());
    }

    /**
     * 根据服务名获取一个具体的服务(随机策略)
     * @param serverName 服务名
     * @return 服务实例
     */
    public Server getServer(String serverName){
        return getServerByRandom(getServers(serverName));
    }

    public List<Server> getServers(String serverName){
        Assert.notNull(serverName,"获取服务失败: 服务名为null！");
        List<Server> servers = serverPool.get(serverName);
        Assert.notNull(servers,"获取服务失败：找不到服务名为`"+serverName+"`的服务！");
        return servers;
    }

    private Server getServerByRandom(List<Server> servers){
        final double d = Math.random();
        final int index = (int) (d * servers.size());
        return servers.get(index);
    }

    public static void workCheck(){
        Set<String> emptyServer=new HashSet<>();
        for (Map.Entry<String,List<Server>> entry:serverPool.entrySet()){
            List<Server> servers = entry.getValue();
            servers.removeIf(server -> {
                boolean normalWork = !server.isNormalWork();
                if(normalWork){
                    log.info("Service `{}` failed ！",server);
                }
                return normalWork;
            });
            if(Assert.isEmptyCollection(servers)){
               emptyServer.add(entry.getKey());
            }
        }
        emptyServer.forEach(serverPool::remove);
    }


}
