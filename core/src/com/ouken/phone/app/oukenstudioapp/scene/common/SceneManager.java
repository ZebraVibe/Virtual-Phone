package com.ouken.phone.app.oukenstudioapp.scene.common;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.ouken.phone.app.oukenstudioapp.common.EditorManager;
import com.ouken.phone.app.oukenstudioapp.scene.components.SceneComponent;
import com.ouken.phone.app.oukenstudioapp.scene.components.TransformComponent;
import com.ouken.phone.app.oukenstudioapp.scene.systems.TransformSystem;

public class SceneManager {
	
	private static final Logger log = new Logger(SceneManager.class.getName(), Logger.DEBUG);
	public static final SceneManager INSTANCE = new SceneManager();

	private SceneManager() {}
	
	@SuppressWarnings("unchecked")
	public static Array<Class<? extends Component>> getInternalComponents() {
		Array<Class<? extends Component>> array = EditorManager.getClassesInPackage(TransformComponent.class.getPackageName(), Component.class);
		Array<Class<? extends Component>> exclude = Array.with(SceneComponent.class);
		array.removeAll(exclude, true);
		log.debug("Found Components: " + array);
		return array;
	}
	
	@SuppressWarnings("unchecked")
	public static Array<Class<? extends EntitySystem>> getInternalSystems() {
		Array<Class<? extends EntitySystem>> array = EditorManager.getClassesInPackage(TransformSystem.class.getPackageName(), EntitySystem.class);
		Array<Class<? extends EntitySystem>> exclude = Array.with(
				IteratingSystem.class, 
				SortedIteratingSystem.class, 
				IntervalSystem.class, 
				IntervalIteratingSystem.class);
		array.removeAll(exclude, true);
		log.debug("Found Systems: " + array);
		return array;
	}
	
	
}
