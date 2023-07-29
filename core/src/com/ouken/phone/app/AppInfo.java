package com.ouken.phone.app;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.badlogic.gdx.graphics.Texture;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AppInfo {
	
	String name();
	String[] authors() default {"unknown"};
	String version() default "0.0";

}
