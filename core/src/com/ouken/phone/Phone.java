package com.ouken.phone;


import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.App;
import com.ouken.phone.app.AppInfo;
import com.ouken.phone.app.AppRegistry;
import com.ouken.phone.app.exampleapp.ExampleApp;
import com.ouken.phone.app.utils.AppScreenViewport;
import com.ouken.phone.app.utils.AppSpriteBatch;
import com.ouken.phone.assets.AssetDescriptors;
import com.ouken.phone.assets.RegionNames;
import com.ouken.phone.common.PhoneManager;
import com.ouken.phone.config.Config;
import com.ouken.phone.screens.AppScreen;
import com.ouken.phone.screens.CacheScreen;
import com.ouken.phone.screens.HomeScreen;
import com.ouken.phone.utils.GdxUtils;
import com.ouken.phone.utils.actor.ColoredButton;
import com.ouken.phone.utils.font.FontSize;
import com.ouken.phone.utils.font.Fonts;

public class Phone extends Game {

	// -- constants --
	private static final Logger log = new Logger(Phone.class.getName(), Logger.DEBUG);

	public static final Phone INSTANCE = new Phone();

	// -- attributes --
	
	private AppSpriteBatch batch;
	private OrthographicCamera camera;
	private Viewport viewport, overlayViewport;
	private Stage overlayStage;
	private AssetManager assets;
	private InputMultiplexer multiplexer;
	
	
	private TextureRegion frameTop, frameBottom, frameLeft, frameRight;
	private TextureRegion homeNavButtonRegion, cacheNavButtonRegion, backNavButtonRegion, 
		navAreaCircleLeft, navAreaCircleCenter, navAreaCircleRight;
	private TextureRegion statusBarAreaLeft, statusBarAreaCenter,statusBarAreaRight,
		battery, reception;
	
	private final Color tmpColor = new Color();
	private final Color offColor = new Color(0.05f, 0.05f, 0.05f, 1);
	private final Rectangle clipArea = new Rectangle();
	private Screen queuedScreen;
	private boolean isAppScreenAboutToBeQueued;
	
	private Table phoneNavArea, navBackground, navForeground;
	private ColoredButton backButton, homeButton, cacheButton;
	private Image bgLeft, bgCenter, bgRight;
	private boolean isNavBarShown;
	
	private Table statusBarArea, statusBarForeground, statusBarBackground;
	private boolean isStatusBarShown;
	

	// -- constructor --
	private Phone() {}

	// -- init --
	@Override
	public void create() {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		batch = new AppSpriteBatch();
		
		camera = new OrthographicCamera();
		viewport = new FitViewport(Config.WORLD_WIDTH, Config.WORLD_HEIGHT, camera);
		overlayViewport = new AppScreenViewport();
		overlayStage = new Stage(overlayViewport, batch);
		assets = new AssetManager();
		assets.getLogger().setLevel(Logger.DEBUG);
	
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(overlayStage);
		Gdx.input.setInputProcessor(multiplexer);
		
		init();
		
		setScreen(HomeScreen.INSTANCE);
	}

	private void init() {
		initAssets();
		initNavOverlay();
		initStatusBarOverlay();
		
		showOverlay();
	}

	private void initAssets() {
		// assetManager
		AssetDescriptors.ALL.forEach(desc -> assets.load(desc));
		assets.finishLoading(); // TODO exchangable with progress bar
		//font
		Fonts.load();
		
		// phone atlas
		TextureAtlas phoneAtlas = assets.get(AssetDescriptors.PHONE);

		// frame
		TextureRegion frameParts = phoneAtlas.findRegion(RegionNames.FRAME);
		frameTop = new TextureRegion(frameParts, 0, 0, 448, 64);
		frameBottom = new TextureRegion(frameParts, 0, 130, 448, 64);
		frameLeft = new TextureRegion(frameParts, 0, 65, 21, 64);
		frameRight = new TextureRegion(frameParts, 427, 65, 21, 64);

		TextureRegion phoneNavRegion = phoneAtlas.findRegion(RegionNames.PHONE_NAVIGATION);
		homeNavButtonRegion = new TextureRegion(phoneNavRegion, 0, 0, 64, 64);
		backNavButtonRegion = new TextureRegion(phoneNavRegion, 72, 0, 48, 48);
		cacheNavButtonRegion = new TextureRegion(phoneNavRegion, 128, 0, 48, 48);
		
		navAreaCircleLeft = new TextureRegion(phoneNavRegion, 184, 0, 32, 64);
		navAreaCircleCenter = new TextureRegion(phoneNavRegion, 217, 0, 1, 64);
		navAreaCircleRight = new TextureRegion(phoneNavRegion, 219, 0, 32, 64);
		
		TextureRegion statusBar = phoneAtlas.findRegion(RegionNames.STATUS_BAR);
		reception = new TextureRegion(statusBar, 0, 1, 26, 15);
		battery = new TextureRegion(statusBar, 32, 2, 37, 14);
		
		statusBarAreaLeft = new TextureRegion(statusBar, 80, 0, 8, 16);
		statusBarAreaCenter = new TextureRegion(statusBar, 89, 0, 1, 16);
		statusBarAreaRight = new TextureRegion(statusBar, 91, 0, 8, 16);
		
	}
	
	
	private void initNavOverlay() {
		
		phoneNavArea = new Table();
		phoneNavArea.center();

		backButton = new ColoredButton(backNavButtonRegion, Color.GRAY.cpy());
		backButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				// ..
			}
		});
		homeButton = new ColoredButton(homeNavButtonRegion, Color.GRAY.cpy());
		homeButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				queueHomeScreen();
			}
		});
		cacheButton = new ColoredButton(cacheNavButtonRegion, Color.GRAY.cpy());
		cacheButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if(getScreen() instanceof CacheScreen)return;
				
				if(isAppScreenActive()) {
					((AppScreen)getScreen()).createScreenshot();
				}
				
				queueScreen(new CacheScreen());
			}
		});
		
		bgLeft = new Image(navAreaCircleLeft);
		bgCenter = new Image(navAreaCircleCenter);
		bgRight = new Image(navAreaCircleRight);
		
		navBackground = new Table();
		navBackground.setColor(1, 1, 1, Config.OVERLAY_BACKGROUND_ALPHA);
		navBackground.add(bgLeft);
		navBackground.add(bgCenter).width(Config.PHONE_NAV_BG_CENTER_WIDTH);
		navBackground.add(bgRight);
		navBackground.pack();
		
		navBackground.setTransform(true);
		navBackground.setOrigin(Align.center);

		
		float gap = 40;
		navForeground = new Table();
		navForeground.setTouchable(Touchable.childrenOnly);
		navForeground.add(backButton);
		navForeground.add(homeButton).padRight(gap).padLeft(gap);
		navForeground.add(cacheButton);
		navForeground.pack();
		
		phoneNavArea.stack(navBackground,navForeground).center();
		phoneNavArea.pack();
		
//		ClickListener navBarClickListener = new ClickListener(Input.Buttons.LEFT) {
//
//			@Override
//			public void clicked(InputEvent event, float x, float y) {
//				if(getTapCount() == 2) {
//					if(isNavBarShown) {
////						hideNavBar();
//						hideOverlay();
//					}else {
////						showNavBar();
//						showOverlay();
//					}
//				};
//			}
//			
//		};
//		navBackground.addListener(navBarClickListener);
		
		InputListener navInputListener = new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if(isNavBarShown) {
				hideOverlay();
			}else {
				showOverlay();
			}
			}
			
		};
		
		navBackground.addListener(navInputListener);
		phoneNavArea.setPosition(overlayViewport.getWorldWidth() / 2f, 0, Align.top);
		overlayStage.addActor(phoneNavArea);
		
//		navForeground.setVisible(false);
//		showNavBar();
		
		
	}

	
	
	private void initStatusBarOverlay() {
		statusBarArea = new Table();
		statusBarArea.center();
		statusBarArea.setTouchable(Touchable.disabled);
		
		
		statusBarBackground = new Table();
		statusBarBackground.setColor(1, 1, 1, Config.OVERLAY_BACKGROUND_ALPHA);
		Image bgLeft = new Image(statusBarAreaLeft);
		Image bgCenter = new Image(statusBarAreaCenter);
		Image bgRight = new Image(statusBarAreaRight);
		
		statusBarBackground.add(bgLeft);
		statusBarBackground.add(bgCenter).size(Config.STATUS_BAR_CENTER_BG_RECT_WIDTH, Config.STATUS_BAR_BG_HEIGHT);
		statusBarBackground.add(bgRight);
		statusBarBackground.pack();
		
		statusBarArea.add(statusBarBackground);
		statusBarArea.pack();
		
		
		
		
		statusBarForeground = new Table();
		statusBarForeground.setSize(statusBarArea.getWidth(), statusBarArea.getHeight());
		

		Image receptionImage = new Image(reception);
		Image batteryImage = new Image(battery);
		
		
		Table area = new Table();
		area.defaults().spaceRight(8);
		
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		LabelStyle style = new LabelStyle();
		style.font = Fonts.getDefaultFontBySize(FontSize.x12);
		Label label = new Label("hh:mm", style) {
			@Override
			public void act(float delta) {
				date.setTime(System.currentTimeMillis());
				setText(formatter.format(date));
				super.act(delta);
			}
		};
		label.setAlignment(Align.bottomLeft);
		area.add(receptionImage, label);
		area.pack();
		
		area.setPosition(bgLeft.getWidth() / 3f + (bgLeft.getWidth() + Config.STATUS_BAR_SIDE_RECT_WIDTH) / 2f, 0, Align.bottom);
		batteryImage.setPosition(statusBarArea.getWidth() - (bgRight.getWidth() + Config.STATUS_BAR_SIDE_RECT_WIDTH) / 2f, 0, Align.bottom);
		
		statusBarForeground.addActor(batteryImage);
		statusBarForeground.addActor(area);
		statusBarArea.addActor(statusBarForeground);
		
		

		statusBarArea.setPosition(overlayViewport.getWorldWidth() / 2f, 
				overlayViewport.getWorldHeight(), Align.bottom);
		overlayStage.addActor(statusBarArea);
		
		
//		statusBarForeground.setVisible(false);
//		showStatusBar();
	}
	


	// -- public methods --

	public SpriteBatch getBatch() {
		return batch;
	}

	public AssetManager getAssets() {
		return assets;
	}
	
	/** Use this to change the screen. screen == the screen to be changed to.
	 * [!] Note: if the screen == {@link HomeScreen} & the homescreen is already active
	 * the screen won't queue the HomeScreen. The AppScreen can be queued again even if active<br>
	 * to call {@link AppScreen#show()}. of another app again */
	public void queueScreen(Screen screen) {
		if(screen != null && screen instanceof HomeScreen && isHomeScreenActive())return;
		queuedScreen = screen;
	}
	
	public boolean hasQueuedScreen() {
		return queuedScreen != null;
	}
	
	public boolean isScreenQueued(Screen screen) {
		return queuedScreen != null && queuedScreen == screen;
	}
	
	public boolean isHomeScreenQueued() {
		return queuedScreen != null && queuedScreen instanceof HomeScreen;
	}
	
	public boolean isAppScreenQueued() {
		return queuedScreen != null && queuedScreen instanceof AppScreen;
	}
	
	
	public boolean isAppScreenAboutToBeQueued() {
		return isAppScreenAboutToBeQueued;
	}
	
	public void queueHomeScreen() {
		if(!isHomeScreenActive())log.debug(" ---- QUEUING HOMESCREEN ---- ");
		queueScreen(HomeScreen.INSTANCE);
	}
	
	
	public void queueAppScreen(Class<? extends App> appClass) {
		if(appClass == null) {
			log.debug(" ----- FAILED TO QUEUE APPSCREEN ! ---- ");
			return;
		}
		AppInfo info = App.getAppInfoFrom(appClass);;
		log.debug(" ----- QUEUING APPSCREEN OF APP: "+ (info != null ? info.name() : "Unnamed") +"... ---- ");
		isAppScreenAboutToBeQueued = true;
		queueScreen(AppScreen.setCurrentApp(appClass));
		isAppScreenAboutToBeQueued = false;
	}
	

	
	
	public boolean isAppScreenActive() {
		return getScreen() != null && getScreen() instanceof AppScreen;
	}
	
	public boolean isHomeScreenActive() {
		return getScreen() != null && getScreen() instanceof HomeScreen;
	}
	
	
	
	public void showOverlay() {
		showNavBar();
		showStatusBar();
	}
	
	public void hideOverlay() {
		hideNavBar();
		hideStatusBar();
	}
	
	
	
	public void showNavBar() {
		showNavBar(false);
	}
	
	public void hideNavBar() {
		hideNavBar(false);
	}
	
	
	
	
	public void showStatusBar() {
		showStatusBar(false);
	}
	
	public void hideStatusBar() {
		hideStatusBar(false);
	}
	
	
	
	// -- render --
	
	@Override
	public void render() {
//		GdxUtils.clearScreen(Color.CLEAR);
		GdxUtils.clearScreen(1,1,1,1);
		GdxUtils.clearScreenStencil();
		batch.setColor(1, 1, 1, 1);
		
		updateDebugInput();

		// screen
		renderScreen();
		
		
		batch.flush();
		batch.setColor(1, 1, 1, 1);
		Gdx.gl.glFlush();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		GdxUtils.clearScreenStencil();
		Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
		
		//nav buttons
		renderNavOverlay();
		
		// frame
		renderFrame();
		
		if (hasQueuedScreen()) {
			removeCurrentScreenProcessors();
			setScreen(queuedScreen);
			addProcessorsIfAppscreen();
			queuedScreen = null;
		}
	}
	
	
	// -- private methods --

	
	
	private void updateDebugInput() {
		// back to homescreen
//		if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {}

//		if(Gdx.input.isKeyJustPressed(Keys.T)) {}
		
//		updateDebugAppInput();

	}
	
	private void updateDebugAppInput() {
		// dispose cache and exit
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
			AppScreen.clearCache();
		}
		
		// set dipose cache
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
			Screen screen = getScreen();
			if(screen != null && screen instanceof AppScreen) {
				((AppScreen)screen).setClearCache();
			}
		}

		// dispose app
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
			AppScreen.clearFromCache(AppScreen.getCurrentApp());
		}

		// exit app
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
			Screen screen = getScreen();
			if (screen != null && screen instanceof AppScreen) {
				((AppScreen) screen).setExitCurrentApp();
			}

		}
		
		// adding a new app
		if(Gdx.input.isKeyJustPressed(Keys.X)) {
			App app = new ExampleApp(batch) {};
			System.out.println("Adding app: " +ExampleApp.class.getSimpleName() +" " + 
			(PhoneManager.INSTANCE.addAppClassAndUpdateHomeScreen(ExampleApp.class)));
			System.out.println("Adding app: " + app.getClass().getSimpleName()+ " " + 
			(PhoneManager.INSTANCE.addAppClassAndUpdateHomeScreen(app.getClass())));
		}
		if(Gdx.input.isKeyJustPressed(Keys.C)) {
//			System.out.println("Replacing app: " + ExampleApp.class.getSimpleName() + " " + 
//		(PhoneManager.INSTANCE.replaceAppIndexWithNewInstanceOf(ExampleApp.class)));
		}
	}

	
	
	private void renderScreen() {
		clipBegin();
		
		// default layer
		GdxUtils.clearScreen(offColor);
		// screen
		super.render();
		
		clipEnd();
	}
	
	private void clipBegin() {
		clipArea.set(Config.SCREEN_RECT_X, Config.SCREEN_Y, Config.SCREEN_WIDTH, Config.SCREEN_HEIGHT);
		Rectangle scissor = Pools.obtain(Rectangle.class);
		
		ScissorStack.calculateScissors(
				viewport.getCamera(), 
				Phone.INSTANCE.getBatch().getTransformMatrix(), 
				clipArea, 
				scissor);
		ScissorStack.pushScissors(scissor);
	}
	
	private void clipEnd() {
		// to avoid exception when minimizing the window
		if(ScissorStack.peekScissors() != null)
			Pools.free(ScissorStack.popScissors());
	}
	
	
	
	private void renderNavOverlay() {
		overlayViewport.apply();
		overlayStage.act();
		overlayStage.draw();
	}
	
	
	
	private void renderFrame() {
		batch.flush();
		batch.setShader(null);
		Gdx.gl.glColorMask(true, true, true, true);
		Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		
		viewport.apply();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		drawFrame();
		batch.end();
	}

	private void drawFrame() {
		// frame
		tmpColor.set(batch.getColor());
		batch.setColor(PhoneManager.INSTANCE.frameColor);
		
		batch.draw(frameTop, Config.PHONE_GAP_LEFT, 
				Config.PHONE_GAP_BOTTOM + Config.PHONE_HEIGHT - frameTop.getRegionHeight());
		batch.draw(frameBottom, Config.PHONE_GAP_LEFT, Config.PHONE_GAP_BOTTOM);
		batch.draw(frameLeft, Config.PHONE_GAP_LEFT, Config.PHONE_GAP_BOTTOM + frameBottom.getRegionHeight(),
				frameLeft.getRegionWidth(),Config.PHONE_HEIGHT - 2 * frameBottom.getRegionHeight());
		batch.draw(frameRight, Config.PHONE_GAP_LEFT + Config.PHONE_WIDTH - frameRight.getRegionWidth(),
				Config.PHONE_GAP_BOTTOM + frameBottom.getRegionHeight(),
				frameRight.getRegionWidth(), Config.PHONE_HEIGHT - 2 * frameBottom.getRegionHeight());
		
		batch.flush();	
		batch.setColor(tmpColor);
	}

	
	private void showNavBar(boolean instantly) {
		// return if already shown
		if (isNavBarShown)return;
		isNavBarShown = true;
		float moveDuration = instantly ? 0 : Config.OVERLAY_MOVE_DURATION;
		float fadeDuration = moveDuration;

		moveAndFadeInChildren(phoneNavArea, navForeground, 
				moveDuration, Config.PHONE_NAV_Y, fadeDuration);
	}

	private void hideNavBar(boolean instantly) {
		// return if already hidden
		if (!isNavBarShown)return;
		isNavBarShown = false;
		float moveDuration = instantly ? 0 : Config.OVERLAY_MOVE_DURATION;
		float fadeDuration = moveDuration;
		
		moveAndFadeOutChildren(phoneNavArea, navForeground, 
				moveDuration, -phoneNavArea.getHeight() / 2f, fadeDuration);
	}
	
	
	

	
	
	private void showStatusBar(boolean instantly ) {
		if(isStatusBarShown)return;
		isStatusBarShown = true;
		
		float moveDuration = instantly ? 0 : Config.OVERLAY_MOVE_DURATION;
		float fadeDuration = moveDuration;
		
		moveAndFadeInChildren(statusBarArea, statusBarForeground, 
				moveDuration, 
				overlayViewport.getWorldHeight() - Config.STATUS_BAR_TOP_Y_SPACE - statusBarArea.getHeight(),
				fadeDuration);
	}

	
	private void hideStatusBar(boolean instantly) {
		if(!isStatusBarShown)return;
		isStatusBarShown = false;
		
		float moveDuration = instantly ? 0 : Config.OVERLAY_MOVE_DURATION;
		float fadeDuration = moveDuration;
		
		moveAndFadeOutChildren(statusBarArea, statusBarForeground, 
				moveDuration, overlayViewport.getWorldHeight(), fadeDuration);
	}
	
	
	
	
	
	
	private void moveAndFadeInChildren(Actor movingActor, Actor fadingChild, float moveDuration, float toY,  float fadeDuration) {
		movingActor.addAction(Actions.after(Actions.sequence(
				Actions.targeting(fadingChild, Actions.visible(true)),
				Actions.parallel(
						Actions.targeting(fadingChild, Actions.fadeIn(fadeDuration)),
						Actions.moveTo(movingActor.getX(), toY, moveDuration, 
								Interpolation.sineIn)))));
	}
	
	private void moveAndFadeOutChildren(Actor movingActor, Actor fadingChild, float moveDuration, float toY, float fadeDuration) {
		movingActor.addAction(
				Actions.after(Actions.sequence(
						Actions.parallel(
								Actions.targeting(fadingChild, Actions.fadeOut(fadeDuration)),
								Actions.moveTo(movingActor.getX(), toY, moveDuration, Interpolation.sineIn)),
						Actions.targeting(fadingChild, Actions.visible(false)))));
	}
	
	
	
	
	
	private void disposeAllScreens() {
		HomeScreen.INSTANCE.dispose();
		//AppScreen.clearCache();
		AppScreen.disposeOnExitApplication();
		
	}
	
	
	
	private void addProcessorsIfAppscreen() {
		if(!isAppScreenActive())return;
		AppScreen as = (AppScreen)getScreen();
		for(InputProcessor p : as.getCurrentApp().getProcessors()) {
			multiplexer.addProcessor(p);
		};
	}
	
	public void updateProcessorsIfAppScreen() {
		removeCurrentScreenProcessors();
		addProcessorsIfAppscreen();
	}

	public void addProcessor(InputProcessor ...processors) {
		if(processors != null)for(InputProcessor p : processors) {
			if(p!= null)multiplexer.addProcessor(p);
		}
	}
	
	private void removeCurrentScreenProcessors() {
		for(int i = multiplexer.size() - 1; i > 0; i--) {
			multiplexer.removeProcessor(i);
		}
	}
	
	
	// -- other --
	
	@Override
	public void pause() {
		log.debug("pause game");
		super.pause();
	}
	
	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		overlayViewport.update(width, height, true);
		super.resize(width, height);
	}

	@Override
	public void dispose() {
		super.dispose();//hides screen
		disposeAllScreens();

		batch.dispoeBatch();
		assets.dispose();
		Fonts.dispose();
	}


}
