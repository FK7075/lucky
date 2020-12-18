package com.lucky.jacklamb.test;

import com.lucky.jacklamb.jdbc.core.abstcore.SqlCore;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlCoreFactory;
import com.lucky.utils.file.FileUtils;
import com.lucky.utils.file.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/18 0018 14:40
 */
public class MyTest {

    public static void main(String[] args) throws IOException {
        SqlCore sqlCore= SqlCoreFactory.createSqlCore();
        BookMapper mapper = sqlCore.getMapper(BookMapper.class);
        mapper.count();
        mapper.findById(12);
        List<Book> list = sqlCore.getList(Book.class);
        mapper.getByName("书名");
        System.out.println(list);

//        BufferedReader reader = Resources.getReader("/com/lucky/jacklamb/activerecord/BaseEntity.class");
//        StringWriter sw=new StringWriter();
//        FileUtils.copy(reader,sw);
//        System.out.println(sw.toString());
    }
}
