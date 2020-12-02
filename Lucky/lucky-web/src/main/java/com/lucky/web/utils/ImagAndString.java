package com.lucky.web.utils;

import java.awt.image.BufferedImage;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/8/25 16:15
 */
public class ImagAndString {

    private BufferedImage img;
    private String str;

    public ImagAndString() {
    }

    public ImagAndString(BufferedImage img, String str) {
        this.img = img;
        this.str = str;
    }

    public BufferedImage getImg() {
        return img;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }
}
