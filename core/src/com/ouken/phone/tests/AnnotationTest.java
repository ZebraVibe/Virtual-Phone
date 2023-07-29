package com.ouken.phone.tests;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;


public class AnnotationTest {
	
	
	public static void main(String[] args) {
		// search how to scan environment for com.ouken.phone.tests.AnnotationTest.App class or path.classes
		// annotated with path.App 
		
		ExampleApp obj = new ExampleApp();
		Annotation[] annotations = obj.getClass().getAnnotations();//ExampleApp.class.getAnnotations();
		System.out.println("Searching for Annotations in [" + ExampleApp.class.getSimpleName() + "]...");
		for(Annotation a : annotations) {
			if(a instanceof AppTestAnnotation) {
				AppTestAnnotation app = (AppTestAnnotation)a;
				System.out.println("Annotation [" + AppTestAnnotation.class.getSimpleName() + "] found:");
				System.out.println("App name: " +app.name());
			}
		}
	
		
	}
	// -- annotations applied to (new) annotations --
	
	/**
	 * Applicationlisteners applied with this annotation will be loaded as App
	 */
	//@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)//annotation "App" shall be available at runtime
	@Target(ElementType.TYPE)// restricts to which type (i.e. class, method, interface...)this annotation can be applied to
	private static @interface TestAnnotation{
		String name(); // public abstract -> must be defined
		String author() default "unknown"; // has default value -> doesnt HAVE TO be defined
		String version() default "0.0";
		int id() default 1;
		String[] array() default { "one", "two"};
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	private static @interface AppTestAnnotation{
		String name();
	}
	
	
	private static class AppBase extends ApplicationAdapter{
		private AppTestAnnotation info;
		
		public AppBase() {
			if(!this.getClass().isAnnotationPresent(AppTestAnnotation.class))return;
			info = this.getClass().getAnnotation(AppTestAnnotation.class);
//			Preferences prefs = Gdx.app.getPreferences(info.name());
			
		}
		
	}
	
	
	@AppTestAnnotation(name = ExampleApp.NAME)
	private static class ExampleApp extends AppBase{
		public static final String NAME = "Example";
		
	}
	
	
	
}
