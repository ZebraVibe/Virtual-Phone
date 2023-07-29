package com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.CustomNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.TextFieldWindow;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.TextFieldWindow.TFValidatable;

public class TFWindowMenuItem<T> extends WindowMenuItem<T, TextFieldWindow>{
	
	public TFWindowMenuItem(String text, Skin skin, ShapeRenderer renderer) {
		this(text, null, skin, renderer);
	}
	
	public TFWindowMenuItem(String text, TextureRegion icon, Skin skin, ShapeRenderer renderer) {
		super(text, icon, skin, renderer);
	}
	
	
	@Override
	public TextFieldWindow createWindow(T selected, ShapeRenderer renderer) {
		return new TextFieldWindow("Window", "enter_text", getSkin(), renderer);
	}
	
	@Override
	public final void onWindowCreated(T selected, TextFieldWindow window) {
		onWindowCreated(selected, window, window.getHeaderText(),window.getTextFieldText());
	}
	
	/**convinience method to more easily change the header and pretext of the window*/
	public void onWindowCreated(T selected, TextFieldWindow window, String header, String preText) {
		window.setHeaderText(header);
		window.setTextFieldText(preText);
	}
	

	@Override
	public void onClick(T selected, TextFieldWindow window) {
		onClick(selected, window, null, null);
	}
	
	/**
	 * [Note]:Convinience method to override validate and onSuccedd like so:<br>
	 * 
	 * super(selected, window, tf -> {return false;}, tf -> {})
	 * 
	 * you can also override {@link #onClick(CustomNode, TextFieldWindow)} and
	 * add setOnValidate and addOnSuccedd yourself
	 * 
	 * @param selected
	 * @param window
	 * @param validate null by default
	 * @param onSuccess null by default
	 */
	public void onClick(T selected, TextFieldWindow window, TFValidatable validate, Consumer<TextField> onSuccess) {
		if(validate != null)window.setOnTextFieldValidation(validate);
		if(onSuccess != null)window.addOnSuccValidation(onSuccess);
	}

}
