package com.ouken.phone.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.ouken.phone.Phone;
import com.ouken.phone.app.App;
import com.ouken.phone.app.AppInfo;
import com.ouken.phone.app.AppRegistry;
import com.ouken.phone.common.PhoneManager;
import com.ouken.phone.config.Config;

/**
 * Getting the AppScreen instance can only be achieved with {@link AppScreen#setCurrentApp(app)}.<br> 
 * To ensure whenever the AppScreen is used an app is set 
 */
public class AppScreen extends ScreenAdapter{
	
	// -- constants --
	private static final AppScreen INSTANCE = new AppScreen();
	
	private static final Logger log = new Logger(AppScreen.class.getName(), Logger.DEBUG);
	private static final boolean DEBUG = true;
	private static Array<App> CACHE; // still open in background
	public static final ArrayMap<App, Texture> SCREENSHOTS = new ArrayMap<>();
	
	// -- attributes --
	private App currentApp;
//	private Class<? extends App> currentAppClass;
	private boolean clearCache;
	private boolean exitCurrentApp;
	
	// -- constructors --
	private AppScreen() {
		CACHE = new Array<App>() {
			@Override
			public boolean removeValue(App value, boolean identity) {
				if(super.removeValue(value, identity)) {
					SCREENSHOTS.removeKey(value);
					value.pause();
					value.dispose();
					return true;
				}
				return false;
			}
		};
	}
	
	
	
	// -- whenever. if or if not the appscreen is set and renders an app --


	/**If app is null  the homescreen is queued.<br>
	 * [!] If no appscreen is about to be queued this method throws an exception.*/
	public static AppScreen setCurrentApp(Class<? extends App> appClass) {
		AppInfo info =  App.getAppInfoFrom(appClass);
		debug("SETTING  APP: "+ (info != null? info.name() : appClass.getSimpleName()) + " TO CURRENT APP");
		if(Phone.INSTANCE.isAppScreenAboutToBeQueued()) {
			
			if(appClass == null)
				INSTANCE.queueHomeScreen();
			
			INSTANCE.currentApp = INSTANCE.getFromCacheOrNewInstance(appClass);
			
			if(INSTANCE.currentApp == null)
				INSTANCE.queueHomeScreen();
			
		}else throw new IllegalArgumentException("AppScreen must be queued to gain access to the instance!");
		return INSTANCE;
	}
	
	
	/**call this only when the applicavtion is exitting*/
	public static void disposeOnExitApplication() {
		INSTANCE.dispose();
	}
	
	/**disposes all apps and clears the cache. homescreen is queued if not already set.
	 * Call this if the phone is still active afterwards*/
	public static void clearCache() {
		debug("CLEAR CACHE");
		if(Phone.INSTANCE.isAppScreenActive()) {
			INSTANCE.setClearCache();
		}else {
			INSTANCE.wipeCacheAndCurrentApp();
		}
	}
	
	/**disposes the current app. if app == currentApp then home screen is queued*/
	public static void clearFromCache(App app) {
		if(app == null)return;
		debug("CLEARING APP FROM CACHE: " + app.getAppInfo().name()  + "");
		if(isCurrentApp(app)) {
			if(Phone.INSTANCE.isAppScreenActive())INSTANCE.setExitCurrentApp();
			else INSTANCE.diposeCurrentApp();
			
		}else {
			if(!CACHE.contains(app, true))return;
			CACHE.removeValue(app, true);
			//app.pause();app.dispose(); inside removeValue
		}
	}
	
	public static void clearFromCache(Class<? extends App> appClass) {
		for(App app : CACHE) {
			if(app.getClass() == appClass) {
				clearFromCache(app);
			}
		}
	}
	
	public static boolean isCacheEmpty() {
		return CACHE.isEmpty();
	}
	
	public static boolean isCurrentApp(App app) {
		return app != null && app == INSTANCE.currentApp;
	}
	
	public static boolean isCurrentApp(Class<? extends App> appClass) {
		return appClass != null && appClass == INSTANCE.currentApp.getClass();
	}
	
	public static App getCurrentApp() {
		return INSTANCE.currentApp;
	}
	
	
	
	
	
	
	// -- If AppScreen is set and currently renders an app--
	
	
	/**schedules the exit of the current app. The app is being paused then disposed*/
	public void setExitCurrentApp() {
		debug("SET EXIT CURRENT APP");
		exitCurrentApp = true;
		queueHomeScreen();
		//exitting is handled in hide()
	}
	
	/**schedules the disposal of the apps in the cache*/
	public void setClearCache() {
		debug("SET CLEAR CACHE");
		clearCache = true;
		queueHomeScreen();
		// clearing is handled in hide();
	}
	
	
	
	
	// ----
	
	private void diposeCurrentApp() {
		CACHE.removeValue(currentApp, true);
		//app.pause();app.dispose(); inside removeValue
//		resetApp(currentApp);
		currentApp = null;
	}
	
	
	private void disposeCache() {
		Array<App> copy = new Array<App>(CACHE);
		for(App app : copy) {
			CACHE.removeValue(app, true);
			//app.pause();app.dispose(); inside removeValue
//			resetApp(app);
		}
	}
	
	private void wipeCacheAndCurrentApp() {
		disposeCache();
		currentApp = null;
	}
	
	private App getFromCacheOrNewInstance(Class<? extends App> appClass) {
		if(appClass != null)for(App app : CACHE) {
			if(app.getClass() == appClass)return app;
		}
		return AppRegistry.getNewInstanceOf(appClass);
	}
	
	
	@Override
	public void show() {
		if(CACHE.contains(currentApp, true)) {
			debug("RESUME APP: " + currentApp.getAppInfo().name());
			this.currentApp.resume();
			
		}else {
			CACHE.add(currentApp);
			debug("CREATE APP: " + currentApp.getAppInfo().name());
			currentApp.create();
		}
	}
	
	@Override
	public void render(float delta) {
		currentApp.render();
		if(currentApp.isScheduledForExit())setExitCurrentApp();
	}

	@Override
	public void resize(int width, int height) {
		debug("RESIZE APP: " +currentApp.getAppInfo().name());
		currentApp.resize(width, height);
	}
	
	@Override
	public void hide() { // also called when screen is chanegd
		if(exitCurrentApp || clearCache) {
			if(exitCurrentApp) {
				diposeCurrentApp();
				exitCurrentApp = false;
			}
			if(clearCache) {
				wipeCacheAndCurrentApp();
				clearCache = false;
			}
			return;
		}else {
			createScreenshot();
		}
		
		debug("PAUSE APP: " +currentApp.getAppInfo().name());
		currentApp.pause();
	}
	
	@Override
	public void dispose() {
		debug("DISPOSING APP-SCREEN AND CACHE:");
		wipeCacheAndCurrentApp();
	}
	
	// -- helper methods --
	
	public void createScreenshot() {
		if(!Phone.INSTANCE.isAppScreenActive())return;
//		byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
		int x = (int)Config.SCREEN_RECT_X;
		int y = (int)Config.SCREEN_RECT_Y;
		int w = (int)Config.SCREEN_RECT_WIDTH;
		int h = (int)Config.SCREEN_RECT_HEIGHT;
		byte[] pixels = ScreenUtils.getFrameBufferPixels(x, y, w, h, true);

		// This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
		for (int i = 4; i <= pixels.length; i += 4) {
		    pixels[i - 1] = (byte) 255;
		}

//		Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
		Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
		BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
//		PixmapIO.writePNG(Gdx.files.external(getCurrentApp().getClass() +".png"), pixmap);
		SCREENSHOTS.put(getCurrentApp(), new Texture(pixmap));
		pixmap.dispose();
		
	}
	
//	private void resetApp(App app) {
//		PhoneManager.INSTANCE.replaceAppIndexWithNewInstanceOf(app.getClass());
//	}
	
	private static void debug(String s) {
		if(DEBUG)log.debug(s);
	}
	
	private void queueHomeScreen() {
		Phone.INSTANCE.queueHomeScreen();
	}
	
	
	
	
}
