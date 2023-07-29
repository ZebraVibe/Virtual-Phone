package com.ouken.phone.common;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.ouken.phone.Phone;
import com.ouken.phone.app.App;
import com.ouken.phone.app.AppRegistry;
import com.ouken.phone.app.exampleapp.ExampleApp;
import com.ouken.phone.config.Config;
import com.ouken.phone.screens.HomeScreen;
import com.ouken.phone.utils.color.ColorConverter;

/*
 *[Scanning for annotations:] 
 *https://stackoverflow.com/questions/13128552/how-to-scan-classes-for-annotations
 *
 **/
public class PhoneManager {
	
	// -- constants --
	public static final PhoneManager INSTANCE = new PhoneManager();
	
	// -- attributes --
	private Array<Class <? extends App>> INSTALLED_APP_CLASSES;
	private FileHandle APPS_FOLDER;
	
	public final Color homeScreenBackgroundColor = ColorConverter.hexToRGBA8888(0x981e82);//ColorConverter.hexToRGBA8888(0xd3e549);
	public final Color frameColor = ColorConverter.hexToRGBA8888(0x202020);

	private Preferences PREFS;


	
	// -- constructor --
	private PhoneManager() {
		init();
		
		try { 
			initApps();
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
		
		
	}
	
	// -- init --
	
	private void initApps() throws Exception {
		// apps folder
		if(Gdx.files.isExternalStorageAvailable()) {
			APPS_FOLDER = Gdx.files.external(Config.APPS_FOLDER_PATH); // externals path is based on externalPath + path
			if(!APPS_FOLDER.exists())APPS_FOLDER.mkdirs();
		}else throw new Exception("External Storage not available!");
		
		INSTALLED_APP_CLASSES = getInstalledAppClassesFromPrefs();
		
		// --- to be removed later on: ---
		for(Class<? extends App> c : AppRegistry.getNewArrayOfAllInternalAppClasses()) {
			INSTALLED_APP_CLASSES.add(c);
		}
		
//		for(int i = 0; i < 28; i++)
//			INSTALLED_APPS.add(ExampleApp.class);
	}
	
	private void init() {
		PREFS = Gdx.app.getPreferences(Config.INSTALLED_APP_CLASSES_PREFS_NAME);
	}
	
	
	// -- apps --
	
	
	public FileHandle getAppsFolder() {
		return APPS_FOLDER;
	}
	
	public FileHandle getAppFolderOf(Class<? extends App> appClass) {
		if(appClass == null)return null;
		FileHandle appFolder = getAppsFolder().child(appClass.getSimpleName());
		if(!appFolder.exists())appFolder.mkdirs();
		return appFolder;
	}
	
	public FileHandle getFileFromAppFolderOf(Class<? extends App> appClass, String fileName) {
		return appClass == null ? null : getAppFolderOf(appClass).child(fileName);
	}
	
	public FileHandle getIconPNGFileFromAppFolderOf(Class<? extends App> appClass) {
		return getFileFromAppFolderOf(appClass, appClass.getSimpleName() + ".png");
	}
	
	public boolean deleteAppFolderOf(Class<? extends App> appClass) {
		return getAppFolderOf(appClass).deleteDirectory();
	}
	
	
	
	public boolean uninstallApp(Class<? extends App> appClass) {
		if(PhoneManager.INSTANCE.deleteAppFolderOf(appClass)) {
			PhoneManager.INSTANCE.removeAppClassFromPrefs(appClass);
			return true;
		}
		return false;
	}
	
	
	
	
	/*to safe which app are installed simply pass all classes into preferences*/
	public Array<Class<? extends App>> getInstalledAppClasses(){
		return INSTALLED_APP_CLASSES;
	}

	
	/**replaces the index of the given app with the provided app,
	 * returns true if the app was successfully replaced*/
	public boolean replaceAppClassIndexWith(Class<? extends App> appClass) {
		if(appClass == null)return false;
		int index = INSTALLED_APP_CLASSES.indexOf(appClass, true);
		if(index < 0)return false;
		INSTALLED_APP_CLASSES.set(index, appClass);
		return true;
	}
	
	
	/**new apps, which are already scanned in {@link AppRegistry} , can be added to the installed apps.
	 * returns true if the app was successfully added */
	public boolean addAppClassAndUpdateHomeScreen(Class<? extends App> appClass) {
		if(appClass == null)return false;
		// check if already existing
		for(Class<? extends App> c : INSTALLED_APP_CLASSES) {
			if(appClass == c)return false;
		}
		
		INSTALLED_APP_CLASSES.add(appClass);
		HomeScreen.INSTANCE.updatePages();
		return true;
	}
	

	
	// -- prefs --
	
	
	private Array<Class<? extends App>> getInstalledAppClassesFromPrefs(){
		Array<Class<? extends App>> array = new Array<Class<? extends App>>();
		Map<String, ?> map = getInstalledAppClassesPreferences().get();
		
		for(Class<? extends App> appClass : AppRegistry.getNewArrayOfAllInternalAppClasses()) {
			map.forEach( (key, val ) -> {
				if(key.equals(appClass.getSimpleName())) {
					array.add(appClass);
				}
				
			});
		}
		return array;
	}
	
	private Preferences getInstalledAppClassesPreferences() {
		return PREFS;
	}
	

	
	public Preferences getPreferencesOfApp(Class<? extends App> appClass, String name){
		if(name == null)throw new IllegalArgumentException("name must not be null!");
		if(appClass.getSimpleName().equals(Config.INSTALLED_APP_CLASSES_PREFS_NAME))
			throw new IllegalArgumentException("appClass must not be named as: " + Config.INSTALLED_APP_CLASSES_PREFS_NAME);
		return Gdx.app.getPreferences(Config.APPS_DIRECTORY_NAME + appClass.getSimpleName() + "/" +name);

	}
	
	/**shall be used after having downloaded a new app*/
	public void putNewAppClassIntoPrefs(Class<? extends App> appClass) {
		if(appClass == null)return;
		PREFS.putString(appClass.getSimpleName(), "");
		PREFS.flush();
	}
	
	/**shall be used when uninstalling an application*/
	public void removeAppClassFromPrefs(Class<? extends App> appClass) {
		if(appClass == null)return;
		PREFS.remove(appClass.getSimpleName());
		PREFS.flush();
	}
	
	
	
	
	
	// -- helper --
	
	
	
	
	
	
	// -- private methods --
	

	
	

	
}
