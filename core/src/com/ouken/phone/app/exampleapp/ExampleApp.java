package com.ouken.phone.app.exampleapp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.app.App;
import com.ouken.phone.app.AppInfo;
import com.ouken.phone.app.utils.AppScreenViewport;
import com.ouken.phone.utils.GdxUtils;

@AppInfo(name = "Example Application", authors = { "ouken11", "zebra" }, version = "0.1")
public class ExampleApp extends App {

	// -- attributes --
	private Viewport viewport;

	// -- constructor --
	public ExampleApp(SpriteBatch batch) {
		super(batch);
	}

	// -- init --

	@Override
	public void create() {
		log().debug("create()");
		viewport = new AppScreenViewport();
		hidePhoneOverlay();
	}

	// -- public methods --

	@Override
	public void render() {
		Color color = Color.GRAY;
		GdxUtils.clearScreen(color);
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		log().debug("resize()");
		viewport.update(width, height, true);
		super.resize(width, height);
	}

	@Override
	public void pause() {
		log().debug("pause()");
		super.pause();
	}

	@Override
	public void resume() {
		log().debug("resume()");
		hidePhoneOverlay();
		super.resume();
	}

	@Override
	public void dispose() {
		log().debug("dipose()");
		super.dispose();
	}

}
