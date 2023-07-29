package com.ouken.phone.app.crawlapp.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.crawlapp.CrawlApp;
import com.ouken.phone.app.crawlapp.assets.AssetDescriptors;
import com.ouken.phone.app.utils.AppScreenViewport;
import com.ouken.phone.utils.GdxUtils;

public class LoadingScreen extends ScreenAdapter{

	public static final int LOADING_BAR_WIDTH = 128, LOADING_BAR_HEIGHT = 32;
	
	private CrawlApp game;
	private AssetManager assets;
	private ShapeRenderer renderer;

	private Viewport viewport;
	private float waitAfterLoading = 1;//sec
	
	public LoadingScreen(CrawlApp game) {
		this.game = game;
		this.assets = game.getAssets();
	}
	
	@Override
	public void show() {
		renderer = new ShapeRenderer();
		viewport = new AppScreenViewport();
		
		AssetDescriptors.ALL.forEach(desc -> assets.load(desc));
		game.log().debug("Loading Assets...");
	}
	
	@Override
	public void render(float delta) {
		GdxUtils.clearScreen(0,0,0,1);
		
		renderer.setProjectionMatrix(viewport.getCamera().combined);
		
		float progress = assets.getProgress();
		renderer.begin(ShapeType.Filled);
	
		renderer.setColor(Color.WHITE);
		renderer.rect((viewport.getWorldWidth() - LOADING_BAR_WIDTH) /2f, (viewport.getWorldHeight() - LOADING_BAR_HEIGHT) / 2f, 
				LOADING_BAR_WIDTH * progress, LOADING_BAR_HEIGHT);
		renderer.end();
		
		if(assets.update()) {
			
			if(waitAfterLoading > 0) {
				waitAfterLoading -= delta;
				return;
			};
			
			game.log().debug("...Done Loading Assets");
			game.setScreen(new EditorScreen2(game));
		}
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
		renderer.dispose();
	}
	
	
}
