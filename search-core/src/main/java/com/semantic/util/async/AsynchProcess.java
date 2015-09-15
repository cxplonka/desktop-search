package com.semantic.util.async;
 
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsynchProcess { 
	String onSuccess() default "onSuccess";
	String onFailure() default "onFailure";
}