package com.lucky.boot.annotation;

import com.lucky.framework.annotation.Component;

import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.server.ServerEndpointConfig;
import java.lang.annotation.*;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/9/15 18:11
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component(type = "endpoint")
public @interface LEndpoint {

    /**
     * URI or URI-template that the annotated class should be mapped to.
     * @return The URI or URI-template that the annotated class should be mapped
     *         to.
     */
    String value();

    String[] subprotocols() default {};

    Class<? extends Decoder>[] decoders() default {};

    Class<? extends Encoder>[] encoders() default {};

    public Class<? extends ServerEndpointConfig.Configurator> configurator()
    default ServerEndpointConfig.Configurator.class;
}
