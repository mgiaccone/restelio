package restelio.annotation;

import restelio.Restelio.HttpMethod;

import java.lang.annotation.*;

/**
 * Annotation to mark a REST resource
 * @author Matteo Giaccone
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RestelioFilter {
    public static enum Priority {
        TOP, HIGH, DEFAULT, LOW
    }

    String value();
    HttpMethod[] method() default {};
    Priority priority() default Priority.DEFAULT;
}
