package restelio.annotation;

import java.lang.annotation.*;

/**
 * Annotation to mark a Restelio factory
 * @author Matteo Giaccone
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RestelioFactory {

}
