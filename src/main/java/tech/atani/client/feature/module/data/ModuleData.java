package tech.atani.client.feature.module.data;

import com.google.common.base.Supplier;
import com.sun.org.apache.xpath.internal.operations.Bool;
import tech.atani.client.feature.module.Module;
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
    String serverIP() default "";
    int key() default 0;
    boolean enabled() default false;
    boolean alwaysRegistered() default false;
    boolean frozenState() default false;

}
