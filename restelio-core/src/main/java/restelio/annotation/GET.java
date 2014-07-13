package restelio.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * Mark a method to respond to a GET HTTP method
 * @author Matteo Giaccone
 */
@Documented
@Inherited
@Target(ElementType.METHOD)
public @interface GET {
    String value();
}
