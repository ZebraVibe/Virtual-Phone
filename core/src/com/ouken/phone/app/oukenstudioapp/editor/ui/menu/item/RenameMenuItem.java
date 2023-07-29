package com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.TextFieldWindow;

public class RenameMenuItem<T> extends TFWindowMenuItem<T>{

	public RenameMenuItem(String text, Skin skin, ShapeRenderer renderer) {
		this(text, null, skin, renderer);
	}
	
	public RenameMenuItem(String text, TextureRegion icon, Skin skin, ShapeRenderer renderer) {
		super(text, icon, skin, renderer);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onWindowCreated(T selected, TextFieldWindow window, String header, String preText) {
		super.onWindowCreated(selected, window, "Rename", preText);
	}
	
	@Override
	public boolean openWindowIf(T selected) {
		return selected != null;
	}

}
