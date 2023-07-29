package com.ouken.phone.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.Phone;
import com.ouken.phone.app.App;
import com.ouken.phone.app.utils.AppScreenViewport;
import com.ouken.phone.common.PhoneManager;
import com.ouken.phone.utils.GdxUtils;
import com.ouken.phone.utils.actor.ColoredButton;
import com.ouken.phone.utils.font.FontSize;
import com.ouken.phone.utils.font.Fonts;

public class CacheScreen extends ScreenAdapter{
	
	private static final Logger log = new Logger(CacheScreen.class.getName(), Logger.DEBUG);
	
	private Stage stage;
	private Viewport viewport;
	
	private final float SCREENSCHOT_WIDTH = 64;
	
	public CacheScreen() {}
	
	@Override
	public void show() {
		viewport = new AppScreenViewport(true);
		stage = new Stage(viewport, Phone.INSTANCE.getBatch());
		
		Phone.INSTANCE.addProcessor(stage);
		
		init();
	}
	
	private void init() {
		Table content = new Table();
		
		int cols = 3;
		float cellPad = (viewport.getWorldWidth() - cols* SCREENSCHOT_WIDTH )/ (2f * cols);
		content.top().left();
		content.defaults().pad(cellPad);
		
		int count  = 0;
		for(int i = 0; i < AppScreen.SCREENSHOTS.size; i++) {
			Texture tex = AppScreen.SCREENSHOTS.getValueAt(i);
			App app = AppScreen.SCREENSHOTS.getKeyAt(i);
			
			ColoredButton screenShot = new ColoredButton(new TextureRegion(tex), Color.GRAY.cpy());
			screenShot.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					return event.getTarget() == screenShot;}
				
				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
					Phone.INSTANCE.queueAppScreen(app.getClass());
				}
			});
			
			float ratio =  screenShot.getHeight() / screenShot.getWidth();
			float ssWidth = SCREENSCHOT_WIDTH;
			float ssHeight = ssWidth * ratio;
			screenShot.setSize(ssWidth, ssHeight);
			
			float gapY = 8;
			float gapX = 8;
			
			LabelStyle style = new LabelStyle();
			style.font = Fonts.getDefaultFontBySize(FontSize.x12);
			Label label = new Label(HomeScreen.INSTANCE.getFormatedAppName(app.getClass()), style);
			label.setTouchable(Touchable.disabled);
			
			Actor icon = HomeScreen.INSTANCE.createAppIcon(app.getClass());
			icon.setSize(label.getHeight(), label.getHeight());
			icon.setTouchable(Touchable.disabled);
			
			style.font = Fonts.getDefaultFontBySize(FontSize.x16);
			Label removeApp = new Label("X", style);
			removeApp.setVisible(false);
			removeApp.addListener(new InputListener() {
				
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					AppScreen.clearFromCache(app);
					screenShot.remove();
					if(AppScreen.isCacheEmpty())Phone.INSTANCE.queueHomeScreen();
					return true;
				}
				
			});
			
			screenShot.addListener(new InputListener() {
				
				@Override
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					removeApp.setVisible(true);
				}
				
				public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					removeApp.setVisible(false);
				};
				
			});
			
			float iconX = (screenShot.getWidth() - (icon.getWidth() + label.getWidth() + gapX)) / 2f;

			icon.setPosition(iconX, - icon.getHeight() - gapY);
			label.setPosition(icon.getX() + icon.getWidth() + gapX, icon.getY());
			removeApp.setPosition(screenShot.getWidth(), screenShot.getHeight(), Align.topRight);
			
			screenShot.addActor(icon);
			screenShot.addActor(label);
			screenShot.addActor(removeApp);
			
			
			content.add(screenShot).size(ssWidth, ssHeight);
			
			
			count++;
			if((count %= cols) == 0)content.row();
			
		};
		content.pack();
		
		ScrollPane scroll = new ScrollPane(content);
		scroll.setFlickScroll(false);
		scroll.setFillParent(true);
		stage.addActor(scroll);
		stage.setScrollFocus(scroll);
		
//		stage.setDebugAll(true);
		
	}
	
	@Override
	public void render(float delta) {
		GdxUtils.clearScreen(PhoneManager.INSTANCE.homeScreenBackgroundColor);
		
		viewport.apply();
		stage.act();
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
	}
	
	@Override
	public void hide() {
		dispose();
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}
	
}
