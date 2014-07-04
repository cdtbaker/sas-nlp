package org.apache.commons.math3;
import java.lang.annotation.*;
/** 
 * Annotation that enables test retries.
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME) @Target({ElementType.METHOD}) public @interface Retry {int value() default 2;
}
