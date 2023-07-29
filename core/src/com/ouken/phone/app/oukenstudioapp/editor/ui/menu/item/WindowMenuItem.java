package com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.ouken.phone.app.oukenstudioapp.editor.utils.WindowPane;

/**
 * 
 * @author sebas
 *
 * @param <T> the selected object type
 * @param <W> the window type
 */
public abstract class WindowMenuItem<T, W extends WindowPane> extends MenuItem<T>{
	
	private ShapeRenderer renderer;
	
	
	public WindowMenuItem(String text, Skin skin, ShapeRenderer renderer) {
		this(text, null, skin, renderer);
	}
	
	public WindowMenuItem(String text, TextureRegion icon, Skin skin, ShapeRenderer renderer) {
		super(text, icon, skin);
		this.renderer = renderer;
	}

	@Override
	public final void onClick(T selected) {
		if(!openWindowIf(selected))return;
		W window = createWindow(selected, renderer);
		onWindowCreated(selected, window);
		addToStageAndCenterPosition(window);
		onClick(selected, window);
	}

	
	
	public abstract W createWindow(T selected, ShapeRenderer renderer);
	
	/**convinience method to change i.e. header text from window*/
	public void onWindowCreated(T selected, W window) {}
	
	/**override this to controll if a window is even created or not*/
	public boolean openWindowIf(T selected) { return true; }
	
	public abstract void onClick(T selected, W window);

	// -- private --
	
	private void addToStageAndCenterPosition(WindowPane window) {
		if(getStage() == null)return;
		window.setPosition(getStage().getWidth() /2, getStage().getHeight() /2, Align.center);
		getStage().addActor(window);
		getStage().setKeyboardFocus(window);
	}

}
