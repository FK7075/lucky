package com.lucky.cloud.server.core;

import com.lucky.cloud.server.conf.LuckyCloudServerConfig;
import com.lucky.utils.base.Assert;
import com.lucky.web.utils.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/28 0028 10:41
 */
public class ServerManagement {

    private static final Logger log= LoggerFactory.getLogger(ServerManagement.class);
    private static final Map<String, List<Server>> serverPool=new ConcurrentHashMap<>(256);

    private static final String FAILED = "FAILED";
    private static final String SUCCESS = "SUCCESS";


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
    public String register(Server server){
        if(!registerCheck(server)){
            return FAILED;
        }
        String name=server.getServerName();
        List<Server> servers = serverPool.get(name);
        if(Assert.isEmptyCollection(servers)){
            servers=new CopyOnWriteArrayList<>();
            servers.add(server);
            serverPool.put(name,servers);
            log.info("Service `{}` registered successfully!",server);
            return SUCCESS;
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
                return SUCCESS;
            }
            return FAILED;
        }
    }

    private boolean registerCheck(Server server){
        String serverMame = server.getServerName();
        LuckyCloudServerConfig config=LuckyCloudServerConfig.getLuckyCloudServerConfig();
        String serverIp = server.getIp();
        String[] legalIP = config.getLegalIP();

        //密码校验
        String password = config.getPassword();
        if(!password.equals(server.getLoginPassword())){
            log.warn("The service '{}' registration is abnormal, and the registration password of the current registration service is incorrect!",serverMame);
            return false;
        }

        //IP校验
        if(!Assert.isEmptyArray(legalIP)){
            for (String ip : legalIP) {
                if (ip.equals(serverIp)) {
                    return true;
                }
            }
        }

        //IP段校验
        String[] legalIpSection = config.getLegalIpSection();
        if(!Assert.isEmptyArray(legalIpSection)){
            for (String ipSection : legalIpSection) {
                if(IpUtil.ipExistsInRange(serverIp,ipSection)){
                    return true;
                }
            }
            log.warn("Service '{}' registration is abnormal, the IP of the currently registered service is illegal!",serverMame);
            return false;
        }else{
            return true;
        }
    }


    public void registerYourself(){
        LuckyCloudServerConfig server=LuckyCloudServerConfig.getLuckyCloudServerConfig();
        if(server.isRegisterYourself()){
            HttpServer httpServer = new HttpServer(server.getName(), server.getIp(), server.getPort(), server.getAgreement(),server.getPassword());
            register(httpServer);
        }
    }

    /**
     * 移除一个服务
     * @param server 服务
     */
    public void remove(Server server){
        String password = LuckyCloudServerConfig.getLuckyCloudServerConfig().getPassword();
        if(!password.equals(server.getLoginPassword())){
            log.warn("Wrong logout password: [{}]",server.getLoginPassword());
            return;
        }
        String name=server.getServerName();
        List<Server> servers = serverPool.get(name);
        if(servers==null){
            log.warn("Service `{}` does not exist, removal failed!",name);
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
