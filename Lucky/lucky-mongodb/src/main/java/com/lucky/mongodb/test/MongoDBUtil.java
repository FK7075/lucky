package com.lucky.mongodb.test;


import com.mongodb.ConnectionString;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.internal.MongoClientImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/1 0001 9:50
 */
public class MongoDBUtil {
//    //不通过认证获取连接数据库对象
//    public static MongoDatabase getConnect(String mongodbUrl){
//        //连接到 mongodb 服务
//        MongoClient mongoClient = MongoClients.create(mongodbUrl);
//        //连接到数据库
//        MongoDatabase mongoDatabase = mongoClient.getDatabase("lucky");
//
//        //返回连接数据库对象
//        return mongoDatabase;
//    }

    public static MongoClient getConnect(String mongodbUrl){
        return MongoClients.create(mongodbUrl);
    }

//    //需要密码认证方式连接
//    public static MongoDatabase getConnect2(){
//        List<ServerAddress> adds = new ArrayList<>();
//        //ServerAddress()两个参数分别为 服务器地址 和 端口
//        ServerAddress serverAddress = new ServerAddress("localhost", 27017);
//        adds.add(serverAddress);
//
//        List<MongoCredential> credentials = new ArrayList<>();
//        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
//        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential("username", "databaseName", "password".toCharArray());
//        credentials.add(mongoCredential);
//        ConnectionString cs=new ConnectionString()
//
//        //通过连接认证获取MongoDB连接
//        MongoClient mongoClient = new MongoClient(adds, credentials);
//
//        //连接到数据库
//        MongoDatabase mongoDatabase = mongoClient.getDatabase("test");
//
//        //返回连接数据库对象
//        return mongoDatabase;
//    }
}
