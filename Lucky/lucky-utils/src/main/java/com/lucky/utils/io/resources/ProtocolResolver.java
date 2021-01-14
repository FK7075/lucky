package com.lucky.utils.io.resources;

import com.lucky.utils.annotation.Nullable;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/14 0014 16:12
 */
@FunctionalInterface
public interface ProtocolResolver {

    @Nullable
    Resource resolve(String location, ResourceLoader resourceLoader);
}
