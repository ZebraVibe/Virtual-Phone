package com.ouken.phone.app.oukenstudioapp.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Null;
import com.ouken.phone.app.App;
import com.ouken.phone.app.oukenstudioapp.OukenStudioApp;
import com.ouken.phone.app.oukenstudioapp.config.EditorConfig;
import com.ouken.phone.common.PhoneManager;

public class EditorManager {
	
	// -- constants
	private static final Logger log = new Logger(EditorManager.class.getName(), Logger.DEBUG);
	
	public static EditorManager INSTANCE = new EditorManager();
	
	// keys
	
	private static final String LAST_PROJECT_NAME_KEY = "lastProjectName";
	
	// -- attributes
	private Preferences editorPrefs;
	
	// -- constructor --
	private EditorManager() { 
		editorPrefs = getEditorPrefs();
	}
	
	// -- public methods --
	
	
	public Preferences getEditorPrefs() {
		return PhoneManager.INSTANCE.getPreferencesOfApp(OukenStudioApp.class, EditorConfig.EDITOR_PREFS_NAME);
	}
	
	/**also the last opened project name.
	 * @return the name stored in prefs (without extension)*/
	public String getCurrentProjectName() {
		return editorPrefs.getString(LAST_PROJECT_NAME_KEY); 
	}
	
	
	// -- dir --
	
	/**
	 * 
	 * @param name
	 * @return false if the project already exists or couldnt get created
	 */
	public boolean createNoneExistingProjectAndSetCurrent(String name) {
		FileHandle file = getProjectsDir().child(name + "/");
		if(file.exists())return false;
		try {
			file.mkdirs();
		} catch (Exception e) {
			return false;
		}finally {
			if(!file.exists())return false;
		}
		
		createDefaultFilesOfProject(file);
		setProjectCurrent(file);
		return true;
	}
	
	/**
	 * 
	 * @param name
	 * @return null if the file coulndt get created
	 */
	public @Null FileHandle createProject(String name) {
		FileHandle file = getProjectsDir().child(name + "/");
		if(!file.exists()) {
			try {
				file.mkdirs();
			} catch (Exception e) {
				return null;
			}finally {
				if(!file.exists())return null;
			}
		}
		createDefaultFilesOfProject(file);
		return file;
	}
	
	/**
	 * 
	 * @param name
	 * @return null if the project does not exist
	 */
	public @Null FileHandle getProject(String name) {
		FileHandle file = getProjectsDir().child(name + "/");
		if(!file.exists())return null;
		return file;
	}
	

	/**sets the project current in preferences and created default files if absent*/
	public void setProjectCurrent(FileHandle projectFile) {
//		setProjectCurrent(projectFile.nameWithoutExtension());
		editorPrefs.putString(LAST_PROJECT_NAME_KEY, projectFile.nameWithoutExtension());
		editorPrefs.flush();
		// recreate default files if absent
		createDefaultFilesOfProject(projectFile);
	}
	
	/**
	 * 
	 * @param name
	 * @return true if the file could be created
	 */
	public boolean renameCurrentProjectTo(String name) {
		FileHandle oldProjDir = getCurrentProjectDir();
		
		if(hasSibling(oldProjDir, name))return false;
		
		FileHandle dest = renameFileHandle(oldProjDir, name, false);
		if(dest == null)return false;
		setProjectCurrent(dest);
		
//		setProjectCurrent(name);
//		FileHandle dest = getCurrentProjectDir();
//		// moving old dir to new dir
//		if(oldProjDir.exists())oldProjDir.moveTo(dest);
//		
//		if(!dest.exists()) {
//			dest.mkdirs();
//			if(!dest.exists())return false;
//		}
		//createDefaultFiles();
		return true;
	}

	/**returns a new fileHandle with the new existing file as value or null.
	 * @param overwriteExisting if true overwrites possibly an already existing file with the same name*/
	public FileHandle renameFileHandle(FileHandle file, String name, boolean overwriteExisting) {
		FileHandle dest =  file.sibling(name + (file.isDirectory() && !name.endsWith("/") ? "/" : (!file.extension().equals("") ?  "."+ file.extension() : "")));
		if(!overwriteExisting && dest.exists())return null;
		file.moveTo(dest);
		if(!dest.exists())return null;
		return dest;
	}
	
	/** returns ans create a new filehandle or null*/
	public FileHandle createFileHandle(FileHandle parent, String name, boolean overwriteExisting) {
		FileHandle child = parent.child(name);
		if (!overwriteExisting && child.exists())
			return null; // child with same name already exists
		else
			child.mkdirs();

		if (!child.exists())
			return null; // could not get created
		return child;

	}
	
	public boolean hasSibling(FileHandle file, String name) {
		FileHandle sib = null;
		try {
			sib = file.sibling(name);
		} catch (Exception e) { // if sibling is null / we are the only one == we are the root
			return false;
		}

		if (sib != null && sib.exists())return true;
		return false;
	}
	
	
	public FileHandle getProjectsDir() {
		FileHandle dir = PhoneManager.INSTANCE.getAppFolderOf(OukenStudioApp.class).
		child(EditorConfig.PROJECTS_DIRECTORY_NAME);
		return dir;
	}
	
	public FileHandle getCurrentProjectDir() {
		FileHandle dir = getProjectsDir().child(getCurrentProjectName() +"/");
		return dir;
	}
	
	private void createDefaultFilesOfProject(FileHandle project) {
		FileHandle[] f = { 
				project.child(EditorConfig.ASSETS_DIR_NAME), 
				project.child(EditorConfig.PACKAGES_DIR_NAME), 
				};
		
		for(FileHandle file : f ) {
			if(!file.exists())file.mkdirs();
		}
	}
	

	public FileHandle getAssetsDirOfCurrentProject() {
		FileHandle dir = getCurrentProjectDir().child(EditorConfig.ASSETS_DIR_NAME);
		return dir;
	}
	
//	public FileHandle getGameObjectsDirOfCurrentProject() {
//		FileHandle dir = getCurrentProjectDir().child(EditorConfig.GAME_OBJECTS_DIR_NAME);
//		return dir;
//	}
	
	public FileHandle getPackagesDirOfCurrentProject() {
		FileHandle dir = getCurrentProjectDir().child(EditorConfig.PACKAGES_DIR_NAME);
		return dir;
	}
	
	
	// -- helper --
	
	
	public static final <T> Array<Class<? extends T>> getClassesInPackage(String packageName, Class<T> superClass) {
		Array<Class<? extends T>> array = new Array<>();
		Reflections ref = new Reflections(packageName);
		long millis = System.currentTimeMillis();
		
		log.debug("Searching for classes of type " + superClass + " in package " + packageName + "...");
		Set<Class<? extends T>> set = ref.getSubTypesOf(superClass);
		
		log.debug("...done Searching! [in " + ((System.currentTimeMillis() - millis) / 1000f) +"s]");
		
		set.forEach(c -> array.add(c));
		log.debug("Classes found: " + array);
		
		return array;
	}
	
	

}
