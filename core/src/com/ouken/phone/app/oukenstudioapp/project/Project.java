package com.ouken.phone.app.oukenstudioapp.project;

import java.util.Set;

import org.reflections.Reflections;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.oukenstudioapp.common.EditorManager;
import com.ouken.phone.app.oukenstudioapp.scene.Scene;
import com.ouken.phone.app.oukenstudioapp.scene.uitls.Serializable;
import com.ouken.phone.app.oukenstudioapp.screens.EditorScreen;

public class Project {
	
	private static final EditorManager manager = EditorManager.INSTANCE;
	
	private String name;
	private Array<Scene> scenes = new Array<Scene>();
	private Scene currentScene;
	
	/**the project name without extenstion and path*/
	public Project(String name, Scene defaultScene) {
		currentScene = defaultScene;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public FileHandle getFile() {
		return manager.getProject(name);
	}
	
	
	public FileHandle getChildFile(String child) {
		return getFile().child(child);
	}
	
	public FileHandle createChild(String name) {
		return manager.createFileHandle(getFile(), name, false);
	}
	
	public FileHandle rename(FileHandle file, String name) {
		return manager.renameFileHandle(file, name, false);
	}
	
	public FileHandle rename(String name) {
		FileHandle file = manager.renameFileHandle(getFile(), name, false);
		if(file != null) {
			setName(name);
		}
		return file;
	}
	
	
	
	public @Null Scene getCurrentScene() {
		return currentScene;
	}
	
	public void setScene(Scene scene) {
		if(scene == null)throw new IllegalArgumentException("scene cant be null");
		if(currentScene != null) {
			//.. save ?
		}
		currentScene = scene;
		sceneChanged();
	}
	
	public void sceneChanged() {} 
	
	/**
	 * searches for internal scens with the scene constructor and a Serializable annotation
	 */
	public Scene[] getInternalScenes(Viewport sceneViewport, EditorScreen editor) {
		if(scenes.notEmpty())return scenes.toArray(Scene.class);
		
		Reflections ref = new Reflections();
		Set<Class<? extends Scene>> sceneClasses = ref.getSubTypesOf(Scene.class);
		sceneClasses.forEach(s -> {
			if(s.getAnnotation(Serializable.class) != null) {
				try {
					Constructor constructor = ClassReflection.getConstructor(s, Viewport.class, EditorScreen.class);
					Object obj = constructor.newInstance(sceneViewport, editor);
					scenes.add((Scene)obj);
				} catch (ReflectionException e) {
					e.printStackTrace();
				}
				
			}
		});
		Scene[] array = scenes.toArray(Scene.class);
		return array;
	}
	
	
	
	
}
