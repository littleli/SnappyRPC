package cz.snappyapps.snappyrpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ales Najmann
 *
 * This annotation serve as an tag for an argument that allows to identify deffered request with identifier
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Id {
}
