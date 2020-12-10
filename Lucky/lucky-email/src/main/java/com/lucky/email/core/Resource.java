package com.lucky.email.core;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/9/9 8:52
 */
public class Resource<F> {

    private F resource;

    private String name;

    public F getResource() {
        return resource;
    }

    public void setResource(F resource) {
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Resource(F resource, String name) {
        this.resource = resource;
        this.name = name;
    }
}
