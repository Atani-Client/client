package tech.atani.client.feature.module.data;

import tech.atani.client.feature.module.data.enums.Category;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleData {
    String name();
    String description();
    Category category();
    int key() default 0;
    boolean alwaysEnabled() default false;
}
