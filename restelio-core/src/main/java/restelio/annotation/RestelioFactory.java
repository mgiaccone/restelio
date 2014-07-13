package restelio.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * Annotation to mark a Restelio factory
 * @author Matteo Giaccone
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
public @interface RestelioFactory {

}
