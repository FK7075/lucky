package com.lucky.framework.welcome;

import com.lucky.framework.uitls.file.Resources;

import java.io.IOException;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/8 11:50 上午
 */
public abstract class Version {

    private static final String LUCKY_VERSION= "/lucky-framework/version.v";

    public static String version(){
        try {
            return Resources.getString(LUCKY_VERSION);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
