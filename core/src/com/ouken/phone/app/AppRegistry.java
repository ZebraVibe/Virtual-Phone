package com.ouken.phone.app;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.reflections.Reflections;

import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.ouken.phone.Phone;
import com.ouken.phone.app.exampleapp.ExampleApp;

/*
 * [scanner]
 * Uses maven lib: org.reflections:reflections:0.9.12
 *
 */
public class AppRegistry {
	
	private static final Array<Class<? extends App>> ALL_INTERNAL_APPS = new Array<Class<? extends App>>();
	static {
		// filling APPS array
		scanForInternalApps();
	}
	
	// -- constructor --
	private AppRegistry() {}
	
	
	// -- public methods --
	
	public static Array<Class<? extends App>> getNewArrayOfAllInternalAppClasses(){
		return new Array<Class<? extends App>>(ALL_INTERNAL_APPS);
	}

	
	public static App getNewInstanceOf(Class<? extends App> appClass) {
		if(appClass == null)throw new IllegalArgumentException("appClass must not be null");
		App app = null;
		
		try {
			app = (App)appClass.getDeclaredConstructor(SpriteBatch.class).
			newInstance(Phone.INSTANCE.getBatch());
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		return app;
	}
	
	
	// -- private methods --
	

	private static void scanForInternalApps() {
		scanForInternalApps(false, false);
	}
	
	/**
	 * Call this again after possibly having downloaded another app.<br>
	 * [Note]:second argument ignored if first argument is false.*/
	private static void scanForInternalApps(boolean addToExistingApps, boolean removeOldAppIfNotInNewScan){
		Array<Class<? extends App>> apps = new Array<>();
		
		
		// Detects all annotated classed in com.ouken.app.* like so :
		Reflections reflections = new Reflections("com.ouken.phone.app");
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(AppInfo.class);
		
		for(Class<?> c : annotated) {
			if(c.getSuperclass() == App.class )
				apps.add((Class<? extends App>) c);
		}
		
		if(addToExistingApps) {
			// add new ones
			for(Class<? extends App> c : apps) {
				if(!ALL_INTERNAL_APPS.contains(c, true))ALL_INTERNAL_APPS.add(c);
			}
			
			// remove old ones
			if(removeOldAppIfNotInNewScan) {
				for(Class<? extends App> c : ALL_INTERNAL_APPS) {
					if(!apps.contains(c, true))ALL_INTERNAL_APPS.removeValue(c, true);
				}
			}
			
		}else {
			ALL_INTERNAL_APPS.clear();
			ALL_INTERNAL_APPS.addAll(apps);
		}
	}
	
	
	
	
}
