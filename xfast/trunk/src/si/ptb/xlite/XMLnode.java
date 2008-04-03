package si.ptb.xlite;

import si.ptb.xlite.converters.NodeConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * User: peter
 * Date: Feb 17, 2008
 * Time: 4:28:09 PM
 */

@Target(value = {ElementType.FIELD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface XMLnode {
    String value() default "";
    Class targetClass() default Object.class;
    Class<? extends NodeConverter> converter() default NodeConverter.class;
}