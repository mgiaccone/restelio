package restelio.annotation;

import java.lang.annotation.*;

/**
 * Annotation to mark a REST resource
 * @author Matteo Giaccone
 */
@Documented
@Inherited
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface RestelioResource {

}
