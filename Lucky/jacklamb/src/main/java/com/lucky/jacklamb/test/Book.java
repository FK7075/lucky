package com.lucky.jacklamb.test;

import com.lucky.jacklamb.annotation.table.Id;
import com.lucky.jacklamb.enums.PrimaryType;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/18 0018 16:13
 */
public class Book {

    @Id(type = PrimaryType.AUTO_INT)
    private Integer id;
    private String name;
    private Double price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
