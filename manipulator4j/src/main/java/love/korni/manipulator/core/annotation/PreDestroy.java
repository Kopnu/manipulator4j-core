package love.korni.manipulator.core.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Sergei_Kornilov
 */
@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PreDestroy {
}
