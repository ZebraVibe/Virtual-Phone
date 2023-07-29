package com.ouken.phone.app.crawlapp;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ouken.phone.app.App;
import com.ouken.phone.app.AppInfo;
import com.ouken.phone.app.crawlapp.screens.LoadingScreen;


@AppInfo(name = "Crawler", authors = { "ouken11", "zebra"}, version = "0.0")
public class CrawlApp extends App{
	
	// -- attributes --
	
	private final AssetManager assets;
	
	
	// -- constructors --
	public CrawlApp(SpriteBatch batch) {
		super(batch);
		assets = new AssetManager();
	}
	
	// -- init --
	
	@Override
	public void create() {
		setScreen(new LoadingScreen(this));
		hidePhoneOverlay();
	}
	
	public AssetManager getAssets() {
		return assets;
	}
	

	
	@Override
	public void resume() {
		hidePhoneOverlay();
		super.resume();
	}
	

	
	@Override
	public void dispose() {
		super.dispose();
		assets.dispose();
	}

	
	
}
