package com.lucky.mongodb.test;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * @author fk
 * @version 1.0
 * @date 2021/3/1 0001 9:52
 */
public class MainTest {

    public static void main(String[] args) {
        MongoCollection<Document> collection = MongoDBUtil.getConnect("mongodb://localhost:27017/")
                .getDatabase("lucky")
                .getCollection("test");
        Document doc=new Document();
        doc.append("name","王五").append("sex", "男").append("age", 20);
        collection.insertOne(doc);

    }
}
