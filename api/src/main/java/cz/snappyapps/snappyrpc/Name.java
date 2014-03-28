package cz.snappyapps.snappyrpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation gives arguments of the interfaces their serializable names.
 * This allows us to write normal method signatures, but serialize them with
 * custom structures, maps and lists.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Name {
    public String value();
}
