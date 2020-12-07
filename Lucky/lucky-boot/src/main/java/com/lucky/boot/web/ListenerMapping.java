package com.lucky.boot.web;

import java.util.EventListener;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/7 0007 11:43
 */
public class ListenerMapping {

    private String name;
    private EventListener listener;

    public ListenerMapping(String name, EventListener listener) {
        this.name = name;
        this.listener = listener;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventListener getListener() {
        return listener;
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }
}
