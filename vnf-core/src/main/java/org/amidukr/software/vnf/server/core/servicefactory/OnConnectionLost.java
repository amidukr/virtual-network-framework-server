package org.amidukr.software.vnf.server.core.servicefactory;

import org.amidukr.software.vnf.server.core.commandprocessor.AuthorizationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Dmytro Brazhnyk on 6/9/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnConnectionLost {
    AuthorizationType authorizationType() default AuthorizationType.AUTHENTICATED_ONLY;
}
