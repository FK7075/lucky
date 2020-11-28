package com.lucky.web.filter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/8/24 9:39
 */
public class VerificationCode {

    // 定义图形验证码中绘制的字符的字体
    private Font mFont;
    // 图形验证码的大小
    public int IMG_WIDTH = 100;
    private int IMG_HEIGHT;
    public int CHAR_LENGTH=5;
    private final int CHAR_SIZE=23;
    private final String CHAR_FONT="Arial Black";

    public VerificationCode(){
        IMG_HEIGHT=CHAR_SIZE+4;
        mFont= new Font("Arial Black", Font.PLAIN, CHAR_SIZE);
    }

    // 获取随机颜色的方法
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    // 获取随机字符串
    private String getRandomChar() {
        int rand = (int) Math.round(Math.random() * 2);
        long itmp = 0;
        char ctmp = '\u0000';
        switch (rand) {
            case 1:
                itmp = Math.round(Math.random() * 25 + 65);
                ctmp = (char) itmp;
                return String.valueOf(ctmp);
            case 2:
                itmp = Math.round(Math.random() * 25 + 97);
                ctmp = (char) itmp;
                return String.valueOf(ctmp);
            default:
                itmp = Math.round(Math.random() * 9);
                return itmp + "";
        }
    }

    public ImagAndString createVerificationCodeImage(){
        BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Random random = new Random();
        g.setColor(getRandColor(200, 250));
        // 填充背景色
        g.fillRect(1, 1, IMG_WIDTH - 1, IMG_HEIGHT - 1);
        // 为图形验证码绘制边框
        g.setColor(new Color(102, 102, 102));
        g.drawRect(0, 0, IMG_WIDTH, IMG_HEIGHT);
        g.setColor(getRandColor(160, 200));
        // 生成随机干扰线
        for (int i = 0; i < 80; i++) {
            int x = random.nextInt(IMG_WIDTH - 1);
            int y = random.nextInt(IMG_HEIGHT - 1);
            int x1 = random.nextInt(6) + 1;
            int y1 = random.nextInt(12) + 1;
            g.drawLine(x, y, x + x1, y + y1);
        }
        g.setColor(getRandColor(160, 200));
        // 生成随机干扰线
        for (int i = 0; i < 80; i++) {
            int x = random.nextInt(IMG_WIDTH - 1);
            int y = random.nextInt(IMG_HEIGHT - 1);
            int x1 = random.nextInt(12) + 1;
            int y1 = random.nextInt(6) + 1;
            g.drawLine(x, y, x - x1, y - y1);
        }
        // 设置绘制字符的字体
        g.setFont(mFont);
        // 用于保存系统生成的随机字符串
        String sRand = "";
        for (int i = 0; i < CHAR_LENGTH; i++) {
            String tmp = getRandomChar();
            sRand += tmp;
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(tmp, 15 * i + 10, 20);
        }
        return new ImagAndString(image,sRand);
    }
}
