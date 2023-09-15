package love.korni.manipulator.core.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Sergei_Kornilov
 */
@Target({FIELD, CONSTRUCTOR, METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Specify {
    String value();
}
