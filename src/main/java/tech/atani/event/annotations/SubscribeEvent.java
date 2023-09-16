package tech.atani.event.annotations;

import tech.atani.event.Priorities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubscribeEvent {
    byte value() default Priorities.MEDIUM;
}