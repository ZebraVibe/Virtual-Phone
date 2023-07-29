package com.ouken.phone.app.oukenstudioapp.editor.ui.window;

import java.util.Iterator;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ouken.phone.app.oukenstudioapp.common.EditorManager;

public class CreateProjectWindow extends TextFieldWindow {

	private EditorManager manager = EditorManager.INSTANCE;
	
	public CreateProjectWindow(Skin skin, ShapeRenderer renderer) {
		super("New Project", "new_proj", skin, renderer);
		
		setOnTextFieldValidation(tf -> {
			if(tf == null || tf.getText() == null || tf.getText().equals(""))return false;
			String text = tf.getText();
			String test = "./"; // a lot is getting check when the file gets created
			for (int i = 0; i < test.length(); i++) {
				if(text.contains(test.substring(i, i+1)))return false;
			}
			return manager.createNoneExistingProjectAndSetCurrent(text);
		});

		
	}
	

	
	

}
