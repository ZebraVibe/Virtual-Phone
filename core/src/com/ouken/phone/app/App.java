package com.ouken.phone.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.ouken.phone.Phone;
import com.ouken.phone.common.PhoneManager;

/**
 * [Icons] (for internal apps):
 * The file must benamed: <appclass>.png or jpg etc. and put in asstes-raw/appicons.
 * Run the AssetPacker and refresh asstes folder in core - Done.
 * 
 */
public abstract class App extends Game {
	// -- constants --
	private Logger log;
	
	// -- attributes --
	private final Array<InputProcessor> processors = new Array<InputProcessor>();
	private SpriteBatch batch;
	private AppInfo info;
	private boolean isScheduledForExit;
	
	// -- constructor --
	public App(SpriteBatch batch) {
		this.batch = batch;
		log = new Logger(getClass().getName(), Logger.DEBUG);
		if(getClass().isAnnotationPresent(AppInfo.class)) {
			info = this.getClass().getAnnotation(AppInfo.class);
		}
	}
	
	
	// -- public methods --
	
	public final SpriteBatch getBatch() {
		return batch;
	}
	
	public Logger log() {
		return log;
	}
	
	public final AppInfo getAppInfo() {
		return info;
	}
	
	/**schedules the exit of the app*/
	public void exit() {
		isScheduledForExit = true;
	}
	
	public final boolean isScheduledForExit() {
		return isScheduledForExit;
	}
	
	public void showPhoneOverlay() {
		Phone.INSTANCE.showOverlay();
	}
	
	public void hidePhoneOverlay() {
		Phone.INSTANCE.hideOverlay();
	}
	
	
	public Preferences getPreferences(String name) {
		return PhoneManager.INSTANCE.getPreferencesOfApp(this.getClass(), name);
	}
	
	
	public FileHandle getDirectory() {
		return PhoneManager.INSTANCE.getAppFolderOf(getClass());
	}
	
	public FileHandle getFileFromDirectory(String name) {
		return getDirectory().child(name);
	}
	
	
	public void addProcessors(InputProcessor... processors) {
		if (processors == null)return;
		
		for (InputProcessor processor : processors) {
			if (processors != null && !this.processors.contains(processor, true)) {
				this.processors.add(processor);
			}
		}
		Phone.INSTANCE.updateProcessorsIfAppScreen();
	}
	
	public void removeProcessors(InputProcessor... processors) {
		if (processors == null)return;
		for (InputProcessor processor : processors) {
			if (processors != null) {
				this.processors.removeValue(processor, true);
			}
		}
		Phone.INSTANCE.updateProcessorsIfAppScreen();	
	}
	
	public final Array<InputProcessor> getProcessors(){
		return processors;
	}
	
	
	
	public static final AppInfo getAppInfoFrom(Class<? extends App> appClass) {
		if(appClass == null) return null;
		return appClass.getAnnotation(AppInfo.class);
	}

	
}
