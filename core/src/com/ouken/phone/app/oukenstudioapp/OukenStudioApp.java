package com.ouken.phone.app.oukenstudioapp;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ouken.phone.app.App;
import com.ouken.phone.app.AppInfo;
import com.ouken.phone.app.oukenstudioapp.common.EditorManager;
import com.ouken.phone.app.oukenstudioapp.screens.LoadingScreen;
import com.ouken.phone.config.Config;


@AppInfo(name = "Ouken Studio", authors = {"Sebastian Krahnke"}, version = "0.1")
public class OukenStudioApp extends App{

	private AssetManager assets;
	private Skin skin;

	public OukenStudioApp(SpriteBatch batch) {
		super(batch);
	}

	// -- init --

	@Override
	public void create() {
		assets = new AssetManager();
		skin = new Skin();
		setScreen(new LoadingScreen(this));
		hidePhoneOverlay();
	}

	public AssetManager getAssetManager() {
		return assets;
	}
	public Skin getSkin() {
		return skin;
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
		skin.dispose();
	}

	
	
	
}
