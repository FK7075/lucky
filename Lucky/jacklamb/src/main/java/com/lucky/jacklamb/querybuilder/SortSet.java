package com.lucky.jacklamb.querybuilder;

import com.lucky.jacklamb.enums.Sort;

public class SortSet {

    private String field;

    private Sort sort;

    public SortSet(String field, Sort sort) {
        this.field = field;
        this.sort = sort;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
