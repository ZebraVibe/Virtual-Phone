package com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.TextFieldWindow;

public class CreateMenuItem<T> extends TFWindowMenuItem<T>{
	
	public CreateMenuItem(String text, Skin skin, ShapeRenderer renderer) {
		this(text, null, skin, renderer);
	}
	
	public CreateMenuItem(String text, TextureRegion icon, Skin skin, ShapeRenderer renderer) {
		super(text, icon, skin, renderer);
	}
	
	@Override
	public void onWindowCreated(T selected, TextFieldWindow window, String header, String preText) {
		super.onWindowCreated(selected, window, "Create", "new_" + getType(false));
	}
	
	private String getType(boolean upperCase) {
		String type = getLabelText().toLowerCase();
		if(type == null)return "";
		type = type.replace("create", "");
		type = type.replace("new ", "");
		type = type.replace(" ", "");
		if(upperCase && !type.isEmpty())type = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
		return type;
	}
	
}
