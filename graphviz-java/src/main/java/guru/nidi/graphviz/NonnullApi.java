package guru.nidi.graphviz;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.*;

@Documented
@Nonnull
@TypeQualifierDefault({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.PACKAGE)
public @interface NonnullApi {
}
