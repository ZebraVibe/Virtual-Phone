package com.ouken.phone.app.oukenstudioapp.editor.ui.window;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class DeleteWindow extends DialogWindow {
	
	
	public DeleteWindow(String objectName, Skin skin, ShapeRenderer renderer) {
		super("Delete", "Delete " + objectName + " ?", skin, renderer);
		Runnable onOkClick = () -> {
			onYesClick();
			closeWindow();
			};
		Button okButton = addButton("YES", onOkClick);
		addButton("NO", () -> onNoClick());
		
		//
		addListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if(keycode == Keys.ENTER)onOkClick.run();
				else if(keycode == Keys.ESCAPE)closeWindow();
				return true;
			}
		});
	}
	
	public abstract void onYesClick();
	
	public void onNoClick() {
		closeWindow();
	}
	


}
