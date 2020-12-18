package com.lucky.jacklamb.test;

import com.lucky.jacklamb.mapper.LuckyMapper;

import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/18 0018 16:24
 */

public interface BookMapper extends LuckyMapper<Book> {

    Book findById(Integer id);

    List<Book> getByName(String name);
}
