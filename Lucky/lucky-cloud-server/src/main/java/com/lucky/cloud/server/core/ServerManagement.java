package com.lucky.cloud.server.core;

import com.lucky.utils.base.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/28 0028 10:41
 */
public class ServerManagement {

    private static Map<String, List<Server>> serverPool=new ConcurrentHashMap<>(256);

    public void register(Server server){
        String name=server.getServerName();
        List<Server> servers = serverPool.get(name);
        if(Assert.isEmptyCollection(servers)){
            servers=new ArrayList<>();
            servers.add(server);
            serverPool.put(name,servers);
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
            }
        }
    }

    public Server getServer(String serverName){
        Assert.notNull(serverName,"获取服务失败: 服务名为null！");
        List<Server> servers = serverPool.get(serverName);
        Assert.notNull(servers,"获取服务失败：找不到服务名为`"+serverName+"`的服务！");
        return getServerByRandom(servers);
    }

    private Server getServerByRandom(List<Server> servers){
        final double d = Math.random();
        final int index = (int) (d * servers.size());
        return servers.get(index);
    }


}
