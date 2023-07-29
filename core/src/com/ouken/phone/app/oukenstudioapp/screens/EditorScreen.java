package com.ouken.phone.app.oukenstudioapp.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.oukenstudioapp.OukenStudioApp;
import com.ouken.phone.app.oukenstudioapp.assets.AssetDescriptors;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.common.EditorManager;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.ContextMenu;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.EntityExplorerTreeContextMenu;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.FileExplorerBrowserContextMenu;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.FileExplorerTreeContextMenu;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.EntityExplorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.Explorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.FileExplorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.CustomNode;
import com.ouken.phone.app.oukenstudioapp.editor.utils.Pane;
import com.ouken.phone.app.oukenstudioapp.editor.utils.PaneSpriteDrawable;
import com.ouken.phone.app.oukenstudioapp.editor.utils.TabPane;
import com.ouken.phone.app.oukenstudioapp.project.Project;
import com.ouken.phone.app.oukenstudioapp.scene.DefaultScene;
import com.ouken.phone.app.oukenstudioapp.scene.Scene;
import com.ouken.phone.app.utils.AppScreenViewport;
import com.ouken.phone.utils.GdxUtils;

public class EditorScreen extends ScreenAdapter {

	private static final Logger log = new Logger(EditorScreen.class.toString(), Logger.DEBUG);
	
	// -- constants --
	
	private static final float BOTTOM_TAP_PANE_HEIGHT = 200;
	private static final float TOP_TAP_PANE_HEIGHT = 165;
	private static final float TOP_NAV_BAR_HEIGHT = TabPane.DEFAULT_TAB_HEIGHT;
	
	private static final float DRAG_BUTTON_W = 64, DRAG_BUTTON_H = 14;
	
	private static final String DRAG_BUTTON_NAME = "dragButton";

	// -- attributes --
	private OukenStudioApp app;
	private EditorManager manager = EditorManager.INSTANCE;
	private AssetManager assets;
	private SpriteBatch batch;
	private ShapeRenderer renderer;
	private Viewport sceneViewport, uiViewport;
	private Stage uiStage;
	
	private TextureAtlas gameplayAtlas, uiAtlas;
	private Skin skin;
	
	private Table topContainer, bottomContainer;
	private Pane topNav;
	private TabPane bottomTabPane, topTabPane; 
	
//	private Project project;
	private FileExplorer fileExplorer;
	private EntityExplorer entityExplorer;
	private Scene currentScene;
	private ContextMenu<?> lastMenu;
	
	private boolean isTopHidden, isBottomHidden, isTopMoving, isBottomMoving;
	private float hideMoveDuration = 0.18f;
	

	// -- constructor --

	public EditorScreen(OukenStudioApp app) {
		this.app = app;
		this.assets = app.getAssetManager();
		this.batch = app.getBatch();
		skin = app.getSkin();
	}

	// -- init --
	@Override
	public void show() {
		app.log().debug("show() : " + getClass().getSimpleName());
		init();
	}

	private void init() {
		renderer = new ShapeRenderer();
		gameplayAtlas = assets.get(AssetDescriptors.GAMEPLAY);
		uiAtlas = assets.get(AssetDescriptors.EDITOR_UI);
		
		sceneViewport = new AppScreenViewport(true);
		uiViewport = new AppScreenViewport(true);
		uiStage = new Stage(uiViewport, batch);
		uiStage.addCaptureListener(createUICaptureListener());
//		uiStage.getRoot().setTouchable(Touchable.childrenOnly);
		
		initScene();
		initUI();
		app.addProcessors(uiStage, currentScene);
		
		//log.debug("InputProcessors: " + app.getProcessors());
	}
	
	
	/**
	 * used to capture event before there are passed down to canvas input processor
	 */
	private InputListener createUICaptureListener() {
		return new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				Actor target = event.getTarget();
				Stage stage = event.getStage();
				if(target == stage.getRoot()) {
					event.stop();
				
					hideContextMenu();
				}
				
				// remove keyboard focus when clicking away
				if(target == null || target != stage.getKeyboardFocus()) {
					stage.setKeyboardFocus(null);
				}
				
				return false;
			}

			
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				Actor target = event.getTarget();
				Stage stage = event.getStage();
				if(target == stage.getRoot())event.stop();
				return false;
			}
			
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				Actor target = event.getTarget();
				Stage stage = event.getStage();
				if(target == stage.getRoot())event.stop();
				return false;
			}
			
			@Override
			public boolean keyTyped(InputEvent event, char character) {
				Actor target = event.getTarget();
				Stage stage = event.getStage();
				if(target == stage.getRoot())event.stop();
				return false;
			}
			
			@Override
			public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY) {
				Actor target = event.getTarget();
				//log.debug("scroll has target= " + (target != null));
				Stage stage = event.getStage();
				if(target == stage.getRoot()) {
					event.stop();
					return false;
				}
				return target != null;
			}
			
		};
	}

	private void initScene() {
		setScene(new DefaultScene(sceneViewport, this));
		
	}
	
	private void initUI() {
		topContainer = new Table();
		topContainer.setFillParent(true);
		topContainer.center().top();

		topContainer.add(createTopNavBar()).height(TOP_NAV_BAR_HEIGHT).expandX().fill().row();
		topContainer.add(createTopTabPane()).height(TOP_TAP_PANE_HEIGHT).expandX().fill().row();
		topContainer.add(createHideButton(false)).size(DRAG_BUTTON_W, DRAG_BUTTON_H);
		
		bottomContainer = new Table();
		bottomContainer.setFillParent(true);
		bottomContainer.center().bottom();
		bottomContainer.add(createHideButton(true)).size(DRAG_BUTTON_W, DRAG_BUTTON_H).row();
		bottomContainer.add(createBottomTabPane()).height(BOTTOM_TAP_PANE_HEIGHT).expandX().fill();
		
		uiStage.addActor(topContainer);
		uiStage.addActor(bottomContainer);
		//uiStage.setDebugAll(true);
		initContextMenus();
	}
	
	
	
	// -- navigation bar --
	private Table createTopNavBar() {
		topNav = new Pane(renderer).paneColor(Pane.DARK_GRAY).smoothEdges(false);
		return topNav;
		
	}
	
	// -- top pane --
	private Table createTopTabPane() {
		topTabPane = new TabPane("Hierarchy", (entityExplorer = new EntityExplorer(currentScene, skin, renderer)), true, renderer);
		topTabPane.setTouchable(Touchable.enabled);
		topTabPane.smoothEdges(false, false, true, true);
		topTabPane.addTab("Tab 2", null);
		topTabPane.addTab("Tab 3", null);
		topTabPane.addTab("Tab 4", null);
		return topTabPane;
	}
	
	
	
	// -- bottom pane -- 
	private Table createBottomTabPane() {
		bottomTabPane = new TabPane("Project", (fileExplorer = new FileExplorer(this, skin, renderer)), false, renderer);
		bottomTabPane.setTouchable(Touchable.enabled);
		bottomTabPane.smoothEdges(true, true, false, false);
		bottomTabPane.addTab("Tab 2", null);
		bottomTabPane.addTab("Tab 3", null);
		bottomTabPane.addTab("Tab 4", null);
		return bottomTabPane;
	}
	
	private Actor createHideButton(boolean bottom) {
		Pane drag = new Pane(renderer).paneColor(Pane.DARK_GRAY);
		drag.smoothEdges(bottom, bottom, !bottom, !bottom);
		
		ButtonStyle style = new ButtonStyle();
		style.up = new PaneSpriteDrawable(skin.getRegion(RegionNames.PANE), Color.CLEAR.cpy());
		Button button = new Button(style);
		button.setName(DRAG_BUTTON_NAME);
		
		button.addListener(new ChangeListener() {
			boolean moving;
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
//				if(moving)return;
//				float height = bottom ? bottomTabPane.getHeight() - TabPane.DEFAULT_TAB_HEIGHT : topTabPane.getHeight();
//				(bottom ? bottomContainer : topContainer).addAction(Actions.after(Actions.sequence(
//						Actions.parallel(
//								Actions.run(() -> moving = true ),
//								Actions.moveBy(0,bottom ? (isBottomHidden ? height : -height) : (isTopHidden ? -height : height), hideMoveDuration,Interpolation.sineOut)), 
//						Actions.run(() -> {
//							if (bottom)isBottomHidden = !isBottomHidden;
//							else isTopHidden = !isTopHidden;
//							moving = false;
//						}))));
				
				if(bottom)changeHidingOfBottom();
				else changeHidingOfTop();
				
			}
			
			
			
			
			
		});
		
		drag.add(button).grow();
		return drag;
	}
	
	
	
	
	// -- drag and drop --
	
	
	
	// -- context menu --
	private void initContextMenus() {
		FileExplorerTreeContextMenu fileExplorerMenu = new FileExplorerTreeContextMenu(fileExplorer, renderer, skin);
		EntityExplorerTreeContextMenu entityExplorerMenu = new EntityExplorerTreeContextMenu(entityExplorer, renderer, skin);
		FileExplorerBrowserContextMenu fileBrowserMenu = new FileExplorerBrowserContextMenu(fileExplorer, renderer, skin);
		
		Array<ContextMenu<?>> menus = new Array<ContextMenu<?>>();
		
		setupExplorerMenu(entityExplorerMenu, entityExplorer, true, menus);
		setupExplorerMenu(fileExplorerMenu, fileExplorer,true, menus);
		setupExplorerMenu(fileBrowserMenu, fileExplorer, false, menus);
		
	}
	
	/**
	 * 
	 * @param menu
	 * @param explorer
	 * @param tree if true the menu is added to the explorer tree else to the browser
	 * @param menus
	 */
	private void setupExplorerMenu(ContextMenu<?> menu, Explorer<?,?> explorer, boolean tree, Array<ContextMenu<?>> menus) {
		menus.add(menu);
		uiStage.addActor(menu);
		uiStage.addListener(createOpenOrCloseContextMenuListener(menu, explorer, tree ? explorer.getExplorerTree() : explorer.getContentBrowserContainer()));
	}
	
	private InputListener createOpenOrCloseContextMenuListener(ContextMenu menu,Explorer explorer, Actor toOpenIn) {
		return new InputListener() { // add right click listener
			
			Rectangle rect = new Rectangle();
			Vector2 tmp = new Vector2();
			
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				CustomNode overNode = (CustomNode) explorer.getExplorerTree().getOverNode();
				//Actor target = event.getTarget();
				if(overNode != null && explorer.getExplorerTree().getSelectedNode() != overNode) {
					explorer.chooseNode(overNode);
					
				}
				return true; // check in which pane is right clicked to select context for menu
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if(button == Buttons.LEFT) {
					rect.set(menu.getX(), menu.getY(), menu.getWidth(), menu.getHeight());
					if(menu.isVisible()) {
						if(!rect.contains(x,y)) {
							menu.setVisible(false);
						}
					}
					
				}else if(button == Buttons.RIGHT) {
					tmp.setZero();
					toOpenIn.localToStageCoordinates(tmp);
					rect.set(tmp.x, tmp.y, toOpenIn.getWidth(), toOpenIn.getHeight());
					tmp.set(event.getStageX(), event.getStageY());
					if(rect.contains(tmp)) {
						menu.setVisible(true);
						float newX = MathUtils.clamp(x, 0, uiStage.getWidth() - menu.getWidth());
						float newY = MathUtils.clamp(y, menu.getHeight(), uiStage.getHeight());
						
						//EditorUtils.mark(tmp.x, tmp.y, event.getStage(), skin);
						
						menu.setPosition(newX, newY, Align.topLeft);
						
						if(lastMenu != null && menu != lastMenu)lastMenu.setVisible(false);
						lastMenu = menu;
					}
				}
			}
			
		};
	}
	
	
	// -- public mehtods --
	
	public AssetManager getAssets() {
		return assets;
	}
	
	public SpriteBatch getBatch() {
		return batch;
	}
	
	public FileExplorer getFileExplorer() {
		return fileExplorer;
	}
	
	public EntityExplorer getEntityExplorer() {
		return entityExplorer;
	}
	
	public Stage getStage() {
		return uiStage;
	}
	
	public Scene getCurrentScene() {
		return currentScene;
	}
	
	public void setScene(Scene scene) {
		currentScene = scene;
		currentScene.repositionSceneCamera();
	}
	
	
	public boolean isTopHidden() {
		return isTopHidden;
	}
	
	public boolean isBottomHidden() {
		return isBottomHidden;
	}
	
	
	
	
	public boolean isTopAboutToShow() {
		return isTopHidden && isTopMoving;
	}
	
	public boolean isTopAboutToHide() {
		return !isTopHidden && isTopMoving;
	}
	
	public boolean isBottomAboutToHide() {
		return !isBottomHidden && isBottomMoving;
	}
	
	public boolean isBottomAboutToShow() {
		return isBottomHidden && isBottomMoving;
	}
	
	public void changeHidingOf(boolean top, boolean bottom) {
//		Button topButton = (Button)topContainer.findActor(DRAG_BUTTON_NAME);
//		Button bottomButton = (Button)bottomContainer.findActor(DRAG_BUTTON_NAME);
//		
//		// if unequally hidden -> hide both first
//		if (top && bottom && topButton.isChecked() != bottomButton.isChecked()) {
//			topButton.setChecked(true);
//			bottomButton.setChecked(true);
//			return;
//		}
//		
//		if(top)topButton.toggle();
//		if(bottom)bottomButton.toggle();
		
		
		// if unequally hidden -> hide both first
		if(top && bottom) {
			if((isTopHidden || isTopAboutToHide()) != (isBottomHidden || isBottomAboutToHide())) {
				hideTop(true);
				hideBottom(true);
				return;
			}
//			else if((!isTopHidden || isTopAboutToShow()) != (!isBottomHidden || isBottomAboutToShow())) {}
		}
		if(top)changeHidingOfTop();
		if(bottom)changeHidingOfBottom();
		
	}

	
	public void changeHidingOfTop() {
		hideTop(!isTopHidden);
	}
	
	public void changeHidingOfBottom() {
		hideBottom(!isBottomHidden);
	}

	public void hideTop(boolean hide) {
		if(hide && (isTopHidden || isTopAboutToHide()) || !hide && (!isTopHidden || isTopAboutToShow()))return;
		
		topContainer.addAction(Actions.after(Actions.run(() -> {
			// check hide here after last action is done setting booleans
			if(hide && (isTopHidden || isTopAboutToHide()) || !hide && (!isTopHidden || isTopAboutToShow()))return;
			
			float distance = topTabPane.getHeight();
			float sign = !isTopHidden ? 1 : -1;
			isTopMoving = true;
			
			topContainer.addAction(Actions.sequence(
					Actions.parallel(
							Actions.run(() -> {
								//isTopMoving = true;
							}),
							Actions.moveBy(0 , distance * sign, hideMoveDuration, Interpolation.sineOut)), 
					Actions.run(() -> {
						isTopMoving = false;
						isTopHidden = !isTopHidden;
					})));
		})));
	}
	
	public void hideBottom(boolean hide) {
		if(hide && (isBottomHidden || isBottomAboutToHide()) || !hide && (!isBottomHidden || isBottomAboutToShow()))return;
		
		bottomContainer.addAction(Actions.after(Actions.run(() -> {
			
			// check hide here after last action is done setting booleans
			if(hide && (isBottomHidden || isBottomAboutToHide()) || !hide && (!isBottomHidden || isBottomAboutToShow()))return;
			
			float distance = bottomTabPane.getHeight() - TabPane.DEFAULT_TAB_HEIGHT;
			float sign = !isBottomHidden ? -1 : 1;
			isBottomMoving = true;
			
			bottomContainer.addAction(Actions.sequence(
					Actions.parallel(
							Actions.run(() -> {
								//isBottomMoving = true;
							}),
							Actions.moveBy(0 , distance * sign, hideMoveDuration, Interpolation.sineOut)), 
					Actions.run(() -> {
						isBottomMoving = false;
						isBottomHidden = !isBottomHidden;
					})));
		})));
	}
	
	
	
	public void hideContextMenu() {
		if(lastMenu != null)lastMenu.setVisible(false);
	}
	
	// -- render --
	@Override
	public void render(float delta) {
		GdxUtils.clearScreen(Pane.GRAY);
		
		updateDebugInput();
		
		sceneViewport.apply();
//		canvasStage.act();
//		canvasStage.draw();
		currentScene.render();
		
		uiViewport.apply();
		uiStage.act();
		uiStage.draw();
		
	}

	// -- private methods --
	private void updateDebugInput() {
			// create test window
//		if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
//			WindowPane a = new WindowPane("Window", null, renderer); // default size 192,96
//			a.setPosition(uiViewport.getWorldWidth() / 2f,
//					uiViewport.getWorldHeight() / 2f, Align.center);
//			uiStage.addActor(a);
//		}
	}

	// -- resize and dispose -- 
	@Override
	public void resize(int width, int height) {
		log.debug("resize() : " + getClass().getSimpleName());
		sceneViewport.update(width, height, true); // false so that 0,0 is in center?
		uiViewport.update(width, height, true);
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		fileExplorer.dispose();
		currentScene.dispose();
		uiStage.dispose();
		renderer.dispose();
	}

	
	
}
