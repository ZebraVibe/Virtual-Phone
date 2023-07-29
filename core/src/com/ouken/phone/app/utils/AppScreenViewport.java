package com.ouken.phone.app.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ouken.phone.Phone;
import com.ouken.phone.config.Config;

/**Not ment to be used with resizable window !*/
public class AppScreenViewport extends FitViewport {
	
	private final Rectangle bounds = new Rectangle(Config.SCREEN_X, Config.SCREEN_Y, Config.SCREEN_WIDTH, Config.SCREEN_HEIGHT);
	
	// -- constructor --
	/**Creates an AppScreenViewport with {@link AppScreenViewport#AppScreenViewport(toInnerRect)} and with toInnerRect = false*/
	public AppScreenViewport() {
		this(false);
	}
	
	/**Creates an AppScreenViewport with worldWidth & -Height = <br><ul>
	 * (toInnerRect = false) : {@link Config#SCREEN_WIDTH} & {@link Config#SCREEN_HEIGHT}<br>
	 * (toInnerRect = true) : {@link Config#SCREEN_RECT_WIDTH} & {@link Config#SCREEN_RECT_HEIGHT}</ul>*/
	public AppScreenViewport(boolean toInnerRect) {
		this(toInnerRect ? Config.SCREEN_RECT_WIDTH : Config.SCREEN_WIDTH, 
				toInnerRect ? Config.SCREEN_RECT_HEIGHT : Config.SCREEN_HEIGHT, toInnerRect);
	}
	
	public AppScreenViewport(float worldWidth, float worldHeight, boolean toInnerRect) {
		this(worldWidth, worldHeight, new OrthographicCamera(), toInnerRect);
	}
	
	public AppScreenViewport(float worldWidth, float worldHeight, Camera camera, boolean toInnerRect) {
		super(worldWidth, worldHeight, camera);
		if(toInnerRect) bounds.set(Config.SCREEN_RECT_X, Config.SCREEN_RECT_Y, Config.SCREEN_RECT_WIDTH, Config.SCREEN_RECT_HEIGHT);
		setScreenBounds((int)bounds.x, (int)bounds.y, (int)bounds.width, (int)bounds.height);
	}
	
	
	// -- update -- //

	@Override
	public void update(int screenWidth, int screenHeight, boolean centerCamera) {
		super.update(screenWidth, screenHeight, centerCamera);
	}
	
	
	// -- public methdos --
	
	
	@Override
	public void setScreenX(int screenX) {
		screenX = (int) MathUtils.clamp(screenX, bounds.x, bounds.x + bounds.width);
		super.setScreenX(screenX);
	}
	
	@Override
	public void setScreenY(int screenY) {
		screenY = (int)MathUtils.clamp(screenY, bounds.y, bounds.y + bounds.height);
		super.setScreenY(screenY);
	}
	
	@Override
	public void setScreenWidth(int screenWidth) {
		screenWidth = (int)MathUtils.clamp(screenWidth, bounds.x, bounds.width);
		super.setScreenWidth(screenWidth);
	}
	
	@Override
	public void setScreenHeight(int screenHeight) {
		screenHeight = (int)MathUtils.clamp(screenHeight, bounds.y, bounds.height);
		super.setScreenHeight(screenHeight);
	}
	
	@Override
	public void setScreenPosition(int screenX, int screenY) {
		setScreenX(screenX);
		setScreenY(screenY);
	}
	
	@Override
	public void setScreenSize(int screenWidth, int screenHeight) {
		setScreenWidth(screenWidth);
		setScreenHeight(screenHeight);
	}
	
	@Override
	public void setScreenBounds(int screenX, int screenY, int screenWidth, int screenHeight) {
		setScreenPosition(screenX, screenY);
		setScreenSize(screenWidth, screenHeight);
	}


	

}
